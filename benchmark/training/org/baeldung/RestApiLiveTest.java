package org.baeldung;


import HttpStatus.CONFLICT;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import MediaType.APPLICATION_JSON_VALUE;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.baeldung.persistence.model.BookReview;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BookReviewsApiApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class RestApiLiveTest {
    private static final String API_URI = "http://localhost:8085/reviews";

    // GET
    @Test
    public void whenGetAllReviews_thenOK() {
        final Response response = RestAssured.get(RestApiLiveTest.API_URI);
        Assert.assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    public void whenGetCreatedReviewById_thenOK() {
        final BookReview review = createRandomReview();
        final String location = createReviewAsUri(review);
        final Response response = RestAssured.get(location);
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertEquals(review.getContent(), response.jsonPath().get("content"));
    }

    @Test
    public void whenGetCreatedReviewByBookId_thenOK() {
        final BookReview review = createRandomReview();
        createReviewAsUri(review);
        final Response response = RestAssured.get((((RestApiLiveTest.API_URI) + "/search/findByBookId?bookId=") + (review.getBookId())));
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertTrue(((response.jsonPath().getLong("page.totalElements")) > 0));
    }

    @Test
    public void whenGetNotExistReviewById_thenNotFound() {
        final Response response = RestAssured.get((((RestApiLiveTest.API_URI) + "/") + (RandomStringUtils.randomNumeric(4))));
        Assert.assertEquals(NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void whenGetNotExistReviewByBookId_thenNotFound() {
        final Response response = RestAssured.get((((RestApiLiveTest.API_URI) + "/search/findByBookId?bookId=") + (RandomStringUtils.randomNumeric(4))));
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertTrue(((response.jsonPath().getLong("page.totalElements")) == 0));
    }

    // POST
    @Test
    public void whenCreateNewReview_thenCreated() {
        final BookReview review = createRandomReview();
        final Response response = RestAssured.given().contentType(APPLICATION_JSON_VALUE).body(review).post(RestApiLiveTest.API_URI);
        Assert.assertEquals(CREATED.value(), response.getStatusCode());
    }

    @Test
    public void whenCreateInvalidReview_thenError() {
        final BookReview review = createRandomReview();
        review.setBookId(null);
        final Response response = RestAssured.given().contentType(APPLICATION_JSON_VALUE).body(review).post(RestApiLiveTest.API_URI);
        Assert.assertEquals(CONFLICT.value(), response.getStatusCode());
    }

    @Test
    public void whenUpdateCreatedReview_thenUpdated() {
        // create
        final BookReview review = createRandomReview();
        final String location = createReviewAsUri(review);
        // update
        review.setRating(4);
        Response response = RestAssured.given().contentType(APPLICATION_JSON_VALUE).body(review).put(location);
        Assert.assertEquals(OK.value(), response.getStatusCode());
        // check if changes saved
        response = RestAssured.get(location);
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertEquals(4, response.jsonPath().getInt("rating"));
    }

    @Test
    public void whenDeleteCreatedReview_thenOk() {
        // create
        final BookReview review = createRandomReview();
        final String location = createReviewAsUri(review);
        // delete
        Response response = RestAssured.delete(location);
        Assert.assertEquals(NO_CONTENT.value(), response.getStatusCode());
        // confirm it was deleted
        response = RestAssured.get(location);
        Assert.assertEquals(NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void whenDeleteNotExistReview_thenError() {
        final Response response = RestAssured.delete((((RestApiLiveTest.API_URI) + "/") + (RandomStringUtils.randomNumeric(4))));
        Assert.assertEquals(NOT_FOUND.value(), response.getStatusCode());
    }
}

