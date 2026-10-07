package inventario;

import org.mindrot.jbcrypt.BCrypt;

public class GenerarHash {
    public static void main(String[] args) {
        String passwordPlano = "inventario2026"; // toca cambiarla
        String hash = BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
        System.out.println(hash);
    }
}