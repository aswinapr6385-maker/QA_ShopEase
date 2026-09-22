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
    private final DataFormatter formatter;

    /**
     * Constructor
     *
     * @param filePath   Excel file path
     * @param sheetName  Excel sheet name
     * @throws IOException if file cannot be opened
     */
    public ExcelUtils(String filePath, String sheetName) throws IOException {

        FileInputStream fis = new FileInputStream(filePath);

        workbook = new XSSFWorkbook(fis);

        sheet = workbook.getSheet(sheetName);

        formatter = new DataFormatter();

        fis.close();

        if (sheet == null) {
            workbook.close();
            throw new IllegalArgumentException(
                    "Sheet not found: " + sheetName
            );
        }
    }


    /**
     * Returns the total number of rows in the sheet.
     */
    public int getRowCount() {

        return sheet.getPhysicalNumberOfRows();
    }


    /**
     * Returns the total number of columns
     * based on the first row.
     */
    public int getColumnCount() {

        Row firstRow = sheet.getRow(0);

        if (firstRow == null) {
            return 0;
        }

        return firstRow.getPhysicalNumberOfCells();
    }


    /**
     * Returns cell data as String.
     *
     * @param rowNum row number
     * @param colNum column number
     * @return cell value
     */
    public String getCellData(int rowNum, int colNum) {

        Row row = sheet.getRow(rowNum);

        if (row == null) {
            return "";
        }

        Cell cell = row.getCell(colNum);

        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell);
    }


    /**
     * Closes the Excel workbook.
     */
    public void closeWorkbook() throws IOException {

        if (workbook != null) {
            workbook.close();
        }
    }
}