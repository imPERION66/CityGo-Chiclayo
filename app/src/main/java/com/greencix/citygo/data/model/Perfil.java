package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class Perfil {
    @SerializedName("id")
    private String id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("correo")
    private String correo;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("dni")
    private String dni;

    @SerializedName("direccion_residencia")
    private String direccionResidencia;

    @SerializedName("foto_url")
    private String fotoUrl;

    @SerializedName("rol")
    private String rol; // ciudadano, colaborador, administrador

    @SerializedName("estado_cuenta")
    private String estadoCuenta; // activo, suspendido_temporal, bloqueado_definitivo

    @SerializedName("terminos_aceptados")
    private boolean terminosAceptados;

    @SerializedName("fecha_terminos_aceptados")
    private String fechaTerminosAceptados;

    public Perfil() {}

    public Perfil(String id, String nombre, String apellido, String correo, String rol, String estadoCuenta) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.rol = rol;
        this.estadoCuenta = estadoCuenta;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni != null ? dni : ""; }
    public void setDni(String dni) { this.dni = dni; }

    public String getDireccionResidencia() { return direccionResidencia != null ? direccionResidencia : ""; }
    public void setDireccionResidencia(String direccionResidencia) { this.direccionResidencia = direccionResidencia; }

    public String getFotoUrl() { return fotoUrl != null ? fotoUrl : ""; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getRol() { return rol != null ? rol : "ciudadano"; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstadoCuenta() { return estadoCuenta != null ? estadoCuenta : "activo"; }
    public void setEstadoCuenta(String estadoCuenta) { this.estadoCuenta = estadoCuenta; }

    public boolean isTerminosAceptados() { return terminosAceptados; }
    public void setTerminosAceptados(boolean terminosAceptados) { this.terminosAceptados = terminosAceptados; }

    public String getFechaTerminosAceptados() { return fechaTerminosAceptados; }
    public void setFechaTerminosAceptados(String fechaTerminosAceptados) { this.fechaTerminosAceptados = fechaTerminosAceptados; }

    public String getNombreCompleto() {
        return ((nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "")).trim();
    }
}

