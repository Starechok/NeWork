package com.example.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.adapter.OnUserInteractionListener
import com.example.nework.adapter.UserAdapter
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentUsersBinding
import com.example.nework.dto.User
import com.example.nework.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserListFragment : Fragment() {
    @Inject
    lateinit var auth: AppAuth
    private val viewModel: UserViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = UserAdapter(object : OnUserInteractionListener {
            override fun openProfile(user: User) {
                if (auth.authStateFlow.value.id == user.id) {
                    findNavController().navigate(R.id.myProfileFragment)
                } else {
                    findNavController().navigate(R.id.userProfileFragment,
                        Bundle().apply {
                            putLong("id", user.id)
                        })
                }
            }
        })
        binding.usersList.adapter = adapter

        val userIds: Set<Long> = arguments?.getLongArray("userIds")?.toSet() ?: emptySet()

        lifecycleScope.launchWhenCreated {
            if (userIds.isNotEmpty()) {
                viewModel.getUsers(userIds)
            }
        }

        viewModel.users.observe(viewLifecycleOwner)
        {
            adapter.submitList(it)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.usersProgress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        return binding.root
    }
}