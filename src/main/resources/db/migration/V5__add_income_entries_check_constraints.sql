-- Las columnas ya las crean V1, V2 y V3. Esta migración solo añade
-- las restricciones de dominio que faltaban (V4 quedó vacía en producción).

ALTER TABLE income_entries DROP CONSTRAINT IF EXISTS chk_payment_method;
ALTER TABLE income_entries
    ADD CONSTRAINT chk_payment_method
    CHECK (payment_method IN ('EFECTIVO', 'TARJETA', 'BIZUM', 'TRANSFERENCIA', 'OTRO'));

ALTER TABLE income_entries DROP CONSTRAINT IF EXISTS chk_change_method;
ALTER TABLE income_entries
    ADD CONSTRAINT chk_change_method
    CHECK (change_method IS NULL
           OR change_method IN ('EFECTIVO', 'TARJETA', 'BIZUM', 'TRANSFERENCIA', 'OTRO'));
