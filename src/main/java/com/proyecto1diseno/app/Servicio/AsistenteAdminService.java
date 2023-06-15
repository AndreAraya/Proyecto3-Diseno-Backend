package com.proyecto1diseno.app.Servicio;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto1diseno.app.DAO.AsistenteAdminDAO;
import com.proyecto1diseno.app.DAO.DBManager;
import com.proyecto1diseno.app.DAO.EstudianteDAO;
import com.proyecto1diseno.app.Modelo.Actividad;
import com.proyecto1diseno.app.Modelo.AsistenteAdmin;
import com.proyecto1diseno.app.Modelo.EquipoGuia;
import com.proyecto1diseno.app.Modelo.Notificacion;
import com.proyecto1diseno.app.Modelo.PlanTrabajo;
import com.proyecto1diseno.app.Modelo.Profesor;

@Service
public class AsistenteAdminService {

    EquipoGuia equipoGuia;
    Actividad actividad;
    PlanTrabajo planTrabajo;
    Collection<Profesor> profesores;

    private AsistenteAdminDAO asistenteDAO;

    @Autowired
    public AsistenteAdminService() throws SQLException {
        asistenteDAO = DBManager.getAsistenteAdminDAO();
    }

    public Optional<AsistenteAdmin> validarCredenciales(String correo, String contrasena) throws SQLException {
        return asistenteDAO.validarCredenciales(correo, contrasena);
    }

    public String subscribirObservador(String user) {
        return asistenteDAO.subscribirObservador(user);
    }

    public List<Map<String, Object>> obtenerNotificaciones(String user) {
        return asistenteDAO.obtenerNotificaciones(user);
    }

    public String agregarNotificacion(Notificacion notificacion, String user) {
        return asistenteDAO.agregarNotificacion(notificacion, user);
    }

    public void notificar(String observadorUser, Notificacion notificacion) {
        asistenteDAO.notificar(observadorUser, notificacion);
    }

    public String eliminarNotificacion(int idNotificacion, String user) {
        return asistenteDAO.eliminarNotificacion(idNotificacion, user);
    }

    public String eliminarNotificaciones(String user) {
        return asistenteDAO.eliminarNotificaciones(user);
    }

    public String desubscribirObservador(String user) {
        return asistenteDAO.desuscribirObservador(user);
    }

    public String marcarNotificacionLeida(String user, String codigoNotif) {
        return asistenteDAO.marcarNotificacionLeida(user, codigoNotif);
    }

    public String marcarNotificacionNoLeida(String user, String codigoNotif) {
        return asistenteDAO.marcarNotificacionNoLeida(user, codigoNotif);
    }
}
