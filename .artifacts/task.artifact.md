# Task: Fix Pincode Auto-fetch Robustness

- [x] Update `RegistrationViewModel.kt`
    - [x] Add `pincodeJob` to manage coroutines
    - [x] Improve `fetchLocationDetails` logic (cancellation, error handling)
- [x] Update `RegistrationScreen.kt`
    - [x] Add loading indicator for pincode fetch
    - [x] Add missing `District` field
- [x] Verification
    - [x] Manual test of subsequent edits
