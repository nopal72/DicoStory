package com.example.dicostory.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicostory.data.UserRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: UserRepository) : ViewModel() {
   fun logout() {
       viewModelScope.launch {
           repository.logout()
       }
   }
}