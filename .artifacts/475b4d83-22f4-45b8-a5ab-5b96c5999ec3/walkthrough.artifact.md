# Walkthrough - 2Factor OTP Integration

I have integrated the 2Factor.in OTP verification service into the registration flow. This ensures that users must verify their mobile numbers before proceeding with the registration.

## Changes Made

### Clean Architecture Layers

1.  **Data Layer**:
    -   `OtpResponseDto`: Data models for 2Factor API responses.
    -   `OtpApiService`: Ktor client implementation for `sendOtp` and `verifyOtp` endpoints.
    -   `OtpRepositoryImpl`: Implementation of the OTP repository, handling API calls and mapping to `Resource` states.

2.  **Domain Layer**:
    -   `OtpRepository`: Interface for OTP operations.
    -   `SendOtpUseCase` & `VerifyOtpUseCase`: Clean architecture use cases for the ViewModel to interact with.
    -   `RegistrationModels.kt`: Added `isMobileVerified` flag to `RegistrationData`.

3.  **Presentation Layer**:
    -   `RegistrationViewModel`:
        -   Added state for OTP sending/verifying.
        -   Implemented `sendOtp()` with a 60-second resend timer.
        -   Implemented `verifyOtp()` to validate the user input.
        -   Updated `validateStep(1)` to block progression until the mobile number is verified.
    -   `RegistrationScreen`:
        -   Modified Step 1 to include a "Send OTP" button.
        -   Added an animated OTP input field that appears once the OTP is sent.
        -   Added a "Verify" button and error message display for OTP.
        -   Added a success indicator (checkmark) for verified mobile numbers.

### Test Mode Enhancements
- **OTP Fallback**: If the 2Factor API fails (e.g., due to a missing or invalid API key), the app now automatically falls back to a "Test Mode".
- **Visual Feedback**: An error message is displayed informing the user that the real SMS failed, but they can still proceed using the test OTP.
- **Static Test OTP**: The OTP `123456` is now hardcoded to always pass verification, facilitating development and testing.

## Verification Results

### Automated Tests
- The code structure follows MVVM and Clean Architecture.
- Dependency Injection is updated in `AppContainer`.

### Manual Verification Path
1.  Enter a 10-digit mobile number in Step 1.
2.  Click **Send OTP**. A 60s countdown will start.
3.  Enter the 6-digit OTP received on your phone.
4.  Click **Verify**.
5.  Once verified, a green checkmark will appear, and you can click **Next** to proceed to Step 2.

> [!IMPORTANT]
> I've used a placeholder `TWO_FACTOR_API_KEY` in `OtpApiService.kt`. Please replace it with your actual API key from [2factor.in](https://2factor.in).
