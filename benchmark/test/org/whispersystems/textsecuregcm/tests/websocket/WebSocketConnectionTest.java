package org.whispersystems.textsecuregcm.tests.websocket;


import Envelope.Type.CIPHERTEXT;
import PubSubProtos.PubSubMessage;
import PubSubProtos.PubSubMessage.Type.DELIVER;
import WebSocketSessionContext.WebSocketEventListener;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;
import io.dropwizard.auth.basic.BasicCredentials;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.whispersystems.textsecuregcm.auth.AccountAuthenticator;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntity;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntityList;
import org.whispersystems.textsecuregcm.push.ApnFallbackManager;
import org.whispersystems.textsecuregcm.push.PushSender;
import org.whispersystems.textsecuregcm.push.ReceiptSender;
import org.whispersystems.textsecuregcm.push.WebsocketSender;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.storage.MessagesManager;
import org.whispersystems.textsecuregcm.storage.PubSubManager;
import org.whispersystems.textsecuregcm.util.Base64;
import org.whispersystems.textsecuregcm.websocket.AuthenticatedConnectListener;
import org.whispersystems.textsecuregcm.websocket.WebSocketAccountAuthenticator;
import org.whispersystems.textsecuregcm.websocket.WebSocketConnection;
import org.whispersystems.textsecuregcm.websocket.WebsocketAddress;
import org.whispersystems.websocket.WebSocketClient;
import org.whispersystems.websocket.auth.WebSocketAuthenticator.AuthenticationResult;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;
import org.whispersystems.websocket.session.WebSocketSessionContext;


public class WebSocketConnectionTest {
    private static final String VALID_USER = "+14152222222";

    private static final String INVALID_USER = "+14151111111";

    private static final String VALID_PASSWORD = "secure";

    private static final String INVALID_PASSWORD = "insecure";

    private static final AccountAuthenticator accountAuthenticator = Mockito.mock(AccountAuthenticator.class);

    private static final AccountsManager accountsManager = Mockito.mock(AccountsManager.class);

    private static final PubSubManager pubSubManager = Mockito.mock(PubSubManager.class);

    private static final Account account = Mockito.mock(Account.class);

    private static final Device device = Mockito.mock(Device.class);

    private static final UpgradeRequest upgradeRequest = Mockito.mock(UpgradeRequest.class);

    private static final PushSender pushSender = Mockito.mock(PushSender.class);

    private static final ReceiptSender receiptSender = Mockito.mock(ReceiptSender.class);

    private static final ApnFallbackManager apnFallbackManager = Mockito.mock(ApnFallbackManager.class);

    @Test
    public void testCredentials() throws Exception {
        MessagesManager storedMessages = Mockito.mock(MessagesManager.class);
        WebSocketAccountAuthenticator webSocketAuthenticator = new WebSocketAccountAuthenticator(WebSocketConnectionTest.accountAuthenticator);
        AuthenticatedConnectListener connectListener = new AuthenticatedConnectListener(WebSocketConnectionTest.pushSender, WebSocketConnectionTest.receiptSender, storedMessages, WebSocketConnectionTest.pubSubManager, WebSocketConnectionTest.apnFallbackManager);
        WebSocketSessionContext sessionContext = Mockito.mock(WebSocketSessionContext.class);
        Mockito.when(WebSocketConnectionTest.accountAuthenticator.authenticate(ArgumentMatchers.eq(new BasicCredentials(WebSocketConnectionTest.VALID_USER, WebSocketConnectionTest.VALID_PASSWORD)))).thenReturn(Optional.of(WebSocketConnectionTest.account));
        Mockito.when(WebSocketConnectionTest.accountAuthenticator.authenticate(ArgumentMatchers.eq(new BasicCredentials(WebSocketConnectionTest.INVALID_USER, WebSocketConnectionTest.INVALID_PASSWORD)))).thenReturn(Optional.<Account>empty());
        Mockito.when(WebSocketConnectionTest.account.getAuthenticatedDevice()).thenReturn(Optional.of(WebSocketConnectionTest.device));
        Mockito.when(WebSocketConnectionTest.upgradeRequest.getParameterMap()).thenReturn(new HashMap<String, List<String>>() {
            {
                put("login", new LinkedList<String>() {
                    {
                        add(WebSocketConnectionTest.VALID_USER);
                    }
                });
                put("password", new LinkedList<String>() {
                    {
                        add(WebSocketConnectionTest.VALID_PASSWORD);
                    }
                });
            }
        });
        AuthenticationResult<Account> account = webSocketAuthenticator.authenticate(WebSocketConnectionTest.upgradeRequest);
        Mockito.when(sessionContext.getAuthenticated(Account.class)).thenReturn(account.getUser().orElse(null));
        connectListener.onWebSocketConnect(sessionContext);
        Mockito.verify(sessionContext).addListener(ArgumentMatchers.any(WebSocketEventListener.class));
        Mockito.when(WebSocketConnectionTest.upgradeRequest.getParameterMap()).thenReturn(new HashMap<String, List<String>>() {
            {
                put("login", new LinkedList<String>() {
                    {
                        add(WebSocketConnectionTest.INVALID_USER);
                    }
                });
                put("password", new LinkedList<String>() {
                    {
                        add(WebSocketConnectionTest.INVALID_PASSWORD);
                    }
                });
            }
        });
        account = webSocketAuthenticator.authenticate(WebSocketConnectionTest.upgradeRequest);
        Assert.assertFalse(account.getUser().isPresent());
        Assert.assertTrue(account.isRequired());
    }

    @Test
    public void testOpen() throws Exception {
        MessagesManager storedMessages = Mockito.mock(MessagesManager.class);
        List<OutgoingMessageEntity> outgoingMessages = new LinkedList<OutgoingMessageEntity>() {
            {
                add(createMessage(1L, false, "sender1", 1111, false, "first"));
                add(createMessage(2L, false, "sender1", 2222, false, "second"));
                add(createMessage(3L, false, "sender2", 3333, false, "third"));
            }
        };
        OutgoingMessageEntityList outgoingMessagesList = new OutgoingMessageEntityList(outgoingMessages, false);
        Mockito.when(WebSocketConnectionTest.device.getId()).thenReturn(2L);
        Mockito.when(WebSocketConnectionTest.device.getSignalingKey()).thenReturn(Base64.encodeBytes(new byte[52]));
        Mockito.when(WebSocketConnectionTest.account.getAuthenticatedDevice()).thenReturn(Optional.of(WebSocketConnectionTest.device));
        Mockito.when(WebSocketConnectionTest.account.getNumber()).thenReturn("+14152222222");
        final Device sender1device = Mockito.mock(Device.class);
        Set<Device> sender1devices = new HashSet<Device>() {
            {
                add(sender1device);
            }
        };
        Account sender1 = Mockito.mock(Account.class);
        Mockito.when(sender1.getDevices()).thenReturn(sender1devices);
        Mockito.when(WebSocketConnectionTest.accountsManager.get("sender1")).thenReturn(Optional.of(sender1));
        Mockito.when(WebSocketConnectionTest.accountsManager.get("sender2")).thenReturn(Optional.empty());
        Mockito.when(storedMessages.getMessagesForDevice(WebSocketConnectionTest.account.getNumber(), WebSocketConnectionTest.device.getId())).thenReturn(outgoingMessagesList);
        final List<SettableFuture<WebSocketResponseMessage>> futures = new LinkedList<>();
        final WebSocketClient client = Mockito.mock(WebSocketClient.class);
        Mockito.when(client.sendRequest(ArgumentMatchers.eq("PUT"), ArgumentMatchers.eq("/api/v1/message"), ArgumentMatchers.nullable(List.class), ArgumentMatchers.<Optional<byte[]>>any())).thenAnswer(new Answer<SettableFuture<WebSocketResponseMessage>>() {
            @Override
            public SettableFuture<WebSocketResponseMessage> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SettableFuture<WebSocketResponseMessage> future = SettableFuture.create();
                futures.add(future);
                return future;
            }
        });
        WebsocketAddress websocketAddress = new WebsocketAddress(WebSocketConnectionTest.account.getNumber(), WebSocketConnectionTest.device.getId());
        WebSocketConnection connection = new WebSocketConnection(WebSocketConnectionTest.pushSender, WebSocketConnectionTest.receiptSender, storedMessages, WebSocketConnectionTest.account, WebSocketConnectionTest.device, client, "someid");
        connection.onDispatchSubscribed(websocketAddress.serialize());
        Mockito.verify(client, Mockito.times(3)).sendRequest(ArgumentMatchers.eq("PUT"), ArgumentMatchers.eq("/api/v1/message"), ArgumentMatchers.nullable(List.class), ArgumentMatchers.<Optional<byte[]>>any());
        Assert.assertTrue(((futures.size()) == 3));
        WebSocketResponseMessage response = Mockito.mock(WebSocketResponseMessage.class);
        Mockito.when(response.getStatus()).thenReturn(200);
        futures.get(1).set(response);
        futures.get(0).setException(new IOException());
        futures.get(2).setException(new IOException());
        Mockito.verify(storedMessages, Mockito.times(1)).delete(ArgumentMatchers.eq(WebSocketConnectionTest.account.getNumber()), ArgumentMatchers.eq(2L), ArgumentMatchers.eq(2L), ArgumentMatchers.eq(false));
        Mockito.verify(WebSocketConnectionTest.receiptSender, Mockito.times(1)).sendReceipt(ArgumentMatchers.eq(WebSocketConnectionTest.account), ArgumentMatchers.eq("sender1"), ArgumentMatchers.eq(2222L));
        connection.onDispatchUnsubscribed(websocketAddress.serialize());
        Mockito.verify(client).close(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    }

    @Test
    public void testOnlineSend() throws Exception {
        MessagesManager storedMessages = Mockito.mock(MessagesManager.class);
        WebsocketSender websocketSender = Mockito.mock(WebsocketSender.class);
        Mockito.when(WebSocketConnectionTest.pushSender.getWebSocketSender()).thenReturn(websocketSender);
        Envelope firstMessage = Envelope.newBuilder().setLegacyMessage(ByteString.copyFrom("first".getBytes())).setSource("sender1").setTimestamp(System.currentTimeMillis()).setSourceDevice(1).setType(CIPHERTEXT).build();
        Envelope secondMessage = Envelope.newBuilder().setLegacyMessage(ByteString.copyFrom("second".getBytes())).setSource("sender2").setTimestamp(System.currentTimeMillis()).setSourceDevice(2).setType(CIPHERTEXT).build();
        List<OutgoingMessageEntity> pendingMessages = new LinkedList<>();
        OutgoingMessageEntityList pendingMessagesList = new OutgoingMessageEntityList(pendingMessages, false);
        Mockito.when(WebSocketConnectionTest.device.getId()).thenReturn(2L);
        Mockito.when(WebSocketConnectionTest.device.getSignalingKey()).thenReturn(Base64.encodeBytes(new byte[52]));
        Mockito.when(WebSocketConnectionTest.account.getAuthenticatedDevice()).thenReturn(Optional.of(WebSocketConnectionTest.device));
        Mockito.when(WebSocketConnectionTest.account.getNumber()).thenReturn("+14152222222");
        final Device sender1device = Mockito.mock(Device.class);
        Set<Device> sender1devices = new HashSet<Device>() {
            {
                add(sender1device);
            }
        };
        Account sender1 = Mockito.mock(Account.class);
        Mockito.when(sender1.getDevices()).thenReturn(sender1devices);
        Mockito.when(WebSocketConnectionTest.accountsManager.get("sender1")).thenReturn(Optional.of(sender1));
        Mockito.when(WebSocketConnectionTest.accountsManager.get("sender2")).thenReturn(Optional.<Account>empty());
        Mockito.when(storedMessages.getMessagesForDevice(WebSocketConnectionTest.account.getNumber(), WebSocketConnectionTest.device.getId())).thenReturn(pendingMessagesList);
        final List<SettableFuture<WebSocketResponseMessage>> futures = new LinkedList<>();
        final WebSocketClient client = Mockito.mock(WebSocketClient.class);
        Mockito.when(client.sendRequest(ArgumentMatchers.eq("PUT"), ArgumentMatchers.eq("/api/v1/message"), ArgumentMatchers.nullable(List.class), ArgumentMatchers.<Optional<byte[]>>any())).thenAnswer(new Answer<SettableFuture<WebSocketResponseMessage>>() {
            @Override
            public SettableFuture<WebSocketResponseMessage> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SettableFuture<WebSocketResponseMessage> future = SettableFuture.create();
                futures.add(future);
                return future;
            }
        });
        WebsocketAddress websocketAddress = new WebsocketAddress(WebSocketConnectionTest.account.getNumber(), WebSocketConnectionTest.device.getId());
        WebSocketConnection connection = new WebSocketConnection(WebSocketConnectionTest.pushSender, WebSocketConnectionTest.receiptSender, storedMessages, WebSocketConnectionTest.account, WebSocketConnectionTest.device, client, "anotherid");
        connection.onDispatchSubscribed(websocketAddress.serialize());
        connection.onDispatchMessage(websocketAddress.serialize(), PubSubMessage.newBuilder().setType(DELIVER).setContent(ByteString.copyFrom(firstMessage.toByteArray())).build().toByteArray());
        connection.onDispatchMessage(websocketAddress.serialize(), PubSubMessage.newBuilder().setType(DELIVER).setContent(ByteString.copyFrom(secondMessage.toByteArray())).build().toByteArray());
        Mockito.verify(client, Mockito.times(2)).sendRequest(ArgumentMatchers.eq("PUT"), ArgumentMatchers.eq("/api/v1/message"), ArgumentMatchers.nullable(List.class), ArgumentMatchers.<Optional<byte[]>>any());
        Assert.assertEquals(futures.size(), 2);
        WebSocketResponseMessage response = Mockito.mock(WebSocketResponseMessage.class);
        Mockito.when(response.getStatus()).thenReturn(200);
        futures.get(1).set(response);
        futures.get(0).setException(new IOException());
        Mockito.verify(WebSocketConnectionTest.receiptSender, Mockito.times(1)).sendReceipt(ArgumentMatchers.eq(WebSocketConnectionTest.account), ArgumentMatchers.eq("sender2"), ArgumentMatchers.eq(secondMessage.getTimestamp()));
        Mockito.verify(websocketSender, Mockito.times(1)).queueMessage(ArgumentMatchers.eq(WebSocketConnectionTest.account), ArgumentMatchers.eq(WebSocketConnectionTest.device), ArgumentMatchers.any(Envelope.class));
        Mockito.verify(WebSocketConnectionTest.pushSender, Mockito.times(1)).sendQueuedNotification(ArgumentMatchers.eq(WebSocketConnectionTest.account), ArgumentMatchers.eq(WebSocketConnectionTest.device));
        connection.onDispatchUnsubscribed(websocketAddress.serialize());
        Mockito.verify(client).close(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    }

    @Test
    public void testPendingSend() throws Exception {
        MessagesManager storedMessages = Mockito.mock(MessagesManager.class);
        WebsocketSender websocketSender = Mockito.mock(WebsocketSender.class);
        Mockito.reset(websocketSender);
        Mockito.reset(WebSocketConnectionTest.pushSender);
        Mockito.when(WebSocketConnectionTest.pushSender.getWebSocketSender()).thenReturn(websocketSender);
        final Envelope firstMessage = Envelope.newBuilder().setLegacyMessage(ByteString.copyFrom("first".getBytes())).setSource("sender1").setTimestamp(System.currentTimeMillis()).setSourceDevice(1).setType(CIPHERTEXT).build();
        final Envelope secondMessage = Envelope.newBuilder().setLegacyMessage(ByteString.copyFrom("second".getBytes())).setSource("sender2").setTimestamp(System.currentTimeMillis()).setSourceDevice(2).setType(CIPHERTEXT).build();
        List<OutgoingMessageEntity> pendingMessages = new LinkedList<OutgoingMessageEntity>() {
            {
                add(new OutgoingMessageEntity(1, true, UUID.randomUUID(), firstMessage.getType().getNumber(), firstMessage.getRelay(), firstMessage.getTimestamp(), firstMessage.getSource(), firstMessage.getSourceDevice(), firstMessage.getLegacyMessage().toByteArray(), firstMessage.getContent().toByteArray(), 0));
                add(new OutgoingMessageEntity(2, false, UUID.randomUUID(), secondMessage.getType().getNumber(), secondMessage.getRelay(), secondMessage.getTimestamp(), secondMessage.getSource(), secondMessage.getSourceDevice(), secondMessage.getLegacyMessage().toByteArray(), secondMessage.getContent().toByteArray(), 0));
            }
        };
        OutgoingMessageEntityList pendingMessagesList = new OutgoingMessageEntityList(pendingMessages, false);
        Mockito.when(WebSocketConnectionTest.device.getId()).thenReturn(2L);
        Mockito.when(WebSocketConnectionTest.device.getSignalingKey()).thenReturn(Base64.encodeBytes(new byte[52]));
        Mockito.when(WebSocketConnectionTest.account.getAuthenticatedDevice()).thenReturn(Optional.of(WebSocketConnectionTest.device));
        Mockito.when(WebSocketConnectionTest.account.getNumber()).thenReturn("+14152222222");
        final Device sender1device = Mockito.mock(Device.class);
        Set<Device> sender1devices = new HashSet<Device>() {
            {
                add(sender1device);
            }
        };
        Account sender1 = Mockito.mock(Account.class);
        Mockito.when(sender1.getDevices()).thenReturn(sender1devices);
        Mockito.when(WebSocketConnectionTest.accountsManager.get("sender1")).thenReturn(Optional.of(sender1));
        Mockito.when(WebSocketConnectionTest.accountsManager.get("sender2")).thenReturn(Optional.<Account>empty());
        Mockito.when(storedMessages.getMessagesForDevice(WebSocketConnectionTest.account.getNumber(), WebSocketConnectionTest.device.getId())).thenReturn(pendingMessagesList);
        final List<SettableFuture<WebSocketResponseMessage>> futures = new LinkedList<>();
        final WebSocketClient client = Mockito.mock(WebSocketClient.class);
        Mockito.when(client.sendRequest(ArgumentMatchers.eq("PUT"), ArgumentMatchers.eq("/api/v1/message"), ArgumentMatchers.nullable(List.class), ArgumentMatchers.<Optional<byte[]>>any())).thenAnswer(new Answer<SettableFuture<WebSocketResponseMessage>>() {
            @Override
            public SettableFuture<WebSocketResponseMessage> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SettableFuture<WebSocketResponseMessage> future = SettableFuture.create();
                futures.add(future);
                return future;
            }
        });
        WebsocketAddress websocketAddress = new WebsocketAddress(WebSocketConnectionTest.account.getNumber(), WebSocketConnectionTest.device.getId());
        WebSocketConnection connection = new WebSocketConnection(WebSocketConnectionTest.pushSender, WebSocketConnectionTest.receiptSender, storedMessages, WebSocketConnectionTest.account, WebSocketConnectionTest.device, client, "onemoreid");
        connection.onDispatchSubscribed(websocketAddress.serialize());
        Mockito.verify(client, Mockito.times(2)).sendRequest(ArgumentMatchers.eq("PUT"), ArgumentMatchers.eq("/api/v1/message"), ArgumentMatchers.nullable(List.class), ArgumentMatchers.<Optional<byte[]>>any());
        Assert.assertEquals(futures.size(), 2);
        WebSocketResponseMessage response = Mockito.mock(WebSocketResponseMessage.class);
        Mockito.when(response.getStatus()).thenReturn(200);
        futures.get(1).set(response);
        futures.get(0).setException(new IOException());
        Mockito.verify(WebSocketConnectionTest.receiptSender, Mockito.times(1)).sendReceipt(ArgumentMatchers.eq(WebSocketConnectionTest.account), ArgumentMatchers.eq("sender2"), ArgumentMatchers.eq(secondMessage.getTimestamp()));
        Mockito.verifyNoMoreInteractions(websocketSender);
        Mockito.verifyNoMoreInteractions(WebSocketConnectionTest.pushSender);
        connection.onDispatchUnsubscribed(websocketAddress.serialize());
        Mockito.verify(client).close(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    }
}

