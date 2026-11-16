package ru.college.attendance.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.college.attendance.R

/**
 * Экран-заготовка раздела «Отметить посещаемость».
 * Спринт 3: выбор занятия, список студентов и статусы отметки.
 */
class AttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
    }
}
