ALTER TABLE expenses
    ADD payment_method VARCHAR(30);

ALTER TABLE expenses
    ALTER COLUMN payment_method SET NOT NULL;