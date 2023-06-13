package com.proyecto1diseno.app.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proyecto1diseno.app.Modelo.AsistenteAdmin;

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
        String obtenerIdAsistenteQuery = "SELECT id FROM Asistentes WHERE correo = ?";
        String insertarReceptorNotificacionQuery = "INSERT INTO ReceptoresNotificaciones (idReceptor, idNotificacion, idTipoUsuario) VALUES (?, 0, 3)";

        try {
            PreparedStatement obtenerIdAsistenteStmt = connection.prepareStatement(obtenerIdAsistenteQuery);
            obtenerIdAsistenteStmt.setString(1, user);
            ResultSet resultado = obtenerIdAsistenteStmt.executeQuery();
            
            if (resultado.next()) {
                String idAsistente = resultado.getString("id");

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

        String obtenerIdAsistenteQuery = "SELECT id FROM Profesores WHERE correo = ?";
        String idAsistente = null;

        try {
            PreparedStatement obtenerIdAsistenteStmt = connection.prepareStatement(obtenerIdAsistenteQuery);
            obtenerIdAsistenteStmt.setString(1, user);
            ResultSet resultado = obtenerIdAsistenteStmt.executeQuery();

            if (resultado.next()) {
                idAsistente = resultado.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (idAsistente == null) {
            return notificaciones;
        }

        // Obtener las notificaciones del asistente con el ID obtenido
        String obtenerNotificacionesQuery = "SELECT n.emisor, n.fecha, n.contenido, rn.leido FROM Notificaciones n " +
                "INNER JOIN ReceptoresNotificaciones rn ON n.id = rn.idNotificacion " +
                "WHERE rn.idReceptor = ? AND rn.idNotificacion <> 0";

        try {
            // Preparar la consulta para obtener las notificaciones
            PreparedStatement obtenerNotificacionesStmt = connection.prepareStatement(obtenerNotificacionesQuery);
            obtenerNotificacionesStmt.setString(1, idAsistente);

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
