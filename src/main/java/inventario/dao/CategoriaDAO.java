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

    // devuelve todas las categorias de la base de datos, ordenadas por nombre

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        try {
            Connection conexion = ConexionDB.conectar();
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM categoria ORDER BY nombre");

            // mapea cada fila del ResultSet a un objeto Categoria y lo agrega a la lista
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id_categoria"));
                categoria.setNombre(rs.getString("nombre"));
                lista.add(categoria);
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    // crea una nueva categoria y devuelve el id generado, o -1 si hubo un error
    public int crearCategoria(String nombre) {
        String sql = "INSERT INTO categoria (nombre) VALUES (?)";
        int idGenerado = -1;
        try { // abre la conexion, prepara la sentencia y ejecuta el insert
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
