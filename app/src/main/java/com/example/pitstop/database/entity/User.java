package com.example.pitstop.database.entity;

// Asegúrate de tener estos imports
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entidad Room para el usuario autenticado (clave primaria `uid` de Firebase).
 * Se almacena email y se usa como padre en FKs de otras tablas.
 */
@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    private String uid;

    private String email;

    // --- Constructores, Getters y Setters ---

    // Room necesita un constructor vacío
    @Ignore
    public User() {
    }

    // Puedes tener otros constructores
    public User(@NonNull String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}