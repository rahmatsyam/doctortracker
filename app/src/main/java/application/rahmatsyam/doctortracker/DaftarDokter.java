package application.rahmatsyam.doctortracker;

/**
 * Created by Rahmat Syam  on 4/4/2017.
 */

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


public class DaftarDokter extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout daftar_doctor;

    public static final String KEY_NAMA_DOKTER = "nama_dokter";
    public static final String KEY_EMAIL_DOKTER = "email_dokter";
    public static final String KEY_NOHP_DOKTER = "nohp_dokter";
    public static final String KEY_PASSWORD_DOKTER = "password_dokter";

    EditText txt_nama_dokter;
    EditText txt_email_dokter;
    EditText txt_nohp_dokter;
    EditText txt_password_dokter;

    String nama_dokter, email_dokter, nohp_dokter, password_dokter;

    private Button Register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_dokter);
        daftar_doctor = findViewById(R.id.daftar_doctor);

        txt_nama_dokter = findViewById(R.id.nama_dokter);
        txt_email_dokter = findViewById(R.id.email_dokter);
        txt_nohp_dokter = findViewById(R.id.nohp_dokter);
        txt_password_dokter = findViewById(R.id.password_dokter);

        Register = findViewById(R.id.btn_registerDok);
        Register.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void regSementara() {

        nama_dokter = txt_nama_dokter.getText().toString();
        email_dokter = txt_email_dokter.getText().toString();
        nohp_dokter = txt_nohp_dokter.getText().toString();
        password_dokter = txt_password_dokter.getText().toString();

        final String strnama_dokter = txt_nama_dokter.getText().toString().trim();
        final String stremail_dokter = txt_email_dokter.getText().toString().trim();
        final String strnohp_dokter = txt_nohp_dokter.getText().toString().trim();
        final String strpassword_dokter = txt_password_dokter.getText().toString().trim();

        if (nama_dokter.equals("") || email_dokter.equals("") || nohp_dokter.equals("") || password_dokter.equals("")) {
            Toasty.info(getApplicationContext(), "Wajib diisi", Toast.LENGTH_LONG).show();
        } else {
            Toasty.info(getApplicationContext(), "Sementara fungsi dimatikan untuk keamanan ^^v", Toast.LENGTH_LONG).show();
        }

    }


    private void registerDokter() throws JSONException {

        final String strnama_dokter = txt_nama_dokter.getText().toString().trim();
        final String stremail_dokter = txt_email_dokter.getText().toString().trim();
        final String strnohp_dokter = txt_nohp_dokter.getText().toString().trim();
        final String strpassword_dokter = txt_password_dokter.getText().toString().trim();

        if (nama_dokter.equals("") || email_dokter.equals("") || nohp_dokter.equals("") || password_dokter.equals("")) {
            Toasty.info(getApplicationContext(), "Wajib Diisi", Toast.LENGTH_LONG).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DAFTAR_DOKTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
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
                    params.put(KEY_NAMA_DOKTER, strnama_dokter);
                    params.put(KEY_EMAIL_DOKTER, stremail_dokter);
                    params.put(KEY_NOHP_DOKTER, strnohp_dokter);
                    params.put(KEY_PASSWORD_DOKTER, strpassword_dokter);
                    return params;
                }

            };

            com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    private void Snack(String content) {
        Snackbar snackbar = Snackbar.make(daftar_doctor, content, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }


    @Override
    public void onClick(View v) {
        if (v == Register) {
            regSementara();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;


    }

}
