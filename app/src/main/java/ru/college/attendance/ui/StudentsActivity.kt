package ru.college.attendance.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.college.attendance.R

/**
 * Экран-заготовка раздела «Студенты».
 * Спринт 2: список студентов, форма добавления и удаление записей.
 */
class StudentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
    }
}
