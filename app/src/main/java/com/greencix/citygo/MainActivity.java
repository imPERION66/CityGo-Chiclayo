package com.greencix.citygo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.greencix.citygo.data.model.Perfil;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.AuthRepository;
import com.greencix.citygo.ui.admin.DashboardAdminActivity;
import com.greencix.citygo.ui.ciudadano.DashboardCiudadanoActivity;
import com.greencix.citygo.ui.colaborador.DashboardColaboradorActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnIngresar;
    private TextView tvRegistrate, tvOlvidaste;
    private EditText etUsuario, etClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnIngresar = findViewById(R.id.btnIngresar);
        tvRegistrate = findViewById(R.id.tvRegistrate);
        tvOlvidaste = findViewById(R.id.tvOlvidaste);
        etUsuario = findViewById(R.id.etUsuario);
        etClave = findViewById(R.id.etClave);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesionSupabase();
            }
        });

        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });

        if (tvOlvidaste != null) {
            tvOlvidaste.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Para restablecer tu contraseña, contacta a soporte@citygo.pe o revisa tu correo registrado.", Toast.LENGTH_LONG).show();
            });
        }
    }

    private void iniciarSesionSupabase() {
        String correo = etUsuario.getText().toString().trim();
        String clave = etClave.getText().toString().trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");

        AuthRepository.getInstance().login(correo, clave, new Callback<Perfil>() {
            @Override
            public void onSuccess(Perfil perfil) {
                btnIngresar.setEnabled(true);
                btnIngresar.setText(getString(R.string.ingresar));

                if (perfil == null) {
                    Toast.makeText(MainActivity.this, "¡Bienvenido a City Go!", Toast.LENGTH_SHORT).show();
                    redirigirPorRol("ciudadano");
                    return;
                }

                String estado = perfil.getEstadoCuenta() != null ? perfil.getEstadoCuenta().toLowerCase() : "activo";

                // Validación de Régimen Disciplinario (Cláusula C3)
                if ("suspendido_temporal".equals(estado)) {
                    mostrarAlertaSancion("Acceso Temporalmente Inhabilitado",
                            "Su cuenta tiene una inhabilitación temporal vigente por infracción (según la Cláusula C3 del Contrato de Uso). Contacte a la administración para más detalles.");
                    return;
                } else if ("bloqueado_definitivo".equals(estado)) {
                    mostrarAlertaSancion("Cuenta Suspendida Definitivamente",
                            "Su cuenta ha sido revocada de forma definitiva e irrevocable por infracción crítica (Cláusula C3 de Términos y Condiciones).");
                    return;
                }

                Toast.makeText(MainActivity.this, "¡Bienvenido, " + perfil.getNombreCompleto() + "!", Toast.LENGTH_SHORT).show();
                redirigirPorRol(perfil.getRol());
            }

            @Override
            public void onError(String errorMessage) {
                btnIngresar.setEnabled(true);
                btnIngresar.setText(getString(R.string.ingresar));
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirigirPorRol(String rol) {
        if (rol == null) rol = "ciudadano";
        Intent intent;
        if ("administrador".equalsIgnoreCase(rol)) {
            intent = new Intent(MainActivity.this, DashboardAdminActivity.class);
        } else if ("colaborador".equalsIgnoreCase(rol)) {
            intent = new Intent(MainActivity.this, DashboardColaboradorActivity.class);
        } else {
            intent = new Intent(MainActivity.this, DashboardCiudadanoActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarAlertaSancion(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Entendido", null)
                .show();
    }
}