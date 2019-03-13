package com.baeldung.springmustache;


import HttpStatus.OK;
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
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringMustacheApplicationIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void givenIndexPageWhenContainsArticleThenTrue() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/article", String.class);
        Assert.assertTrue(entity.getStatusCode().equals(OK));
        Assert.assertTrue(entity.getBody().contains("Article Title 0"));
    }
}

