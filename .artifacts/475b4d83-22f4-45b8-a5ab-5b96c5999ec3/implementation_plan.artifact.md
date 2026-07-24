# Implementation Plan - 2Factor OTP Integration

Integrate the 2Factor API for mobile number verification via OTP during the registration process in `RegistrationScreen.kt`.

## User Review Required

> [!IMPORTANT]
> The implementation requires a 2Factor API Key. I will use a placeholder `TWO_FACTOR_API_KEY` which should be added to your `.env` file or `BuildConfig`.
> Please provide the API key if you have one, or you can update it later.

## Proposed Changes

### Data Layer

#### [NEW] [OtpResponseDto.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/remote/model/OtpResponseDto.kt)
- Define `OtpResponseDto` and `OtpVerifyResponseDto` for 2Factor API responses.

#### [NEW] [OtpApiService.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/remote/api/OtpApiService.kt)
- Create a Ktor-based service to handle `sendOtp` and `verifyOtp` calls to 2Factor.in.

#### [NEW] [OtpRepositoryImpl.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/repository/OtpRepositoryImpl.kt)
- Implement `OtpRepository` to bridge the API and Domain layer.

### Domain Layer

#### [MODIFY] [RegistrationModels.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/model/RegistrationModels.kt)
- Add `isMobileVerified: Boolean` to `RegistrationData`.

#### [NEW] [OtpRepository.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/repository/OtpRepository.kt)
- Define the interface for OTP operations.

#### [NEW] [OtpUseCases.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/usecase/OtpUseCases.kt)
- Implement `SendOtpUseCase` and `VerifyOtpUseCase`.

### Dependency Injection

#### [MODIFY] [AppContainer.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/di/AppContainer.kt)
- Initialize `OtpApiService`, `OtpRepositoryImpl`, and Use Cases.

### Presentation Layer

#### [MODIFY] [RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)
- Update `RegistrationUiState` to include:
    - `otpSessionId: String?`
    - `otpInput: String`
    - `isOtpSent: Boolean`
    - `isOtpSending: Boolean`
    - `isOtpVerifying: Boolean`
    - `otpTimer: Int` (countdown for resending)
- Add methods:
    - `sendOtp()`: Calls `SendOtpUseCase`.
    - `verifyOtp()`: Calls `VerifyOtpUseCase`.
    - `startOtpTimer()`: Handles resend cooldown.
- Update `validateStep(1)` to ensure `isMobileVerified` is true before proceeding.

#### [MODIFY] [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt)
- Update `StepPersonal` UI:
    - Add a "Send OTP" button next to the mobile number field (or below it).
    - If `isOtpSent` is true, show an OTP text field.
    - Add a "Verify" button for the OTP field.
    - Show a "Verified" badge/success icon once OTP is successfully verified.
    - Show the resend timer if OTP was sent.

## Verification Plan

### Manual Verification
1.  Navigate to Registration Screen.
2.  Enter a 10-digit mobile number.
3.  Click "Send OTP" and verify the API call is made (check logs).
4.  Enter the received OTP and click "Verify".
5.  Ensure the "Next" button remains disabled until verification is successful.
6.  Ensure the "Next" button enables after verification.
7.  Check the resend timer functionality.
