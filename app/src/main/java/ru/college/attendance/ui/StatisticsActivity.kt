package ru.college.attendance.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.college.attendance.R

/**
 * Экран-заготовка раздела «Статистика».
 * Спринт 5: история посещаемости, фильтры и подсчёт статусов.
 */
class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
    }
}
