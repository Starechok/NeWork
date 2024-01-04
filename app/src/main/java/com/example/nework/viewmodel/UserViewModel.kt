package com.example.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nework.dto.User
import com.example.nework.model.StateModel
import com.example.nework.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val data: LiveData<List<User>> =
        userRepository.data
            .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            userRepository.getAll()
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getUser(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            _user.value = userRepository.getUser(id)
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getUsers(userIds: Set<Long>) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            val list: MutableList<User>  = emptyList<User>().toMutableList()
            for (id in userIds) launch {
                list.add(userRepository.getUser(id))
            }.join()
            _users.value = list
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }
}