package com.proyecto1diseno.app.DAO;
import com.proyecto1diseno.app.Modelo.Estudiante;
import com.proyecto1diseno.app.Modelo.Notificacion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstudianteDAO {

    private final Connection connection;

    public EstudianteDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Map<String, Object>> obtenerEstudiantes(String user) throws SQLException {
        List<Map<String, Object>> estudiantes = new ArrayList<>();

        String query1 = "SELECT * FROM Profesores WHERE correo = ?"; 
        PreparedStatement statement1 = connection.prepareStatement(query1);
        statement1.setString(1, user);
        ResultSet resultSet1 = statement1.executeQuery();
        if (resultSet1.next()) {
            String estudianteSede = resultSet1.getString("idSede");
            
            if (estudianteSede != null && !estudianteSede.isEmpty()) {
                String query2 = "SELECT * FROM Estudiantes WHERE idSede = ?";
                PreparedStatement statement2 = connection.prepareStatement(query2);
                statement2.setString(1, estudianteSede);
                ResultSet resultSet2 = statement2.executeQuery();
                
                while (resultSet2.next()) {
                    Map<String, Object> estudiante = new HashMap<>();
                    estudiante.put("id", resultSet2.getInt("carne"));
                    estudiante.put("nombre", resultSet2.getString("nombre")+" "+resultSet2.getString("apellido1")+" "+resultSet2.getString("apellido2"));
                    estudiante.put("correo", resultSet2.getString("correo"));
                    estudiante.put("tel", resultSet2.getString("numeroCelular"));
                    estudiantes.add(estudiante);
                }

            }
        }

        return estudiantes;
    }

    public Estudiante getEstudiante(String carnet) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Estudiante estudianteEncontrado = null;
        
        int carnetNum = Integer.parseInt(carnet);
        //int carnetNum = 2020087412;
        
        try {

            String query = "SELECT * FROM Estudiantes WHERE carne = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, carnetNum);
            resultSet = statement.executeQuery();
            
            // Verificar si se encontró un estudiante con el código dado
            if (resultSet.next()) {
                // Crear un objeto Estudiante con los datos obtenidos de la consulta
                estudianteEncontrado = new Estudiante();
                estudianteEncontrado.setCarnet(resultSet.getInt("carne"));
                estudianteEncontrado.setNombre(resultSet.getString("nombre"));
                estudianteEncontrado.setApellido1(resultSet.getString("apellido1"));
                estudianteEncontrado.setApellido2(resultSet.getString("apellido2"));
                estudianteEncontrado.setCorreo(resultSet.getString("correo"));
                estudianteEncontrado.setContrasena(resultSet.getString("contraseña"));
                estudianteEncontrado.setCelular(resultSet.getInt("numeroCelular"));  
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
        
        return estudianteEncontrado;
    }

    public String modificarEstudiante(Estudiante estudiante) throws SQLException {
        String sqlCheckEmail = "SELECT carne FROM Estudiantes WHERE correo = ?";
        String sqlUpdate = "UPDATE Estudiantes SET nombre = ?, apellido1 = ?, apellido2 = ?, correo = ?, contraseña = ?,  numeroCelular = ? WHERE carne = ?";
        
        try (PreparedStatement checkEmailStatement = connection.prepareStatement(sqlCheckEmail);
             PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate)) {
            
            checkEmailStatement.setString(1, estudiante.getCorreo());
            log.info("AQUI");
            log.info(estudiante.getCorreo());
            ResultSet resultSet = checkEmailStatement.executeQuery();
            
            if (resultSet.next()) {
                if (resultSet.getInt("carne") != estudiante.getCarnet()) {
                    return "Error: El correo ya está en uso por otro estudiante.";
                } else {
                        
                    updateStatement.setString(1, estudiante.getNombre());
                    updateStatement.setString(2,estudiante.getApellido1());
                    updateStatement.setString(3,estudiante.getApellido2());
                    updateStatement.setString(4, estudiante.getCorreo());
                    updateStatement.setString(5, estudiante.getContrasena());
                    updateStatement.setInt(6, estudiante.getCelular());
                    updateStatement.setInt(7, estudiante.getCarnet());
                    updateStatement.executeUpdate();
                    return "Modificación exitosa.";
            }
        }
        else{
                    updateStatement.setString(1, estudiante.getNombre());
                    updateStatement.setString(2,estudiante.getApellido1());
                    updateStatement.setString(3,estudiante.getApellido2());
                    updateStatement.setString(4, estudiante.getCorreo());
                    updateStatement.setString(5, estudiante.getContrasena());
                    updateStatement.setInt(6, estudiante.getCelular());
                    updateStatement.setInt(7, estudiante.getCarnet());
                    updateStatement.executeUpdate();
                    return "Modificación exitosa.";

        }
    }
} 
    public String insertarEstudiante(Estudiante estudiante) throws SQLException{
        String query = "INSERT INTO dbo.Estudiantes (idSede, carne, apellido1, apellido2, nombre, segundoNombre, correo, numeroCelular, contraseña) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        log.info("AQUI1");
        if (!existeCarne(estudiante.getCarnet()) && !existeCorreo(estudiante.getCorreo())) {
            log.info("AQUI1");
            try (PreparedStatement selectProfesorStatement = connection.prepareStatement(query)) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, estudiante.getIdSede());
                statement.setInt(2, estudiante.getCarnet());
                statement.setString(3, estudiante.getApellido1());
                statement.setString(4, estudiante.getApellido2());
                statement.setString(5, estudiante.getNombre());
                statement.setString(6, estudiante.getSegundoNombre());
                statement.setString(7, estudiante.getCorreo());
                statement.setInt(8, estudiante.getCelular());
                statement.setString(9, estudiante.getContrasena());
                statement.executeUpdate();
                return "Se inserto correctamente";
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error en la coneccion";
            }
        } else {
            
            return "Error: El carne o el correo ya existen en la base de datos.";
        }
    }

    private boolean existeCarne(int carne) {
        String query = "SELECT * FROM Estudiantes WHERE carne = ?";
        try (PreparedStatement selectProfesorStatement = connection.prepareStatement(query)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, carne);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Devuelve true si hay algún resultado, es decir, el carne ya existe
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean existeCorreo(String correo) {
        String query = "SELECT * FROM Estudiantes WHERE correo = ?";
        try (PreparedStatement selectProfesorStatement = connection.prepareStatement(query)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, correo);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Devuelve true si hay algún resultado, es decir, el correo ya existe
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<Estudiante> validarCredenciales(String correo, String carne) throws SQLException {
        String sql = "SELECT * FROM Estudiantes WHERE correo = ? AND carne = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            statement.setString(2, carne);

            try (ResultSet result = statement.executeQuery()) {
                //int carnet = Integer.parseInt(carne);
                if (result.next()) {
                    Estudiante estudiante = new Estudiante(
                        result.getString("correo"),
                        result.getInt("carne")
                    );
                    return Optional.of(estudiante);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public String modificarEstudiante2(String correo, String celular) throws SQLException {
        //String celular = mostrarCelular(correo);
        System.out.println("Celular mod2");
        System.out.println(celular);
        System.out.println("Correo mod2");
        System.out.println(correo);
        String sqlUpdate = "UPDATE Estudiantes SET numeroCelular = ? WHERE correo = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate)) {
            int cel = Integer.parseInt(celular);
            updateStatement.setInt(1, cel);
            updateStatement.setString(2, correo);
            updateStatement.executeUpdate();
            System.out.println("Modificación exitosa.");
            return "Modificación exitosa.";
        } 
    }

    public String subscribirObservador(String user) {
        String obtenerIdEstudianteQuery = "SELECT id FROM Estudiantes WHERE correo = ?";
        String insertarReceptorNotificacionQuery = "INSERT INTO ReceptoresNotificaciones (idReceptor, idNotificacion, idTipoUsuario) VALUES (?, 0, 2)";

        try {
            PreparedStatement obtenerIdEstudianteStmt = connection.prepareStatement(obtenerIdEstudianteQuery);
            obtenerIdEstudianteStmt.setString(1, user);
            ResultSet resultado = obtenerIdEstudianteStmt.executeQuery();
            
            if (resultado.next()) {
                String idEstudiante = resultado.getString("id");

                PreparedStatement insertarReceptorNotificacionStmt = connection.prepareStatement(insertarReceptorNotificacionQuery);
                insertarReceptorNotificacionStmt.setString(1, idEstudiante);

                insertarReceptorNotificacionStmt.executeUpdate();
                
                return "Subscripción exitosa.";
            } else {
                return "Error: El usuario no es un estudiante válido";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Error en la suscripción";
        }
    }


    public List<Map<String, Object>> obtenerNotificaciones(String user) {
        List<Map<String, Object>> notificaciones = new ArrayList<>();

        String obtenerIdEstudianteQuery = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
        String idEstudiante = null;

        try {
            PreparedStatement obtenerIdEstudianteStmt = connection.prepareStatement(obtenerIdEstudianteQuery);
            obtenerIdEstudianteStmt.setString(1, user);
            ResultSet resultado = obtenerIdEstudianteStmt.executeQuery();

            if (resultado.next()) {
                idEstudiante = resultado.getString("idEstudiante");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // Comprobar si el estudiante está suscrito
        String comprobarSuscritoQuery = "SELECT idReceptor FROM ReceptoresNotificaciones WHERE idReceptor = ? AND idNotificacion = 0";

        try {
            PreparedStatement comprobarSuscritoStmt = connection.prepareStatement(comprobarSuscritoQuery);
            comprobarSuscritoStmt.setString(1, idEstudiante);
            ResultSet suscripcionResult = comprobarSuscritoStmt.executeQuery();

            boolean suscrito = suscripcionResult.next();

            if (!suscrito) {
                // El estudiante no está suscrito, agregar el mensaje de error
                Map<String, Object> error = new HashMap<>();
                error.put("error", "No está suscrito");
                notificaciones.add(error);
                return notificaciones;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return notificaciones;
        }

        // Obtener las notificaciones del estudiante con el ID obtenido
        String obtenerNotificacionesQuery = "SELECT n.idNotificacion, n.idEmisor, n.idTipoUsuario, n.fecha, n.contenido, rn.leida FROM Notificaciones n " +
            "INNER JOIN ReceptoresNotificaciones rn ON n.idNotificacion = rn.idNotificacion " +
            "WHERE rn.idReceptor = ? AND rn.idNotificacion <> 0";

        try {
            // Preparar la consulta para obtener las notificaciones
            PreparedStatement obtenerNotificacionesStmt = connection.prepareStatement(obtenerNotificacionesQuery);
            obtenerNotificacionesStmt.setString(1, idEstudiante);

            // Ejecutar la consulta para obtener las notificaciones
            ResultSet resultado = obtenerNotificacionesStmt.executeQuery();

            // Recorrer los resultados y agregarlos al mapa de notificaciones
            while (resultado.next()) {
                int idNotificacion = resultado.getInt("idNotificacion");
                int emisor = resultado.getInt("idEmisor");
                int usuario = resultado.getInt("idTipoUsuario");
                String fecha = resultado.getString("fecha");
                String contenido = resultado.getString("contenido");
                boolean leida = resultado.getBoolean("leida");

                String nombreEmisor = obtenerUsuarioEmisor(usuario, emisor);

                Map<String, Object> notificacion = new HashMap<>();
                notificacion.put("id", idNotificacion);
                notificacion.put("emisor", nombreEmisor);
                notificacion.put("fecha", fecha);
                notificacion.put("mensaje", contenido);
                notificacion.put("leido", leida);

                notificaciones.add(notificacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return notificaciones;
        }

        return notificaciones;
    }

    public String obtenerUsuarioEmisor(int usuario, int emisor) {
        String nombreCompleto = null;

        if (usuario == 2) {
            String obtenerNombreEstudianteQuery = "SELECT CONCAT(nombre, ' ', apellido1, ' ', apellido2) AS nombreCompleto " +
                    "FROM Estudiantes WHERE idEstudiante = ?";

            try {
                // Preparar la consulta para obtener el nombre del estudiante
                PreparedStatement obtenerNombreEstudianteStmt = connection.prepareStatement(obtenerNombreEstudianteQuery);
                obtenerNombreEstudianteStmt.setInt(1, emisor);

                // Ejecutar la consulta para obtener el nombre del estudiante
                ResultSet resultado = obtenerNombreEstudianteStmt.executeQuery();

                if (resultado.next()) {
                    nombreCompleto = resultado.getString("nombreCompleto");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return nombreCompleto;
            }
        } else if (usuario == 1) {
            String obtenerNombreProfesorQuery = "SELECT nombre FROM Profesores WHERE idProfesor = ?";

            try {
                // Preparar la consulta para obtener el nombre del profesor
                PreparedStatement obtenerNombreProfesorStmt = connection.prepareStatement(obtenerNombreProfesorQuery);
                obtenerNombreProfesorStmt.setInt(1, emisor);

                // Ejecutar la consulta para obtener el nombre del profesor
                ResultSet resultado = obtenerNombreProfesorStmt.executeQuery();

                if (resultado.next()) {
                    nombreCompleto = resultado.getString("nombre");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return nombreCompleto;
            }
        } else if (usuario == 3) {
            String obtenerNombreAsistenteQuery = "SELECT nombre FROM Asistentes WHERE idAsistente = ?";

            try {
                // Preparar la consulta para obtener el nombre del asistente
                PreparedStatement obtenerNombreAsistenteStmt = connection.prepareStatement(obtenerNombreAsistenteQuery);
                obtenerNombreAsistenteStmt.setInt(1, emisor);

                // Ejecutar la consulta para obtener el nombre del asistente
                ResultSet resultado = obtenerNombreAsistenteStmt.executeQuery();

                if (resultado.next()) {
                    nombreCompleto = resultado.getString("nombre");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return nombreCompleto;
            }
        }

        return nombreCompleto;
    }
 

    public Estudiante getEstudiante2(String correo) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            Estudiante estudianteEncontrado = null;
            
            //int carnetNum = 2020087412;

            System.out.println("CORREO");
            System.out.println(correo);
            
            try {

                String query = "SELECT * FROM Estudiantes WHERE correo = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, correo);
                resultSet = statement.executeQuery();
                
                // Verificar si se encontró un estudiante con el código dado
                if (resultSet.next()) {
                    // Crear un objeto Estudiante con los datos obtenidos de la consulta
                    estudianteEncontrado = new Estudiante();
                    estudianteEncontrado.setCarnet(resultSet.getInt("carne"));
                    estudianteEncontrado.setNombre(resultSet.getString("nombre"));
                    estudianteEncontrado.setApellido1(resultSet.getString("apellido1"));
                    estudianteEncontrado.setApellido2(resultSet.getString("apellido2"));
                    estudianteEncontrado.setCorreo(resultSet.getString("correo"));
                    estudianteEncontrado.setContrasena(resultSet.getString("contraseña"));
                    estudianteEncontrado.setCelular(resultSet.getInt("numeroCelular"));  
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
            
            return estudianteEncontrado;
        }

        public String mostrarCelular(String correo) throws SQLException {
        String sql = "SELECT numeroCelular FROM Estudiantes WHERE correo = ?";
        System.out.println("ya1 Dao");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            System.out.println("ya2 Dao");
           try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return String.valueOf(result.getInt("numeroCelular"));
                } else {
                    return "Error";
                }
            } 
    
        }
    }

    public String agregarNotificacion(Notificacion notificacion, String user) {
        String idEstudiante = null;
        String idNotificacion = null;

        // Buscar el ID del estudiante en la tabla Estudiantes
        String buscarIdEstudianteQuery = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
        try {
            PreparedStatement buscarIdEstudianteStmt = connection.prepareStatement(buscarIdEstudianteQuery);
            buscarIdEstudianteStmt.setString(1, user);
            ResultSet resultado = buscarIdEstudianteStmt.executeQuery();

            if (resultado.next()) {
                // Obtener el ID del estudiante
                idEstudiante = resultado.getString("idEstudiante");

                // Insertar un registro en la tabla Notificaciones
                String insertarNotificacionQuery = "INSERT INTO Notificaciones (idEmisor, fecha, contenido, idTipoUsuario) " +
                        "VALUES (?, ?, ?, 2)";
                try {
                    PreparedStatement insertarNotificacionStmt = connection.prepareStatement(insertarNotificacionQuery);
                    insertarNotificacionStmt.setString(1, idEstudiante);
                    insertarNotificacionStmt.setTimestamp(2, Timestamp.valueOf(notificacion.getFechaHora()));
                    insertarNotificacionStmt.setString(3, notificacion.getContenido());
                    insertarNotificacionStmt.executeUpdate();

                    ResultSet generatedKeys = insertarNotificacionStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        idNotificacion = generatedKeys.getString(1);
                    } else {
                        // Si no se obtiene el ID, se muestra un mensaje de error
                        return "Error: No se pudo obtener el ID de la notificación agregada";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return "Error: Error";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return idEstudiante + " " + idNotificacion;
    }

    public void notificar(String observadorUser, Notificacion notificacion) {
        String buscarIdEstudianteQuery = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
        try {
            PreparedStatement buscarIdEstudianteStmt = connection.prepareStatement(buscarIdEstudianteQuery);
            buscarIdEstudianteStmt.setString(1, observadorUser);
            ResultSet resultado = buscarIdEstudianteStmt.executeQuery();

            if (resultado.next()) {
                // Obtener el ID del estudiante
                int idEstudiante = resultado.getInt("idEstudiante");

                // Insertar un registro en la tabla ReceptoresNotificaciones
                String insertarReceptorQuery = "INSERT INTO ReceptoresNotificaciones (idReceptor, idNotificacion, idTipoUsuario, leida, eliminada) " +
                        "VALUES (?, ?, 2, 0, 0)";
                try {
                    PreparedStatement insertarReceptorStmt = connection.prepareStatement(insertarReceptorQuery);
                    insertarReceptorStmt.setInt(1, idEstudiante);
                    insertarReceptorStmt.setInt(2, notificacion.getIdNotificacion());
                    insertarReceptorStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.info("Fallo en la inserción en la tabla ReceptoresNotificaciones");
                }
            } else {
                log.info("No se encuentra el estudiante con el correo proporcionado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Fallo en la inserción en la tabla ReceptoresNotificaciones");
        }
    }

    public String eliminarNotificacion(Object idNotificacion, String user) {
        try {
            int idEstudiante = 0;
            
            // Buscar el ID del estudiante basado en el correo del usuario
            String query = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idEstudiante = rs.getInt("idEstudiante");
            } else {
                return "Error: No se encontró ningún estudiante con ese correo.";
            }
            
            // Eliminar registros de la tabla basado en el ID del estudiante y la notificación
            query = "DELETE FROM ReceptoresNotificaciones WHERE idEstudiante = ? AND idNotificacion = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, idEstudiante);
            stmt.setInt(2, (int) idNotificacion);
            stmt.executeUpdate();
            
            return "Notificacion eliminada exitosamente.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al eliminar las notificaciones.";
        }
    }

    public String eliminarNotificaciones(String user) {
        try {
            String idEstudiante = null;
            
            // Buscar el ID del estudiante basado en el correo del usuario
            String query = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idEstudiante = rs.getString("idEstudiante");
            } else {
                return "Error: No se encontró ningún estudiante con ese correo.";
            }
            
            // Eliminar registros de la tabla ReceptoresNotificaciones basados en el ID del estudiante
            query = "DELETE FROM ReceptoresNotificaciones WHERE idReceptor = ? AND idNotificacion <> 0";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idEstudiante);
            int rowsAffected = stmt.executeUpdate();
            
            return "Se eliminaron " + rowsAffected + " notificaciones.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al eliminar las notificaciones.";
        }
    }

    public String desuscribirObservador(String user) {
        try {
            String idEstudiante = null;
            
            // Buscar el ID del estudiante basado en el correo del usuario
            String query = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idEstudiante = rs.getString("idEstudiante");
            } else {
                return "Error: No se encontró ningún estudiante con ese correo.";
            }
            
            // Eliminar registros de la tabla ReceptoresNotificaciones basados en el ID del estudiante
            query = "DELETE FROM ReceptoresNotificaciones WHERE idReceptor = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idEstudiante);
            int rowsAffected = stmt.executeUpdate();
            
            return "Se eliminaron " + rowsAffected + " notificaciones.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al eliminar las notificaciones.";
        }
    }

    public String marcarNotificacionLeida(String user, String codigoNotif) {
        try {
            String idEstudiante = null;
            
            // Buscar el ID del estudiante basado en el correo del usuario
            String query = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idEstudiante = rs.getString("idEstudiante");
            } else {
                return "Error: No se encontró ningún estudiante con ese correo.";
            }
            
            // Actualizar el campo "leida" a 1 en la tabla ReceptoresNotificaciones
            query = "UPDATE ReceptoresNotificaciones SET leida = 1 WHERE idReceptor = ? AND idNotificacion = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idEstudiante);
            stmt.setString(2, codigoNotif);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "Notificación marcada como leída.";
            } else {
                return "Error: No se encontró ninguna notificación.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al marcar la notificación como leída.";
        }
    }

    public String marcarNotificacionNoLeida(String user, String codigoNotif) {
        try {
            String idEstudiante = null;
            
            // Buscar el ID del estudiante basado en el correo del usuario
            String query = "SELECT idEstudiante FROM Estudiantes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idEstudiante = rs.getString("idEstudiante");
            } else {
                return "Error: No se encontró ningún estudiante con ese correo.";
            }
            
            query = "UPDATE ReceptoresNotificaciones SET leida = 0 WHERE idReceptor = ? AND idNotificacion = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idEstudiante);
            stmt.setString(2, codigoNotif);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "Notificación marcada como leída.";
            } else {
                return "Error: No se encontró ninguna notificación.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al marcar la notificación como no leída.";
        }
    }

    


    

}



