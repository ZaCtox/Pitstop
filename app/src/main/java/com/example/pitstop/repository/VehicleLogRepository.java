package com.example.pitstop.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.pitstop.database.AppDatabase;
import com.example.pitstop.database.dao.VehicleLogDao;
import com.example.pitstop.database.entity.VehicleLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio de logs de vehículo (kilometraje).
 * Expone consultas reactivas y operaciones CRUD en hilos de fondo.
 */
public class VehicleLogRepository {
    private VehicleLogDao vehicleLogDao;
    private ExecutorService executor;

    public VehicleLogRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        vehicleLogDao = database.vehicleLogDao();
        executor = Executors.newFixedThreadPool(2);
    }

    public LiveData<List<VehicleLog>> getAllVehicleLogsByUser(String userUid) {
        return vehicleLogDao.getAllVehicleLogsByUser(userUid);
    }

    public LiveData<VehicleLog> getLatestVehicleLog(String userUid) {
        return vehicleLogDao.getLatestVehicleLog(userUid);
    }

    public LiveData<VehicleLog> getVehicleLogById(int id) {
        return vehicleLogDao.getVehicleLogById(id);
    }

    // Versiones síncronas (no usar en UI)
    public VehicleLog getLatestVehicleLogSync(String userUid) {
        return vehicleLogDao.getLatestVehicleLogSync(userUid);
    }

    public List<VehicleLog> getAllVehicleLogsByUserSync(String userUid) {
        return vehicleLogDao.getAllVehicleLogsByUserSync(userUid);
    }

    public VehicleLog getVehicleLogByIdSync(int id) {
        return vehicleLogDao.getVehicleLogByIdSync(id);
    }

    // Mutaciones en hilo de fondo
    public void insertVehicleLog(VehicleLog vehicleLog) {
        executor.execute(() -> vehicleLogDao.insertVehicleLog(vehicleLog));
    }

    public void updateVehicleLog(VehicleLog vehicleLog) {
        executor.execute(() -> vehicleLogDao.updateVehicleLog(vehicleLog));
    }

    public void deleteVehicleLog(VehicleLog vehicleLog) {
        executor.execute(() -> vehicleLogDao.deleteVehicleLog(vehicleLog));
    }

    public void deleteVehicleLogById(int id) {
        executor.execute(() -> vehicleLogDao.deleteVehicleLogById(id));
    }

    public void deleteAllVehicleLogsByUser(String userUid) {
        executor.execute(() -> vehicleLogDao.deleteAllVehicleLogsByUser(userUid));
    }

    public LiveData<VehicleLog> getLatestVehicleLogByVehicle(String userUid, int vehicleId) {
        return vehicleLogDao.getLatestVehicleLogByVehicle(userUid, vehicleId);
    }

    public VehicleLog getLatestVehicleLogByVehicleSync(String userUid, int vehicleId) {
        return vehicleLogDao.getLatestVehicleLogByVehicleSync(userUid, vehicleId);
    }

}
