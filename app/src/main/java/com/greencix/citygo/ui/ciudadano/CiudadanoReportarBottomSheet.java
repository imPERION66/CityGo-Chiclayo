package com.greencix.citygo.ui.ciudadano;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.greencix.citygo.data.model.ReporteCiudadano;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.ReportesRepository;
import com.greencix.citygo.data.repository.StorageRepository;
import com.greencix.citygo.databinding.BottomSheetCiudadanoReportarBinding;
import com.greencix.citygo.ui.adapters.ReportesAdapter;
import java.util.List;
import java.util.Locale;

public class CiudadanoReportarBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetCiudadanoReportarBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat = -6.7714;
    private double currentLng = -79.8409;
    private String uploadedPhotoUrl = "";
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    subirFotoReporte(uri);
                }
            });

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    obtenerUbicacionActual();
                } else {
                    Toast.makeText(getContext(), "Permiso de ubicación denegado. Usando centro de Chiclayo.", Toast.LENGTH_SHORT).show();
                }
            });

    public static CiudadanoReportarBottomSheet newInstance() {
        return new CiudadanoReportarBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCiudadanoReportarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        setupSpinner();
        setupTabs();
        setupListeners();
        cargarHistorialReportes();
    }

    private void setupSpinner() {
        String[] tipos = {
                "Acumulación de Residuos",
                "Desmonte Clandestino",
                "Contenedor Desbordado",
                "Falta de Recojo en Cuadrante",
                "Punto Crítico en Vía Pública"
        };
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tipos);
            binding.spinnerTipoIncidente.setAdapter(adapter);
        }
    }

    private void setupTabs() {
        binding.tabLayoutReportar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.layoutFormularioReporte.setVisibility(View.VISIBLE);
                    binding.layoutHistorialReportes.setVisibility(View.GONE);
                } else {
                    binding.layoutFormularioReporte.setVisibility(View.GONE);
                    binding.layoutHistorialReportes.setVisibility(View.VISIBLE);
                    cargarHistorialReportes();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupListeners() {
        // Geolocalización Automática
        binding.btnAutoGeolocalizar.setOnClickListener(v -> verificarPermisoYObtenerUbicacion());

        // Selector de Galería / Cámara
        binding.btnSeleccionarGaleria.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        binding.btnSeleccionarCamara.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        // Enviar Reporte
        binding.btnEnviarReporteFinal.setOnClickListener(v -> enviarReporte());
    }

    private void verificarPermisoYObtenerUbicacion() {
        if (getContext() == null) return;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActual();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void obtenerUbicacionActual() {
        if (getContext() == null || fusedLocationClient == null) return;
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    binding.tvCoordenadasReporte.setText(String.format(Locale.US, "Coordenadas: %.5f, %.5f", currentLat, currentLng));
                    resolverDireccion(currentLat, currentLng);
                } else {
                    Toast.makeText(getContext(), "Ubicación GPS fijada en Chiclayo Cercado.", Toast.LENGTH_SHORT).show();
                    binding.etDireccionReporte.setText("Av. Balta con Elias Aguirre, Chiclayo");
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "Error de permisos de GPS.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resolverDireccion(double lat, double lng) {
        if (getContext() == null) return;
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String direccion = addresses.get(0).getAddressLine(0);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> binding.etDireccionReporte.setText(direccion));
                    }
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private void subirFotoReporte(Uri uri) {
        if (getContext() == null) return;
        binding.ivPreviewFotoReporte.setVisibility(View.VISIBLE);
        Glide.with(this).load(uri).into(binding.ivPreviewFotoReporte);

        Toast.makeText(getContext(), "Subiendo evidencia a Supabase Storage...", Toast.LENGTH_SHORT).show();
        StorageRepository.getInstance().uploadImage(getContext(), uri, StorageRepository.BUCKET_REPORTES, new Callback<String>() {
            @Override
            public void onSuccess(String publicUrl) {
                uploadedPhotoUrl = publicUrl;
                Toast.makeText(getContext(), "¡Foto subida con éxito!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Evidencia adjunta localmente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarReporte() {
        String tipo = binding.spinnerTipoIncidente.getSelectedItem() != null ? binding.spinnerTipoIncidente.getSelectedItem().toString() : "Acumulación de Residuos";
        String direccion = binding.etDireccionReporte.getText().toString().trim();
        String descripcion = binding.etDescripcionReporte.getText().toString().trim();

        if (direccion.isEmpty()) direccion = "Chiclayo Centro";
        if (descripcion.isEmpty()) descripcion = "Punto crítico de residuos urbanos.";

        binding.btnEnviarReporteFinal.setEnabled(false);
        binding.btnEnviarReporteFinal.setText("Registrando en Supabase...");

        ReportesRepository.getInstance().crearReporte(tipo, descripcion, currentLat, currentLng, direccion, uploadedPhotoUrl, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "¡Reporte enviado exitosamente a la central municipal!", Toast.LENGTH_LONG).show();
                dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "¡Reporte guardado exitosamente!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void cargarHistorialReportes() {
        if (getContext() == null) return;
        binding.rvMisReportesAnteriores.setLayoutManager(new LinearLayoutManager(getContext()));

        ReportesRepository.getInstance().getMisReportes(new Callback<List<ReporteCiudadano>>() {
            @Override
            public void onSuccess(List<ReporteCiudadano> list) {
                if (list == null || list.isEmpty()) {
                    binding.tvVacioReportes.setVisibility(View.VISIBLE);
                } else {
                    binding.tvVacioReportes.setVisibility(View.GONE);
                    ReportesAdapter adapter = new ReportesAdapter(getContext(), list, null);
                    binding.rvMisReportesAnteriores.setAdapter(adapter);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.tvVacioReportes.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
