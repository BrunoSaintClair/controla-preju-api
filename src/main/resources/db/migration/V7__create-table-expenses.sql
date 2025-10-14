CREATE TABLE expenses
(
    id UUID NOT NULL,
    title           VARCHAR(30) NOT NULL,
    description     VARCHAR(50) NOT NULL,
    amount_in_cents BIGINT      NOT NULL,
    category        VARCHAR(30) NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    account_id UUID,
    CONSTRAINT pk_expenses PRIMARY KEY (id)
);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);
