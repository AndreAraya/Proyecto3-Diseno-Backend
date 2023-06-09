package com.proyecto1diseno.app.Controlador;

import com.proyecto1diseno.app.Servicio.AsistenteAdminService;
import com.proyecto1diseno.app.Servicio.NotificacionService;

import lombok.extern.slf4j.Slf4j;

import com.proyecto1diseno.app.Modelo.PlanTrabajo;
import com.proyecto1diseno.app.Modelo.Profesor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.proyecto1diseno.app.Modelo.Actividad;
import com.proyecto1diseno.app.Modelo.EquipoGuia;
import com.proyecto1diseno.app.Modelo.Notificacion;
import com.proyecto1diseno.app.Modelo.Observador;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Slf4j
@RequestMapping("/asistenteadministrativa")
public class ControladorAsistenteAdmin implements Observador {

    private AsistenteAdminService asistenteAdminService;
    private final NotificacionService notificacionService;

    @Autowired
    public ControladorAsistenteAdmin(AsistenteAdminService asistenteAdminService, NotificacionService notificacionService) {
        this.asistenteAdminService = asistenteAdminService;
        this.notificacionService = notificacionService;
    }

    String observadorUser = null;

    @PostMapping("/subscribirObservador")
    public ResponseEntity<String> subscribirObservador(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String)  requestBody.get("user");
        String respuestaSubscripcion = asistenteAdminService.subscribirObservador(user);
        if (respuestaSubscripcion.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaSubscripcion);
        } else {
            observadorUser = user;
            notificacionService.agregarObservador(this);
            return ResponseEntity.ok().body(respuestaSubscripcion);
        }
    }

    @PostMapping("/gestionarBuzon")
        public ResponseEntity<List<Map<String,Object>>> obtenerNotificaciones(@RequestBody Map<String, Object> requestBody) throws SQLException, JsonProcessingException {
            String user = (String) requestBody.get("user");
            List<Map<String, Object>> notificaciones = asistenteAdminService.obtenerNotificaciones(user);
            if (notificaciones.stream().anyMatch(map -> map.containsKey("error"))) {
                log.info("Error: No estás suscrito al sistema de notificaciones.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
            } else {
                return ResponseEntity.ok().body(notificaciones);
            }
        }

    @PostMapping("/agregarNotif")
    public ResponseEntity<String> agregarNotificacion(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        Notificacion notificacion = new Notificacion();
        notificacion.setContenido((String) requestBody.get("contenido"));
        notificacion.setFechaHora(LocalDateTime.now());
        notificacion.setLeida(false);
        String respuesta = asistenteAdminService.agregarNotificacion(notificacion, user);
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
        int idNotificacion = Integer.parseInt(requestBody.get("codigo").toString());
        String respuesta = asistenteAdminService.eliminarNotificacion(idNotificacion, user);
        if (respuesta.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        } else {
            return ResponseEntity.ok().body("Notificacion eliminada exitosamente.");
        }
    }

    @PostMapping("/delNotifs")
        public ResponseEntity<String> eliminarNotificaciones(@RequestBody Map<String, Object> requestBody) throws SQLException {
            String user = (String) requestBody.get("user");
            String respuesta = asistenteAdminService.eliminarNotificaciones(user);
            if (respuesta.startsWith("Error: ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            } else {
                return ResponseEntity.ok().body(respuesta);
            }
        }

    @PostMapping("/desubscribirObservador")
    public ResponseEntity<String> desubscribirObservador(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String)  requestBody.get("user");
        String respuestaSubscripcion = asistenteAdminService.desubscribirObservador(user);
        if (respuestaSubscripcion.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaSubscripcion);
        } else {
            notificacionService.removerObservador(this);
            return ResponseEntity.ok().body(respuestaSubscripcion);
        }
    }

    @PostMapping("/marcarNotifLeida")
    public ResponseEntity<String> marcarNotificacionLeida(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        String codigoNotif = (String) requestBody.get("codigo");
        String respuestaMarcar = asistenteAdminService.marcarNotificacionLeida(user, codigoNotif);
        if (respuestaMarcar.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaMarcar);
        } else {
            return ResponseEntity.ok().body(respuestaMarcar);
        }
    }

    @PostMapping("/marcarNotifNoLeida")
    public ResponseEntity<String> marcarNotificacionNoLeida(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String) requestBody.get("user");
        String codigoNotif = (String) requestBody.get("codigo");
        String respuestaMarcar = asistenteAdminService.marcarNotificacionNoLeida(user, codigoNotif);
        if (respuestaMarcar.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaMarcar);
        } else {
            return ResponseEntity.ok().body(respuestaMarcar);
        }
    }

    

    @Override
    public void notificar(Notificacion notificacion) {
        asistenteAdminService.notificar(observadorUser, notificacion);
    }
}