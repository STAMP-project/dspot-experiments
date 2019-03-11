package com.baeldung;


import HttpStatus.BAD_REQUEST;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import MediaType.APPLICATION_JSON_VALUE;
import com.baeldung.persistence.model.Book;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;


public class SpringBootBootstrapLiveTest {
    private static final String API_ROOT = "http://localhost:8080/api/books";

    @Test
    public void whenGetAllBooks_thenOK() {
        final Response response = RestAssured.get(SpringBootBootstrapLiveTest.API_ROOT);
        Assert.assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    public void whenGetBooksByTitle_thenOK() {
        final Book book = createRandomBook();
        createBookAsUri(book);
        final Response response = RestAssured.get((((SpringBootBootstrapLiveTest.API_ROOT) + "/title/") + (book.getTitle())));
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertTrue(((response.as(List.class).size()) > 0));
    }

    @Test
    public void whenGetCreatedBookById_thenOK() {
        final Book book = createRandomBook();
        final String location = createBookAsUri(book);
        final Response response = RestAssured.get(location);
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertEquals(book.getTitle(), response.jsonPath().get("title"));
    }

    @Test
    public void whenGetNotExistBookById_thenNotFound() {
        final Response response = RestAssured.get((((SpringBootBootstrapLiveTest.API_ROOT) + "/") + (RandomStringUtils.randomNumeric(4))));
        Assert.assertEquals(NOT_FOUND.value(), response.getStatusCode());
    }

    // POST
    @Test
    public void whenCreateNewBook_thenCreated() {
        final Book book = createRandomBook();
        final Response response = RestAssured.given().contentType(APPLICATION_JSON_VALUE).body(book).post(SpringBootBootstrapLiveTest.API_ROOT);
        Assert.assertEquals(CREATED.value(), response.getStatusCode());
    }

    @Test
    public void whenInvalidBook_thenError() {
        final Book book = createRandomBook();
        book.setAuthor(null);
        final Response response = RestAssured.given().contentType(APPLICATION_JSON_VALUE).body(book).post(SpringBootBootstrapLiveTest.API_ROOT);
        Assert.assertEquals(BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    public void whenUpdateCreatedBook_thenUpdated() {
        final Book book = createRandomBook();
        final String location = createBookAsUri(book);
        book.setId(Long.parseLong(location.split("api/books/")[1]));
        book.setAuthor("newAuthor");
        Response response = RestAssured.given().contentType(APPLICATION_JSON_VALUE).body(book).put(location);
        Assert.assertEquals(OK.value(), response.getStatusCode());
        response = RestAssured.get(location);
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertEquals("newAuthor", response.jsonPath().get("author"));
    }

    @Test
    public void whenDeleteCreatedBook_thenOk() {
        final Book book = createRandomBook();
        final String location = createBookAsUri(book);
        Response response = RestAssured.delete(location);
        Assert.assertEquals(OK.value(), response.getStatusCode());
        response = RestAssured.get(location);
        Assert.assertEquals(NOT_FOUND.value(), response.getStatusCode());
    }
}

