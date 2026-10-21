package inventario.routes;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import inventario.dao.ItemDAO;
import inventario.model.Item;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemRoutes {
    private static final Logger LOGGER = Logger.getLogger(ItemRoutes.class.getName());

    public static void registrar(Javalin app) {
        ItemDAO dao = new ItemDAO();

        app.get("/api/items", ctx -> {
            String categoriaParam = ctx.queryParam("categoria");
            if (categoriaParam != null && !categoriaParam.isBlank()) {
                int idCategoria = Integer.parseInt(categoriaParam);
                ctx.json(dao.listarPorCategoria(idCategoria));
            } else {
                ctx.json(dao.listarItems());
            }
        });

        app.post("/api/items", ctx -> {
            try {
                Item item = construirItemDesdeFormulario(ctx, dao, null);
                dao.guardarItem(item);
                ctx.status(201);
            } catch (IllegalArgumentException e) {
                ctx.status(400).result("Los datos numéricos o la fecha del producto no son válidos");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "No se pudo guardar el producto", e);
                ctx.status(500).result("No se pudo guardar el producto. Verifica los datos y la conexión a la base de datos.");
            }
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
        item.setIdCategoria(Integer.parseInt(ctx.formParam("idCategoria")));
        item.setNombre(ctx.formParam("nombre"));
        item.setMarca(ctx.formParam("marca"));
        item.setModelo(ctx.formParam("modelo"));
        item.setDescripcion(ctx.formParam("descripcion"));
        item.setPrecioUnitario(Double.parseDouble(ctx.formParam("precioUnitario")));
        String fechaCompra = ctx.formParam("fechaCompra");
        item.setFechaCompra(fechaCompra == null || fechaCompra.isBlank() ? null : fechaCompra);
        item.setNotas(ctx.formParam("notas"));

        Item itemActual = null;
        if (idExistente != null) {
            itemActual = dao.buscarPorId(idExistente);
        }

        UploadedFile archivo = ctx.uploadedFile("imagen");

        if (archivo != null) {
            try (InputStream contenido = archivo.content()) {
                String nombreArchivo = System.currentTimeMillis() + "_" + archivo.filename();
                Path destino = Paths.get("uploads/images", nombreArchivo);
                Files.createDirectories(destino.getParent());
                Files.copy(contenido, destino, StandardCopyOption.REPLACE_EXISTING);
                item.setFoto(nombreArchivo);
            } catch (IOException e) {
                throw new RuntimeException("Error guardando la imagen del producto", e);
            }
        } else if (itemActual != null) {
            item.setFoto(itemActual.getFoto());
        }

        item.setActive(itemActual != null ? itemActual.getActive() : 1);

        return item;
    }
}