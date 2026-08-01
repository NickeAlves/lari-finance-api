ALTER TABLE income_entries
    ADD COLUMN payment_method VARCHAR(40) NOT NULL DEFAULT 'OTRO',
    ADD COLUMN vat_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN fixed_expenses_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN products_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN salary_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN annual_tax_reserve_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN notes VARCHAR(500),
    ADD COLUMN change_given BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN change_method VARCHAR(40),
    ADD COLUMN change_amount NUMERIC(12,2),
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;


ALTER TABLE income_entries
ADD CONSTRAINT chk_payment_method
CHECK (
    payment_method IN (
        'EFECTIVO',
        'TARJETA',
        'BIZUM',
        'TRANSFERENCIA',
        'OTRO'
    )
);


ALTER TABLE income_entries
ADD CONSTRAINT chk_change_method
CHECK (
    change_method IS NULL OR
    change_method IN (
        'EFECTIVO',
        'TARJETA',
        'BIZUM',
        'TRANSFERENCIA',
        'OTRO'
    )
);