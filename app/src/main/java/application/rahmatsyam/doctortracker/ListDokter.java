package application.rahmatsyam.doctortracker;

/**
 * Created by Rahmat Syam on 11/2/2017.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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

import application.rahmatsyam.doctortracker.adapter.DokterAdapter;
import application.rahmatsyam.doctortracker.adapter.GetDataAdapter;
import application.rahmatsyam.doctortracker.adapter.PasienAdapter;
import application.rahmatsyam.doctortracker.config.Config;
import es.dmoral.toasty.Toasty;

public class ListDokter extends AppCompatActivity {

    List<GetDataAdapter> GetDataAdapter3;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;


    SwipeRefreshLayout mySwipeRefreshLayout;


    String GET_JSON_DATA_HTTP_URL = Config.LIST_DOKTER;
    String JSON_ID_DOKTER = "id_dokter";
    String JSON_ID_LOKASI = "id_lokasi";
    String JSON_NAMA_DOKTER = "nama_dokter";
    String JSON_NAMA_SPESIALISASI = "nama_spesialisasi";
    String JSON_NAMA_PRAKTEK = "nama_praktek";
    String JSON_ALAMAT_PRAKTEK = "alamat_praktek";
    String JSON_NOTLPN_PRAKTEK = "noTlpn_praktek";
    String JSON_HARI_PRAKTEK = "hari_praktek";
    String JSON_JAM_BUKA = "jam_buka";
    String JSON_JAM_TUTUP = "jam_tutup";
    String JSON_LAYANAN = "layanan";

    private static final CharSequence[] dokter_tipe =
            {"Semua", "Gigi Umum", "Dokter Umum", "Penyakit Dalam Umum"};
    private AlertDialog filterDokter;


    JsonArrayRequest jsonArrayRequest;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_dokter);


        GetDataAdapter3 = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);

        recyclerViewadapter = new DokterAdapter(GetDataAdapter3, this);


        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh2);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);
        mySwipeRefreshLayout.setRefreshing(true);
        JSON_DATA_WEB_CALL();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        JSON_DATA_WEB_CALL();


                    }
                }
        );

        showMapTypeSelectorDialog();


        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

    }

    @Override
    public void onResume() {
        super.onResume();
        mySwipeRefreshLayout.setRefreshing(true);
        GetDataAdapter3.clear();
        JSON_DATA_WEB_CALL();


    }


    public void JSON_DATA_WEB_CALL() {

        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                        Toasty.info(ListDokter.this, "Data ditemukan", Toast.LENGTH_SHORT).show();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(ListDokter.this, "Jaringan bermasalah, coba lagi", Toast.LENGTH_SHORT).show();
                        mySwipeRefreshLayout.setRefreshing(false);

                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        GetDataAdapter3.clear();

        for (int i = 0; i < array.length(); i++) {

            GetDataAdapter GetDataAdapter4 = new GetDataAdapter();

            JSONObject json;
            try {
                json = array.getJSONObject(i);


                GetDataAdapter4.setNama_dokter(json.getString(JSON_NAMA_DOKTER));

                GetDataAdapter4.setNama_spesialisasi(json.getString(JSON_NAMA_SPESIALISASI));

                GetDataAdapter4.setNama_praktek(json.getString(JSON_NAMA_PRAKTEK));

                GetDataAdapter4.setAlamat_praktek(json.getString(JSON_ALAMAT_PRAKTEK));

                GetDataAdapter4.setNoTlpn_praktek(json.getString(JSON_NOTLPN_PRAKTEK));

                GetDataAdapter4.setHari_praktek(json.getString(JSON_HARI_PRAKTEK));

                GetDataAdapter4.setId_dokter(json.getString(JSON_ID_DOKTER));

                GetDataAdapter4.setId_lokasi(json.getString(JSON_ID_LOKASI));

                GetDataAdapter4.setJam_buka(json.getString(JSON_JAM_BUKA));

                GetDataAdapter4.setJam_tutup(json.getString(JSON_JAM_TUTUP));

                GetDataAdapter4.setLayanan(json.getString(JSON_LAYANAN));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            GetDataAdapter3.add(GetDataAdapter4);
        }

        recyclerViewadapter = new DokterAdapter(GetDataAdapter3, this);

        recyclerView.setAdapter(recyclerViewadapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_kedua, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                filterDokter.show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean filterrong(String newText) {
        final List<GetDataAdapter> filteredModelList = filter(GetDataAdapter3, newText);
        if (filteredModelList.size() > 0) {
            recyclerViewadapter = new DokterAdapter(filteredModelList, this);
            recyclerView.setAdapter(recyclerViewadapter);
            return true;
        } else {
            Toasty.info(ListDokter.this, "Tidak Ditemukan", Toast.LENGTH_LONG, true).show();
            return false;
        }
    }


    private List<GetDataAdapter> filter(List<GetDataAdapter> models, String query) {
        query = query.toLowerCase();

        final List<GetDataAdapter> filteredModelList = new ArrayList<>();
        for (GetDataAdapter model : models) {
            final String text = model.getNama_spesialisasi().toLowerCase();

            if (text.contains(query)) {
                filteredModelList.add(model);

            }
        }

        recyclerViewadapter = new PasienAdapter(filteredModelList, ListDokter.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListDokter.this));
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();
        return filteredModelList;
    }


    private void showMapTypeSelectorDialog() {
        final String fDialogTitle = "Kategori Dokter";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(dokter_tipe, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                filterrong("Gigi");
                                break;
                            case 2:
                                filterrong("Dokter Umum");
                                break;
                            case 3:
                                filterrong("Penyakit Dalam");
                                break;
                            default:
                                filterrong("Umum");
                                //idspe = 0;
                        }

                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        filterDokter = builder.create();
        filterDokter.setCanceledOnTouchOutside(true);
    }


}