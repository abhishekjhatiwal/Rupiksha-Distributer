# Implementation Plan: Bright Theme Update

The goal is to update the application theme to be "bright" as requested. This involves making the Light theme the default (or forced) and potentially adjusting the brand colors to be more vibrant.

## User Review Required

> [!IMPORTANT]
> I am assuming "bright" means both "Light Theme" and more vibrant brand colors. If you only wanted the Light theme enabled (regardless of system settings), please let me know.

## Proposed Changes

### [Theme & Colors]

#### [MODIFY] [Color.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/ui/theme/Color.kt)
- Update `BrandPrimary` to a more vibrant blue (e.g., `0xFF3D5AFE`).
- Update `BrandPrimaryDark` to a corresponding brighter shade.

#### [MODIFY] [Theme.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/ui/theme/Theme.kt)
- Set `darkTheme = false` as the default in `MyApplicationTheme` to ensure a bright experience regardless of system settings, or simply ensure the light scheme is the intended "bright" look.

## Verification Plan

### Manual Verification
- Deploy the app and verify the new "bright" aesthetic on the Login and Registration screens.
- Check that buttons and headers look vibrant and clear.
