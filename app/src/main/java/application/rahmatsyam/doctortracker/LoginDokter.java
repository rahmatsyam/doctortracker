package application.rahmatsyam.doctortracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.config.SessionManager;
import application.rahmatsyam.doctortracker.connection.JSONParser;
import es.dmoral.toasty.Toasty;


public class LoginDokter extends AppCompatActivity {
    EditText email_dokter, password_dokter;
    Intent a;
    String url, success;
    TextView verifi;
    SessionManager session;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dokter);


        session = new SessionManager(getApplicationContext());
        Toasty.info(getApplicationContext(), "User Login Status: " +
               session.isLoggedIn(), Toast.LENGTH_SHORT, true).show();

        if (android.os.Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.enableDefaults();
        }

        Button Login = (Button) findViewById(R.id.btn_signDr);
        email_dokter = (EditText) findViewById(R.id.editText);
        password_dokter = (EditText) findViewById(R.id.editText2);
        verifi = (TextView) findViewById(R.id.verifi);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                url = Config.LOGIN_DOKTER +
                        "email_dokter=" + email_dokter.getText().toString() + "&password_dokter=" + password_dokter.getText().toString();

                if (email_dokter.getText().toString().trim().length() > 0
                        && password_dokter.getText().toString().trim().length() > 0)
                    new AmbilData().execute();
                else {
                    alert.showAlertDialog(LoginDokter.this, "Field tidak boleh kosong...!",
                            "Silahkan isi username dan password");
                }
            }
        });

        verifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = new Intent(LoginDokter.this, DaftarDokter.class);
                startActivity(a);
            }
        });

    }

    private class AmbilData extends AsyncTask<String, String, String> {

        ProgressDialog pDialog;

        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginDokter.this);
            pDialog.setMessage("Tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(url);
        try {


            success = json.getString("success");
            Log.e("error", "nilai sukses=" + success);
            JSONArray hasil = json.getJSONArray("login");
            if (success.equals("1")) {
                for (int i = 0; i < hasil.length(); i++) {
                    JSONObject c = hasil.getJSONObject(i);

                    //Storing each json
                    String id_dokter = c.getString("id_dokter").trim();
                    String id_lokasi = c.getString("id_lokasi").trim();
                    String nama_dokter = c.getString("nama_dokter").trim();
                    String email_dokter = c.getString("email_dokter").trim();
                    session.createLoginSession(id_dokter, id_lokasi, nama_dokter, email_dokter);


                }
            } else {
                Log.e("Error", "tidak bisa ambil data 0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error", "Tidak bisa ambil data 1");
        }
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        pDialog.dismiss();
        if (success.equals("1")) {
            a = new Intent(LoginDokter.this, NavigasiDokter.class);
            startActivity(a);
            finish();
        } else {
            alert.showAlertDialog(LoginDokter.this, "Login gagal..", "Email atau Password salah");
        }
    }

}


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MenuPilihan.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}

