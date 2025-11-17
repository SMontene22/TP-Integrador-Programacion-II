package biblioteca.Entities;

import java.util.Objects;
/**
 * Representa un libro dentro del sistema de biblioteca.
 * Contiene los atributos principales del libro como título, autor, editorial y año de edición.
 * Hereda de Base, incorporando el identificador único y el estado de eliminación lógica.
 * Puede estar vinculado a una FichaBibliografica, que complementa su información técnica y de catalogación.
 */

public class Libro extends Base {

    private String titulo;
    private String autor;
    private String editorial;
    private Integer anioEdicion;

    private FichaBibliografica fichaBibliografica;
    
    /**
     * Constructor completo que inicializa un libro con todos sus atributos principales.
     *
     * @param id identificador único del libro
     * @param titulo título del libro
     * @param autor autor del libro
     * @param editorial editorial responsable de la publicación
     * @param anioEdicion año de edición del libro
     */
    public Libro(int id, String titulo, String autor, String editorial, Integer anioEdicion) {
        super(id, false);
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioEdicion = anioEdicion;
    }

    /**
    * Constructor por defecto que inicializa un libro vacío con estado no eliminado.
    */
    public Libro() {
        super();
    }

    /**
    * Retorna el Titulo del libro.
    * @return el titulo como cadena.
    */
    public String getTitulo() {
        return titulo;
    }

    /**
    * Establece el titulo del Libro.
    * @param titulo el nuevo título del libro
    */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    /**
    * Retorna el autor del libro.
    * @return el autor como cadena.
    */
    public String getAutor() {
        return autor;
    }

    /**
    * Establece el autor del Libro.
    * @param autor el nuevo autor del libro
    */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
    * Retorna la editorial del libro.
    * @return la editorial como cadena.
    */
    public String getEditorial() {
        return editorial;
    }

    /**
    * Establece la editorial del Libro.
    * @param editorial nueva editorial del libro
    */
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    /**
    * Retorna el año de edición del libro.
    * @return el año como entero.
    */
    public Integer getAnioEdicion() {
        return anioEdicion;
    }

    /**
    * Establece el año de edición del Libro.
    * @param anioEdicion nuevo año de edición del libro
    */
    public void setAnioEdicion(Integer anioEdicion) {
        this.anioEdicion = anioEdicion;
    }

    /**
    * Obtiene la ficha bibliográfica asociada al libro.
    * Este método permite acceder a la información complementaria del libro, como ISBN, clasificación Dewey,
    * ubicación y idioma, encapsulada en el objeto FichaBibliografica.
    *
    * @return la ficha bibliográfica vinculada al libro, o null si no está asociada
    */
    public FichaBibliografica getFichaBibliografica() {
        return fichaBibliografica;
    }

    /**
    * Asocia una ficha bibliográfica al libro actual.
    * Este método establece la relación entre el libro y su ficha técnica, permitiendo enriquecer
    * la representación del libro con datos catalográficos. No valida la consistencia entre IDs.
    *
    * @param fichaBibliografica el objeto FichaBibliografica a vincular al libro
    */
   public void setFichaBibliografica(FichaBibliografica fichaBibliografica) {
        this.fichaBibliografica = fichaBibliografica;
    }

    /**
    * Devuelve una representación textual del objeto Libro.
    * Incluye los campos principales y, si corresponde, el estado eliminado ya que 
    * en el listado de activos es redundante mostrar el campo eliminado = "false".
    *
    * @return Cadena con los datos del libro formateados.
    */

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Libro{id=").append(getId())
          .append(", titulo='").append(titulo).append('\'')
          .append(", autor='").append(autor).append('\'')
          .append(", editorial='").append(editorial).append('\'')
          .append(", anioEdicion=").append(anioEdicion);
        
        if (fichaBibliografica != null) {
            sb.append(" ====> ").append(fichaBibliografica);
        }


        if (isEliminado()) {
            sb.append(", eliminado=true");
        }

        sb.append('}');
        return sb.toString();
    }

    /**
     * Compara este libro con otro, considerando título, autor, editorial y año de edición.
     * @param o el objeto a comparar
     * @return true si ambos libros tienen los mismos atributos principales; false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Libro)) return false;
    Libro libro = (Libro) o;
    return Objects.equals(titulo, libro.titulo) &&
           Objects.equals(autor, libro.autor) &&
           Objects.equals(editorial, libro.editorial) &&
           Objects.equals(anioEdicion, libro.anioEdicion);
    }

    /**
    * Calcula el código hash del libro, basado en sus atributos principales.
    *
    * @return el valor hash correspondiente
    */
    @Override
    public int hashCode() {
    return Objects.hash(titulo, autor, editorial, anioEdicion);
    }

}

