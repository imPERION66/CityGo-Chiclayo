package com.greencix.citygo.ui.colaborador;

import android.content.Intent;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.greencix.citygo.MainActivity;
import com.greencix.citygo.R;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseApiClient;
import com.greencix.citygo.data.network.SupabaseConfig;
import com.greencix.citygo.data.repository.StorageRepository;
import com.greencix.citygo.databinding.FragmentColaboradorInicioBinding;
import com.greencix.citygo.ui.ciudadano.NotificacionesDialogFragment;

public class ColaboradorInicioFragment extends Fragment {

    public interface OnColaboradorNavigationListener {
        void onNavigateToMiRuta();
        void onNavigateToEduca();
        void onNavigateToAlertas();
        void onNavigateToPerfil();
    }

    private FragmentColaboradorInicioBinding binding;
    private OnColaboradorNavigationListener navigationListener;
    private boolean enRetraso = false;

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    subirAvatarSupabase(uri);
                }
            });

    public static ColaboradorInicioFragment newInstance() {
        return new ColaboradorInicioFragment();
    }

    public void setNavigationListener(OnColaboradorNavigationListener listener) {
        this.navigationListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentColaboradorInicioBinding.inflate(inflater, container, false);
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
            nombre = "Conductor";
        }
        binding.tvSaludoColaborador.setText("Hola, " + nombre + " 🚛");
    }

    private void setupListeners() {
        // Avatar Click -> Upload to Supabase Storage bucket 'avatares'
        binding.btnAvatarColaboradorContainer.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));

        // Campana Notificaciones Operativas
        binding.btnNotificacionesColaborador.setOnClickListener(v -> {
            NotificacionesDialogFragment.newInstance().show(getParentFragmentManager(), "notificaciones_colab");
        });

        // Cerrar Sesión
        binding.btnCerrarSesionColaborador.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        // Banner Operativo - Toggle Estado A tiempo / Atrasado
        binding.btnEstadoTurno.setOnClickListener(v -> toggleEstadoRetraso());
        binding.cardHeroColaborador.setOnClickListener(v -> toggleEstadoRetraso());

        // Bento Grid Card 1: GPS en Vivo
        binding.cardNavegacionGps.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToMiRuta();
        });

        // Bento Grid Card 2: Tramos
        binding.cardTramosCalle.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToMiRuta();
        });

        // Bento Grid Card 3: Chiclayo Educa Operativo
        binding.cardCapacitacionEduca.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToEduca();
        });

        // Bento Grid Card 4: Alertas Ciudadanas en Sector
        binding.cardAlertasCiudadanas.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.onNavigateToAlertas();
        });
    }

    private void toggleEstadoRetraso() {
        enRetraso = !enRetraso;
        if (enRetraso) {
            binding.btnEstadoTurno.setText("⚠️ Retrasado (+20 min)");
            binding.btnEstadoTurno.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.red_dark));
            binding.tvDetalleAlertaColab.setText("⚠️ ALERTA DE TIEMPO: Retraso detectado en tramo actual. Puedes pulsar en 'Mi Ruta' y solicitar una prórroga para evitar sanciones C3.");
            Toast.makeText(getContext(), "Alerta de retraso activada. Considera pedir una prórroga.", Toast.LENGTH_SHORT).show();
        } else {
            binding.btnEstadoTurno.setText("🟢 A tiempo");
            binding.btnEstadoTurno.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.orange_dark));
            binding.tvDetalleAlertaColab.setText("🟢 Sector Chiclayo Cercado · Trazado regular sin congestión detectada.");
            Toast.makeText(getContext(), "Estado: Horario en curso y a tiempo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirAvatarSupabase(Uri imageUri) {
        if (getContext() == null) return;
        Glide.with(this).load(imageUri).circleCrop().into(binding.ivAvatarColaborador);
        Toast.makeText(getContext(), "Actualizando foto de conductor en Supabase Storage...", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(getContext(), "¡Foto de conductor actualizada!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(String errorMessage) {}
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void mostrarDialogoCerrarSesion() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir de tu turno y sesión operativa?")
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
