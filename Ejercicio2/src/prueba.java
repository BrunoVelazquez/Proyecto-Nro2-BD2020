import java.sql.SQLException;
import java.util.Scanner;

public class prueba {

	public static void main(String[] args) {
		Logica log = new Logica();
		boolean conectado = false;
		while (!conectado) {
			try {
				log.iniciar_sesion_usuario_inspector();	
				
				conectado = true;
				System.out.print("Ingrese Legajo: ");
				Scanner scanner = new Scanner(System.in);
		        String user= scanner.nextLine();
		        System.out.print("Ingrese contraseña: ");
		        scanner.reset();
		        String con = scanner.nextLine();
		        
		        String nombre_completo = log.iniciar_sesion_inspector(Integer.parseInt(user),con);
				
		        scanner.close();
				}
			 catch (SQLException e) {
				System.out.println(e.getMessage());
				conectado = false;
			}
		}
	}

}
