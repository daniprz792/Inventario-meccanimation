package inventario;

import inventario.dao.ItemDAO;
import inventario.model.Item;

// entrada de la app de inventario  //
public class App {

    public static void main(String[] args) {

        // El DAO concentra el acceso a los datos y evita mezclar SQL con el arranque.

        //ItemDAO dao = new ItemDAO();

       // Item item = new Item();

        ItemDAO dao = new ItemDAO();

        dao.eliminarItem(4);


        // item.setId(4);
        // item.setNombre("LG 27 pulgadas");
        // ItemDAO dao = new ItemDAO();
        // dao.actualizarItem(item);

        //  item.setCodigoInventario("IN0003");
        // item.setProducto("Monitor");
        // item.setNombre("LG 24 pulgadas");
        //ItemDAO dao = new ItemDAO();

        //create 
        //dao.guardarItem(item);

     // Consulta los registros actuales y los muestra en la salida de la consola.
    dao.listarItems();

    }
} 