package br.com.dental_care.controller;

import static io.restassured.RestAssured.given;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import br.com.dental_care.dto.AbsenceRequestDTO;
import br.com.dental_care.util.TokenUtil;
import io.restassured.http.ContentType;

public class ScheduleControllerTest extends BaseIntegrationTest {

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
                .post("/api/v1/schedules")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("absenceStart", Matchers.equalTo("2027-04-25T09:00:00"))
                .body("absenceEnd", Matchers.equalTo("2027-04-25T17:00:00"));
    }

    @Test
    void createDentistAbsence_Should_return403_When_notDentistLogged() throws Exception {
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
                .post("/api/v1/schedules")
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
                .post("/api/v1/schedules")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
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
                .post("/api/v1/schedules/")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void removeDentistAbsence_Should_return204_When_absenceExists() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("alex.green@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/schedules")
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
                .delete("/api/v1/schedules")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void findSelfAbsence_Should_ReturnAbsenceDTO_When_DentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("john.doe@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schedules/self")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.notNullValue());
    }

    @Test
    void findSelfAbsence_Should_Return401_When_Unauthenticated() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schedules/self")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

}