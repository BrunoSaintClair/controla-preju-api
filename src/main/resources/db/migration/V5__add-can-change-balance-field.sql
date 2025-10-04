ALTER TABLE accounts ADD COLUMN can_change_balance BOOLEAN;

UPDATE accounts SET can_change_balance = true;

ALTER TABLE accounts ALTER COLUMN can_change_balance SET NOT NULL;