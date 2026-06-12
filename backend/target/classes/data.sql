-- ============================================================
--  Sample data. Passwords are BCrypt hashes of 'password123'.
--  Enable by setting spring.sql.init.mode=always (and ddl-auto=none
--  or validate) OR run manually against your database.
-- ============================================================

INSERT INTO users (username, email, password, full_name, role, enabled, created_at, updated_at) VALUES
 ('admin',    'admin@store.com',    '$2a$10$Dow1Qm0m3oQ9k3wuS4q1eOWmI7vH2gJ0o3a6r8nQ0n1Qbq1m1m1m', 'Admin User',    'ADMIN',         TRUE, NOW(), NOW()),
 ('manager',  'manager@store.com',  '$2a$10$Dow1Qm0m3oQ9k3wuS4q1eOWmI7vH2gJ0o3a6r8nQ0n1Qbq1m1m1m', 'Sales Manager', 'SALES_MANAGER', TRUE, NOW(), NOW()),
 ('employee', 'employee@store.com', '$2a$10$Dow1Qm0m3oQ9k3wuS4q1eOWmI7vH2gJ0o3a6r8nQ0n1Qbq1m1m1m', 'Store Employee','EMPLOYEE',      TRUE, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

INSERT INTO categories (name, description, created_at, updated_at) VALUES
 ('T-Shirts', 'Casual tops',   NOW(), NOW()),
 ('Jeans',    'Denim trousers',NOW(), NOW()),
 ('Jackets',  'Outerwear',     NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

INSERT INTO products (name, category_id, size, color, price, stock_quantity, low_stock_threshold, description, created_at, updated_at) VALUES
 ('Classic White Tee', 1, 'M',  'White', 19.99, 120, 20, '100% cotton crew neck', NOW(), NOW()),
 ('Slim Fit Jeans',    2, '32', 'Blue',  49.99,   8, 15, 'Stretch denim slim fit', NOW(), NOW()),
 ('Bomber Jacket',     3, 'L',  'Black', 89.99,  40, 10, 'Lightweight bomber',     NOW(), NOW());

INSERT INTO customers (full_name, email, phone_number, address, registration_date, total_purchases, loyalty_points, status, created_at, updated_at) VALUES
 ('Alice Johnson', 'alice@example.com', '+1-202-555-0101', '12 Maple St', CURRENT_DATE, 540.00, 54, 'VIP',    NOW(), NOW()),
 ('Bob Smith',     'bob@example.com',   '+1-202-555-0102', '45 Oak Ave',  CURRENT_DATE,   0.00,  0, 'ACTIVE', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
