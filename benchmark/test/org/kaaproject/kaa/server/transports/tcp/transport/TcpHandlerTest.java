/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.transports.tcp.transport;


import DisconnectReason.BAD_REQUEST;
import DisconnectReason.INTERNAL_ERROR;
import ReturnCode.REFUSE_BAD_CREDENTIALS;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.ConnAck;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.ConnAck.ReturnCode;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.Connect;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.Disconnect;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.KaaSync;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.PingRequest;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.PingResponse;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.SyncRequest;
import org.kaaproject.kaa.server.transport.message.MessageHandler;
import org.kaaproject.kaa.server.transport.message.SessionAwareMessage;
import org.kaaproject.kaa.server.transport.message.SessionInitMessage;
import org.kaaproject.kaa.server.transport.session.SessionAware;
import org.kaaproject.kaa.server.transports.tcp.transport.netty.AbstractKaaTcpCommandProcessor;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


public class TcpHandlerTest {
    MessageHandler messageHandler = new MessageHandler() {
        @Override
        public void process(SessionInitMessage message) {
            Object[] response = message.getMessageBuilder().build("response".getBytes(), false);
            Assert.assertEquals(2, response.length);
            Assert.assertTrue(((response[0]) instanceof ConnAck));
            Assert.assertTrue(((response[1]) instanceof KaaSync));
            response = message.getErrorBuilder().build(Mockito.mock(GeneralSecurityException.class));
            Assert.assertTrue(((response[0]) instanceof ConnAck));
            ConnAck connAck = ((ConnAck) (response[0]));
            Assert.assertEquals(REFUSE_BAD_CREDENTIALS, connAck.getReturnCode());
        }

        @Override
        public void process(SessionAware message) {
            if (message instanceof SessionAwareMessage) {
                SessionAwareMessage request = ((SessionAwareMessage) (message));
                Object[] response;
                response = request.getErrorBuilder().build(Mockito.mock(GeneralSecurityException.class));
                Assert.assertTrue(((response[0]) instanceof Disconnect));
                Disconnect disconnect = ((Disconnect) (response[0]));
                Assert.assertEquals(BAD_REQUEST, disconnect.getReason());
                response = request.getErrorBuilder().build(Mockito.mock(RuntimeException.class));
                Assert.assertTrue(((response[0]) instanceof Disconnect));
                disconnect = ((Disconnect) (response[0]));
                Assert.assertEquals(INTERNAL_ERROR, disconnect.getReason());
            }
        }
    };

    @Test
    public void testConnect() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageHandler akkaService = Mockito.mock(MessageHandler.class);
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new Connect());
        TcpHandler handler = new TcpHandler(uuid, akkaService);
        handler.channelRead0(buildDummyCtxMock(), msg);
        Mockito.verify(akkaService).process(any(org.kaaproject.kaa.server.transports.tcp.transport.messages.NettyTcpConnectMessage.class));
    }

    @Test
    public void testDuplicateConnect() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageHandler akkaService = Mockito.mock(MessageHandler.class);
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new Connect());
        TcpHandler handler = new TcpHandler(uuid, akkaService);
        handler.onSessionCreated(buildSessionInfo(uuid));
        handler.channelRead0(buildDummyCtxMock(), msg);
        Mockito.verify(akkaService, Mockito.never()).process(any(org.kaaproject.kaa.server.transports.tcp.transport.messages.NettyTcpConnectMessage.class));
    }

    @Test
    public void testKaaSyncWithoutSession() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageHandler akkaService = Mockito.mock(MessageHandler.class);
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new KaaSync());
        TcpHandler handler = new TcpHandler(uuid, akkaService);
        ChannelHandlerContext context = buildDummyCtxMock();
        handler.channelRead0(context, msg);
        ArgumentCaptor<ConnAck> captor = ArgumentCaptor.forClass(ConnAck.class);
        Mockito.verify(context).writeAndFlush(captor.capture());
        Assert.assertTrue(((captor.getValue().getReturnCode()) == (ReturnCode.REFUSE_BAD_PROTOCOL)));
        Mockito.verify(akkaService, Mockito.never()).process(any(org.kaaproject.kaa.server.transports.tcp.transport.messages.NettyTcpConnectMessage.class));
    }

    @Test
    public void testKaaSync() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageHandler akkaService = Mockito.mock(MessageHandler.class);
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new SyncRequest());
        TcpHandler handler = new TcpHandler(uuid, akkaService);
        handler.onSessionCreated(buildSessionInfo(uuid));
        handler.channelRead0(null, msg);
        Mockito.verify(akkaService).process(Mockito.any(org.kaaproject.kaa.server.transports.tcp.transport.messages.NettyTcpSyncMessage.class));
    }

    @Test
    public void testKaaSyncHandlers() throws Exception {
        UUID uuid = UUID.randomUUID();
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new SyncRequest());
        TcpHandler handler = new TcpHandler(uuid, messageHandler);
        handler.onSessionCreated(buildSessionInfo(uuid));
        handler.channelRead0(null, msg);
    }

    @Test
    public void testPing() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageHandler akkaService = Mockito.mock(MessageHandler.class);
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new PingRequest());
        TcpHandler handler = new TcpHandler(uuid, akkaService);
        handler.onSessionCreated(buildSessionInfo(uuid));
        handler.channelRead0(null, msg);
        Mockito.verify(akkaService).process(Mockito.any(org.kaaproject.kaa.server.transports.tcp.transport.messages.NettyTcpSyncMessage.class));
    }

    @Test
    public void testDisconnect() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageHandler akkaService = Mockito.mock(MessageHandler.class);
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new PingRequest());
        TcpHandler handler = new TcpHandler(uuid, akkaService);
        handler.onSessionCreated(buildSessionInfo(uuid));
        handler.channelRead0(null, msg);
        Mockito.verify(akkaService).process(Mockito.any(org.kaaproject.kaa.server.transports.tcp.transport.messages.NettyTcpDisconnectMessage.class));
    }

    @Test
    public void testUnsupportedMessage() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageHandler akkaService = Mockito.mock(MessageHandler.class);
        AbstractKaaTcpCommandProcessor msg = Mockito.mock(AbstractKaaTcpCommandProcessor.class);
        Mockito.when(msg.getRequest()).thenReturn(new PingResponse());
        TcpHandler handler = new TcpHandler(uuid, akkaService);
        handler.onSessionCreated(buildSessionInfo(uuid));
        handler.channelRead0(null, msg);
        Mockito.verify(akkaService, Mockito.never()).process(Mockito.any(SessionAware.class));
        Mockito.verify(akkaService, Mockito.never()).process(Mockito.any(SessionInitMessage.class));
    }
}

