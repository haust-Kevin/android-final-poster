package com.wdb.poster.db.api.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.wdb.poster.db.api.entity.Connection;
import com.wdb.poster.db.api.entity.Request;

import java.util.List;

@Dao
public interface RequestDao {

    @Insert
    void insert(Request... reqs);

    @Update
    void update(Request... reqs);

    @Delete
    void delete(Request... reqs);

    @Query("delete from Request")
    void deleteAll();

    @Query("delete from Request where reqId=:reqId")
    void deleteById(int reqId);

//    @Query("delete from Request where connId=:connId")
//    void deleteAllByConnId(int connId);

    @Query("select * from Request where connId=:connId")
    List<Request> queryByConnId(int connId);

    @Query("select * from Request where reqId=:reqId")
    Request queryById(int reqId);


}
