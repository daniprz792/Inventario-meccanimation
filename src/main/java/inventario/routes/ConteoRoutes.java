package inventario.routes;

import io.javalin.Javalin;
import inventario.dao.ConteoDAO;
import inventario.model.ConteoInventario;

public class ConteoRoutes {

    public static void registrar(Javalin app) {
        ConteoDAO dao = new ConteoDAO();

        app.post("/api/conteos", ctx -> {
            ConteoInventario conteo = ctx.bodyAsClass(ConteoInventario.class);
            dao.guardarConteo(conteo);
            ctx.status(201);
        });

        app.get("/api/conteos/ultimo", ctx -> {
            ConteoInventario ultimo = dao.obtenerUltimoConteo();
            if (ultimo == null) {
                ctx.status(204);
            } else {
                ctx.json(ultimo);
            }
        });
    }
}