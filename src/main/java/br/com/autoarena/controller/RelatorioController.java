package br.com.autoarena.controller;

import br.com.autoarena.service.PdfGeneratorService;
import br.com.autoarena.service.VeiculoService;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/private/relatorios")
public class RelatorioController {

    private final VeiculoService veiculoService;
    private final PdfGeneratorService pdfGeneratorService;

    @Autowired
    public RelatorioController(VeiculoService veiculoService, PdfGeneratorService pdfGeneratorService) {
        this.veiculoService = veiculoService;
        this.pdfGeneratorService = pdfGeneratorService;

    }

    @GetMapping
    public String showRelatoriosPage() {
        return "private/relatorios";
    }

    @GetMapping("/veiculos-por-montadora")
    @ResponseBody
    public Map<String, Long> getVeiculosPorMontadora() {
        List<Object[]> resultados = veiculoService.countVeiculosByMontadora();
        Map<String, Long> mapaResultados = new HashMap<>();

        for (Object[] resultado : resultados) {
            String nomeMontadora = (String) resultado[0];
            Long contagem = (Long) resultado[1];
            mapaResultados.put(nomeMontadora, contagem);
        }

        return mapaResultados;
    }

    @GetMapping("/veiculos-por-montadora/exportar-excel")
    public void exportarVeiculosPorMontadora(HttpServletResponse response) throws IOException {
        // 1. Configurar o tipo de resposta HTTP para download de arquivo Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String headerValue = "attachment; filename=relatorio_veiculos_por_montadora_" + timestamp + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // 2. Obter os dados do serviço
        List<Object[]> resultados = veiculoService.countVeiculosByMontadora();

        // 3. Criar o arquivo Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Veículos por Montadora");

        // 4. Criar o cabeçalho
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Montadora");
        headerRow.createCell(1).setCellValue("Quantidade de Veículos");

        // 5. Preencher a planilha com os dados
        int rowNum = 1;
        for (Object[] resultado : resultados) {
            Row row = sheet.createRow(rowNum++);
            Cell montadoraCell = row.createCell(0);
            montadoraCell.setCellValue((String) resultado[0]);

            Cell contagemCell = row.createCell(1);
            contagemCell.setCellValue((Long) resultado[1]);
        }

        // 6. Ajustar a largura das colunas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // 7. Escrever o workbook na resposta HTTP
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/veiculos-por-montadora/exportar-pdf")
    public void exportarVeiculosPorMontadoraPdf(HttpServletResponse response) throws IOException, DocumentException {
        // 1. Obter os dados
        Map<String, Long> veiculosPorMontadora = getVeiculosPorMontadora();
        Map<String, Object> data = new HashMap<>();
        data.put("veiculosPorMontadora", veiculosPorMontadora);

        // 2. Gerar o PDF
        byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml(
                //"private/relatorios/relatorio-veiculos-pdf", data
                "private/relatorios/veiculos-pdf", data
        );

        // 3. Configurar a resposta HTTP
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=relatorio_veiculos_por_montadora.pdf";
        response.setHeader(headerKey, headerValue);
        response.setContentLength(pdfBytes.length);

        // 4. Escrever o PDF na resposta
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

}