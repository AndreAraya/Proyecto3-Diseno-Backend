package com.proyecto1diseno.app.DAO;
import com.proyecto1diseno.app.Modelo.Estudiante;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

}



