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
import CommonConfigurationKeys.HADOOP_SECURITY_AUTHORIZATION;
import CommonConfigurationKeys.IPC_CLIENT_CONNECT_MAX_RETRIES_KEY;
import CommonConfigurationKeys.IPC_CLIENT_PING_KEY;
import CommonConfigurationKeys.IPC_CLIENT_RPC_TIMEOUT_KEY;
import CommonConfigurationKeys.IPC_PING_INTERVAL_KEY;
import CommonConfigurationKeys.IPC_SERVER_HANDLER_QUEUE_SIZE_DEFAULT;
import CommonConfigurationKeys.IPC_SERVER_HANDLER_QUEUE_SIZE_KEY;
import CommonConfigurationKeys.IPC_SERVER_RPC_READ_THREADS_DEFAULT;
import CommonConfigurationKeys.IPC_SERVER_RPC_READ_THREADS_KEY;
import CommonConfigurationKeys.RPC_METRICS_PERCENTILES_INTERVALS_KEY;
import CommonConfigurationKeys.RPC_METRICS_QUANTILE_ENABLE;
import DecayRpcScheduler.LOG;
import RPC.Builder;
import RetryPolicies.RETRY_FOREVER;
import RpcErrorCodeProto.FATAL_UNAUTHORIZED;
import TestProtos.AddRequestProto;
import TestProtos.AddResponseProto;
import TestProtos.ExchangeRequestProto;
import TestProtos.ExchangeResponseProto;
import com.google.common.base.Supplier;
import com.google.protobuf.ServiceException;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.SocketFactory;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.io.retry.RetryProxy;
import org.apache.hadoop.ipc.Client.ConnectionId;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.ipc.Server.Call;
import org.apache.hadoop.ipc.Server.Connection;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto;
import org.apache.hadoop.ipc.protobuf.TestProtos;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.security.authorize.Service;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.hadoop.test.MockitoUtil;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.mockito.ArgumentMatchers.any;


/**
 * Unit tests for RPC.
 */
@SuppressWarnings("deprecation")
public class TestRPC extends TestRpcBase {
    public static final Logger LOG = LoggerFactory.getLogger(TestRPC.class);

    int datasize = 1024 * 100;

    int numThreads = 50;

    public interface TestProtocol extends VersionedProtocol {
        long versionID = 1L;

        void ping() throws IOException;

        void sleep(long delay) throws IOException, InterruptedException;

        String echo(String value) throws IOException;

        String[] echo(String[] value) throws IOException;

        Writable echo(Writable value) throws IOException;

        int add(int v1, int v2) throws IOException;

        int add(int[] values) throws IOException;

        int error() throws IOException;
    }

    public static class TestImpl implements TestRPC.TestProtocol {
        int fastPingCounter = 0;

        @Override
        public long getProtocolVersion(String protocol, long clientVersion) {
            return TestRPC.TestProtocol.versionID;
        }

        @Override
        public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int hashcode) {
            return new ProtocolSignature(TestRPC.TestProtocol.versionID, null);
        }

        @Override
        public void ping() {
        }

        @Override
        public void sleep(long delay) throws InterruptedException {
            Thread.sleep(delay);
        }

        @Override
        public String echo(String value) throws IOException {
            return value;
        }

        @Override
        public String[] echo(String[] values) throws IOException {
            return values;
        }

        @Override
        public Writable echo(Writable writable) {
            return writable;
        }

        @Override
        public int add(int v1, int v2) {
            return v1 + v2;
        }

        @Override
        public int add(int[] values) {
            int sum = 0;
            for (int i = 0; i < (values.length); i++) {
                sum += values[i];
            }
            return sum;
        }

        @Override
        public int error() throws IOException {
            throw new IOException("bobo");
        }
    }

    // 
    // an object that does a bunch of transactions
    // 
    static class Transactions implements Runnable {
        int datasize;

        TestRpcBase.TestRpcService proxy;

        Transactions(TestRpcBase.TestRpcService proxy, int datasize) {
            this.proxy = proxy;
            this.datasize = datasize;
        }

        // do two RPC that transfers data.
        @Override
        public void run() {
            Integer[] indata = new Integer[datasize];
            Arrays.fill(indata, 123);
            TestProtos.ExchangeRequestProto exchangeRequest = ExchangeRequestProto.newBuilder().addAllValues(Arrays.asList(indata)).build();
            Integer[] outdata = null;
            TestProtos.ExchangeResponseProto exchangeResponse;
            TestProtos.AddRequestProto addRequest = AddRequestProto.newBuilder().setParam1(1).setParam2(2).build();
            TestProtos.AddResponseProto addResponse;
            int val = 0;
            try {
                exchangeResponse = proxy.exchange(null, exchangeRequest);
                outdata = new Integer[exchangeResponse.getValuesCount()];
                outdata = exchangeResponse.getValuesList().toArray(outdata);
                addResponse = proxy.add(null, addRequest);
                val = addResponse.getResult();
            } catch (ServiceException e) {
                Assert.assertTrue(("Exception from RPC exchange() " + e), false);
            }
            Assert.assertEquals(indata.length, outdata.length);
            Assert.assertEquals(3, val);
            for (int i = 0; i < (outdata.length); i++) {
                Assert.assertEquals(outdata[i].intValue(), i);
            }
        }
    }

    // 
    // A class that does an RPC but does not read its response.
    // 
    static class SlowRPC implements Runnable {
        private TestRpcBase.TestRpcService proxy;

        private volatile boolean done;

        SlowRPC(TestRpcBase.TestRpcService proxy) {
            this.proxy = proxy;
            done = false;
        }

        boolean isDone() {
            return done;
        }

        @Override
        public void run() {
            try {
                // this would hang until two fast pings happened
                ping(true);
                done = true;
            } catch (ServiceException e) {
                Assert.assertTrue(("SlowRPC ping exception " + e), false);
            }
        }

        void ping(boolean shouldSlow) throws ServiceException {
            // this would hang until two fast pings happened
            proxy.slowPing(null, TestRpcBase.newSlowPingRequest(shouldSlow));
        }
    }

    /**
     * A basic interface for testing client-side RPC resource cleanup.
     */
    private interface StoppedProtocol {
        long versionID = 0;

        void stop();
    }

    /**
     * A class used for testing cleanup of client side RPC resources.
     */
    private static class StoppedRpcEngine implements RpcEngine {
        @Override
        public <T> ProtocolProxy<T> getProxy(Class<T> protocol, long clientVersion, InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory, int rpcTimeout, RetryPolicy connectionRetryPolicy) throws IOException {
            return getProxy(protocol, clientVersion, addr, ticket, conf, factory, rpcTimeout, connectionRetryPolicy, null, null);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> ProtocolProxy<T> getProxy(Class<T> protocol, long clientVersion, InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory, int rpcTimeout, RetryPolicy connectionRetryPolicy, AtomicBoolean fallbackToSimpleAuth, AlignmentContext alignmentContext) throws IOException {
            T proxy = ((T) (Proxy.newProxyInstance(protocol.getClassLoader(), new Class[]{ protocol }, new TestRPC.StoppedInvocationHandler())));
            return new ProtocolProxy<T>(protocol, proxy, false);
        }

        @Override
        public Server getServer(Class<?> protocol, Object instance, String bindAddress, int port, int numHandlers, int numReaders, int queueSizePerHandler, boolean verbose, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, String portRangeConfig, AlignmentContext alignmentContext) throws IOException {
            return null;
        }

        @Override
        public ProtocolProxy<ProtocolMetaInfoPB> getProtocolMetaInfoProxy(ConnectionId connId, Configuration conf, SocketFactory factory) throws IOException {
            throw new UnsupportedOperationException("This proxy is not supported");
        }
    }

    /**
     * An invocation handler which does nothing when invoking methods, and just
     * counts the number of times close() is called.
     */
    private static class StoppedInvocationHandler implements Closeable , InvocationHandler {
        private int closeCalled = 0;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }

        @Override
        public void close() throws IOException {
            (closeCalled)++;
        }

        public int getCloseCalled() {
            return closeCalled;
        }
    }

    @Test
    public void testConfRpc() throws IOException {
        Server server = TestRpcBase.newServerBuilder(TestRpcBase.conf).setNumHandlers(1).setVerbose(false).build();
        // Just one handler
        int confQ = TestRpcBase.conf.getInt(IPC_SERVER_HANDLER_QUEUE_SIZE_KEY, IPC_SERVER_HANDLER_QUEUE_SIZE_DEFAULT);
        Assert.assertEquals(confQ, server.getMaxQueueSize());
        int confReaders = TestRpcBase.conf.getInt(IPC_SERVER_RPC_READ_THREADS_KEY, IPC_SERVER_RPC_READ_THREADS_DEFAULT);
        Assert.assertEquals(confReaders, server.getNumReaders());
        server = TestRpcBase.newServerBuilder(TestRpcBase.conf).setNumHandlers(1).setnumReaders(3).setQueueSizePerHandler(200).setVerbose(false).build();
        Assert.assertEquals(3, server.getNumReaders());
        Assert.assertEquals(200, server.getMaxQueueSize());
        server = TestRpcBase.newServerBuilder(TestRpcBase.conf).setQueueSizePerHandler(10).setNumHandlers(2).setVerbose(false).build();
        Assert.assertEquals((2 * 10), server.getMaxQueueSize());
    }

    @Test
    public void testProxyAddress() throws Exception {
        Server server = null;
        TestRpcBase.TestRpcService proxy = null;
        try {
            server = TestRpcBase.setupTestServer(TestRpcBase.conf, (-1));
            // create a client
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            Assert.assertEquals(TestRpcBase.addr, RPC.getServerAddress(proxy));
        } finally {
            TestRpcBase.stop(server, proxy);
        }
    }

    @Test
    public void testSlowRpc() throws ServiceException, IOException {
        Server server;
        TestRpcBase.TestRpcService proxy = null;
        System.out.println("Testing Slow RPC");
        // create a server with two handlers
        server = TestRpcBase.setupTestServer(TestRpcBase.conf, 2);
        try {
            // create a client
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            TestRPC.SlowRPC slowrpc = new TestRPC.SlowRPC(proxy);
            Thread thread = new Thread(slowrpc, "SlowRPC");
            thread.start();// send a slow RPC, which won't return until two fast pings

            Assert.assertTrue("Slow RPC should not have finished1.", (!(slowrpc.isDone())));
            slowrpc.ping(false);// first fast ping

            // verify that the first RPC is still stuck
            Assert.assertTrue("Slow RPC should not have finished2.", (!(slowrpc.isDone())));
            slowrpc.ping(false);// second fast ping

            // Now the slow ping should be able to be executed
            while (!(slowrpc.isDone())) {
                System.out.println("Waiting for slow RPC to get done.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            } 
        } finally {
            System.out.println("Down slow rpc testing");
            TestRpcBase.stop(server, proxy);
        }
    }

    @Test
    public void testCalls() throws Exception {
        testCallsInternal(TestRpcBase.conf);
    }

    @Test
    public void testClientWithoutServer() throws Exception {
        TestRpcBase.TestRpcService proxy;
        short invalidPort = 20;
        InetSocketAddress invalidAddress = new InetSocketAddress(TestRpcBase.ADDRESS, invalidPort);
        long invalidClientVersion = 1L;
        try {
            proxy = RPC.getProxy(TestRpcBase.TestRpcService.class, invalidClientVersion, invalidAddress, TestRpcBase.conf);
            // Test echo method
            proxy.echo(null, TestRpcBase.newEchoRequest("hello"));
            Assert.fail("We should not have reached here");
        } catch (ServiceException ioe) {
            // this is what we expected
            if (!((ioe.getCause()) instanceof ConnectException)) {
                Assert.fail("We should not have reached here");
            }
        }
    }

    private static final String ACL_CONFIG = "test.protocol.acl";

    private static class TestPolicyProvider extends PolicyProvider {
        @Override
        public Service[] getServices() {
            return new Service[]{ new Service(TestRPC.ACL_CONFIG, TestRpcBase.TestRpcService.class) };
        }
    }

    @Test
    public void testServerAddress() throws IOException {
        Server server;
        server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5);
        try {
            InetSocketAddress bindAddr = NetUtils.getConnectAddress(server);
            Assert.assertEquals(InetAddress.getLocalHost(), bindAddr.getAddress());
        } finally {
            TestRpcBase.stop(server, null);
        }
    }

    @Test
    public void testAuthorization() throws Exception {
        Configuration myConf = new Configuration();
        myConf.setBoolean(HADOOP_SECURITY_AUTHORIZATION, true);
        // Expect to succeed
        myConf.set(TestRPC.ACL_CONFIG, "*");
        doRPCs(myConf, false);
        // Reset authorization to expect failure
        myConf.set(TestRPC.ACL_CONFIG, "invalid invalid");
        doRPCs(myConf, true);
        myConf.setInt(IPC_SERVER_RPC_READ_THREADS_KEY, 2);
        // Expect to succeed
        myConf.set(TestRPC.ACL_CONFIG, "*");
        doRPCs(myConf, false);
        // Reset authorization to expect failure
        myConf.set(TestRPC.ACL_CONFIG, "invalid invalid");
        doRPCs(myConf, true);
    }

    /**
     * Test stopping a non-registered proxy
     *
     * @throws IOException
     * 		
     */
    @Test(expected = HadoopIllegalArgumentException.class)
    public void testStopNonRegisteredProxy() throws IOException {
        RPC.stopProxy(null);
    }

    /**
     * Test that the mockProtocol helper returns mock proxies that can
     * be stopped without error.
     */
    @Test
    public void testStopMockObject() throws IOException {
        RPC.stopProxy(MockitoUtil.mockProtocol(TestRPC.TestProtocol.class));
    }

    @Test
    public void testStopProxy() throws IOException {
        RPC.setProtocolEngine(TestRpcBase.conf, TestRPC.StoppedProtocol.class, TestRPC.StoppedRpcEngine.class);
        TestRPC.StoppedProtocol proxy = RPC.getProxy(TestRPC.StoppedProtocol.class, TestRPC.StoppedProtocol.versionID, null, TestRpcBase.conf);
        TestRPC.StoppedInvocationHandler invocationHandler = ((TestRPC.StoppedInvocationHandler) (Proxy.getInvocationHandler(proxy)));
        Assert.assertEquals(0, invocationHandler.getCloseCalled());
        RPC.stopProxy(proxy);
        Assert.assertEquals(1, invocationHandler.getCloseCalled());
    }

    @Test
    public void testWrappedStopProxy() throws IOException {
        TestRPC.StoppedProtocol wrappedProxy = RPC.getProxy(TestRPC.StoppedProtocol.class, TestRPC.StoppedProtocol.versionID, null, TestRpcBase.conf);
        TestRPC.StoppedInvocationHandler invocationHandler = ((TestRPC.StoppedInvocationHandler) (Proxy.getInvocationHandler(wrappedProxy)));
        TestRPC.StoppedProtocol proxy = ((TestRPC.StoppedProtocol) (RetryProxy.create(TestRPC.StoppedProtocol.class, wrappedProxy, RETRY_FOREVER)));
        Assert.assertEquals(0, invocationHandler.getCloseCalled());
        RPC.stopProxy(proxy);
        Assert.assertEquals(1, invocationHandler.getCloseCalled());
    }

    @Test
    public void testErrorMsgForInsecureClient() throws IOException {
        Server server;
        TestRpcBase.TestRpcService proxy = null;
        Configuration serverConf = new Configuration(TestRpcBase.conf);
        SecurityUtil.setAuthenticationMethod(KERBEROS, serverConf);
        UserGroupInformation.setConfiguration(serverConf);
        server = TestRpcBase.setupTestServer(serverConf, 5);
        boolean succeeded = false;
        try {
            UserGroupInformation.setConfiguration(TestRpcBase.conf);
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            proxy.echo(null, TestRpcBase.newEchoRequest(""));
        } catch (ServiceException e) {
            Assert.assertTrue(((e.getCause()) instanceof RemoteException));
            RemoteException re = ((RemoteException) (e.getCause()));
            TestRPC.LOG.info(("LOGGING MESSAGE: " + (re.getLocalizedMessage())));
            Assert.assertEquals("RPC error code should be UNAUTHORIZED", FATAL_UNAUTHORIZED, re.getErrorCode());
            Assert.assertTrue(((re.unwrapRemoteException()) instanceof AccessControlException));
            succeeded = true;
        } finally {
            TestRpcBase.stop(server, proxy);
        }
        Assert.assertTrue(succeeded);
        TestRpcBase.conf.setInt(IPC_SERVER_RPC_READ_THREADS_KEY, 2);
        UserGroupInformation.setConfiguration(serverConf);
        server = TestRpcBase.setupTestServer(serverConf, 5);
        succeeded = false;
        proxy = null;
        try {
            UserGroupInformation.setConfiguration(TestRpcBase.conf);
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            proxy.echo(null, TestRpcBase.newEchoRequest(""));
        } catch (ServiceException e) {
            RemoteException re = ((RemoteException) (e.getCause()));
            TestRPC.LOG.info(("LOGGING MESSAGE: " + (re.getLocalizedMessage())));
            Assert.assertEquals("RPC error code should be UNAUTHORIZED", FATAL_UNAUTHORIZED, re.getErrorCode());
            Assert.assertTrue(((re.unwrapRemoteException()) instanceof AccessControlException));
            succeeded = true;
        } finally {
            TestRpcBase.stop(server, proxy);
        }
        Assert.assertTrue(succeeded);
    }

    /**
     * Test that server.stop() properly stops all threads
     */
    @Test
    public void testStopsAllThreads() throws IOException, InterruptedException {
        Server server;
        int threadsBefore = TestRpcBase.countThreads("Server$Listener$Reader");
        Assert.assertEquals("Expect no Reader threads running before test", 0, threadsBefore);
        server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5);
        try {
            // Wait for at least one reader thread to start
            int threadsRunning = 0;
            long totalSleepTime = 0;
            do {
                totalSleepTime += 10;
                Thread.sleep(10);
                threadsRunning = TestRpcBase.countThreads("Server$Listener$Reader");
            } while ((threadsRunning == 0) && (totalSleepTime < 5000) );
            // Validate that at least one thread started (we didn't timeout)
            threadsRunning = TestRpcBase.countThreads("Server$Listener$Reader");
            Assert.assertTrue((threadsRunning > 0));
        } finally {
            server.stop();
        }
        int threadsAfter = TestRpcBase.countThreads("Server$Listener$Reader");
        Assert.assertEquals("Expect no Reader threads left running after test", 0, threadsAfter);
    }

    @Test
    public void testRPCBuilder() throws IOException {
        // Test mandatory field conf
        try {
            setInstance(new TestRPC.TestImpl()).setBindAddress(TestRpcBase.ADDRESS).setPort(0).setNumHandlers(5).setVerbose(true).build();
            Assert.fail("Didn't throw HadoopIllegalArgumentException");
        } catch (Exception e) {
            if (!(e instanceof HadoopIllegalArgumentException)) {
                Assert.fail(("Expecting HadoopIllegalArgumentException but caught " + e));
            }
        }
        // Test mandatory field protocol
        try {
            setInstance(new TestRPC.TestImpl()).setBindAddress(TestRpcBase.ADDRESS).setPort(0).setNumHandlers(5).setVerbose(true).build();
            Assert.fail("Didn't throw HadoopIllegalArgumentException");
        } catch (Exception e) {
            if (!(e instanceof HadoopIllegalArgumentException)) {
                Assert.fail(("Expecting HadoopIllegalArgumentException but caught " + e));
            }
        }
        // Test mandatory field instance
        try {
            setProtocol(TestRPC.TestProtocol.class).setBindAddress(TestRpcBase.ADDRESS).setPort(0).setNumHandlers(5).setVerbose(true).build();
            Assert.fail("Didn't throw HadoopIllegalArgumentException");
        } catch (Exception e) {
            if (!(e instanceof HadoopIllegalArgumentException)) {
                Assert.fail(("Expecting HadoopIllegalArgumentException but caught " + e));
            }
        }
    }

    @Test(timeout = 90000)
    public void testRPCInterruptedSimple() throws Exception {
        Server server;
        TestRpcBase.TestRpcService proxy = null;
        RPC.Builder builder = TestRpcBase.newServerBuilder(TestRpcBase.conf).setNumHandlers(5).setVerbose(true).setSecretManager(null);
        server = TestRpcBase.setupTestServer(builder);
        try {
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            // Connect to the server
            proxy.ping(null, TestRpcBase.newEmptyRequest());
            // Interrupt self, try another call
            Thread.currentThread().interrupt();
            try {
                proxy.ping(null, TestRpcBase.newEmptyRequest());
                Assert.fail("Interruption did not cause IPC to fail");
            } catch (ServiceException se) {
                if ((se.toString().contains("InterruptedException")) || ((se.getCause()) instanceof InterruptedIOException)) {
                    // clear interrupt status for future tests
                    Thread.interrupted();
                    return;
                }
                throw se;
            }
        } finally {
            TestRpcBase.stop(server, proxy);
        }
    }

    @Test(timeout = 30000)
    public void testRPCInterrupted() throws Exception {
        Server server;
        RPC.Builder builder = TestRpcBase.newServerBuilder(TestRpcBase.conf).setNumHandlers(5).setVerbose(true).setSecretManager(null);
        server = TestRpcBase.setupTestServer(builder);
        int numConcurrentRPC = 200;
        final CyclicBarrier barrier = new CyclicBarrier(numConcurrentRPC);
        final CountDownLatch latch = new CountDownLatch(numConcurrentRPC);
        final AtomicBoolean leaderRunning = new AtomicBoolean(true);
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread leaderThread = null;
        try {
            for (int i = 0; i < numConcurrentRPC; i++) {
                final int num = i;
                final TestRpcBase.TestRpcService proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
                Thread rpcThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            barrier.await();
                            while ((num == 0) || (leaderRunning.get())) {
                                proxy.slowPing(null, TestRpcBase.newSlowPingRequest(false));
                            } 
                            proxy.slowPing(null, TestRpcBase.newSlowPingRequest(false));
                        } catch (Exception e) {
                            if (num == 0) {
                                leaderRunning.set(false);
                            } else {
                                error.set(e);
                            }
                            TestRPC.LOG.error(("thread " + num), e);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
                rpcThread.start();
                if (leaderThread == null) {
                    leaderThread = rpcThread;
                }
            }
            // let threads get past the barrier
            Thread.sleep(1000);
            // stop a single thread
            while (leaderRunning.get()) {
                leaderThread.interrupt();
            } 
            latch.await();
            // should not cause any other thread to get an error
            Assert.assertTrue(("rpc got exception " + (error.get())), ((error.get()) == null));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testConnectionPing() throws Exception {
        Server server;
        TestRpcBase.TestRpcService proxy = null;
        int pingInterval = 50;
        TestRpcBase.conf.setBoolean(IPC_CLIENT_PING_KEY, true);
        TestRpcBase.conf.setInt(IPC_PING_INTERVAL_KEY, pingInterval);
        server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5);
        try {
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            proxy.sleep(null, TestRpcBase.newSleepRequest((pingInterval * 4)));
        } finally {
            TestRpcBase.stop(server, proxy);
        }
    }

    @Test(timeout = 30000)
    public void testExternalCall() throws Exception {
        final UserGroupInformation ugi = UserGroupInformation.createUserForTesting("user123", new String[0]);
        final IOException expectedIOE = new IOException("boom");
        // use 1 handler so the callq can be plugged
        final Server server = TestRpcBase.setupTestServer(TestRpcBase.conf, 1);
        try {
            final AtomicBoolean result = new AtomicBoolean();
            ExternalCall<String> remoteUserCall = newExtCall(ugi, new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws Exception {
                    return UserGroupInformation.getCurrentUser().getUserName();
                }
            });
            ExternalCall<String> exceptionCall = newExtCall(ugi, new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws Exception {
                    throw expectedIOE;
                }
            });
            final CountDownLatch latch = new CountDownLatch(1);
            final CyclicBarrier barrier = new CyclicBarrier(2);
            ExternalCall<Void> barrierCall = newExtCall(ugi, new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // notify we are in a handler and then wait to keep the callq
                    // plugged up
                    latch.countDown();
                    barrier.await();
                    return null;
                }
            });
            server.queueCall(barrierCall);
            server.queueCall(exceptionCall);
            server.queueCall(remoteUserCall);
            // wait for barrier call to enter the handler, check that the other 2
            // calls are actually queued
            latch.await();
            Assert.assertEquals(2, server.getCallQueueLen());
            // unplug the callq
            barrier.await();
            barrierCall.get();
            // verify correct ugi is used
            String answer = remoteUserCall.get();
            Assert.assertEquals(ugi.getUserName(), answer);
            try {
                exceptionCall.get();
                Assert.fail("didn't throw");
            } catch (ExecutionException ee) {
                Assert.assertTrue(((ee.getCause()) instanceof IOException));
                Assert.assertEquals(expectedIOE.getMessage(), ee.getCause().getMessage());
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testRpcMetrics() throws Exception {
        final Server server;
        TestRpcBase.TestRpcService proxy = null;
        final int interval = 1;
        TestRpcBase.conf.setBoolean(RPC_METRICS_QUANTILE_ENABLE, true);
        TestRpcBase.conf.set(RPC_METRICS_PERCENTILES_INTERVALS_KEY, ("" + interval));
        server = TestRpcBase.setupTestServer(TestRpcBase.conf, 5);
        String testUser = "testUser";
        UserGroupInformation anotherUser = UserGroupInformation.createRemoteUser(testUser);
        TestRpcBase.TestRpcService proxy2 = anotherUser.doAs(new PrivilegedAction<TestRpcBase.TestRpcService>() {
            public TestRpcBase.TestRpcService run() {
                try {
                    return RPC.getProxy(TestRpcBase.TestRpcService.class, 0, server.getListenerAddress(), TestRpcBase.conf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        try {
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            for (int i = 0; i < 1000; i++) {
                proxy.ping(null, TestRpcBase.newEmptyRequest());
                proxy.echo(null, TestRpcBase.newEchoRequest(("" + i)));
                proxy2.echo(null, TestRpcBase.newEchoRequest(("" + i)));
            }
            MetricsRecordBuilder rpcMetrics = MetricsAsserts.getMetrics(server.getRpcMetrics().name());
            Assert.assertTrue("Expected non-zero rpc queue time", ((MetricsAsserts.getLongCounter("RpcQueueTimeNumOps", rpcMetrics)) > 0));
            Assert.assertTrue("Expected non-zero rpc processing time", ((MetricsAsserts.getLongCounter("RpcProcessingTimeNumOps", rpcMetrics)) > 0));
            MetricsAsserts.assertQuantileGauges((("RpcQueueTime" + interval) + "s"), rpcMetrics);
            MetricsAsserts.assertQuantileGauges((("RpcProcessingTime" + interval) + "s"), rpcMetrics);
            String actualUserVsCon = MetricsAsserts.getStringMetric("NumOpenConnectionsPerUser", rpcMetrics);
            String proxyUser = UserGroupInformation.getCurrentUser().getShortUserName();
            Assert.assertTrue(actualUserVsCon.contains((("\"" + proxyUser) + "\":1")));
            Assert.assertTrue(actualUserVsCon.contains((("\"" + testUser) + "\":1")));
        } finally {
            if (proxy2 != null) {
                RPC.stopProxy(proxy2);
            }
            TestRpcBase.stop(server, proxy);
        }
    }

    /**
     * Test RPC backoff by queue full.
     */
    @Test(timeout = 30000)
    public void testClientBackOff() throws Exception {
        Server server;
        final TestRpcBase.TestRpcService proxy;
        boolean succeeded = false;
        final int numClients = 2;
        final List<Future<Void>> res = new ArrayList<Future<Void>>();
        final ExecutorService executorService = Executors.newFixedThreadPool(numClients);
        TestRpcBase.conf.setInt(IPC_CLIENT_CONNECT_MAX_RETRIES_KEY, 0);
        TestRpcBase.conf.setBoolean((((CommonConfigurationKeys.IPC_NAMESPACE) + ".0.") + (CommonConfigurationKeys.IPC_BACKOFF_ENABLE)), true);
        RPC.Builder builder = TestRpcBase.newServerBuilder(TestRpcBase.conf).setQueueSizePerHandler(1).setNumHandlers(1).setVerbose(true);
        server = TestRpcBase.setupTestServer(builder);
        @SuppressWarnings("unchecked")
        CallQueueManager<Call> spy = Mockito.spy(((CallQueueManager<Call>) (Whitebox.getInternalState(server, "callQueue"))));
        Whitebox.setInternalState(server, "callQueue", spy);
        Exception lastException = null;
        proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
        try {
            // start a sleep RPC call to consume the only handler thread.
            // Start another sleep RPC call to make callQueue full.
            // Start another sleep RPC call to make reader thread block on CallQueue.
            for (int i = 0; i < numClients; i++) {
                res.add(executorService.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws ServiceException, InterruptedException {
                        proxy.sleep(null, TestRpcBase.newSleepRequest(100000));
                        return null;
                    }
                }));
                Mockito.verify(spy, Mockito.timeout(500).times((i + 1))).add(any());
            }
            try {
                proxy.sleep(null, TestRpcBase.newSleepRequest(100));
            } catch (ServiceException e) {
                RemoteException re = ((RemoteException) (e.getCause()));
                IOException unwrapExeption = re.unwrapRemoteException();
                if (unwrapExeption instanceof RetriableException) {
                    succeeded = true;
                } else {
                    lastException = unwrapExeption;
                }
            }
        } finally {
            executorService.shutdown();
            TestRpcBase.stop(server, proxy);
        }
        if (lastException != null) {
            TestRPC.LOG.error("Last received non-RetriableException:", lastException);
        }
        Assert.assertTrue("RetriableException not received", succeeded);
    }

    /**
     * Test RPC backoff by response time of each priority level.
     */
    @Test(timeout = 30000)
    public void testClientBackOffByResponseTime() throws Exception {
        final TestRpcBase.TestRpcService proxy;
        boolean succeeded = false;
        final int numClients = 1;
        GenericTestUtils.setLogLevel(DecayRpcScheduler.LOG, Level.DEBUG);
        GenericTestUtils.setLogLevel(RPC.LOG, Level.DEBUG);
        final List<Future<Void>> res = new ArrayList<Future<Void>>();
        final ExecutorService executorService = Executors.newFixedThreadPool(numClients);
        TestRpcBase.conf.setInt(IPC_CLIENT_CONNECT_MAX_RETRIES_KEY, 0);
        final String ns = (CommonConfigurationKeys.IPC_NAMESPACE) + ".0";
        Server server = setupDecayRpcSchedulerandTestServer((ns + "."));
        @SuppressWarnings("unchecked")
        CallQueueManager<Call> spy = Mockito.spy(((CallQueueManager<Call>) (Whitebox.getInternalState(server, "callQueue"))));
        Whitebox.setInternalState(server, "callQueue", spy);
        Exception lastException = null;
        proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
        MetricsRecordBuilder rb1 = MetricsAsserts.getMetrics(("DecayRpcSchedulerMetrics2." + ns));
        final long beginDecayedCallVolume = MetricsAsserts.getLongCounter("DecayedCallVolume", rb1);
        final long beginRawCallVolume = MetricsAsserts.getLongCounter("CallVolume", rb1);
        final int beginUniqueCaller = MetricsAsserts.getIntCounter("UniqueCallers", rb1);
        try {
            // start a sleep RPC call that sleeps 3s.
            for (int i = 0; i < numClients; i++) {
                res.add(executorService.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws ServiceException, InterruptedException {
                        proxy.sleep(null, TestRpcBase.newSleepRequest(3000));
                        return null;
                    }
                }));
                Mockito.verify(spy, Mockito.timeout(500).times((i + 1))).add(any());
            }
            // Start another sleep RPC call and verify the call is backed off due to
            // avg response time(3s) exceeds threshold (2s).
            try {
                // wait for the 1st response time update
                Thread.sleep(5500);
                proxy.sleep(null, TestRpcBase.newSleepRequest(100));
            } catch (ServiceException e) {
                RemoteException re = ((RemoteException) (e.getCause()));
                IOException unwrapExeption = re.unwrapRemoteException();
                if (unwrapExeption instanceof RetriableException) {
                    succeeded = true;
                } else {
                    lastException = unwrapExeption;
                }
                // Lets Metric system update latest metrics
                GenericTestUtils.waitFor(new Supplier<Boolean>() {
                    @Override
                    public Boolean get() {
                        MetricsRecordBuilder rb2 = MetricsAsserts.getMetrics(("DecayRpcSchedulerMetrics2." + ns));
                        long decayedCallVolume1 = MetricsAsserts.getLongCounter("DecayedCallVolume", rb2);
                        long rawCallVolume1 = MetricsAsserts.getLongCounter("CallVolume", rb2);
                        int uniqueCaller1 = MetricsAsserts.getIntCounter("UniqueCallers", rb2);
                        long callVolumePriority0 = MetricsAsserts.getLongGauge("Priority.0.CompletedCallVolume", rb2);
                        long callVolumePriority1 = MetricsAsserts.getLongGauge("Priority.1.CompletedCallVolume", rb2);
                        double avgRespTimePriority0 = MetricsAsserts.getDoubleGauge("Priority.0.AvgResponseTime", rb2);
                        double avgRespTimePriority1 = MetricsAsserts.getDoubleGauge("Priority.1.AvgResponseTime", rb2);
                        TestRPC.LOG.info(("DecayedCallVolume: " + decayedCallVolume1));
                        TestRPC.LOG.info(("CallVolume: " + rawCallVolume1));
                        TestRPC.LOG.info(("UniqueCaller: " + uniqueCaller1));
                        TestRPC.LOG.info(("Priority.0.CompletedCallVolume: " + callVolumePriority0));
                        TestRPC.LOG.info(("Priority.1.CompletedCallVolume: " + callVolumePriority1));
                        TestRPC.LOG.info(("Priority.0.AvgResponseTime: " + avgRespTimePriority0));
                        TestRPC.LOG.info(("Priority.1.AvgResponseTime: " + avgRespTimePriority1));
                        return ((decayedCallVolume1 > beginDecayedCallVolume) && (rawCallVolume1 > beginRawCallVolume)) && (uniqueCaller1 > beginUniqueCaller);
                    }
                }, 30, 60000);
            }
        } finally {
            executorService.shutdown();
            TestRpcBase.stop(server, proxy);
        }
        if (lastException != null) {
            TestRPC.LOG.error("Last received non-RetriableException:", lastException);
        }
        Assert.assertTrue("RetriableException not received", succeeded);
    }

    /**
     * Test RPC timeout.
     */
    @Test(timeout = 30000)
    public void testClientRpcTimeout() throws Exception {
        Server server;
        TestRpcBase.TestRpcService proxy = null;
        RPC.Builder builder = TestRpcBase.newServerBuilder(TestRpcBase.conf).setQueueSizePerHandler(1).setNumHandlers(1).setVerbose(true);
        server = TestRpcBase.setupTestServer(builder);
        try {
            // Test RPC timeout with default ipc.client.ping.
            try {
                Configuration c = new Configuration(TestRpcBase.conf);
                c.setInt(IPC_CLIENT_RPC_TIMEOUT_KEY, 1000);
                proxy = TestRpcBase.getClient(TestRpcBase.addr, c);
                proxy.sleep(null, TestRpcBase.newSleepRequest(3000));
                Assert.fail("RPC should time out.");
            } catch (ServiceException e) {
                Assert.assertTrue(((e.getCause()) instanceof SocketTimeoutException));
                TestRPC.LOG.info("got expected timeout.", e);
            }
            // Test RPC timeout when ipc.client.ping is false.
            try {
                Configuration c = new Configuration(TestRpcBase.conf);
                c.setBoolean(IPC_CLIENT_PING_KEY, false);
                c.setInt(IPC_CLIENT_RPC_TIMEOUT_KEY, 1000);
                proxy = TestRpcBase.getClient(TestRpcBase.addr, c);
                proxy.sleep(null, TestRpcBase.newSleepRequest(3000));
                Assert.fail("RPC should time out.");
            } catch (ServiceException e) {
                Assert.assertTrue(((e.getCause()) instanceof SocketTimeoutException));
                TestRPC.LOG.info("got expected timeout.", e);
            }
            // Test negative timeout value.
            try {
                Configuration c = new Configuration(TestRpcBase.conf);
                c.setInt(IPC_CLIENT_RPC_TIMEOUT_KEY, (-1));
                proxy = TestRpcBase.getClient(TestRpcBase.addr, c);
                proxy.sleep(null, TestRpcBase.newSleepRequest(2000));
            } catch (ServiceException e) {
                TestRPC.LOG.info("got unexpected exception.", e);
                Assert.fail("RPC should not time out.");
            }
            // Test RPC timeout greater than ipc.ping.interval.
            try {
                Configuration c = new Configuration(TestRpcBase.conf);
                c.setBoolean(IPC_CLIENT_PING_KEY, true);
                c.setInt(IPC_PING_INTERVAL_KEY, 800);
                c.setInt(IPC_CLIENT_RPC_TIMEOUT_KEY, 1000);
                proxy = TestRpcBase.getClient(TestRpcBase.addr, c);
                try {
                    // should not time out because effective rpc-timeout is
                    // multiple of ping interval: 1600 (= 800 * (1000 / 800 + 1))
                    proxy.sleep(null, TestRpcBase.newSleepRequest(1300));
                } catch (ServiceException e) {
                    TestRPC.LOG.info("got unexpected exception.", e);
                    Assert.fail("RPC should not time out.");
                }
                proxy.sleep(null, TestRpcBase.newSleepRequest(2000));
                Assert.fail("RPC should time out.");
            } catch (ServiceException e) {
                Assert.assertTrue(((e.getCause()) instanceof SocketTimeoutException));
                TestRPC.LOG.info("got expected timeout.", e);
            }
        } finally {
            TestRpcBase.stop(server, proxy);
        }
    }

    @Test
    public void testServerNameFromClass() {
        Assert.assertEquals("TestRPC", RPC.Server.serverNameFromClass(this.getClass()));
        Assert.assertEquals("TestClass", RPC.Server.serverNameFromClass(TestRPC.TestClass.class));
        Object testing = new TestRPC.TestClass().classFactory();
        Assert.assertEquals("Embedded", RPC.Server.serverNameFromClass(testing.getClass()));
        testing = new TestRPC.TestClass().classFactoryAbstract();
        Assert.assertEquals("TestClass", RPC.Server.serverNameFromClass(testing.getClass()));
        testing = new TestRPC.TestClass().classFactoryObject();
        Assert.assertEquals("TestClass", RPC.Server.serverNameFromClass(testing.getClass()));
    }

    static class TestClass {
        class Embedded {}

        abstract class AbstractEmbedded {}

        private Object classFactory() {
            return new TestRPC.TestClass.Embedded();
        }

        private Object classFactoryAbstract() {
            return new TestRPC.TestClass.AbstractEmbedded() {};
        }

        private Object classFactoryObject() {
            return new Object() {};
        }
    }

    public static class FakeRequestClass extends RpcWritable {
        static volatile IOException exception;

        @Override
        void writeTo(ResponseBuffer out) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        <T> T readFrom(ByteBuffer bb) throws IOException {
            throw TestRPC.FakeRequestClass.exception;
        }
    }

    @SuppressWarnings("serial")
    public static class TestReaderException extends IOException {
        public TestReaderException(String msg) {
            super(msg);
        }

        @Override
        public boolean equals(Object t) {
            return ((t.getClass()) == (TestRPC.TestReaderException.class)) && (getMessage().equals(((TestRPC.TestReaderException) (t)).getMessage()));
        }
    }

    @Test(timeout = 30000)
    public void testReaderExceptions() throws Exception {
        Server server = null;
        TestRpcBase.TestRpcService proxy = null;
        // will attempt to return this exception from a reader with and w/o
        // the connection closing.
        IOException expectedIOE = new TestRPC.TestReaderException("testing123");
        @SuppressWarnings("serial")
        IOException rseError = new RpcServerException("keepalive", expectedIOE) {
            @Override
            public RpcStatusProto getRpcStatusProto() {
                return RpcStatusProto.ERROR;
            }
        };
        @SuppressWarnings("serial")
        IOException rseFatal = new RpcServerException("disconnect", expectedIOE) {
            @Override
            public RpcStatusProto getRpcStatusProto() {
                return RpcStatusProto.FATAL;
            }
        };
        try {
            RPC.Builder builder = TestRpcBase.newServerBuilder(TestRpcBase.conf).setQueueSizePerHandler(1).setNumHandlers(1).setVerbose(true);
            server = TestRpcBase.setupTestServer(builder);
            Whitebox.setInternalState(server, "rpcRequestClass", TestRPC.FakeRequestClass.class);
            MutableCounterLong authMetric = ((MutableCounterLong) (Whitebox.getInternalState(server.getRpcMetrics(), "rpcAuthorizationSuccesses")));
            proxy = TestRpcBase.getClient(TestRpcBase.addr, TestRpcBase.conf);
            boolean isDisconnected = true;
            Connection lastConn = null;
            long expectedAuths = 0;
            // fuzz the client.
            for (int i = 0; i < 128; i++) {
                String reqName = ("request[" + i) + "]";
                int r = ThreadLocalRandom.current().nextInt();
                final boolean doDisconnect = (r % 4) == 0;
                TestRPC.LOG.info((((((("TestDisconnect request[" + i) + "] ") + " shouldConnect=") + isDisconnected) + " willDisconnect=") + doDisconnect));
                if (isDisconnected) {
                    expectedAuths++;
                }
                try {
                    TestRPC.FakeRequestClass.exception = (doDisconnect) ? rseFatal : rseError;
                    proxy.ping(null, TestRpcBase.newEmptyRequest());
                    Assert.fail((reqName + " didn't fail"));
                } catch (ServiceException e) {
                    RemoteException re = ((RemoteException) (e.getCause()));
                    Assert.assertEquals(reqName, expectedIOE, re.unwrapRemoteException());
                }
                // check authorizations to ensure new connection when expected,
                // then conclusively determine if connections are disconnected
                // correctly.
                Assert.assertEquals(reqName, expectedAuths, authMetric.value());
                if (!doDisconnect) {
                    // if it wasn't fatal, verify there's only one open connection.
                    Connection[] conns = server.getConnections();
                    Assert.assertEquals(reqName, 1, conns.length);
                    // verify whether the connection should have been reused.
                    if (isDisconnected) {
                        Assert.assertNotSame(reqName, lastConn, conns[0]);
                    } else {
                        Assert.assertSame(reqName, lastConn, conns[0]);
                    }
                    lastConn = conns[0];
                } else
                    if (lastConn != null) {
                        // avoid race condition in server where connection may not be
                        // fully removed yet.  just make sure it's marked for being closed.
                        // the open connection checks above ensure correct behavior.
                        Assert.assertTrue(reqName, lastConn.shouldClose());
                    }

                isDisconnected = doDisconnect;
            }
        } finally {
            TestRpcBase.stop(server, proxy);
        }
    }
}

