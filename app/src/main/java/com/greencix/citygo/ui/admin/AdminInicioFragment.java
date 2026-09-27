package com.greencix.citygo.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.greencix.citygo.MainActivity;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.databinding.FragmentAdminInicioBinding;
import com.greencix.citygo.ui.ciudadano.NotificacionesDialogFragment;

public class AdminInicioFragment extends Fragment {

    public interface OnAdminNavigationListener {
        void onNavigateToFlota();
        void onNavigateToReportes();
        void onNavigateToSanciones();
        void onNavigateToCodigos();
    }

    private FragmentAdminInicioBinding binding;
    private OnAdminNavigationListener navigationListener;

    public static AdminInicioFragment newInstance() {
        return new AdminInicioFragment();
    }

    public void setNavigationListener(OnAdminNavigationListener listener) {
        this.navigationListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUserData();
        setupListeners();
    }

    private void setupUserData() {
        String nombre = SessionManager.getInstance().getNombreCompleto();
        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "Administrador";
        }
        binding.tvSaludoAdmin.setText("Hola, " + nombre + " 🛡️");
    }

    private void setupListeners() {
        binding.btnNotificacionesAdmin.setOnClickListener(v -> {
            NotificacionesDialogFragment.newInstance().show(getParentFragmentManager(), "notificaciones_admin");
        });

        binding.btnCerrarSesionAdmin.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        // Banner Hero
        binding.cardHeroAdmin.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToFlota();
        });

        // Bento Card 1: Flota en Vivo
        binding.cardFlotaAdmin.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToFlota();
        });

        // Bento Card 2: Reportes
        binding.cardReportesAdmin.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToReportes();
        });

        // Bento Card 3: Sanciones C3
        binding.cardSancionesC3.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToSanciones();
        });

        // Bento Card 4: Códigos de Activación & Staff
        binding.cardCodigosAcceso.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToCodigos();
        });
    }

    private void mostrarDialogoCerrarSesion() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Cerrar Sesión Administrativa")
                .setMessage("¿Estás seguro de que deseas salir del panel de administración?")
                .setPositiveButton("Cerrar Sesión", (dialog, which) -> {
                    SessionManager.getInstance().clearSession();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
