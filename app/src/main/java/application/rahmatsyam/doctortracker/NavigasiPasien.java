package application.rahmatsyam.doctortracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.config.SessionManager;
import es.dmoral.toasty.Toasty;


public class NavigasiPasien extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    SessionManager session;
    String nama_lengkap, no_telp;

    private String latitude, longitude;
    private TextView textViewNamaPasien, textViewNoAntrian, textViewNamaDokter;

    SwipeRefreshLayout mySwipe;

    ProgressDialog pDialog;
    private FloatingActionButton Direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigasi_pasien);

        session = new SessionManager(getApplicationContext());
        /*Toasty.info(getApplicationContext(), "User Login Status: " +
                session.isLoggedInPasien(), Toast.LENGTH_SHORT, true).show(); */
        session.checkLoginPasien();

        HashMap<String, String> Pasien = session.getPasienDetails();
        nama_lengkap = Pasien.get(SessionManager.KEY_NAMA_LENGKAP);
        no_telp = Pasien.get(SessionManager.KEY_NO_TELP);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.navigation_view2);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_lisDokter:
                        Toasty.info(getBaseContext(), "List Dokter Dipilih", Toast.LENGTH_SHORT, true).show();
                        Intent listDokter = new Intent(getApplication(), ListDokter.class);
                        startActivity(listDokter);
                        return true;
                    case R.id.nav_ceklokasi:
                        Intent lokasi = new Intent(getApplication(), LokasiPraktik.class);
                        startActivity(lokasi);
                        return true;
                    case R.id.nav_logOut:
                        session.logoutPasien();
                        return true;
                    case R.id.nav_no_antrian:
                        Intent antrian = new Intent(getApplication(), CekNomorAntrian.class);
                        startActivity(antrian);
                        return true;
                    default:
                        Toasty.error(getBaseContext(), "Kesalahan Terjadi ", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        View navHeaderView = navigationView.getHeaderView(0);
        TextView namalengkap = navHeaderView.findViewById(R.id.name1);
        namalengkap.setText(nama_lengkap);
        TextView notelp = navHeaderView.findViewById(R.id.telp);
        notelp.setText(no_telp);


        drawerLayout = findViewById(R.id.drawer2);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        mySwipe = findViewById(R.id.swipePasien);
        mySwipe.setColorSchemeResources(R.color.pink,R.color.indigo,R.color.lime);
        mySwipe.setRefreshing(true);
        getData();
        mySwipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData();


                    }
                }
        );

        textViewNoAntrian = findViewById(R.id.txt_no_antrian1);
        textViewNamaPasien = findViewById(R.id.txt_nama_pasien1);
        textViewNamaDokter = findViewById(R.id.txt_nama_dokter1);

        Direction = findViewById(R.id.fab_direction);
        Direction.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        mySwipe.setRefreshing(true);
        getData();
    }


    public void getData() {
        String nohp_pasien = session.getPasienDetails().get(SessionManager.KEY_NO_TELP);
        if (nohp_pasien == null || nohp_pasien.isEmpty()) {
            return;
        }
        String url = Config.DATA_URL + nohp_pasien;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
                mySwipe.setRefreshing(false);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NavigasiPasien.this, "Jaringan bermasalah", Toast.LENGTH_LONG).show();
                        mySwipe.setRefreshing(false);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String no_antrian = "";
        String nama_pasien = "";
        String nama_dokter = "";


        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            no_antrian = collegeData.getString(Config.KEY_NO_ANTRIAN);
            nama_pasien = collegeData.getString(Config.KEY_NAMA_PASIEN);
            nama_dokter = collegeData.getString(Config.KEY_NAMA_DOKTER);
            latitude = collegeData.getString(Config.KEY_LATITUDE);
            longitude = collegeData.getString(Config.KEY_LONGITUDE);



        } catch (JSONException e) {
            e.printStackTrace();


        }

        textViewNoAntrian.setText(no_antrian);
        textViewNamaPasien.setText(nama_pasien);
        textViewNamaDokter.setText(nama_dokter);


    }


    public void getDirection() throws JSONException {
        pDialog = new ProgressDialog(NavigasiPasien.this);
        pDialog.setMessage("Tunggu...");
        pDialog.setIndeterminate(false);
        pDialog.show();
        if (latitude == null || latitude.isEmpty() || longitude == null || longitude.isEmpty()) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            new AlertDialog.Builder(NavigasiPasien.this)
                    .setTitle("Navigasi")
                    .setMessage("Anda belum terdaftar,silakan daftar terlebih dahulu")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //   pDialog.dismiss();

                        }
                    })
                    .setIcon(R.mipmap.ic_launcher).show();

        } else {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }


        }


    }

    @Override
    public void onClick(View v) {
        if (v == Direction) {
            try {
                getDirection();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent(getApplicationContext(), MenuPilihan.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }


}