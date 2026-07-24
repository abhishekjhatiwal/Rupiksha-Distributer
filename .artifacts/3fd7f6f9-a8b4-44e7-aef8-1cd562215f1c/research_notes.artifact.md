# Research Notes - Registration Document Upload Update

## Current State
- `RegistrationData` holds `adharFrontUri` and `panCardUri`.
- `StepMedia` provides a single entry for "Document Photo (Adhar/PAN)".
- `DocumentType` enum has `ADHAR_PAN`, `PHOTO_EMPLOYEE`, and `SHOP_PHOTO`.
- `MediaItem` is a reusable component for document uploads.

## Requested Changes
1. **Separate Aadhaar and PAN uploads**:
   - Aadhaar: Front and Back photos.
   - PAN: Front and Back photos.
2. **Additional Documents**:
   - Options: Electricity bill (Bijli bill), Voter ID card, Passport.
   - One generic upload option for these.

## Impacted Files
1. `D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/model/RegistrationModels.kt`: Update `RegistrationData` data class.
2. `D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt`:
   - Update `DocumentType` enum.
   - Update `handleImageSelection`.
   - Update `StepMedia` UI to include new upload items and "Additional Documents" section.
