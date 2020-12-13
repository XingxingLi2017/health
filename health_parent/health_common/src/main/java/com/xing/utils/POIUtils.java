package com.xing.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class POIUtils {
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";
    private final static String DATE_FORMAT = "yyyy/MM/dd";

    /***
     * read excel file and return a list of row values
     * @param file
     * @return
     * @throws IOException
     */
    public static List<String[]> readExcel(MultipartFile file) throws IOException {
        checkFile(file);
        Workbook workbook = getWorkbook(file);
        List<String[]> list = new ArrayList<String[]>();
        if(workbook != null) {
            for(int sheetNum = 0 ; sheetNum < workbook.getNumberOfSheets() ; sheetNum++) {
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null) continue;
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();

                // start from the second row
                for(int i = firstRowNum + 1 ; i <= lastRowNum ; i++) {
                    Row row = sheet.getRow(i);
                    if(row == null) continue;
                    int firstCellNum = row.getFirstCellNum();
                    int lastCellNum = row.getLastCellNum();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    // end before lastCellNum
                    for(int idx = firstCellNum ; idx < lastCellNum ; idx++) {
                        Cell cell = row.getCell(idx);
                        cells[idx] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
            workbook.close();
        }
        return list;
    }

    /***
     * check if the processing file is EXCEL
     * @param file
     * @throws IOException
     */
    public static void checkFile(MultipartFile file) throws IOException {
        if(file == null) {
            throw new FileNotFoundException("Can't find EXCEL file in POIUtils.");
        }
        String fileName = file.getOriginalFilename();
        if(fileName.endsWith(xls) || fileName.endsWith(xlsx)) {
            return;
        } else {
            throw new IOException(fileName + " is not EXCEL file in POIUtils.");
        }
    }

    /***
     * get workbook through extension of file name
     * @param file
     * @return
     */
    public static Workbook getWorkbook(MultipartFile file){
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        try {
            InputStream is = file.getInputStream();
            if(fileName.endsWith(xls)) {
                // excel 2003
                workbook = new HSSFWorkbook(is);
            }
            if(fileName.endsWith(xlsx)) {
                //excel 2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /***
     * get cell string value from different data type
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if(cell == null) {
            return cellValue;
        }

        // if the cell type is DATE , change format and return
        String dataFormatString = cell.getCellStyle().getDataFormatString();
        if(dataFormatString.equals("m/d/yy")) {
            cellValue = new SimpleDateFormat(DATE_FORMAT).format(cell.getDateCellValue());
            return cellValue;
        }

        // if the cell type is number , convert to string
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }

        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //number
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //number
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: // boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: // expression
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: // blank
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: // illegal
                cellValue = "illegal";
                break;
            default:
                cellValue = "undefined";
                break;
        }
        return cellValue;
    }

}
