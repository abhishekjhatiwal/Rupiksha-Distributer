# Implementation Plan - Retailer Registration with Supabase Integration

This plan outlines the changes required to implement the retailer registration flow on the Android app, including image uploads to Supabase Storage and data persistence in the `retailers` table.

## User Review Required

> [!IMPORTANT]
> The app needs to handle image uploads before storing the retailer data. If any image upload fails, the registration process will stop with an error.

## Proposed Changes

### 1. Dependencies & Configuration

#### [MODIFY] [libs.versions.toml](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/gradle/libs.versions.toml)
- Add `supabase-storage` dependency definition.

#### [MODIFY] [app/build.gradle.kts](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/build.gradle.kts)
- Add `implementation(libs.supabase.storage)` to the dependencies.

#### [MODIFY] [AppContainer.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/di/AppContainer.kt)
- Install `Storage` in the `supabaseClient`.

### 2. Domain & Data Layer

#### [MODIFY] [RegisterRepository.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/repository/RegisterRepository.kt)
- Add `registerRetailer(data: RegistrationData): Resource<Unit>` method.

#### [MODIFY] [RegistrationModels.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/domain/model/RegistrationModels.kt)
- Add `@Serializable` and field mapping to a new `RetailerEntity` class that matches the Supabase `retailers` table schema.

#### [MODIFY] [RegisterRepositoryImpl.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/repository/RegisterRepositoryImpl.kt)
- Update constructor to take `android.content.Context` (or `ContentResolver`) to read image bytes from `Uri`.
- Implement `registerRetailer`:
    1. Sign up the user in Supabase Auth using email and PIN/Password.
    2. Upload all images from `RegistrationData` to the `retailer-docs` bucket.
    3. Insert the registration record into the `retailers` table with the image URLs.

### 3. Presentation Layer

#### [MODIFY] [RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)
- Update `register()` to call `registerRepository.registerRetailer(uiState.value.data)`.
- Handle loading, success, and error states accordingly.

## Verification Plan

### Automated Tests
- I will verify the build after adding dependencies.
- (Optional) Unit tests for `RegisterRepositoryImpl` if time permits.

### Manual Verification
- Deploy the app to a device/emulator.
- Complete the 5-step registration process.
- Verify that images are uploaded to the Supabase Storage bucket.
- Verify that a new record is created in the `retailers` table.
- Verify that the user is created in Supabase Auth.
