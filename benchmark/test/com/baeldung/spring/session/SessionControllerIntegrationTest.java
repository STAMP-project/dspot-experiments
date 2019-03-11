package com.baeldung.spring.session;


import HttpMethod.GET;
import HttpStatus.UNAUTHORIZED;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SessionWebApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class SessionControllerIntegrationTest {
    private Jedis jedis;

    private static RedisServer redisServer;

    private TestRestTemplate testRestTemplate;

    private TestRestTemplate testRestTemplateWithAuth;

    private String testUrl = "http://localhost:8080/";

    @Test
    public void testRedisIsEmpty() {
        Set<String> result = jedis.keys("*");
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testUnauthenticatedCantAccess() {
        ResponseEntity<String> result = testRestTemplate.getForEntity(testUrl, String.class);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void testRedisControlsSession() {
        ResponseEntity<String> result = testRestTemplateWithAuth.getForEntity(testUrl, String.class);
        Assert.assertEquals("hello admin", result.getBody());// login worked

        Set<String> redisResult = jedis.keys("*");
        Assert.assertTrue(((redisResult.size()) > 0));// redis is populated with session data

        String sessionCookie = result.getHeaders().get("Set-Cookie").get(0).split(";")[0];
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<String> httpEntity = new HttpEntity(headers);
        result = testRestTemplate.exchange(testUrl, GET, httpEntity, String.class);
        Assert.assertEquals("hello admin", result.getBody());// access with session works worked

        jedis.flushAll();// clear all keys in redis

        result = testRestTemplate.exchange(testUrl, GET, httpEntity, String.class);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusCode());// access denied after sessions are removed in redis

    }
}

