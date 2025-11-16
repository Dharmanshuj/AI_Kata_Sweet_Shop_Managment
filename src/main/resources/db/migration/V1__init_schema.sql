-- V1__init_schema.sql
-- Initial schema for SweetShop

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id          VARCHAR(36) PRIMARY KEY,
  email       VARCHAR(255) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  name        VARCHAR(255),
  role        VARCHAR(20) NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sweets table
CREATE TABLE IF NOT EXISTS sweets (
  id          VARCHAR(36) PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  category    VARCHAR(255),
  price       NUMERIC(12,2) NOT NULL,
  quantity    INT NOT NULL DEFAULT 0,
  created_by  VARCHAR(36) NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sweets_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for search
CREATE INDEX IF NOT EXISTS idx_sweets_name ON sweets (name);
CREATE INDEX IF NOT EXISTS idx_sweets_category ON sweets (category);

-- Seed admin user (replace the password hash below with a real bcrypt hash)
-- Example: generate a bcrypt hash for your chosen admin password and paste it here.
INSERT INTO users (id, email, password, name, role)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin@example.com',
        '$2a$10$REPLACE_WITH_BCRYPT_HASH_OF_ADMIN_PASSWORD',
        'Admin', 'ADMIN')
ON CONFLICT (email) DO NOTHING;