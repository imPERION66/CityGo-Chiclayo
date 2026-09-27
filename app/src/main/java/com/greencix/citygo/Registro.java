package com.greencix.citygo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Registro extends AppCompatActivity {

    private Button btnRegistrarR;
    private TextView tvIrLogin;
    private EditText etNombre, etApellido, etCorreo, etClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        btnRegistrarR = findViewById(R.id.btnRegistrarR);
        tvIrLogin = findViewById(R.id.tvIrLoginR);
        etNombre = findViewById(R.id.etNombreR);
        etApellido = findViewById(R.id.etApellidoR);
        etCorreo = findViewById(R.id.etCorreoR);
        etClave = findViewById(R.id.etContrasenaR);

        btnRegistrarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String clave = etClave.getText().toString().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
                    Toast.makeText(Registro.this, "Por favor, complete todos los campos para continuar", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    Toast.makeText(Registro.this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (clave.length() < 6) {
                    Toast.makeText(Registro.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Registro.this, SeleccionRol.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido", apellido);
                intent.putExtra("correo", correo);
                intent.putExtra("clave", clave);
                startActivity(intent);
            }
        });

        tvIrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}