package com.proyecto1diseno.app.Modelo;

public class Profesor {
    private int idProfesor;
    private String codigo;
    private String nombre;
    private String idSede;
    private String correo;
    private String contrasena;
    private int telOficina;
    private int celular;
    private String fotografia;

    public Profesor(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public Profesor() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTelOficina() {
        return telOficina;
    }

    public void setTelOficina(int telOficina) {
        this.telOficina = telOficina;
    }

    public int getCelular() {
        return celular;
    }

    public void setCelular(int celular) {
        this.celular = celular;
    }

    public String getFotografia() {
        return fotografia;
    }

    public void setFotografia(String fotografia) {
        this.fotografia = fotografia;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }      

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getIdSede() {
        return idSede;
    }

    public void setIdSede(String idSede) {
        this.idSede = idSede;
    }

    public int getIdProfesor() {       
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) { 
        this.idProfesor = idProfesor;  
    }


}