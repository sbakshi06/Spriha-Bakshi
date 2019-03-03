package com.heroku.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.heroku.base.TestBase;
import com.heroku.model.Booking;
import com.heroku.model.BookingResponse;
import com.heroku.model.LoginRequest;
import com.heroku.util.ConversionUtil;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.heroku.constants.TestConstant.*;
import static io.restassured.RestAssured.given;

/***
 * The below class is to test the playground API by Heroku
 */
public class PlaygroundBookerAPITest extends TestBase {

    private Booking bookingRequest;
    private String allBookingsGetPath;
    private String bookingPathWithId;
    private String bookingPath;
    private String authTokenPath;
    private String userToken;

    /***
     * This method is executed once before all the tests are executed.
      */
    @BeforeTest
    public void init() {
        RestAssured.baseURI = PROPS.getProperty(API_BASE_URL);
        allBookingsGetPath = PROPS.getProperty(BOOKINGS_API_GET_PATH);
        bookingPathWithId = PROPS.getProperty(BOOKING_API_GET_PATH);
        bookingPath = PROPS.getProperty(BOOKING_API_POST_PATH);
        authTokenPath = PROPS.getProperty(BOOKING_API_TOKEN_PATH);
    }

    /***
     * This method is executed once before every test method.
     */
    @BeforeMethod
    public void setup() {
        userToken = generateToken(LoginRequest.builder().username(PROPS.getProperty(USERNAME))
                .password(PROPS.getProperty(PASSWORD)).build());
    }

    /***
     * This method will validate the auth token generation.
     */
    @Test
    public void validateAuthTokenGeneration() {
        Assert.assertNotNull(userToken);
    }


    /***
     * This test will fetch the list of all bookings and validate response.
     */
    @Test
    public void validateAllExistingBookings() {
        List<BookingResponse> responseList = Arrays.asList(given()
                .when().get(allBookingsGetPath)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().body().as(BookingResponse[].class));

        Assert.assertNotNull(responseList);
        Assert.assertTrue(responseList.size() > 0);
        for (BookingResponse response : responseList) {
            Assert.assertTrue(Objects.nonNull(response.getBookingid()));
        }
    }

    /***
     * This test will validate the creation of new booking.
     * The response is compared with the input data request
     * @throws IOException
     */
    @Test
    public void validateNewBookingByUser() throws IOException {
        bookingRequest = ConversionUtil.convertFileContentToObject("jsons/test-create-booking.json",
                new TypeReference<Booking>() {
                });
        BookingResponse bookingResponse = given().contentType("application/json").body(bookingRequest)
                .when().post(bookingPath)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().body().as(BookingResponse.class);

        Assert.assertNotNull(bookingResponse.getBookingid());
        Assert.assertEquals(bookingResponse.getBooking().getFirstname(), bookingRequest.getFirstname());
        Assert.assertEquals(bookingResponse.getBooking().getLastname(), bookingRequest.getLastname());
        Assert.assertEquals(bookingResponse.getBooking().getTotalprice(), bookingRequest.getTotalprice());
        Assert.assertEquals(bookingResponse.getBooking().getBookingdates().getCheckin(),
                bookingRequest.getBookingdates().getCheckin());
        Assert.assertEquals(bookingResponse.getBooking().getBookingdates().getCheckout(),
                bookingRequest.getBookingdates().getCheckout());
        Assert.assertEquals(bookingResponse.getBooking().getDepositpaid(), bookingRequest.getDepositpaid());
        Assert.assertEquals(bookingResponse.getBooking().getAdditionalneeds(), bookingRequest.getAdditionalneeds());
    }

    /***
     * Validate response for a single booking using booking id.
     */
    @Test
    public void validateSingleBookingInformation() {
        bookingRequest = given().pathParam("id", 1)
                .when().get(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().body().as(Booking.class);

        Assert.assertNotNull(bookingRequest.getFirstname());
        Assert.assertNotNull(bookingRequest.getLastname());
        Assert.assertNotNull(bookingRequest.getTotalprice());
        Assert.assertNotNull(bookingRequest.getBookingdates().getCheckin());
        Assert.assertNotNull(bookingRequest.getBookingdates().getCheckout());
    }

    /***
     * Validate response with updated request data.
     * @throws IOException
     */
    @Test
    public void validateUpdateOnExistingBooking() throws IOException {
        bookingRequest = ConversionUtil.convertFileContentToObject("jsons/test-update-booking.json",
                new TypeReference<Booking>() {
                });
        Booking bookingRespone = given().contentType("application/json").accept("application/json")
                .cookie("token", userToken)
                .when().body(bookingRequest).pathParam("id", 1).put(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().body().as(Booking.class);

        Assert.assertEquals(bookingRespone.getFirstname(), bookingRequest.getFirstname());
    }

    /***
     * Validate deletion of existing booking.
     */
    @Test
    public void validateDeletionOfExistingBooking() {
        given().contentType("application/json").cookie("token", userToken)
                .when().pathParam("id", 10).delete(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_CREATED);
    }

    /***
     * Validate response upon partially updating booking details
     */
    @Test
    public void validatePartialUpdate() {
        JSONObject jsonObject = new JSONObject().put("firstname", "James").put("lastname", "Brown");
        bookingRequest = given().contentType("application/json").accept("application/json")
                .cookie("token", userToken)
                .when().body(jsonObject.toString()).pathParam("id", 1).patch(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().body().as(Booking.class);

        Assert.assertEquals(bookingRequest.getFirstname(), "James");
        Assert.assertEquals(bookingRequest.getLastname(), "Brown");
    }

    /***
     * Validate response for a booking id that does not exist.
     */
    @Test
    public void validateGetOnNonExistentBookingID() {
        given().pathParam("id", 100001122)
                .when().get(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    /***
     * Validate response upon updating booking with id that does not exist.
     */
    @Test
    public void validatePartialUpdateOnNonExistentBooking() {
        JSONObject jsonObject = new JSONObject().put("firstname", "James");
        given().contentType("application/json").accept("application/json").cookie("token", userToken)
                .when().body(jsonObject.toString()).pathParam("id", 1234).patch(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    /***
     * Validate response upon partially updating booking with valid id but invalid attribute in request.
     */
    @Test
    public void validatePartialUpdateOnNonExistingAttribute() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nonExistingAttribute", "James");
        given().contentType("application/json").accept("application/json").cookie("token", userToken)
                .when().body(jsonObject.toString()).pathParam("id", 1).patch(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_OK);
    }

    /***
     * Validate response on existing booking with incorrect attribute names
     */
    @Test
    public void validateUpdateOnIncorrectAttributeNames() {
        JSONObject bookingDates = new JSONObject()
                .put("checkin", "2018-01-01")
                .put("checkout", "2018-02-01");
        JSONObject jsonObject = new JSONObject()
                .put("firstName", "James")
                .put("lastName", "Brown")
                .put("totalprice", 111)
                .put("depositpaid", true)
                .put("additionalneeds", "Iron")
                .put("bookingdates", bookingDates);

        given().contentType("application/json").accept("application/json").cookie("token", userToken)
                .when().body(jsonObject.toString()).pathParam("id", 1).put(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    /***
     * Validate response upon updating a non existent booking id
     */
    @Test
    public void validateUpdateOnNonExistentBooking() {
        JSONObject bookingDates = new JSONObject()
                .put("checkin", "2018-01-01")
                .put("checkout", "2018-02-01");
        JSONObject jsonObject = new JSONObject()
                .put("firstname", "James")
                .put("lastname", "Brown")
                .put("totalprice", 111)
                .put("depositpaid", true)
                .put("additionalneeds", "Iron")
                .put("bookingdates", bookingDates);

        given().contentType("application/json").accept("application/json").cookie("token", userToken)
                .when().body(jsonObject.toString()).pathParam("id", 134).put(bookingPathWithId)
                .then().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    /***
     * Helper method to generate AuthToken
     * @param loginRequest
     * @return
     */
    private String generateToken(LoginRequest loginRequest) {
        return given().urlEncodingEnabled(true)
                .contentType("application/json").body(loginRequest)
                .when().post(authTokenPath)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("token");
    }

}


