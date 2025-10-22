CREATE TABLE transfers
(
    id UUID NOT NULL,
    title           VARCHAR(30) NOT NULL,
    description     VARCHAR(50) NOT NULL,
    amount_in_cents BIGINT      NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    source_account_id UUID,
    destination_account_id UUID,
    CONSTRAINT pk_transfers PRIMARY KEY (id)
);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_ON_DESTINATIONACCOUNT FOREIGN KEY (destination_account_id) REFERENCES accounts (id);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_ON_SOURCEACCOUNT FOREIGN KEY (source_account_id) REFERENCES accounts (id);