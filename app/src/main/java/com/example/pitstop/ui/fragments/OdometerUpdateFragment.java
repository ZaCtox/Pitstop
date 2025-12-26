package com.example.pitstop.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.pitstop.R;
import com.example.pitstop.viewmodel.DashboardViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Actualización de odómetro.
 * Permite actualizar el km actual manualmente o tomando una foto del tablero.
 * Valida entradas, maneja permisos de cámara y actualiza el ViewModel.
 */
public class OdometerUpdateFragment extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    private static final int CAMERA_REQUEST_CODE = 1003;
    
    private DashboardViewModel viewModel;
    private MaterialToolbar toolbar;
    private TextView currentKmDisplay;
    private MaterialButton manualInputButton;
    private MaterialButton cameraButton;
    private MaterialCardView manualInputCard;
    private MaterialCardView cameraPreviewCard;
    private TextInputLayout newKmLayout;
    private TextInputEditText newKmInput;
    private MaterialButton saveManualButton;
    private ImageView odometerPreview;
    private TextInputLayout odometerReadingLayout;
    private TextInputEditText odometerReadingInput;
    private MaterialButton retakePhotoButton;
    private MaterialButton savePhotoButton;
    
    private Uri capturedImageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_odometer_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        
        initViews(view);
        setupToolbar();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        currentKmDisplay = view.findViewById(R.id.current_km_display);
        manualInputButton = view.findViewById(R.id.manual_input_button);
        cameraButton = view.findViewById(R.id.camera_button);
        manualInputCard = view.findViewById(R.id.manual_input_card);
        cameraPreviewCard = view.findViewById(R.id.camera_preview_card);
        newKmLayout = view.findViewById(R.id.new_km_layout);
        newKmInput = view.findViewById(R.id.new_km_input);
        saveManualButton = view.findViewById(R.id.save_manual_button);
        odometerPreview = view.findViewById(R.id.odometer_preview);
        odometerReadingLayout = view.findViewById(R.id.odometer_reading_layout);
        odometerReadingInput = view.findViewById(R.id.odometer_reading_input);
        retakePhotoButton = view.findViewById(R.id.retake_photo_button);
        savePhotoButton = view.findViewById(R.id.save_photo_button);
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

    // Listeners para alternar modos, guardar y manejar cámara
    private void setupClickListeners() {
        manualInputButton.setOnClickListener(v -> {
            manualInputCard.setVisibility(View.VISIBLE);
            cameraPreviewCard.setVisibility(View.GONE);
        });

        cameraButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            }
        });

        saveManualButton.setOnClickListener(v -> {
            if (validateManualInput()) {
                int newKm = Integer.parseInt(newKmInput.getText().toString().trim());
                viewModel.updateKilometerage(newKm);
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        retakePhotoButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            }
        });

        savePhotoButton.setOnClickListener(v -> {
            if (validatePhotoInput()) {
                int newKm = Integer.parseInt(odometerReadingInput.getText().toString().trim());
                viewModel.updateKilometerageWithPhoto(newKm, capturedImageUri.toString());
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    // Verifica y solicita permiso de cámara si es necesario
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    // Abre la cámara del sistema para capturar una imagen
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(requireContext(), "No se puede abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    // Valida el campo de km ingresado manualmente
    private boolean validateManualInput() {
        if (TextUtils.isEmpty(newKmInput.getText())) {
            newKmLayout.setError("El kilometraje es requerido");
            return false;
        }
        
        try {
            int km = Integer.parseInt(newKmInput.getText().toString().trim());
            if (km < 0) {
                newKmLayout.setError("El kilometraje debe ser positivo");
                return false;
            }
        } catch (NumberFormatException e) {
            newKmLayout.setError("Ingresa un número válido");
            return false;
        }
        
        newKmLayout.setError(null);
        return true;
    }

    // Valida el campo de lectura del odómetro cuando se usa foto
    private boolean validatePhotoInput() {
        if (TextUtils.isEmpty(odometerReadingInput.getText())) {
            odometerReadingLayout.setError("La lectura del odómetro es requerida");
            return false;
        }
        
        try {
            int km = Integer.parseInt(odometerReadingInput.getText().toString().trim());
            if (km < 0) {
                odometerReadingLayout.setError("El kilometraje debe ser positivo");
                return false;
            }
        } catch (NumberFormatException e) {
            odometerReadingLayout.setError("Ingresa un número válido");
            return false;
        }
        
        odometerReadingLayout.setError(null);
        return true;
    }

    // Observa cambios del km actual y errores para mostrarlos en la UI
    private void observeViewModel() {
        viewModel.getCurrentKm().observe(getViewLifecycleOwner(), currentKm -> {
            if (currentKm != null) {
                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
                currentKmDisplay.setText(formatter.format(currentKm) + " km");
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                // For now, just show the camera preview card
                // In a real implementation, you would save the image and get its URI
                cameraPreviewCard.setVisibility(View.VISIBLE);
                manualInputCard.setVisibility(View.GONE);
                odometerPreview.setImageBitmap((android.graphics.Bitmap) data.getExtras().get("data"));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
