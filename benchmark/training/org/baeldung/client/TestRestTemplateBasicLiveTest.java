package org.baeldung.client;


import HttpStatus.OK;
import MediaType.APPLICATION_JSON;
import TestRestTemplate.HttpClientOption;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class TestRestTemplateBasicLiveTest {
    private RestTemplate restTemplate;

    private static final String FOO_RESOURCE_URL = ("http://localhost:" + 8082) + "/spring-rest/foos";

    private static final String URL_SECURED_BY_AUTHENTICATION = "http://httpbin.org/basic-auth/user/passwd";

    private static final String BASE_URL = ("http://localhost:" + 8082) + "/spring-rest";

    // GET
    @Test
    public void givenTestRestTemplate_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<String> response = testRestTemplate.getForEntity(((TestRestTemplateBasicLiveTest.FOO_RESOURCE_URL) + "/1"), String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    @Test
    public void givenRestTemplateWrapper_whenSendGetForEntity_thenStatusOk() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.configure(restTemplate);
        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder);
        ResponseEntity<String> response = testRestTemplate.getForEntity(((TestRestTemplateBasicLiveTest.FOO_RESOURCE_URL) + "/1"), String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    @Test
    public void givenRestTemplateBuilderWrapper_whenSendGetForEntity_thenStatusOk() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.build();
        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder);
        ResponseEntity<String> response = testRestTemplate.getForEntity(((TestRestTemplateBasicLiveTest.FOO_RESOURCE_URL) + "/1"), String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    @Test
    public void givenRestTemplateWrapperWithCredentials_whenSendGetForEntity_thenStatusOk() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.configure(restTemplate);
        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder, "user", "passwd");
        ResponseEntity<String> response = testRestTemplate.getForEntity(TestRestTemplateBasicLiveTest.URL_SECURED_BY_AUTHENTICATION, String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    @Test
    public void givenTestRestTemplateWithCredentials_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd");
        ResponseEntity<String> response = testRestTemplate.getForEntity(TestRestTemplateBasicLiveTest.URL_SECURED_BY_AUTHENTICATION, String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    @Test
    public void givenTestRestTemplateWithBasicAuth_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<String> response = testRestTemplate.withBasicAuth("user", "passwd").getForEntity(TestRestTemplateBasicLiveTest.URL_SECURED_BY_AUTHENTICATION, String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    @Test
    public void givenTestRestTemplateWithCredentialsAndEnabledCookies_whenSendGetForEntity_thenStatusOk() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd", HttpClientOption.ENABLE_COOKIES);
        ResponseEntity<String> response = testRestTemplate.getForEntity(TestRestTemplateBasicLiveTest.URL_SECURED_BY_AUTHENTICATION, String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    // HEAD
    @Test
    public void givenFooService_whenCallHeadForHeaders_thenReceiveAllHeaders() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        final HttpHeaders httpHeaders = testRestTemplate.headForHeaders(TestRestTemplateBasicLiveTest.FOO_RESOURCE_URL);
        Assert.assertTrue(httpHeaders.getContentType().includes(APPLICATION_JSON));
    }

    // POST
    @Test
    public void givenService_whenPostForObject_thenCreatedObjectIsReturned() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd");
        final RequestBody body = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), "{\"id\":1,\"name\":\"Jim\"}");
        final Request request = new Request.Builder().url(((TestRestTemplateBasicLiveTest.BASE_URL) + "/users/detail")).post(body).build();
        testRestTemplate.postForObject(TestRestTemplateBasicLiveTest.URL_SECURED_BY_AUTHENTICATION, request, String.class);
    }

    // PUT
    @Test
    public void givenService_whenPutForObject_thenCreatedObjectIsReturned() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user", "passwd");
        final RequestBody body = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), "{\"id\":1,\"name\":\"Jim\"}");
        final Request request = new Request.Builder().url(((TestRestTemplateBasicLiveTest.BASE_URL) + "/users/detail")).post(body).build();
        testRestTemplate.put(TestRestTemplateBasicLiveTest.URL_SECURED_BY_AUTHENTICATION, request, String.class);
    }
}

