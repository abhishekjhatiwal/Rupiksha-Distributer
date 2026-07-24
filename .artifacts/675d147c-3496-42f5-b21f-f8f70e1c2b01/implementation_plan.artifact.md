# Implementation Plan - User Registration Screen

Create a comprehensive multi-step registration form for distributors, adhering to the existing Material 3 theme.

## User Review Required

> [!IMPORTANT]
> The registration form is quite extensive. I propose a **5-step Wizard** approach to improve UX and avoid overwhelming the user.
> 1. **Step 1: Personal Info** (Name, Mobile, Email)
> 2. **Step 2: Business Info** (Shop Name, Pincode, State, District, Shop Address)
> 3. **Step 3: KYC & Finance** (Adhar, PAN, Permanent Address, Bank Details)
> 4. **Step 4: Media Capture** (Document Photo, Photo with Employee + GPS, Shop Photo + GPS)
> 5. **Step 5: Security** (Password, PIN)

> [!WARNING]
> "Photo with GPS" requires location permissions (`ACCESS_FINE_LOCATION`) and camera access. I will implement a basic permission handling flow.

## Proposed Changes

### [Presentation Layer]

#### [MODIFY] [Screen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/navigation/Screen.kt)
- Add `Register` route.

#### [MODIFY] [AppNavGraph.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/navigation/AppNavGraph.kt)
- Add `RegistrationScreen` to the navigation graph.

#### [MODIFY] [LoginScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/login/LoginScreen.kt)
- Add a "Don't have an account? Register" button at the bottom.

#### [NEW] [RegistrationScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationScreen.kt)
- Multi-step UI using `HorizontalPager` or simple state-based visibility.
- Re-use `CustomOutlinedTextField` and other theme-consistent components.
- Progress indicator at the top.

#### [NEW] [RegistrationViewModel.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/register/RegistrationViewModel.kt)
- Manage `RegistrationUiState` holding all field values.
- Handle step transitions and validation.

### [Data/Domain Layer]

#### [NEW] [RegistrationModels.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/domain/model/RegistrationModels.kt)
- Define `RegistrationRequest` data class.

### [Utilities]

#### [NEW] [LocationUtils.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/util/LocationUtils.kt)
- Helper for fetching current GPS coordinates.

## Verification Plan

### Automated Tests
- Unit tests for `RegistrationViewModel` to ensure validation logic works correctly for each step.

### Manual Verification
- Deploy to emulator/device.
- Navigate from Login to Register.
- Fill all steps, verify validation messages.
- Test "Photo with GPS" functionality (requires real device or mock location).
- Verify final submission state.
