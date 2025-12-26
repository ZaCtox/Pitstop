package com.example.pitstop.model;

import android.graphics.Color;

public enum MaintenanceType {
    OIL_CHANGE("Cambio de Aceite", Color.parseColor("#FF6B6B")),
    TIRE_ROTATION("Rotación de Neumáticos", Color.parseColor("#4ECDC4")),
    BRAKE_CHECK("Revisión de Frenos", Color.parseColor("#45B7D1")),
    FILTER_CHANGE("Cambio de Filtros", Color.parseColor("#96CEB4")),
    TIRE_CHANGE("Cambio de Neumáticos", Color.parseColor("#FFEAA7")),
    ENGINE_CHECK("Revisión de Motor", Color.parseColor("#DDA0DD")),
    TRANSMISSION("Transmisión", Color.parseColor("#98D8C8")),
    COOLING_SYSTEM("Sistema de Refrigeración", Color.parseColor("#F7DC6F")),
    ELECTRICAL("Sistema Eléctrico", Color.parseColor("#BB8FCE")),
    OTHER("Otro", Color.parseColor("#85C1E9"));

    private final String displayName;
    private final int color;

    MaintenanceType(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }

    public static MaintenanceType fromString(String type) {
        for (MaintenanceType maintenanceType : values()) {
            if (maintenanceType.displayName.equals(type)) {
                return maintenanceType;
            }
        }
        return OTHER;
    }
}
