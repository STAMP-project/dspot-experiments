/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.websockets.jsr.test;


import ClientEndpointConfig.Builder;
import DefaultWebSocketClientSslProvider.SSL_CONTEXT;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.websockets.jsr.ServerWebSocketContainer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Andrej Golovnin
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
@RunWith(DefaultServer.class)
@HttpOneOnly
public class BinaryEndpointTest {
    private static ServerWebSocketContainer deployment;

    private static byte[] bytes;

    @Test
    public void testBytesOnMessage() throws Exception {
        SSLContext context = DefaultServer.getClientSSLContext();
        BinaryEndpointTest.ProgramaticClientEndpoint endpoint = new BinaryEndpointTest.ProgramaticClientEndpoint();
        ClientEndpointConfig clientEndpointConfig = Builder.create().build();
        clientEndpointConfig.getUserProperties().put(SSL_CONTEXT, context);
        ContainerProvider.getWebSocketContainer().connectToServer(endpoint, clientEndpointConfig, new URI((((("wss://" + (DefaultServer.getHostAddress("default"))) + ":") + (DefaultServer.getHostSSLPort("default"))) + "/partial")));
        Assert.assertArrayEquals(BinaryEndpointTest.bytes, endpoint.getResponses().poll(15, TimeUnit.SECONDS));
        endpoint.session.close();
        endpoint.closeLatch.await(10, TimeUnit.SECONDS);
    }

    public static class ProgramaticClientEndpoint extends Endpoint {
        private final LinkedBlockingDeque<byte[]> responses = new LinkedBlockingDeque<>();

        final CountDownLatch closeLatch = new CountDownLatch(1);

        volatile Session session;

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            this.session = session;
            // Copy, because masking will modify this data
            byte[] mutableBytes = new byte[BinaryEndpointTest.bytes.length];
            System.arraycopy(BinaryEndpointTest.bytes, 0, mutableBytes, 0, BinaryEndpointTest.bytes.length);
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(mutableBytes));
            session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                @Override
                public void onMessage(byte[] message) {
                    responses.add(message);
                }
            });
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            closeLatch.countDown();
        }

        public LinkedBlockingDeque<byte[]> getResponses() {
            return responses;
        }
    }
}

