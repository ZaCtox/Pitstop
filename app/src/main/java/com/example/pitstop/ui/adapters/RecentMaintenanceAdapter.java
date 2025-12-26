package com.example.pitstop.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Maintenance;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para mantenimientos recientes.
 * Muestra tipo, fecha, km ejecutados y costo; emite clics al listener.
 */
public class RecentMaintenanceAdapter extends RecyclerView.Adapter<RecentMaintenanceAdapter.ViewHolder> {
    private List<Maintenance> maintenances = new ArrayList<>();
    private OnMaintenanceClickListener listener;

    public interface OnMaintenanceClickListener {
        void onMaintenanceClick(Maintenance maintenance);
    }

    public RecentMaintenanceAdapter(OnMaintenanceClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_recent_maintenance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Maintenance maintenance = maintenances.get(position);
        holder.bind(maintenance);
    }

    @Override
    public int getItemCount() {
        return maintenances.size();
    }

    public void updateMaintenances(List<Maintenance> newMaintenances) {
        this.maintenances = newMaintenances;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView maintenanceType;
        private TextView maintenanceDate;
        private TextView maintenanceKm;
        private TextView maintenanceCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            maintenanceType = itemView.findViewById(R.id.maintenance_type);
            maintenanceDate = itemView.findViewById(R.id.maintenance_date);
            maintenanceKm = itemView.findViewById(R.id.maintenance_km);
            maintenanceCost = itemView.findViewById(R.id.maintenance_cost);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMaintenanceClick(maintenances.get(position));
                }
            });
        }

        public void bind(Maintenance maintenance) {
            maintenanceType.setText(maintenance.getType());
            
            // Formatear fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            maintenanceDate.setText(dateFormat.format(new Date(maintenance.getDate())));
            
            // Formatear km
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            maintenanceKm.setText(formatter.format(maintenance.getExecutedKm()) + " km");
            
            // Formatear costo
            if (maintenance.getCost() != null) {
                maintenanceCost.setText("$" + String.format(Locale.getDefault(), "%.2f", maintenance.getCost()));
            } else {
                maintenanceCost.setText("N/A");
            }
        }
    }
}
