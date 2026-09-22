package inventario;

import inventario.dao.ItemDAO;

// entrada de la app de inventario  //
public class App {

    public static void main(String[] args) {

        // El DAO concentra el acceso a los datos y evita mezclar SQL con el arranque.
        ItemDAO dao = new ItemDAO();

        // Consulta los registros actuales y los muestra en la salida de la consola.
        dao.listarItems();

    }
}