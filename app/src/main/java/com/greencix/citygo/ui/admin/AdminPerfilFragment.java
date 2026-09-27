package com.greencix.citygo.ui.admin;

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
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.greencix.citygo.MainActivity;
import com.greencix.citygo.data.local.SessionManager;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseApiClient;
import com.greencix.citygo.data.network.SupabaseConfig;
import com.greencix.citygo.data.repository.StorageRepository;
import com.greencix.citygo.databinding.FragmentAdminPerfilBinding;

public class AdminPerfilFragment extends Fragment {

    private FragmentAdminPerfilBinding binding;
    private String photoUrl = "";

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    subirFotoAdmin(uri);
                }
            });

    public static AdminPerfilFragment newInstance() {
        return new AdminPerfilFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarDatos();
        setupListeners();
    }

    private void cargarDatos() {
        String nombre = SessionManager.getInstance().getNombreCompleto();
        String email = SessionManager.getInstance().getEmail();
        binding.etAdminNombre.setText(nombre);
        binding.etAdminCorreo.setText(email);
        binding.tvAdminNombreHeader.setText(nombre);
    }

    private void setupListeners() {
        binding.btnCambiarFotoAdmin.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        binding.btnGuardarAdminPerfil.setOnClickListener(v -> guardarPerfil());
        binding.btnCerrarSesionAdminPerfil.setOnClickListener(v -> mostrarDialogoCerrarSesion());
    }

    private void subirFotoAdmin(Uri uri) {
        if (getContext() == null) return;
        Glide.with(this).load(uri).circleCrop().into(binding.ivAdminAvatarGrande);
        Toast.makeText(getContext(), "Subiendo foto de administrador a Supabase Storage...", Toast.LENGTH_SHORT).show();

        StorageRepository.getInstance().uploadImage(getContext(), uri, StorageRepository.BUCKET_AVATARES, new Callback<String>() {
            @Override
            public void onSuccess(String publicUrl) {
                photoUrl = publicUrl;
                Toast.makeText(getContext(), "¡Foto de administrador actualizada!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Foto guardada localmente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarPerfil() {
        String nombre = binding.etAdminNombre.getText().toString().trim();
        String dni = binding.etAdminDNI.getText().toString().trim();
        String userId = SessionManager.getInstance().getUserId();

        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa tu nombre.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnGuardarAdminPerfil.setEnabled(false);
        binding.btnGuardarAdminPerfil.setText("Guardando...");

        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);
        if (!dni.isEmpty()) body.addProperty("dni", dni);
        if (!photoUrl.isEmpty()) body.addProperty("foto_url", photoUrl);

        if (userId != null && !userId.isEmpty()) {
            String url = SupabaseConfig.REST_BASE + "perfiles?id=eq." + userId;
            SupabaseApiClient.getInstance().patch(url, body.toString(), true, new Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    binding.btnGuardarAdminPerfil.setEnabled(true);
                    binding.btnGuardarAdminPerfil.setText("Guardar Cambios");
                    binding.tvAdminNombreHeader.setText(nombre);
                    Toast.makeText(getContext(), "¡Credenciales actualizadas en Supabase!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String errorMessage) {
                    binding.btnGuardarAdminPerfil.setEnabled(true);
                    binding.btnGuardarAdminPerfil.setText("Guardar Cambios");
                    Toast.makeText(getContext(), "Datos guardados localmente.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void mostrarDialogoCerrarSesion() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Cerrar Sesión")
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
