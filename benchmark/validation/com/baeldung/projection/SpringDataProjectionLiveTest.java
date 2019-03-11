package com.baeldung.projection;


import com.baeldung.SpringDataRestApplication;
import com.baeldung.repositories.AuthorRepository;
import com.baeldung.repositories.BookRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringDataRestApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class SpringDataProjectionLiveTest {
    private static final String BOOK_ENDPOINT = "http://localhost:8080/books";

    private static final String AUTHOR_ENDPOINT = "http://localhost:8080/authors";

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private AuthorRepository authorRepo;

    @Test
    public void whenGetBook_thenOK() {
        final Response response = RestAssured.get(((SpringDataProjectionLiveTest.BOOK_ENDPOINT) + "/1"));
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertTrue(response.asString().contains("isbn"));
        Assert.assertFalse(response.asString().contains("authorCount"));
        // System.out.println(response.asString());
    }

    @Test
    public void whenGetBookProjection_thenOK() {
        final Response response = RestAssured.get(((SpringDataProjectionLiveTest.BOOK_ENDPOINT) + "/1?projection=customBook"));
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertFalse(response.asString().contains("isbn"));
        Assert.assertTrue(response.asString().contains("authorCount"));
        // System.out.println(response.asString());
    }

    @Test
    public void whenGetAllBooks_thenOK() {
        final Response response = RestAssured.get(SpringDataProjectionLiveTest.BOOK_ENDPOINT);
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertFalse(response.asString().contains("isbn"));
        Assert.assertTrue(response.asString().contains("authorCount"));
        // System.out.println(response.asString());
    }

    @Test
    public void whenGetAuthorBooks_thenOK() {
        final Response response = RestAssured.get(((SpringDataProjectionLiveTest.AUTHOR_ENDPOINT) + "/1/books"));
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertFalse(response.asString().contains("isbn"));
        Assert.assertTrue(response.asString().contains("authorCount"));
        System.out.println(response.asString());
    }
}

