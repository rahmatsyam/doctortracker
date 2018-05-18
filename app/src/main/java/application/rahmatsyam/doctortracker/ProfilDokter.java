package application.rahmatsyam.doctortracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.config.SessionManager;
import es.dmoral.toasty.Toasty;

public class ProfilDokter extends AppCompatActivity {

    LayoutInflater inflater;
    AlertDialog.Builder Builder;
    View dialogView;

    Button daftarKerabat, daftarDiri, btn_daftar, btn_tutup;
    EditText txt_nama_pasien, txt_nohp_pasien;

    Toolbar mytoolbar;

    SessionManager session;

    String nama_lengkap, no_telp, nama_pasien, nohp_pasien, id_dokter, id_lokasi, tgl_registrasi;

    ProgressDialog pDialog;

    TextView nama_dokter, noTlpn_praktek, id_spesialisasi, alamat_praktek, hari_praktek, layanan, jam_praktik;


    String REGISTER_URL = Config.DAFTAR_PASIEN;
    String REGISTER_ME = Config.DAFTAR_DIRI;


    public static final String KEY_NAMA_PASIEN = "nama_pasien";
    public static final String KEY_NOHP_PASIEN = "nohp_pasien";
    public static final String KEY_TGL_REGISTRASI = "tgl_registrasi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_dokter);


        nama_dokter = findViewById(R.id.nama_dokter);
        noTlpn_praktek = findViewById(R.id.noTlpn_praktek);
        id_spesialisasi = findViewById(R.id.id_spesialisasi);
        alamat_praktek = findViewById(R.id.alamat_praktek);
        hari_praktek = findViewById(R.id.hari_praktek);
        layanan = findViewById(R.id.layanan);
        jam_praktik = findViewById(R.id.jam_praktik);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> Pasien = session.getPasienDetails();
        nama_lengkap = Pasien.get(SessionManager.KEY_NAMA_LENGKAP);
        no_telp = Pasien.get(SessionManager.KEY_NO_TELP);

        Bundle bundle = getIntent().getExtras();
        nama_dokter.setText(bundle.getString("nama_dokter"));
        noTlpn_praktek.setText(bundle.getString("notlpn_praktek"));
        id_spesialisasi.setText(bundle.getString("nama_spesialisasi"));
        alamat_praktek.setText(bundle.getString("alamat_praktek"));
        hari_praktek.setText(bundle.getString("hari_praktek"));

        jam_praktik.setText((bundle.getString("jam_buka")) + "-" + (bundle.getString("jam_tutup")));
        layanan.setText(bundle.getString("layanan"));
        id_dokter = bundle.getString("id_dokter");
        id_lokasi = bundle.getString("id_lokasi");

        daftarKerabat = findViewById(R.id.btn_dftrKerabat);
        daftarKerabat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogForm();
            }
        });


        daftarDiri = findViewById(R.id.dftrDiri);
        daftarDiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(ProfilDokter.this)
                        .setTitle("Pendaftaran")
                        .setMessage("Anda yakin?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    daftarDiri();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        })

                        .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setIcon(R.mipmap.ic_launcher).show();


            }
        });

        tgl_registrasi = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

    }


    private void kosong() {
        txt_nama_pasien.setText(null);
        txt_nohp_pasien.setText(null);

    }


    private void DialogForm() {

        Builder = new AlertDialog.Builder(this);
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
            public void onClick(View v) {

                nama_pasien = txt_nama_pasien.getText().toString();
                nohp_pasien = txt_nohp_pasien.getText().toString();

                if (nama_pasien.isEmpty()) {
                    Toasty.info(getApplicationContext(), "Nama tak boleh kosong", Toast.LENGTH_LONG).show();

                } else if (nohp_pasien.isEmpty()) {
                    Toasty.info(getApplicationContext(), "Nomor tak boleh kosong", Toast.LENGTH_LONG).show();
                } else {

                    onClickedButton(alertDialog);
                    try {

                        daftarPasien();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        });


        btn_tutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickedButton(alertDialog);
            }
        });


    }

    private void onClickedButton(AlertDialog alertDialog) {
        alertDialog.dismiss();
    }


    private void daftarPasien() throws JSONException {

        final String strnama_pasien = txt_nama_pasien.getText().toString().trim();
        final String strnohp_pasien = txt_nohp_pasien.getText().toString().trim();

        pDialog = new ProgressDialog(ProfilDokter.this);
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

                        new AlertDialog.Builder(ProfilDokter.this)
                                .setTitle("Pendaftaran")
                                .setMessage(response)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
                        Toasty.error(ProfilDokter.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_NAMA_PASIEN, strnama_pasien);
                params.put(KEY_NOHP_PASIEN, strnohp_pasien);
                params.put("id_dokter", id_dokter);
                params.put("id_lokasi", id_lokasi);
                params.put(KEY_TGL_REGISTRASI, tgl_registrasi);
                return params;
            }

        };

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void daftarDiri() throws JSONException {

        pDialog = new ProgressDialog(ProfilDokter.this);
        pDialog.setMessage("Tunggu...");
        pDialog.setIndeterminate(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_ME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        new AlertDialog.Builder(ProfilDokter.this)
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
                        new AlertDialog.Builder(ProfilDokter.this)
                                .setTitle("Pendaftran")
                                .setMessage("Gagal terdaftar, coba lagi")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })

                                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setIcon(R.mipmap.ic_launcher).show();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_NAMA_PASIEN, nama_lengkap);
                params.put(KEY_NOHP_PASIEN, no_telp);
                params.put(KEY_TGL_REGISTRASI, tgl_registrasi);
                params.put("id_dokter", id_dokter);
                params.put("id_lokasi", id_lokasi);
                return params;

            }

        };

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}