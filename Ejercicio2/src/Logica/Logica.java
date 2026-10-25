package Logica;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

public class Logica {

    private LogIn_Checker lic;
    private Connection conexionBD;
    private Statement stmt;
    private PreparedStatement p_stmt;
    private int legajo_inspector;
    private static final String[] diaSemana =     {"do","lu","ma","mi","ju","vi","sa"};
    
    public Logica() {
        lic = new LogIn_Checker();
        conexionBD = null;
        stmt = p_stmt = null;
        legajo_inspector = 0;
    }
        
    public Connection getConexionBD() {
        return conexionBD;
    }

    public void desconectarBD() {
        try {
            conexionBD.close();
        } catch (SQLException e) {}
    }
    
    public void iniciar_sesion_admin(String clave) throws SQLException{
        conexionBD = lic.chequear_conexion_admin(clave);
    }
    
    public void iniciar_sesion_usuario_inspector() throws SQLException {
        conexionBD = lic.iniciar_sesion_usuario_inspector();
    }
    
    public void iniciar_sesion_usuario_parquimetro() throws SQLException{
        conexionBD = lic.iniciar_sesion_usuario_parquimetro();
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
        
        sql = "SELECT legajo,password as coded_pass,nombre,apellido "
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
        this.legajo_inspector = legajo;
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
    
    public ArrayList<String[]> get_ubicaciones(String tabla){
        
        ArrayList<String[]> toRet = new ArrayList<String[]>();
        String sql = "SELECT DISTINCT calle,altura "
                    +"FROM "+tabla+" "
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
    
    public ArrayList<String> get_parquimetros_de_ubicaciones(String[] calle_altura,String tabla) {
        ArrayList<String> toRet = new ArrayList<String>();
        
        String sql = "SELECT DISTINCT id_parq "
                    +"FROM "+tabla+" NATURAL JOIN parquimetros "
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
    public void registrarAccesoInspector(int parquimetro) {
        String sql = "INSERT INTO accede (legajo, id_parq, fecha, hora) "
                    +"VALUES(?,?,CURDATE(),CURTIME())";
        try {
            p_stmt= conexionBD.prepareStatement(sql);
            p_stmt.setInt(1,this.legajo_inspector);
            p_stmt.setInt(2,parquimetro);
            p_stmt.executeUpdate();
        } catch (SQLException e) {}
    }
        
    //Carga una multa en la BD con los parametros especificados
    //Se inserta NULL en donde el valor seria un autoincrement
    //Retorna el numero de esa multa recien cargada
    public int cargarMulta(String patente, String calle, int altura, int id_asociado_con) throws ExceptionNoExisteEnBD, ExceptionYaEstacionada {
        int toRet = 0;
        ResultSet rs = null;
        boolean estacionada = verificarPatenteEstacionada(patente, calle, altura);
        
        if (!estacionada) {
        
            String sql = "INSERT INTO multas (numero, fecha, hora, patente, id_asociado_con) "
                    +"VALUES(NULL, CURDATE(), CURTIME(), '"+patente+"', "+id_asociado_con+")";
            
            try {
                stmt = conexionBD.createStatement();
                stmt.executeUpdate(sql);
                rs = stmt.executeQuery("SELECT numero FROM multas ORDER BY numero DESC");
                
                if(rs.next())
                    toRet = rs.getInt("numero");
                
                stmt.close();
                rs.close();
            } catch (SQLException e) {
                System.out.println("llege");
                throw new ExceptionNoExisteEnBD(patente);
            }
            
        } else {
            throw new ExceptionYaEstacionada(patente);
        }
        return toRet;
    }
    
    
    
    //Verifico si la patente se encuentra en la vista estacionados en esa calle y altura
    public boolean verificarPatenteEstacionada(String patente, String calle, int altura) {
        boolean toRet = false;
        String sql = "SELECT patente FROM estacionados WHERE (patente='"+patente+"' AND calle = '"+calle+"' AND altura = "+altura+")";
        
        try {
            Statement st = conexionBD.createStatement();
            ResultSet rs = st.executeQuery(sql);
            toRet = rs.next();
            st.close();
            rs.close();
        } catch (SQLException e) {}
        return toRet;
    }

    //Obtiene el id asociado al inspector logeado actualmente en la calle y altura pasada por parametro.
    public int obtenerIdAsociado(String calle, int altura) throws SQLException{
        int toRet = 0;
        
        //Consigo el dia actual
        LocalDate date = LocalDate.now();
        DayOfWeek dow = date.getDayOfWeek();
        String dayName = dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String dia = "lu"; //Inicializo por defecto en lunes.
        switch (dayName) {
            case "Monday" :
                dia = "lu";
                break;
            case "Tuesday" :
                dia = "ma";
                break;
            case "Wednesday" :
                dia = "mi";
                break;
            case "Thursday" :
                dia = "ju";
                break;
            case "Friday" :
                dia = "vi";
                break;
            case "Saturday" :
                dia = "sa";
                break;
            case "Sunday" :
                dia = "do";
                break;
        }

        //Consigo el turno actual
        Calendar rightNow = Calendar.getInstance();
        int hora = rightNow.get(Calendar.HOUR_OF_DAY); //hour da la hora actual (sin min ni seg)
        
        String turno = "M"; //inicializa en M por default
        if((hora >= 14) && (hora <=20)) {
            turno = "T";
        } else {
            if(hora >= 8 && hora <=14) {
                turno = "M";
            }
        }
        
        String sql = "SELECT * FROM asociado_con "
                    +"WHERE (turno = '"+turno+"' AND dia = '"+dia+"' AND legajo="+legajo_inspector+" AND calle = '"+calle+"' AND altura="+altura+")";
        
        stmt = conexionBD.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if(rs.next())
            toRet = rs.getInt("id_asociado_con");
        else // No encuentra id_asociado_Con
            throw new SQLException("Inspector con legajo "+legajo_inspector+" no tiene asociado dicha ubicaci�n, no se labrar�n las multas.");
        
        stmt.close();
        rs.close();
        return toRet;
            
    }

    public void comprobar_dia_horario(int id_asociado_con) throws SQLException{
        
        boolean le_corresponde = true;
        int dia = 0;
        String turno, limiteInf, limiteSup = "";
        String sql = "SELECT DAYOFWEEK(CURDATE())";
        
        stmt = conexionBD.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if(rs.next()) {
            dia = rs.getInt(1);
        }
        
        sql = "SELECT dia,turno "
                    +"FROM asociado_con "
                    +"WHERE id_asociado_con = "+id_asociado_con;
        
        stmt = conexionBD.createStatement();
        rs = stmt.executeQuery(sql);
        
        if(rs.next()) {
            turno = rs.getString("turno");
            if (turno.equals("M")) {     // es turno ma�ana
                limiteInf = "08:00:00";
                limiteSup = "13:59:59";
            } else {                    // es turno tarde
                limiteInf = "14:00:00";
                limiteSup = "20:00:00";
            }
            
            le_corresponde = rs.getString("dia").toLowerCase().equals(diaSemana[dia-1]);
            le_corresponde &= esta_dentro_de_horario(limiteInf,limiteSup);
        }
        
        rs.close();
        stmt.close();
        if (!le_corresponde)
            throw new SQLException("No le corresponde el d�a y/o el turno actual, no se labrar�n las multas.");
        
    }

    private boolean esta_dentro_de_horario(String limiteInf, String limiteSup) {
        boolean toRet = false;
        
        String sql = "SELECT SIGN(TIMEDIFF(CURTIME(), '"+limiteInf+"')) as horainf, SIGN(TIMEDIFF('"+limiteSup+"',CURTIME())) as horasup";
        Statement stmt_aux;
        ResultSet rs_aux;
        try {
            stmt_aux = conexionBD.createStatement();
            rs_aux = stmt_aux.executeQuery(sql);
            
            if (rs_aux.next()) {
                toRet = (rs_aux.getInt(1) > 0) && (rs_aux.getInt(2) > 0); // esta dentro del horario
            }
            
            rs_aux.close();
            stmt_aux.close();
        } catch (SQLException e) {}

        return toRet;
    }

    
    public Vector<String> get_datos_multa(int numero_multa, int id_asociado_con) {
        Vector<String> toRet = new Vector<String>(7,0);
        
        String sql = "SELECT numero,fecha,hora,calle,altura,patente,legajo "
                + "FROM multas NATURAL JOIN asociado_con "
                + "WHERE numero = ? AND id_asociado_con = ?";

        try {
            p_stmt = conexionBD.prepareStatement(sql);
            p_stmt.setString(1,""+numero_multa);
            p_stmt.setString(2,""+id_asociado_con);
            sql = "SELECT numero,fecha,hora,calle,altura,patente,legajo "
                    + "FROM multas NATURAL JOIN asociado_con "
                    + "WHERE numero = "+numero_multa+" AND id_asociado_con = "+id_asociado_con;
            ResultSet rs = p_stmt.executeQuery();
            
            if(rs.next()) {
                toRet.addElement(rs.getString(1));
                toRet.addElement(rs.getString(2));
                toRet.addElement(rs.getString(3));
                toRet.addElement(rs.getString(4));
                toRet.addElement(rs.getString(5));
                toRet.addElement(rs.getString(6));
                toRet.addElement(rs.getString(7));
            }
            rs.close();
            p_stmt.close();
        } catch (SQLException e) {}
        
        return toRet;
        
    }

    public ArrayList<String> get_tarjetas() {
        ArrayList<String> toRet = new ArrayList<String>();
        
        String sql = "SELECT id_tarjeta "
                    +"FROM tarjetas "
                    +"ORDER BY id_tarjeta";
        try {
            p_stmt = conexionBD.prepareStatement(sql);
            ResultSet tarjetas = p_stmt.executeQuery();
            while(tarjetas.next()) {
                toRet.add(tarjetas.getString("id_tarjeta"));
            }
            p_stmt.close();
            tarjetas.close();
        } catch (SQLException e) {}
        
        return toRet;
    }

    public String conectar(String tarjeta, String parquimetro) throws SQLException {
        String toRet = "";
        String sql = "CALL conectar(?,?)";
        
        p_stmt = conexionBD.prepareStatement(sql);
        p_stmt.setString(1,tarjeta);
        p_stmt.setString(2,parquimetro);
        
        ResultSet res = p_stmt.executeQuery();
        ResultSetMetaData res_metadata = res.getMetaData();
        int columnas = res_metadata.getColumnCount();
        
        if (res.next()) {
            for (int i=1 ; i<=columnas; i++) {
                toRet += res_metadata.getColumnLabel(i)+": "+res.getString(i)+"\n\n";
            }    
        }
        toRet = toRet.replaceAll("_"," ");
        
        return toRet;
    }
    
}