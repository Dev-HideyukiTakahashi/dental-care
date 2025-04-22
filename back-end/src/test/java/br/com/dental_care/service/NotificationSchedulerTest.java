package br.com.dental_care.service;

import br.com.dental_care.model.Appointment;
import br.com.dental_care.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationSchedulerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @Test
    public void sendAppointmentReminders_Should_sendEmails_When_appointmentsExist() {

        // Arrange
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        Appointment appointment = new Appointment();
        appointment.setDate(tomorrow.withHour(10).withMinute(0));
        List<Appointment> appointments = List.of(appointment);

        // Mock behavior of appointmentRepository
        when(appointmentRepository.findByDateBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(appointments);

        // Act
        notificationScheduler.sendAppointmentReminders();

        // Assert
        verify(emailService, times(1)).sendAppointmentReminder(appointment);
    }

    @Test
    public void sendAppointmentReminders_Should_logNoAppointments_When_NoAppointmentsFound() {

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Appointment> appointments = Collections.emptyList();

        when(appointmentRepository.findByDateBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(appointments);

        notificationScheduler.sendAppointmentReminders();

        verify(emailService, never()).sendAppointmentReminder(any(Appointment.class));
    }

    @Test
    public void sendAppointmentReminders_Should_sendMultipleEmails_When_MultipleAppointmentsExist() {

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Appointment appointment1 = new Appointment();
        appointment1.setDate(tomorrow.withHour(10).withMinute(0));

        Appointment appointment2 = new Appointment();
        appointment2.setDate(tomorrow.withHour(14).withMinute(0));

        List<Appointment> appointments = List.of(appointment1, appointment2);

        when(appointmentRepository.findByDateBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(appointments);

        notificationScheduler.sendAppointmentReminders();

        verify(emailService, times(2)).sendAppointmentReminder(any(Appointment.class));
    }
}
