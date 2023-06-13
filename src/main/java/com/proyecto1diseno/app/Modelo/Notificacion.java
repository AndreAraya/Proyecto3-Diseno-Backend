package com.proyecto1diseno.app.Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Notificacion {
    private int idNotificacion;
    private int emisor;
    private LocalDateTime fecha;
    private String contenido;
    private boolean leida;

    public Notificacion(int idNotificacion, int emisor, LocalDateTime fecha, String contenido, boolean leida) {
        this.idNotificacion = idNotificacion;
        this.emisor = emisor;
        this.fecha = fecha;
        this.contenido = contenido;
        this.leida = leida;
    }

    public Notificacion() {
    }

    public int getEmisor() {
        return emisor;
    }

    public void setEmisor(int emisor) {
        this.emisor = emisor;
    }

    public LocalDateTime getFechaHora() {
        return fecha;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fecha = fechaHora;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion; 
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }
}