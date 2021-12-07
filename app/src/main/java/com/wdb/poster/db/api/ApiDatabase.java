package com.wdb.poster.db.api;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.wdb.poster.db.api.dao.ConnectionDao;
import com.wdb.poster.db.api.dao.RequestDao;
import com.wdb.poster.db.api.entity.Connection;
import com.wdb.poster.db.api.entity.Request;


@Database(entities = {Connection.class, Request.class}, version = 2, exportSchema = false)
public abstract class ApiDatabase extends RoomDatabase {

    private static final String DB_NAME = "ApiDatabase.db";
    private static volatile ApiDatabase instance;

    public static synchronized ApiDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static ApiDatabase create(Context context) {
        return Room.databaseBuilder(
                context,
                ApiDatabase.class,
                DB_NAME).build();
    }

    public abstract ConnectionDao getConnectionDao();
    public abstract RequestDao getRequestDao();
}
