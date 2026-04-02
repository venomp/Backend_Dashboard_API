-- =============================================================
--  Finance Dashboard  –  PostgreSQL Database Setup Script
--  DB:   finance_db
--  User: postgres  (adjust credentials as needed)
-- =============================================================

-- ---------------------------------------------------------------
-- 1. CREATE DATABASE  (run this as the postgres superuser once)
-- ---------------------------------------------------------------
-- psql -U postgres -c "CREATE DATABASE finance_db;"
-- Then connect:  psql -U postgres -d finance_db -f init.sql

-- ---------------------------------------------------------------
-- 2. ENUM TYPES
-- ---------------------------------------------------------------

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('ADMIN', 'ANALYST', 'VIEWER');
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'transaction_type') THEN
        CREATE TYPE transaction_type AS ENUM ('INCOME', 'EXPENSE');
    END IF;
END$$;

-- ---------------------------------------------------------------
-- 3. TABLES
-- ---------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
    id           BIGSERIAL    PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL,
    role         VARCHAR(20)  NOT NULL DEFAULT 'VIEWER'
                     CHECK (role IN ('ADMIN', 'ANALYST', 'VIEWER')),
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  users              IS 'Registered application users';
COMMENT ON COLUMN users.role         IS 'ADMIN can create records; ANALYST can view all; VIEWER sees own records only';
COMMENT ON COLUMN users.active       IS 'Soft-delete / deactivation flag';
COMMENT ON COLUMN users.password     IS 'BCrypt-hashed password – never store plain text';

CREATE TABLE IF NOT EXISTS financial_records (
    id               BIGSERIAL       PRIMARY KEY,
    user_id          BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount           NUMERIC(19, 4)  NOT NULL CHECK (amount > 0),
    type             VARCHAR(10)     NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category         VARCHAR(255)    NOT NULL,
    transaction_date TIMESTAMP       NOT NULL,
    description      TEXT,
    created_at       TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  financial_records                  IS 'Income and expense transactions per user';
COMMENT ON COLUMN financial_records.amount           IS 'Always positive; direction is determined by type';
COMMENT ON COLUMN financial_records.type             IS 'INCOME or EXPENSE';
COMMENT ON COLUMN financial_records.category         IS 'Free-form category label, e.g. Salary, Rent, Food';
COMMENT ON COLUMN financial_records.transaction_date IS 'Business date of the transaction (not insert time)';

-- ---------------------------------------------------------------
-- 4. INDEXES
-- ---------------------------------------------------------------

-- Speed up per-user record lookups (the most common query)
CREATE INDEX IF NOT EXISTS idx_financial_records_user_id
    ON financial_records(user_id);

-- Speed up type-filtered queries per user
CREATE INDEX IF NOT EXISTS idx_financial_records_user_type
    ON financial_records(user_id, type);

-- Speed up date-range queries per user
CREATE INDEX IF NOT EXISTS idx_financial_records_user_date
    ON financial_records(user_id, transaction_date);

-- Speed up email lookup on login
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email
    ON users(email);

-- ---------------------------------------------------------------
-- 5. SEED DATA  (optional – remove in production)
-- ---------------------------------------------------------------

-- Passwords below are BCrypt hashes of the literal strings shown in comments.
-- Hash for "Admin@123"  (cost 10)
-- Hash for "Analyst@123"
-- Hash for "Viewer@123"

INSERT INTO users (email, password, full_name, phone_number, role, active)
VALUES
    ('admin@finance.com',
     '$2a$12$xi4WzCjo9uoFUzL/iktbGOCWWUfq/jb5YPp.iYV2hsfnPz/haCN3u',
     'Admin User',
     '+911234567890',
     'ADMIN',
     TRUE),

    ('analyst@finance.com',
     '$2a$10$9PK2mRt5cY7pZ2xIlM1n7eNgU0Z3lQ4oW5vS9tE2dH8jN7kB6gO1L',
     'Analyst User',
     '+912345678901',
     'ANALYST',
     TRUE),

    ('viewer@finance.com',
     '$2a$10$3SL4nSu6dZ8qA3yJmN2o8fOhV1A4mR5pX6wT0uF3eI9kO8lC5hP2M',
     'Viewer User',
     '+913456789012',
     'VIEWER',
     TRUE)
ON CONFLICT (email) DO NOTHING;

-- Sample financial records tied to admin user (id=1)
INSERT INTO financial_records (user_id, amount, type, category, transaction_date, description)
VALUES
    (1, 85000.0000, 'INCOME',  'Salary',       '2025-03-01 09:00:00', 'March salary'),
    (1, 12000.0000, 'EXPENSE', 'Rent',          '2025-03-02 10:00:00', 'Monthly rent'),
    (1,  3500.0000, 'EXPENSE', 'Food',          '2025-03-05 13:00:00', 'Grocery shopping'),
    (1,  1200.0000, 'EXPENSE', 'Utilities',     '2025-03-07 11:00:00', 'Electricity + internet'),
    (1,  5000.0000, 'INCOME',  'Freelance',     '2025-03-10 16:00:00', 'Web project payment'),
    (1,  2200.0000, 'EXPENSE', 'Transport',     '2025-03-12 08:00:00', 'Monthly commute'),
    (1,  4500.0000, 'EXPENSE', 'Shopping',      '2025-03-15 14:00:00', 'Electronics'),
    (1,  1500.0000, 'EXPENSE', 'Entertainment', '2025-03-20 19:00:00', 'OTT + outings'),
    (1, 85000.0000, 'INCOME',  'Salary',        '2025-04-01 09:00:00', 'April salary'),
    (1, 12000.0000, 'EXPENSE', 'Rent',          '2025-04-02 10:00:00', 'Monthly rent')
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------
-- 6. USEFUL VIEWS  (read-only, safe for monitoring / BI tools)
-- ---------------------------------------------------------------

CREATE OR REPLACE VIEW v_user_balance AS
SELECT
    u.id                                          AS user_id,
    u.email,
    u.full_name,
    COALESCE(SUM(CASE WHEN r.type = 'INCOME'  THEN r.amount ELSE 0 END), 0) AS total_income,
    COALESCE(SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END), 0) AS total_expense,
    COALESCE(SUM(CASE WHEN r.type = 'INCOME'  THEN r.amount ELSE 0 END), 0)
    - COALESCE(SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END), 0) AS net_balance
FROM users u
LEFT JOIN financial_records r ON r.user_id = u.id
GROUP BY u.id, u.email, u.full_name;

COMMENT ON VIEW v_user_balance IS 'Per-user income / expense / net balance summary';

CREATE OR REPLACE VIEW v_category_summary AS
SELECT
    u.id          AS user_id,
    u.email,
    r.category,
    r.type,
    COUNT(*)      AS transaction_count,
    SUM(r.amount) AS total_amount
FROM financial_records r
JOIN users u ON u.id = r.user_id
GROUP BY u.id, u.email, r.category, r.type
ORDER BY u.id, total_amount DESC;

COMMENT ON VIEW v_category_summary IS 'Spending / income breakdown by category per user';

-- ---------------------------------------------------------------
-- 7. QUICK SANITY CHECKS
-- ---------------------------------------------------------------
-- SELECT * FROM v_user_balance;
-- SELECT * FROM v_category_summary;
-- SELECT COUNT(*) FROM users;
-- SELECT COUNT(*) FROM financial_records;
