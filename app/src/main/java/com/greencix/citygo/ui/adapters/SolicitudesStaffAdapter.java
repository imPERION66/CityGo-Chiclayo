package com.greencix.citygo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.greencix.citygo.R;
import com.greencix.citygo.data.model.SolicitudStaff;
import com.greencix.citygo.databinding.ItemSolicitudStaffBinding;
import java.util.List;

public class SolicitudesStaffAdapter extends RecyclerView.Adapter<SolicitudesStaffAdapter.ViewHolder> {

    public interface OnAprobarStaffListener {
        void onAprobarStaff(SolicitudStaff solicitud);
    }

    private final Context context;
    private final List<SolicitudStaff> list;
    private final OnAprobarStaffListener listener;

    public SolicitudesStaffAdapter(Context context, List<SolicitudStaff> list, OnAprobarStaffListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSolicitudStaffBinding binding = ItemSolicitudStaffBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SolicitudStaff item = list.get(position);
        holder.binding.tvStaffNombre.setText(item.getNombreCompleto());
        holder.binding.tvStaffCorreo.setText(item.getCorreo());
        holder.binding.tvStaffDni.setText("🪪 DNI: " + item.getDni());
        holder.binding.tvStaffFichaMunicipal.setText("📑 Ficha Física Municipal: " + item.getCodigoFichaMunicipal());
        holder.binding.tvStaffRolSolicitado.setText(item.getRolSolicitado().toUpperCase());

        if (item.getRolSolicitado().equalsIgnoreCase("administrador")) {
            holder.binding.tvStaffRolSolicitado.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.wine_light));
            holder.binding.tvStaffRolSolicitado.setTextColor(ContextCompat.getColor(context, R.color.wine_primary));
        } else {
            holder.binding.tvStaffRolSolicitado.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange_light));
            holder.binding.tvStaffRolSolicitado.setTextColor(ContextCompat.getColor(context, R.color.orange_dark));
        }

        if (item.getEstado().equalsIgnoreCase("aprobada")) {
            holder.binding.btnAprobarStaff.setText("✅ Aprobado (" + item.getCodigoActivacionGenerado() + ")");
            holder.binding.btnAprobarStaff.setEnabled(false);
            holder.binding.btnAprobarStaff.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green_light));
            holder.binding.btnAprobarStaff.setTextColor(ContextCompat.getColor(context, R.color.green_dark));
        } else {
            holder.binding.btnAprobarStaff.setText("✅ Aprobar y Enviar Código al Correo");
            holder.binding.btnAprobarStaff.setEnabled(true);
            holder.binding.btnAprobarStaff.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green_primary));
            holder.binding.btnAprobarStaff.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.binding.btnAprobarStaff.setOnClickListener(v -> {
            if (listener != null) listener.onAprobarStaff(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemSolicitudStaffBinding binding;
        public ViewHolder(ItemSolicitudStaffBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
