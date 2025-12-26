package com.example.pitstop.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.example.pitstop.R;
import com.example.pitstop.viewmodel.DashboardViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import androidx.lifecycle.ViewModelProvider;

/**
 * Sensores y tracking de viaje.
 * Solicita permiso de ubicación, inicia/detiene un "viaje" y acumula distancia aproximada.
 * Al finalizar, propone actualizar el kilometraje total vía `DashboardViewModel`.
 */
public class SensorsFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private boolean isTrackingTrip = false;
    private double tripDistance = 0.0;
    private DashboardViewModel dashboardViewModel;
    
    private MaterialButton startTripButton;
    private MaterialButton stopTripButton;
    private TextView tripDistanceText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        
        initViews(view);
        setupClickListeners();
        checkLocationPermission();
    }

    // Vincula vistas del layout
    private void initViews(View view) {
        startTripButton = view.findViewById(R.id.start_trip_button);
        stopTripButton = view.findViewById(R.id.stop_trip_button);
        tripDistanceText = view.findViewById(R.id.trip_distance);
    }

    // Listeners para iniciar/detener el tracking del viaje
    private void setupClickListeners() {
        startTripButton.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                startTripTracking();
            }
        });

        stopTripButton.setOnClickListener(v -> {
            stopTripTracking();
        });
    }

    // Verifica y solicita permiso de ubicación en tiempo de ejecución
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    // Inicia el tracking de viaje, resetea distancia y obtiene ubicación inicial
    private void startTripTracking() {
        if (checkLocationPermission()) {
            isTrackingTrip = true;
            tripDistance = 0.0;
            
            startTripButton.setVisibility(View.GONE);
            stopTripButton.setVisibility(View.VISIBLE);
            tripDistanceText.setVisibility(View.VISIBLE);
            tripDistanceText.setText("Distancia: 0.0 km");
            
            // Get initial location
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        lastKnownLocation = location;
                    }
                });
            
            Toast.makeText(requireContext(), "Viaje iniciado", Toast.LENGTH_SHORT).show();
        }
    }

    // Detiene el tracking y actualiza el odómetro con la distancia acumulada
    private void stopTripTracking() {
        isTrackingTrip = false;
        
        startTripButton.setVisibility(View.VISIBLE);
        stopTripButton.setVisibility(View.GONE);
        tripDistanceText.setVisibility(View.GONE);
        
        // Actualizar el kilometraje con la distancia del viaje
        if (tripDistance > 0) {
            dashboardViewModel.getCurrentKm().observe(getViewLifecycleOwner(), currentKm -> {
                if (currentKm != null) {
                    int newKm = currentKm + (int) Math.round(tripDistance);
                    dashboardViewModel.updateKilometerage(newKm);
                }
            });
        }
        
        Toast.makeText(requireContext(), 
            String.format("Viaje finalizado. Distancia: %.2f km", tripDistance), 
            Toast.LENGTH_LONG).show();
        
        // Resetear la distancia para el próximo viaje
        tripDistance = 0.0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
