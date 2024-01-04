package com.example.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nework.R
import com.example.nework.adapter.ProfileAdapter
import com.example.nework.databinding.FragmentProfileBinding
import com.example.nework.view.loadCircleCrop
import com.example.nework.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private val userViewModel by activityViewModels<UserViewModel>()

    private val profileTitles = arrayOf(
        R.string.title_posts,
        R.string.title_jobs
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        val viewPagerProfile = binding.profileViewPager
        val tabLayoutProfile = binding.profileTabLayout
        val id = arguments?.getLong("id")
        if (id != null) {
            userViewModel.getUser(id)
        }

        viewPagerProfile.adapter = ProfileAdapter(this)

        TabLayoutMediator(tabLayoutProfile, viewPagerProfile) { tab, position ->
            tab.text = getString(profileTitles[position])
        }.attach()

        userViewModel.user.observe(viewLifecycleOwner) {
            with(binding) {
                profileUserName.text = it.name.removeSurrounding("\"")
                if (it.avatar != null) {
                    profileAvatar.loadCircleCrop(
                        it.avatar,
                        R.drawable.baseline_account_circle_24
                    )
                } else {
                    profileAvatar.setImageResource(R.drawable.baseline_account_circle_24)
                }
            }
        }

        return binding.root
    }
}