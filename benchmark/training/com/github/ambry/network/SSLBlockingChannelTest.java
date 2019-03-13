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
import com.github.ambry.config.SSLConfig;
import java.io.IOException;
import javax.net.ssl.SSLSocketFactory;
import org.junit.Assert;
import org.junit.Test;


public class SSLBlockingChannelTest {
    private static SSLFactory sslFactory;

    private static SSLConfig clientSSLConfig;

    private static SSLSocketFactory sslSocketFactory;

    private static EchoServer sslEchoServer;

    private static String hostName = "localhost";

    private static int sslPort = 18284;

    @Test
    public void testSendAndReceive() throws Exception {
        BlockingChannel channel = new SSLBlockingChannel(SSLBlockingChannelTest.hostName, SSLBlockingChannelTest.sslPort, new MetricRegistry(), 10000, 10000, 10000, 2000, SSLBlockingChannelTest.sslSocketFactory, SSLBlockingChannelTest.clientSSLConfig);
        sendAndReceive(channel);
        channel.disconnect();
    }

    @Test
    public void testRenegotiation() throws Exception {
        BlockingChannel channel = new SSLBlockingChannel(SSLBlockingChannelTest.hostName, SSLBlockingChannelTest.sslPort, new MetricRegistry(), 10000, 10000, 10000, 2000, SSLBlockingChannelTest.sslSocketFactory, SSLBlockingChannelTest.clientSSLConfig);
        sendAndReceive(channel);
        SSLBlockingChannelTest.sslEchoServer.renegotiate();
        sendAndReceive(channel);
        channel.disconnect();
    }

    @Test
    public void testWrongPortConnection() throws Exception {
        BlockingChannel channel = new SSLBlockingChannel(SSLBlockingChannelTest.hostName, ((SSLBlockingChannelTest.sslPort) + 1), new MetricRegistry(), 10000, 10000, 10000, 2000, SSLBlockingChannelTest.sslSocketFactory, SSLBlockingChannelTest.clientSSLConfig);
        try {
            // send request
            channel.connect();
            Assert.fail("should have thrown!");
        } catch (IOException e) {
        }
    }
}

