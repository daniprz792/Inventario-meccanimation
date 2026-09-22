package inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

import inventario.config.ConexionDB;
import inventario.model.Item;

//acceso a los datos almacenados en la tabla de items //
public class ItemDAO {
    //guardar un item en la bd

    // crud - c - create 
        public void guardarItem(Item item) {


            // insertar un nuevo registro en la tabla de items 
    String sql = """
        INSERT INTO items
        (codigo_inventario, producto, nombre)
        VALUES (?, ?, ?)
        """;

        // se abre la conexion solo cuando se necesita ejecutaar la consulta
    try {

        Connection conexion = ConexionDB.conectar();

        PreparedStatement stmt =
                conexion.prepareStatement(sql);

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

//update 
    public void actualizarItem(Item item) {

    String sql = """
        UPDATE items
        SET nombre = ?
        WHERE id = ?
        """;

    try {

        Connection conexion = ConexionDB.conectar();

        PreparedStatement stmt =
                conexion.prepareStatement(sql);

        stmt.setString(1, item.getNombre());
        stmt.setInt(2, item.getId());

        stmt.executeUpdate();

        System.out.println("Producto actualizado correctamente");

        conexion.close();

    } catch (Exception e) {
        System.out.println(e.getMessage());
    }

}

    // cconsulta todos los items y muestra su codigo de barras y nombre en la consola //
    //read 
    public void listarItems() {
        try {

            // se abre la conexion solo cuando se necesita ejecutar la consulta
            Connection conexion = ConexionDB.conectar();

            Statement stmt = conexion.createStatement();

            // la consulta obtiene todas las columnas para recorrer cada registro.
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM items"
            );

            // cada fila del resultado representa un item del inventario
            while (rs.next()) {

                System.out.println(
                    rs.getString("codigo_inventario")
                    + " - "
                    + rs.getString("nombre")
                );

            }

            conexion.close();

        } catch (Exception e) {
            // informa cualquier problema de conexion o de ejecucion de la consulta
            System.out.println(e.getMessage());
        }

    }

    // delete - eliminar algun item de la bd 
    public void eliminarItem(int id) {

    String sql = """
        DELETE FROM items
        WHERE id = ?
        """;

    try {

        Connection conexion = ConexionDB.conectar();

        PreparedStatement stmt =
                conexion.prepareStatement(sql);

        stmt.setInt(1, id);

        stmt.executeUpdate();

        System.out.println("Producto eliminado correctamente");

        conexion.close();

    } catch (Exception e) {
        System.out.println(e.getMessage());
    }

}
}