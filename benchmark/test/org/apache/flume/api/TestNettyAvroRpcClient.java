/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.api;


import RpcClientConfigurationConstants.CONFIG_BATCH_SIZE;
import RpcClientConfigurationConstants.CONFIG_COMPRESSION_LEVEL;
import RpcClientConfigurationConstants.CONFIG_COMPRESSION_TYPE;
import RpcClientConfigurationConstants.CONFIG_HOSTS;
import RpcClientConfigurationConstants.MAX_IO_WORKERS;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.avro.ipc.Server;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static RpcClientConfigurationConstants.CONFIG_HOSTS_PREFIX;


/**
 *
 */
public class TestNettyAvroRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(TestNettyAvroRpcClient.class);

    private static final String localhost = "127.0.0.1";

    /**
     * Simple request
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testOKServerSimple() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.OKAvroHandler());
    }

    /**
     * Simple request with compression on the server and client with compression level 6
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testOKServerSimpleCompressionLevel6() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.OKAvroHandler(), true, true, 6);
    }

    /**
     * Simple request with compression on the server and client with compression level 0
     *
     * Compression level 0 = no compression
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testOKServerSimpleCompressionLevel0() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.OKAvroHandler(), true, true, 0);
    }

    /**
     * Simple request with compression on the client only
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test(expected = EventDeliveryException.class)
    public void testOKServerSimpleCompressionClientOnly() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.OKAvroHandler(), false, true, 6);
    }

    /**
     * Simple request with compression on the server only
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test(expected = EventDeliveryException.class)
    public void testOKServerSimpleCompressionServerOnly() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.OKAvroHandler(), true, false, 6);
    }

    /**
     * Simple batch request
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testOKServerBatch() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.OKAvroHandler());
    }

    /**
     * Simple batch request with compression deflate level 0
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testOKServerBatchCompressionLevel0() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.OKAvroHandler(), true, true, 0);
    }

    /**
     * Simple batch request with compression deflate level 6
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testOKServerBatchCompressionLevel6() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.OKAvroHandler(), true, true, 6);
    }

    /**
     * Simple batch request where the server only is using compression
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test(expected = EventDeliveryException.class)
    public void testOKServerBatchCompressionServerOnly() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.OKAvroHandler(), true, false, 6);
    }

    /**
     * Simple batch request where the client only is using compression
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test(expected = EventDeliveryException.class)
    public void testOKServerBatchCompressionClientOnly() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.OKAvroHandler(), false, true, 6);
    }

    /**
     * Try to connect to a closed port.
     * Note: this test tries to connect to port 1 on localhost.
     *
     * @throws FlumeException
     * 		
     */
    @Test(expected = FlumeException.class)
    public void testUnableToConnect() throws FlumeException {
        @SuppressWarnings("unused")
        NettyAvroRpcClient client = new NettyAvroRpcClient();
        Properties props = new Properties();
        props.setProperty(CONFIG_HOSTS, "localhost");
        props.setProperty(((CONFIG_HOSTS_PREFIX) + "localhost"), (((TestNettyAvroRpcClient.localhost) + ":") + 1));
        client.configure(props);
    }

    /**
     * Send too many events at once. Should handle this case gracefully.
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testBatchOverrun() throws EventDeliveryException, FlumeException {
        int batchSize = 10;
        int moreThanBatchSize = batchSize + 1;
        NettyAvroRpcClient client = null;
        Server server = RpcTestUtils.startServer(new RpcTestUtils.OKAvroHandler());
        Properties props = new Properties();
        props.setProperty(CONFIG_HOSTS, "localhost");
        props.setProperty(((CONFIG_HOSTS_PREFIX) + "localhost"), (((TestNettyAvroRpcClient.localhost) + ":") + (server.getPort())));
        props.setProperty(CONFIG_BATCH_SIZE, ("" + batchSize));
        try {
            client = new NettyAvroRpcClient();
            client.configure(props);
            // send one more than the batch size
            List<Event> events = new ArrayList<Event>();
            for (int i = 0; i < moreThanBatchSize; i++) {
                events.add(EventBuilder.withBody(("evt: " + i), Charset.forName("UTF8")));
            }
            client.appendBatch(events);
        } finally {
            RpcTestUtils.stopServer(server);
            if (client != null)
                client.close();

        }
    }

    /**
     * First connect the client, then shut down the server, then send a request.
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(expected = EventDeliveryException.class)
    public void testServerDisconnect() throws InterruptedException, EventDeliveryException, FlumeException {
        NettyAvroRpcClient client = null;
        Server server = RpcTestUtils.startServer(new RpcTestUtils.OKAvroHandler());
        try {
            client = RpcTestUtils.getStockLocalClient(server.getPort());
            server.close();
            Thread.sleep(1000L);// wait a second for the close to occur

            try {
                server.join();
            } catch (InterruptedException ex) {
                TestNettyAvroRpcClient.logger.warn("Thread interrupted during join()", ex);
                Thread.currentThread().interrupt();
            }
            try {
                client.append(EventBuilder.withBody("hello", Charset.forName("UTF8")));
            } finally {
                Assert.assertFalse("Client should not be active", client.isActive());
            }
        } finally {
            RpcTestUtils.stopServer(server);
            if (client != null)
                client.close();

        }
    }

    /**
     * First connect the client, then close the client, then send a request.
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test(expected = EventDeliveryException.class)
    public void testClientClosedRequest() throws EventDeliveryException, FlumeException {
        NettyAvroRpcClient client = null;
        Server server = RpcTestUtils.startServer(new RpcTestUtils.OKAvroHandler());
        try {
            client = RpcTestUtils.getStockLocalClient(server.getPort());
            client.close();
            Assert.assertFalse("Client should not be active", client.isActive());
            System.out.println("Yaya! I am not active after client close!");
            client.append(EventBuilder.withBody("hello", Charset.forName("UTF8")));
        } finally {
            RpcTestUtils.stopServer(server);
            if (client != null)
                client.close();

        }
    }

    /**
     * Send an event to an online server that returns FAILED.
     */
    @Test(expected = EventDeliveryException.class)
    public void testFailedServerSimple() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.FailedAvroHandler());
        TestNettyAvroRpcClient.logger.error("Failed: I should never have gotten here!");
    }

    /**
     * Send an event to an online server that returns UNKNOWN.
     */
    @Test(expected = EventDeliveryException.class)
    public void testUnknownServerSimple() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.UnknownAvroHandler());
        TestNettyAvroRpcClient.logger.error("Unknown: I should never have gotten here!");
    }

    /**
     * Send an event to an online server that throws an exception.
     */
    @Test(expected = EventDeliveryException.class)
    public void testThrowingServerSimple() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerSimpleAppendTest(new RpcTestUtils.ThrowingAvroHandler());
        TestNettyAvroRpcClient.logger.error("Throwing: I should never have gotten here!");
    }

    /**
     * Send a batch of events to a server that returns FAILED.
     */
    @Test(expected = EventDeliveryException.class)
    public void testFailedServerBatch() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.FailedAvroHandler());
        TestNettyAvroRpcClient.logger.error("Failed: I should never have gotten here!");
    }

    /**
     * Send a batch of events to a server that returns UNKNOWN.
     */
    @Test(expected = EventDeliveryException.class)
    public void testUnknownServerBatch() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.UnknownAvroHandler());
        TestNettyAvroRpcClient.logger.error("Unknown: I should never have gotten here!");
    }

    /**
     * Send a batch of events to a server that always throws exceptions.
     */
    @Test(expected = EventDeliveryException.class)
    public void testThrowingServerBatch() throws EventDeliveryException, FlumeException {
        RpcTestUtils.handlerBatchAppendTest(new RpcTestUtils.ThrowingAvroHandler());
        TestNettyAvroRpcClient.logger.error("Throwing: I should never have gotten here!");
    }

    /**
     * configure the NettyAvroRpcClient with a non-default
     * NioClientSocketChannelFactory number of io worker threads
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testAppendWithMaxIOWorkers() throws EventDeliveryException, FlumeException {
        NettyAvroRpcClient client = null;
        Server server = RpcTestUtils.startServer(new RpcTestUtils.OKAvroHandler());
        Properties props = new Properties();
        props.setProperty(CONFIG_HOSTS, "localhost");
        props.setProperty(((CONFIG_HOSTS_PREFIX) + "localhost"), (((TestNettyAvroRpcClient.localhost) + ":") + (server.getPort())));
        props.setProperty(MAX_IO_WORKERS, Integer.toString(2));
        try {
            client = new NettyAvroRpcClient();
            client.configure(props);
            for (int i = 0; i < 5; i++) {
                client.append(EventBuilder.withBody(("evt:" + i), Charset.forName("UTF8")));
            }
        } finally {
            RpcTestUtils.stopServer(server);
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * Simple request with compression on the server and client with compression
     * level 0
     *
     * configure the NettyAvroRpcClient with a non-default
     * NioClientSocketChannelFactory number of io worker threads
     *
     * Compression level 0 = no compression
     *
     * @throws FlumeException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testAppendWithMaxIOWorkersSimpleCompressionLevel0() throws EventDeliveryException, FlumeException {
        NettyAvroRpcClient client = null;
        Server server = RpcTestUtils.startServer(new RpcTestUtils.OKAvroHandler(), 0, true);
        Properties props = new Properties();
        props.setProperty(CONFIG_HOSTS, "localhost");
        props.setProperty(((CONFIG_HOSTS_PREFIX) + "localhost"), (((TestNettyAvroRpcClient.localhost) + ":") + (server.getPort())));
        props.setProperty(MAX_IO_WORKERS, Integer.toString(2));
        props.setProperty(CONFIG_COMPRESSION_TYPE, "deflate");
        props.setProperty(CONFIG_COMPRESSION_LEVEL, ("" + 0));
        try {
            client = new NettyAvroRpcClient();
            client.configure(props);
            for (int i = 0; i < 5; i++) {
                client.append(EventBuilder.withBody(("evt:" + i), Charset.forName("UTF8")));
            }
        } finally {
            RpcTestUtils.stopServer(server);
            if (client != null) {
                client.close();
            }
        }
    }
}

