package com.proyecto1diseno.app.Servicio;

import org.apache.poi.ss.usermodel.*;

import com.proyecto1diseno.app.Modelo.Estudiante;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {

    public static List<Estudiante> cargarDatosDesdeExcel(String filePath) {
        List<Estudiante> estudiantes = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Obtener la primera hoja del archivo Excel

            Iterator<Row> rowIterator = sheet.iterator();
            // Iterar sobre las filas del archivo Excel
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Saltar la primera fila (encabezados de columna)
                if (row.getRowNum() == 0) {
                    continue;
                }

                // Crear un objeto Estudiante y asignar los valores de las celdas a las propiedades correspondientes
                Estudiante estudiante = new Estudiante();
                /*estudiante.setIdSede((int) row.getCell(0).getNumericCellValue());
                estudiante.setCarne(row.getCell(1).getStringCellValue());
                estudiante.setApellido1(row.getCell(2).getStringCellValue());
                estudiante.setApellido2(row.getCell(3).getStringCellValue());
                estudiante.setNombre(row.getCell(4).getStringCellValue());
                estudiante.setSegundoNombre(row.getCell(5).getStringCellValue());
                estudiante.setCorreo(row.getCell(6).getStringCellValue());
                estudiante.setNumeroCelular(row.getCell(7).getStringCellValue());
                estudiante.setContraseña(row.getCell(8).getStringCellValue());*/

                estudiantes.add(estudiante);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return estudiantes;
    }
}
