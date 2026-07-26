# Walkthrough - Retailer Registration with Supabase

I have implemented the complete retailer onboarding flow, connecting the Android app to Supabase for authentication, storage, and database persistence.

## Changes Made

### 1. Supabase Infrastructure
I provided the SQL scripts to create:
-   **`retailers` table**: Stores all profile and KYC information.
-   **`retailer-docs` bucket**: Securely stores uploaded documents (Aadhaar, PAN, etc.).
-   **Security Policies**: RLS is enabled so each retailer can only manage their own data and files.

### 2. Android Dependencies
-   Added `supabase-storage` to [libs.versions.toml](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/gradle/libs.versions.toml).
-   Included the storage library in [app/build.gradle.kts](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/build.gradle.kts).

### 3. Core Logic (Data & Domain)
-   **[AppContainer.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/di/AppContainer.kt)**: Updated to initialize Supabase `Storage` and pass `Context` to repositories.
-   **[RegistrationModels.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/model/RegistrationModels.kt)**: Added `RetailerEntity` for Supabase mapping.
-   **[RegisterRepository.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/repository/RegisterRepository.kt)**: Added `registerRetailer` interface.
-   **[RegisterRepositoryImpl.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/repository/RegisterRepositoryImpl.kt)**: Implemented the registration flow:
    1.  **Auth**: User sign-up with Email/Password.
    2.  **Storage**: Uploading images from `Uri` to Supabase Storage.
    3.  **Database**: Inserting the profile record into the `retailers` table.

### 4. UI Integration
-   **[RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)**: Updated the `register()` function to call the repository and handle the loading/error/success states.

## How to Verify

1.  **Run the SQL**: Ensure you have executed the SQL scripts provided in the previous step in your Supabase SQL Editor.
2.  **Sync Gradle**: Click "Sync Now" in Android Studio to download the new Supabase Storage library.
3.  **Test Registration**:
    -   Fill out the registration form.
    -   Upload images for Aadhaar, PAN, and Shop photos.
    -   Submit the form.
    -   Check **Supabase Auth** for the new user.
    -   Check **Supabase Storage** for the uploaded files in the `retailer-docs` bucket.
    -   Check **Supabase Table Editor** for the new entry in the `retailers` table.

> [!IMPORTANT]
> The image upload path is `${userId}/filename.jpg`. This matches the RLS policies to ensure security.

> [!TIP]
> If you encounter "Unresolved reference" errors in `AppContainer.kt`, please perform a Gradle Sync.
