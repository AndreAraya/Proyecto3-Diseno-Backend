package com.proyecto1diseno.app.Modelo;

import java.util.Collection;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Actividad {

    private int semanaRealizacion;
    private Tipos tipo;
    private String nombre;
    private LocalDateTime fechaHora;
    private String responsable;
    private Date fechaPublicacion;
    private Collection<Date> fechasRecordatorios;
    private Modalidades modalidad;
    private String enlace;
    private String afiche;
    private Estados estado;

    public Actividad(int semana, String tipoActividad, String nombre, LocalDateTime fechaHora2, String responsableProfesor,
            LocalDate fechaPublicacion, String modalidad, String enlace, String estado) {
        this.semanaRealizacion = semana;
        this.tipo = Tipos.valueOf(tipoActividad);
        this.nombre = nombre;
        this.fechaHora = fechaHora2;
        this.responsable = responsableProfesor;
        this.fechaPublicacion = Date.from(fechaPublicacion.atStartOfDay().toInstant(ZoneOffset.UTC));
        this.modalidad = Modalidades.valueOf(modalidad);
        this.enlace = enlace;
        this.estado = Estados.valueOf(estado);
    }

    public int getSemanaRealizacion() {
        return semanaRealizacion;
    }

    public void setSemanaRealizacion(int semanaRealizacion) {
        this.semanaRealizacion = semanaRealizacion;
    }

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getResponsable(){
        return this.responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Collection<Date> getFechasRecordatorios() {
        return fechasRecordatorios;
    }

    public void setFechasRecordatorios(Collection<Date> fechasRecordatorios) {
        this.fechasRecordatorios = fechasRecordatorios;
    }

    public Modalidades getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidades modalidad) {
        this.modalidad = modalidad;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getAfiche() {
        return afiche;
    }

    public void setAfiche(String afiche) {
        this.afiche = afiche;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }
}