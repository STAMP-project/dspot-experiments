package com.baeldung.inmemory;


import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = InMemoryAuthApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class InMemoryAuthControllerIntegrationTest {
    @Autowired
    private TestRestTemplate template;

    @Test
    public void givenRequestOnPublicService_shouldSucceedWith200() throws Exception {
        ResponseEntity<String> result = template.getForEntity("/public/hello", String.class);
        Assert.assertEquals(OK, result.getStatusCode());
    }

    @Test
    public void givenRequestOnPrivateService_shouldFailWith401() throws Exception {
        ResponseEntity<String> result = template.getForEntity("/private/hello", String.class);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        ResponseEntity<String> result = template.withBasicAuth("spring", "secret").getForEntity("/private/hello", String.class);
        Assert.assertEquals(OK, result.getStatusCode());
    }

    @Test
    public void givenInvalidAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        ResponseEntity<String> result = template.withBasicAuth("spring", "wrong").getForEntity("/private/hello", String.class);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusCode());
    }
}

