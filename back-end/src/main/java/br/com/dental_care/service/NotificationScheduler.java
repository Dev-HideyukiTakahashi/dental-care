package br.com.dental_care.service;

import br.com.dental_care.model.Appointment;
import br.com.dental_care.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Scheduled(cron = "0 0 8 * * *")
    public void sendAppointmentReminders() {
        logger.info("Starting appointment reminder notifications for patients.");

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        List<Appointment> appointments = appointmentRepository.findByDateBetween(
                tomorrow.withHour(0).withMinute(0),
                tomorrow.withHour(23).withMinute(59)
        );

        if(appointments.isEmpty()) {
            logger.info("No scheduled appointments for tomorrow.");
            return;
        }

        appointments.forEach(emailService::sendAppointmentReminder);
        logger.info("Total reminder emails successfully sent: {}", appointments.size());
    }
}
