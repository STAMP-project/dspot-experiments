/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.network;


import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.SSLFactory;
import com.github.ambry.config.ClusterMapConfig;
import com.github.ambry.config.NetworkConfig;
import com.github.ambry.config.SSLConfig;
import com.github.ambry.config.VerifiableProperties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLSocketFactory;
import org.junit.Assert;
import org.junit.Test;

import static PortType.PLAINTEXT;
import static PortType.SSL;


/**
 * Test for the blocking channel connection pool
 */
public class BlockingChannelConnectionPoolTest {
    private SocketServer server1 = null;

    private SocketServer server2 = null;

    private SocketServer server3 = null;

    private static File trustStoreFile = null;

    private static SSLFactory sslFactory;

    private static SSLConfig sslConfig;

    private static ClusterMapConfig plainTextClusterMapConfig;

    private static ClusterMapConfig sslEnabledClusterMapConfig;

    private static SSLConfig serverSSLConfig1;

    private static SSLConfig serverSSLConfig2;

    private static SSLConfig serverSSLConfig3;

    private static SSLSocketFactory sslSocketFactory;

    public BlockingChannelConnectionPoolTest() throws Exception {
        Properties props = new Properties();
        props.setProperty("port", "6667");
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
        VerifiableProperties propverify = new VerifiableProperties(props);
        NetworkConfig config = new NetworkConfig(propverify);
        ArrayList<Port> ports = new ArrayList<Port>();
        ports.add(new Port(6667, PLAINTEXT));
        ports.add(new Port(7667, SSL));
        server1 = new SocketServer(config, BlockingChannelConnectionPoolTest.serverSSLConfig1, new MetricRegistry(), ports);
        server1.start();
        props.setProperty("port", "6668");
        propverify = new VerifiableProperties(props);
        config = new NetworkConfig(propverify);
        ports = new ArrayList<Port>();
        ports.add(new Port(6668, PLAINTEXT));
        ports.add(new Port(7668, SSL));
        server2 = new SocketServer(config, BlockingChannelConnectionPoolTest.serverSSLConfig2, new MetricRegistry(), ports);
        server2.start();
        props.setProperty("port", "6669");
        propverify = new VerifiableProperties(props);
        config = new NetworkConfig(propverify);
        ports = new ArrayList<Port>();
        ports.add(new Port(6669, PLAINTEXT));
        ports.add(new Port(7669, SSL));
        server3 = new SocketServer(config, BlockingChannelConnectionPoolTest.serverSSLConfig3, new MetricRegistry(), ports);
        server3.start();
    }

    class BlockingChannelInfoThread implements Runnable {
        private final BlockingChannelInfo channelInfo;

        private final CountDownLatch channelCount;

        private final CountDownLatch shouldRelease;

        private final CountDownLatch releaseComplete;

        private final boolean destroyConnection;

        private final AtomicReference<Exception> exception;

        public BlockingChannelInfoThread(BlockingChannelInfo channelInfo, CountDownLatch channelCount, CountDownLatch shouldRelease, CountDownLatch releaseComplete, boolean destroyConnection, AtomicReference<Exception> exception) {
            this.channelInfo = channelInfo;
            this.channelCount = channelCount;
            this.shouldRelease = shouldRelease;
            this.releaseComplete = releaseComplete;
            this.destroyConnection = destroyConnection;
            this.exception = exception;
        }

        @Override
        public void run() {
            try {
                BlockingChannel channel = channelInfo.getBlockingChannel(1000);
                channelCount.countDown();
                if (shouldRelease.await(1000, TimeUnit.MILLISECONDS)) {
                    if (destroyConnection) {
                        channelInfo.destroyBlockingChannel(channel);
                    } else {
                        channelInfo.releaseBlockingChannel(channel);
                    }
                } else
                    if ((exception.get()) == null) {
                        exception.set(new Exception("Timed out waiting for signal to release connections"));
                    }

            } catch (Exception e) {
                exception.set(e);
            } finally {
                releaseComplete.countDown();
            }
        }
    }

    /**
     * Tests how connection failures are handled by BlockingChannelInfo.
     */
    @Test
    public void testConnectionFailureCases() throws ConnectionPoolTimeoutException, IOException, InterruptedException {
        int port = 6680;
        String host = "127.0.0.1";
        SocketServer server = startServer(port);
        Properties props = new Properties();
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
        BlockingChannelInfo channelInfo = new BlockingChannelInfo(new com.github.ambry.config.ConnectionPoolConfig(new VerifiableProperties(props)), host, new Port(port, PLAINTEXT), new MetricRegistry(), BlockingChannelConnectionPoolTest.sslSocketFactory, BlockingChannelConnectionPoolTest.sslConfig);
        // ask for N no of connections
        Assert.assertEquals(channelInfo.getNumberOfConnections(), 0);
        BlockingChannel blockingChannel1 = channelInfo.getBlockingChannel(1000);
        Assert.assertEquals(channelInfo.getNumberOfConnections(), 1);
        BlockingChannel blockingChannel2 = channelInfo.getBlockingChannel(1000);
        Assert.assertEquals(channelInfo.getNumberOfConnections(), 2);
        BlockingChannel blockingChannel3 = channelInfo.getBlockingChannel(1000);
        Assert.assertEquals(channelInfo.getNumberOfConnections(), 3);
        // realease 2 of them back to pool
        channelInfo.releaseBlockingChannel(blockingChannel2);
        channelInfo.releaseBlockingChannel(blockingChannel3);
        Assert.assertEquals("Available connections count mismatch ", 2, channelInfo.availableConnections.getValue().intValue());
        // shutdown server
        server.shutdown();
        // destroy one of the connections and verify that the available connections cleaned up
        channelInfo.destroyBlockingChannel(blockingChannel1);
        Assert.assertEquals("Available connections should have not been cleaned up", 0, channelInfo.availableConnections.getValue().intValue());
        // bring up the server
        startServer(port);
        // ask for 2 more connections
        BlockingChannel blockingChannel4 = channelInfo.getBlockingChannel(1000);
        Assert.assertEquals(channelInfo.getNumberOfConnections(), 1);
        BlockingChannel blockingChannel5 = channelInfo.getBlockingChannel(1000);
        Assert.assertEquals(channelInfo.getNumberOfConnections(), 2);
        // release one of them back to pool
        channelInfo.releaseBlockingChannel(blockingChannel4);
        // verify that destroy connection will not trigger clean up of available connections
        // as connection recreation should have passed
        channelInfo.destroyBlockingChannel(blockingChannel5);
        Assert.assertEquals("Available connections should not have been cleaned up", 2, channelInfo.availableConnections.getValue().intValue());
    }

    class ConnectionPoolThread implements Runnable {
        private final AtomicReference<Exception> exception;

        private final Map<String, CountDownLatch> channelCount;

        private final ConnectionPool connectionPool;

        private final boolean destroyConnection;

        private final CountDownLatch shouldRelease;

        private final CountDownLatch releaseComplete;

        private Map<String, Port> channelToPortMap;

        public ConnectionPoolThread(Map<String, CountDownLatch> channelCount, Map<String, Port> channelToPortMap, ConnectionPool connectionPool, boolean destroyConnection, CountDownLatch shouldRelease, CountDownLatch releaseComplete, AtomicReference<Exception> e) {
            this.channelCount = channelCount;
            this.channelToPortMap = channelToPortMap;
            this.connectionPool = connectionPool;
            this.destroyConnection = destroyConnection;
            this.shouldRelease = shouldRelease;
            this.releaseComplete = releaseComplete;
            this.exception = e;
        }

        @Override
        public void run() {
            try {
                List<ConnectedChannel> connectedChannels = new ArrayList<ConnectedChannel>();
                for (String channelStr : channelCount.keySet()) {
                    Port port = channelToPortMap.get(channelStr);
                    ConnectedChannel channel = connectionPool.checkOutConnection("localhost", new Port(port.getPort(), port.getPortType()), 1000);
                    connectedChannels.add(channel);
                    channelCount.get(channelStr).countDown();
                }
                if (shouldRelease.await(5000, TimeUnit.MILLISECONDS)) {
                    for (ConnectedChannel channel : connectedChannels) {
                        if (destroyConnection) {
                            connectionPool.destroyConnection(channel);
                        } else {
                            connectionPool.checkInConnection(channel);
                        }
                    }
                } else
                    if ((exception.get()) == null) {
                        exception.set(new Exception("Timed out waiting for signal to release connections"));
                    }

            } catch (Exception e) {
                exception.set(e);
            } finally {
                releaseComplete.countDown();
            }
        }
    }

    @Test
    public void testBlockingChannelConnectionPool() throws Exception {
        Properties props = new Properties();
        props.put("connectionpool.max.connections.per.port.plain.text", "5");
        props.put("connectionpool.max.connections.per.port.ssl", "5");
        props.put("clustermap.cluster.name", "test");
        props.put("clustermap.datacenter.name", "dc1");
        props.put("clustermap.host.name", "localhost");
        ConnectionPool connectionPool = new BlockingChannelConnectionPool(new com.github.ambry.config.ConnectionPoolConfig(new VerifiableProperties(props)), BlockingChannelConnectionPoolTest.sslConfig, BlockingChannelConnectionPoolTest.plainTextClusterMapConfig, new MetricRegistry());
        connectionPool.start();
        CountDownLatch shouldRelease = new CountDownLatch(1);
        CountDownLatch releaseComplete = new CountDownLatch(10);
        AtomicReference<Exception> exception = new AtomicReference<Exception>();
        Map<String, CountDownLatch> channelCount = new HashMap<String, CountDownLatch>();
        channelCount.put(("localhost" + 6667), new CountDownLatch(5));
        channelCount.put(("localhost" + 6668), new CountDownLatch(5));
        channelCount.put(("localhost" + 6669), new CountDownLatch(5));
        Map<String, Port> channelToPortMap = new HashMap<String, Port>();
        channelToPortMap.put(("localhost" + 6667), new Port(6667, PLAINTEXT));
        channelToPortMap.put(("localhost" + 6668), new Port(6668, PLAINTEXT));
        channelToPortMap.put(("localhost" + 6669), new Port(6669, PLAINTEXT));
        for (int i = 0; i < 10; i++) {
            BlockingChannelConnectionPoolTest.ConnectionPoolThread connectionPoolThread = new BlockingChannelConnectionPoolTest.ConnectionPoolThread(channelCount, channelToPortMap, connectionPool, false, shouldRelease, releaseComplete, exception);
            Thread t = new Thread(connectionPoolThread);
            t.start();
        }
        for (String channelStr : channelCount.keySet()) {
            awaitCountdown(channelCount.get(channelStr), 1000, exception, "Timed out waiting for channel count to reach 5");
        }
        // reset
        for (String channelStr : channelCount.keySet()) {
            channelCount.put(channelStr, new CountDownLatch(5));
        }
        shouldRelease.countDown();
        for (String channelStr : channelCount.keySet()) {
            awaitCountdown(channelCount.get(channelStr), 1000, exception, "Timed out waiting for channel count to reach 5");
        }
        awaitCountdown(releaseComplete, 2000, exception, "Timed out while waiting for channels to be released");
        connectionPool.shutdown();
    }
}

