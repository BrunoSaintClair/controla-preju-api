ALTER TABLE users ALTER COLUMN status TYPE VARCHAR(20) USING status::VARCHAR;

UPDATE users SET status = 'PENDING' WHERE status = 'P';
UPDATE users SET status = 'ACTIVE' WHERE status = 'A';
UPDATE users SET status = 'INACTIVE' WHERE status = 'I';
