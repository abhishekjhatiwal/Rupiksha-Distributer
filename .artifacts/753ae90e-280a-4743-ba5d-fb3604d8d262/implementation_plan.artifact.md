# Implementation Plan - Modern Login Screen UI

Update the existing Login Screen to match the provided high-fidelity design from HTML/Tailwind CSS. This includes updating the color palette, typography (if possible), and implementing the new UI components in Jetpack Compose.

## Proposed Changes

### UI Theme

#### [MODIFY] [Color.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/ui/theme/Color.kt)
- Add new color constants based on the Tailwind design:
    - `Primary`: #2B4494
    - `PrimaryDark`: #1A2C6B
    - `BackgroundLight`: #F8FAFC
    - `BackgroundDark`: #0F172A
    - `SurfaceLight`: #FFFFFF
    - `SurfaceDark`: #1E293B
    - `BorderLight`: #E2E8F0
    - `BorderDark`: #334155
    - `TextPrimaryLight`: #0F172A
    - `TextPrimaryDark`: #F8FAFC
    - `TextSecondaryLight`: #475569
    - `TextSecondaryDark`: #94A3B8

#### [MODIFY] [Theme.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/ui/theme/Theme.kt)
- Update `LightColorScheme` and `DarkColorScheme` to use the new colors.
- Ensure the background and surface colors match the design for both modes.

### Login Feature

#### [MODIFY] [LoginScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/auth/login/LoginScreen.kt)
- **Root Layout**: Change from gradient background to a solid background (`MaterialTheme.colorScheme.background`).
- **Logo Section**: Implement the circular logo container with a light border and shadow.
- **Header**: Update "Welcome," and subtitle styling.
- **Role Selector**: Redesign the `RoleSegmentedControl` to match the pill-shaped design with the selected role having a primary background and shadow.
- **Input Fields**:
    - Use `OutlinedTextField` with custom styling (rounded corners, specific border colors).
    - Add leading icons (Person, Lock).
    - Add trailing icon for PIN visibility toggle.
- **Forget Password**: Add the "Forget login ID or Password?" link with the help icon.
- **Agreement Section**: Implement the custom checkbox and "Terms & Conditions" / "Privacy Policy" links.
- **Login Button**: Apply a vertical gradient (`Primary` to `PrimaryDark`) and shadow.
- **Footer**: Add the "© RUPIKSHA" and "System Version" text at the bottom.

## Verification Plan

### Automated Tests
- Run `app:assembleDebug` to ensure the project still builds.

### Manual Verification
- Render the `LoginScreenPreview` to visually verify the changes against the HTML design.
- Verify both Light and Dark modes.
