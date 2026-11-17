package ru.college.attendance.data

/**
 * Карточка студента.
 *
 * @property id уникальный идентификатор (в спринте 2 выдаётся репозиторием в памяти)
 * @property lastName фамилия
 * @property firstName имя
 * @property groupName учебная группа, например «ИС-21»
 */
data class Student(
    val id: Long,
    val lastName: String,
    val firstName: String,
    val groupName: String
) {
    val fullName: String
        get() = "$lastName $firstName"

    val initials: String
        get() = buildString {
            lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
            firstName.firstOrNull()?.let { append(it.uppercaseChar()) }
        }
}
