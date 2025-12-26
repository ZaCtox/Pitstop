package com.example.pitstop.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pitstop.MainActivity;
import com.example.pitstop.R;
import com.example.pitstop.database.AppDatabase;
import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.database.entity.VehicleLog;

import java.util.List;

/**
 * Servicio que evalúa recordatorios de mantenimiento y dispara notificaciones.
 * Criterio: notifica cuando restan <= 1000 km para el próximo servicio.
 */
public class MaintenanceNotificationService {
    private static final String CHANNEL_ID = "maintenance_reminders";
    private static final int NOTIFICATION_ID = 1001;
    
    private Context context;
    private AppDatabase database;

    public MaintenanceNotificationService(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
    }

    // Calcula recordatorios en hilo de fondo
    public void checkMaintenanceReminders(String userUid) {
        // Ejecutar en hilo en segundo plano
        new Thread(() -> {
            try {
                // Obtener el kilometraje actual
                VehicleLog latestLog = database.vehicleLogDao().getLatestVehicleLogSync(userUid);
                if (latestLog == null) return;
                
                int currentKm = latestLog.getCurrentKm();
                
                // Obtener todos los mantenimientos del usuario
                List<Maintenance> maintenances = database.maintenanceDao().getAllMaintenanceByUserSync(userUid);
                
                for (Maintenance maintenance : maintenances) {
                    int nextServiceKm = maintenance.getNextServiceKm();
                    int remainingKm = nextServiceKm - currentKm;
                    
                    // Notificar si faltan 1000 km o menos para el próximo servicio
                    if (remainingKm <= 1000 && remainingKm > 0) {
                        sendMaintenanceNotification(maintenance, remainingKm);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Construye y envía una notificación que navega al detalle del mantenimiento
    private void sendMaintenanceNotification(Maintenance maintenance, int remainingKm) {
        createNotificationChannel();
        
        // Crear intent que navegue directamente al detalle del mantenimiento
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("navigate_to_maintenance_detail", maintenance.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, maintenance.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = "🔧 Recordatorio de Mantenimiento";
        String message = String.format(
            "Tu %s está próximo a vencer. Faltan %d km para el servicio.",
            maintenance.getType(),
            remainingKm
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_maintenance)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message + "\n\nPróximo servicio: " + maintenance.getNextServiceKm() + " km"));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID + maintenance.getId(), builder.build());
    }

    // Crea el canal de notificaciones (Android O+)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Recordatorios de Mantenimiento";
            String description = "Notificaciones cuando un servicio de mantenimiento está próximo a vencer";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
