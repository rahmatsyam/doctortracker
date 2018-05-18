package application.rahmatsyam.doctortracker;

/**
 * Created by Rahmat Syam on 3/5/2017.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import application.rahmatsyam.doctortracker.config.Config;

public class CekNomorAntrian extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextId;
    private TextView textViewNamaPasien, textViewNoAntrian, textViewNamaDokter;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomor_antrian);

        editTextId = findViewById(R.id.editTextId);

        textViewNoAntrian = findViewById(R.id.txt_no_antrian);
        textViewNamaPasien = findViewById(R.id.txt_nama_pasien);
        textViewNamaDokter = findViewById(R.id.txt_nama_dokter);

        Button buttonGet = findViewById(R.id.buttonGet);
        buttonGet.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getData() {
        String nohp_pasien = editTextId.getText().toString().trim();
        if (nohp_pasien.equals("")) {
            Toast.makeText(this, "Masukkan nomor telepon", Toast.LENGTH_LONG).show();
            return;
        }
        loading = ProgressDialog.show(this, "Loading...", "Fetching...", true, true);

        String url = Config.DATA_URL + editTextId.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CekNomorAntrian.this, "Jaringan bermasalah", Toast.LENGTH_LONG).show();
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


        } catch (JSONException e) {
            e.printStackTrace();
        }

        textViewNoAntrian.setText(no_antrian);
        textViewNamaPasien.setText(nama_pasien);
        textViewNamaDokter.setText(nama_dokter);


    }

    @Override
    public void onClick(View v) {
        getData();
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