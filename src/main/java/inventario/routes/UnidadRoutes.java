package inventario.routes;

import io.javalin.Javalin;
import inventario.dao.UnidadDAO;
import inventario.model.Unidad;

public class UnidadRoutes {

    public static void registrar(Javalin app) {
        UnidadDAO dao = new UnidadDAO();

        // unidades de un producto especifico
        app.get("/api/productos/{idProducto}/unidades", ctx -> {
            int idProducto = Integer.parseInt(ctx.pathParam("idProducto"));
            ctx.json(dao.listarPorProducto(idProducto));
        });

        // crea una unidad nueva para ese producto
        app.post("/api/productos/{idProducto}/unidades", ctx -> {
            int idProducto = Integer.parseInt(ctx.pathParam("idProducto"));
            Unidad unidad = new Unidad();
            unidad.setIdProducto(idProducto);
            unidad.setCodigoUnidad(ctx.formParam("codigoUnidad"));
            unidad.setEstado(ctx.formParam("estado"));
            dao.crearUnidad(unidad);
            ctx.status(201);
        });

        // cambia el estado de UNA unidad puntual (disponible, asignado, dañado, baja...)
        app.put("/api/unidades/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            String estado = ctx.formParam("estado");
            dao.actualizarEstado(id, estado);
            ctx.status(204);
        });

        app.delete("/api/unidades/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            dao.eliminarUnidad(id);
            ctx.status(204);
        });
    }
}