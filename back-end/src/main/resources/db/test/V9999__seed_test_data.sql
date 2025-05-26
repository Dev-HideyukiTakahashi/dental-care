-- DATABASE SEED FOR TESTS

-- Insert Roles
INSERT INTO tb_role (authority)
VALUES ('ROLE_ADMIN'),
       ('ROLE_PATIENT'),
       ('ROLE_DENTIST');

-- Insert Users
INSERT INTO tb_user (email, name, password, phone)
VALUES ('elias.warrior@example.com', 'Elias Warrior', '$2b$12$.rYCnS9Yx5BPr8N6uGhY0etPuXhCqTGuJ7GpnNzMYsl6RwvqrIDRe',
        '1155551234'),
       ('leonardo.smile@example.com', 'Leonardo Smile', '$2b$12$.rYCnS9Yx5BPr8N6uGhY0etPuXhCqTGuJ7GpnNzMYsl6RwvqrIDRe',
        '3155553456'),
       ('yuki.murasaki90@gmail.com', 'Nina Soul', '$2b$12$.rYCnS9Yx5BPr8N6uGhY0etPuXhCqTGuJ7GpnNzMYsl6RwvqrIDRe',
        '5155555678'),
       ('victor.dent@example.com', 'Victor Dent', '$2b$12$.rYCnS9Yx5BPr8N6uGhY0etPuXhCqTGuJ7GpnNzMYsl6RwvqrIDRe',
        '6155556789'),
       ('henry.surge@example.com', 'Henry Surge', '$2b$12$.rYCnS9Yx5BPr8N6uGhY0etPuXhCqTGuJ7GpnNzMYsl6RwvqrIDRe',
        '8155558901'),
        ('john.doe@example.com', 'John Doe', '$2b$12$.rYCnS9Yx5BPr8N6uGhY0etPuXhCqTGuJ7GpnNzMYsl6RwvqrIDRe',
        '8155558901'),
        ('alex.green@example.com', 'Alex Green', '$2b$12$.rYCnS9Yx5BPr8N6uGhY0etPuXhCqTGuJ7GpnNzMYsl6RwvqrIDRe',
        '8155558901');

-- Admin 
INSERT INTO tb_admin (id)
VALUES (1);

-- Patient
INSERT INTO tb_patient (id, medical_history)
VALUES (2, 'Limpeza dentária'),
       (3, 'Tratamento de canal');

-- Dentist
INSERT INTO tb_dentist (id, registration_number, speciality, score)
VALUES (4, 'DR12345', 'Ortodontia', 9),
       (5, 'DR23256', 'Periodontia', 7),
       (6, 'DR95256', 'Endodontia', 6),
       (7, 'DR11456', 'Odontopediatria', 10);

-- Association User - Role
-- Admin (ROLE_ADMIN)
INSERT INTO tb_user_role (role_id, user_id)
VALUES ((SELECT id FROM tb_role WHERE authority = 'ROLE_ADMIN' LIMIT 1),
       (SELECT id FROM tb_user WHERE name = 'Elias Warrior'));

-- Patients (ROLE_PATIENT)
INSERT INTO tb_user_role (role_id, user_id)
VALUES ((SELECT id FROM tb_role WHERE authority = 'ROLE_PATIENT' LIMIT 1),
       (SELECT id FROM tb_user WHERE name = 'Leonardo Smile')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_PATIENT' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Nina Soul'));

-- Dentists (ROLE_DENTIST)
INSERT INTO tb_user_role (role_id, user_id)
VALUES ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1),
       (SELECT id FROM tb_user WHERE name = 'Victor Dent')),
       ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Henry Surge')),
       ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'John Doe')),
        ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Alex Green'));

-- Insert Schedule dentist id 4
INSERT INTO tb_schedule (dentist_id, unavailable_time_slot)
VALUES (4, '2025-12-20 09:00:00'),
       (4, '2025-12-20 14:00:00'),
       (4, '2025-12-21 10:00:00');

-- Insert Schedule dentist id 5
INSERT INTO tb_schedule (dentist_id, unavailable_time_slot)
VALUES (5, '2025-12-21 15:00:00');

INSERT INTO tb_schedule (unavailable_time_slot, dentist_id, absence_start, absence_end) VALUES (NULL, 5, '2027-12-01 00:00:00','2027-12-31 23:59:59');
INSERT INTO tb_schedule (unavailable_time_slot, dentist_id, absence_start, absence_end) VALUES (NULL, 6, '2027-10-01 00:00:00','2027-10-31 23:59:59');
INSERT INTO tb_schedule (unavailable_time_slot, dentist_id, absence_start, absence_end) VALUES (NULL, 7, '2027-8-11 00:00:00','2027-8-21 23:59:59');

-- Insert Appointment SCHEDULED
INSERT INTO tb_appointment (date, dentist_id, patient_id, status, description)
VALUES ('2025-12-20 09:00:00', 4, 2, 'SCHEDULED', 'Consulta de rotina e limpeza dental'),
       ('2025-12-20 14:00:00', 4, 3, 'SCHEDULED', 'Consulta para obturação de cárie'),
       ('2025-12-21 10:00:00', 4, 2, 'SCHEDULED', 'Consulta para avaliação de aparelho ortodôntico'),
       ('2025-12-21 15:00:00', 5, 3, 'SCHEDULED', 'Sessão de clareamento dental');

-- Insert Appointment CANCELLED
INSERT INTO tb_appointment (date, dentist_id, patient_id, status, description)
VALUES ('2025-12-22 10:00:00', 4, 2, 'CANCELED', 'Tratamento de canal (cancelado pelo paciente)'),
       ('2025-03-22 13:00:00', 5, 3, 'CANCELED', 'Extração de dente do siso (cancelada por motivos de saúde)');

-- Insert Appointment COMPLETED
INSERT INTO tb_appointment (date, dentist_id, patient_id, status, description)
VALUES ('2025-02-23 10:00:00', 4, 2, 'COMPLETED', 'Procedimento de limpeza profunda concluído com sucesso'),
       ('2025-02-23 13:00:00', 4, 2, 'COMPLETED', 'Consulta odontológica de rotina e aplicação de flúor concluída'),
       ('2025-02-10 14:30:00', 5, 3, 'COMPLETED', 'Consulta odontológica de rotina'),
       ('2025-02-15 09:00:00', 5, 3, 'COMPLETED', 'Avaliação inicial e planejamento de tratamento'),
       ('2025-02-27 10:00:00', 4, 2, 'COMPLETED', 'Procedimento de limpeza profunda');

-- Insert Ratings
INSERT INTO TB_RATING (SCORE, DATE, IS_RATED, DENTIST_ID, PATIENT_ID, APPOINTMENT_ID, COMMENT)
VALUES (10, '2024-03-10T10:30:00', true, 4, 2, 7, 'Excelente atendimento!'),
       (7, '2024-03-12T14:45:00', true, 4, 2, 8, 'Muito bom, mas poderia melhorar o tempo de espera.'),
       (5, '2024-03-15T09:15:00', true, 5, 3, 9, 'Atendimento razoável, esperava mais.'),
       (8, '2024-03-18T16:00:00', true, 5, 3, 10, 'Ótima experiência, super recomendo!');