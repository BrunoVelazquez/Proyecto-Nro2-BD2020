package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Logica.Logica;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;


@SuppressWarnings("serial")
public class VentanaParquimetro extends javax.swing.JInternalFrame 
{
   private JPanel panel_Ubicaciones_Parquimetros;
   private JScrollPane scrollPane_1,scrollPane_2,scrollPane;
   private JList<String> lista_ubicaciones,lista_parquimetros,lista_tarjetas;
   private DefaultListModel<String> model1,model2,model3;
   private JLabel lblUbicaciones,lblParquimetros,lblTarjetas;
   private JButton btnConectar; 
   private Logica log;
   
   	public VentanaParquimetro(Logica logica) 
   {
   		
      super();
      initGUI();
      this.log = logica;
      this.generar_ubicaciones();
      this.generar_tartejas();
   }
   
   private void initGUI() 
   {

         setPreferredSize(new Dimension(800, 600));
         this.setBounds(0, 0, 800, 600);
         setVisible(true);
         this.setTitle("Unidad Parquimetro");
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
                  
         panel_Ubicaciones_Parquimetros = new JPanel();
         panel_Ubicaciones_Parquimetros.setBounds(10, 11, 236, 548);
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
         
         lblParquimetros = new JLabel("Parquimetros:");
         lblParquimetros.setBounds(10, 153, 99, 14);
         panel_Ubicaciones_Parquimetros.add(lblParquimetros);
         
         lblTarjetas = new JLabel("Tarjetas (definidas por número de identificación):");
         lblTarjetas.setBounds(10, 301, 99, 14);
         panel_Ubicaciones_Parquimetros.add(lblTarjetas);
         
         scrollPane = new JScrollPane();
         scrollPane.setBounds(10, 326, 142, 109);
         panel_Ubicaciones_Parquimetros.add(scrollPane);
         
         model1 = new DefaultListModel<String>();
         lista_tarjetas = new JList<String>(model1);
         scrollPane.setViewportView(lista_tarjetas);
         
         btnConectar = new JButton("Conectar");
         btnConectar.setBounds(10, 463, 142, 63);
         panel_Ubicaciones_Parquimetros.add(btnConectar);
         btnConectar.setEnabled(false);

         lista_parquimetros.addListSelectionListener(new ListSelectionListener() {
     		public void valueChanged(ListSelectionEvent e) {
     			if (!model1.isEmpty())
     				btnConectar.setEnabled(!lista_tarjetas.isSelectionEmpty());
     		}
       	 
        });
        lista_ubicaciones.addListSelectionListener(new ListSelectionListener() {
     		public void valueChanged(ListSelectionEvent e) {
     			generar_parquimetros(e);
     			btnConectar.setEnabled(false);
     		}
        });
        lista_tarjetas.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				btnConectar.setEnabled(!lista_parquimetros.isSelectionEmpty());
			}
        });
        
        btnConectar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		try {
					String mensaje = log.conectar(lista_tarjetas.getSelectedValue(),lista_parquimetros.getSelectedValue());
					
					JOptionPane.showMessageDialog(null,mensaje,"", JOptionPane.PLAIN_MESSAGE);
					
					model3.removeAllElements();
					btnConectar.setEnabled(false);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "", JOptionPane.ERROR_MESSAGE );
				}
        	}
    	});
   }
   
   private void generar_tartejas() {
	   
	   ArrayList<String> tarjetas = log.get_tarjetas();
	   Iterator<String> ite_tarjetas = tarjetas.iterator();
	   
	   while (ite_tarjetas.hasNext()) {
		   String id_tarjeta = ite_tarjetas.next();
		   this.model1.addElement(id_tarjeta);
	   }
	}
   private void thisComponentHidden(ComponentEvent evt) 
   {
      this.log.desconectarBD();
   }
   
   	private void generar_parquimetros(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			model3.removeAllElements();
			String id_parq = "";
			String ubicacion_seleccionada = (String) lista_ubicaciones.getSelectedValue();
			ArrayList<String> parquimetros = log.get_parquimetros_de_ubicaciones(ubicacion_seleccionada.split(","),"ubicaciones");
			
			Iterator<String> ite_parquimetros = parquimetros.iterator();
			   
		   while (ite_parquimetros.hasNext()) {
			   id_parq = ite_parquimetros.next();
			   model3.addElement(id_parq);
		   }
		}
		
	}
	
   	private void generar_ubicaciones() {
	   
	   ArrayList<String[]> ubicaciones = this.log.get_ubicaciones("ubicaciones");
	   Iterator<String[]> ite_ubicaciones = ubicaciones.iterator();
	   
	   
	   while (ite_ubicaciones.hasNext()) {
		   String[] calle_altura = ite_ubicaciones.next();
		   String calle_altura_para_lista = calle_altura[0] +","+ calle_altura[1];
		   this.model2.addElement(calle_altura_para_lista);		   
	   }
   }
}
