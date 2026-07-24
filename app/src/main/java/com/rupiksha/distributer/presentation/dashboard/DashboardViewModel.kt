package com.rupiksha.distributer.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rupiksha.distributer.di.AppContainer
import com.rupiksha.distributer.domain.model.DashboardData
import com.rupiksha.distributer.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val data: DashboardData? = null,
    val error: String? = null
)

class DashboardViewModel(private val container: AppContainer) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState
    init { loadDashboard() }
    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState(isLoading = true)
            when(val result = container.repository.getDashboardData()) {
                is Resource.Success -> _uiState.value = DashboardUiState(data = result.data)
                is Resource.Error -> _uiState.value = DashboardUiState(error = result.message)
                else -> {}
            }
        }
    }
}

class DashboardViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(container) as T
    }
}