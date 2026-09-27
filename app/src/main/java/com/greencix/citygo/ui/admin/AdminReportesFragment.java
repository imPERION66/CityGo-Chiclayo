package com.greencix.citygo.ui.admin;

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
import com.google.android.material.tabs.TabLayout;
import com.greencix.citygo.data.model.ReporteCiudadano;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.ReportesRepository;
import com.greencix.citygo.databinding.FragmentAdminReportesBinding;
import com.greencix.citygo.ui.adapters.ReportesAdapter;
import java.util.ArrayList;
import java.util.List;

public class AdminReportesFragment extends Fragment {

    public interface OnVolverReportesAdminListener {
        void onVolverReportesAdmin();
    }

    private FragmentAdminReportesBinding binding;
    private OnVolverReportesAdminListener volverListener;
    private int selectedTab = 0;

    public static AdminReportesFragment newInstance() {
        return new AdminReportesFragment();
    }

    public void setVolverListener(OnVolverReportesAdminListener listener) {
        this.volverListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminReportesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvAdminReportes.setLayoutManager(new LinearLayoutManager(getContext()));

        setupListeners();
        cargarReportes();
    }

    private void setupListeners() {
        binding.btnVolverReportesAdmin.setOnClickListener(v -> {
            if (volverListener != null) volverListener.onVolverReportesAdmin();
        });

        binding.tabLayoutAdminReportes.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                cargarReportes();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void cargarReportes() {
        ReportesRepository.getInstance().getAllReportes(new Callback<List<ReporteCiudadano>>() {
            @Override
            public void onSuccess(List<ReporteCiudadano> list) {
                if (binding == null || getContext() == null) return;
                List<ReporteCiudadano> filtrados = new ArrayList<>(list);

                ReportesAdapter adapter = new ReportesAdapter(getContext(), filtrados, reporte -> {
                    mostrarOpcionesModeracion(reporte);
                });
                binding.rvAdminReportes.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void mostrarOpcionesModeracion(ReporteCiudadano r) {
        if (getContext() == null) return;
        String[] opciones = {"✅ Marcar como RESUELTO / ATENDIDO", "🚫 Descartar como REPORTE FALSO", "Cancelar"};

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("📋 Moderación de Incidente (" + r.getTipoIncidente() + ")")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        ReportesRepository.getInstance().cambiarEstadoReporte(r.getId(), "resuelto", new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(getContext(), "Reporte marcado como RESUELTO.", Toast.LENGTH_SHORT).show();
                                cargarReportes();
                            }
                            @Override public void onError(String errorMessage) {}
                        });
                    } else if (which == 1) {
                        ReportesRepository.getInstance().cambiarEstadoReporte(r.getId(), "descartado", new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(getContext(), "Reporte marcado como DESCARTADO.", Toast.LENGTH_SHORT).show();
                                cargarReportes();
                            }
                            @Override public void onError(String errorMessage) {}
                        });
                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
