package com.greencix.citygo.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.ReporteCiudadano;
import com.greencix.citygo.databinding.ItemReporteCiudadanoBinding;
import java.util.List;

public class ReportesAdapter extends RecyclerView.Adapter<ReportesAdapter.ViewHolder> {

    public interface OnReporteClickListener {
        void onReporteClick(ReporteCiudadano reporte);
    }

    private final Context context;
    private final List<ReporteCiudadano> list;
    private final OnReporteClickListener listener;

    public ReportesAdapter(Context context, List<ReporteCiudadano> list, OnReporteClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReporteCiudadanoBinding binding = ItemReporteCiudadanoBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReporteCiudadano item = list.get(position);
        holder.binding.tvItemTipoIncidente.setText(item.getTipoIncidente());
        holder.binding.tvItemDescripcion.setText(item.getDescripcion());
        holder.binding.tvItemDireccion.setText(item.getDireccionReferencia() != null && !item.getDireccionReferencia().isEmpty() ? item.getDireccionReferencia() : "Chiclayo");
        holder.binding.tvItemFecha.setText(item.getCreadoEn() != null ? item.getCreadoEn() : "Reciente");

        String estado = item.getEstado() != null ? item.getEstado().toLowerCase() : "recibido";
        holder.binding.tvItemEstadoChip.setText(estado.toUpperCase());

        if (estado.contains("resuelto")) {
            holder.binding.tvItemEstadoChip.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green_light));
            holder.binding.tvItemEstadoChip.setTextColor(ContextCompat.getColor(context, R.color.green_dark));
        } else if (estado.contains("atencion") || estado.contains("camino")) {
            holder.binding.tvItemEstadoChip.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bento_blue_bg));
            holder.binding.tvItemEstadoChip.setTextColor(ContextCompat.getColor(context, R.color.bento_blue_accent));
        } else {
            holder.binding.tvItemEstadoChip.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bento_cream_bg));
            holder.binding.tvItemEstadoChip.setTextColor(ContextCompat.getColor(context, R.color.orange_dark));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onReporteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemReporteCiudadanoBinding binding;
        public ViewHolder(ItemReporteCiudadanoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
