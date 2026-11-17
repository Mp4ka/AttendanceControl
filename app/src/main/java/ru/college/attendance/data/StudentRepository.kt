package ru.college.attendance.data

/**
 * Временное хранилище студентов в памяти приложения (спринт 2).
 *
 * Данные исчезают после полного закрытия процесса. На спринте 4 этот класс
 * заменится на Room-репозиторий с теми же методами, поэтому экраны не изменятся.
 */
object StudentRepository {

    private val students = mutableListOf<Student>()
    private var nextId = 1L

    /** Список студентов, отсортированный по фамилии и имени. */
    fun getAll(): List<Student> = students.sortedWith(
        compareBy({ it.lastName.lowercase() }, { it.firstName.lowercase() })
    )

    /** Добавляет студента и возвращает сохранённую запись. */
    fun add(lastName: String, firstName: String, groupName: String): Student {
        val student = Student(
            id = nextId++,
            lastName = lastName.trim(),
            firstName = firstName.trim(),
            groupName = groupName.trim()
        )
        students += student
        return student
    }

    /** Удаляет студента; возвращает true, если запись была найдена. */
    fun delete(student: Student): Boolean = students.remove(student)

    /** Проверяет, есть ли уже такой студент в этой группе (без учёта регистра). */
    fun isDuplicate(lastName: String, firstName: String, groupName: String): Boolean =
        students.any { existing ->
            existing.lastName.equals(lastName.trim(), ignoreCase = true) &&
                existing.firstName.equals(firstName.trim(), ignoreCase = true) &&
                existing.groupName.equals(groupName.trim(), ignoreCase = true)
        }

    fun count(): Int = students.size
}
