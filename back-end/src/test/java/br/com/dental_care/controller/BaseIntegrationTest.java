package br.com.dental_care.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void initRestAssured() {
        RestAssured.baseURI = "http://localhost";
    }

    @BeforeEach
    void setupPort() {
        RestAssured.port = port;
    }
}