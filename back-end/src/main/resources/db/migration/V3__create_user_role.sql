CREATE TABLE tb_user_role (
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES tb_role,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES tb_user
);