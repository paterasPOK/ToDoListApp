package com.example.todolist

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapter.ToDoClickInterface
import com.example.todolist.adapter.ToDoRVAdapter
import com.example.todolist.db.ToDo
import com.example.todolist.viewmodel.ToDoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ToDoClickInterface {

    private lateinit var viewModel: ToDoViewModel
    private lateinit var toDoRv: RecyclerView
    private lateinit var cardView: CardView
    private lateinit var txtEmptyList: TextView
    private lateinit var addBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDoRv = findViewById(R.id.todorv)
        addBtn = findViewById(R.id.addBtn)
        cardView = findViewById(R.id.cardEmptyList)
        txtEmptyList = findViewById(R.id.txtEmptyList)

        toDoRv.layoutManager = LinearLayoutManager(this)
        val toDoRvAdapter = ToDoRVAdapter(this)
        toDoRv.adapter = toDoRvAdapter


        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[ToDoViewModel::class.java]

        viewModel.allToDos.observe(this) {list ->
            list?.let {
                toDoRvAdapter.updateList(it)

                if (it.isEmpty()){
                    toDoRv.visibility = View.GONE
                    cardView.visibility = View.VISIBLE
                }else{
                    toDoRv.visibility = View.VISIBLE
                    cardView.visibility = View.GONE
                }
            }
        }

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deleteToDo = toDoRvAdapter.getItemAtPosition(position) as ToDo

                viewModel.deleteToDo(deleteToDo)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView = viewHolder.itemView
                    val paint = Paint()
                    val icon: Bitmap

                    if(dX < 0){
                        icon = BitmapFactory.decodeResource(resources, R.drawable.baseline_delete_24)
                        paint.color = Color.parseColor("#FF0000")

                        c.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), paint)

                        c.drawBitmap(icon,
                        itemView.right.toFloat() - icon.width,
                        itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2, paint)
                    }
                    viewHolder.itemView.translationX = dX
                }
            }

        }
        val drawItemTouchHelper = ItemTouchHelper(itemTouchCallback)
        drawItemTouchHelper.attachToRecyclerView(toDoRv)


        addBtn.setOnClickListener {
            val intent = Intent(this, AddEditToDoActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onToDoClick(toDo: ToDo, viewId: Int) {
        val intent = Intent(this,AddEditToDoActivity::class.java)
        intent.putExtra("type", "Edit")
        intent.putExtra("toDoId", toDo.id)
        intent.putExtra("toDoTitle", toDo.title)
        intent.putExtra("toDoDescription", toDo.description)
        intent.putExtra("toDoDate", toDo.date)
        intent.putExtra("toDoTime", toDo.time)
        startActivity(intent)

    }
}