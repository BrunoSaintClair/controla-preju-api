CREATE TABLE users
(
    id UUID NOT NULL,
    email      VARCHAR(150) NOT NULL,
    name       VARCHAR(100) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    status     CHAR         NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);