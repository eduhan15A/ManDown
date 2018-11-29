package com.example.android.mandown

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_entity")
    fun getAllTasks(): MutableList<TaskEntity>

    @Insert
    fun addTask(taskEntity : TaskEntity):Long

    @Insert
    fun insertAll(vararg dataEntities: TaskEntity)



    //For Settings
    @Query("SELECT * FROM tsettings")
    fun getAllSettings(): MutableList<tSettings>

    @Insert
    fun addSetting(setting : tSettings):Long

    @Update
    fun updateSettings (setting: tSettings): Int

}