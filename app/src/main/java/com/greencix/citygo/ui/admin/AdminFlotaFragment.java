package com.greencix.citygo.ui.admin;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.Camion;
import com.greencix.citygo.databinding.FragmentAdminFlotaBinding;
import com.greencix.citygo.ui.adapters.CamionesFlotaAdapter;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;
import java.util.List;

public class AdminFlotaFragment extends Fragment {

    private FragmentAdminFlotaBinding binding;
    private final GeoPoint chiclayoCentro = new GeoPoint(-6.7714, -79.8409);
    private List<Camion> listaCamiones = new ArrayList<>();

    public static AdminFlotaFragment newInstance() {
        return new AdminFlotaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() != null) {
            Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
        binding = FragmentAdminFlotaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMap();
        cargarFlota();
    }

    private void setupMap() {
        binding.mapViewAdminFlota.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapViewAdminFlota.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        binding.mapViewAdminFlota.setMultiTouchControls(true);
        binding.mapViewAdminFlota.getController().setZoom(15.0);
        binding.mapViewAdminFlota.getController().setCenter(chiclayoCentro);

        // Pintar Camiones en Mapa
        Marker m1 = new Marker(binding.mapViewAdminFlota);
        m1.setPosition(new GeoPoint(-6.7714, -79.8409));
        m1.setTitle("🚛 Unidad M4X-810");
        m1.setSnippet("Conductor: Carlos Vega · 24 km/h");
        if (getContext() != null) m1.setIcon(getContext().getDrawable(R.drawable.ic_truck_eco));
        binding.mapViewAdminFlota.getOverlays().add(m1);

        Marker m2 = new Marker(binding.mapViewAdminFlota);
        m2.setPosition(new GeoPoint(-6.7800, -79.8370));
        m2.setTitle("🚛 Unidad EGB-452");
        m2.setSnippet("Conductor: Luis Morales · 18 km/h");
        if (getContext() != null) m2.setIcon(getContext().getDrawable(R.drawable.ic_truck_eco));
        binding.mapViewAdminFlota.getOverlays().add(m2);

        binding.mapViewAdminFlota.invalidate();
    }

    private void cargarFlota() {
        listaCamiones = new ArrayList<>();
        listaCamiones.add(new Camion("M4X-810", "Volvo FMX Recolector 15m3", 8.5, "Carlos Alberto Vega", "43901234"));
        listaCamiones.add(new Camion("EGB-452", "Hino 500 Compactador 12m3", 6.0, "Luis Morales Ramos", "41872930"));
        listaCamiones.add(new Camion("CIX-109", "Mercedes-Benz Atego 1726", 7.5, "Héctor Díaz Campos", "45129844"));

        if (getContext() == null) return;
        binding.rvAdminCamiones.setLayoutManager(new LinearLayoutManager(getContext()));
        CamionesFlotaAdapter adapter = new CamionesFlotaAdapter(getContext(), listaCamiones, camion -> {
            AdminReasignarPermisoDialog.newInstance(camion.getPlaca(), camion.getConductorTitularNombre())
                    .show(getParentFragmentManager(), "reasignar_dialog");
        });
        binding.rvAdminCamiones.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) binding.mapViewAdminFlota.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null) binding.mapViewAdminFlota.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
