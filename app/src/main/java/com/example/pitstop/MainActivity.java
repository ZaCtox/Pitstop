package com.example.pitstop;

// Imports necesarios para que funcione la Activity principal
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity principal de la aplicación.
 * - Configura navegación con el `BottomNavigationView`.
 * - Verifica la autenticación del usuario para redirigir a login o dashboard.
 * - Gestiona navegación desde notificaciones hacia el detalle de mantenimiento.
 */
public class MainActivity extends AppCompatActivity {
    // Controlador de navegación para manejar los destinos del NavGraph
    private NavController navController;
    // Barra de navegación inferior (tabs principales de la app)
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilita contenido de borde a borde (status/navigation bar transparentes)
        EdgeToEdge.enable(this);
        // Infla el layout principal de la Activity
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Inicializa navegación inferior
        setupNavigation();
        // Verifica si hay usuario autenticado y navega acorde
        checkAuthentication();
        // Gestiona navegación si la app se abrió desde una notificación
        handleNotificationNavigation();
    }

    // Configura el `NavController` y lo conecta con la barra inferior
    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Configure bottom navigation only (without ActionBar)
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    // Comprueba si hay usuario autenticado y redirige a login o dashboard
    private void checkAuthentication() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, navigate to login
            try {
                if (navController.getCurrentDestination() == null ||
                    navController.getCurrentDestination().getId() != R.id.navigation_login) {
                    navController.navigate(R.id.navigation_login);
                }
            } catch (Exception ignore) {}
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            // User authenticated, navigate to dashboard
            try {
                if (navController.getCurrentDestination() == null ||
                    navController.getCurrentDestination().getId() != R.id.navigation_dashboard) {
                    navController.navigate(R.id.navigation_dashboard);
                }
            } catch (Exception ignore) {}
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Asegurar que la barra de navegación esté visible cuando se regresa a la app
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Soporta navegación "atrás" integrándose con el NavController
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
    
    // Permite ocultar la barra inferior desde Fragments
    public void hideBottomNavigation() {
        bottomNavigationView.setVisibility(View.GONE);
    }
    
    // Permite mostrar la barra inferior desde Fragments
    public void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
    
    // Maneja navegación cuando la Activity es lanzada desde una notificación
    private void handleNotificationNavigation() {
        // Verificar si la app se abrió desde una notificación
        if (getIntent().hasExtra("navigate_to_maintenance_detail")) {
            int maintenanceId = getIntent().getIntExtra("navigate_to_maintenance_detail", -1);
            if (maintenanceId != -1) {
                // Navegar al detalle del mantenimiento después de un breve delay
                // para asegurar que la navegación esté lista
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        Bundle args = new Bundle();
                        args.putInt("maintenanceId", maintenanceId);
                        navController.navigate(R.id.navigation_maintenance_detail, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 500);
            }
        }
    }
}