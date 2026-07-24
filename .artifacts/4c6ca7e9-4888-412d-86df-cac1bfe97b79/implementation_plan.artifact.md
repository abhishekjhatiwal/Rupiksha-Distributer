# Dashboard Screen Implementation Plan

The goal is to implement the Dashboard Screen as per the provided image, ensuring it matches the design for the "Rupiksha" app.

## User Review Required

> [!IMPORTANT]
> Some icons in the image (like the specific wallet, payout, and AEPS icons) are not present in the current project's resources. I will use standard Material Icons or generic placeholders that closely match the design.

## Proposed Changes

### [UI Components & Styling]

#### [MODIFY] [Color.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/ui/theme/Color.kt)
- Add colors for trends (SuccessGreen, ErrorRed, etc.) and specific UI elements like the interaction button background.

#### [NEW] [DashboardComponents.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/dashboard/DashboardComponents.kt)
- Create reusable components:
    - `StatCard`: Horizontal cards for stats (Total SMAs, Wallet Balance, etc.).
    - `SummaryItemCard`: List items with trend indicators and expandable details.
    - `DashboardToggle`: Monthly/Daily toggle.

### [Dashboard Feature]

#### [MODIFY] [DashboardScreen.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/dashboard/DashboardScreen.kt)
- Overhaul the screen layout:
    - Implement the Top Bar with Logo, "+ Interaction", Search, Notifications (with badge), and Menu.
    - Add the greeting and date header.
    - Add the horizontal stats row.
    - Update the Tab selection to match the "Portfolio Summary" and "Business Summary" design.
    - Integrate the bottom navigation bar.

#### [MODIFY] [DashboardTabs.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/presentation/dashboard/DashboardTabs.kt)
- Update `PortfolioSummaryTab` to use the new `SummaryItemCard` and match the mock data from the image.

## Verification Plan

### Automated Tests
- I will use `render_compose_preview` to verify the UI of individual components and the full screen.

### Manual Verification
- Deploy to an emulator/device to check scroll behavior and interactions.
