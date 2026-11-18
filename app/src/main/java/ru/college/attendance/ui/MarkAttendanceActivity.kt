package ru.college.attendance.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import ru.college.attendance.R
import ru.college.attendance.data.Attendance
import ru.college.attendance.data.AttendanceRepository
import ru.college.attendance.data.AttendanceStatus
import ru.college.attendance.data.Student
import ru.college.attendance.data.StudentRepository

/**
 * Экран отметки посещаемости (спринт 3).
 *
 * Для каждого студента группы показывается выбор статуса. До нажатия
 * «Сохранить отметки» статус можно менять сколько угодно раз; сохраняется
 * ровно одна отметка на студента и занятие.
 */
class MarkAttendanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: View
    private lateinit var btnSave: MaterialButton

    private lateinit var dateIso: String
    private lateinit var subject: String
    private var students: List<Student> = emptyList()
    private val selectedStatuses = mutableMapOf<Long, AttendanceStatus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance)

        val group = intent.getStringExtra(EXTRA_GROUP).orEmpty()
        dateIso = intent.getStringExtra(EXTRA_DATE).orEmpty()
        subject = intent.getStringExtra(EXTRA_SUBJECT).orEmpty().trim()
        val dateLabel = intent.getStringExtra(EXTRA_DATE_LABEL).orEmpty()

        findViewById<TextView>(R.id.tvLessonInfo).text =
            getString(R.string.lesson_info, group, dateLabel, subject)
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.rvAttendance)
        emptyState = findViewById(R.id.emptyMark)
        btnSave = findViewById(R.id.btnSaveMarks)

        students = StudentRepository.getAll().filter { it.groupName == group }
        students.forEach { student ->
            AttendanceRepository.find(student.id, dateIso, subject)?.let { attendance ->
                selectedStatuses[student.id] = attendance.status
            }
        }

        val adapter = AttendanceAdapter(
            statusOf = { studentId -> selectedStatuses[studentId] },
            onStatusSelected = { studentId, status -> selectedStatuses[studentId] = status }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.submitList(students)

        val isEmpty = students.isEmpty()
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        btnSave.isEnabled = !isEmpty

        btnSave.setOnClickListener { saveMarks() }
    }

    private fun saveMarks() {
        val missing = students.firstOrNull { selectedStatuses[it.id] == null }
        if (missing != null) {
            Snackbar.make(
                findViewById(R.id.rootMark),
                R.string.error_mark_all,
                Snackbar.LENGTH_SHORT
            ).show()
            recyclerView.scrollToPosition(students.indexOf(missing))
            return
        }

        val records = students.map { student ->
            Attendance(
                studentId = student.id,
                lessonDate = dateIso,
                subject = subject,
                status = selectedStatuses.getValue(student.id)
            )
        }
        AttendanceRepository.saveAll(records)

        Snackbar.make(
            findViewById(R.id.rootMark),
            R.string.marks_saved,
            Snackbar.LENGTH_LONG
        ).show()
    }

    companion object {
        const val EXTRA_GROUP = "extra_group"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_DATE_LABEL = "extra_date_label"
        const val EXTRA_SUBJECT = "extra_subject"
    }
}
