package com.wdb.poster.db.api.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.wdb.poster.db.api.entity.Connection;

import java.util.List;

@Dao
public interface ConnectionDao {

    @Insert
    void insert(Connection... conns);

    @Update
    void update(Connection... conns);

    @Delete
    void delete(Connection... conns);

    @Query("delete from Connection")
    void deleteAll();

    @Query("delete from Connection where connId=:connId")
    void deleteById(int connId);

    @Query("select * from Connection order by connId desc")
    List<Connection> queryAll();

    @Query("select * from Connection where connId=(select max(connId) from Connection)")
    Connection top();

    @Query("select * from Connection where connId = :connId")
    Connection queryById(int connId);


}
