package com.example.pitstop.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.viewmodel.MaintenanceViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Detalle de mantenimiento.
 * Muestra información completa del mantenimiento y permite editar o eliminar.
 */
public class MaintenanceDetailFragment extends Fragment {
    private MaintenanceViewModel viewModel;
    private MaterialToolbar toolbar;
    private TextView maintenanceType;
    private TextView maintenanceDate;
    private TextView maintenanceDescription;
    private TextView executedKm;
    private TextView periodicity;
    private TextView nextService;
    private TextView cost;
    private TextView notes;
    private MaterialButton editButton;
    private MaterialButton deleteButton;
    
    private int maintenanceId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(MaintenanceViewModel.class);
        
        // Get maintenance ID from arguments
        if (getArguments() != null) {
            maintenanceId = getArguments().getInt("maintenanceId", -1);
        }
        
        initViews(view);
        setupToolbar();
        setupClickListeners();
        observeViewModel();
        
        if (maintenanceId != -1) {
            loadMaintenanceData();
        }
    }

    // Vincula vistas del layout
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        maintenanceType = view.findViewById(R.id.maintenance_type);
        maintenanceDate = view.findViewById(R.id.maintenance_date);
        maintenanceDescription = view.findViewById(R.id.maintenance_description);
        executedKm = view.findViewById(R.id.executed_km);
        periodicity = view.findViewById(R.id.periodicity);
        nextService = view.findViewById(R.id.next_service);
        cost = view.findViewById(R.id.cost);
        notes = view.findViewById(R.id.notes);
        editButton = view.findViewById(R.id.edit_button);
        deleteButton = view.findViewById(R.id.delete_button);
    }
    
    // Configura toolbar con navegación atrás
    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });
    }

    private void setupClickListeners() {
        editButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("maintenanceId", maintenanceId);
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_maintenance_detail_to_navigation_maintenance_form, args);
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Mantenimiento")
                .setMessage("¿Estás seguro de que quieres eliminar este mantenimiento?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    viewModel.deleteMaintenanceById(maintenanceId);
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton("No", null)
                .show();
        });
    }

    // Carga el mantenimiento por ID y actualiza la UI
    private void loadMaintenanceData() {
        System.out.println("DEBUG: Loading maintenance data for ID: " + maintenanceId);
        viewModel.getMaintenanceById(maintenanceId).observe(getViewLifecycleOwner(), maintenance -> {
            System.out.println("DEBUG: Maintenance data received: " + (maintenance != null ? maintenance.getType() : "null"));
            if (maintenance != null) {
                displayMaintenanceData(maintenance);
            } else {
                System.out.println("DEBUG: Maintenance is null for ID: " + maintenanceId);
                Toast.makeText(requireContext(), "No se encontró el mantenimiento", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mapea los datos del mantenimiento a las vistas formateando fecha, km y costo
    private void displayMaintenanceData(Maintenance maintenance) {
        maintenanceType.setText(maintenance.getType());
        
        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM, yyyy", Locale.getDefault());
        maintenanceDate.setText(dateFormat.format(new Date(maintenance.getDate())));
        
        maintenanceDescription.setText(maintenance.getDescription());
        
        // Format kilometerage
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        executedKm.setText(formatter.format(maintenance.getExecutedKm()) + " km");
        periodicity.setText(formatter.format(maintenance.getPeriodicityKm()) + " km");
        
        int nextServiceKm = maintenance.getNextServiceKm();
        nextService.setText(formatter.format(nextServiceKm) + " km");
        
        // Format cost
        if (maintenance.getCost() != null) {
            cost.setText("$" + String.format(Locale.getDefault(), "%.2f", maintenance.getCost()));
        } else {
            cost.setText("N/A");
        }
        
        // Notes
        if (maintenance.getNotes() != null && !maintenance.getNotes().isEmpty()) {
            notes.setText(maintenance.getNotes());
        } else {
            notes.setText("Sin notas");
        }
    }

    // Observa errores del ViewModel
    private void observeViewModel() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
