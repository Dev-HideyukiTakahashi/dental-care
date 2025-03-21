CREATE TABLE tb_dentist (
    id BIGINT NOT NULL,
    registration_number VARCHAR(255),
    speciality VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_dentist_user FOREIGN KEY (id) REFERENCES tb_user
);