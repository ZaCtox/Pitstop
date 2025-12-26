package com.example.pitstop.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.pitstop.service.MaintenanceNotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Receiver para disparar recordatorios de mantenimiento (por alarmas o eventos del sistema).
 * Obtiene el usuario autenticado y delega la verificación al servicio de notificaciones.
 */
public class MaintenanceReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "MaintenanceReminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "MaintenanceReminderReceiver triggered");
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MaintenanceNotificationService notificationService = new MaintenanceNotificationService(context);
            notificationService.checkMaintenanceReminders(user.getUid());
        }
    }
}
