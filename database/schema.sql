-- noinspection SqlNoDataSourceInspectionForFile

create schema ecommerce_accounts_db;
use ecommerce_accounts_db;


CREATE TABLE address
(
    id             BIGINT AUTO_INCREMENT   NOT NULL,
    street_address VARCHAR(200) DEFAULT '' NOT NULL,
    second_address VARCHAR(200) DEFAULT '' NULL,
    city           VARCHAR(200) DEFAULT '' NOT NULL,
    state          VARCHAR(2)   DEFAULT '' NOT NULL,
    province       VARCHAR(200)            null DEFAULT '' NULL,
    postal_code    VARCHAR(10)  DEFAULT '' NOT NULL,
    country        VARCHAR(100) DEFAULT '' NOT NULL,
    CONSTRAINT pk_address PRIMARY KEY (id)
);

CREATE TABLE accounts
(
    id             BIGINT AUTO_INCREMENT   NOT NULL,
    first_name     VARCHAR(200) DEFAULT '' NOT NULL,
    last_name      VARCHAR(200) DEFAULT '' NOT NULL,
    email_address  VARCHAR(200) DEFAULT '' NOT NULL,
    account_ref_id VARCHAR(255) NOT NULL   DEFAULT '',
    created_dt     datetime                NULL,
    updated_dt     datetime                NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE TABLE accounts_addresses
(
    accounts_id  BIGINT NOT NULL,
    addresses_id BIGINT NOT NULL,
    CONSTRAINT pk_accounts_addresses PRIMARY KEY (accounts_id, addresses_id)
);

ALTER TABLE accounts_addresses
    ADD CONSTRAINT uc_accounts_addresses_addresses UNIQUE (addresses_id);

ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_emailaddress UNIQUE (email_address);

ALTER TABLE accounts_addresses
    ADD CONSTRAINT fk_accadd_on_accounts FOREIGN KEY (accounts_id) REFERENCES accounts (id);

ALTER TABLE accounts_addresses
    ADD CONSTRAINT fk_accadd_on_address FOREIGN KEY (addresses_id) REFERENCES address (id);