package com.proyecto1diseno.app.Modelo;

import java.util.Collection;

public class PlanTrabajo {
    private Collection<Actividad> plan;

    public PlanTrabajo(Collection<Actividad> plan) {
        this.plan = plan;
    }

    public Collection<Actividad> getPlan() {
        return plan;
    }

    public void setPlan(Collection<Actividad> plan) {
        this.plan = plan;
    }
}