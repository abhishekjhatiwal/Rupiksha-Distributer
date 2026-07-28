package com.rupiksha.distributer.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rupiksha.distributer.domain.model.DashboardData
import com.rupiksha.distributer.domain.repository.DistributorRepository
import com.rupiksha.distributer.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val data: DashboardData? = null,
    val error: String? = null
)

class DashboardViewModel(private val repository: DistributorRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState
    init { loadDashboard() }
    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState(isLoading = true)
            when(val result = repository.getDashboardData()) {
                is Resource.Success -> _uiState.value = DashboardUiState(data = result.data)
                is Resource.Error -> _uiState.value = DashboardUiState(error = result.message)
                else -> {}
            }
        }
    }
}
