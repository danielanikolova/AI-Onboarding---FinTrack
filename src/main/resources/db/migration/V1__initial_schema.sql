-- Flyway migration V1: initial schema

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Enums
CREATE TYPE category_type AS ENUM ('INCOME', 'EXPENSE');
CREATE TYPE account_type  AS ENUM ('CHECKING', 'SAVINGS', 'CASH', 'CREDIT');

-- users
CREATE TABLE users (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR     NOT NULL UNIQUE,
    password_hash VARCHAR     NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- categories
CREATE TABLE categories (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR       NOT NULL UNIQUE,
    type        category_type NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);

-- accounts
CREATE TABLE accounts (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL REFERENCES users(id),
    name            VARCHAR      NOT NULL,
    type            account_type NOT NULL,
    currency        VARCHAR(3)   NOT NULL DEFAULT 'BGN',
    initial_balance DECIMAL(19,4) NOT NULL DEFAULT 0.00,
    is_active       BOOLEAN      NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_account_name_user UNIQUE (name, user_id)
);

-- transactions
CREATE TABLE transactions (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id       UUID          NOT NULL REFERENCES accounts(id),
    category_id      UUID          NOT NULL REFERENCES categories(id),
    amount           DECIMAL(19,4) NOT NULL CHECK (amount > 0),
    description      TEXT,
    transaction_date DATE          NOT NULL,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- budgets
CREATE TABLE budgets (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL REFERENCES users(id),
    category_id UUID          NOT NULL REFERENCES categories(id),
    amount      DECIMAL(19,4) NOT NULL,
    start_date  DATE          NOT NULL,
    end_date    DATE          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Indexes
CREATE INDEX idx_accounts_user_id        ON accounts(user_id);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_date        ON transactions(transaction_date);
CREATE INDEX idx_budgets_user_id          ON budgets(user_id);
CREATE INDEX idx_categories_deleted_at    ON categories(deleted_at) WHERE deleted_at IS NULL;
