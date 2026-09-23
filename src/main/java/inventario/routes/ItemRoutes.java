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

        // Actualiza un item existente
            app.put("/api/items/{id}", ctx -> {
                Item item = ctx.bodyAsClass(Item.class);
                item.setId(Integer.parseInt(ctx.pathParam("id")));
                dao.actualizarItem(item);
                ctx.status(204);
            });

            // Elimina un item
            app.delete("/api/items/{id}", ctx -> {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminarItem(id);
                ctx.status(204);
            });
    }
}