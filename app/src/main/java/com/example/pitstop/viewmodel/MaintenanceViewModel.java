package com.example.pitstop.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.database.entity.User;
import com.example.pitstop.repository.MaintenanceRepository;
import com.example.pitstop.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * ViewModel de mantenimientos.
 * Expone consultas por usuario y operaciones CRUD, asegurando existencia del usuario local.
 */
public class MaintenanceViewModel extends AndroidViewModel {
    private MaintenanceRepository maintenanceRepository;
    private UserRepository userRepository;
    private FirebaseAuth mAuth;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public MaintenanceViewModel(@NonNull Application application) {
        super(application);
        maintenanceRepository = new MaintenanceRepository(application);
        userRepository = new UserRepository(application);
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Lista todos los mantenimientos del usuario actual
    public LiveData<List<Maintenance>> getAllMaintenance() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.getAllMaintenanceByUser(user.getUid());
        }
        return new MutableLiveData<>();
    }

    // Obtiene un mantenimiento por ID
    public LiveData<Maintenance> getMaintenanceById(int id) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.getMaintenanceById(id, user.getUid());
        }
        return new MutableLiveData<>();
    }

    // Busca mantenimientos por tipo (texto)
    public LiveData<List<Maintenance>> searchMaintenanceByType(String searchQuery) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.searchMaintenanceByType(user.getUid(), searchQuery);
        }
        return new MutableLiveData<>();
    }

    // Filtra mantenimientos por tipo exacto
    public LiveData<List<Maintenance>> getMaintenanceByType(String type) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.getMaintenanceByType(user.getUid(), type);
        }
        return new MutableLiveData<>();
    }

    // Inserta un mantenimiento y asegura la existencia del usuario local
    public void insertMaintenance(Maintenance maintenance) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Asegurar que el usuario existe en la base de datos local
            userRepository.getUserByUid(firebaseUser.getUid()).observeForever(user -> {
                if (user == null) {
                    // Crear usuario si no existe
                    User newUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                    userRepository.insertUser(newUser);
                }
                // Insertar mantenimiento
                maintenance.setUserUid(firebaseUser.getUid());
                maintenanceRepository.insertMaintenance(maintenance);
            });
        } else {
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    // Actualiza un mantenimiento existente
    public void updateMaintenance(Maintenance maintenance) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Asegurar que el usuario existe en la base de datos local
            userRepository.getUserByUid(firebaseUser.getUid()).observeForever(user -> {
                if (user == null) {
                    // Crear usuario si no existe
                    User newUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                    userRepository.insertUser(newUser);
                }
                // Actualizar mantenimiento
                maintenance.setUserUid(firebaseUser.getUid());
                maintenanceRepository.updateMaintenance(maintenance);
            });
        } else {
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    // Elimina un mantenimiento
    public void deleteMaintenance(Maintenance maintenance) {
        maintenanceRepository.deleteMaintenance(maintenance);
    }

    // Elimina un mantenimiento por ID
    public void deleteMaintenanceById(int id) {
        maintenanceRepository.deleteMaintenanceById(id);
    }

    // Versión síncrona para obtener todos los mantenimientos (no usar en UI)
    public List<Maintenance> getAllMaintenanceSync() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.getAllMaintenanceByUserSync(user.getUid());
        }
        return null;
    }

    // Versión síncrona para obtener por ID (no usar en UI)
    public Maintenance getMaintenanceByIdSync(int id) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.getMaintenanceByIdSync(id, user.getUid());
        }
        return null;
    }
}
