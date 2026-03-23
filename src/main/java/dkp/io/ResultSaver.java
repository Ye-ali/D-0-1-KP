package dkp.io;

import dkp.model.DKPInstance;
import dkp.model.Item;
import dkp.model.Solution;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 结果保存类
 * 支持将求解结果保存为txt或excel文件
 */
public class ResultSaver {
    
    /**
     * 将结果保存为txt文件
     */
    public static void saveAsTxt(Solution solution, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("=================================================");
            writer.println("           D{0-1}KP Problem Solution");
            writer.println("=================================================");
            writer.println();
            writer.println("Instance Name: " + solution.getInstance().getName());
            writer.println("Dimension: " + solution.getInstance().getDimension());
            writer.println("Capacity: " + solution.getInstance().getCapacity());
            writer.println();
            writer.println("Algorithm: " + solution.getAlgorithm());
            writer.println("Solve Time: " + solution.getSolveTime() + " ms");
            writer.println();
            writer.println("-------------------------------------------------");
            writer.println("                   RESULT");
            writer.println("-------------------------------------------------");
            writer.println("Total Profit: " + solution.getTotalProfit());
            writer.println("Total Weight: " + solution.getTotalWeight());
            writer.println("Remaining Capacity: " + 
                (solution.getInstance().getCapacity() - solution.getTotalWeight()));
            writer.println();
            writer.println("Selected Items (" + solution.getSelectedItems().size() + "):");
            writer.println();
            writer.println(String.format("%-8s %-8s %-12s %-12s %-12s", 
                "ItemID", "Set", "Position", "Profit", "Weight"));
            writer.println("-------------------------------------------------");
            
            for (Item item : solution.getSelectedItems()) {
                writer.println(String.format("%-8d %-8d %-12d %-12d %-12d",
                    item.getId(),
                    item.getSetIndex(),
                    item.getItemIndex(),
                    item.getProfit(),
                    item.getWeight()));
            }
            
            writer.println();
            writer.println("=================================================");
            writer.println("Generated at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println("=================================================");
        }
    }
    
    /**
     * 将结果保存为Excel文件
     */
    public static void saveAsExcel(Solution solution, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solution");
        
        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        
        int rowNum = 0;
        
        // 标题
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("D{0-1}KP Problem Solution");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));
        rowNum++;
        
        // 实例信息
        Row infoRow1 = sheet.createRow(rowNum++);
        infoRow1.createCell(0).setCellValue("Instance Name:");
        infoRow1.createCell(1).setCellValue(solution.getInstance().getName());
        
        Row infoRow2 = sheet.createRow(rowNum++);
        infoRow2.createCell(0).setCellValue("Dimension:");
        infoRow2.createCell(1).setCellValue(solution.getInstance().getDimension());
        
        Row infoRow3 = sheet.createRow(rowNum++);
        infoRow3.createCell(0).setCellValue("Capacity:");
        infoRow3.createCell(1).setCellValue(solution.getInstance().getCapacity());
        
        Row infoRow4 = sheet.createRow(rowNum++);
        infoRow4.createCell(0).setCellValue("Algorithm:");
        infoRow4.createCell(1).setCellValue(solution.getAlgorithm());
        
        Row infoRow5 = sheet.createRow(rowNum++);
        infoRow5.createCell(0).setCellValue("Solve Time:");
        infoRow5.createCell(1).setCellValue(solution.getSolveTime() + " ms");
        
        rowNum++;
        
        // 结果汇总
        Row resultTitleRow = sheet.createRow(rowNum++);
        Cell resultTitleCell = resultTitleRow.createCell(0);
        resultTitleCell.setCellValue("RESULT SUMMARY");
        resultTitleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 4));
        
        Row resultRow1 = sheet.createRow(rowNum++);
        resultRow1.createCell(0).setCellValue("Total Profit:");
        resultRow1.createCell(1).setCellValue(solution.getTotalProfit());
        
        Row resultRow2 = sheet.createRow(rowNum++);
        resultRow2.createCell(0).setCellValue("Total Weight:");
        resultRow2.createCell(1).setCellValue(solution.getTotalWeight());
        
        Row resultRow3 = sheet.createRow(rowNum++);
        resultRow3.createCell(0).setCellValue("Remaining Capacity:");
        resultRow3.createCell(1).setCellValue(
            solution.getInstance().getCapacity() - solution.getTotalWeight());
        
        rowNum++;
        
        // 选中物品列表
        Row listTitleRow = sheet.createRow(rowNum++);
        Cell listTitleCell = listTitleRow.createCell(0);
        listTitleCell.setCellValue("SELECTED ITEMS (" + solution.getSelectedItems().size() + ")");
        listTitleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 4));
        
        // 表头
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Item ID", "Set Index", "Position", "Profit", "Weight"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 数据行
        for (Item item : solution.getSelectedItems()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getSetIndex());
            row.createCell(2).setCellValue(item.getItemIndex());
            row.createCell(3).setCellValue(item.getProfit());
            row.createCell(4).setCellValue(item.getWeight());
            
            // 应用数据样式
            for (int i = 0; i < 5; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // 调整列宽
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 保存文件
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        
        workbook.close();
    }
    
    /**
     * 创建标题样式
     */
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    /**
     * 保存实例信息到txt
     */
    public static void saveInstanceInfo(DKPInstance instance, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(instance.toString());
            writer.println();
            writer.println("ItemSets:");
            writer.println("-------------------------------------------------");
            
            for (int i = 0; i < instance.getSetCount(); i++) {
                writer.println(instance.getItemSet(i).toString());
            }
        }
    }
}
