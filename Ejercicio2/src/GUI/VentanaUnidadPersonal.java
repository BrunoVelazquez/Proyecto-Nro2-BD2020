package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.PlainDocument;

import Logica.ExceptionNoExisteEnBD;
import Logica.Logica;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import quick.dbtable.DBTable;


@SuppressWarnings("serial")
public class VentanaUnidadPersonal extends javax.swing.JInternalFrame 
{
	private JTextField textFieldPatente;
   private JPanel panel_Patentes,panel_Ubicaciones_Parquimetros,panel_Multas;
   private JScrollPane scrollPane,scrollPane_1,scrollPane_2;
   private JList<String> lista_patentes,lista_ubicaciones,lista_parquimetros;
   private DefaultListModel<String> model1,model2,model3;
   private JLabel lblPatente,lblListaPatentes,lblUbicaciones,lblParquimetros,lbl_multas;
   private JButton btnIngresarPatente,btnRegistrarAcceso_LabrarMultas;
   private DBTable tabla;
   private Logica log;
   
   	public VentanaUnidadPersonal(Logica logica) 
   {
   		
      super();
      initGUI();
      this.log = logica;
      this.generar_ubicaciones();
   }
   
   private void initGUI() 
   {

         setPreferredSize(new Dimension(800, 600));
         this.setBounds(0, 0, 800, 600);
         setVisible(true);
         this.setTitle("Consultas (Utilizando DBTable)");
         this.setClosable(true);
         this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
         this.setMaximizable(true);
         this.addComponentListener(new ComponentAdapter() {
             public void componentHidden(ComponentEvent evt) {
                thisComponentHidden(evt);
             }
          });
         
         
         setResizable(false);
         getContentPane().setLayout(null);
         
         panel_Patentes = new JPanel();
         panel_Patentes.setBounds(0, 0, 152, 206);
         getContentPane().add(panel_Patentes);
         panel_Patentes.setLayout(null);
         
         scrollPane = new JScrollPane();
         scrollPane.setBounds(10, 95, 142, 111);
         panel_Patentes.add(scrollPane);
         
         model1 = new DefaultListModel<String>();
         lista_patentes = new JList<String>(model1);
         scrollPane.setViewportView(lista_patentes);
         
         lblPatente = new JLabel("Patente:");
         lblPatente.setBounds(10, 14, 46, 14);
         panel_Patentes.add(lblPatente);
         
         textFieldPatente = new JTextField();
         textFieldPatente.setBounds(66, 11, 86, 20);
         panel_Patentes.add(textFieldPatente);
         
         textFieldPatente.setDocument(new JTextFieldLimit(6));
         this.textFieldPatente.addKeyListener(new KeyAdapter() {
        	 public void keyTyped(KeyEvent e) {
        		 jTextField1KeyTyped(e);
        	 }
         });
         
         btnIngresarPatente = new JButton("Ingresar patente");
         btnIngresarPatente.setBounds(10, 39, 142, 23);
         btnIngresarPatente.setEnabled(false);
         panel_Patentes.add(btnIngresarPatente);
         btnIngresarPatente.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				agregar_a_lista_patentes(textFieldPatente.getText());
			}      	 
         });
         
         lblListaPatentes = new JLabel("Lista de patentes ingresadas:");
         lblListaPatentes.setBounds(10, 73, 142, 14);
         panel_Patentes.add(lblListaPatentes);
         
         panel_Ubicaciones_Parquimetros = new JPanel();
         panel_Ubicaciones_Parquimetros.setBounds(0, 214, 152, 346);
         getContentPane().add(panel_Ubicaciones_Parquimetros);
         panel_Ubicaciones_Parquimetros.setLayout(null);
         
         lblUbicaciones = new JLabel("Ubicaciones:");
         lblUbicaciones.setBounds(10, 0, 99, 14);
         panel_Ubicaciones_Parquimetros.add(lblUbicaciones);
         
         scrollPane_1 = new JScrollPane();
         scrollPane_1.setBounds(10, 25, 142, 111);
         panel_Ubicaciones_Parquimetros.add(scrollPane_1);
         
         model2 = new DefaultListModel<String>();
         lista_ubicaciones = new JList<String>(model2);
         scrollPane_1.setViewportView(lista_ubicaciones);
         
         scrollPane_2 = new JScrollPane();
         scrollPane_2.setBounds(11, 179, 141, 111);
         panel_Ubicaciones_Parquimetros.add(scrollPane_2);
         
         model3 = new DefaultListModel<String>();
         lista_parquimetros = new JList<String>(model3);
         scrollPane_2.setViewportView(lista_parquimetros);
         lista_parquimetros.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!model1.isEmpty())
					btnRegistrarAcceso_LabrarMultas.setEnabled(true);
			}
        	 
         });
         lista_ubicaciones.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				generar_parquimetros(e);
				btnRegistrarAcceso_LabrarMultas.setEnabled(false);
			}
         });
         lblParquimetros = new JLabel("Parquimetros:");
         lblParquimetros.setBounds(10, 153, 99, 14);
         panel_Ubicaciones_Parquimetros.add(lblParquimetros);
         
         btnRegistrarAcceso_LabrarMultas = new JButton("<html>Registrar acceso y<br/>labrar multas</html>");
         btnRegistrarAcceso_LabrarMultas.setBounds(10, 301, 142, 45);
         panel_Ubicaciones_Parquimetros.add(btnRegistrarAcceso_LabrarMultas);
         btnRegistrarAcceso_LabrarMultas.setEnabled(false);
         
         panel_Multas = new JPanel();
         panel_Multas.setBounds(162, 11, 612, 549);
         getContentPane().add(panel_Multas);
         panel_Multas.setLayout(null);
         
         tabla = new DBTable();
         tabla.setEditable(false);
         tabla.setBounds(0, 59, 602, 479);
         panel_Multas.add(tabla);
         
         lbl_multas = new JLabel("Multas nuevas generadas:");
         lbl_multas.setBounds(0, 0, 127, 14);
         panel_Multas.add(lbl_multas);
         btnRegistrarAcceso_LabrarMultas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				registrar_acceso();
				String ubicacion_seleccionada = (String) lista_ubicaciones.getSelectedValue();
				String[] arreglo = ubicacion_seleccionada.split(",");
				try {
					int id_asociado_con = comprobar_ubicacion_asociada(arreglo);
					comprobar_dia_horario(id_asociado_con);
					ArrayList<Integer> lista_numero_multas = labrar_multas(id_asociado_con,arreglo[0],Integer.parseInt(arreglo[1]));
					
					mostrar_en_tabla(id_asociado_con,lista_numero_multas);
					
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "", JOptionPane.ERROR_MESSAGE );
				}
				finally {
					btnRegistrarAcceso_LabrarMultas.setEnabled(false);
					model1.removeAllElements();
				}
			}
			
         });

   }
   
   private void thisComponentHidden(ComponentEvent evt) 
   {
      this.log.desconectarBD();
   }
   
   	private class JTextFieldLimit extends PlainDocument {
	   private int limit;

	   JTextFieldLimit(int limit) {
	    super();
	    this.limit = limit;
	    }

	   public void insertString( int offset, String  str, AttributeSet attr) throws BadLocationException {
	     if (str == null) return;

	     if ((getLength() + str.length()) <= limit) {
	       super.insertString(offset, str, attr);
	     }
	   }
	 }
   
   	private void generar_parquimetros(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			model3.removeAllElements();
			String id_parq = "";
			String ubicacion_seleccionada = (String) lista_ubicaciones.getSelectedValue();
			ArrayList<String> parquimetros = log.get_parquimetros_de_ubicaciones(ubicacion_seleccionada.split(","));
			
			Iterator<String> ite_parquimetros = parquimetros.iterator();
			   
		   while (ite_parquimetros.hasNext()) {
			   id_parq = ite_parquimetros.next();
			   model3.addElement(id_parq);
		   }
		}
		
	}
	
   	private void generar_ubicaciones() {
	   
	   ArrayList<String[]> ubicaciones = this.log.get_ubicaciones();
	   Iterator<String[]> ite_ubicaciones = ubicaciones.iterator();
	   
	   
	   while (ite_ubicaciones.hasNext()) {
		   String[] calle_altura = ite_ubicaciones.next();
		   String calle_altura_para_lista = calle_altura[0] +","+ calle_altura[1];
		   this.model2.addElement(calle_altura_para_lista);		   
	   }
   }
   
   	private void jTextField1KeyTyped(KeyEvent evt) {
	   	boolean max = this.textFieldPatente.getText().length() + 2  >= 7;
	   	this.btnIngresarPatente.setEnabled(max);
	    
	}
   
   	private void agregar_a_lista_patentes(String text) {	   
	   String texto_mayus = text.toUpperCase();

	   if (!this.model1.contains(texto_mayus))
	   		this.model1.addElement(texto_mayus);

	   this.textFieldPatente.setText("");
	   this.btnIngresarPatente.setEnabled(false);
	   this.lista_parquimetros.clearSelection();
	   this.btnRegistrarAcceso_LabrarMultas.setEnabled(false);
   }
   
   	private void registrar_acceso() {
		log.registrarAccesoInspector(Integer.parseInt(this.lista_parquimetros.getSelectedValue()));
	}
	
	private int comprobar_ubicacion_asociada(String[] arreglo) throws SQLException {
		int toRet = 0;
		toRet = this.log.obtenerIdAsociado(arreglo[0],Integer.parseInt(arreglo[1]));
		return toRet;
	}
   
	private void comprobar_dia_horario(int id_asociado_con) throws SQLException {
		this.log.comprobar_dia_horario(id_asociado_con);
	}
	
	private ArrayList<Integer> labrar_multas(int id_asociado_con, String calle, int altura) {
		
		ArrayList<String> patentes = new ArrayList<String>();
		ArrayList<String> patentes_no_existentes = new ArrayList<String>();
		ArrayList<Integer> numeros_multas = new ArrayList<Integer>();
		
		for(int i = 0; i < model1.getSize(); i++) {
			patentes.add(model1.get(i));
		}	
		
		Iterator<String> ite_patentes = patentes.iterator();
		
		while (ite_patentes.hasNext()) {
			try {
				numeros_multas.add(log.cargarMulta(ite_patentes.next(), calle, altura, id_asociado_con));
			} catch (ExceptionNoExisteEnBD e) {
				patentes_no_existentes.add(e.getMessage());
			}
		}
		
		if (!patentes_no_existentes.isEmpty())
			mostrar_patentes_no_existentes(patentes_no_existentes);
		
		return numeros_multas;
	}

	private void mostrar_patentes_no_existentes(ArrayList<String> patentes_no_existentes) {
		String mensaje_a_mostrar = "";
		
		Iterator<String> ite_patentes_no_existentes = patentes_no_existentes.iterator();
		
		while (ite_patentes_no_existentes.hasNext())
			mensaje_a_mostrar += ite_patentes_no_existentes.next() +"\n";
		
		JOptionPane.showMessageDialog(null, mensaje_a_mostrar, "Patentes no registradas en el sistema", JOptionPane.ERROR_MESSAGE );
	}
	
	private void mostrar_en_tabla(int id_asociado_con, ArrayList<Integer> lista_numero_multas/*, String[] arreglo*/) {
		
		String sql_para_modelo = "SELECT numero as numero_multa,fecha,hora,calle,altura,patente,legajo as legajo_inspector "
						+ "FROM multa NATURAL JOIN asociado_con";
		tabla.setConnection(log.getConexionBD());
		tabla.setSelectSql(sql_para_modelo);
		try {
			tabla.createColumnModelFromQuery();
			for (int i = 0; i < tabla.getColumnCount(); i++)
	    	  {  		   		  
	    		 if	 (tabla.getColumn(i).getType()==Types.TIME | (tabla.getColumn(i).getType()==Types.DATE))
	    		 {    		 
	    		    tabla.getColumn(i).setType(Types.CHAR);  
	  	       	 }
	          }
			tabla.refresh();			
			tabla.removeAllRows();
			tabla.setSelectSql("");
			Iterator<Integer> ite_lista_numero_multas = lista_numero_multas.iterator();
			
			while(ite_lista_numero_multas.hasNext()) {				
				tabla.insert(this.log.get_datos_multa(ite_lista_numero_multas.next(),id_asociado_con));		
			}
			
		} catch (SQLException e) {}	
	}
}
