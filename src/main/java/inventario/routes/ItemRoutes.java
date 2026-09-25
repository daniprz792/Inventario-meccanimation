package inventario.routes;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import inventario.dao.ItemDAO;
import inventario.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ItemRoutes {

    public static void registrar(Javalin app) {
        ItemDAO dao = new ItemDAO();

        app.get("/api/items", ctx -> {
            ctx.json(dao.listarItems());
        });

        app.post("/api/items", ctx -> {
            Item item = construirItemDesdeFormulario(ctx, dao, null);
            dao.guardarItem(item);
            ctx.status(201);
        });

        app.put("/api/items/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Item item = construirItemDesdeFormulario(ctx, dao, id);
            item.setId(id);
            dao.actualizarItem(item);
            ctx.status(204);
        });

        app.delete("/api/items/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            dao.eliminarItem(id);
            ctx.status(204);
        });
    }

    private static Item construirItemDesdeFormulario(Context ctx, ItemDAO dao, Integer idExistente) {
        Item item = new Item();
        item.setCodigoInventario(ctx.formParam("codigoInventario"));
        item.setProducto(ctx.formParam("producto"));
        item.setNombre(ctx.formParam("nombre"));
        item.setDescripcion(ctx.formParam("descripcion"));
        item.setPrecioUnitario(Double.parseDouble(ctx.formParam("precioUnitario")));
        item.setCantidadExistencias(Integer.parseInt(ctx.formParam("cantidadExistencias")));
        item.setFechaIngreso(ctx.formParam("fechaIngreso"));
        item.setObservaciones(ctx.formParam("observaciones"));
        item.setDescontinuado(Boolean.parseBoolean(ctx.formParam("descontinuado")));

        UploadedFile archivo = ctx.uploadedFile("imagen");

        if (archivo != null) {
            try {
                String nombreArchivo = System.currentTimeMillis() + "_" + archivo.filename();
                Path destino = Paths.get("uploads/images", nombreArchivo);
                Files.createDirectories(destino.getParent());
                Files.copy(archivo.content(), destino, StandardCopyOption.REPLACE_EXISTING);
                item.setImagenProducto(nombreArchivo);
            } catch (IOException e) {
                throw new RuntimeException("Error guardando la imagen del producto", e);
            }
        } else if (idExistente != null) {
            // es una edicion y no se subio imagen nueva: conservamos la imagen actual
            Item itemActual = dao.buscarPorId(idExistente);
            if (itemActual != null) {
                item.setImagenProducto(itemActual.getImagenProducto());
            }
        }

        return item;
    }
}