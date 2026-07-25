# Implementation Plan - Debugging 2Factor OTP Integration

The user integrated the 2Factor.in OTP API but is not receiving any OTPs. This plan focuses on adding diagnostic logging and fixing potential configuration issues with the API key.

## User Review Required

> [!IMPORTANT]
> **Network Connectivity:** The logs show `ENETUNREACH (Network is unreachable)`. This means your emulator or device currently does not have internet access. Please ensure your computer is connected to the internet and that the emulator's Wi-Fi is toggled ON.
>
> **API Key Sanitization:** I have added code to automatically remove the "API Key " prefix if it's present in your `.env` file, but it's still best practice to keep only the raw key in your configuration.

> [!WARNING]
> A `google-services.json` file is still missing. This won't block OTP, but it's causing Firebase errors and app lag.

## Proposed Changes

### [Component: Networking & Diagnostics]

I have sanitized the API key usage and added logging to identify connectivity issues.

#### [MODIFY] [OtpApiService.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/remote/api/OtpApiService.kt)
- Added `.replace("API Key", "").trim()` to ensure the `apiKey` is correctly formatted even if copied with the prefix from the 2Factor dashboard.

#### [MODIFY] [libs.versions.toml](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/gradle/libs.versions.toml) [COMPLETED]
- Added `ktor-client-logging` dependency.

#### [MODIFY] [build.gradle.kts](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/build.gradle.kts) [COMPLETED]
- Included `ktor-client-logging` in implementation dependencies.

#### [MODIFY] [AppContainer.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/di/AppContainer.kt) [COMPLETED]
- Configured Ktor `HttpClient` with `Logging` plugin.
- Set `LogLevel.ALL` to capture full request/response in Logcat.

#### [MODIFY] [OtpRepositoryImpl.kt](file:///D:/Rupiksha/Rupiksha%20Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/repository/OtpRepositoryImpl.kt) [COMPLETED]
- Added `Log.d` and `Log.e` calls to track the OTP sending process and capture any exceptions or error messages from the API.

## Verification Plan

### Automated Tests
- Run `gradlew :app:assembleDebug` to ensure the project still builds after dependency changes.

### Manual Verification
1. Deploy the app to a device/emulator.
2. Navigate to the Registration screen.
3. Enter a 10-digit mobile number and tap "Send OTP".
4. Monitor Logcat with the tag `Ktor` and `OtpRepository` to see the outgoing URL and the response from 2Factor.in.
5. If an error is logged (e.g., 401 Unauthorized or 400 Bad Request), verify the API key and phone number format.
6. Check if the error message is correctly displayed on the UI.
