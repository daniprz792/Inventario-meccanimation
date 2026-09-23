package inventario;
//import inventario.dao.ItemDAO;
//import inventario.model.Item;
import io.javalin.Javalin;
import inventario.routes.ItemRoutes;

// entrada de la app de inventario  //

public class App {
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(7000);

        ItemRoutes.registrar(app);

    }


}

                        // estas son las pruebas que hice desde la console :P

        // acceso de los datos de java a mysql 

        //ItemDAO dao = new ItemDAO();

       // Item item = new Item();

        //ItemDAO dao = new ItemDAO();

        //dao.eliminarItem(4);


        // actualizar

        // item.setId(4);
        // item.setNombre("LG 27 pulgadas");
        // ItemDAO dao = new ItemDAO();
        // dao.actualizarItem(item);

        //create 

        //  item.setCodigoInventario("IN0003");
        // item.setProducto("Monitor");
        // item.setNombre("LG 24 pulgadas");
        //ItemDAO dao = new ItemDAO();

        //dao.guardarItem(item);

     // Consulta los registros actuales y los muestra en la salida de la consola.
    //dao.listarItems();

   