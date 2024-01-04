package com.example.nework.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nework.api.UserService
import com.example.nework.dto.Token
import com.example.nework.enums.AttachmentType
import com.example.nework.error.ApiError
import com.example.nework.model.MediaModel
import com.example.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val apiService: UserService,
) : ViewModel() {
    val data = MutableLiveData<Token>()

    private val noPhoto = MediaModel()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<MediaModel>
        get() = _photo

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun registrationUser(login: String, password: String, name: String) {
        viewModelScope.launch {
            _dataState.postValue(StateModel(loading = true))
            try {
                val file = photo.value?.uri?.toFile()
                val response = if (file != null) {
                    val media = MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
                    apiService.registerUser(
                        login = login.toRequestBody("text/plain".toMediaType()),
                        pass = password.toRequestBody("text/plain".toMediaType()),
                        name = name.toRequestBody("text/plain".toMediaType()),
                        file = media
                    )
                } else {
                    apiService.registerUser(
                        login = login.toRequestBody("text/plain".toMediaType()),
                        pass = password.toRequestBody("text/plain".toMediaType()),
                        name = name.toRequestBody("text/plain".toMediaType()),
                        file = null
                    )
                }

                if (!response.isSuccessful) {
                    Log.d("AUTH", response.message())
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                data.value = Token(body.id, body.token)
                _dataState.postValue(StateModel())
            } catch (e: IOException) {
                _dataState.postValue(StateModel(error = true))
            } catch (e: Exception) {
                _dataState.postValue(StateModel(registrationError = true))
            }
        }
        _photo.value = noPhoto
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = MediaModel(uri, null, AttachmentType.IMAGE)
    }
}