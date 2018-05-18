package application.rahmatsyam.doctortracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.config.SessionManager;
import application.rahmatsyam.doctortracker.connection.JSONParser;
import es.dmoral.toasty.Toasty;


public class LoginPasien extends AppCompatActivity {

    private EditText editTextUser, editTextPass;
    String user, pass;
    String berhasil;
    String apilogin;
    SessionManager session;
    ProgressDialog pDialog;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pasien);

        editTextUser = findViewById(R.id.editTextUser);
        editTextPass = findViewById(R.id.editTextPass);

        session = new SessionManager(getApplicationContext());
        Toasty.info(getApplicationContext(), "User Login Status: " +
                session.isLoggedInPasien(), Toast.LENGTH_SHORT, true).show();

        if (android.os.Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.enableDefaults();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button signPasienButton = findViewById(R.id.btn_signPasien);
        signPasienButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                user = editTextUser.getText().toString().trim();
                pass = editTextPass.getText().toString().trim();


                if (user.equals("")) {
                    Toasty.info(LoginPasien.this, "Username tak boleh kosong", Toast.LENGTH_LONG).show();
                } else if (pass.equals("")) {
                    Toasty.info(LoginPasien.this, "Password tak boleh kosong", Toast.LENGTH_LONG).show();

                } else {

                    Login(user, pass);

                }

            }
        });

        TextView regPasien = findViewById(R.id.txt_regPasien);
        regPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regPas = new Intent(getApplicationContext(), DaftarPasien.class);
                startActivity(regPas);
            }
        });


    }

    public void Login(final String user, final String pass) {


        pDialog = new ProgressDialog(LoginPasien.this);
        pDialog.setMessage("Tunggu...");
        pDialog.setIndeterminate(false);
        pDialog.show();

        apilogin = Config.LOGIN_PASIEN + user + "&pass=" + pass;

        StringRequest strReq = new StringRequest(Request.Method.POST, apilogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                            openProfile();

                            JSONParser jParser = new JSONParser();
                            JSONObject json = jParser.getJSONFromUrl(apilogin);
                            try {
                                berhasil = json.getString("berhasil");
                                JSONArray hasil = json.getJSONArray("loginpasien");
                                if (berhasil.equals("1")) {
                                    for (int i = 0; i < hasil.length(); i++) {

                                        JSONObject c = hasil.getJSONObject(i);
                                        String nama_lengkap = c.getString("nama_lengkap").trim();
                                        String no_telp = c.getString("no_telp").trim();
                                        session.createLoginPasien(nama_lengkap, no_telp);

                                    }
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        } else {
                            Toasty.info(LoginPasien.this, "Login gagal", Toast.LENGTH_SHORT).show();

                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();

                            new AlertDialog.Builder(LoginPasien.this)
                                    .setTitle("Koneksi Jaringan")
                                    .setMessage("Sepertinya jaringan anda bermasalah " +
                                            "Silakan login ulang")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setIcon(R.mipmap.ic_launcher).show();

                        }
                        NetworkResponse response = error.networkResponse;

                        if (response != null && response.data != null) {
                            Toasty.error(LoginPasien.this, "LOGIN GAGAL", Toast.LENGTH_LONG, true).show();
                        }
                    }
                });


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(strReq);
    }

    private void openProfile() {
        Intent intent = new Intent(LoginPasien.this, NavigasiPasien.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MenuPilihan.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            session.checkLoginPasien();
            Intent i = new Intent(getApplicationContext(), MenuPilihan.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        return true;
    }


}