package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import Logica.Logica;
import Logica.WrongInputException;



public class VentanaPrincipal extends javax.swing.JFrame 
{

	private Logica logica;
   private Ventana_Consulta_A_Logica vcal;
   private VentanaUnidadPersonal vup;
   private VentanaParquimetro vp;
   
   private JDesktopPane jDesktopPane1;
   private JMenuBar jMenuBar1;
   private JMenuItem mniConsultas, mniUnidadPersonal, mniConectar;
   private JMenu menuAdmin, menuInspector, menuParq;

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
                   {
                   	menuParq = new JMenu();
                   	menuParq.setText("Parquimetro");
                   	jMenuBar1.add(menuParq);
                   	
                   	
                   	{
                   		mniConectar = new JMenuItem();
                   		mniConectar.setText("Conexi\u00F3n de una tarjeta");
                   		menuParq.add(mniConectar);
                   		mniConectar.addActionListener(new ActionListener() {
                   			public void actionPerformed(ActionEvent evt) {
                   				mniConectarActionPermformed(evt);
                   			}

                   		});
                   	}
                   }
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
		  try {
	    	  int res = JOptionPane.showConfirmDialog(null, pass, "Ingrese contraseña de Administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	    	  if (res == JOptionPane.OK_OPTION) {
	    	    String password = new String(pass.getPassword());
	    	   
				logica.iniciar_sesion_admin(password);
	    	    this.vcal = new Ventana_Consulta_A_Logica(logica);
	    	    this.jDesktopPane1.add(this.vcal);
	    	    this.vcal.generar_tabla_atributos();
	            this.vcal.setMaximum(true);
	            this.vcal.setVisible(true);
	    	  }
	         
	      } catch (PropertyVetoException e) {}
	      	catch (SQLException e) {
	    	  JOptionPane.showMessageDialog(null, "ERROR: \n" + e.getMessage()+"\nMensaje para el administrador: "
	    	  									+"Verifique su contraseña, ó bien, consulte por el "
	    	  									+ "funcionamiento de la base de datos.", "", JOptionPane.ERROR_MESSAGE );
		} finally {
			pass.setText("");
		}
		  
	  }  
 	
   //Accion que ocurre luego de presionar el boton de unidad personal
   private void mniUnidadPersonalActionPerformed(ActionEvent evt) {
	   
	   //Se pide el login
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
				   int legajo = this.validarCampos(nro_legajo,pass);
				   logica.iniciar_sesion_usuario_inspector();
				   String password = new String(pass.getPassword());
				   String nombre_completo =logica.iniciar_sesion_inspector(legajo,password);
				   JOptionPane.showMessageDialog(null, "Bienvenido: "+nombre_completo, "Sesión iniciada", JOptionPane.PLAIN_MESSAGE);
				   
				   //Se muestran los campos para ingresar las patentes, ubicacion y id parquimetro hasta que sean validos.
				   this.vup = new VentanaUnidadPersonal(logica);
				   this.jDesktopPane1.add(this.vup);
				   this.vup.setMaximum(true);
				   this.vup.setVisible(true);
			   }
				
				//Si el login no fue correcto 
		   	} 	catch (PropertyVetoException e) {}
		   		catch (SQLException e) {
		   			JOptionPane.showMessageDialog(null,e.getMessage(), "", JOptionPane.ERROR_MESSAGE );
		   		}
	   			catch (WrongInputException e) {
				JOptionPane.showMessageDialog(null, "Datos erróneos:\n" + e.getMessage(), "", JOptionPane.ERROR_MESSAGE );
		  	  	conectado = false;

			} finally {
				   nro_legajo.setText("");
				   pass.setText("");
			}
		   	 
	   }

	   
   }
   
   private void mniConectarActionPermformed(ActionEvent evt) {
	   try {
		   logica.iniciar_sesion_usuario_parquimetro();
		   vp = new VentanaParquimetro(logica);
		   this.jDesktopPane1.add(this.vp);
		   this.vp.setMaximum(true);
		   this.vp.setVisible(true);
	   }
	   catch (PropertyVetoException e) {}
	   catch (SQLException e) {
		   JOptionPane.showMessageDialog(null,e.getMessage(), "", JOptionPane.ERROR_MESSAGE );
	   } 
   }
   
   private int validarCampos(JTextField nro_legajo, JPasswordField pass) throws WrongInputException{
	   int toRet = 0;
	   boolean faltante = (nro_legajo.getText().isEmpty()) || (pass.getPassword().length == 0);
	   
		   if (!faltante) {
			   try {
			   toRet = Integer.parseInt(nro_legajo.getText());
			   
			   }
			   catch (NumberFormatException e) {
				   throw new WrongInputException("El legajo debe ser numerico");
			   }
		   }
		   else
			   throw new WrongInputException("No deje espacios en blanco");
	   
	   return toRet;
   }
  
}

