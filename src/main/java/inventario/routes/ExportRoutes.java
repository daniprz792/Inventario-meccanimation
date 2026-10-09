package inventario.routes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inventario.dao.ConteoDAO;
import inventario.model.ConteoInventario;
import io.javalin.Javalin;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class ExportRoutes {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void registrar(Javalin app) {
        ConteoDAO dao = new ConteoDAO();

        app.get("/api/conteos/{id}/excel", ctx -> {
            int id;
            try {
                id = Integer.parseInt(ctx.pathParam("id"));
            } catch (NumberFormatException e) {
                ctx.status(400).result("ID inválido");
                return;
            }

            ConteoInventario conteo = dao.listarConteos().stream()
                    .filter(c -> c.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (conteo == null) {
                ctx.status(404).result("Conteo no encontrado");
                return;
            }

            List<Map<String, Object>> escaneados = leerLista(conteo.getEscaneados());
            List<Map<String, Object>> faltantes = leerLista(conteo.getFaltantes());

            try (Workbook wb = new XSSFWorkbook();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                Sheet hoja = wb.createSheet("Conteo " + id);

                CellStyle estiloEncabezado = wb.createCellStyle();
                Font negrita = wb.createFont();
                negrita.setBold(true);
                estiloEncabezado.setFont(negrita);

                String[] titulos = {"Código", "Nombre", "Estado"};
                Row encabezado = hoja.createRow(0);
                for (int i = 0; i < titulos.length; i++) {
                    Cell celda = encabezado.createCell(i);
                    celda.setCellValue(titulos[i]);
                    celda.setCellStyle(estiloEncabezado);
                }

                int fila = 1;
                fila = agregarFilas(hoja, fila, escaneados, "Escaneado");
                agregarFilas(hoja, fila, faltantes, "Faltante");

                for (int i = 0; i < titulos.length; i++) hoja.autoSizeColumn(i);

                wb.write(out);

                ctx.contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                ctx.header("Content-Disposition", "attachment; filename=\"conteo-" + id + ".xlsx\"");
                ctx.result(out.toByteArray());
            }
        });
    }

    private static int agregarFilas(Sheet hoja, int filaInicial,
                                    List<Map<String, Object>> items, String estado) {
        int r = filaInicial;
        for (Map<String, Object> item : items) {
            Object codigo = item.get("codigoInventario") != null
                    ? item.get("codigoInventario") : item.get("codigo");
            Object nombre = item.get("nombre");

            Row row = hoja.createRow(r++);
            row.createCell(0).setCellValue(codigo == null ? "" : String.valueOf(codigo));
            row.createCell(1).setCellValue(nombre == null ? "" : String.valueOf(nombre));
            row.createCell(2).setCellValue(estado);
        }
        return r;
    }

    private static List<Map<String, Object>> leerLista(String json) throws Exception {
        if (json == null || json.isBlank()) return List.of();
        return MAPPER.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
    }
}