package com.example.pitstop.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Vehicle;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para la lista de vehículos.
 * Maneja selección, edición y eliminación a través de un listener de acciones.
 */
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    private List<Vehicle> vehicles = new ArrayList<>();
    private OnVehicleActionListener listener;

    public interface OnVehicleActionListener {
        void onEditVehicle(Vehicle vehicle);
        void onSelectVehicle(Vehicle vehicle);
        void onDeleteVehicle(Vehicle vehicle);
    }

    public VehicleAdapter(OnVehicleActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        holder.bind(vehicle);
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public void updateVehicles(List<Vehicle> newVehicles) {
        this.vehicles = newVehicles != null ? newVehicles : new ArrayList<>();
        notifyDataSetChanged();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {
        private TextView vehicleName;
        private TextView vehicleDetails;
        private TextView currentKm;
        private ImageView vehicleIcon;
        private ImageView moreButton;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleName = itemView.findViewById(R.id.vehicle_name);
            vehicleDetails = itemView.findViewById(R.id.vehicle_details);
            currentKm = itemView.findViewById(R.id.current_km);
            vehicleIcon = itemView.findViewById(R.id.vehicle_icon);
            moreButton = itemView.findViewById(R.id.more_button);

            // Click en el item para seleccionar el vehículo
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSelectVehicle(vehicles.get(position));
                }
            });

            // Click en el botón de más opciones (editar/seleccionar/eliminar)
            moreButton.setOnClickListener(v -> showPopupMenu(v, getAdapterPosition()));
        }

        public void bind(Vehicle vehicle) {
            vehicleName.setText(vehicle.getDisplayName());
            vehicleDetails.setText(vehicle.getFullName());
            
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            currentKm.setText(formatter.format(vehicle.getCurrentKm()) + " km");

            // Icono por defecto (puedes personalizar según tipo)
            vehicleIcon.setImageResource(R.drawable.ic_car);
        }

        private void showPopupMenu(View view, int position) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.vehicle_actions, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                Vehicle vehicle = vehicles.get(position);
                int itemId = item.getItemId();

                if (itemId == R.id.action_edit) {
                    listener.onEditVehicle(vehicle);
                } else if (itemId == R.id.action_select) {
                    listener.onSelectVehicle(vehicle);
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteVehicle(vehicle);
                }
                return true;
            });

            popup.show();
        }
    }
}
