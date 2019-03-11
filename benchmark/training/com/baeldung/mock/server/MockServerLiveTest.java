package com.baeldung.mock.server;


import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;


public class MockServerLiveTest {
    private static ClientAndServer mockServer;

    @Test
    public void whenPostRequestMockServer_thenServerReceived() {
        createExpectationForInvalidAuth();
        hitTheServerWithPostRequest();
        verifyPostRequest();
    }

    @Test
    public void whenPostRequestForInvalidAuth_then401Received() {
        createExpectationForInvalidAuth();
        HttpResponse response = hitTheServerWithPostRequest();
        Assert.assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void whenGetRequest_ThenForward() {
        createExpectationForForward();
        hitTheServerWithGetRequest("index.html");
        verifyGetRequest();
    }

    @Test
    public void whenCallbackRequest_ThenCallbackMethodCalled() {
        createExpectationForCallBack();
        HttpResponse response = hitTheServerWithGetRequest("/callback");
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
}

