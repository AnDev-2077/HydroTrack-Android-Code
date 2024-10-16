package com.example.tank.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DataModule {
    private String porcentaje;
    private String fecha;

    public DataModule(String porcentaje, String fecha) {
        this.porcentaje = porcentaje;
        this.fecha = fecha;
    }
    public DataModule() {

    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
