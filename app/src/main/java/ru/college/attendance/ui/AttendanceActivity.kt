package ru.college.attendance.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import ru.college.attendance.R
import ru.college.attendance.data.StudentRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Экран занятия (спринт 3): группа, дата и дисциплина.
 *
 * Группы берутся из уже добавленных студентов. После заполнения полей
 * открывается экран отметки посещаемости.
 */
class AttendanceActivity : AppCompatActivity() {

    private val displayFormat = SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru"))
    private val storageFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var tilGroup: TextInputLayout
    private lateinit var etGroup: MaterialAutoCompleteTextView
    private lateinit var tilSubject: TextInputLayout
    private lateinit var btnDate: MaterialButton
    private lateinit var btnStart: MaterialButton
    private lateinit var tvNoStudents: TextView

    private var groups: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        tilGroup = findViewById(R.id.tilGroup)
        etGroup = findViewById(R.id.etGroup)
        tilSubject = findViewById(R.id.tilSubject)
        btnDate = findViewById(R.id.btnDate)
        btnStart = findViewById(R.id.btnStart)
        tvNoStudents = findViewById(R.id.tvNoStudents)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        btnDate.text = getString(R.string.lesson_date, displayFormat.format(calendar.time))
        btnDate.setOnClickListener { pickDate() }
        btnStart.setOnClickListener { startMarking() }

        tilSubject.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                tilSubject.error = null
            }
        })

        groups = StudentRepository.getAll().map { it.groupName }.distinct().sorted()
        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, groups)
        etGroup.setAdapter(groupAdapter)
        etGroup.setOnItemClickListener { _, _, position, _ ->
            etGroup.setText(groups[position], false)
            tilGroup.error = null
        }
        etGroup.setOnClickListener { etGroup.showDropDown() }

        if (groups.isEmpty()) {
            tvNoStudents.visibility = View.VISIBLE
            tilGroup.isEnabled = false
            btnStart.isEnabled = false
        }
    }

    private fun pickDate() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                btnDate.text = getString(R.string.lesson_date, displayFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun startMarking() {
        val group = etGroup.text?.toString()?.trim().orEmpty()
        val subject = tilSubject.editText?.text?.toString()?.trim().orEmpty()

        var hasError = false
        if (group.isEmpty() || !groups.contains(group)) {
            tilGroup.error = getString(R.string.error_group_required)
            hasError = true
        } else {
            tilGroup.error = null
        }
        if (subject.isEmpty()) {
            tilSubject.error = getString(R.string.error_required)
            hasError = true
        } else {
            tilSubject.error = null
        }
        if (hasError) return

        val intent = Intent(this, MarkAttendanceActivity::class.java).apply {
            putExtra(MarkAttendanceActivity.EXTRA_GROUP, group)
            putExtra(MarkAttendanceActivity.EXTRA_DATE, storageFormat.format(calendar.time))
            putExtra(MarkAttendanceActivity.EXTRA_DATE_LABEL, displayFormat.format(calendar.time))
            putExtra(MarkAttendanceActivity.EXTRA_SUBJECT, subject)
        }
        startActivity(intent)
    }
}
