package Logica;
import java.sql.*;

public class LogIn_Checker {
	
	public Connection chequear_conexion_admin(String clave) throws SQLException {
		
		String servidor = "localhost:3306";
    	String baseDatos = "parquimetros"; 
    	String usuario = "admin";
    	String password = clave;
    	String driverMySQL = "jdbc:mysql://";
    	String driverMariaDB = "jdbc:mariadb://";
        String uriConexion = servidor + "/" + 
    	                     baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
    	
        Connection conexionBD = java.sql.DriverManager.getConnection(driverMySQL+uriConexion, usuario, password);
        
    	if (conexionBD == null) {
    		conexionBD = java.sql.DriverManager.getConnection(driverMariaDB+uriConexion, usuario, password);
    			if (conexionBD == null);
    				throw new SQLException("No se puede accede a la base de datos.");
    	}
    	return conexionBD;
	}
	
	public Connection iniciar_sesion_usuario_inspector() throws SQLException {
		String servidor = "localhost:3306";
    	String baseDatos = "parquimetros"; 
    	String usuario = "inspector";
    	String password = "inspector";
    	String driverMySQL = "jdbc:mysql://";
    	String driverMariaDB = "jdbc:mariadb://";
        String uriConexion = servidor + "/" + 
    	                     baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
    	
        Connection conexionBD = java.sql.DriverManager.getConnection(driverMySQL+uriConexion, usuario, password);
        								
        
        if (conexionBD == null) {
    		conexionBD = java.sql.DriverManager.getConnection(driverMariaDB+uriConexion, usuario, password);
    			if (conexionBD == null)
    					throw new SQLException("No se logró conectar como Inspector");
        }
    	return conexionBD;
	}

}