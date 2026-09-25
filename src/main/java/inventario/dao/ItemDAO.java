package inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import inventario.config.ConexionDB;
import inventario.model.Item;

public class ItemDAO {

    public void guardarItem(Item item) {
        String sql = """
            INSERT INTO items
            (codigo_inventario, id_categoria, nombre, marca, modelo, descripcion,
             precio_unitario, cantidad_existencias, valor_inventario, foto, notas,
             active, fecha_compra)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, item.getCodigoInventario());
            stmt.setInt(2, item.getIdCategoria());
            stmt.setString(3, item.getNombre());
            stmt.setString(4, item.getMarca());
            stmt.setString(5, item.getModelo());
            stmt.setString(6, item.getDescripcion());
            stmt.setDouble(7, item.getPrecioUnitario());
            stmt.setInt(8, item.getCantidadExistencias());
            stmt.setDouble(9, item.getPrecioUnitario() * item.getCantidadExistencias());
            stmt.setString(10, item.getFoto());
            stmt.setString(11, item.getNotas());
            stmt.setInt(12, 1); // un item nuevo siempre nace activo
            stmt.setString(13, item.getFechaCompra());
            stmt.executeUpdate();
            System.out.println("Producto guardado correctamente");
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void actualizarItem(Item item) {
        String sql = """
            UPDATE items
            SET codigo_inventario = ?, id_categoria = ?, nombre = ?, marca = ?, modelo = ?,
                descripcion = ?, precio_unitario = ?, cantidad_existencias = ?, valor_inventario = ?,
                foto = ?, notas = ?, active = ?, fecha_compra = ?
            WHERE id = ?
            """;

        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, item.getCodigoInventario());
            stmt.setInt(2, item.getIdCategoria());
            stmt.setString(3, item.getNombre());
            stmt.setString(4, item.getMarca());
            stmt.setString(5, item.getModelo());
            stmt.setString(6, item.getDescripcion());
            stmt.setDouble(7, item.getPrecioUnitario());
            stmt.setInt(8, item.getCantidadExistencias());
            stmt.setDouble(9, item.getPrecioUnitario() * item.getCantidadExistencias());
            stmt.setString(10, item.getFoto());
            stmt.setString(11, item.getNotas());
            stmt.setInt(12, item.getActive());
            stmt.setString(13, item.getFechaCompra());
            stmt.setInt(14, item.getId());
            stmt.executeUpdate();
            System.out.println("Producto actualizado correctamente");
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // solo trae los items activos, con el nombre de su categoria incluido
    public List<Item> listarItems() {
        List<Item> lista = new ArrayList<>();
        String sql = """
            SELECT i.*, c.nombre AS nombre_categoria
            FROM items i
            JOIN categorias c ON c.id = i.id_categoria
            WHERE i.active = 1
            ORDER BY c.nombre, i.nombre
            """;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearItem(rs));
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    public Item buscarPorId(int id) {
        String sql = """
            SELECT i.*, c.nombre AS nombre_categoria
            FROM items i
            JOIN categorias c ON c.id = i.id_categoria
            WHERE i.id = ?
            """;
        Item item = null;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                item = mapearItem(rs);
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return item;
    }

    // en vez de borrar el registro, lo marca como inactivo para no perder el historial
    public void eliminarItem(int id) {
        String sql = "UPDATE items SET active = 0 WHERE id = ?";
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Producto marcado como inactivo");
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // arma un Item a partir de una fila del ResultSet, para no repetir el mismo bloque 3 veces
    private Item mapearItem(ResultSet rs) throws Exception {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setCodigoInventario(rs.getString("codigo_inventario"));
        item.setIdCategoria(rs.getInt("id_categoria"));
        item.setNombreCategoria(rs.getString("nombre_categoria"));
        item.setNombre(rs.getString("nombre"));
        item.setMarca(rs.getString("marca"));
        item.setModelo(rs.getString("modelo"));
        item.setDescripcion(rs.getString("descripcion"));
        item.setPrecioUnitario(rs.getDouble("precio_unitario"));
        item.setCantidadExistencias(rs.getInt("cantidad_existencias"));
        item.setValorInventario(rs.getDouble("valor_inventario"));
        item.setFoto(rs.getString("foto"));
        item.setNotas(rs.getString("notas"));
        item.setActive(rs.getInt("active"));
        item.setFechaCompra(rs.getString("fecha_compra"));
        item.setFechaRegistro(rs.getString("fecha_registro"));
        item.setFechaActualizacion(rs.getString("fecha_actualizacion"));
        return item;
    }
}