package com.example.todolist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface ToDosDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert (toDo: ToDo)

    @Delete()
    suspend fun delete (toDo: ToDo)

    @Update()
    suspend fun update (toDo: ToDo)

    @Query("Select * from toDos")
    fun getAllToDos() : LiveData<List<ToDo>>

    @Query("Select * from toDos where date = :selectedDate order by time asc")
    fun getToDosForSelectedDate(selectedDate: Long) : LiveData<List<ToDo>>
}