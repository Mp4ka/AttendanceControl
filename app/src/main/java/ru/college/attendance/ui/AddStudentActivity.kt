package ru.college.attendance.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import ru.college.attendance.R
import ru.college.attendance.data.StudentRepository

/**
 * Форма добавления студента (спринт 2).
 *
 * Проверяет обязательные поля, длину значений и дубликаты, затем сохраняет
 * студента в репозиторий и возвращает RESULT_OK на экран списка.
 */
class AddStudentActivity : AppCompatActivity() {

    private lateinit var tilLastName: TextInputLayout
    private lateinit var tilFirstName: TextInputLayout
    private lateinit var tilGroup: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        tilLastName = findViewById(R.id.tilLastName)
        tilFirstName = findViewById(R.id.tilFirstName)
        tilGroup = findViewById(R.id.tilGroup)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { saveStudent() }

        clearErrorOnInput(tilLastName)
        clearErrorOnInput(tilFirstName)
        clearErrorOnInput(tilGroup)
    }

    private fun saveStudent() {
        val lastName = tilLastName.editText?.text?.toString().orEmpty()
        val firstName = tilFirstName.editText?.text?.toString().orEmpty()
        val group = tilGroup.editText?.text?.toString().orEmpty()

        val invalidFields = listOfNotNull(
            validate(tilLastName, lastName),
            validate(tilFirstName, firstName),
            validate(tilGroup, group)
        )
        if (invalidFields.isNotEmpty()) {
            invalidFields.first().editText?.requestFocus()
            return
        }

        if (StudentRepository.isDuplicate(lastName, firstName, group)) {
            tilGroup.error = getString(R.string.error_duplicate)
            tilGroup.editText?.requestFocus()
            return
        }

        StudentRepository.add(lastName, firstName, group)
        setResult(RESULT_OK)
        finish()
    }

    /** Возвращает поле с ошибкой или null, если значение корректно. */
    private fun validate(field: TextInputLayout, rawValue: String): TextInputLayout? {
        val value = rawValue.trim()
        val message = when {
            value.isEmpty() && rawValue.isEmpty() -> getString(R.string.error_required)
            value.isEmpty() -> getString(R.string.error_spaces)
            value.length > MAX_LENGTH -> getString(R.string.error_too_long)
            else -> null
        }
        field.error = message
        return if (message != null) field else null
    }

    private fun clearErrorOnInput(field: TextInputLayout) {
        field.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                field.error = null
            }
        })
    }

    private companion object {
        const val MAX_LENGTH = 40
    }
}
