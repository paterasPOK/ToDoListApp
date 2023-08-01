package com.example.todolist.adapter

import com.example.todolist.db.ToDo

interface ToDoClickInterface {

    fun onToDoClick(toDo: ToDo, viewId: Int)
}