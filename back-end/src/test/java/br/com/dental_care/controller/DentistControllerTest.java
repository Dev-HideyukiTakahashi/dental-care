package br.com.dental_care.controller;

import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import br.com.dental_care.dto.CreateDentistDTO;
import br.com.dental_care.dto.UpdateDentistDTO;
import br.com.dental_care.util.TokenUtil;
import io.restassured.http.ContentType;

public class DentistControllerTest extends BaseIntegrationTest {

    @Test
    void findById_Should_return200_When_adminLoggedAndDentistExists() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/dentists/4")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(4))
                .body("email", Matchers.notNullValue());
    }

    @Test
    void findById_Should_return200_When_patientLoggedAndDentistExists() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/dentists/4")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(4))
                .body("email", Matchers.notNullValue());
    }

    @Test
    void findById_Should_return403_When_dentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/dentists/4")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void findAll_Should_return200_When_adminLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/dentists")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.notNullValue());
    }

    @Test
    void findAll_Should_return200_When_patientLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/dentists")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", Matchers.notNullValue());
    }

    @Test
    void findAll_Should_return403_When_dentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/dentists")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void insert_Should_return201_When_adminLoggedAndValidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        CreateDentistDTO dto = CreateDentistDTO.builder()
                .name("Dr. Teste")
                .email("dr.teste@example.com")
                .speciality("Orthodontics")
                .password("#Newpassword123")
                .registrationNumber("123456")
                .phone("11999998888")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/dentists")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .body("email", Matchers.equalTo("dr.teste@example.com"));
    }

    @Test
    void insert_Should_return422_When_adminLoggedAndInvalidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        CreateDentistDTO dto = CreateDentistDTO.builder()
                .name("Dr. Teste")
                .email("")
                .speciality("")
                .password("111")
                .registrationNumber("123456")
                .phone("11999998888")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/dentists")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void insert_Should_return403_When_dentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        CreateDentistDTO dto = CreateDentistDTO.builder()
                .name("Dr. Teste")
                .email("dr.teste@example.com")
                .speciality("Orthodontics")
                .password("#Newpassword123")
                .registrationNumber("123456")
                .phone("11999998888")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/dentists")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void update_Should_return200_When_adminLoggedAndValidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        UpdateDentistDTO dto = UpdateDentistDTO.builder()
                .name("Dr. Updated")
                .email("dr.updated@example.com")
                .speciality("Orthodontics")
                .password("#Newpassword123")
                .registrationNumber("654321")
                .phone("11988887777")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/dentists/5")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", Matchers.equalTo("Dr. Updated"))
                .body("email", Matchers.equalTo("dr.updated@example.com"));
    }

    @Test
    void update_Should_return200_When_dentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        UpdateDentistDTO dto = UpdateDentistDTO.builder()
                .name("Dr. Updated")
                .email("dr.updated@example.com")
                .speciality("Orthodontics")
                .password("#Newpassword123")
                .registrationNumber("654321")
                .phone("11988887777")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/dentists/5")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", Matchers.equalTo("Dr. Updated"))
                .body("email", Matchers.equalTo("dr.updated@example.com"));
    }

    @Test
    void update_Should_return422_When_adminLoggedAndInvalidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        UpdateDentistDTO dto = UpdateDentistDTO.builder()
                .name("Dr. Updated")
                .email("")
                .speciality("Orthodontics")
                .password("#Newpassword123")
                .registrationNumber("654321")
                .phone("11988887777")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/dentists/5")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void deleteById_Should_return204_When_adminDeletesDentistWithoutDependencies() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        CreateDentistDTO newDentist = CreateDentistDTO.builder()
                .name("Dr. Teste")
                .email("dr.teste2@example.com")
                .speciality("Orthodontics")
                .registrationNumber("123456")
                .password("#Minhasenha123")
                .phone("11999998888")
                .build();

        Integer dentistId = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newDentist)
                .when()
                .post("/api/v1/dentists")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/v1/dentists/" + dentistId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteById_Should_return409_When_adminDeletesDentistWithDependencies() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/v1/dentists/4")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void changePassword_ShouldReturn204_WhenDentistLoggedAndValidData() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("henry.surge@example.com", "123456");

        String payload = """
                    {
                        "username": "henry.surge@example.com",
                        "password": "123456",
                        "newPassword": "#NewPassword123",
                        "confirmPassword": "#NewPassword123"
                    }
                """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/v1/dentists/change-password")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void changePassword_ShouldReturn409_WhenCurrentPasswordIsIncorrect() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        String payload = """
                    {
                        "username": "victor.dent@example.com",
                        "password": "senhaErrada",
                        "newPassword": "#NewPassword123",
                        "confirmPassword": "#NewPassword123"
                    }
                """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/v1/dentists/change-password")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("error", Matchers.equalTo("The current password is incorrect."));
    }

    @Test
    void changePassword_ShouldReturn409_WhenNewPasswordAndConfirmationDoNotMatch() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        String payload = """
                    {
                        "username": "victor.dent@example.com",
                        "password": "123456",
                        "newPassword": "#NewPassword123",
                        "confirmPassword": "outraSenha123"
                    }
                """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/v1/dentists/change-password")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("error", Matchers.equalTo("New password and confirmation password do not match."));
    }

    @Test
    void changePassword_ShouldReturn403_WhenPatientLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        String payload = """
                    {
                        "username": "leonardo.smile@example.com",
                        "password": "123456",
                        "newPassword": "#NewPassword123",
                        "confirmPassword": "#NewPassword123"
                    }
                """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/v1/dentists/change-password")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

}
