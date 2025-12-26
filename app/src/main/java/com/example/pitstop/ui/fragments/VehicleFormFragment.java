package com.example.pitstop.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Vehicle;
import com.example.pitstop.viewmodel.VehicleViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Formulario de vehículo (crear/editar).
 * Valida campos principales del vehículo y guarda vía `VehicleViewModel`.
 */
public class VehicleFormFragment extends Fragment {
    private VehicleViewModel viewModel;
    private MaterialToolbar toolbar;
    private TextInputLayout nameLayout;
    private TextInputEditText nameInput;
    private TextInputLayout brandLayout;
    private TextInputEditText brandInput;
    private TextInputLayout modelLayout;
    private TextInputEditText modelInput;
    private TextInputLayout yearLayout;
    private TextInputEditText yearInput;
    private TextInputLayout colorLayout;
    private TextInputEditText colorInput;
    private TextInputLayout licensePlateLayout;
    private TextInputEditText licensePlateInput;
    private TextInputLayout currentKmLayout;
    private TextInputEditText currentKmInput;
    private MaterialButton saveButton;

    private int vehicleId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicle_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        
        // Obtener ID del vehículo si es una edición
        if (getArguments() != null) {
            vehicleId = getArguments().getInt("vehicleId", -1);
        }
        
        initViews(view);
        setupToolbar();
        setupClickListeners();
        observeViewModel();
        
        if (vehicleId != -1) {
            loadVehicleData();
        }
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        nameLayout = view.findViewById(R.id.name_layout);
        nameInput = view.findViewById(R.id.name_input);
        brandLayout = view.findViewById(R.id.brand_layout);
        brandInput = view.findViewById(R.id.brand_input);
        modelLayout = view.findViewById(R.id.model_layout);
        modelInput = view.findViewById(R.id.model_input);
        yearLayout = view.findViewById(R.id.year_layout);
        yearInput = view.findViewById(R.id.year_input);
        colorLayout = view.findViewById(R.id.color_layout);
        colorInput = view.findViewById(R.id.color_input);
        licensePlateLayout = view.findViewById(R.id.license_plate_layout);
        licensePlateInput = view.findViewById(R.id.license_plate_input);
        currentKmLayout = view.findViewById(R.id.current_km_layout);
        currentKmInput = view.findViewById(R.id.current_km_input);
        saveButton = view.findViewById(R.id.save_button);
    }

    // Configura la toolbar con título según si es nuevo o edición
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        
        if (vehicleId == -1) {
            toolbar.setTitle("Nuevo Vehículo");
        } else {
            toolbar.setTitle("Editar Vehículo");
        }
    }

    // Listener para validar y guardar cuando se pulsa "Guardar"
    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveVehicle();
            }
        });
    }

    // Valida los campos obligatorios y rangos (año, km)
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validar nombre
        if (TextUtils.isEmpty(nameInput.getText())) {
            nameLayout.setError("El nombre es requerido");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }
        
        // Validar marca
        if (TextUtils.isEmpty(brandInput.getText())) {
            brandLayout.setError("La marca es requerida");
            isValid = false;
        } else {
            brandLayout.setError(null);
        }
        
        // Validar modelo
        if (TextUtils.isEmpty(modelInput.getText())) {
            modelLayout.setError("El modelo es requerido");
            isValid = false;
        } else {
            modelLayout.setError(null);
        }
        
        // Validar año
        if (TextUtils.isEmpty(yearInput.getText())) {
            yearLayout.setError("El año es requerido");
            isValid = false;
        } else {
            try {
                int year = Integer.parseInt(yearInput.getText().toString().trim());
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                if (year < 1900 || year > currentYear + 1) {
                    yearLayout.setError("Ingresa un año válido");
                    isValid = false;
                } else {
                    yearLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                yearLayout.setError("Ingresa un año válido");
                isValid = false;
            }
        }
        
        // Validar kilometraje actual
        if (TextUtils.isEmpty(currentKmInput.getText())) {
            currentKmLayout.setError("El kilometraje actual es requerido");
            isValid = false;
        } else {
            try {
                int km = Integer.parseInt(currentKmInput.getText().toString().trim());
                if (km < 0) {
                    currentKmLayout.setError("El kilometraje debe ser positivo");
                    isValid = false;
                } else {
                    currentKmLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                currentKmLayout.setError("Ingresa un número válido");
                isValid = false;
            }
        }
        
        return isValid;
    }

    // Construye el objeto Vehicle y realiza inserción o actualización
    private void saveVehicle() {
        android.util.Log.d("VehicleFormFragment", "Iniciando guardado de vehículo");
        
        Vehicle vehicle = new Vehicle();
        
        if (vehicleId != -1) {
            vehicle.setId(vehicleId);
        }
        
        String name = nameInput.getText().toString().trim();
        String brand = brandInput.getText().toString().trim();
        String model = modelInput.getText().toString().trim();
        String color = colorInput.getText().toString().trim();
        String licensePlate = licensePlateInput.getText().toString().trim();
        
        vehicle.setName(name);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setYear(Integer.parseInt(yearInput.getText().toString().trim()));
        vehicle.setColor(color);
        vehicle.setLicensePlate(licensePlate);
        vehicle.setCurrentKm(Integer.parseInt(currentKmInput.getText().toString().trim()));
        
        // Configurar campos adicionales para nuevos vehículos
        if (vehicleId == -1) {
            vehicle.setCreatedAt(System.currentTimeMillis());
            vehicle.setActive(true);
        }
        
        android.util.Log.d("VehicleFormFragment", "Vehículo configurado: " + name + " " + brand + " " + model);
        
        if (vehicleId == -1) {
            viewModel.insertVehicle(vehicle);
            Toast.makeText(requireContext(), "Vehículo agregado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.updateVehicle(vehicle);
            Toast.makeText(requireContext(), "Vehículo actualizado exitosamente", Toast.LENGTH_SHORT).show();
        }
        
        Navigation.findNavController(requireView()).navigateUp();
    }

    // Carga los datos del vehículo para edición y completa los campos
    private void loadVehicleData() {
        viewModel.getVehicleById(vehicleId).observe(getViewLifecycleOwner(), vehicle -> {
            if (vehicle != null) {
                nameInput.setText(vehicle.getName());
                brandInput.setText(vehicle.getBrand());
                modelInput.setText(vehicle.getModel());
                yearInput.setText(String.valueOf(vehicle.getYear()));
                colorInput.setText(vehicle.getColor());
                licensePlateInput.setText(vehicle.getLicensePlate());
                currentKmInput.setText(String.valueOf(vehicle.getCurrentKm()));
            }
        });
    }

    // Observa errores del ViewModel y los informa al usuario
    private void observeViewModel() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                android.util.Log.e("VehicleFormFragment", "Error del ViewModel: " + error);
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
