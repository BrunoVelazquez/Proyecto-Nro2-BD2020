package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.PlainDocument;

import Logica.Logica;
import Logica.WrongInputException;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;


@SuppressWarnings("serial")
public class VentanaUnidadPersonal extends javax.swing.JInternalFrame 
{
	private JTextField textFieldPatente;
   private JPanel panel_Patentes,panel_Ubicaciones_Parquimetros;
   private JScrollPane scrollPane,scrollPane_1,scrollPane_2;
   private JList<String> lista_patentes,lista_ubicaciones,lista_parquimetros;
   private DefaultListModel<String> model1,model2,model3;
   private JLabel lblPatente,lblListaPatentes,lblUbicaciones,lblParquimetros;
   private JButton btnIngresarPatente,btnRegistrarAcceso_LabrarMultas;
   private Logica log;
   
   	public VentanaUnidadPersonal(Logica logica) 
   {
   		
      super();
      initGUI();
      this.log = logica;
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
         btnRegistrarAcceso_LabrarMultas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registrar_acceso();
				String ubicacion_seleccionada = (String) lista_ubicaciones.getSelectedValue();
				String[] arreglo = ubicacion_seleccionada.split(",");
				log.labrarMultas(model1, arreglo[0], Integer.parseInt(arreglo[1]));
				btnRegistrarAcceso_LabrarMultas.setEnabled(false);
				model1.removeAllElements();
			}
         });
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
	
   public void generar_ubicaciones() {
	   
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
	
	/*
	 * 	PROXIMOS UPDATES
	 * 		Cuando se presiona el boton de Registrar acceso y labrar multas, debe suceder lo siguiente
	 * 
	 * 			Si el inspector está asociado con la ubicación y es su horario laboral
	 * 				Mientras hayan patentes sin comprobar en la lista
	 * 				LabrarMultasCorrespondientes( ... )
	 * 			Sino
	 * 				Notificar que no puede labrar multas
	 * 
	 * 
	 * 			LabrarMultasCorrespondientes( ... )
	 * 				- Se recibe una lista de patentes
	 * 				Si la patente no existe en la BD, el metodo INSERT a utilizar en Multas tirará error,
	 * 				que será atrapado por la GUI, y ésta la agregará en una lista a mostrar por pantalla
	 * 				las patentes que no existen en la BD
	 */
}
