package biblioteca.Entities;

import java.util.Objects;

/**
 * Representa una ficha bibliográfica asociada a un libro dentro del sistema de biblioteca.
 * Contiene información complementaria como ISBN, clasificación Dewey, ubicación en estantería e idioma.
 * Hereda de Base, incorporando el identificador único y el estado de eliminación lógica.
 * La relación con el libro se establece mediante el campo libroId, que actúa como clave foránea.
 */


public class FichaBibliografica extends Base {

    private String isbn;
    private String clasificacionDewey;
    private String estanteria;
    private String idioma;
    private int libroId;
    
    /**
    * Constructor completo que inicializa una ficha bibliográfica con todos sus atributos, excepto el vínculo al libro.
    *
    * @param id identificador único de la ficha
    * @param isbn código ISBN del libro
    * @param clasificacionDewey clasificación Dewey utilizada para catalogación
    * @param estanteria ubicación física del libro en la biblioteca
    * @param idioma idioma principal del libro
    */

    public FichaBibliografica(int id, String isbn, String clasificacionDewey, String estanteria, String idioma) {
        super(id, false);
        this.isbn = isbn;
        this.clasificacionDewey = clasificacionDewey;
        this.estanteria = estanteria;
        this.idioma = idioma;
    }

    
    /**
     * Constructor por defecto que inicializa una ficha vacía con valores nulos y estado no eliminado.
     */
    public FichaBibliografica() {
        super();
    }

    /**
    * Retorna el código ISBN de la ficha.
    * @return el ISBN como cadena
    */
    public String getIsbn() {
        return isbn;
    }
    
    /**
    * Establece el código ISBN de la ficha.
    * @param isbn el nuevo valor de ISBN
    */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    /**
     * Retorna la clasificación Dewey de la ficha.
     * @return la clasificación como cadena
     */
    public String getClasificacionDewey() {
        return clasificacionDewey;
    }
    
    /**
    * Establece la clasificación Dewey de la ficha.
    * @param clasificacionDewey el nuevo valor de clasificación
    */
    public void setClasificacionDewey(String clasificacionDewey) {
        this.clasificacionDewey = clasificacionDewey;
    }

    /**
    * Retorna la ubicación en estantería.
    * @return el nombre o código de estantería
    */
    public String getEstanteria() {
        return estanteria;
    }

    /**
    * Establece la ubicación en estantería.
    * @param estanteria el nuevo valor de estantería
    */
    public void setEstanteria(String estanteria) {
        this.estanteria = estanteria;
    }
    
    /**
     * Retorna el idioma del libro asociado.
     * @return el idioma como cadena
     */
    public String getIdioma() {
        return idioma;
    }
    
    /**
    * Establece el idioma del libro asociado.
    * @param idioma el nuevo valor de idioma
    */
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
    /**
     * Establece el identificador del libro asociado a esta ficha.
     * @param libroId el ID del libro
     */    
    public void setLibroId(int libroId) {
        this.libroId = libroId;
    }

    /**
    * Retorna el identificador del libro asociado.
    * @return el ID del libro vinculado
    */
    public int getLibroId() {
        return libroId;
    }

    /**
     * Retorna una representación textual de la ficha bibliográfica, útil para depuración o visualización.
     * @return una cadena con los atributos principales de la ficha
     */
    @Override
    public String toString() {
        return "FichaBibliografica{" +
                "id=" + getId() +
                ", isbn='" + isbn + '\'' +
                ", clasificacionDewey='" + clasificacionDewey + '\'' +
                ", estanteria='" + estanteria + '\'' +
                ", idioma='" + idioma + '\'' +
                ", eliminado=" + isEliminado() +
                '}';
    }

    /**
    * Compara esta ficha con otra, considerando únicamente el campo isbn.
    * @param o el objeto a comparar
    * @return true si ambos objetos tienen el mismo ISBN; false en caso contrario
    */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FichaBibliografica)) return false;
        FichaBibliografica that = (FichaBibliografica) o;
        return Objects.equals(isbn, that.isbn);
    }

    /**
    * Calcula el código hash de la ficha, basado en el campo {@code isbn}.
    * @return el valor hash correspondiente
    */
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}


