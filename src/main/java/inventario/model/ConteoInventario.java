package inventario.model;

public class ConteoInventario {
    private int id;
    private String fechaConteo;
    private int totalProductos;
    private int totalContados;
    private String escaneados; // lista de productos contados, guardada como texto JSON
    private String faltantes;  // lista de productos faltantes, guardada como texto JSON

    public ConteoInventario() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFechaConteo() { return fechaConteo; }
    public void setFechaConteo(String fechaConteo) { this.fechaConteo = fechaConteo; }

    public int getTotalProductos() { return totalProductos; }
    public void setTotalProductos(int totalProductos) { this.totalProductos = totalProductos; }

    public int getTotalContados() { return totalContados; }
    public void setTotalContados(int totalContados) { this.totalContados = totalContados; }

    public String getEscaneados() { return escaneados; }
    public void setEscaneados(String escaneados) { this.escaneados = escaneados; }

    public String getFaltantes() { return faltantes; }
    public void setFaltantes(String faltantes) { this.faltantes = faltantes; }
}