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
import RemoteEndpoint.Basic;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.FutureResult;


@RunWith(DefaultServer.class)
@HttpOneOnly
public class TestMessagesReceivedInOrder {
    private static int MESSAGES = 1000;

    private static final List<Throwable> stacks = new CopyOnWriteArrayList<>();

    @Test
    public void testMessagesReceivedInOrder() throws Exception {
        TestMessagesReceivedInOrder.stacks.clear();
        TestMessagesReceivedInOrder.EchoSocket.receivedEchos = new FutureResult();
        final ClientEndpointConfig clientEndpointConfig = Builder.create().build();
        final CountDownLatch done = new CountDownLatch(1);
        final AtomicReference<String> error = new AtomicReference<>();
        ContainerProvider.getWebSocketContainer().connectToServer(new Endpoint() {
            @Override
            public void onOpen(final Session session, EndpointConfig endpointConfig) {
                try {
                    RemoteEndpoint.Basic rem = session.getBasicRemote();
                    List<String> messages = new ArrayList<>();
                    for (int i = 0; i < (TestMessagesReceivedInOrder.MESSAGES); i++) {
                        byte[] data = new byte[2048];
                        new Random().nextBytes(data);
                        String crc = TestMessagesReceivedInOrder.md5(data);
                        rem.sendBinary(ByteBuffer.wrap(data));
                        messages.add(crc);
                    }
                    List<String> received = TestMessagesReceivedInOrder.EchoSocket.receivedEchos.getIoFuture().get();
                    StringBuilder sb = new StringBuilder();
                    boolean fail = false;
                    for (int i = 0; i < (messages.size()); i++) {
                        if ((received.size()) <= i) {
                            fail = true;
                            sb.append((((i + ": should be ") + (messages.get(i))) + " but is empty."));
                        } else {
                            if (!(messages.get(i).equals(received.get(i)))) {
                                fail = true;
                                sb.append((((((((i + ": should be ") + (messages.get(i))) + " but is ") + (received.get(i))) + " (but found at ") + (received.indexOf(messages.get(i)))) + ")."));
                            }
                        }
                    }
                    if (fail) {
                        error.set(sb.toString());
                    }
                    done.countDown();
                } catch (Throwable t) {
                    System.out.println(t);
                }
            }
        }, clientEndpointConfig, new URI(((DefaultServer.getDefaultServerURL()) + "/webSocket")));
        done.await(30, TimeUnit.SECONDS);
        if ((error.get()) != null) {
            Assert.fail(error.get());
        }
    }

    @ServerEndpoint("/webSocket")
    public static class EchoSocket {
        private final List<String> echos = new CopyOnWriteArrayList<>();

        public static volatile FutureResult<List<String>> receivedEchos = new FutureResult();

        @OnMessage
        public void onMessage(ByteBuffer dataBuffer, Session session) throws IOException {
            byte[] hd = new byte[dataBuffer.remaining()];
            dataBuffer.get(hd);
            String hash = TestMessagesReceivedInOrder.md5(hd);
            echos.add(hash);
            TestMessagesReceivedInOrder.stacks.add(new RuntimeException());
            if ((echos.size()) == (TestMessagesReceivedInOrder.MESSAGES)) {
                TestMessagesReceivedInOrder.EchoSocket.receivedEchos.setResult(echos);
            }
            session.getBasicRemote().sendBinary(dataBuffer);
        }
    }
}

