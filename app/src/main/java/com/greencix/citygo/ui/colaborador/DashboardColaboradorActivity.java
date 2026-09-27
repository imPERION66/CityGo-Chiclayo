package com.greencix.citygo.ui.colaborador;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.greencix.citygo.R;
import com.greencix.citygo.databinding.ActivityDashboardColaboradorBinding;

public class DashboardColaboradorActivity extends AppCompatActivity {

    private ActivityDashboardColaboradorBinding binding;

    private ColaboradorInicioFragment inicioFragment;
    private ColaboradorMiRutaFragment miRutaFragment;
    private ColaboradorNivelFragment nivelFragment;
    private ColaboradorPerfilFragment perfilFragment;
    private ColaboradorEducaFragment educaFragment;
    private ColaboradorAlertasFragment alertasFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardColaboradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFragments();
        setupBottomNav();

        loadFragment(inicioFragment);
    }

    private void initFragments() {
        inicioFragment = ColaboradorInicioFragment.newInstance();
        inicioFragment.setNavigationListener(new ColaboradorInicioFragment.OnColaboradorNavigationListener() {
            @Override
            public void onNavigateToMiRuta() {
                binding.bottomNavColaborador.setSelectedItemId(R.id.nav_ruta_colab);
            }

            @Override
            public void onNavigateToEduca() {
                loadFragment(educaFragment);
            }

            @Override
            public void onNavigateToAlertas() {
                loadFragment(alertasFragment);
            }

            @Override
            public void onNavigateToPerfil() {
                binding.bottomNavColaborador.setSelectedItemId(R.id.nav_perfil_colab);
            }
        });

        miRutaFragment = ColaboradorMiRutaFragment.newInstance();
        nivelFragment = ColaboradorNivelFragment.newInstance();
        perfilFragment = ColaboradorPerfilFragment.newInstance();

        educaFragment = ColaboradorEducaFragment.newInstance();
        educaFragment.setVolverListener(() -> binding.bottomNavColaborador.setSelectedItemId(R.id.nav_inicio_colab));

        alertasFragment = ColaboradorAlertasFragment.newInstance();
        alertasFragment.setVolverListener(() -> binding.bottomNavColaborador.setSelectedItemId(R.id.nav_inicio_colab));
    }

    private void setupBottomNav() {
        binding.bottomNavColaborador.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_inicio_colab) {
                loadFragment(inicioFragment);
                return true;
            } else if (itemId == R.id.nav_ruta_colab) {
                loadFragment(miRutaFragment);
                return true;
            } else if (itemId == R.id.nav_nivel_colab) {
                loadFragment(nivelFragment);
                return true;
            } else if (itemId == R.id.nav_perfil_colab) {
                loadFragment(perfilFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment_container_colaborador, fragment);
        transaction.commit();
    }
}
