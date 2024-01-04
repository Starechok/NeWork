package com.example.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nework.dto.Post
import com.example.nework.model.StateModel
import com.example.nework.repository.WallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2023-01-27T17:00:00.000Z",
    mentionedMe = false,
    likedByMe = false,
)

@HiltViewModel
class WallViewModel @Inject constructor(
    private val repository: WallRepository,
) : ViewModel() {

    val data: LiveData<List<Post>> =
        repository.data
            .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val edited = MutableLiveData(empty)

    fun loadUserWall(userId: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.getUserWall(userId)
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.removeById(id)
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.likeById(id)
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.unlikeById(id)
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

}