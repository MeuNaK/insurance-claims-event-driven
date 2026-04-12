-- liquibase formatted sql

-- changeset Meuna:1
CREATE TABLE claim
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    policy_id     BIGINT,
    claim_number  VARCHAR(20),
    incident_date TIMESTAMP WITHOUT TIME ZONE,
    type          SMALLINT,
    status        SMALLINT,
    description   VARCHAR(500)
);

