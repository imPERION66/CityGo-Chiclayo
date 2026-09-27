package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class TurnoRecorrido {
    @SerializedName("id")
    private String id;

    @SerializedName("ruta_id")
    private String rutaId;

    @SerializedName("camion_id")
    private String camionId;

    @SerializedName("conductor_id")
    private String conductorId;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora_inicio_programada")
    private String horaInicioProgramada;

    @SerializedName("hora_fin_programada")
    private String horaFinProgramada;

    @SerializedName("estado")
    private String estado; // programado, en_curso, completado, retrasado, cancelado

    @SerializedName("latitud_actual")
    private Double latitudActual;

    @SerializedName("longitud_actual")
    private Double longitudActual;

    @SerializedName("minutos_retraso_estimado")
    private int minutosRetrasoEstimado;

    @SerializedName("ultima_actualizacion_gps")
    private String ultimaActualizacionGps;

    public TurnoRecorrido() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRutaId() { return rutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }

    public String getCamionId() { return camionId; }
    public void setCamionId(String camionId) { this.camionId = camionId; }

    public String getConductorId() { return conductorId; }
    public void setConductorId(String conductorId) { this.conductorId = conductorId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicioProgramada() { return horaInicioProgramada; }
    public void setHoraInicioProgramada(String horaInicioProgramada) { this.horaInicioProgramada = horaInicioProgramada; }

    public String getHoraFinProgramada() { return horaFinProgramada; }
    public void setHoraFinProgramada(String horaFinProgramada) { this.horaFinProgramada = horaFinProgramada; }

    public String getEstado() { return estado != null ? estado : "programado"; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getLatitudActual() { return latitudActual; }
    public void setLatitudActual(Double latitudActual) { this.latitudActual = latitudActual; }

    public Double getLongitudActual() { return longitudActual; }
    public void setLongitudActual(Double longitudActual) { this.longitudActual = longitudActual; }

    public int getMinutosRetrasoEstimado() { return minutosRetrasoEstimado; }
    public void setMinutosRetrasoEstimado(int minutosRetrasoEstimado) { this.minutosRetrasoEstimado = minutosRetrasoEstimado; }

    public String getUltimaActualizacionGps() { return ultimaActualizacionGps; }
    public void setUltimaActualizacionGps(String ultimaActualizacionGps) { this.ultimaActualizacionGps = ultimaActualizacionGps; }
}
