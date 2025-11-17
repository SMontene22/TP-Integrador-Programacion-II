package biblioteca.Main;
/**
 * Clase utilitaria encargada de mostrar el menú principal de la aplicación de biblioteca.
 * Centraliza la lógica de presentación en consola, separando la interfaz visual del flujo de control.
 * Facilita la reutilización y el mantenimiento del menú sin afectar la lógica de ejecución.
 */

public class MenuDisplay {
    /**
    * Muestra en consola el menú principal de opciones disponibles para el usuario.
    * Incluye acciones relacionadas con libros y fichas bibliográficas, tanto activas como eliminadas.
    * Se espera que el usuario ingrese un número correspondiente a la acción deseada.
    */

    public static void mostrarMenuPrincipal() {
        // Encabezado visual del menú
        System.out.println("\n========= MENÚ BIBLIOTECA =========");
        
        // Opciones de gestión de libros
        System.out.println("1. Crear libro con ficha bibliográfica");
        System.out.println("2. Listar libros activos");
        System.out.println("3. Actualizar libro");
        System.out.println("4. Eliminar libro");
        System.out.println("5. Listar libros eliminados");
        
        // Opciones de gestión de fichas bibliográficas
        System.out.println("6. Listar fichas bibliográficas");
        System.out.println("7. Actualizar ficha por ID");
        System.out.println("8. Eliminar ficha por ID");
        System.out.println("9. Listar fichas bibliograficas eliminadas");
                
        // Herramientas administrativas
        System.out.println("10. Herramientas administrativas (Administrador)");
        
        // Opción de salida
        System.out.println("0. Salir");
        
        // Solicitud de entrada al usuario
        System.out.print("Ingrese una opción: ");
    }
}
