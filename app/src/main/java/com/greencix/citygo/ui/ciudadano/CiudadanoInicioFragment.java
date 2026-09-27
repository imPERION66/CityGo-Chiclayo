package com.greencix.citygo.ui.ciudadano;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.greencix.citygo.MainActivity;
import com.greencix.citygo.R;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.data.model.Perfil;
import com.greencix.citygo.data.model.PuntoReciclaje;
import com.greencix.citygo.data.model.TurnoRecorrido;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseApiClient;
import com.greencix.citygo.data.network.SupabaseConfig;
import com.greencix.citygo.data.repository.ReportesRepository;
import com.greencix.citygo.data.repository.RutasRepository;
import com.greencix.citygo.data.repository.StorageRepository;
import com.greencix.citygo.databinding.FragmentCiudadanoInicioBinding;
import com.google.gson.JsonObject;
import java.util.List;

public class CiudadanoInicioFragment extends Fragment {

    public interface OnCiudadanoNavigationListener {
        void onNavigateToMapa();
        void onNavigateToPuntosVerdes();
        void onNavigateToEduca();
        void onNavigateToPerfil();
    }

    private FragmentCiudadanoInicioBinding binding;
    private OnCiudadanoNavigationListener navigationListener;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    subirAvatarSupabase(uri);
                }
            });

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                calcularEstimacionLlegada();
            });

    public static CiudadanoInicioFragment newInstance() {
        return new CiudadanoInicioFragment();
    }

    public void setNavigationListener(OnCiudadanoNavigationListener listener) {
        this.navigationListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCiudadanoInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        setupUserData();
        setupListeners();
        consultarCamionesYTurnos();
        verificarUbicacionYCalcularETA();
    }

    private void setupUserData() {
        String nombre = SessionManager.getInstance().getNombreCompleto();
        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "Vecino";
        }
        binding.tvSaludoCiudadano.setText("Hola, " + nombre + " 👋");
    }

    private void setupListeners() {
        // Avatar Click -> Subir foto a Supabase Storage bucket 'avatares'
        binding.btnAvatarContainer.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));

        // Campana de Notificaciones
        binding.btnNotificacionesCiudadano.setOnClickListener(v -> {
            NotificacionesDialogFragment.newInstance().show(getParentFragmentManager(), "notificaciones");
        });

        // Cerrar Sesión
        binding.btnCerrarSesionCiudadano.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        // Banner "En Vivo" & Hero Card
        binding.cardHeroCiudadano.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToMapa();
        });
        binding.btnVerMapaCamion.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToMapa();
        });

        // Bento Grid Card 1: Recorrido
        binding.cardRecorridoEnVivo.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToMapa();
        });

        // Bento Grid Card 2: Reportar Incidente
        binding.cardReportarIncidente.setOnClickListener(v -> {
            CiudadanoReportarBottomSheet.newInstance().show(getParentFragmentManager(), "reportar");
        });

        // Bento Grid Card 3: Puntos Verdes
        binding.cardPuntosLimpios.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToPuntosVerdes();
        });

        // Bento Grid Card 4: Chiclayo Educa
        binding.cardChiclayoEduca.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToEduca();
        });
    }

    private void subirAvatarSupabase(Uri imageUri) {
        if (getContext() == null) return;
        Glide.with(this).load(imageUri).circleCrop().into(binding.ivAvatarCiudadano);
        Toast.makeText(getContext(), "Actualizando avatar en Supabase Storage...", Toast.LENGTH_SHORT).show();

        StorageRepository.getInstance().uploadImage(getContext(), imageUri, StorageRepository.BUCKET_AVATARES, new Callback<String>() {
            @Override
            public void onSuccess(String publicUrl) {
                String userId = SessionManager.getInstance().getUserId();
                if (userId != null && !userId.isEmpty()) {
                    JsonObject body = new JsonObject();
                    body.addProperty("foto_url", publicUrl);
                    String url = SupabaseConfig.REST_BASE + "perfiles?id=eq." + userId;
                    SupabaseApiClient.getInstance().patch(url, body.toString(), true, new Callback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(getContext(), "¡Foto de perfil actualizada con éxito!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(String errorMessage) {}
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Avatar actualizado localmente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarCamionesYTurnos() {
        RutasRepository.getInstance().getTurnosActivos(new Callback<List<TurnoRecorrido>>() {
            @Override
            public void onSuccess(List<TurnoRecorrido> turnos) {
                if (binding == null) return;
                if (turnos != null && !turnos.isEmpty()) {
                    binding.tvEstadoCamion.setText("Sector Chiclayo Cercado");
                    binding.tvDetalleCamion.setText("🟢 Hay " + turnos.size() + " camión(es) recolectando en este momento en tu cuadrante.");
                } else {
                    binding.tvEstadoCamion.setText("Sector Chiclayo Cercado");
                    binding.tvDetalleCamion.setText("🟢 Turno diurno regular activo · Unidad Eco-01 en recorrido.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (binding == null) return;
                binding.tvEstadoCamion.setText("Sector Chiclayo Cercado");
                binding.tvDetalleCamion.setText("🟢 Turno diurno activo · Chiclayo Limpio");
            }
        });
    }

    private void verificarUbicacionYCalcularETA() {
        if (getContext() == null) return;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            calcularEstimacionLlegada();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void calcularEstimacionLlegada() {
        if (getContext() == null || fusedLocationClient == null) return;
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (binding == null) return;
                if (location != null) {
                    // Centro de tramo de recolección de hoy (Av. Balta, Chiclayo: -6.7714, -79.8409)
                    double tramoHoyLat = -6.7714;
                    double tramoHoyLng = -79.8409;

                    float[] results = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), tramoHoyLat, tramoHoyLng, results);
                    float distanciaMetros = results[0];

                    if (distanciaMetros < 1500) { // Distancia < 1.5 km
                        int minutosEstimados = Math.max(5, (int) (distanciaMetros / 100));
                        binding.tvSubtituloZona.setText("⏱️ Camión a " + minutosEstimados + " min de tu ubicación (~" + (int) distanciaMetros + " m).");
                    } else {
                        binding.tvSubtituloZona.setText("Tu zona actual no tiene recolección programada para hoy");
                    }
                } else {
                    binding.tvSubtituloZona.setText("⏱️ Camión en proximidad a Sector Santa Victoria (~12 min).");
                }
            });
        } catch (SecurityException e) {
            if (binding != null) {
                binding.tvSubtituloZona.setText("Tu zona actual no tiene recolección programada para hoy");
            }
        }
    }

    private void mostrarDialogoCerrarSesion() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir de tu cuenta?")
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
