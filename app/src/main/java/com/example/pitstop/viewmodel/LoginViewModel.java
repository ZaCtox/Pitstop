package com.example.pitstop.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel de autenticación.
 * Maneja login/registro con FirebaseAuth y expone estados de sesión, errores y carga.
 */
public class LoginViewModel extends AndroidViewModel {
    private static final String TAG = "LoginViewModel";
    
    private FirebaseAuth mAuth;
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        
        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        isLoggedIn.setValue(currentUser != null);
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Inicia sesión con email y contraseña
    public void login(String email, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                isLoading.setValue(false);
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    isLoggedIn.setValue(true);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    errorMessage.setValue("Error al iniciar sesión: " + 
                        (task.getException() != null ? task.getException().getMessage() : "Error desconocido"));
                }
            });
    }

    // Registra un nuevo usuario con email y contraseña
    public void register(String email, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                isLoading.setValue(false);
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    isLoggedIn.setValue(true);
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    errorMessage.setValue("Error al registrar usuario: " + 
                        (task.getException() != null ? task.getException().getMessage() : "Error desconocido"));
                }
            });
    }

    // Cierra sesión y actualiza el estado
    public void logout() {
        mAuth.signOut();
        isLoggedIn.setValue(false);
    }

    // Devuelve el usuario autenticado actual (si existe)
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
