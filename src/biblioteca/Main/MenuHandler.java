package biblioteca.Main;

import biblioteca.Entities.Libro;
import biblioteca.Entities.FichaBibliografica;
import biblioteca.Service.LibroService;
import biblioteca.Service.FichaBibliograficaService;
import biblioteca.Config.DatabaseConnection;
import biblioteca.Config.TransactionManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Clase encargada de manejar las acciones del menú principal de la aplicación de biblioteca.
 * Recibe las entradas del usuario desde consola y delega las operaciones a los servicios correspondientes,
 * gestionando libros y fichas bibliográficas. Actúa como intermediario entre la interfaz de usuario y la lógica de negocio.
 */


public class MenuHandler {
    private final Scanner scanner;
    private final LibroService libroService;

    /**
    * Constructor que inicializa el manejador de menú con las dependencias necesarias.
    *
    * @param scanner objeto Scanner para la lectura de entradas desde consola
    * @param libroService servicio que encapsula la lógica de negocio relacionada con libros y fichas
    */
    public MenuHandler(Scanner scanner, LibroService libroService) {
        this.scanner = scanner;
        this.libroService = libroService;
    }
    
    /**
    * Crea un nuevo libro junto con su ficha bibliográfica en una única transacción.
    * 
    * Este método solicita al usuario los datos del libro y su ficha bibliográfica,
    * los encapsula en objetos Libro y FichaBibliografica, y los persiste utilizando
    * los servicios correspondientes. Si alguna operación falla, la transacción se revierte
    * para mantener la integridad de los datos.
    * 
    * El flujo incluye la creación del libro, la obtención de su ID, la creación de la ficha
    * asociada y la confirmación de la transacción. En caso de error, se revierte la operación.
    * 
    * @param scanner instancia de Scanner utilizada para capturar la entrada del usuario.
    *                Debe estar inicializada previamente.
    * @param libroService servicio encargado de persistir objetos Libro.
    *                     Debe estar correctamente configurado.
    * 
    * @throws SQLException si ocurre un error al persistir el libro o la ficha bibliográfica.
    * @throws IllegalArgumentException si los datos ingresados son inválidos o incompletos.
    */
    // 1. Crear libro con ficha
    public void crearLibroConFicha() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();

            // --- Crear Libro ---
            System.out.println("\n--- Crear Libro ---");
            System.out.print("Título: ");
            String titulo = scanner.nextLine();
            System.out.print("Autor: ");
            String autor = scanner.nextLine();
            System.out.print("Editorial: ");
            String editorial = scanner.nextLine();
            System.out.print("Año de edición: ");
            Integer anio = Integer.parseInt(scanner.nextLine());

            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setEditorial(editorial);
            libro.setAnioEdicion(anio);

            int libroID = libroService.crearLibro(libro, txManager);
            if (libroID <= 0) throw new SQLException("No se pudo crear el libro.");

            System.out.println("📘 Libro creado con ID: " + libroID);

            // --- Crear Ficha ---
            System.out.println("\n--- Crear Ficha Bibliográfica ---");
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine();
            System.out.print("Clasificación Dewey: ");
            String dewey = scanner.nextLine();
            System.out.print("Estantería: ");
            String estanteria = scanner.nextLine();
            System.out.print("Idioma: ");
            String idioma = scanner.nextLine();

            FichaBibliografica ficha = new FichaBibliografica();
            ficha.setIsbn(isbn);
            ficha.setClasificacionDewey(dewey);
            ficha.setEstanteria(estanteria);
            ficha.setIdioma(idioma);
            ficha.setLibroId(libroID);

            FichaBibliograficaService fichaService = new FichaBibliograficaService();
            int fichaID = fichaService.crearFicha(ficha, txManager);
            if (fichaID <= 0) throw new SQLException("No se pudo crear la ficha.");

            System.out.println("📄 Ficha creada con ID: " + fichaID);

            txManager.commit();
            System.out.println("✅ Libro y ficha creados correctamente.");

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
            System.out.println("↩️ Transacción revertida.");
        }
    }

    /**
    * Lista todos los libros registrados en el sistema utilizando una transacción controlada.
    * 
    * Este método obtiene la lista de libros desde el servicio correspondiente y la muestra
    * por consola. Si no hay libros registrados, informa al usuario. Utiliza TransactionManager
    * para asegurar el acceso consistente a la base de datos.
    * 
    * @param libroService servicio encargado de recuperar los libros desde la base de datos.
    *                     Debe estar correctamente inicializado.
    * 
    * @throws SQLException si ocurre un error al acceder a la base de datos durante la operación.
    */
    // 2. Listar libros
    public void listarLibros() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            List<Libro> libros = libroService.listarLibros(txManager);
            System.out.println("\n--- Libros registrados ---");

            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados.");
            } else {
                for (Libro libro : libros) {
                    System.out.println(libro);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un libro registrado en el sistema utilizando una transacción controlada.
     * 
     * Este método solicita al usuario el ID del libro a modificar, recupera la instancia correspondiente
     * desde la base de datos y permite actualizar sus atributos (título, autor, editorial y año de edición).
     * Utiliza TransactionManager para garantizar la consistencia de la operación. Si el libro no existe,
     * informa al usuario y finaliza el proceso sin realizar cambios.
     * 
     * La transacción se confirma al finalizar la actualización. En caso de error, se revierte automáticamente.
     * 
     * @throws SQLException si ocurre un error al acceder a la base de datos durante la operación.
     * @throws NumberFormatException si el usuario ingresa un valor no numérico al especificar el ID o el año.
     */
    // 3. Actualizar libro
    public void actualizarLibro() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();

            System.out.print("\nID del libro a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine());

            Libro libro = libroService.buscarPorId(id, txManager);
            if (libro == null) {
                System.out.println("Libro no encontrado.");
                return;
            }

            System.out.print("Nuevo título (" + libro.getTitulo() + "): ");
            String nuevoTitulo = scanner.nextLine().trim();
            if (!nuevoTitulo.isEmpty()) libro.setTitulo(nuevoTitulo);

            System.out.print("Nuevo autor (" + libro.getAutor() + "): ");
            String nuevoAutor = scanner.nextLine().trim();
            if (!nuevoAutor.isEmpty()) libro.setAutor(nuevoAutor);

            System.out.print("Nueva editorial (" + libro.getEditorial() + "): ");
            String nuevaEditorial = scanner.nextLine().trim();
            if (!nuevaEditorial.isEmpty()) libro.setEditorial(nuevaEditorial);

            System.out.print("Nuevo año de edición (" + libro.getAnioEdicion() + "): ");
            String nuevoAnio = scanner.nextLine().trim();
            if (!nuevoAnio.isEmpty()) libro.setAnioEdicion(Integer.parseInt(nuevoAnio));

            libroService.actualizarLibro(libro, txManager);
            txManager.commit();

            System.out.println("✅ Libro actualizado correctamente.");
        } catch (SQLException | NumberFormatException e) {
            System.out.println("❌ Error: " + e.getMessage());
            System.out.println("↩️ Transacción revertida.");
        }
    }

    /**
    * Marca lógicamente un libro como eliminado en el sistema utilizando una transacción controlada.
    * 
    * Este método solicita al usuario el ID del libro a eliminar y ejecuta la operación de eliminación lógica
    * a través del servicio correspondiente. No se elimina físicamente el registro de la base de datos, sino que
    * se actualiza su estado para indicar que ha sido eliminado. Utiliza TransactionManager para garantizar
    * la consistencia de la operación.
    * 
    * La transacción se confirma al finalizar la operación. En caso de error, se revierte automáticamente.
    * 
    * @throws SQLException si ocurre un error al acceder a la base de datos durante la operación.
    * @throws NumberFormatException si el usuario ingresa un valor no numérico al especificar el ID.
    */
    // 4. Eliminar libro
    public void eliminarLibro() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();

            System.out.print("\nID del libro a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());

            libroService.eliminarLibro(id, txManager);
            txManager.commit();

            System.out.println("✅ Libro eliminado correctamente.");
        } catch (SQLException | NumberFormatException e) {
            System.out.println("❌ Error al eliminar el libro: " + e.getMessage());
            System.out.println("↩️ Transacción revertida.");
        }
    }

    
    /**
     * Lista todos los libros marcados como eliminados en el sistema utilizando una transacción controlada.
     * 
     * Este método obtiene desde el servicio correspondiente aquellos libros que han sido eliminados lógicamente,
     * es decir, cuyo estado indica que no están activos en el sistema. Utiliza TransactionManager para asegurar
     * la consistencia en el acceso a la base de datos.
     * 
     * Los libros eliminados se muestran por consola. Este método es útil para auditorías, restauraciones o revisiones internas.
     * 
     * @throws SQLException si ocurre un error al acceder a la base de datos durante la operación.
     */
    // 5. Listar Libros eliminados
    public void listarLibrosEliminados() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            System.out.println("\n--- Libros registrados ---");
            for (Libro libro : libroService.listarEliminados(txManager)) {
                System.out.println(libro);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
    }
    
    /**
    * Lista todas las fichas bibliográficas registradas en el sistema utilizando una transacción controlada.
    * 
    * Este método obtiene desde el servicio correspondiente las fichas bibliográficas asociadas a los libros
    * registrados y las muestra por consola. Utiliza TransactionManager para asegurar la consistencia en el acceso
    * a la base de datos durante la operación.
    * 
    * Las fichas incluyen información estructurada sobre cada libro, útil para tareas de catalogación, consulta o exportación.
    * 
    * @throws SQLException si ocurre un error al acceder a la base de datos durante la operación.
    */
    //6. Listar fichas
    public void listarFichas() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            System.out.println("\n--- Fichas bibliográficas ---");
            for (FichaBibliografica ficha : libroService.listarFichas(txManager)) {
                System.out.println(ficha);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar fichas: " + e.getMessage());
        }
    }
    /**
    * Actualiza los datos de una ficha bibliográfica existente, identificada por su ID.
    * Permite modificar los campos principales de la ficha. Si no se encuentra, informa al usuario.
    * Si se presiona Enter deja el valor actual
    */

    // 7. Actualizar ficha por ID
    public void actualizarFichaPorId() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            System.out.print("\nID de la ficha a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine());

            FichaBibliografica ficha = libroService.buscarFichaPorId(id, txManager);
            if (ficha == null) {
                System.out.println("Ficha no encontrada.");
                return;
            }

            System.out.print("Nuevo ISBN (" + ficha.getIsbn() + "): ");
            String nuevoIsbn = scanner.nextLine().trim();
            if (!nuevoIsbn.isEmpty()) ficha.setIsbn(nuevoIsbn);

            System.out.print("Nueva clasificación Dewey (" + ficha.getClasificacionDewey() + "): ");
            String nuevaDewey = scanner.nextLine().trim();
            if (!nuevaDewey.isEmpty()) ficha.setClasificacionDewey(nuevaDewey);

            System.out.print("Nueva estantería (" + ficha.getEstanteria() + "): ");
            String nuevaEstanteria = scanner.nextLine().trim();
            if (!nuevaEstanteria.isEmpty()) ficha.setEstanteria(nuevaEstanteria);

            System.out.print("Nuevo idioma (" + ficha.getIdioma() + "): ");
            String nuevoIdioma = scanner.nextLine().trim();
            if (!nuevoIdioma.isEmpty()) ficha.setIdioma(nuevoIdioma);

            libroService.actualizarFicha(ficha, txManager);
            txManager.commit();
            System.out.println("✅ Ficha actualizada correctamente.");
        } catch (SQLException | NumberFormatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de una ficha bibliográfica registrada en el sistema utilizando una transacción controlada.
     * 
     * Este método solicita al usuario el ID de la ficha a modificar, recupera la instancia correspondiente
     * desde la base de datos y permite actualizar sus atributos: ISBN, clasificación Dewey, estantería e idioma.
     * Utiliza TransactionManager para garantizar la consistencia de la operación. Si la ficha no existe,
     * informa al usuario y finaliza el proceso sin realizar cambios.
     * 
     * La transacción se confirma al finalizar la actualización. En caso de error, se informa y no se aplican cambios.
     * 
     * @throws SQLException si ocurre un error al acceder a la base de datos durante la operación.
     * @throws NumberFormatException si el usuario ingresa un valor no numérico al especificar el ID.
     */
    // 8. Eliminar ficha por ID
    public void eliminarFichaPorId() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            System.out.print("\nID de la ficha a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            libroService.eliminarFicha(id,txManager);
            txManager.commit();
            System.out.println("Ficha eliminada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
    * Muestra por consola todas las fichas bibliográficas que fueron eliminadas mediante baja lógica.
    * 
    * Este método inicia una transacción utilizando TransactionManager para garantizar la coherencia
    * en la lectura de datos. Invoca el servicio libroService.listarFichasEliminadas(txManager) que
    * recupera las fichas marcadas como eliminadas en la base de datos (eliminado = TRUE).
    * 
    * Cada ficha se imprime en consola utilizando su representación textual definida en FichaBibliografica#toString().
    * 
    * En caso de error en la conexión o ejecución de la consulta, se captura la excepción SQLException
    * y se muestra un mensaje descriptivo.
    *
    * @implNote Este método no realiza commit explícito ya que no modifica datos, pero mantiene la estructura
    *           transaccional para respetar la arquitectura del sistema.
    *
    * @see TransactionManager
    * @see libroService#listarFichasEliminadas(TransactionManager)
    * @see FichaBibliografica
    */
    // 9. Listar fichas bibliograficas eliminadas.
    public void listarFichasEliminadas() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            System.out.println("\n--- Fichas bibliográficas ---");
            for (FichaBibliografica ficha : libroService.listarFichasEliminadas(txManager)) {
                System.out.println(ficha);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar fichas: " + e.getMessage());
        }
    }
    
    
    
    /**
    * Vincula una ficha bibliográfica existente a un libro existente.
    * Esta operación administrativa permite corregir o reasignar relaciones entre fichas y libros
    * sin necesidad de eliminar o recrear registros.
    *
    * Uso típico: cuando se desea asociar una ficha que fue creada sin vínculo,
    * o corregir una asociación incorrecta.
    *
    * Requiere que ambos IDs existan en la base de datos. No realiza validaciones internas,
    * por lo que se recomienda verificar previamente la existencia de los registros.
    *
    * Esta operación se ejecuta dentro de una transacción controlada por TransactionManager.
    *
    * @throws SQLException si ocurre un error al ejecutar la actualización en la base de datos
    */
    // 10.Herramientas administrativas
    public void vincularFichaAdministrativamente() {
        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();

            System.out.print("\nID de la ficha bibliográfica a vincular: ");
            int fichaId = Integer.parseInt(scanner.nextLine());

            System.out.print("ID del libro al que se vinculará: ");
            int libroId = Integer.parseInt(scanner.nextLine());

            libroService.vincularFicha(fichaId, libroId, txManager);
            txManager.commit();

            System.out.println("✅ Ficha vinculada correctamente al libro.");
        } catch (SQLException | NumberFormatException e) {
            System.out.println("❌ Error al vincular ficha: " + e.getMessage());
            System.out.println("↩️ Transacción revertida.");
        }
    }

}
