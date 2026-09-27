package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class TramoRuta {
    @SerializedName("id")
    private String id;

    @SerializedName("ruta_id")
    private String rutaId;

    @SerializedName("orden")
    private int orden;

    @SerializedName("nombre_calle")
    private String nombreCalle;

    @SerializedName("tiempo_estimado_minutos")
    private int tiempoEstimadoMinutos;

    @SerializedName("latitud")
    private double latitud;

    @SerializedName("longitud")
    private double longitud;

    public TramoRuta() {}

    public TramoRuta(String id, String rutaId, int orden, String nombreCalle, int tiempoEstimadoMinutos, double latitud, double longitud) {
        this.id = id;
        this.rutaId = rutaId;
        this.orden = orden;
        this.nombreCalle = nombreCalle;
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRutaId() { return rutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public String getNombreCalle() { return nombreCalle; }
    public void setNombreCalle(String nombreCalle) { this.nombreCalle = nombreCalle; }

    public int getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public void setTiempoEstimadoMinutos(int tiempoEstimadoMinutos) { this.tiempoEstimadoMinutos = tiempoEstimadoMinutos; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}
