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
import com.example.nework.adapter.JobAdapter
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentJobsBinding
import com.example.nework.dto.Job
import com.example.nework.viewmodel.JobViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JobsFragment : Fragment() {
    @Inject
    lateinit var auth: AppAuth

    private val viewModel by activityViewModels<JobViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentJobsBinding.inflate(
            inflater,
            container,
            false
        )
        val id = parentFragment?.arguments?.getLong("id")
        val ownedByMe = auth.authStateFlow.value.id == id

        val adapter = JobAdapter(ownedByMe,
            object : JobAdapter.OnInteractionListener {
                override fun onEdit(job: Job) {
                    viewModel.edit(job)
                    findNavController().navigate(
                        R.id.action_jobsFragment_to_newJobFragment,
                        Bundle().apply {
                            putString("name", job.name)
                            putString("position", job.position)
                            putString("start", job.start)
                            job.finish?.let {
                                putString("finish", it)
                            }
                            job.link?.let {
                                putString("link", it)
                            }
                        })
                }

                override fun onRemove(job: Job) {
                    viewModel.removeById(job.id)
                }

            })

        binding.jobsList.adapter = adapter

        lifecycleScope.launchWhenCreated {
            if (id != null) {
                viewModel.loadJobs(id)
            }
        }

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.jobsEmptyText.isVisible = it.isEmpty()
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.jobsProgress.isVisible = state.loading
            binding.jobsSwiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        if (id != null) {
                            viewModel.loadJobs(id)
                        }
                    }
                    .show()
            }
        }

        binding.jobsSwiperefresh.setOnRefreshListener {
            if (id != null) {
                viewModel.loadJobs(id)
            }
        }

        binding.jobsFab.isVisible = ownedByMe

        binding.jobsFab.setOnClickListener {
            findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
        }

        return binding.root
    }
}