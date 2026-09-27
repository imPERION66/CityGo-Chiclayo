package com.greencix.citygo.ui.ciudadano;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.greencix.citygo.R;
import com.greencix.citygo.databinding.ActivityDashboardCiudadanoBinding;

public class DashboardCiudadanoActivity extends AppCompatActivity {

    private ActivityDashboardCiudadanoBinding binding;

    private CiudadanoInicioFragment inicioFragment;
    private CiudadanoMapaFragment mapaFragment;
    private CiudadanoNivelFragment nivelFragment;
    private CiudadanoPerfilFragment perfilFragment;
    private CiudadanoPuntosVerdesFragment puntosVerdesFragment;
    private CiudadanoEducaFragment educaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardCiudadanoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFragments();
        setupBottomNav();

        // Mostrar fragmento inicial
        loadFragment(inicioFragment);
    }

    private void initFragments() {
        inicioFragment = CiudadanoInicioFragment.newInstance();
        inicioFragment.setNavigationListener(new CiudadanoInicioFragment.OnCiudadanoNavigationListener() {
            @Override
            public void onNavigateToMapa() {
                binding.bottomNavCiudadano.setSelectedItemId(R.id.nav_mapa);
            }

            @Override
            public void onNavigateToPuntosVerdes() {
                loadFragment(puntosVerdesFragment);
            }

            @Override
            public void onNavigateToEduca() {
                loadFragment(educaFragment);
            }

            @Override
            public void onNavigateToPerfil() {
                binding.bottomNavCiudadano.setSelectedItemId(R.id.nav_perfil);
            }
        });

        mapaFragment = CiudadanoMapaFragment.newInstance();
        mapaFragment.setVolverListener(() -> binding.bottomNavCiudadano.setSelectedItemId(R.id.nav_inicio));

        nivelFragment = CiudadanoNivelFragment.newInstance();
        perfilFragment = CiudadanoPerfilFragment.newInstance();

        puntosVerdesFragment = CiudadanoPuntosVerdesFragment.newInstance();
        puntosVerdesFragment.setVolverListener(() -> binding.bottomNavCiudadano.setSelectedItemId(R.id.nav_inicio));

        educaFragment = CiudadanoEducaFragment.newInstance();
        educaFragment.setVolverListener(() -> binding.bottomNavCiudadano.setSelectedItemId(R.id.nav_inicio));
    }

    private void setupBottomNav() {
        binding.bottomNavCiudadano.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_inicio) {
                loadFragment(inicioFragment);
                return true;
            } else if (itemId == R.id.nav_mapa) {
                loadFragment(mapaFragment);
                return true;
            } else if (itemId == R.id.nav_nivel) {
                loadFragment(nivelFragment);
                return true;
            } else if (itemId == R.id.nav_perfil) {
                loadFragment(perfilFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment_container_ciudadano, fragment);
        transaction.commit();
    }
}
