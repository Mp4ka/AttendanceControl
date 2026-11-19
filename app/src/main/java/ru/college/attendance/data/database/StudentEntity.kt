package ru.college.attendance.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.college.attendance.data.Student

/** Таблица студентов в Room. */
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val groupName: String
)

fun StudentEntity.toModel(): Student = Student(
    id = id,
    lastName = lastName,
    firstName = firstName,
    groupName = groupName
)

fun Student.toEntity(): StudentEntity = StudentEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    groupName = groupName
)
