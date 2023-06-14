package com.proyecto1diseno.app.Servicio;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto1diseno.app.DAO.DBManager;
import com.proyecto1diseno.app.DAO.EstudianteDAO;
import com.proyecto1diseno.app.Modelo.Estudiante;
import com.proyecto1diseno.app.Modelo.Notificacion;

@Service
public class EstudianteService {

    private EstudianteDAO estudianteDAO;

    @Autowired
    public EstudianteService() throws SQLException {
        estudianteDAO = DBManager.getEstudianteDAO();
    }

    public List<Map<String, Object>> obtenerEstudiantes(String user) throws SQLException {
        EstudianteDAO estudianteDAO = DBManager.getEstudianteDAO();
        List<Map<String, Object>> estudiantes = estudianteDAO.obtenerEstudiantes(user);
        return estudiantes;
    }

    public Estudiante getEstudiante(String carnetEst) throws SQLException {
        Estudiante estudianteEncontrado = estudianteDAO.getEstudiante(carnetEst);

        if (estudianteEncontrado == null) {
            throw new NoSuchElementException("Estudiante no encontrado");
        }

        return estudianteEncontrado;
    }
    
    public String modificarEstudiante(Estudiante estudiante) throws SQLException {
        return estudianteDAO.modificarEstudiante(estudiante);
    }

    public String insertarEstudiante(Estudiante estudiante) throws SQLException{
        return estudianteDAO.insertarEstudiante(estudiante);
    }

    public Optional<Estudiante> validarCredenciales(String correo, String carne) throws SQLException {
        return estudianteDAO.validarCredenciales(correo, carne);
    }

    public String modificarEstudiante2(String correo, String celular) throws SQLException {
        return estudianteDAO.modificarEstudiante2(correo, celular);
    }

    public Estudiante getEstudiante2(String correo) throws SQLException {
        Estudiante estudianteEncontrado = estudianteDAO.getEstudiante2(correo);

        if (estudianteEncontrado == null) {
            throw new NoSuchElementException("Estudiante no encontrado");
        }

        return estudianteEncontrado;
    }

    public String mostrarCelular(String correo) throws SQLException {
        return estudianteDAO.mostrarCelular(correo);
    }

    public String subscribirObservador(String user) {
        return estudianteDAO.subscribirObservador(user);
    }

    public List<Map<String, Object>> obtenerNotificaciones(String user) {
        return estudianteDAO.obtenerNotificaciones(user);
    }

    public String agregarNotificacion(Notificacion notificacion, String user) {
        return estudianteDAO.agregarNotificacion(notificacion, user);
    }

    public void notificar(String observadorUser, Notificacion notificacion) {
        estudianteDAO.notificar(observadorUser, notificacion);
    }

    public String eliminarNotificacion(int idNotificacion, String user) {
        return estudianteDAO.eliminarNotificacion(idNotificacion, user);
    }

    public String eliminarNotificaciones(String user) {
       return estudianteDAO.eliminarNotificaciones(user);
    }

    public String desubscribirObservador(String user) {
        return estudianteDAO.desuscribirObservador(user);
    }

    public String marcarNotificacionLeida(String user, String codigoNotif) {
        return estudianteDAO.marcarNotificacionLeida(user, codigoNotif);
    }
}
