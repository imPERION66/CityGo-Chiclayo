package com.greencix.citygo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greencix.citygo.data.model.PuntoReciclaje;
import com.greencix.citygo.databinding.ItemPuntoVerdeBinding;
import java.util.List;

public class PuntosVerdesAdapter extends RecyclerView.Adapter<PuntosVerdesAdapter.ViewHolder> {

    public interface OnPuntoClickListener {
        void onPuntoClick(PuntoReciclaje punto);
    }

    private final Context context;
    private final List<PuntoReciclaje> list;
    private final OnPuntoClickListener listener;

    public PuntosVerdesAdapter(Context context, List<PuntoReciclaje> list, OnPuntoClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPuntoVerdeBinding binding = ItemPuntoVerdeBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PuntoReciclaje item = list.get(position);
        holder.binding.tvPuntoNombre.setText(item.getNombre());
        holder.binding.tvPuntoTipoResiduo.setText("♻️ " + item.getTipoResiduo());
        holder.binding.tvPuntoDireccion.setText(item.getDireccion());
        holder.binding.tvPuntoHorario.setText(item.getHorarioAtencion() != null ? item.getHorarioAtencion() : "08:00 - 18:00");

        if (item.getDistanciaMetros() > 0) {
            if (item.getDistanciaMetros() >= 1000) {
                holder.binding.tvPuntoDistancia.setText(String.format("~%.1f km", item.getDistanciaMetros() / 1000.0));
            } else {
                holder.binding.tvPuntoDistancia.setText(String.format("~%.0f m", item.getDistanciaMetros()));
            }
        } else {
            holder.binding.tvPuntoDistancia.setText("Chiclayo");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPuntoClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemPuntoVerdeBinding binding;
        public ViewHolder(ItemPuntoVerdeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
