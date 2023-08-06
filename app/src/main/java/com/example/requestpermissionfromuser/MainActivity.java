package com.example.requestpermissionfromuser;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.os.BuildCompat;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn_request_permission);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(hasLocationPermission())
               {
                   Toast.makeText(MainActivity.this,"both permissions granted",Toast.LENGTH_SHORT).show();
               }else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
               {

                  showAlertDialog("Permission Required", "This app need the permission to run this feature.", "ok", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          resultLauncherMultiPermissions.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION});                      }
                  }, "cancel", (dialogInterface, i) -> dialogInterface.dismiss());
               }else
               {
                  resultLauncherMultiPermissions.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION});
               }
            }
        });

    }

    ActivityResultLauncher<String[]> resultLauncherMultiPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            boolean allGranted = true;
            for(String key: result.keySet())
            {
                allGranted = allGranted && result.get(key);
            }

            if(allGranted)
            {
                Toast.makeText(MainActivity.this,"all permission granted",Toast.LENGTH_SHORT).show();
            }else
            {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    showAlertDialog("Location Permission", "go to app settings and enable fine location permission", "settings", (dialogInterface, i) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getApplicationContext().getPackageName()));
                                startActivity(intent);
                            }, "cancel", (dialogInterface, i) -> dialogInterface.dismiss()
                    );

                    Log.d("demo", "onActivity Result: not granted");
                }
            }

        }
    });

    ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean isGranted) {
            if (isGranted) {
                Log.d("demo", "onActivity Result: Granted");
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showAlertDialog("Permission Required", "go to app settings and enable fine location permission", "settings", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getApplicationContext().getPackageName()));
                        startActivity(intent);
                    }, "cancel", (dialogInterface, i) -> dialogInterface.dismiss()
                    );

                Log.d("demo", "onActivity Result: not granted");
            }
        }
    });

    boolean hasLocationPermission()
    {
        return ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    boolean allPermissionGranted()
    {
        return ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showAlertDialog(String title, String message, String btn_ok_name, DialogInterface.OnClickListener ok_interface, String btn_cancel_name, DialogInterface.OnClickListener cancel_interface)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title).setMessage(message).setPositiveButton(btn_ok_name,ok_interface).setNegativeButton(btn_cancel_name,cancel_interface).create().show();
    }



}

