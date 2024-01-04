package com.example.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nework.R
import com.example.nework.databinding.CardUserBinding
import com.example.nework.dto.User
import com.example.nework.view.loadCircleCrop

interface OnUserInteractionListener {
    fun openProfile(user: User)
}

class UserAdapter(
    private val onUserInteractionListener: OnUserInteractionListener
) :
    ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding, onUserInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onUserInteractionListener: OnUserInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        with(binding) {
            userName.text = user.name.removeSurrounding("\"")
            if (user.avatar != null) userAvatarCard.loadCircleCrop(
                user.avatar,
                R.drawable.baseline_account_circle_24
            )
            else userAvatarCard.setImageResource(R.drawable.baseline_account_circle_24)

            userView.setOnClickListener {
                onUserInteractionListener.openProfile(user)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}