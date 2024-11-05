package com.example.tank.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tank.MainActivity;
import com.example.tank.databinding.FragmentHomeBinding;
import com.example.tank.domain.DataModule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private static final String SERVER_IP = "192.168.4.1";
    private static final int SERVER_PORT = 80;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    /**/

    public static int percentageT = 0;
    public static int percentageY= 0;
    public static int percentageBY = 0;

    public static int mililitros = 0;
    public static int mlT = 0;
    public static int mlY= 50;
    public static int mlBY = 60;
    public static int ml = 0;

    public static int befpercentage = -1;
    public static int percentage = 0;
    public static boolean bloquear = true;

    FragmentHomeBinding binding;
    int day = 2;

    private ValueAnimator progressAnimator;
    private ObjectAnimator alphaAnimator;
    private ObjectAnimator alphaAnimator2;

    public DataModule currentDataModule = null;

    private DatabaseReference databaseReferenceG;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable consultaRunnable;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        binding  =  FragmentHomeBinding.inflate(inflater,container,false);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.waterPorsentage.setText(String.valueOf(percentage)+"%");
        binding.arrowRight.setEnabled(false);
        binding.arrowLeft.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);
        initThread();



    }

    @Override
    public void onResume() {
        super.onResume();

        initAnimate();
        befpercentage = -1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(consultaRunnable);
    }


    private void animate(int targetProgress) {

        progressAnimator = ValueAnimator.ofInt(0, targetProgress);

        if(percentage>80) progressAnimator.setDuration(700);
        else if(percentage>50) progressAnimator.setDuration(500);
        else if(percentage>20) progressAnimator.setDuration(300);
        else progressAnimator.setDuration(100);

        progressAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            binding.waveView.setProgress(animatedValue);
            //binding.waveBar.setPorsentage(animatedValue);
        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                secondAnimation();

            }
        });

        progressAnimator.start();
    }
    private void secondAnimation() {
        binding.containerWater.post(() -> {
            alphaAnimator = ObjectAnimator.ofFloat(binding.waterPorsentage, "alpha", 0f, 1f);
            alphaAnimator.setDuration(800);
            alphaAnimator.start();

            alphaAnimator2 = ObjectAnimator.ofFloat(binding.waterVol, "alpha", 0f, 1f);
            alphaAnimator2.setDuration(800);
            alphaAnimator2.start();

            /*translationAnimator = ObjectAnimator.ofFloat(binding.waterMl, "translationY", binding.containerWater.getHeight()/2, 0);
            translationAnimator.setDuration(800);
            translationAnimator.setInterpolator(new DecelerateInterpolator());
            translationAnimator.start();

            alphaAnimator2 = ObjectAnimator.ofFloat(binding.waterMl, "alpha", 0f, 1f);
            alphaAnimator2.setDuration(2000);
            alphaAnimator2.start();*/
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        restartAnimation();;
    }
    private void changeDay(){
        binding.containerArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(day<2){
                    day++;

                    binding.txtDay.setText(updateDay());
                    updateMl();;

                    binding.arrowRight.setAlpha(1.0f);
                    if(day==2){
                        binding.arrowRight.setAlpha(0.15f);
                        binding.arrowLeft.setAlpha(1.0f);
                        initThread();
                    }else{
                        binding.arrowLeft.setAlpha(1.0f);
                        stopThread();
                    }
                }



            }
        });
        binding.containerArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day > 0) {
                    day--;

                    binding.txtDay.setText(updateDay());
                    updateMl();;

                    binding.arrowLeft.setAlpha(1.0f);
                    if(day==0) {
                        binding.arrowLeft.setAlpha(0.15f);
                        binding.arrowRight.setAlpha(1.0f);

                    }else{
                        binding.arrowRight.setAlpha(1.0f);
                        stopThread();
                    }
                }



            }
        });
    }
    private String updateDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
        String dayF = dayFormat.format(calendar.getTime());

       String dayEs = dayF.substring(0, 1).toUpperCase() + dayF.substring(1).toLowerCase();

        switch (day){
            case 0:
                percentage = percentageBY;
                double mililitrosActuales = calcularLitros(mililitros, percentage);
                binding.waterVol.setText(mililitrosActuales+"L");
                initAnimate();
                return dayEs;
            case 1:
                percentage = percentageY;
                double mililitrosActuales2 = calcularLitros(mililitros, percentage);
                binding.waterVol.setText(mililitrosActuales2+"L");
                initAnimate();
                return "Ayer";
            case 2:
                percentage = percentageT;
                double mililitrosActuales3 = calcularLitros(mililitros, percentage);
                binding.waterVol.setText(mililitrosActuales3+"L");
                initAnimate();
                return "Hoy";
            default:
                percentage = percentageT;
                double mililitrosActuales4 = calcularLitros(mililitros, percentage);
                binding.waterVol.setText(mililitrosActuales4+"L");
                initAnimate();
                return "Hoy";
        }
    }
    private void updateMl(){

      /*  int mlW = ml;
        if(day == 0){
            mlW = mlBY;
        }
        if(day == 1){
            mlW = mlY;
        }
        if(day == 2){
            mlW = mlT;
        }


        ml = mlW;*/
    }
    private void initAnimate(){
        restartAnimation();
       // changeDay();
        //binding.waveView.setVisibility(View.VISIBLE);
        animate(percentage);

    }
    private void initThread(){

        consultaRunnable = new Runnable() {
            @Override
            public void run() {
                consultarUltimoObjeto();
                handler.postDelayed(this, 2000);
            }
        };


        handler.post(consultaRunnable);

    }

    private void consultarUltimoObjeto() {
        if(MainActivity.keyModuleCurrent==null){

            return;
        }
        databaseReferenceG = FirebaseDatabase.getInstance().getReference("ModulesWifi/"+ MainActivity.keyModuleCurrent);

        Query lastQuery = databaseReferenceG.orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        DataModule dataModule = snapshot.getValue(DataModule.class);
                        if (dataModule != null) {
                            currentDataModule = dataModule;
                            String porcentajeString = currentDataModule.getPorcentaje(); // "50,5"


                            porcentajeString = porcentajeString.replace(",", ".");


                            percentageT = (int) Float.parseFloat(porcentajeString);
                            double mililitrosActuales = calcularLitros(mililitros, percentageT);
                            binding.waterVol.setText(mililitrosActuales+"L");

                            if(percentageT!=befpercentage){
                                percentage = percentageT;
                                initAnimate();
                                befpercentage = percentageT;
                                if(!bloquear){
                                    binding.arrowRight.setEnabled(true);
                                    binding.arrowLeft.setEnabled(true);
                                    binding.progressBar.setVisibility(View.GONE);
                                    changeDay();
                                }
                            }
                            //initAnimate();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error
                System.err.println("Error al consultar Firebase: " + databaseError.getMessage());
            }
        });


    }
    public static double calcularLitros(double capacidadMililitros, double porcentaje) {

        double aguaMililitros = (capacidadMililitros * porcentaje) / 100;


        double aguaLitros = aguaMililitros / 1000;


        aguaLitros = Math.round(aguaLitros * 10.0) / 10.0;

        return aguaLitros;
    }

    private void stopThread() {
        if (consultaRunnable != null) {
            handler.removeCallbacks(consultaRunnable);
        }
    }
    private void restartAnimation() {
        binding.waterPorsentage.setAlpha(0.0f);
        binding.waterVol.setAlpha(0.0f);
         /*binding.waterMl.setAlpha(0.0f);
        binding.waveBar.setPorsentage(percentage);*/
        binding.waterPorsentage.setText(String.valueOf(percentage) + "%");


        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }
        if (alphaAnimator != null && alphaAnimator.isRunning()) {
            alphaAnimator.cancel();
        }
        if (alphaAnimator2 != null && alphaAnimator2.isRunning()) {
            alphaAnimator2.cancel();
        }
        /*binding.waterMl.setAlpha(0.0f);
        binding.waveBar.setPorsentage(0);*/
        binding.waveView.setProgress(0);
    }
    private void animateLine(){

        ValueAnimator blinkAnimator = ValueAnimator.ofFloat(1f, 0f);
        blinkAnimator.setDuration(500);
        blinkAnimator.setRepeatCount(ValueAnimator.INFINITE);
        blinkAnimator.setRepeatMode(ValueAnimator.REVERSE);


        blinkAnimator.addUpdateListener(animation -> {
            float alpha = (float) animation.getAnimatedValue();
           // binding.tankLine.setAlpha(alpha);
        });



        blinkAnimator.start();



    }
    public static void obtenerDatos() {
        if(MainActivity.keyModuleCurrent==null) return;;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String fechaAyer = sdf.format(calendar.getTime());


        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String fechaAntesDeAyer = sdf.format(calendar.getTime());


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ModulesWifi/"+MainActivity.keyModuleCurrent);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataModule data = snapshot.getValue(DataModule.class);

                    if(data!=null){
                        String porcentajeStr = String.valueOf(data.getPorcentaje());
                        if (data.getFecha().equals(fechaAyer)) {
                            percentageY = (int) Math.round(Double.parseDouble(porcentajeStr));
                        } else if (data.getFecha().equals(fechaAntesDeAyer)) {
                            percentageBY = Integer.valueOf(porcentajeStr);
                        }
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }

}