package biblioteca.Service;

import biblioteca.Config.TransactionManager;
import biblioteca.Entities.FichaBibliografica;
import biblioteca.DAO.FichaBibliograficaDAO;
import java.sql.SQLException;
import java.util.List;
/**
 * Servicio que encapsula la lógica de negocio relacionada con fichas bibliográficas.
 * Coordina operaciones de creación, actualización, eliminación lógica, búsqueda y listado de fichas,
 * delegando la persistencia al FichaBibliograficaDAO. Incluye validaciones previas para asegurar
 * la integridad de los datos antes de interactuar con la base de datos.
 */

public class FichaBibliograficaService {

    private final FichaBibliograficaDAO fichaDAO;
    
    /**
    * Constructor que recibe una instancia de FichaBibliograficaDAO, útil para inyección de dependencias.
    * @param fichaDAO DAO especializado en operaciones de persistencia de fichas bibliográficas
    */
    public FichaBibliograficaService(FichaBibliograficaDAO fichaDAO) {
        this.fichaDAO = fichaDAO;
    }
    
    /**
     * Constructor por defecto que inicializa el servicio con una nueva instancia de {@code FichaBibliograficaDAO}.
     */
    public FichaBibliograficaService() {
        this.fichaDAO = new FichaBibliograficaDAO();
    
    }
    
   /**
    * Crea una nueva ficha bibliográfica en la base de datos, validando previamente sus atributos.
    * Verifica que no exista otra ficha con el mismo ISBN y lanza una excepción si se detecta duplicidad.
    * La operación se realiza dentro de una conexión transaccional proporcionada externamente.
    *
    * @param ficha Objeto que contiene los datos de la ficha bibliográfica a insertar.
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return El identificador único (ID) generado para la nueva ficha bibliográfica.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    * @throws IllegalArgumentException si ya existe una ficha con el mismo ISBN.
    */
    public int crearFicha(FichaBibliografica ficha, TransactionManager txManager) throws SQLException {
        validarFicha(ficha);

        if (fichaDAO.existeFichaPorIsbn(ficha.getIsbn(), txManager)) {
            throw new IllegalArgumentException("Ya existe una ficha con el mismo ISBN.");
        }

        return fichaDAO.insertar(ficha, txManager);
    }

    /**
    * Actualiza los datos de una ficha bibliográfica existente, validando previamente sus atributos.
    *
    * @param ficha objeto FichaBibliografica con los nuevos datos a persistir
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @throws SQLException si ocurre un error durante la actualización
    * @throws IllegalArgumentException si los datos no cumplen las reglas de negocio
    */
    public void actualizarFicha(FichaBibliografica ficha, TransactionManager txManager) throws SQLException {
        validarFicha(ficha);
        fichaDAO.actualizar(ficha, txManager);
    }

    /**
    * Elimina lógicamente una ficha bibliográfica, marcándola como inactiva en la base de datos.
    * 
    * @param id identificador único de la ficha a eliminar
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @throws SQLException si ocurre un error durante la operación
    */
    public void eliminarFicha(int id, TransactionManager txManager) throws SQLException {
        fichaDAO.eliminarLogicamente(id, txManager);
    }
    
    /**
     * Busca una ficha bibliográfica por su identificador único.
     * @param id identificador de la ficha
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return la ficha correspondiente, o null si no se encuentra
     * @throws SQLException si ocurre un error durante la consulta
     */
    public FichaBibliografica buscarPorId(int id, TransactionManager txManager) throws SQLException {
        return fichaDAO.buscarPorId(id, txManager);
    }
    
    /**
     * Lista todas las fichas bibliográficas activas (no eliminadas) registradas en el sistema.
     *
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return una lista de objetos FichaBibliografica activos
     * @throws SQLException si ocurre un error durante la consulta
     */
    public List<FichaBibliografica> listarFichas(TransactionManager txManager) throws SQLException {
        return fichaDAO.listarActivas(txManager);
    }
    
     /**
     * Lista todas las fichas bibliográficas eliminadas logicamente registradas en el sistema.
     *
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return una lista de objetos FichaBibliografica activos
     * @throws SQLException si ocurre un error durante la consulta
     */
    public List<FichaBibliografica> listarFichasEliminadas(TransactionManager txManager) throws SQLException {
        return fichaDAO.listarEliminadas(txManager);
    }
   
    /**
     * Valida los atributos de una ficha bibliográfica según reglas de negocio definidas.
     * Verifica la presencia y longitud del ISBN, y aplica restricciones de tamaño a los campos
     * de clasificación Dewey, estantería e idioma. Lanza excepciones descriptivas ante inconsistencias.
     * @param ficha objeto FichaBibliografica a validar
     * @throws IllegalArgumentException si algún atributo no cumple las reglas establecidas
     */
    private void validarFicha(FichaBibliografica ficha) {
        if (ficha.getIsbn() == null || ficha.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN es obligatorio.");
        }
        if (ficha.getIsbn().length() > 17) {
            throw new IllegalArgumentException("El ISBN excede los 17 caracteres.");
        }
        if (ficha.getClasificacionDewey() != null && ficha.getClasificacionDewey().length() > 20) {
            throw new IllegalArgumentException("La clasificación Dewey excede los 20 caracteres.");
        }
        if (ficha.getEstanteria() != null && ficha.getEstanteria().length() > 20) {
            throw new IllegalArgumentException("La estantería excede los 20 caracteres.");
        }
        if (ficha.getIdioma() != null && ficha.getIdioma().length() > 30) {
            throw new IllegalArgumentException("El idioma excede los 30 caracteres.");
        }
    }
}
