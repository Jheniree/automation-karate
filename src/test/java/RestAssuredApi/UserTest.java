package RestAssuredApi;

import ResponseObjects.MessageResponse;
import ResponseObjects.User;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {

    private static final String BASE_URI = "http://127.0.0.1:8900/";
    private static final String ID = "pepe@pepe.pe1-0.1";
    private static final String ID_ERROR = "Jheni";
    private static final String CONTENT_TYPE = "application/json";

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    //POST
    @Test
    public void shouldBeAbleToDoAPostUser() {
        String payload = "{\n" +
                "  \"email\": \"jhenigc@prueba.com\",\n" +
                "  \"name\": \"Jheni\"\n" +
                "}";
        User userCreated = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("user")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().as(User.class);

        assertNotNull(userCreated);
        assertTrue(userCreated.getName().equals("Jheni"));
    }

    @Test
    public void shouldBeAbleToShowErrorWrongEmail() {
        String errormessage = "malformed email";
        String payload = "{\n" +
                "  \"email\": \"jhenigcprueba.com\",\n" +
                "  \"name\": \"Jheni\"\n" +
                "}";
        MessageResponse messageResponse = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("user")
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .extract()
                .as(MessageResponse.class);

        assertEquals(messageResponse.getMessage(), errormessage);
    }

    @Test
    public void shouldBeAbleToShowErrorNameNull() {
        String errormessage = "Check fields";
        String payload = "{\n" +
                "  \"email\": \"jhenigc@prueba.com\",\n" +
                "  \"name\": \"\"\n" +
                "}";
        String messageResponse = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("user")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .extract()
                .asString();

        assertEquals(messageResponse, errormessage);
    }


    //GET
    @Test
    public void shouldBeAbleToGetUserById() throws IOException {

        User user = when()
                .get("user?id={ID}", ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(User.class);

        assertTrue(user.getName().length() > 0);
    }

    @Test
    public void shouldBeAbleToGetUserByErrorId() throws IOException {

        String expectedError = "User not found";

        String error1 = when()
                .get("user?id={ID}", ID_ERROR)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .extract()
                .asString();

        assertEquals(error1, expectedError);
    }

    @Test
    public void shouldBeAbleToGetUserByError() throws IOException {

        String expectedError1 = "No message available";

        MessageResponse messageResponse = when()
                .get("/user/id")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .extract()
                .as(MessageResponse.class);

        assertEquals(messageResponse.getMessage(), expectedError1);
    }

    @Test
    public void shouldBeAbleToGetUserAll() throws IOException {

        User[] users = when()
                .get("user/all")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(User[].class);

        assertTrue(users.length > 0);
        assertTrue(users[0].getEmail().length() > 0);
    }
}