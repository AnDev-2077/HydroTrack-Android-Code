package com.example.tank.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tank.R;
import com.example.tank.databinding.ActivityStorageBinding;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageActivity extends AppCompatActivity {

    ActivityStorageBinding binding;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_storage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding  =     ActivityStorageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;

            }
        });
        binding.crateExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(StorageActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StorageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                } else {
                    crearPDF();
                }
            }
        });
        binding.btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StorageActivity.this,ListPdfActivity.class);
                startActivity(intent);
            }
        });
    }
    private void crearPDF(){
        String htmlContent = "<html><head><style>" +
                "body { font-family: Arial; margin: 20px; }" +
                "h1 { color: blue; }" +
                "p { font-size: 14px; }" +
                "</style></head>" +
                "<body>" +
                "<h1>Hola, mundo!</h1>" +
                "<p>Este es un PDF generado desde HTML y CSS.</p>" +
                "</body></html>";


        String uniqueCode = String.valueOf(System.currentTimeMillis());
        String pdfFileName = "Rpt_" + uniqueCode + ".pdf";

        File pdfFile = new File(getExternalFilesDir(null), pdfFileName);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
            HtmlConverter.convertToPdf(htmlContent, fileOutputStream);
            fileOutputStream.close();
            Toast.makeText(this, "PDF creado en: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                crearPDF();
            } else {
                Toast.makeText(this, "Permiso denegado para escribir en almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

}