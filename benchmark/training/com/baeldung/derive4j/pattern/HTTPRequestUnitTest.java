package com.baeldung.derive4j.pattern;


import HTTPServer.POST_RESPONSE_BODY;
import org.junit.Assert;
import org.junit.Test;


public class HTTPRequestUnitTest {
    public static HTTPServer server;

    @Test
    public void givenHttpGETRequest_whenRequestReachesServer_thenProperResponseIsReturned() {
        HTTPRequest postRequest = HTTPRequests.POST("http://test.com/post", "Resource");
        HTTPResponse response = HTTPRequestUnitTest.server.acceptRequest(postRequest);
        Assert.assertEquals(201, response.getStatusCode());
        Assert.assertEquals(POST_RESPONSE_BODY, response.getResponseBody());
    }
}

