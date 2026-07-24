# Implementation Plan - Improve Logo Visibility in Dashboard

The logo in the `DashboardScreen` is currently being displayed using the `Icon` component with a `BrandBlue` tint. Since the logo is a JPEG image, tinting it overrides all its natural colors and details, making it appear as a solid block. This plan aims to fix this by using the `Image` component to preserve the logo's original appearance.

## Proposed Changes

### [dashboard]

#### [MODIFY] [DashboardScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/dashboard/DashboardScreen.kt)

- Replace `Icon` with `Image` for the logo in the Top Bar.
- Remove the `tint` parameter to allow the logo's natural colors to show.
- Increase the size of the logo in the Top Bar slightly from `36.dp` to `40.dp` for better visibility if needed, or keep it consistent with the UI.
- Replace `Icon` with `Image` for the logo in the `ExtendedFloatingActionButton`.

## Verification Plan

### Manual Verification
- Deploy the app and navigate to the Dashboard.
- Verify that the Rupiksha logo is now visible with its original colors and details.
- Verify that the logo in the "FUND REQUESTS" FAB is also clearly visible.
