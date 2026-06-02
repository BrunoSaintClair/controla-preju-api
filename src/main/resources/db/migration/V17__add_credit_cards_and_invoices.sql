CREATE TABLE credit_cards
(
    id UUID NOT NULL,
    name VARCHAR(30) NOT NULL,
    limit_in_cents BIGINT NOT NULL,
    available_limit_in_cents BIGINT NOT NULL,
    closing_day INTEGER NOT NULL,
    due_day INTEGER NOT NULL,
    user_id UUID,
    CONSTRAINT pk_credit_cards PRIMARY KEY (id),
    CONSTRAINT fk_credit_cards_on_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE invoices
(
    id UUID NOT NULL,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    total_amount_in_cents BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    credit_card_id UUID,
    CONSTRAINT pk_invoices PRIMARY KEY (id),
    CONSTRAINT fk_invoices_on_credit_card FOREIGN KEY (credit_card_id) REFERENCES credit_cards (id)
);

ALTER TABLE expenses ALTER COLUMN account_id DROP NOT NULL;
ALTER TABLE expenses ADD COLUMN invoice_id UUID;
ALTER TABLE expenses ADD CONSTRAINT fk_expenses_on_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id);
