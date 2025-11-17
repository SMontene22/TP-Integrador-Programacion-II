package biblioteca.Main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import biblioteca.Config.DatabaseConnection;
/**
 * Clase de utilidad para probar la conexión con la base de datos.
 * Establece una conexión utilizando la configuración definida en DatabaseConnection
 * y muestra información relevante del entorno de conexión, como el usuario, la base de datos,
 * la URL y el driver JDBC utilizado.
 * Útil para validar la configuración antes de ejecutar operaciones críticas o desplegar la aplicación.
 */

public class TestConexion {
    /**
    * Punto de entrada para ejecutar la prueba de conexión a la base de datos.
    * Intenta establecer una conexión y, si es exitosa, imprime metadatos relevantes del entorno JDBC.
    * En caso de error, muestra el mensaje y el stack trace para facilitar el diagnóstico.
    *
    * @param args argumentos de línea de comandos 
    */

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Conexion exitosa a la base de datos");

                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("Usuario conectado: " + metaData.getUserName());
                System.out.println("Base de datos: " + conn.getCatalog());
                System.out.println("URL: " + metaData.getURL());
                System.out.println("Driver: " + metaData.getDriverName() + " v" + metaData.getDriverVersion());
            } else {
                System.out.println("No se pudo establecer la conexion.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}