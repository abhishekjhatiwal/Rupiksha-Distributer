package com.rupiksha.distributer.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rupiksha.distributer.di.AppContainer
import com.rupiksha.distributer.domain.model.RegistrationData
import com.rupiksha.distributer.domain.usecase.GetPincodeDetailsUseCase
import com.rupiksha.distributer.domain.usecase.SendOtpUseCase
import com.rupiksha.distributer.domain.usecase.VerifyOtpUseCase
import com.rupiksha.distributer.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val data: RegistrationData = RegistrationData(),
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val isPincodeLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val fieldErrors: Map<String, String?> = emptyMap(),
    val isOtpSending: Boolean = false,
    val isOtpSent: Boolean = false,
    val isOtpVerifying: Boolean = false,
    val otpSessionId: String? = null,
    val otpInput: String = "",
    val otpTimer: Int = 0
)

class RegistrationViewModel(
    private val appContainer: AppContainer,
    private val getPincodeDetailsUseCase: GetPincodeDetailsUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private var pincodeJob: Job? = null
    private var timerJob: Job? = null

    fun fetchLocationDetails(pincode: String) {
        if (pincode.length != 6) return
        
        pincodeJob?.cancel()
        pincodeJob = viewModelScope.launch {
            _uiState.update { it.copy(isPincodeLoading = true) }
            // Small delay to debounce if needed, though onValueChange already filters for length == 6
            when (val result = getPincodeDetailsUseCase(pincode)) {
                is Resource.Success -> {
                    val info = result.data!!
                    _uiState.update { state ->
                        state.copy(
                            data = state.data.copy(state = info.state, district = info.district),
                            fieldErrors = state.fieldErrors.toMutableMap().apply {
                                remove("state")
                                remove("district")
                            }
                        )
                    }
                }
                is Resource.Error -> {
                    // Optionally clear or show error
                }
                else -> {}
            }
            _uiState.update { it.copy(isPincodeLoading = false) }
        }
    }

    fun updateData(fieldName: String? = null, update: (RegistrationData) -> RegistrationData) {
        _uiState.update { state ->
            val newData = update(state.data)
            // If mobile changes, reset verification
            val updatedData = if (newData.mobile != state.data.mobile) {
                newData.copy(isMobileVerified = false)
            } else {
                newData
            }
            state.copy(
                data = updatedData,
                fieldErrors = if (fieldName != null) {
                    state.fieldErrors.toMutableMap().apply { remove(fieldName) }
                } else {
                    state.fieldErrors
                },
                isOtpSent = if (newData.mobile != state.data.mobile) false else state.isOtpSent,
                otpInput = if (newData.mobile != state.data.mobile) "" else state.otpInput
            )
        }
    }

    fun updateOtpInput(otp: String) {
        _uiState.update { it.copy(otpInput = otp) }
    }

    fun sendOtp() {
        val mobile = _uiState.value.data.mobile
        if (mobile.length != 10) {
            _uiState.update { it.copy(fieldErrors = it.fieldErrors + ("mobile" to "Enter a valid 10-digit mobile number")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isOtpSending = true, error = null) }
            when (val result = sendOtpUseCase(mobile)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isOtpSent = true, otpSessionId = result.data, otpTimer = 60) }
                    startTimer()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> {}
            }
            _uiState.update { it.copy(isOtpSending = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.otpTimer > 0) {
                delay(1000)
                _uiState.update { it.copy(otpTimer = it.otpTimer - 1) }
            }
        }
    }

    fun verifyOtp() {
        val sessionId = _uiState.value.otpSessionId ?: return
        val otp = _uiState.value.otpInput
        if (otp.length < 4) return

        viewModelScope.launch {
            _uiState.update { it.copy(isOtpVerifying = true, error = null) }
            when (val result = verifyOtpUseCase(sessionId, otp)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            data = state.data.copy(isMobileVerified = true),
                            isOtpSent = false,
                            otpInput = "",
                            fieldErrors = state.fieldErrors.toMutableMap().apply { remove("mobile") }
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> {}
            }
            _uiState.update { it.copy(isOtpVerifying = false) }
        }
    }

    fun nextStep() {
        if (validateStep(_uiState.value.currentStep)) {
            if (_uiState.value.currentStep < 5) {
                _uiState.update { it.copy(currentStep = it.currentStep + 1) }
            }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1, fieldErrors = emptyMap()) }
        }
    }

    private fun validateStep(step: Int): Boolean {
        val data = _uiState.value.data
        val errors = mutableMapOf<String, String?>()

        when (step) {
            1 -> {
                if (data.name.isBlank()) errors["name"] = "Name is required"
                if (data.mobile.length != 10) errors["mobile"] = "Mobile number must be 10 digits"
                else if (!data.isMobileVerified) errors["mobile"] = "Please verify your mobile number"
                
                if (data.email.isBlank()) {
                    errors["email"] = "Email is required"
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(data.email).matches()) {
                    errors["email"] = "Enter a valid email address"
                }
            }
            2 -> {
                if (data.shopName.isBlank()) errors["shopName"] = "Shop name is required"
                if (data.shopAddress.isBlank()) errors["shopAddress"] = "Shop address is required"
                if (data.permanentAddress.isBlank()) errors["permanentAddress"] = "Permanent address is required"
                if (data.pincode.length != 6) errors["pincode"] = "Pincode must be 6 digits"
                if (data.district.isBlank()) errors["district"] = "District is required"
                if (data.state.isBlank()) errors["state"] = "State is required"
            }
            3 -> {
                if (data.adharNumber.length != 12) errors["adharNumber"] = "Adhar number must be 12 digits"
                if (!data.panNumber.matches(Regex("[A-Z]{5}[0-9]{4}[A-Z]"))) {
                    errors["panNumber"] = "Enter a valid PAN number (ABCDE1234F)"
                }
            }
            4 -> {
                if (data.adharFrontUri == null) errors["adharFront"] = "Aadhaar Front required"
                if (data.adharBackUri == null) errors["adharBack"] = "Aadhaar Back required"
                if (data.panFrontUri == null) errors["panFront"] = "PAN Front required"
                if (data.panBackUri == null) errors["panBack"] = "PAN Back required"
                if (data.photoWithEmployeeUri == null) errors["photoEmployee"] = "Photo required"
                if (data.shopPhotoUri == null) errors["shopPhoto"] = "Shop Photo required"
            }
            5 -> {
                // Password: 8-15 characters, numbers + letters + special chars
                val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,15}$")
                if (!data.password.matches(passwordRegex)) {
                    errors["password"] = "Password: 8-15 chars with number, upper/lower case & special char"
                }
                if (data.password != data.confirmPassword) {
                    errors["confirmPassword"] = "Passwords do not match"
                }
                if (data.pin.length != 4) errors["pin"] = "PIN must be 4 digits"
                if (data.pin != data.confirmPin) {
                    errors["confirmPin"] = "PINs do not match"
                }
            }
        }

        _uiState.update { it.copy(fieldErrors = errors) }
        return errors.isEmpty()
    }

    fun register() {
        if (validateStep(5)) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                // Simulation of registration
                delay(2000)
                _uiState.update { it.copy(isLoading = false, success = true) }
            }
        }
    }
}

class RegistrationViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(
                appContainer = appContainer,
                getPincodeDetailsUseCase = appContainer.getPincodeDetailsUseCase,
                sendOtpUseCase = appContainer.sendOtpUseCase,
                verifyOtpUseCase = appContainer.verifyOtpUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
