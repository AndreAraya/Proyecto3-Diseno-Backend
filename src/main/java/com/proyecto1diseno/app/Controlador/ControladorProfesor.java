package com.proyecto1diseno.app.Controlador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.proyecto1diseno.app.Modelo.Notificacion;
import com.proyecto1diseno.app.Modelo.Observador;
import com.proyecto1diseno.app.Modelo.Profesor;
import com.proyecto1diseno.app.Servicio.NotificacionService;
import com.proyecto1diseno.app.Servicio.ProfesorService;


@RestController
@Slf4j
@RequestMapping("/profesor")
public class ControladorProfesor implements Observador {
    
    private final ProfesorService profesorService;
    private final NotificacionService notificacionService;
    
    public ControladorProfesor(ProfesorService profesorService, NotificacionService notificacionService) {
        this.profesorService = profesorService;
        this.notificacionService = notificacionService;
    }

    @PostMapping("/agregarProf")
    public ResponseEntity<String> agregarProfesor(@RequestBody Map<String, Object> profesorData) throws SQLException {
        log.info("ENTRO 1");
        String user = (String) profesorData.get("user");
        Profesor profesor = new Profesor();
        profesor.setNombre((String) profesorData.get("nombre"));
        profesor.setCorreo((String) profesorData.get("correo"));
        profesor.setContrasena((String) profesorData.get("pass"));
        profesor.setTelOficina(Integer.parseInt(profesorData.get("tel").toString()));
        profesor.setCelular(Integer.parseInt(profesorData.get("cel").toString()));
        String respuestaAgregar = profesorService.agregarProfesor(profesor, user);
        log.info(respuestaAgregar.toString());
        if (respuestaAgregar.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaAgregar);
        } else {
            return ResponseEntity.ok().body(respuestaAgregar);
        }
    }

    @PostMapping("/gestionarProf")
    public ResponseEntity<List<Map<String,Object>>> obtenerProfesores(@RequestBody Map<String, Object> requestBody) throws SQLException, JsonProcessingException {
        String user = (String) requestBody.get("user");
        List<Map<String, Object>> profesores = profesorService.obtenerProfesores(user);
        return ResponseEntity.ok().body(profesores);
    } 

    @PostMapping("/modProf")
    public ResponseEntity<String> obtenerProfesor(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String codigoProf = (String) requestBody.get("codigo");
        Profesor profesorAMostrar = profesorService.obtenerProfesor(codigoProf);
        Gson gson = new Gson();
        String jsonProfesor = gson.toJson(profesorAMostrar);
        return ResponseEntity.ok().body(jsonProfesor);
    }

    @PostMapping("/datosProfesRes")
    public ResponseEntity<String> modificarProfesor(@RequestBody Map<String, Object> profesorData) throws SQLException {
        String user = (String) profesorData.get("user");
        Profesor profesor = new Profesor();
        profesor.setIdProfesor(Integer.parseInt(profesorData.get("id").toString()));
        profesor.setNombre((String) profesorData.get("nombre"));
        profesor.setCorreo((String) profesorData.get("correo"));
        profesor.setContrasena((String) profesorData.get("pass"));
        profesor.setTelOficina(Integer.parseInt(profesorData.get("tel").toString()));
        profesor.setCelular(Integer.parseInt(profesorData.get("cel").toString()));
        String respuestaModificar = profesorService.modificarProfesor(profesor, user);
        if (respuestaModificar.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaModificar);
        } else {
            return ResponseEntity.ok().body(respuestaModificar);
        }
    }

    @PostMapping("/bajaProf")
    public ResponseEntity<String> darDeBajaProfesor(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        String codigoProfString = (String) requestBody.get("codigo");
        int codigoProf = Integer.parseInt(codigoProfString);
        String respuestaBaja = profesorService.darDeBajaProfesor(codigoProf, user);
        if (respuestaBaja.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaBaja);
        } else {
            return ResponseEntity.ok().body(respuestaBaja);
        }
    }

    @PostMapping("/defGuia")
    public ResponseEntity<String> defGuiaProfesor(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String codigoProfString = (String) requestBody.get("codigo");
        int codigoProf = Integer.parseInt(codigoProfString);
        String respuestaGuia = profesorService.defGuiaProfesor(codigoProf);
        if (respuestaGuia.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaGuia);
        } else {
            return ResponseEntity.ok().body(respuestaGuia);
        }
    }

    @PostMapping("/gestionarProfGuia")
    public ResponseEntity<List<Map<String,Object>>> obtenerProfesoresGuia(@RequestBody Map<String, Object> requestBody) throws SQLException, JsonProcessingException {
        String user = (String) requestBody.get("user");
        List<Map<String, Object>> profesoresGuia = profesorService.obtenerProfesoresGuia(user);
        return ResponseEntity.ok().body(profesoresGuia);
    } 

    @PostMapping("/defCoord")
    public ResponseEntity<String> definirCoordinador(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        String codigoProfString = (String) requestBody.get("codigo");
        int codigoProf = Integer.parseInt(codigoProfString);
        String respuestaBaja = profesorService.definirCoordinador(codigoProf, user);
        if (respuestaBaja.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaBaja);
        } else {
            return ResponseEntity.ok().body(respuestaBaja);
        }
    }
    
    @PostMapping("/subscribirObservador")
    public ResponseEntity<String> subscribirObservador(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String)  requestBody.get("user");
        String respuestaSubscripcion = profesorService.subscribirObservador(user);
        notificacionService.agregarObservador(this);
        if (respuestaSubscripcion.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaSubscripcion);
        } else {
            return ResponseEntity.ok().body(respuestaSubscripcion);
        }
    }

    String observadorUser = null;

    @PostMapping("/gestionarBuzon")
    public ResponseEntity<List<Map<String,Object>>> obtenerNotificaciones(@RequestBody Map<String, Object> requestBody) throws SQLException, JsonProcessingException {
        String user = (String) requestBody.get("user");
        List<Map<String, Object>> notificaciones = profesorService.obtenerNotificaciones(user);
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
        String respuesta = profesorService.agregarNotificacion(notificacion, user);
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

    @PostMapping("/delNotif")
    public ResponseEntity<String> eliminarNotificacion(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        int idNotificacion = (int) requestBody.get("idNotif");
        String respuesta = profesorService.eliminarNotificacion(idNotificacion, user);
        if (respuesta.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        } else {
            return ResponseEntity.ok().body("Notificacion eliminada exitosamente.");
        }
    }

    @PostMapping("/delNotifs")
    public ResponseEntity<String> eliminarNotificaciones(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        String respuesta = profesorService.eliminarNotificaciones(user);
        if (respuesta.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        } else {
            return ResponseEntity.ok().body("Notificaciones eliminadas exitosamente.");
        }
    }


    @Override
    public void notificar(Notificacion notificacion) {
        profesorService.notificar(observadorUser, notificacion);
    }


}