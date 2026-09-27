package com.greencix.citygo.ui.colaborador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.greencix.citygo.data.model.EducaPublicacion;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.EducaRepository;
import com.greencix.citygo.databinding.FragmentCiudadanoEducaBinding;
import com.greencix.citygo.ui.adapters.EducaPublicacionesAdapter;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorEducaFragment extends Fragment {

    public interface OnVolverEducaColabListener {
        void onVolverEducaColab();
    }

    private FragmentCiudadanoEducaBinding binding;
    private OnVolverEducaColabListener volverListener;

    public static ColaboradorEducaFragment newInstance() {
        return new ColaboradorEducaFragment();
    }

    public void setVolverListener(OnVolverEducaColabListener listener) {
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
        cargarContenidoCapacitacion();
    }

    private void setupListeners() {
        binding.btnVolverEduca.setOnClickListener(v -> {
            if (volverListener != null) volverListener.onVolverEducaColab();
        });
    }

    private void cargarContenidoCapacitacion() {
        List<EducaPublicacion> list = new ArrayList<>();
        list.add(new EducaPublicacion("op-1", "🧤 Protocolo: Hallazgo de Residuos Biocontaminados", "Uso obligatorio de guantes de nitrilo reforzados y reporte inmediato a supervisión ante residuos punzocortantes en vía pública.", "guia_reciclaje", ""));
        list.add(new EducaPublicacion("op-2", "🐶 Protocolo: Presencia de Fauna Callejera", "Procedimiento seguro para despejar animales domésticos antes de accionar la prensa hidráulica de la tolva.", "aviso", ""));
        list.add(new EducaPublicacion("op-3", "🦺 Charla de Seguridad y Salud en el Trabajo", "Prevención de riesgos ergonómicos y pausas activas durante la jornada de recolección en Chiclayo.", "charla_vivo", "https://meet.google.com/citygo-staff"));

        if (getContext() != null) {
            EducaPublicacionesAdapter adapter = new EducaPublicacionesAdapter(getContext(), list, null);
            binding.rvEducaPublicaciones.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
