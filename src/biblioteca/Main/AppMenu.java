package biblioteca.Main;

import biblioteca.DAO.FichaBibliograficaDAO;
import biblioteca.DAO.LibroDAO;
import biblioteca.Service.FichaBibliograficaService;
import biblioteca.Service.LibroService;
import java.util.Scanner;

/**
 * Clase principal que gestiona el ciclo de vida del menú de la aplicación de biblioteca.
 * Inicializa los servicios, muestra el menú principal y delega las acciones al MenuHandler
 * según la opción seleccionada por el usuario. Representa el punto de entrada de la aplicación.
 * 
 * IMPORTANTE: Esta clase NO tiene lógica de negocio ni de UI.
 * Solo coordina y delega.
 */

public class AppMenu {
    private final Scanner scanner;
    private final MenuHandler menuHandler;
    private boolean running;

    /**
    * Constructor que inicializa el escáner de entrada, los servicios necesarios y el manejador de menú.
    * Crea instancias de LibroService y MenuHandler, dejando la aplicación lista para ejecutarse.
    */
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        LibroService libroService = createLibroService();
        this.menuHandler = new MenuHandler(scanner, libroService);
        this.running = true;
    }
    /**
     * Método principal que inicia la ejecución de la aplicación.
     * Crea una instancia de AppMenu y lanza el ciclo de ejecución del menú.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */

    public static void main(String[] args) {
        AppMenu app = new AppMenu();
        app.run();
    }
    /**
     * Ejecuta el ciclo principal del menú de la aplicación.
     * Muestra el menú, lee la opción ingresada por el usuario y la procesa.
     * Maneja errores de entrada no numérica y mantiene el ciclo activo hasta que se seleccione la opción de salida.
     */

    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine());
                processOption(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }
        scanner.close();
    }
    /**
    * Procesa la opción seleccionada por el usuario en el menú principal.
    * Delega la acción correspondiente al MenuHandler. Si la opción es inválida, muestra un mensaje de advertencia.
    * @param opcion número ingresado por el usuario que representa una acción del menú
    */

    private void processOption(int opcion) {
        switch (opcion) {
            case 1 -> menuHandler.crearLibroConFicha();
            case 2 -> menuHandler.listarLibros();
            case 3 -> menuHandler.actualizarLibro();
            case 4 -> menuHandler.eliminarLibro();
            case 5 -> menuHandler.listarLibrosEliminados();
            case 6 -> menuHandler.listarFichas();
            case 7 -> menuHandler.actualizarFichaPorId();
            case 8 -> menuHandler.eliminarFichaPorId();
            case 9 -> menuHandler.listarFichasEliminadas();
            case 10 -> menuHandler.vincularFichaAdministrativamente();
            
            case 0 -> {
                System.out.println("Saliendo...");
                running = false;
            }
            default -> System.out.println("Opción no válida.");
        }
    }
    /**
     * Crea e inicializa el servicio de libros con sus dependencias.
     * Instancia los DAOs y servicios necesarios para construir el LibroService,
     * incluyendo el servicio de fichas bibliográficas.
     * @return una instancia completamente configurada de LibroService
     */

    private LibroService createLibroService() {
        FichaBibliograficaDAO fichaDAO = new FichaBibliograficaDAO();
        FichaBibliograficaService fichaService = new FichaBibliograficaService(fichaDAO);
        LibroDAO libroDAO = new LibroDAO();
        return new LibroService(libroDAO, fichaService);
    }
}
