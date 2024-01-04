package com.example.nework.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nework.R
import com.example.nework.databinding.CardJobBinding
import com.example.nework.dto.Job
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val Job.startFormatted: String
    get() {
        val localDateTime = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME)
        return try {
            localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            start
        }
    }

private val Job.finishFormatted: String
    get() {
        val localDateTime = LocalDateTime.parse(finish, DateTimeFormatter.ISO_DATE_TIME)
        return try {
            localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            finish?: "till now"
        }
    }
class JobAdapter(
    private val ownedByMe: Boolean,
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    interface OnInteractionListener {
        fun onEdit(job: Job)
        fun onRemove(job: Job)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, ownedByMe)
        }
    }
}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val onInteractionListener: JobAdapter.OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job, ownedByMe: Boolean) {

        binding.apply {
            jobName.text = job.name
            jobPosition.text = job.position
            jobStart.text = job.startFormatted
            jobFinish.text = job.finishFormatted
            jobLink.visibility =
                if (job.link == null) GONE else VISIBLE
            jobLink.text = job.link

            jobMenu.visibility = if (ownedByMe) VISIBLE else View.INVISIBLE

            jobMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(job)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(job)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}