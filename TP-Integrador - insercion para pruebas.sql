USE biblioteca;

-- Insertar 4 libros para pruebas. Utilizar luego del intento de dos libros.
INSERT INTO libros (titulo, autor, editorial, anioEdicion)
VALUES 
('Cien años de soledad', 'Gabriel García Márquez', 'Sudamericana', 1967),
('El nombre de la rosa', 'Umberto Eco', 'Lumen', 1980),
('Rayuela', 'Julio Cortázar', 'Sudamericana', 1963),
('1984', 'George Orwell', 'Secker & Warburg', 1949);

-- Insertar 4 fichas bibliográficas asociadas a los libros anteriores 
INSERT INTO fichabibliografica (isbn, clasificacionDewey, estanteria, idioma, libro_id)
VALUES 
('978-84-376-0494-7', '863.64', 'A1', 'Español', 3),
('978-84-264-1746-1', '853.914', 'B2', 'Italiano', 4),
('978-84-376-0493-0', '863.64', 'A3', 'Español', 5),
('978-0-452-28423-4', '823.912', 'C1', 'Inglés', 6);