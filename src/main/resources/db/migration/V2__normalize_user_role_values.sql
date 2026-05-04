UPDATE users
SET role = 'USER'
WHERE role IN ('ROLE_USER', 'user', 'User');

UPDATE users
SET role = 'ADMIN'
WHERE role IN ('ROLE_ADMIN', 'admin', 'Admin');

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM users
        WHERE role NOT IN ('USER', 'ADMIN')
    ) THEN
        RAISE EXCEPTION 'Unexpected users.role value found. Normalize all role values before enabling UserRole enum mapping.';
    END IF;
END $$;
