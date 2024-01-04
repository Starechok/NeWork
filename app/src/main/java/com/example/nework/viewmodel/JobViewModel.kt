package com.example.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nework.dto.Job
import com.example.nework.model.StateModel
import com.example.nework.repository.JobRepository
import com.example.nework.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
)

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: JobRepository,
) : ViewModel() {

    val data: LiveData<List<Job>> =
        repository.data
            .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val edited = MutableLiveData(empty)

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated


    fun loadJobs(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            repository.getUserJobs(id)
            _dataState.value = StateModel()
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun save() {
        edited.value?.let { job ->
            viewModelScope.launch {
                _dataState.postValue(StateModel(loading = true))
                try {
                    repository.save(job)
                    _dataState.postValue(StateModel())
                    _jobCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.postValue(StateModel(error = true))
                }
            }
        }
        edited.value = empty
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun changeJob(job: Job) {
        val name = job.name.trim()
        if (edited.value?.name != name) {
            edited.value = edited.value?.copy(name = name)
        }

        val position = job.position.trim()
        if (edited.value?.position != position) {
            edited.value = edited.value?.copy(position = position)
        }

        if (edited.value?.start != job.start) {
            edited.value = edited.value?.copy(start = job.start)
        }

        if (edited.value?.finish != job.finish) {
            edited.value = edited.value?.copy(finish = job.finish)
        }

        val link = job.link?.trim()
        if (edited.value?.link != link) {
            edited.value = edited.value?.copy(link = link)
        }
    }

    fun removeById(id: Long) =
        viewModelScope.launch {
            _dataState.postValue(StateModel(loading = true))
            try {
                repository.removeById(id)
                _dataState.postValue(StateModel())
            } catch (e: Exception) {
                _dataState.postValue(StateModel(error = true))
            }
        }
}