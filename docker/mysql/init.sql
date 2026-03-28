-- ════════════════════════════════════════════
--     Initial Database Setup
-- ════════════════════════════════════════════

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS ecommerce
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Grant permissions
GRANT ALL PRIVILEGES ON ecommerce.* TO 'ecommerce_user'@'%';
FLUSH PRIVILEGES;

USE ecommerce;

-- Tables are auto-created by JPA hibernate.ddl-auto=update
-- This file is for any additional SQL initialization if needed