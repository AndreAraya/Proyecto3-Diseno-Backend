package com.proyecto1diseno.app.DAO;

import java.io.Console;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proyecto1diseno.app.Modelo.Profesor;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class ProfesorDAO {
    private final Connection connection;
    Date fechaActual = Date.valueOf(LocalDate.now());

    public ProfesorDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Profesor> validarCredenciales(String correo, String contraseña) throws SQLException {
        String sql = "SELECT * FROM Profesores WHERE correo = ? AND contraseña = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            statement.setString(2, contraseña);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Profesor profesor = new Profesor(
                        result.getString("correo"),
                        result.getString("contraseña")
                    );
                    return Optional.of(profesor);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public List<Map<String, Object>> obtenerProfesores(String user) throws SQLException {
        List<Map<String, Object>> profes = new ArrayList<>();
        String query1 = "SELECT * FROM Asistentes WHERE correo = ?";
        PreparedStatement statement1 = null;
        ResultSet resultSet1 = null;
        PreparedStatement statement2 = null;
        ResultSet resultSet2 = null;
    
        try {
            statement1 = connection.prepareStatement(query1);
            statement1.setString(1, user);
            resultSet1 = statement1.executeQuery();
    
            if (resultSet1.next()) {
                String profesorSede = resultSet1.getString("idSede");
    
                if (profesorSede != null && !profesorSede.isEmpty()) {
                    String query2 = "SELECT * FROM Profesores WHERE idSede = ? AND darDeBaja = 0";
                    statement2 = connection.prepareStatement(query2);
                    statement2.setString(1, profesorSede);
                    resultSet2 = statement2.executeQuery();
    
                    while (resultSet2.next()) {
                        Map<String, Object> profesor = new HashMap<>();
                        profesor.put("id", resultSet2.getInt("idProfesor"));
                        profesor.put("nombre", resultSet2.getString("nombre"));
                        profesor.put("correo", resultSet2.getString("correo"));
                        profesor.put("tel", resultSet2.getString("numeroOficina"));
                        profes.add(profesor);
                    }
                }
            }
    
            return profes;
        } finally {
            // Cerrar los recursos en el bloque finally
            if (resultSet2 != null) {
                resultSet2.close();
            }
    
            if (statement2 != null) {
                statement2.close();
            }
    
            if (resultSet1 != null) {
                resultSet1.close();
            }
    
            if (statement1 != null) {
                statement1.close();
            }
        }
    }

    public Profesor obtenerProfesor(String codigo) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Profesor profesorEncontrado = null;
        
        int codigoNum = Integer.parseInt(codigo);
        
        try {

            String query = "SELECT * FROM Profesores WHERE idProfesor = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, codigoNum);
            resultSet = statement.executeQuery();
            
            // Verificar si se encontró un profesor con el código dado
            if (resultSet.next()) {
                // Crear un objeto Profesor con los datos obtenidos de la consulta
                profesorEncontrado = new Profesor();
                profesorEncontrado.setIdProfesor(resultSet.getInt("idProfesor"));
                profesorEncontrado.setNombre(resultSet.getString("nombre"));
                profesorEncontrado.setCorreo(resultSet.getString("correo"));
                profesorEncontrado.setContrasena(resultSet.getString("contraseña"));
                profesorEncontrado.setTelOficina(resultSet.getInt("numeroOficina"));
                profesorEncontrado.setCelular(resultSet.getInt("numeroCelular"));
                profesorEncontrado.setFotografia(resultSet.getString("foto"));   
            }
            
        } finally {
            // Cerrar los recursos utilizados
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
        
        return profesorEncontrado;
    }

    public String agregarProfesor(Profesor profesor, String user) throws SQLException {

        String selectProfesorSql = "SELECT idAsistente, idSede FROM Asistentes WHERE correo = ?";
        String idSede;
        String idAsistente;

        try (PreparedStatement selectProfesorStatement = connection.prepareStatement(selectProfesorSql)) {
            selectProfesorStatement.setString(1, user);

            try (ResultSet resultSet = selectProfesorStatement.executeQuery()) {
                if (resultSet.next()) {
                    idSede = resultSet.getString("idSede");
                    idAsistente = resultSet.getString("idAsistente");
                } else {
                    return "Error: No se encontro el profesor a hacer guia.";
                }
            }
        }

        String sqlCheckEmail = "SELECT idProfesor FROM Profesores WHERE correo = ?";
        String sqlInsert = "INSERT INTO Profesores (nombre, correo, idSede, contraseña, numeroOficina, numeroCelular, darDeBaja) VALUES (?, ?, ?, ?, ?, ?, 0)";
        String sqlInsertModificacion = "INSERT INTO ModificacionesProfesores (idProfesor, idAsistente, idTipoModificacion, fecha) VALUES (?, ?, ?, ?)";

        try (PreparedStatement checkEmailStatement = connection.prepareStatement(sqlCheckEmail);
            PreparedStatement insertStatement = connection.prepareStatement(sqlInsert);
            PreparedStatement insertModificacionStatement = connection.prepareStatement(sqlInsertModificacion)) {

            checkEmailStatement.setString(1, profesor.getCorreo());
            ResultSet resultSet = checkEmailStatement.executeQuery();

            if (resultSet.next()) {
                return "Error: El correo ya está en uso por otro profesor.";
            } else {
                insertStatement.setString(1, profesor.getNombre());
                insertStatement.setString(2, profesor.getCorreo());
                insertStatement.setString(3, idSede);
                insertStatement.setString(4, profesor.getContrasena());
                insertStatement.setInt(5, profesor.getTelOficina());
                insertStatement.setInt(6, profesor.getCelular());
                insertStatement.executeUpdate();

                // Obtener el idProfesor del profesor recién agregado
                String selectIdProfesorQuery = "SELECT idProfesor FROM Profesores WHERE correo = ?";
                PreparedStatement selectIdProfesorStatement = connection.prepareStatement(selectIdProfesorQuery);
                selectIdProfesorStatement.setString(1, profesor.getCorreo());
                ResultSet idProfesorResultSet = selectIdProfesorStatement.executeQuery();
                int idTipoModificacion = 1;

                if (idProfesorResultSet.next()) {
                    int idProfesor = idProfesorResultSet.getInt("idProfesor");
                    insertModificacionStatement.setInt(1, idProfesor);
                    insertModificacionStatement.setString(2, idAsistente);
                    insertModificacionStatement.setInt(3, idTipoModificacion);
                    insertModificacionStatement.setDate(4, fechaActual);
                    insertModificacionStatement.executeUpdate(); 
                }

                return "Profesor agregado exitosamente.";
            }
        }   
    }

    public String modificarProfesor(Profesor profesor, String user) throws SQLException {
        String sqlCheckEmail = "SELECT idProfesor FROM Profesores WHERE correo = ?";
        String sqlUpdate = "UPDATE Profesores SET nombre = ?, correo = ?, contraseña = ?, numeroOficina = ?, numeroCelular = ? WHERE idProfesor = ?";
        String sqlInsertModificacion = "INSERT INTO ModificacionesProfesores (idProfesor, idAsistente, idTipoModificacion, fecha) VALUES (?, ?, ?, ?)";

        try (PreparedStatement checkEmailStatement = connection.prepareStatement(sqlCheckEmail);
            PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate);
            PreparedStatement insertModificacionStatement = connection.prepareStatement(sqlInsertModificacion)) {

            checkEmailStatement.setString(1, profesor.getCorreo());
            ResultSet resultSet = checkEmailStatement.executeQuery();

            if (resultSet.next()) {
                if (resultSet.getInt("idProfesor") != profesor.getIdProfesor()) {
                    return "Error: El correo ya está en uso por otro profesor.";
                } else {
                    updateStatement.setString(1, profesor.getNombre());
                    updateStatement.setString(2, profesor.getCorreo());
                    updateStatement.setString(3, profesor.getContrasena());
                    updateStatement.setInt(4, profesor.getTelOficina());
                    updateStatement.setInt(5, profesor.getCelular());
                    updateStatement.setInt(6, profesor.getIdProfesor());
                    updateStatement.executeUpdate();

                    // Obtener el idAsistente del asistente correspondiente al correo "user"
                    String selectIdAsistenteQuery = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
                    PreparedStatement selectIdAsistenteStatement = connection.prepareStatement(selectIdAsistenteQuery);
                    selectIdAsistenteStatement.setString(1, user);
                    ResultSet idAsistenteResultSet = selectIdAsistenteStatement.executeQuery();

                    if (idAsistenteResultSet.next()) {
                        String idAsistente = idAsistenteResultSet.getString("idAsistente");
                        int idTipoModificacion = 3;
                        Date fechaActual = Date.valueOf(LocalDate.now());

                        // Insertar la modificación en la tabla ModificacionesProfesores
                        insertModificacionStatement.setInt(1, profesor.getIdProfesor());
                        insertModificacionStatement.setString(2, idAsistente);
                        insertModificacionStatement.setInt(3, idTipoModificacion);
                        insertModificacionStatement.setDate(4, fechaActual);
                        insertModificacionStatement.executeUpdate();
                    }

                    return "Modificación exitosa.";
                }
            } else {
                updateStatement.setString(1, profesor.getNombre());
                updateStatement.setString(2, profesor.getCorreo());
                updateStatement.setString(3, profesor.getContrasena());
                updateStatement.setInt(4, profesor.getTelOficina());
                updateStatement.setInt(5, profesor.getCelular());
                updateStatement.setInt(6, profesor.getIdProfesor());
                updateStatement.executeUpdate();

                // Obtener el idAsistente del asistente correspondiente al correo "user"
                String selectIdAsistenteQuery = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
                PreparedStatement selectIdAsistenteStatement = connection.prepareStatement(selectIdAsistenteQuery);
                selectIdAsistenteStatement.setString(1, user);
                ResultSet idAsistenteResultSet = selectIdAsistenteStatement.executeQuery();

                if (idAsistenteResultSet.next()) {
                    String idAsistente = idAsistenteResultSet.getString("idAsistente");
                    int idTipoModificacion = 3;
                    Date fechaActual = Date.valueOf(LocalDate.now());

                    insertModificacionStatement.setInt(1, profesor.getIdProfesor());
                    insertModificacionStatement.setString(2, idAsistente);
                    insertModificacionStatement.setInt(3, idTipoModificacion);
                    insertModificacionStatement.setDate(4, fechaActual);
                    insertModificacionStatement.executeUpdate();
                }

                return "Modificación exitosa.";
            }
        }
    }

    public String darDeBajaProfesor(int codigoProf, String user) throws SQLException {
        String sql = "SELECT darDeBaja FROM Profesores WHERE idProfesor = ?";
        String sqlInsertModificacion = "INSERT INTO ModificacionesProfesores (idProfesor, idAsistente, idTipoModificacion, fecha) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql);
            PreparedStatement insertModificacionStatement = connection.prepareStatement(sqlInsertModificacion)) {

            statement.setInt(1, codigoProf);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int darDeBaja = resultSet.getInt("darDeBaja");
                    if (darDeBaja == 0) {
                        // Actualizar el campo "darDeBaja" del profesor a 1
                        String updateSql = "UPDATE Profesores SET darDeBaja = 1 WHERE idProfesor = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                            updateStatement.setInt(1, codigoProf);
                            updateStatement.executeUpdate();
                        }

                        // Obtener el idAsistente del asistente correspondiente al correo "user"
                        String selectIdAsistenteQuery = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
                        PreparedStatement selectIdAsistenteStatement = connection.prepareStatement(selectIdAsistenteQuery);
                        selectIdAsistenteStatement.setString(1, user);
                        ResultSet idAsistenteResultSet = selectIdAsistenteStatement.executeQuery();

                        if (idAsistenteResultSet.next()) {
                            String idAsistente = idAsistenteResultSet.getString("idAsistente");
                            int idTipoModificacion = 2;

                            // Insertar la modificación en la tabla ModificacionesProfesores
                            insertModificacionStatement.setInt(1, codigoProf);
                            insertModificacionStatement.setString(2, idAsistente);
                            insertModificacionStatement.setInt(3, idTipoModificacion);
                            insertModificacionStatement.setDate(4, fechaActual);
                            insertModificacionStatement.executeUpdate();
                        }

                        return "Profesor dado de baja.";
                    } else {
                        return "Error: El profesor ya está de baja.";
                    }
                } else {
                    return "Error: No se encontró el profesor con el código especificado.";
                }
            }
        }
    }       

    public String defGuiaProfesor(int idProfesor) throws SQLException {
        String selectProfesorSql = "SELECT idSede FROM Profesores WHERE idProfesor = ?";
        String idSede;

        try (PreparedStatement selectProfesorStatement = connection.prepareStatement(selectProfesorSql)) {
            selectProfesorStatement.setInt(1, idProfesor);

            try (ResultSet resultSet = selectProfesorStatement.executeQuery()) {
                if (resultSet.next()) {
                    idSede = resultSet.getString("idSede");
                } else {
                    return "Error: No se encontro el profesor a hacer guia.";
                }
            }
        }

        String selectIdProfesorSql = "SELECT idProfesor FROM ProfesoresGuias WHERE idProfesor = ?";
        
        try (PreparedStatement selectIdProfesorStatement = connection.prepareStatement(selectIdProfesorSql)) {
            selectIdProfesorStatement.setInt(1, idProfesor);
            try (ResultSet resultSet = selectIdProfesorStatement.executeQuery()) {
                if (resultSet.next()) {
                    return "Error: Este profesor ya es profesor guia.";
                }
            }
        }

        String selectMaxCodigoSql = "SELECT MAX(CAST(RIGHT(codigo, 2) AS INT)) AS max_numero FROM ProfesoresGuias WHERE LEFT(codigo, CHARINDEX('-', codigo) - 1) = ?";
        String insertProfesorGuiaSql = "INSERT INTO ProfesoresGuias (idProfesor, codigo, coordinador) VALUES (?, ?, 0)";
        int maxNumero;

        try (PreparedStatement statement = connection.prepareStatement(selectMaxCodigoSql)) {
            statement.setString(1, idSede);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    maxNumero = resultSet.getInt("max_numero");
                } else {
                    maxNumero = 0;
                }
            }
        }
        
        int nuevoNumero = maxNumero + 1;
        String nuevoCodigo = idSede + "-" + String.format("%02d", nuevoNumero);
        
        try (PreparedStatement statement = connection.prepareStatement(insertProfesorGuiaSql)) {
            statement.setInt(1, idProfesor);
            statement.setString(2, nuevoCodigo);
            statement.executeUpdate();
            return "Profesor agregado a grupo guia.";
        }
    }

    public List<Map<String, Object>> obtenerProfesoresGuia(String user) throws SQLException {
        List<Map<String, Object>> profesGuia = new ArrayList<>();
        //String query1 = "SELECT * FROM Profesores";
        PreparedStatement statement1 = null;
        ResultSet resultSet1 = null;
        PreparedStatement statement2 = null;
        ResultSet resultSet2 = null;
    
        try {
                String query2 = "SELECT DISTINCT p.* FROM Profesores p INNER JOIN ProfesoresGuias pg ON p.idProfesor = pg.idProfesor WHERE p.idProfesor = pg.idProfesor";
                statement2 = connection.prepareStatement(query2);
                String profesorID = "1";
                //statement2.setString(1, profesorID);
                resultSet2 = statement2.executeQuery();
                while (resultSet2.next()) {
                    
                    Map<String, Object> profesorGuia = new HashMap<>();
                    profesorGuia.put("id", resultSet2.getInt("idProfesor"));
                    profesorGuia.put("nombre", resultSet2.getString("nombre"));
                    profesorGuia.put("correo", resultSet2.getString("correo"));
                    profesorGuia.put("tel", resultSet2.getString("numeroOficina"));
                    profesGuia.add(profesorGuia);
                    }
               // }
           // }
    
            return profesGuia;
        } finally {
            // Cerrar los recursos en el bloque finally
            if (resultSet2 != null) {
                resultSet2.close();
            }
    
            if (statement2 != null) {
                statement2.close();
            }
    
            if (resultSet1 != null) {
                resultSet1.close();
            }
    
            if (statement1 != null) {
                statement1.close();
            }
        }
    }
    
    public String definirCoordinador(int codigoProf, String user) throws SQLException {
        System.out.println(user);
        System.out.println(codigoProf);
        log.info("AQUI1");
        String query1 = "SELECT * FROM Asistentes WHERE correo = ?";
        PreparedStatement statement1 = null;
        ResultSet resultSet1 = null;
    
        log.info("AQUI2");
        statement1 = connection.prepareStatement(query1);
        statement1.setString(1, user);
        resultSet1 = statement1.executeQuery();
        String asistSede = null;
        log.info("AQUI3");
        if (resultSet1.next()) {
            asistSede = resultSet1.getObject("idSede").toString();
        } else {
            asistSede = "";
        }

        System.out.println(asistSede);
        if(asistSede.equals("CA")){
            log.info("AQUI4");
        String sql = "SELECT coordinador FROM ProfesoresGuias WHERE idProfesor = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, codigoProf);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int coordinador = resultSet.getInt("coordinador");
                    if (coordinador == 0) {
                        String updateSql = "UPDATE ProfesoresGuias SET coordinador = 1 WHERE idProfesor = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                            updateStatement.setInt(1, codigoProf);
                            updateStatement.executeUpdate();
                        }
                        return "Profesor definido como coordinador.";
                    } else {
                        return "Error: El profesor ya es coordinador.";
                    }
                } else {
                    return "Error: No se encontró el profesor con el código especificado.";
                }
            }
            }
            
        } else{
            return "No es asistente de Cartago";
        }

    }

    public String subscribirObservador(String user) {
        String obtenerIdProfesorQuery = "SELECT id FROM Profesores WHERE correo = ?";
        String insertarReceptorNotificacionQuery = "INSERT INTO ReceptoresNotificaciones (idReceptor, idNotificacion, idTipoUsuario) VALUES (?, 0, 1)";

        try {
            PreparedStatement obtenerIdProfesorStmt = connection.prepareStatement(obtenerIdProfesorQuery);
            obtenerIdProfesorStmt.setString(1, user);
            ResultSet resultado = obtenerIdProfesorStmt.executeQuery();
            
            if (resultado.next()) {
                String idProfesor = resultado.getString("id");

                PreparedStatement insertarReceptorNotificacionStmt = connection.prepareStatement(insertarReceptorNotificacionQuery);
                insertarReceptorNotificacionStmt.setString(1, idProfesor);

                insertarReceptorNotificacionStmt.executeUpdate();
                
                return "Subscripción exitosa.";
            } else {
                return "Error: El usuario no es un profesor válido";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Error en la suscripción";
        }
    }

    public List<Map<String, Object>> obtenerNotificaciones(String user) {
        List<Map<String, Object>> notificaciones = new ArrayList<>();

        String obtenerIdProfesorQuery = "SELECT id FROM Profesores WHERE correo = ?";
        String idProfesor = null;

        try {
            PreparedStatement obtenerIdProfesorStmt = connection.prepareStatement(obtenerIdProfesorQuery);
            obtenerIdProfesorStmt.setString(1, user);
            ResultSet resultado = obtenerIdProfesorStmt.executeQuery();

            if (resultado.next()) {
                idProfesor = resultado.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (idProfesor == null) {
            return notificaciones;
        }

        // Obtener las notificaciones del profesor con el ID obtenido
        String obtenerNotificacionesQuery = "SELECT n.emisor, n.fecha, n.contenido, rn.leido FROM Notificaciones n " +
                "INNER JOIN ReceptoresNotificaciones rn ON n.id = rn.idNotificacion " +
                "WHERE rn.idReceptor = ? AND rn.idNotificacion <> 0";

        try {
            // Preparar la consulta para obtener las notificaciones
            PreparedStatement obtenerNotificacionesStmt = connection.prepareStatement(obtenerNotificacionesQuery);
            obtenerNotificacionesStmt.setString(1, idProfesor);

            // Ejecutar la consulta para obtener las notificaciones
            ResultSet resultado = obtenerNotificacionesStmt.executeQuery();

            // Recorrer los resultados y agregarlos al mapa de notificaciones
            while (resultado.next()) {
                String emisor = resultado.getString("emisor");
                String fecha = resultado.getString("fecha");
                String contenido = resultado.getString("contenido");
                boolean leida = resultado.getBoolean("leida");

                Map<String, Object> notificacion = new HashMap<>();
                notificacion.put("emisor", emisor);
                notificacion.put("fecha", fecha);
                notificacion.put("contenido", contenido);
                notificacion.put("leido", leida);

                notificaciones.add(notificacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return notificaciones;
        }

        return notificaciones;
    }



}
