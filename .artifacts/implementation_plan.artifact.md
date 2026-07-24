# Implementation Plan - Fix Pincode Auto-fetch Not Triggering on Subsequent Edits

The user reported that the pincode API is not called again after the first successful auto-fill when they edit the pincode. This plan aims to make the pincode fetching logic more robust by handling concurrency and providing feedback.

## Proposed Changes

### 1. Presentation Layer

#### [MODIFY] [RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)
- Use a `Job` to manage the pincode fetching coroutine, ensuring previous requests are cancelled when a new one starts.
- Add a check to avoid re-fetching if the pincode hasn't changed (optional, but good for performance).
- Add error handling to clear `state` and `district` if the pincode fetch fails, or at least provide some feedback.
- Add debug logging to verify when the API is being called.

#### [MODIFY] [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt)
- Ensure the `onValueChange` for the pincode field is correctly triggering the ViewModel method.
- Add a visual indicator (like a small loading spinner or changing the trailing icon) when `isPincodeLoading` is true.

### 2. Data Layer

#### [MODIFY] [DistributorApiService.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/remote/api/DistributorApiService.kt)
- Double-check the URL construction to ensure it's always absolute and not affected by the base URL in `AppContainer`.

## Verification Plan

### Automated Tests
- Update `RegistrationViewModelTest` (if it exists) to verify that `fetchLocationDetails` cancels previous jobs.

### Manual Verification
1. Navigate to Step 2.
2. Enter a valid 6-digit pincode -> Verify auto-fill.
3. Delete one digit and type a different one -> Verify auto-fill updates.
4. Paste a 6-digit pincode over an existing one -> Verify auto-fill.
