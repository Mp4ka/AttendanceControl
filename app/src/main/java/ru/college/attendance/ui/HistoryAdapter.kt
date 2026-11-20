package ru.college.attendance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.R
import ru.college.attendance.data.AttendanceStatus
import ru.college.attendance.data.HistoryRecord

/** История отметок: занятие, студент и статус с цветовой подсказкой. */
class HistoryAdapter : ListAdapter<HistoryRecord, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvLesson: TextView = itemView.findViewById(R.id.tvHistoryLesson)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvHistoryStatus)
        private val tvStudent: TextView = itemView.findViewById(R.id.tvHistoryStudent)

        fun bind(record: HistoryRecord) {
            tvLesson.text = record.lessonLabel
            tvStudent.text = record.studentName
            tvStatus.text = record.status.title

            val colorRes = when (record.status) {
                AttendanceStatus.PRESENT -> R.color.status_present_color
                AttendanceStatus.ABSENT -> R.color.status_absent_color
                AttendanceStatus.LATE -> R.color.status_late_color
            }
            tvStatus.setTextColor(ContextCompat.getColor(itemView.context, colorRes))
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<HistoryRecord>() {
        override fun areItemsTheSame(oldItem: HistoryRecord, newItem: HistoryRecord): Boolean =
            oldItem.lessonLabel == newItem.lessonLabel &&
                oldItem.studentName == newItem.studentName &&
                oldItem.status == newItem.status

        override fun areContentsTheSame(oldItem: HistoryRecord, newItem: HistoryRecord): Boolean =
            oldItem == newItem
    }
}
