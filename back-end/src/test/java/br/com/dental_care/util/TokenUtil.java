package br.com.dental_care.util;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONException;

import static io.restassured.RestAssured.given;

public class TokenUtil {

    public static String obtainAccessToken(String username, String password) throws JSONException {
        Response response = authRequest(username, password);
        JsonPath jsonBody = response.jsonPath();
        return jsonBody.getString("access_token");
    }

    private static Response authRequest(String username, String password) {
        return given()
                .auth()
                .preemptive()
                .basic("myclientid", "myclientsecret")
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/oauth2/token");
    }
}
