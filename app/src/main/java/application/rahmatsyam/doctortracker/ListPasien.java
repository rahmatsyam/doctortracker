package application.rahmatsyam.doctortracker;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import application.rahmatsyam.doctortracker.adapter.GetDataAdapter;
import application.rahmatsyam.doctortracker.adapter.PasienAdapter;
import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.config.SessionManager;
import es.dmoral.toasty.Toasty;


public class ListPasien extends AppCompatActivity implements SearchView.OnQueryTextListener {
    List<GetDataAdapter> GetDataAdapter1;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RecyclerView.Adapter recyclerViewadapter;

    private TextView emptyView;


    SwipeRefreshLayout mySwipeRefreshLayout;

    SessionManager sessionManager;

    String GET_JSON_DATA_HTTP_URL = Config.LIST_PASIEN;
    String JSON_NAMA_PASIEN = "nama_pasien";
    String JSON_NOHP_PASIEN = "nohp_pasien";
    String JSON_TGL_REGISTRASI = "tgl_registrasi";
    String JSON_NO_ANTRIAN = "no_antrian";

    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pasien);

        sessionManager = new SessionManager(getApplicationContext());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emptyView = findViewById(R.id.empty_data);



        GetDataAdapter1 = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.drawable_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);
        mySwipeRefreshLayout.setRefreshing(true);


        JSON_DATA_WEB_CALL();
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                JSON_DATA_WEB_CALL();
            }
        });
    }

    public void JSON_DATA_WEB_CALL() {


        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL + "?iddokter=" + sessionManager.getUserDetails().get(SessionManager.KEY_ID_DOKTER), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSON_PARSE_DATA_AFTER_WEBCALL(response);
                Toasty.info(ListPasien.this, "Data ditemukan", Toast.LENGTH_SHORT).show();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ListPasien.this, "Jaringan bermasalah, coba lagi", Toast.LENGTH_SHORT).show();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);


    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {
        GetDataAdapter1.clear();
        for (int i = 0; i < array.length(); i++) {

            GetDataAdapter GetDataAdapter2 = new GetDataAdapter();

            JSONObject json;
            try {
                json = array.getJSONObject(i);

                GetDataAdapter2.setNama_pasien(json.getString(JSON_NAMA_PASIEN));

                GetDataAdapter2.setNohp_pasien(json.getString(JSON_NOHP_PASIEN));

                GetDataAdapter2.setTgl_registrasi(json.getString(JSON_TGL_REGISTRASI));

                GetDataAdapter2.setNo_antrian(json.getString(JSON_NO_ANTRIAN));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            GetDataAdapter1.add(GetDataAdapter2);

        }

        recyclerViewadapter = new PasienAdapter(GetDataAdapter1, this);

        recyclerView.setAdapter(recyclerViewadapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        final List<GetDataAdapter> filteredModelList = filter(GetDataAdapter1, newText);
        if (filteredModelList.size() > 0) {
            recyclerViewadapter = new PasienAdapter(filteredModelList, this);
            recyclerView.setAdapter(recyclerViewadapter);
            return true;
        }

        return false;
    }

    private List<GetDataAdapter> filter(List<GetDataAdapter> models, String query) {
        query = query.toLowerCase();

        final List<GetDataAdapter> filteredModelList = new ArrayList<>();
        for (GetDataAdapter model : models) {
            final String text = model.getNama_pasien().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);

            }
        }

        recyclerViewadapter = new PasienAdapter(filteredModelList, ListPasien.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListPasien.this));
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();
        return filteredModelList;
    }

    private void checkAdapterIsEmpty () {
        if (recyclerViewadapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    protected void setupRecyclerView() {
    //   recyclerViewadapter = new PasienAdapter(ListPasien.this);
        recyclerViewadapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewadapter);
        checkAdapterIsEmpty();
    }


}


