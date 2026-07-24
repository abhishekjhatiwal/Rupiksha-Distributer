# Walkthrough - Secure Username Login Setup

I have updated the app to use a completely separate table for username-to-email mapping, ensuring your existing database structure remains untouched.

## Changes Made

### Data Layer
- **[DistributorRepositoryImpl.kt](file:///D:/Rupiksha/Rupiksha Distributer/rupiksha/app/src/main/java/com/rupiksha/distributer/data/repository/DistributorRepositoryImpl.kt)**:
    - Changed the lookup table name to `app_user_profiles`.
    - Maintained the "Direct Email Login" feature as a safe default.
    - Updated error messages to guide you toward the correct setup.

## Supabase Setup (Run this SQL)

To support username login without modifying your existing tables, run this script in your Supabase SQL Editor. It creates a new, isolated table and a trigger to keep it in sync.

```sql
-- 1. Create a NEW isolated table for app-specific profile data
CREATE TABLE public.app_user_profiles (
  id uuid REFERENCES auth.users NOT NULL PRIMARY KEY,
  username text UNIQUE NOT NULL,
  email text UNIQUE NOT NULL,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Enable Row Level Security (RLS)
ALTER TABLE public.app_user_profiles ENABLE ROW LEVEL SECURITY;

-- 3. Allow public read access for the login lookup
CREATE POLICY "Allow public read for username lookup" ON public.app_user_profiles
  FOR SELECT USING (true);

-- 4. Create a function to sync new Auth users to this table
CREATE OR REPLACE FUNCTION public.handle_app_user_signup()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.app_user_profiles (id, username, email)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'username', SPLIT_PART(NEW.email, '@', 1)), -- Use metadata or part of email as username
    NEW.email
  );
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 5. Create the trigger
DROP TRIGGER IF EXISTS on_auth_user_created_sync ON auth.users;
CREATE TRIGGER on_auth_user_created_sync
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.handle_app_user_signup();
```

## How to Verify
1.  **Run the SQL**: Execute the code above in Supabase.
2.  **Existing Users**: For any existing users in `auth.users`, you'll need to manually add them to `app_user_profiles` if you want them to use a username.
3.  **App Login**: Enter an email to log in directly, or a username once the mapping is in the table.
