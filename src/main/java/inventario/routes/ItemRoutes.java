package inventario.routes;

import io.javalin.Javalin;
import inventario.dao.ItemDAO;
import inventario.model.Item;

public class ItemRoutes {

    public static void registrar(Javalin app) {
        ItemDAO dao = new ItemDAO();

        // Devuelve todos los items en JSON
        app.get("/api/items", ctx -> {
            ctx.json(dao.listarItems());
        });

        // Recibe un item nuevo desde el formulario y lo guarda
        app.post("/api/items", ctx -> {
            Item item = ctx.bodyAsClass(Item.class);
            dao.guardarItem(item);
            ctx.status(201);
        });
    }
}