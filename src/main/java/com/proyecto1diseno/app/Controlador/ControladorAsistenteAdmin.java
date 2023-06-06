package com.proyecto1diseno.app.Controlador;

import com.proyecto1diseno.app.Servicio.AsistenteAdminService;
import com.proyecto1diseno.app.Modelo.PlanTrabajo;
import com.proyecto1diseno.app.Modelo.Profesor;
import com.proyecto1diseno.app.Modelo.Actividad;
import com.proyecto1diseno.app.Modelo.EquipoGuia;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/asistenteadministrativa")
public class ControladorAsistenteAdmin {

    private AsistenteAdminService asistenteAdminService;

    public ControladorAsistenteAdmin(AsistenteAdminService asistenteAdminService) {
        this.asistenteAdminService = asistenteAdminService;
    }

    @PostMapping("/registrarIngreso")
    public boolean registrarNuevoIngreso(@RequestBody Profesor profesor) {
        return asistenteAdminService.registrarNuevoIngreso(profesor);
    }

    @PostMapping("/darDeBaja")
    public boolean darDeBaja(@RequestBody Profesor profesor) {
        return asistenteAdminService.darDeBaja(profesor);
    }

    @PostMapping("/editarMiembro")
    public boolean editarMiembro(@RequestBody Profesor profesor) {
        return asistenteAdminService.editarMiembro(profesor);
    }

    @GetMapping("/detalleEquipo")
    public EquipoGuia obtenerDetalleEquipo() {
        return asistenteAdminService.obtenerDetalleEquipo();
    }

    @PostMapping("/nombrarCoordinador")
    public boolean nombrarCoordinador(@RequestBody Profesor coordinador) {
        return asistenteAdminService.nombrarCoordinador(coordinador);
    }

    @GetMapping("/consultarPlanTrabajo")
    public PlanTrabajo consultarPlanTrabajo() {
        return asistenteAdminService.consultarPlanTrabajo();
    }

    @GetMapping("/detalleProxAct")
    public Actividad verDetalleProxAct() {
        return asistenteAdminService.verDetalleProxAct();
    }
}