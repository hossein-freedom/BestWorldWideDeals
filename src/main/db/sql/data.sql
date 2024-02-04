-- REMOVE ALL ACTIVE CONNECTIONS FOR BWWD DB--
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'bwwd' -- ← change this to your DB
  AND pid <> pg_backend_pid();

DROP DATABASE IF EXISTS  bwwd;

CREATE DATABASE bwwd;

DROP SCHEMA IF EXISTS  products;

Alter DATABASE  bwwd  SET SEARCH_PATH TO products;

\c bwwd;

CREATE SCHEMA products;

DROP OWNED BY bwwd_admin;
DROP USER IF EXISTS bwwd_admin;

CREATE USER bwwd_admin with encrypted password 'adminbwwd';

GRANT ALL PRIVILEGES ON DATABASE bwwd to bwwd_admin;

GRANT ALL PRIVILEGES ON SCHEMA products to bwwd_admin;

CREATE TABLE products.product_details(
    p_id SERIAL PRIMARY KEY,
    source VARCHAR(250),
    bannercode VARCHAR(500),
    isactive BOOLEAN DEFAULT true,
    category VARCHAR(250),
    subcategory VARCHAR(250),
    sellerwebsite VARCHAR(250),
    affiliatelink  VARCHAR(250),
    email VARCHAR(100),
    price DEC(10,4),
    isonsale BOOLEAN DEFAULT false,
    saleprice DEC(10,4),
    title Text,
    description Text,
    enddate BIGINT DEFAULT 0
 );

GRANT ALL PRIVILEGES ON TABLE products.product_details to bwwd_admin;

CREATE TABLE products.product_images(
    id SERIAL PRIMARY KEY,
    p_id INT,
    imagelink Text
);

GRANT ALL PRIVILEGES ON TABLE products.product_images to bwwd_admin;

ALTER TABLE products.product_images ADD FOREIGN KEY (p_id) REFERENCES products.product_details(p_id);

CREATE TABLE products.users(
    id SERIAL PRIMARY KEY,
    username VARCHAR(250),
    password VARCHAR(250)
);

GRANT ALL PRIVILEGES ON TABLE products.users to bwwd_admin;

INSERT into products.users(username,password) VALUES('admin','admin');
