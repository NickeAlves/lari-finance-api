-- Las columnas de esta tabla ya las crean V1, V2 y V3.
-- Esta migración solo añade las restricciones de dominio que faltaban.

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