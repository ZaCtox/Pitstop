package com.example.pitstop.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad Room para el registro de kilometraje (log del vehículo).
 * Guarda el km actual, fecha y opcionalmente una foto del odómetro.
 * Relación: pertenece a un `User` y a un `Vehicle` por `vehicleId`.
 */
@Entity(
    tableName = "vehicle_logs",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "uid",
        childColumns = "userUid",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("userUid"), @Index("vehicleId")}
)
public class VehicleLog {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userUid;
    private int vehicleId;
    private int currentKm;
    private long date;
    private String odometerPhotoUri;

    @Ignore
    public VehicleLog() {}

    public VehicleLog(String userUid, int vehicleId, int currentKm, long date, String odometerPhotoUri) {
        this.userUid = userUid;
        this.vehicleId = vehicleId;
        this.currentKm = currentKm;
        this.date = date;
        this.odometerPhotoUri = odometerPhotoUri;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getCurrentKm() {
        return currentKm;
    }

    public void setCurrentKm(int currentKm) {
        this.currentKm = currentKm;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getOdometerPhotoUri() {
        return odometerPhotoUri;
    }

    public void setOdometerPhotoUri(String odometerPhotoUri) {
        this.odometerPhotoUri = odometerPhotoUri;
    }
}
