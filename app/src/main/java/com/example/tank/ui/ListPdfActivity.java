package com.example.tank.ui;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tank.R;
import com.example.tank.adapters.AdapterPDF;
import com.example.tank.adapters.GroupsAdapter;
import com.example.tank.databinding.ActivityListPdfBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListPdfActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterPDF adapter;
    ActivityListPdfBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_pdf);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding  = ActivityListPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createRecyclerView();
    }
    void createRecyclerView(){
        adapter = new AdapterPDF(getPdfFiles());
        recyclerView = binding.rvPdf;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
    private List<File> getPdfFiles() {
        List<File> pdfList = new ArrayList<>();
        File dir = getExternalFilesDir(null);
        if (dir != null) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".pdf"));
            if (files != null) {
                Collections.addAll(pdfList, files);
            }
        }
        return pdfList;
    }

}