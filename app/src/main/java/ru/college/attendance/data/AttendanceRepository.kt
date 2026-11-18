package ru.college.attendance.data

/**
 * Хранилище отметок посещаемости в памяти (спринт 3).
 *
 * На занятии у каждого студента ровно одна отметка: повторное сохранение
 * заменяет существующую запись, а не создаёт вторую.
 */
object AttendanceRepository {

    private val records = mutableListOf<Attendance>()

    /** Сохраняет отметки занятия, заменяя прежние значения по тому же студенту и занятию. */
    fun saveAll(items: List<Attendance>) {
        items.forEach { newRecord ->
            val index = records.indexOfFirst { it.sameLessonAs(newRecord) }
            if (index >= 0) {
                records[index] = newRecord
            } else {
                records += newRecord
            }
        }
    }

    /** Отметка конкретного студента на занятии (дата + дисциплина). */
    fun find(studentId: Long, lessonDate: String, subject: String): Attendance? =
        records.firstOrNull {
            it.studentId == studentId &&
                it.lessonDate == lessonDate &&
                it.subject.equals(subject, ignoreCase = true)
        }

    /** Все отметки занятия. */
    fun forLesson(lessonDate: String, subject: String): List<Attendance> =
        records.filter {
            it.lessonDate == lessonDate && it.subject.equals(subject, ignoreCase = true)
        }

    fun all(): List<Attendance> = records.toList()

    private fun Attendance.sameLessonAs(other: Attendance): Boolean =
        studentId == other.studentId &&
            lessonDate == other.lessonDate &&
            subject.equals(other.subject, ignoreCase = true)
}
