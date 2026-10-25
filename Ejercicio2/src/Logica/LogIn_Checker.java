package Logica;
import java.sql.*;

public class LogIn_Checker {
    
    public Connection chequear_conexion_admin(String clave) throws SQLException{
        
        String servidor = "localhost:3306";
        String baseDatos = "parquimetros"; 
        String usuario = "admin";
        String password = clave;
        String driverMySQL = "jdbc:mysql://";
        String uriConexion = servidor + "/" + 
                             baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
        
        Connection conexionBD = null;
        try {
            java.sql.DriverManager.setLoginTimeout(5);
            conexionBD = java.sql.DriverManager.getConnection(driverMySQL+uriConexion, usuario, password);
        } catch (SQLException e) {
            throw new SQLException("No se puede acceder a la base de datos.");
        }
        return conexionBD;
        
    }
    
    public Connection iniciar_sesion_usuario_inspector() throws SQLException{
        String servidor = "localhost:3306";
        String baseDatos = "parquimetros"; 
        String usuario = "inspector";
        String password = "inspector";
        String driverMySQL = "jdbc:mysql://";
        String uriConexion = servidor + "/" + 
                             baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
        
        Connection conexionBD = null;
        try {
            java.sql.DriverManager.setLoginTimeout(5);
            conexionBD = java.sql.DriverManager.getConnection(driverMySQL+uriConexion, usuario, password);
        } catch (SQLException e) {
            throw new SQLException("No se puede acceder a la base de datos.");
        }
        return conexionBD;
    }

    public Connection iniciar_sesion_usuario_parquimetro() throws SQLException {
        String servidor = "localhost:3306";
        String baseDatos = "parquimetros"; 
        String usuario = "parquimetro";
        String password = "parq";
        String driverMySQL = "jdbc:mysql://";
        String uriConexion = servidor + "/" + 
                             baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
        
        Connection conexionBD = null;
        try {
            java.sql.DriverManager.setLoginTimeout(5);
            conexionBD = java.sql.DriverManager.getConnection(driverMySQL+uriConexion, usuario, password);
        } catch (SQLException e) {
            throw new SQLException("No se puede acceder a la base de datos.");
        }
        return conexionBD;
    }

}