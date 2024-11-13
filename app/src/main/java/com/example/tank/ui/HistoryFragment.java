package com.example.tank.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.example.tank.Handler.HistoryFragmentHandler;
import com.example.tank.MainActivity;
import com.example.tank.R;
import com.example.tank.databinding.FragmentHistoryBinding;
import com.example.tank.databinding.FragmentHomeBinding;
import com.example.tank.databinding.FragmentSettingsBinding;
import com.example.tank.domain.DataModule;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private HistoryFragmentHandler historyFragmentHandler;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int week = 2;
    private String mParam1;
    private String mParam2;

    /**/
    FragmentHistoryBinding binding;
    private static final String CHANNEL_ID = "my_channel_id";

    private float[] currentData = new float[7];

    float[] estaSemana = new float[]{0, 0, 0, 0, 0, 0, 0};
    float[] semanaPasada = new float[]{0, 0, 0, 0, 0, 0, 0};
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyFragmentHandler = new HistoryFragmentHandler(FirebaseDatabase.getInstance().getReference());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        obtenerData();
    }
    public void obtenerData() {
        historyFragmentHandler.obtenerDatos(MainActivity.keyModuleCurrent, new HistoryFragmentHandler.DataCallback() {
            @Override
            public void onDataRetrieved(List<DataModule> data) {
                procesarSemanas(data);
            }

            @Override
            public void onError(Exception e) {
                System.err.println("Error al leer datos: " + e.getMessage());
            }
        });
    }

    void initData() {
        updateChartData();
        changeDay();
        obtenerDatos();
    }

    private void updateChartData() {
        if (!isAdded()) {
            return;
        }
        LineChart lineChart = binding.lineChar;

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < currentData.length; i++) {
            entries.add(new Entry(i, currentData[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        lineChart.getLegend().setEnabled(false);
        dataSet.setColor(getResources().getColor(R.color.water));
        dataSet.setCircleColor(getResources().getColor(R.color.water));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        dataSet.setLineWidth(4f);
        dataSet.setCircleSize(6f);
        dataSet.setDrawValues(false);
        dataSet.setHighLightColor(getResources().getColor(R.color.water));
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(getResources().getDrawable(R.drawable.gradient_fill));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"L", "M", "X", "J", "V", "S", "D"})); // Etiquetas de los días
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);


        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setAxisMaximum(100);
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setTextSize(14f);
        leftYAxis.setTypeface(Typeface.DEFAULT_BOLD);


        leftYAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return (int) value + "%   ";
            }
        });

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateXY(800, 800);
        lineChart.invalidate(); // Refresh
    }


    private void changeDay() {
        binding.containerArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (week < 2) {
                    week++;

                    binding.txtDay.setText(updateDay());

                    binding.arrowRight.setAlpha(1.0f);
                    if (week == 2) {
                        binding.arrowRight.setAlpha(0.15f);
                        binding.arrowLeft.setAlpha(1.0f);
                    } else {
                        binding.arrowLeft.setAlpha(1.0f);
                    }
                }
            }
        });
        binding.containerArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (week > 1) {
                    week--;
                    binding.txtDay.setText(updateDay());
                    binding.arrowLeft.setAlpha(1.0f);
                    if (week == 1) {
                        binding.arrowLeft.setAlpha(0.15f);
                        binding.arrowRight.setAlpha(1.0f);
                    } else {
                        binding.arrowRight.setAlpha(1.0f);
                    }
                }

            }
        });
    }

    String updateDay() {

        switch (week) {
            case 1:

                currentData = semanaPasada;
                updateChartData();
                return "Semana pasada";
            case 2:
                currentData = estaSemana;

                updateChartData();
                return "Esta semana";
            default:
                currentData = estaSemana;
                updateChartData();
                return "Esta semana";
        }
    }

    public void obtenerDatos() {


        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("ModulesWifi/" + MainActivity.keyModuleCurrent);

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
                procesarSemanas(listaFinal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error al leer datos: " + databaseError.getMessage());
            }
        });
    }


    public void procesarSemanas(List<DataModule> listaFinal) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendario = Calendar.getInstance();


        calendario.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date lunesActual = calendario.getTime();

        calendario.add(Calendar.DATE, -7);
        Date lunesPasado = calendario.getTime();


        Map<String, Float> porcentajePorFecha = new HashMap<>();
        for (DataModule data : listaFinal) {
            porcentajePorFecha.put(data.getFecha(), Float.parseFloat(data.getPorcentaje()));
        }


        llenarSemana(semanaPasada, porcentajePorFecha, lunesPasado, dateFormat);


        llenarSemana(estaSemana, porcentajePorFecha, lunesActual, dateFormat);

        currentData = estaSemana;
        updateChartData();
        Log.i("Mimundoupn","Semana pasada: " + Arrays.toString(semanaPasada));
        Log.i("Mimundoupn","Esta semana: " + Arrays.toString(estaSemana));
    }

    private void llenarSemana(float[] semana, Map<String, Float> porcentajePorFecha, Date lunes, SimpleDateFormat dateFormat) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lunes);

        for (int i = 0; i < 7; i++) {
            String fecha = dateFormat.format(cal.getTime());
            if (porcentajePorFecha.containsKey(fecha)) {
                semana[i] = porcentajePorFecha.get(fecha);
            }

            cal.add(Calendar.DATE, 1);
        }


        Date hoy = new Date();
        if (cal.getTime().after(hoy)) {
            while (cal.getTime().before(hoy)) {
                semana[cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY] = 0;
                cal.add(Calendar.DATE, 1);
            }
        }

        for (int i = 0; i < semana.length; i++) {
            semana[i] = 100 - semana[i];
        }

    }
}