package br.com.dental_care.controller;

import br.com.dental_care.util.TokenUtil;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class UserControllerIT extends BaseIntegrationTest{

    @Test
    void getMe_Should_returnPatient_When_patientLogged() throws JSONException {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/users/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("email", Matchers.equalTo("leonardo.smile@example.com"));
    }

    @Test
    void getMe_Should_return403_When_adminLogged() throws JSONException {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/users/me")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void getMe_Should_return403_When_dentistLogged() throws JSONException {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/users/me")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
