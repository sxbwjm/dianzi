package com.example.dianzi;

import com.example.dianzi.entity.TransactionData;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Migration {
    public static void import_excel(InputStream stream) throws Exception{
           // FileInputStream stream = new FileInputStream(new File(path));
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheet("Transactions");
            Row row = sheet.getRow(1);
            TransactionData transactionData = new TransactionData();
            transactionData.senderName = row.getCell(0).getStringCellValue();
            transactionData.plateNumber = row.getCell(2).getStringCellValue();
            transactionData.accountName = row.getCell(4).getStringCellValue();
            transactionData.weight = (float)row.getCell(5).getNumericCellValue();

    }
}
