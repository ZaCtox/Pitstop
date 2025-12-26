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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.pitstop.MainActivity;
import com.example.pitstop.R;
import com.example.pitstop.database.entity.User;
import com.example.pitstop.viewmodel.SettingsViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * Configuración de usuario.
 * Muestra el email actual, permite cerrar sesión y limpiar datos locales.
 */
public class SettingsFragment extends Fragment {
    private SettingsViewModel viewModel;
    private TextView userEmail;
    private MaterialButton logoutButton;
    private MaterialButton clearDataButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        
        initViews(view);
        setupClickListeners();
        observeViewModel();
    }

    // Vincula vistas del layout
    private void initViews(View view) {
        userEmail = view.findViewById(R.id.user_email);
        logoutButton = view.findViewById(R.id.logout_button);
        clearDataButton = view.findViewById(R.id.clear_data_button);
    }

    // Listeners para cerrar sesión y limpiar datos con confirmación
    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    viewModel.logout();
                    // Ocultar el menú de navegación antes de ir al login
                    if (requireActivity() instanceof MainActivity) {
                        ((MainActivity) requireActivity()).hideBottomNavigation();
                    }
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_settings_to_navigation_login);
                })
                .setNegativeButton("No", null)
                .show();
        });

        clearDataButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Limpiar Datos")
                .setMessage("¿Estás seguro de que quieres eliminar todos los datos? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", (dialog, which) -> {
                    viewModel.clearAllData();
                    Toast.makeText(requireContext(), "Datos eliminados exitosamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
        });
    }

    // Observa el usuario actual y errores para mostrarlos en la UI
    private void observeViewModel() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userEmail.setText(user.getEmail());
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
