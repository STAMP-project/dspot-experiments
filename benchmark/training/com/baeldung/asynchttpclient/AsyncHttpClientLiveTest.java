package com.baeldung.asynchttpclient;


import WebSocketUpgradeHandler.Builder;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.junit.Assert;
import org.junit.Test;

import static State.CONTINUE;


public class AsyncHttpClientLiveTest {
    private static AsyncHttpClient HTTP_CLIENT;

    @Test
    public void givenHttpClient_executeSyncGetRequest() {
        BoundRequestBuilder boundGetRequest = AsyncHttpClientLiveTest.HTTP_CLIENT.prepareGet("http://www.baeldung.com");
        Future<Response> responseFuture = boundGetRequest.execute();
        try {
            Response response = responseFuture.get(5000, TimeUnit.MILLISECONDS);
            Assert.assertNotNull(response);
            Assert.assertEquals(200, response.getStatusCode());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenHttpClient_executeAsyncGetRequest() {
        // execute an unbound GET request
        Request unboundGetRequest = Dsl.get("http://www.baeldung.com").build();
        AsyncHttpClientLiveTest.HTTP_CLIENT.executeRequest(unboundGetRequest, new org.asynchttpclient.AsyncCompletionHandler<Integer>() {
            @Override
            public Integer onCompleted(Response response) {
                int resposeStatusCode = response.getStatusCode();
                Assert.assertEquals(200, resposeStatusCode);
                return resposeStatusCode;
            }
        });
        // execute a bound GET request
        BoundRequestBuilder boundGetRequest = AsyncHttpClientLiveTest.HTTP_CLIENT.prepareGet("http://www.baeldung.com");
        boundGetRequest.execute(new org.asynchttpclient.AsyncCompletionHandler<Integer>() {
            @Override
            public Integer onCompleted(Response response) {
                int resposeStatusCode = response.getStatusCode();
                Assert.assertEquals(200, resposeStatusCode);
                return resposeStatusCode;
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenHttpClient_executeAsyncGetRequestWithAsyncHandler() {
        // execute an unbound GET request
        Request unboundGetRequest = Dsl.get("http://www.baeldung.com").build();
        AsyncHttpClientLiveTest.HTTP_CLIENT.executeRequest(unboundGetRequest, new org.asynchttpclient.AsyncHandler<Integer>() {
            int responseStatusCode = -1;

            @Override
            public State onStatusReceived(HttpResponseStatus responseStatus) {
                responseStatusCode = responseStatus.getStatusCode();
                return CONTINUE;
            }

            @Override
            public State onHeadersReceived(HttpHeaders headers) {
                return CONTINUE;
            }

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                return CONTINUE;
            }

            @Override
            public void onThrowable(Throwable t) {
            }

            @Override
            public Integer onCompleted() {
                Assert.assertEquals(200, responseStatusCode);
                return responseStatusCode;
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenHttpClient_executeAsyncGetRequestWithListanableFuture() {
        // execute an unbound GET request
        Request unboundGetRequest = Dsl.get("http://www.baeldung.com").build();
        ListenableFuture<Response> listenableFuture = AsyncHttpClientLiveTest.HTTP_CLIENT.executeRequest(unboundGetRequest);
        listenableFuture.addListener(() -> {
            Response response;
            try {
                response = listenableFuture.get(5000, TimeUnit.MILLISECONDS);
                assertEquals(200, response.getStatusCode());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }, Executors.newCachedThreadPool());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenWebSocketClient_tryToConnect() {
        WebSocketUpgradeHandler.Builder upgradeHandlerBuilder = new WebSocketUpgradeHandler.Builder();
        WebSocketUpgradeHandler wsHandler = upgradeHandlerBuilder.addWebSocketListener(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket websocket) {
                // WebSocket connection opened
            }

            @Override
            public void onClose(WebSocket websocket, int code, String reason) {
                // WebSocket connection closed
            }

            @Override
            public void onError(Throwable t) {
                // WebSocket connection error
                Assert.assertTrue(t.getMessage().contains("Request timeout"));
            }
        }).build();
        WebSocket WEBSOCKET_CLIENT = null;
        try {
            WEBSOCKET_CLIENT = Dsl.asyncHttpClient().prepareGet("ws://localhost:5590/websocket").addHeader("header_name", "header_value").addQueryParam("key", "value").setRequestTimeout(5000).execute(wsHandler).get();
            if (WEBSOCKET_CLIENT.isOpen()) {
                WEBSOCKET_CLIENT.sendPingFrame();
                WEBSOCKET_CLIENT.sendTextFrame("test message");
                WEBSOCKET_CLIENT.sendBinaryFrame(new byte[]{ 't', 'e', 's', 't' });
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            if ((WEBSOCKET_CLIENT != null) && (WEBSOCKET_CLIENT.isOpen())) {
                WEBSOCKET_CLIENT.sendCloseFrame(200, "OK");
            }
        }
    }
}

