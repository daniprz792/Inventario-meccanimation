package inventario.model;

public class Item { // producto o item del inventario

    // Identificador interno asignado por la base de datos
    private int id;

    // Codigo visible que identifica el item dentro del inventario.
    private String codigoInventario;

    // Producto o categoria a la que pertenece el item.
    private String producto;

    // nombre del item
    private String nombre;

    // descripcion del producto
    private String descripcion;

    // precio del producto
    private double precioUnitario;

    // unidades disponibles
    private int cantidadExistencias;

    //  existencias del item
    private double valorInventario;
    
    // fecha en la que ingreso al inventario
    private String fechaIngreso;

    // ruta o referencia de la imagen del producto
    private String imagenProducto;

    // notas adicional
    private String observaciones;

    // el producto ya no se comercializa pero son pocos 
    private boolean descontinuado;


    // getters y setters 


    // constructor vacio de la clase item
    public Item() {
    }

    // id del producto 
    public int getId() {
    return id;
    }

    // asina el id dek producto 
    public void setId(int id) {
        this.id = id;
    }

    // obtiene el codigo interno del inventario 
    public String getCodigoInventario() {
        return codigoInventario;
    }

    // asigna el codigo de inventario del producto
    public void setCodigoInventario(String codigoInventario) {
        this.codigoInventario = codigoInventario;
    }

    // obtiene el nombre del producto
    public String getNombre() {
        return nombre;
    }

    // asigna el nombre del producto
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // obtiene el nombre del producto
    public String getProducto() {
        return producto;
    }

    // asigna el nombre del producto
    public void setProducto(String producto) {
        this.producto = producto;
    }

    // obtiene la descripcion del producto
    public String getDescripcion() {
        return descripcion;
    }

    // asigna la descripcion del producto
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // obtiene el precio del producto
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    // asigna el precio del producto
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // obtiene la cantidad de existencias del producto
    public int getCantidadExistencias() {
        return cantidadExistencias;
    }

    // asigna la cantidad de existencias del producto
    public void setCantidadExistencias(int cantidadExistencias) {
        this.cantidadExistencias = cantidadExistencias;
    }
    
    // obtiene el valor del inventario del producto
    public double getValorInventario() {
        return valorInventario;
    }

    // asigna el valor del inventario del producto
    public void setValorInventario(double valorInventario) {
        this.valorInventario = valorInventario;
    }

    // obtiene la fecha de ingreso del producto
    public String getFechaIngreso() {
        return fechaIngreso;
    }

    // asigna la fecha de ingreso del producto
    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    // obtiene la ruta de la imagen del producto
    public String getImagenProducto() {
        return imagenProducto;
    }

    // asigna la ruta de la imagen del producto
    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    // obtiene las observaciones del producto
    public String getObservaciones() {
        return observaciones;
    }

    // asigna las observaciones del producto
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    // obtiene si el producto esta descontinuado
    public boolean isDescontinuado() {
        return descontinuado;
    }
    
    // asigna si el producto esta descontinuado
    public void setDescontinuado(boolean descontinuado) {
        this.descontinuado = descontinuado;
    }

}    
    

