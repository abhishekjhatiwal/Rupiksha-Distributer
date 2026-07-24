# Implementation Plan - Secure Username Lookup with New Table

To avoid disrupting your existing database, I will create a dedicated table named `app_user_profiles`. This table will ONLY be used to map a **Username** to an **Email** for the login process.

## User Review Required

> [!IMPORTANT]
> This approach creates a new table `app_user_profiles` which is completely separate from your existing `users` or `aeps_users` tables.
> 1. It will be populated automatically when new users are created in Supabase Auth.
> 2. For existing users, you will need to manually insert one row into this table if you want them to log in via Username.

## Proposed Changes

### Data Layer

#### [MODIFY] [DistributorRepositoryImpl.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/repository/DistributorRepositoryImpl.kt)
- Update the login logic to query `app_user_profiles` instead of `users` or `profiles`.
- Keep the "Direct Email Login" logic as it is the safest fallback.

## Verification Plan

### Manual Verification
- Run the SQL script in Supabase.
- Test login with an email address.
- Test login with a username after adding a mapping to `app_user_profiles`.
