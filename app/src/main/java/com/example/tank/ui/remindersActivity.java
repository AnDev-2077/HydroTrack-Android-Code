package com.example.tank.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tank.R;
import com.example.tank.databinding.ActivityListPdfBinding;
import com.example.tank.databinding.ActivityRemindersBinding;
import com.example.tank.notifications.NotificationReceiver;

import java.util.Calendar;

public class remindersActivity extends AppCompatActivity {

    ActivityRemindersBinding binding;
    Calendar selectedCalendar;
    int currentDay;
    int currentMonth;
    int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityRemindersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("reminders_prefs", MODE_PRIVATE);

        // Restaurar valores guardados
        boolean isSwitchEnabled = sharedPreferences.getBoolean("switch_state", false);
        int savedYear = sharedPreferences.getInt("selected_year", -1);
        int savedMonth = sharedPreferences.getInt("selected_month", -1);
        int savedDay = sharedPreferences.getInt("selected_day", -1);

        // Configurar la interfaz según los valores restaurados
        if (isSwitchEnabled && savedYear != -1 && savedMonth != -1 && savedDay != -1) {
            String restoredDate = savedDay + "/" + (savedMonth + 1) + "/" + savedYear;
            binding.txtFecha.setText(restoredDate);
            selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(savedYear, savedMonth, savedDay);
        } else {
            binding.txtFecha.setText("- / - / ----");
        }
        binding.swit.setChecked(isSwitchEnabled);

        binding.calendar.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            Calendar minDateCalendar = Calendar.getInstance();
            minDateCalendar.add(Calendar.MONTH, -3);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    remindersActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        binding.txtFecha.setText(selectedDate);
                        currentDay = selectedDay;
                        currentMonth = selectedMonth + 1;
                        currentYear = selectedYear;

                        selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                        Toast.makeText(remindersActivity.this, "Fecha seleccionada: " + selectedDate, Toast.LENGTH_SHORT).show();
                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTimeInMillis());
            datePickerDialog.show();
        });

        binding.swit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isChecked) {
                // Si el Switch está activado, guarda la fecha seleccionada
                if (!binding.txtFecha.getText().toString().equals("- / - / ----")) {
                    editor.putBoolean("switch_state", true);
                    editor.putInt("selected_year", currentYear);
                    editor.putInt("selected_month", currentMonth - 1); // Calendar usa meses desde 0
                    editor.putInt("selected_day", currentDay);
                    editor.apply();
                    scheduleNotification(selectedCalendar);
                    Toast.makeText(remindersActivity.this, "Notificación en 3 meses", Toast.LENGTH_SHORT).show();
                } else {
                    binding.swit.setChecked(false);
                    Toast.makeText(remindersActivity.this, "Selecciona una fecha antes de activar el switch", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Si el Switch está desactivado, elimina los valores guardados
                editor.putBoolean("switch_state", false);
                editor.remove("selected_year");
                editor.remove("selected_month");
                editor.remove("selected_day");
                editor.apply();
                binding.txtFecha.setText("- / - / ----");
                Toast.makeText(remindersActivity.this, "Fecha eliminada", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para regresar
        binding.back.setOnClickListener(v -> finish());
    }


    private void scheduleNotification(Calendar targetCalendar) {
        Intent intent = new Intent(this, NotificationReceiver.class);  // Asumiendo que NotificationReceiver es tu BroadcastReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCalendar.getTimeInMillis(), pendingIntent);
        }
    }
}