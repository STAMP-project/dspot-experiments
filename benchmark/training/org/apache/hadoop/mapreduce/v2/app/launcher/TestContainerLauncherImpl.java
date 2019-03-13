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
package org.apache.hadoop.mapreduce.v2.app.launcher;


import EventType.CONTAINER_REMOTE_CLEANUP;
import EventType.CONTAINER_REMOTE_LAUNCH;
import TaskType.MAP;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.CommitResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ContainerUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ContainerUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetLocalizationStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetLocalizationStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.IncreaseContainersResourceRequest;
import org.apache.hadoop.yarn.api.protocolrecords.IncreaseContainersResourceResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReInitializeContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReInitializeContainerResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ResourceLocalizationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ResourceLocalizationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RestartContainerResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RollbackResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SignalContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.SignalContainerResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.client.api.impl.ContainerManagementProtocolProxy.ContainerManagementProtocolProxyData;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestContainerLauncherImpl {
    static final Logger LOG = LoggerFactory.getLogger(TestContainerLauncherImpl.class);

    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    private Map<String, ByteBuffer> serviceResponse = new HashMap<String, ByteBuffer>();

    // tests here mock ContainerManagementProtocol which does not have close
    // method. creating an interface that implements ContainerManagementProtocol
    // and Closeable so the tests does not fail with NoSuchMethodException
    private static interface ContainerManagementProtocolClient extends Closeable , ContainerManagementProtocol {}

    private static class ContainerLauncherImplUnderTest extends ContainerLauncherImpl {
        private ContainerManagementProtocol containerManager;

        public ContainerLauncherImplUnderTest(AppContext context, ContainerManagementProtocol containerManager) {
            super(context);
            this.containerManager = containerManager;
        }

        @Override
        public ContainerManagementProtocolProxyData getCMProxy(String containerMgrBindAddr, ContainerId containerId) throws IOException {
            ContainerManagementProtocolProxyData protocolProxy = Mockito.mock(ContainerManagementProtocolProxyData.class);
            Mockito.when(protocolProxy.getContainerManagementProtocol()).thenReturn(containerManager);
            return protocolProxy;
        }

        public void waitForPoolToIdle() throws InterruptedException {
            // I wish that we did not need the sleep, but it is here so that we are sure
            // That the other thread had time to insert the event into the queue and
            // start processing it.  For some reason we were getting interrupted
            // exceptions within eventQueue without this sleep.
            Thread.sleep(100L);
            TestContainerLauncherImpl.LOG.debug(((((("POOL SIZE 1: " + (this.eventQueue.size())) + " POOL SIZE 2: ") + (this.launcherPool.getQueue().size())) + " ACTIVE COUNT: ") + (this.launcherPool.getActiveCount())));
            while (((!(this.eventQueue.isEmpty())) || (!(this.launcherPool.getQueue().isEmpty()))) || ((this.launcherPool.getActiveCount()) > 0)) {
                Thread.sleep(100L);
                TestContainerLauncherImpl.LOG.debug(((((("POOL SIZE 1: " + (this.eventQueue.size())) + " POOL SIZE 2: ") + (this.launcherPool.getQueue().size())) + " ACTIVE COUNT: ") + (this.launcherPool.getActiveCount())));
            } 
            TestContainerLauncherImpl.LOG.debug(((((("POOL SIZE 1: " + (this.eventQueue.size())) + " POOL SIZE 2: ") + (this.launcherPool.getQueue().size())) + " ACTIVE COUNT: ") + (this.launcherPool.getActiveCount())));
        }
    }

    @Test(timeout = 5000)
    public void testHandle() throws Exception {
        TestContainerLauncherImpl.LOG.info("STARTING testHandle");
        AppContext mockContext = Mockito.mock(AppContext.class);
        @SuppressWarnings("unchecked")
        EventHandler<Event> mockEventHandler = Mockito.mock(EventHandler.class);
        Mockito.when(mockContext.getEventHandler()).thenReturn(mockEventHandler);
        String cmAddress = "127.0.0.1:8000";
        TestContainerLauncherImpl.ContainerManagementProtocolClient mockCM = Mockito.mock(TestContainerLauncherImpl.ContainerManagementProtocolClient.class);
        TestContainerLauncherImpl.ContainerLauncherImplUnderTest ut = new TestContainerLauncherImpl.ContainerLauncherImplUnderTest(mockContext, mockCM);
        Configuration conf = new Configuration();
        ut.init(conf);
        start();
        try {
            ContainerId contId = TestContainerLauncherImpl.makeContainerId(0L, 0, 0, 1);
            TaskAttemptId taskAttemptId = TestContainerLauncherImpl.makeTaskAttemptId(0L, 0, 0, MAP, 0);
            StartContainersResponse startResp = TestContainerLauncherImpl.recordFactory.newRecordInstance(StartContainersResponse.class);
            startResp.setAllServicesMetaData(serviceResponse);
            TestContainerLauncherImpl.LOG.info("inserting launch event");
            ContainerRemoteLaunchEvent mockLaunchEvent = Mockito.mock(ContainerRemoteLaunchEvent.class);
            Mockito.when(mockLaunchEvent.getType()).thenReturn(CONTAINER_REMOTE_LAUNCH);
            Mockito.when(mockLaunchEvent.getContainerID()).thenReturn(contId);
            Mockito.when(mockLaunchEvent.getTaskAttemptID()).thenReturn(taskAttemptId);
            Mockito.when(mockLaunchEvent.getContainerMgrAddress()).thenReturn(cmAddress);
            Mockito.when(startContainers(ArgumentMatchers.any(StartContainersRequest.class))).thenReturn(startResp);
            Mockito.when(mockLaunchEvent.getContainerToken()).thenReturn(createNewContainerToken(contId, cmAddress));
            ut.handle(mockLaunchEvent);
            ut.waitForPoolToIdle();
            startContainers(ArgumentMatchers.any(StartContainersRequest.class));
            TestContainerLauncherImpl.LOG.info("inserting cleanup event");
            ContainerLauncherEvent mockCleanupEvent = Mockito.mock(ContainerLauncherEvent.class);
            Mockito.when(mockCleanupEvent.getType()).thenReturn(CONTAINER_REMOTE_CLEANUP);
            Mockito.when(mockCleanupEvent.getContainerID()).thenReturn(contId);
            Mockito.when(mockCleanupEvent.getTaskAttemptID()).thenReturn(taskAttemptId);
            Mockito.when(mockCleanupEvent.getContainerMgrAddress()).thenReturn(cmAddress);
            ut.handle(mockCleanupEvent);
            ut.waitForPoolToIdle();
            stopContainers(ArgumentMatchers.any(StopContainersRequest.class));
        } finally {
            stop();
        }
    }

    @Test(timeout = 5000)
    public void testOutOfOrder() throws Exception {
        TestContainerLauncherImpl.LOG.info("STARTING testOutOfOrder");
        AppContext mockContext = Mockito.mock(AppContext.class);
        @SuppressWarnings("unchecked")
        EventHandler<Event> mockEventHandler = Mockito.mock(EventHandler.class);
        Mockito.when(mockContext.getEventHandler()).thenReturn(mockEventHandler);
        TestContainerLauncherImpl.ContainerManagementProtocolClient mockCM = Mockito.mock(TestContainerLauncherImpl.ContainerManagementProtocolClient.class);
        TestContainerLauncherImpl.ContainerLauncherImplUnderTest ut = new TestContainerLauncherImpl.ContainerLauncherImplUnderTest(mockContext, mockCM);
        Configuration conf = new Configuration();
        ut.init(conf);
        start();
        try {
            ContainerId contId = TestContainerLauncherImpl.makeContainerId(0L, 0, 0, 1);
            TaskAttemptId taskAttemptId = TestContainerLauncherImpl.makeTaskAttemptId(0L, 0, 0, MAP, 0);
            String cmAddress = "127.0.0.1:8000";
            StartContainersResponse startResp = TestContainerLauncherImpl.recordFactory.newRecordInstance(StartContainersResponse.class);
            startResp.setAllServicesMetaData(serviceResponse);
            TestContainerLauncherImpl.LOG.info("inserting cleanup event");
            ContainerLauncherEvent mockCleanupEvent = Mockito.mock(ContainerLauncherEvent.class);
            Mockito.when(mockCleanupEvent.getType()).thenReturn(CONTAINER_REMOTE_CLEANUP);
            Mockito.when(mockCleanupEvent.getContainerID()).thenReturn(contId);
            Mockito.when(mockCleanupEvent.getTaskAttemptID()).thenReturn(taskAttemptId);
            Mockito.when(mockCleanupEvent.getContainerMgrAddress()).thenReturn(cmAddress);
            ut.handle(mockCleanupEvent);
            ut.waitForPoolToIdle();
            stopContainers(ArgumentMatchers.any(StopContainersRequest.class));
            TestContainerLauncherImpl.LOG.info("inserting launch event");
            ContainerRemoteLaunchEvent mockLaunchEvent = Mockito.mock(ContainerRemoteLaunchEvent.class);
            Mockito.when(mockLaunchEvent.getType()).thenReturn(CONTAINER_REMOTE_LAUNCH);
            Mockito.when(mockLaunchEvent.getContainerID()).thenReturn(contId);
            Mockito.when(mockLaunchEvent.getTaskAttemptID()).thenReturn(taskAttemptId);
            Mockito.when(mockLaunchEvent.getContainerMgrAddress()).thenReturn(cmAddress);
            Mockito.when(startContainers(ArgumentMatchers.any(StartContainersRequest.class))).thenReturn(startResp);
            Mockito.when(mockLaunchEvent.getContainerToken()).thenReturn(createNewContainerToken(contId, cmAddress));
            ut.handle(mockLaunchEvent);
            ut.waitForPoolToIdle();
            startContainers(ArgumentMatchers.any(StartContainersRequest.class));
        } finally {
            stop();
        }
    }

    @Test(timeout = 5000)
    public void testMyShutdown() throws Exception {
        TestContainerLauncherImpl.LOG.info("in test Shutdown");
        AppContext mockContext = Mockito.mock(AppContext.class);
        @SuppressWarnings("unchecked")
        EventHandler<Event> mockEventHandler = Mockito.mock(EventHandler.class);
        Mockito.when(mockContext.getEventHandler()).thenReturn(mockEventHandler);
        TestContainerLauncherImpl.ContainerManagementProtocolClient mockCM = Mockito.mock(TestContainerLauncherImpl.ContainerManagementProtocolClient.class);
        TestContainerLauncherImpl.ContainerLauncherImplUnderTest ut = new TestContainerLauncherImpl.ContainerLauncherImplUnderTest(mockContext, mockCM);
        Configuration conf = new Configuration();
        ut.init(conf);
        start();
        try {
            ContainerId contId = TestContainerLauncherImpl.makeContainerId(0L, 0, 0, 1);
            TaskAttemptId taskAttemptId = TestContainerLauncherImpl.makeTaskAttemptId(0L, 0, 0, MAP, 0);
            String cmAddress = "127.0.0.1:8000";
            StartContainersResponse startResp = TestContainerLauncherImpl.recordFactory.newRecordInstance(StartContainersResponse.class);
            startResp.setAllServicesMetaData(serviceResponse);
            TestContainerLauncherImpl.LOG.info("inserting launch event");
            ContainerRemoteLaunchEvent mockLaunchEvent = Mockito.mock(ContainerRemoteLaunchEvent.class);
            Mockito.when(mockLaunchEvent.getType()).thenReturn(CONTAINER_REMOTE_LAUNCH);
            Mockito.when(mockLaunchEvent.getContainerID()).thenReturn(contId);
            Mockito.when(mockLaunchEvent.getTaskAttemptID()).thenReturn(taskAttemptId);
            Mockito.when(mockLaunchEvent.getContainerMgrAddress()).thenReturn(cmAddress);
            Mockito.when(startContainers(ArgumentMatchers.any(StartContainersRequest.class))).thenReturn(startResp);
            Mockito.when(mockLaunchEvent.getContainerToken()).thenReturn(createNewContainerToken(contId, cmAddress));
            ut.handle(mockLaunchEvent);
            ut.waitForPoolToIdle();
            startContainers(ArgumentMatchers.any(StartContainersRequest.class));
            // skip cleanup and make sure stop kills the container
        } finally {
            stop();
            stopContainers(ArgumentMatchers.any(StopContainersRequest.class));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(timeout = 5000)
    public void testContainerCleaned() throws Exception {
        TestContainerLauncherImpl.LOG.info("STARTING testContainerCleaned");
        CyclicBarrier startLaunchBarrier = new CyclicBarrier(2);
        CyclicBarrier completeLaunchBarrier = new CyclicBarrier(2);
        AppContext mockContext = Mockito.mock(AppContext.class);
        EventHandler mockEventHandler = Mockito.mock(EventHandler.class);
        Mockito.when(mockContext.getEventHandler()).thenReturn(mockEventHandler);
        TestContainerLauncherImpl.ContainerManagementProtocolClient mockCM = new TestContainerLauncherImpl.ContainerManagerForTest(startLaunchBarrier, completeLaunchBarrier);
        TestContainerLauncherImpl.ContainerLauncherImplUnderTest ut = new TestContainerLauncherImpl.ContainerLauncherImplUnderTest(mockContext, mockCM);
        Configuration conf = new Configuration();
        ut.init(conf);
        start();
        try {
            ContainerId contId = TestContainerLauncherImpl.makeContainerId(0L, 0, 0, 1);
            TaskAttemptId taskAttemptId = TestContainerLauncherImpl.makeTaskAttemptId(0L, 0, 0, MAP, 0);
            String cmAddress = "127.0.0.1:8000";
            StartContainersResponse startResp = TestContainerLauncherImpl.recordFactory.newRecordInstance(StartContainersResponse.class);
            startResp.setAllServicesMetaData(serviceResponse);
            TestContainerLauncherImpl.LOG.info("inserting launch event");
            ContainerRemoteLaunchEvent mockLaunchEvent = Mockito.mock(ContainerRemoteLaunchEvent.class);
            Mockito.when(mockLaunchEvent.getType()).thenReturn(CONTAINER_REMOTE_LAUNCH);
            Mockito.when(mockLaunchEvent.getContainerID()).thenReturn(contId);
            Mockito.when(mockLaunchEvent.getTaskAttemptID()).thenReturn(taskAttemptId);
            Mockito.when(mockLaunchEvent.getContainerMgrAddress()).thenReturn(cmAddress);
            Mockito.when(mockLaunchEvent.getContainerToken()).thenReturn(createNewContainerToken(contId, cmAddress));
            ut.handle(mockLaunchEvent);
            startLaunchBarrier.await();
            TestContainerLauncherImpl.LOG.info("inserting cleanup event");
            ContainerLauncherEvent mockCleanupEvent = Mockito.mock(ContainerLauncherEvent.class);
            Mockito.when(mockCleanupEvent.getType()).thenReturn(CONTAINER_REMOTE_CLEANUP);
            Mockito.when(mockCleanupEvent.getContainerID()).thenReturn(contId);
            Mockito.when(mockCleanupEvent.getTaskAttemptID()).thenReturn(taskAttemptId);
            Mockito.when(mockCleanupEvent.getContainerMgrAddress()).thenReturn(cmAddress);
            ut.handle(mockCleanupEvent);
            completeLaunchBarrier.await();
            ut.waitForPoolToIdle();
            ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
            Mockito.verify(mockEventHandler, Mockito.atLeast(2)).handle(arg.capture());
            boolean containerCleaned = false;
            for (int i = 0; i < (arg.getAllValues().size()); i++) {
                TestContainerLauncherImpl.LOG.info(arg.getAllValues().get(i).toString());
                Event currentEvent = arg.getAllValues().get(i);
                if ((currentEvent.getType()) == (TaskAttemptEventType.TA_CONTAINER_CLEANED)) {
                    containerCleaned = true;
                }
            }
            assert containerCleaned;
        } finally {
            stop();
        }
    }

    private static class ContainerManagerForTest implements TestContainerLauncherImpl.ContainerManagementProtocolClient {
        private CyclicBarrier startLaunchBarrier;

        private CyclicBarrier completeLaunchBarrier;

        ContainerManagerForTest(CyclicBarrier startLaunchBarrier, CyclicBarrier completeLaunchBarrier) {
            this.startLaunchBarrier = startLaunchBarrier;
            this.completeLaunchBarrier = completeLaunchBarrier;
        }

        @Override
        public StartContainersResponse startContainers(StartContainersRequest request) throws IOException {
            try {
                startLaunchBarrier.await();
                completeLaunchBarrier.await();
                // To ensure the kill is started before the launch
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            throw new IOException(new TestContainerLauncherImpl.ContainerException("Force fail CM"));
        }

        @Override
        public StopContainersResponse stopContainers(StopContainersRequest request) throws IOException {
            return null;
        }

        @Override
        public GetContainerStatusesResponse getContainerStatuses(GetContainerStatusesRequest request) throws IOException {
            return null;
        }

        @Override
        @Deprecated
        public IncreaseContainersResourceResponse increaseContainersResource(IncreaseContainersResourceRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public SignalContainerResponse signalToContainer(SignalContainerRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public ResourceLocalizationResponse localize(ResourceLocalizationRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public ReInitializeContainerResponse reInitializeContainer(ReInitializeContainerRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public RestartContainerResponse restartContainer(ContainerId containerId) throws IOException, YarnException {
            return null;
        }

        @Override
        public RollbackResponse rollbackLastReInitialization(ContainerId containerId) throws IOException, YarnException {
            return null;
        }

        @Override
        public CommitResponse commitLastReInitialization(ContainerId containerId) throws IOException, YarnException {
            return null;
        }

        @Override
        public ContainerUpdateResponse updateContainer(ContainerUpdateRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public GetLocalizationStatusesResponse getLocalizationStatuses(GetLocalizationStatusesRequest request) throws IOException, YarnException {
            return null;
        }
    }

    @SuppressWarnings("serial")
    private static class ContainerException extends YarnException {
        public ContainerException(String message) {
            super(message);
        }

        @Override
        public YarnException getCause() {
            return null;
        }
    }
}

