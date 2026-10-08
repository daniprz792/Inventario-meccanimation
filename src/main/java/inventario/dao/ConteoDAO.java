package inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import inventario.config.ConexionDB;
import inventario.model.ConteoInventario;

public class ConteoDAO {

    public void guardarConteo(ConteoInventario conteo) {
        String sql = """
            INSERT INTO conteo_inventario
            (fecha_conteo, total_productos, total_contados, escaneados, faltantes)
            VALUES (NOW(), ?, ?, ?, ?)
            """;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, conteo.getTotalProductos());
            stmt.setInt(2, conteo.getTotalContados());
            stmt.setString(3, conteo.getEscaneados());
            stmt.setString(4, conteo.getFaltantes());
            stmt.executeUpdate();
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // trae el conteo más reciente, ordenando por fecha descendente y tomando solo 1
    public ConteoInventario obtenerUltimoConteo() {
        String sql = """
            SELECT * FROM conteo_inventario
            ORDER BY fecha_conteo DESC
            LIMIT 1
            """;
        ConteoInventario conteo = null;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                conteo = mapearConteo(rs);
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conteo;
    }

    // trae todos los conteos guardados, del más reciente al más antiguo
    public List<ConteoInventario> listarConteos() {
        List<ConteoInventario> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM conteo_inventario
            ORDER BY fecha_conteo DESC
            """;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearConteo(rs));
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    // convierte una fila de la tabla en un objeto ConteoInventario
    // (así no repetimos estas 6 líneas en los dos métodos de lectura)
    private ConteoInventario mapearConteo(ResultSet rs) throws Exception {
        ConteoInventario conteo = new ConteoInventario();
        conteo.setId(rs.getInt("id_conteo"));
        conteo.setFechaConteo(rs.getString("fecha_conteo"));
        conteo.setTotalProductos(rs.getInt("total_productos"));
        conteo.setTotalContados(rs.getInt("total_contados"));
        conteo.setEscaneados(rs.getString("escaneados"));
        conteo.setFaltantes(rs.getString("faltantes"));
        return conteo;
    }
}