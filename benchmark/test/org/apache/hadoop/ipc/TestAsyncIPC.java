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


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC.RpcKind;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos.RpcResponseHeaderProto;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAsyncIPC {
    private static Configuration conf;

    private static final Logger LOG = LoggerFactory.getLogger(TestAsyncIPC.class);

    static class AsyncCaller extends Thread {
        private Client client;

        private InetSocketAddress server;

        private int count;

        private boolean failed;

        Map<Integer, Future<LongWritable>> returnFutures = new HashMap<Integer, Future<LongWritable>>();

        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();

        public AsyncCaller(Client client, InetSocketAddress server, int count) {
            this.client = client;
            this.server = server;
            this.count = count;
            // set asynchronous mode, since AsyncCaller extends Thread
            Client.setAsynchronousMode(true);
        }

        @Override
        public void run() {
            // in case Thread#Start is called, which will spawn new thread
            Client.setAsynchronousMode(true);
            for (int i = 0; i < (count); i++) {
                try {
                    final long param = TestIPC.RANDOM.nextLong();
                    TestIPC.call(client, param, server, TestAsyncIPC.conf);
                    returnFutures.put(i, TestAsyncIPC.getAsyncRpcResponseFuture());
                    expectedValues.put(i, param);
                } catch (Exception e) {
                    failed = true;
                    throw new RuntimeException(e);
                }
            }
        }

        void assertReturnValues() throws InterruptedException, ExecutionException {
            for (int i = 0; i < (count); i++) {
                LongWritable value = returnFutures.get(i).get();
                Assert.assertEquals((("call" + i) + " failed."), expectedValues.get(i).longValue(), value.get());
            }
            Assert.assertFalse(failed);
        }

        void assertReturnValues(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
            final boolean[] checked = new boolean[count];
            for (boolean done = false; !done;) {
                done = true;
                for (int i = 0; i < (count); i++) {
                    if (checked[i]) {
                        continue;
                    } else {
                        done = false;
                    }
                    final LongWritable value;
                    try {
                        value = returnFutures.get(i).get(timeout, unit);
                    } catch (TimeoutException e) {
                        TestAsyncIPC.LOG.info((("call" + i) + " caught "), e);
                        continue;
                    }
                    Assert.assertEquals((("call" + i) + " failed."), expectedValues.get(i).longValue(), value.get());
                    checked[i] = true;
                }
            }
            Assert.assertFalse(failed);
        }
    }

    static class AsyncLimitlCaller extends Thread {
        private Client client;

        private InetSocketAddress server;

        private int count;

        private boolean failed;

        Map<Integer, Future<LongWritable>> returnFutures = new HashMap<Integer, Future<LongWritable>>();

        Map<Integer, Long> expectedValues = new HashMap<Integer, Long>();

        int start = 0;

        int end = 0;

        int getStart() {
            return start;
        }

        int getEnd() {
            return end;
        }

        int getCount() {
            return count;
        }

        public AsyncLimitlCaller(Client client, InetSocketAddress server, int count) {
            this(0, client, server, count);
        }

        final int callerId;

        public AsyncLimitlCaller(int callerId, Client client, InetSocketAddress server, int count) {
            this.client = client;
            this.server = server;
            this.count = count;
            // set asynchronous mode, since AsyncLimitlCaller extends Thread
            Client.setAsynchronousMode(true);
            this.callerId = callerId;
        }

        @Override
        public void run() {
            // in case Thread#Start is called, which will spawn new thread
            Client.setAsynchronousMode(true);
            for (int i = 0; i < (count); i++) {
                try {
                    final long param = TestIPC.RANDOM.nextLong();
                    runCall(i, param);
                } catch (Exception e) {
                    TestAsyncIPC.LOG.error(String.format("Caller-%d Call-%d caught: %s", callerId, i, StringUtils.stringifyException(e)));
                    failed = true;
                }
            }
        }

        private void runCall(final int idx, final long param) throws IOException, InterruptedException, ExecutionException {
            for (; ;) {
                try {
                    doCall(idx, param);
                    return;
                } catch (AsyncCallLimitExceededException e) {
                    /**
                     * reached limit of async calls, fetch results of finished async calls
                     * to let follow-on calls go
                     */
                    start = end;
                    end = idx;
                    waitForReturnValues(start, end);
                }
            }
        }

        private void doCall(final int idx, final long param) throws IOException {
            TestIPC.call(client, param, server, TestAsyncIPC.conf);
            returnFutures.put(idx, TestAsyncIPC.getAsyncRpcResponseFuture());
            expectedValues.put(idx, param);
        }

        private void waitForReturnValues(final int start, final int end) throws InterruptedException, ExecutionException {
            for (int i = start; i < end; i++) {
                LongWritable value = returnFutures.get(i).get();
                if ((expectedValues.get(i)) != (value.get())) {
                    TestAsyncIPC.LOG.error(String.format("Caller-%d Call-%d failed!", callerId, i));
                    failed = true;
                    break;
                }
            }
        }
    }

    @Test(timeout = 60000)
    public void testAsyncCall() throws IOException, InterruptedException, ExecutionException {
        internalTestAsyncCall(3, false, 2, 5, 100);
        internalTestAsyncCall(3, true, 2, 5, 10);
    }

    @Test(timeout = 60000)
    public void testAsyncCallLimit() throws IOException, InterruptedException, ExecutionException {
        internalTestAsyncCallLimit(100, false, 5, 10, 500);
    }

    @Test(timeout = 60000)
    public void testCallGetReturnRpcResponseMultipleTimes() throws IOException, InterruptedException, ExecutionException {
        int handlerCount = 10;
        int callCount = 100;
        Server server = new TestIPC.TestServer(handlerCount, false, TestAsyncIPC.conf);
        InetSocketAddress addr = NetUtils.getConnectAddress(server);
        server.start();
        final Client client = new Client(LongWritable.class, TestAsyncIPC.conf);
        int asyncCallCount = client.getAsyncCallCount();
        try {
            TestAsyncIPC.AsyncCaller caller = new TestAsyncIPC.AsyncCaller(client, addr, callCount);
            caller.run();
            caller.assertReturnValues();
            caller.assertReturnValues();
            caller.assertReturnValues();
            Assert.assertEquals(asyncCallCount, client.getAsyncCallCount());
        } finally {
            client.stop();
            server.stop();
        }
    }

    @Test(timeout = 60000)
    public void testFutureGetWithTimeout() throws IOException, InterruptedException, ExecutionException {
        // GenericTestUtils.setLogLevel(AsyncGetFuture.LOG, Level.ALL);
        final Server server = new TestIPC.TestServer(10, true, TestAsyncIPC.conf);
        final InetSocketAddress addr = NetUtils.getConnectAddress(server);
        server.start();
        final Client client = new Client(LongWritable.class, TestAsyncIPC.conf);
        try {
            final TestAsyncIPC.AsyncCaller caller = new TestAsyncIPC.AsyncCaller(client, addr, 10);
            caller.run();
            caller.assertReturnValues(10, TimeUnit.MILLISECONDS);
        } finally {
            client.stop();
            server.stop();
        }
    }

    /**
     * Test if (1) the rpc server uses the call id/retry provided by the rpc
     * client, and (2) the rpc client receives the same call id/retry from the rpc
     * server.
     *
     * @throws ExecutionException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 60000)
    public void testCallIdAndRetry() throws IOException, InterruptedException, ExecutionException {
        final Map<Integer, TestIPC.CallInfo> infoMap = new HashMap<Integer, TestIPC.CallInfo>();
        // Override client to store the call info and check response
        final Client client = new Client(LongWritable.class, TestAsyncIPC.conf) {
            @Override
            Call createCall(RpcKind rpcKind, Writable rpcRequest) {
                // Set different call id and retry count for the next call
                Client.setCallIdAndRetryCount(Client.nextCallId(), TestIPC.RANDOM.nextInt(255), null);
                final Call call = super.createCall(rpcKind, rpcRequest);
                TestIPC.CallInfo info = new TestIPC.CallInfo();
                info.id = call.id;
                info.retry = call.retry;
                infoMap.put(call.id, info);
                return call;
            }

            @Override
            void checkResponse(RpcResponseHeaderProto header) throws IOException {
                super.checkResponse(header);
                Assert.assertEquals(infoMap.get(header.getCallId()).retry, header.getRetryCount());
            }
        };
        // Attach a listener that tracks every call received by the server.
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false, TestAsyncIPC.conf);
        server.callListener = new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(infoMap.get(Server.getCallId()).retry, Server.getCallRetryCount());
            }
        };
        try {
            InetSocketAddress addr = NetUtils.getConnectAddress(server);
            start();
            final TestAsyncIPC.AsyncCaller caller = new TestAsyncIPC.AsyncCaller(client, addr, 4);
            caller.run();
            caller.assertReturnValues();
        } finally {
            client.stop();
            stop();
        }
    }

    /**
     * Test if the rpc server gets the retry count from client.
     *
     * @throws ExecutionException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 60000)
    public void testCallRetryCount() throws IOException, InterruptedException, ExecutionException {
        final int retryCount = 255;
        // Override client to store the call id
        final Client client = new Client(LongWritable.class, TestAsyncIPC.conf);
        Client.setCallIdAndRetryCount(Client.nextCallId(), retryCount, null);
        // Attach a listener that tracks every call ID received by the server.
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false, TestAsyncIPC.conf);
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
            final TestAsyncIPC.AsyncCaller caller = new TestAsyncIPC.AsyncCaller(client, addr, 10);
            caller.run();
            caller.assertReturnValues();
        } finally {
            client.stop();
            stop();
        }
    }

    /**
     * Test if the rpc server gets the default retry count (0) from client.
     *
     * @throws ExecutionException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 60000)
    public void testInitialCallRetryCount() throws IOException, InterruptedException, ExecutionException {
        // Override client to store the call id
        final Client client = new Client(LongWritable.class, TestAsyncIPC.conf);
        // Attach a listener that tracks every call ID received by the server.
        final TestIPC.TestServer server = new TestIPC.TestServer(1, false, TestAsyncIPC.conf);
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
            final TestAsyncIPC.AsyncCaller caller = new TestAsyncIPC.AsyncCaller(client, addr, 10);
            caller.run();
            caller.assertReturnValues();
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
     * @throws ExecutionException
     * 		
     */
    @Test(timeout = 60000)
    public void testUniqueSequentialCallIds() throws IOException, InterruptedException, ExecutionException {
        int serverThreads = 10;
        int callerCount = 100;
        int perCallerCallCount = 100;
        TestIPC.TestServer server = new TestIPC.TestServer(serverThreads, false, TestAsyncIPC.conf);
        // Attach a listener that tracks every call ID received by the server. This
        // list must be synchronized, because multiple server threads will add to
        // it.
        final List<Integer> callIds = Collections.synchronizedList(new ArrayList<Integer>());
        server.callListener = new Runnable() {
            @Override
            public void run() {
                callIds.add(Server.getCallId());
            }
        };
        Client client = new Client(LongWritable.class, TestAsyncIPC.conf);
        try {
            InetSocketAddress addr = NetUtils.getConnectAddress(server);
            start();
            TestAsyncIPC.AsyncCaller[] callers = new TestAsyncIPC.AsyncCaller[callerCount];
            for (int i = 0; i < callerCount; ++i) {
                callers[i] = new TestAsyncIPC.AsyncCaller(client, addr, perCallerCallCount);
                callers[i].start();
            }
            for (int i = 0; i < callerCount; ++i) {
                callers[i].join();
                callers[i].assertReturnValues();
            }
        } finally {
            client.stop();
            stop();
        }
        int expectedCallCount = callerCount * perCallerCallCount;
        Assert.assertEquals(expectedCallCount, callIds.size());
        // It is not guaranteed that the server executes requests in sequential
        // order
        // of client call ID, so we must sort the call IDs before checking that it
        // contains every expected value.
        Collections.sort(callIds);
        final int startID = callIds.get(0).intValue();
        for (int i = 0; i < expectedCallCount; ++i) {
            Assert.assertEquals((startID + i), callIds.get(i).intValue());
        }
    }
}

