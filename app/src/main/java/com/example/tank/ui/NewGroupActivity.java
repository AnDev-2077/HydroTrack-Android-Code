package com.example.tank.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tank.MainActivity;
import com.example.tank.R;
import com.example.tank.adapters.MemberAdapter;
import com.example.tank.databinding.ActivityMainBinding;
import com.example.tank.databinding.ActivityNewGroupBinding;
import com.example.tank.domain.Group;
import com.example.tank.domain.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NewGroupActivity extends AppCompatActivity {

    ActivityNewGroupBinding binding;
    Dialog dialog;
    List<Member> members = new ArrayList<>();
    RecyclerView recyclerView;
    MemberAdapter memberAdapter;
    private FirebaseAuth mAuth;
    private boolean claveVerificada = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding  = ActivityNewGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.txtGroupName.requestFocus();

        createRecyclerView();
        init();


    }
    void createRecyclerView(){
        memberAdapter = new MemberAdapter(members);
        recyclerView = binding.rvMembers;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(memberAdapter);

    }
    void init(){
        mAuth = FirebaseAuth.getInstance();


        Glide.with(this)
                .load(R.drawable.subir_foto)
                .circleCrop()
                .into(binding.imgGroup);
        binding.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.txtEmail.getText().toString();
                if (name.isEmpty()) {
                    showEditTitleDialog("Ingrese un Email ⚠\uFE0F");
                    return;
                }

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                String searchEmail = binding.txtEmail.getText().toString();

                database.child("users").orderByChild("email").equalTo(searchEmail)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Member user = snapshot.getValue(Member.class);

                                        for (Member member : members) {
                                            if (member.getEmail().equals(user.getEmail())) {
                                                showEditTitleDialog("El usuario ya está agregado ⚠\uFE0F");
                                                return;
                                            }
                                        }
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if(currentUser.getEmail().equals(user.getEmail())){
                                            showEditTitleDialog("Tú ya estás incluido por defecto ⚠\uFE0F");
                                            return;
                                        }
                                        members.add(user);
                                        memberAdapter.notifyItemInserted(members.size() - 1);
                                        binding.numMiembros.setText(String.valueOf(members.size()));
                                    }
                                } else {
                                    showEditTitleDialog("No se encontró el usuario");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //Log.w("RealtimeDB", "Error al buscar el usuario", databaseError.toException());
                                showEditTitleDialog("Error al buscar el usuario");
                            }
                        });

            }
        });
        binding.btnVerificarKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.txtClave.getText().toString().trim().isEmpty()) {
                    showEditTitleDialog("Clave vacía ❌");
                    return;
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ModulesWifi");
                String moduleId = binding.txtClave.getText().toString().trim(); // Obtener el texto del EditText correctamente.
                databaseReference.child(moduleId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            showEditTitleDialog("Clave correcta ✅");
                            claveVerificada = true;
                            binding.txtClave.setEnabled(false);
                            binding.txtClave.setFocusable(false);
                            binding.txtClave.setFocusableInTouchMode(false);
                            binding.btnVerificarKey.setEnabled(false);

                        } else {
                            showEditTitleDialog("La clave no es válida ❌");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showEditTitleDialog("Error al buscar la clave ❌");
                    }
                });

            }
        });
        binding.btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.txtGroupName.getText().toString();
                if (name.isEmpty()) {
                    showEditTitleDialog("El nombre no puede estar vacío ⚠\uFE0F");
                    return;
                }
                else if (name.length() < 3) {
                    showEditTitleDialog("El nombre debe tener al menos 3 caracteres ⚠\uFE0F");
                    return;
                }
                else  if (name.length() > 25) {
                    showEditTitleDialog("El nombre no puede tener más de 25 caracteres ⚠\uFE0F");
                    return;
                }


                //Verificar clave del producto
                if(!claveVerificada){
                    showEditTitleDialog("Verificar clave ⚠\uFE0F");
                    return;
                }
                //

                FirebaseUser currentUser = mAuth.getCurrentUser();
                Member admin = new Member(currentUser.getEmail(),currentUser.getPhotoUrl().toString(),currentUser.getDisplayName());
                members.add(admin);

                if(members.size()<2){
                    showEditTitleDialog("Agregar al menos 1 miembro");
                    return;
                }
                List<String> emails = new ArrayList<>();
                for(Member member: members){
                    emails.add(member.getEmail());
                }
                List<String> moduls = new ArrayList<>();
                moduls.add(binding.txtClave.getText().toString());
                addGroupToFirebase(name, moduls,  emails, admin.getEmail(),binding.txtClave.getText().toString());

            }
        });

        /*atras*/
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
    }
    public void addGroupToFirebase(String name, List<String> moduls, List<String> emails, String admin,String modul) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String groupId = databaseReference.child("groups").push().getKey();
        Group group = new Group(groupId, name,  emails, admin,moduls );

        updateUserGroups(emails,groupId,modul);
        if (groupId != null) {
            databaseReference.child("groups").child(groupId).setValue(group)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(NewGroupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showEditTitleDialog("Error al crear el grupo");
                        }
                    });
        }
    }

    private void showEditTitleDialog(String error) {

        dialog = new Dialog(NewGroupActivity.this);
        dialog.setContentView(R.layout.dialog_no_found);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        Button btnCancelar = dialog.findViewById(R.id.btn_ce);

        TextView textView = dialog.findViewById(R.id.message);
        textView.setText(error);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
    private void updateUserGroups(List<String> emails, String groupId,String modul) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        for (String email : emails) {
            // Crear una consulta para encontrar el usuario por email
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                        String userId = userSnapshot.getKey();

                        // Obtener la lista actual de grupos
                        List<String> groups = new ArrayList<>();
                        if (userSnapshot.child("groups").getValue() != null) {
                            // Obtener la lista actual de grupos
                            groups = (List<String>) userSnapshot.child("groups").getValue();
                        }



                        if (groups.isEmpty()) {
                            usersRef.child(userId).child("idModule").setValue( modul)
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            showEditTitleDialog("Error al actualizar el idGroup del usuario con email: " + email);
                                        }
                                    });
                        }
                        groups.add(groupId);


                        // Actualizar la lista de grupos del usuario
                        usersRef.child(userId).child("groups").setValue(groups)
                                .addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        showEditTitleDialog("Error al agregar el groupId a la lista de grupos del usuario con email: " + email);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showEditTitleDialog("Error al buscar el usuario por email");
                }
            });
        }
    }
}