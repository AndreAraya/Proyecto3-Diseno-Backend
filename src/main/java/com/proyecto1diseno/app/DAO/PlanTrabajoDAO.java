package com.proyecto1diseno.app.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lombok.extern.slf4j.Slf4j;


import com.proyecto1diseno.app.Modelo.Actividad;

@Slf4j
public class PlanTrabajoDAO {
    private final Connection connection;

    public PlanTrabajoDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Map<String, Object>> obtenerActividades() throws SQLException {
        List<Map<String, Object>> actividades = new ArrayList<>();

        String query = "SELECT semana, nombre, idTipoActividad, fechaHora FROM Actividades";
        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Map<String, Object> actividad = new HashMap<>();
            actividad.put("semana", resultSet.getString("semana"));
            actividad.put("nombre", resultSet.getString("nombre"));

            int idTipoActividad = resultSet.getInt("idTipoActividad");
            String tipoActividad = obtenerTipoActividad(idTipoActividad);

            actividad.put("tipoAct", tipoActividad);
            actividad.put("FechaHora", resultSet.getTimestamp("fechaHora"));
            actividades.add(actividad);
        }

        resultSet.close();
        statement.close();

        return actividades;
    }

    private String obtenerTipoActividad(int idTipoActividad) throws SQLException {
        String tipoActividad = null;

        String query = "SELECT tipo FROM TiposActividad WHERE idTipoActividad = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idTipoActividad);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            tipoActividad = resultSet.getString("tipo");
        }

        resultSet.close();
        statement.close();

        return tipoActividad;
    }

    public String agregarActividad(Actividad actividad, String user) throws SQLException {

        //Verifica que no exista el nombre.
        String verificarExistenciaSql = "SELECT COUNT(*) AS count FROM Actividades WHERE nombre = ?";
        PreparedStatement verificarExistenciaStmt = connection.prepareStatement(verificarExistenciaSql);
        verificarExistenciaStmt.setString(1, actividad.getNombre());
        ResultSet existenciaResult = verificarExistenciaStmt.executeQuery();
        existenciaResult.next();
        int count = existenciaResult.getInt("count");
    
        
        if (count > 0) {
            return("Error: El nombre de la actividad ya existe. No se puede agregar la actividad.");
        }

        //Se prepara para hacer insert.
        String sql = "INSERT INTO Actividades (idPlanDeTrabajo, semana, idTipoActividad, nombre, cantidadDiasAnuncio, fechaPublicacion, cantidadRecordatorios, idModalidad, enlace, idEstado, fechaHora, afiche) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, 1);
        statement.setInt(2, actividad.getSemanaRealizacion());
        statement.setInt(3, actividad.getTipo().ordinal() + 1);
        statement.setString(4, actividad.getNombre());

        java.util.Date fechaActual = new java.util.Date();
        java.util.Date fechaPublicacion = actividad.getFechaPublicacion();
        long diferenciaEnMilisegundos = fechaPublicacion.getTime() - fechaActual.getTime();
        int diasRestantes = (int) Math.round(diferenciaEnMilisegundos / (24 * 60 * 60 * 1000.0));

        statement.setInt(5, diasRestantes);
        statement.setDate(6, new java.sql.Date(fechaPublicacion.getTime()));
        statement.setInt(7, 0);
        statement.setInt(8, actividad.getModalidad().ordinal() + 1);
        statement.setString(9, actividad.getEnlace());
        statement.setInt(10, actividad.getEstado().ordinal() + 1);
        statement.setTimestamp(11, Timestamp.valueOf(actividad.getFechaHora()));
        byte[] datos = new byte[1];
        datos[0] = 0;

        statement.setBytes(12, datos);

        //Verifica que los profesores existan.
        String[] profesores = actividad.getResponsable().split(", ");
        List<Integer> idProfesores = new ArrayList<>();

        for (String profesor : profesores) {
            String consultarProfesorSql = "SELECT idProfesor FROM Profesores WHERE nombre = ?";
            PreparedStatement consultarProfesorStmt = connection.prepareStatement(consultarProfesorSql);
            consultarProfesorStmt.setString(1, profesor);

            ResultSet resultSet = consultarProfesorStmt.executeQuery();

            if (resultSet.next()) {
                int idProfesor = resultSet.getInt("idProfesor");
                idProfesores.add(idProfesor);
            } else {
                statement.close();
                resultSet.close();
                consultarProfesorStmt.close();
                return ("Error: El profesor " + profesor + " no existe en la base de datos. Recuerda escribir el nombre completo de los profesores y separar los profesores por una coma y un espacio. Ejem: Mario Castillo, Gustavo Madriz");
            }

            resultSet.close();
            consultarProfesorStmt.close();
        }

        //Hace el insert luego de las validaciones.
        statement.executeUpdate();
        statement.close();

        //Recupera el ID de la ultima actividad ingresada
        String nombreActividad = actividad.getNombre();
        String obtenerIdActividadSql = "SELECT idActividad FROM Actividades WHERE nombre = ?";
        PreparedStatement obtenerIdActividadStmt = connection.prepareStatement(obtenerIdActividadSql);
        obtenerIdActividadStmt.setString(1, nombreActividad);
        ResultSet idActividadResult = obtenerIdActividadStmt.executeQuery();
        int idActividad = 0;
        if (idActividadResult.next()) {
            idActividad = idActividadResult.getInt("idActividad");
        }
        obtenerIdActividadStmt.close();

        //Hace el insert de responsables de la Actividad en ProfesoresActividad.
        String insertarProfesoresActividadesSql = "INSERT INTO ProfesoresActividades (idActividad, idProfesor) VALUES (?, ?)";
        PreparedStatement insertarProfesoresActividadesStmt = connection.prepareStatement(insertarProfesoresActividadesSql);

        for (int idProfesor : idProfesores) {
            insertarProfesoresActividadesStmt.setInt(1, idActividad);
            insertarProfesoresActividadesStmt.setInt(2, idProfesor);
            insertarProfesoresActividadesStmt.executeUpdate();
        }

        insertarProfesoresActividadesStmt.close();

        return "Actividad agregada";
    }

    public String marcarActividad(String nombreAct, int estado) {
        try {
            String selectQuery = "SELECT idEstado FROM Actividades WHERE nombre = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, nombreAct);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int idEstado = resultSet.getInt("idEstado");
                if (idEstado == estado) {
                    return "Error: La actividad ya ha sido marcada con este estado anteriormente.";
                } else {
                    String updateQuery = "UPDATE Actividades SET idEstado = ? WHERE nombre = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, estado);
                    updateStatement.setString(2, nombreAct);
                    updateStatement.executeUpdate();
                    
                    return "La actividad se ha marcado con el estado correspondiente exitosamente.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al marcar la actividad.";
        }
        return "";
    }

    int idActividadComent = 0;

    public List<Map<String, Object>> obtenerComentarios(String nombreAct) {
        List<Map<String, Object>> comentarios = new ArrayList<>();
    
        try {
            String selectIdQuery = "SELECT idActividad FROM Actividades WHERE nombre = ?";
            PreparedStatement selectIdStatement = connection.prepareStatement(selectIdQuery);
            selectIdStatement.setString(1, nombreAct);
            ResultSet idResultSet = selectIdStatement.executeQuery();
    
            if (idResultSet.next()) {
                idActividadComent = idResultSet.getInt("idActividad");
    
                // Obtener los comentarios de la tabla Comentarios para el idActividad correspondiente
                String selectComentariosQuery = "SELECT c.comentario, CONCAT(c.idComentario, ' - ', p.nombre) AS nombreProfesor, c.idComentarioOriginal " +
                                                "FROM Comentarios c " +
                                                "INNER JOIN Profesores p ON c.idProfesor = p.idProfesor " +
                                                "WHERE c.idActividad = ?";
                PreparedStatement selectComentariosStatement = connection.prepareStatement(selectComentariosQuery);
                selectComentariosStatement.setInt(1, idActividadComent);
                ResultSet comentariosResultSet = selectComentariosStatement.executeQuery();
    
                while (comentariosResultSet.next()) {
                    String comentario = comentariosResultSet.getString("comentario");
                    String nombreProfesor;
                    int idComentarioOriginal = comentariosResultSet.getInt("idComentarioOriginal");
    
                    if (idComentarioOriginal != 0) {
                        nombreProfesor = comentariosResultSet.getString("nombreProfesor") + " <Replica a comentario: " + idComentarioOriginal + ">";
                    } else {
                        nombreProfesor = comentariosResultSet.getString("nombreProfesor");
                    }

                    Map<String, Object> comentarioMap = new HashMap<>();
                    comentarioMap.put("nombre", nombreProfesor);
                    comentarioMap.put("mensaje", comentario);
    
                    comentarios.add(comentarioMap);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return comentarios;
    }

    public String agregarComentario(String user, int codigoComent, String mensaje) throws SQLException {
        int idProfesor = 0;
        String sql = "SELECT idProfesor FROM Profesores WHERE correo = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idProfesor = resultSet.getInt("idProfesor");
                }else{
                    return ("Error: No tiene permisos para publicar comentarios.");
                }
            }
            log.info(Integer.toString(idProfesor));
            LocalDateTime fechaActual = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(fechaActual);
            String sqlInsert = "INSERT INTO Comentarios (idActividad, idProfesor, fecha, idComentarioOriginal, comentario) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement statementInsert = connection.prepareStatement(sqlInsert)) {
                statementInsert.setInt(1, idActividadComent);
                statementInsert.setInt(2, idProfesor);
                statementInsert.setTimestamp(3, timestamp);

                if (codigoComent != 0) {
                    statementInsert.setInt(4, codigoComent);
                } else {
                    statementInsert.setNull(4, java.sql.Types.INTEGER);
                }

                statementInsert.setString(5, mensaje);
                statementInsert.executeUpdate();

                return "Comentario agregado";
            }
        }
    }
}

