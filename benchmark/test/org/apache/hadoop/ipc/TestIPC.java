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
package org.apache.hadoop.ipc;


import AuthenticationMethod.KERBEROS;
import CommonConfigurationKeys.IPC_CLIENT_CONNECTION_IDLESCANINTERVAL_KEY;
import CommonConfigurationKeys.IPC_MAXIMUM_RESPONSE_LENGTH;
import CommonConfigurationKeysPublic.IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY;
import CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY;
import CommonConfigurationKeysPublic.IPC_CLIENT_IDLETHRESHOLD_KEY;
import CommonConfigurationKeysPublic.IPC_CLIENT_KILL_MAX_KEY;
import RetryPolicies.RETRY_FOREVER;
import RetryPolicies.TRY_ONCE_THEN_FAIL;
import Server.LOG;
import Server.RECEIVED_HTTP_REQ_RESPONSE;
import com.google.common.base.Supplier;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.SocketFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.retry.FailoverProxyProvider;
import org.apache.hadoop.io.retry.Idempotent;
import org.apache.hadoop.io.retry.RetryPolicies;
import org.apache.hadoop.io.retry.RetryProxy;
import org.apache.hadoop.ipc.Client.ConnectionId;
import org.apache.hadoop.ipc.RPC.RpcKind;
import org.apache.hadoop.ipc.Server.Call;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos.RpcResponseHeaderProto;
import org.apache.hadoop.net.ConnectTimeoutException;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static RpcConstants.CURRENT_VERSION;
import static RpcConstants.INVALID_CALL_ID;
import static RpcConstants.INVALID_RETRY_COUNT;


/**
 * Unit tests for IPC.
 */
public class TestIPC {
    public static final Logger LOG = LoggerFactory.getLogger(TestIPC.class);

    private static Configuration conf;

    static final int PING_INTERVAL = 1000;

    private static final int MIN_SLEEP_TIME = 1000;

    /**
     * Flag used to turn off the fault injection behavior
     * of the various writables.
     */
    static boolean WRITABLE_FAULTS_ENABLED = true;

    static int WRITABLE_FAULTS_SLEEP = 0;

    static final Random RANDOM = new Random();

    private static final String ADDRESS = "0.0.0.0";

    /**
     * Directory where we can count open file descriptors on Linux
     */
    private static final File FD_DIR = new File("/proc/self/fd");

    static class TestServer extends Server {
        // Tests can set callListener to run a piece of code each time the server
        // receives a call.  This code executes on the server thread, so it has
        // visibility of that thread's thread-local storage.
        Runnable callListener;

        private boolean sleep;

        private Class<? extends Writable> responseClass;

        public TestServer(int handlerCount, boolean sleep) throws IOException {
            this(handlerCount, sleep, LongWritable.class, null);
        }

        public TestServer(int port, int handlerCount, boolean sleep) throws IOException {
            this(port, handlerCount, sleep, LongWritable.class, null);
        }

        public TestServer(int handlerCount, boolean sleep, Configuration conf) throws IOException {
            this(handlerCount, sleep, LongWritable.class, null, conf);
        }

        public TestServer(int handlerCount, boolean sleep, Class<? extends Writable> paramClass, Class<? extends Writable> responseClass) throws IOException {
            this(handlerCount, sleep, paramClass, responseClass, TestIPC.conf);
        }

        public TestServer(int port, int handlerCount, boolean sleep, Class<? extends Writable> paramClass, Class<? extends Writable> responseClass) throws IOException {
            this(port, handlerCount, sleep, paramClass, responseClass, TestIPC.conf);
        }

        public TestServer(int handlerCount, boolean sleep, Class<? extends Writable> paramClass, Class<? extends Writable> responseClass, Configuration conf) throws IOException {
            this(0, handlerCount, sleep, paramClass, responseClass, conf);
        }

        public TestServer(int port, int handlerCount, boolean sleep, Class<? extends Writable> paramClass, Class<? extends Writable> responseClass, Configuration conf) throws IOException {
            super(TestIPC.ADDRESS, port, paramClass, handlerCount, conf);
            this.sleep = sleep;
            this.responseClass = responseClass;
        }

        @Override
        public Writable call(RPC.RpcKind rpcKind, String protocol, Writable param, long receiveTime) throws IOException {
            if (sleep) {
                // sleep a bit
                try {
                    Thread.sleep(((TestIPC.RANDOM.nextInt(TestIPC.PING_INTERVAL)) + (TestIPC.MIN_SLEEP_TIME)));
                } catch (InterruptedException e) {
                }
            }
            if ((callListener) != null) {
                callListener.run();
            }
            if ((responseClass) != null) {
                try {
                    return responseClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                return param;// echo param as result

            }
        }
    }

    private static class SerialCaller extends Thread {
        private Client client;

        private InetSocketAddress server;

        private int count;

        private boolean failed;

        public SerialCaller(Client client, InetSocketAddress server, int count) {
            this.client = client;
            this.server = server;
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 0; i < (count); i++) {
                try {
                    final long param = TestIPC.RANDOM.nextLong();
                    LongWritable value = TestIPC.call(client, param, server, TestIPC.conf);
                    if ((value.get()) != param) {
                        TestIPC.LOG.error("Call failed!");
                        failed = true;
                        break;
                    }
                } catch (Exception e) {
                    TestIPC.LOG.error(("Caught: " + (StringUtils.stringifyException(e))));
                    failed = true;
                }
            }
        }
    }

    /**
     * A RpcInvocationHandler instance for test. Its invoke function uses the same
     * {@link Client} instance, and will fail the first totalRetry times (by
     * throwing an IOException).
     */
    private static class TestInvocationHandler implements RpcInvocationHandler {
        private static int retry = 0;

        private final Client client;

        private final Server server;

        private final int total;

        TestInvocationHandler(Client client, Server server, int total) {
            this.client = client;
            this.server = server;
            this.total = total;
        }

        protected Object returnValue(Object value) throws Exception {
            if (((TestIPC.TestInvocationHandler.retry)++) < (total)) {
                throw new IOException("Fake IOException");
            }
            return value;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            LongWritable value = TestIPC.call(client, TestIPC.RANDOM.nextLong(), NetUtils.getConnectAddress(server), TestIPC.conf);
            return returnValue(value);
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public ConnectionId getConnectionId() {
            return null;
        }
    }

    private static class TestInvalidTokenHandler extends TestIPC.TestInvocationHandler {
        private int invocations = 0;

        TestInvalidTokenHandler(Client client, Server server) {
            super(client, server, 1);
        }

        @Override
        protected Object returnValue(Object value) throws Exception {
            throw new InvalidToken("Invalid Token");
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            (invocations)++;
            return super.invoke(proxy, method, args);
        }
    }

    @Test(timeout = 60000)
    public void testSerial() throws IOException, InterruptedException {
        internalTestSerial(3, false, 2, 5, 100);
        internalTestSerial(3, true, 2, 5, 10);
    }

    @Test
    public void testAuxiliaryPorts() throws IOException, InterruptedException {
        int defaultPort = 9000;
        int[] auxiliaryPorts = new int[]{ 9001, 9002, 9003 };
        final int handlerCount = 5;
        final boolean handlerSleep = false;
        Server server = new TestIPC.TestServer(defaultPort, handlerCount, handlerSleep);
        for (int port : auxiliaryPorts) {
            server.addAuxiliaryListener(port);
        }
        Set<InetSocketAddress> listenerAddrs = server.getAuxiliaryListenerAddresses();
        Set<InetSocketAddress> addrs = new HashSet<>();
        for (InetSocketAddress addr : listenerAddrs) {
            addrs.add(NetUtils.getConnectAddress(addr));
        }
        server.start();
        Client client = new Client(LongWritable.class, TestIPC.conf);
        Set<TestIPC.SerialCaller> calls = new HashSet<>();
        for (InetSocketAddress addr : addrs) {
            calls.add(new TestIPC.SerialCaller(client, addr, 100));
        }
        for (TestIPC.SerialCaller caller : calls) {
            caller.join();
            Assert.assertFalse(caller.failed);
        }
        client.stop();
        server.stop();
    }

    @Test(timeout = 60000)
    public void testStandAloneClient() throws IOException {
        Client client = new Client(LongWritable.class, TestIPC.conf);
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 10);
        try {
            TestIPC.call(client, TestIPC.RANDOM.nextLong(), address, TestIPC.conf);
            Assert.fail("Expected an exception to have been thrown");
        } catch (IOException e) {
            String message = e.getMessage();
            String addressText = ((address.getHostName()) + ":") + (address.getPort());
            Assert.assertTrue(((("Did not find " + addressText) + " in ") + message), message.contains(addressText));
            Throwable cause = e.getCause();
            Assert.assertNotNull(("No nested exception in " + e), cause);
            String causeText = cause.getMessage();
            Assert.assertTrue(((("Did not find " + causeText) + " in ") + message), message.contains(causeText));
        } finally {
            client.stop();
        }
    }

    @SuppressWarnings("unused")
    private static class IOEOnReadWritable extends LongWritable {
        public IOEOnReadWritable() {
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            super.readFields(in);
            TestIPC.maybeThrowIOE();
        }
    }

    @SuppressWarnings("unused")
    private static class RTEOnReadWritable extends LongWritable {
        public RTEOnReadWritable() {
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            super.readFields(in);
            TestIPC.maybeThrowRTE();
        }
    }

    @SuppressWarnings("unused")
    private static class IOEOnWriteWritable extends LongWritable {
        public IOEOnWriteWritable() {
        }

        @Override
        public void write(DataOutput out) throws IOException {
            super.write(out);
            TestIPC.maybeThrowIOE();
        }
    }

    @SuppressWarnings("unused")
    private static class RTEOnWriteWritable extends LongWritable {
        public RTEOnWriteWritable() {
        }

        @Override
        public void write(DataOutput out) throws IOException {
            super.write(out);
            TestIPC.maybeThrowRTE();
        }
    }

    @Test(timeout = 60000)
    public void testIOEOnClientWriteParam() throws Exception {
        doErrorTest(TestIPC.IOEOnWriteWritable.class, LongWritable.class, LongWritable.class, LongWritable.class);
    }

    @Test(timeout = 60000)
    public void testRTEOnClientWriteParam() throws Exception {
        doErrorTest(TestIPC.RTEOnWriteWritable.class, LongWritable.class, LongWritable.class, LongWritable.class);
    }

    @Test(timeout = 60000)
    public void testIOEOnServerReadParam() throws Exception {
        doErrorTest(LongWritable.class, TestIPC.IOEOnReadWritable.class, LongWritable.class, LongWritable.class);
    }

    @Test(timeout = 60000)
    public void testRTEOnServerReadParam() throws Exception {
        doErrorTest(LongWritable.class, TestIPC.RTEOnReadWritable.class, LongWritable.class, LongWritable.class);
    }

    @Test(timeout = 60000)
    public void testIOEOnServerWriteResponse() throws Exception {
        doErrorTest(LongWritable.class, LongWritable.class, TestIPC.IOEOnWriteWritable.class, LongWritable.class);
    }

    @Test(timeout = 60000)
    public void testRTEOnServerWriteResponse() throws Exception {
        doErrorTest(LongWritable.class, LongWritable.class, TestIPC.RTEOnWriteWritable.class, LongWritable.class);
    }

    @Test(timeout = 60000)
    public void testIOEOnClientReadResponse() throws Exception {
        doErrorTest(LongWritable.class, LongWritable.class, LongWritable.class, TestIPC.IOEOnReadWritable.class);
    }

    @Test(timeout = 60000)
    public void testRTEOnClientReadResponse() throws Exception {
        doErrorTest(LongWritable.class, LongWritable.class, LongWritable.class, TestIPC.RTEOnReadWritable.class);
    }

    /**
     * Test case that fails a write, but only after taking enough time
     * that a ping should have been sent. This is a reproducer for a
     * deadlock seen in one iteration of HADOOP-6762.
     */
    @Test(timeout = 60000)
    public void testIOEOnWriteAfterPingClient() throws Exception {
        // start server
        Client.setPingInterval(TestIPC.conf, 100);
        try {
            TestIPC.WRITABLE_FAULTS_SLEEP = 1000;
            doErrorTest(TestIPC.IOEOnWriteWritable.class, LongWritable.class, LongWritable.class, LongWritable.class);
        } finally {
            TestIPC.WRITABLE_FAULTS_SLEEP = 0;
        }
    }

    /**
     * Test that, if the socket factory throws an IOE, it properly propagates
     * to the client.
     */
    @Test(timeout = 60000)
    public void testSocketFactoryException() throws IOException {
        SocketFactory mockFactory = Mockito.mock(SocketFactory.class);
        Mockito.doThrow(new IOException("Injected fault")).when(mockFactory).createSocket();
        Client client = new Client(LongWritable.class, TestIPC.conf, mockFactory);
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 10);
        try {
            TestIPC.call(client, TestIPC.RANDOM.nextLong(), address, TestIPC.conf);
            Assert.fail("Expected an exception to have been thrown");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Injected fault"));
        } finally {
            client.stop();
        }
    }

    /**
     * Mock socket class to help inject an exception for HADOOP-7428.
     */
    static class MockSocket extends Socket {
        @Override
        public synchronized void setSoTimeout(int timeout) {
            throw new RuntimeException("Injected fault");
        }
    }

    /**
     * Test that, if a RuntimeException is thrown after creating a socket
     * but before successfully connecting to the IPC server, that the
     * failure is handled properly. This is a regression test for
     * HADOOP-7428.
     */
    @Test(timeout = 60000)
    public void testRTEDuringConnectionSetup() throws IOException {
        // Set up a socket factory which returns sockets which
        // throw an RTE when setSoTimeout is called.
        SocketFactory spyFactory = Mockito.spy(NetUtils.getDefaultSocketFactory(TestIPC.conf));
        Mockito.doAnswer(new Answer<Socket>() {
            @Override
            public Socket answer(InvocationOnMock invocation) {
                return new TestIPC.MockSocket();
            }
        }).when(spyFactory).createSocket();
        Server server = new TestIPC.TestServer(1, true);
        Client client = new Client(LongWritable.class, TestIPC.conf, spyFactory);
        server.start();
        try {
            // Call should fail due to injected exception.
            InetSocketAddress address = NetUtils.getConnectAddress(server);
            try {
                TestIPC.call(client, TestIPC.RANDOM.nextLong(), address, TestIPC.conf);
                Assert.fail("Expected an exception to have been thrown");
            } catch (Exception e) {
                TestIPC.LOG.info("caught expected exception", e);
                Assert.assertTrue(StringUtils.stringifyException(e).contains("Injected fault"));
            }
            // Resetting to the normal socket behavior should succeed
            // (i.e. it should not have cached a half-constructed connection)
            Mockito.reset(spyFactory);
            TestIPC.call(client, TestIPC.RANDOM.nextLong(), address, TestIPC.conf);
        } finally {
            client.stop();
            server.stop();
        }
    }

    @Test(timeout = 60000)
    public void testIpcTimeout() throws IOException {
        // start server
        Server server = new TestIPC.TestServer(1, true);
        InetSocketAddress addr = NetUtils.getConnectAddress(server);
        server.start();
        // start client
        Client client = new Client(LongWritable.class, TestIPC.conf);
        // set timeout to be less than MIN_SLEEP_TIME
        try {
            TestIPC.call(client, new LongWritable(TestIPC.RANDOM.nextLong()), addr, ((TestIPC.MIN_SLEEP_TIME) / 2), TestIPC.conf);
            Assert.fail("Expected an exception to have been thrown");
        } catch (SocketTimeoutException e) {
            TestIPC.LOG.info("Get a SocketTimeoutException ", e);
        }
        // set timeout to be bigger than 3*ping interval
        TestIPC.call(client, new LongWritable(TestIPC.RANDOM.nextLong()), addr, ((3 * (TestIPC.PING_INTERVAL)) + (TestIPC.MIN_SLEEP_TIME)), TestIPC.conf);
        client.stop();
    }

    @Test(timeout = 60000)
    public void testIpcConnectTimeout() throws IOException {
        // start server
        Server server = new TestIPC.TestServer(1, true);
        InetSocketAddress addr = NetUtils.getConnectAddress(server);
        // Intentionally do not start server to get a connection timeout
        // start client
        Client.setConnectTimeout(TestIPC.conf, 100);
        Client client = new Client(LongWritable.class, TestIPC.conf);
        // set the rpc timeout to twice the MIN_SLEEP_TIME
        try {
            TestIPC.call(client, new LongWritable(TestIPC.RANDOM.nextLong()), addr, ((TestIPC.MIN_SLEEP_TIME) * 2), TestIPC.conf);
            Assert.fail("Expected an exception to have been thrown");
        } catch (SocketTimeoutException e) {
            TestIPC.LOG.info("Get a SocketTimeoutException ", e);
        }
        client.stop();
    }

    /**
     * Check service class byte in IPC header is correct on wire.
     */
    @Test(timeout = 60000)
    public void testIpcWithServiceClass() throws IOException {
        // start server
        Server server = new TestIPC.TestServer(5, false);
        InetSocketAddress addr = NetUtils.getConnectAddress(server);
        server.start();
        // start client
        Client.setConnectTimeout(TestIPC.conf, 10000);
        TestIPC.callAndVerify(server, addr, 0, true);
        // Service Class is low to -128 as byte on wire.
        // -128 shouldn't be casted on wire but -129 should.
        TestIPC.callAndVerify(server, addr, (-128), true);
        TestIPC.callAndVerify(server, addr, (-129), false);
        // Service Class is up to 127.
        // 127 shouldn't be casted on wire but 128 should.
        TestIPC.callAndVerify(server, addr, 127, true);
        TestIPC.callAndVerify(server, addr, 128, false);
        server.stop();
    }

    private static class TestServerQueue extends Server {
        final CountDownLatch firstCallLatch = new CountDownLatch(1);

        final CountDownLatch callBlockLatch = new CountDownLatch(1);

        TestServerQueue(int expectedCalls, int readers, int callQ, int handlers, Configuration conf) throws IOException {
            super(TestIPC.ADDRESS, 0, LongWritable.class, handlers, readers, callQ, conf, null, null);
        }

        @Override
        public Writable call(RPC.RpcKind rpcKind, String protocol, Writable param, long receiveTime) throws IOException {
            firstCallLatch.countDown();
            try {
                callBlockLatch.await();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            return param;
        }
    }

    /**
     * Check that reader queueing works
     *
     * @throws BrokenBarrierException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 60000)
    public void testIpcWithReaderQueuing() throws Exception {
        // 1 reader, 1 connectionQ slot, 1 callq
        for (int i = 0; i < 10; i++) {
            checkBlocking(1, 1, 1);
        }
        // 4 readers, 5 connectionQ slots, 2 callq
        for (int i = 0; i < 10; i++) {
            checkBlocking(4, 5, 2);
        }
    }

    @Test(timeout = 30000)
    public void testConnectionIdleTimeouts() throws Exception {
        GenericTestUtils.setLogLevel(Server.LOG, Level.DEBUG);
        final int maxIdle = 1000;
        final int cleanupInterval = (maxIdle * 3) / 4;// stagger cleanups

        final int killMax = 3;
        final int clients = 1 + (killMax * 2);// 1 to block, 2 batches to kill

        TestIPC.conf.setInt(IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY, maxIdle);
        TestIPC.conf.setInt(IPC_CLIENT_IDLETHRESHOLD_KEY, 0);
        TestIPC.conf.setInt(IPC_CLIENT_KILL_MAX_KEY, killMax);
        TestIPC.conf.setInt(IPC_CLIENT_CONNECTION_IDLESCANINTERVAL_KEY, cleanupInterval);
        final CyclicBarrier firstCallBarrier = new CyclicBarrier(2);
        final CyclicBarrier callBarrier = new CyclicBarrier(clients);
        final CountDownLatch allCallLatch = new CountDownLatch(clients);
        final AtomicBoolean error = new AtomicBoolean();
        final TestIPC.TestServer server = new TestIPC.TestServer(clients, false);
        Thread[] threads = new Thread[clients];
        try {
            server.callListener = new Runnable() {
                AtomicBoolean first = new AtomicBoolean(true);

                @Override
                public void run() {
                    try {
                        allCallLatch.countDown();
                        // block first call
                        if (first.compareAndSet(true, false)) {
                            firstCallBarrier.await();
                        } else {
                            callBarrier.await();
                        }
                    } catch (Throwable t) {
                        TestIPC.LOG.error(t.toString());
                        error.set(true);
                    }
                }
            };
            start();
            // start client
            final CountDownLatch callReturned = new CountDownLatch((clients - 1));
            final InetSocketAddress addr = NetUtils.getConnectAddress(server);
            final Configuration clientConf = new Configuration();
            clientConf.setInt(IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY, 10000);
            for (int i = 0; i < clients; i++) {
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Client client = new Client(LongWritable.class, clientConf);
                        try {
                            TestIPC.call(client, Thread.currentThread().getId(), addr, clientConf);
                            callReturned.countDown();
                            Thread.sleep(10000);
                        } catch (IOException e) {
                            TestIPC.LOG.error(e.toString());
                        } catch (InterruptedException e) {
                        } finally {
                            client.stop();
                        }
                    }
                });
                threads[i].start();
            }
            // all calls blocked in handler so all connections made
            allCallLatch.await();
            Assert.assertFalse(error.get());
            Assert.assertEquals(clients, getNumOpenConnections());
            // wake up blocked calls and wait for client call to return, no
            // connections should have closed
            callBarrier.await();
            callReturned.await();
            Assert.assertEquals(clients, getNumOpenConnections());
            // server won't close till maxIdle*2, so give scanning thread time to
            // be almost ready to close idle connection.  after which it should
            // close max connections on every cleanupInterval
            Thread.sleep(((maxIdle * 2) - cleanupInterval));
            for (int i = clients; i > 1; i -= killMax) {
                Thread.sleep(cleanupInterval);
                Assert.assertFalse(error.get());
                Assert.assertEquals(i, getNumOpenConnections());
            }
            // connection for the first blocked call should still be open
            Thread.sleep(cleanupInterval);
            Assert.assertFalse(error.get());
            Assert.assertEquals(1, getNumOpenConnections());
            // wake up call and ensure connection times out
            firstCallBarrier.await();
            Thread.sleep((maxIdle * 2));
            Assert.assertFalse(error.get());
            Assert.assertEquals(0, getNumOpenConnections());
        } finally {
            for (Thread t : threads) {
                if (t != null) {
                    t.interrupt();
                    t.join();
                }
                stop();
            }
        }
    }

    @Test(timeout = 30000, expected = IOException.class)
    public void testIpcAfterStopping() throws IOException {
        // start server
        Server server = new TestIPC.TestServer(5, false);
        InetSocketAddress addr = NetUtils.getConnectAddress(server);
        server.start();
        // start client
        Client client = new Client(LongWritable.class, TestIPC.conf);
        TestIPC.call(client, addr, 0, TestIPC.conf);
        client.stop();
        // This call should throw IOException.
        TestIPC.call(client, addr, 0, TestIPC.conf);
    }

    /**
     * Check that file descriptors aren't leaked by starting
     * and stopping IPC servers.
     */
    @Test(timeout = 60000)
    public void testSocketLeak() throws IOException {
        Assume.assumeTrue(TestIPC.FD_DIR.exists());// only run on Linux

        long startFds = countOpenFileDescriptors();
        for (int i = 0; i < 50; i++) {
            Server server = new TestIPC.TestServer(1, true);
            server.start();
            server.stop();
        }
        long endFds = countOpenFileDescriptors();
        Assert.assertTrue((("Leaked " + (endFds - startFds)) + " file descriptors"), ((endFds - startFds) < 20));
    }

    /**
     * Check if Client is interrupted after handling
     * InterruptedException during cleanup
     */
    @Test(timeout = 30000)
    public void testInterrupted() {
        Client client = new Client(LongWritable.class, TestIPC.conf);
        Client.getClientExecutor().submit(new Runnable() {
            public void run() {
                while (true);
            }
        });
        Thread.currentThread().interrupt();
        client.stop();
        try {
            Assert.assertTrue(Thread.currentThread().isInterrupted());
            TestIPC.LOG.info("Expected thread interrupt during client cleanup");
        } catch (AssertionError e) {
            TestIPC.LOG.error("The Client did not interrupt after handling an Interrupted Exception");
            Assert.fail("The Client did not interrupt after handling an Interrupted Exception");
        }
        // Clear Thread interrupt
        Thread.interrupted();
    }

    @Test(timeout = 60000)
    public void testIpcFromHadoop_0_18_13() throws IOException {
        doIpcVersionTest(TestIPC.NetworkTraces.HADOOP_0_18_3_RPC_DUMP, TestIPC.NetworkTraces.RESPONSE_TO_HADOOP_0_18_3_RPC);
    }

    @Test(timeout = 60000)
    public void testIpcFromHadoop0_20_3() throws IOException {
        doIpcVersionTest(TestIPC.NetworkTraces.HADOOP_0_20_3_RPC_DUMP, TestIPC.NetworkTraces.RESPONSE_TO_HADOOP_0_20_3_RPC);
    }

    @Test(timeout = 60000)
    public void testIpcFromHadoop0_21_0() throws IOException {
        doIpcVersionTest(TestIPC.NetworkTraces.HADOOP_0_21_0_RPC_DUMP, TestIPC.NetworkTraces.RESPONSE_TO_HADOOP_0_21_0_RPC);
    }

    @Test(timeout = 60000)
    public void testHttpGetResponse() throws IOException {
        doIpcVersionTest("GET / HTTP/1.0\r\n\r\n".getBytes(), RECEIVED_HTTP_REQ_RESPONSE.getBytes());
    }

    @Test(timeout = 60000)
    public void testConnectionRetriesOnSocketTimeoutExceptions() throws IOException {
        Configuration conf = new Configuration();
        // set max retries to 0
        conf.setInt(IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY, 0);
        assertRetriesOnSocketTimeouts(conf, 1);
        // set max retries to 3
        conf.setInt(IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY, 3);
        assertRetriesOnSocketTimeouts(conf, 4);
    }

    static class CallInfo {
        int id = INVALID_CALL_ID;

        int retry = INVALID_RETRY_COUNT;
    }

    /**
     * Test if
     * (1) the rpc server uses the call id/retry provided by the rpc client, and
     * (2) the rpc client receives the same call id/retry from the rpc server.
     */
    @Test(timeout = 60000)
    public void testCallIdAndRetry() throws IOException {
        final TestIPC.CallInfo info = new TestIPC.CallInfo();
        // Override client to store the call info and check response
        final Client client = new Client(LongWritable.class, TestIPC.conf) {
            @Override
            Call createCall(RpcKind rpcKind, Writable rpcRequest) {
                final Call call = super.createCall(rpcKind, rpcRequest);
                info.id = call.id;
                info.retry = call.retry;
                return call;
            }

            @Override
            void checkResponse(RpcResponseHeaderProto header) throws IOException {
                super.checkResponse(header);
                Assert.assertEquals(info.id, header.getCallId());
                Assert.assertEquals(info.retry, header.getRetryCount());
            }
        };
        // Attach a listener that tracks every call received by the server.
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false);
        server.callListener = new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(info.id, Server.getCallId());
                Assert.assertEquals(info.retry, Server.getCallRetryCount());
            }
        };
        try {
            InetSocketAddress addr = NetUtils.getConnectAddress(server);
            start();
            final TestIPC.SerialCaller caller = new TestIPC.SerialCaller(client, addr, 10);
            caller.run();
            Assert.assertFalse(caller.failed);
        } finally {
            client.stop();
            stop();
        }
    }

    /**
     * A dummy protocol
     */
    interface DummyProtocol {
        @Idempotent
        public void dummyRun() throws IOException;
    }

    /**
     * Test the retry count while used in a retry proxy.
     */
    @Test(timeout = 60000)
    public void testRetryProxy() throws IOException {
        final Client client = new Client(LongWritable.class, TestIPC.conf);
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false);
        server.callListener = new Runnable() {
            private int retryCount = 0;

            @Override
            public void run() {
                Assert.assertEquals(((retryCount)++), Server.getCallRetryCount());
            }
        };
        // try more times, so it is easier to find race condition bug
        // 10000 times runs about 6s on a core i7 machine
        final int totalRetry = 10000;
        TestIPC.DummyProtocol proxy = ((TestIPC.DummyProtocol) (Proxy.newProxyInstance(TestIPC.DummyProtocol.class.getClassLoader(), new Class[]{ TestIPC.DummyProtocol.class }, new TestIPC.TestInvocationHandler(client, server, totalRetry))));
        TestIPC.DummyProtocol retryProxy = ((TestIPC.DummyProtocol) (RetryProxy.create(TestIPC.DummyProtocol.class, proxy, RETRY_FOREVER)));
        try {
            start();
            retryProxy.dummyRun();
            Assert.assertEquals(TestIPC.TestInvocationHandler.retry, (totalRetry + 1));
        } finally {
            Client.setCallIdAndRetryCount(0, 0, null);
            client.stop();
            stop();
        }
    }

    /**
     * Test that there is no retry when invalid token exception is thrown.
     * Verfies fix for HADOOP-12054
     */
    @Test(expected = InvalidToken.class)
    public void testNoRetryOnInvalidToken() throws IOException {
        final Client client = new Client(LongWritable.class, TestIPC.conf);
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false);
        TestIPC.TestInvalidTokenHandler handler = new TestIPC.TestInvalidTokenHandler(client, server);
        TestIPC.DummyProtocol proxy = ((TestIPC.DummyProtocol) (Proxy.newProxyInstance(TestIPC.DummyProtocol.class.getClassLoader(), new Class[]{ TestIPC.DummyProtocol.class }, handler)));
        FailoverProxyProvider<TestIPC.DummyProtocol> provider = new org.apache.hadoop.io.retry.DefaultFailoverProxyProvider<TestIPC.DummyProtocol>(TestIPC.DummyProtocol.class, proxy);
        TestIPC.DummyProtocol retryProxy = ((TestIPC.DummyProtocol) (RetryProxy.create(TestIPC.DummyProtocol.class, provider, RetryPolicies.failoverOnNetworkException(TRY_ONCE_THEN_FAIL, 100, 100, 10000, 0))));
        try {
            start();
            retryProxy.dummyRun();
        } finally {
            // Check if dummyRun called only once
            Assert.assertEquals(handler.invocations, 1);
            Client.setCallIdAndRetryCount(0, 0, null);
            client.stop();
            stop();
        }
    }

    /**
     * Test if the rpc server gets the default retry count (0) from client.
     */
    @Test(timeout = 60000)
    public void testInitialCallRetryCount() throws IOException {
        // Override client to store the call id
        final Client client = new Client(LongWritable.class, TestIPC.conf);
        // Attach a listener that tracks every call ID received by the server.
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false);
        server.callListener = new Runnable() {
            @Override
            public void run() {
                // we have not set the retry count for the client, thus on the server
                // side we should see retry count as 0
                Assert.assertEquals(0, Server.getCallRetryCount());
            }
        };
        try {
            InetSocketAddress addr = NetUtils.getConnectAddress(server);
            start();
            final TestIPC.SerialCaller caller = new TestIPC.SerialCaller(client, addr, 10);
            caller.run();
            Assert.assertFalse(caller.failed);
        } finally {
            client.stop();
            stop();
        }
    }

    /**
     * Test if the rpc server gets the retry count from client.
     */
    @Test(timeout = 60000)
    public void testCallRetryCount() throws IOException {
        final int retryCount = 255;
        // Override client to store the call id
        final Client client = new Client(LongWritable.class, TestIPC.conf);
        Client.setCallIdAndRetryCount(Client.nextCallId(), 255, null);
        // Attach a listener that tracks every call ID received by the server.
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false);
        server.callListener = new Runnable() {
            @Override
            public void run() {
                // we have not set the retry count for the client, thus on the server
                // side we should see retry count as 0
                Assert.assertEquals(retryCount, Server.getCallRetryCount());
            }
        };
        try {
            InetSocketAddress addr = NetUtils.getConnectAddress(server);
            start();
            final TestIPC.SerialCaller caller = new TestIPC.SerialCaller(client, addr, 10);
            caller.run();
            Assert.assertFalse(caller.failed);
        } finally {
            client.stop();
            stop();
        }
    }

    /**
     * Tests that client generates a unique sequential call ID for each RPC call,
     * even if multiple threads are using the same client.
     *
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 60000)
    public void testUniqueSequentialCallIds() throws IOException, InterruptedException {
        int serverThreads = 10;
        int callerCount = 100;
        int perCallerCallCount = 100;
        TestIPC.TestServer server = new TestIPC.TestServer(serverThreads, false);
        // Attach a listener that tracks every call ID received by the server.  This
        // list must be synchronized, because multiple server threads will add to it.
        final List<Integer> callIds = Collections.synchronizedList(new ArrayList<Integer>());
        server.callListener = new Runnable() {
            @Override
            public void run() {
                callIds.add(Server.getCallId());
            }
        };
        Client client = new Client(LongWritable.class, TestIPC.conf);
        try {
            InetSocketAddress addr = NetUtils.getConnectAddress(server);
            start();
            TestIPC.SerialCaller[] callers = new TestIPC.SerialCaller[callerCount];
            for (int i = 0; i < callerCount; ++i) {
                callers[i] = new TestIPC.SerialCaller(client, addr, perCallerCallCount);
                callers[i].start();
            }
            for (int i = 0; i < callerCount; ++i) {
                callers[i].join();
                Assert.assertFalse(callers[i].failed);
            }
        } finally {
            client.stop();
            stop();
        }
        int expectedCallCount = callerCount * perCallerCallCount;
        Assert.assertEquals(expectedCallCount, callIds.size());
        // It is not guaranteed that the server executes requests in sequential order
        // of client call ID, so we must sort the call IDs before checking that it
        // contains every expected value.
        Collections.sort(callIds);
        final int startID = callIds.get(0).intValue();
        for (int i = 0; i < expectedCallCount; ++i) {
            Assert.assertEquals((startID + i), callIds.get(i).intValue());
        }
    }

    @Test
    public void testMaxConnections() throws Exception {
        TestIPC.conf.setInt("ipc.server.max.connections", 6);
        Server server = null;
        Thread[] connectors = new Thread[10];
        try {
            server = new TestIPC.TestServer(3, false);
            final InetSocketAddress addr = NetUtils.getConnectAddress(server);
            server.start();
            Assert.assertEquals(0, server.getNumOpenConnections());
            for (int i = 0; i < 10; i++) {
                connectors[i] = new Thread() {
                    @Override
                    public void run() {
                        Socket sock = null;
                        try {
                            sock = NetUtils.getDefaultSocketFactory(TestIPC.conf).createSocket();
                            NetUtils.connect(sock, addr, 3000);
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException ie) {
                            }
                        } catch (IOException ioe) {
                        } finally {
                            if (sock != null) {
                                try {
                                    sock.close();
                                } catch (IOException ioe) {
                                }
                            }
                        }
                    }
                };
                connectors[i].start();
            }
            Thread.sleep(1000);
            // server should only accept up to 6 connections
            Assert.assertEquals(6, server.getNumOpenConnections());
            // server should drop the other 4 connections
            Assert.assertEquals(4, server.getNumDroppedConnections());
            for (int i = 0; i < 10; i++) {
                connectors[i].join();
            }
        } finally {
            if (server != null) {
                server.stop();
            }
            TestIPC.conf.setInt("ipc.server.max.connections", 0);
        }
    }

    @Test
    public void testClientGetTimeout() throws IOException {
        Configuration config = new Configuration();
        Assert.assertEquals(Client.getTimeout(config), (-1));
    }

    @Test(timeout = 60000)
    public void testSetupConnectionShouldNotBlockShutdown() throws Exception {
        // Start server
        SocketFactory mockFactory = Mockito.mock(SocketFactory.class);
        Server server = new TestIPC.TestServer(1, true);
        final InetSocketAddress addr = NetUtils.getConnectAddress(server);
        // Track how many times we retried to set up the connection
        final AtomicInteger createSocketCalled = new AtomicInteger();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                createSocketCalled.addAndGet(1);
                Thread.sleep(((TestIPC.MIN_SLEEP_TIME) * 5));
                throw new ConnectTimeoutException("fake");
            }
        }).when(mockFactory).createSocket();
        final Client client = new Client(LongWritable.class, TestIPC.conf, mockFactory);
        final AtomicBoolean callStarted = new AtomicBoolean(false);
        // Call a random function asynchronously so that we can call stop()
        new Thread(new Runnable() {
            public void run() {
                try {
                    callStarted.set(true);
                    TestIPC.call(client, TestIPC.RANDOM.nextLong(), addr, TestIPC.conf);
                } catch (IOException ignored) {
                }
            }
        }).start();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (callStarted.get()) && ((createSocketCalled.get()) == 1);
            }
        }, 50, 60000);
        // stop() should stop the client immediately without any more retries
        client.stop();
        Assert.assertEquals(1, createSocketCalled.get());
    }

    @Test(timeout = 4000)
    public void testInsecureVersionMismatch() throws IOException {
        checkVersionMismatch();
    }

    @Test(timeout = 4000)
    public void testSecureVersionMismatch() throws IOException {
        SecurityUtil.setAuthenticationMethod(KERBEROS, TestIPC.conf);
        UserGroupInformation.setConfiguration(TestIPC.conf);
        checkVersionMismatch();
    }

    @Test
    public void testRpcResponseLimit() throws Throwable {
        Server server = new TestIPC.TestServer(1, false);
        InetSocketAddress addr = NetUtils.getConnectAddress(server);
        server.start();
        TestIPC.conf.setInt(IPC_MAXIMUM_RESPONSE_LENGTH, 0);
        Client client = new Client(LongWritable.class, TestIPC.conf);
        TestIPC.call(client, 0, addr, TestIPC.conf);
        TestIPC.conf.setInt(IPC_MAXIMUM_RESPONSE_LENGTH, 4);
        client = new Client(LongWritable.class, TestIPC.conf);
        try {
            TestIPC.call(client, 0, addr, TestIPC.conf);
        } catch (IOException ioe) {
            Throwable t = ioe.getCause();
            Assert.assertNotNull(t);
            Assert.assertEquals(RpcException.class, t.getClass());
            Assert.assertEquals("RPC response exceeds maximum data length", t.getMessage());
            return;
        }
        Assert.fail("didn't get limit exceeded");
    }

    @Test
    public void testUserBinding() throws Exception {
        checkUserBinding(false);
    }

    @Test
    public void testProxyUserBinding() throws Exception {
        checkUserBinding(true);
    }

    // dummy protocol that claims to support kerberos.
    @KerberosInfo(serverPrincipal = "server@REALM")
    private static class TestBindingProtocol {}

    /**
     * Wireshark traces collected from various client versions. These enable
     * us to test that old versions of the IPC stack will receive the correct
     * responses so that they will throw a meaningful error message back
     * to the user.
     */
    private abstract static class NetworkTraces {
        /**
         * Wireshark dump of an RPC request from Hadoop 0.18.3
         */
        static final byte[] HADOOP_0_18_3_RPC_DUMP = TestIPC.hexDumpToBytes(("68 72 70 63 02 00 00 00  82 00 1d 6f 72 67 2e 61 hrpc.... ...org.a\n" + (((((((((((((("70 61 63 68 65 2e 68 61  64 6f 6f 70 2e 69 6f 2e pache.ha doop.io.\n" + "57 72 69 74 61 62 6c 65  00 30 6f 72 67 2e 61 70 Writable .0org.ap\n") + "61 63 68 65 2e 68 61 64  6f 6f 70 2e 69 6f 2e 4f ache.had oop.io.O\n") + "62 6a 65 63 74 57 72 69  74 61 62 6c 65 24 4e 75 bjectWri table$Nu\n") + "6c 6c 49 6e 73 74 61 6e  63 65 00 2f 6f 72 67 2e llInstan ce./org.\n") + "61 70 61 63 68 65 2e 68  61 64 6f 6f 70 2e 73 65 apache.h adoop.se\n") + "63 75 72 69 74 79 2e 55  73 65 72 47 72 6f 75 70 curity.U serGroup\n") + "49 6e 66 6f 72 6d 61 74  69 6f 6e 00 00 00 6c 00 Informat ion...l.\n") + "00 00 00 00 12 67 65 74  50 72 6f 74 6f 63 6f 6c .....get Protocol\n") + "56 65 72 73 69 6f 6e 00  00 00 02 00 10 6a 61 76 Version. .....jav\n") + "61 2e 6c 61 6e 67 2e 53  74 72 69 6e 67 00 2e 6f a.lang.S tring..o\n") + "72 67 2e 61 70 61 63 68  65 2e 68 61 64 6f 6f 70 rg.apach e.hadoop\n") + "2e 6d 61 70 72 65 64 2e  4a 6f 62 53 75 62 6d 69 .mapred. JobSubmi\n") + "73 73 69 6f 6e 50 72 6f  74 6f 63 6f 6c 00 04 6c ssionPro tocol..l\n") + "6f 6e 67 00 00 00 00 00  00 00 0a                ong..... ...     \n")));

        static final String HADOOP0_18_ERROR_MSG = ("Server IPC version " + (CURRENT_VERSION)) + " cannot communicate with client version 2";

        /**
         * Wireshark dump of the correct response that triggers an error message
         * on an 0.18.3 client.
         */
        static final byte[] RESPONSE_TO_HADOOP_0_18_3_RPC = Bytes.concat(TestIPC.hexDumpToBytes(("00 00 00 00 01 00 00 00  29 6f 72 67 2e 61 70 61 ........ )org.apa\n" + (("63 68 65 2e 68 61 64 6f  6f 70 2e 69 70 63 2e 52 che.hado op.ipc.R\n" + "50 43 24 56 65 72 73 69  6f 6e 4d 69 73 6d 61 74 PC$Versi onMismat\n") + "63 68                                            ch               \n"))), Ints.toByteArray(TestIPC.NetworkTraces.HADOOP0_18_ERROR_MSG.length()), TestIPC.NetworkTraces.HADOOP0_18_ERROR_MSG.getBytes());

        /**
         * Wireshark dump of an RPC request from Hadoop 0.20.3
         */
        static final byte[] HADOOP_0_20_3_RPC_DUMP = TestIPC.hexDumpToBytes(("68 72 70 63 03 00 00 00  79 27 6f 72 67 2e 61 70 hrpc.... y\'org.ap\n" + (((((((((((((("61 63 68 65 2e 68 61 64  6f 6f 70 2e 69 70 63 2e ache.had oop.ipc.\n" + "56 65 72 73 69 6f 6e 65  64 50 72 6f 74 6f 63 6f Versione dProtoco\n") + "6c 01 0a 53 54 52 49 4e  47 5f 55 47 49 04 74 6f l..STRIN G_UGI.to\n") + "64 64 09 04 74 6f 64 64  03 61 64 6d 07 64 69 61 dd..todd .adm.dia\n") + "6c 6f 75 74 05 63 64 72  6f 6d 07 70 6c 75 67 64 lout.cdr om.plugd\n") + "65 76 07 6c 70 61 64 6d  69 6e 05 61 64 6d 69 6e ev.lpadm in.admin\n") + "0a 73 61 6d 62 61 73 68  61 72 65 06 6d 72 74 65 .sambash are.mrte\n") + "73 74 00 00 00 6c 00 00  00 00 00 12 67 65 74 50 st...l.. ....getP\n") + "72 6f 74 6f 63 6f 6c 56  65 72 73 69 6f 6e 00 00 rotocolV ersion..\n") + "00 02 00 10 6a 61 76 61  2e 6c 61 6e 67 2e 53 74 ....java .lang.St\n") + "72 69 6e 67 00 2e 6f 72  67 2e 61 70 61 63 68 65 ring..or g.apache\n") + "2e 68 61 64 6f 6f 70 2e  6d 61 70 72 65 64 2e 4a .hadoop. mapred.J\n") + "6f 62 53 75 62 6d 69 73  73 69 6f 6e 50 72 6f 74 obSubmis sionProt\n") + "6f 63 6f 6c 00 04 6c 6f  6e 67 00 00 00 00 00 00 ocol..lo ng......\n") + "00 14                                            ..               \n")));

        static final String HADOOP0_20_ERROR_MSG = ("Server IPC version " + (CURRENT_VERSION)) + " cannot communicate with client version 3";

        static final byte[] RESPONSE_TO_HADOOP_0_20_3_RPC = Bytes.concat(TestIPC.hexDumpToBytes(("ff ff ff ff ff ff ff ff  00 00 00 29 6f 72 67 2e ........ ...)org.\n" + (("61 70 61 63 68 65 2e 68  61 64 6f 6f 70 2e 69 70 apache.h adoop.ip\n" + "63 2e 52 50 43 24 56 65  72 73 69 6f 6e 4d 69 73 c.RPC$Ve rsionMis\n") + "6d 61 74 63 68                                   match            \n"))), Ints.toByteArray(TestIPC.NetworkTraces.HADOOP0_20_ERROR_MSG.length()), TestIPC.NetworkTraces.HADOOP0_20_ERROR_MSG.getBytes());

        static final String HADOOP0_21_ERROR_MSG = ("Server IPC version " + (CURRENT_VERSION)) + " cannot communicate with client version 4";

        static final byte[] HADOOP_0_21_0_RPC_DUMP = TestIPC.hexDumpToBytes(("68 72 70 63 04 50                                hrpc.P" + // in 0.21 it comes in two separate TCP packets
        ((((((((((("00 00 00 3c 33 6f 72 67  2e 61 70 61 63 68 65 2e ...<3org .apache.\n" + "68 61 64 6f 6f 70 2e 6d  61 70 72 65 64 75 63 65 hadoop.m apreduce\n") + "2e 70 72 6f 74 6f 63 6f  6c 2e 43 6c 69 65 6e 74 .protoco l.Client\n") + "50 72 6f 74 6f 63 6f 6c  01 00 04 74 6f 64 64 00 Protocol ...todd.\n") + "00 00 00 71 00 00 00 00  00 12 67 65 74 50 72 6f ...q.... ..getPro\n") + "74 6f 63 6f 6c 56 65 72  73 69 6f 6e 00 00 00 02 tocolVer sion....\n") + "00 10 6a 61 76 61 2e 6c  61 6e 67 2e 53 74 72 69 ..java.l ang.Stri\n") + "6e 67 00 33 6f 72 67 2e  61 70 61 63 68 65 2e 68 ng.3org. apache.h\n") + "61 64 6f 6f 70 2e 6d 61  70 72 65 64 75 63 65 2e adoop.ma preduce.\n") + "70 72 6f 74 6f 63 6f 6c  2e 43 6c 69 65 6e 74 50 protocol .ClientP\n") + "72 6f 74 6f 63 6f 6c 00  04 6c 6f 6e 67 00 00 00 rotocol. .long...\n") + "00 00 00 00 21                                   ....!            \n")));

        static final byte[] RESPONSE_TO_HADOOP_0_21_0_RPC = Bytes.concat(TestIPC.hexDumpToBytes(("ff ff ff ff ff ff ff ff  00 00 00 29 6f 72 67 2e ........ ...)org.\n" + (("61 70 61 63 68 65 2e 68  61 64 6f 6f 70 2e 69 70 apache.h adoop.ip\n" + "63 2e 52 50 43 24 56 65  72 73 69 6f 6e 4d 69 73 c.RPC$Ve rsionMis\n") + "6d 61 74 63 68                                   match            \n"))), Ints.toByteArray(TestIPC.NetworkTraces.HADOOP0_21_ERROR_MSG.length()), TestIPC.NetworkTraces.HADOOP0_21_ERROR_MSG.getBytes());
    }
}

