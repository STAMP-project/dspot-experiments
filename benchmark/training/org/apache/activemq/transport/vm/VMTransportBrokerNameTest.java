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
package org.apache.activemq.transport.vm;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.broker.PublishedAddressPolicy;
import org.apache.activemq.broker.TransportConnector;
import org.junit.Assert;
import org.junit.Test;


public class VMTransportBrokerNameTest {
    private static final String MY_BROKER = "myBroker";

    final String vmUrl = ("vm:(broker:(tcp://localhost:61616)/" + (VMTransportBrokerNameTest.MY_BROKER)) + "?persistent=false)";

    @Test
    public void testBrokerName() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(new URI(vmUrl));
        ActiveMQConnection c1 = ((ActiveMQConnection) (cf.createConnection()));
        Assert.assertTrue(("Transport has name in it: " + (c1.getTransport())), c1.getTransport().toString().contains(VMTransportBrokerNameTest.MY_BROKER));
        // verify Broker is there with name
        ActiveMQConnectionFactory cfbyName = new ActiveMQConnectionFactory(new URI((("vm://" + (VMTransportBrokerNameTest.MY_BROKER)) + "?create=false")));
        Connection c2 = cfbyName.createConnection();
        Assert.assertNotNull(BrokerRegistry.getInstance().lookup(VMTransportBrokerNameTest.MY_BROKER));
        Assert.assertEquals(BrokerRegistry.getInstance().findFirst().getBrokerName(), VMTransportBrokerNameTest.MY_BROKER);
        Assert.assertEquals(BrokerRegistry.getInstance().getBrokers().size(), 1);
        c1.close();
        c2.close();
    }

    @Test
    public void testPublishableAddressUri() throws Exception {
        PublishedAddressPolicy publishedAddressPolicy = new PublishedAddressPolicy();
        final AtomicReference<URI> uriAtomicReference = new AtomicReference<>();
        TransportConnector dummyTransportConnector = new TransportConnector() {
            @Override
            public URI getConnectUri() throws IOException, URISyntaxException {
                return uriAtomicReference.get();
            }
        };
        URI ok = new URI("vm://b1");
        uriAtomicReference.set(ok);
        Assert.assertEquals(uriAtomicReference.get(), publishedAddressPolicy.getPublishableConnectURI(dummyTransportConnector));
        ok = new URI("vm://b1?async=false");
        uriAtomicReference.set(ok);
        Assert.assertEquals(uriAtomicReference.get(), publishedAddressPolicy.getPublishableConnectURI(dummyTransportConnector));
        URI badHost = new URI("vm://b1_11");
        uriAtomicReference.set(badHost);
        Assert.assertEquals(uriAtomicReference.get(), publishedAddressPolicy.getPublishableConnectURI(dummyTransportConnector));
    }

    @Test
    public void testBrokerInfoReceiptClientAsync() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(new URI(vmUrl));
        ActiveMQConnection c1 = ((ActiveMQConnection) (cf.createConnection()));
        final int numIterations = 400;
        final CountDownLatch successLatch = new CountDownLatch(numIterations);
        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < numIterations; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        verifyBrokerInfo(successLatch);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.SECONDS);
        c1.close();
        Assert.assertTrue(("all success: " + (successLatch.getCount())), successLatch.await(1, TimeUnit.SECONDS));
    }
}

