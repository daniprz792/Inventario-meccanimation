package inventario;

import inventario.dao.ItemDAO;

public class App {

    public static void main(String[] args) {

        ItemDAO dao = new ItemDAO();

        dao.listarItems();

    }
}