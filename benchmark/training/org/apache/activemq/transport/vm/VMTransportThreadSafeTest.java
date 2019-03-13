/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.vm;


import java.io.IOException;
import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.command.BaseCommand;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ShutdownInfo;
import org.apache.activemq.state.CommandVisitor;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import org.apache.activemq.transport.ResponseCorrelator;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportDisposedIOException;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VMTransportThreadSafeTest {
    private static final Logger LOG = LoggerFactory.getLogger(VMTransportThreadSafeTest.class);

    private static final String location1 = "vm://transport1";

    private static final String location2 = "vm://transport2";

    private final ConcurrentLinkedQueue<VMTransportThreadSafeTest.DummyCommand> localReceived = new ConcurrentLinkedQueue<VMTransportThreadSafeTest.DummyCommand>();

    private final ConcurrentLinkedQueue<VMTransportThreadSafeTest.DummyCommand> remoteReceived = new ConcurrentLinkedQueue<VMTransportThreadSafeTest.DummyCommand>();

    private class DummyCommand extends BaseCommand {
        public final int sequenceId;

        public DummyCommand() {
            this.sequenceId = 0;
        }

        public DummyCommand(int id) {
            this.sequenceId = id;
        }

        @Override
        public Response visit(CommandVisitor visitor) throws Exception {
            return null;
        }

        @Override
        public byte getDataStructureType() {
            return 42;
        }
    }

    private class VMTestTransportListener implements TransportListener {
        protected final Queue<VMTransportThreadSafeTest.DummyCommand> received;

        public boolean shutdownReceived = false;

        public VMTestTransportListener(Queue<VMTransportThreadSafeTest.DummyCommand> receiveQueue) {
            this.received = receiveQueue;
        }

        @Override
        public void onCommand(Object command) {
            if (command instanceof ShutdownInfo) {
                shutdownReceived = true;
            } else {
                received.add(((VMTransportThreadSafeTest.DummyCommand) (command)));
            }
        }

        @Override
        public void onException(IOException error) {
        }

        @Override
        public void transportInterupted() {
        }

        @Override
        public void transportResumed() {
        }
    }

    private class VMResponderTransportListener implements TransportListener {
        protected final Queue<VMTransportThreadSafeTest.DummyCommand> received;

        private final Transport peer;

        public VMResponderTransportListener(Queue<VMTransportThreadSafeTest.DummyCommand> receiveQueue, Transport peer) {
            this.received = receiveQueue;
            this.peer = peer;
        }

        @Override
        public void onCommand(Object command) {
            if (command instanceof ShutdownInfo) {
                return;
            } else {
                received.add(((VMTransportThreadSafeTest.DummyCommand) (command)));
                if ((peer) != null) {
                    try {
                        peer.oneway(command);
                    } catch (IOException e) {
                    }
                }
            }
        }

        @Override
        public void onException(IOException error) {
        }

        @Override
        public void transportInterupted() {
        }

        @Override
        public void transportResumed() {
        }
    }

    private class SlowVMTestTransportListener extends VMTransportThreadSafeTest.VMTestTransportListener {
        private final TimeUnit delayUnit;

        private final long delay;

        public SlowVMTestTransportListener(Queue<VMTransportThreadSafeTest.DummyCommand> receiveQueue) {
            this(receiveQueue, 10, TimeUnit.MILLISECONDS);
        }

        public SlowVMTestTransportListener(Queue<VMTransportThreadSafeTest.DummyCommand> receiveQueue, long delay, TimeUnit delayUnit) {
            super(receiveQueue);
            this.delay = delay;
            this.delayUnit = delayUnit;
        }

        @Override
        public void onCommand(Object command) {
            super.onCommand(command);
            try {
                delayUnit.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
    }

    private class GatedVMTestTransportListener extends VMTransportThreadSafeTest.VMTestTransportListener {
        private final CountDownLatch gate;

        public GatedVMTestTransportListener(Queue<VMTransportThreadSafeTest.DummyCommand> receiveQueue) {
            this(receiveQueue, new CountDownLatch(1));
        }

        public GatedVMTestTransportListener(Queue<VMTransportThreadSafeTest.DummyCommand> receiveQueue, CountDownLatch gate) {
            super(receiveQueue);
            this.gate = gate;
        }

        @Override
        public void onCommand(Object command) {
            super.onCommand(command);
            try {
                gate.await();
            } catch (InterruptedException e) {
            }
        }
    }

    @Test(timeout = 60000)
    public void testStartWthoutListenerIOE() throws Exception {
        final VMTransport local = new VMTransport(new URI(VMTransportThreadSafeTest.location1));
        final VMTransport remote = new VMTransport(new URI(VMTransportThreadSafeTest.location2));
        local.setPeer(remote);
        remote.setPeer(local);
        remote.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(localReceived));
        try {
            local.start();
            Assert.fail("Should have thrown an IOExcoption");
        } catch (IOException e) {
        }
    }

    @Test(timeout = 60000)
    public void testOnewayOnStoppedTransportTDE() throws Exception {
        final VMTransport local = new VMTransport(new URI(VMTransportThreadSafeTest.location1));
        final VMTransport remote = new VMTransport(new URI(VMTransportThreadSafeTest.location2));
        local.setPeer(remote);
        remote.setPeer(local);
        local.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(localReceived));
        remote.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(remoteReceived));
        local.start();
        local.stop();
        try {
            local.oneway(new VMTransportThreadSafeTest.DummyCommand());
            Assert.fail("Should have thrown a TransportDisposedException");
        } catch (TransportDisposedIOException e) {
        }
    }

    @Test(timeout = 60000)
    public void testStopSendsShutdownToPeer() throws Exception {
        final VMTransport local = new VMTransport(new URI(VMTransportThreadSafeTest.location1));
        final VMTransport remote = new VMTransport(new URI(VMTransportThreadSafeTest.location2));
        local.setPeer(remote);
        remote.setPeer(local);
        final VMTransportThreadSafeTest.VMTestTransportListener remoteListener = new VMTransportThreadSafeTest.VMTestTransportListener(remoteReceived);
        local.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(localReceived));
        remote.setTransportListener(remoteListener);
        local.start();
        local.stop();
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return remoteListener.shutdownReceived;
            }
        }));
    }

    @Test(timeout = 60000)
    public void testRemoteStopSendsExceptionToPendingRequests() throws Exception {
        final VMTransport local = new VMTransport(new URI(VMTransportThreadSafeTest.location1));
        final VMTransport remote = new VMTransport(new URI(VMTransportThreadSafeTest.location2));
        local.setPeer(remote);
        remote.setPeer(local);
        final VMTransportThreadSafeTest.VMTestTransportListener remoteListener = new VMTransportThreadSafeTest.VMTestTransportListener(remoteReceived);
        remote.setTransportListener(remoteListener);
        remote.start();
        final Response[] answer = new Response[1];
        ResponseCorrelator responseCorrelator = new ResponseCorrelator(local);
        responseCorrelator.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(localReceived));
        responseCorrelator.start();
        responseCorrelator.asyncRequest(new VMTransportThreadSafeTest.DummyCommand(), new ResponseCallback() {
            @Override
            public void onCompletion(FutureResponse resp) {
                try {
                    answer[0] = resp.getResult();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // simulate broker stop
        remote.stop();
        Assert.assertTrue("got expected exception response", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                VMTransportThreadSafeTest.LOG.info(("answer: " + (answer[0])));
                return ((answer[0]) instanceof ExceptionResponse) && ((getException()) instanceof TransportDisposedIOException);
            }
        }));
        local.stop();
    }

    @Test(timeout = 60000)
    public void testMultipleStartsAndStops() throws Exception {
        final VMTransport local = new VMTransport(new URI(VMTransportThreadSafeTest.location1));
        final VMTransport remote = new VMTransport(new URI(VMTransportThreadSafeTest.location2));
        local.setPeer(remote);
        remote.setPeer(local);
        local.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(localReceived));
        remote.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(remoteReceived));
        local.start();
        remote.start();
        local.start();
        remote.start();
        for (int i = 0; i < 100; ++i) {
            local.oneway(new VMTransportThreadSafeTest.DummyCommand());
        }
        for (int i = 0; i < 100; ++i) {
            remote.oneway(new VMTransportThreadSafeTest.DummyCommand());
        }
        local.start();
        remote.start();
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (remoteReceived.size()) == 100;
            }
        }));
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (localReceived.size()) == 100;
            }
        }));
        local.stop();
        local.stop();
        remote.stop();
        remote.stop();
    }

    @Test(timeout = 60000)
    public void testStartWithPeerNotStartedEnqueusCommandsNonAsync() throws Exception {
        doTestStartWithPeerNotStartedEnqueusCommands(false);
    }

    @Test(timeout = 60000)
    public void testBlockedOnewayEnqeueAandStopTransportAsync() throws Exception {
        doTestBlockedOnewayEnqeueAandStopTransport(true);
    }

    @Test(timeout = 60000)
    public void testBlockedOnewayEnqeueAandStopTransportNonAsync() throws Exception {
        doTestBlockedOnewayEnqeueAandStopTransport(false);
    }

    @Test(timeout = 60000)
    public void testBlockedOnewayEnqeueWhileStartedDetectsStop() throws Exception {
        final VMTransport local = new VMTransport(new URI(VMTransportThreadSafeTest.location1));
        final VMTransport remote = new VMTransport(new URI(VMTransportThreadSafeTest.location2));
        final AtomicInteger sequenceId = new AtomicInteger();
        remote.setAsync(true);
        remote.setAsyncQueueDepth(2);
        local.setPeer(remote);
        remote.setPeer(local);
        local.setTransportListener(new VMTransportThreadSafeTest.VMTestTransportListener(localReceived));
        remote.setTransportListener(new VMTransportThreadSafeTest.GatedVMTestTransportListener(remoteReceived));
        local.start();
        remote.start();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; ++i) {
                    try {
                        local.oneway(new VMTransportThreadSafeTest.DummyCommand(sequenceId.incrementAndGet()));
                    } catch (Exception e) {
                    }
                }
            }
        });
        t.start();
        VMTransportThreadSafeTest.LOG.debug("Started async delivery, wait for remote's queue to fill up");
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (remote.getMessageQueue().remainingCapacity()) == 0;
            }
        }));
        VMTransportThreadSafeTest.LOG.debug("Starting async gate open.");
        Thread gateman = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
                ((VMTransportThreadSafeTest.GatedVMTestTransportListener) (remote.getTransportListener())).gate.countDown();
            }
        });
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (remoteReceived.size()) == 1;
            }
        }));
        gateman.start();
        remote.stop();
        local.stop();
        Assert.assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (remoteReceived.size()) == 1;
            }
        }));
        assertMessageAreOrdered(remoteReceived);
    }

    @Test(timeout = 60000)
    public void testStopWhileStartingAsyncWithNoAsyncLimit() throws Exception {
        // In the async case the iterate method should see that we are stopping and
        // drop out before we dispatch all the messages but it should get at least 49 since
        // the stop thread waits 500 mills and the listener is waiting 10 mills on each receive.
        doTestStopWhileStartingWithNoAsyncLimit(true, 49);
    }

    @Test(timeout = 60000)
    public void testStopWhileStartingNonAsyncWithNoAsyncLimit() throws Exception {
        // In the non-async case the start dispatches all messages up front and then continues on
        doTestStopWhileStartingWithNoAsyncLimit(false, 100);
    }

    @Test(timeout = 120000)
    public void TestTwoWayMessageThroughPutSync() throws Exception {
        long totalTimes = 0;
        final long executions = 20;
        for (int i = 0; i < 20; ++i) {
            totalTimes += doTestTwoWayMessageThroughPut(false);
        }
        VMTransportThreadSafeTest.LOG.info((("Total time of one way sync send throughput test: " + (totalTimes / executions)) + "ms"));
    }

    @Test(timeout = 120000)
    public void TestTwoWayMessageThroughPutAsnyc() throws Exception {
        long totalTimes = 0;
        final long executions = 50;
        for (int i = 0; i < executions; ++i) {
            totalTimes += doTestTwoWayMessageThroughPut(false);
        }
        VMTransportThreadSafeTest.LOG.info((("Total time of one way async send throughput test: " + (totalTimes / executions)) + "ms"));
    }

    @Test(timeout = 120000)
    public void TestOneWayMessageThroughPutSync() throws Exception {
        long totalTimes = 0;
        final long executions = 30;
        for (int i = 0; i < executions; ++i) {
            totalTimes += doTestOneWayMessageThroughPut(false);
        }
        VMTransportThreadSafeTest.LOG.info((("Total time of one way sync send throughput test: " + (totalTimes / executions)) + "ms"));
    }

    @Test(timeout = 120000)
    public void TestOneWayMessageThroughPutAsnyc() throws Exception {
        long totalTimes = 0;
        final long executions = 20;
        for (int i = 0; i < 20; ++i) {
            totalTimes += doTestOneWayMessageThroughPut(true);
        }
        VMTransportThreadSafeTest.LOG.info((("Total time of one way async send throughput test: " + (totalTimes / executions)) + "ms"));
    }

    @Test(timeout = 120000)
    public void testTwoWayTrafficWithMutexTransportSync1() throws Exception {
        for (int i = 0; i < 20; ++i) {
            doTestTwoWayTrafficWithMutexTransport(false, false);
        }
    }

    @Test(timeout = 120000)
    public void testTwoWayTrafficWithMutexTransportSync2() throws Exception {
        for (int i = 0; i < 20; ++i) {
            doTestTwoWayTrafficWithMutexTransport(true, false);
        }
    }

    @Test(timeout = 120000)
    public void testTwoWayTrafficWithMutexTransportSync3() throws Exception {
        for (int i = 0; i < 20; ++i) {
            doTestTwoWayTrafficWithMutexTransport(false, true);
        }
    }

    @Test(timeout = 120000)
    public void testTwoWayTrafficWithMutexTransportSync4() throws Exception {
        for (int i = 0; i < 20; ++i) {
            doTestTwoWayTrafficWithMutexTransport(false, false);
        }
    }
}

