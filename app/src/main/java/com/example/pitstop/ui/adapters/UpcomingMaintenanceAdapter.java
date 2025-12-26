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
 * Adapter para próximos mantenimientos (lista compacta en dashboard).
 * Calcula y muestra km restantes u objetivo basado en el km actual.
 */
public class UpcomingMaintenanceAdapter extends RecyclerView.Adapter<UpcomingMaintenanceAdapter.ViewHolder> {
    private List<Maintenance> maintenances = new ArrayList<>();
    private OnMaintenanceClickListener listener;
    private int currentKm = 0;

    public interface OnMaintenanceClickListener {
        void onMaintenanceClick(Maintenance maintenance);
    }

    public UpcomingMaintenanceAdapter(OnMaintenanceClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_upcoming_maintenance, parent, false);
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
    
    public void updateCurrentKm(int currentKm) {
        this.currentKm = currentKm;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView maintenanceType;
        private TextView remainingKm;
        private TextView nextServiceKm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            maintenanceType = itemView.findViewById(R.id.maintenance_type);
            remainingKm = itemView.findViewById(R.id.remaining_km);
            nextServiceKm = itemView.findViewById(R.id.next_service_km);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMaintenanceClick(maintenances.get(position));
                }
            });
        }

        public void bind(Maintenance maintenance) {
            maintenanceType.setText(maintenance.getType());
            
            // Calcular kilómetros restantes usando el kilometraje actual
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            
            // Si el mantenimiento ya se ejecutó (executedKm > 0), calcular el próximo servicio
            if (maintenance.getExecutedKm() > 0) {
                int nextServiceKmValue = maintenance.getNextServiceKm();
                int remaining = nextServiceKmValue - currentKm;
                
                if (remaining > 0) {
                    remainingKm.setText("Faltan " + formatter.format(remaining) + " km");
                } else {
                    remainingKm.setText("¡Vencido!");
                }
                
                this.nextServiceKm.setText("Próximo: " + formatter.format(nextServiceKmValue) + " km");
            } else {
                // Mantenimiento futuro - usar el kilometraje de periodicidad
                int targetKm = maintenance.getPeriodicityKm();
                int remaining = targetKm - currentKm;
                
                if (remaining > 0) {
                    remainingKm.setText("Faltan " + formatter.format(remaining) + " km");
                } else {
                    remainingKm.setText("¡Listo para hacer!");
                }
                
                this.nextServiceKm.setText("Objetivo: " + formatter.format(targetKm) + " km");
            }
        }
    }
}
