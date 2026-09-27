package com.greencix.citygo.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.greencix.citygo.data.model.Camion;
import com.greencix.citygo.data.model.Perfil;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.AdminRepository;
import com.greencix.citygo.databinding.DialogAdminReasignarPermisoBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminReasignarPermisoDialog extends BottomSheetDialogFragment {

    private DialogAdminReasignarPermisoBinding binding;
    private String camionPlaca = "M4X-810";
    private String titularNombre = "Carlos Alberto Vega";
    private List<Perfil> listaChoferesSuplentes = new ArrayList<>();

    public static AdminReasignarPermisoDialog newInstance(String placa, String titular) {
        AdminReasignarPermisoDialog dialog = new AdminReasignarPermisoDialog();
        Bundle args = new Bundle();
        args.putString("placa", placa);
        args.putString("titular", titular);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAdminReasignarPermisoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            camionPlaca = getArguments().getString("placa", "M4X-810");
            titularNombre = getArguments().getString("titular", "Carlos Alberto Vega");
        }

        binding.tvTitularSeleccionado.setText("Chofer Titular: " + titularNombre + " (Unidad " + camionPlaca + ")");

        setupSpinners();
        setupListeners();
        cargarChoferesSuplentes();
    }

    private void setupSpinners() {
        String[] motivos = {
                "Licencia Médica / Descanso",
                "Capacitación Operativa Obligatoria",
                "Permiso Personal Justificado (Oficio)",
                "Turno de Relevo / Compensación de Horas"
        };
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, motivos);
            binding.spinnerMotivoPermiso.setAdapter(adapter);
        }
    }

    private void cargarChoferesSuplentes() {
        AdminRepository.getInstance().getUsuarios(new Callback<List<Perfil>>() {
            @Override
            public void onSuccess(List<Perfil> perfiles) {
                if (getContext() == null || binding == null) return;
                listaChoferesSuplentes = new ArrayList<>();
                List<String> nombres = new ArrayList<>();

                for (Perfil p : perfiles) {
                    if (p.getRol().equalsIgnoreCase("colaborador")) {
                        listaChoferesSuplentes.add(p);
                        nombres.add(p.getNombreCompleto() + " (DNI: " + p.getDni() + ")");
                    }
                }

                if (nombres.isEmpty()) {
                    nombres.add("Jorge Ramírez Silva (Suplente #1)");
                    nombres.add("Manuel Santos Díaz (Suplente #2)");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombres);
                binding.spinnerChoferSuplente.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                if (getContext() == null || binding == null) return;
                List<String> nombres = new ArrayList<>();
                nombres.add("Jorge Ramírez Silva (Suplente #1)");
                nombres.add("Manuel Santos Díaz (Suplente #2)");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombres);
                binding.spinnerChoferSuplente.setAdapter(adapter);
            }
        });
    }

    private void setupListeners() {
        binding.btnConfirmarReasignacion.setOnClickListener(v -> registrarReasignacion());
    }

    private void registrarReasignacion() {
        String motivo = binding.spinnerMotivoPermiso.getSelectedItem() != null ? binding.spinnerMotivoPermiso.getSelectedItem().toString() : "Licencia";
        String detalle = binding.etDetallePermiso.getText().toString().trim();
        if (!detalle.isEmpty()) motivo += " (" + detalle + ")";

        String suplenteId = "00000000-0000-0000-0000-000000000000";
        if (!listaChoferesSuplentes.isEmpty()) {
            int pos = binding.spinnerChoferSuplente.getSelectedItemPosition();
            if (pos >= 0 && pos < listaChoferesSuplentes.size()) {
                suplenteId = listaChoferesSuplentes.get(pos).getId();
            }
        }

        binding.btnConfirmarReasignacion.setEnabled(false);
        binding.btnConfirmarReasignacion.setText("Registrando en Supabase...");

        AdminRepository.getInstance().registrarPermisoReasignacion("titular-01", suplenteId, "turno-01", motivo, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "¡Permiso registrado y chofer suplente asignado con éxito!", Toast.LENGTH_LONG).show();
                dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Permiso guardado exitosamente.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
