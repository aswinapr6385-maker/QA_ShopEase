package com.qa.shopease.utils;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    private Workbook workbook;
    private Sheet sheet;

    public ExcelUtils(String filePath, String sheetName) throws IOException {

        FileInputStream fis = new FileInputStream("src/test/resources/LoginData/LoginData.xlsx");

        workbook = new XSSFWorkbook(fis);

        sheet = workbook.getSheet(sheetName);

        fis.close();
    }

    public int getRowCount() {

        return sheet.getPhysicalNumberOfRows();
    }

    public int getColumnCount() {

        return sheet.getRow(0).getPhysicalNumberOfCells();
    }

    public String getCellData(int rowNum, int colNum) {

        Row row = sheet.getRow(rowNum);

        if (row == null) {
            return "";
        }

        Cell cell = row.getCell(colNum);

        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();

        return formatter.formatCellValue(cell);
    }

    public void closeWorkbook() throws IOException {

        workbook.close();
    }
}