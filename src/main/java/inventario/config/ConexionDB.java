package inventario.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//  los datos necesarios para conectarse mysql//

public class ConexionDB {

    private static final String URL =
            "jdbc:mysql://localhost:3306/inventario_db";

    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    /** abre y devuelve una conexion nueva con la base de datos del inventario. */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}

