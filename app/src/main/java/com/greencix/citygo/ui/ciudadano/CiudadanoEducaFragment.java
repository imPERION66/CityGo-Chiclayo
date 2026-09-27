package com.greencix.citygo.ui.ciudadano;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.EducaPublicacion;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.EducaRepository;
import com.greencix.citygo.databinding.FragmentCiudadanoEducaBinding;
import com.greencix.citygo.ui.adapters.EducaPublicacionesAdapter;
import java.util.List;

public class CiudadanoEducaFragment extends Fragment {

    public interface OnVolverEducaListener {
        void onVolverEduca();
    }

    private FragmentCiudadanoEducaBinding binding;
    private OnVolverEducaListener volverListener;
    private String currentFilter = "todos";

    public static CiudadanoEducaFragment newInstance() {
        return new CiudadanoEducaFragment();
    }

    public void setVolverListener(OnVolverEducaListener listener) {
        this.volverListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCiudadanoEducaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvEducaPublicaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        setupListeners();
        cargarPublicaciones(currentFilter);
    }

    private void setupListeners() {
        binding.btnVolverEduca.setOnClickListener(v -> {
            if (volverListener != null) volverListener.onVolverEduca();
        });

        binding.chipGroupFiltrosEduca.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipAvisos)) {
                currentFilter = "aviso";
            } else if (checkedIds.contains(R.id.chipGuias)) {
                currentFilter = "guia_reciclaje";
            } else if (checkedIds.contains(R.id.chipVideos)) {
                currentFilter = "video";
            } else if (checkedIds.contains(R.id.chipCharlas)) {
                currentFilter = "charla_vivo";
            } else {
                currentFilter = "todos";
            }
            cargarPublicaciones(currentFilter);
        });
    }

    private void cargarPublicaciones(String filtro) {
        EducaRepository.getInstance().getPublicaciones(filtro, new Callback<List<EducaPublicacion>>() {
            @Override
            public void onSuccess(List<EducaPublicacion> publicaciones) {
                if (binding == null || getContext() == null) return;
                EducaPublicacionesAdapter adapter = new EducaPublicacionesAdapter(getContext(), publicaciones, new EducaPublicacionesAdapter.OnEducaActionListener() {
                    @Override
                    public void onLikeClick(EducaPublicacion publicacion) {
                        EducaRepository.getInstance().sumarPuntosInteraccion(10, false, new Callback<Integer>() {
                            @Override
                            public void onSuccess(Integer nuevoTotal) {
                                Toast.makeText(getContext(), "¡+10 Puntos Ecológicos! Total acumulado: " + nuevoTotal + " pts 🌱", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(String errorMessage) {}
                        });
                    }

                    @Override
                    public void onAsistirClick(EducaPublicacion publicacion) {
                        EducaRepository.getInstance().sumarPuntosInteraccion(25, true, new Callback<Integer>() {
                            @Override
                            public void onSuccess(Integer nuevoTotal) {
                                Toast.makeText(getContext(), "¡+25 Puntos! Asistencia registrada en Chiclayo Educa 🎙️", Toast.LENGTH_SHORT).show();
                                if (publicacion.getEnlaceReunion() != null && !publicacion.getEnlaceReunion().isEmpty()) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(publicacion.getEnlaceReunion()));
                                        startActivity(intent);
                                    } catch (Exception ignored) {}
                                }
                            }
                            @Override
                            public void onError(String errorMessage) {}
                        });
                    }
                });
                binding.rvEducaPublicaciones.setAdapter(adapter);
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
