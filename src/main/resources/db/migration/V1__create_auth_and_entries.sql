CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(40) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE income_entries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    entry_date DATE NOT NULL,
    client_name VARCHAR(160) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    payment_method VARCHAR(40) NOT NULL,
    vat_amount NUMERIC(12, 2) NOT NULL,
    fixed_expenses_amount NUMERIC(12, 2) NOT NULL,
    products_amount NUMERIC(12, 2) NOT NULL,
    salary_amount NUMERIC(12, 2) NOT NULL,
    annual_tax_reserve_amount NUMERIC(12, 2) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_income_entries_user_date ON income_entries(user_id, entry_date);
