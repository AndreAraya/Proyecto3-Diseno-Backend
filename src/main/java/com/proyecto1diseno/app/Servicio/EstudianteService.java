package com.proyecto1diseno.app.Servicio;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto1diseno.app.DAO.DBManager;
import com.proyecto1diseno.app.DAO.EstudianteDAO;
import com.proyecto1diseno.app.Modelo.Estudiante;

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
}
