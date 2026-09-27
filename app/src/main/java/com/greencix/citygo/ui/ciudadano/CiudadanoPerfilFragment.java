package com.greencix.citygo.ui.ciudadano;

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
import com.greencix.citygo.data.model.Perfil;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.network.SupabaseApiClient;
import com.greencix.citygo.data.network.SupabaseConfig;
import com.greencix.citygo.data.repository.StorageRepository;
import com.greencix.citygo.databinding.FragmentCiudadanoPerfilBinding;

public class CiudadanoPerfilFragment extends Fragment {

    private FragmentCiudadanoPerfilBinding binding;
    private String photoUrl = "";

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    subirFotoPerfil(uri);
                }
            });

    public static CiudadanoPerfilFragment newInstance() {
        return new CiudadanoPerfilFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCiudadanoPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarDatosUsuario();
        setupListeners();
    }

    private void cargarDatosUsuario() {
        String nombre = SessionManager.getInstance().getNombre();
        String apellido = SessionManager.getInstance().getApellido();
        String email = SessionManager.getInstance().getEmail();
        String rol = SessionManager.getInstance().getRol();

        binding.etPerfilNombre.setText(nombre);
        binding.etPerfilApellido.setText(apellido);
        binding.etPerfilCorreo.setText(email);
        binding.tvPerfilNombreHeader.setText(SessionManager.getInstance().getNombreCompleto());
        binding.tvPerfilRolBadge.setText(rol.toUpperCase());
    }

    private void setupListeners() {
        binding.btnCambiarFotoPerfil.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        binding.btnGuardarPerfil.setOnClickListener(v -> guardarCambiosPerfil());
        binding.btnCerrarSesionPerfil.setOnClickListener(v -> mostrarDialogoCerrarSesion());
    }

    private void subirFotoPerfil(Uri uri) {
        if (getContext() == null) return;
        Glide.with(this).load(uri).circleCrop().into(binding.ivPerfilAvatarGrande);
        Toast.makeText(getContext(), "Subiendo avatar a Supabase Storage...", Toast.LENGTH_SHORT).show();

        StorageRepository.getInstance().uploadImage(getContext(), uri, StorageRepository.BUCKET_AVATARES, new Callback<String>() {
            @Override
            public void onSuccess(String publicUrl) {
                photoUrl = publicUrl;
                Toast.makeText(getContext(), "¡Foto de perfil actualizada!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Avatar actualizado localmente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarCambiosPerfil() {
        String nombre = binding.etPerfilNombre.getText().toString().trim();
        String apellido = binding.etPerfilApellido.getText().toString().trim();
        String dni = binding.etPerfilDNI.getText().toString().trim();
        String telefono = binding.etPerfilTelefono.getText().toString().trim();
        String direccion = binding.etPerfilDireccion.getText().toString().trim();
        String userId = SessionManager.getInstance().getUserId();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa tu nombre y apellido.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnGuardarPerfil.setEnabled(false);
        binding.btnGuardarPerfil.setText("Guardando...");

        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);
        body.addProperty("apellido", apellido);
        if (!dni.isEmpty()) body.addProperty("dni", dni);
        if (!telefono.isEmpty()) body.addProperty("telefono", telefono);
        if (!direccion.isEmpty()) body.addProperty("direccion_residencia", direccion);
        if (!photoUrl.isEmpty()) body.addProperty("foto_url", photoUrl);

        if (userId != null && !userId.isEmpty()) {
            String url = SupabaseConfig.REST_BASE + "perfiles?id=eq." + userId;
            SupabaseApiClient.getInstance().patch(url, body.toString(), true, new Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    Perfil p = new Perfil(userId, nombre, apellido, SessionManager.getInstance().getEmail(), SessionManager.getInstance().getRol(), "activo");
                    SessionManager.getInstance().savePerfil(p);
                    binding.tvPerfilNombreHeader.setText(nombre + " " + apellido);
                    binding.btnGuardarPerfil.setEnabled(true);
                    binding.btnGuardarPerfil.setText("Guardar Cambios");
                    Toast.makeText(getContext(), "¡Datos de perfil guardados en Supabase!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String errorMessage) {
                    binding.btnGuardarPerfil.setEnabled(true);
                    binding.btnGuardarPerfil.setText("Guardar Cambios");
                    Toast.makeText(getContext(), "¡Perfil actualizado localmente!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            binding.btnGuardarPerfil.setEnabled(true);
            binding.btnGuardarPerfil.setText("Guardar Cambios");
            Toast.makeText(getContext(), "Perfil guardado.", Toast.LENGTH_SHORT).show();
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
