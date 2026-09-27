package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class PermisoConductor {
    @SerializedName("id")
    private String id;

    @SerializedName("conductor_titular_id")
    private String conductorTitularId;

    @SerializedName("conductor_suplente_id")
    private String conductorSuplenteId;

    @SerializedName("turno_id")
    private String turnoId;

    @SerializedName("fecha_permiso")
    private String fechaPermiso;

    @SerializedName("motivo")
    private String motivo;

    @SerializedName("estado")
    private String estado; // pendiente, aprobado, rechazado

    @SerializedName("revisado_por")
    private String revisadoPor;

    @SerializedName("creado_en")
    private String creadoEn;

    public PermisoConductor() {}

    public PermisoConductor(String conductorTitularId, String conductorSuplenteId, String turnoId, String motivo) {
        this.conductorTitularId = conductorTitularId;
        this.conductorSuplenteId = conductorSuplenteId;
        this.turnoId = turnoId;
        this.motivo = motivo;
        this.estado = "pendiente";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConductorTitularId() { return conductorTitularId; }
    public void setConductorTitularId(String conductorTitularId) { this.conductorTitularId = conductorTitularId; }

    public String getConductorSuplenteId() { return conductorSuplenteId; }
    public void setConductorSuplenteId(String conductorSuplenteId) { this.conductorSuplenteId = conductorSuplenteId; }

    public String getTurnoId() { return turnoId; }
    public void setTurnoId(String turnoId) { this.turnoId = turnoId; }

    public String getFechaPermiso() { return fechaPermiso; }
    public void setFechaPermiso(String fechaPermiso) { this.fechaPermiso = fechaPermiso; }

    public String getMotivo() { return motivo != null ? motivo : ""; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado != null ? estado : "pendiente"; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRevisadoPor() { return revisadoPor; }
    public void setRevisadoPor(String revisadoPor) { this.revisadoPor = revisadoPor; }

    public String getCreadoEn() { return creadoEn; }
    public void setCreadoEn(String creadoEn) { this.creadoEn = creadoEn; }
}
