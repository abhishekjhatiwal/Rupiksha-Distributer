# Implementation Plan - Registration Form Validation

Implement field-level validation for the registration flow to ensure data integrity and a better user experience.

## User Review Required

> [!IMPORTANT]
> I will be adding validation for the following fields as requested:
> - **Mobile**: 10 digits.
> - **Email**: Valid email format.
> - **Pincode**: 6 digits.
> - **Aadhaar**: 12 digits.
> - **PAN**: Valid PAN format (e.g., `ABCDE1234F`).
> - **Password**: 8-15 characters, including numbers, letters, and special characters.
> - **Security Pin**: 4 digits.
>
> I will also add "Not Empty" validation for other mandatory fields like Name, Shop Name, Address, etc.
>
> **UI Change**: I will update `CustomOutlinedTextField` to show error messages below the field when validation fails.

## Proposed Changes

### Domain/Utility Layer

#### [NEW] [ValidationUtils.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/util/ValidationUtils.kt)
- Create regex patterns and functions for:
    - `isValidMobile(String)`
    - `isValidEmail(String)`
    - `isValidPincode(String)`
    - `isValidAadhaar(String)`
    - `isValidPan(String)`
    - `isStrongPassword(String)`
    - `isValidPin(String)`

### Presentation Layer

#### [MODIFY] [RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)
- Update `RegistrationUiState` to include `val errors: Map<String, String?> = emptyMap()`.
- Implement `validateCurrentStep(): Boolean` function to check fields in the current step and update the `errors` map.
- Modify `nextStep()` to call `validateCurrentStep()` before proceeding.
- Modify `register()` to perform a final validation of all steps.
- Add `clearError(fieldKey: String)` to clear error when user starts typing again.

#### [MODIFY] [LoginScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/login/LoginScreen.kt)
- Update `CustomOutlinedTextField` to accept an `errorMessage: String?` parameter.
- Display the `errorMessage` in red below the `OutlinedTextField` if it's not null.

#### [MODIFY] [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt)
- Update all `CustomOutlinedTextField` usages to pass the corresponding error from `uiState.errors`.
- Ensure `onValueChange` calls `viewModel.clearError(fieldKey)`.

## Verification Plan

### Automated Tests
- I will verify the build succeeds.

### Manual Verification
- Deploy the app and go through the registration steps.
- Try to proceed with invalid data (e.g., 9-digit mobile, weak password) and verify that:
    1. The "Next" button shows errors or prevents navigation.
    2. Error messages appear below the invalid fields.
    3. Errors disappear when the user corrects the input.
- Complete registration with valid data to ensure the flow is still functional.
