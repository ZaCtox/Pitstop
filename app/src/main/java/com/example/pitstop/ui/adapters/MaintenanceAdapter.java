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
 * Adapter para la lista de mantenimientos (vista de lista).
 * Muestra tipo, fecha, descripción, km y costo; avisa clics mediante listener.
 */
public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.ViewHolder> {
    private List<Maintenance> maintenances = new ArrayList<>();
    private OnMaintenanceClickListener listener;
    private int currentKm = 0;

    public interface OnMaintenanceClickListener {
        void onMaintenanceClick(Maintenance maintenance);
    }

    public MaintenanceAdapter(OnMaintenanceClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_maintenance, parent, false);
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
    
    // Actualiza el km actual para calcular información contextual
    public void updateCurrentKm(int currentKm) {
        this.currentKm = currentKm;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView maintenanceType;
        private TextView maintenanceDate;
        private TextView maintenanceDescription;
        private TextView maintenanceKm;
        private TextView maintenanceCost;
        private TextView nextServiceInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            maintenanceType = itemView.findViewById(R.id.maintenance_type);
            maintenanceDate = itemView.findViewById(R.id.maintenance_date);
            maintenanceDescription = itemView.findViewById(R.id.maintenance_description);
            maintenanceKm = itemView.findViewById(R.id.maintenance_km);
            maintenanceCost = itemView.findViewById(R.id.maintenance_cost);
            nextServiceInfo = itemView.findViewById(R.id.next_service_info);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Maintenance maintenance = maintenances.get(position);
                    System.out.println("DEBUG: Maintenance clicked - ID: " + maintenance.getId() + ", Type: " + maintenance.getType());
                    listener.onMaintenanceClick(maintenance);
                }
            });
        }

        public void bind(Maintenance maintenance) {
            maintenanceType.setText(maintenance.getType());
            
            // Formatear fecha dd/MM/yyyy
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            maintenanceDate.setText(dateFormat.format(new Date(maintenance.getDate())));
            
            maintenanceDescription.setText(maintenance.getDescription());
            
            // Formatear km
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            maintenanceKm.setText(formatter.format(maintenance.getExecutedKm()) + " km");
            
            // Formatear costo
            if (maintenance.getCost() != null) {
                maintenanceCost.setText("$" + String.format(Locale.getDefault(), "%.2f", maintenance.getCost()));
            } else {
                maintenanceCost.setText("N/A");
            }
            
            // Información sobre próximo servicio basada en km actual
            if (maintenance.getExecutedKm() > 0) {
                int nextServiceKm = maintenance.getNextServiceKm();
                int remaining = nextServiceKm - currentKm;
                if (remaining > 0) {
                    nextServiceInfo.setText("Próximo: " + formatter.format(nextServiceKm) + " km (Faltan " + formatter.format(remaining) + " km)");
                } else {
                    nextServiceInfo.setText("¡Vencido! (Próximo: " + formatter.format(nextServiceKm) + " km)");
                }
            } else {
                int targetKm = maintenance.getPeriodicityKm();
                int remaining = targetKm - currentKm;
                if (remaining > 0) {
                    nextServiceInfo.setText("Objetivo: " + formatter.format(targetKm) + " km (Faltan " + formatter.format(remaining) + " km)");
                } else {
                    nextServiceInfo.setText("¡Listo para hacer! (Objetivo: " + formatter.format(targetKm) + " km)");
                }
            }
        }
    }
}
