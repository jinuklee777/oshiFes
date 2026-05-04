UPDATE users
SET role = 'USER'
WHERE role IN ('ROLE_USER', 'user', 'User');

UPDATE users
SET role = 'ADMIN'
WHERE role IN ('ROLE_ADMIN', 'admin', 'Admin');
