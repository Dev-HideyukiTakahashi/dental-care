CREATE TABLE tb_admin (
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_admin_user FOREIGN KEY (id) REFERENCES tb_user
);