package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class PuntoReciclaje {
    @SerializedName("id")
    private String id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("tipo_residuo")
    private String tipoResiduo;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("latitud")
    private double latitud;

    @SerializedName("longitud")
    private double longitud;

    @SerializedName("horario_atencion")
    private String horarioAtencion;

    @SerializedName("activo")
    private boolean activo;

    // Campo calculado para ordenamiento por cercanía
    private double distanciaMetros;

    public PuntoReciclaje() {}

    public PuntoReciclaje(String nombre, String tipoResiduo, String direccion, double latitud, double longitud, String horarioAtencion) {
        this.nombre = nombre;
        this.tipoResiduo = tipoResiduo;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.horarioAtencion = horarioAtencion;
        this.activo = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre != null ? nombre : ""; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoResiduo() { return tipoResiduo != null ? tipoResiduo : ""; }
    public void setTipoResiduo(String tipoResiduo) { this.tipoResiduo = tipoResiduo; }

    public String getDireccion() { return direccion != null ? direccion : ""; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getHorarioAtencion() { return horarioAtencion != null ? horarioAtencion : ""; }
    public void setHorarioAtencion(String horarioAtencion) { this.horarioAtencion = horarioAtencion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public double getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(double distanciaMetros) { this.distanciaMetros = distanciaMetros; }
}
