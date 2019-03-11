/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler;


import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import RecoveredContainerStatus.COMPLETED;
import RecoveredContainerStatus.LAUNCHED;
import RecoveredContainerStatus.PAUSED;
import RecoveredContainerStatus.QUEUED;
import RecoveredContainerStatus.REQUESTED;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceUtilization;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerImpl;
import org.apache.hadoop.yarn.server.nodemanager.metrics.NodeManagerMetrics;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredContainerState;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests to verify that the {@link ContainerScheduler} is able to
 * recover active containers based on RecoveredContainerStatus and
 * ExecutionType.
 */
public class TestContainerSchedulerRecovery {
    private static final Resource CONTAINER_SIZE = Resource.newInstance(1024, 4);

    private static final ResourceUtilization ZERO = ResourceUtilization.newInstance(0, 0, 0.0F);

    @Mock
    private NMContext context;

    @Mock
    private NodeManagerMetrics metrics;

    @Mock
    private AsyncDispatcher dispatcher;

    @Mock
    private ContainerTokenIdentifier token;

    @Mock
    private ContainerImpl container;

    @Mock
    private ApplicationId appId;

    @Mock
    private ApplicationAttemptId appAttemptId;

    @Mock
    private ContainerId containerId;

    private ContainerScheduler spy;

    /* Test if a container is recovered as QUEUED, GUARANTEED,
    it should be added to queuedGuaranteedContainers map.
     */
    @Test
    public void testRecoverContainerQueuedGuaranteed() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(QUEUED);
        Mockito.when(token.getExecutionType()).thenReturn(GUARANTEED);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(1, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as QUEUED, OPPORTUNISTIC,
    it should be added to queuedOpportunisticContainers map.
     */
    @Test
    public void testRecoverContainerQueuedOpportunistic() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(QUEUED);
        Mockito.when(token.getExecutionType()).thenReturn(OPPORTUNISTIC);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(1, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as PAUSED, GUARANTEED,
    it should be added to queuedGuaranteedContainers map.
     */
    @Test
    public void testRecoverContainerPausedGuaranteed() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(PAUSED);
        Mockito.when(token.getExecutionType()).thenReturn(GUARANTEED);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(1, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as PAUSED, OPPORTUNISTIC,
    it should be added to queuedOpportunisticContainers map.
     */
    @Test
    public void testRecoverContainerPausedOpportunistic() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(PAUSED);
        Mockito.when(token.getExecutionType()).thenReturn(OPPORTUNISTIC);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(1, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as LAUNCHED, GUARANTEED,
    it should be added to runningContainers map.
     */
    @Test
    public void testRecoverContainerLaunchedGuaranteed() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(LAUNCHED);
        Mockito.when(token.getExecutionType()).thenReturn(GUARANTEED);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(1, spy.getNumRunningContainers());
        Assert.assertEquals(ResourceUtilization.newInstance(1024, 1024, 4.0F), spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as LAUNCHED, OPPORTUNISTIC,
    it should be added to runningContainers map.
     */
    @Test
    public void testRecoverContainerLaunchedOpportunistic() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(LAUNCHED);
        Mockito.when(token.getExecutionType()).thenReturn(OPPORTUNISTIC);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(1, spy.getNumRunningContainers());
        Assert.assertEquals(ResourceUtilization.newInstance(1024, 1024, 4.0F), spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as REQUESTED, GUARANTEED,
    it should not be added to any map mentioned below.
     */
    @Test
    public void testRecoverContainerRequestedGuaranteed() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(REQUESTED);
        Mockito.when(token.getExecutionType()).thenReturn(GUARANTEED);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as REQUESTED, OPPORTUNISTIC,
    it should not be added to any map mentioned below.
     */
    @Test
    public void testRecoverContainerRequestedOpportunistic() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(REQUESTED);
        Mockito.when(token.getExecutionType()).thenReturn(OPPORTUNISTIC);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as COMPLETED, GUARANTEED,
    it should not be added to any map mentioned below.
     */
    @Test
    public void testRecoverContainerCompletedGuaranteed() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(COMPLETED);
        Mockito.when(token.getExecutionType()).thenReturn(GUARANTEED);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as COMPLETED, OPPORTUNISTIC,
    it should not be added to any map mentioned below.
     */
    @Test
    public void testRecoverContainerCompletedOpportunistic() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(COMPLETED);
        Mockito.when(token.getExecutionType()).thenReturn(OPPORTUNISTIC);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as GUARANTEED but no executionType set,
    it should not be added to any map mentioned below.
     */
    @Test
    public void testContainerQueuedNoExecType() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(QUEUED);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }

    /* Test if a container is recovered as PAUSED but no executionType set,
    it should not be added to any map mentioned below.
     */
    @Test
    public void testContainerPausedNoExecType() throws IllegalAccessException, IllegalArgumentException {
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        RecoveredContainerState rcs = createRecoveredContainerState(PAUSED);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(token);
        spy.recoverActiveContainer(container, rcs);
        Assert.assertEquals(0, spy.getNumQueuedGuaranteedContainers());
        Assert.assertEquals(0, spy.getNumQueuedOpportunisticContainers());
        Assert.assertEquals(0, spy.getNumRunningContainers());
        Assert.assertEquals(TestContainerSchedulerRecovery.ZERO, spy.getCurrentUtilization());
    }
}

