# Registration Field Validation Plan

This plan outlines the changes required to add field validation to the user registration screen.

## User Review Required

> [!IMPORTANT]
> - Validation error messages will be displayed below the fields if they are invalid.
> - The "Next" and "Register" buttons will only proceed if the current step's fields are valid.
> - `CustomOutlinedTextField` will be updated to support optional error messages and `KeyboardOptions`.

## Proposed Changes

### 1. Common UI Components

#### [MODIFY] [LoginScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/login/LoginScreen.kt)
- Update `CustomOutlinedTextField` to include:
    - `errorMessage: String? = null`
    - `keyboardOptions: KeyboardOptions = KeyboardOptions.Default`
- Ensure `isValid` defaults to `errorMessage == null` if not explicitly provided.

---

### 2. Domain Models

#### [MODIFY] [RegistrationModels.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/model/RegistrationModels.kt)
- No changes needed here, existing fields are sufficient.

---

### 3. Presentation Layer (Registration)

#### [MODIFY] [RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)
- Add validation functions for:
    - Mobile Number (10 digits)
    - Email ID (Valid format)
    - Pincode (6 digits)
    - Aadhaar Number (12 digits)
    - PAN Number (Regex: `[A-Z]{5}[0-9]{4}[A-Z]{1}`)
    - Password (8-15 chars, alphanumeric + special chars)
    - PIN (4 digits)
- Update `RegistrationUiState` to include a map or dedicated fields for error messages.
- Update `nextStep()` to validate the current step before proceeding.
- Update `register()` to validate all fields.

#### [MODIFY] [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt)
- Pass validation results and error messages from `RegistrationUiState` to `CustomOutlinedTextField`.
- Configure `KeyboardOptions` for fields (e.g., `KeyboardType.Number` for mobile, pincode, etc.).

## Verification Plan

### Automated Tests
- No automated tests are currently requested, but validation logic will be tested manually.

### Manual Verification
- Verify that entering an invalid mobile number (not 10 digits) shows an error.
- Verify that entering an invalid email format shows an error.
- Verify that entering an invalid pincode (not 6 digits) shows an error.
- Verify that entering an invalid Aadhaar number (not 12 digits) shows an error.
- Verify that entering an invalid PAN number (wrong format) shows an error.
- Verify that password strength requirements are enforced.
- Verify that PIN is exactly 4 digits.
- Ensure the "Next" button only works when all fields in the current step are valid.
- Verify that these changes do not affect the Login screen functionality.
