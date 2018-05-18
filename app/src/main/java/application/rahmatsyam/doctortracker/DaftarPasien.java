package application.rahmatsyam.doctortracker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import application.rahmatsyam.doctortracker.config.Config;
import es.dmoral.toasty.Toasty;

public class DaftarPasien extends AppCompatActivity implements View.OnClickListener {


    private RelativeLayout daftar_pasien;

    public static final String KEY_NAMA_LENGKAP = "nama_lengkap";
    public static final String KEY_EMAIL_PASIEN = "email_pasien";
    public static final String KEY_PASSWORD_PASIEN = "password_pasien";
    public static final String KEY_NOHP_PASIEN = "nohp_pasien";

    String nama_lengkap, email_pasien, password_pasien, nohp_pasien;

    EditText txt_nama_lengkap, txt_email_pasien, txt_password_pasien, txt_nohp_pasien;

    ProgressDialog pDialog;

    private Button Register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pasien);
        daftar_pasien = findViewById(R.id.daftar_pasien);

        txt_nama_lengkap = findViewById(R.id.nama_lengkap);
        txt_email_pasien = findViewById(R.id.email_pasien);
        txt_password_pasien = findViewById(R.id.password_pasien);
        txt_nohp_pasien = findViewById(R.id.nohp_pasien);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Register = findViewById(R.id.btn_sendRegister);
        Register.setOnClickListener(this);


    }

    private void registerSementara() {

        nama_lengkap = txt_nama_lengkap.getText().toString();
        email_pasien = txt_email_pasien.getText().toString();
        password_pasien = txt_password_pasien.getText().toString();
        nohp_pasien = txt_nohp_pasien.getText().toString();

        final String strnama_lengkap = txt_nama_lengkap.getText().toString().trim();
        final String stremail_pasien = txt_email_pasien.getText().toString().trim();
        final String strpassword_pasien = txt_password_pasien.getText().toString().trim();
        final String strnohp_pasien = txt_nohp_pasien.getText().toString().trim();

        if (nama_lengkap.equals("") || email_pasien.equals("") || password_pasien.equals("") || nohp_pasien.equals("")) {
            Toasty.warning(getApplicationContext(), "Wajib diisi", Toast.LENGTH_LONG).show();
        } else {
            Toasty.info(getApplicationContext(), "Sementara fungsi dimatikan untuk keamanan ^^v", Toast.LENGTH_LONG).show();

        }

    }

    private void registerUser() throws JSONException {

        nama_lengkap = txt_nama_lengkap.getText().toString();
        email_pasien = txt_email_pasien.getText().toString();
        password_pasien = txt_password_pasien.getText().toString();
        nohp_pasien = txt_nohp_pasien.getText().toString();

        final String strnama_lengkap = txt_nama_lengkap.getText().toString().trim();
        final String stremail_pasien = txt_email_pasien.getText().toString().trim();
        final String strpassword_pasien = txt_password_pasien.getText().toString().trim();
        final String strnohp_pasien = txt_nohp_pasien.getText().toString().trim();

        if (nama_lengkap.equals("") || email_pasien.equals("") || password_pasien.equals("") || nohp_pasien.equals("")) {
            Toasty.warning(getApplicationContext(), "Wajib diisi", Toast.LENGTH_LONG).show();
        } else {
            pDialog = new ProgressDialog(DaftarPasien.this);
            pDialog.setMessage("Tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_PASIEN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            Snack(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snack(error.toString());

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_NAMA_LENGKAP, strnama_lengkap);
                    params.put(KEY_EMAIL_PASIEN, stremail_pasien);
                    params.put(KEY_PASSWORD_PASIEN, strpassword_pasien);
                    params.put(KEY_NOHP_PASIEN, strnohp_pasien);
                    return params;
                }

            };

            com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    private void Snack(String content) {
        Snackbar snackbar = Snackbar.make(daftar_pasien, content, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }


    @Override
    public void onClick(View v) {
        if (v == Register) {
            registerSementara();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selectio
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;


    }
}
