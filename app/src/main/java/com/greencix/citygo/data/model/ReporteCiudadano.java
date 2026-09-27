package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class ReporteCiudadano {
    @SerializedName("id")
    private String id;

    @SerializedName("ciudadano_id")
    private String ciudadanoId;

    @SerializedName("tipo_incidente")
    private String tipoIncidente;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("foto_url")
    private String fotoUrl;

    @SerializedName("latitud")
    private double latitud;

    @SerializedName("longitud")
    private double longitud;

    @SerializedName("direccion_referencia")
    private String direccionReferencia;

    @SerializedName("estado")
    private String estado; // recibido, en_atencion, resuelto, descartado

    @SerializedName("creado_en")
    private String creadoEn;

    public ReporteCiudadano() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCiudadanoId() { return ciudadanoId; }
    public void setCiudadanoId(String ciudadanoId) { this.ciudadanoId = ciudadanoId; }

    public String getTipoIncidente() { return tipoIncidente; }
    public void setTipoIncidente(String tipoIncidente) { this.tipoIncidente = tipoIncidente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getDireccionReferencia() { return direccionReferencia; }
    public void setDireccionReferencia(String direccionReferencia) { this.direccionReferencia = direccionReferencia; }

    public String getEstado() { return estado != null ? estado : "recibido"; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCreadoEn() { return creadoEn; }
    public void setCreadoEn(String creadoEn) { this.creadoEn = creadoEn; }
}
