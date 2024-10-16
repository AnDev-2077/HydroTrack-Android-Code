package com.example.tank;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.example.tank.domain.DataModule;
import com.example.tank.domain.Group;
import com.example.tank.domain.Member;
import com.example.tank.tank.WaveBar;
import com.example.tank.tank.WaveView;
import com.example.tank.ui.HistoryFragment;
import com.example.tank.ui.SettingsFragment;
import com.example.tank.ui.homeFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

        ActivityMainBinding binding;
        private static final int PERMISSION_REQUEST_CODE = 123;
        Dialog dialog;
        SharedPreferences sharedPreferences;
        Member member = null;
        List<Group> groupsUser = new ArrayList<>();
        public static String keyModuleCurrent = null;
         private FirebaseAuth mAuth;
         public DataModule currentDataModule = null;

        private DatabaseReference databaseReferenceG;
        private Handler handler = new Handler(Looper.getMainLooper());
        private Runnable consultaRunnable;

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

            init();

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
        private void init(){
            getUser();

            sharedPreferences = getSharedPreferences("MiPreferencia", MODE_PRIVATE);
            String savedTitle = sharedPreferences.getString("titulo", null); // null si no hay nada guardado

            if (savedTitle != null) {

                binding.titleG.setText(savedTitle);
            } else {
                binding.titleG.setText("HydroTrak");
            }

            binding.editTitle.setVisibility(View.GONE);
            binding.editTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditTitleDialog();

                }
            });
            binding.claveKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomPopupMenu(v);
                }
            });
        }


        private void showEditTitleDialog() {

            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.edit_title);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);

            EditText editText = dialog.findViewById(R.id.txt_title);
            editText.requestFocus();
            TextView txt_error = dialog.findViewById(R.id.txt_error);
            Button btn_cancelar = dialog.findViewById(R.id.btn_Cancelar);
            Button btn_guardar = dialog.findViewById(R.id.btn_guardar);
            btn_cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = editText.getText().toString().trim();
                    if (title.isEmpty()) {
                        txt_error.setVisibility(View.VISIBLE);
                        txt_error.setText("No puede estar vacío");
                        return;
                    }
                    else if (title.length() < 3) {
                        txt_error.setVisibility(View.VISIBLE);
                        txt_error.setText("Debe tener al menos 3 caracteres");
                        return;
                    }
                    else  if (title.length() > 25) {
                        txt_error.setVisibility(View.VISIBLE);
                        txt_error.setText("No puede tener más de 25 caracteres");
                        return;
                    }else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("titulo" ,String.valueOf(editText.getText()));
                        editor.apply(); // O editor.commit();
                        binding.titleG.setText(editText.getText());
                        dialog.dismiss();
                    }

                }
            });


            dialog.show();
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
        void getUser(){
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference("users");
            membersRef.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                            member = memberSnapshot.getValue(Member.class);
                            if(member.getIdModule()!=null){
                                Log.i("miloggggg76",String.valueOf("Tecnicamente si"));
                                keyModuleCurrent = member.getIdModule();

                            }

                            if(member.getGroups()!=null){
                                addGroup(member.getGroups());
                            }
                        }
                    } else {
                        member = null;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    member = null;
                }
            });
        }
        private void addGroup(List<String> groups){
            DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");

            for(String group : groups){
                groupsRef.child(group).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Group grupo = dataSnapshot.getValue(Group.class);
                            if (grupo != null) {
                                groupsUser.add(grupo);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        groupsUser = null;
                    }
                });
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
            handler.post(consultaRunnable);
        }

        private void showCustomPopupMenu(View anchor) {

            PopupWindow popupWindow = new PopupWindow(this);

            LinearLayout popupLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_options_main, null);

            // Agregar elementos al PopupWindow
            popupWindow.setContentView(popupLayout);
            popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);

            for(Group g : groupsUser){
                for(String key : g.getKeyModuls()){
                    addMenuItem(popupLayout,g.getName(),key, popupWindow);
                }

            }



            popupWindow.showAsDropDown(anchor);
        }

        private void addMenuItem(LinearLayout layout, String name, String clave , PopupWindow popupWindow) {
            View menuItem = LayoutInflater.from(this).inflate(R.layout.option_main, layout, false);
            TextView nameT = menuItem.findViewById(R.id.name_o);
            TextView claveT= menuItem.findViewById(R.id.clave_o);

            if (name.length() > 15) {
                name = name.substring(0, 15) + "...";
            }
            nameT.setText(name);

            claveT.setText(ocultarString(clave));


            menuItem.setOnClickListener(v -> {
                //Click en uno de ellos
                keyModuleCurrent = clave;
                popupWindow.dismiss();
            });

            layout.addView(menuItem);
        }
    String ocultarString(String clave){
        if (clave.length() > 5) {
            String visiblePart = clave.substring(0, 5);
            String maskedPart = new String(new char[clave.length() - 5]).replace("\0", "*");
            return visiblePart + maskedPart;
        }
        else{
            return "key";
        }
    }


    void changeTank(String key){
            if(member==null) return;;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userEmail = member.getEmail();

        String nuevoIdGroup = "nuevo_valor_de_idGroup";


        Map<String, Object> updates = new HashMap<>();
        updates.put("idGroup", nuevoIdGroup);


        database.child("users").child(userEmail).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {
                    Log.w("RealtimeDB", "Error al actualizar el campo 'idGroup' para el usuario con email: " + userEmail, e);
                });
    }

}

