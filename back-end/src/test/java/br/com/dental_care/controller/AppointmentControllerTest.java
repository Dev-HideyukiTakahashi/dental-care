package br.com.dental_care.controller;


import br.com.dental_care.dto.AppointmentDTO;
import br.com.dental_care.dto.DentistMinDTO;
import br.com.dental_care.dto.PatientMinDTO;
import br.com.dental_care.model.enums.AppointmentStatus;
import br.com.dental_care.util.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AppointmentControllerTest extends BaseIntegrationTest {


    private String adminToken;
    private String patientToken;
    private String dentistToken;
    private DentistMinDTO dentist;
    private PatientMinDTO patient;
    private AppointmentDTO newAppointmentDTO;
    private AppointmentDTO existsAppointmentDTO;

    @BeforeEach
    void customSetup() throws JSONException {
        adminToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");
        patientToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");
        dentistToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");
        ;

        dentist = DentistMinDTO.builder()
                .id(4L)
                .speciality("Ortodontia")
                .name("Victor Dent")
                .registrationNumber("DR12345")
                .score(9)
                .build();

        patient = PatientMinDTO.builder()
                .id(2L)
                .medicalHistory("Limpeza dentária")
                .name("Leonardo Smile")
                .phone("(31) 55555-3456")
                .build();

        existsAppointmentDTO = AppointmentDTO.builder()
                .date(LocalDateTime.parse("2025-12-20T09:00:00"))
                .status(AppointmentStatus.SCHEDULED)
                .description("Consulta de rotina e limpeza dental")
                .dentistMinDTO(dentist)
                .patientMinDTO(patient)
                .build();

        newAppointmentDTO = AppointmentDTO.builder()
                .date(LocalDateTime.parse("2027-12-20T09:00:00"))
                .status(AppointmentStatus.SCHEDULED)
                .description("Testando nova consulta")
                .dentistMinDTO(dentist)
                .patientMinDTO(patient)
                .build();

    }

    @Test
    void createAppointment_Should_return201AndLocationHeader_When_creatingAppointmentWithValidPatientAndData() {

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(newAppointmentDTO)
                .when()
                .post("/api/v1/appointments")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("dentist.id", equalTo(4))
                .body("patient.id", equalTo(2));
    }

    @Test
    void createAppointment_Should_return422_When_creatingAppointmentWithInvalidPatientAndData() {

        AppointmentDTO invalidDTO = existsAppointmentDTO
                .toBuilder()
                .description("")
                .date(null)
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(invalidDTO)
                .when()
                .post("/api/v1/appointments")
                .then()
                .statusCode(422)
                .body("fields.message", hasItems(
                        "The date is required.",
                        "Description is required."
                ));
    }

    @Test
    void createAppointment_Should_return409_When_creatingAppointmentAtAlreadyDentistScheduledTime() {

        AppointmentDTO conflictingAppointmentDTO = newAppointmentDTO
                .toBuilder()
                .date(LocalDateTime.parse("2025-12-20T14:00:00"))
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(conflictingAppointmentDTO)
                .when()
                .post("/api/v1/appointments")
                .then()
                .statusCode(409)
                .body("error", equalTo("An appointment already exists for this time slot."));
    }

    @Test
    void createAppointment_Should_return409_When_appointmentFallsWithinAnotherSlot() {

        AppointmentDTO conflictingAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .date(LocalDateTime.parse("2025-12-20T14:30:00"))
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(conflictingAppointmentDTO)
                .when()
                .post("/api/v1/appointments")
                .then()
                .statusCode(409)
                .body("error", equalTo("The time falls within another appointment slot."));
    }

    @Test
    void createAppointment_Should_return401_When_notAuthenticated() {

        DentistMinDTO invalidDentist = dentist.toBuilder().id(999L).build();

        AppointmentDTO conflictingAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .dentistMinDTO(invalidDentist)
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(conflictingAppointmentDTO)
                .when()
                .post("/api/v1/appointments")
                .then()
                .statusCode(404);
    }

    @Test
    void createAppointment_Should_return404_When_dentistNotFound() {

        AppointmentDTO conflictingAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .date(LocalDateTime.parse("2025-12-20T14:30:00"))
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(conflictingAppointmentDTO)
                .when()
                .post("/api/v1/appointments")
                .then()
                .statusCode(401);
    }

    @Test
    void createAppointment_Should_return403_When_patientAttemptsToScheduleForAnotherPatient() {

        PatientMinDTO anotherPatient = patient.toBuilder().id(3L).build();

        AppointmentDTO conflictingAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .patientMinDTO(anotherPatient)
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(conflictingAppointmentDTO)
                .when()
                .post("/api/v1/appointments")
                .then()
                .statusCode(403)
                .body("error", equalTo("Access denied: You do not have permission to perform this action"));
    }

    @Test
    void findById_Should_return200_When_adminLoggedAndAppointmentExists() {

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/appointments/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1));
    }

    @Test
    void findById_Should_return200_When_patientLogged() {

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/appointments/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1))
                .body("patient.id", equalTo(2));
    }

    @Test
    void findById_Should_return403_When_dentistLogged() throws Exception {

        given()
                .header("Authorization", "Bearer " + dentistToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/appointments/1")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void cancelAppointment_Should_return200_When_appointmentExistsAndPatientLogged() {

        Long existingAppointmentId = 3L;

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/cancel", existingAppointmentId)
                .then()
                .statusCode(200)
                .body("id", equalTo(3))
                .body("status", equalTo("CANCELED"));
    }

    @Test
    void cancelAppointment_Should_return401_When_notAuthenticated() {

        Long existingAppointmentId = 1L;

        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/cancel", existingAppointmentId)
                .then()
                .statusCode(401);
    }

    @Test
    void cancelAppointment_Should_return403_When_dentistLogged() {

        Long existingAppointmentId = 1L;

        given()
                .header("Authorization", "Bearer " + dentistToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/cancel", existingAppointmentId)
                .then()
                .statusCode(403);
    }

    @Test
    void cancelAppointment_Should_return403_When_patientAttemptsToCancelAppointmentForAnotherPatient() {

        Long anotherPatientAppointmentId = 2L;

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/cancel", anotherPatientAppointmentId)
                .then()
                .statusCode(403);
    }

    @Test
    void cancelAppointment_Should_return404_When_appointmentNotFound() {

        Long nonExistingId = 999L;

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/cancel", nonExistingId)
                .then()
                .statusCode(404);
    }

    @Test
    void cancelAppointment_Should_return409_When_appointmentAlreadyCanceled() {

        Long alreadyCanceledAppointment = 6L;

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/cancel", alreadyCanceledAppointment)
                .then()
                .statusCode(409)
                .body("error", equalTo("The appointment has already been completed or canceled."));
    }

    @Test
    void cancelAppointment_Should_return409_When_appointmentAlreadyCompleted() {

        Long alreadyCompletedAppointment = 6L;

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/cancel", alreadyCompletedAppointment)
                .then()
                .statusCode(409)
                .body("error", equalTo("The appointment has already been completed or canceled."));
    }

    @Test
    void completeAppointment_Should_return200_When_appointmentExistsAndAdminLogged() {

        Long existingAppointment = 4L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/complete", existingAppointment)
                .then()
                .statusCode(200)
                .body("id", equalTo(4))
                .body("status", equalTo("COMPLETED"));
    }

    @Test
    void completeAppointment_Should_return401_When_notAuthenticated() {

        Long existingAppointment = 1L;

        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/complete", existingAppointment)
                .then()
                .statusCode(401);
    }

    @Test
    void completeAppointment_Should_return403_When_patientTriesToCompleteAppointment() {

        Long existingAppointment = 3L;

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/complete", existingAppointment)
                .then()
                .statusCode(403);
    }

    @Test
    void completeAppointment_Should_return404_When_appointmentNotFound() {

        Long nonExistingAppointment = 999L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/complete", nonExistingAppointment)
                .then()
                .statusCode(404);
    }

    @Test
    void completeAppointment_Should_return409_When_appointmentAlreadyCompleted() {

        Long alreadyCompletedAppointment = 7L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/complete", alreadyCompletedAppointment)
                .then()
                .statusCode(409)
                .body("error", equalTo("The appointment has already been completed or canceled."));
    }

    @Test
    void completeAppointment_Should_return409_When_appointmentCanceled() {

        Long alreadyCanceledAppointment = 6L;

        AppointmentDTO completedAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .status(AppointmentStatus.CANCELED)
                .build();

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/appointments/{id}/complete", alreadyCanceledAppointment)
                .then()
                .statusCode(409)
                .body("error", equalTo("The appointment has already been completed or canceled."));
    }

    @Test
    void updateAppointmentDateTime_Should_return200_When_appointmentExistsAndValidDateTime() {

        AppointmentDTO updatedAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .id(1L)
                .date(LocalDateTime.parse("2025-12-21T19:00:00"))
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(updatedAppointmentDTO)
                .when()
                .put("/api/v1/appointments/{id}", 4L)
                .then()
                .statusCode(200)
                .body("id", equalTo(4))
                .body("status", equalTo("SCHEDULED"))
                .body("date", equalTo("2025-12-21T19:00:00"));
    }

    @Test
    void updateAppointmentDateTime_Should_return401_When_notAuthenticated() {

        AppointmentDTO updatedAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .id(4L)
                .date(LocalDateTime.parse("2025-12-21T19:00:00"))
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(updatedAppointmentDTO)
                .when()
                .put("/api/v1/appointments/{id}", 1L)
                .then()
                .statusCode(401);
    }

    @Test
    void updateAppointmentDateTime_Should_return403_When_patientAttemptsToUpdateAppointmentForAnotherPatient() {

        PatientMinDTO anotherPatient = patient.toBuilder().id(3L).build();

        AppointmentDTO conflictingAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .patientMinDTO(anotherPatient)
                .build();

        AppointmentDTO updatedAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .date(LocalDateTime.now().plusMinutes(30))
                .build();

        given()
                .header("Authorization", "Bearer " + dentistToken)
                .contentType(ContentType.JSON)
                .body(conflictingAppointmentDTO)
                .when()
                .put("/api/v1/appointments/{id}", 1L)
                .then()
                .statusCode(403);
    }

    @Test
    void updateAppointmentDateTime_Should_return404_When_appointmentNotFound() {

        AppointmentDTO nonExistsAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .id(999L)
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(nonExistsAppointmentDTO)
                .when()
                .put("/api/v1/appointments/{id}", 999L)
                .then()
                .statusCode(404);
    }

    @Test
    void updateAppointmentDateTime_Should_return409_When_appointmentFallsWithinAnotherSlot() {

        AppointmentDTO updatedAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .date(LocalDateTime.parse("2025-12-20T14:30:00"))
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(updatedAppointmentDTO)
                .when()
                .put("/api/v1/appointments/{id}", 1L)
                .then()
                .statusCode(409)
                .body("error", equalTo("The time falls within another appointment slot."));
    }

    @Test
    void updateAppointmentDateTime_Should_return409_When_conflictsWithExistingTimeAppointment(){

        AppointmentDTO updatedAppointmentDTO = existsAppointmentDTO
                .toBuilder()
                .date(LocalDateTime.parse("2025-12-20T14:00:00"))
                .build();

        given()
                .header("Authorization", "Bearer " + patientToken)
                .contentType(ContentType.JSON)
                .body(updatedAppointmentDTO)
                .when()
                .put("/api/v1/appointments/{id}", 1L)
                .then()
                .statusCode(409)
                .body("error", equalTo("An appointment already exists for this time slot."));
    }
}
