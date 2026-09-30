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
            INSERT INTO producto
            (codigo_inventario, id_categoria, requiere_unidades, nombre, marca, modelo,
             descripcion, precio_unitario, cantidad_existencias, valor_inventario,
             foto, notas, active, fecha_compra)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, item.getCodigoInventario());
            stmt.setInt(2, item.getIdCategoria());
            stmt.setBoolean(3, item.isRequiereUnidades());
            stmt.setString(4, item.getNombre());
            stmt.setString(5, item.getMarca());
            stmt.setString(6, item.getModelo());
            stmt.setString(7, item.getDescripcion());
            stmt.setDouble(8, item.getPrecioUnitario());
            stmt.setInt(9, item.getCantidadExistencias());
            stmt.setDouble(10, item.getPrecioUnitario() * item.getCantidadExistencias());
            stmt.setString(11, item.getFoto());
            stmt.setString(12, item.getNotas());
            stmt.setInt(13, 1); // un producto nuevo siempre nace activo
            stmt.setString(14, item.getFechaCompra());
            stmt.executeUpdate();
            System.out.println("Producto guardado correctamente");
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // WHERE id_producto = ? garantiza que solo se modifica ESE producto
    public void actualizarItem(Item item) {
        String sql = """
            UPDATE producto
            SET codigo_inventario = ?, id_categoria = ?, requiere_unidades = ?, nombre = ?,
                marca = ?, modelo = ?, descripcion = ?, precio_unitario = ?,
                cantidad_existencias = ?, valor_inventario = ?, foto = ?, notas = ?,
                active = ?, fecha_compra = ?
            WHERE id_producto = ?
            """;

        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, item.getCodigoInventario());
            stmt.setInt(2, item.getIdCategoria());
            stmt.setBoolean(3, item.isRequiereUnidades());
            stmt.setString(4, item.getNombre());
            stmt.setString(5, item.getMarca());
            stmt.setString(6, item.getModelo());
            stmt.setString(7, item.getDescripcion());
            stmt.setDouble(8, item.getPrecioUnitario());
            stmt.setInt(9, item.getCantidadExistencias());
            stmt.setDouble(10, item.getPrecioUnitario() * item.getCantidadExistencias());
            stmt.setString(11, item.getFoto());
            stmt.setString(12, item.getNotas());
            stmt.setInt(13, item.getActive());
            stmt.setString(14, item.getFechaCompra());
            stmt.setInt(15, item.getId());
            stmt.executeUpdate();
            System.out.println("Producto actualizado correctamente");
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // trae todos los productos activos, con el nombre de su categoria
    public List<Item> listarItems() {
        List<Item> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nombre AS nombre_categoria
            FROM producto p
            INNER JOIN categoria c ON c.id_categoria = p.id_categoria
            WHERE p.active = 1
            ORDER BY c.nombre, p.nombre
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

    // el filtro por categoria que pediste: INNER JOIN + WHERE id_categoria = ?
    public List<Item> listarPorCategoria(int idCategoria) {
        List<Item> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nombre AS nombre_categoria
            FROM producto p
            INNER JOIN categoria c ON c.id_categoria = p.id_categoria
            WHERE p.active = 1 AND p.id_categoria = ?
            ORDER BY p.nombre
            """;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idCategoria);
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
            SELECT p.*, c.nombre AS nombre_categoria
            FROM producto p
            INNER JOIN categoria c ON c.id_categoria = p.id_categoria
            WHERE p.id_producto = ?
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

    // soft-delete: WHERE id_producto = ? asegura que solo se apaga ese producto
    public void eliminarItem(int id) {
        String sql = "UPDATE producto SET active = 0 WHERE id_producto = ?";
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

    private Item mapearItem(ResultSet rs) throws Exception {
        Item item = new Item();
        item.setId(rs.getInt("id_producto"));
        item.setCodigoInventario(rs.getString("codigo_inventario"));
        item.setIdCategoria(rs.getInt("id_categoria"));
        item.setRequiereUnidades(rs.getBoolean("requiere_unidades"));
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