package com.proyecto1diseno.app.Controlador;

import com.proyecto1diseno.app.Servicio.AsistenteAdminService;
import com.proyecto1diseno.app.Servicio.NotificacionService;
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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/asistenteadministrativa")
public class ControladorAsistenteAdmin implements Observador {

    private AsistenteAdminService asistenteAdminService;
    private final NotificacionService notificacionService;

    @Autowired
    public ControladorAsistenteAdmin(AsistenteAdminService asistenteAdminService, NotificacionService notificacionService) {
        this.asistenteAdminService = asistenteAdminService;
        this.notificacionService = notificacionService;
    }

    @PostMapping("/subscribirObservador")
    public ResponseEntity<String> subscribirObservador(@RequestBody Map<String, Object> requestBody) throws SQLException {
        String user = (String)  requestBody.get("user");
        String respuestaSubscripcion = asistenteAdminService.subscribirObservador(user);
        notificacionService.agregarObservador(this);
        if (respuestaSubscripcion.startsWith("Error: ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaSubscripcion);
        } else {
            return ResponseEntity.ok().body(respuestaSubscripcion);
        }
    }

    @PostMapping("/gestionarBuzon")
        public ResponseEntity<List<Map<String,Object>>> obtenerNotificaciones(@RequestBody Map<String, Object> requestBody) throws SQLException, JsonProcessingException {
            String user = (String) requestBody.get("user");
            List<Map<String, Object>> notificaciones = asistenteAdminService.obtenerNotificaciones(user);
            return ResponseEntity.ok().body(notificaciones);
        }

    @Override
    public void notificar(Notificacion notificacion) {
        throw new UnsupportedOperationException("Unimplemented method 'notificar'");
    }
}