package com.proyecto1diseno.app.Controlador;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto1diseno.app.Servicio.AsistenteAdminService;
import com.proyecto1diseno.app.Servicio.ProfesorService;
import com.proyecto1diseno.app.Modelo.AsistenteAdmin;
import com.proyecto1diseno.app.Modelo.Profesor;

@RestController
@RequestMapping("/validarDatos")
public class ControladorLogin {

    private final ProfesorService servicioProfesor;
    private final AsistenteAdminService servicioAsistenteAdmin;

    public ControladorLogin(ProfesorService servicioProfesor, AsistenteAdminService servicioAsistenteAdmin) {
        this.servicioProfesor = servicioProfesor;
        this.servicioAsistenteAdmin = servicioAsistenteAdmin;
    }
 
    @PostMapping
    public ResponseEntity<String> validarDatos(@RequestBody Map<String, String> usuario) throws SQLException {
        if (usuario == null|| !usuario.containsKey("user") || !usuario.containsKey("password")) {
            return ResponseEntity.badRequest().body("Usuario no puede estar vacío");
        }

        String correo = usuario.get("user");
        String contrasena = usuario.get("password");
 
        // Validar credenciales del Profesor
        Optional<Profesor> profesor = servicioProfesor.validarCredenciales(correo, contrasena);
        if (profesor.isPresent()) {
            return ResponseEntity.ok("Profesor");
        }
 
        // Validar credenciales del Asistente Administrativo
        Optional<AsistenteAdmin> asistente = servicioAsistenteAdmin.validarCredenciales(correo, contrasena);
        if (asistente.isPresent()) {
            return ResponseEntity.ok("Asistente");
        }
 
        // Si no se encontró un usuario válido, devolver un error
        return ResponseEntity.badRequest().body("Usuario y/o contraseña incorrectos");
    }
}