package com.greencix.citygo.ui.ciudadano;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.TurnoRecorrido;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.RutasRepository;
import com.greencix.citygo.databinding.FragmentCiudadanoMapaBinding;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.util.ArrayList;
import java.util.List;

public class CiudadanoMapaFragment extends Fragment {

    public interface OnVolverInicioListener {
        void onVolverInicio();
    }

    private FragmentCiudadanoMapaBinding binding;
    private OnVolverInicioListener volverListener;
    private Marker camionMarker;
    private final GeoPoint chiclayoCentro = new GeoPoint(-6.7714, -79.8409);

    public static CiudadanoMapaFragment newInstance() {
        return new CiudadanoMapaFragment();
    }

    public void setVolverListener(OnVolverInicioListener listener) {
        this.volverListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() != null) {
            Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
        binding = FragmentCiudadanoMapaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMap();
        setupListeners();
        cargarRutaYCamion();
    }

    private void setupMap() {
        binding.mapViewCiudadano.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapViewCiudadano.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        binding.mapViewCiudadano.setMultiTouchControls(true);
        binding.mapViewCiudadano.getController().setZoom(16.0);
        binding.mapViewCiudadano.getController().setCenter(chiclayoCentro);

        // Trazar Ruta por calles principales de Chiclayo
        Polyline rutaPolyline = new Polyline();
        List<GeoPoint> puntosRuta = new ArrayList<>();
        puntosRuta.add(new GeoPoint(-6.7680, -79.8430)); // Av. Balta Norte
        puntosRuta.add(new GeoPoint(-6.7714, -79.8409)); // Plaza Principal / Aguirre
        puntosRuta.add(new GeoPoint(-6.7745, -79.8390)); // Bolognesi
        puntosRuta.add(new GeoPoint(-6.7780, -79.8375)); // Paseo Las Musas
        puntosRuta.add(new GeoPoint(-6.7820, -79.8395)); // Santa Victoria

        rutaPolyline.setPoints(puntosRuta);
        rutaPolyline.getOutlinePaint().setColor(0xFF2E7D32); // Verde Ecológico
        rutaPolyline.getOutlinePaint().setStrokeWidth(12f);
        binding.mapViewCiudadano.getOverlays().add(rutaPolyline);

        // Marcador del Camión Compactador
        camionMarker = new Marker(binding.mapViewCiudadano);
        camionMarker.setPosition(chiclayoCentro);
        camionMarker.setTitle("🚛 Camión Eco-01 (M4X-810)");
        camionMarker.setSnippet("Estado: En ruta activa · Velocidad: 22 km/h");
        camionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if (getContext() != null) {
            camionMarker.setIcon(getContext().getDrawable(R.drawable.ic_truck_eco));
        }
        binding.mapViewCiudadano.getOverlays().add(camionMarker);
        binding.mapViewCiudadano.invalidate();
    }

    private void setupListeners() {
        binding.btnVolverInicio.setOnClickListener(v -> {
            if (volverListener != null) volverListener.onVolverInicio();
        });

        binding.fabCentrarCamion.setOnClickListener(v -> {
            if (camionMarker != null) {
                binding.mapViewCiudadano.getController().animateTo(camionMarker.getPosition());
            }
        });

        binding.fabMiUbicacion.setOnClickListener(v -> {
            binding.mapViewCiudadano.getController().animateTo(chiclayoCentro);
        });
    }

    private void cargarRutaYCamion() {
        RutasRepository.getInstance().getTurnosActivos(new Callback<List<TurnoRecorrido>>() {
            @Override
            public void onSuccess(List<TurnoRecorrido> turnos) {
                if (binding == null) return;
                if (turnos != null && !turnos.isEmpty()) {
                    TurnoRecorrido t = turnos.get(0);
                    if (t.getLatitudActual() != null && t.getLongitudActual() != null) {
                        GeoPoint nuevaPos = new GeoPoint(t.getLatitudActual(), t.getLongitudActual());
                        camionMarker.setPosition(nuevaPos);
                        binding.mapViewCiudadano.getController().setCenter(nuevaPos);
                    }
                    binding.tvEstadoCamionMapa.setText("🟢 Turno Activo · " + t.getEstado().toUpperCase());
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) binding.mapViewCiudadano.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null) binding.mapViewCiudadano.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
