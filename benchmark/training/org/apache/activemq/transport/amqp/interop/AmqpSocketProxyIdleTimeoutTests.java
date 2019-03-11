/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.amqp.interop;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpConnectionListener;
import org.apache.activemq.util.SocketProxy;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for idle timeout processing using SocketProxy to interrupt coms.
 */
public class AmqpSocketProxyIdleTimeoutTests extends AmqpClientTestSupport {
    private final int TEST_IDLE_TIMEOUT = 3000;

    private SocketProxy socketProxy;

    @Test(timeout = 60000)
    public void testBrokerSendsRequestedHeartbeats() throws Exception {
        final CountDownLatch disconnected = new CountDownLatch(1);
        AmqpClient client = createAmqpClient();
        Assert.assertNotNull(client);
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setIdleTimeout(TEST_IDLE_TIMEOUT);
        Assert.assertNotNull(connection);
        connection.setListener(new AmqpConnectionListener() {
            @Override
            public void onException(Throwable ex) {
                disconnected.countDown();
            }
        });
        connection.connect();
        Assert.assertEquals(1, getProxyToBroker().getCurrentConnectionsCount());
        Assert.assertFalse(disconnected.await(5, TimeUnit.SECONDS));
        Assert.assertEquals(1, getProxyToBroker().getCurrentConnectionsCount());
        socketProxy.pause();
        Assert.assertEquals(1, getProxyToBroker().getCurrentConnectionsCount());
        Assert.assertTrue(disconnected.await(10, TimeUnit.SECONDS));
        socketProxy.goOn();
        connection.close();
        Assert.assertTrue("Connection should get cleaned up.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }

    @Test(timeout = 60000)
    public void testClientWithoutHeartbeatsGetsDropped() throws Exception {
        final CountDownLatch disconnected = new CountDownLatch(1);
        AmqpClient client = createAmqpClient();
        Assert.assertNotNull(client);
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setCloseTimeout(1000);// Socket will have silently gone away, don't wait to long.

        Assert.assertNotNull(connection);
        connection.setListener(new AmqpConnectionListener() {
            @Override
            public void onException(Throwable ex) {
                disconnected.countDown();
            }
        });
        connection.connect();
        Assert.assertEquals(1, getProxyToBroker().getCurrentConnectionsCount());
        socketProxy.pause();
        // Client still sends ok but broker doesn't see them.
        Assert.assertFalse(disconnected.await(5, TimeUnit.SECONDS));
        socketProxy.halfClose();
        Assert.assertTrue(disconnected.await(15, TimeUnit.SECONDS));
        socketProxy.close();
        connection.close();
        Assert.assertTrue("Connection should get cleaned up.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToBroker().getCurrentConnectionsCount()) == 0;
            }
        }));
    }
}

