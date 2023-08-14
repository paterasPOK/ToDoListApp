package com.example.todolist

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapter.ToDoClickInterface
import com.example.todolist.adapter.ToDoRVAdapter
import com.example.todolist.db.ToDo
import com.example.todolist.viewmodel.ToDoViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), ToDoClickInterface {

    private lateinit var viewModel: ToDoViewModel
    private lateinit var toDoRv: RecyclerView
    private lateinit var cardView: CardView
    private lateinit var txtEmptyList: TextView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var toolbar: MaterialToolbar
    private lateinit var calendarView: ImageView
    private lateinit var mCalendar: Calendar
    private var selectedDate = 0L
    private var selectedTime = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDoRv = findViewById(R.id.todorv)
        addBtn = findViewById(R.id.addBtn)
        cardView = findViewById(R.id.cardEmptyList)
        txtEmptyList = findViewById(R.id.txtEmptyList)
        toolbar = findViewById(R.id.toolbar)
        calendarView = findViewById(R.id.calendar)
        mCalendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = todayDate()

        toDoRv.layoutManager = LinearLayoutManager(this)
        val toDoRvAdapter = ToDoRVAdapter(this)
        toDoRv.adapter = toDoRvAdapter


        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[ToDoViewModel::class.java]


        searchToDo(todayDate, toDoRvAdapter)
        swipe(toDoRvAdapter)


        addBtn.setOnClickListener {
            val intent = Intent(this, AddEditToDoActivity::class.java)
            startActivity(intent)
        }

        calendarView.setOnClickListener {
            showDatePicker(toDoRvAdapter)
        }

    }

    private fun swipe(toDoRvAdapter: ToDoRVAdapter) {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = Paint()
                    val icon: Bitmap

                    if (dX > 0) {
                        val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete_black)
                        icon = drawable?.toBitmap()!!
                        paint.color = Color.parseColor("#FF0000")

                        c.drawRect(
                            itemView.left.toFloat() + dX, itemView.top.toFloat(),
                            itemView.left.toFloat(), itemView.bottom.toFloat(), paint
                        )

                        c.drawBitmap(
                            icon,
                            itemView.left.toFloat() + icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX = dX
                }
            }

        }
        val drawItemTouchHelper = ItemTouchHelper(itemTouchCallback)
        drawItemTouchHelper.attachToRecyclerView(toDoRv)
    }

    private fun todayDate(): Long {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, day)
        mCalendar.set(Calendar.HOUR_OF_DAY, 0)
        mCalendar.set(Calendar.MINUTE, 0)
        mCalendar.set(Calendar.SECOND, 0)
        mCalendar.set(Calendar.MILLISECOND, 0)

        return mCalendar.timeInMillis
    }

    private fun showDatePicker(toDoRvAdapter: ToDoRVAdapter) {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            {_, year, month, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, month)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                mCalendar.set(Calendar.HOUR_OF_DAY, 0)
                mCalendar.set(Calendar.MINUTE, 0)
                mCalendar.set(Calendar.SECOND, 0)
                mCalendar.set(Calendar.MILLISECOND, 0)

                selectedDate = mCalendar.timeInMillis
                searchToDo(selectedDate, toDoRvAdapter)


            },
            year, month, day)
        datePickerDialog.show()
    }


    private fun searchToDo(selectedDate: Long, toDoRvAdapter: ToDoRVAdapter) {
        Log.d("DEBUG", "Selected date: $selectedDate")
        viewModel.getToDosForSelectedDate(selectedDate).observe(this) { todos ->
            toDoRvAdapter.updateList(todos)
            if (todos.isEmpty()){
                toDoRv.visibility = View.GONE
                cardView.visibility = View.VISIBLE
            }else{
                toDoRv.visibility = View.VISIBLE
                cardView.visibility = View.GONE
            }

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