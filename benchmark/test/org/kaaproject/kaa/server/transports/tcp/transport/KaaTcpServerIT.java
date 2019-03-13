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


import java.net.Socket;
import java.util.Properties;
import java.util.UUID;
import org.junit.Test;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.Connect;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.Disconnect;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.Disconnect.DisconnectReason;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.PingRequest;
import org.kaaproject.kaa.server.transport.GenericTransportContext;
import org.kaaproject.kaa.server.transport.TransportProperties;
import org.kaaproject.kaa.server.transport.channel.ChannelContext;
import org.kaaproject.kaa.server.transport.channel.ChannelType;
import org.kaaproject.kaa.server.transport.message.MessageHandler;
import org.kaaproject.kaa.server.transport.message.SessionInitMessage;
import org.kaaproject.kaa.server.transport.session.SessionAware;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;


public class KaaTcpServerIT {
    private static final int TIMEOUT = 10000;

    private static final int TEST_PORT = 9997;

    private static final String TEST_HOST = "localhost";

    private static final Logger LOG = LoggerFactory.getLogger(KaaTcpServerIT.class);

    @Test
    public void basicKaaTcpServerTest() throws Exception {
        TcpTransport transport = new TcpTransport();
        try {
            KaaTcpServerIT.LOG.debug("Initializing TCP server");
            final MessageHandler handler = Mockito.mock(MessageHandler.class);
            GenericTransportContext context = new GenericTransportContext(new org.kaaproject.kaa.server.transport.TransportContext(new TransportProperties(new Properties()), null, new MessageHandler() {
                @Override
                public void process(SessionInitMessage message) {
                    message.onSessionCreated(new org.kaaproject.kaa.server.transport.session.SessionInfo(UUID.randomUUID(), 1, Mockito.mock(ChannelContext.class), ChannelType.ASYNC, null, null, null, null, 100, false));
                    handler.process(message);
                }

                @Override
                public void process(SessionAware message) {
                    handler.process(message);
                }
            }), getTestConfig());
            transport.init(context);
            KaaTcpServerIT.LOG.debug("Starting TCP server");
            transport.start();
            final Socket socket = new Socket(KaaTcpServerIT.TEST_HOST, KaaTcpServerIT.TEST_PORT);
            Connect connect = new Connect(1, 1, new byte[0], new byte[0], new byte[0]);
            socket.getOutputStream().write(connect.getFrame().array());
            Mockito.verify(handler, Mockito.timeout(KaaTcpServerIT.TIMEOUT)).process(any(SessionInitMessage.class));
            PingRequest ping = new PingRequest();
            socket.getOutputStream().write(ping.getFrame().array());
            Mockito.verify(handler, Mockito.timeout(KaaTcpServerIT.TIMEOUT)).process(Mockito.any(org.kaaproject.kaa.server.transport.message.SessionPingMessage.class));
            Disconnect disconnect = new Disconnect(DisconnectReason.NONE);
            socket.getOutputStream().write(disconnect.getFrame().array());
            Mockito.verify(handler, Mockito.timeout(KaaTcpServerIT.TIMEOUT)).process(Mockito.any(org.kaaproject.kaa.server.transport.message.SessionDisconnectMessage.class));
            socket.close();
        } finally {
            KaaTcpServerIT.LOG.debug("Shutdown TCP server");
            transport.stop();
        }
    }
}

