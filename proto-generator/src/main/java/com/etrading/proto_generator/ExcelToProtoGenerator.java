package com.etrading.proto_generator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelToProtoGenerator {

    public static void main(String[] args) throws Exception {
        String excelPath = args[0];
        String packageName = args[1];
        String outputPath = args[2];

        Map<String, List<String>> messageMap = new LinkedHashMap<>();
        try (InputStream inp = Files.newInputStream(Paths.get(excelPath));
             Workbook wb = new XSSFWorkbook(inp)) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header
                String msg = row.getCell(0).getStringCellValue();
                String type = row.getCell(1).getStringCellValue();
                String name = row.getCell(2).getStringCellValue();
                int tag = (int) row.getCell(3).getNumericCellValue();

                messageMap.computeIfAbsent(msg, k -> new ArrayList<>())
                        .add("  " + type + " " + name + " = " + tag + ";");
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("syntax = \"proto3\";\n").append("package ").append(packageName).append(";\n\n");
        for (var entry : messageMap.entrySet()) {
            sb.append("message ").append(entry.getKey()).append(" {\n");
            for (String field : entry.getValue()) sb.append(field).append("\n");
            sb.append("}\n\n");
        }

        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        Files.write(path, sb.toString().getBytes());
        System.out.println("Generated .proto file at " + outputPath);
    }

}
