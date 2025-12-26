package com.example.pitstop.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pitstop.database.entity.Vehicle;
import com.example.pitstop.repository.VehicleRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * ViewModel de vehículos.
 * Expone operaciones CRUD y selección de vehículo actual por usuario de Firebase.
 */
public class VehicleViewModel extends AndroidViewModel {
    private VehicleRepository vehicleRepository;
    private FirebaseAuth mAuth;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Vehicle> currentVehicle = new MutableLiveData<>();

    public VehicleViewModel(@NonNull Application application) {
        super(application);
        vehicleRepository = new VehicleRepository(application);
        mAuth = FirebaseAuth.getInstance();
    }

    // Lista todos los vehículos del usuario actual
    public LiveData<List<Vehicle>> getAllVehicles() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return vehicleRepository.getAllVehiclesByUser(user.getUid());
        }
        return new MutableLiveData<>();
    }

    // Obtiene el vehículo marcado como "actual" para el usuario
    public LiveData<Vehicle> getCurrentVehicle() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return vehicleRepository.getCurrentSelectedVehicle(user.getUid());
        }
        return new MutableLiveData<>();
    }

    // Busca un vehículo por ID
    public LiveData<Vehicle> getVehicleById(int id) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return vehicleRepository.getVehicleById(id, user.getUid());
        }
        return new MutableLiveData<>();
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Vehicle> getCurrentVehicleLiveData() {
        return currentVehicle;
    }

    // Inserta un nuevo vehículo asociado al usuario autenticado
    public void insertVehicle(Vehicle vehicle) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            vehicle.setUserUid(user.getUid());
            android.util.Log.d("VehicleViewModel", "Insertando vehículo: " + vehicle.getName() + " para usuario: " + user.getUid());
            try {
                vehicleRepository.insertVehicle(vehicle);
                android.util.Log.d("VehicleViewModel", "Vehículo insertado exitosamente");
            } catch (Exception e) {
                android.util.Log.e("VehicleViewModel", "Error al insertar vehículo", e);
                errorMessage.setValue("Error al guardar vehículo: " + e.getMessage());
            }
        } else {
            android.util.Log.e("VehicleViewModel", "Usuario no autenticado");
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    // Actualiza un vehículo existente
    public void updateVehicle(Vehicle vehicle) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            vehicle.setUserUid(user.getUid());
            vehicleRepository.updateVehicle(vehicle);
        } else {
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    // Elimina un vehículo
    public void deleteVehicle(Vehicle vehicle) {
        vehicleRepository.deleteVehicle(vehicle);
    }

    // Elimina un vehículo por ID
    public void deleteVehicleById(int id) {
        vehicleRepository.deleteVehicleById(id);
    }

    // Desactiva un vehículo (no lo elimina)
    public void deactivateVehicle(int id) {
        vehicleRepository.deactivateVehicle(id);
    }

    // Actualiza el kilometraje del vehículo
    public void updateVehicleKm(int id, int currentKm) {
        vehicleRepository.updateVehicleKm(id, currentKm);
    }

    // Marca un vehículo como "actual" y publica en LiveData (disparará actualización de km)
    public void setCurrentVehicle(Vehicle vehicle) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            vehicleRepository.setCurrentVehicle(vehicle.getId(), user.getUid());
            currentVehicle.setValue(vehicle);
        } else {
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    // Obtiene sincrónicamente el vehículo actual (usar con cuidado fuera del hilo principal)
    public Vehicle getCurrentVehicleSync() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return vehicleRepository.getCurrentSelectedVehicleSync(user.getUid());
        }
        return null;
    }
}
