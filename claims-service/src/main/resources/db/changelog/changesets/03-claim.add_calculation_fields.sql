-- liquibase formatted sql

-- changeset Meuna:1
ALTER TABLE claim
    ADD COLUMN claim_submitted_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN actual_expenses NUMERIC(10, 4);
