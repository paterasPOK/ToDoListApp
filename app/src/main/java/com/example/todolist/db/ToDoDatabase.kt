package com.example.todolist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToDo::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun getToDosDao(): ToDosDao

    companion object {
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getToDosDatabase(context: Context) : ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                ToDoDatabase::class.java,
                "todos_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}