-- DATABASE SEED FOR TESTS

-- Insert Roles
INSERT INTO tb_role (authority) VALUES 
    ('ROLE_ADMIN'),
    ('ROLE_PATIENT'),
    ('ROLE_DENTIST');

-- Insert Users
INSERT INTO tb_user (email, name, password, phone) VALUES
    ('elias.warrior@example.com', 'Elias Warrior', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(11) 55555-1234'),
    ('lara.bright@example.com', 'Lara Bright', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(21) 55555-2345'),
    ('leonardo.smile@example.com', 'Leonardo Smile', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(31) 55555-3456'),
    ('ariel.star@example.com', 'Ariel Star', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(41) 55555-4567'),
    ('nina.soul@example.com', 'Nina Soul', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(51) 55555-5678'),
    ('victor.dent@example.com', 'Victor Dent', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(61) 55555-6789'),
    ('jasmine.glow@example.com', 'Jasmine Glow', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(71) 55555-7890'),
    ('henry.surge@example.com', 'Henry Surge', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(81) 55555-8901'),
    ('olga.moon@example.com', 'Olga Moon', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(91) 55555-9012'),
    ('lucas.maverick@example.com', 'Lucas Maverick', '$2a$10$QFGTtkV2GZD4XNOihHxl9u2woNw7XyWvBbwQL6GvJ1w8KXaLgbSOq', '(11) 55555-0123');

-- Association User - Role
-- Admins (ROLE_ADMIN)
INSERT INTO tb_user_role (role_id, user_id) VALUES
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_ADMIN' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Elias Warrior')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_ADMIN' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Lara Bright')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_ADMIN' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Ariel Star')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_ADMIN' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Jasmine Glow'));

-- Patients (ROLE_PATIENT)
INSERT INTO tb_user_role (role_id, user_id) VALUES
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_PATIENT' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Leonardo Smile')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_PATIENT' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Nina Soul')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_PATIENT' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Olga Moon'));

-- Dentists (ROLE_DENTIST)
INSERT INTO tb_user_role (role_id, user_id) VALUES
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Victor Dent')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Henry Surge')),
    ((SELECT id FROM tb_role WHERE authority = 'ROLE_DENTIST' LIMIT 1), (SELECT id FROM tb_user WHERE name = 'Lucas Maverick'));

-- Insert Notifications
INSERT INTO tb_notification (message, sent_date, user_id) VALUES
    ('Consulta agendada para amanhã às 10h.', '2025-03-18 09:00:00', 1),
    ('Lembrete: Revisão odontológica semestral.', '2025-03-19 08:30:00', 2),
    ('Pagamento da consulta confirmado.', '2025-03-17 15:45:00', 3),
    ('Novo agendamento disponível para sua especialidade.', '2025-03-16 14:20:00', 1),
    ('Seu dentista adicionou novas recomendações.', '2025-03-15 10:10:00', 2);