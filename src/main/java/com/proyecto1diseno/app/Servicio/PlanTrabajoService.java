package com.proyecto1diseno.app.Servicio;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto1diseno.app.DAO.DBManager;
import com.proyecto1diseno.app.DAO.PlanTrabajoDAO;
import com.proyecto1diseno.app.Modelo.Actividad;

@Service
public class PlanTrabajoService {
    private PlanTrabajoDAO planTrabajoDAO;

    @Autowired
    public PlanTrabajoService() throws SQLException {
        planTrabajoDAO = DBManager.getPlanTrabajoDAO();
    }

    public List<Map<String, Object>> obtenerActividades() throws SQLException {
        return planTrabajoDAO.obtenerActividades();
    }

    public String agregarActividad(Actividad actividad, String user) throws SQLException {
        return planTrabajoDAO.agregarActividad(actividad, user);
    }

    public String marcarActividad(String nombreAct, int estado) throws SQLException {
        return planTrabajoDAO.marcarActividad(nombreAct, estado);
    }

    public List<Map<String, Object>> obtenerComentarios(String nombreAct) {
        return planTrabajoDAO.obtenerComentarios(nombreAct);
    }

    public String agregarComentario(String user, int codigoComent, String mensaje) throws SQLException {
        return planTrabajoDAO.agregarComentario(user, codigoComent, mensaje);
    }
}
