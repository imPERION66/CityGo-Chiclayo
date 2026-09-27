package com.greencix.citygo.ui.colaborador;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.TramoRuta;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.RutasRepository;
import com.greencix.citygo.databinding.FragmentColaboradorMiRutaBinding;
import com.greencix.citygo.ui.adapters.TramosAdapter;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorMiRutaFragment extends Fragment {

    private FragmentColaboradorMiRutaBinding binding;
    private final GeoPoint chiclayoCentro = new GeoPoint(-6.7714, -79.8409);
    private List<TramoRuta> listaTramos = new ArrayList<>();

    public static ColaboradorMiRutaFragment newInstance() {
        return new ColaboradorMiRutaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() != null) {
            Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
        binding = FragmentColaboradorMiRutaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMap();
        setupListeners();
        cargarTramosSecuenciales();
    }

    private void setupMap() {
        binding.mapViewConductor.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapViewConductor.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        binding.mapViewConductor.setMultiTouchControls(true);
        binding.mapViewConductor.getController().setZoom(16.0);
        binding.mapViewConductor.getController().setCenter(chiclayoCentro);

        // Trazado de ruta en color ámbar / naranja operativo
        Polyline polyline = new Polyline();
        List<GeoPoint> puntos = new ArrayList<>();
        puntos.add(new GeoPoint(-6.7680, -79.8430)); // Tramo 1
        puntos.add(new GeoPoint(-6.7714, -79.8409)); // Tramo 2
        puntos.add(new GeoPoint(-6.7745, -79.8390)); // Tramo 3
        puntos.add(new GeoPoint(-6.7780, -79.8375)); // Tramo 4
        polyline.setPoints(puntos);
        polyline.getOutlinePaint().setColor(0xFFE65100);
        polyline.getOutlinePaint().setStrokeWidth(12f);
        binding.mapViewConductor.getOverlays().add(polyline);

        Marker truckMarker = new Marker(binding.mapViewConductor);
        truckMarker.setPosition(chiclayoCentro);
        truckMarker.setTitle("🚛 Tu Unidad (M4X-810)");
        truckMarker.setSnippet("Navegación giro a giro activa");
        if (getContext() != null) {
            truckMarker.setIcon(getContext().getDrawable(R.drawable.ic_truck_eco));
        }
        binding.mapViewConductor.getOverlays().add(truckMarker);
        binding.mapViewConductor.invalidate();
    }

    private void setupListeners() {
        // Botón Flotante "Solicitar Prórroga"
        binding.fabSolicitarProrroga.setOnClickListener(v -> {
            ColaboradorProrrogaBottomSheet.newInstance("turno-01")
                    .show(getParentFragmentManager(), "prorroga_modal");
        });
    }

    private void cargarTramosSecuenciales() {
        listaTramos = new ArrayList<>();
        listaTramos.add(new TramoRuta("1", "tramo-1", 1, "Av. José Balta (Cuadras 1 a 6)", 25, -6.7680, -79.8430));
        listaTramos.add(new TramoRuta("2", "tramo-1", 2, "Calle Elías Aguirre / San José", 20, -6.7714, -79.8409));
        listaTramos.add(new TramoRuta("3", "tramo-1", 3, "Av. Bolognesi (Cuadras 1 a 4)", 30, -6.7745, -79.8390));
        listaTramos.add(new TramoRuta("4", "tramo-1", 4, "Av. Salaverry / Santa Victoria", 25, -6.7780, -79.8375));

        if (getContext() == null) return;
        binding.rvTramosSecuenciales.setLayoutManager(new LinearLayoutManager(getContext()));
        TramosAdapter adapter = new TramosAdapter(getContext(), listaTramos, (tramo, totalCompletados) -> {
            binding.tvTramosCompletadosContador.setText(totalCompletados + " de " + listaTramos.size() + " completados");
            Toast.makeText(getContext(), "✅ Tramo completado: " + tramo.getNombreCalle(), Toast.LENGTH_SHORT).show();
        });
        binding.rvTramosSecuenciales.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) binding.mapViewConductor.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null) binding.mapViewConductor.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
