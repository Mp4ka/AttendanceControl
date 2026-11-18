package ru.college.attendance.data

/** Статус посещаемости студента на занятии. */
enum class AttendanceStatus(val title: String) {
    PRESENT("Присутствовал"),
    ABSENT("Отсутствовал"),
    LATE("Опоздал");

    companion object {
        fun byTitle(title: String): AttendanceStatus? = entries.firstOrNull { it.title == title }
    }
}

/**
 * Отметка посещаемости.
 *
 * @property studentId студент, к которому относится отметка
 * @property lessonDate дата занятия в формате ISO `yyyy-MM-dd`
 * @property subject дисциплина или тема занятия
 * @property status итоговый статус студента на занятии
 */
data class Attendance(
    val studentId: Long,
    val lessonDate: String,
    val subject: String,
    val status: AttendanceStatus
)
