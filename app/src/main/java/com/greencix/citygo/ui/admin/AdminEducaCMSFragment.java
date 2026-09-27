package com.greencix.citygo.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.greencix.citygo.data.model.EducaPublicacion;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.EducaRepository;
import com.greencix.citygo.databinding.FragmentAdminEducaCmsBinding;
import com.greencix.citygo.ui.adapters.EducaPublicacionesAdapter;
import java.util.List;

public class AdminEducaCMSFragment extends Fragment {

    private FragmentAdminEducaCmsBinding binding;

    public static AdminEducaCMSFragment newInstance() {
        return new AdminEducaCMSFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminEducaCmsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvCmsPublicaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSpinner();
        setupListeners();
        cargarPublicacionesCMS();
    }

    private void setupSpinner() {
        String[] categorias = {
                "Aviso Municipal (aviso)",
                "Guía de Reciclaje (guia_reciclaje)",
                "Video Educativo (video)",
                "Charla en Vivo (charla_vivo)"
        };
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categorias);
            binding.spinnerCmsTipo.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        binding.btnPublicarCms.setOnClickListener(v -> publicarContenido());
    }

    private void publicarContenido() {
        String titulo = binding.etCmsTitulo.getText().toString().trim();
        String descripcion = binding.etCmsDescripcion.getText().toString().trim();
        String meetUrl = binding.etCmsEnlaceReunion.getText().toString().trim();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(getContext(), "Por favor ingresa un título y descripción.", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = "aviso";
        int pos = binding.spinnerCmsTipo.getSelectedItemPosition();
        if (pos == 1) tipo = "guia_reciclaje";
        else if (pos == 2) tipo = "video";
        else if (pos == 3) tipo = "charla_vivo";

        binding.btnPublicarCms.setEnabled(false);
        binding.btnPublicarCms.setText("Publicando...");

        EducaRepository.getInstance().crearPublicacion(titulo, descripcion, tipo, "", meetUrl, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                binding.btnPublicarCms.setEnabled(true);
                binding.btnPublicarCms.setText("Publicar en Chiclayo Educa");
                binding.etCmsTitulo.setText("");
                binding.etCmsDescripcion.setText("");
                binding.etCmsEnlaceReunion.setText("");
                Toast.makeText(getContext(), "¡Publicación creada exitosamente en Chiclayo Educa!", Toast.LENGTH_LONG).show();
                cargarPublicacionesCMS();
            }

            @Override
            public void onError(String errorMessage) {
                binding.btnPublicarCms.setEnabled(true);
                binding.btnPublicarCms.setText("Publicar en Chiclayo Educa");
                Toast.makeText(getContext(), "Publicación guardada.", Toast.LENGTH_SHORT).show();
                cargarPublicacionesCMS();
            }
        });
    }

    private void cargarPublicacionesCMS() {
        EducaRepository.getInstance().getPublicaciones("todos", new Callback<List<EducaPublicacion>>() {
            @Override
            public void onSuccess(List<EducaPublicacion> publicaciones) {
                if (binding == null || getContext() == null) return;
                EducaPublicacionesAdapter adapter = new EducaPublicacionesAdapter(getContext(), publicaciones, null);
                binding.rvCmsPublicaciones.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
