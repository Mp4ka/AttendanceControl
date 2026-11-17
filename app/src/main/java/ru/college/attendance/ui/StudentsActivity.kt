package ru.college.attendance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.college.attendance.R
import ru.college.attendance.data.Student
import ru.college.attendance.data.StudentRepository

/**
 * Список студентов (спринт 2).
 *
 * Показывает карточки студентов, пустое состояние, счётчик и кнопку добавления.
 * Добавление открывается отдельным экраном, удаление — с подтверждением.
 */
class StudentsActivity : AppCompatActivity() {

    private lateinit var adapter: StudentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: View
    private lateinit var tvCount: TextView
    private lateinit var btnAddStudent: View

    private val addStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(
                findViewById(R.id.rootStudents),
                R.string.student_added,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        recyclerView = findViewById(R.id.rvStudents)
        emptyState = findViewById(R.id.emptyState)
        tvCount = findViewById(R.id.tvCount)
        btnAddStudent = findViewById(R.id.btnAddStudent)

        adapter = StudentAdapter(::confirmDelete)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        btnAddStudent.setOnClickListener { openAddForm() }
        findViewById<View>(R.id.btnAddStudentEmpty).setOnClickListener { openAddForm() }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun openAddForm() {
        addStudentLauncher.launch(Intent(this, AddStudentActivity::class.java))
    }

    private fun refresh() {
        val students = StudentRepository.getAll()
        adapter.submitList(students)

        tvCount.text = getString(R.string.students_count, students.size)

        val isEmpty = students.isEmpty()
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        btnAddStudent.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun confirmDelete(student: Student) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_student_title)
            .setMessage(getString(R.string.delete_student_message, student.fullName))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                StudentRepository.delete(student)
                refresh()
                Snackbar.make(
                    findViewById(R.id.rootStudents),
                    R.string.student_deleted,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}
