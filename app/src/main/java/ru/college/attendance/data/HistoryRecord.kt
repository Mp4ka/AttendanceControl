package ru.college.attendance.data

import ru.college.attendance.data.AttendanceStatus

/** Строка истории посещаемости для экрана статистики. */
data class HistoryRecord(
    val lessonLabel: String,
    val studentName: String,
    val status: AttendanceStatus
)
