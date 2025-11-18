# Sistema de Gestión de Libros y Fichas Bibliográficas

## Trabajo Práctico Integrador - Programación 2

### Descripción del Proyecto

Este Trabajo Práctico Integrador tiene como objetivo aplicar los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos. El sistema permite gestionar libros y sus fichas bibliográficas, implementando una arquitectura profesional con operaciones CRUD, validaciones, transacciones y eliminación lógica.


### Objetivos Académicos

Este proyecto permite demostrar competencias en:

1. Arquitectura en Capas
- Separación en 4 capas: Presentación, Servicio, DAO y Modelo
- Modularidad y bajo acoplamiento entre componentes

2. Programación Orientada a Objetos
- Aplicación de principios SOLID
- Encapsulamiento, herencia y polimorfismo
- Uso de clases DAO y servicios especializados

3. Persistencia con JDBC
- Conexión a MySQL mediante JDBC
- Uso de PreparedStatements
- Manejo de claves autogeneradas
- Transacciones con commit y rollback

4. Manejo de Recursos y Excepciones
- Uso de try-with-resources
- Implementación de AutoCloseable en TransactionManager
- Validaciones en múltiples capas

5. Patrones de Diseño
- DAO Pattern
- Service Layer Pattern
- Factory Pattern (DatabaseConnection)
- Soft Delete Pattern
- Dependency Injection manual

6. Validación de Integridad
- Validación de unicidad (libros y fichas)
- Validación de campos obligatorios
- Integridad referencial entre libros y ficha

### Funcionalidades Implementadas

El sistema permite gestionar dos entidades principales con las siguientes operaciones:

## Características Principales

- Crear, listar, actualizar y eliminar libros
- Listar, actualizar y eliminar fichas bibliográficas
- Validación de unicidad de libros (por título, autor, editorial y año)
- Validación de ISBN único
- Eliminación lógica (soft delete)
- Transacciones atómicas para alta de libro + ficha
- Vinculación automática entre libro y ficha
- Listado separado de registros eliminados

## Modelo de datos
```
┌───────────────┐           ┌───────────────────────────┐
│   libros      │◄──────────│ fichabibliografica        │
├───────────────┤           ├───────────────────────────┤
│ id (PK)       │           │ id (PK)                   │
│ titulo        │           │ isbn (UNIQUE)             │
│ autor         │           │ clasificacionDewey        │
│ editorial     │           │ estanteria                │
│ anioEdicion   │           │ idioma                    │
│ eliminado     │           │ libro_id (FK → libros_id) │
└───────────────┘           │ eliminado                 │
                      	    └───────────────────────────┘
```
## Arquitectura del Sistema
### Estructura en Capas
```


┌──────────────────────────────┐
│        Main / UI Layer       │
│  (Interacción con usuario)   │
│  MenuHandler, Main           │
└───────────┬──────────────────┘
            │
┌───────────▼──────────────────┐
│        Service Layer         │
│  (Lógica de negocio)         │
│  LibroService, FichaService  │
└────────────┬─────────────────┘
             │
┌────────────▼─────────────────┐
│         DAO Layer            │
│  (Acceso a datos)            │
│  LibroDAO, FichaDAO          │
└────────────┬─────────────────┘
             │
┌────────────▼─────────────────┐
│        Model Layer           │
│  (Entidades del dominio)     │
│  Libro, FichaBibliografica   │
└──────────────────────────────┘
```

## Requisitos del Sistema

| Componente         | Versión Requerida                                   |
|--------------------|-----------------------------------------------------|
| Java JDK           | 17 o superior                                       |
| MySQL              | 8.0 o superior                                      |
| JDBC Driver        | mysql-connector-j-9.5.0                             |
| Gradle             | 8.12 (incluido wrapper)                             |
| IDE sugerido       | NetBeans (alternativamente IntelliJ IDEA o Eclipse) |
| Sistema Operativo  | Windows, Linux o macOS                              |

## Instalación

### 1. Configurar Base de Datos

Ejecutar el siguiente script SQL en MySQL:
```
CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

CREATE TABLE libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(120) NOT NULL,
    editorial VARCHAR(100),
    anioEdicion INT,
    eliminado BOOLEAN DEFAULT FALSE
);

CREATE TABLE fichabibliografica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(17) NOT NULL,
    clasificacionDewey VARCHAR(20),
    estanteria VARCHAR(20),
    idioma VARCHAR(30),
    libro_id INT,
    eliminado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (libro_id) REFERENCES libros(id)
);
```

### 2. Compilar el Proyecto
```
# Linux/macOS
./gradlew clean build

# Windows
gradlew.bat clean build
```
## 3. Ejecutar desde IDE o Consola

- Desde IDE: Ejecutar clase Main
- Desde consola:
```  
java -cp "build/classes/java/main:<ruta-mysql-connector>" biblioteca.Main.Main
```

### 4. Configurar Conexión (Opcional)

Por defecto conecta a:
- **Host**: localhost:3306
- **Base de datos**: biblioteca
- **Usuario**: root
- **Contraseña**: (vacía)

Para cambiar la configuración, usar propiedades del sistema:
```
bash
java -Ddb.url=jdbc:mysql://localhost:3306/biblioteca \
     -Ddb.user=usuario \
     -Ddb.password=clave \
     -cp ...
```
### Componentes Principales

**Config/**
- DatabaseConnection.java: Gestión de conexiones JDBC con validación en inicialización estática
- TransactionManager.java: Recibe una conexión JDBC, inicia una transacción, y permite confirmarla con commit o reverterla con rollback. Implenta AutoCloseable

**Entities/**
- Base.java: Clase abstracta con campos id y eliminado
- FichaBibliografica.java: Entidad Ficha Bibliografica (ISBN, Clasificación Dewey, estantería, idioma)
- Libro.java: Entidad Libro (titulo, autor, editorial, año de edición)

**Dao/**
- GenericDAO<T>: Interface genérica con operaciones CRUD
- FichaBliograficaDAO:Implementa operaciones CRUD estándar sobre la entidad FichaBibliografica
- LibroDAO:Implementa operaciones CRUD estándar sobre la entidad Libro.

**Service/**
- GenericService<T>: Interface genérica para servicios
- FichaBliograficaService: Servicio que encapsula la lógica de negocio relacionada con fichas bibliográficas.
- LibroService: Servicio que encapsula la lógica de negocio relacionada con libros.

**Main/**
- Main.java: Punto de entrada
- AppMenu.java: Orquestador del ciclo de menú
- MenuHandler.java: Implementación de operaciones CRUD con captura de entrada
- MenuDisplay.java: Lógica de visualización de menús
- TestConexion.java: Utilidad para verificar conexión a BD

## Validaciones y Reglas de Negocio

- Título y autor obligatorios (máx. 150 y 120 caracteres)
- ISBN obligatorio y único (máx. 17 caracteres)
- Validación de campos nulos y longitudes
- Validación de año de edición (debe ser positivo)
- Validación de duplicados antes de insertar
- Validación de integridad referencial (libro debe existir antes de asociar ficha)

## Eliminación Lógica

- eliminado = true en lugar de DELETE físico
- Métodos listarActivos() y listarEliminados() separados
- Permite restauración futura y evita pérdida de datos

## Herramientas administrativas

- se decidió crear un método interno para corregir o reasignar relaciones entre fichas y libros en caso de ser necesario.
En la producción la asignación del libro_id en la ficha bibliográfica se hace automáticamente al crear el libro.
  


## Patrones y Buenas Prácticas

- DAO Pattern: LibroDAO, FichaBibliograficaDAO
- Service Layer: LibroService, FichaBibliograficaService
- Factory: DatabaseConnection para obtener conexiones
- Transaction Manager: Encapsula la gestión de transacciones JDBC, asegurando rollback automático y cierre seguro mediante try-with-resources
- PreparedStatements: protección contra SQL Injection
- Validaciones en capa de servicio, no en DAO

## Uso del Sistema

### Menú Principal

```
========= MENÚ BIBLIOTECA =========
1. Crear libro con ficha bibliográfica
2. Listar libros activos
3. Actualizar libro
4. Eliminar libro
5. Listar libros eliminados
6. Listar fichas bibliográficas
7. Actualizar ficha por ID
8. Eliminar ficha por ID
9. Listar fichas bibliográficas eliminadas
10. Herramientas administrativas (Administrador)
0. Salir
```
### Operaciones Disponibles

#### 1. Crear libro con ficha bibliográfica.
- Captura titulo, autor, editorial, año de edición.
- Si los datos del libro son correctos captura datos de ficha, ISBN, clasificación Dewey, estantería e idioma.  
- Si los datos de la ficha son correcto, actualiza las tablas.
- Valida ISBN único (no permite duplicados)

#### 2. Listar activos
- Listar todos los libros activos con su ficha bibliográfica

#### 3. Actualizar libro
- Permite modificar titulo, autor, editorial y año de edición
- Muestra el valor actual, si se presiona Enter no lo modifica.

#### 4. Eliminar Libro
- Eliminación lógica (marca como eliminado, no borra físicamente)
- Requiere ID del libro

#### 5. Listar libros eliminado
- Lista los libros que han sido marcados como eliminados.

#### 6. Listar Fichas bibliográficas.
- Muestra todas las fichas bibliográficas activas.

#### 7. Actualizar fichas por ID
- Actualiza los datos de la fichas.
- Muestra los datos cargados, si se presiona Enter no se modifica.
- Requiere ID de la ficha.

#### 8. Eliminar ficha por ID
- Eliminación lógica (marca como eliminado, no borra físicamente)
- Requiere ID de la ficha

#### 9. Listar fichas bibliográficas eliminadas
- Lista las fichas que han sido marcados como eliminados.

#### 10. Herramientas administrativas (Administrador)
- Vincula una ficha bibliográfica existente a un libro existente.
- Esta operación administrativa permite corregir o reasignar relaciones entre fichas y libros
  sin necesidad de eliminar o recrear registros.

#### 0. Salir
- Sale del sistema.

## Limitaciones Conocidas

- No hay interfaz gráfica (solo consola)
- No se permite más de una ficha por libro
- No hay paginación en listados
- No se implementó recuperación de registros eliminados
- No hay tests automatizados

## Documentación Técnica

- **README.md**: Guía de instalación, ejecución y estructura general del proyecto.
- **TransactionManager.java**: Gestión segura y controlada de transacciones JDBC mediante `try-with-resources`.
- **DatabaseConnection.java**: Clase factory para obtener conexiones JDBC reutilizables.
- **LibroService.java**: Coordinación de lógica de negocio, validaciones y control transaccional para libros.
- **FichaBibliograficaService.java**: Lógica de negocio y validaciones para fichas bibliográficas.
- **LibroDAO.java**: Acceso a datos de libros (CRUD, consultas, eliminación lógica).
- **FichaBibliograficaDAO.java**: Acceso a datos de fichas (CRUD, búsqueda por ISBN o libro).
- **MenuHandler.java**: Interfaz de usuario por consola, orquesta flujos de interacción.


### Links
video: https://youtu.be/_0qjnT92jyY

UML: https://i.postimg.cc/zvvYdbYf/TP-Integrador-Final.png




## Créditos y Contexto
- Materia: Programación 2
- Trabajo Práctico Integrador
- Institución: Universidad Tecnológica Nacional
- Carrera: Técnico Universitario en Programación. 
- Autores: 	Sergio Montenegro - Comisión 17
		Esteban Daniel Miguel - Comisión 17
		Bruno Morales - Comisión 17
		Santiago Julian Pace - Comisión 18
- Año: 2025
