package biblioteca.DAO;

import biblioteca.Entities.Libro;
import biblioteca.Config.TransactionManager;
import biblioteca.Entities.FichaBibliografica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Libro.
 * Encapsula todas las operaciones de persistencia relacionadas con libros en la base de datos.
 *
 * Características principales:
 * - Implementa operaciones CRUD estándar sobre la entidad Libro.
 * - Utiliza PreparedStatements en todas las consultas para prevenir SQL injection.
 * - Aplica borrado lógico mediante el campo 'eliminado' (TRUE = inactivo, FALSE = activo).
 * - Gestiona la relación con FichaBibliografica de forma unidireccional (no carga fichas asociadas).
 * - Soporta transacciones mediante métodos que aceptan Connection externa (ej. insertarTx).
 *
 * Diferencias con FichaBibliograficaDAO:
 * - Puede incluir lógica adicional para validaciones editoriales o de autor.
 * - En algunos casos, puede requerir JOINs para obtener información relacionada (según implementación).
 * - La entidad Libro suele ser el punto de entrada para operaciones compuestas (ej. alta de libro + ficha).
 *
 * Patrón utilizado:
 * - DAO con manejo automático de recursos JDBC mediante try-with-resources.
 */
public class LibroDAO {
    
    /**
    * Inserta un nuevo libro en la base de datos.
    * 
    * Este método realiza la operación de persistencia de un objeto Libro en la tabla libros,
    * estableciendo el campo eliminado como false por defecto. Utiliza una sentencia preparada
    * para evitar inyecciones SQL y asegura la obtención del ID generado automáticamente por la base de datos.
    * 
    * La operación se realiza dentro del contexto de la conexión proporcionada, sin cerrar la misma.
    * En caso de error al obtener el ID generado, se lanza una SQLException.
    *
    * @param libro Objeto que contiene los datos del libro a insertar.
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return El identificador único (ID) generado para el nuevo libro.
    * @throws SQLException si ocurre un error al insertar el registro o al obtener el ID generado.
    */
    public int insertar(Libro libro, TransactionManager txManager) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, editorial, anioEdicion, eliminado) VALUES (?, ?, ?, ?, false)";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setObject(4, libro.getAnioEdicion(), Types.INTEGER);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // ID generado
                } else {
                    throw new SQLException("No se pudo obtener el ID del libro.");
                }
            }
        }
    }  
    
    /**
     * Actualiza los datos de un libro existente en la base de datos.
     * 
     * Este método modifica los campos titulo, autor, editorial y anioEdicion
     * del registro correspondiente al id del libro, siempre que el mismo no esté marcado como eliminado.
     * Utiliza una conexión obtenida internamente y una sentencia preparada para garantizar seguridad y eficiencia.
     * 
     * No realiza validaciones previas sobre la existencia del libro ni sobre posibles duplicados.
     * Se recomienda validar la existencia del libro antes de invocar este método.
     *
     * @param libro Objeto que contiene los datos actualizados del libro.
     * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
     * @throws SQLException si ocurre un error al ejecutar la actualización.
     */
    public void actualizar(Libro libro,TransactionManager txManager) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, anioEdicion = ? WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setObject(4, libro.getAnioEdicion(), Types.INTEGER);
            stmt.setInt(5, libro.getId());
            stmt.executeUpdate();
        }
    }
    
    /**
    * Realiza la eliminación lógica de un libro en la base de datos.
    * 
    * Este método actualiza el campo eliminado del registro correspondiente al id indicado,
    * marcándolo como true. La eliminación lógica permite preservar la integridad referencial y el
    * historial de datos, evitando la eliminación física del registro.
    * 
    * No se valida la existencia previa del libro ni su estado actual de eliminación.
    *
    * @param id Identificador único del libro a marcar como eliminado.
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @throws SQLException si ocurre un error al ejecutar la actualización.
    */
    public void eliminarLogicamente(int id, TransactionManager txManager) throws SQLException {
        String sql = "UPDATE libros SET eliminado = true WHERE id = ?";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
    * Busca un libro por su identificador, siempre que no esté marcado como eliminado.
    * Este método consulta la tabla libros utilizando el id proporcionado,
    * filtrando aquellos registros que no hayan sido eliminados lógicamente eliminado = false.
    * Si se encuentra un resultado, se mapea a un objeto Libro mediante el método mapearLibro.
    * La conexión se obtiene internamente y se cierra automáticamente. Si no se encuentra el libro,
    * el método retorna null.
    *
    * @param id Identificador único del libro a buscar.
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return El libro correspondiente al ID si existe y no está eliminado; {@code null} en caso contrario.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */
    public Libro buscarPorId(int id, TransactionManager txManager) throws SQLException {
        String sql = "SELECT * FROM libros WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearLibro(rs);
            }
        }
        return null;
    }

    /**
    * Recupera todos los libros activos (no eliminados) desde la base de datos, incluyendo su ficha bibliográfica asociada.
    * Este método consulta la tabla libros filtrando aquellos registros cuyo campo eliminado sea false.
    * Por cada libro encontrado, se construye un objeto Libro y se asocia su correspondiente FichaBibliografica
    * mediante el DAO especializado. La conexión se obtiene internamente y se cierra automáticamente.
    * La lista resultante contiene únicamente libros activos, con sus datos completos y ficha asociada si existe.
    * Se recomienda validar el comportamiento del método buscarPorLibroId ante fichas inexistentes.
    *
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return Lista de libros activos con sus respectivas fichas bibliográficas si están disponibles.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */
    public List<Libro> listarActivos(TransactionManager txManager) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE eliminado = false";

        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery())  {

            FichaBibliograficaDAO fichaDAO = new FichaBibliograficaDAO();

            while (rs.next()) {
                Libro libro = new Libro();
                int libroId = rs.getInt("id");

                libro.setId(libroId);
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setEditorial(rs.getString("editorial"));
                libro.setAnioEdicion(rs.getInt("anioEdicion"));
                libro.setEliminado(rs.getBoolean("eliminado"));

                // Cargar ficha asociada
                FichaBibliografica ficha = fichaDAO.buscarPorLibroId(libroId,txManager);
                libro.setFichaBibliografica(ficha);

                libros.add(libro);
            }
        }
        return libros;
    }
    
    /**
    * Recupera todos los libros marcados como eliminados en la base de datos, incluyendo su ficha bibliográfica asociada si existe.
    * Este método consulta la tabla libros filtrando aquellos registros cuyo campo eliminado sea true.
    * Por cada libro eliminado, se construye un objeto Libro y se asocia su correspondiente FichaBibliografica
    * mediante el DAO especializado. La conexión se obtiene internamente y se cierra automáticamente.
    * La lista resultante permite auditar o restaurar libros eliminados lógicamente, preservando su información y relaciones.
    *
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return Lista de libros eliminados con sus respectivas fichas bibliográficas si están disponibles.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */
    public List<Libro> listarEliminados(TransactionManager txManager) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE eliminado = true";

        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery())  {

            FichaBibliograficaDAO fichaDAO = new FichaBibliograficaDAO();

            while (rs.next()) {
                Libro libro = new Libro();
                int libroId = rs.getInt("id");

                libro.setId(libroId);
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setEditorial(rs.getString("editorial"));
                libro.setAnioEdicion(rs.getInt("anioEdicion"));
                libro.setEliminado(rs.getBoolean("eliminado"));

                // Cargar ficha asociada (si existe)
                FichaBibliografica ficha = fichaDAO.buscarPorLibroId(libroId, txManager);
                libro.setFichaBibliografica(ficha);

                libros.add(libro);
            }
        }
        return libros;
    }
    
    /**
    * Verifica si existe un libro activo en la base de datos con los mismos atributos que el proporcionado.
    * Este método realiza una consulta sobre la tabla libro, comparando los campos titulo, autor,
    * editorial y anioEdicion, y excluyendo aquellos registros marcados como eliminados eliminado = false.
    * Es útil para validar duplicados antes de realizar una inserción, asegurando la integridad lógica del catálogo.
    * La conexión se obtiene internamente y se cierra automáticamente.
    * @param libro Objeto que contiene los datos del libro a verificar.
    * @param txManager Manejador de transacciones que provee la conexión a la base de datos.
    * @return true si existe al menos un libro activo con los mismos atributos; false en caso contrario.
    * @throws SQLException si ocurre un error al acceder a la base de datos.
    */
    public boolean existeLibro(Libro libro, TransactionManager txManager) throws SQLException {
        String sql = "SELECT COUNT(*) FROM libros WHERE titulo = ? AND autor = ? AND editorial = ? AND anioEdicion = ? AND eliminado = false";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setObject(4, libro.getAnioEdicion(), Types.INTEGER);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    /**
     * Vincula una ficha bibliográfica existente a un libro, actualizando la clave foránea en la tabla fichabibliografica.
     * Este método establece la relación entre una ficha y un libro mediante la actualización del campo libro_id
     * en la ficha correspondiente. Es útil en escenarios donde la ficha se crea de forma independiente y se asocia al libro
     * en una etapa posterior del flujo de negocio.
     * La conexión se obtiene internamente y se cierra automáticamente. No se valida la existencia previa de los IDs proporcionados.
     *
     * @param fichaId el identificador de la ficha bibliográfica a vincular
     * @param libroId el identificador del libro al cual se desea asociar la ficha
     * @throws SQLException si ocurre un error durante la operación de actualización
     */
    public void vincularFicha(int fichaId, int libroId, TransactionManager txManager) throws SQLException {
        String sql = "UPDATE fichabibliografica SET libro_id = ? WHERE id = ?";
        try (PreparedStatement stmt = txManager.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, libroId);
            stmt.setInt(2, fichaId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mapea un registro del ResultSet a un objeto Libro.
     * Este método extrae los campos relevantes del resultado de una consulta SQL y construye un objeto Libro
     * con sus atributos completos. Se contempla el posible valor nulo en anioEdicion, evitando errores de conversión.
     * Es utilizado como método auxiliar en operaciones de recuperación, garantizando encapsulamiento y reutilización
     * de la lógica de mapeo.
     * @param rs el ResultSet posicionado en el registro a mapear
     * @return un objeto Libro con los datos extraídos del ResultSet
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet
     */
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getInt("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnioEdicion(rs.getObject("anioEdicion") != null ? rs.getInt("anioEdicion") : null);
        libro.setEliminado(rs.getBoolean("eliminado"));
        return libro;
    }
}
