package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;


import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Logica.Logica;
import quick.dbtable.DBTable;
import java.awt.Font;


public class Ventana_Consulta_A_Logica extends javax.swing.JInternalFrame{

    private Logica log;
    private JPanel pnlConsulta,panel;
    private JScrollPane panel_consulta_texto,panel_tabla,panel_atributos;
    private JTextArea txtConsulta;
    private JButton btn_ejecutar;
    private JList<String> tablas, atributos;
    private DefaultListModel<String> model1, model2;
    private DBTable tabla;
    
    public Ventana_Consulta_A_Logica(Logica log) 
    {
        super();
        this.log = log;
        initGUI();
      
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
             getContentPane().setLayout(null);
             this.addComponentListener(new ComponentAdapter() {
                 public void componentHidden(ComponentEvent evt) {
                    thisComponentHidden(evt);
                 }
              });
             
             pnlConsulta = new JPanel();
             pnlConsulta.setBounds(0, 0, 774, 186);
             getContentPane().add(pnlConsulta);
             pnlConsulta.setLayout(null);
             
             panel_consulta_texto = new JScrollPane();
             panel_consulta_texto.setBounds(10, 11, 564, 174);
             pnlConsulta.add(panel_consulta_texto);
             
             txtConsulta = new JTextArea();
             panel_consulta_texto.setViewportView(txtConsulta);
             txtConsulta.setText("SELECT * FROM inspectores");
             txtConsulta.setTabSize(3);
             txtConsulta.setRows(10);
             txtConsulta.setFont(new Font("Monospaced", Font.PLAIN, 12));
             txtConsulta.setColumns(80);
             txtConsulta.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
             
             btn_ejecutar = new JButton("Ejecutar");
             btn_ejecutar.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent evt) {
                     ejecutar_sentencia_SQL();
                 }
             });
             btn_ejecutar.setBounds(584, 86, 89, 23);
             pnlConsulta.add(btn_ejecutar);
             
             panel = new JPanel();
             panel.setBounds(10, 197, 764, 353);
             getContentPane().add(panel);
             panel.setLayout(null);
             
             panel_tabla = new JScrollPane();
             panel_tabla.setBounds(534, 0, 220, 176);
             panel.add(panel_tabla);
             
             model1 = new DefaultListModel<>();
             tablas = new JList<String>(model1);
             panel_tabla.setViewportView(tablas);
             tablas.addListSelectionListener(new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent e) {
                    mostrar_atributos_de_tabla_seleccionada(e);
                }
                 
             });
             
             panel_atributos = new JScrollPane();
             panel_atributos.setBounds(534, 187, 220, 155);
             panel.add(panel_atributos);
             
             model2 = new DefaultListModel<>();
             atributos = new JList<String>(model2);
             panel_atributos.setViewportView(atributos);
             
             tabla = new DBTable();
             tabla.setEditable(false);
             tabla.setBounds(0, 0, 524, 353);
             panel.add(tabla);
             
             
       }
       
       private void thisComponentHidden(ComponentEvent evt) 
       {
          this.log.desconectarBD();
       }

       private void ejecutar_sentencia_SQL() {    
            String consulta = txtConsulta.getText().trim();
            try {
                ResultSet rs = log.ejecutar_sentencia(consulta);
                if (rs != null) { //es una consulta
                    tabla.setConnection(log.getConexionBD());
                    tabla.setSelectSql(consulta);
                    tabla.createColumnModelFromQuery();
                    for (int i = 0; i < tabla.getColumnCount(); i++)
                      {                       
                         if     (tabla.getColumn(i).getType()==Types.TIME)  
                         {             
                            tabla.getColumn(i).setType(Types.CHAR);  
                              }
                         if     (tabla.getColumn(i).getType()==Types.DATE)
                         {
                            tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
                         }
                      }
                    tabla.refresh();
                    for (int i = 0; i < tabla.getColumnCount(); i++)
                        tabla.getColumn(i).setMinWidth(60);
                    rs.close();
                } else {
                    JOptionPane.showMessageDialog(null, "Informaci�n agregada/actualizada/borrada correctamente", "", JOptionPane.INFORMATION_MESSAGE );
                }                                                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error sint�tico en la sentencia SQL \n" + e.getMessage(), "", JOptionPane.ERROR_MESSAGE );
            }
            finally {
                log.cerrar_sentencia();
            }
        }
       
       private void mostrar_atributos_de_tabla_seleccionada(ListSelectionEvent e) {       
           if (!e.getValueIsAdjusting()) {
               model2.removeAllElements();
                String tablaSeleccionada = (String) tablas.getSelectedValue();
                ArrayList<String> atributos2 = log.get_atributos(tablaSeleccionada);
                Iterator<String> ite_atributos = atributos2.iterator();
                                
                while (ite_atributos.hasNext()) {
                    model2.addElement(ite_atributos.next());
                }
           }
              
           
       }

       public void generar_tabla_atributos(){
               ArrayList<String> tablas2 = log.get_tablas();
             
             Iterator<String> ite_tabla = tablas2.iterator();
             
             while (ite_tabla.hasNext()) {
                 model1.addElement(ite_tabla.next());
             }
       }
       
       
}    