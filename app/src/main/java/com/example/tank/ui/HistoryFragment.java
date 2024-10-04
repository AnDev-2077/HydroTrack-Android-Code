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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.example.tank.MainActivity;
import com.example.tank.R;
import com.example.tank.databinding.FragmentHistoryBinding;
import com.example.tank.databinding.FragmentHomeBinding;
import com.example.tank.databinding.FragmentSettingsBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int week = 2;
    private String mParam1;
    private String mParam2;

    /**/
    FragmentHistoryBinding binding;
    private static final String CHANNEL_ID = "my_channel_id";

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding  =   FragmentHistoryBinding.inflate(inflater,container,false);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createNotificationChannel();

       /* binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification("Nivel Tanque", "El tanque está completamente lleno.");
            }
        });
        binding.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification("Nivel Tanque", "Tanque al "+homeFragment.percentage+"% de su capacidad");
            }
        });*/
        initData();
    }
    void initData() {
        float[] newValues = new float[]{20, 30, 50, 70, 90, 80, 60};
        updateChartData(newValues);
        changeDay();
    }

    private void updateChartData(float[] values) {
        LineChart lineChart = binding.lineChar;

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new Entry(i, values[i]));
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH // Cambia a IMPORTANCE_HIGH
            );
            channel.setDescription("Default notification channel");

            NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    private void showNotification(String title, String message) {
        int drawableId = R.drawable.tank_w;

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            drawableId = R.drawable.tank_w;
        } else {
            drawableId = R.drawable.tank_b;
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.tank_w);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(drawableId)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon);

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
    private void changeDay(){
        binding.containerArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( week<2){
                    week++;

                    binding.txtDay.setText(updateDay());

                    binding.arrowRight.setAlpha(1.0f);
                    if(week==2){
                        binding.arrowRight.setAlpha(0.15f);
                        binding.arrowLeft.setAlpha(1.0f);
                    }else{
                        binding.arrowLeft.setAlpha(1.0f);
                    }
                }
            }
        });
        binding.containerArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( week > 1) {
                    week--;
                    binding.txtDay.setText(updateDay());
                    binding.arrowLeft.setAlpha(1.0f);
                    if(week==1) {
                        binding.arrowLeft.setAlpha(0.15f);
                        binding.arrowRight.setAlpha(1.0f);
                    }else{
                        binding.arrowRight.setAlpha(1.0f);
                    }
                }

            }
        });
    }
    String updateDay(){

        switch (week){
            case 1:

                float[] newValuess = new float[]{10, 10, 30, 80, 30, 60, 40};
                updateChartData(newValuess);
                return "Semana pasada";
            case 2:

                float[] newValues = new float[]{20, 30, 50, 70, 90, 80, 60};
                updateChartData(newValues);
                return "Esta semana";
            default:
                float[] newValuesw = new float[]{20, 30, 50, 70, 90, 80, 60};
                updateChartData(newValuesw);
                return "Esta semana";
        }
    }
}