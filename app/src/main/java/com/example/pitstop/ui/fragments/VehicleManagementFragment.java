package com.example.pitstop.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Vehicle;
import com.example.pitstop.ui.adapters.VehicleAdapter;
import com.example.pitstop.viewmodel.VehicleViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Fragmento de gestión de vehículos.
 * Permite listar, seleccionar, editar y eliminar vehículos, así como crear nuevos.
 */
public class VehicleManagementFragment extends Fragment {
    private VehicleViewModel viewModel;
    private RecyclerView vehicleRecycler;
    private FloatingActionButton fabAddVehicle;
    private VehicleAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicle_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    // Vincula las vistas del layout
    private void initViews(View view) {
        vehicleRecycler = view.findViewById(R.id.vehicle_recycler);
        fabAddVehicle = view.findViewById(R.id.fab_add_vehicle);
    }

    // Configura el RecyclerView y su adapter con acciones por vehículo
    private void setupRecyclerView() {
        adapter = new VehicleAdapter(new VehicleAdapter.OnVehicleActionListener() {
            @Override
            public void onEditVehicle(Vehicle vehicle) {
                // Navegar al formulario de edición del vehículo
                Bundle args = new Bundle();
                args.putInt("vehicleId", vehicle.getId());
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_navigation_vehicles_to_navigation_vehicle_form, args);
            }

            @Override
            public void onSelectVehicle(Vehicle vehicle) {
                // Seleccionar como vehículo actual para el dashboard
                viewModel.setCurrentVehicle(vehicle);
                Toast.makeText(requireContext(), 
                    "Vehículo seleccionado: " + vehicle.getDisplayName(), 
                    Toast.LENGTH_SHORT).show();
                // Navegar de vuelta al dashboard
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_navigation_vehicles_to_navigation_dashboard);
            }

            @Override
            public void onDeleteVehicle(Vehicle vehicle) {
                // Mostrar diálogo de confirmación antes de eliminar
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar Vehículo")
                    .setMessage("¿Estás seguro de que quieres eliminar este vehículo? Se eliminarán también todos sus mantenimientos.")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        viewModel.deleteVehicle(vehicle);
                        Toast.makeText(requireContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        });
        
        vehicleRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        vehicleRecycler.setAdapter(adapter);
    }

    // Listeners para acciones de UI
    private void setupClickListeners() {
        fabAddVehicle.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_vehicles_to_navigation_vehicle_form);
        });
    }

    // Observa cambios del ViewModel para refrescar la lista y errores
    private void observeViewModel() {
        viewModel.getAllVehicles().observe(getViewLifecycleOwner(), vehicles -> {
            if (vehicles != null) {
                adapter.updateVehicles(vehicles);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
