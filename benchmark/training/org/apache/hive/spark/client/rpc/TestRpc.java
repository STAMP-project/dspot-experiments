/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.spark.client.rpc;


import HiveConf.ConfVars.SPARK_RPC_CHANNEL_LOG_LEVEL.varname;
import HiveConf.ConfVars.SPARK_RPC_SERVER_PORT;
import Rpc.SASL_AUTH_CONF;
import RpcServer.YarnApplicationStateFinder;
import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import java.io.Closeable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.security.sasl.SaslException;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hive.common.ServerUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static RpcConfiguration.RPC_SASL_OPT_PREFIX;


public class TestRpc {
    private static final Logger LOG = LoggerFactory.getLogger(TestRpc.class);

    private Collection<Closeable> closeables;

    private static final Map<String, String> emptyConfig = ImmutableMap.of(varname, "DEBUG");

    private static final int RETRY_ACQUIRE_PORT_COUNT = 10;

    private HiveConf hiveConf;

    @Test
    public void testRpcDispatcher() throws Exception {
        Rpc serverRpc = autoClose(Rpc.createEmbedded(new TestRpc.TestDispatcher()));
        Rpc clientRpc = autoClose(Rpc.createEmbedded(new TestRpc.TestDispatcher()));
        TestRpc.TestMessage outbound = new TestRpc.TestMessage("Hello World!");
        Future<TestRpc.TestMessage> call = clientRpc.call(outbound, TestRpc.TestMessage.class);
        TestRpc.LOG.debug("Transferring messages...");
        transfer(serverRpc, clientRpc);
        TestRpc.TestMessage reply = call.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(outbound.message, reply.message);
    }

    @Test
    public void testClientServer() throws Exception {
        RpcServer server = autoClose(new RpcServer(TestRpc.emptyConfig, hiveConf));
        Rpc[] rpcs = createRpcConnection(server);
        Rpc serverRpc = rpcs[0];
        Rpc client = rpcs[1];
        TestRpc.TestMessage outbound = new TestRpc.TestMessage("Hello World!");
        Future<TestRpc.TestMessage> call = client.call(outbound, TestRpc.TestMessage.class);
        TestRpc.TestMessage reply = call.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(outbound.message, reply.message);
        TestRpc.TestMessage another = new TestRpc.TestMessage("Hello again!");
        Future<TestRpc.TestMessage> anotherCall = client.call(another, TestRpc.TestMessage.class);
        TestRpc.TestMessage anotherReply = anotherCall.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(another.message, anotherReply.message);
        String errorMsg = "This is an error.";
        try {
            client.call(new TestRpc.ErrorCall(errorMsg)).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException ee) {
            Assert.assertTrue(((ee.getCause()) instanceof RpcException));
            Assert.assertTrue(((ee.getCause().getMessage().indexOf(errorMsg)) >= 0));
        }
        // Test from server to client too.
        TestRpc.TestMessage serverMsg = new TestRpc.TestMessage("Hello from the server!");
        Future<TestRpc.TestMessage> serverCall = serverRpc.call(serverMsg, TestRpc.TestMessage.class);
        TestRpc.TestMessage serverReply = serverCall.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(serverMsg.message, serverReply.message);
    }

    @Test
    public void testServerAddress() throws Exception {
        String hostAddress = InetAddress.getLocalHost().getHostName();
        Map<String, String> config = new HashMap<String, String>();
        // Test if rpc_server_address is configured
        config.put(HiveConf.ConfVars.SPARK_RPC_SERVER_ADDRESS.varname, hostAddress);
        RpcServer server1 = autoClose(new RpcServer(config, hiveConf));
        Assert.assertTrue("Host address should match the expected one", ((server1.getAddress()) == hostAddress));
        // Test if rpc_server_address is not configured but HS2 server host is configured
        config.put(HiveConf.ConfVars.SPARK_RPC_SERVER_ADDRESS.varname, "");
        config.put(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_BIND_HOST.varname, hostAddress);
        RpcServer server2 = autoClose(new RpcServer(config, hiveConf));
        Assert.assertTrue("Host address should match the expected one", ((server2.getAddress()) == hostAddress));
        // Test if both are not configured
        config.put(HiveConf.ConfVars.SPARK_RPC_SERVER_ADDRESS.varname, "");
        config.put(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_BIND_HOST.varname, "");
        RpcServer server3 = autoClose(new RpcServer(config, hiveConf));
        Assert.assertTrue("Host address should match the expected one", ((server3.getAddress()) == (InetAddress.getLocalHost().getHostName())));
    }

    @Test
    public void testBadHello() throws Exception {
        RpcServer server = autoClose(new RpcServer(TestRpc.emptyConfig, hiveConf));
        Future<Rpc> serverRpcFuture = server.registerClient("client", "newClient", new TestRpc.TestDispatcher());
        NioEventLoopGroup eloop = new NioEventLoopGroup();
        Future<Rpc> clientRpcFuture = Rpc.createClient(TestRpc.emptyConfig, eloop, "localhost", server.getPort(), "client", "wrongClient", new TestRpc.TestDispatcher());
        try {
            autoClose(clientRpcFuture.get(10, TimeUnit.SECONDS));
            Assert.fail("Should have failed to create client with wrong secret.");
        } catch (ExecutionException ee) {
            // On failure, the SASL handler will throw an exception indicating that the SASL
            // negotiation failed.
            Assert.assertTrue(("Unexpected exception: " + (ee.getCause())), ((ee.getCause()) instanceof SaslException));
        }
        serverRpcFuture.cancel(true);
    }

    @Test
    public void testServerPort() throws Exception {
        Map<String, String> config = new HashMap<String, String>();
        RpcServer server0 = new RpcServer(config, hiveConf);
        Assert.assertTrue(("Empty port range should return a random valid port: " + (server0.getPort())), ((server0.getPort()) >= 0));
        IOUtils.closeQuietly(server0);
        config.put(HiveConf.ConfVars.SPARK_RPC_SERVER_PORT.varname, "49152-49222,49223,49224-49333");
        RpcServer server1 = new RpcServer(config, hiveConf);
        Assert.assertTrue(("Port should be within configured port range:" + (server1.getPort())), (((server1.getPort()) >= 49152) && ((server1.getPort()) <= 49333)));
        IOUtils.closeQuietly(server1);
        int expectedPort = ServerUtils.findFreePort();
        RpcServer server2 = null;
        for (int i = 0; i < (TestRpc.RETRY_ACQUIRE_PORT_COUNT); i++) {
            try {
                config.put(HiveConf.ConfVars.SPARK_RPC_SERVER_PORT.varname, String.valueOf(expectedPort));
                server2 = new RpcServer(config, hiveConf);
                break;
            } catch (Exception e) {
                TestRpc.LOG.debug(((("Error while connecting to port " + expectedPort) + " retrying: ") + (e.getMessage())));
                expectedPort = ServerUtils.findFreePort();
            }
        }
        Assert.assertNotNull("Unable to create RpcServer with any attempted port", server2);
        Assert.assertEquals(("Port should match configured one: " + (server2.getPort())), expectedPort, server2.getPort());
        IOUtils.closeQuietly(server2);
        config.put(HiveConf.ConfVars.SPARK_RPC_SERVER_PORT.varname, "49552-49222,49223,49224-49333");
        try {
            autoClose(new RpcServer(config, hiveConf));
            Assert.assertTrue("Invalid port range should throw an exception", false);// Should not reach here

        } catch (IllegalArgumentException e) {
            Assert.assertEquals(("Malformed configuration value for " + (SPARK_RPC_SERVER_PORT.varname)), e.getMessage());
        }
        // Retry logic
        expectedPort = ServerUtils.findFreePort();
        RpcServer server3 = null;
        for (int i = 0; i < (TestRpc.RETRY_ACQUIRE_PORT_COUNT); i++) {
            try {
                config.put(HiveConf.ConfVars.SPARK_RPC_SERVER_PORT.varname, ((String.valueOf(expectedPort)) + ",21-23"));
                server3 = new RpcServer(config, hiveConf);
                break;
            } catch (Exception e) {
                TestRpc.LOG.debug((("Error while connecting to port " + expectedPort) + " retrying"));
                expectedPort = ServerUtils.findFreePort();
            }
        }
        Assert.assertNotNull("Unable to create RpcServer with any attempted port", server3);
        Assert.assertEquals(("Port should match configured one:" + (server3.getPort())), expectedPort, server3.getPort());
        IOUtils.closeQuietly(server3);
    }

    @Test
    public void testCloseListener() throws Exception {
        RpcServer server = autoClose(new RpcServer(TestRpc.emptyConfig, hiveConf));
        Rpc[] rpcs = createRpcConnection(server);
        Rpc client = rpcs[1];
        final AtomicInteger closeCount = new AtomicInteger();
        client.addListener(new Rpc.Listener() {
            @Override
            public void rpcClosed(Rpc rpc) {
                closeCount.incrementAndGet();
            }
        });
        client.close();
        client.close();
        Assert.assertEquals(1, closeCount.get());
    }

    @Test
    public void testNotDeserializableRpc() throws Exception {
        RpcServer server = autoClose(new RpcServer(TestRpc.emptyConfig, hiveConf));
        Rpc[] rpcs = createRpcConnection(server);
        Rpc client = rpcs[1];
        try {
            client.call(new TestRpc.NotDeserializable(42)).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException ee) {
            Assert.assertTrue(((ee.getCause()) instanceof RpcException));
            Assert.assertTrue(((ee.getCause().getMessage().indexOf("KryoException")) >= 0));
        }
    }

    @Test
    public void testEncryption() throws Exception {
        Map<String, String> eConf = ImmutableMap.<String, String>builder().putAll(TestRpc.emptyConfig).put(((RPC_SASL_OPT_PREFIX) + "qop"), SASL_AUTH_CONF).build();
        RpcServer server = autoClose(new RpcServer(eConf, hiveConf));
        Rpc[] rpcs = createRpcConnection(server, eConf, null);
        Rpc client = rpcs[1];
        TestRpc.TestMessage outbound = new TestRpc.TestMessage("Hello World!");
        Future<TestRpc.TestMessage> call = client.call(outbound, TestRpc.TestMessage.class);
        TestRpc.TestMessage reply = call.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(outbound.message, reply.message);
    }

    @Test
    public void testClientTimeout() throws Exception {
        Map<String, String> conf = ImmutableMap.<String, String>builder().putAll(TestRpc.emptyConfig).build();
        RpcServer server = autoClose(new RpcServer(conf, hiveConf));
        String secret = server.createSecret();
        try {
            autoClose(server.registerClient("client", secret, new TestRpc.TestDispatcher(), 1L).get());
            Assert.fail("Server should have timed out client.");
        } catch (ExecutionException ee) {
            Assert.assertTrue(((ee.getCause()) instanceof TimeoutException));
        }
        NioEventLoopGroup eloop = new NioEventLoopGroup();
        Future<Rpc> clientRpcFuture = Rpc.createClient(conf, eloop, "localhost", server.getPort(), "client", secret, new TestRpc.TestDispatcher());
        try {
            autoClose(clientRpcFuture.get());
            Assert.fail("Client should have failed to connect to server.");
        } catch (ExecutionException ee) {
            // Error should not be a timeout.
            Assert.assertFalse(((ee.getCause()) instanceof TimeoutException));
        }
    }

    static class MockYarnApplicationStateFinder extends RpcServer.YarnApplicationStateFinder {
        private int count = 0;

        public boolean isApplicationAccepted(HiveConf conf, String applicationId) {
            return ((count)++) < 10;
        }
    }

    /**
     * Tests that we don't timeout with a short timeout but the spark application isn't running.
     */
    @Test
    public void testExtendClientTimeout() throws Exception {
        Map<String, String> conf = ImmutableMap.<String, String>builder().putAll(TestRpc.emptyConfig).build();
        RpcServer server = autoClose(new RpcServer(conf, hiveConf));
        String secret = server.createSecret();
        TestRpc.MockYarnApplicationStateFinder yarnApplicationStateFinder = new TestRpc.MockYarnApplicationStateFinder();
        Future<Rpc> promise = server.registerClient("client", secret, new TestRpc.TestDispatcher(), 2L, yarnApplicationStateFinder);
        Assert.assertFalse(promise.isDone());
        Thread.sleep(50);
        try {
            promise.get();
            Assert.fail("Server should have timed out client.");
        } catch (ExecutionException ee) {
            Assert.assertTrue(((ee.getCause()) instanceof TimeoutException));
        }
        NioEventLoopGroup eloop = new NioEventLoopGroup();
        Future<Rpc> clientRpcFuture = Rpc.createClient(conf, eloop, "localhost", server.getPort(), "client", secret, new TestRpc.TestDispatcher());
        try {
            autoClose(clientRpcFuture.get());
            Assert.fail("Client should have failed to connect to server.");
        } catch (ExecutionException ee) {
            // Error should not be a timeout.
            Assert.assertFalse(((ee.getCause()) instanceof TimeoutException));
        }
    }

    @Test
    public void testRpcServerMultiThread() throws Exception {
        final RpcServer server = autoClose(new RpcServer(TestRpc.emptyConfig, hiveConf));
        final String msg = "Hello World!";
        Callable<String> callable = () -> {
            Rpc[] rpcs = createRpcConnection(server, TestRpc.emptyConfig, UUID.randomUUID().toString());
            Rpc rpc;
            if (ThreadLocalRandom.current().nextBoolean()) {
                rpc = rpcs[0];
            } else {
                rpc = rpcs[1];
            }
            TestRpc.TestMessage outbound = new TestRpc.TestMessage("Hello World!");
            Future<TestRpc.TestMessage> call = rpc.call(outbound, TestRpc.TestMessage.class);
            TestRpc.TestMessage reply = call.get(10, TimeUnit.SECONDS);
            return reply.message;
        };
        final int numThreads = (ThreadLocalRandom.current().nextInt(5)) + 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<java.util.concurrent.Future<String>> futures = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(callable));
        }
        executor.shutdown();
        for (java.util.concurrent.Future<String> future : futures) {
            Assert.assertEquals(msg, future.get());
        }
    }

    private static class TestMessage {
        final String message;

        public TestMessage() {
            this(null);
        }

        public TestMessage(String message) {
            this.message = message;
        }
    }

    private static class ErrorCall {
        final String error;

        public ErrorCall() {
            this(null);
        }

        public ErrorCall(String error) {
            this.error = error;
        }
    }

    private static class NotDeserializable {
        NotDeserializable(int unused) {
        }
    }

    private static class TestDispatcher extends RpcDispatcher {
        protected TestRpc.TestMessage handle(ChannelHandlerContext ctx, TestRpc.TestMessage msg) {
            return msg;
        }

        protected void handle(ChannelHandlerContext ctx, TestRpc.ErrorCall msg) {
            throw new IllegalArgumentException(msg.error);
        }

        protected void handle(ChannelHandlerContext ctx, TestRpc.NotDeserializable msg) {
            // No op. Shouldn't actually be called, if it is, the test will fail.
        }
    }
}

