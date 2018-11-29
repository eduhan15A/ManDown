package com.example.android.mandown

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "task_entity")
data class TaskEntity (
        @PrimaryKey(autoGenerate = true)
        var id:Int = 0,
        var area:String = "",
        var cel:String = ""
)

@Entity(tableName = "tsettings")
data class tSettings (
        @PrimaryKey(autoGenerate = true)
        var id:Int = 0,
        var cel:String = "",
        var sensibility:Int = 0
)

