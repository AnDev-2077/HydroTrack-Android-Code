package com.example.tank.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tank.MainActivity;
import com.example.tank.R;
import com.example.tank.databinding.ActivityStorageBinding;
import com.example.tank.domain.DataModule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageActivity extends AppCompatActivity {

    ActivityStorageBinding binding;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    List<Float> semanas = new ArrayList<>();
    List<List<Float>> semanasPorcentaje = new ArrayList<>();

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

        // Configurar el WebView
        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnReportes.setEnabled(false);
        binding.crateExcel.setEnabled(false);

       /* webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void crearPDF() {
                if (ActivityCompat.checkSelfPermission(StorageActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StorageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnReportes.setEnabled(false);
                    binding.crateExcel.setEnabled(false);
                    crearPDF();
                }
            }
        }, "Android");*/


        binding.back.setOnClickListener(v -> finish());



        binding.btnReportes.setOnClickListener(v -> {
            Intent intent = new Intent(StorageActivity.this, ListPdfActivity.class);
            startActivity(intent);
        });
        obtenerDatos();
    }

    private String getHtmlContent() {
        LocalDate fechaActual = LocalDate.now();

        LocalDate mesAnterior = fechaActual.minusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        String mesAnteriorS = mesAnterior.format(formatter);

        String barras = "";
        String porcent = "";
        int i = 1;
        for (Float s : semanas){
            barras+="<div class=\"barra\"><div class=\"sub_barra b"+i+"\"><div class=\"tag_g\">"+s.toString()+"</div><div class=\"tag_leyenda\">Semana "+i+"</div></div></div>";
            porcent+= ".b"+i+"{ height: "+s.toString()+"%}";
            i++;
        }

        String lineas = "";
        String leyend = "";
        for (int p = 0; p < semanasPorcentaje.size(); p++) {
            List<Float> semana = semanasPorcentaje.get(p);  // Obtienes la lista de porcentajes de la semana i
            Log.i("Semana " + (p + 1), "Porcentajes diarios:");

            float[] valoresDia = {0, 0, 0, 0, 0, 0, 0};

            for (int j = 0; j < semana.size(); j++) {

                int valorBase = 324;
                int valorFinal = 0;
                float porcentaje =  semana.get(j);

                // Calculamos el valor correspondiente
                float valorCorrespondiente = valorBase - ((porcentaje / 100.0f) * (valorBase - valorFinal));

                // Redondeamos el valor a entero
                int valorRedondeado = Math.round(valorCorrespondiente);
                valoresDia[j] = valorRedondeado;
            }

            lineas+= "    <polyline class=\"line line"+(p+1)+"\" points=\"20,"+valoresDia[0]+" 130,"+valoresDia[1]+" 240,"+valoresDia[2] +" 350,"+valoresDia[3] +" 460,"+valoresDia[4]+" 570,"+valoresDia[5]+" 680,"+valoresDia[6]+"\"></polyline>";
            leyend+="     <div><div class=\"cuadro cuadro"+(p+1)+"\"></div><span>Semana "+(p+1)+"</span></div>";
        }

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "    <style>\n" +
                "*{\n" +
                "    font-family: \"Verdana\", sans-serif;\n" +
                "}\n" +
                "body{\n" +
                "    width: 50rem;\n" +
                "  background-color: white;\n" +
                "}\n" +
                "h1{\n" +
                "    text-align: center;\n" +
                "    margin-bottom: 1.875rem; /* 30px */\n" +
                "}\n" +
                ".board{\n" +
                "    margin: auto;\n" +
                "    width: 100%;\n" +
                "    height: 28.125rem; /* 450px */\n" +
                "    background-color: white;\n" +
                "    box-sizing: border-box;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                ".titulo_grafica{\n" +
                "    width: 100%;\n" +
                "    height: 10%;\n" +
                "}\n" +
                ".titulo_grafica>h3{\n" +
                "    padding: 0;\n" +
                "    margin: 0;\n" +
                "    text-align: center;\n" +
                "    color: #666666;\n" +
                "}\n" +
                ".sub_board{\n" +
                "    width: 100%;\n" +
                "    height: 90%;\n" +
                "    padding: 0.625rem; /* 10px */\n" +
                "    margin-top: 0;\n" +
                "    background-color:#f4f4f4;\n" +
                "    overflow: hidden;\n" +
                "    box-sizing: border-box;\n" +
                "  background-color: white" +
                "}\n" +
                ".sep_board{\n" +
                "    width: 100%;\n" +
                "    height: 10%;\n" +
                "  background-color: white"+
                "}\n" +
                ".cont_board{\n" +
                "    width: 100%;\n" +
                "    height: 80%;\n" +
                "  background-color: white"+
                "}\n" +
                ".graf_board{\n" +
                "    width: 92%;\n" +
                "    height: 100%;\n" +
                "    float: right;\n" +
                "    margin-top: 0;\n" +
                "    background-color: white;\n" +
                "    border-left: 0.125rem solid #999999; /* 2px */\n" +
                "    border-bottom: 0.125rem solid #999999; /* 2px */\n" +
                "    box-sizing: border-box;\n" +
                "    display: flex;\n" +
                "    background: linear-gradient(to bottom, rgba(0,0,0,0) 0%,\n" +
                "    rgba(0,0,0,0) 9.5%,  rgba(0,0,0,0.3) 10%, rgba(0,0,0,0) 10.5%, \n" +
                "    rgba(0,0,0,0) 19.5%, rgba(0,0,0,0.3) 20%, rgba(0,0,0,0) 20.5%, \n" +
                "    rgba(0,0,0,0) 29.5%, rgba(0,0,0,0.3) 30%, rgba(0,0,0,0) 30.5%, \n" +
                "    rgba(0,0,0,0) 39.5%, rgba(0,0,0,0.3) 40%, rgba(0,0,0,0) 40.5%, \n" +
                "    rgba(0,0,0,0) 49.5%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0) 50.5%, \n" +
                "    rgba(0,0,0,0) 59.5%, rgba(0,0,0,0.3) 60%, rgba(0,0,0,0) 60.5%, \n" +
                "    rgba(0,0,0,0) 69.5%, rgba(0,0,0,0.3) 70%, rgba(0,0,0,0) 70.5%, \n" +
                "    rgba(0,0,0,0) 79.5%, rgba(0,0,0,0.3) 80%, rgba(0,0,0,0) 80.5%, \n" +
                "    rgba(0,0,0,0) 89.5%, rgba(0,0,0,0.3) 90%, rgba(0,0,0,0) 90.5%, \n" +
                "    rgba(0,0,0,0) 100%);\n" +
                "}\n" +
                ".barra{\n" +
                "    width:100%;\n" +
                "    height: 100%;\n" +
                "    margin-right: 0.9375rem; /* 15px */\n" +
                "    margin-left: 0.9375rem; /* 15px */\n" +
                "    background-color: none;\n" +
                "    display: flex;\n" +
                "    flex-wrap: wrap;\n" +
                "    align-items: flex-end;\n" +
                "}\n" +
                ".sub_barra{\n" +
                "    width: 100%;\n" +
                "    height: 80%;\n" +
                "    background: #00799b;\n" +
                "    background: -moz-linear-gradient(top, #00799b 0%, #64d1be 100%);\n" +
                "    background: -webkit-linear-gradient(top, #00799b 0%,#64d1be 100%);\n" +
                "    background: linear-gradient(to bottom, #00799b 0%,#64d1be 100%);\n" +
                "    filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#00799b', endColorstr='#64d1be',GradientType=0 );\n" +
                "    -webkit-border-radius: 0.1875rem 0.1875rem 0 0; /* 3px */\n" +
                "    border-radius: 0.1875rem 0.1875rem 0 0; /* 3px */\n" +
                "}\n" +
                ".tag_g{\n" +
                "    position: relative;\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    margin-bottom: 1.875rem; /* 30px */\n" +
                "    text-align: center;\n" +
                "    margin-top: -1.25rem; /* -20px */\n" +
                "    z-index: 2;\n" +
                "}\n" +
                ".tag_leyenda{\n" +
                "    width: 100%;\n" +
                "    text-align: center;\n" +
                "    margin-top: 0.3125rem; /* 5px */\n" +
                "}\n" +
                ".tag_board{\n" +
                "    height: 100%;\n" +
                "    width: 5%;\n" +
                "    border-bottom: 0.125rem solid rgba(0,0,0,0); /* 2px */\n" +
                "    box-sizing: border-box;\n" +
                "}\n" +
                ".sub_tag_board{\n" +
                "    height: 100%;\n" +
                "    width: 100%;\n" +
                "    display: flex;\n" +
                "    align-items: flex-end;\n" +
                "    flex-wrap: wrap;\n" +
                "}\n" +
                ".sub_tag_board>div{\n" +
                "    width: 100%;\n" +
                "    height: 10%;\n" +
                "    text-align: right;\n" +
                "    padding-right: 0.625rem; /* 10px */\n" +
                "    box-sizing: border-box;\n" +
                "}\n" + porcent+
                "footer{\n" +
                "    position: absolute;\n" +
                "    bottom: 0;\n" +
                "    width: 100%;\n" +
                "    text-align: center;\n" +
                "    font-size: 0.75rem; /* 12px */\n" +
                "    font-family: sans-serif;\n" +
                "}\n" +
                ".chart-container {\n" +
                "            width: 50rem;\n" +
                "            height: 450px;\n" +
                "            position: relative;\n" +
                "            padding-top: 3rem;\n" +
                "            background-color: white;\n" +
                "\n" +
                "    \n" +
                "        }\n" +
                "        .chart-title {\n" +
                "            text-align: center;\n" +
                "            text-align: center;\n" +
                "            color: #666666;\n" +
                "            font-size: 1.2rem;\n" +
                "            margin-bottom: 1.5rem;\n" +
                "            font-weight: 900;\n" +
                "           \n" +
                "        }\n" +
                "        .chart {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            align-items: flex-end;\n" +
                "            height: 360px;\n" +
                "            margin-left: 20px; /* Márgenes izquierdo */\n" +
                "            margin-right: 20px; /* Márgenes derecho */\n" +
                "         \n" +
                "            \n" +
                "            \n" +
                "        }\n" +
                "        .y-axis {\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            justify-content: space-between;\n" +
                "            height: 92%;\n" +
                "            transform: translateY(-9%);\n" +
                "            width: 3rem;\n" +
                "  \n" +
                "            font-size: 14px;\n" +
                "\n" +
                "        }\n" +
                "      \n" +
                "        .x-axis {\n" +
                "            position: absolute;\n" +
                "            display: flex;\n" +
                "            gap: 4.1rem;\n" +
                "            font-size: 0.8rem;\n" +
                "            padding-top: 10px;\n" +
                "            margin-top: 10px;\n" +
                "            left: 5rem;\n" +
                "        }\n" +
                "        .lines {\n" +
                "            flex-grow: 1;\n" +
                "            position: relative;\n" +
                "        }\n" +
                "        .line {\n" +
                "            fill: none;\n" +
                "   stroke-width: 3; "+
                "        }\n" +
                "        .line1 {\n" +
                "            stroke: #196B24;\n" +
                "        }\n" +
                "        .line2 {\n" +
                "            stroke: #0F9ED5;\n" +
                "        }\n" +
                "        .line3 {\n" +
                "            stroke: #156082;\n" +
                "        }\n" +
                "        .line4 {\n" +
                "            stroke: #E97132;\n" +
                "        }"+
                "        .line5 {\n" +
                "            stroke: #E135FF;\n" +
                "        }"+
                "   .sep_board{\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            font-size: 0.8rem;\n" +

                "        }.sep_board div{\n" +
                "             background-color: #156082;\n" +
                "             width: 0.8rem;\n" +
                "             height: 0.8rem;\n" +
                "             margin-right: 0.5rem;\n" +
                "        }"+
                " .leyen{\n" +
                "            width: 100%;\n" +
                "            font-size: 0.7rem;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            gap: 1rem;\n" +
                "        }\n" +
                "        .leyen div{\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            gap: 0.3rem;\n" +
                "        }\n" +
                "        .leyen .cuadro{\n" +
                "            width: 1.6rem;\n" +
                "            height: 0.3rem;\n" +
                "            border-radius: 0.2rem;\n" +
                "            background-color: red;\n" +
                "        }\n" +
                "        .leyen .cuadro1 {\n" +
                "            background-color: #196B24;\n" +
                "        }"+
                "        .leyen .cuadro2 {\n" +
                "            background-color: #0F9ED5;\n" +
                "        }"+
                "        .leyen .cuadro3 {\n" +
                "            background-color: #156082;\n" +
                "        }"+
                "        .leyen .cuadro4 {\n" +
                "            background-color: #E97132;\n" +
                "        }"+
                "        .leyen .cuadro5 {\n" +
                "            background-color: #E135FF;\n" +
                "        }"+
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <h1>Reporte de consumo mensual ("+mesAnteriorS+")</h1>\n" +
                "    <div class=\"board\">\n" +
                "      \n" +
                "        <div class=\"titulo_grafica\">\n" +
                "            <h3 class=\"t_grafica\">Resumen</h3>\n" +
                "        </div>\n" +
                "        <div class=\"sub_board\">\n" +
                "            <div class=\"sep_board\"></div>\n" +
                "            <div class=\"cont_board\">\n" +
                "                <div class=\"graf_board\">\n" + barras+

                "                </div>\n" +
                "                <div class=\"tag_board\">\n" +
                "                    <div class=\"sub_tag_board\">\n" +
                "                        <div>100%</div>\n" +
                "                        <div>90%</div>\n" +
                "                        <div>80%</div>\n" +
                "                        <div>70%</div>\n" +
                "                        <div>60%</div>\n" +
                "                        <div>50%</div>\n" +
                "                        <div>40%</div>\n" +
                "                        <div>30%</div>\n" +
                "                        <div>20%</div>\n" +
                "                        <div>10</div>\n" +
                "                    </div>\n" +
                "                </div>\n" +

                "       </div>    \n" +
                "    </div>\n" +
                "<footer>\n" +
                "   \n" +
                "</footer>\n" +
                "           </div> \n" +
                " <div class=\"sep_board\">\n" +
                "                <div>\n" +
                "\n" +
                "                </div>\n" +
                "                <span>\n" +
                "                    Porcentaje de Consumo \n" +
                "                </span>\n" +
                "            </div>"+
                "<div class=\"chart-container\">\n" +
                "        <div class=\"chart-title\">Consumo por semana</div>\n" +
                "        <div class=\"chart\">\n" +
                "            <div class=\"y-axis\">\n" +
                "                <div>10%</div>\n" +
                "                <div>9%</div>\n" +
                "                <div>8%</div>\n" +
                "                <div>7%</div>\n" +
                "                <div>6%</div>\n" +
                "                <div>5%</div>\n" +
                "                <div>4%</div>\n" +
                "                <div>3%</div>\n" +
                "                <div>2%</div>\n" +
                "                <div>1%</div>\n" +
                "                <div>0%</div>\n" +
                "            </div>\n" +
                "            <svg width=\"640\" height=\"360\" class=\"lines\"> <!-- Aumento el ancho del SVG -->\n" +
                "                <!-- Líneas horizontales -->\n" +
                "                <line x1=\"0\" y1=\"0\" x2=\"1000\" y2=\"0\" stroke=\"#ccc\" stroke-width=\"1\"></line> <!-- Aumento el grosor de las líneas de la cuadrícula -->\n" +
                "                <line x1=\"0\" y1=\"36\" x2=\"1000\" y2=\"36\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"72\" x2=\"700\" y2=\"72\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"108\" x2=\"1000\" y2=\"108\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"144\" x2=\"1000\" y2=\"144\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"180\" x2=\"1000\" y2=\"180\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"216\" x2=\"1000\" y2=\"216\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"252\" x2=\"1000\" y2=\"252\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"288\" x2=\"1000\" y2=\"288\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <line x1=\"0\" y1=\"324\" x2=\"1000\" y2=\"324\" stroke=\"#ccc\" stroke-width=\"1\"></line>\n" +
                "                <!-- Gráfico de líneas -->\n" +
               lineas+
                "                \n" +
                "                \n" +
                "                \n" +
                "                \n" +
                "                \n" +
                "            </svg>\n" +
                "            <div class=\"x-axis\">\n" +
                "                <div>Lunes</div>\n" +
                "                <div>Martes</div>\n" +
                "                <div>Miércoles</div>\n" +
                "                <div>Jueves</div>\n" +
                "                <div>Viernes</div>\n" +
                "                <div>Sábado</div>\n" +
                "                <div>Domingo</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "      \n" +
                "    </div>\n" +
                " <div class=\"leyen\">\n" +
                leyend+
                "    </div>"+
                "</body>\n" +
                "</html>\n";



    
    }

    private void crearPDF() {

        WebView webView = findViewById(R.id.web_view);

        webView.postDelayed(new Runnable() {
            @Override
            public void run() {

                Picture picture = webView.capturePicture();
                Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                picture.draw(canvas);


                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);


                canvas = page.getCanvas();
                canvas.drawBitmap(bitmap, 0, 0, null);
                document.finishPage(page);

                String uniqueCode = String.valueOf(System.currentTimeMillis());
                String pdfFileName = "Rpt_" + uniqueCode + ".pdf";
                File file = new File(getExternalFilesDir(null), pdfFileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    document.writeTo(fos);
                    Toast.makeText(StorageActivity.this, "PDF generado con éxito", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnReportes.setEnabled(true);
                    binding.crateExcel.setEnabled(true);

                    Intent intent = new Intent(StorageActivity.this, SeePdfActivity.class);
                    intent.putExtra("path", file.getAbsolutePath());
                    intent.putExtra("name", file.getName()) ;
                    startActivity(intent);
                } catch (IOException e) {
                    Toast.makeText(StorageActivity.this, "Error al generar el PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnReportes.setEnabled(true);
                    binding.crateExcel.setEnabled(true);
                }

                document.close();
            }
        }, 1000);
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



    public void obtenerDatos() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ModulesWifi/" + MainActivity.keyModuleCurrent);

        ref.orderByChild("fecha").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, DataModule> latestDataByDate = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataModule data = snapshot.getValue(DataModule.class);

                    if (data != null) {
                        String fecha = data.getFecha();


                        latestDataByDate.put(fecha, data);
                    }
                }

                List<DataModule> listaFinal = new ArrayList<>(latestDataByDate.values());
                procesarSemanas(listaFinal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error al leer datos: " + databaseError.getMessage());
            }
        });
    }
    public void procesarSemanas(List<DataModule> listaFinal) {





        Map<String, Float> porcentajePorFecha = new HashMap<>();
        for (DataModule data : listaFinal) {
            porcentajePorFecha.put(data.getFecha(), 100 - Float.parseFloat(data.getPorcentaje()));
        }

        LocalDate fechaActual = LocalDate.now();
        LocalDate primerDiaMesAnterior = fechaActual.minusMonths(1).withDayOfMonth(1);
        LocalDate ultimoDiaMesAnterior = primerDiaMesAnterior.withDayOfMonth(primerDiaMesAnterior.lengthOfMonth());

        Map<Integer, Float> sumaPorSemana = new HashMap<>();
        float sumaTotal = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        Map<Integer, List<Float>> porcentajesPorSemana = new HashMap<>();

        for (Map.Entry<String, Float> entry : porcentajePorFecha.entrySet()) {
            String fechaStr = entry.getKey();
            Float valor = entry.getValue();
            LocalDate fecha = LocalDate.parse(fechaStr, formatter);

            if (fecha.isAfter(primerDiaMesAnterior.minusDays(1)) && fecha.isBefore(ultimoDiaMesAnterior.plusDays(1))) {
                int semana = fecha.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

                sumaPorSemana.put(semana, sumaPorSemana.getOrDefault(semana, 0f) + valor);
                sumaTotal += valor;


                porcentajesPorSemana.putIfAbsent(semana, new ArrayList<>());
                porcentajesPorSemana.get(semana).add(valor);
            }
        }

        int semanaInicioMes = primerDiaMesAnterior.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int semanaFinMes = ultimoDiaMesAnterior.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        for (int semana = semanaInicioMes; semana <= semanaFinMes; semana++) {
            sumaPorSemana.putIfAbsent(semana, 0f);
        }

        for (Map.Entry<Integer, List<Float>> entry : porcentajesPorSemana.entrySet()) {
            int semana = entry.getKey();
            List<Float> porcentajesDiarios = entry.getValue();


            List<Float> semanaValores = new ArrayList<>(Collections.nCopies(7, 0f));


            LocalDate fechaSemana = primerDiaMesAnterior.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(semana - semanaInicioMes);
            DayOfWeek primerDiaSemana = fechaSemana.getDayOfWeek();


            for (int i = 0; i < porcentajesDiarios.size(); i++) {

                int diaSemana = (primerDiaSemana.getValue() + i) % 7;
                semanaValores.set(diaSemana, Math.round(porcentajesDiarios.get(i) * 10) / 10f);
            }


            semanasPorcentaje.add(semanaValores);
        }


        for (int i = 0; i < semanasPorcentaje.size(); i++) {
            List<Float> semana = semanasPorcentaje.get(i);
            Log.i("Semana " + (i + 1), "Valores de porcentaje de cada día: " + semana);
        }


        for (int i = 0; i < semanasPorcentaje.size(); i++) {
            List<Float> semana = semanasPorcentaje.get(i);
            float sumaSemana = 0f;

            // Sumar los valores de la semana
            for (float valor : semana) {
                sumaSemana += valor;
            }


            float porcentaje = (sumaSemana / sumaTotal) * 100;
            semanas.add(Math.round(porcentaje * 10) / 10f);
        }


        for (int i = 0; i < semanas.size(); i++) {
            Log.i("Porcentaje Semana " + (i + 1), "Porcentaje total: " + semanas.get(i) + "%");
        }



        binding.progressBar.setVisibility(View.GONE);
        binding.btnReportes.setEnabled(true);
        binding.crateExcel.setEnabled(true);
        //
        binding.crateExcel.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(StorageActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StorageActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            } else {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnReportes.setEnabled(false);
                binding.crateExcel.setEnabled(false);
                crearPDF();
            }
        });

        binding.webView.loadDataWithBaseURL(null, getHtmlContent(), "text/html", "UTF-8", null);


    }
}
