ALTER TABLE users ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE accounts ADD COLUMN updated_at TIMESTAMP;

UPDATE users SET updated_at = created_at;
UPDATE accounts SET updated_at = created_at;

ALTER TABLE users ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE accounts ALTER COLUMN updated_at SET NOT NULL;