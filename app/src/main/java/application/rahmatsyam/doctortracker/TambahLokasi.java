package application.rahmatsyam.doctortracker;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import application.rahmatsyam.doctortracker.config.Config;


/**
 * Created by Rahmat Syam on 28/1/2017.
 */

public class TambahLokasi extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout tambah_lokasi;
    private LocationManager lm;
    private LocationListener locListener;
    private EditText latitude, longitude;
    String REGISTER_URL = Config.TAMBAH_LOKASI;


    public static final String KEY_NAMA_PRAKTEK = "nama_praktek";
    public static final String KEY_ALAMAT_PRAKTEK = "alamat_praktek";
    public static final String KEY_NOTLPN_PRAKTEK = "noTlpn_praktek";
    public static final String KEY_IZIN_PRAKTEK = "izin_praktek";
    public static final String KEY_HARI_PRAKTEK = "hari_praktek";
    public static final String KEY_JAM_BUKA = "jam_buka";
    public static final String KEY_JAM_TUTUP = "jam_tutup";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    static EditText jam_buka, jam_tutup;
    EditText nama_praktek, alamat_praktek, noTlpn_praktek, izin_praktek, hari_praktek;
    private Button Register;
    private TimePickerDialog timeEventPicker;
    private TimePickerDialog timeEventPicker2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_lokasi);
        tambah_lokasi = (LinearLayout) findViewById(R.id.tambah_lokasi);

        nama_praktek = (EditText) findViewById(R.id.txt_namaPraktek);
        alamat_praktek = (EditText) findViewById(R.id.txt_alamatPraktek);
        noTlpn_praktek = (EditText) findViewById(R.id.txt_noTelepon);
        izin_praktek = (EditText) findViewById(R.id.txt_izinPraktek);
        hari_praktek = (EditText) findViewById(R.id.txt_hariPraktek);

        Register = (Button) findViewById(R.id.btn_regLokasi);
        Register.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        jam_buka = (EditText) findViewById(R.id.txt_jamBuka);
        jam_buka.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setTimeField();
                timeEventPicker.setTitle("Select time");
                timeEventPicker.show();
            }
        });

        jam_tutup = (EditText) findViewById(R.id.txt_jamTutup);
        jam_tutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeField2();
                timeEventPicker2.setTitle("Select time");
                timeEventPicker2.show();
            }

        });


        latitude = (EditText) findViewById(R.id.latitudeTxt);
        longitude = (EditText) findViewById(R.id.longitudeTxt);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
    }

    private void setTimeField() {
        final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.UK);
        Calendar newCalendar = Calendar.getInstance();
        int hours = newCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = newCalendar.get(Calendar.MINUTE);
        Log.i("Tag jam", "" + newCalendar.getTime());
        timeEventPicker = new TimePickerDialog(TambahLokasi.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                jam_buka.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, hours, minutes, true);
        timeEventPicker.show();

    }

    private void setTimeField2() {
        final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.UK);
        Calendar newCalendar = Calendar.getInstance();
        int hours = newCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = newCalendar.get(Calendar.MINUTE);
        Log.i("Tag jam", "" + newCalendar.getTime());
        timeEventPicker2 = new TimePickerDialog(TambahLokasi.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                jam_tutup.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, hours, minutes, true);
        timeEventPicker2.show();

    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            if (loc != null) {
                latitude.setText(String.valueOf(loc.getLatitude()));
                longitude.setText(String.valueOf(loc.getLongitude()));
                Toast.makeText(getBaseContext(), "Location Changed : Lat " + loc.getLatitude() +
                        "lgt: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        }

        public void onProviderDisabled(String arg0) {
        }

        public void onProviderEnabled(String arg0) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        lm.removeUpdates(locListener);
        lm = null;

    }


    private void registerLokasi() throws JSONException {

        final String strnama_praktek = nama_praktek.getText().toString().trim();
        final String stralamat_praktek = alamat_praktek.getText().toString().trim();
        final String strnoTlpn_praktek = noTlpn_praktek.getText().toString().trim();
        final String strizin_praktek = izin_praktek.getText().toString().trim();
        final String strhari_praktek = hari_praktek.getText().toString().trim();
        final String strjam_buka = jam_buka.getText().toString().trim();
        final String strjam_tutup = jam_tutup.getText().toString().trim();
        final String strlatitude = latitude.getText().toString().trim();
        final String strlongitude = longitude.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
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
                params.put(KEY_NAMA_PRAKTEK, strnama_praktek);
                params.put(KEY_ALAMAT_PRAKTEK, stralamat_praktek);
                params.put(KEY_NOTLPN_PRAKTEK, strnoTlpn_praktek);
                params.put(KEY_IZIN_PRAKTEK, strizin_praktek);
                params.put(KEY_HARI_PRAKTEK, strhari_praktek);
                params.put(KEY_JAM_BUKA, strjam_buka);
                params.put(KEY_JAM_TUTUP, strjam_tutup);
                params.put(KEY_LATITUDE, strlatitude);
                params.put(KEY_LONGITUDE, strlongitude);
                return params;
            }

        };

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void Snack(String content) {
        Snackbar snackbar = Snackbar.make(tambah_lokasi, content, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        if (v == Register) {
            try {
                registerLokasi();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if (id == android.R.id.home) {
            // LocationManager.removeUpdates(this);
            finish();
        }
        return true;


    }


}

