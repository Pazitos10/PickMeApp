package com.dit.escuelas_de_informatica.utiles;

import android.app.Activity;
import android.widget.Toast;


public class Utiles {

    public static void mostrarToast(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
