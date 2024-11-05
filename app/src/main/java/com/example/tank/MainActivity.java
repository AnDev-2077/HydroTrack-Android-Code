package com.example.tank;

import android.app.Dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tank.databinding.ActivityMainBinding;
import com.example.tank.domain.DataModule;
import com.example.tank.domain.Group;
import com.example.tank.domain.Member;

import com.example.tank.ui.HistoryFragment;
import com.example.tank.ui.SettingsFragment;
import com.example.tank.ui.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {

        ActivityMainBinding binding;
        private static final int PERMISSION_REQUEST_CODE = 123;
        Dialog dialog;
        SharedPreferences sharedPreferences;
       public Member member = null;
        List<Group> groupsUser = new ArrayList<>();
        public static String keyModuleCurrent = null;
         public FirebaseAuth mAuth;
         public DataModule currentDataModule = null;

        public DatabaseReference databaseReferenceG;
        private Handler handler = new Handler(Looper.getMainLooper());
        private Runnable consultaRunnable;
        public String currentIdGroup = null;


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

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            } else {


            }
        }
        private void init(){

            getUser();
            binding.editTitle.setVisibility(View.GONE);
            binding.editTitle.setEnabled(false);

            binding.claveKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showCustomPopupMenu(v);
                }
            });
        }


        public void showEditTitleDialog() {

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

                        //logica guardar titulo

                        String groupId = currentIdGroup;

                        String newName = editText.getText().toString();


                        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupId);


                        groupRef.child("name").setValue(newName).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                            } else {

                            }
                        });
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
                                keyModuleCurrent = member.getIdModule();
                                HomeFragment.befpercentage = -1;
                                HomeFragment.obtenerDatos();
                            }

                            if(member.getGroups()!=null){
                                addGroup(member.getGroups());
                                addTitle();
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
        private void addTitle(){
            DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");

            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Group grupo = snapshot.getValue(Group.class);
                        Log.i("milo78451", keyModuleCurrent);
                        binding.titleG.setText(grupo.getName());
                        HomeFragment.bloquear = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Database/Error", "Error al leer el grupo: " + databaseError.getMessage());
                    HomeFragment.bloquear = false;
                }
            });


        }

        private void addGroup(List<String> groups){
            DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");
            groupsUser.clear();
            int totalGroups = groups.size();
            AtomicInteger groupsProcessed = new AtomicInteger(0);

            for (String group : groups) {
                groupsRef.child(group).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Group grupo = dataSnapshot.getValue(Group.class);
                            if (grupo != null) {
                                groupsUser.add(grupo);
                                //Solo del primero
                                HomeFragment.mililitros =Integer.parseInt(grupo.getMl());

                            }
                        }


                        if (groupsProcessed.incrementAndGet() == totalGroups) {
                            //si
                            for(Group grup : groupsUser){
                                for(String mod : grup.getKeyModuls()){
                                    if(mod.equals(keyModuleCurrent)){
                                        mAuth = FirebaseAuth.getInstance();
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if(grup.getAdmin().equals(currentUser.getEmail())){
                                            binding.editTitle.setVisibility(View.VISIBLE);
                                            binding.editTitle.setEnabled(true);
                                            currentIdGroup = grup.getId();
                                            binding.editTitle.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showEditTitleDialog();
                                                }
                                            });
                                            return;
                                        }

                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        groupsUser = null;
                        if (groupsProcessed.incrementAndGet() == totalGroups) {


                        }
                    }
                });
            }



        }
        private void initTabLayaut(){

            replaceFragment(new HomeFragment());
            binding.btnNavegation.setOnItemSelectedListener(item -> {
                if(item.getItemId()==R.id.home){
                    replaceFragment(new HomeFragment());
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
                changeModule(clave);

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
    void changeModule(String clave){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String emailToFind = currentUser.getEmail();

        String newIdModule = clave;


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(emailToFind).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();

                        DatabaseReference userIdModuleRef = usersRef.child(userId).child("idModule");


                        userIdModuleRef.setValue(newIdModule).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("Firebase", "Error al actualizar el idModule del usuario.", task.getException());
                            }
                        });
                    }

                } else {
                    Log.d("Firebase", "Usuario no encontrado con el correo proporcionado.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error en la búsqueda del usuario.", databaseError.toException());
            }
        });

    }


}

