package com.example.nework.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.databinding.FragmentNewJobBinding
import com.example.nework.dto.Job
import com.example.nework.utils.AndroidUtils
import com.example.nework.view.markRequired
import com.example.nework.view.pickDate
import com.example.nework.viewmodel.JobViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private val viewModel: JobViewModel by activityViewModels()

    private var fragmentBinding: FragmentNewJobBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding
        binding.newJobName.markRequired()
        binding.newJobPosition.markRequired()
        binding.newJobStart.markRequired()

        arguments?.getString("name")
            ?.let(binding.newJobNameEdit::setText)

        arguments?.getString("position")
            ?.let(binding.newJobPositionEdit::setText)

        arguments?.getString("start")
            ?.let(binding.newJobStartEdit::setText)

        arguments?.getString("finish")
            ?.let(binding.newJobFinishEdit::setText)

        arguments?.getString("link")
            ?.let(binding.newJobLinkEdit::setText)

        binding.newJobName.requestFocus()

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.newJobStartEdit.setOnClickListener {
            context?.let {
                binding.newJobStartEdit.pickDate(it)
            }
        }

        binding.newJobFinishEdit.setOnClickListener {
            context?.let {
                binding.newJobFinishEdit.pickDate(it)
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        AndroidUtils.hideKeyboard(requireView())
                        fragmentBinding?.let {
                            if (it.newJobNameEdit.text.isNullOrBlank() ||
                                it.newJobPositionEdit.text.isNullOrBlank() ||
                                it.newJobStartEdit.text.isNullOrBlank()
                            ) {
                                Snackbar.make(
                                    binding.root,
                                    "Some fields are required",
                                    Snackbar.LENGTH_LONG
                                ).show()
                                return@let
                            }
                            menuItem.isEnabled = false
                            viewModel.changeJob(
                                Job(
                                    0,
                                    it.newJobNameEdit.text.toString(),
                                    it.newJobPositionEdit.text.toString(),
                                    it.newJobStartEdit.text.toString(),
                                    it.newJobFinishEdit.text.toString(),
                                    it.newJobLinkEdit.text.toString()
                                )
                            )
                            viewModel.save()
                        }
                        true
                    }

                    else -> false
                }
        }, viewLifecycleOwner)

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}