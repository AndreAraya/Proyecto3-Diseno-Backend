package com.proyecto1diseno.app.Servicio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.proyecto1diseno.app.Modelo.Notificacion;
import com.proyecto1diseno.app.Modelo.Observador;

@Service
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
        for (Observador observador : observadores) { 
            observador.notificar(notificacion);
        }
    }
}