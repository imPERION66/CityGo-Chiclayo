package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class Camion {
    @SerializedName("id")
    private String id;

    @SerializedName("placa")
    private String placa;

    @SerializedName("modelo")
    private String modelo;

    @SerializedName("capacidad_toneladas")
    private double capacidadToneladas;

    @SerializedName("activo")
    private boolean activo;

    // Campos enriquecidos para vista administrativa
    private String conductorTitularNombre;
    private String conductorTitularDni;
    private String estadoRuta;
    private int velocidadKmH;

    public Camion() {}

    public Camion(String placa, String modelo, double capacidadToneladas, String conductorTitularNombre, String conductorTitularDni) {
        this.placa = placa;
        this.modelo = modelo;
        this.capacidadToneladas = capacidadToneladas;
        this.conductorTitularNombre = conductorTitularNombre;
        this.conductorTitularDni = conductorTitularDni;
        this.activo = true;
        this.estadoRuta = "EN RUTA";
        this.velocidadKmH = 24;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlaca() { return placa != null ? placa : ""; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo != null ? modelo : ""; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public double getCapacidadToneladas() { return capacidadToneladas; }
    public void setCapacidadToneladas(double capacidadToneladas) { this.capacidadToneladas = capacidadToneladas; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getConductorTitularNombre() { return conductorTitularNombre != null ? conductorTitularNombre : "Conductor Asignado"; }
    public void setConductorTitularNombre(String conductorTitularNombre) { this.conductorTitularNombre = conductorTitularNombre; }

    public String getConductorTitularDni() { return conductorTitularDni != null ? conductorTitularDni : "---"; }
    public void setConductorTitularDni(String conductorTitularDni) { this.conductorTitularDni = conductorTitularDni; }

    public String getEstadoRuta() { return estadoRuta != null ? estadoRuta : "EN RUTA"; }
    public void setEstadoRuta(String estadoRuta) { this.estadoRuta = estadoRuta; }

    public int getVelocidadKmH() { return velocidadKmH > 0 ? velocidadKmH : 22; }
    public void setVelocidadKmH(int velocidadKmH) { this.velocidadKmH = velocidadKmH; }
}
