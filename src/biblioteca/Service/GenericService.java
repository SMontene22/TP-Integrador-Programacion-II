package biblioteca.Service;

import java.util.List;
/**
 * Interfaz genérica que define operaciones CRUD básicas para entidades del sistema.
 * Permite estandarizar el acceso a datos y la lógica de negocio sobre cualquier tipo de entidad,
 * facilitando la reutilización y la implementación de servicios específicos.
 *
 * @param <T> el tipo de entidad sobre el cual se aplican las operaciones
 */

public interface GenericService<T> {
    
    /**
    * Inserta una nueva entidad en el sistema.
    * @param entidad la entidad a persistir
    * @throws Exception si ocurre un error durante la operación
    */
    void insertar(T entidad) throws Exception;
    
    /**
    * Actualiza los datos de una entidad existente.
    * @param entidad la entidad con los datos actualizados
    * @throws Exception si ocurre un error durante la operación
    */
    
    void actualizar(T entidad) throws Exception;
    
    /**
    * Elimina una entidad del sistema, según su identificador.
    * @param id el identificador único de la entidad a eliminar
    * @throws Exception si ocurre un error durante la operación
    */
    void eliminar(int id) throws Exception;
    
    /**
    * Recupera una entidad por su identificador único.
    * @param id el identificador de la entidad
    * @return la entidad correspondiente, o null si no se encuentra
    * @throws Exception si ocurre un error durante la operación
    */
    T getById(int id) throws Exception;
    /**
    * Lista todas las entidades disponibles en el sistema.
    * @return una lista de entidades
    * @throws Exception si ocurre un error durante la operación
    */
    List<T> getAll() throws Exception;
}
