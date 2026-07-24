# Implementation Plan - Add Permanent Address to Business Details

The goal is to move the "Permanent Address" field from the KYC & Finance section (Step 3) to the Business Details section (Step 2) in the Registration flow.

## User Review Required

> [!NOTE]
> I am moving the existing `permanentAddress` field from Step 3 to Step 2. If you wanted a *new* separate field and wanted to keep the one in Step 3, please let me know. Based on the request, it seems like a relocation/addition to the Business Details section.

## Proposed Changes

### Presentation Layer

#### [MODIFY] [RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)
- Move validation for `permanentAddress` from `step 3` to `step 2` in the `validateStep` function.

#### [MODIFY] [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt)
- In `StepBusiness` (Step 2), add a new `AnimatedField` with a `CustomOutlinedTextField` for `permanentAddress` below the Shop Address.
- In `StepKycFinance` (Step 3), remove the `permanentAddress` field.

## Verification Plan

### Manual Verification
- Run the app and navigate to the Registration screen.
- Go to Step 2 (Business Details) and verify that "Permanent Address" is present.
- Verify that validation works for both "Shop Address" and "Permanent Address" in Step 2.
- Verify that "Permanent Address" is no longer present in Step 3 (KYC & Finance).
- Test the "Same as Shop Address" checkbox functionality.
