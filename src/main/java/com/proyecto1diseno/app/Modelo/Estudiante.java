package com.proyecto1diseno.app.Modelo;

public class Estudiante {
    private String idSede;
    private int carnet;
    private String nombre;
    private String segundoNombre;
    private String apellido1;
    private String apellido2;
    private String correo;
    private int celular;
    private String contrasena;

    public Estudiante(String idSede, int carnet, String nombre, String segundoNombre, String apellido1, String apellido2, String correo, int celular, String contrasena) {
        this.idSede = idSede;
        this.carnet = carnet;
        this.nombre = nombre;
        this.segundoNombre = segundoNombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.correo = correo;
        this.celular = celular;
        this.contrasena = contrasena;
    }

    public Estudiante (){
        
    }

    public String getIdSede() {
        return idSede;
    }

    public void setIdSede(String idSede) {
        this.idSede = idSede;
    }

    public int getCarnet() {
        return carnet;
    }

    public void setCarnet(int carnet) {
        this.carnet = carnet;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getCelular() {
        return celular;
    }

    public void setCelular(int celular) {
        this.celular = celular;
    }

    public String getContrasena(){
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void imprimirDatos() {

        System.out.println();
        System.out.println(this.idSede);
        System.out.println(this.carnet);
        System.out.println(this.nombre);
        System.out.println(this.segundoNombre);
        System.out.println(this.apellido1);
        System.out.println(this.apellido2);
        System.out.println(this.correo);
        System.out.println(this.celular);
        System.out.println(this.contrasena);
        System.out.println();
    }

}