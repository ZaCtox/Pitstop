package com.example.pitstop.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad Room que representa un vehículo del usuario.
 * Incluye datos descriptivos, kilometraje actual y banderas de estado/selección.
 * Relación: pertenece a un `User` por `userUid` (FK con borrado en cascada).
 */
@Entity(
    tableName = "vehicles",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "uid",
        childColumns = "userUid",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("userUid")}
)
public class Vehicle {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userUid;
    private String name;
    private String brand;
    private String model;
    private int year;
    private String color;
    private String licensePlate;
    private int currentKm;
    private long createdAt;
    private boolean isActive;
    private boolean isCurrent;

    @Ignore
    public Vehicle() {}

    public Vehicle(String userUid, String name, String brand, String model, int year, 
                  String color, String licensePlate, int currentKm) {
        this.userUid = userUid;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.licensePlate = licensePlate;
        this.currentKm = currentKm;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
        this.isCurrent = false;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getCurrentKm() {
        return currentKm;
    }

    public void setCurrentKm(int currentKm) {
        this.currentKm = currentKm;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    // Métodos auxiliares para mostrar nombres legibles
    public String getFullName() {
        return String.format("%s %s %d", brand, model, year);
    }

    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        return getFullName();
    }
}
