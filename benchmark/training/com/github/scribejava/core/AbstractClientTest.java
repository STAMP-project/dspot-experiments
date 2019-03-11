package com.github.scribejava.core;


import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.github.scribejava.core.utils.StreamUtils;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractClientTest {
    private OAuthService oAuthService;

    private static class TestCallback implements OAuthAsyncRequestCallback<Response> {
        private Response response;

        @Override
        public void onCompleted(Response response) {
            this.response = response;
        }

        @Override
        public void onThrowable(Throwable throwable) {
        }

        public Response getResponse() {
            return response;
        }
    }

    @Test
    public void shouldSendGetRequest() throws Exception {
        final String expectedResponseBody = "response body for test shouldSendGetRequest";
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(expectedResponseBody));
        server.start();
        final HttpUrl baseUrl = server.url("/testUrl");
        final OAuthRequest request = new OAuthRequest(Verb.GET, baseUrl.toString());
        final Response response = oAuthService.execute(request, null).get(30, TimeUnit.SECONDS);
        Assert.assertEquals(expectedResponseBody, response.getBody());
        final RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("GET", recordedRequest.getMethod());
        server.shutdown();
    }

    @Test
    public void shouldSendPostRequest() throws Exception {
        final String expectedResponseBody = "response body for test shouldSendPostRequest";
        final String expectedRequestBody = "request body";
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(expectedResponseBody));
        server.enqueue(new MockResponse().setBody(expectedResponseBody));
        server.start();
        final HttpUrl baseUrl = server.url("/testUrl");
        // request with body
        OAuthRequest request = new OAuthRequest(Verb.POST, baseUrl.toString());
        request.setPayload(expectedRequestBody);
        Response response = oAuthService.execute(request, null).get(30, TimeUnit.SECONDS);
        Assert.assertEquals(expectedResponseBody, response.getBody());
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("POST", recordedRequest.getMethod());
        Assert.assertEquals(expectedRequestBody, recordedRequest.getBody().readUtf8());
        // request with empty body
        request = new OAuthRequest(Verb.POST, baseUrl.toString());
        response = oAuthService.execute(request, null).get(30, TimeUnit.SECONDS);
        Assert.assertEquals(expectedResponseBody, response.getBody());
        recordedRequest = server.takeRequest();
        Assert.assertEquals("POST", recordedRequest.getMethod());
        Assert.assertEquals("", recordedRequest.getBody().readUtf8());
        server.shutdown();
    }

    @Test
    public void shouldReadResponseStream() throws Exception {
        final String expectedResponseBody = "response body for test shouldReadResponseStream";
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(expectedResponseBody));
        server.start();
        final HttpUrl baseUrl = server.url("/testUrl");
        final OAuthRequest request = new OAuthRequest(Verb.GET, baseUrl.toString());
        final Response response = oAuthService.execute(request, null).get(30, TimeUnit.SECONDS);
        Assert.assertEquals(expectedResponseBody, StreamUtils.getStreamContents(response.getStream()));
        final RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("GET", recordedRequest.getMethod());
        server.shutdown();
    }

    @Test
    public void shouldCallCallback() throws Exception {
        final String expectedResponseBody = "response body for test shouldCallCallback";
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(expectedResponseBody));
        server.start();
        final HttpUrl baseUrl = server.url("/testUrl");
        final OAuthRequest request = new OAuthRequest(Verb.GET, baseUrl.toString());
        final AbstractClientTest.TestCallback callback = new AbstractClientTest.TestCallback();
        oAuthService.execute(request, callback).get();
        Assert.assertEquals(expectedResponseBody, callback.getResponse().getBody());
        server.shutdown();
    }

    @Test
    public void shouldPassErrors() throws Exception {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(500));
        server.start();
        final HttpUrl baseUrl = server.url("/testUrl");
        final OAuthRequest request = new OAuthRequest(Verb.GET, baseUrl.toString());
        final AbstractClientTest.TestCallback callback = new AbstractClientTest.TestCallback();
        final Response response = oAuthService.execute(request, callback).get();
        Assert.assertEquals(500, response.getCode());
        Assert.assertEquals(500, callback.getResponse().getCode());
        server.shutdown();
    }
}

