package inventario.model;

public class Item {

    private int id;
    private String codigoInventario;
    private String producto;
    private String nombre;
    private String descripcion;
    private double precioUnitario;
    private int cantidadExistencias;
    private double valorInventario;
    private String fechaIngreso;
    private String imagenProducto;
    private String observaciones;
    private boolean descontinuado;

    public Item() {
    }

    public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public String getCodigoInventario() {
    return codigoInventario;
}

public void setCodigoInventario(String codigoInventario) {
    this.codigoInventario = codigoInventario;
}

public String getNombre() {
    return nombre;
}

public void setNombre(String nombre) {
    this.nombre = nombre;
}

}
    
    

