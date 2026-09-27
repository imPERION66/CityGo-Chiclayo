package com.greencix.citygo.ui.ciudadano;

import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.PuntoReciclaje;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.ReportesRepository;
import com.greencix.citygo.databinding.FragmentCiudadanoPuntosVerdesBinding;
import com.greencix.citygo.ui.adapters.PuntosVerdesAdapter;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import java.util.Collections;
import java.util.List;

public class CiudadanoPuntosVerdesFragment extends Fragment {

    public interface OnVolverPuntosListener {
        void onVolverPuntos();
    }

    private FragmentCiudadanoPuntosVerdesBinding binding;
    private OnVolverPuntosListener volverListener;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat = -6.7714;
    private double userLng = -79.8409;

    public static CiudadanoPuntosVerdesFragment newInstance() {
        return new CiudadanoPuntosVerdesFragment();
    }

    public void setVolverListener(OnVolverPuntosListener listener) {
        this.volverListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() != null) {
            Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
        binding = FragmentCiudadanoPuntosVerdesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        setupMap();
        setupListeners();
        obtenerUbicacionYCargarPuntos();
    }

    private void setupMap() {
        binding.mapViewPuntosVerdes.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapViewPuntosVerdes.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        binding.mapViewPuntosVerdes.setMultiTouchControls(true);
        binding.mapViewPuntosVerdes.getController().setZoom(15.0);
        binding.mapViewPuntosVerdes.getController().setCenter(new GeoPoint(userLat, userLng));
    }

    private void setupListeners() {
        binding.btnVolverPuntos.setOnClickListener(v -> {
            if (volverListener != null) volverListener.onVolverPuntos();
        });
    }

    private void obtenerUbicacionYCargarPuntos() {
        if (fusedLocationClient != null && getContext() != null) {
            try {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        userLat = location.getLatitude();
                        userLng = location.getLongitude();
                    }
                    cargarYPintarPuntos();
                });
            } catch (SecurityException e) {
                cargarYPintarPuntos();
            }
        } else {
            cargarYPintarPuntos();
        }
    }

    private void cargarYPintarPuntos() {
        ReportesRepository.getInstance().getPuntosReciclaje(new Callback<List<PuntoReciclaje>>() {
            @Override
            public void onSuccess(List<PuntoReciclaje> puntos) {
                if (binding == null || getContext() == null) return;
                binding.mapViewPuntosVerdes.getOverlays().clear();

                for (PuntoReciclaje p : puntos) {
                    float[] results = new float[1];
                    Location.distanceBetween(userLat, userLng, p.getLatitud(), p.getLongitud(), results);
                    p.setDistanciaMetros(results[0]);

                    // Marcador en Mapa
                    Marker marker = new Marker(binding.mapViewPuntosVerdes);
                    marker.setPosition(new GeoPoint(p.getLatitud(), p.getLongitud()));
                    marker.setTitle("♻️ " + p.getNombre());
                    marker.setSnippet(p.getTipoResiduo() + "\n" + p.getDireccion());
                    marker.setIcon(getContext().getDrawable(R.drawable.ic_recycle_points));
                    binding.mapViewPuntosVerdes.getOverlays().add(marker);
                }

                // Ordenar por cercanía métrica (menor a mayor)
                Collections.sort(puntos, (p1, p2) -> Double.compare(p1.getDistanciaMetros(), p2.getDistanciaMetros()));

                binding.rvPuntosVerdes.setLayoutManager(new LinearLayoutManager(getContext()));
                PuntosVerdesAdapter adapter = new PuntosVerdesAdapter(getContext(), puntos, punto -> {
                    binding.mapViewPuntosVerdes.getController().animateTo(new GeoPoint(punto.getLatitud(), punto.getLongitud()));
                });
                binding.rvPuntosVerdes.setAdapter(adapter);
                binding.mapViewPuntosVerdes.invalidate();
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) binding.mapViewPuntosVerdes.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null) binding.mapViewPuntosVerdes.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
