package com.greencix.citygo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.EducaPublicacion;
import com.greencix.citygo.databinding.ItemPublicacionEducaBinding;
import java.util.List;

public class EducaPublicacionesAdapter extends RecyclerView.Adapter<EducaPublicacionesAdapter.ViewHolder> {

    public interface OnEducaActionListener {
        void onLikeClick(EducaPublicacion publicacion);
        void onAsistirClick(EducaPublicacion publicacion);
    }

    private final Context context;
    private final List<EducaPublicacion> list;
    private final OnEducaActionListener listener;

    public EducaPublicacionesAdapter(Context context, List<EducaPublicacion> list, OnEducaActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPublicacionEducaBinding binding = ItemPublicacionEducaBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EducaPublicacion item = list.get(position);
        holder.binding.tvEducaTitulo.setText(item.getTitulo());
        holder.binding.tvEducaDescripcion.setText(item.getDescripcion());
        holder.binding.tvEducaFecha.setText(item.getCreadoEn() != null ? item.getCreadoEn() : "Chiclayo Limpio");

        String tipo = item.getTipo() != null ? item.getTipo().toUpperCase() : "AVISO";
        holder.binding.tvEducaTipoBadge.setText(tipo.replace("_", " "));

        if (tipo.contains("CHARLA") || tipo.contains("VIVO")) {
            holder.binding.tvEducaTipoBadge.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red_light));
            holder.binding.tvEducaTipoBadge.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
            holder.binding.btnAsistirCharla.setVisibility(View.VISIBLE);
        } else if (tipo.contains("VIDEO")) {
            holder.binding.tvEducaTipoBadge.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bento_lilac_bg));
            holder.binding.tvEducaTipoBadge.setTextColor(ContextCompat.getColor(context, R.color.bento_lilac_accent));
            holder.binding.btnAsistirCharla.setVisibility(View.GONE);
        } else if (tipo.contains("GUIA") || tipo.contains("RECICLAJE")) {
            holder.binding.tvEducaTipoBadge.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bento_mint_bg));
            holder.binding.tvEducaTipoBadge.setTextColor(ContextCompat.getColor(context, R.color.green_primary));
            holder.binding.btnAsistirCharla.setVisibility(View.GONE);
        } else {
            holder.binding.tvEducaTipoBadge.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bento_blue_bg));
            holder.binding.tvEducaTipoBadge.setTextColor(ContextCompat.getColor(context, R.color.bento_blue_accent));
            holder.binding.btnAsistirCharla.setVisibility(View.GONE);
        }

        holder.binding.btnLikeEduca.setOnClickListener(v -> {
            if (listener != null) listener.onLikeClick(item);
        });

        holder.binding.btnAsistirCharla.setOnClickListener(v -> {
            if (listener != null) listener.onAsistirClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemPublicacionEducaBinding binding;
        public ViewHolder(ItemPublicacionEducaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
