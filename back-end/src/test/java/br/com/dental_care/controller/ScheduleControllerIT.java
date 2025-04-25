package br.com.dental_care.controller;

import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.util.TokenUtil;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleControllerIT extends BaseIntegrationTest {

    @Test
    void createDentistAbsence_Should_returnAbsence_When_validRequestAndDentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        AbsenceRequestDTO absenceRequest = AbsenceRequestDTO.builder()
                .absenceStart(LocalDateTime.parse("2027-04-25T09:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-04-25T17:00:00"))
                .build();


        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(absenceRequest)
                .when()
                .post("/api/v1/schedules/absences/dentist/4")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("absenceStart", Matchers.equalTo("2027-04-25T09:00:00"))
                .body("absenceEnd", Matchers.equalTo("2027-04-25T17:00:00"));
    }

    @Test
    void createDentistAbsence_Should_return403_When_validRequestAndPatientLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        AbsenceRequestDTO absenceRequest = AbsenceRequestDTO.builder()
                .absenceStart(LocalDateTime.parse("2027-04-25T09:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-04-25T17:00:00"))
                .build();


        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(absenceRequest)
                .when()
                .post("/api/v1/schedules/absences/dentist/4")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void createDentistAbsence_Should_return400_When_invalidRequest() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        AbsenceRequestDTO invalidRequest = AbsenceRequestDTO.builder()
                .absenceStart(LocalDateTime.parse("2027-04-25T17:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-04-25T09:00:00"))
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/v1/schedules/absences/dentist/4")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createDentistAbsence_Should_return409_When_creatingAbsenceForAnotherDentist() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        AbsenceRequestDTO invalidRequest = AbsenceRequestDTO.builder()
                .absenceStart(LocalDateTime.parse("2027-04-25T17:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-04-25T09:00:00"))
                .build();

        String errorMessage = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/v1/schedules/absences/dentist/5")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .jsonPath()
                .getString("error");

        assertEquals("You are not allowed to create an absence for another dentist.", errorMessage);
    }

    @Test
    void createDentistAbsence_Should_return404_When_dentistNotFound() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        AbsenceRequestDTO request = AbsenceRequestDTO.builder()
                .absenceStart(LocalDateTime.parse("2027-04-25T09:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-04-25T17:00:00"))
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/schedules/absences/dentist/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createDentistAbsence_Should_return401_When_unauthenticated() {
        AbsenceRequestDTO request = AbsenceRequestDTO.builder()
                .absenceStart(LocalDateTime.parse("2027-04-25T09:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-04-25T17:00:00"))
                .build();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/schedules/absences/dentist/4")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void removeDentistAbsence_Should_return204_When_absenceExists() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        AbsenceRequestDTO request = AbsenceRequestDTO.builder()
                .absenceStart(LocalDateTime.parse("2027-07-26T09:00:00"))
                .absenceEnd(LocalDateTime.parse("2027-07-26T17:00:00"))
                .build();

        int scheduleId = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/schedules/absences/dentist/4")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/schedules/" + scheduleId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void removeDentistAbsence_Should_return404_When_absenceNotFound() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/schedules/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void removeDentistAbsence_Should_return403_When_notAuthorizedUser() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/schedules/1")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}