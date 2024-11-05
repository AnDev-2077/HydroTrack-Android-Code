package com.example.tank.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.tank.MainActivity;
import com.example.tank.R;
import com.example.tank.adapters.MembersAdpater;
import com.example.tank.databinding.ActivityGroupInformationBinding;
import com.example.tank.domain.Member;
import com.example.tank.domain.MemberGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupInformationActivity extends AppCompatActivity {

    ActivityGroupInformationBinding binding;
    ArrayList<String> emails;
    private FirebaseAuth mAuth;
    String admin;
    String idGrupo;
    RecyclerView recyclerView;
    MembersAdpater memberAdapter;
    List<MemberGroup> memberGroups = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding  =   ActivityGroupInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String titulo = getIntent().getStringExtra("titulo");
        admin = getIntent().getStringExtra("admin");
        idGrupo = getIntent().getStringExtra("idGrupo");
        binding.nameGroup.setText(titulo);

        emails = getIntent().getStringArrayListExtra("emails");
        binding.members.setText("Grupos - "+emails.size()+" miembros");
        init();
    }
    private void init(){
        Glide.with(this)
                .load(R.drawable.groups_img)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(binding.imgGroup);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(!currentUser.getEmail().equals(admin)){
            binding.eliminarG.setVisibility(View.GONE);
            binding.eliminarG.setEnabled(false);
        }else{
            binding.eliminarG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarGrupo();
                }
            });
        }
        createRecyclerView(memberGroups);
        getUsers();
    }
    private void getUsers(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        List<String> emailList = emails;
        Log.i("holsasdfe", emailList.get(0));
        for (String email : emailList) {
            databaseReference.child("users").orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                                    Member member = userSnapshot.getValue(Member.class);
                                    if(member.getEmail().equals(admin)){
                                        memberGroups.add(new MemberGroup(member.getName(), member.getImg(), "Admin"));
                                    }else{
                                        memberGroups.add(new MemberGroup(member.getName(), member.getImg(), "Miembro"));
                                    }

                                    memberAdapter.notifyDataSetChanged();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("FirebaseData", "Error al leer los datos", databaseError.toException());
                        }
                    });
        }


    }
    void createRecyclerView(List<MemberGroup> groups){
        memberAdapter = new MembersAdpater(groups);
        recyclerView = binding.rvIntegrantes;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(memberAdapter);

    }


    void eliminarGrupo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        List<String> emailList = emails;
        int[] completedTasks = {0};

        for (String email : emailList) {

            databaseReference.child("users").orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    Member member = userSnapshot.getValue(Member.class);
                                    String groupIdToRemove = idGrupo;

                                    if (member.getGroups() != null) {

                                        member.getGroups().remove(groupIdToRemove);

                                        userSnapshot.getRef().setValue(member).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d("FirebaseData", "Grupo eliminado con éxito para: " + member.getEmail());
                                            } else {
                                                Log.w("FirebaseData", "Error al eliminar el grupo para: " + member.getEmail(), task.getException());
                                            }


                                            completedTasks[0]++;

                                            if (completedTasks[0] == emailList.size()) {
                                                deleteModule(emails);


                                            
                                            }
                                        });
                                    } else {

                                        completedTasks[0]++;
                                        if (completedTasks[0] == emailList.size()) {
                                            finish(); // Cerrar la actividad
                                        }
                                    }
                                }
                            } else {

                                completedTasks[0]++;
                                if (completedTasks[0] == emailList.size()) {
                                    //finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("FirebaseData", "Error al leer los datos", databaseError.toException());
                        }
                    });
        }
    }
    void deleteModule(List<String> emails) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");


        for (String emailToFind : emails) {

            usersRef.orderByChild("email").equalTo(emailToFind).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                            String userId = userSnapshot.getKey();


                            DatabaseReference userIdModuleRef = usersRef.child(userId).child("idModule");


                            userIdModuleRef.removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {


                                    Log.d("Firebase", "idModule eliminado para el usuario con email: " + emailToFind);
                                } else {

                                    Log.e("Firebase", "Error al eliminar el idModule del usuario con email: " + emailToFind, task.getException());
                                }
                            });
                        }

                    } else {
                        Log.d("Firebase", "Usuario no encontrado con el correo: " + emailToFind);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Error en la búsqueda del usuario con email: " + emailToFind, databaseError.toException());
                }
            });
        }
        MainActivity.keyModuleCurrent = null;
        HomeFragment.befpercentage = -1;
        HomeFragment.percentage = 0;
        Intent intent = new Intent(GroupInformationActivity.this, GroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



}