package com.example.todolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.db.ToDo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToDoRVAdapter(
    private val toDoClickInterface: ToDoClickInterface
) : RecyclerView.Adapter<ToDoRVAdapter.ViewHolder>(){

    private val allToDos = ArrayList<ToDo>()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            if (v != null) {
                val position = adapterPosition
                val toDo = allToDos[position]
                toDoClickInterface.onToDoClick(toDo, v.id)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.todo_rv, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allToDos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val toDo = allToDos[position]

        holder.txtTitle.text = toDo.title
        holder.txtDescription.text = toDo.description
        holder.txtDate.text = dateFormatter.format(Date(toDo.date))
        holder.txtTime.text = timeFormatter.format(Date(toDo.time))

    }

    fun updateList(it: List<ToDo>) {
        allToDos.clear()
        allToDos.addAll(it)
        notifyDataSetChanged()
    }

    fun getItemAtPosition(position: Int): Any {
        return allToDos[position]
    }

}