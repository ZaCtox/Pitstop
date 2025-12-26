package com.example.pitstop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pitstop.database.entity.VehicleLog;

import java.util.List;

/**
 * DAO de `VehicleLog` para registrar y consultar logs de kilometraje.
 */
@Dao
public interface VehicleLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVehicleLog(VehicleLog vehicleLog);

    @Update
    void updateVehicleLog(VehicleLog vehicleLog);

    @Delete
    void deleteVehicleLog(VehicleLog vehicleLog);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid ORDER BY date DESC")
    LiveData<List<VehicleLog>> getAllVehicleLogsByUser(String userUid);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid ORDER BY date DESC")
    List<VehicleLog> getAllVehicleLogsByUserSync(String userUid);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid AND vehicleId = :vehicleId ORDER BY date DESC")
    LiveData<List<VehicleLog>> getAllVehicleLogsByUserAndVehicle(String userUid, int vehicleId);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid AND vehicleId = :vehicleId ORDER BY date DESC")
    List<VehicleLog> getAllVehicleLogsByUserAndVehicleSync(String userUid, int vehicleId);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid ORDER BY date DESC LIMIT 1")
    LiveData<VehicleLog> getLatestVehicleLog(String userUid);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid ORDER BY date DESC LIMIT 1")
    VehicleLog getLatestVehicleLogSync(String userUid);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid AND vehicleId = :vehicleId ORDER BY date DESC LIMIT 1")
    LiveData<VehicleLog> getLatestVehicleLogByVehicle(String userUid, int vehicleId);

    @Query("SELECT * FROM vehicle_logs WHERE userUid = :userUid AND vehicleId = :vehicleId ORDER BY date DESC LIMIT 1")
    VehicleLog getLatestVehicleLogByVehicleSync(String userUid, int vehicleId);

    @Query("SELECT * FROM vehicle_logs WHERE id = :id")
    LiveData<VehicleLog> getVehicleLogById(int id);

    @Query("SELECT * FROM vehicle_logs WHERE id = :id")
    VehicleLog getVehicleLogByIdSync(int id);

    @Query("DELETE FROM vehicle_logs WHERE id = :id")
    void deleteVehicleLogById(int id);

    @Query("DELETE FROM vehicle_logs WHERE userUid = :userUid")
    void deleteAllVehicleLogsByUser(String userUid);
}
