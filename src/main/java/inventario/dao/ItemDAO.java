package inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import inventario.config.ConexionDB;
import inventario.model.Item;

public class ItemDAO {

    public void guardarItem(Item item) {
        String sql = """
            INSERT INTO items
            (codigo_inventario, producto, nombre)
            VALUES (?, ?, ?)
            """;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, item.getCodigoInventario());
            stmt.setString(2, item.getProducto());
            stmt.setString(3, item.getNombre());
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
            SET nombre = ?
            WHERE id = ?
            """;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, item.getNombre());
            stmt.setInt(2, item.getId());
            stmt.executeUpdate();
            System.out.println("Producto actualizado correctamente");
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Item> listarItems() {
        List<Item> lista = new ArrayList<>();
        try {
            Connection conexion = ConexionDB.conectar();
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM items");

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setCodigoInventario(rs.getString("codigo_inventario"));
                item.setProducto(rs.getString("producto"));
                item.setNombre(rs.getString("nombre"));
                lista.add(item);
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    public void eliminarItem(int id) {
        String sql = """
            DELETE FROM items
            WHERE id = ?
            """;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Producto eliminado correctamente");
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}