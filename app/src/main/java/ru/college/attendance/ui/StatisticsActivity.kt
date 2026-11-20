package ru.college.attendance.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import ru.college.attendance.R
import ru.college.attendance.data.Attendance
import ru.college.attendance.data.AttendanceRepository
import ru.college.attendance.data.AttendanceStatus
import ru.college.attendance.data.HistoryRecord
import ru.college.attendance.data.Student
import ru.college.attendance.data.StudentRepository
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Экран статистики (спринт 5).
 *
 * Показывает количество занятий, присутствий, пропусков и опозданий,
 * историю отметок и фильтры по группе, студенту и дате.
 */
class StatisticsActivity : AppCompatActivity() {

    private val displayFormat = SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru"))
    private val storageFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var etGroupFilter: MaterialAutoCompleteTextView
    private lateinit var etStudentFilter: MaterialAutoCompleteTextView
    private lateinit var etDateFilter: MaterialAutoCompleteTextView

    private lateinit var tvLessonsCount: TextView
    private lateinit var tvPresentCount: TextView
    private lateinit var tvAbsentCount: TextView
    private lateinit var tvLateCount: TextView
    private lateinit var tvHistoryCount: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyHistory: View
    private lateinit var tvEmptyTitle: TextView
    private lateinit var tvEmptyHint: TextView

    private lateinit var adapter: HistoryAdapter

    private var students: List<Student> = emptyList()
    private var records: List<Attendance> = emptyList()

    private var groups: List<String> = emptyList()
    private var dates: List<String> = emptyList()
    private var studentOptions: List<Student> = emptyList()

    private var selectedGroup: String? = null
    private var selectedStudent: Student? = null
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        etGroupFilter = findViewById(R.id.etGroupFilter)
        etStudentFilter = findViewById(R.id.etStudentFilter)
        etDateFilter = findViewById(R.id.etDateFilter)

        tvLessonsCount = findViewById(R.id.tvLessonsCount)
        tvPresentCount = findViewById(R.id.tvPresentCount)
        tvAbsentCount = findViewById(R.id.tvAbsentCount)
        tvLateCount = findViewById(R.id.tvLateCount)
        tvHistoryCount = findViewById(R.id.tvHistoryCount)

        recyclerView = findViewById(R.id.rvHistory)
        emptyHistory = findViewById(R.id.emptyHistory)
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle)
        tvEmptyHint = findViewById(R.id.tvEmptyHint)

        adapter = HistoryAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        etGroupFilter.setOnClickListener { etGroupFilter.showDropDown() }
        etStudentFilter.setOnClickListener { etStudentFilter.showDropDown() }
        etDateFilter.setOnClickListener { etDateFilter.showDropDown() }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            students = StudentRepository.getAll()
            records = AttendanceRepository.all()
            groups = students.map { it.groupName }.distinct().sorted()
            dates = records.map { it.lessonDate }.distinct().sortedDescending()

            setupGroupFilter()
            setupDateFilter()
            setupStudentFilter()
            applyFilters()
        }
    }

    private fun setupGroupFilter() {
        val labels = listOf(getString(R.string.filter_all_groups)) + groups
        etGroupFilter.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, labels))
        etGroupFilter.setText(labels.first(), false)
        etGroupFilter.setOnItemClickListener { _, _, position, _ ->
            selectedGroup = if (position == 0) null else groups[position - 1]
            setupStudentFilter()
            applyFilters()
        }
    }

    private fun setupStudentFilter() {
        studentOptions = students.filter { student ->
            selectedGroup == null || student.groupName == selectedGroup
        }
        val labels = listOf(getString(R.string.filter_all_students)) +
            studentOptions.map { it.fullName }

        etStudentFilter.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, labels))
        val currentIndex = studentOptions.indexOfFirst { it.id == selectedStudent?.id }
        if (currentIndex >= 0) {
            etStudentFilter.setText(labels[currentIndex + 1], false)
        } else {
            selectedStudent = null
            etStudentFilter.setText(labels.first(), false)
        }
        etStudentFilter.setOnItemClickListener { _, _, position, _ ->
            selectedStudent = if (position == 0) null else studentOptions[position - 1]
            applyFilters()
        }
    }

    private fun setupDateFilter() {
        val labels = listOf(getString(R.string.filter_all_dates)) + dates.map { formatDate(it) }
        etDateFilter.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, labels))
        val currentIndex = dates.indexOf(selectedDate)
        if (currentIndex >= 0) {
            etDateFilter.setText(labels[currentIndex + 1], false)
        } else {
            selectedDate = null
            etDateFilter.setText(labels.first(), false)
        }
        etDateFilter.setOnItemClickListener { _, _, position, _ ->
            selectedDate = if (position == 0) null else dates[position - 1]
            applyFilters()
        }
    }

    private fun applyFilters() {
        val studentIdsByGroup = selectedGroup?.let { group ->
            students.filter { it.groupName == group }.map { it.id }.toSet()
        }

        val filtered = records.filter { record ->
            (studentIdsByGroup == null || record.studentId in studentIdsByGroup) &&
                (selectedStudent == null || record.studentId == selectedStudent?.id) &&
                (selectedDate == null || record.lessonDate == selectedDate)
        }

        val lessons = filtered.map { "${it.lessonDate}|${it.subject}" }.distinct().size
        val present = filtered.count { it.status == AttendanceStatus.PRESENT }
        val absent = filtered.count { it.status == AttendanceStatus.ABSENT }
        val late = filtered.count { it.status == AttendanceStatus.LATE }

        tvLessonsCount.text = lessons.toString()
        tvPresentCount.text = present.toString()
        tvAbsentCount.text = absent.toString()
        tvLateCount.text = late.toString()

        val studentsById = students.associateBy { it.id }
        val history = filtered
            .sortedWith(compareByDescending<Attendance> { it.lessonDate }.thenByDescending { it.subject })
            .map { record ->
                HistoryRecord(
                    lessonLabel = getString(
                        R.string.history_lesson,
                        formatDate(record.lessonDate),
                        record.subject
                    ),
                    studentName = studentsById[record.studentId]?.fullName ?: "",
                    status = record.status
                )
            }

        adapter.submitList(history)
        tvHistoryCount.text = getString(R.string.history_count, history.size)

        val isEmpty = history.isEmpty()
        emptyHistory.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE

        if (isEmpty) {
            val noRecordsAtAll = records.isEmpty()
            tvEmptyTitle.text = getString(
                if (noRecordsAtAll) R.string.history_empty else R.string.history_filtered_empty
            )
            tvEmptyHint.text = getString(
                if (noRecordsAtAll) R.string.history_empty_hint else R.string.history_filtered_hint
            )
        }
    }

    private fun formatDate(isoDate: String): String {
        val parsed = runCatching { storageFormat.parse(isoDate) }.getOrNull()
        return if (parsed != null) displayFormat.format(parsed) else isoDate
    }
}
