package com.example.tank.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.tank.R;
import com.example.tank.domain.DataModule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString("title");
        String message = getInputData().getString("message");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        String link = sharedPreferences.getString("link_key", "");

        DatabaseReference  databaseReferenceG = FirebaseDatabase.getInstance().getReference("ModulesWifi/"+link );

        Query lastQuery = databaseReferenceG.orderByKey().limitToLast(1);



        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DataModule dataModule = snapshot.getValue(DataModule.class);
                        if (dataModule != null) {
                            showNotification("Estado del Tanque ", "Tu tanque de agua está al " + dataModule.getPorcentaje()+"%");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showNotification("¡Revisa tu Tanque de Agua!","Recuerda revisar el suministro y la capacidad de tu tanque.");
            }
        });


        //
        return Result.success();
    }

    private void showNotification(String title, String message) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "default_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Canal de Notificaciones",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.tank_w)
                .setContentTitle(title != null ? title : "Hydrotrak")
                .setContentText(message != null ? message : "Monitorear Tanque")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, notificationBuilder.build());
    }
}