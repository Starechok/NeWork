package com.example.nework.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.databinding.FragmentNewEventBinding
import com.example.nework.enums.AttachmentType
import com.example.nework.enums.EventType
import com.example.nework.utils.AndroidUtils
import com.example.nework.view.load
import com.example.nework.view.pickDate
import com.example.nework.view.pickTime
import com.example.nework.viewmodel.EventViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewEventFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()

    private var fragmentBinding: FragmentNewEventBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding

        arguments?.getString("date")
            ?.let(binding.newEventDate::setText)

        arguments?.getString("time")
            ?.let(binding.newEventTime::setText)

        arguments?.getString("content")
            ?.let(binding.newEventEdit::setText)

        binding.newEventEdit.requestFocus()

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

        binding.newEventPickPhoto.setOnClickListener {
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

        binding.newEventTakePhoto.setOnClickListener {
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


        binding.newEventPickVideo.setOnClickListener {
            mediaLauncher.launch("video/*")
        }

        binding.newEventPickAudio.setOnClickListener {
            mediaLauncher.launch("audio/*")
        }

        binding.newEventRemoveAttachment.setOnClickListener {
            viewModel.changeMedia(null, null, null)
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.newEventAttachmentContainer.visibility = View.GONE
                return@observe
            }

            if (it.attachmentType == AttachmentType.IMAGE || it.attachmentType == AttachmentType.VIDEO) {
                binding.newEventAttachmentContainer.visibility = View.VISIBLE
                binding.newEventAttachment.load(it.uri, R.drawable.baseline_broken_image_24)
            }

            if (it.attachmentType == AttachmentType.AUDIO) {
                binding.newEventAttachmentContainer.visibility = View.VISIBLE
                binding.newEventAttachment.setImageResource(R.drawable.baseline_audio_file)
            }
        }

        binding.newEventDate.setOnClickListener {
            context?.let {
                binding.newEventDate.pickDate(it)
            }
        }

        binding.newEventTime.setOnClickListener {
            context?.let {
                binding.newEventTime.pickTime(it)
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        fragmentBinding?.let {
                            if (it.newEventDate.text.isNullOrBlank() ||
                                it.newEventTime.text.isNullOrBlank() ||
                                it.newEventEdit.text.isNullOrBlank()
                            ) {
                                Toast.makeText(
                                    context,
                                    "All fields are required",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return@let
                            }
                            menuItem.isEnabled = false
                            viewModel.changeDatetime(
                                it.newEventDate.text.toString(),
                                it.newEventTime.text.toString()
                            )
                            viewModel.changeType(getEventType(it.newEventType.checkedRadioButtonId))
                            viewModel.changeContent(it.newEventEdit.text.toString())
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

    private fun getEventType(id: Int): EventType {
        return when (id) {
            R.id.radio_button_online -> EventType.ONLINE
            else -> EventType.OFFLINE
        }
    }
}