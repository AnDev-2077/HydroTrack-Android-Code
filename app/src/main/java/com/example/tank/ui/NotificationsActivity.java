package com.example.tank.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.tank.MainActivity;
import com.example.tank.R;
import com.example.tank.notifications.NotificationWorker;

import java.util.concurrent.TimeUnit;

public class NotificationsActivity extends AppCompatActivity {


    private Switch swNoti;
    private Switch swMan;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private LinearLayout btn_recorda;
    public static final String PREFS_NAME = "MyPrefs";
    public static final String SWITCH_STATE_KEY = "switch_state";
    public static final String SWITCH_MAN_STATE_KEY = "switch_man_state";
    public static final String LINK_KEY = "link_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView btnBack = findViewById(R.id.back);
        swNoti  =findViewById(R.id.sw_noti);
        swMan  =findViewById(R.id.sw_mant);
        btn_recorda = findViewById(R.id.btn_recorda);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
        btn_recorda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this,remindersActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        } else {


        }
        initNotification();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // proceedWithNotifications();
            } else {

            }
        }
    }
    private void initNotification(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isSwitchOn = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false);
        swNoti.setChecked(isSwitchOn);


        swNoti.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_STATE_KEY, isChecked);
            editor.apply();


            if (isChecked) {
                swMan.setChecked(false);
                if(MainActivity.keyModuleCurrent!=null){
                    editor.putString(LINK_KEY, MainActivity.keyModuleCurrent);
                    editor.apply();
                    Toast.makeText(NotificationsActivity.this, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
                    scheduleNotificationWork("Notificación de Bienvenida", "¡Le mantendremos al tanto!");
                }else{
                    Toast.makeText(NotificationsActivity.this, "No existe ningún tanque", Toast.LENGTH_SHORT).show();
                }
            } else {

                cancelNotificationWork();
                Toast.makeText(NotificationsActivity.this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
            }
        });
        if (isSwitchOn) {
            scheduleNotificationWork("HydroTrak", "¡Le mantendremos al tanto!");
        }
        //
        boolean isSwitchManOn = sharedPreferences.getBoolean(SWITCH_MAN_STATE_KEY, false);
        swMan.setChecked(isSwitchManOn);
        swMan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_MAN_STATE_KEY, isChecked);
            editor.apply();
            if (isChecked) {
                Toast.makeText(NotificationsActivity.this, "Mantenimiento activado", Toast.LENGTH_SHORT).show();

                swNoti.setChecked(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    for (NotificationChannel channel : notificationManager.getNotificationChannels()) {
                        notificationManager.deleteNotificationChannel(channel.getId());
                    }
                }


            } else {

                cancelNotificationWork();
                Toast.makeText(NotificationsActivity.this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void scheduleNotificationWork(String title, String message) {

        Data data = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build();


        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotificationWorker.class, 15,  TimeUnit.MINUTES)
                .setInputData(data)
                .build();


        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationWork
        );
    }
    private void cancelNotificationWork() {
        WorkManager.getInstance(this).cancelUniqueWork("notification_work");
    }
    private void scheduleNotificationWork2(String title, String message) {
        Data data = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build();


        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                .setInitialDelay(2, TimeUnit.MINUTES)
                .setInputData(data)
                .build();


        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationWork
        );
    }
}