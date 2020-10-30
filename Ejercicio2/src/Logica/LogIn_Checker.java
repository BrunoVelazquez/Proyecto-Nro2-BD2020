package Logica;
import java.sql.*;

public class LogIn_Checker {
	
	public Connection chequear_conexion_admin(String clave) throws SQLException {
		
		String servidor = "localhost:3306";
    	String baseDatos = "parquimetros"; 
    	String usuario = "admin";
    	String password = clave;
        String uriConexion = "jdbc:mysql://" + servidor + "/" + 
    	                     baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
    	
        Connection conexionBD = java.sql.DriverManager.getConnection(uriConexion, usuario, password);
    	if (conexionBD == null)
    		throw new SQLException("Ingrese los datos correctamente");
    	
    	return conexionBD;
	}
	
	public Connection iniciar_sesion_usuario_inspector() throws SQLException {
		String servidor = "localhost:3306";
    	String baseDatos = "parquimetros"; 
    	String usuario = "inspector";
    	String password = "inspector";
        String uriConexion = "jdbc:mysql://" + servidor + "/" + 
    	                     baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
    	
        Connection conexionBD = java.sql.DriverManager.getConnection(uriConexion, usuario, password);
        
    	if (conexionBD == null)
    		throw new SQLException("No se logró conectar como Inspector");
    	
    	return conexionBD;
	}

}