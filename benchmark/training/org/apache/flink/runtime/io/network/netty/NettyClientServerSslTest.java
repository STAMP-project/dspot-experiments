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
package org.apache.flink.runtime.io.network.netty;


import SecurityOptions.SSL_INTERNAL_KEYSTORE;
import SecurityOptions.SSL_INTERNAL_KEYSTORE_PASSWORD;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.netty4.io.netty.channel.Channel;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandler;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.string.StringDecoder;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.string.StringEncoder;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the SSL connection between Netty Server and Client used for the
 * data plane.
 */
public class NettyClientServerSslTest extends TestLogger {
    /**
     * Verify valid ssl configuration and connection.
     */
    @Test
    public void testValidSslConnection() throws Exception {
        testValidSslConnection(NettyClientServerSslTest.createSslConfig());
    }

    /**
     * Verify valid (advanced) ssl configuration and connection.
     */
    @Test
    public void testValidSslConnectionAdvanced() throws Exception {
        Configuration sslConfig = NettyClientServerSslTest.createSslConfig();
        sslConfig.setInteger(SSL_INTERNAL_SESSION_CACHE_SIZE, 1);
        sslConfig.setInteger(SSL_INTERNAL_SESSION_TIMEOUT, 1000);
        sslConfig.setInteger(SSL_INTERNAL_HANDSHAKE_TIMEOUT, 1000);
        sslConfig.setInteger(SSL_INTERNAL_CLOSE_NOTIFY_FLUSH_TIMEOUT, 1000);
        testValidSslConnection(sslConfig);
    }

    /**
     * Verify failure on invalid ssl configuration.
     */
    @Test
    public void testInvalidSslConfiguration() throws Exception {
        NettyProtocol protocol = new NettyClientServerSslTest.NoOpProtocol();
        Configuration config = NettyClientServerSslTest.createSslConfig();
        // Modify the keystore password to an incorrect one
        config.setString(SSL_INTERNAL_KEYSTORE_PASSWORD, "invalidpassword");
        NettyConfig nettyConfig = NettyClientServerSslTest.createNettyConfig(config);
        NettyTestUtil.NettyServerAndClient serverAndClient = null;
        try {
            serverAndClient = NettyTestUtil.initServerAndClient(protocol, nettyConfig);
            Assert.fail("Created server and client from invalid configuration");
        } catch (Exception e) {
            // Exception should be thrown as expected
        }
        NettyTestUtil.shutdown(serverAndClient);
    }

    /**
     * Verify SSL handshake error when untrusted server certificate is used.
     */
    @Test
    public void testSslHandshakeError() throws Exception {
        NettyProtocol protocol = new NettyClientServerSslTest.NoOpProtocol();
        Configuration config = NettyClientServerSslTest.createSslConfig();
        // Use a server certificate which is not present in the truststore
        config.setString(SSL_INTERNAL_KEYSTORE, "src/test/resources/untrusted.keystore");
        NettyConfig nettyConfig = NettyClientServerSslTest.createNettyConfig(config);
        NettyTestUtil.NettyServerAndClient serverAndClient = NettyTestUtil.initServerAndClient(protocol, nettyConfig);
        Channel ch = NettyTestUtil.connect(serverAndClient);
        ch.pipeline().addLast(new StringDecoder()).addLast(new StringEncoder());
        // Attempting to write data over ssl should fail
        Assert.assertFalse(ch.writeAndFlush("test").await().isSuccess());
        NettyTestUtil.shutdown(serverAndClient);
    }

    @Test
    public void testClientUntrustedCertificate() throws Exception {
        final Configuration serverConfig = NettyClientServerSslTest.createSslConfig();
        final Configuration clientConfig = NettyClientServerSslTest.createSslConfig();
        // give the client a different keystore / certificate
        clientConfig.setString(SSL_INTERNAL_KEYSTORE, "src/test/resources/untrusted.keystore");
        final NettyConfig nettyServerConfig = NettyClientServerSslTest.createNettyConfig(serverConfig);
        final NettyConfig nettyClientConfig = NettyClientServerSslTest.createNettyConfig(clientConfig);
        final NettyBufferPool bufferPool = new NettyBufferPool(1);
        final NettyProtocol protocol = new NettyClientServerSslTest.NoOpProtocol();
        final NettyServer server = NettyTestUtil.initServer(nettyServerConfig, protocol, bufferPool);
        final NettyClient client = NettyTestUtil.initClient(nettyClientConfig, protocol, bufferPool);
        final NettyTestUtil.NettyServerAndClient serverAndClient = new NettyTestUtil.NettyServerAndClient(server, client);
        final Channel ch = NettyTestUtil.connect(serverAndClient);
        ch.pipeline().addLast(new StringDecoder()).addLast(new StringEncoder());
        // Attempting to write data over ssl should fail
        Assert.assertFalse(ch.writeAndFlush("test").await().isSuccess());
        NettyTestUtil.shutdown(serverAndClient);
    }

    private static final class NoOpProtocol extends NettyProtocol {
        NoOpProtocol() {
            super(null, null, true);
        }

        @Override
        public ChannelHandler[] getServerChannelHandlers() {
            return new ChannelHandler[0];
        }

        @Override
        public ChannelHandler[] getClientChannelHandlers() {
            return new ChannelHandler[0];
        }
    }
}

