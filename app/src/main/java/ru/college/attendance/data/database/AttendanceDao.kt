package ru.college.attendance.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AttendanceDao {

    /** Вставка с заменой: уникальный индекс не даёт создать вторую отметку на то же занятие. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<AttendanceEntity>)

    @Query(
        "SELECT * FROM attendance " +
            "WHERE studentId = :studentId AND lessonDate = :lessonDate AND subject = :subject LIMIT 1"
    )
    suspend fun find(studentId: Long, lessonDate: String, subject: String): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE lessonDate = :lessonDate AND subject = :subject")
    suspend fun forLesson(lessonDate: String, subject: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendance")
    suspend fun all(): List<AttendanceEntity>
}
