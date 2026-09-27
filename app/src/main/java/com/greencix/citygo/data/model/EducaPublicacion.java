package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class EducaPublicacion {
    @SerializedName("id")
    private String id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("tipo")
    private String tipo; // aviso, guia_reciclaje, video, charla_vivo

    @SerializedName("multimedia_url")
    private String multimediaUrl;

    @SerializedName("enlace_reunion")
    private String enlaceReunion;

    @SerializedName("programado_para")
    private String programadoPara;

    @SerializedName("activo")
    private boolean activo;

    @SerializedName("creado_por")
    private String creadoPor;

    @SerializedName("creado_en")
    private String creadoEn;

    public EducaPublicacion() {}

    public EducaPublicacion(String id, String titulo, String descripcion, String tipo, String multimediaUrl) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.multimediaUrl = multimediaUrl;
        this.activo = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo != null ? titulo : ""; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion != null ? descripcion : ""; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo != null ? tipo : "aviso"; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMultimediaUrl() { return multimediaUrl != null ? multimediaUrl : ""; }
    public void setMultimediaUrl(String multimediaUrl) { this.multimediaUrl = multimediaUrl; }

    public String getEnlaceReunion() { return enlaceReunion != null ? enlaceReunion : ""; }
    public void setEnlaceReunion(String enlaceReunion) { this.enlaceReunion = enlaceReunion; }

    public String getProgramadoPara() { return programadoPara; }
    public void setProgramadoPara(String programadoPara) { this.programadoPara = programadoPara; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

    public String getCreadoEn() { return creadoEn; }
    public void setCreadoEn(String creadoEn) { this.creadoEn = creadoEn; }
}
