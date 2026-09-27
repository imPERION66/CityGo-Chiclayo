package com.greencix.citygo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SeleccionRol extends AppCompatActivity {

    private CardView cardCiudadano, cardColaborador, cardAdmin;
    private CheckBox checkCiudadano, checkColaborador, checkAdmin;
    private Button btnContinuar;

    private String rolSeleccionado = "ciudadano"; // Por defecto ciudadano
    private String nombre = "";
    private String apellido = "";
    private String correo = "";
    private String clave = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_rol);

        // Recuperación de datos con soporte de ciclo de vida
        if (savedInstanceState != null) {
            nombre = savedInstanceState.getString("nombre", "");
            apellido = savedInstanceState.getString("apellido", "");
            correo = savedInstanceState.getString("correo", "");
            clave = savedInstanceState.getString("clave", "");
            rolSeleccionado = savedInstanceState.getString("rolSeleccionado", "ciudadano");
        } else {
            if (getIntent() != null) {
                nombre = getIntent().getStringExtra("nombre");
                apellido = getIntent().getStringExtra("apellido");
                correo = getIntent().getStringExtra("correo");
                clave = getIntent().getStringExtra("clave");
            }
        }

        if (nombre == null) nombre = "";
        if (apellido == null) apellido = "";
        if (correo == null) correo = "";
        if (clave == null) clave = "";

        cardCiudadano = findViewById(R.id.cardCiudadano);
        cardColaborador = findViewById(R.id.cardColaborador);
        cardAdmin = findViewById(R.id.cardAdmin);

        checkCiudadano = findViewById(R.id.checkCiudadano);
        checkColaborador = findViewById(R.id.checkColaborador);
        checkAdmin = findViewById(R.id.checkAdmin);
        btnContinuar = findViewById(R.id.btnContinuarRol);

        // Interacción tanto en el CheckBox como en la Card completa para mejorar la usabilidad
        if (cardCiudadano != null) cardCiudadano.setOnClickListener(v -> actualizarSeleccion("ciudadano"));
        if (cardColaborador != null) cardColaborador.setOnClickListener(v -> actualizarSeleccion("colaborador"));
        if (cardAdmin != null) cardAdmin.setOnClickListener(v -> actualizarSeleccion("administrador"));

        if (checkCiudadano != null) checkCiudadano.setOnClickListener(v -> actualizarSeleccion("ciudadano"));
        if (checkColaborador != null) checkColaborador.setOnClickListener(v -> actualizarSeleccion("colaborador"));
        if (checkAdmin != null) checkAdmin.setOnClickListener(v -> actualizarSeleccion("administrador"));

        actualizarSeleccion(rolSeleccionado);

        btnContinuar.setOnClickListener(v -> {
            if (rolSeleccionado == null || rolSeleccionado.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar un perfil para continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("ciudadano".equalsIgnoreCase(rolSeleccionado)) {
                // Ciudadano va directo a Términos y Condiciones
                Intent intent = new Intent(SeleccionRol.this, TerminosUso.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido", apellido);
                intent.putExtra("correo", correo);
                intent.putExtra("clave", clave);
                intent.putExtra("rol", "ciudadano");
                intent.putExtra("codigo_activacion", "");
                startActivity(intent);
            } else {
                // Colaborador y Administrador requieren validación de código de activación empresarial
                Intent intent = new Intent(SeleccionRol.this, ValidacionCodigo.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido", apellido);
                intent.putExtra("correo", correo);
                intent.putExtra("clave", clave);
                intent.putExtra("rol", rolSeleccionado.toLowerCase());
                startActivity(intent);
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
        outState.putString("rolSeleccionado", rolSeleccionado);
    }

    private void actualizarSeleccion(String rol) {
        rolSeleccionado = rol != null ? rol.toLowerCase() : "ciudadano";
        if (checkCiudadano != null) checkCiudadano.setChecked("ciudadano".equals(rolSeleccionado));
        if (checkColaborador != null) checkColaborador.setChecked("colaborador".equals(rolSeleccionado));
        if (checkAdmin != null) checkAdmin.setChecked("administrador".equals(rolSeleccionado));
    }
}