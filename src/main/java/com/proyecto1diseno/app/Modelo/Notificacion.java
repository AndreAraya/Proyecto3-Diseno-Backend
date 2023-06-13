package com.proyecto1diseno.app.Modelo;

import java.time.LocalDateTime;

public class Notificacion {
    private String emisor;
    private LocalDateTime fechaHora;
    private String contenido;
    private boolean leida;

    public Notificacion(String emisor, LocalDateTime fechaHora, String contenido, boolean leida) {
        this.emisor = emisor;
        this.fechaHora = fechaHora;
        this.contenido = contenido;
        this.leida = leida;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
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
}