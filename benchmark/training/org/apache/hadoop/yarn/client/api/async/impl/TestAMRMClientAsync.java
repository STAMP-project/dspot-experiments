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
package org.apache.hadoop.yarn.client.api.async.impl;


import AMRMClientAsync.AbstractCallbackHandler;
import ContainerState.COMPLETE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.UpdatedContainer;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.impl.AMRMClientImpl;
import org.apache.hadoop.yarn.exceptions.ApplicationAttemptNotFoundException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAMRMClientAsync {
    private static final Logger LOG = LoggerFactory.getLogger(TestAMRMClientAsync.class);

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testAMRMClientAsync() throws Exception {
        Configuration conf = new Configuration();
        final AtomicBoolean heartbeatBlock = new AtomicBoolean(true);
        List<ContainerStatus> completed1 = Arrays.asList(ContainerStatus.newInstance(TestAMRMClientAsync.newContainerId(0, 0, 0, 0), COMPLETE, "", 0));
        List<Container> containers = Arrays.asList(Container.newInstance(null, null, null, null, null, null));
        final AllocateResponse response1 = createAllocateResponse(new ArrayList<ContainerStatus>(), containers, null);
        final AllocateResponse response2 = createAllocateResponse(completed1, new ArrayList<Container>(), null);
        final AllocateResponse response3 = createAllocateResponse(new ArrayList<ContainerStatus>(), new ArrayList<Container>(), containers, containers, null);
        final AllocateResponse emptyResponse = createAllocateResponse(new ArrayList<ContainerStatus>(), new ArrayList<Container>(), null);
        TestAMRMClientAsync.TestCallbackHandler callbackHandler = new TestAMRMClientAsync.TestCallbackHandler();
        final AMRMClient<ContainerRequest> client = Mockito.mock(AMRMClientImpl.class);
        final AtomicInteger secondHeartbeatSync = new AtomicInteger(0);
        Mockito.when(client.allocate(ArgumentMatchers.anyFloat())).thenReturn(response1).thenAnswer(new Answer<AllocateResponse>() {
            @Override
            public AllocateResponse answer(InvocationOnMock invocation) throws Throwable {
                secondHeartbeatSync.incrementAndGet();
                while (heartbeatBlock.get()) {
                    synchronized(heartbeatBlock) {
                        heartbeatBlock.wait();
                    }
                } 
                secondHeartbeatSync.incrementAndGet();
                return response2;
            }
        }).thenReturn(response3).thenReturn(emptyResponse);
        Mockito.when(client.registerApplicationMaster(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(client.getAvailableResources()).thenAnswer(new Answer<Resource>() {
            @Override
            public Resource answer(InvocationOnMock invocation) throws Throwable {
                // take client lock to simulate behavior of real impl
                synchronized(client) {
                    Thread.sleep(10);
                }
                return null;
            }
        });
        AMRMClientAsync<ContainerRequest> asyncClient = AMRMClientAsync.createAMRMClientAsync(client, 20, callbackHandler);
        asyncClient.init(conf);
        asyncClient.start();
        asyncClient.registerApplicationMaster("localhost", 1234, null);
        // while the CallbackHandler will still only be processing the first response,
        // heartbeater thread should still be sending heartbeats.
        // To test this, wait for the second heartbeat to be received.
        while ((secondHeartbeatSync.get()) < 1) {
            Thread.sleep(10);
        } 
        // heartbeat will be blocked. make sure we can call client methods at this
        // time. Checks that heartbeat is not holding onto client lock
        assert (secondHeartbeatSync.get()) < 2;
        asyncClient.getAvailableResources();
        // method returned. now unblock heartbeat
        assert (secondHeartbeatSync.get()) < 2;
        synchronized(heartbeatBlock) {
            heartbeatBlock.set(false);
            heartbeatBlock.notifyAll();
        }
        // allocated containers should come before completed containers
        Assert.assertEquals(null, callbackHandler.takeCompletedContainers());
        // wait for the allocated containers from the first heartbeat's response
        while ((callbackHandler.takeAllocatedContainers()) == null) {
            Assert.assertEquals(null, callbackHandler.takeCompletedContainers());
            Thread.sleep(10);
        } 
        // wait for the completed containers from the second heartbeat's response
        while ((callbackHandler.takeCompletedContainers()) == null) {
            Thread.sleep(10);
        } 
        // wait for the changed containers from the thrid heartbeat's response
        while ((callbackHandler.takeChangedContainers()) == null) {
            Thread.sleep(10);
        } 
        asyncClient.stop();
        Assert.assertEquals(null, callbackHandler.takeAllocatedContainers());
        Assert.assertEquals(null, callbackHandler.takeCompletedContainers());
        Assert.assertEquals(null, callbackHandler.takeChangedContainers());
    }

    @Test(timeout = 10000)
    public void testAMRMClientAsyncException() throws Exception {
        String exStr = "TestException";
        YarnException mockException = Mockito.mock(YarnException.class);
        Mockito.when(mockException.getMessage()).thenReturn(exStr);
        runHeartBeatThrowOutException(mockException);
    }

    @Test(timeout = 10000)
    public void testAMRMClientAsyncRunTimeException() throws Exception {
        String exStr = "TestRunTimeException";
        RuntimeException mockRunTimeException = Mockito.mock(RuntimeException.class);
        Mockito.when(mockRunTimeException.getMessage()).thenReturn(exStr);
        runHeartBeatThrowOutException(mockRunTimeException);
    }

    @Test(timeout = 10000)
    public void testAMRMClientAsyncShutDown() throws Exception {
        Configuration conf = new Configuration();
        TestAMRMClientAsync.TestCallbackHandler callbackHandler = new TestAMRMClientAsync.TestCallbackHandler();
        @SuppressWarnings("unchecked")
        AMRMClient<ContainerRequest> client = Mockito.mock(AMRMClientImpl.class);
        createAllocateResponse(new ArrayList<ContainerStatus>(), new ArrayList<Container>(), null);
        Mockito.when(client.allocate(ArgumentMatchers.anyFloat())).thenThrow(new ApplicationAttemptNotFoundException("app not found, shut down"));
        AMRMClientAsync<ContainerRequest> asyncClient = AMRMClientAsync.createAMRMClientAsync(client, 10, callbackHandler);
        asyncClient.init(conf);
        asyncClient.start();
        asyncClient.registerApplicationMaster("localhost", 1234, null);
        Thread.sleep(50);
        Mockito.verify(client, Mockito.times(1)).allocate(ArgumentMatchers.anyFloat());
        asyncClient.stop();
    }

    @Test(timeout = 10000)
    public void testAMRMClientAsyncShutDownWithWaitFor() throws Exception {
        Configuration conf = new Configuration();
        final TestAMRMClientAsync.TestCallbackHandler callbackHandler = new TestAMRMClientAsync.TestCallbackHandler();
        @SuppressWarnings("unchecked")
        AMRMClient<ContainerRequest> client = Mockito.mock(AMRMClientImpl.class);
        Mockito.when(client.allocate(ArgumentMatchers.anyFloat())).thenThrow(new ApplicationAttemptNotFoundException("app not found, shut down"));
        AMRMClientAsync<ContainerRequest> asyncClient = AMRMClientAsync.createAMRMClientAsync(client, 10, callbackHandler);
        asyncClient.init(conf);
        asyncClient.start();
        Supplier<Boolean> checker = new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return callbackHandler.reboot;
            }
        };
        asyncClient.registerApplicationMaster("localhost", 1234, null);
        asyncClient.waitFor(checker);
        asyncClient.stop();
        // stopping should have joined all threads and completed all callbacks
        Assert.assertTrue(((callbackHandler.callbackCount) == 0));
        Mockito.verify(client, Mockito.times(1)).allocate(ArgumentMatchers.anyFloat());
        asyncClient.stop();
    }

    @Test(timeout = 5000)
    public void testCallAMRMClientAsyncStopFromCallbackHandler() throws IOException, InterruptedException, YarnException {
        Configuration conf = new Configuration();
        TestAMRMClientAsync.TestCallbackHandler2 callbackHandler = new TestAMRMClientAsync.TestCallbackHandler2();
        @SuppressWarnings("unchecked")
        AMRMClient<ContainerRequest> client = Mockito.mock(AMRMClientImpl.class);
        List<ContainerStatus> completed = Arrays.asList(ContainerStatus.newInstance(TestAMRMClientAsync.newContainerId(0, 0, 0, 0), COMPLETE, "", 0));
        final AllocateResponse response = createAllocateResponse(completed, new ArrayList<Container>(), null);
        Mockito.when(client.allocate(ArgumentMatchers.anyFloat())).thenReturn(response);
        AMRMClientAsync<ContainerRequest> asyncClient = AMRMClientAsync.createAMRMClientAsync(client, 20, callbackHandler);
        callbackHandler.asynClient = asyncClient;
        asyncClient.init(conf);
        asyncClient.start();
        synchronized(callbackHandler.notifier) {
            asyncClient.registerApplicationMaster("localhost", 1234, null);
            while ((callbackHandler.notify) == false) {
                try {
                    callbackHandler.notifier.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } 
        }
    }

    @Test(timeout = 5000)
    public void testCallAMRMClientAsyncStopFromCallbackHandlerWithWaitFor() throws IOException, InterruptedException, YarnException {
        Configuration conf = new Configuration();
        final TestAMRMClientAsync.TestCallbackHandler2 callbackHandler = new TestAMRMClientAsync.TestCallbackHandler2();
        @SuppressWarnings("unchecked")
        AMRMClient<ContainerRequest> client = Mockito.mock(AMRMClientImpl.class);
        List<ContainerStatus> completed = Arrays.asList(ContainerStatus.newInstance(TestAMRMClientAsync.newContainerId(0, 0, 0, 0), COMPLETE, "", 0));
        final AllocateResponse response = createAllocateResponse(completed, new ArrayList<Container>(), null);
        Mockito.when(client.allocate(ArgumentMatchers.anyFloat())).thenReturn(response);
        AMRMClientAsync<ContainerRequest> asyncClient = AMRMClientAsync.createAMRMClientAsync(client, 20, callbackHandler);
        callbackHandler.asynClient = asyncClient;
        asyncClient.init(conf);
        asyncClient.start();
        Supplier<Boolean> checker = new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return callbackHandler.notify;
            }
        };
        asyncClient.registerApplicationMaster("localhost", 1234, null);
        asyncClient.waitFor(checker);
        Assert.assertTrue(checker.get());
    }

    @Test(timeout = 5000)
    public void testCallBackThrowOutException() throws IOException, InterruptedException, YarnException {
        // test exception in callback with app calling stop() on app.onError()
        TestAMRMClientAsync.TestCallbackHandler2 callbackHandler = Mockito.spy(new TestAMRMClientAsync.TestCallbackHandler2());
        runCallBackThrowOutException(callbackHandler);
    }

    @Test(timeout = 5000)
    public void testCallBackThrowOutExceptionNoStop() throws IOException, InterruptedException, YarnException {
        // test exception in callback with app not calling stop() on app.onError()
        TestAMRMClientAsync.TestCallbackHandler2 callbackHandler = Mockito.spy(new TestAMRMClientAsync.TestCallbackHandler2());
        callbackHandler.stop = false;
        runCallBackThrowOutException(callbackHandler);
    }

    private class TestCallbackHandler extends AMRMClientAsync.AbstractCallbackHandler {
        private volatile List<ContainerStatus> completedContainers;

        private volatile List<Container> allocatedContainers;

        private final List<UpdatedContainer> changedContainers = new ArrayList<>();

        Exception savedException = null;

        volatile boolean reboot = false;

        Object notifier = new Object();

        int callbackCount = 0;

        public List<ContainerStatus> takeCompletedContainers() {
            List<ContainerStatus> ret = completedContainers;
            if (ret == null) {
                return null;
            }
            completedContainers = null;
            synchronized(ret) {
                ret.notify();
            }
            return ret;
        }

        public List<UpdatedContainer> takeChangedContainers() {
            List<UpdatedContainer> ret = null;
            synchronized(changedContainers) {
                if (!(changedContainers.isEmpty())) {
                    ret = new ArrayList(changedContainers);
                    changedContainers.clear();
                    changedContainers.notify();
                }
            }
            return ret;
        }

        public List<Container> takeAllocatedContainers() {
            List<Container> ret = allocatedContainers;
            if (ret == null) {
                return null;
            }
            allocatedContainers = null;
            synchronized(ret) {
                ret.notify();
            }
            return ret;
        }

        @Override
        public void onContainersCompleted(List<ContainerStatus> statuses) {
            completedContainers = statuses;
            // wait for containers to be taken before returning
            synchronized(completedContainers) {
                while ((completedContainers) != null) {
                    try {
                        completedContainers.wait();
                    } catch (InterruptedException ex) {
                        TestAMRMClientAsync.LOG.error("Interrupted during wait", ex);
                    }
                } 
            }
        }

        @Override
        public void onContainersUpdated(List<UpdatedContainer> changed) {
            synchronized(changedContainers) {
                changedContainers.clear();
                changedContainers.addAll(changed);
                while (!(changedContainers.isEmpty())) {
                    try {
                        changedContainers.wait();
                    } catch (InterruptedException ex) {
                        TestAMRMClientAsync.LOG.error("Interrupted during wait", ex);
                    }
                } 
            }
        }

        @Override
        public void onContainersAllocated(List<Container> containers) {
            allocatedContainers = containers;
            // wait for containers to be taken before returning
            synchronized(allocatedContainers) {
                while ((allocatedContainers) != null) {
                    try {
                        allocatedContainers.wait();
                    } catch (InterruptedException ex) {
                        TestAMRMClientAsync.LOG.error("Interrupted during wait", ex);
                    }
                } 
            }
        }

        @Override
        public void onShutdownRequest() {
            reboot = true;
            synchronized(notifier) {
                notifier.notifyAll();
            }
        }

        @Override
        public void onNodesUpdated(List<NodeReport> updatedNodes) {
        }

        @Override
        public float getProgress() {
            (callbackCount)++;
            return 0.5F;
        }

        @Override
        public void onError(Throwable e) {
            savedException = new Exception(e.getMessage());
            synchronized(notifier) {
                notifier.notifyAll();
            }
        }
    }

    private class TestCallbackHandler2 extends AMRMClientAsync.AbstractCallbackHandler {
        Object notifier = new Object();

        @SuppressWarnings("rawtypes")
        AMRMClientAsync asynClient;

        boolean stop = true;

        volatile boolean notify = false;

        boolean throwOutException = false;

        @Override
        public void onContainersCompleted(List<ContainerStatus> statuses) {
            if (throwOutException) {
                throw new YarnRuntimeException("Exception from callback handler");
            }
        }

        @Override
        public void onContainersAllocated(List<Container> containers) {
        }

        @Override
        public void onContainersUpdated(List<UpdatedContainer> containers) {
        }

        @Override
        public void onShutdownRequest() {
        }

        @Override
        public void onNodesUpdated(List<NodeReport> updatedNodes) {
        }

        @Override
        public float getProgress() {
            callStopAndNotify();
            return 0;
        }

        @Override
        public void onError(Throwable e) {
            Assert.assertEquals(e.getMessage(), "Exception from callback handler");
            callStopAndNotify();
        }

        void callStopAndNotify() {
            if (stop) {
                asynClient.stop();
            }
            notify = true;
            synchronized(notifier) {
                notifier.notifyAll();
            }
        }
    }
}

