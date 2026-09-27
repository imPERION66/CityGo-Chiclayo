package com.greencix.citygo.ui.ciudadano;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.greencix.citygo.data.model.EducaInsignia;
import com.greencix.citygo.data.model.EducaParticipacion;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.EducaRepository;
import com.greencix.citygo.databinding.FragmentCiudadanoNivelBinding;
import com.greencix.citygo.ui.adapters.InsigniasAdapter;
import java.util.List;

public class CiudadanoNivelFragment extends Fragment {

    private FragmentCiudadanoNivelBinding binding;

    public static CiudadanoNivelFragment newInstance() {
        return new CiudadanoNivelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCiudadanoNivelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvInsigniasCiudadano.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarProgresoGamificacion();
        cargarInsignias();
    }

    private void cargarProgresoGamificacion() {
        EducaRepository.getInstance().getMiParticipacion(new Callback<EducaParticipacion>() {
            @Override
            public void onSuccess(EducaParticipacion p) {
                if (binding == null) return;
                int puntos = p.getPuntosAcumulados();
                binding.tvPuntosCiudadano.setText(puntos + " Puntos Acumulados");
                binding.tvLikesContador.setText(String.valueOf(p.getLikesInteracciones()));
                binding.tvCharlasContador.setText(String.valueOf(p.getCharlasAsistidas()));

                // Determinar Nivel Ciudadano
                if (puntos >= 300) {
                    binding.tvNivelCiudadanoNombre.setText("Héroe Ambiental Chiclayo 🏆");
                    binding.pbNivelProgreso.setMax(500);
                    binding.pbNivelProgreso.setProgress(puntos);
                    binding.tvProgresoDetalle.setText("¡Máximo nivel alcanzado! Eres un referente ecológico.");
                } else if (puntos >= 150) {
                    binding.tvNivelCiudadanoNombre.setText("Guardián Ecológico 🛡️");
                    binding.pbNivelProgreso.setMax(300);
                    binding.pbNivelProgreso.setProgress(puntos);
                    binding.tvProgresoDetalle.setText("Faltan " + (300 - puntos) + " pts para 'Héroe Ambiental Chiclayo'");
                } else {
                    binding.tvNivelCiudadanoNombre.setText("Semilla Verde 🌱");
                    binding.pbNivelProgreso.setMax(150);
                    binding.pbNivelProgreso.setProgress(puntos);
                    binding.tvProgresoDetalle.setText("Faltan " + (150 - puntos) + " pts para 'Guardián Ecológico'");
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void cargarInsignias() {
        EducaRepository.getInstance().getInsignias("ciudadano", new Callback<List<EducaInsignia>>() {
            @Override
            public void onSuccess(List<EducaInsignia> insignias) {
                if (binding == null || getContext() == null) return;
                InsigniasAdapter adapter = new InsigniasAdapter(getContext(), insignias);
                binding.rvInsigniasCiudadano.setAdapter(adapter);
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
