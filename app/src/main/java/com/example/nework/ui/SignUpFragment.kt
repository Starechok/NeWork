package com.example.nework.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentSignUpBinding
import com.example.nework.utils.AndroidUtils.hideKeyboard
import com.example.nework.viewmodel.SignUpViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider

import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment: Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        }

        with(binding) {
            buttonSignUp.setOnClickListener {
                let {
                    if (
                        it.editTextFieldLogin.text.isNullOrBlank() ||
                        it.editTextFieldPassword.text.isNullOrBlank() ||
                        it.editTextFieldRepeatPassword.text.isNullOrBlank() ||
                        it.editTextFieldName.text.isNullOrBlank()
                    ) {
                        Toast.makeText(
                            activity,
                            "All fields are required",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (
                            textFieldPassword.editText?.text.toString() ==
                            textFieldRepeatPassword.editText?.text.toString()
                        ) {
                            viewModel.registrationUser(
                                textFieldLogin.editText?.text.toString(),
                                textFieldPassword.editText?.text.toString(),
                                textFieldName.editText?.text.toString()
                            )
                            hideKeyboard(requireView())
                        } else
                            textFieldRepeatPassword.error =
                                "the password is incorrect"
                    }
                }
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri)
                    }
                }
            }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.BOTH)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        viewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.registrationError -> {
                    binding.textFieldPassword.error = getString(R.string.error_registration)
                }
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentSignUp.isVisible = it.loading
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                return@observe
            }
            binding.pickPhoto.setImageURI(it.uri)
        }

        return binding.root
    }
}