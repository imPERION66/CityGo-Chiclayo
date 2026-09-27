package com.greencix.citygo.ui.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.greencix.citygo.R;
import com.greencix.citygo.databinding.ActivityDashboardAdminBinding;

public class DashboardAdminActivity extends AppCompatActivity {

    private ActivityDashboardAdminBinding binding;

    private AdminInicioFragment inicioFragment;
    private AdminFlotaFragment flotaFragment;
    private AdminEducaCMSFragment educaCmsFragment;
    private AdminPerfilFragment perfilFragment;
    private AdminReportesFragment reportesFragment;
    private AdminSancionesFragment sancionesFragment;
    private AdminCodigosFragment codigosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFragments();
        setupBottomNav();

        loadFragment(inicioFragment);
    }

    private void initFragments() {
        inicioFragment = AdminInicioFragment.newInstance();
        inicioFragment.setNavigationListener(new AdminInicioFragment.OnAdminNavigationListener() {
            @Override
            public void onNavigateToFlota() {
                binding.bottomNavAdmin.setSelectedItemId(R.id.nav_flota_admin);
            }

            @Override
            public void onNavigateToReportes() {
                loadFragment(reportesFragment);
            }

            @Override
            public void onNavigateToSanciones() {
                loadFragment(sancionesFragment);
            }

            @Override
            public void onNavigateToCodigos() {
                loadFragment(codigosFragment);
            }
        });

        flotaFragment = AdminFlotaFragment.newInstance();
        educaCmsFragment = AdminEducaCMSFragment.newInstance();
        perfilFragment = AdminPerfilFragment.newInstance();

        reportesFragment = AdminReportesFragment.newInstance();
        reportesFragment.setVolverListener(() -> binding.bottomNavAdmin.setSelectedItemId(R.id.nav_panel_admin));

        sancionesFragment = AdminSancionesFragment.newInstance();
        codigosFragment = AdminCodigosFragment.newInstance();
    }

    private void setupBottomNav() {
        binding.bottomNavAdmin.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_panel_admin) {
                loadFragment(inicioFragment);
                return true;
            } else if (itemId == R.id.nav_flota_admin) {
                loadFragment(flotaFragment);
                return true;
            } else if (itemId == R.id.nav_educa_admin) {
                loadFragment(educaCmsFragment);
                return true;
            } else if (itemId == R.id.nav_perfil_admin) {
                loadFragment(perfilFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment_container_admin, fragment);
        transaction.commit();
    }
}
