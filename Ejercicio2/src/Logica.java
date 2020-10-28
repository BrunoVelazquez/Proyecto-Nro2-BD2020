
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logica {

	private LogIn_Checker lic;
	private Connection conexionBD;
	private Statement stmt;
	private PreparedStatement p_stmt;
	
	public Logica() {
		lic = new LogIn_Checker();
		conexionBD = null;
		stmt = p_stmt = null;
		new ArrayList<String>();
		new ArrayList<String>();
	}
	
	public void iniciar_sesion_admin(String clave) throws SQLException {
		conexionBD = lic.chequear_conexion_admin(clave);
	}
	
	public void iniciar_sesion_usuario_inspector() throws SQLException {
		conexionBD = lic.iniciar_sesion_usuario_inspector();
	}
	
	public String iniciar_sesion_inspector(int legajo, String contrasenia) throws SQLException {
		
		boolean coincide = false;
		String sql = "select md5(?) as password";
		String pass_md5 = "";
		String toRet = "";
		p_stmt = conexionBD.prepareStatement(sql);
		p_stmt.setString(1,contrasenia);
		ResultSet rs = p_stmt.executeQuery();
		
		while (rs.next())
			pass_md5 = rs.getString("password");
		
		sql = "SELECT legajo, md5(password) as coded_pass,nombre,apellido "
			+ "FROM inspectores "
			+ "WHERE legajo = ?";
		
		p_stmt = conexionBD.prepareStatement(sql);
		p_stmt.setInt(1,legajo);
		rs = p_stmt.executeQuery();
		
		while (rs.next()) {
			coincide = (rs.getInt("legajo") == legajo) && (rs.getString("coded_pass").equals(pass_md5));
			toRet += rs.getString("nombre")+" "+rs.getString("apellido");
		}
		if (!coincide)
			throw new SQLException("Datos incorrectos, vuelva a intentar.");
		
		rs.close();
		p_stmt.close();
		
		return toRet;
	}
	
	public ResultSet ejecutar_sentencia(String sql) throws SQLException {
		
		ResultSet toRet = null;
		boolean es_consulta = false;
		stmt = conexionBD.createStatement();
		es_consulta = (boolean) stmt.execute(sql);
			
		if (es_consulta)
			toRet = stmt.getResultSet();
		
		return toRet;
	}
	
	public void cerrar_sentencia() {
		try {
			this.stmt.close();
		} catch (SQLException e) {}
	}
	
	public ArrayList<String> get_tablas(){
		
		ArrayList<String> toRet = new ArrayList<String>();
		ResultSet tablas_aux = null;
		String sql = "show tables";
		String nombre = "";
		
		try {
			stmt = conexionBD.createStatement();
			tablas_aux = stmt.executeQuery(sql);
			
			while(tablas_aux.next()) {
				nombre = tablas_aux.getString(1);
				toRet.add(nombre);
			}
		
			tablas_aux.close();
			stmt.close();
		} catch (SQLException e) {}
		
		return toRet;
	}
	
	public ArrayList<String> get_atributos(String tabla){
		
		ArrayList<String> toRet = new ArrayList<String>();
		String sql = "describe " + tabla;
		
		try {
			Statement stmt = conexionBD.createStatement();
			ResultSet atributos_aux = stmt.executeQuery(sql);
			
			while(atributos_aux.next()) {
				toRet.add(atributos_aux.getString(1));
			}
			atributos_aux.close();
			stmt.close();
		} catch (SQLException e) {}
		return toRet;
	}
	
	public ArrayList<String[]> get_ubicaciones(){
		
		ArrayList<String[]> toRet = new ArrayList<String[]>();
		String sql = "SELECT calle,altura "
					+"FROM estacionados "
					+"ORDER BY calle";

		try {
			stmt = conexionBD.createStatement();
			ResultSet ubicaciones = stmt.executeQuery(sql);
			
			while(ubicaciones.next()) {
				String[] calle_altura = new String[2];
				calle_altura[0] = ubicaciones.getString("calle");
				calle_altura[1] = ubicaciones.getString("altura");
				toRet.add(calle_altura);
			}
			stmt.close();
			ubicaciones.close();
		} catch (SQLException e) {}
		return toRet;
	}
	
	
	public ArrayList<String> get_parquimetros_de_ubicaciones(String[] calle_altura) {
		ArrayList<String> toRet = new ArrayList<String>();
		
		String sql = "SELECT id_parq "
					+"FROM estacionados NATURAL JOIN parquimetros "
					+"WHERE calle = ? AND altura = ?";
		
		try {
			p_stmt = conexionBD.prepareStatement(sql);
			p_stmt.setString(1, calle_altura[0]);
			p_stmt.setString(2, calle_altura[1]);
			ResultSet parquimetros = p_stmt.executeQuery();
			
			while(parquimetros.next()) {
				toRet.add(parquimetros.getString("id_parq"));
			}
			p_stmt.close();
			parquimetros.close();
		} catch (SQLException e) {}
		
		return toRet;
	}
	
	//Verifica si un parquimetro existe en esa ubicacion
	public boolean esParquimetroValido(String[] data) {
		boolean toRet = false;
		if(data.length < 3)
			return false;
		
		String calle = data[0];
		int altura = Integer.parseInt(data[1]);
		int idParquimetro = Integer.parseInt(data[2]);

		try {
			Statement statement = conexionBD.createStatement();
			String consulta = "SELECT * FROM parquimetros WHERE calle = '"+calle+ "' and altura = " +altura+ " and id_parq = " +idParquimetro;
			ResultSet res = statement.executeQuery(consulta);
			//verifico si existe por lo menos una tupla
			if(res.next()) //si entra aca significa q tiene por lo menos una tupla y existe este parquimetro
				toRet = true;
			
			statement.close();
			res.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		   return toRet;
	   }
	
	//registra en la tabla accede, que el inspector accedio al parquimetro
	public void registrarIngresoInspector(int legajo, int parquimetro) {
		try {
			Statement statement = conexionBD.createStatement();
			String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); 
			String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
			String insert = "INSERT INTO accede (legajo, id_parq, fecha, hora) VALUES (" +legajo+ ", " +parquimetro+ ", '" +fecha+ "', '"+hora+"')";
			System.out.println(insert);
			statement.executeUpdate(insert);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	public void chequear_patente(String text) throws WrongInputException {
		
		String sql = "SELECT patente "
					+"FROM estacionados "
					+"WHERE patente = ?";
				
		ResultSet rs;
		boolean existe = false;
		try {
			p_stmt = conexionBD.prepareStatement(sql);
			p_stmt.setString(1,text);
			rs = p_stmt.executeQuery();
			
			existe = rs.next();
			rs.close();
			p_stmt.close();
			
			if (!existe)
				throw new WrongInputException("La patente no existe en la base de datos");
			
		} catch (SQLException e) {}	
	}
}

