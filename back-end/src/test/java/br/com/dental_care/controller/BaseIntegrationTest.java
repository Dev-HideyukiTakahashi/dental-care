package br.com.dental_care.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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