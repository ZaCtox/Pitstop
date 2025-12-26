package com.example.pitstop.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pitstop.database.AppDatabase;
import com.example.pitstop.database.entity.User;
import com.example.pitstop.repository.MaintenanceRepository;
import com.example.pitstop.repository.UserRepository;
import com.example.pitstop.repository.VehicleLogRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel de configuración.
 * Expone el usuario actual, cierre de sesión y limpieza total de datos del usuario.
 */
public class SettingsViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private MaintenanceRepository maintenanceRepository;
    private VehicleLogRepository vehicleLogRepository;
    private FirebaseAuth mAuth;
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        maintenanceRepository = new MaintenanceRepository(application);
        vehicleLogRepository = new VehicleLogRepository(application);
        mAuth = FirebaseAuth.getInstance();
        
        loadCurrentUser();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Carga el usuario actual desde DB local o lo crea si no existe
    private void loadCurrentUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            userRepository.getUserByUid(firebaseUser.getUid()).observeForever(user -> {
                if (user != null) {
                    currentUser.setValue(user);
                } else {
                    // Create user if doesn't exist in local database
                    User newUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                    userRepository.insertUser(newUser);
                    currentUser.setValue(newUser);
                }
            });
        }
    }

    public void logout() {
        mAuth.signOut();
        currentUser.setValue(null);
    }

    public FirebaseUser getFirebaseUser() {
        return mAuth.getCurrentUser();
    }
    
    // Elimina mantenimientos, logs y el registro del usuario en la base local
    public void clearAllData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Eliminar todos los datos del usuario
            maintenanceRepository.deleteAllMaintenanceByUser(user.getUid());
            vehicleLogRepository.deleteAllVehicleLogsByUser(user.getUid());
            userRepository.deleteUser(user.getUid());
        }
    }
}
