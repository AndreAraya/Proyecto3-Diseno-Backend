package com.proyecto1diseno.app.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proyecto1diseno.app.Modelo.AsistenteAdmin;
import com.proyecto1diseno.app.Modelo.Notificacion;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsistenteAdminDAO {
    private final Connection connection;

    public AsistenteAdminDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<AsistenteAdmin> validarCredenciales(String correo, String contraseña) throws SQLException {
        String sql = "SELECT * FROM Asistentes WHERE correo = ? AND contraseña = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            statement.setString(2, contraseña);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    AsistenteAdmin asistenteAdmin = new AsistenteAdmin(
                        result.getString("correo"),
                        result.getString("contraseña")
                    );
                    return Optional.of(asistenteAdmin);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public String subscribirObservador(String user) {
        String obtenerIdAsistenteQuery = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
        String insertarReceptorNotificacionQuery = "INSERT INTO ReceptoresNotificaciones (idReceptor, idNotificacion, idTipoUsuario, leida, eliminada) VALUES (?, 0, 3, 0, 0)";
        String comprobarSuscritoQuery = "SELECT idReceptor FROM ReceptoresNotificaciones WHERE idReceptor = ? AND idNotificacion = 0";

        try {
            PreparedStatement obtenerIdAsistenteStmt = connection.prepareStatement(obtenerIdAsistenteQuery);
            obtenerIdAsistenteStmt.setString(1, user);
            ResultSet resultado = obtenerIdAsistenteStmt.executeQuery();
            
            if (resultado.next()) {
                String idAsistente = resultado.getString("idAsistente");

                PreparedStatement comprobarSuscritoStmt = connection.prepareStatement(comprobarSuscritoQuery);
                comprobarSuscritoStmt.setString(1, idAsistente);
                ResultSet suscripcionResult = comprobarSuscritoStmt.executeQuery();

                boolean suscrito = suscripcionResult.next();

                if (suscrito) {
                    return "Error: Ya estas suscrito al sistema de notificaciones.";
                }

                PreparedStatement insertarReceptorNotificacionStmt = connection.prepareStatement(insertarReceptorNotificacionQuery);
                insertarReceptorNotificacionStmt.setString(1, idAsistente);

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

        String obtenerIdAsistenteQuery = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
        String idAsistente = null;

        try {
            PreparedStatement obtenerIdAsistenteStmt = connection.prepareStatement(obtenerIdAsistenteQuery);
            obtenerIdAsistenteStmt.setString(1, user);
            ResultSet resultado = obtenerIdAsistenteStmt.executeQuery();

            if (resultado.next()) {
                idAsistente = resultado.getString("idAsistente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // Comprobar si el asistente está suscrito
        String comprobarSuscritoQuery = "SELECT idReceptor FROM ReceptoresNotificaciones WHERE idReceptor = ? AND idNotificacion = 0";

        try {
            PreparedStatement comprobarSuscritoStmt = connection.prepareStatement(comprobarSuscritoQuery);
            comprobarSuscritoStmt.setString(1, idAsistente);
            ResultSet suscripcionResult = comprobarSuscritoStmt.executeQuery();

            boolean suscrito = suscripcionResult.next();

            if (!suscrito) {
                // El asistente no está suscrito, agregar el mensaje de error
                Map<String, Object> error = new HashMap<>();
                error.put("error", "No está suscrito");
                notificaciones.add(error);
                return notificaciones;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return notificaciones;
        }

        // Obtener las notificaciones del asistente con el ID obtenido
        String obtenerNotificacionesQuery = "SELECT n.idNotificacion, n.idEmisor, n.idTipoUsuario, n.fecha, n.contenido, rn.leida FROM Notificaciones n " +
            "INNER JOIN ReceptoresNotificaciones rn ON n.idNotificacion = rn.idNotificacion " +
            "WHERE rn.idReceptor = ? AND rn.idNotificacion <> 0";

        try {
            // Preparar la consulta para obtener las notificaciones
            PreparedStatement obtenerNotificacionesStmt = connection.prepareStatement(obtenerNotificacionesQuery);
            obtenerNotificacionesStmt.setString(1, idAsistente);

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

    public String agregarNotificacion(Notificacion notificacion, String user) {
        String idAsistente = null;
        String idNotificacion = null;

        // Buscar el ID del asistente en la tabla Asistentes
        String buscarIdAsistenteQuery = "SELECT idAsistente FROM Asistentes WHERE correo = ? AND idNotificacion <> 0";
        try {
            PreparedStatement buscarIdAsistenteStmt = connection.prepareStatement(buscarIdAsistenteQuery);
            buscarIdAsistenteStmt.setString(1, user);
            ResultSet resultado = buscarIdAsistenteStmt.executeQuery();

            if (resultado.next()) {
                // Obtener el ID del asistente
                idAsistente = resultado.getString("idAsistente");

                // Insertar un registro en la tabla Notificaciones
                String insertarNotificacionQuery = "INSERT INTO Notificaciones (idEmisor, fecha, contenido, idTipoUsuario) " +
                        "VALUES (?, ?, ?, 3)";
                try {
                    PreparedStatement insertarNotificacionStmt = connection.prepareStatement(insertarNotificacionQuery);
                    insertarNotificacionStmt.setString(1, idAsistente);
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

        return idAsistente + " " + idNotificacion;
    }

    public void notificar(String observadorUser, Notificacion notificacion) {
        String buscarIdAsistenteQuery = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
        try {
            PreparedStatement buscarIdAsistenteStmt = connection.prepareStatement(buscarIdAsistenteQuery);
            buscarIdAsistenteStmt.setString(1, observadorUser);
            ResultSet resultado = buscarIdAsistenteStmt.executeQuery();

            if (resultado.next()) {
                // Obtener el ID del asistente
                int idAsistente = resultado.getInt("idAsistente");

                // Insertar un registro en la tabla ReceptoresNotificaciones
                String insertarReceptorQuery = "INSERT INTO ReceptoresNotificaciones (idReceptor, idNotificacion, idTipoUsuario, Leida, Eliminada) " +
                        "VALUES (?, ?, 3, 0, 0)";
                try {
                    PreparedStatement insertarReceptorStmt = connection.prepareStatement(insertarReceptorQuery);
                    insertarReceptorStmt.setInt(1, idAsistente);
                    insertarReceptorStmt.setInt(2, notificacion.getIdNotificacion());
                    insertarReceptorStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.info("Fallo en la inserción en la tabla ReceptoresNotificaciones");
                }
            } else {
                log.info("No se encuentra el asistente con el correo proporcionado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Fallo en la inserción en la tabla ReceptoresNotificaciones");
        }
    }

    public String eliminarNotificacion(Object idNotificacion, String user) {
        try {
            int idAsistente = 0;
            
            // Buscar el ID del asistente basado en el correo del usuario
            String query = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idAsistente = rs.getInt("idAsistente");
            } else {
                return "Error: No se encontró ningún asistente.";
            }
            
            // Eliminar registros de la tabla basado en el ID del asistente y la notificación
            query = "DELETE FROM ReceptoresNotificaciones WHERE idReceptor = ? AND idNotificacion = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, idAsistente);
            stmt.setInt(2, (int) idNotificacion);
            
            return "Notificacion eliminada exitosamente.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al eliminar las notificaciones.";
        }
    }

    public String eliminarNotificaciones(String user) {
        try {
            String idAsistente = null;
            
            // Buscar el ID del asistente basado en el correo del usuario
            String query = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idAsistente = rs.getString("idAsistente");
            } else {
                return "Error: No se encontró ningún asistente";
            }
            
            // Eliminar registros de la tabla ReceptoresNotificaciones basados en el ID del asistente
            query = "DELETE FROM ReceptoresNotificaciones WHERE idReceptor = ? AND idNotificacion <> 0";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idAsistente);
            int rowsAffected = stmt.executeUpdate();
            
            return "Se eliminaron " + rowsAffected + " notificaciones.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al eliminar las notificaciones.";
        }
    }

    public String desuscribirObservador(String user) {
        try {
            String idAsistente = null;

            // Buscar el ID del asistente basado en el correo del usuario
            String query = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idAsistente = rs.getString("idAsistente");
            } else {
                return "Error: No se encontró ningún asistente con ese correo.";
            }

            // Verificar si el asistente está suscrito en el sistema de notificaciones
            query = "SELECT * FROM ReceptoresNotificaciones WHERE idReceptor = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idAsistente);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return "Error: No estás suscrito en el sistema de notificaciones.";
            }

            // Eliminar registros de la tabla ReceptoresNotificaciones basados en el ID del asistente
            query = "DELETE FROM ReceptoresNotificaciones WHERE idReceptor = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idAsistente);
            stmt.executeUpdate();

            return "Se canceló la suscripción.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al eliminar las notificaciones.";
        }
    }

    public String marcarNotificacionLeida(String user, String codigoNotif) {
        try {
            String idAsistente = null;
            
            // Buscar el ID del asistente basado en el correo del usuario
            String query = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idAsistente = rs.getString("idAsistente");
            } else {
                return "Error: No se encontró ningún asistente con ese correo.";
            }
            
            // Actualizar el campo "leida" a 1 en la tabla ReceptoresNotificaciones
            query = "UPDATE ReceptoresNotificaciones SET leida = 1 WHERE idReceptor = ? AND idNotificacion = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idAsistente);
            stmt.setString(2, codigoNotif);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "Notificación marcada como leída.";
            } else {
                return "No se encontró ninguna notificación con ese código.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al marcar la notificación como leída.";
        }
    }

    public String marcarNotificacionNoLeida(String user, String codigoNotif) {
        try {
            String idAsistente = null;
            
            // Buscar el ID del asistente basado en el correo del usuario
            String query = "SELECT idAsistente FROM Asistentes WHERE correo = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idAsistente = rs.getString("idAsistente");
            } else {
                return "Error: No se encontró ningún asistente con ese correo.";
            }
            
            query = "UPDATE ReceptoresNotificaciones SET leida = 0 WHERE idReceptor = ? AND idNotificacion = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, idAsistente);
            stmt.setString(2, codigoNotif);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                return "Notificación marcada como leída.";
            } else {
                return "No se encontró ninguna notificación con ese código.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un error al marcar la notificación como no leída.";
        }
    }
}
