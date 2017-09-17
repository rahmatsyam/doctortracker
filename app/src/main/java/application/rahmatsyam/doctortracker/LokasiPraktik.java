package application.rahmatsyam.doctortracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.connection.DirectionsJSONParser;

import static android.os.Build.VERSION_CODES.M;
import static application.rahmatsyam.doctortracker.R.id.map;
import static java.lang.Double.parseDouble;

public class LokasiPraktik extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    public LocationManager mLocationManager;
    private GoogleApiClient mGoogleClient;
    private Location mUserLocation;
    private LocationRequest mLocationRequest;
    private SupportMapFragment mMapFragment;
    private ArrayList<Marker> mMarker;
    public static ArrayList<HashMap<String, String>> lokasipraktek = null;
    ArrayList<LatLng> markerPoints;
    private JSONArray data;


    ProgressDialog pDialog;

    private TextView textViewJarak, textViewWaktu;


    private int idspe = 0;
    private static final CharSequence[] dokter_tipe =
            {"Semua", "Gigi Umum", "Dokter Umum", "Penyakit Dalam Umum"};
    private AlertDialog fMapTypeDialog;

    private LatLng point;
    private Location titik_mulai = new Location("-5.226023,119.502251"), titik_tujuan = new Location(""), titik_temp = new Location("");
    private double jarak_tempuh = 1000.0f;
    float[] distance = new float[1];


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_praktik);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        //Permission strictmode
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setInterval(10 * 1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleClient.connect();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, mLocationListener);

        if (android.os.Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.enableDefaults();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showMapTypeSelectorDialog();

        textViewJarak = (TextView) findViewById(R.id.txt_jarak);
        textViewWaktu = (TextView) findViewById(R.id.txt_waktu);


        ImageView nearMe = (ImageView) findViewById(R.id.img_near_me);
        nearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickMe();
            }
        });

        String url = Config.LOKASI_PRAKTIK;
        try {
            data = new JSONArray(getHttpGet(url));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Gagal mengambil data!", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mUserLocation = location;

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("dssds", titik_mulai.getLatitude() + titik_mulai.getLongitude() + "), new LatLng( " + titik_tujuan.getLatitude() + titik_tujuan.getLongitude());
        String urll = getDirectionsUrl(new LatLng(titik_mulai.getLatitude(), titik_mulai.getLongitude()), new LatLng(titik_tujuan.getLatitude(), titik_tujuan.getLongitude()));

        DownloadTask downloadTask = new DownloadTask();


        // Start downloading json data from Google Directions API
        downloadTask.execute(urll);

    }

    private void clickMe() {
        Log.i("dssds", titik_mulai.getLatitude() + titik_mulai.getLongitude() + "), new LatLng( " + titik_tujuan.getLatitude() + titik_tujuan.getLongitude());
        String urll = getDirectionsUrl(new LatLng(titik_mulai.getLatitude(), titik_mulai.getLongitude()), new LatLng(titik_tujuan.getLatitude(), titik_tujuan.getLongitude()));

        pDialog = new ProgressDialog(LokasiPraktik.this);
        pDialog.setMessage("Tunggu...");
        pDialog.setIndeterminate(false);
        pDialog.show();
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(urll);


    }



    @SuppressWarnings("deprecation")
    public static String getHttpGet(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { //Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("aa", str.toString());
        return str.toString();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //marker.get
        for (int i = 0; i < mMarker.size(); i++) {
            if (marker.getId().equals(mMarker.get(i).getId())) {
                Intent intent = new Intent(this, ProfilDokter.class);
                intent.putExtra("nama_dokter", lokasipraktek.get(i).get("nama_dokter"));
                intent.putExtra("notlpn_praktek", lokasipraktek.get(i).get("noTlpn_praktek"));
                intent.putExtra("nama_spesialisasi", lokasipraktek.get(i).get("spesialisasi"));
                intent.putExtra("alamat_praktek", lokasipraktek.get(i).get("alamat_praktek"));
                intent.putExtra("jam_buka", lokasipraktek.get(i).get("jam_buka"));
                intent.putExtra("jam_tutup", lokasipraktek.get(i).get("jam_tutup"));
                intent.putExtra("hari_praktek", lokasipraktek.get(i).get("hari_praktik"));
                intent.putExtra("id_dokter", lokasipraktek.get(i).get("id_dokter"));
                intent.putExtra("id_lokasi", lokasipraktek.get(i).get("id_lokasi"));
                intent.putExtra("layanan", lokasipraktek.get(i).get("layanan"));


                Log.i("markerid", lokasipraktek.get(i).toString());
                startActivity(intent);


                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "Access has been granted.", Toast.LENGTH_SHORT).show();
                    try {
                        mMap.setMyLocationEnabled(true);


                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Akses ke GPS harus diperbolehkan untuk menggunakan fitur maps!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        if (Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);


            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                // Show rationale and request permission.
            }
        } else mMap.setMyLocationEnabled(true);


        //add pin at user's location


    }




    private void getLokasiDokter() {

        try {
            lokasipraktek = new ArrayList<>();
            mMarker = new ArrayList<>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                if (idspe != 0) {
                    if (idspe != Integer.parseInt(c.getString("id_spesialisasi"))) continue;
                }
                map = new HashMap<>();
                map.put("nama_dokter", c.getString("nama_dokter"));
                map.put("noTlpn_praktek", c.getString("noTlpn_praktek"));
                map.put("spesialisasi", c.getString("spesialisasi"));
                map.put("id_spesialisasi", c.getString("id_spesialisasi"));
                map.put("alamat_praktek", c.getString("alamat_praktek"));
                map.put("hari_praktik", c.getString("hari_praktik"));
                map.put("jam_buka", c.getString("jam_buka"));
                map.put("jam_tutup", c.getString("jam_tutup"));
                map.put("latitude", c.getString("latitude"));
                map.put("longitude", c.getString("longitude"));
                map.put("id_dokter", c.getString("id_dokter"));
                map.put("id_lokasi", c.getString("id_lokasi"));
                map.put("layanan", c.getString("layanan"));
                lokasipraktek.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Focus & Zoom
        assert lokasipraktek != null;
        Double latitude   = parseDouble(lokasipraktek.get(0).get("latitude"));
        Double longitude  = parseDouble(lokasipraktek.get(0).get("longitude"));
        LatLng coordinate = new LatLng(latitude, longitude);

        mMap.clear();
        if (mMarker != null) {
            for (int i = 0; i < mMarker.size(); i++) {
                mMarker.get(i).remove();
            }
        }
        //Marker loop


        for (int i = 0; i < lokasipraktek.size(); i++) {
            latitude = parseDouble(lokasipraktek.get(i).get("latitude"));
            longitude = parseDouble(lokasipraktek.get(i).get("longitude"));

            String name = lokasipraktek.get(i).get("nama_dokter");
            String spesial = lokasipraktek.get(i).get("spesialisasi");
            String id_spesial = lokasipraktek.get(i).get("id_spesialisasi");
            int icon = R.drawable.ic_marker_umum;
            if (Integer.parseInt(id_spesial) == 40) icon = R.drawable.ic_marker_umum;
            else if (Integer.parseInt(id_spesial) == 59) icon = R.drawable.ic_marker_dalam;
            else if (Integer.parseInt(id_spesial) == 101) icon = R.drawable.ic_marker_gigi;


            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(name)
                    .snippet(spesial)
                    .icon(BitmapDescriptorFactory
                            .fromResource(icon));
            Log.i("lat", latitude + "   " + longitude);
            titik_temp.setLatitude(latitude);
            titik_temp.setLongitude(longitude);

            Log.i("ddd", titik_mulai + "ccc" + titik_temp);
            if (titik_temp != null && mUserLocation != null) {
                double jarak_temp = haversine(mUserLocation.getLatitude(), mUserLocation.getLongitude(), titik_temp.getLatitude(), titik_temp.getLongitude());
                //jarak_temp = titik_mulai.distanceTo(titik_temp);
                Log.i("ddd", jarak_temp + "-aaaa");

                if (i == 0) {
                    jarak_tempuh = jarak_temp;

                    titik_tujuan.setLatitude(latitude);
                    titik_tujuan.setLongitude(longitude);
                } else {
                    if (jarak_temp < jarak_tempuh) {
                        //if (Float.compare(jarak_temp, jarak_tempuh) < 0) {
                        Log.i("ddd", jarak_temp + "-++-" + jarak_tempuh);
                        titik_tujuan.setLatitude(latitude);
                        titik_tujuan.setLongitude(longitude);
                        jarak_tempuh = jarak_temp;
                    }
                }

                Log.i("ddd", jarak_temp + "-" + jarak_tempuh);

            } else {
            }


            mMap.setOnInfoWindowClickListener(this);
            //mMap.addMarker(marker);
            mMarker.add(mMap.addMarker(marker));
        }
        Log.i("lllat", String.valueOf(mMarker.size()));
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("while downloading url", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mUserLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);


        if (mUserLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude()), 17));
            titik_mulai.setLatitude(mUserLocation.getLatitude());
            titik_mulai.setLongitude(mUserLocation.getLongitude());


            getLokasiDokter();

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, mLocationRequest, this);
        }
        //add pin at user's location

    }


    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6372.8; // In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mUserLocation = location;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {


        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {


        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            // pDialog = new ProgressDialog(LokasiPraktik.this);
            // pDialog.setMessage("Tunggu...");
            // pDialog.setIndeterminate(false);
            /// pDialog.show();

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            //   if (pDialog.isShowing()) {
            //     pDialog.dismiss();
            //  }

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.RED);
            }

            textViewJarak.setText(distance);
            textViewWaktu.setText(duration);

            mMap.addPolyline(lineOptions);


        }


    }

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.

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
                                idspe = 101;
                                break;
                            case 2:
                                idspe = 40;
                                break;
                            case 3:
                                idspe = 59;
                                break;
                            default:
                                idspe = 0;
                        }
                        getLokasiDokter();

                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
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
                fMapTypeDialog.show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);


    }


}