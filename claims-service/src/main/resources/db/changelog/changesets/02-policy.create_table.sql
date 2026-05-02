-- liquibase formatted sql

-- changeset Meuna:1
CREATE TABLE policy
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    risk_type            SMALLINT       NOT NULL,
    sum_insured          NUMERIC(19, 2) NOT NULL,
    max_per_claim        NUMERIC(19, 2) NOT NULL,
    start_date           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    daily_payout_percent NUMERIC(10, 4) NOT NULL,
    waiting_days         INT            NOT NULL,
    survival_days        INT            NOT NULL
);
