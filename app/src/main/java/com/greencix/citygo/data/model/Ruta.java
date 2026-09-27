package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class Ruta {
    @SerializedName("id")
    private String id;

    @SerializedName("nombre_ruta")
    private String nombreRuta;

    @SerializedName("zona_chiclayo")
    private String zonaChiclayo;

    @SerializedName("duracion_estimada_minutos")
    private int duracionEstimadaMinutos;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("activo")
    private boolean activo;

    public Ruta() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreRuta() { return nombreRuta; }
    public void setNombreRuta(String nombreRuta) { this.nombreRuta = nombreRuta; }

    public String getZonaChiclayo() { return zonaChiclayo; }
    public void setZonaChiclayo(String zonaChiclayo) { this.zonaChiclayo = zonaChiclayo; }

    public int getDuracionEstimadaMinutos() { return duracionEstimadaMinutos; }
    public void setDuracionEstimadaMinutos(int duracionEstimadaMinutos) { this.duracionEstimadaMinutos = duracionEstimadaMinutos; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
