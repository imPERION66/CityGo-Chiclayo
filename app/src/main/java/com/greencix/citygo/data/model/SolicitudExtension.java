package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class SolicitudExtension {
    @SerializedName("id")
    private String id;

    @SerializedName("turno_id")
    private String turnoId;

    @SerializedName("conductor_id")
    private String conductorId;

    @SerializedName("minutos_solicitados")
    private int minutosSolicitados;

    @SerializedName("motivo")
    private String motivo;

    @SerializedName("estado")
    private String estado; // pendiente, aprobada, rechazada

    @SerializedName("creado_en")
    private String creadoEn;

    public SolicitudExtension() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTurnoId() { return turnoId; }
    public void setTurnoId(String turnoId) { this.turnoId = turnoId; }

    public String getConductorId() { return conductorId; }
    public void setConductorId(String conductorId) { this.conductorId = conductorId; }

    public int getMinutosSolicitados() { return minutosSolicitados; }
    public void setMinutosSolicitados(int minutosSolicitados) { this.minutosSolicitados = minutosSolicitados; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado != null ? estado : "pendiente"; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCreadoEn() { return creadoEn; }
    public void setCreadoEn(String creadoEn) { this.creadoEn = creadoEn; }
}
