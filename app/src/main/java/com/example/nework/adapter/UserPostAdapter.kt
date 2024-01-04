package com.example.nework.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nework.R
import com.example.nework.databinding.CardPostBinding
import com.example.nework.dto.Post
import com.example.nework.dto.publishedFormatted
import com.example.nework.enums.AttachmentType
import com.example.nework.view.load
import com.example.nework.view.loadCircleCrop


class UserPostAdapter(
    private val ownedByMe: Boolean,
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, UserPostViewHolder>(UserPostDiffCallback()) {

    interface OnInteractionListener {
        fun onAttachmentClick(post: Post) {}
        fun onEdit(post: Post) {}
        fun onRemove(post: Post) {}
        fun onLike(post: Post) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostViewHolder {
        return UserPostViewHolder(
            CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onInteractionListener
        )
    }

    override fun onBindViewHolder(holder: UserPostViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, ownedByMe)
        }
    }
}

class UserPostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: UserPostAdapter.OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post, ownedByMe: Boolean) {
        binding.apply {
            author.text = post.author
            published.text = post.publishedFormatted()
            content.text = post.content
            if (post.authorAvatar != null) avatar.loadCircleCrop(
                post.authorAvatar,
                R.drawable.baseline_account_circle_24
            )
            else avatar.setImageResource(R.drawable.baseline_account_circle_24)
            if (ownedByMe) like.isToggleCheckedStateOnClick = false
            like.isChecked = post.likedByMe
            like.text = "${post.likeOwnerIds.size}"
            if (post.attachment != null) {
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> {
                        attachment.load(post.attachment.url, R.drawable.baseline_broken_image_24)
                        attachment.visibility = View.VISIBLE
                        playVideo.visibility = View.GONE
                    }

                    AttachmentType.VIDEO -> {
                        attachment.load(post.attachment.url, R.drawable.baseline_broken_image_24)
                        attachment.visibility = View.VISIBLE
                        playVideo.visibility = View.VISIBLE
                    }

                    AttachmentType.AUDIO -> {
                        attachment.setImageResource(R.drawable.baseline_audio_file)
                        attachment.visibility = View.VISIBLE
                        playVideo.visibility = View.GONE
                    }
                }
            } else {
                attachment.visibility = View.GONE
                playVideo.visibility = View.GONE
            }

            menu.visibility = if (ownedByMe) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            attachment.setOnClickListener {
                onInteractionListener.onAttachmentClick(post)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
        }
    }
}

class UserPostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
