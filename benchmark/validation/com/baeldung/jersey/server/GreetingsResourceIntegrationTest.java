package com.baeldung.jersey.server;


import HttpHeaders.CONTENT_TYPE;
import MediaType.TEXT_HTML;
import Status.OK;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


public class GreetingsResourceIntegrationTest extends JerseyTest {
    @Test
    public void givenGetHiGreeting_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
        Response response = target("/greetings/hi").request().get();
        Assert.assertEquals("Http Response should be 200: ", OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Http Content-Type should be: ", TEXT_HTML, response.getHeaderString(CONTENT_TYPE));
        String content = response.readEntity(String.class);
        Assert.assertEquals("Content of ressponse is: ", "hi", content);
    }
}

