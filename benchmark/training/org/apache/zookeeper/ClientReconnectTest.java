/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ClientReconnectTest extends ZKTestCase {
    private SocketChannel sc;

    private CountDownLatch countDownLatch = new CountDownLatch(3);

    class MockCnxn extends ClientCnxnSocketNIO {
        MockCnxn() throws IOException {
            super(new ZKClientConfig());
        }

        @Override
        void registerAndConnect(SocketChannel sock, InetSocketAddress addr) throws IOException {
            countDownLatch.countDown();
            throw new IOException("failed to register");
        }

        @Override
        SocketChannel createSock() {
            return sc;
        }
    }

    @Test
    public void testClientReconnect() throws IOException, InterruptedException {
        HostProvider hostProvider = Mockito.mock(HostProvider.class);
        Mockito.when(hostProvider.size()).thenReturn(1);
        InetSocketAddress inaddr = new InetSocketAddress("127.0.0.1", 1111);
        Mockito.when(hostProvider.next(ArgumentMatchers.anyLong())).thenReturn(inaddr);
        ZooKeeper zk = Mockito.mock(ZooKeeper.class);
        Mockito.when(zk.getClientConfig()).thenReturn(new ZKClientConfig());
        sc = SocketChannel.open();
        ClientCnxnSocketNIO nioCnxn = new ClientReconnectTest.MockCnxn();
        ClientWatchManager watcher = Mockito.mock(ClientWatchManager.class);
        ClientCnxn clientCnxn = new ClientCnxn("tmp", hostProvider, 5000, zk, watcher, nioCnxn, false);
        clientCnxn.start();
        countDownLatch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(((countDownLatch.getCount()) == 0));
        clientCnxn.close();
    }
}

