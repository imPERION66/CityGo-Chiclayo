package com.greencix.citygo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.ui.admin.DashboardAdminActivity;
import com.greencix.citygo.ui.ciudadano.DashboardCiudadanoActivity;
import com.greencix.citygo.ui.colaborador.DashboardColaboradorActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navegarSegunSesion();
            }
        }, 2500);
    }

    private void navegarSegunSesion() {
        if (SessionManager.getInstance().isLoggedIn()) {
            String rol = SessionManager.getInstance().getRol().toLowerCase();
            Intent intent;
            if ("administrador".equals(rol)) {
                intent = new Intent(SplashScreen.this, DashboardAdminActivity.class);
            } else if ("colaborador".equals(rol)) {
                intent = new Intent(SplashScreen.this, DashboardColaboradorActivity.class);
            } else {
                intent = new Intent(SplashScreen.this, DashboardCiudadanoActivity.class);
            }
            startActivity(intent);
        } else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        }
        finish();
    }
}