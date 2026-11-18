package ru.college.attendance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.R
import ru.college.attendance.data.AttendanceStatus
import ru.college.attendance.data.Student

/**
 * Список студентов на занятии с выбором статуса посещаемости.
 * Ровно один статус на студента; выбор сообщается наружу сразу.
 */
class AttendanceAdapter(
    private val statusOf: (Long) -> AttendanceStatus?,
    private val onStatusSelected: (Long, AttendanceStatus) -> Unit
) : ListAdapter<Student, AttendanceAdapter.StudentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position), statusOf, onStatusSelected)
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val rgStatus: RadioGroup = itemView.findViewById(R.id.rgStatus)
        private val rbPresent: RadioButton = itemView.findViewById(R.id.rbPresent)
        private val rbAbsent: RadioButton = itemView.findViewById(R.id.rbAbsent)
        private val rbLate: RadioButton = itemView.findViewById(R.id.rbLate)

        fun bind(
            student: Student,
            statusOf: (Long) -> AttendanceStatus?,
            onStatusSelected: (Long, AttendanceStatus) -> Unit
        ) {
            tvStudentName.text = student.fullName
            val status = statusOf(student.id)

            rgStatus.setOnCheckedChangeListener(null)
            rbPresent.isChecked = status == AttendanceStatus.PRESENT
            rbAbsent.isChecked = status == AttendanceStatus.ABSENT
            rbLate.isChecked = status == AttendanceStatus.LATE

            rgStatus.setOnCheckedChangeListener { _, checkedId ->
                val newStatus = when (checkedId) {
                    R.id.rbPresent -> AttendanceStatus.PRESENT
                    R.id.rbAbsent -> AttendanceStatus.ABSENT
                    R.id.rbLate -> AttendanceStatus.LATE
                    else -> null
                }
                if (newStatus != null) {
                    onStatusSelected(student.id, newStatus)
                }
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem == newItem
    }
}
