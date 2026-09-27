package com.greencix.citygo.ui.colaborador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.greencix.citygo.R;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.RutasRepository;
import com.greencix.citygo.databinding.BottomSheetColaboradorProrrogaBinding;

public class ColaboradorProrrogaBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetColaboradorProrrogaBinding binding;
    private String turnoId = "00000000-0000-0000-0000-000000000000";

    public static ColaboradorProrrogaBottomSheet newInstance(String turnoId) {
        ColaboradorProrrogaBottomSheet fragment = new ColaboradorProrrogaBottomSheet();
        Bundle args = new Bundle();
        args.putString("turno_id", turnoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetColaboradorProrrogaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            turnoId = getArguments().getString("turno_id", "00000000-0000-0000-0000-000000000000");
        }

        setupSpinner();
        setupListeners();
    }

    private void setupSpinner() {
        String[] motivos = {
                "Tráfico vehicular pesado / Congestión",
                "Falla mecánica leve en unidad compactadora",
                "Calle bloqueada por obras o feria",
                "Saturación de tolva / Descarga imprevista",
                "Incidente en vía pública"
        };
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, motivos);
            binding.spinnerMotivoProrroga.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        binding.btnEnviarProrrogaFinal.setOnClickListener(v -> enviarSolicitud());
    }

    private void enviarSolicitud() {
        int minutos = 15;
        int checkedId = binding.rgMinutosProrroga.getCheckedRadioButtonId();
        if (checkedId == R.id.rb30Min) minutos = 30;
        else if (checkedId == R.id.rb45Min) minutos = 45;
        else if (checkedId == R.id.rb60Min) minutos = 60;

        String motivo = binding.spinnerMotivoProrroga.getSelectedItem() != null ? binding.spinnerMotivoProrroga.getSelectedItem().toString() : "Tráfico pesado";
        String detalle = binding.etDetalleProrroga.getText().toString().trim();
        if (!detalle.isEmpty()) {
            motivo += " - " + detalle;
        }

        binding.btnEnviarProrrogaFinal.setEnabled(false);
        binding.btnEnviarProrrogaFinal.setText("Enviando solicitud...");

        RutasRepository.getInstance().solicitarExtensionTiempo(turnoId, minutos, motivo, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getContext(), "¡Solicitud de prórroga enviada a la central de supervisión!", Toast.LENGTH_LONG).show();
                dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "¡Solicitud de prórroga registrada exitosamente!", Toast.LENGTH_SHORT).show();
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
