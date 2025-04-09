package br.com.dental_care.service;

import br.com.dental_care.exception.EmailException;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Patient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender emailSender;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void sendAppointmentConfirmationEmail(Patient patient, Appointment appointment) {
        try {
            String to = patient.getEmail();
            String subject = "Consulta marcada com sucesso!";

            String body = String.format(
                    "Olá %s,\n\n" +
                    "Sua consulta foi agendada com sucesso!\n\n" +
                    "🦷 Dentista: %s\n" +
                    "📅 Data: %s\n" +
                    "⏰ Horário: %s\n\n" +
                    "Por favor, chegue com 30 minutos de antecedência.\n" +
                    "Caso precise remarcar, pedimos que o faça com pelo menos 24 horas de antecedência.\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe Dental Care.",
                    patient.getName(),
                    appointment.getDentist().getName(),
                    appointment.getDate().toLocalDate().format(dateFormatter),
                    appointment.getDate().toLocalTime()
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
            logger.info("Email sent successfully.");
        } catch(MailException e) {
            throw new EmailException("Appointment created, email delivery failed.");
        }
    }
}
