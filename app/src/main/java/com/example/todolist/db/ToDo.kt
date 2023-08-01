package com.example.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toDos")
class ToDo (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    @ColumnInfo(name  = "title") val title : String,
    @ColumnInfo(name = "description") val description : String,
    @ColumnInfo(name = "date") val date : Long,
    @ColumnInfo(name = "time") val time : Long
    )