package com.proyecto1diseno.app.Controlador;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.sound.midi.SysexMessage;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.proyecto1diseno.app.Modelo.Estudiante;
import com.proyecto1diseno.app.Modelo.Notificacion;
import com.proyecto1diseno.app.Modelo.Observador;
import com.proyecto1diseno.app.Modelo.Profesor;
import com.proyecto1diseno.app.Servicio.EstudianteService;
import com.proyecto1diseno.app.Servicio.NotificacionService;
import com.proyecto1diseno.app.Servicio.ProfesorService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/estudiante")
public class ControladorEstudiante implements Observador {

    private final EstudianteService estudianteService;
    private final NotificacionService notificacionService;

    @Autowired
    public ControladorEstudiante(EstudianteService estudianteService, NotificacionService notificacionService) {
        this.estudianteService = estudianteService;
        this.notificacionService = notificacionService;
    }

    @PostMapping("/gestionarEst")
    public ResponseEntity<List<Map<String,Object>>> gestionarEst(@RequestBody Map<String, Object> requestBody) throws SQLException, JsonProcessingException {
        String user = (String) requestBody.get("user");
        log.info("USER: " + user);
         
        List<Map<String, Object>> estudiantes = estudianteService.obtenerEstudiantes(user);
        log.info("ESTUDIANTES: " + estudiantes);
        return ResponseEntity.ok().body(estudiantes);
    }

    /*@PostMapping("/modificarEstudiante")
    public ResponseEntity<String> getEstudiante(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String codigoEst = (String) requestBody.get("carnet");
        log.info("AQUI");
        log.info(codigoEst);
        Estudiante estudianteAMostrar = estudianteService.getEstudiante(codigoEst);
        Gson gson = new Gson();
        String jsonEstudiante = gson.toJson(estudianteAMostrar);
        return ResponseEntity.ok().body(jsonEstudiante);
        }*/

        @PostMapping("/modEst")
        public ResponseEntity<String> getEstudiante(@RequestBody Map<String, Object> requestBody) throws SQLException {
            String codigoEst = (String) requestBody.get("codigo");
            Estudiante estudianteAMostrar = estudianteService.getEstudiante(codigoEst);
            Gson gson = new Gson();
            String jsonEstudiante = gson.toJson(estudianteAMostrar);
            return ResponseEntity.ok().body(jsonEstudiante);
            }

        @PostMapping("/datosEstRes")
        public ResponseEntity<String> modificarEstudiante(@RequestBody Map<String, Object> estudianteData) throws SQLException {
            Estudiante estudiante = new Estudiante();
            estudiante.setCarnet(Integer.parseInt(estudianteData.get("id").toString()));
            estudiante.setNombre((String) estudianteData.get("nombre"));
            estudiante.setApellido1((String) estudianteData.get("apellido1"));
            estudiante.setApellido2((String) estudianteData.get("apellido2"));
            estudiante.setCorreo((String) estudianteData.get("correo"));
            estudiante.setContrasena((String) estudianteData.get("pass"));
            estudiante.setCelular(Integer.parseInt(estudianteData.get("tel").toString()));
            String respuestaModificar = estudianteService.modificarEstudiante(estudiante);
            if (respuestaModificar.startsWith("Error: ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaModificar);
            } else {
                return ResponseEntity.ok().body(respuestaModificar);
            }
        }

        @PostMapping("/generarExcel")
        public List<Map<String,Object>> generarExcel(@RequestBody Map<String, Object> requestBody) {
            try {
                
                String user = (String) requestBody.get("user");

                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println(user);
                System.out.println();
                System.out.println();


                List<Map<String, Object>> estudiantes = estudianteService.obtenerEstudiantes(user);

                // Crear un objeto de Excel
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Estudiantes");
    
                // Establecer los encabezados de columna
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("ID");
                headerRow.createCell(1).setCellValue("Nombre");
                headerRow.createCell(2).setCellValue("Correo");
                headerRow.createCell(3).setCellValue("Teléfono");
    
                // Agregar los datos de los estudiantes a las filas
                int rowNum = 1;
                for (Map<String, Object> estudiante : estudiantes) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(String.valueOf(estudiante.get("id")));
                    row.createCell(1).setCellValue(String.valueOf(estudiante.get("nombre")));
                    row.createCell(2).setCellValue(String.valueOf(estudiante.get("correo")));
                    row.createCell(3).setCellValue(String.valueOf(estudiante.get("tel")));
                }
                // Guardar el archivo Excel
                try (OutputStream outputStream = new FileOutputStream("estudiantes.xlsx")) {
                    workbook.write(outputStream);
                } catch (IOException e) {
                    // Manejar el error de escritura del archivo
                }
                return estudiantes;
            } catch (SQLException e) {
                List<Map<String, Object>> estudiantes = new ArrayList<Map<String, Object>>();
                return estudiantes;
                // Manejar el error y enviar una respuesta de error al cliente
            }
        }
    
        @PostMapping("/cargarExcel")
        public List<Map<String,Object>> cargarExcel(@RequestBody Map<String, Object> requestBody) {
            try {
                
                String user = (String) requestBody.get("user");
                
                String path = (String) requestBody.get("path");

                FileInputStream archivo = new FileInputStream(new File(path));
    
                Workbook workbook = WorkbookFactory.create(archivo);
    
                Sheet hoja = workbook.getSheetAt(0);

                try {
                    boolean flag = true;
                    for (Row fila : hoja) {
                        if (flag) {
                            for (Cell celda : fila) {
                                System.out.print(celda.toString() + "\t");
                            }
                            flag = false;
                            continue;
                        }

                        ArrayList tempEst = new ArrayList<Object>();
                        for (Cell celda : fila) {
                            if (celda.getCellType().toString() == "STRING") {
                                tempEst.add(celda.getStringCellValue());
                            } else if (celda.getCellType().toString() == "NUMERIC") {
                                tempEst.add((int) celda.getNumericCellValue());
                            } else if (celda.getCellType().toString() == "BLANK") {
                                break;
                            }
                        }

                        for (Object o : tempEst) {
                            System.out.println(o.toString());
                        }

                        Estudiante estudiante = new Estudiante(tempEst.get(0).toString(),
                        Integer.parseInt(tempEst.get(1).toString()), tempEst.get(2).toString(),
                        tempEst.get(3).toString(), tempEst.get(4).toString(),
                        tempEst.get(5).toString(), tempEst.get(6).toString(),
                        Integer.parseInt(tempEst.get(7).toString()), tempEst.get(8).toString());

                        String respuestaInsertar = estudianteService.insertarEstudiante(estudiante);
                        tempEst.clear();
                    }

                    List<Map<String, Object>> estudiantes = estudianteService.obtenerEstudiantes(user);
                    workbook.close();
                    return estudiantes;
                } catch (SQLException e) {
                    List<Map<String, Object>> estudiantes = new ArrayList<Map<String, Object>>();
                    workbook.close();
                    return estudiantes;
                    // Manejar el error y enviar una respuesta de error al cliente
                }

            } catch (IOException e) {
                e.printStackTrace();
                List<Map<String, Object>> estudiantes = new ArrayList<Map<String, Object>>();
                    return estudiantes;
            }
        }

        @PostMapping("/cambiarNumEst")
        public ResponseEntity<String> modificarEstudiante2(@RequestBody Map<String, Object> estudianteData) throws SQLException {
            System.out.println("ESTUDIANTE");
            System.out.println(estudianteData);
            String correo = (String) estudianteData.get("user");
            String celular = (String) estudianteData.get("cel");
            System.out.println("CELULAR");
            System.out.println(celular);
            String respuestaModificar = estudianteService.modificarEstudiante2(correo ,celular);
            if (respuestaModificar.startsWith("Error: ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaModificar);
            } else {
                return ResponseEntity.ok().body(respuestaModificar);
            }
        }   

        @PostMapping("/perfilEst")
        public ResponseEntity<String> getEstudiante2(@RequestBody Map<String, Object> requestBody) throws SQLException {
            String correo = (String) requestBody.get("user");
            Estudiante estudianteAMostrar = estudianteService.getEstudiante2(correo);
            Gson gson = new Gson();
            String jsonEstudiante = gson.toJson(estudianteAMostrar);
            return ResponseEntity.ok().body(jsonEstudiante);
            }

        @PostMapping("/mostCel")
        public ResponseEntity<String> mostrarCelular(@RequestBody Map<String, Object> requestBody) throws SQLException {
            String correo = (String) requestBody.get("user");
            System.out.println("correo");
            System.out.println(correo);
            String celular = estudianteService.mostrarCelular(correo);
            System.out.println("celular");
            System.out.println(celular);
            Gson gson = new Gson();
            String jsonCelular = gson.toJson(celular);
            return ResponseEntity.ok().body(jsonCelular);
        }

        @PostMapping("/subscribirObservador")
        public ResponseEntity<String> subscribirObservador(@RequestBody Map<String, Object> requestBody) throws SQLException {
            String user = (String)  requestBody.get("user");
            String respuestaSubscripcion = estudianteService.subscribirObservador(user);
            if (respuestaSubscripcion.startsWith("Error: ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaSubscripcion);
            } else {
                notificacionService.agregarObservador(this);
                return ResponseEntity.ok().body(respuestaSubscripcion);
            }
        }

        String observadorUser = null;

        @PostMapping("/gestionarBuzon")
        public ResponseEntity<List<Map<String,Object>>> obtenerNotificaciones(@RequestBody Map<String, Object> requestBody) throws SQLException, JsonProcessingException {
            String user = (String) requestBody.get("user");
            List<Map<String, Object>> notificaciones = estudianteService.obtenerNotificaciones(user);
            if (notificaciones == null || notificaciones.isEmpty()) {
                log.info("Error: No estas suscrito al sistema de notificaciones.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
            } else {
                observadorUser = user;
                return ResponseEntity.ok().body(notificaciones);
            }
        }

        @PostMapping("/agregarNotif")
        public ResponseEntity<String> agregarNotificacion(@RequestBody Map<String, Object> requestBody) throws SQLException {
            String user = (String) requestBody.get("user");
            Notificacion notificacion = new Notificacion();
            notificacion.setContenido((String) requestBody.get("correo"));
            notificacion.setFechaHora(LocalDateTime.now());
            notificacion.setLeida(false);
            String respuesta = estudianteService.agregarNotificacion(notificacion, user);
            if (respuesta.startsWith("Error: ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            } else {
                String[] partes = respuesta.split(" ");
                int idEmisor = Integer.parseInt(partes[0]);
                int idNotificacion = Integer.parseInt(partes[1]);
                notificacion.setIdNotificacion(idNotificacion);
                notificacion.setEmisor(idEmisor);
                notificacionService.notificar(notificacion);
                return ResponseEntity.ok().body("Notificacion agregada exitosamente.");
            }
        }

        @Override
        public void notificar(Notificacion notificacion) {
            estudianteService.notificar(observadorUser, notificacion);
        }

}