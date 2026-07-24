# Walkthrough - Registration Document Upload Update

I have updated the registration flow to separate Aadhaar and PAN front/back uploads and added a section for additional documents.

## Changes Made

### 1. Updated Registration Data Model
In [RegistrationModels.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/model/RegistrationModels.kt), I updated `RegistrationData` to include:
- `adharFrontUri` and `adharBackUri`
- `panFrontUri` and `panBackUri`
- `additionalDocUri` and `additionalDocType`

### 2. Updated Document Types and Handling
In [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt):
- Updated `DocumentType` enum with new constants: `ADHAR_FRONT`, `ADHAR_BACK`, `PAN_FRONT`, `PAN_BACK`, and `ADDITIONAL_DOC`.
- Updated `handleImageSelection` to correctly map selected images to the new fields in `RegistrationData`.

### 3. Refactored Media Step UI
In [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt), the `StepMedia` composable was significantly updated:
- **Aadhaar Section**: Now shows two separate items for Front and Back photos.
- **PAN Section**: Now shows two separate items for Front and Back photos.
- **Additional Documents**: Added a new section with selectable chips for "Bijli Bill", "Voter ID Card", and "Passport". Once a type is selected, an upload item appears for that specific document.

## Verification Results

### Automated Tests
- Verified that the code compiles without errors after the refactoring.
- Checked for unresolved references and missing imports.

### Manual Verification
- The UI now correctly presents the four mandatory document slots (Aadhaar F/B, PAN F/B).
- The additional document section allows choosing a document type before uploading.
