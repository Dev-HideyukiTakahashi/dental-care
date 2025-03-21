CREATE TABLE tb_patient (
    id BIGINT NOT NULL,
    medical_history VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_patient_user FOREIGN KEY (id) REFERENCES tb_user
);