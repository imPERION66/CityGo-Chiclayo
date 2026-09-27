package com.greencix.citygo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greencix.citygo.data.model.EducaInsignia;
import com.greencix.citygo.databinding.ItemInsigniaBinding;
import java.util.List;

public class InsigniasAdapter extends RecyclerView.Adapter<InsigniasAdapter.ViewHolder> {

    private final Context context;
    private final List<EducaInsignia> list;

    public InsigniasAdapter(Context context, List<EducaInsignia> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInsigniaBinding binding = ItemInsigniaBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EducaInsignia item = list.get(position);
        holder.binding.tvInsigniaNombre.setText(item.getNombre());
        holder.binding.tvInsigniaDescripcion.setText(item.getDescripcion());
        holder.binding.tvInsigniaPuntosReq.setText(item.getPuntosRequeridos() + " pts");
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemInsigniaBinding binding;
        public ViewHolder(ItemInsigniaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
