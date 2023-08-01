package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.db.ToDo
import com.example.todolist.viewmodel.ToDoViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditToDoActivity : AppCompatActivity(), View.OnClickListener {

  private lateinit var mCalendar: Calendar
  private lateinit var toolbar: androidx.appcompat.widget.Toolbar
  private lateinit var txtTitle: TextInputLayout
  private lateinit var txtDescription: EditText
  private lateinit var txtDate: EditText
  private lateinit var txtTime: EditText
  private lateinit var btn: MaterialButton
  private lateinit var viewModel: ToDoViewModel

    private var selectedDate = 0L
    private var selectedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_to_do)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[ToDoViewModel::class.java]

        toolbar = findViewById(R.id.toolbarAddTask)
        txtTitle = findViewById(R.id.titleInpLay)
        txtDescription = findViewById(R.id.txtDescription)
        txtDate = findViewById(R.id.dateEdt)
        txtTime = findViewById(R.id.timeEdt)
        btn = findViewById(R.id.saveBtn)

        val type = intent.getStringExtra("type")
        //changeOnEditScreen(type)
        if (type.equals("Edit")){
            val todoTitle = intent.getStringExtra("toDoTitle")
            val todoDescription = intent.getStringExtra("toDoDescription")
            val toDoDate = intent.getLongExtra("toDoDate", 0L)
            val toDoTime = intent.getLongExtra("toDoTime", 0L)
            toolbar.title = "Update To Do"

            txtTitle.editText?.setText(todoTitle)
            txtDescription.setText(todoDescription)
            if (toDoDate > 0) {
                selectedDate = toDoDate // Set the selectedDate variable
                txtDate.setText(formatDate(selectedDate)) // Format and set the date
            }

            if (toDoTime > 0) {
                selectedTime = toDoTime // Set the selectedTime variable
                txtTime.setText(formatTime(selectedTime)) // Format and set the time
            }

        }
        txtDate.setOnClickListener(this)
        txtTime.setOnClickListener(this)
        btn.setOnClickListener(this)



    }

    override fun onClick(v: View?) {

        when(v!!.id) {
            R.id.dateEdt -> {
                openCalendar()
            }

            R.id.timeEdt -> {
                openTimePicker()
            }

            R.id.saveBtn -> {
                saveToDo()
            }
        }
    }

    private fun openCalendar() {

        mCalendar = Calendar.getInstance()
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            {_, year, month, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, month)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                selectedDate = mCalendar.timeInMillis
                txtDate.setText(formatDate(selectedDate))

            },
        year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()


    }


    private fun openTimePicker() {

        mCalendar = Calendar.getInstance()
        val hour = mCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = mCalendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                mCalendar.set(Calendar.MINUTE, minute)

                selectedTime = mCalendar.timeInMillis
                txtTime.setText(formatTime(selectedTime))
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun formatDate(selectedDate: Long): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return sdf.format(Date(selectedDate))
    }

    private fun formatTime(selectedTime: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(selectedTime))
    }

    private fun saveToDo() {
        val type = intent.getStringExtra("type")

        if(type.equals("Edit")){
            val title = txtTitle.editText?.text.toString()
            val description = txtDescription.text.toString()
            val toDoId = intent.getIntExtra("toDoId", -1)

            if (title.isNotEmpty() && description.isNotEmpty() && selectedDate > 0 && selectedTime > 0) {
                if(toDoId != -1) {
                    val updatedToDo = ToDo(id = toDoId ,title = title, description = description, date = selectedDate, time = selectedTime )
                    viewModel.updateToDo(updatedToDo)
                    Toast.makeText(this,"To Do Updated Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }

            }else{
                Toast.makeText(this,"Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }else{
            val title = txtTitle.editText?.text.toString()
            val description = txtDescription.text.toString()
            if (title.isNotEmpty() && description.isNotEmpty() && selectedDate > 0 && selectedTime > 0) {

                val newToDo = ToDo(title = title, description = description, date = selectedDate, time = selectedTime )
                viewModel.addToDo(newToDo)
                Toast.makeText(this,"To Do Added Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }

    }
}