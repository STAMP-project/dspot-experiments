package org.web3j.protocol.core;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketListener;
import org.web3j.protocol.websocket.WebSocketService;


public class WebSocketEventTest {
    private WebSocketClient webSocketClient = Mockito.mock(WebSocketClient.class);

    private WebSocketService webSocketService = new WebSocketService(webSocketClient, true);

    private Web3j web3j = Web3j.build(webSocketService);

    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private WebSocketListener listener;

    @Test
    public void testNewHeadsNotifications() {
        web3j.newHeadsNotifications();
        Mockito.verify(webSocketClient).send(ArgumentMatchers.matches(("\\{\"jsonrpc\":\"2.0\",\"method\":\"eth_subscribe\"," + "\"params\":\\[\"newHeads\"],\"id\":[0-9]{1,}}")));
    }

    @Test
    public void testLogsNotificationsWithoutArguments() {
        web3j.logsNotifications(new ArrayList(), new ArrayList());
        Mockito.verify(webSocketClient).send(ArgumentMatchers.matches(("\\{\"jsonrpc\":\"2.0\",\"method\":\"eth_subscribe\"," + "\"params\":\\[\"logs\",\\{}],\"id\":[0-9]{1,}}")));
    }

    @Test
    public void testLogsNotificationsWithArguments() {
        web3j.logsNotifications(Collections.singletonList("0x1"), Collections.singletonList("0x2"));
        Mockito.verify(webSocketClient).send(ArgumentMatchers.matches(("\\{\"jsonrpc\":\"2.0\",\"method\":\"eth_subscribe\"," + ("\"params\":\\[\"logs\",\\{\"address\":\\[\"0x1\"]," + "\"topics\":\\[\"0x2\"]}],\"id\":[0-9]{1,}}"))));
    }
}

