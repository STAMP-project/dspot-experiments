/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.protocol.tcp.client;


import LogLevel.DEBUG;
import LogLevel.ERROR;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.server.TcpServer;
import java.net.SocketAddress;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.Observable;


public class EventListenerTest {
    @Rule
    public final EventListenerTest.TcpServerRule rule = new EventListenerTest.TcpServerRule();

    @Test(timeout = 60000)
    public void testEventListener() throws Exception {
        TcpClient<ByteBuf, ByteBuf> client = TcpClient.newClient(rule.serverAddress);
        EventListenerTest.assertListenerCalled(client);
    }

    @Test(timeout = 60000)
    public void testEventListenerPostCopy() throws Exception {
        TcpClient<ByteBuf, ByteBuf> client = TcpClient.newClient(rule.serverAddress).enableWireLogging("test", ERROR);
        EventListenerTest.assertListenerCalled(client);
    }

    @Test(timeout = 60000)
    public void testSubscriptionPreCopy() throws Exception {
        TcpClient<ByteBuf, ByteBuf> client = TcpClient.newClient(rule.serverAddress);
        MockTcpClientEventListener listener = EventListenerTest.subscribe(client);
        client = client.enableWireLogging("test", DEBUG);
        EventListenerTest.connectAndAssertListenerInvocation(client, listener);
    }

    public static class TcpServerRule extends ExternalResource {
        private SocketAddress serverAddress;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    serverAddress = TcpServer.newServer().start(new io.reactivex.netty.protocol.tcp.server.ConnectionHandler<ByteBuf, ByteBuf>() {
                        @Override
                        public Observable<Void> handle(Connection<ByteBuf, ByteBuf> newConnection) {
                            return newConnection.writeString(Observable.just("Hello"));
                        }
                    }).getServerAddress();
                    base.evaluate();
                }
            };
        }
    }
}

