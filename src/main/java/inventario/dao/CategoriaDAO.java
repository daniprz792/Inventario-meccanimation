package inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import inventario.config.ConexionDB;
import inventario.model.Categoria;

public class CategoriaDAO {

    // devuelve todas las categorias ordenadas alfabeticamente
    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        try {
            Connection conexion = ConexionDB.conectar();
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM categorias ORDER BY nombre");

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("nombre"));
                lista.add(categoria);
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    // crea una categoria nueva y devuelve el id que le asigno la base de datos
    public int crearCategoria(String nombre) {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        int idGenerado = -1;
        try {
            Connection conexion = ConexionDB.conectar();
            PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nombre);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return idGenerado;
    }
}