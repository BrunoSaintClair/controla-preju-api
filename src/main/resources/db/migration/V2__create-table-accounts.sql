CREATE TABLE accounts
(
    id UUID NOT NULL,
    name        VARCHAR(30) NOT NULL,
    description VARCHAR(50) NOT NULL,
    type        VARCHAR(30) NOT NULL,
    balance_in_cents     BIGINT      NOT NULL,
    created_at  TIMESTAMP   NOT NULL,
    user_id UUID,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);