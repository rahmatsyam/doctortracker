package application.rahmatsyam.doctortracker;

import android.content.Context;
import android.support.v7.app.AlertDialog;

class AlertDialogManager {

    void showAlertDialog(Context context, String title, String message) {


        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Tutup", null);
        builder.show();


    }
}				
