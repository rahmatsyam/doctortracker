package application.rahmatsyam.doctortracker.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import application.rahmatsyam.doctortracker.LoginDokter;
import application.rahmatsyam.doctortracker.LoginPasien;

/**
 *Created by Rahmat Syam on 29/12/2016
 */

@SuppressLint("CommitPrefEdits")
public class SessionManager  {
    //Shared Preferences
    private SharedPreferences pref;

    //Editor for Shared preferences
    private Editor editor;

    //Context
    private Context _context;

    //nama sharepreference
    private static final String PREF_NAME = "Sesi";

    //All Shared Preferences Keys
    private static final String IS_LOGIN_DOKTER = "IsLoggedIn";
    private static final String IS_LOGIN_PASIEN = "IsLoggedInPasien";
    public static final String KEY_ID_DOKTER = "id_dokter";
    public static final String KEY_ID_LOKASI = "id_lokasi";
    public static final String KEY_NAMA_DOKTER = "nama_dokter";
    public static final String KEY_EMAIL_DOKTER = "email_dokter";
    public static final String KEY_NAMA_LENGKAP = "nama_lengkap";
    public static final String KEY_NO_TELP = "no_telp";



    //Constructor
    public SessionManager(Context context){
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    //Create Login session
    public void createLoginSession(String id_dokter, String id_lokasi, String nama_dokter, String email_dokter){
        //Storing login value as true
        editor.putBoolean(IS_LOGIN_DOKTER, true);
        editor.putString(KEY_ID_DOKTER,id_dokter);
        editor.putString(KEY_ID_LOKASI,id_lokasi);
        editor.putString(KEY_NAMA_DOKTER,nama_dokter);
        editor.putString(KEY_EMAIL_DOKTER,email_dokter);

        editor.commit();
    }


    public void createLoginPasien(String nama_lengkap, String no_telp){
        //Storing login value as true
        editor.putBoolean(IS_LOGIN_PASIEN, true);
        editor.putString(KEY_NAMA_LENGKAP,nama_lengkap);
        editor.putString(KEY_NO_TELP,no_telp);

        editor.commit();
    }

    /**
     *Check login method will check user login status
     *If false it will redirect user to login page
     *Else won't do anything
     **/

    public void checkLogin(){
        //check login status
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginDokter.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void checkLoginPasien(){
        //check login status
        if(!this.isLoggedInPasien()){
            Intent ip = new Intent(_context, LoginPasien.class);
            ip.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ip.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(ip);
        }
    }

    /**
     *Get stored session data
     **/

    public HashMap<String, String> getUserDetails() {
        HashMap<String,String> user = new HashMap<>();
        user.put(KEY_ID_DOKTER, pref.getString(KEY_ID_DOKTER,null));
        user.put(KEY_ID_LOKASI, pref.getString(KEY_ID_LOKASI,null));
        user.put(KEY_NAMA_DOKTER, pref.getString(KEY_NAMA_DOKTER,null));
        user.put(KEY_EMAIL_DOKTER, pref.getString(KEY_EMAIL_DOKTER,null));
        return user;
    }

    public HashMap<String, String> getPasienDetails() {
        HashMap<String,String> Pasien = new HashMap<>();
        Pasien.put(KEY_NAMA_LENGKAP, pref.getString(KEY_NAMA_LENGKAP,null));
        Pasien.put(KEY_NO_TELP, pref.getString(KEY_NO_TELP,null));
        return Pasien;
    }


    /**
     * Clear session details
     **/


    public void logout() {
        //Clearing all data from shared preferences
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginDokter.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public void logoutPasien() {
        //Clearing all data from shared preferences
        editor.clear();
        editor.commit();
        Intent r = new Intent(_context, LoginPasien.class);
        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        r.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(r);
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN_DOKTER, false);
    }

    public boolean isLoggedInPasien(){
        return pref.getBoolean(IS_LOGIN_PASIEN, false);
    }
}		
		
			
	
	
	
	