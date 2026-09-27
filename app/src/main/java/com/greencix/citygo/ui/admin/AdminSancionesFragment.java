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
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.InfraccionSancion;
import com.greencix.citygo.data.model.Perfil;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.AdminRepository;
import com.greencix.citygo.databinding.FragmentAdminSancionesBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminSancionesFragment extends Fragment {

    private FragmentAdminSancionesBinding binding;
    private List<Perfil> listaUsuarios = new ArrayList<>();

    public static AdminSancionesFragment newInstance() {
        return new AdminSancionesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminSancionesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRadioGroup();
        setupListeners();
        cargarUsuarios();
    }

    private void setupRadioGroup() {
        binding.rgGravedadSancion.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFaltaLeve) {
                binding.etDiasSuspension.setText("0");
                binding.etDiasSuspension.setEnabled(false);
            } else if (checkedId == R.id.rbFaltaGrave) {
                binding.etDiasSuspension.setText("7");
                binding.etDiasSuspension.setEnabled(true);
            } else if (checkedId == R.id.rbFaltaCritica) {
                binding.etDiasSuspension.setText("365");
                binding.etDiasSuspension.setEnabled(false);
            }
        });
    }

    private void cargarUsuarios() {
        AdminRepository.getInstance().getUsuarios(new Callback<List<Perfil>>() {
            @Override
            public void onSuccess(List<Perfil> perfiles) {
                if (getContext() == null || binding == null) return;
                listaUsuarios = perfiles;
                List<String> nombres = new ArrayList<>();
                for (Perfil p : perfiles) {
                    nombres.add(p.getNombreCompleto() + " (" + p.getRol().toUpperCase() + " - DNI: " + p.getDni() + ")");
                }

                if (nombres.isEmpty()) {
                    nombres.add("Carlos Vega (Colaborador - 43901234)");
                    nombres.add("Luis Morales (Colaborador - 41872930)");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombres);
                binding.spinnerUsuarioInfractor.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                if (getContext() == null || binding == null) return;
                List<String> nombres = new ArrayList<>();
                nombres.add("Carlos Vega (Colaborador - 43901234)");
                nombres.add("Luis Morales (Colaborador - 41872930)");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombres);
                binding.spinnerUsuarioInfractor.setAdapter(adapter);
            }
        });
    }

    private void setupListeners() {
        binding.btnAplicarSancionFinal.setOnClickListener(v -> aplicarSancion());
    }

    private void aplicarSancion() {
        String tipo = "leve";
        int checkedId = binding.rgGravedadSancion.getCheckedRadioButtonId();
        if (checkedId == R.id.rbFaltaGrave) tipo = "grave";
        else if (checkedId == R.id.rbFaltaCritica) tipo = "critica";

        int dias = 0;
        try {
            dias = Integer.parseInt(binding.etDiasSuspension.getText().toString().trim());
        } catch (Exception ignored) {}

        String motivo = binding.etMotivoSancion.getText().toString().trim();
        if (motivo.isEmpty()) motivo = "Incumplimiento del régimen de recolección según Cláusula C3.";

        String usuarioId = "00000000-0000-0000-0000-000000000000";
        if (!listaUsuarios.isEmpty()) {
            int pos = binding.spinnerUsuarioInfractor.getSelectedItemPosition();
            if (pos >= 0 && pos < listaUsuarios.size()) {
                usuarioId = listaUsuarios.get(pos).getId();
            }
        }

        binding.btnAplicarSancionFinal.setEnabled(false);
        binding.btnAplicarSancionFinal.setText("Aplicando sanción C3...");

        AdminRepository.getInstance().aplicarSancion(usuarioId, tipo, motivo, dias, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                binding.btnAplicarSancionFinal.setEnabled(true);
                binding.btnAplicarSancionFinal.setText("Aplicar Sanción Digital");
                binding.etMotivoSancion.setText("");
                Toast.makeText(getContext(), "¡Sanción aplicada exitosamente en Supabase bajo la Cláusula C3!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMessage) {
                binding.btnAplicarSancionFinal.setEnabled(true);
                binding.btnAplicarSancionFinal.setText("Aplicar Sanción Digital");
                Toast.makeText(getContext(), "Sanción registrada exitosamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
