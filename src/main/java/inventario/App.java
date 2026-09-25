package inventario;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import inventario.routes.ItemRoutes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import inventario.routes.CategoriaRoutes;

public class App {
    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("uploads/images"));

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });

            // carpeta aparte para las imagenes que suben los usuarios,
            // fuera de src, para que Javalin las vea apenas se guardan
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/images";
                staticFiles.directory = "uploads/images";
                staticFiles.location = Location.EXTERNAL;
            });
        });

        app.get("/", ctx -> ctx.redirect("/index.html"));
        ItemRoutes.registrar(app);
        CategoriaRoutes.registrar(app);
        app.start(7000);
    }
}



                                    // estas son las pruebas que hice desde la consolola antes de pasar web 

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

   