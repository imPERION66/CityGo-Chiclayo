package com.greencix.citygo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greencix.citygo.data.model.Camion;
import com.greencix.citygo.databinding.ItemCamionFlotaBinding;
import java.util.List;

public class CamionesFlotaAdapter extends RecyclerView.Adapter<CamionesFlotaAdapter.ViewHolder> {

    public interface OnCamionActionListener {
        void onReasignarClick(Camion camion);
    }

    private final Context context;
    private final List<Camion> list;
    private final OnCamionActionListener listener;

    public CamionesFlotaAdapter(Context context, List<Camion> list, OnCamionActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCamionFlotaBinding binding = ItemCamionFlotaBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Camion item = list.get(position);
        holder.binding.tvCamionPlaca.setText("Unidad " + item.getPlaca());
        holder.binding.tvCamionModelo.setText(item.getModelo() + " · Capacidad " + item.getCapacidadToneladas() + " Tn");
        holder.binding.tvConductorAsignado.setText("👤 Chofer: " + item.getConductorTitularNombre() + " (DNI: " + item.getConductorTitularDni() + ")");
        holder.binding.tvTelemetriaDetalle.setText("⚡ Velocidad: " + item.getVelocidadKmH() + " km/h · Retraso: 0 min · Telemetría Activa");
        holder.binding.tvCamionEstadoChip.setText(item.getEstadoRuta());

        holder.binding.btnReasignarConductor.setOnClickListener(v -> {
            if (listener != null) listener.onReasignarClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCamionFlotaBinding binding;
        public ViewHolder(ItemCamionFlotaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
