package biblioteca.DAO;

import biblioteca.Entities.FichaBibliografica;
import biblioteca.Config.TransactionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad FichaBibliografica.
 * Encapsula todas las operaciones de persistencia relacionadas con fichas bibliográficas en la base de datos.
 *
 * Características principales:
 * - Implementa operaciones CRUD estándar sobre la entidad FichaBibliografica.
 * - Utiliza PreparedStatements en todas las consultas para prevenir SQL injection.
 * - Aplica borrado lógico mediante el campo 'eliminado' (TRUE = inactiva, FALSE = activa).
 * - Gestiona la relación unidireccional con Libro a través del campo libro_id (clave foránea).
 * - Soporta transacciones externas mediante métodos que aceptan Connection como parámetro (ej. insertarTx).
 *
 * Diferencias con LibroDAO:
 * - No carga el objeto Libro completo; solo utiliza su ID como referencia.
 * - No realiza JOINs ni consultas compuestas; se enfoca en persistencia directa.
 * - Todas las consultas filtran por 'eliminado = FALSE' para excluir registros inactivos.
 *
 * Patrón utilizado:
 * - DAO con manejo automático de recursos JDBC mediante try-with-resources.
 */

public class FichaBibliograficaDAO {
    
    /**
    * Inserta una nueva ficha bibliográfica en la base de datos utilizando una transacción controlada.
    * 
    * Este método registra una ficha con sus atributos principales (ISBN, clasificación Dewey, estantería, idioma y libro asociado),
    * marcándola como activa (eliminado = false). Utiliza TransactionManager para asegurar la consistencia de la operación.
    * 
    * Si la inserción es exitosa, retorna el ID generado automáticamente por la base de datos. En caso contrario,
    * lanza una excepción indicando el motivo del fallo.
    * 
    * @param ficha objeto FichaBibliografica con los datos a registrar. Debe estar correctamente poblado.
    * @param txManager gestor de transacciones activo, utilizado para ejecutar la operación dentro de una transacción.
    * 
    * @return el ID generado para la ficha insertada.
    * 
    * @throws SQLException si ocurre un error durante la inserción o no se genera un ID válido.
    */
    public int insertar(FichaBibliografica ficha, TransactionManager txManager) throws SQLException {
        String sql = "INSERT INTO fichabibliografica (isbn, clasificacionDewey, estanteria, idioma, libro_id, eliminado) VALUES (?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, ficha.getIsbn());
            stmt.setString(2, ficha.getClasificacionDewey());
            stmt.setString(3, ficha.getEstanteria());
            stmt.setString(4, ficha.getIdioma());
            stmt.setInt(5, ficha.getLibroId());

            int filas = stmt.executeUpdate();
            if (filas == 0) throw new SQLException("No se pudo insertar la ficha.");

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // ID generado
                } else {
                    throw new SQLException("No se generó ID para la ficha.");
                }
            }
        }
    }

    /**
    * Actualiza los datos de una ficha bibliográfica activa en la base de datos utilizando una transacción controlada.
    * 
    * Este método modifica los atributos principales de una ficha existente (ISBN, clasificación Dewey, estantería e idioma).
    * No permite modificar el estado lógico de eliminación ('eliminado'); dicho campo solo se gestiona mediante el borrado lógico.
    * Utiliza TransactionManager para garantizar la consistencia de la operación dentro de una transacción.
    * 
    * No realiza validaciones adicionales sobre la existencia previa de la ficha; se espera que el objeto esté correctamente poblado.
    * 
    * @param ficha objeto FichaBibliografica con los datos actualizados. Debe contener un ID válido.
    * @param txManager gestor de transacciones activo, utilizado para ejecutar la operación dentro de una transacción.
    * 
    * @throws SQLException si ocurre un error durante la actualización en la base de datos.
    */
    public void actualizar(FichaBibliografica ficha,TransactionManager txManager) throws SQLException {
        String sql = "UPDATE fichaBibliografica SET isbn = ?, clasificacionDewey = ?, estanteria = ?, idioma = ? WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setString(1, ficha.getIsbn());
            stmt.setString(2, ficha.getClasificacionDewey());
            stmt.setString(3, ficha.getEstanteria());
            stmt.setString(4, ficha.getIdioma());
            stmt.setInt(5, ficha.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Marca lógicamente como eliminada una ficha bibliográfica en la base de datos.
     * 
     * Este método actualiza el campo eliminado de la ficha correspondiente al ID proporcionado,
     * estableciéndolo en true. No elimina físicamente el registro, sino que lo inactiva para
     * que no sea considerado en operaciones normales del sistema.
     * 
     * La operación se ejecuta dentro de una transacción controlada mediante TransactionManager,
     * lo que garantiza la consistencia del cambio.
     * 
     * @param id identificador único de la ficha a marcar como eliminada.
     * @param txManager gestor de transacciones activo, utilizado para ejecutar la operación de forma segura.
     * 
     * @throws SQLException si ocurre un error durante la actualización en la base de datos.
     */
    public void eliminarLogicamente(int id, TransactionManager txManager) throws SQLException {
        String sql = "UPDATE fichaBibliografica SET eliminado = true WHERE id = ?";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Busca una ficha bibliográfica activa por su identificador único.
     * Solo se consideran aquellas cuyo campo 'eliminado' sea FALSE, es decir, no marcadas como eliminadas.
     *
     * @param id Identificador de la ficha bibliográfica a buscar.
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return La ficha bibliográfica correspondiente al ID si existe y no está eliminada; {@code null} en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public FichaBibliografica buscarPorId(int id, TransactionManager txManager) throws SQLException {
        String sql = "SELECT * FROM fichaBibliografica WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearFicha(rs);
            }
        }
        return null;
    }

    /**
     * Recupera todas las fichas bibliográficas activas desde la base de datos.
     * Solo incluye aquellas cuyo campo 'eliminado' sea FALSE, es decir, no marcadas como eliminadas.
     *
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return Lista de fichas bibliográficas activas.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public List<FichaBibliografica> listarActivas(TransactionManager txManager) throws SQLException {
        List<FichaBibliografica> fichas = new ArrayList<>();
        String sql = "SELECT * FROM fichaBibliografica WHERE eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fichas.add(mapearFicha(rs));
            }
        }
        return fichas;
    }
    
    /**
    * Query para obtener ficha por LibroID.
    * Solo retorna fichas activas (eliminado=FALSE).
    * Características:
    * - Busca en la tabla fichabibliografica por libro_id
    * - Aplica filtro por eliminado = FALSE (baja lógica)
    * - No realiza JOINs ni carga el objeto Libro (solo usa el ID como referencia)
    * - Reconstruye el objeto usando setters individuales
    *
    * Mapeo de columnas:
    * - id → id
    * - isbn → isbn
    * - clasificacionDewey → clasificacionDewey
    * - estanteria → estanteria
    * - idioma → idioma
    * - libro_id → libroId (clave foránea)
    * - eliminado → eliminado
    *
    * @param libroId Identificador del libro asociado a la ficha bibliográfica.
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return La ficha bibliográfica correspondiente al libro si existe y no está eliminada; {@code null} en caso contrario.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */

    public FichaBibliografica buscarPorLibroId(int libroId, TransactionManager txManager) throws SQLException {
        String sql = "SELECT * FROM fichabibliografica WHERE libro_id = ? AND eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, libroId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    FichaBibliografica ficha = new FichaBibliografica();
                    ficha.setId(rs.getInt("id"));
                    ficha.setIsbn(rs.getString("isbn"));
                    ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
                    ficha.setEstanteria(rs.getString("estanteria"));
                    ficha.setIdioma(rs.getString("idioma"));
                    ficha.setLibroId(libroId);
                    ficha.setEliminado(rs.getBoolean("eliminado"));
                    return ficha;
                }
            }
        }
        return null;
    }

    /**
     * Verifica si existe una ficha bibliográfica activa con el ISBN especificado.
     * Solo se consideran aquellas cuyo campo 'eliminado' sea FALSE, es decir, no marcadas como eliminadas.
     *
     * @param isbn Código ISBN del libro asociado a la ficha bibliográfica.
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @return {@code true} si existe al menos una ficha activa con el ISBN indicado; {@code false} en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public boolean existeFichaPorIsbn(String isbn, TransactionManager txManager) throws SQLException {
        String sql = "SELECT COUNT(*) FROM fichabibliografica WHERE isbn = ? AND eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
    * Recupera todas las fichas bibliográficas marcadas como eliminadas desde la base de datos.
    * Solo incluye aquellas cuyo campo 'eliminado' sea TRUE, es decir, registradas como inactivas o descartadas.
    *
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return Lista de fichas bibliográficas eliminadas.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */

    public List<FichaBibliografica> listarEliminadas(TransactionManager txManager) throws SQLException {
        List<FichaBibliografica> fichas = new ArrayList<>();
        String sql = "SELECT * FROM fichabibliografica WHERE eliminado = true";

        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fichas.add(mapearFicha(rs));
            }
        }
        return fichas;
    }
    
    /**
    * Mapea un ResultSet a un objeto FichaBibliografica.
    * Reconstruye el objeto utilizando setters individuales.
    *
    * Mapeo de columnas:
    * - id → id
    * - isbn → isbn
    * - clasificacionDewey → clasificacionDewey
    * - estanteria → estanteria
    * - idioma → idioma
    * - eliminado → eliminado
    * - libro_id → libroId (clave foránea que vincula con Libro)
    *
    * Nota: A diferencia de otros mapeos simples, aquí se incluye el campo eliminado
    * porque puede ser necesario para operaciones internas o listados administrativos.
    *
    * @param rs ResultSet posicionado en una fila con datos de ficha bibliográfica
    * @return FichaBibliografica reconstruida
    * @throws SQLException Si hay error al leer columnas del ResultSet
    */
    private FichaBibliografica mapearFicha(ResultSet rs) throws SQLException {
        FichaBibliografica ficha = new FichaBibliografica();
        ficha.setId(rs.getInt("id"));
        ficha.setIsbn(rs.getString("isbn"));
        ficha.setClasificacionDewey(rs.getString("clasificacionDewey"));
        ficha.setEstanteria(rs.getString("estanteria"));
        ficha.setIdioma(rs.getString("idioma"));
        ficha.setEliminado(rs.getBoolean("eliminado"));
        ficha.setLibroId(rs.getInt("libro_id"));
        return ficha;
    }
}
