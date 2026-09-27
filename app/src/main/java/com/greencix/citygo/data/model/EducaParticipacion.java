package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class EducaParticipacion {
    @SerializedName("id")
    private String id;

    @SerializedName("usuario_id")
    private String usuarioId;

    @SerializedName("puntos_acumulados")
    private int puntosAcumulados;

    @SerializedName("charlas_asistidas")
    private int charlasAsistidas;

    @SerializedName("likes_interacciones")
    private int likesInteracciones;

    @SerializedName("insignia_actual_id")
    private String insigniaActualId;

    @SerializedName("actualizado_en")
    private String actualizadoEn;

    public EducaParticipacion() {}

    public EducaParticipacion(String usuarioId, int puntosAcumulados) {
        this.usuarioId = usuarioId;
        this.puntosAcumulados = puntosAcumulados;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public int getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }

    public int getCharlasAsistidas() { return charlasAsistidas; }
    public void setCharlasAsistidas(int charlasAsistidas) { this.charlasAsistidas = charlasAsistidas; }

    public int getLikesInteracciones() { return likesInteracciones; }
    public void setLikesInteracciones(int likesInteracciones) { this.likesInteracciones = likesInteracciones; }

    public String getInsigniaActualId() { return insigniaActualId; }
    public void setInsigniaActualId(String insigniaActualId) { this.insigniaActualId = insigniaActualId; }

    public String getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(String actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
