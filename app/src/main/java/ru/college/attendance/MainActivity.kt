package ru.college.attendance

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.college.attendance.ui.LessonActivity
import ru.college.attendance.ui.StatisticsActivity
import ru.college.attendance.ui.StudentsActivity

/**
 * Главный экран приложения: точка входа и навигация по разделам.
 *
 * Спринт 1: экран содержит название приложения и три кнопки-раздела.
 * Студенты, отметка посещаемости и статистика на этом спринте открывают
 * экраны-заготовки, которые наполняются в следующих спринтах.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.cardStudents).setOnClickListener {
            openScreen(StudentsActivity::class.java)
        }
        findViewById<View>(R.id.cardAttendance).setOnClickListener {
            openScreen(LessonActivity::class.java)
        }
        findViewById<View>(R.id.cardStatistics).setOnClickListener {
            openScreen(StatisticsActivity::class.java)
        }
    }

    private fun openScreen(screen: Class<out AppCompatActivity>) {
        startActivity(Intent(this, screen))
    }
}
