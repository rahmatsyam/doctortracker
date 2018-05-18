package application.rahmatsyam.doctortracker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.config.SessionManager;
import es.dmoral.toasty.Toasty;


public class NavigasiDokter extends AppCompatActivity {
    LayoutInflater inflater;
    private DrawerLayout drawerLayout;

    SessionManager session;
    String id_dokter, id_lokasi, nama_dokter, email_dokter, nama_pasien, nohp_pasien, tgl_registrasi, update_terbaru;

    EditText txt_nama_pasien, txt_nohp_pasien;


    Button btn_daftar, btn_tutup;
    Toolbar mytoolbar;

    private TextView textViewJumlahAntrian;

    SwipeRefreshLayout swipeku;
    ProgressDialog pDialog;

    AlertDialog.Builder Builder;
    View dialogView;

    String REGISTER_URL = Config.DAFTAR_PASIEN;

    public static final String KEY_NAMA_PASIEN = "nama_pasien";
    public static final String KEY_NOHP_PASIEN = "nohp_pasien";
    public static final String KEY_ID_DOKTER = "id_dokter";
    public static final String KEY_ID_LOKASI = "id_lokasi";
    public static final String KEY_TGL_REGISTRASI = "tgl_registrasi";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigasi_dokter);


        session = new SessionManager(getApplicationContext());
       /* Toasty.info(getApplicationContext(), "User Login Status:" +
                session.isLoggedIn(), Toast.LENGTH_SHORT, true).show(); */
        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        id_dokter = user.get(SessionManager.KEY_ID_DOKTER);
        id_lokasi = user.get(SessionManager.KEY_ID_LOKASI);
        nama_dokter = user.get(SessionManager.KEY_NAMA_DOKTER);
        email_dokter = user.get(SessionManager.KEY_EMAIL_DOKTER);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        update_terbaru = new SimpleDateFormat("d/M/yyyy - HH:mm", Locale.getDefault()).format(new Date());
        tgl_registrasi = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        NavigationView navigationView = findViewById(R.id.navigation_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    case R.id.nav_listPasien:
                        Toasty.info(getApplicationContext(), "List Pasien Telah Dipilih", Toast.LENGTH_SHORT).show();
                        Intent list = new Intent(getApplicationContext(), ListPasien.class);
                        startActivity(list);
                        return true;
                    case R.id.nav_tambahKlinik:
                        Toasty.info(getApplicationContext(), "Tambah Klinik Telah Dipilih", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), TambahLokasi.class);
                        startActivity(i);
                        return true;
                    case R.id.nav_tmbhPasien:
                        DialogForm();
                        return true;
                    case R.id.nav_signout:
                        session.logout();
                        return true;
                    default:
                        Toasty.error(getApplicationContext(), "Kesalahan Terjadi ", Toast.LENGTH_SHORT, true).show();
                        return true;
                }


            }
        });


        View navHeaderView = LayoutInflater.from(this).inflate(R.layout.layout_header, navigationView, true);
        TextView rahsya = navHeaderView.findViewById(R.id.namaDokter);
        rahsya.setText(nama_dokter);
        TextView bb = navHeaderView.findViewById(R.id.emailDokter);
        bb.setText(email_dokter);


        drawerLayout = findViewById(R.id.drawer);
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

        swipeku = findViewById(R.id.swipeDokter);
        swipeku.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);
        swipeku.setRefreshing(true);
        getData();
        swipeku.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData();


                    }
                }
        );


        textViewJumlahAntrian = findViewById(R.id.jumlah_antrian);
        TextView textViewJam = findViewById(R.id.txt_jam);
        textViewJam.setText("Update Terakhir: \t" + update_terbaru);


    }

    @Override
    public void onResume() {
        super.onResume();
        update_terbaru = new SimpleDateFormat("d/M/yyyy - HH:mm", Locale.getDefault()).format(new Date());
        TextView textViewJam = findViewById(R.id.txt_jam);
        textViewJam.setText("Update Terakhir: \t" + update_terbaru);
        swipeku.setRefreshing(true);
        getData();
    }

    private void kosong() {
        txt_nama_pasien.setText(null);
        txt_nohp_pasien.setText(null);

    }


    private void DialogForm() {

        Builder = new AlertDialog.Builder(NavigasiDokter.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.layout_form_daftar, null);
        Builder.setView(dialogView);
        Builder.setCancelable(true);
        Builder.setIcon(R.mipmap.ic_launcher);
        Builder.setTitle("Form Biodata");
        btn_daftar = dialogView.findViewById(R.id.dialog_daftar);
        btn_tutup = dialogView.findViewById(R.id.dialog_tutup);


        txt_nama_pasien = dialogView.findViewById(R.id.txt_nama);
        txt_nohp_pasien = dialogView.findViewById(R.id.txt_nohp);

        final AlertDialog alertDialog = Builder.show();



        kosong();

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nama_pasien = txt_nama_pasien.getText().toString();
                nohp_pasien = txt_nohp_pasien.getText().toString();

                if (nama_pasien.isEmpty()) {
                    Toasty.info(getApplicationContext(), "Nama tak boleh kosong", Toast.LENGTH_LONG).show();

                } else if (nohp_pasien.isEmpty()) {
                    Toasty.info(getApplicationContext(), "Nomor tak boleh kosong", Toast.LENGTH_LONG).show();
                } else {

                    onClickedButton(alertDialog);
                    try {

                        tambahPasien();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        });


        btn_tutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onClickedButton(alertDialog);
            }
        });


    }

    private void onClickedButton(AlertDialog alertDialog) {
        alertDialog.dismiss();
    }



    private void tambahPasien() throws JSONException {

        final String strnama_pasien = txt_nama_pasien.getText().toString().trim();
        final String strnohp_pasien = txt_nohp_pasien.getText().toString().trim();


        pDialog = new ProgressDialog(NavigasiDokter.this);
        pDialog.setMessage("Tunggu...");
        pDialog.setIndeterminate(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        new AlertDialog.Builder(NavigasiDokter.this)
                                .setTitle("Pendaftaran")
                                .setMessage(response)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })

                                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setIcon(R.mipmap.ic_launcher).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(NavigasiDokter.this, error.toString(), Toast.LENGTH_SHORT, true).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_NAMA_PASIEN, strnama_pasien);
                params.put(KEY_NOHP_PASIEN, strnohp_pasien);
                params.put(KEY_ID_DOKTER, id_dokter);
                params.put(KEY_ID_LOKASI, id_lokasi);
                params.put(KEY_TGL_REGISTRASI, tgl_registrasi);
                return params;
            }

        };

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getData() {
        String iddokter = session.getUserDetails().get(SessionManager.KEY_ID_DOKTER);
        if (iddokter == null || iddokter.isEmpty()) {
            return;
        }
        String url = Config.TOTAL_URL + iddokter;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
                swipeku.setRefreshing(false);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NavigasiDokter.this, "Jaringan bermasalah", Toast.LENGTH_LONG).show();
                        swipeku.setRefreshing(false);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String total_pasien = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.TOTAL_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            total_pasien = collegeData.getString(Config.KEY_TOTAL_PASIEN);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        textViewJumlahAntrian.setText(total_pasien);


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

