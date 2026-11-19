package ru.college.attendance.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudentDao {

    @Insert
    suspend fun insert(student: StudentEntity): Long

    @Query("SELECT * FROM students ORDER BY lastName, firstName")
    suspend fun getAll(): List<StudentEntity>

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun count(): Int
}
