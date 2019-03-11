package com.baeldung.restart;


import HttpMethod.POST;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;


public class RestartApplicationIntegrationTest {
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void givenBootApp_whenRestart_thenOk() throws Exception {
        Application.main(new String[0]);
        ResponseEntity response = restTemplate.exchange("http://localhost:8080/restart", POST, null, Object.class);
        Assert.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void givenBootApp_whenRestartUsingActuator_thenOk() throws Exception {
        Application.main(new String[]{ "--server.port=8090" });
        ResponseEntity response = restTemplate.exchange("http://localhost:8090/restartApp", POST, null, Object.class);
        Assert.assertEquals(200, response.getStatusCode().value());
    }
}

