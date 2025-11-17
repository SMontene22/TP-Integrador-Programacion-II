package biblioteca.Config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar transacciones JDBC de forma controlada y segura.
 * 
 * TransactionManager encapsula una conexión a base de datos y permite iniciar, confirmar (commit)
 * o revertir (rollback) transacciones de manera explícita. Implementa AutoCloseable para facilitar
 * su uso en bloques try-with-resources, asegurando el cierre adecuado de la conexión y la reversión
 * automática en caso de errores.
 * 
 * Este gestor valida el estado de la conexión antes de iniciar o confirmar una transacción,
 * y mantiene un indicador interno para saber si hay una transacción activa.
 * 
 * Uso recomendado:
 * <pre>
 * try (TransactionManager tx = new TransactionManager(conn)) {
 *     tx.startTransaction();
 *     // operaciones JDBC
 *     tx.commit();
 * }
 * </pre>
 * 
 * En caso de no llamar a commit, la transacción se revierte automáticamente al cerrar.
 */

public class TransactionManager implements AutoCloseable {
    private Connection conn;
    private boolean transactionActive;

    /**
    * Crea una instancia de TransactionManager con la conexión proporcionada.
    * 
    * @param conn conexión JDBC válida y abierta.
    * @throws SQLException si la conexión es null.
     */

    public TransactionManager(Connection conn) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("La conexión no puede ser null");
        }
        this.conn = conn;
        this.transactionActive = false;
    }

    public Connection getConnection() {
        return conn;
    }
    
    /**
    * Inicia una transacción desactivando el modo de auto-commit.
    * 
    * @throws SQLException si la conexión es nula o está cerrada.
    */
    public void startTransaction() throws SQLException {
        if (conn == null) {
            throw new SQLException("No se puede iniciar la transacción: conexión no disponible");
        }
        if (conn.isClosed()) {
            throw new SQLException("No se puede iniciar la transacción: conexión cerrada");
        }
        conn.setAutoCommit(false);
        transactionActive = true;
    }
    
    /**
    * Confirma la transacción activa y restablece el estado interno.
    * 
    * @throws SQLException si no hay conexión o no hay transacción activa.
    */
    public void commit() throws SQLException {
        if (conn == null) {
            throw new SQLException("Error al hacer commit: no hay conexión establecida");
        }
        if (!transactionActive) {
            throw new SQLException("No hay una transacción activa para hacer commit");
        }
        conn.commit();
        transactionActive = false;
    }
    
    /**
     * Revierte la transacción activa si existe. No lanza excepción, pero informa errores por consola.
     */
    public void rollback() {
        if (conn != null && transactionActive) {
            try {
                conn.rollback();
                transactionActive = false;
            } catch (SQLException e) {
                System.err.println("Error durante el rollback: " + e.getMessage());
            }
        }
    }

    /**
    * Cierra la conexión y revierte la transacción si aún está activa.
    * Restablece el modo de auto-commit antes de cerrar.
    */
    @Override
    public void close() {
        if (conn != null) {
            try {
                if (transactionActive) {
                    rollback();
                }
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
    * Indica si hay una transacción activa en curso.
    * 
    * @return true si la transacción está activa, false en caso contrario.
    */
    public boolean isTransactionActive() {
        return transactionActive;
    }
}
