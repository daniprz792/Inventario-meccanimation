package inventario.routes;

import io.javalin.Javalin;
import org.mindrot.jbcrypt.BCrypt;
import inventario.dao.UsuarioDAO;
import inventario.model.Usuario;

public class LoginRoutes {

    public static void registrar(Javalin app) {
        UsuarioDAO dao = new UsuarioDAO();

        // recibe usuario y contraseña, y si coinciden, guarda el nombre en la sesión
        app.post("/api/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            Usuario usuario = dao.buscarPorUsername(username);

            // BCrypt.checkpw compara la contraseña escrita contra el hash guardado,
            // sin necesidad de desencriptar nada
            if (usuario == null || !BCrypt.checkpw(password, usuario.getPasswordHash())) {
                ctx.status(401).result("Usuario o contraseña incorrectos");
                return;
            }

            ctx.req().getSession().setAttribute("usuarioNombre", usuario.getNombre());
            ctx.status(200).result(usuario.getNombre());
        });

        // le dice al frontend si hay alguien con sesión activa ahora mismo
        app.get("/api/sesion", ctx -> {
            Object nombre = ctx.req().getSession().getAttribute("usuarioNombre");
            if (nombre == null) {
                ctx.status(401);
            } else {
                ctx.result((String) nombre);
            }
        });

        app.post("/api/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.status(200);
        });
    }
}