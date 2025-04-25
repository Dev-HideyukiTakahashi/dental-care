package br.com.dental_care.controller;

import br.com.dental_care.dto.RatingDTO;
import br.com.dental_care.util.TokenUtil;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class RatingControllerTest extends BaseIntegrationTest {

    @Test
    void rateDentist_Should_return201_When_patientLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        RatingDTO ratingDTO = RatingDTO.builder()
                .score(5)
                .comment("Great service, highly recommend!")
                .patientId(2L)
                .dentistId(4L)
                .appointmentId(11L)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(ratingDTO)
                .when()
                .post("/api/v1/ratings")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("dentistId", Matchers.equalTo(4))
                .body("patientId", Matchers.equalTo(2))
                .body("appointmentId", Matchers.equalTo(11))
                .body("score", Matchers.equalTo(5))
                .body("comment", Matchers.equalTo("Great service, highly recommend!"));
    }

    @Test
    void rateDentist_Should_return403_When_adminLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("elias.warrior@example.com", "123456");

        RatingDTO ratingDTO = RatingDTO.builder()
                .score(5)
                .comment("Great service, highly recommend!")
                .patientId(2L)
                .dentistId(4L)
                .appointmentId(11L)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(ratingDTO)
                .when()
                .post("/api/v1/ratings")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void rateDentist_Should_return403_When_dentistLogged() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("victor.dent@example.com", "123456");

        RatingDTO ratingDTO = RatingDTO.builder()
                .score(5)
                .comment("Great service, highly recommend!")
                .patientId(2L)
                .dentistId(4L)
                .appointmentId(11L)
                .build();


        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(ratingDTO)
                .when()
                .post("/api/v1/ratings")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void rateDentist_Should_return422_When_appointmentAlreadyRated() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        RatingDTO ratingDTO = RatingDTO.builder()
                .score(5)
                .comment("Great service, highly recommend!")
                .patientId(2L)
                .dentistId(4L)
                .appointmentId(8L)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(ratingDTO)
                .when()
                .post("/api/v1/ratings")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("error", Matchers.equalTo("This appointment has already been rated."));
    }

    @Test
    void rateDentist_Should_return422_When_appointmentNotCompleted() throws Exception {
        String accessToken = TokenUtil.obtainAccessToken("leonardo.smile@example.com", "123456");

        RatingDTO ratingDTO = RatingDTO.builder()
                .score(5)
                .comment("Great service, highly recommend!")
                .patientId(2L)
                .dentistId(4L)
                .appointmentId(1L)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(ratingDTO)
                .when()
                .post("/api/v1/ratings")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("error", Matchers.equalTo("Appointment status must be 'COMPLETED' to proceed with the rating."));
    }
}


