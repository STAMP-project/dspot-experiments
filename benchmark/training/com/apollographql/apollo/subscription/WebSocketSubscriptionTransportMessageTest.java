package com.apollographql.apollo.subscription;


import OperationServerMessage.Complete;
import OperationServerMessage.ConnectionAcknowledge;
import OperationServerMessage.ConnectionError;
import OperationServerMessage.Data;
import OperationServerMessage.Error;
import OperationServerMessage.Unsupported;
import Protocol.HTTP_1_0;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ScalarType;
import com.apollographql.apollo.api.Subscription;
import com.apollographql.apollo.response.CustomTypeAdapter;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import okhttp3.Request;
import okhttp3.Response.Builder;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


public class WebSocketSubscriptionTransportMessageTest {
    private WebSocketSubscriptionTransportMessageTest.MockWebSocketFactory webSocketFactory;

    private WebSocketSubscriptionTransport subscriptionTransport;

    private WebSocketSubscriptionTransportMessageTest.MockSubscriptionTransportCallback transportCallback;

    @Test
    public void connectionInit() {
        subscriptionTransport.send(new OperationClientMessage.Init(Collections.<String, Object>emptyMap()));
        assertThat(webSocketFactory.webSocket.lastSentMessage).isEqualTo("{\"type\":\"connection_init\"}");
        subscriptionTransport.send(new OperationClientMessage.Init(new com.apollographql.apollo.api.internal.UnmodifiableMapBuilder<String, Object>().put("param1", true).put("param2", "value").build()));
        assertThat(webSocketFactory.webSocket.lastSentMessage).isEqualTo("{\"type\":\"connection_init\",\"payload\":{\"param1\":true,\"param2\":\"value\"}}");
    }

    @Test
    public void startSubscription() {
        subscriptionTransport.send(new OperationClientMessage.Start("subscriptionId", new WebSocketSubscriptionTransportMessageTest.MockSubscription(), new com.apollographql.apollo.response.ScalarTypeAdapters(Collections.<ScalarType, CustomTypeAdapter>emptyMap())));
        assertThat(webSocketFactory.webSocket.lastSentMessage).isEqualTo("{\"id\":\"subscriptionId\",\"type\":\"start\",\"payload\":{\"query\":\"subscription{commentAdded{id  name}\",\"variables\":{},\"operationName\":\"SomeSubscription\"}}");
    }

    @Test
    public void stopSubscription() {
        subscriptionTransport.send(new OperationClientMessage.Stop("subscriptionId"));
        assertThat(webSocketFactory.webSocket.lastSentMessage).isEqualTo("{\"id\":\"subscriptionId\",\"type\":\"stop\"}");
    }

    @Test
    public void terminateSubscription() {
        subscriptionTransport.send(new OperationClientMessage.Terminate());
        assertThat(webSocketFactory.webSocket.lastSentMessage).isEqualTo("{\"type\":\"connection_terminate\"}");
    }

    @Test
    public void connectionAcknowledge() {
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"connection_ack\"}");
        assertThat(transportCallback.lastMessage).isInstanceOf(ConnectionAcknowledge.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void data() {
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"data\",\"id\":\"subscriptionId\",\"payload\":{\"data\":{\"commentAdded\":{\"__typename\":\"Comment\",\"id\":10,\"content\":\"test10\"}}}}");
        assertThat(transportCallback.lastMessage).isInstanceOf(Data.class);
        assertThat(((OperationServerMessage.Data) (transportCallback.lastMessage)).id).isEqualTo("subscriptionId");
        assertThat(((Map<String, Object>) (((Map<String, Object>) (((OperationServerMessage.Data) (transportCallback.lastMessage)).payload.get("data"))).get("commentAdded")))).containsExactlyEntriesIn(new com.apollographql.apollo.api.internal.UnmodifiableMapBuilder<String, Object>().put("__typename", "Comment").put("id", BigDecimal.valueOf(10)).put("content", "test10").build());
    }

    @Test
    public void connectionError() {
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"connection_error\",\"payload\":{\"message\":\"Connection Error\"}}");
        assertThat(transportCallback.lastMessage).isInstanceOf(ConnectionError.class);
        assertThat(((OperationServerMessage.ConnectionError) (transportCallback.lastMessage)).payload).containsExactly("message", "Connection Error");
    }

    @Test
    public void error() {
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"error\", \"id\":\"subscriptionId\", \"payload\":{\"message\":\"Error\"}}");
        assertThat(transportCallback.lastMessage).isInstanceOf(Error.class);
        assertThat(((OperationServerMessage.Error) (transportCallback.lastMessage)).id).isEqualTo("subscriptionId");
        assertThat(((OperationServerMessage.Error) (transportCallback.lastMessage)).payload).containsExactly("message", "Error");
    }

    @Test
    public void complete() {
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"complete\", \"id\":\"subscriptionId\"}");
        assertThat(transportCallback.lastMessage).isInstanceOf(Complete.class);
        assertThat(((OperationServerMessage.Complete) (transportCallback.lastMessage)).id).isEqualTo("subscriptionId");
    }

    @Test
    public void unsupported() {
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"unsupported\"}");
        assertThat(transportCallback.lastMessage).isInstanceOf(Unsupported.class);
        assertThat(((OperationServerMessage.Unsupported) (transportCallback.lastMessage)).rawMessage).isEqualTo("{\"type\":\"unsupported\"}");
        webSocketFactory.webSocket.listener.onMessage(webSocketFactory.webSocket, "{\"type\":\"unsupported");
        assertThat(transportCallback.lastMessage).isInstanceOf(Unsupported.class);
        assertThat(((OperationServerMessage.Unsupported) (transportCallback.lastMessage)).rawMessage).isEqualTo("{\"type\":\"unsupported");
    }

    private static final class MockWebSocketFactory implements WebSocket.Factory {
        WebSocketSubscriptionTransportMessageTest.MockWebSocket webSocket;

        @Override
        public WebSocket newWebSocket(@NotNull
        Request request, @NotNull
        WebSocketListener listener) {
            if ((webSocket) != null) {
                throw new IllegalStateException("already initialized");
            }
            return webSocket = new WebSocketSubscriptionTransportMessageTest.MockWebSocket(request, listener);
        }
    }

    private static final class MockWebSocket implements WebSocket {
        final Request request;

        final WebSocketListener listener;

        String lastSentMessage;

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
            throw new UnsupportedOperationException();
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class MockSubscriptionTransportCallback implements SubscriptionTransport.Callback {
        OperationServerMessage lastMessage;

        @Override
        public void onConnected() {
        }

        @Override
        public void onFailure(Throwable t) {
        }

        @Override
        public void onMessage(OperationServerMessage message) {
            lastMessage = message;
        }

        @Override
        public void onClosed() {
        }
    }

    private static final class MockSubscription implements Subscription<Operation.Data, Operation.Data, Operation.Variables> {
        @Override
        public String queryDocument() {
            return "subscription{commentAdded{id\n  name\n}";
        }

        @Override
        public Variables variables() {
            return EMPTY_VARIABLES;
        }

        @Override
        public ResponseFieldMapper<Operation.Data> responseFieldMapper() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Operation.Data wrapData(Data data) {
            return data;
        }

        @NotNull
        @Override
        public OperationName name() {
            return new OperationName() {
                @Override
                public String name() {
                    return "SomeSubscription";
                }
            };
        }

        @NotNull
        @Override
        public String operationId() {
            return "someId";
        }
    }
}

