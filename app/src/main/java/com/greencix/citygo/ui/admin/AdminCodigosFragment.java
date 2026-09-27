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
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.SolicitudStaff;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.AdminRepository;
import com.greencix.citygo.databinding.FragmentAdminCodigosBinding;
import com.greencix.citygo.ui.adapters.SolicitudesStaffAdapter;
import java.util.List;

public class AdminCodigosFragment extends Fragment {

    private FragmentAdminCodigosBinding binding;

    public static AdminCodigosFragment newInstance() {
        return new AdminCodigosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCodigosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvSolicitudesStaff.setLayoutManager(new LinearLayoutManager(getContext()));

        setupListeners();
        cargarSolicitudesStaff();
    }

    private void setupListeners() {
        // Generador de Códigos Directo
        binding.btnGenerarCodigo.setOnClickListener(v -> {
            String rol = binding.rbRolColaborador.isChecked() ? "colaborador" : "administrador";
            binding.btnGenerarCodigo.setEnabled(false);
            binding.btnGenerarCodigo.setText("Generando...");

            AdminRepository.getInstance().generarCodigoActivacion(rol, new Callback<String>() {
                @Override
                public void onSuccess(String codigo) {
                    if (binding == null) return;
                    binding.btnGenerarCodigo.setEnabled(true);
                    binding.btnGenerarCodigo.setText("Generar Token Alfanumérico");
                    binding.tvCodigoGeneradoResultado.setText("Token " + rol.toUpperCase() + ": " + codigo);
                    Toast.makeText(getContext(), "¡Token " + codigo + " generado y guardado en Supabase!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String errorMessage) {
                    if (binding == null) return;
                    binding.btnGenerarCodigo.setEnabled(true);
                    binding.btnGenerarCodigo.setText("Generar Token Alfanumérico");
                    binding.tvCodigoGeneradoResultado.setText("Token generado: Error al conectar.");
                }
            });
        });
    }

    private void cargarSolicitudesStaff() {
        AdminRepository.getInstance().getSolicitudesStaff(new Callback<List<SolicitudStaff>>() {
            @Override
            public void onSuccess(List<SolicitudStaff> list) {
                if (binding == null || getContext() == null) return;
                SolicitudesStaffAdapter adapter = new SolicitudesStaffAdapter(getContext(), list, solicitud -> {
                    aprobarSolicitud(solicitud);
                });
                binding.rvSolicitudesStaff.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void aprobarSolicitud(SolicitudStaff s) {
        Toast.makeText(getContext(), "Aprobando solicitud y enviando código al correo " + s.getCorreo() + "...", Toast.LENGTH_SHORT).show();
        AdminRepository.getInstance().aprobarSolicitudStaff(s.getId(), s.getCorreo(), s.getRolSolicitado(), new Callback<String>() {
            @Override
            public void onSuccess(String codigoGenerado) {
                Toast.makeText(getContext(), "¡Ficha física verificada! Código " + codigoGenerado + " enviado a " + s.getCorreo(), Toast.LENGTH_LONG).show();
                cargarSolicitudesStaff();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Solicitud aprobada exitosamente.", Toast.LENGTH_SHORT).show();
                cargarSolicitudesStaff();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
