package ru.college.attendance

import android.app.Application
import ru.college.attendance.data.AttendanceRepository
import ru.college.attendance.data.StudentRepository
import ru.college.attendance.data.database.DatabaseProvider

/**
 * Инициализирует Room и подключает репозитории к DAO до запуска экранов.
 */
class AttendanceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val database = DatabaseProvider.get(this)
        StudentRepository.init(database.studentDao())
        AttendanceRepository.init(database.attendanceDao())
    }
}
