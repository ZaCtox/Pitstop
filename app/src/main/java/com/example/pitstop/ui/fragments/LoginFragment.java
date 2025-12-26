package com.example.pitstop.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.pitstop.MainActivity;
import com.example.pitstop.R;
import com.example.pitstop.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Fragmento de autenticación (Login y Registro).
 * Permite ingresar correo y contraseña para iniciar sesión o registrarse.
 * Valida entradas, muestra estado de carga y errores, y navega al dashboard al autenticar.
 */
public class LoginFragment extends Fragment {
    private LoginViewModel viewModel;
    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private ProgressBar progressBar;
    private TextView errorMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        
        initViews(view);
        setupClickListeners();
        observeViewModel();
    }

    // Vincula las vistas del layout
    private void initViews(View view) {
        emailLayout = view.findViewById(R.id.email_layout);
        emailInput = view.findViewById(R.id.email_input);
        passwordLayout = view.findViewById(R.id.password_layout);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);
        progressBar = view.findViewById(R.id.progress_bar);
        errorMessage = view.findViewById(R.id.error_message);
    }

    // Listeners para manejar acciones de login/registro
    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                viewModel.login(email, password);
            }
        });

        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                viewModel.register(email, password);
            }
        });
    }

    // Valida email y contraseña, mostrando errores en los TextInputLayout
    private boolean validateInputs() {
        boolean isValid = true;
        
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("El correo electrónico es requerido");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Ingresa un correo electrónico válido");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }
        
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("La contraseña es requerida");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }
        
        return isValid;
    }

    // Observa estados del ViewModel para navegar, mostrar errores y loading
    private void observeViewModel() {
        viewModel.getIsLoggedIn().observe(getViewLifecycleOwner(), isLoggedIn -> {
            if (isLoggedIn) {
                // Mostrar la barra de navegación antes de navegar
                if (requireActivity() instanceof MainActivity) {
                    ((MainActivity) requireActivity()).showBottomNavigation();
                }
                // Evitar múltiples navegaciones si el fragment ya no está en estado válido
                try {
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_login_to_navigation_dashboard);
                } catch (Exception ignore) {}
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                errorMessage.setText(error);
                errorMessage.setVisibility(View.VISIBLE);
            } else {
                errorMessage.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                registerButton.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
            }
        });
    }
}
