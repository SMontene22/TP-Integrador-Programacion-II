package biblioteca.DAO;

import biblioteca.Config.TransactionManager;
import java.util.List;

public interface GenericDAO<T> {
    // Esta es una interfaz genérica que define métodos comunes para trabajar con cualquier entidad.
    // Sirve como base para evitar repetir código en distintas clases DAO (como LibroDAO o FichaBibliograficaDAO).


    // Inserción sin transacción explícita (autocommit)
    void insertar(T entidad) throws Exception;

    // Inserción dentro de una transacción gestionada por TransactionManager
    void insertTx(T entidad, TransactionManager txManager) throws Exception;

    // Actualización sin transacción explícita
    void actualizar(T entidad) throws Exception;

    // Eliminación sin transacción explícita
    void eliminar(int id) throws Exception;

    // Búsqueda por ID
    T getById(int id) throws Exception;

    // Listado completo
    List<T> getAll() throws Exception;
}

