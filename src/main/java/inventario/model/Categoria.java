package inventario.model;

public class Categoria {

    // atributos de la clase Categoria de la base de datos

    private int id;
    private String nombre;

    public Categoria() {
    }

    public int getId() {
        return id;
    }
    // setters y getters para los atributos de la clase Categoria

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}