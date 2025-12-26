package com.example.pitstop.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.database.entity.User;
import com.example.pitstop.database.entity.VehicleLog;
import com.example.pitstop.receiver.MaintenanceReminderReceiver;
import com.example.pitstop.repository.MaintenanceRepository;
import com.example.pitstop.repository.UserRepository;
import com.example.pitstop.repository.VehicleLogRepository;
import com.example.pitstop.repository.VehicleRepository;
import com.example.pitstop.service.MaintenanceNotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * ViewModel del Dashboard.
 * - Expone el kilometraje actual y listas de mantenimientos.
 * - Inserta logs de vehículo y dispara recordatorios/servicio de notificaciones.
 * - Asegura que el usuario exista en la base local y maneja errores.
 */
public class DashboardViewModel extends AndroidViewModel {
    private MaintenanceRepository maintenanceRepository;
    private VehicleLogRepository vehicleLogRepository;
    private VehicleRepository vehicleRepository;
    private UserRepository userRepository;
    private FirebaseAuth mAuth;
    private MutableLiveData<Integer> currentKm = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        maintenanceRepository = new MaintenanceRepository(application);
        vehicleLogRepository = new VehicleLogRepository(application);
        vehicleRepository = new VehicleRepository(application);
        userRepository = new UserRepository(application);
        mAuth = FirebaseAuth.getInstance();
        
        ensureUserExists(); // Crea el usuario en DB local si no existe
        loadCurrentKm();    // Carga el km actual desde el último VehicleLog
    }

    public LiveData<Integer> getCurrentKm() {
        return currentKm;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Maintenance>> getUpcomingMaintenance() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.getUpcomingMaintenance(user.getUid());
        }
        return new MutableLiveData<>();
    }

    public LiveData<List<Maintenance>> getAllMaintenance() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return maintenanceRepository.getAllMaintenanceByUser(user.getUid());
        }
        return new MutableLiveData<>();
    }

    // Verifica/crea el usuario en la base de datos local usando el UID de Firebase
    private void ensureUserExists() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Verificar si el usuario existe en la base de datos local
            userRepository.getUserByUid(firebaseUser.getUid()).observeForever(user -> {
                if (user == null) {
                    // Crear usuario si no existe
                    User newUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                    userRepository.insertUser(newUser);
                }
            });
        }
    }

    // Publica el km del vehículo actualmente seleccionado
    private void loadCurrentKm() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Observar vehículo actual y, cuando cambie, observar su último log
            vehicleRepository.getCurrentSelectedVehicle(user.getUid()).observeForever(currentVehicle -> {
                if (currentVehicle != null) {
                    vehicleLogRepository.getLatestVehicleLogByVehicle(user.getUid(), currentVehicle.getId())
                        .observeForever(vehicleLog -> {
                            if (vehicleLog != null) {
                                currentKm.setValue(vehicleLog.getCurrentKm());
                            } else {
                                // Si no hay logs para este vehículo, usar su currentKm o 0
                                Integer km = currentVehicle.getCurrentKm();
                                currentKm.setValue(km);
                            }
                        });
                } else {
                    currentKm.setValue(0);
                }
            });
        }
    }

    // Inserta un nuevo `VehicleLog` con el km dado y actualiza `currentKm`
    public void updateKilometerage(int newKm) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Necesitamos obtener el vehículo actual para el vehicleId
            new Thread(() -> {
                try {
                    com.example.pitstop.database.entity.Vehicle currentVehicle = vehicleRepository.getCurrentSelectedVehicleSync(user.getUid());
                    int vehicleId = currentVehicle != null ? currentVehicle.getId() : 1; // Default a 1 si no hay vehículo
                    
                    VehicleLog vehicleLog = new VehicleLog(
                        user.getUid(),
                        vehicleId,
                        newKm,
                        System.currentTimeMillis(),
                        null
                    );
                    vehicleLogRepository.insertVehicleLog(vehicleLog);
                    // Actualizar el km del vehículo también
                    vehicleRepository.updateVehicleKm(vehicleId, newKm);
                    
                    // Actualizar en el hilo principal
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        currentKm.setValue(newKm);
                        
                        // Verificar recordatorios de mantenimiento después de actualizar kilometraje
                        checkMaintenanceReminders(user.getUid());
                    });
                } catch (Exception e) {
                    android.util.Log.e("DashboardViewModel", "Error al actualizar kilometraje", e);
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        errorMessage.setValue("Error al actualizar kilometraje: " + e.getMessage());
                    });
                }
            }).start();
        } else {
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    // Igual a `updateKilometerage` pero guardando una foto asociada al log
    public void updateKilometerageWithPhoto(int newKm, String photoUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Necesitamos obtener el vehículo actual para el vehicleId
            new Thread(() -> {
                try {
                    com.example.pitstop.database.entity.Vehicle currentVehicle = vehicleRepository.getCurrentSelectedVehicleSync(user.getUid());
                    int vehicleId = currentVehicle != null ? currentVehicle.getId() : 1; // Default a 1 si no hay vehículo
                    
                    VehicleLog vehicleLog = new VehicleLog(
                        user.getUid(),
                        vehicleId,
                        newKm,
                        System.currentTimeMillis(),
                        photoUri
                    );
                    vehicleLogRepository.insertVehicleLog(vehicleLog);
                    // Actualizar el km del vehículo también
                    vehicleRepository.updateVehicleKm(vehicleId, newKm);
                    
                    // Actualizar en el hilo principal
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        currentKm.setValue(newKm);
                        
                        // Verificar recordatorios de mantenimiento después de actualizar kilometraje
                        checkMaintenanceReminders(user.getUid());
                    });
                } catch (Exception e) {
                    android.util.Log.e("DashboardViewModel", "Error al actualizar kilometraje con foto", e);
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        errorMessage.setValue("Error al actualizar kilometraje: " + e.getMessage());
                    });
                }
            }).start();
        } else {
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    public void addMaintenance(Maintenance maintenance) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            maintenance.setUserUid(user.getUid());
            maintenanceRepository.insertMaintenance(maintenance);
        } else {
            errorMessage.setValue("Usuario no autenticado");
        }
    }

    public void deleteMaintenance(Maintenance maintenance) {
        maintenanceRepository.deleteMaintenance(maintenance);
    }
    
    // Marca un mantenimiento como completado usando el km actual del último log
    public void completeMaintenance(Maintenance maintenance) {
        // Marcar como completado actualizando el kilometraje ejecutado al actual
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Ejecutar la consulta de base de datos en un hilo de fondo
            new Thread(() -> {
                try {
                    VehicleLog latestLog = vehicleLogRepository.getLatestVehicleLogSync(user.getUid());
                    if (latestLog != null) {
                        maintenance.setExecutedKm(latestLog.getCurrentKm());
                        maintenanceRepository.updateMaintenance(maintenance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Manejar errores si es necesario
                }
            }).start();
        }
    }
    
    // Ejecuta el servicio que calcula y dispara recordatorios si corresponde
    private void checkMaintenanceReminders(String userUid) {
        // Usar el executor del repositorio para evitar problemas de hilos
        new Thread(() -> {
            try {
                MaintenanceNotificationService notificationService = new MaintenanceNotificationService(getApplication());
                notificationService.checkMaintenanceReminders(userUid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void testNotification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            checkMaintenanceReminders(user.getUid());
        }
    }
}
