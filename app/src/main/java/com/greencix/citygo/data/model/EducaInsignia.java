package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class EducaInsignia {
    @SerializedName("id")
    private String id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("icono_url")
    private String iconoUrl;

    @SerializedName("puntos_requeridos")
    private int puntosRequeridos;

    @SerializedName("rol_aplicable")
    private String rolAplicable; // ciudadano, colaborador

    public EducaInsignia() {}

    public EducaInsignia(String id, String nombre, String descripcion, String iconoUrl, int puntosRequeridos, String rolAplicable) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.iconoUrl = iconoUrl;
        this.puntosRequeridos = puntosRequeridos;
        this.rolAplicable = rolAplicable;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre != null ? nombre : ""; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion != null ? descripcion : ""; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIconoUrl() { return iconoUrl != null ? iconoUrl : ""; }
    public void setIconoUrl(String iconoUrl) { this.iconoUrl = iconoUrl; }

    public int getPuntosRequeridos() { return puntosRequeridos; }
    public void setPuntosRequeridos(int puntosRequeridos) { this.puntosRequeridos = puntosRequeridos; }

    public String getRolAplicable() { return rolAplicable != null ? rolAplicable : "ciudadano"; }
    public void setRolAplicable(String rolAplicable) { this.rolAplicable = rolAplicable; }
}
