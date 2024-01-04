package com.example.nework.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.databinding.FragmentNewPostBinding
import com.example.nework.enums.AttachmentType
import com.example.nework.utils.AndroidUtils
import com.example.nework.utils.StringArg
import com.example.nework.view.load
import com.example.nework.viewmodel.PostViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()

    private var fragmentBinding: FragmentNewPostBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding

        arguments?.textArg
            ?.let(binding.edit::setText)

        binding.edit.requestFocus()

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        it.data?.data?.let {
                            val stream = context?.contentResolver?.openInputStream(it)
                            viewModel.changeMedia(it, stream?.readBytes(), AttachmentType.IMAGE)
                            stream?.close()
                        }
                    }
                }
            }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        val mediaLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val stream = context?.contentResolver?.openInputStream(it)
                    viewModel.changeMedia(uri, stream?.readBytes(), AttachmentType.VIDEO)
                    stream?.close()
                }
            }


        binding.pickVideo.setOnClickListener {
            mediaLauncher.launch("video/*")
        }

        binding.pickAudio.setOnClickListener {
            mediaLauncher.launch("audio/*")
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changeMedia(null, null, null)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            if (it.attachmentType == AttachmentType.IMAGE || it.attachmentType == AttachmentType.VIDEO) {
                binding.photoContainer.visibility = View.VISIBLE
                binding.photo.load(it.uri, R.drawable.baseline_broken_image_24)
            }

            if (it.attachmentType == AttachmentType.AUDIO) {
                binding.photoContainer.visibility = View.VISIBLE
                binding.photo.setImageResource(R.drawable.baseline_audio_file)
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        fragmentBinding?.let {
                            if (it.edit.text.isNullOrBlank()) {
                                Toast.makeText(
                                    context,
                                    "Text content is required",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return@let
                            }
                            menuItem.isEnabled = false
                            viewModel.changeContent(it.edit.text.toString())
                            viewModel.save()
                            AndroidUtils.hideKeyboard(requireView())
                        }
                        true
                    }

                    else -> false
                }

        }, viewLifecycleOwner)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}