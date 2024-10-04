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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.example.tank.R;
import com.example.tank.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    /**/

    int percentageT = 50;
    int percentageY= 20;
    int percentageBY = 100;

    int mlT = 1600;
    int mlY= 1200;
    int mlBY = 2400;
    int ml =mlT;

   public static int percentage = 50;

    FragmentHomeBinding binding;
    int day = 2;

    private ValueAnimator progressAnimator;
    private ObjectAnimator alphaAnimator;
    private ObjectAnimator alphaAnimator2;

    public homeFragment() {

    }

    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
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
        //
       // binding.waveBar.setPorsentage(percentage);
        binding.waterPorsentage.setText(String.valueOf(percentage)+"%");
        changeDay();
    }

    @Override
    public void onResume() {
        super.onResume();
        initAnimate();
        new Handler().postDelayed(this::animateLine, 1500);
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
                    binding.waterVol.setText(String.valueOf(ml)+"L");
                    binding.arrowRight.setAlpha(1.0f);
                    if(day==2){
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
                if (day > 0) {
                    day--;

                    binding.txtDay.setText(updateDay());
                    updateMl();;
                    binding.waterVol.setText(String.valueOf(ml)+"L");
                    binding.arrowLeft.setAlpha(1.0f);
                    if(day==0) {
                        binding.arrowLeft.setAlpha(0.15f);
                        binding.arrowRight.setAlpha(1.0f);
                    }else{
                        binding.arrowRight.setAlpha(1.0f);
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
                initAnimate();
                return dayEs;
            case 1:
                percentage = percentageY;
                initAnimate();
                return "Ayer";
            case 2:
                percentage = percentageT;
                initAnimate();
                return "Hoy";
            default:
                percentage = percentageT;
                initAnimate();
                return "Hoy";
        }
    }
    private void updateMl(){

        int mlW = ml;
        if(day == 0){
            mlW = mlBY;
        }
        if(day == 1){
            mlW = mlY;
        }
        if(day == 2){
            mlW = mlT;
        }


        ml = mlW;
    }
    private void initAnimate(){
        restartAnimation();
        changeDay();
        animate(percentage);

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


       // binding.tankLine.setVisibility(View.VISIBLE);


        blinkAnimator.start();


        /*binding.tankLine.postDelayed(() -> {
            blinkAnimator.cancel();
            binding.tankLine.setAlpha(0f);
        }, 2000);*/
    }
}