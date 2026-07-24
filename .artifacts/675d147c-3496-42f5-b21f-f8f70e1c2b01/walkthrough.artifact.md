# Walkthrough - User Registration Screen

I have implemented a comprehensive 5-step registration wizard for the Rupiksha Distributor app.

## Changes Made

### 1. Navigation & Entry Point
- Added `Register` route to `Screen.kt`.
- Updated `AppNavGraph.kt` to include the `RegistrationScreen`.
- Added a "Register" link to the `LoginScreen` to allow users to navigate to the registration flow.

### 2. Registration Wizard UI
Implemented a multi-step form in `RegistrationScreen.kt` using a custom state-driven approach:
- **Step 1: Personal Info**: Name, Mobile, and Email.
- **Step 2: Business Info**: Shop Name, Address, Pincode, State, and District.
- **Step 3: KYC & Finance**: Adhar, PAN, Permanent Address, and Bank Account Details (including Bank Name and IFSC).
- **Step 4: Media Capture**: Placeholders for document photos and "Photo with GPS" features. Integrated GPS location fetching using `LocationUtils`.
- **Step 5: Security**: Creation of Password and PIN with confirmation fields.

### 3. Backend & Logic
- **RegistrationViewModel**: Manages the multi-step state, validation (basic), and transition logic.
- **RegistrationData**: A clean data model representing all form fields.
- **LocationUtils**: A utility to fetch the current GPS coordinates for business compliance.

### 4. Permissions & Configuration
- Added necessary permissions (`ACCESS_FINE_LOCATION`, `CAMERA`) and features to `AndroidManifest.xml`.
- Updated `build.gradle.kts` and `libs.versions.toml` to include `play-services-location` and `accompanist-permissions`.

### 5. Document Upload & Camera Integration
- **FileProvider Configuration**: Set up a `FileProvider` in `AndroidManifest.xml` and `res/xml/file_paths.xml` to safely share file Uris with the Camera app.
- **Image Source Picker**: Added a dialog in Step 4 that allows users to choose between the **Camera** and **Gallery** for uploading documents.
- **Launchers**: Implemented `ActivityResultLauncher`s for both picking images and taking new photos.
- **Permission Handling**: Integrated `cameraPermissionState` alongside location permissions to ensure the app has necessary access for capturing real-time photos with GPS.

## Verification Results
- **Compiles**: Yes, build successful.
- **Flow**: User can now click "Add Photo" items in Step 4, choose a source, and see the item state update to "Checked" once a file is selected.

render_diffs(file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt)
render_diffs(file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/login/LoginScreen.kt)
