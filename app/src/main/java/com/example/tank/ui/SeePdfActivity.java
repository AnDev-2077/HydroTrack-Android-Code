package com.example.tank.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tank.R;
import com.example.tank.databinding.ActivitySeePdfBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class SeePdfActivity extends AppCompatActivity {

    ActivitySeePdfBinding binding;
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see_pdf);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivitySeePdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pdfView = binding.pdfContainer;
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        String pdfPath = intent.getStringExtra("path");
        String name = intent.getStringExtra("name");
        binding.namePdf.setText(name);
        File pdfFile = new File(pdfPath);

        if (pdfFile.exists()) {

            pdfView.fromFile(pdfFile)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load();
        } else {
            Toast.makeText(this, "El archivo PDF no existe", Toast.LENGTH_SHORT).show();
        }



    }
}