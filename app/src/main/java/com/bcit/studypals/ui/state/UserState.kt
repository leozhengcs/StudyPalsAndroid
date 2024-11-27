package com.bcit.studypals.ui.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserState: ViewModel() {
    private val _studying = MutableLiveData(false)
    val studying: LiveData<Boolean> get() = _studying

    fun setStudying(value: Boolean) {
        _studying.value = value
    }
}