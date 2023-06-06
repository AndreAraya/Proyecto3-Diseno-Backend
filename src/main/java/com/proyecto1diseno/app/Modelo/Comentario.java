package com.proyecto1diseno.app.Modelo;

import java.util.Collection;
import java.time.LocalDateTime;

public class Comentario {
    private String comentario;
    private Collection<String> respuestas;
    private Profesor emisor;
    private LocalDateTime fechaHora;

    public Comentario(String comentario, Collection<String> respuestas, Profesor emisor, LocalDateTime fechaHora) {
        this.comentario = comentario;
        this.respuestas = respuestas;
        this.emisor = emisor;
        this.fechaHora = fechaHora;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Collection<String> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(Collection<String> respuestas) {
        this.respuestas = respuestas;
    }

    public Profesor getEmisor() {
        return emisor;
    }

    public void setEmisor(Profesor emisor) {
        this.emisor = emisor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}