package com.rupiksha.distributer.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rupiksha.distributer.domain.usecase.LoginUseCase
import com.rupiksha.distributer.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, pin: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            when (val result = loginUseCase(username, pin)) {
                is Resource.Success -> {
                    _uiState.value = LoginUiState(success = true)
                }
                is Resource.Error -> {
                    _uiState.value = LoginUiState(error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }
}
