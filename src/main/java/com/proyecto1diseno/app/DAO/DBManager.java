package com.proyecto1diseno.app.DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBManager {

    private static final String URL = "jdbc:sqlserver://db-diseno.cwnromljo7ts.us-east-2.rds.amazonaws.com:1433;databaseName=EquipoGuia";
    private static final String USER = "admin";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static AsistenteAdminDAO getAsistenteAdminDAO() throws SQLException {
        return new AsistenteAdminDAO(getConnection());
    }

    public static ProfesorDAO getProfesorDAO() throws SQLException {
        return new ProfesorDAO(getConnection());
    }

    public static EstudianteDAO getEstudianteDAO() throws SQLException {
        return new EstudianteDAO(getConnection());
    }

    public static PlanTrabajoDAO getPlanTrabajoDAO() throws SQLException {
        return new PlanTrabajoDAO(getConnection());
    }
}
