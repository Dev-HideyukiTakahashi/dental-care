-- DATABASE SEED FOR TESTS

-- Insert Roles
INSERT INTO tb_role (authority)
VALUES ('ROLE_ADMIN'),
       ('ROLE_PATIENT'),
       ('ROLE_DENTIST');

-- Insert Users
INSERT INTO tb_user (email, name, password, phone)
VALUES ('elias.warrior@example.com', 'Elias Warrior', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq',
        '(11) 55555-1234'),
       ('leonardo.smile@example.com', 'Leonardo Smile', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq',
        '(31) 55555-3456'),
       ('nina.soul@example.com', 'Nina Soul', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq',
        '(51) 55555-5678'),
       ('victor.dent@example.com', 'Victor Dent', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq',
        '(61) 55555-6789'),
       ('henry.surge@example.com', 'Henry Surge', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq',
        '(81) 55555-8901');

-- Admin 
INSERT INTO tb_admin (id)
VALUES (1);

-- Patient
INSERT INTO tb_patient (id, medical_history)
VALUES (2, 'Limpeza dentária'),
       (3, 'Tratamento de canal');

-- Dentist
INSERT INTO tb_dentist (id, registration_number, speciality)
VALUES (4, 'DR12345', 'Ortodontia'),
       (5, 'DR23456', 'Periodontia');

-- Association User - Role
-- Admin (ROLE_ADMIN)
INSERT INTO tb_user_role (role_id, user_id)
VALUES ((SELECT id FROM tb_role WHERE authority = 'ROLE_ADMIN' LIMIT 1),
       (SELECT id FROM tb_user WHERE name = 'Elias Warrior') );

-- Patients (ROLE_PATIENT)
INSERT INTO tb_user_role (role_id, user_id)
VALUES ((SELECT id FROM tb_role WHERE authority = 'ROLE_PATIENT' LIMIT 1),
       (SELECT id FROM tb_user WHERE name = 'Leonardo Smile')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_PATIENT' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Nina Soul'));

-- Dentists (ROLE_DENTIST)
INSERT INTO tb_user_role (role_id, user_id)
VALUES ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1),
       (SELECT id FROM tb_user WHERE name = 'Victor Dent')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Henry Surge'));

-- Insert Notifications
INSERT INTO tb_notification (message, sent_date, user_id)
VALUES ('Consulta agendada para amanhã às 10h.', '2025-03-18 09:00:00', 1),
       ('Lembrete: Revisão odontológica semestral.', '2025-03-19 08:30:00', 2),
       ('Pagamento da consulta confirmado.', '2025-03-17 15:45:00', 3),
       ('Novo agendamento disponível para sua especialidade.', '2025-03-16 14:20:00', 1),
       ('Seu dentista adicionou novas recomendações.', '2025-03-15 10:10:00', 2);

-- Insert Ratings
INSERT INTO TB_RATING (SCORE, DATE, DENTIST_ID, ID, PATIENT_ID, COMMENT)
VALUES (5, '2024-03-10T10:30:00', 4, 1, 2, 'Excelente atendimento!'),
       (4, '2024-03-12T14:45:00', 5, 2, 3, 'Muito bom, mas poderia melhorar o tempo de espera.'),
       (3, '2024-03-15T09:15:00', 4, 3, 2, 'Atendimento razoável, esperava mais.'),
       (5, '2024-03-18T16:00:00', 5, 4, 3, 'Ótima experiência, super recomendo!');

-- Insert Schedule dentist id 4
INSERT INTO tb_schedule (dentist_id, unavailable_time_slot)
VALUES (4, '2025-03-21 09:00:00'),
       (4, '2025-03-21 14:00:00');

-- Insert Schedule dentist id 5
INSERT INTO tb_schedule (dentist_id, unavailable_time_slot)
VALUES (5, '2025-03-22 10:00:00'),
       (5, '2025-03-22 13:00:00'),
       (5, '2025-03-22 15:00:00'),
       (5, '2025-03-22 17:00:00');

-- Insert Appointment SCHEDULED
INSERT INTO tb_appointment (date, dentist_id, patient_id, status, description)
VALUES ('2025-03-20 09:00:00', 4, 2, 'SCHEDULED', 'Consulta de rotina e limpeza dental'),
       ('2025-03-20 14:00:00', 4, 3, 'SCHEDULED', 'Consulta para obturação de cárie'),
       ('2025-03-21 10:00:00', 5, 2, 'SCHEDULED', 'Consulta para avaliação de aparelho ortodôntico'),
       ('2025-03-21 15:00:00', 5, 3, 'SCHEDULED', 'Sessão de clareamento dental');

-- Insert Appointment CANCELLED
INSERT INTO tb_appointment (date, dentist_id, patient_id, status, description)
VALUES ('2025-03-22 10:00:00', 4, 2, 'CANCELLED', 'Tratamento de canal (cancelado pelo paciente)'),
       ('2025-03-22 13:00:00', 5, 3, 'CANCELLED', 'Extração de dente do siso (cancelada por motivos de saúde)');

-- Insert Appointment COMPLETED
INSERT INTO tb_appointment (date, dentist_id, patient_id, status, description)
VALUES ('2025-03-23 10:00:00', 4, 3, 'COMPLETED', 'Procedimento de limpeza profunda concluído com sucesso'),
       ('2025-03-23 13:00:00', 5, 2, 'COMPLETED', 'Consulta odontológica de rotina e aplicação de flúor concluída');