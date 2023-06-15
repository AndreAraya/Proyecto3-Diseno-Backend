package com.proyecto1diseno.app.Servicio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.proyecto1diseno.app.Modelo.Notificacion;
import com.proyecto1diseno.app.Modelo.Observador;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificacionService {
    private List<Observador> observadores;

    public NotificacionService() {
        this.observadores = new ArrayList<>();
    }

    public void agregarObservador(Observador observador) {
        observadores.add(observador);
    }

    public void removerObservador(Observador observador) {
        observadores.remove(observador);
    }

    public void notificar(Notificacion notificacion) {
        log.info("Ha entrado a notificar va a entrar a ciclo");
        log.info(observadores.toString());
        for (Observador observador : observadores) { 
            log.info("Notificando");
            observador.notificar(notificacion);
        }
    }
}