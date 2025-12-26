package com.example.pitstop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pitstop.database.entity.Vehicle;

import java.util.List;

/**
 * DAO de `Vehicle` con operaciones CRUD y consultas por usuario.
 * Incluye selección de vehículo actual y actualizaciones de km.
 */
@Dao
public interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVehicle(Vehicle vehicle);

    @Update
    void updateVehicle(Vehicle vehicle);

    @Delete
    void deleteVehicle(Vehicle vehicle);

    @Query("SELECT * FROM vehicles WHERE userUid = :userUid AND isActive = 1 ORDER BY createdAt DESC")
    LiveData<List<Vehicle>> getAllVehiclesByUser(String userUid);

    @Query("SELECT * FROM vehicles WHERE userUid = :userUid AND isActive = 1 ORDER BY createdAt DESC")
    List<Vehicle> getAllVehiclesByUserSync(String userUid);

    @Query("SELECT * FROM vehicles WHERE id = :id AND userUid = :userUid")
    LiveData<Vehicle> getVehicleById(int id, String userUid);

    @Query("SELECT * FROM vehicles WHERE id = :id AND userUid = :userUid")
    Vehicle getVehicleByIdSync(int id, String userUid);

    @Query("SELECT * FROM vehicles WHERE userUid = :userUid AND isActive = 1 ORDER BY createdAt DESC LIMIT 1")
    LiveData<Vehicle> getCurrentVehicle(String userUid);

    @Query("SELECT * FROM vehicles WHERE userUid = :userUid AND isActive = 1 ORDER BY createdAt DESC LIMIT 1")
    Vehicle getCurrentVehicleSync(String userUid);

    @Query("UPDATE vehicles SET isActive = 0 WHERE id = :id")
    void deactivateVehicle(int id);

    @Query("UPDATE vehicles SET currentKm = :currentKm WHERE id = :id")
    void updateVehicleKm(int id, int currentKm);

    @Query("DELETE FROM vehicles WHERE id = :id")
    void deleteVehicleById(int id);

    @Query("UPDATE vehicles SET isCurrent = 0 WHERE userUid = :userUid")
    void clearCurrentVehicle(String userUid);

    @Query("UPDATE vehicles SET isCurrent = 1 WHERE id = :id AND userUid = :userUid")
    void setCurrentVehicle(int id, String userUid);

    @Query("SELECT * FROM vehicles WHERE userUid = :userUid AND isCurrent = 1 AND isActive = 1 LIMIT 1")
    LiveData<Vehicle> getCurrentSelectedVehicle(String userUid);

    @Query("SELECT * FROM vehicles WHERE userUid = :userUid AND isCurrent = 1 AND isActive = 1 LIMIT 1")
    Vehicle getCurrentSelectedVehicleSync(String userUid);
}
