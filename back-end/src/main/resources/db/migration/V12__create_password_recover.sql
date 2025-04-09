CREATE TABLE tb_password_recover (
     id BIGSERIAL PRIMARY KEY,
     token VARCHAR(255) NOT NULL,
     email VARCHAR(255) NOT NULL,
     expiration TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL
);