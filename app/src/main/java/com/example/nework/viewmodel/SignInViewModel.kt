package com.example.nework.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nework.api.UserService
import com.example.nework.dto.Token
import com.example.nework.error.ApiError
import com.example.nework.model.StateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val apiService: UserService,
) : ViewModel() {

    val data = MutableLiveData<Token>()

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun authorizationUser(login: String, password: String) {
        viewModelScope.launch {
            _dataState.postValue(StateModel(loading = true))
            try {
                val response = apiService.login(login, password)
                if (!response.isSuccessful) {
                    Log.d("AUTH", response.message())
                    throw ApiError(response.code(), response.message())
                }
                _dataState.postValue(StateModel())
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                _dataState.postValue(StateModel(error = true))
            } catch (e: Exception) {
                _dataState.postValue(StateModel(loginError = true))
            }
        }
    }
}