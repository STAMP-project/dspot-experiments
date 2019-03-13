package com.baeldung.ethereumj.controllers;


import Constants.ENDPOINT_ONE;
import Constants.ENDPOINT_TWO;
import HttpMethod.GET;
import HttpStatus.OK;
import SpringBootTest.WebEnvironment;
import com.baeldung.ethereumj.ApplicationMain;
import com.baeldung.ethereumj.transfer.EthResponse;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationMain.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8080")
public class EthControllerLiveTest {
    @LocalServerPort
    int port;

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void bestBlockTest() throws Exception {
        Thread.sleep(20000);
        EthResponse a = restTemplate.getForObject(url(ENDPOINT_ONE), EthResponse.class);
        Assert.assertNotNull(a);
        ResponseEntity<EthResponse> b = restTemplate.exchange(url(ENDPOINT_ONE), GET, new org.springframework.http.HttpEntity<EthResponse>(null, new HttpHeaders()), EthResponse.class);
        TestCase.assertTrue("Status 200?", b.getStatusCode().equals(OK));
        System.out.println(("Status 200?: " + (b.getStatusCode().equals(OK))));
        TestCase.assertTrue("Dynamic data returned?", b.hasBody());
        System.out.println(("Dynamic data returned?: " + (b.hasBody())));
    }

    @Test
    public void difficultyTest() throws Exception {
        Thread.sleep(20000);
        ResponseEntity<EthResponse> a = restTemplate.exchange(url(ENDPOINT_TWO), GET, new org.springframework.http.HttpEntity<EthResponse>(null, new HttpHeaders()), EthResponse.class);
        TestCase.assertTrue("Status 200?", a.getStatusCode().equals(OK));
        System.out.println(("Status 200?: " + (a.getStatusCode().equals(OK))));
        TestCase.assertTrue("Dynamic data returned?", a.hasBody());
        System.out.println(("Dynamic data returned?: " + (a.hasBody())));
    }
}

