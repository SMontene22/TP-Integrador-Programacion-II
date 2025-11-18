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
