package com.baeldung.spring.cloud.ribbon.client;


import SpringBootTest.WebEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerLocationApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ServerLocationAppIntegrationTest {
    ConfigurableApplicationContext application2;

    ConfigurableApplicationContext application3;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void loadBalancingServersTest() throws InterruptedException {
        ResponseEntity<String> response = this.testRestTemplate.getForEntity((("http://localhost:" + (this.port)) + "/server-location"), String.class);
        Assert.assertEquals(response.getBody(), "Australia");
    }
}

