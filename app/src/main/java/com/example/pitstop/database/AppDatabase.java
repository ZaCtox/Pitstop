package com.example.pitstop.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.pitstop.database.dao.MaintenanceDao;
import com.example.pitstop.database.dao.UserDao;
import com.example.pitstop.database.dao.VehicleDao;
import com.example.pitstop.database.dao.VehicleLogDao;
import com.example.pitstop.database.entity.Maintenance;
import com.example.pitstop.database.entity.User;
import com.example.pitstop.database.entity.Vehicle;
import com.example.pitstop.database.entity.VehicleLog;

/**
 * Configuración principal de Room Database.
 * Define entidades, DAOs, versión y migraciones registradas.
 */
@Database(
    entities = {User.class, Vehicle.class, Maintenance.class, VehicleLog.class},
    version = 4,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "pitstop_database";

    // Migración de 3 a 4: agrega columna `isCurrent` a la tabla vehicles
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE vehicles ADD COLUMN isCurrent INTEGER NOT NULL DEFAULT 0");
        }
    };

    public abstract UserDao userDao();
    public abstract VehicleDao vehicleDao();
    public abstract MaintenanceDao maintenanceDao();
    public abstract VehicleLogDao vehicleLogDao();

    // Singleton para obtener instancia de la base de datos
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
            )
            .addMigrations(MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build();
        }
        return INSTANCE;
    }

    // Libera la instancia (útil para tests)
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
