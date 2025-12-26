package com.example.pitstop.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.model.MaintenanceType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter de tarjetas de mantenimiento para el dashboard.
 * Muestra color por tipo, km restantes y permite acciones (editar, completar, borrar).
 */
public class MaintenanceCardAdapter extends RecyclerView.Adapter<MaintenanceCardAdapter.ViewHolder> {
    private List<Maintenance> maintenances = new ArrayList<>();
    private OnMaintenanceActionListener listener;
    private int currentKm = 0;

    public interface OnMaintenanceActionListener {
        void onEditMaintenance(Maintenance maintenance);
        void onCompleteMaintenance(Maintenance maintenance);
        void onDeleteMaintenance(Maintenance maintenance);
    }

    public MaintenanceCardAdapter(OnMaintenanceActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_maintenance_card, parent, false);
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
        private View colorIndicator;
        private TextView maintenanceType;
        private TextView remainingKm;
        private TextView nextServiceKm;
        private TextView actionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
            maintenanceType = itemView.findViewById(R.id.maintenance_type);
            remainingKm = itemView.findViewById(R.id.remaining_km);
            nextServiceKm = itemView.findViewById(R.id.next_service_km);
            actionButton = itemView.findViewById(R.id.action_button);

            actionButton.setOnClickListener(v -> showPopupMenu(v, getAdapterPosition()));
        }

        public void bind(Maintenance maintenance) {
            maintenanceType.setText(maintenance.getType());

            // Obtener color del tipo de mantenimiento
            MaintenanceType type = MaintenanceType.fromString(maintenance.getType());
            colorIndicator.setBackgroundColor(type.getColor());

            // Calcular kilómetros restantes usando el kilometraje actual
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            
            if (maintenance.getExecutedKm() > 0) {
                int nextServiceKmValue = maintenance.getNextServiceKm();
                int remaining = nextServiceKmValue - currentKm;
                
                if (remaining > 0) {
                    remainingKm.setText("Faltan " + formatter.format(remaining) + " km");
                    remainingKm.setTextColor(itemView.getContext().getColor(android.R.color.black));
                } else {
                    remainingKm.setText("¡Vencido!");
                    remainingKm.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                }
                
                nextServiceKm.setText("Próximo: " + formatter.format(nextServiceKmValue) + " km");
            } else {
                int targetKm = maintenance.getPeriodicityKm();
                int remaining = targetKm - currentKm;
                
                if (remaining > 0) {
                    remainingKm.setText("Faltan " + formatter.format(remaining) + " km");
                    remainingKm.setTextColor(itemView.getContext().getColor(android.R.color.black));
                } else {
                    remainingKm.setText("¡Listo para hacer!");
                    remainingKm.setTextColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
                }
                
                nextServiceKm.setText("Objetivo: " + formatter.format(targetKm) + " km");
            }
        }

        private void showPopupMenu(View view, int position) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.maintenance_actions, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                Maintenance maintenance = maintenances.get(position);
                int itemId = item.getItemId();

                if (itemId == R.id.action_edit) {
                    listener.onEditMaintenance(maintenance);
                } else if (itemId == R.id.action_complete) {
                    listener.onCompleteMaintenance(maintenance);
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteMaintenance(maintenance);
                }
                return true;
            });

            popup.show();
        }
    }
}
