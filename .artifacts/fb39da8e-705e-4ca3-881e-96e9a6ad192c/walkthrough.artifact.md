# Walkthrough - Permanent Address added to Business Details

I have moved the "Permanent Address" field from the KYC section to the Business Details section in the registration flow. This ensures that all address-related information is collected together.

## Changes Made

### Domain Model
- **RegistrationModels.kt**: Reordered `permanentAddress` in `RegistrationData` to be grouped with business fields.

### Logic
- **RegistrationViewModel.kt**: Updated `validateStep` to move the validation of `permanentAddress` from Step 3 to Step 2.

### UI
- **RegistrationScreen.kt**:
    - Added the `Permanent Address` field in `StepBusiness` (Step 2) right below the Shop Address.
    - Removed the `Permanent Address` field from `StepKycFinance` (Step 3).

## Verification Results

### Automated Tests
- Successfully ran `:app:assembleDebug` to ensure no compilation errors or broken references.

### Manual Verification Required
- Navigate to the Registration flow.
- Verify "Permanent Address" is now in Step 2.
- Verify validation (it's a required field in Step 2 now).
- Verify "Permanent Address" is gone from Step 3.
