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
package org.apache.hadoop.hbase.ipc;


import HConstants.NORMAL_QOS;
import HConstants.REGION_SERVER_HANDLER_COUNT;
import HConstants.REPLICATION_QOS;
import RWQueueRpcExecutor.CALL_QUEUE_READ_SHARE_CONF_KEY;
import RWQueueRpcExecutor.CALL_QUEUE_SCAN_SHARE_CONF_KEY;
import RpcExecutor.CALL_QUEUE_HANDLER_FACTOR_CONF_KEY;
import RpcExecutor.CALL_QUEUE_TYPE_CODEL_CONF_VALUE;
import RpcExecutor.CALL_QUEUE_TYPE_CONF_KEY;
import RpcExecutor.CALL_QUEUE_TYPE_DEADLINE_CONF_VALUE;
import RpcExecutor.CALL_QUEUE_TYPE_FIFO_CONF_VALUE;
import RpcScheduler.Context;
import RpcScheduler.IPC_SERVER_MAX_CALLQUEUE_LENGTH;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.monitoring.MonitoredRPCHandlerImpl;
import org.apache.hadoop.hbase.shaded.protobuf.RequestConverter;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RPCProtos.RequestHeader;
import org.apache.hadoop.hbase.testclassification.RPCTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdge;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableList;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableMap;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableSet;
import org.apache.hbase.thirdparty.com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RPCTests.class, SmallTests.class })
public class TestSimpleRpcScheduler {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSimpleRpcScheduler.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSimpleRpcScheduler.class);

    private final Context CONTEXT = new RpcScheduler.Context() {
        @Override
        public InetSocketAddress getListenerAddress() {
            return InetSocketAddress.createUnresolved("127.0.0.1", 1000);
        }
    };

    private Configuration conf;

    @Test
    public void testBasic() throws IOException, InterruptedException {
        PriorityFunction qosFunction = Mockito.mock(PriorityFunction.class);
        RpcScheduler scheduler = new SimpleRpcScheduler(conf, 10, 0, 0, qosFunction, 0);
        scheduler.init(CONTEXT);
        scheduler.start();
        CallRunner task = createMockTask();
        task.setStatus(new MonitoredRPCHandlerImpl());
        scheduler.dispatch(task);
        Mockito.verify(task, Mockito.timeout(10000)).run();
        scheduler.stop();
    }

    @Test
    public void testCallQueueInfo() throws IOException, InterruptedException {
        PriorityFunction qosFunction = Mockito.mock(PriorityFunction.class);
        RpcScheduler scheduler = new SimpleRpcScheduler(conf, 0, 0, 0, qosFunction, 0);
        scheduler.init(CONTEXT);
        // Set the handlers to zero. So that number of requests in call Queue can be tested
        scheduler = disableHandlers(scheduler);
        scheduler.start();
        int totalCallMethods = 10;
        for (int i = totalCallMethods; i > 0; i--) {
            CallRunner task = createMockTask();
            task.setStatus(new MonitoredRPCHandlerImpl());
            scheduler.dispatch(task);
        }
        CallQueueInfo callQueueInfo = scheduler.getCallQueueInfo();
        for (String callQueueName : callQueueInfo.getCallQueueNames()) {
            for (String calledMethod : callQueueInfo.getCalledMethodNames(callQueueName)) {
                Assert.assertEquals(totalCallMethods, callQueueInfo.getCallMethodCount(callQueueName, calledMethod));
            }
        }
        scheduler.stop();
    }

    @Test
    public void testHandlerIsolation() throws IOException, InterruptedException {
        CallRunner generalTask = createMockTask();
        CallRunner priorityTask = createMockTask();
        CallRunner replicationTask = createMockTask();
        List<CallRunner> tasks = ImmutableList.of(generalTask, priorityTask, replicationTask);
        Map<CallRunner, Integer> qos = ImmutableMap.of(generalTask, 0, priorityTask, ((HConstants.HIGH_QOS) + 1), replicationTask, REPLICATION_QOS);
        PriorityFunction qosFunction = Mockito.mock(PriorityFunction.class);
        final Map<CallRunner, Thread> handlerThreads = Maps.newHashMap();
        final CountDownLatch countDownLatch = new CountDownLatch(tasks.size());
        Answer<Void> answerToRun = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                synchronized(handlerThreads) {
                    handlerThreads.put(((CallRunner) (invocationOnMock.getMock())), Thread.currentThread());
                }
                countDownLatch.countDown();
                return null;
            }
        };
        for (CallRunner task : tasks) {
            task.setStatus(new MonitoredRPCHandlerImpl());
            Mockito.doAnswer(answerToRun).when(task).run();
        }
        RpcScheduler scheduler = new SimpleRpcScheduler(conf, 1, 1, 1, qosFunction, HConstants.HIGH_QOS);
        scheduler.init(CONTEXT);
        scheduler.start();
        for (CallRunner task : tasks) {
            Mockito.when(qosFunction.getPriority(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(qos.get(task));
            scheduler.dispatch(task);
        }
        for (CallRunner task : tasks) {
            Mockito.verify(task, Mockito.timeout(10000)).run();
        }
        scheduler.stop();
        // Tests that these requests are handled by three distinct threads.
        countDownLatch.await();
        Assert.assertEquals(3, ImmutableSet.copyOf(handlerThreads.values()).size());
    }

    @Test
    public void testRpcScheduler() throws Exception {
        testRpcScheduler(CALL_QUEUE_TYPE_DEADLINE_CONF_VALUE);
        testRpcScheduler(CALL_QUEUE_TYPE_FIFO_CONF_VALUE);
    }

    @Test
    public void testScanQueueWithZeroScanRatio() throws Exception {
        Configuration schedConf = HBaseConfiguration.create();
        schedConf.setFloat(CALL_QUEUE_HANDLER_FACTOR_CONF_KEY, 1.0F);
        schedConf.setFloat(CALL_QUEUE_READ_SHARE_CONF_KEY, 0.5F);
        schedConf.setFloat(CALL_QUEUE_SCAN_SHARE_CONF_KEY, 0.0F);
        PriorityFunction priority = Mockito.mock(PriorityFunction.class);
        Mockito.when(priority.getPriority(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(NORMAL_QOS);
        RpcScheduler scheduler = new SimpleRpcScheduler(schedConf, 2, 1, 1, priority, HConstants.QOS_THRESHOLD);
        Assert.assertNotEquals(null, scheduler);
    }

    @Test
    public void testScanQueues() throws Exception {
        Configuration schedConf = HBaseConfiguration.create();
        schedConf.setFloat(CALL_QUEUE_HANDLER_FACTOR_CONF_KEY, 1.0F);
        schedConf.setFloat(CALL_QUEUE_READ_SHARE_CONF_KEY, 0.7F);
        schedConf.setFloat(CALL_QUEUE_SCAN_SHARE_CONF_KEY, 0.5F);
        PriorityFunction priority = Mockito.mock(PriorityFunction.class);
        Mockito.when(priority.getPriority(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(NORMAL_QOS);
        RpcScheduler scheduler = new SimpleRpcScheduler(schedConf, 3, 1, 1, priority, HConstants.QOS_THRESHOLD);
        try {
            scheduler.start();
            CallRunner putCallTask = Mockito.mock(CallRunner.class);
            ServerCall putCall = Mockito.mock(ServerCall.class);
            putCall.param = RequestConverter.buildMutateRequest(Bytes.toBytes("abc"), new org.apache.hadoop.hbase.client.Put(Bytes.toBytes("row")));
            RequestHeader putHead = RequestHeader.newBuilder().setMethodName("mutate").build();
            Mockito.when(putCallTask.getRpcCall()).thenReturn(putCall);
            Mockito.when(putCall.getHeader()).thenReturn(putHead);
            Mockito.when(putCall.getParam()).thenReturn(putCall.param);
            CallRunner getCallTask = Mockito.mock(CallRunner.class);
            ServerCall getCall = Mockito.mock(ServerCall.class);
            RequestHeader getHead = RequestHeader.newBuilder().setMethodName("get").build();
            Mockito.when(getCallTask.getRpcCall()).thenReturn(getCall);
            Mockito.when(getCall.getHeader()).thenReturn(getHead);
            CallRunner scanCallTask = Mockito.mock(CallRunner.class);
            ServerCall scanCall = Mockito.mock(ServerCall.class);
            scanCall.param = ScanRequest.newBuilder().setScannerId(1).build();
            RequestHeader scanHead = RequestHeader.newBuilder().setMethodName("scan").build();
            Mockito.when(scanCallTask.getRpcCall()).thenReturn(scanCall);
            Mockito.when(scanCall.getHeader()).thenReturn(scanHead);
            Mockito.when(scanCall.getParam()).thenReturn(scanCall.param);
            ArrayList<Integer> work = new ArrayList<>();
            doAnswerTaskExecution(putCallTask, work, 1, 1000);
            doAnswerTaskExecution(getCallTask, work, 2, 1000);
            doAnswerTaskExecution(scanCallTask, work, 3, 1000);
            // There are 3 queues: [puts], [gets], [scans]
            // so the calls will be interleaved
            scheduler.dispatch(putCallTask);
            scheduler.dispatch(putCallTask);
            scheduler.dispatch(putCallTask);
            scheduler.dispatch(getCallTask);
            scheduler.dispatch(getCallTask);
            scheduler.dispatch(getCallTask);
            scheduler.dispatch(scanCallTask);
            scheduler.dispatch(scanCallTask);
            scheduler.dispatch(scanCallTask);
            while ((work.size()) < 6) {
                Thread.sleep(100);
            } 
            for (int i = 0; i < ((work.size()) - 2); i += 3) {
                Assert.assertNotEquals(work.get((i + 0)), work.get((i + 1)));
                Assert.assertNotEquals(work.get((i + 0)), work.get((i + 2)));
                Assert.assertNotEquals(work.get((i + 1)), work.get((i + 2)));
            }
        } finally {
            scheduler.stop();
        }
    }

    @Test
    public void testSoftAndHardQueueLimits() throws Exception {
        Configuration schedConf = HBaseConfiguration.create();
        schedConf.setInt(REGION_SERVER_HANDLER_COUNT, 0);
        schedConf.setInt("hbase.ipc.server.max.callqueue.length", 5);
        schedConf.set(CALL_QUEUE_TYPE_CONF_KEY, CALL_QUEUE_TYPE_DEADLINE_CONF_VALUE);
        PriorityFunction priority = Mockito.mock(PriorityFunction.class);
        Mockito.when(priority.getPriority(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(NORMAL_QOS);
        SimpleRpcScheduler scheduler = new SimpleRpcScheduler(schedConf, 0, 0, 0, priority, HConstants.QOS_THRESHOLD);
        try {
            scheduler.start();
            CallRunner putCallTask = Mockito.mock(CallRunner.class);
            ServerCall putCall = Mockito.mock(ServerCall.class);
            putCall.param = RequestConverter.buildMutateRequest(Bytes.toBytes("abc"), new org.apache.hadoop.hbase.client.Put(Bytes.toBytes("row")));
            RequestHeader putHead = RequestHeader.newBuilder().setMethodName("mutate").build();
            Mockito.when(putCallTask.getRpcCall()).thenReturn(putCall);
            Mockito.when(putCall.getHeader()).thenReturn(putHead);
            Assert.assertTrue(scheduler.dispatch(putCallTask));
            schedConf.setInt("hbase.ipc.server.max.callqueue.length", 0);
            scheduler.onConfigurationChange(schedConf);
            Assert.assertFalse(scheduler.dispatch(putCallTask));
            TestSimpleRpcScheduler.waitUntilQueueEmpty(scheduler);
            schedConf.setInt("hbase.ipc.server.max.callqueue.length", 1);
            scheduler.onConfigurationChange(schedConf);
            Assert.assertTrue(scheduler.dispatch(putCallTask));
        } finally {
            scheduler.stop();
        }
    }

    private static final class CoDelEnvironmentEdge implements EnvironmentEdge {
        private final BlockingQueue<Long> timeQ = new LinkedBlockingQueue<>();

        private long offset;

        private final Set<String> threadNamePrefixs = new HashSet<>();

        @Override
        public long currentTime() {
            for (String threadNamePrefix : threadNamePrefixs) {
                String threadName = Thread.currentThread().getName();
                if (threadName.startsWith(threadNamePrefix)) {
                    return (timeQ.poll().longValue()) + (offset);
                }
            }
            return System.currentTimeMillis();
        }
    }

    // FIX. I don't get this test (St.Ack). When I time this test, the minDelay is > 2 * codel delay from the get go.
    // So we are always overloaded. The test below would seem to complete the queuing of all the CallRunners inside
    // the codel check interval. I don't think we are skipping codel checking. Second, I think this test has been
    // broken since HBASE-16089 Add on FastPath for CoDel went in. The thread name we were looking for was the name
    // BEFORE we updated: i.e. "RpcServer.CodelBQ.default.handler". But same patch changed the name of the codel
    // fastpath thread to: new FastPathBalancedQueueRpcExecutor("CodelFPBQ.default", handlerCount, numCallQueues...
    // Codel is hard to test. This test is going to be flakey given it all timer-based. Disabling for now till chat
    // with authors.
    @Test
    public void testCoDelScheduling() throws Exception {
        TestSimpleRpcScheduler.CoDelEnvironmentEdge envEdge = new TestSimpleRpcScheduler.CoDelEnvironmentEdge();
        envEdge.threadNamePrefixs.add("RpcServer.default.FPBQ.Codel.handler");
        Configuration schedConf = HBaseConfiguration.create();
        schedConf.setInt(IPC_SERVER_MAX_CALLQUEUE_LENGTH, 250);
        schedConf.set(CALL_QUEUE_TYPE_CONF_KEY, CALL_QUEUE_TYPE_CODEL_CONF_VALUE);
        PriorityFunction priority = Mockito.mock(PriorityFunction.class);
        Mockito.when(priority.getPriority(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(NORMAL_QOS);
        SimpleRpcScheduler scheduler = new SimpleRpcScheduler(schedConf, 1, 1, 1, priority, HConstants.QOS_THRESHOLD);
        try {
            // Loading mocked call runner can take a good amount of time the first time through (haven't looked why).
            // Load it for first time here outside of the timed loop.
            getMockedCallRunner(System.currentTimeMillis(), 2);
            scheduler.start();
            EnvironmentEdgeManager.injectEdge(envEdge);
            envEdge.offset = 5;
            // Calls faster than min delay
            // LOG.info("Start");
            for (int i = 0; i < 100; i++) {
                long time = System.currentTimeMillis();
                envEdge.timeQ.put(time);
                CallRunner cr = getMockedCallRunner(time, 2);
                // LOG.info("" + i + " " + (System.currentTimeMillis() - now) + " cr=" + cr);
                scheduler.dispatch(cr);
            }
            // LOG.info("Loop done");
            // make sure fast calls are handled
            TestSimpleRpcScheduler.waitUntilQueueEmpty(scheduler);
            Thread.sleep(100);
            Assert.assertEquals("None of these calls should have been discarded", 0, scheduler.getNumGeneralCallsDropped());
            envEdge.offset = 151;
            // calls slower than min delay, but not individually slow enough to be dropped
            for (int i = 0; i < 20; i++) {
                long time = System.currentTimeMillis();
                envEdge.timeQ.put(time);
                CallRunner cr = getMockedCallRunner(time, 2);
                scheduler.dispatch(cr);
            }
            // make sure somewhat slow calls are handled
            TestSimpleRpcScheduler.waitUntilQueueEmpty(scheduler);
            Thread.sleep(100);
            Assert.assertEquals("None of these calls should have been discarded", 0, scheduler.getNumGeneralCallsDropped());
            envEdge.offset = 2000;
            // now slow calls and the ones to be dropped
            for (int i = 0; i < 60; i++) {
                long time = System.currentTimeMillis();
                envEdge.timeQ.put(time);
                CallRunner cr = getMockedCallRunner(time, 100);
                scheduler.dispatch(cr);
            }
            // make sure somewhat slow calls are handled
            TestSimpleRpcScheduler.waitUntilQueueEmpty(scheduler);
            Thread.sleep(100);
            Assert.assertTrue(("There should have been at least 12 calls dropped however there were " + (scheduler.getNumGeneralCallsDropped())), ((scheduler.getNumGeneralCallsDropped()) > 12));
        } finally {
            scheduler.stop();
        }
    }
}

