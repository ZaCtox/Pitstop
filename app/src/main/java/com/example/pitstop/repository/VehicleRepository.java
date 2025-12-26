package com.example.pitstop.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.pitstop.database.AppDatabase;
import com.example.pitstop.database.dao.VehicleDao;
import com.example.pitstop.database.entity.Vehicle;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio de vehículos.
 * Encapsula el acceso al DAO y ejecuta operaciones en un hilo de fondo.
 */
public class VehicleRepository {
    private VehicleDao vehicleDao;
    private ExecutorService executor;

    public VehicleRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        vehicleDao = database.vehicleDao();
        executor = Executors.newFixedThreadPool(2);
    }

    // Consultas reactivas
    public LiveData<List<Vehicle>> getAllVehiclesByUser(String userUid) {
        return vehicleDao.getAllVehiclesByUser(userUid);
    }

    public List<Vehicle> getAllVehiclesByUserSync(String userUid) {
        return vehicleDao.getAllVehiclesByUserSync(userUid);
    }

    public LiveData<Vehicle> getVehicleById(int id, String userUid) {
        return vehicleDao.getVehicleById(id, userUid);
    }

    public Vehicle getVehicleByIdSync(int id, String userUid) {
        return vehicleDao.getVehicleByIdSync(id, userUid);
    }

    public LiveData<Vehicle> getCurrentVehicle(String userUid) {
        return vehicleDao.getCurrentVehicle(userUid);
    }

    public Vehicle getCurrentVehicleSync(String userUid) {
        return vehicleDao.getCurrentVehicleSync(userUid);
    }

    // Mutaciones en hilo de fondo
    public void insertVehicle(Vehicle vehicle) {
        android.util.Log.d("VehicleRepository", "Ejecutando inserción de vehículo: " + vehicle.getName());
        executor.execute(() -> {
            try {
                android.util.Log.d("VehicleRepository", "Llamando a vehicleDao.insertVehicle");
                vehicleDao.insertVehicle(vehicle);
                android.util.Log.d("VehicleRepository", "Vehículo insertado en la base de datos");
            } catch (Exception e) {
                android.util.Log.e("VehicleRepository", "Error en inserción de vehículo", e);
            }
        });
    }

    public void updateVehicle(Vehicle vehicle) {
        executor.execute(() -> vehicleDao.updateVehicle(vehicle));
    }

    public void deleteVehicle(Vehicle vehicle) {
        executor.execute(() -> vehicleDao.deleteVehicle(vehicle));
    }

    public void deleteVehicleById(int id) {
        executor.execute(() -> vehicleDao.deleteVehicleById(id));
    }

    public void deactivateVehicle(int id) {
        executor.execute(() -> vehicleDao.deactivateVehicle(id));
    }

    public void updateVehicleKm(int id, int currentKm) {
        executor.execute(() -> vehicleDao.updateVehicleKm(id, currentKm));
    }

    public void setCurrentVehicle(int id, String userUid) {
        executor.execute(() -> {
            // Primero limpia el actual, luego marca el nuevo como actual
            vehicleDao.clearCurrentVehicle(userUid);
            vehicleDao.setCurrentVehicle(id, userUid);
        });
    }

    public LiveData<Vehicle> getCurrentSelectedVehicle(String userUid) {
        return vehicleDao.getCurrentSelectedVehicle(userUid);
    }

    public Vehicle getCurrentSelectedVehicleSync(String userUid) {
        return vehicleDao.getCurrentSelectedVehicleSync(userUid);
    }
}
