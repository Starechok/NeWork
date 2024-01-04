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
import com.example.nework.adapter.PostAdapter
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentPostsBinding
import com.example.nework.dto.Post
import com.example.nework.enums.AttachmentType
import com.example.nework.ui.NewPostFragment.Companion.textArg
import com.example.nework.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostsFragment : Fragment() {

    @Inject
    lateinit var auth: AppAuth

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostsBinding.inflate(inflater, container, false)
        val adapter = PostAdapter(object : PostAdapter.OnInteractionListener {
            override fun onAttachmentClick(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)
                    when (post.attachment?.type) {
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

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                post.attachment?.let {
                    viewModel.changeMedia(Uri.parse(it.url), null, it.type)
                }
                findNavController().navigate(R.id.action_postsFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }

            override fun onLike(post: Post) {
                if (auth.authStateFlow.value.id != 0L) {
                    if (post.ownedByMe) {
                        if (post.likeOwnerIds.isNotEmpty()) {
                            findNavController().navigate(R.id.userListFragment,
                                Bundle().apply {
                                    putLongArray("userIds", post.likeOwnerIds.toLongArray())
                                })
                        }
                    } else {
                        if (!post.likedByMe) viewModel.likeById(post.id)
                        else viewModel.unlikeById(post.id)
                    }
                } else {
                    findNavController().navigate(R.id.signInFragment)
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onAvatarClick(post: Post) {
                if (auth.authStateFlow.value.id == post.authorId) {
                    findNavController().navigate(R.id.myProfileFragment)
                } else {
                    findNavController().navigate(R.id.userProfileFragment,
                        Bundle().apply {
                            putLong("id", post.authorId)
                        })
                }
            }
        })

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swiperefresh.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        binding.fab.setOnClickListener {
            if (auth.authStateFlow.value.id != 0L)
                findNavController().navigate(R.id.action_postsFragment_to_newPostFragment)
            else findNavController().navigate(R.id.signInFragment)
        }

        return binding.root
    }
}