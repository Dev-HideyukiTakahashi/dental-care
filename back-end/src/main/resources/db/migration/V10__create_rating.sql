create table tb_rating (
    score integer,
    date TIMESTAMP WITHOUT TIME ZONE,
    is_rated boolean,
    dentist_id bigint,
    id bigint generated by default as identity,
    patient_id bigint,
    appointment_id bigint,
    comment varchar(255),
    primary key (id),
    CONSTRAINT fk_rating_appointment FOREIGN KEY (appointment_id) REFERENCES tb_appointment,
    CONSTRAINT fk_rating_dentist FOREIGN KEY (dentist_id) REFERENCES tb_dentist,
    CONSTRAINT fk_rating_patient FOREIGN KEY (patient_id) REFERENCES tb_patient
)