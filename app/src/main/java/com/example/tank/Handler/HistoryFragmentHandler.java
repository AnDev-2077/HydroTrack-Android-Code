package com.example.tank.Handler;

import com.example.tank.domain.DataModule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragmentHandler {
    private DatabaseReference databaseReference;

    public float[] semanaPasada = new float[7];
    public float[] estaSemana = new float[7];
    private float[] currentData;

    public HistoryFragmentHandler(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void obtenerDatos(String keyModuleCurrent, DataCallback callback) {
        DatabaseReference ref = databaseReference.child("ModulesWifi/" + keyModuleCurrent);
        ref.orderByChild("fecha").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, DataModule> latestDataByDate = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataModule data = snapshot.getValue(DataModule.class);
                    if (data != null) {
                        String fecha = data.getFecha();
                        latestDataByDate.put(fecha, data);
                    }
                }
                List<DataModule> listaFinal = new ArrayList<>(latestDataByDate.values());
                callback.onDataRetrieved(listaFinal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface DataCallback {
        void onDataRetrieved(List<DataModule> dataModules);
        void onError(Exception e);
    }





}
