package br.com.dental_care.controller;

import br.com.dental_care.dto.PatientDTO;
import br.com.dental_care.util.TokenUtil;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class PatientControllerTest extends BaseIntegrationTest {

    @Test
    void findById_Should_return200_When_adminLoggedAndPatientExists() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/patients/2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(2))
                .body("email", Matchers.equalTo("leonardo.smile@example.com"));
    }

    @Test
    void findById_Should_return403_When_patientLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/patients/1")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void findById_Should_return403_When_dentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/patients/1")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void findAll_Should_return200_When_adminLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/patients")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", Matchers.greaterThan(0));
    }

    @Test
    void findAll_Should_return403_When_patientLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/patients")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void findAll_Should_return403_When_dentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/patients")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void insert_Should_return201_When_adminLoggedAndValidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        PatientDTO patientDTO = PatientDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("#Newpassword123")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(patientDTO)
                .when()
                .post("/api/v1/patients")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.greaterThan(0))
                .body("email", Matchers.equalTo("john.doe@example.com"));
    }

    @Test
    void insert_Should_return422_When_adminLoggedAndInvalidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        PatientDTO patientDTO = PatientDTO.builder()
                .name("John Doe")
                .email("test")
                .password("")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(patientDTO)
                .when()
                .post("/api/v1/patients")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void insert_Should_return403_When_patientLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        PatientDTO patientDTO = PatientDTO.builder()
                .name("John Doe")
                .email("john.doe2@example.com")
                .password("#Newpassword123")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(patientDTO)
                .when()
                .post("/api/v1/patients")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void update_Should_return200_When_patientLoggedAndValidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("yuki.murasaki90@gmail.com", "123456");

        PatientDTO patientDTO = PatientDTO.builder()
                .name("Updated Name")
                .email("updated.email@example.com")
                .password("#Updatepassword123")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(patientDTO)
                .when()
                .put("/api/v1/patients/3")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("email", Matchers.equalTo("updated.email@example.com"));
    }

    @Test
    void update_Should_return422_When_patientLoggedAndInvalidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        PatientDTO patientDTO = PatientDTO.builder()
                .name("Updated Name")
                .email("")
                .password("#Updatepassword123")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(patientDTO)
                .when()
                .put("/api/v1/patients/2")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void update_Should_return403_When_patientTriesToUpdateAnotherPatientData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        PatientDTO patientDTO = PatientDTO.builder()
                .name("Updated Name")
                .email("updated.email@example.com")
                .password("#Updatepassword123")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(patientDTO)
                .when()
                .put("/api/v1/patients/3")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void update_Should_return200_When_adminLoggedAndValidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        PatientDTO patientDTO = PatientDTO.builder()
                .name("Updated Name")
                .email("updated.email2@example.com")
                .password("#Updatepassword123")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(patientDTO)
                .when()
                .put("/api/v1/patients/3")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("email", Matchers.equalTo("updated.email2@example.com"));
    }

    @Test
    void delete_Should_return409_When_adminDeletesPatientWithDependencies() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/patients/2")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void deletePatient_ShouldReturn204_When_adminDeletesPatientWithoutDependencies() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        PatientDTO newPatient = PatientDTO.builder()
                .name("Temporary Patient")
                .email("temp.patient@example.com")
                .password("#TempPassword123")
                .build();

        Integer patientId =
                given()
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(newPatient)
                        .when()
                        .post("/api/v1/patients")
                        .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .path("id");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/patients/" + patientId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void delete_Should_return403_When_patientLoggedAndTriesToDeleteAnotherPatient() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .delete("/api/v1/patients/2")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}