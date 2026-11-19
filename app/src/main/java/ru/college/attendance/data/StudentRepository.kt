package ru.college.attendance.data

import ru.college.attendance.data.database.StudentDao
import ru.college.attendance.data.database.StudentEntity
import ru.college.attendance.data.database.toModel

/**
 * Репозиторий студентов поверх Room (спринт 4).
 *
 * Данные сохраняются в базе `attendance.db` и не пропадают после перезапуска приложения.
 */
object StudentRepository {

    private lateinit var dao: StudentDao

    fun init(dao: StudentDao) {
        this.dao = dao
    }

    /** Список студентов, отсортированный по фамилии и имени. */
    suspend fun getAll(): List<Student> = dao.getAll().map { it.toModel() }

    /** Добавляет студента и возвращает сохранённую запись с присвоенным id. */
    suspend fun add(lastName: String, firstName: String, groupName: String): Student {
        val entity = StudentEntity(
            lastName = lastName.trim(),
            firstName = firstName.trim(),
            groupName = groupName.trim()
        )
        val id = dao.insert(entity)
        return entity.copy(id = id).toModel()
    }

    /** Удаляет студента; его отметки удаляются каскадно. */
    suspend fun delete(student: Student) {
        dao.deleteById(student.id)
    }

    /** Проверяет, есть ли уже такой студент в этой группе (без учёта регистра). */
    suspend fun isDuplicate(lastName: String, firstName: String, groupName: String): Boolean =
        dao.getAll().any { existing ->
            existing.lastName.equals(lastName.trim(), ignoreCase = true) &&
                existing.firstName.equals(firstName.trim(), ignoreCase = true) &&
                existing.groupName.equals(groupName.trim(), ignoreCase = true)
        }

    suspend fun count(): Int = dao.count()
}
