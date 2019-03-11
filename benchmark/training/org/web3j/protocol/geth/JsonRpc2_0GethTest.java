package org.web3j.protocol.geth;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketListener;
import org.web3j.protocol.websocket.WebSocketService;


public class JsonRpc2_0GethTest {
    private WebSocketClient webSocketClient = Mockito.mock(WebSocketClient.class);

    private WebSocketService webSocketService = new WebSocketService(webSocketClient, true);

    private Geth geth = Geth.build(webSocketService);

    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private WebSocketListener listener;

    @Test
    public void testPendingTransactionsNotifications() {
        geth.newPendingTransactionsNotifications();
        Mockito.verify(webSocketClient).send(ArgumentMatchers.matches(("\\{\"jsonrpc\":\"2.0\",\"method\":\"eth_subscribe\",\"params\":" + "\\[\"newPendingTransactions\"],\"id\":[0-9]{1,}}")));
    }

    @Test
    public void testSyncingStatusNotifications() {
        geth.syncingStatusNotifications();
        Mockito.verify(webSocketClient).send(ArgumentMatchers.matches(("\\{\"jsonrpc\":\"2.0\",\"method\":\"eth_subscribe\"," + "\"params\":\\[\"syncing\"],\"id\":[0-9]{1,}}")));
    }
}

