/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.daemon.supervisor;


import GoodToGo.GoodToGoLatch;
import MachineState.EMPTY;
import MachineState.KILL;
import MachineState.KILL_AND_RELAUNCH;
import MachineState.KILL_BLOB_UPDATE;
import MachineState.RUNNING;
import MachineState.WAITING_FOR_BLOB_LOCALIZATION;
import MachineState.WAITING_FOR_BLOB_UPDATE;
import MachineState.WAITING_FOR_WORKER_START;
import ProfileAction.JPROFILE_STOP;
import Slot.BlobChanging;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.storm.daemon.supervisor.Slot.DynamicState;
import org.apache.storm.daemon.supervisor.Slot.StaticState;
import org.apache.storm.daemon.supervisor.Slot.TopoProfileAction;
import org.apache.storm.generated.ExecutorInfo;
import org.apache.storm.generated.LSWorkerHeartbeat;
import org.apache.storm.generated.LocalAssignment;
import org.apache.storm.generated.NodeInfo;
import org.apache.storm.generated.ProfileRequest;
import org.apache.storm.localizer.AsyncLocalizer;
import org.apache.storm.localizer.BlobChangingCallback;
import org.apache.storm.localizer.GoodToGo;
import org.apache.storm.localizer.LocallyCachedBlob;
import org.apache.storm.metric.StormMetricsRegistry;
import org.apache.storm.scheduler.ISupervisor;
import org.apache.storm.utils.LocalState;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Time.SimulatedTime;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SlotTest {
    private static final Logger LOG = LoggerFactory.getLogger(SlotTest.class);

    @Test
    public void testEquivilant() {
        LocalAssignment a = SlotTest.mkLocalAssignment("A", SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5), SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
        LocalAssignment aResized = SlotTest.mkLocalAssignment("A", SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5), SlotTest.mkWorkerResources(100.0, 200.0, 100.0));
        LocalAssignment b = SlotTest.mkLocalAssignment("B", SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5, 6), SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
        LocalAssignment bReordered = SlotTest.mkLocalAssignment("B", SlotTest.mkExecutorInfoList(6, 5, 4, 3, 2, 1), SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
        Assert.assertTrue(Slot.equivalent(null, null));
        Assert.assertTrue(Slot.equivalent(a, a));
        Assert.assertTrue(Slot.equivalent(b, bReordered));
        Assert.assertTrue(Slot.equivalent(bReordered, b));
        Assert.assertFalse(Slot.equivalent(a, aResized));
        Assert.assertFalse(Slot.equivalent(aResized, a));
        Assert.assertFalse(Slot.equivalent(a, null));
        Assert.assertFalse(Slot.equivalent(null, b));
        Assert.assertFalse(Slot.equivalent(a, b));
    }

    @Test
    public void testForSameTopology() {
        LocalAssignment a = SlotTest.mkLocalAssignment("A", SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5), SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
        LocalAssignment aResized = SlotTest.mkLocalAssignment("A", SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5), SlotTest.mkWorkerResources(100.0, 200.0, 100.0));
        LocalAssignment b = SlotTest.mkLocalAssignment("B", SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5, 6), SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
        LocalAssignment bReordered = SlotTest.mkLocalAssignment("B", SlotTest.mkExecutorInfoList(6, 5, 4, 3, 2, 1), SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
        Assert.assertTrue(Slot.forSameTopology(null, null));
        Assert.assertTrue(Slot.forSameTopology(a, a));
        Assert.assertTrue(Slot.forSameTopology(a, aResized));
        Assert.assertTrue(Slot.forSameTopology(aResized, a));
        Assert.assertTrue(Slot.forSameTopology(b, bReordered));
        Assert.assertTrue(Slot.forSameTopology(bReordered, b));
        Assert.assertFalse(Slot.forSameTopology(a, null));
        Assert.assertFalse(Slot.forSameTopology(null, b));
        Assert.assertFalse(Slot.forSameTopology(a, b));
    }

    @Test
    public void testEmptyToEmpty() throws Exception {
        try (SimulatedTime t = new SimulatedTime(1010)) {
            AsyncLocalizer localizer = Mockito.mock(AsyncLocalizer.class);
            LocalState state = Mockito.mock(LocalState.class);
            BlobChangingCallback cb = Mockito.mock(BlobChangingCallback.class);
            ContainerLauncher containerLauncher = Mockito.mock(ContainerLauncher.class);
            ISupervisor iSuper = Mockito.mock(ISupervisor.class);
            SlotMetrics slotMetrics = new SlotMetrics(new StormMetricsRegistry());
            StaticState staticState = new StaticState(localizer, 1000, 1000, 1000, 1000, containerLauncher, "localhost", 8080, iSuper, state, cb, null, null, slotMetrics);
            DynamicState dynamicState = new DynamicState(null, null, null, slotMetrics);
            DynamicState nextState = Slot.handleEmpty(dynamicState, staticState);
            Assert.assertEquals(EMPTY, nextState.state);
            Assert.assertTrue(((Time.currentTimeMillis()) > 1000));
        }
    }

    @Test
    public void testLaunchContainerFromEmpty() throws Exception {
        try (SimulatedTime t = new SimulatedTime(1010)) {
            int port = 8080;
            String topoId = "NEW";
            List<ExecutorInfo> execList = SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5);
            LocalAssignment newAssignment = SlotTest.mkLocalAssignment(topoId, execList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            AsyncLocalizer localizer = Mockito.mock(AsyncLocalizer.class);
            BlobChangingCallback cb = Mockito.mock(BlobChangingCallback.class);
            Container container = Mockito.mock(Container.class);
            LocalState state = Mockito.mock(LocalState.class);
            ContainerLauncher containerLauncher = Mockito.mock(ContainerLauncher.class);
            Mockito.when(containerLauncher.launchContainer(port, newAssignment, state)).thenReturn(container);
            LSWorkerHeartbeat hb = SlotTest.mkWorkerHB(topoId, port, execList, Time.currentTimeSecs());
            Mockito.when(container.readHeartbeat()).thenReturn(hb, hb);
            @SuppressWarnings("unchecked")
            CompletableFuture<Void> blobFuture = Mockito.mock(CompletableFuture.class);
            Mockito.when(localizer.requestDownloadTopologyBlobs(newAssignment, port, cb)).thenReturn(blobFuture);
            ISupervisor iSuper = Mockito.mock(ISupervisor.class);
            SlotMetrics slotMetrics = new SlotMetrics(new StormMetricsRegistry());
            StaticState staticState = new StaticState(localizer, 5000, 120000, 1000, 1000, containerLauncher, "localhost", port, iSuper, state, cb, null, null, slotMetrics);
            DynamicState dynamicState = new DynamicState(null, null, null, slotMetrics).withNewAssignment(newAssignment);
            DynamicState nextState = Slot.stateMachineStep(dynamicState, staticState);
            Mockito.verify(localizer).requestDownloadTopologyBlobs(newAssignment, port, cb);
            Assert.assertEquals(WAITING_FOR_BLOB_LOCALIZATION, nextState.state);
            Assert.assertSame("pendingDownload not set properly", blobFuture, nextState.pendingDownload);
            Assert.assertEquals(newAssignment, nextState.pendingLocalization);
            Assert.assertEquals(0, Time.currentTimeMillis());
            nextState = Slot.stateMachineStep(nextState, staticState);
            Mockito.verify(blobFuture).get(1000, TimeUnit.MILLISECONDS);
            Mockito.verify(containerLauncher).launchContainer(port, newAssignment, state);
            Assert.assertEquals(WAITING_FOR_WORKER_START, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(newAssignment, nextState.currentAssignment);
            Assert.assertSame(container, nextState.container);
            Assert.assertEquals(0, Time.currentTimeMillis());
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(newAssignment, nextState.currentAssignment);
            Assert.assertSame(container, nextState.container);
            Assert.assertEquals(0, Time.currentTimeMillis());
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(newAssignment, nextState.currentAssignment);
            Assert.assertSame(container, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 1000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(newAssignment, nextState.currentAssignment);
            Assert.assertSame(container, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
        }
    }

    @Test
    public void testRelaunch() throws Exception {
        try (SimulatedTime t = new SimulatedTime(1010)) {
            int port = 8080;
            String topoId = "CURRENT";
            List<ExecutorInfo> execList = SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5);
            LocalAssignment assignment = SlotTest.mkLocalAssignment(topoId, execList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            AsyncLocalizer localizer = Mockito.mock(AsyncLocalizer.class);
            BlobChangingCallback cb = Mockito.mock(BlobChangingCallback.class);
            Container container = Mockito.mock(Container.class);
            ContainerLauncher containerLauncher = Mockito.mock(ContainerLauncher.class);
            LSWorkerHeartbeat oldhb = SlotTest.mkWorkerHB(topoId, port, execList, ((Time.currentTimeSecs()) - 10));
            LSWorkerHeartbeat goodhb = SlotTest.mkWorkerHB(topoId, port, execList, Time.currentTimeSecs());
            Mockito.when(container.readHeartbeat()).thenReturn(oldhb, oldhb, goodhb, goodhb);
            Mockito.when(container.areAllProcessesDead()).thenReturn(false, false, true);
            ISupervisor iSuper = Mockito.mock(ISupervisor.class);
            LocalState state = Mockito.mock(LocalState.class);
            SlotMetrics slotMetrics = new SlotMetrics(new StormMetricsRegistry());
            StaticState staticState = new StaticState(localizer, 5000, 120000, 1000, 1000, containerLauncher, "localhost", port, iSuper, state, cb, null, null, slotMetrics);
            DynamicState dynamicState = new DynamicState(assignment, container, assignment, slotMetrics);
            DynamicState nextState = Slot.stateMachineStep(dynamicState, staticState);
            Assert.assertEquals(KILL_AND_RELAUNCH, nextState.state);
            Mockito.verify(container).kill();
            Assert.assertTrue(((Time.currentTimeMillis()) > 1000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(KILL_AND_RELAUNCH, nextState.state);
            Mockito.verify(container).forceKill();
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(WAITING_FOR_WORKER_START, nextState.state);
            Mockito.verify(container).relaunch();
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(WAITING_FOR_WORKER_START, nextState.state);
            Assert.assertTrue(((Time.currentTimeMillis()) > 3000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
        }
    }

    @Test
    public void testReschedule() throws Exception {
        try (SimulatedTime t = new SimulatedTime(1010)) {
            int port = 8080;
            String cTopoId = "CURRENT";
            List<ExecutorInfo> cExecList = SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5);
            LocalAssignment cAssignment = SlotTest.mkLocalAssignment(cTopoId, cExecList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            BlobChangingCallback cb = Mockito.mock(BlobChangingCallback.class);
            Container cContainer = Mockito.mock(Container.class);
            LSWorkerHeartbeat chb = SlotTest.mkWorkerHB(cTopoId, port, cExecList, Time.currentTimeSecs());
            Mockito.when(cContainer.readHeartbeat()).thenReturn(chb);
            Mockito.when(cContainer.areAllProcessesDead()).thenReturn(false, false, true);
            String nTopoId = "NEW";
            List<ExecutorInfo> nExecList = SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5);
            LocalAssignment nAssignment = SlotTest.mkLocalAssignment(nTopoId, nExecList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            AsyncLocalizer localizer = Mockito.mock(AsyncLocalizer.class);
            Container nContainer = Mockito.mock(Container.class);
            LocalState state = Mockito.mock(LocalState.class);
            ContainerLauncher containerLauncher = Mockito.mock(ContainerLauncher.class);
            Mockito.when(containerLauncher.launchContainer(port, nAssignment, state)).thenReturn(nContainer);
            LSWorkerHeartbeat nhb = SlotTest.mkWorkerHB(nTopoId, 100, nExecList, Time.currentTimeSecs());
            Mockito.when(nContainer.readHeartbeat()).thenReturn(nhb, nhb);
            @SuppressWarnings("unchecked")
            CompletableFuture<Void> blobFuture = Mockito.mock(CompletableFuture.class);
            Mockito.when(localizer.requestDownloadTopologyBlobs(nAssignment, port, cb)).thenReturn(blobFuture);
            ISupervisor iSuper = Mockito.mock(ISupervisor.class);
            SlotMetrics slotMetrics = new SlotMetrics(new StormMetricsRegistry());
            StaticState staticState = new StaticState(localizer, 5000, 120000, 1000, 1000, containerLauncher, "localhost", port, iSuper, state, cb, null, null, slotMetrics);
            DynamicState dynamicState = new DynamicState(cAssignment, cContainer, nAssignment, slotMetrics);
            DynamicState nextState = Slot.stateMachineStep(dynamicState, staticState);
            Assert.assertEquals(KILL, nextState.state);
            Mockito.verify(cContainer).kill();
            Mockito.verify(localizer).requestDownloadTopologyBlobs(nAssignment, port, cb);
            Assert.assertSame("pendingDownload not set properly", blobFuture, nextState.pendingDownload);
            Assert.assertEquals(nAssignment, nextState.pendingLocalization);
            Assert.assertTrue(((Time.currentTimeMillis()) > 1000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(KILL, nextState.state);
            Mockito.verify(cContainer).forceKill();
            Assert.assertSame("pendingDownload not set properly", blobFuture, nextState.pendingDownload);
            Assert.assertEquals(nAssignment, nextState.pendingLocalization);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(WAITING_FOR_BLOB_LOCALIZATION, nextState.state);
            Mockito.verify(cContainer).cleanUp();
            Mockito.verify(localizer).releaseSlotFor(cAssignment, port);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Mockito.verify(blobFuture).get(1000, TimeUnit.MILLISECONDS);
            Mockito.verify(containerLauncher).launchContainer(port, nAssignment, state);
            Assert.assertEquals(WAITING_FOR_WORKER_START, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(nAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(nAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(nAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 3000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertSame("pendingDownload is not null", null, nextState.pendingDownload);
            Assert.assertSame(null, nextState.pendingLocalization);
            Assert.assertSame(nAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 4000));
        }
    }

    @Test
    public void testRunningToEmpty() throws Exception {
        try (SimulatedTime t = new SimulatedTime(1010)) {
            int port = 8080;
            String cTopoId = "CURRENT";
            List<ExecutorInfo> cExecList = SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5);
            LocalAssignment cAssignment = SlotTest.mkLocalAssignment(cTopoId, cExecList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            Container cContainer = Mockito.mock(Container.class);
            LSWorkerHeartbeat chb = SlotTest.mkWorkerHB(cTopoId, port, cExecList, Time.currentTimeSecs());
            Mockito.when(cContainer.readHeartbeat()).thenReturn(chb);
            Mockito.when(cContainer.areAllProcessesDead()).thenReturn(false, false, true);
            AsyncLocalizer localizer = Mockito.mock(AsyncLocalizer.class);
            BlobChangingCallback cb = Mockito.mock(BlobChangingCallback.class);
            ContainerLauncher containerLauncher = Mockito.mock(ContainerLauncher.class);
            ISupervisor iSuper = Mockito.mock(ISupervisor.class);
            LocalState state = Mockito.mock(LocalState.class);
            SlotMetrics slotMetrics = new SlotMetrics(new StormMetricsRegistry());
            StaticState staticState = new StaticState(localizer, 5000, 120000, 1000, 1000, containerLauncher, "localhost", port, iSuper, state, cb, null, null, slotMetrics);
            DynamicState dynamicState = new DynamicState(cAssignment, cContainer, null, slotMetrics);
            DynamicState nextState = Slot.stateMachineStep(dynamicState, staticState);
            Assert.assertEquals(KILL, nextState.state);
            Mockito.verify(cContainer).kill();
            Mockito.verify(localizer, Mockito.never()).requestDownloadTopologyBlobs(null, port, cb);
            Assert.assertSame("pendingDownload not set properly", null, nextState.pendingDownload);
            Assert.assertEquals(null, nextState.pendingLocalization);
            Assert.assertTrue(((Time.currentTimeMillis()) > 1000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(KILL, nextState.state);
            Mockito.verify(cContainer).forceKill();
            Assert.assertSame("pendingDownload not set properly", null, nextState.pendingDownload);
            Assert.assertEquals(null, nextState.pendingLocalization);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(EMPTY, nextState.state);
            Mockito.verify(cContainer).cleanUp();
            Mockito.verify(localizer).releaseSlotFor(cAssignment, port);
            Assert.assertEquals(null, nextState.container);
            Assert.assertEquals(null, nextState.currentAssignment);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(EMPTY, nextState.state);
            Assert.assertEquals(null, nextState.container);
            Assert.assertEquals(null, nextState.currentAssignment);
            Assert.assertTrue(((Time.currentTimeMillis()) > 3000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(EMPTY, nextState.state);
            Assert.assertEquals(null, nextState.container);
            Assert.assertEquals(null, nextState.currentAssignment);
            Assert.assertTrue(((Time.currentTimeMillis()) > 3000));
        }
    }

    @Test
    public void testRunWithProfileActions() throws Exception {
        try (SimulatedTime t = new SimulatedTime(1010)) {
            int port = 8080;
            String cTopoId = "CURRENT";
            List<ExecutorInfo> cExecList = SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5);
            LocalAssignment cAssignment = SlotTest.mkLocalAssignment(cTopoId, cExecList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            Container cContainer = Mockito.mock(Container.class);
            LSWorkerHeartbeat chb = SlotTest.mkWorkerHB(cTopoId, port, cExecList, ((Time.currentTimeSecs()) + 100));// NOT going to timeout for a while

            Mockito.when(cContainer.readHeartbeat()).thenReturn(chb, chb, chb, chb, chb, chb);
            Mockito.when(cContainer.runProfiling(ArgumentMatchers.any(ProfileRequest.class), ArgumentMatchers.anyBoolean())).thenReturn(true);
            AsyncLocalizer localizer = Mockito.mock(AsyncLocalizer.class);
            BlobChangingCallback cb = Mockito.mock(BlobChangingCallback.class);
            ContainerLauncher containerLauncher = Mockito.mock(ContainerLauncher.class);
            ISupervisor iSuper = Mockito.mock(ISupervisor.class);
            LocalState state = Mockito.mock(LocalState.class);
            StaticState staticState = new StaticState(localizer, 5000, 120000, 1000, 1000, containerLauncher, "localhost", port, iSuper, state, cb, null, null, new SlotMetrics(new StormMetricsRegistry()));
            Set<TopoProfileAction> profileActions = new HashSet<>();
            ProfileRequest request = new ProfileRequest();
            request.set_action(JPROFILE_STOP);
            NodeInfo info = new NodeInfo();
            info.set_node("localhost");
            info.add_to_port(port);
            request.set_nodeInfo(info);
            request.set_time_stamp(((Time.currentTimeMillis()) + 3000));// 3 seconds from now

            TopoProfileAction profile = new TopoProfileAction(cTopoId, request);
            profileActions.add(profile);
            Set<TopoProfileAction> expectedPending = new HashSet<>();
            expectedPending.add(profile);
            SlotMetrics slotMetrics = new SlotMetrics(new StormMetricsRegistry());
            DynamicState dynamicState = new DynamicState(cAssignment, cContainer, cAssignment, slotMetrics).withProfileActions(profileActions, Collections.<TopoProfileAction>emptySet());
            DynamicState nextState = Slot.stateMachineStep(dynamicState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Mockito.verify(cContainer).runProfiling(request, false);
            Assert.assertEquals(expectedPending, nextState.pendingStopProfileActions);
            Assert.assertEquals(expectedPending, nextState.profileActions);
            Assert.assertTrue(((Time.currentTimeMillis()) > 1000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertEquals(expectedPending, nextState.pendingStopProfileActions);
            Assert.assertEquals(expectedPending, nextState.profileActions);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertEquals(expectedPending, nextState.pendingStopProfileActions);
            Assert.assertEquals(expectedPending, nextState.profileActions);
            Assert.assertTrue(((Time.currentTimeMillis()) > 3000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Mockito.verify(cContainer).runProfiling(request, true);
            Assert.assertEquals(Collections.<TopoProfileAction>emptySet(), nextState.pendingStopProfileActions);
            Assert.assertEquals(Collections.<TopoProfileAction>emptySet(), nextState.profileActions);
            Assert.assertTrue(((Time.currentTimeMillis()) > 4000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertEquals(Collections.<TopoProfileAction>emptySet(), nextState.pendingStopProfileActions);
            Assert.assertEquals(Collections.<TopoProfileAction>emptySet(), nextState.profileActions);
            Assert.assertTrue(((Time.currentTimeMillis()) > 5000));
        }
    }

    @Test
    public void testResourcesChangedFiltered() throws Exception {
        try (SimulatedTime t = new SimulatedTime(1010)) {
            int port = 8080;
            String cTopoId = "CURRENT";
            List<ExecutorInfo> cExecList = SlotTest.mkExecutorInfoList(1, 2, 3, 4, 5);
            LocalAssignment cAssignment = SlotTest.mkLocalAssignment(cTopoId, cExecList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            String otherTopoId = "OTHER";
            LocalAssignment otherAssignment = SlotTest.mkLocalAssignment(otherTopoId, cExecList, SlotTest.mkWorkerResources(100.0, 100.0, 100.0));
            BlobChangingCallback cb = Mockito.mock(BlobChangingCallback.class);
            Container cContainer = Mockito.mock(Container.class);
            LSWorkerHeartbeat chb = SlotTest.mkWorkerHB(cTopoId, port, cExecList, Time.currentTimeSecs());
            Mockito.when(cContainer.readHeartbeat()).thenReturn(chb);
            Mockito.when(cContainer.areAllProcessesDead()).thenReturn(false, false, true);
            AsyncLocalizer localizer = Mockito.mock(AsyncLocalizer.class);
            Container nContainer = Mockito.mock(Container.class);
            LocalState state = Mockito.mock(LocalState.class);
            ContainerLauncher containerLauncher = Mockito.mock(ContainerLauncher.class);
            Mockito.when(containerLauncher.launchContainer(port, cAssignment, state)).thenReturn(nContainer);
            Mockito.when(nContainer.readHeartbeat()).thenReturn(chb, chb);
            ISupervisor iSuper = Mockito.mock(ISupervisor.class);
            long heartbeatTimeoutMs = 5000;
            StaticState staticState = new StaticState(localizer, heartbeatTimeoutMs, 120000, 1000, 1000, containerLauncher, "localhost", port, iSuper, state, cb, null, null, new SlotMetrics(new StormMetricsRegistry()));
            Set<Slot.BlobChanging> changing = new HashSet<>();
            LocallyCachedBlob stormJar = Mockito.mock(LocallyCachedBlob.class);
            GoodToGo.GoodToGoLatch stormJarLatch = Mockito.mock(GoodToGoLatch.class);
            CompletableFuture<Void> stormJarLatchFuture = Mockito.mock(CompletableFuture.class);
            Mockito.when(stormJarLatch.countDown()).thenReturn(stormJarLatchFuture);
            changing.add(new Slot.BlobChanging(cAssignment, stormJar, stormJarLatch));
            Set<Slot.BlobChanging> desired = new HashSet(changing);
            LocallyCachedBlob otherJar = Mockito.mock(LocallyCachedBlob.class);
            GoodToGo.GoodToGoLatch otherJarLatch = Mockito.mock(GoodToGoLatch.class);
            changing.add(new Slot.BlobChanging(otherAssignment, otherJar, otherJarLatch));
            SlotMetrics slotMetrics = new SlotMetrics(new StormMetricsRegistry());
            DynamicState dynamicState = new DynamicState(cAssignment, cContainer, cAssignment, slotMetrics).withChangingBlobs(changing);
            DynamicState nextState = Slot.stateMachineStep(dynamicState, staticState);
            Assert.assertEquals(KILL_BLOB_UPDATE, nextState.state);
            Mockito.verify(iSuper).killedWorker(port);
            Mockito.verify(cContainer).kill();
            Mockito.verify(localizer, Mockito.never()).requestDownloadTopologyBlobs(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
            Mockito.verify(stormJarLatch, Mockito.never()).countDown();
            Mockito.verify(otherJarLatch, Mockito.times(1)).countDown();
            Assert.assertNull(nextState.pendingDownload);
            Assert.assertNull(nextState.pendingLocalization);
            Assert.assertEquals(desired, nextState.changingBlobs);
            Assert.assertTrue(nextState.pendingChangingBlobs.isEmpty());
            Assert.assertNull(nextState.pendingChangingBlobsAssignment);
            Assert.assertThat(Time.currentTimeMillis(), Matchers.greaterThan(1000L));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(KILL_BLOB_UPDATE, nextState.state);
            Mockito.verify(cContainer).forceKill();
            Assert.assertNull(nextState.pendingDownload);
            Assert.assertNull(nextState.pendingLocalization);
            Assert.assertEquals(desired, nextState.changingBlobs);
            Assert.assertTrue(nextState.pendingChangingBlobs.isEmpty());
            Assert.assertNull(nextState.pendingChangingBlobsAssignment);
            Assert.assertThat(Time.currentTimeMillis(), Matchers.greaterThan(2000L));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(WAITING_FOR_BLOB_UPDATE, nextState.state);
            Mockito.verify(cContainer).cleanUp();
            Assert.assertThat(Time.currentTimeMillis(), Matchers.greaterThan(2000L));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Mockito.verify(stormJarLatchFuture).get(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
            Mockito.verify(containerLauncher).launchContainer(port, cAssignment, state);
            Assert.assertEquals(WAITING_FOR_WORKER_START, nextState.state);
            Assert.assertNull(nextState.pendingChangingBlobsAssignment);
            Assert.assertTrue(nextState.pendingChangingBlobs.isEmpty());
            Assert.assertSame(cAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertThat(Time.currentTimeMillis(), Matchers.greaterThan(2000L));
            Assert.assertThat(Time.currentTimeMillis(), Matchers.lessThan(heartbeatTimeoutMs));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertNull(nextState.pendingChangingBlobsAssignment);
            Assert.assertTrue(nextState.pendingChangingBlobs.isEmpty());
            Assert.assertSame(cAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 2000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertNull(nextState.pendingChangingBlobsAssignment);
            Assert.assertTrue(nextState.pendingChangingBlobs.isEmpty());
            Assert.assertSame(cAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 3000));
            nextState = Slot.stateMachineStep(nextState, staticState);
            Assert.assertEquals(RUNNING, nextState.state);
            Assert.assertNull(nextState.pendingChangingBlobsAssignment);
            Assert.assertTrue(nextState.pendingChangingBlobs.isEmpty());
            Assert.assertSame(cAssignment, nextState.currentAssignment);
            Assert.assertSame(nContainer, nextState.container);
            Assert.assertTrue(((Time.currentTimeMillis()) > 4000));
        }
    }
}

