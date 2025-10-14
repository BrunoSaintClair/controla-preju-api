CREATE TABLE revenues
(
    id UUID NOT NULL,
    title           VARCHAR(30) NOT NULL,
    description     VARCHAR(50) NOT NULL,
    amount_in_cents BIGINT      NOT NULL,
    category        VARCHAR(30)    NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    account_id UUID,
    CONSTRAINT pk_revenues PRIMARY KEY (id)
);

ALTER TABLE revenues
    ADD CONSTRAINT FK_REVENUES_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);