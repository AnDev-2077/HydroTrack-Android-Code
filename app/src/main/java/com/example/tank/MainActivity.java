package com.example.tank;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tank.adapters.MainAdapter;
import com.example.tank.databinding.ActivityMainBinding;
import com.example.tank.tank.WaveBar;
import com.example.tank.tank.WaveView;
import com.example.tank.ui.HistoryFragment;
import com.example.tank.ui.SettingsFragment;
import com.example.tank.ui.homeFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.Manifest;



    public class MainActivity extends AppCompatActivity {

        ActivityMainBinding binding;
        private static final int PERMISSION_REQUEST_CODE = 123;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            binding  = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            initTabLayaut();


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permiso no concedido, solicitar
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            } else {

                //proceedWithNotifications();
            }
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
        private void initTabLayaut(){

            replaceFragment(new homeFragment());
            binding.btnNavegation.setOnItemSelectedListener(item -> {
                if(item.getItemId()==R.id.home){
                    replaceFragment(new homeFragment());
                }
                if(item.getItemId()==R.id.history){
                    replaceFragment(new HistoryFragment());
                }
                if(item.getItemId()==R.id.setting){
                    replaceFragment(new SettingsFragment());
                }
                return true;
            });
        }
        public void replaceFragment(Fragment fragment){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
            fragmentTransition.replace(R.id.frame_layout,fragment);
            fragmentTransition.commit();


        }
        @Override
        protected void onResume() {
            super.onResume();

        }

    }