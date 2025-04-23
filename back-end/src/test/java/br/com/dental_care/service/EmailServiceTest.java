package br.com.dental_care.service;

import br.com.dental_care.exception.EmailException;
import br.com.dental_care.model.Appointment;
import br.com.dental_care.model.Dentist;
import br.com.dental_care.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "emailFrom", "noreply@dentalcare.com");
        ReflectionTestUtils.setField(emailService, "recoverUri", "https://dentalcare.com/recover/");
    }

    @Test
    void sendAppointmentConfirmationEmail_Should_sendEmailSuccessfully() {

        Patient patient = new Patient();
        patient.setName("João");
        patient.setEmail("joao@example.com");

        Dentist dentist = new Dentist();
        dentist.setName("Dra. Ana");

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setDate(LocalDateTime.parse("2025-04-25T14:30"));

        emailService.sendAppointmentConfirmationEmail(patient, appointment);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendAppointmentReminder_Should_throwEmailException_When_MailFails() {

        Appointment appointment = mock(Appointment.class);
        Patient patient = mock(Patient.class);
        Dentist dentist = mock(Dentist.class);

        when(appointment.getPatient()).thenReturn(patient);
        when(appointment.getDentist()).thenReturn(dentist);
        when(patient.getEmail()).thenReturn("fail@example.com");
        when(patient.getName()).thenReturn("Maria");
        when(dentist.getName()).thenReturn("Dr. Paulo");
        when(appointment.getDate()).thenReturn(LocalDateTime.now().plusDays(1));

        doThrow(new MailException("Fail to send mail.") {})
                .when(mailSender).send(any(SimpleMailMessage.class));

        Exception exception = assertThrows(EmailException.class, ()
                -> emailService.sendAppointmentReminder(appointment));

        assertEquals("Failed to send reminder email to the patient.", exception.getMessage());
    }

    @Test
    void sendPasswordResetTokenEmail_Should_sendEmailWithCorrectToken() {

        emailService.sendPasswordResetTokenEmail("user@example.com", "abc123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertNotNull(msg.getText());
        assertNotNull(msg.getTo());
    }
}
