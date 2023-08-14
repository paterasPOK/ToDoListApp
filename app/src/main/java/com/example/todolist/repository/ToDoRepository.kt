package com.example.todolist.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todolist.db.ToDo
import com.example.todolist.db.ToDosDao
import java.util.Date

class ToDoRepository(private val toDosDao: ToDosDao) {

    // get all todos from dao class
    val allToDos: LiveData<List<ToDo>> = toDosDao.getAllToDos()

    //add todo to db
    suspend fun insert(toDo: ToDo) {
        toDosDao.insert(toDo)
    }

    //delete todo from db
    suspend fun delete(toDo: ToDo) {
        toDosDao.delete(toDo)
    }

    suspend fun update(toDo: ToDo) {
        toDosDao.update(toDo)
    }

    fun getToDosForSelectedDate(selectedDate: Long): LiveData<List<ToDo>>{
        return toDosDao.getToDosForSelectedDate(selectedDate)
    }
}