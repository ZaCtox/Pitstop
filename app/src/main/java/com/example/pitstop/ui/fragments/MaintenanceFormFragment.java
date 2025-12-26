package com.example.pitstop.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.pitstop.model.MaintenanceType;

import com.example.pitstop.R;
import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.viewmodel.MaintenanceViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

/**
 * Formulario de creación/edición de mantenimiento.
 * Permite seleccionar tipo, descripción, periodicidad, km ejecutados, fecha, costo y notas.
 * Valida entradas, guarda/actualiza vía ViewModel y navega hacia atrás al terminar.
 */
public class MaintenanceFormFragment extends Fragment {
    private MaintenanceViewModel viewModel;
    private MaterialToolbar toolbar;
    private TextInputLayout typeLayout;
    private AutoCompleteTextView typeInput;
    private TextInputLayout descriptionLayout;
    private TextInputEditText descriptionInput;
    private TextInputLayout periodicityLayout;
    private TextInputEditText periodicityInput;
    private TextInputLayout executedKmLayout;
    private TextInputEditText executedKmInput;
    private TextInputLayout dateLayout;
    private TextInputEditText dateInput;
    private TextInputLayout costLayout;
    private TextInputEditText costInput;
    private TextInputLayout notesLayout;
    private TextInputEditText notesInput;
    private MaterialButton cancelButton;
    private MaterialButton saveButton;
    
    private int maintenanceId = -1;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maintenance_form, container, false);
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
        setupTypeSpinner();
        observeViewModel();
        
        if (maintenanceId != -1) {
            loadMaintenanceData();
        }
    }

    // Vincula vistas del layout
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        typeLayout = view.findViewById(R.id.type_layout);
        typeInput = view.findViewById(R.id.type_input);
        descriptionLayout = view.findViewById(R.id.description_layout);
        descriptionInput = view.findViewById(R.id.description_input);
        periodicityLayout = view.findViewById(R.id.periodicity_layout);
        periodicityInput = view.findViewById(R.id.periodicity_input);
        executedKmLayout = view.findViewById(R.id.executed_km_layout);
        executedKmInput = view.findViewById(R.id.executed_km_input);
        dateLayout = view.findViewById(R.id.date_layout);
        dateInput = view.findViewById(R.id.date_input);
        costLayout = view.findViewById(R.id.cost_layout);
        costInput = view.findViewById(R.id.cost_input);
        notesLayout = view.findViewById(R.id.notes_layout);
        notesInput = view.findViewById(R.id.notes_input);
        cancelButton = view.findViewById(R.id.cancel_button);
        saveButton = view.findViewById(R.id.save_button);
    }
    
    // Configura la toolbar con navegación atrás
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

    // Listeners para abrir fecha, cancelar o guardar
    private void setupClickListeners() {
        dateInput.setOnClickListener(v -> showDatePicker());
        
        cancelButton.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });
        
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveMaintenance();
            }
        });
    }

    // Carga los tipos de mantenimiento en el selector desplegable
    private void setupTypeSpinner() {
        String[] types = new String[MaintenanceType.values().length];
        for (int i = 0; i < MaintenanceType.values().length; i++) {
            types[i] = MaintenanceType.values()[i].getDisplayName();
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, types);
        typeInput.setAdapter(adapter);
    }

    // Muestra un selector de fecha y coloca el resultado en el campo
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                String dateStr = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                dateInput.setText(dateStr);
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Valida entradas obligatorias y normaliza km ejecutados (0 si vacío)
    private boolean validateInputs() {
        boolean isValid = true;
        
        if (TextUtils.isEmpty(typeInput.getText())) {
            typeLayout.setError("El tipo es requerido");
            isValid = false;
        } else {
            typeLayout.setError(null);
        }
        
        if (TextUtils.isEmpty(descriptionInput.getText())) {
            descriptionLayout.setError("La descripción es requerida");
            isValid = false;
        } else {
            descriptionLayout.setError(null);
        }
        
        if (TextUtils.isEmpty(periodicityInput.getText())) {
            periodicityLayout.setError("La periodicidad es requerida");
            isValid = false;
        } else {
            periodicityLayout.setError(null);
        }
        
        // El kilometraje ejecutado puede ser 0 para mantenimientos futuros
        if (TextUtils.isEmpty(executedKmInput.getText())) {
            executedKmInput.setText("0");
        }
        executedKmLayout.setError(null);
        
        if (TextUtils.isEmpty(dateInput.getText())) {
            dateLayout.setError("La fecha es requerida");
            isValid = false;
        } else {
            dateLayout.setError(null);
        }
        
        return isValid;
    }

    // Construye el objeto Maintenance y ejecuta inserción/actualización
    private void saveMaintenance() {
        Maintenance maintenance = new Maintenance();
        
        // Solo asignar ID si es una actualización (maintenanceId != -1)
        if (maintenanceId != -1) {
            maintenance.setId(maintenanceId);
        }
        // Para nuevos mantenimientos, no asignar ID para que Room lo auto-genere
        
        maintenance.setType(typeInput.getText().toString().trim());
        maintenance.setDescription(descriptionInput.getText().toString().trim());
        maintenance.setPeriodicityKm(Integer.parseInt(periodicityInput.getText().toString()));
        maintenance.setExecutedKm(Integer.parseInt(executedKmInput.getText().toString()));
        maintenance.setDate(selectedDate.getTimeInMillis());
        
        if (!TextUtils.isEmpty(costInput.getText())) {
            maintenance.setCost(Double.parseDouble(costInput.getText().toString()));
        }
        
        if (!TextUtils.isEmpty(notesInput.getText())) {
            maintenance.setNotes(notesInput.getText().toString().trim());
        }
        
        if (maintenanceId == -1) {
            viewModel.insertMaintenance(maintenance);
            Toast.makeText(requireContext(), "Mantenimiento agregado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.updateMaintenance(maintenance);
            Toast.makeText(requireContext(), "Mantenimiento actualizado exitosamente", Toast.LENGTH_SHORT).show();
        }
        
        // Navegar de vuelta después de guardar
        Navigation.findNavController(requireView()).navigateUp();
    }

    // Carga datos para edición o limpia campos si es nuevo
    private void loadMaintenanceData() {
        System.out.println("DEBUG: Loading maintenance data for editing, ID: " + maintenanceId);
        if (maintenanceId != -1) {
            viewModel.getMaintenanceById(maintenanceId).observe(getViewLifecycleOwner(), maintenance -> {
                System.out.println("DEBUG: Maintenance data for editing: " + (maintenance != null ? maintenance.getType() : "null"));
                if (maintenance != null) {
                    typeInput.setText(maintenance.getType());
                    descriptionInput.setText(maintenance.getDescription());
                    periodicityInput.setText(String.valueOf(maintenance.getPeriodicityKm()));
                    executedKmInput.setText(String.valueOf(maintenance.getExecutedKm()));
                    
                    selectedDate.setTimeInMillis(maintenance.getDate());
                    String dateStr = String.format("%02d/%02d/%04d", 
                        selectedDate.get(Calendar.DAY_OF_MONTH),
                        selectedDate.get(Calendar.MONTH) + 1,
                        selectedDate.get(Calendar.YEAR));
                    dateInput.setText(dateStr);
                    
                    if (maintenance.getCost() != null) {
                        costInput.setText(String.valueOf(maintenance.getCost()));
                    }
                    
                    if (maintenance.getNotes() != null) {
                        notesInput.setText(maintenance.getNotes());
                    }
                } else {
                    System.out.println("DEBUG: Maintenance is null for editing ID: " + maintenanceId);
                    Toast.makeText(requireContext(), "No se encontró el mantenimiento para editar", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Para nuevo mantenimiento, limpiar campos
            typeInput.setText("");
            descriptionInput.setText("");
            periodicityInput.setText("");
            executedKmInput.setText("");
            costInput.setText("");
            notesInput.setText("");
            
            // Establecer fecha actual
            selectedDate = Calendar.getInstance();
            String dateStr = String.format("%02d/%02d/%04d", 
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR));
            dateInput.setText(dateStr);
        }
    }

    // Observa errores del ViewModel y los muestra como Toasts
    private void observeViewModel() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // No navegar automáticamente después de guardar
        // El usuario debe tocar "Guardar" explícitamente
    }
}
