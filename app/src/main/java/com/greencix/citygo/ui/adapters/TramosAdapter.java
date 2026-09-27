package com.greencix.citygo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.TramoRuta;
import com.greencix.citygo.databinding.ItemTramoCalleBinding;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TramosAdapter extends RecyclerView.Adapter<TramosAdapter.ViewHolder> {

    public interface OnTramoCompletadoListener {
        void onTramoCompletado(TramoRuta tramo, int totalCompletados);
    }

    private final Context context;
    private final List<TramoRuta> list;
    private final OnTramoCompletadoListener listener;
    private final Set<Integer> completados = new HashSet<>();

    public TramosAdapter(Context context, List<TramoRuta> list, OnTramoCompletadoListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTramoCalleBinding binding = ItemTramoCalleBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TramoRuta item = list.get(position);
        holder.binding.tvTramoOrden.setText(String.valueOf(item.getOrden() > 0 ? item.getOrden() : position + 1));
        holder.binding.tvTramoCalleNombre.setText(item.getNombreCalle());
        holder.binding.tvTramoTiempoEstimado.setText("⏱️ Estimado: " + item.getTiempoEstimadoMinutos() + " min");

        boolean estaCompletado = completados.contains(position);
        if (estaCompletado) {
            holder.binding.btnCompletarTramo.setText("✅ Concluido");
            holder.binding.btnCompletarTramo.setEnabled(false);
            holder.binding.tvTramoOrden.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green_light));
            holder.binding.tvTramoOrden.setTextColor(ContextCompat.getColor(context, R.color.green_dark));
        } else {
            holder.binding.btnCompletarTramo.setText("Completar");
            holder.binding.btnCompletarTramo.setEnabled(true);
            holder.binding.tvTramoOrden.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange_light));
            holder.binding.tvTramoOrden.setTextColor(ContextCompat.getColor(context, R.color.orange_dark));
        }

        holder.binding.btnCompletarTramo.setOnClickListener(v -> {
            completados.add(position);
            notifyItemChanged(position);
            if (listener != null) listener.onTramoCompletado(item, completados.size());
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTramoCalleBinding binding;
        public ViewHolder(ItemTramoCalleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
