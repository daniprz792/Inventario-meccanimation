package inventario.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import inventario.config.ConexionDB;

public class ItemDAO {

    public void listarItems() {

        try {
            Connection conexion = ConexionDB.conectar();

            Statement stmt = conexion.createStatement();

            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM items"
            );

            while (rs.next()) {

                System.out.println(
                    rs.getString("codigo_inventario")
                    + " - "
                    + rs.getString("nombre")
                );

            }

            conexion.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}