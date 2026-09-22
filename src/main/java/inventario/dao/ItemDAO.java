package inventario.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import inventario.config.ConexionDB;

//acceso a los datos almacenados en la tabla de items //
public class ItemDAO {

    // cconsulta todos los items y muestra su codigo de barras y nombre en la consola //
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
}