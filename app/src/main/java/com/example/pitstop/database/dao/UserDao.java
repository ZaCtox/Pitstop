package com.example.pitstop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pitstop.database.entity.User;

/**
 * DAO de `User` para CRUD básico y consulta por UID.
 */
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE uid = :uid")
    LiveData<User> getUserByUid(String uid);

    @Query("SELECT * FROM users WHERE uid = :uid")
    User getUserByUidSync(String uid);

    @Query("DELETE FROM users WHERE uid = :uid")
    void deleteUser(String uid);
}
