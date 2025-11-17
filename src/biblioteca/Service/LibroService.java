package biblioteca.Service;

import biblioteca.Config.TransactionManager;
import biblioteca.Entities.Libro;
import biblioteca.DAO.LibroDAO;
import biblioteca.Entities.FichaBibliografica;
import java.sql.SQLException;
import java.util.List;


/**
 * Servicio que encapsula la lógica de negocio relacionada con libros y sus fichas bibliográficas.
 * Coordina operaciones de creación, actualización, eliminación lógica, búsqueda y listado de libros,
 * delegando la persistencia al LibroDAO. También gestiona fichas bibliográficas mediante
 * el FichaBibliograficaService, permitiendo una integración modular entre entidades.
 */

public class LibroService {

    private final LibroDAO libroDAO;
    
    /**
    * Constructor por defecto que inicializa el servicio con instancias internas de DAO y servicio de fichas.
    */
    public LibroService() {
        this.libroDAO = new LibroDAO();
        this.fichaService = new FichaBibliograficaService();
    }
    /**
    * Constructor que permite inyectar dependencias externas para mayor flexibilidad y testeo.
    * @param libroDAO DAO especializado en operaciones de persistencia de libros
    * @param fichaService servicio que gestiona la lógica de negocio de fichas bibliográficas
    */
    public LibroService(LibroDAO libroDAO, FichaBibliograficaService fichaService) {
        this.libroDAO = libroDAO;
        this.fichaService = fichaService;
    }
        
    private final FichaBibliograficaService fichaService;
    
    /**
    * Lista todas las fichas bibliográficas activas registradas en el sistema.
    *
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return Lista completa de fichas bibliográficas, tanto activas como eliminadas.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */
    public List<FichaBibliografica> listarFichas(TransactionManager txManager) throws SQLException {
        return fichaService.listarFichas(txManager);
    }
    
    /**
    * Lista todas las fichas bibliográficas eliminadas logicamente registradas en el sistema.
    *
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return Lista completa de fichas bibliográficas, tanto activas como eliminadas.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */  
    public List<FichaBibliografica> listarFichasEliminadas(TransactionManager txManager) throws SQLException {
        return fichaService.listarFichasEliminadas(txManager);
    }
    
    /**
    * Busca una ficha bibliográfica por su identificador único.
    *
    * @param id identificador de la ficha
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return la ficha correspondiente, o null si no se encuentra
    * @throws SQLException si ocurre un error durante la consulta
    */
    public FichaBibliografica buscarFichaPorId(int id, TransactionManager txManager) throws SQLException {
        return fichaService.buscarPorId(id, txManager);
    }
    
    /**
     * Actualiza los datos de una ficha bibliográfica existente.
     *
     * @param ficha objeto FichaBibliografica con los nuevos datos
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @throws SQLException si ocurre un error durante la operación
     */
    public void actualizarFicha(FichaBibliografica ficha, TransactionManager txManager) throws SQLException {
        fichaService.actualizarFicha(ficha, txManager);
    }
    
    /**
    * Elimina lógicamente una ficha bibliográfica del sistema.
    *
    * @param id identificador único de la ficha
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @throws SQLException si ocurre un error durante la operación
    */
    public void eliminarFicha(int id, TransactionManager txManager) throws SQLException {
        fichaService.eliminarFicha(id, txManager);
    }
    
    
    /**
    * Vincula una ficha bibliográfica existente a un libro existente.
    * Esta operación administrativa permite corregir o establecer la relación entre una ficha y su libro,
    * actualizando el campo libro_id en la tabla fichaBibliografica.
    *
    * Este método no realiza validaciones previas sobre la existencia de los IDs,
    * por lo que se recomienda verificar la validez de los datos antes de invocarlo.
    *
    * Se ejecuta dentro de una transacción gestionada por TransactionManager,
    * lo que permite integrarlo en flujos más amplios de actualización o mantenimiento.
    *
    * @param fichaId el ID de la ficha bibliográfica que se desea vincular
    * @param libroId el ID del libro al que se desea asociar la ficha
    * @param txManager el gestor de transacciones que proporciona la conexión activa
    * @throws SQLException si ocurre un error al ejecutar la actualización en la base de datos
    */
    public void vincularFicha(int fichaId, int libroId, TransactionManager txManager) throws SQLException {
        libroDAO.vincularFicha(fichaId, libroId, txManager);
    }

    /**
    * Crea un nuevo libro en la base de datos, validando previamente sus atributos.
    * Verifica que no exista otro libro con los mismos datos. La operación se realiza
    * dentro de una conexión transaccional proporcionada externamente.
    *
    * @param libro Objeto que contiene los datos del libro a insertar.
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return El identificador único (ID) generado para el nuevo libro.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    * @throws IllegalArgumentException si ya existe un libro con los mismos datos.
    */
    public int crearLibro(Libro libro, TransactionManager txManager) throws SQLException {
        validarLibro(libro);

        if (libroDAO.existeLibro(libro, txManager)) {
            throw new IllegalArgumentException("Ya existe un libro con los mismos datos.");
        }

        return libroDAO.insertar(libro, txManager);
    }
        
    /**
     * Actualiza los datos de un libro existente, validando previamente sus atributos.
     *
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @param libro objeto Libro con los nuevos datos
     * @throws SQLException si ocurre un error durante la operación
     * @throws IllegalArgumentException si los datos no cumplen las reglas de negocio
     */
    public void actualizarLibro(Libro libro, TransactionManager txManager) throws SQLException {
        validarLibro(libro);
        libroDAO.actualizar(libro,txManager);
    }

    /**
     * Elimina lógicamente un libro del sistema, marcándolo como inactivo.
     *
     * @param id identificador único del libro
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @throws SQLException si ocurre un error durante la operación
     */
    public void eliminarLibro(int id, TransactionManager txManager) throws SQLException {
        libroDAO.eliminarLogicamente(id, txManager);
    }

    /**
     * Busca un libro por su identificador único.
     *
     * @param id identificador del libro
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return el libro correspondiente, o null si no se encuentra
     * @throws SQLException si ocurre un error durante la consulta
     */
    public Libro buscarPorId(int id, TransactionManager txManager) throws SQLException {
        return libroDAO.buscarPorId(id, txManager);
    }

    /**
     * Lista todos los libros activos (no eliminados) registrados en el sistema.
     *
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return lista de objetos Libro activos
     * @throws SQLException si ocurre un error durante la consulta
     */
    public List<Libro> listarLibros(TransactionManager txManager) throws SQLException {
        return libroDAO.listarActivos(txManager);
    }
    
    /**
    * Lista todos los libros que han sido eliminados lógicamente.
    *
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return lista de objetos Libro eliminados
    * @throws SQLException si ocurre un error durante la consulta
    */
    public List<Libro> listarEliminados(TransactionManager txManager) throws SQLException {
        return libroDAO.listarEliminados(txManager);
    }

    /**
    * Valida los atributos de un libro según reglas de negocio definidas.
    * Verifica la presencia y longitud del título y autor, y aplica restricciones
    * de tamaño a la editorial y al año de edición. Lanza excepciones descriptivas ante inconsistencias.
    *
    * @param libro objeto Libro a validar
    * @throws IllegalArgumentException si algún atributo no cumple las reglas establecidas
    */
    private void validarLibro(Libro libro) {
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            throw new IllegalArgumentException("El autor es obligatorio.");
        }
        if (libro.getTitulo().length() > 150) {
            throw new IllegalArgumentException("El título excede los 150 caracteres.");
        }
        if (libro.getAutor().length() > 120) {
            throw new IllegalArgumentException("El autor excede los 120 caracteres.");
        }
        if (libro.getEditorial() != null && libro.getEditorial().length() > 100) {
            throw new IllegalArgumentException("La editorial excede los 100 caracteres.");
        }
        if (libro.getAnioEdicion() != null && libro.getAnioEdicion() < 0) {
            throw new IllegalArgumentException("El año de edición no puede ser negativo.");
        }
    }
}
