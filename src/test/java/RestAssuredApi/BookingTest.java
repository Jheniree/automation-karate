package RestAssuredApi;

import ResponseObjects.Booking;
import ResponseObjects.MessageResponse;
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
public class BookingTest {

    private static final String BASE_URI = "http://127.0.0.1:8900";
    private static final String ENDPOINT = "/booking";
    private static final String ID = "pepe@pepe.pe1-0.1";
    private static final String WRONG_ID = "pepe@pepe.pe1-0.WRONG";
    private static final String DATE = "2019-11-22";
    private static final String WRONG_DATE = "2019-22-33";
    private static final String CONTENT_TYPE = "application/json";

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
    }


    //POST

    @Test
    public void shouldBeAbleToDoAPostBooking() {
        String payload = "{\n" +
                "  \"date\": \"2019-11-22\",\n" +
                "  \"destination\": \"FRA\",\n" +
                "  \"id\": \"" + ID + "\",\n" +
                "  \"origin\": \"MAD\"\n" +
                "}";

        Booking bookingCreated = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("booking")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().as(Booking.class);

        assertNotNull(bookingCreated);
        assertTrue(bookingCreated.getIdUser().equals(ID));
    }

    @Test
    public void shouldBeAbleToShowErrorWhenWrongDestination() {
        String errorCodeIata = "Origin or Destination is not a IATA code (Three Uppercase Letters)";
        String payload = "{\n" +
                "  \"date\": \"2019-11-22\",\n" +
                "  \"destination\": \"FRANKFURT\",\n" +
                "  \"id\": \"" + ID + "\",\n" +
                "  \"origin\": \"MAD\"\n" +
                "}";

        String errorIata = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("booking")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .extract().asString();

        assertEquals(errorCodeIata, errorIata);

    }

    @Test
    public void shouldBeAbleToShowErrorWhenWrongOrigin() {
        String errorCodeIata = "Origin or Destination is not a IATA code (Three Uppercase Letters)";
        String payload = "{\n" +
                "  \"date\": \"2019-11-22\",\n" +
                "  \"destination\": \"FRA\",\n" +
                "  \"id\": \"" + ID + "\",\n" +
                "  \"origin\": \"MADRID\"\n" +
                "}";

        String errorIata = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("booking")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .extract().asString();

        assertEquals(errorCodeIata, errorIata);

    }

    @Test
    public void shouldBeAbleToShowErrorWhenPostBookingByWrongDate() throws IOException {
        String expectedError = "Date format not valid";
        String payload = "{\n" +
                "  \"date\": \"" + WRONG_DATE + "\",\n" +
                "  \"destination\": \"FRA\",\n" +
                "  \"id\": \"" + ID + "\",\n" +
                "  \"origin\": \"MAD\"\n" +
                "}";
        String errorResponse = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("booking")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().asString();


        assertEquals(errorResponse, expectedError);
    }

    @Test
    public void shouldBeAbleToShowErrorMessageWhenWrongPostId() throws IOException {
        String expectedError = "Internal Server Error";

        String payload = "{\n" +
                "  \"date\": \"2019-02-22\",\n" +
                "  \"destination\": \"FRA\",\n" +
                "  \"id\": \"" + WRONG_ID + "\",\n" +
                "  \"origin\": \"MAD\"\n" +
                "}";

        MessageResponse messageResponse = given()
                .contentType(CONTENT_TYPE)
                .body(payload)
                .when()
                .post("booking")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .extract()
                .as(MessageResponse.class);

        assertEquals(messageResponse.getError(), expectedError);
    }

    //GET

    @Test
    public void shouldBeAbleToGetBookingById() throws IOException {

        Booking[] bookings = when()
                .get("booking?id={ID}", ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Booking[].class);

        assertTrue(bookings.length > 0);
        assertTrue(bookings[0].getIdUser().equals(ID));
    }

    @Test
    public void shouldBeAbleToGetBookingByDate() throws IOException {

        Booking[] bookings = when()
                .get("booking?date={Date}", DATE)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Booking[].class);

        assertTrue(bookings.length > 0);
        assertTrue(bookings[0].getDate().equals(DATE));

    }

    @Test
    public void shouldBeAbleToGetBookingByIdDate() throws IOException {
        Booking[] bookings = when()
                .get("booking?id={ID}&date={Date}", ID, DATE)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Booking[].class);

        assertTrue(bookings.length > 0);
        assertTrue(bookings[0].getDate().equals(DATE));
    }

    @Test
    public void shouldBeAbleToShowErrorMessageWhenWrongGetEndpoint() throws IOException {
        String expectedError = "No message available";

        MessageResponse messageResponse = when()
                .get("/booking/id")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .extract()
                .as(MessageResponse.class);

        assertEquals(messageResponse.getMessage(), expectedError);
    }

    @Test
    public void shouldBeAbleToShowErrorWhenGetBookingByWrongDate() throws IOException {
        String expectedError = "Format date not valid";

        MessageResponse errorResponse = when()
                .get("booking?date={Date}", WRONG_DATE)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .extract()
                .as(MessageResponse.class);

        assertEquals(errorResponse.getMessage(), expectedError);
    }
}
