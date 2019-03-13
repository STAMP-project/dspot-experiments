package org.web3j.protocol.websocket;


import java.io.IOException;
import org.junit.Test;
import org.mockito.Mockito;


public class WebSocketClientTest {
    private WebSocketListener listener = Mockito.mock(WebSocketListener.class);

    private WebSocketClient client;

    @Test
    public void testNotifyListenerOnMessage() throws Exception {
        client.onMessage("message");
        Mockito.verify(listener).onMessage("message");
    }

    @Test
    public void testNotifyListenerOnError() throws Exception {
        IOException e = new IOException("123");
        client.onError(e);
        Mockito.verify(listener).onError(e);
    }

    @Test
    public void testErrorBeforeListenerSet() throws Exception {
        final IOException e = new IOException("123");
        client.setListener(null);
        client.onError(e);
        Mockito.verify(listener, Mockito.never()).onError(e);
    }

    @Test
    public void testNotifyListenerOnClose() throws Exception {
        client.onClose(1, "reason", true);
        Mockito.verify(listener).onClose();
    }
}

