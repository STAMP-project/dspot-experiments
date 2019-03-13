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
package org.apache.flume.source;


import LifecycleState.START;
import LifecycleState.START_OR_ERROR;
import LifecycleState.STOP;
import LifecycleState.STOP_OR_ERROR;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.avro.AvroRemoteException;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurables;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.lifecycle.LifecycleController;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAvroSource {
    private static final Logger logger = LoggerFactory.getLogger(TestAvroSource.class);

    private int selectedPort;

    private AvroSource source;

    private Channel channel;

    private InetAddress localhost;

    @Test
    public void testLifecycle() throws IOException, InterruptedException {
        Context context = new Context();
        context.put("port", String.valueOf((selectedPort = TestAvroSource.getFreePort())));
        context.put("bind", "0.0.0.0");
        Configurables.configure(source, context);
        source.start();
        Assert.assertTrue("Reached start or error", LifecycleController.waitForOneOf(source, START_OR_ERROR));
        Assert.assertEquals("Server is started", START, source.getLifecycleState());
        source.stop();
        Assert.assertTrue("Reached stop or error", LifecycleController.waitForOneOf(source, STOP_OR_ERROR));
        Assert.assertEquals("Server is stopped", STOP, source.getLifecycleState());
    }

    @Test
    public void testSourceStoppedOnFlumeExceptionIfPortUsed() throws IOException, InterruptedException {
        final String loopbackIPv4 = "127.0.0.1";
        final int port = 10500;
        // create a dummy socket bound to a known port.
        try (ServerSocketChannel dummyServerSocket = ServerSocketChannel.open()) {
            dummyServerSocket.socket().setReuseAddress(true);
            dummyServerSocket.socket().bind(new InetSocketAddress(loopbackIPv4, port));
            Context context = new Context();
            context.put("port", String.valueOf(port));
            context.put("bind", loopbackIPv4);
            Configurables.configure(source, context);
            try {
                source.start();
                Assert.fail("Expected an exception during startup caused by binding on a used port");
            } catch (FlumeException e) {
                TestAvroSource.logger.info("Received an expected exception.", e);
                Assert.assertTrue("Expected a server socket setup related root cause", e.getMessage().contains("server socket"));
            }
        }
        // As port is already in use, an exception is thrown and the source is stopped
        // cleaning up the opened sockets during source.start().
        Assert.assertEquals("Server is stopped", STOP, source.getLifecycleState());
    }

    @Test
    public void testInvalidAddress() throws IOException, InterruptedException {
        final String invalidHost = "invalid.host";
        final int port = 10501;
        Context context = new Context();
        context.put("port", String.valueOf(port));
        context.put("bind", invalidHost);
        Configurables.configure(source, context);
        try {
            source.start();
            Assert.fail("Expected an exception during startup caused by binding on a invalid host");
        } catch (FlumeException e) {
            TestAvroSource.logger.info("Received an expected exception.", e);
            Assert.assertTrue("Expected a server socket setup related root cause", e.getMessage().contains("server socket"));
        }
        // As port is already in use, an exception is thrown and the source is stopped
        // cleaning up the opened sockets during source.start().
        Assert.assertEquals("Server is stopped", STOP, source.getLifecycleState());
    }

    @Test
    public void testRequestWithNoCompression() throws IOException, InterruptedException {
        doRequest(false, false, 6);
    }

    @Test
    public void testRequestWithCompressionOnClientAndServerOnLevel0() throws IOException, InterruptedException {
        doRequest(true, true, 0);
    }

    @Test
    public void testRequestWithCompressionOnClientAndServerOnLevel1() throws IOException, InterruptedException {
        doRequest(true, true, 1);
    }

    @Test
    public void testRequestWithCompressionOnClientAndServerOnLevel6() throws IOException, InterruptedException {
        doRequest(true, true, 6);
    }

    @Test
    public void testRequestWithCompressionOnClientAndServerOnLevel9() throws IOException, InterruptedException {
        doRequest(true, true, 9);
    }

    @Test(expected = AvroRemoteException.class)
    public void testRequestWithCompressionOnServerOnly() throws IOException, InterruptedException {
        // This will fail because both client and server need compression on
        doRequest(true, false, 6);
    }

    @Test(expected = AvroRemoteException.class)
    public void testRequestWithCompressionOnClientOnly() throws IOException, InterruptedException {
        // This will fail because both client and server need compression on
        doRequest(false, true, 6);
    }

    private static class CompressionChannelFactory extends NioClientSocketChannelFactory {
        private int compressionLevel;

        public CompressionChannelFactory(int compressionLevel) {
            super();
            this.compressionLevel = compressionLevel;
        }

        @Override
        public SocketChannel newChannel(ChannelPipeline pipeline) {
            try {
                ZlibEncoder encoder = new ZlibEncoder(compressionLevel);
                pipeline.addFirst("deflater", encoder);
                pipeline.addFirst("inflater", new ZlibDecoder());
                return super.newChannel(pipeline);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot create Compression channel", ex);
            }
        }
    }

    @Test
    public void testSslRequestWithComponentKeystore() throws IOException, InterruptedException {
        Context context = new Context();
        context.put("port", String.valueOf((selectedPort = TestAvroSource.getFreePort())));
        context.put("bind", "0.0.0.0");
        context.put("ssl", "true");
        context.put("keystore", "src/test/resources/server.p12");
        context.put("keystore-password", "password");
        context.put("keystore-type", "PKCS12");
        Configurables.configure(source, context);
        doSslRequest();
    }

    @Test
    public void testSslRequestWithGlobalKeystore() throws IOException, InterruptedException {
        System.setProperty("javax.net.ssl.keyStore", "src/test/resources/server.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        Context context = new Context();
        context.put("port", String.valueOf((selectedPort = TestAvroSource.getFreePort())));
        context.put("bind", "0.0.0.0");
        context.put("ssl", "true");
        Configurables.configure(source, context);
        doSslRequest();
        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
    }

    /**
     * Factory of SSL-enabled client channels
     * Copied from Avro's org.apache.avro.ipc.TestNettyServerWithSSL test
     */
    private static class SSLChannelFactory extends NioClientSocketChannelFactory {
        public SSLChannelFactory() {
            super(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        }

        @Override
        public SocketChannel newChannel(ChannelPipeline pipeline) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{ new TestAvroSource.PermissiveTrustManager() }, null);
                SSLEngine sslEngine = sslContext.createSSLEngine();
                sslEngine.setUseClientMode(true);
                // addFirst() will make SSL handling the first stage of decoding
                // and the last stage of encoding
                pipeline.addFirst("ssl", new SslHandler(sslEngine));
                return super.newChannel(pipeline);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot create SSL channel", ex);
            }
        }
    }

    /**
     * Bogus trust manager accepting any certificate
     */
    private static class PermissiveTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] certs, String s) {
            // nothing
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String s) {
            // nothing
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    @Test
    public void testValidIpFilterAllows() throws IOException, InterruptedException {
        doIpFilterTest(localhost, "allow:name:localhost,deny:ip:*", true, false);
        doIpFilterTest(localhost, (("allow:ip:" + (localhost.getHostAddress())) + ",deny:ip:*"), true, false);
        doIpFilterTest(localhost, "allow:ip:*", true, false);
        doIpFilterTest(localhost, (("allow:ip:" + (localhost.getHostAddress().substring(0, 3))) + "*,deny:ip:*"), true, false);
        doIpFilterTest(localhost, (("allow:ip:127.0.0.2,allow:ip:" + (localhost.getHostAddress().substring(0, 3))) + "*,deny:ip:*"), true, false);
        doIpFilterTest(localhost, "allow:name:localhost,deny:ip:*", true, true);
        doIpFilterTest(localhost, "allow:ip:*", true, true);
    }

    @Test
    public void testValidIpFilterDenys() throws IOException, InterruptedException {
        doIpFilterTest(localhost, "deny:ip:*", false, false);
        doIpFilterTest(localhost, "deny:name:localhost", false, false);
        doIpFilterTest(localhost, (("deny:ip:" + (localhost.getHostAddress())) + ",allow:ip:*"), false, false);
        doIpFilterTest(localhost, "deny:ip:*", false, false);
        doIpFilterTest(localhost, "allow:ip:45.2.2.2,deny:ip:*", false, false);
        doIpFilterTest(localhost, (("deny:ip:" + (localhost.getHostAddress().substring(0, 3))) + "*,allow:ip:*"), false, false);
        doIpFilterTest(localhost, "deny:ip:*", false, true);
    }

    @Test
    public void testInvalidIpFilter() throws IOException, InterruptedException {
        doIpFilterTest(localhost, "deny:ip:*", false, false);
        doIpFilterTest(localhost, "allow:name:localhost", true, false);
        doIpFilterTest(localhost, ("deny:ip:127.0.0.2,allow:ip:*,deny:ip:" + (localhost.getHostAddress())), true, false);
        doIpFilterTest(localhost, (("deny:ip:" + (localhost.getHostAddress().substring(0, 3))) + "*,allow:ip:*"), false, false);
        // Private lambda expression to check the received FlumeException within this test
        Consumer<Exception> exceptionChecker = (Exception ex) -> {
            TestAvroSource.logger.info("Received an expected exception", ex);
            // covers all ipFilter related exceptions
            Assert.assertTrue("Expected an ipFilterRules related exception", ex.getMessage().contains("ipFilter"));
        };
        try {
            doIpFilterTest(localhost, null, false, false);
            Assert.fail("The null ipFilterRules config should have thrown an exception.");
        } catch (FlumeException e) {
            exceptionChecker.accept(e);
        }
        try {
            doIpFilterTest(localhost, "", true, false);
            Assert.fail(("The empty string ipFilterRules config should have thrown " + "an exception"));
        } catch (FlumeException e) {
            exceptionChecker.accept(e);
        }
        try {
            doIpFilterTest(localhost, "homer:ip:45.4.23.1", true, false);
            Assert.fail("Bad ipFilterRules config should have thrown an exception.");
        } catch (FlumeException e) {
            exceptionChecker.accept(e);
        }
        try {
            doIpFilterTest(localhost, "allow:sleeps:45.4.23.1", true, false);
            Assert.fail("Bad ipFilterRules config should have thrown an exception.");
        } catch (FlumeException e) {
            exceptionChecker.accept(e);
        }
    }

    @Test
    public void testErrorCounterChannelWriteFail() throws Exception {
        Context context = new Context();
        context.put("port", String.valueOf((selectedPort = TestAvroSource.getFreePort())));
        context.put("bind", "0.0.0.0");
        source.configure(context);
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new ChannelException("dummy")).when(cp).processEvent(ArgumentMatchers.any(Event.class));
        Mockito.doThrow(new ChannelException("dummy")).when(cp).processEventBatch(ArgumentMatchers.anyListOf(Event.class));
        source.setChannelProcessor(cp);
        source.start();
        AvroFlumeEvent avroEvent = new AvroFlumeEvent();
        avroEvent.setHeaders(new HashMap<CharSequence, CharSequence>());
        avroEvent.setBody(ByteBuffer.wrap("Hello avro ssl".getBytes()));
        source.append(avroEvent);
        source.appendBatch(Arrays.asList(avroEvent));
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(source, "sourceCounter")));
        Assert.assertEquals(2, sc.getChannelWriteFail());
        source.stop();
    }
}

