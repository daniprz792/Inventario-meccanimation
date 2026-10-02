package inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import inventario.config.ConexionDB;
import inventario.model.Unidad;

public class UnidadDAO {

    // crea una unidad nueva para un producto que requiere_unidades = 1
    public void crearUnidad(Unidad unidad) {
        String sql = "INSERT INTO unidad (id_producto, codigo_unidad, estado) VALUES (?, ?, ?)";
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, unidad.getIdProducto());
            stmt.setString(2, unidad.getCodigoUnidad());
            stmt.setString(3, unidad.getEstado() != null ? unidad.getEstado() : "disponible");
            stmt.executeUpdate();
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // todas las unidades de UN producto especifico (nunca de otro)
    public List<Unidad> listarPorProducto(int idProducto) {
        List<Unidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM unidad WHERE id_producto = ? ORDER BY id_unidad";
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearUnidad(rs));
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    // WHERE id_unidad = ? garantiza que solo se actualiza ESA unidad,
    // sin afectar las demas unidades ni otros productos
    public void actualizarEstado(int idUnidad, String estado) {
        String sql = "UPDATE unidad SET estado = ? WHERE id_unidad = ?";
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, estado);
            stmt.setInt(2, idUnidad);
            stmt.executeUpdate();
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// este lo que hace es eliminar la unidad de la base de datos, no solo cambiar su estado
    public void eliminarUnidad(int idUnidad) {
        String sql = "DELETE FROM unidad WHERE id_unidad = ?";
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idUnidad);
            stmt.executeUpdate();
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// hace un mapeo de los datos de la base de datos a un objeto Unidad
    private Unidad mapearUnidad(ResultSet rs) throws Exception {
        Unidad unidad = new Unidad();
        unidad.setId(rs.getInt("id_unidad"));
        unidad.setIdProducto(rs.getInt("id_producto"));
        unidad.setCodigoUnidad(rs.getString("codigo_unidad"));
        unidad.setEstado(rs.getString("estado"));
        unidad.setFechaRegistro(rs.getString("fecha_registro"));
        return unidad;
    }
}