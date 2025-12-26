package com.example.pitstop.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.pitstop.database.AppDatabase;
import com.example.pitstop.database.dao.MaintenanceDao;
import com.example.pitstop.database.entity.Maintenance;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio de mantenimientos.
 * Provee métodos reactivas y operaciones CRUD en hilos de fondo mediante Executor.
 */
public class MaintenanceRepository {
    private MaintenanceDao maintenanceDao;
    private ExecutorService executor;

    public MaintenanceRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        maintenanceDao = database.maintenanceDao();
        executor = Executors.newFixedThreadPool(2);
    }

    // Consultas reactivas
    public LiveData<List<Maintenance>> getAllMaintenanceByUser(String userUid) {
        return maintenanceDao.getAllMaintenanceByUser(userUid);
    }

    public LiveData<Maintenance> getMaintenanceById(int id, String userUid) {
        return maintenanceDao.getMaintenanceById(id, userUid);
    }

    public LiveData<List<Maintenance>> searchMaintenanceByType(String userUid, String searchQuery) {
        return maintenanceDao.searchMaintenanceByType(userUid, searchQuery);
    }

    public LiveData<List<Maintenance>> getMaintenanceByType(String userUid, String type) {
        return maintenanceDao.getMaintenanceByType(userUid, type);
    }

    public LiveData<List<Maintenance>> getUpcomingMaintenance(String userUid) {
        return maintenanceDao.getUpcomingMaintenance(userUid);
    }

    // Mutaciones en hilo de fondo
    public void insertMaintenance(Maintenance maintenance) {
        executor.execute(() -> maintenanceDao.insertMaintenance(maintenance));
    }

    public void updateMaintenance(Maintenance maintenance) {
        executor.execute(() -> maintenanceDao.updateMaintenance(maintenance));
    }

    public void deleteMaintenance(Maintenance maintenance) {
        executor.execute(() -> maintenanceDao.deleteMaintenance(maintenance));
    }

    public void deleteMaintenanceById(int id) {
        executor.execute(() -> maintenanceDao.deleteMaintenanceById(id));
    }

    public void deleteAllMaintenanceByUser(String userUid) {
        executor.execute(() -> maintenanceDao.deleteAllMaintenanceByUser(userUid));
    }

    // Consultas síncronas (no usar en UI)
    public List<Maintenance> getAllMaintenanceByUserSync(String userUid) {
        return maintenanceDao.getAllMaintenanceByUserSync(userUid);
    }

    public Maintenance getMaintenanceByIdSync(int id, String userUid) {
        return maintenanceDao.getMaintenanceByIdSync(id, userUid);
    }
}
