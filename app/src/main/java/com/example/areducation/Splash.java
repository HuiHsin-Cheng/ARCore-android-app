package com.example.areducation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;

public class Splash extends Activity {
    private static boolean splashLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!splashLoaded) {
            setContentView(R.layout.splash);

            ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
            

            if(availability == ArCoreApk.Availability.SUPPORTED_INSTALLED)
            {
                int secondsDelayed = 1;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(Splash.this, MainActivity.class));
                        finish();
                    }
                }, secondsDelayed * 500);

                splashLoaded = true;
            }

            else if(availability == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {

                new AlertDialog.Builder(this)
                        .setTitle("装置不支援此App")
                        .setMessage("请参考：https://developers.google.com/ar/discover/supported-devices")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developers.google.com/ar/discover/supported-devices"));
                                startActivity(myIntent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else if(availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED){
                new AlertDialog.Builder(this)
                        .setTitle("尚未安装 Google Play Service for AR")
                        .setMessage("请至应用商店搜索  Google Play Service for AR 并安装。")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        else {
            Intent goToMainActivity = new Intent(Splash.this, MainActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }
    }
}
