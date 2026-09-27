package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class InfraccionSancion {
    @SerializedName("id")
    private String id;

    @SerializedName("usuario_id")
    private String usuarioId;

    @SerializedName("tipo_infraccion")
    private String tipoInfraccion; // leve, grave, critica

    @SerializedName("motivo")
    private String motivo;

    @SerializedName("dias_suspension")
    private int diasSuspension;

    @SerializedName("fecha_inicio")
    private String fechaInicio;

    @SerializedName("fecha_fin")
    private String fechaFin;

    @SerializedName("activo")
    private boolean activo;

    public InfraccionSancion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getTipoInfraccion() { return tipoInfraccion; }
    public void setTipoInfraccion(String tipoInfraccion) { this.tipoInfraccion = tipoInfraccion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public int getDiasSuspension() { return diasSuspension; }
    public void setDiasSuspension(int diasSuspension) { this.diasSuspension = diasSuspension; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
