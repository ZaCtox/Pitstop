package com.example.pitstop.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.ui.adapters.MaintenanceAdapter;
import com.example.pitstop.viewmodel.DashboardViewModel;
import com.example.pitstop.viewmodel.MaintenanceViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * Lista de mantenimientos.
 * Permite buscar por tipo, ver todos, navegar al detalle y crear nuevos.
 * Además reacciona al kilometraje actual para cálculos en el adapter.
 */
public class MaintenanceListFragment extends Fragment {
    private MaintenanceViewModel viewModel;
    private DashboardViewModel dashboardViewModel;
    private TextInputLayout searchLayout;
    private TextInputEditText searchInput;
    private MaterialButton filterButton;
    private RecyclerView maintenanceRecycler;
    private LinearLayout emptyState;
    private FloatingActionButton fabAddMaintenance;
    
    private MaintenanceAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(MaintenanceViewModel.class);
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    // Vincula vistas del layout
    private void initViews(View view) {
        searchLayout = view.findViewById(R.id.search_layout);
        searchInput = view.findViewById(R.id.search_input);
        filterButton = view.findViewById(R.id.filter_button);
        maintenanceRecycler = view.findViewById(R.id.maintenance_recycler);
        emptyState = view.findViewById(R.id.empty_state);
        fabAddMaintenance = view.findViewById(R.id.fab_add_maintenance);
    }

    // Configura el RecyclerView y su adapter con navegación al detalle
    private void setupRecyclerView() {
        adapter = new MaintenanceAdapter(maintenance -> {
            // Navigate to maintenance detail
            System.out.println("DEBUG: Navigating to maintenance detail for ID: " + maintenance.getId());
            Bundle args = new Bundle();
            args.putInt("maintenanceId", maintenance.getId());
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_maintenance_to_navigation_maintenance_detail, args);
        });
        
        maintenanceRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        maintenanceRecycler.setAdapter(adapter);
    }

    // Listeners para crear mantenimiento y ejecutar búsqueda
    private void setupClickListeners() {
        fabAddMaintenance.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_maintenance_to_navigation_maintenance_form);
        });

        filterButton.setOnClickListener(v -> {
            performSearch();
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }
    
    // Ejecuta la búsqueda según el texto; si está vacío, muestra todos
    private void performSearch() {
        String searchQuery = searchInput.getText().toString().trim();
        if (!TextUtils.isEmpty(searchQuery)) {
            // Observar resultados de búsqueda
            viewModel.searchMaintenanceByType(searchQuery).observe(getViewLifecycleOwner(), maintenances -> {
                if (maintenances != null) {
                    adapter.updateMaintenances(maintenances);
                    updateEmptyState(maintenances.isEmpty());
                }
            });
        } else {
            // Mostrar todos los mantenimientos
            viewModel.getAllMaintenance().observe(getViewLifecycleOwner(), maintenances -> {
                if (maintenances != null) {
                    adapter.updateMaintenances(maintenances);
                    updateEmptyState(maintenances.isEmpty());
                }
            });
        }
    }

    // Observa kilometraje actual y lista completa para actualizar la UI
    private void observeViewModel() {
        // Observar el kilometraje actual para actualizar los adapters
        dashboardViewModel.getCurrentKm().observe(getViewLifecycleOwner(), currentKm -> {
            if (currentKm != null) {
                adapter.updateCurrentKm(currentKm);
            }
        });
        
        viewModel.getAllMaintenance().observe(getViewLifecycleOwner(), maintenances -> {
            if (maintenances != null) {
                adapter.updateMaintenances(maintenances);
                updateEmptyState(maintenances.isEmpty());
            }
        });

        // Remover la observación de búsqueda vacía que causaba problemas

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                // Handle error
            }
        });
    }

    // Alterna entre estado vacío y lista visible
    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            emptyState.setVisibility(View.VISIBLE);
            maintenanceRecycler.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            maintenanceRecycler.setVisibility(View.VISIBLE);
        }
    }
}
