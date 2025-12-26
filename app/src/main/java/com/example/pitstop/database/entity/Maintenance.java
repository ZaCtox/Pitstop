package com.example.pitstop.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad Room que representa un mantenimiento.
 * Almacena el tipo, descripción, periodicidad, km ejecutado, fecha, costo y notas.
 * Relación: pertenece a un `User` (por `userUid`) y referencia un `vehicleId`.
 */
@Entity(
    tableName = "maintenance",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "uid",
        childColumns = "userUid",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("userUid"), @Index("vehicleId")}
)
public class Maintenance {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userUid;
    private int vehicleId;
    private String type;
    private String description;
    private int periodicityKm;
    private int executedKm;
    private long date;
    private Double cost;
    private String notes;

    @Ignore
    public Maintenance() {}

    public Maintenance(String userUid, int vehicleId, String type, String description, int periodicityKm, 
                      int executedKm, long date, Double cost, String notes) {
        this.userUid = userUid;
        this.vehicleId = vehicleId;
        this.type = type;
        this.description = description;
        this.periodicityKm = periodicityKm;
        this.executedKm = executedKm;
        this.date = date;
        this.cost = cost;
        this.notes = notes;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPeriodicityKm() {
        return periodicityKm;
    }

    public void setPeriodicityKm(int periodicityKm) {
        this.periodicityKm = periodicityKm;
    }

    public int getExecutedKm() {
        return executedKm;
    }

    public void setExecutedKm(int executedKm) {
        this.executedKm = executedKm;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Método auxiliar para calcular el próximo servicio
    public int getNextServiceKm() {
        return executedKm + periodicityKm;
    }
}
