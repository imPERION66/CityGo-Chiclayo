package com.greencix.citygo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.greencix.citygo.data.model.Perfil;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.AuthRepository;
import com.greencix.citygo.ui.admin.DashboardAdminActivity;
import com.greencix.citygo.ui.ciudadano.DashboardCiudadanoActivity;
import com.greencix.citygo.ui.colaborador.DashboardColaboradorActivity;

public class TerminosUso extends AppCompatActivity {

    private CheckBox checkAceptar;
    private Button btnFinalizar;
    private String nombre = "";
    private String apellido = "";
    private String correo = "";
    private String clave = "";
    private String rol = "ciudadano";
    private String codigoActivacion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_uso);

        // Recuperación de estado ante recreaciones
        if (savedInstanceState != null) {
            nombre = savedInstanceState.getString("nombre", "");
            apellido = savedInstanceState.getString("apellido", "");
            correo = savedInstanceState.getString("correo", "");
            clave = savedInstanceState.getString("clave", "");
            rol = savedInstanceState.getString("rol", "ciudadano");
            codigoActivacion = savedInstanceState.getString("codigo_activacion", "");
        } else if (getIntent() != null) {
            nombre = getIntent().getStringExtra("nombre");
            apellido = getIntent().getStringExtra("apellido");
            correo = getIntent().getStringExtra("correo");
            clave = getIntent().getStringExtra("clave");
            rol = getIntent().getStringExtra("rol");
            codigoActivacion = getIntent().getStringExtra("codigo_activacion");
        }

        if (nombre == null) nombre = "";
        if (apellido == null) apellido = "";
        if (correo == null) correo = "";
        if (clave == null) clave = "";
        if (codigoActivacion == null) codigoActivacion = "";

        if (rol == null || rol.trim().isEmpty()) {
            rol = "ciudadano";
        } else {
            rol = rol.trim().toLowerCase();
        }

        Log.d("AUTH_DEBUG", "TerminosUso onCreate -> nombre: '" + nombre + "', apellido: '" + apellido + "', correo: '" + correo + "', rol: '" + rol + "', codigo: '" + codigoActivacion + "'");

        checkAceptar = findViewById(R.id.checkAceptarTerminos);
        btnFinalizar = findViewById(R.id.btnFinalizarRegistro);

        btnFinalizar.setOnClickListener(v -> {
            if (checkAceptar != null && checkAceptar.isChecked()) {
                ejecutarRegistroSupabase();
            } else {
                Toast.makeText(this, "Debe aceptar las cláusulas legales para continuar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nombre", nombre);
        outState.putString("apellido", apellido);
        outState.putString("correo", correo);
        outState.putString("clave", clave);
        outState.putString("rol", rol);
        outState.putString("codigo_activacion", codigoActivacion);
    }

    private void ejecutarRegistroSupabase() {
        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Datos de registro no encontrados. Vuelva a iniciar el proceso.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final String rolNormalizado = (rol != null && !rol.trim().isEmpty()) ? rol.trim().toLowerCase() : "ciudadano";
        final String codigoNormalizado = (codigoActivacion != null) ? codigoActivacion.trim().toUpperCase() : "";
        final String nombreFinal = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : "Usuario";
        final String apellidoFinal = (apellido != null && !apellido.trim().isEmpty()) ? apellido.trim() : "Nuevo";
        final String emailFinal = correo.trim();
        final String claveFinal = clave.trim();

        Log.d("AUTH_DEBUG", "Iniciando signUpWith -> nombre: '" + nombreFinal + "', apellido: '" + apellidoFinal + "', correo: '" + emailFinal + "', rol: '" + rolNormalizado + "', codigo: '" + codigoNormalizado + "'");

        btnFinalizar.setEnabled(false);
        btnFinalizar.setText("Creando cuenta...");

        try {
            AuthRepository.getInstance().register(
                    nombreFinal,
                    apellidoFinal,
                    emailFinal,
                    claveFinal,
                    rolNormalizado,
                    codigoNormalizado,
                    new Callback<Perfil>() {
                        @Override
                        public void onSuccess(Perfil perfil) {
                            btnFinalizar.setEnabled(true);
                            btnFinalizar.setText("ACEPTAR Y FINALIZAR");

                            Log.d("AUTH_DEBUG", "Registro exitoso en Supabase para: " + emailFinal + " con rol: " + rolNormalizado);
                            Toast.makeText(TerminosUso.this, "¡Cuenta creada exitosamente en City Go!", Toast.LENGTH_LONG).show();

                            Intent intent;
                            if ("administrador".equalsIgnoreCase(rolNormalizado) || "admin".equalsIgnoreCase(rolNormalizado)) {
                                intent = new Intent(TerminosUso.this, DashboardAdminActivity.class);
                            } else if ("colaborador".equalsIgnoreCase(rolNormalizado) || "conductor".equalsIgnoreCase(rolNormalizado)) {
                                intent = new Intent(TerminosUso.this, DashboardColaboradorActivity.class);
                            } else {
                                intent = new Intent(TerminosUso.this, DashboardCiudadanoActivity.class);
                            }

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            btnFinalizar.setEnabled(true);
                            btnFinalizar.setText("ACEPTAR Y FINALIZAR");
                            Log.e("AUTH_DEBUG", "Error al registrarse: " + errorMessage);
                            Toast.makeText(TerminosUso.this, "Error real: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        } catch (Exception e) {
            btnFinalizar.setEnabled(true);
            btnFinalizar.setText("ACEPTAR Y FINALIZAR");
            Log.e("AUTH_DEBUG", "Error al registrarse: ", e);
            Toast.makeText(TerminosUso.this, "Error real: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}