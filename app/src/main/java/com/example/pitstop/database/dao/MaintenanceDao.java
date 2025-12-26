package com.example.pitstop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pitstop.database.entity.Maintenance;

import java.util.List;

/**
 * DAO de `Maintenance` con operaciones CRUD, búsqueda por tipo y próximos servicios.
 */
@Dao
public interface MaintenanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMaintenance(Maintenance maintenance);

    @Update
    void updateMaintenance(Maintenance maintenance);

    @Delete
    void deleteMaintenance(Maintenance maintenance);

    @Query("SELECT * FROM maintenance WHERE userUid = :userUid ORDER BY date DESC")
    LiveData<List<Maintenance>> getAllMaintenanceByUser(String userUid);

    @Query("SELECT * FROM maintenance WHERE userUid = :userUid ORDER BY date DESC")
    List<Maintenance> getAllMaintenanceByUserSync(String userUid);

    @Query("SELECT * FROM maintenance WHERE userUid = :userUid AND vehicleId = :vehicleId ORDER BY date DESC")
    LiveData<List<Maintenance>> getAllMaintenanceByUserAndVehicle(String userUid, int vehicleId);

    @Query("SELECT * FROM maintenance WHERE userUid = :userUid AND vehicleId = :vehicleId ORDER BY date DESC")
    List<Maintenance> getAllMaintenanceByUserAndVehicleSync(String userUid, int vehicleId);

    @Query("SELECT * FROM maintenance WHERE id = :id AND userUid = :userUid")
    LiveData<Maintenance> getMaintenanceById(int id, String userUid);

    @Query("SELECT * FROM maintenance WHERE id = :id AND userUid = :userUid")
    Maintenance getMaintenanceByIdSync(int id, String userUid);

    @Query("SELECT * FROM maintenance WHERE userUid = :userUid AND type LIKE '%' || :searchQuery || '%' ORDER BY date DESC")
    LiveData<List<Maintenance>> searchMaintenanceByType(String userUid, String searchQuery);

    @Query("SELECT * FROM maintenance WHERE userUid = :userUid AND type = :type ORDER BY date DESC")
    LiveData<List<Maintenance>> getMaintenanceByType(String userUid, String type);

    @Query("SELECT * FROM maintenance WHERE userUid = :userUid ORDER BY executedKm + periodicityKm ASC")
    LiveData<List<Maintenance>> getUpcomingMaintenance(String userUid);

    @Query("DELETE FROM maintenance WHERE id = :id")
    void deleteMaintenanceById(int id);

    @Query("DELETE FROM maintenance WHERE userUid = :userUid")
    void deleteAllMaintenanceByUser(String userUid);
}
