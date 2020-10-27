import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;



@SuppressWarnings("serial")
public class VentanaPrincipal extends javax.swing.JFrame 
{

	private Logica logica;
   private Ventana_Consulta_A_Logica vcal;
   private VentanaTablas ventanaTablas;

   private JDesktopPane jDesktopPane1;
   private JMenuBar jMenuBar1;
   private JMenuItem mniConsultas, mniUnidadPersonal;
   private JMenu menuAdmin, menuInspector;

   /**
   * Auto-generated main method to display this JFrame
   */
   public static void main(String[] args) 
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            VentanaPrincipal inst = new VentanaPrincipal();
            inst.setLocationRelativeTo(null);
            inst.setVisible(true);
         }
      });
   }
   
   public VentanaPrincipal() 
   {
      super();

      initGUI();
      logica = new Logica();
      this.vcal = new Ventana_Consulta_A_Logica(logica);
      this.vcal.setVisible(false);
      this.jDesktopPane1.add(this.vcal);
      
      this.ventanaTablas = new VentanaTablas();
//      this.ventanaTablas.setVisible(false);
//      this.jDesktopPane1.add(this.ventanaTablas);
   }
   
   private void initGUI() 
   {
      try 
      {
         javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
      } 
      catch(Exception e) 
      {
         e.printStackTrace();
      }
      
      try {
         {
            this.setTitle("Ejercicio numero 2");
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.setResizable(false);
         }
         {
            jDesktopPane1 = new JDesktopPane();
            getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
            jDesktopPane1.setPreferredSize(new java.awt.Dimension(800, 600));
         }
         {
            jMenuBar1 = new JMenuBar();
            setJMenuBar(jMenuBar1);
            {
            	menuAdmin = new JMenu();
               jMenuBar1.add(menuAdmin);
               menuAdmin.setText("Admin");
               
               menuInspector = new JMenu();
               jMenuBar1.add(menuInspector);
               menuInspector.setText("Inspector");
               {
                  mniConsultas = new JMenuItem();
                  menuAdmin.add(mniConsultas);
                  mniConsultas.setText("Consultas (Utilizando DBTable)");
                  mniConsultas.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent evt) {
                        mniConsultasActionPerformed(evt);
                     }
                  });
               }
               {
                   mniUnidadPersonal = new JMenuItem();
                   menuInspector.add(mniUnidadPersonal);
                   mniUnidadPersonal.setText("Unidad Personal");
                   mniUnidadPersonal.addActionListener(new ActionListener() {
                      public void actionPerformed(ActionEvent evt) {
                         mniUnidadPersonalActionPerformed(evt);
                      }
                   });
                }
            }
         }
         this.setSize(800, 600);
         pack();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   private void mniConsultasActionPerformed(ActionEvent evt) {
	  JPasswordField pass = new JPasswordField();
	  boolean conectado = false;
	  while (!conectado) {
		  try {
			  conectado = true;
	    	  int res = JOptionPane.showConfirmDialog(null, pass, "Ingrese contraseña de Administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	    	  if (res == JOptionPane.OK_OPTION) {
	    	    String password = new String(pass.getPassword());
	    	    logica.iniciar_sesion_admin(password);
	    	    this.vcal.generar_tabla_atributos();
	            this.vcal.setMaximum(true);
	            this.vcal.setVisible(true); 
	    	  }
	         
	      } catch (PropertyVetoException e) {} 
	      	catch (SQLException e) {
	    	  JOptionPane.showMessageDialog(null, "Ingrese los datos correctamente \n" + e.getMessage(), "", JOptionPane.ERROR_MESSAGE );
	    	  conectado = false;
		}
		  pass.setText("");
	  }  
 	
   }

   private void mniUnidadPersonalActionPerformed(ActionEvent evt) {
	   
	   
	   JTextField nro_legajo = new JTextField();
	   JPasswordField pass = new JPasswordField();
	   boolean conectado = false;
	   while (!conectado) {
		   try {
			   conectado = true;
			   
			   Object[] message = {
			       "Legajo:", nro_legajo,
			       "Contraseña:", pass
			   };
		
			   int res = JOptionPane.showConfirmDialog(null, message, "Ingrese los datos de inspector", JOptionPane.OK_CANCEL_OPTION);
			   
			   if (res == JOptionPane.OK_OPTION) {
				   logica.iniciar_sesion_usuario_inspector();
				   int legajo = this.validarCampos(nro_legajo,pass);
				   String password = new String(pass.getPassword());
				   String nombre_completo =logica.iniciar_sesion_inspector(legajo,password);
				   JOptionPane.showMessageDialog(null, "Bienvenido "+nombre_completo, "Sesión iniciada", JOptionPane.PLAIN_MESSAGE);
			   }
				
				
			} catch (SQLException | NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Datos erróneos:\n" + e.getMessage(), "", JOptionPane.ERROR_MESSAGE );
		  	  	conectado = false;
		  	  	nro_legajo.setText("");
		  	  	pass.setText("");
			}
		   	  
	   }
	   nro_legajo.setText("");
	   pass.setText("");
	   
   }
   
   private int validarCampos(JTextField nro_legajo, JPasswordField pass) throws NumberFormatException{
	   int toRet = 0;
	   boolean faltante = (nro_legajo.getText().isEmpty()) || (pass.getPassword().length == 0);
	   
		   if (!faltante) {
			   try {
			   toRet = Integer.parseInt(nro_legajo.getText());
			   }
			   catch (NumberFormatException e) {
				   throw new NumberFormatException("El legajo debe ser numerico");
			   }
		   }
		   else
			   throw new NumberFormatException("No deje espacios en blanco");
	   
	   return toRet;
   }
   
}
