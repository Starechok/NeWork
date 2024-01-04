package com.example.nework.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.nework.R
import com.example.nework.adapter.EventAdapter
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentEventsBinding
import com.example.nework.dto.Event
import com.example.nework.enums.AttachmentType
import com.example.nework.utils.Helper
import com.example.nework.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EventsFragment : Fragment() {
    @Inject
    lateinit var auth: AppAuth

    private val viewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventsBinding.inflate(inflater, container, false)
        val adapter = EventAdapter(object : EventAdapter.OnInteractionListener {
            override fun onAttachmentClick(event: Event) {
                try {
                    val uri = Uri.parse(event.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)
                    when (event.attachment?.type) {
                        AttachmentType.IMAGE -> intent.setDataAndType(uri, "image/*")
                        AttachmentType.VIDEO -> intent.setDataAndType(uri, "video/*")
                        AttachmentType.AUDIO -> intent.setDataAndType(uri, "audio/*")
                        else -> throw Exception("Bad attachment type")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onEdit(event: Event) {
                viewModel.edit(event)
                event.attachment?.let {
                    viewModel.changeMedia(Uri.parse(it.url), null, it.type)
                }
                findNavController().navigate(
                    R.id.action_eventsFragment_to_newEventFragment,
                    Bundle().apply {
                        putString("date", Helper.getDate(event.datetime))
                        putString("time", Helper.getTime(event.datetime))
                        putString("content", event.content)
                    })
            }

            override fun onLike(event: Event) {
                if (auth.authStateFlow.value.id != 0L) {
                    if (event.ownedByMe) {
                        if (event.likeOwnerIds.isNotEmpty()) {
                            findNavController().navigate(R.id.userListFragment,
                                Bundle().apply {
                                    putLongArray("userIds", event.likeOwnerIds.toLongArray())
                                })
                        }
                    } else {
                        if (!event.likedByMe) viewModel.likeById(event.id)
                        else viewModel.unlikeById(event.id)
                    }
                } else {
                    findNavController().navigate(R.id.signInFragment)
                }
            }

            override fun onParticipate(event: Event) {
                if (auth.authStateFlow.value.id != 0L) {
                    if (event.ownedByMe) {
                        if (event.participantsIds.isNotEmpty()) {
                            findNavController().navigate(R.id.userListFragment,
                                Bundle().apply {
                                    putLongArray("userIds", event.participantsIds.toLongArray())
                                })
                        }
                    } else {
                        if (!event.participatedByMe) viewModel.participateById(event.id)
                        else viewModel.doNotParticipateById(event.id)
                    }
                } else {
                    findNavController().navigate(R.id.signInFragment)
                }
            }

            override fun onRemove(event: Event) {
                viewModel.removeById(event.id)
            }

            override fun onAvatarClick(event: Event) {
                if (auth.authStateFlow.value.id == event.authorId) {
                    findNavController().navigate(R.id.myProfileFragment)
                } else {
                    findNavController().navigate(R.id.userProfileFragment,
                        Bundle().apply {
                            putLong("id", event.authorId)
                        })
                }
            }
        })

        binding.eventsList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.eventsSwiperefresh.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }

        binding.eventsSwiperefresh.setOnRefreshListener(adapter::refresh)

        binding.eventsFab.setOnClickListener {
            if (auth.authStateFlow.value.id != 0L)
                findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
            else findNavController().navigate(R.id.signInFragment)
        }

        return binding.root
    }

}