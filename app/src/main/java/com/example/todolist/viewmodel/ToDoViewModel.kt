package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.db.ToDo
import com.example.todolist.db.ToDoDatabase
import com.example.todolist.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel (application: Application): AndroidViewModel(application){

    val allToDos: LiveData<List<ToDo>>
    private val repository: ToDoRepository



    init {
        val dao = ToDoDatabase.getToDosDatabase(application).getToDosDao()
        repository = ToDoRepository(dao)
        allToDos = repository.allToDos
    }

    fun addToDo(toDo: ToDo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(toDo)
    }

    fun deleteToDo(toDo: ToDo) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(toDo)
    }

    fun updateToDo(toDo: ToDo) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(toDo)
    }

    fun getToDosForSelectedDate(selectedDate: Long): LiveData<List<ToDo>>{
        return repository.getToDosForSelectedDate(selectedDate)
    }
}