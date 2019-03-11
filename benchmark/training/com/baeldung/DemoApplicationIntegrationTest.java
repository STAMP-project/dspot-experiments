package com.baeldung;


import SpringBootTest.WebEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { DemoApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationIntegrationTest {
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void contextLoads() {
        final String result = restTemplate.getForObject((("http://localhost:" + (port)) + "/"), String.class);
        assertThat(result).isEqualTo("Hello world");
    }
}

