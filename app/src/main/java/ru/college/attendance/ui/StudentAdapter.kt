package ru.college.attendance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.R
import ru.college.attendance.data.Student

/**
 * Список студентов: карточка с инициалами, ФИО и группой.
 * Удаление доступно кнопкой-корзиной и долгим нажатием на карточку.
 */
class StudentAdapter(
    private val onDelete: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position), onDelete)
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)
        private val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvGroup: TextView = itemView.findViewById(R.id.tvStudentGroup)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(student: Student, onDelete: (Student) -> Unit) {
            tvInitials.text = student.initials
            tvName.text = student.fullName
            tvGroup.text = student.groupName

            btnDelete.setOnClickListener { onDelete(student) }
            itemView.setOnLongClickListener {
                onDelete(student)
                true
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
