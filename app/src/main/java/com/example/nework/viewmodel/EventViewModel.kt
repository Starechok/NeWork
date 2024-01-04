package com.example.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.nework.auth.AppAuth
import com.example.nework.dto.Event
import com.example.nework.dto.MediaUpload
import com.example.nework.enums.AttachmentType
import com.example.nework.enums.EventType
import com.example.nework.model.MediaModel
import com.example.nework.model.StateModel
import com.example.nework.repository.EventRepository
import com.example.nework.utils.Helper
import com.example.nework.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2023-01-27T17:00:00.000Z",
    datetime = "2023-01-27T17:00:00.000Z",
    type = EventType.ONLINE,
    speakerIds = emptySet()
)

private val noMedia = MediaModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    auth: AppAuth,
) : ViewModel() {

    private val cached: Flow<PagingData<Event>> = repository
        .data
//        .map { pagingData ->
//            pagingData.map { it as Post }
//        }
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Event>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached
                .map { pagingData ->
                    pagingData.map { item ->
                        item.copy(
                            ownedByMe = item.authorId == myId,
                            likedByMe = item.likeOwnerIds.contains(myId),
                            participatedByMe = item.participantsIds.contains(myId),
                        )
                    }
                }
        }

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val edited = MutableLiveData(empty)

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    fun edit(event: Event) {
        edited.value = event
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun changeDatetime(date: String, time: String) {
        val datetime = Helper.getDatetime(date, time)
        if (edited.value?.datetime == datetime) {
            return
        }
        edited.value = edited.value?.copy(datetime = datetime)
    }

    fun changeType(type: EventType) {
        if (edited.value?.type == type) {
            return
        }
        edited.value = edited.value?.copy(type = type)
    }

    fun changeMedia(uri: Uri?, array: ByteArray?, attachmentType: AttachmentType?) {
        _media.value = MediaModel(uri, array, attachmentType)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.unlikeById(id)
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun participateById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.participate(id)
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun doNotParticipateById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.doNotParticipate(id)
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {

                    if (_media.value?.byteArray != null) {
                        repository.saveWithAttachment(
                            it,
                            MediaUpload(_media.value?.byteArray!!),
                            _media.value?.attachmentType!!
                        )
                    } else {
                        repository.save(it)
                    }

                    _eventCreated.value = Unit
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        edited.value = empty
        _media.value = noMedia
    }

}