package br.com.dental_care.controller;

import br.com.dental_care.dto.EmailDTO;
import br.com.dental_care.dto.PatientDTO;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class AuthControllerTest extends BaseIntegrationTest {

    @Test
    void registerPatient_Should_return201_When_dataIsValid() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                .name("John Doe")
                .email("new.patient@example.com")
                .password("#Password123")
                .phone("(11) 99710-2376")
                .build();


        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/auth/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("email", Matchers.equalTo("new.patient@example.com"));
    }

    @Test
    void registerPatient_Should_return422_When_dataIsInvalid() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                .name("John Doe")
                .email("")
                .password("#Password123")
                .phone("(11) 99710-2376")
                .build();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/auth/signup")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void registerPatient_Should_return409_When_emailAlreadyRegistered() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                .name("John Doe")
                .email("leonardo.smile@example.com")
                .password("#Password123")
                .phone("(11) 99710-2376")
                .build();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/auth/signup")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void createRecoverToken_Should_return401_when_emailConfigurationIsMissing() {
        EmailDTO dto = EmailDTO.builder().email("leonardo.smile@example.com").build();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/auth/recover-token")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void createRecoverToken_Should_return404_when_emailNotExists() {
        EmailDTO dto = EmailDTO.builder().email("test@example.com").build();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/auth/recover-token")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}