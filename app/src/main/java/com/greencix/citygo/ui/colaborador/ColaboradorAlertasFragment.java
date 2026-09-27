package com.greencix.citygo.ui.colaborador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.greencix.citygo.data.model.ReporteCiudadano;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.ReportesRepository;
import com.greencix.citygo.databinding.FragmentColaboradorAlertasBinding;
import com.greencix.citygo.ui.adapters.ReportesAdapter;
import java.util.List;

public class ColaboradorAlertasFragment extends Fragment {

    public interface OnVolverAlertasListener {
        void onVolverAlertas();
    }

    private FragmentColaboradorAlertasBinding binding;
    private OnVolverAlertasListener volverListener;

    public static ColaboradorAlertasFragment newInstance() {
        return new ColaboradorAlertasFragment();
    }

    public void setVolverListener(OnVolverAlertasListener listener) {
        this.volverListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentColaboradorAlertasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvAlertasSectorConductor.setLayoutManager(new LinearLayoutManager(getContext()));
        setupListeners();
        cargarAlertasSector();
    }

    private void setupListeners() {
        binding.btnVolverAlertasColab.setOnClickListener(v -> {
            if (volverListener != null) volverListener.onVolverAlertas();
        });
    }

    private void cargarAlertasSector() {
        ReportesRepository.getInstance().getMisReportes(new Callback<List<ReporteCiudadano>>() {
            @Override
            public void onSuccess(List<ReporteCiudadano> reportes) {
                if (binding == null || getContext() == null) return;
                ReportesAdapter adapter = new ReportesAdapter(getContext(), reportes, reporte -> {
                    mostrarDialogoAtenderAlerta(reporte);
                });
                binding.rvAlertasSectorConductor.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void mostrarDialogoAtenderAlerta(ReporteCiudadano r) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("🚨 Atender Alerta Ciudadana")
                .setMessage("Ubicación: " + r.getDireccionReferencia() + "\n\nDetalle: " + r.getDescripcion() + "\n\n¿Deseas marcar esta emergencia como 'En Atención por Cuadrilla'?")
                .setPositiveButton("Marcar En Atención", (dialog, which) -> {
                    ReportesRepository.getInstance().cambiarEstadoReporte(r.getId(), "en_atencion", new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "¡Alerta en atención registrada!", Toast.LENGTH_SHORT).show();
                            cargarAlertasSector();
                        }
                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(getContext(), "Estado actualizado.", Toast.LENGTH_SHORT).show();
                        }
                    });
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
