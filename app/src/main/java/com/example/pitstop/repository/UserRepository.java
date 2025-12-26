package com.example.pitstop.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.pitstop.database.AppDatabase;
import com.example.pitstop.database.dao.UserDao;
import com.example.pitstop.database.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio de usuarios.
 * Maneja acceso a `UserDao` y ejecuta mutaciones en un Executor dedicado.
 */
public class UserRepository {
    private UserDao userDao;
    private ExecutorService executor;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        executor = Executors.newFixedThreadPool(2);
    }

    public LiveData<User> getUserByUid(String uid) {
        return userDao.getUserByUid(uid);
    }

    public User getUserByUidSync(String uid) {
        return userDao.getUserByUidSync(uid);
    }

    public void insertUser(User user) {
        executor.execute(() -> userDao.insertUser(user));
    }

    public void updateUser(User user) {
        executor.execute(() -> userDao.updateUser(user));
    }

    public void deleteUser(String uid) {
        executor.execute(() -> userDao.deleteUser(uid));
    }
}
