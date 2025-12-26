package com.example.pitstop.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.database.entity.Vehicle;
import com.example.pitstop.ui.adapters.MaintenanceCardAdapter;
import com.example.pitstop.viewmodel.DashboardViewModel;
import com.example.pitstop.viewmodel.VehicleViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Fragmento del panel principal (Dashboard).
 * Muestra: vehículo actual, kilometraje, mantenimientos próximos y recientes.
 * Permite: actualizar odómetro, iniciar viaje (sensores), probar notificaciones y
 * navegar a formularios de mantenimiento y gestión de vehículos.
 */
public class DashboardFragment extends Fragment {
    private DashboardViewModel viewModel;
    private VehicleViewModel vehicleViewModel;
    private TextView currentKmText;
    private TextView currentVehicleName;
    private TextView currentVehicleDetails;
    private MaterialButton updateKmButton;
    private MaterialButton startTripButton;
    private MaterialButton testNotificationButton;
    private MaterialButton changeVehicleButton;
    private RecyclerView upcomingMaintenanceRecycler;
    private RecyclerView recentMaintenanceRecycler;
    private FloatingActionButton fabAddMaintenance;
    
    private MaintenanceCardAdapter upcomingAdapter;
    private MaintenanceCardAdapter recentAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        vehicleViewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        
        initViews(view);
        setupRecyclerViews();
        setupClickListeners();
        observeViewModel();
    }

    // Referencia y vinculación de vistas del layout
    private void initViews(View view) {
        currentKmText = view.findViewById(R.id.current_km_text);
        currentVehicleName = view.findViewById(R.id.current_vehicle_name);
        currentVehicleDetails = view.findViewById(R.id.current_vehicle_details);
        updateKmButton = view.findViewById(R.id.update_km_button);
        startTripButton = view.findViewById(R.id.start_trip_button);
        testNotificationButton = view.findViewById(R.id.test_notification_button);
        changeVehicleButton = view.findViewById(R.id.change_vehicle_button);
        upcomingMaintenanceRecycler = view.findViewById(R.id.upcoming_maintenance_recycler);
        recentMaintenanceRecycler = view.findViewById(R.id.recent_maintenance_recycler);
        fabAddMaintenance = view.findViewById(R.id.fab_add_maintenance);
    }

    // Configura los RecyclerViews y sus adapters para listas de mantenimientos
    private void setupRecyclerViews() {
        // Upcoming Maintenance RecyclerView
        upcomingAdapter = new MaintenanceCardAdapter(new MaintenanceCardAdapter.OnMaintenanceActionListener() {
            @Override
            public void onEditMaintenance(Maintenance maintenance) {
                Bundle args = new Bundle();
                args.putInt("maintenanceId", maintenance.getId());
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_navigation_dashboard_to_navigation_maintenance_form, args);
            }

            @Override
            public void onCompleteMaintenance(Maintenance maintenance) {
                // Marcar como completado y actualizar el kilometraje ejecutado
                viewModel.completeMaintenance(maintenance);
            }

            @Override
            public void onDeleteMaintenance(Maintenance maintenance) {
                viewModel.deleteMaintenance(maintenance);
            }
        });
        
        upcomingMaintenanceRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        upcomingMaintenanceRecycler.setAdapter(upcomingAdapter);

        // RecyclerView de mantenimientos recientes
        recentAdapter = new MaintenanceCardAdapter(new MaintenanceCardAdapter.OnMaintenanceActionListener() {
            @Override
            public void onEditMaintenance(Maintenance maintenance) {
                Bundle args = new Bundle();
                args.putInt("maintenanceId", maintenance.getId());
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_navigation_dashboard_to_navigation_maintenance_form, args);
            }

            @Override
            public void onCompleteMaintenance(Maintenance maintenance) {
                viewModel.completeMaintenance(maintenance);
            }

            @Override
            public void onDeleteMaintenance(Maintenance maintenance) {
                viewModel.deleteMaintenance(maintenance);
            }
        });
        
        recentMaintenanceRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recentMaintenanceRecycler.setAdapter(recentAdapter);
    }

    // Configura listeners de los botones y acciones de UI
    private void setupClickListeners() {
        updateKmButton.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_dashboard_to_navigation_odometer_update);
        });

        startTripButton.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_dashboard_to_navigation_sensors);
        });

        testNotificationButton.setOnClickListener(v -> {
            // Verificar permisos de notificación antes de disparar una prueba
            if (checkNotificationPermission()) {
                viewModel.testNotification();
            } else {
                Toast.makeText(requireContext(), "Permisos de notificación requeridos", Toast.LENGTH_SHORT).show();
            }
        });

        fabAddMaintenance.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_dashboard_to_navigation_maintenance_form);
        });

        changeVehicleButton.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_dashboard_to_navigation_vehicles);
        });
    }

    // Suscribe observadores al ViewModel para refrescar la UI con datos
    private void observeViewModel() {
        // Observar vehículo actual
        vehicleViewModel.getCurrentVehicle().observe(getViewLifecycleOwner(), vehicle -> {
            if (vehicle != null) {
                currentVehicleName.setText(vehicle.getDisplayName());
                currentVehicleDetails.setText(vehicle.getFullName());
            } else {
                currentVehicleName.setText("Sin vehículo");
                currentVehicleDetails.setText("Agrega un vehículo para comenzar");
            }
        });

        viewModel.getCurrentKm().observe(getViewLifecycleOwner(), currentKm -> {
            if (currentKm != null) {
                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
                currentKmText.setText(formatter.format(currentKm) + " km");
                
                // Actualizar los adapters con el nuevo kilometraje
                upcomingAdapter.updateCurrentKm(currentKm);
                recentAdapter.updateCurrentKm(currentKm);
            }
        });

        viewModel.getUpcomingMaintenance().observe(getViewLifecycleOwner(), maintenances -> {
            if (maintenances != null) {
                upcomingAdapter.updateMaintenances(maintenances);
            }
        });

        viewModel.getAllMaintenance().observe(getViewLifecycleOwner(), maintenances -> {
            if (maintenances != null) {
                // Mostrar solamente los últimos 5 mantenimientos
                List<Maintenance> recentMaintenances = maintenances.size() > 5 
                    ? maintenances.subList(0, 5) 
                    : maintenances;
                recentAdapter.updateMaintenances(recentMaintenances);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // Verifica el permiso de notificaciones en Android 13+
    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Para versiones anteriores a Android 13, no se requiere permiso
    }
}
