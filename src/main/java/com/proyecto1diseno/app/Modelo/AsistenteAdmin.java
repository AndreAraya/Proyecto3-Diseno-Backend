package com.proyecto1diseno.app.Modelo;

public class AsistenteAdmin {
    private int codigo;
    private String nombre;
    private String idSede;
    private String correo;
    private String contraseña;
    private int celular;

    public AsistenteAdmin(int codigo, String nombre, String idSede, String correo, String contraseña, int celular) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.idSede = idSede;
        this.correo = correo;
        this.contraseña = contraseña;
        this.celular = celular;
    }

    public AsistenteAdmin(String correo, String contraseña) {
        this.correo = correo;
        this.contraseña = contraseña;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getContraseña()  {   
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getIdSede() {
            return idSede;
    }
    
    public void setIdSede(String idSede) {
        this.idSede = idSede;
    }

            
}