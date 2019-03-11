package org.baeldung.web;


import HttpStatus.BAD_REQUEST;
import HttpStatus.METHOD_NOT_ALLOWED;
import HttpStatus.NOT_FOUND;
import HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class FooLiveTest {
    private static final String URL_PREFIX = "http://localhost:8080/spring-security-rest";

    // private FormAuthConfig formConfig = new FormAuthConfig(URL_PREFIX + "/login", "temporary", "temporary");
    private String cookie;

    @Test
    public void whenTry_thenOK() {
        final Response response = givenAuth().get(((FooLiveTest.URL_PREFIX) + "/api/foos"));
        Assert.assertEquals(200, response.statusCode());
        System.out.println(response.asString());
    }

    @Test
    public void whenMethodArgumentMismatch_thenBadRequest() {
        final Response response = givenAuth().get(((FooLiveTest.URL_PREFIX) + "/api/foos/ccc"));
        final ApiError error = response.as(ApiError.class);
        Assert.assertEquals(BAD_REQUEST, error.getStatus());
        Assert.assertEquals(1, error.getErrors().size());
        Assert.assertTrue(error.getErrors().get(0).contains("should be of type"));
    }

    @Test
    public void whenNoHandlerForHttpRequest_thenNotFound() {
        final Response response = givenAuth().delete(((FooLiveTest.URL_PREFIX) + "/api/xx"));
        final ApiError error = response.as(ApiError.class);
        Assert.assertEquals(NOT_FOUND, error.getStatus());
        Assert.assertEquals(1, error.getErrors().size());
        Assert.assertTrue(error.getErrors().get(0).contains("No handler found"));
        System.out.println(response.asString());
    }

    @Test
    public void whenHttpRequestMethodNotSupported_thenMethodNotAllowed() {
        final Response response = givenAuth().delete(((FooLiveTest.URL_PREFIX) + "/api/foos/1"));
        final ApiError error = response.as(ApiError.class);
        Assert.assertEquals(METHOD_NOT_ALLOWED, error.getStatus());
        Assert.assertEquals(1, error.getErrors().size());
        Assert.assertTrue(error.getErrors().get(0).contains("Supported methods are"));
        System.out.println(response.asString());
    }

    @Test
    public void whenSendInvalidHttpMediaType_thenUnsupportedMediaType() {
        final Response response = givenAuth().body("").post(((FooLiveTest.URL_PREFIX) + "/api/foos"));
        final ApiError error = response.as(ApiError.class);
        Assert.assertEquals(UNSUPPORTED_MEDIA_TYPE, error.getStatus());
        Assert.assertEquals(1, error.getErrors().size());
        Assert.assertTrue(error.getErrors().get(0).contains("media type is not supported"));
        System.out.println(response.asString());
    }
}

