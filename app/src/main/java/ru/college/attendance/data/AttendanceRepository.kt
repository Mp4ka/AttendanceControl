package ru.college.attendance.data

import ru.college.attendance.data.database.AttendanceDao
import ru.college.attendance.data.database.toEntity
import ru.college.attendance.data.database.toModel

/**
 * Репозиторий отметок посещаемости поверх Room (спринт 4).
 *
 * Отметки сохраняются в базе; уникальный индекс не позволяет создать
 * две отметки на одного студента и одно занятие.
 */
object AttendanceRepository {

    private lateinit var dao: AttendanceDao

    fun init(dao: AttendanceDao) {
        this.dao = dao
    }

    /** Сохраняет отметки занятия, заменяя прежние значения по тому же студенту и занятию. */
    suspend fun saveAll(items: List<Attendance>) {
        dao.upsertAll(items.map { it.toEntity() })
    }

    /** Отметка конкретного студента на занятии (дата + дисциплина). */
    suspend fun find(studentId: Long, lessonDate: String, subject: String): Attendance? =
        dao.find(studentId, lessonDate, subject)?.toModel()

    /** Все отметки занятия. */
    suspend fun forLesson(lessonDate: String, subject: String): List<Attendance> =
        dao.forLesson(lessonDate, subject).map { it.toModel() }

    suspend fun all(): List<Attendance> = dao.all().map { it.toModel() }
}
