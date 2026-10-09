package inventario.routes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inventario.dao.ConteoDAO;
import inventario.dao.ItemDAO;
import inventario.model.ConteoInventario;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ExportRoutes {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void registrar(Javalin app) {
        ConteoDAO conteoDAO = new ConteoDAO();
        ItemDAO itemDAO = new ItemDAO();

        // ---------- Excel de UN conteo ----------
        app.get("/api/conteos/{id}/excel", ctx -> {
            int id;
            try {
                id = Integer.parseInt(ctx.pathParam("id"));
            } catch (NumberFormatException e) {
                ctx.status(400).result("ID inválido");
                return;
            }

            ConteoInventario conteo = conteoDAO.listarConteos().stream()
                    .filter(c -> c.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (conteo == null) {
                ctx.status(404).result("Conteo no encontrado");
                return;
            }

            List<Map<String, Object>> escaneados = leerLista(conteo.getEscaneados());
            List<Map<String, Object>> faltantes = leerLista(conteo.getFaltantes());

            try (Workbook wb = new XSSFWorkbook()) {
                Sheet hoja = wb.createSheet("Conteo " + id);
                CellStyle estiloEncabezado = estiloEncabezado(wb);

                String[] titulos = {"Código", "Nombre", "Estado"};
                escribirEncabezado(hoja, titulos, estiloEncabezado);

                int fila = 1;
                fila = agregarFilasConteo(hoja, fila, escaneados, "Escaneado");
                agregarFilasConteo(hoja, fila, faltantes, "Faltante");

                autoAjustar(hoja, titulos.length);
                enviar(ctx, wb, "conteo-" + id + ".xlsx");
            }
        });

        // ---------- Excel del INVENTARIO COMPLETO ----------
        // Ojo: ruta distinta a /api/items/... para no chocar con /api/items/{id}
        app.get("/api/inventario/excel", ctx -> {
            // AJUSTA: usa exactamente el mismo método que llama tu GET /api/items en ItemRoutes
            List<?> items = itemDAO.listarItems();

            // Convertimos con el mismo JSON que ve el navegador, así no dependemos de getters
            String json = ctx.jsonMapper().toJsonString(items, items.getClass());
            List<Map<String, Object>> filas = leerLista(json);

            String[] titulos = {"Código", "Categoría", "Nombre", "Marca", "Modelo",
                                "Precio unitario", "Descripción", "Notas", "Fecha de compra"};
            String[] claves = {"codigoInventario", "nombreCategoria", "nombre", "marca", "modelo",
                               "precioUnitario", "descripcion", "notas", "fechaCompra"};

            try (Workbook wb = new XSSFWorkbook()) {
                Sheet hoja = wb.createSheet("Inventario");
                escribirEncabezado(hoja, titulos, estiloEncabezado(wb));

                int r = 1;
                for (Map<String, Object> item : filas) {
                    Row row = hoja.createRow(r++);
                    for (int j = 0; j < claves.length; j++) {
                        Object valor = item.get(claves[j]);
                        Cell celda = row.createCell(j);
                        if (valor instanceof Number) {
                            celda.setCellValue(((Number) valor).doubleValue());
                        } else {
                            celda.setCellValue(valor == null ? "" : String.valueOf(valor));
                        }
                    }
                }

                autoAjustar(hoja, titulos.length);
                enviar(ctx, wb, "inventario-" + LocalDate.now() + ".xlsx");
            }
        });
    }

    // ---------- utilidades ----------

    private static CellStyle estiloEncabezado(Workbook wb) {
        CellStyle estilo = wb.createCellStyle();
        Font negrita = wb.createFont();
        negrita.setBold(true);
        estilo.setFont(negrita);
        return estilo;
    }

    private static void escribirEncabezado(Sheet hoja, String[] titulos, CellStyle estilo) {
        Row encabezado = hoja.createRow(0);
        for (int i = 0; i < titulos.length; i++) {
            Cell celda = encabezado.createCell(i);
            celda.setCellValue(titulos[i]);
            celda.setCellStyle(estilo);
        }
    }

    private static void autoAjustar(Sheet hoja, int columnas) {
        for (int i = 0; i < columnas; i++) hoja.autoSizeColumn(i);
    }

    private static int agregarFilasConteo(Sheet hoja, int filaInicial,
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

    private static void enviar(Context ctx, Workbook wb, String nombreArchivo) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        ctx.contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ctx.header("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
        ctx.result(out.toByteArray());
    }

    private static List<Map<String, Object>> leerLista(String json) throws Exception {
        if (json == null || json.isBlank()) return List.of();
        return MAPPER.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
    }
}