package com.proyecto1diseno.app.Controlador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.proyecto1diseno.app.Modelo.Actividad;
import com.proyecto1diseno.app.Servicio.PlanTrabajoService;

@RestController
@Slf4j
@RequestMapping("/plantrabajo")
public class ControladorPlanTrabajo {
    
    @Autowired
    private PlanTrabajoService planTrabajoService;
    
    public ControladorPlanTrabajo() {
    }
    
    @PostMapping("/obtenerActividades")
    public ResponseEntity<List<Map<String, Object>>> obtenerActividades() throws SQLException, JsonProcessingException {
        List<Map<String, Object>> actividades = planTrabajoService.obtenerActividades();
        return ResponseEntity.ok().body(actividades);
    }

    @PostMapping("/agregarAct")
    public ResponseEntity<String> agregarActividad(@RequestBody Map<String, Object> requestBody) throws SQLException {
        try {
            String tipoActividad = (String) requestBody.get("tipoActividad");
            int semana = Integer.parseInt((String) requestBody.get("semana"));
            String nombre = (String) requestBody.get("nombre");
            LocalDate fechaPublicacion = LocalDate.parse((String) requestBody.get("fechaPublicacion"));
            String responsableProfesor = (String) requestBody.get("responsableProfesor");
            String modalidad = (String) requestBody.get("modalidad");
            String enlace = (String) requestBody.get("enlace");
            String estado = (String) requestBody.get("estado");
            LocalDate fecha = LocalDate.parse((String) requestBody.get("fecha"));
            LocalTime hora = LocalTime.parse((String) requestBody.get("hora"));
            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
            Actividad actividad = new Actividad(semana, tipoActividad, nombre, fechaHora, responsableProfesor, fechaPublicacion, modalidad, enlace, estado);
            String user = (String )requestBody.get("user");
            String respuestaAgregar = planTrabajoService.agregarActividad(actividad, user);
            if (respuestaAgregar.startsWith("Error: ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaAgregar);
            } else {
                return ResponseEntity.ok().body(respuestaAgregar);
            }
        }finally{  
        }
    }

    @PostMapping("/marcarActividad")
    public ResponseEntity<String> marcarActividad(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String nombreAct = (String) requestBody.get("codigo");
        int estado = Integer.parseInt(requestBody.get("estado").toString());
        String respuestaMarcar = planTrabajoService.marcarActividad(nombreAct, estado);
        if (respuestaMarcar.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaMarcar);
        } else {
            return ResponseEntity.ok().body(respuestaMarcar);
        }
    }

    @PostMapping("/obtenerComentarios")
    public ResponseEntity<List<Map<String, Object>>> obtenerComentarios(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String nombreAct = (String) requestBody.get("codigo");
        List<Map<String, Object>> comentarios = planTrabajoService.obtenerComentarios(nombreAct);
        return ResponseEntity.ok().body(comentarios);
    }

    @PostMapping("/agregarComentario")
    public ResponseEntity<String> agregarComentario(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        String mensaje = (String) requestBody.get("mensaje");
        String reply = (String) requestBody.get("reply");
        int codigoComent = 0;

        if (reply != "" && reply != null){
            String pattern = "(\\d+)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(reply);

            if (matcher.find()) {
                String firstNumber = matcher.group(1);
                codigoComent = Integer.parseInt(firstNumber);
            }
        }

        String respuestaComentar = planTrabajoService.agregarComentario(user, codigoComent, mensaje);
        if (respuestaComentar.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaComentar);
        } else {
            return ResponseEntity.ok().body(respuestaComentar);
        }
    
    }          
}