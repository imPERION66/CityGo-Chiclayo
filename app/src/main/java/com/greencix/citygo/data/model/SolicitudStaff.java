package com.greencix.citygo.data.model;

import com.google.gson.annotations.SerializedName;

public class SolicitudStaff {
    @SerializedName("id")
    private String id;

    @SerializedName("nombre_completo")
    private String nombreCompleto;

    @SerializedName("correo")
    private String correo;

    @SerializedName("dni")
    private String dni;

    @SerializedName("codigo_ficha_municipal")
    private String codigoFichaMunicipal;

    @SerializedName("rol_solicitado")
    private String rolSolicitado; // colaborador, administrador

    @SerializedName("estado")
    private String estado; // pendiente, aprobada, rechazada

    @SerializedName("codigo_activacion_generado")
    private String codigoActivacionGenerado;

    @SerializedName("atendido_por")
    private String atendidoPor;

    @SerializedName("creado_en")
    private String creadoEn;

    public SolicitudStaff() {}

    public SolicitudStaff(String nombreCompleto, String correo, String dni, String codigoFichaMunicipal, String rolSolicitado) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.dni = dni;
        this.codigoFichaMunicipal = codigoFichaMunicipal;
        this.rolSolicitado = rolSolicitado;
        this.estado = "pendiente";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto != null ? nombreCompleto : ""; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo != null ? correo : ""; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDni() { return dni != null ? dni : ""; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCodigoFichaMunicipal() { return codigoFichaMunicipal != null ? codigoFichaMunicipal : ""; }
    public void setCodigoFichaMunicipal(String codigoFichaMunicipal) { this.codigoFichaMunicipal = codigoFichaMunicipal; }

    public String getRolSolicitado() { return rolSolicitado != null ? rolSolicitado : "colaborador"; }
    public void setRolSolicitado(String rolSolicitado) { this.rolSolicitado = rolSolicitado; }

    public String getEstado() { return estado != null ? estado : "pendiente"; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCodigoActivacionGenerado() { return codigoActivacionGenerado; }
    public void setCodigoActivacionGenerado(String codigoActivacionGenerado) { this.codigoActivacionGenerado = codigoActivacionGenerado; }

    public String getAtendidoPor() { return atendidoPor; }
    public void setAtendidoPor(String atendidoPor) { this.atendidoPor = atendidoPor; }

    public String getCreadoEn() { return creadoEn; }
    public void setCreadoEn(String creadoEn) { this.creadoEn = creadoEn; }
}
