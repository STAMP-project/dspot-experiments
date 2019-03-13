package com.baeldung.spring.cloud.zuulratelimitdemo.controller;


import SpringBootTest.WebEnvironment;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GreetingControllerUnitTest {
    private static final String SIMPLE_GREETING = "/greeting/simple";

    private static final String ADVANCED_GREETING = "/greeting/advanced";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenRequestNotExceedingCapacity_thenReturnOkResponse() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(GreetingControllerUnitTest.SIMPLE_GREETING, String.class);
        HttpHeaders headers = response.getHeaders();
        String key = "rate-limit-application_serviceSimple_127.0.0.1";
        String limit = headers.getFirst(((HEADER_LIMIT) + key));
        String remaining = headers.getFirst(((HEADER_REMAINING) + key));
        String reset = headers.getFirst(((HEADER_RESET) + key));
        Assert.assertEquals(limit, "5");
        Assert.assertEquals(remaining, "4");
        Assert.assertEquals(reset, "60000");
        Assert.assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void whenRequestExceedingCapacity_thenReturnTooManyRequestsResponse() throws InterruptedException {
        ResponseEntity<String> response = this.restTemplate.getForEntity(GreetingControllerUnitTest.ADVANCED_GREETING, String.class);
        HttpHeaders headers = response.getHeaders();
        String key = "rate-limit-application_serviceAdvanced_127.0.0.1";
        assertHeaders(headers, key, false, false);
        Assert.assertEquals(OK, response.getStatusCode());
        for (int i = 0; i < 2; i++) {
            response = this.restTemplate.getForEntity(GreetingControllerUnitTest.ADVANCED_GREETING, String.class);
        }
        headers = response.getHeaders();
        String limit = headers.getFirst(((HEADER_LIMIT) + key));
        String remaining = headers.getFirst(((HEADER_REMAINING) + key));
        String reset = headers.getFirst(((HEADER_RESET) + key));
        Assert.assertEquals(limit, "1");
        Assert.assertEquals(remaining, "0");
        Assert.assertNotEquals(reset, "2000");
        Assert.assertEquals(TOO_MANY_REQUESTS, response.getStatusCode());
        TimeUnit.SECONDS.sleep(2);
        response = this.restTemplate.getForEntity(GreetingControllerUnitTest.ADVANCED_GREETING, String.class);
        headers = response.getHeaders();
        assertHeaders(headers, key, false, false);
        Assert.assertEquals(OK, response.getStatusCode());
    }
}

