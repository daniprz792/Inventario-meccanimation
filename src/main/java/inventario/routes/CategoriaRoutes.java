package inventario.routes;

import io.javalin.Javalin;
import inventario.dao.CategoriaDAO;
import inventario.model.Categoria;

public class CategoriaRoutes {

    public static void registrar(Javalin app) {
        CategoriaDAO dao = new CategoriaDAO();

        // devuelve todas las categorias, para llenar el <select> del formulario
        app.get("/api/categorias", ctx -> {
            ctx.json(dao.listarCategorias());
        });

        // crea una categoria nueva desde el formulario web
        app.post("/api/categorias", ctx -> {
            String nombre = ctx.formParam("nombre");
            if (nombre == null || nombre.isBlank()) {
                ctx.status(400).result("El nombre de la categoria es obligatorio");
                return;
            }
            int id = dao.crearCategoria(nombre.trim());

            Categoria categoria = new Categoria();
            categoria.setId(id);
            categoria.setNombre(nombre.trim());
            ctx.status(201).json(categoria);
        });
    }
}