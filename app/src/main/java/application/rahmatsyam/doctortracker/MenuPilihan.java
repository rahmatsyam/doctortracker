package application.rahmatsyam.doctortracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import application.rahmatsyam.doctortracker.config.Introduction;

public class MenuPilihan extends AppCompatActivity {


    public boolean isFirstStart;
    Context mcontext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pilihan);


        CardView dokter = findViewById(R.id.cardViewDokter);
        dokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dok = new Intent(getApplicationContext(), NavigasiDokter.class);
                startActivity(dok);
            }
        });

        CardView pasien = findViewById(R.id.cardViewPasien);
        pasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pas = new Intent(getApplicationContext(), NavigasiPasien.class);
                startActivity(pas);
            }
        });

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (isFirstStart) {

                    //  Launch application introduction screen
                    Intent i = new Intent(MenuPilihan.this, Introduction.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

    }


    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuPilihan.this);
        builder.setMessage("Keluar dari aplikasi ini?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        finish();

                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .create()
                .show();
    }

}
