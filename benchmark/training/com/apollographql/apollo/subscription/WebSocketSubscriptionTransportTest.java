package com.apollographql.apollo.subscription;


import OperationServerMessage.ConnectionAcknowledge;
import Protocol.HTTP_1_0;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;
import okhttp3.Request;
import okhttp3.Response.Builder;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


public class WebSocketSubscriptionTransportTest {
    private Request webSocketRequest;

    private WebSocketSubscriptionTransportTest.MockWebSocketFactory webSocketFactory;

    private WebSocketSubscriptionTransport subscriptionTransport;

    @Test
    public void connect() {
        assertThat(subscriptionTransport.webSocket.get()).isNull();
        assertThat(subscriptionTransport.webSocketListener.get()).isNull();
        subscriptionTransport.connect();
        assertThat(subscriptionTransport.webSocket.get()).isNotNull();
        assertThat(subscriptionTransport.webSocketListener.get()).isNotNull();
        assertThat(webSocketFactory.request.header("Sec-WebSocket-Protocol")).isEqualTo("graphql-ws");
        assertThat(webSocketFactory.request.header("Cookie")).isEqualTo("");
    }

    @Test
    public void disconnect() {
        subscriptionTransport.connect();
        assertThat(subscriptionTransport.webSocket.get()).isNotNull();
        assertThat(subscriptionTransport.webSocketListener.get()).isNotNull();
        subscriptionTransport.disconnect(new OperationClientMessage.Terminate());
        assertThat(subscriptionTransport.webSocket.get()).isNull();
        assertThat(subscriptionTransport.webSocketListener.get()).isNull();
    }

    @Test
    public void send() {
        try {
            subscriptionTransport.send(new OperationClientMessage.Init(Collections.<String, Object>emptyMap()));
            TestCase.fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
            // expected
        }
        subscriptionTransport.connect();
        subscriptionTransport.send(new OperationClientMessage.Init(Collections.<String, Object>emptyMap()));
        subscriptionTransport.disconnect(new OperationClientMessage.Terminate());
        try {
            subscriptionTransport.send(new OperationClientMessage.Init(Collections.<String, Object>emptyMap()));
            TestCase.fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
            // expected
        }
    }

    @Test
    public void subscriptionTransportCallback() {
        final AtomicBoolean callbackConnected = new AtomicBoolean();
        final AtomicReference<Throwable> callbackFailure = new AtomicReference<>();
        final AtomicReference<OperationServerMessage> callbackMessage = new AtomicReference<>();
        subscriptionTransport = new WebSocketSubscriptionTransport(webSocketRequest, webSocketFactory, new SubscriptionTransport.Callback() {
            @Override
            public void onConnected() {
                callbackConnected.set(true);
            }

            @Override
            public void onFailure(Throwable t) {
                callbackFailure.set(t);
            }

            @Override
            public void onMessage(OperationServerMessage message) {
                callbackMessage.set(message);
            }

            @Override
            public void onClosed() {
            }
        });
        subscriptionTransport.connect();
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"connection_ack\"}");
        webSocketFactory.webSocket.listener.onFailure(webSocketFactory.webSocket, new UnsupportedOperationException(), null);
        assertThat(callbackConnected.get()).isTrue();
        assertThat(callbackMessage.get()).isInstanceOf(ConnectionAcknowledge.class);
        assertThat(callbackFailure.get()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void subscriptionTransportClosedCallback() {
        final AtomicBoolean callbackConnected = new AtomicBoolean();
        final AtomicBoolean callbackClosed = new AtomicBoolean();
        subscriptionTransport = new WebSocketSubscriptionTransport(webSocketRequest, webSocketFactory, new SubscriptionTransport.Callback() {
            @Override
            public void onConnected() {
                callbackConnected.set(true);
            }

            @Override
            public void onFailure(Throwable t) {
                throw new UnsupportedOperationException("Unexpected");
            }

            @Override
            public void onMessage(OperationServerMessage message) {
            }

            @Override
            public void onClosed() {
                callbackClosed.set(true);
            }
        });
        subscriptionTransport.connect();
        webSocketFactory.webSocket.listener.onClosed(webSocketFactory.webSocket, 1001, "");
        assertThat(callbackConnected.get()).isTrue();
        assertThat(callbackClosed.get()).isTrue();
    }

    private static final class MockWebSocketFactory implements WebSocket.Factory {
        Request request;

        WebSocketSubscriptionTransportTest.MockWebSocket webSocket;

        @Override
        public WebSocket newWebSocket(@NotNull
        Request request, @NotNull
        WebSocketListener listener) {
            if ((webSocket) != null) {
                throw new IllegalStateException("already initialized");
            }
            this.request = request;
            return webSocket = new WebSocketSubscriptionTransportTest.MockWebSocket(request, listener);
        }
    }

    private static final class MockWebSocket implements WebSocket {
        final Request request;

        final WebSocketListener listener;

        String lastSentMessage;

        boolean closed;

        MockWebSocket(Request request, WebSocketListener listener) {
            this.request = request;
            this.listener = listener;
            this.listener.onOpen(this, new Builder().request(request).protocol(HTTP_1_0).code(200).message("Ok").build());
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public long queueSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean send(@NotNull
        String text) {
            lastSentMessage = text;
            return true;
        }

        @Override
        public boolean send(@NotNull
        ByteString bytes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean close(int code, @Nullable
        String reason) {
            return closed = true;
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }
    }
}

