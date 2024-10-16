package com.example.tank.ui;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tank.R;
import com.example.tank.adapters.GroupsAdapter;
import com.example.tank.adapters.MemberAdapter;
import com.example.tank.databinding.ActivityMainBinding;
import com.example.tank.databinding.ActivityShowGruopsBinding;
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

public class ShowGruopsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    GroupsAdapter memberAdapter;
    private FirebaseAuth mAuth;
    List<Group> groups;
    ActivityShowGruopsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_gruops);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding  =  ActivityShowGruopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());init();


    }
    void init(){
        groups = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference("users");
        membersRef.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                        Member member = memberSnapshot.getValue(Member.class);
                        if (member != null) {
                            List<String> groupIds = member.getGroups();
                            //binding.noGrupos.setText(String.valueOf(groupIds.size()) );
                            if(groupIds==null){
                                binding.noGrupos.setText("No hay grupos");
                            }else{
                                obtenerGruposPorIds(groupIds);
                            }

                        }
                    }
                } else {
                    binding.noGrupos.setVisibility(View.VISIBLE);
                    binding.noGrupos.setText("El usuario no se encuentra en la base de datos");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                binding.noGrupos.setVisibility(View.VISIBLE);
                binding.noGrupos.setText("Error al buscar el usuario");
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void obtenerGruposPorIds(List<String> groupIds) {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");


        for (String groupId : groupIds) {
            groupsRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Group grupo = dataSnapshot.getValue(Group.class);
                        if (grupo != null) {
                            groups.add(grupo);
                            createRecyclerView();
                            binding.noGrupos.setVisibility(View.GONE);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //showEditTitleDialog("Error al obtener grupos ❌");
                    binding.noGrupos.setVisibility(View.VISIBLE);
                    binding.noGrupos.setText("Error al obtener grupos ");
                }
            });
        }
    }
    void createRecyclerView(){
        memberAdapter = new GroupsAdapter(groups);
        recyclerView = binding.rvGroups;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(memberAdapter);

    }
}