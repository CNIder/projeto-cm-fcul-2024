package com.example.projeto_cm_24_25.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel:ViewModel() {
    private val _userName = MutableLiveData<String>()
    val userName : LiveData<String> = _userName

    fun onNameUpdate(newName: String) {
        _userName.value = newName
    }
}