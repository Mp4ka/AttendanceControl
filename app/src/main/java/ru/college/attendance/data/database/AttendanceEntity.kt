package ru.college.attendance.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.college.attendance.data.Attendance
import ru.college.attendance.data.AttendanceStatus

/**
 * Таблица отметок посещаемости.
 *
 * Уникальный индекс «студент + дата + дисциплина» гарантирует одну отметку
 * на студента и занятие; внешний ключ удаляет отметки вместе со студентом.
 */
@Entity(
    tableName = "attendance",
    indices = [
        Index("studentId"),
        Index(value = ["studentId", "lessonDate", "subject"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val lessonDate: String,
    val subject: String,
    val status: String
)

fun AttendanceEntity.toModel(): Attendance = Attendance(
    studentId = studentId,
    lessonDate = lessonDate,
    subject = subject,
    status = AttendanceStatus.byTitle(status) ?: AttendanceStatus.PRESENT
)

fun Attendance.toEntity(): AttendanceEntity = AttendanceEntity(
    studentId = studentId,
    lessonDate = lessonDate,
    subject = subject,
    status = status.title
)
