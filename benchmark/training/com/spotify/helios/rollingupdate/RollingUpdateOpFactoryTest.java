/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.rollingupdate;


import DeploymentGroupStatus.State.DONE;
import DeploymentGroupStatus.State.FAILED;
import DeploymentGroupStatus.State.ROLLING_OUT;
import RollingUpdateError.HOST_NOT_FOUND;
import RollingUpdateError.TIMED_OUT_WAITING_FOR_JOB_TO_REACH_RUNNING;
import RolloutTask.Action.AWAIT_RUNNING;
import RolloutTask.Action.DEPLOY_NEW_JOB;
import RolloutTask.Action.UNDEPLOY_OLD_JOBS;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.DeploymentGroupStatus;
import com.spotify.helios.common.descriptors.DeploymentGroupTasks;
import com.spotify.helios.common.descriptors.RolloutOptions;
import com.spotify.helios.common.descriptors.RolloutTask;
import com.spotify.helios.servicescommon.coordination.CreateEmpty;
import com.spotify.helios.servicescommon.coordination.Delete;
import com.spotify.helios.servicescommon.coordination.ZooKeeperClient;
import com.spotify.helios.servicescommon.coordination.ZooKeeperOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.apache.zookeeper.data.Stat;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RollingUpdateOpFactoryTest {
    private static final DeploymentGroup MANUAL_DEPLOYMENT_GROUP = DeploymentGroup.newBuilder().setName("my_group").setRolloutOptions(RolloutOptions.getDefault()).setRollingUpdateReason(MANUAL).build();

    private static final DeploymentGroup HOSTS_CHANGED_DEPLOYMENT_GROUP = DeploymentGroup.newBuilder().setName("my_group").setRolloutOptions(RolloutOptions.getDefault()).setRollingUpdateReason(HOSTS_CHANGED).build();

    private final DeploymentGroupEventFactory eventFactory = Mockito.mock(DeploymentGroupEventFactory.class);

    @Test
    public void testStartManualNoHosts() throws Exception {
        // Create a DeploymentGroupTasks object with no rolloutTasks (defaults to empty list).
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final ZooKeeperClient client = Mockito.mock(ZooKeeperClient.class);
        Mockito.when(client.exists(ArgumentMatchers.anyString())).thenReturn(null);
        final RollingUpdateOp op = opFactory.start(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP, client);
        // Three ZK operations should return:
        // * create tasks node
        // * delete the tasks
        // * set the status to DONE
        Assert.assertEquals(ImmutableSet.of(new CreateEmpty("/status/deployment-group-tasks/my_group"), new Delete("/status/deployment-group-tasks/my_group"), new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-groups/my_group", DeploymentGroupStatus.newBuilder().setState(DONE).setError(null).build().toJsonBytes())), ImmutableSet.copyOf(op.operations()));
        // Two events should return: rollingUpdateStarted and rollingUpdateDone
        Assert.assertEquals(2, op.events().size());
        Mockito.verify(eventFactory).rollingUpdateStarted(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP);
        Mockito.verify(eventFactory).rollingUpdateDone(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP);
    }

    @Test
    public void testStartManualNoHostsTasksAlreadyExist() throws Exception {
        // Create a DeploymentGroupTasks object with no rolloutTasks (defaults to empty list).
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final ZooKeeperClient client = Mockito.mock(ZooKeeperClient.class);
        Mockito.when(client.exists(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(Stat.class));
        final RollingUpdateOp op = opFactory.start(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP, client);
        // Two ZK operations should return:
        // * delete the tasks
        // * set the status to DONE
        Assert.assertEquals(ImmutableSet.of(new Delete("/status/deployment-group-tasks/my_group"), new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-groups/my_group", DeploymentGroupStatus.newBuilder().setState(DONE).setError(null).build().toJsonBytes())), ImmutableSet.copyOf(op.operations()));
        // Two events should return: rollingUpdateStarted and rollingUpdateDone
        Assert.assertEquals(2, op.events().size());
        Mockito.verify(eventFactory).rollingUpdateStarted(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP);
        Mockito.verify(eventFactory).rollingUpdateDone(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP);
    }

    @Test
    public void testStartManualWithHosts() throws Exception {
        // Create a DeploymentGroupTasks object with some rolloutTasks.
        final ArrayList<RolloutTask> rolloutTasks = Lists.newArrayList(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"));
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setTaskIndex(0).setRolloutTasks(rolloutTasks).setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final ZooKeeperClient client = Mockito.mock(ZooKeeperClient.class);
        Mockito.when(client.exists(ArgumentMatchers.anyString())).thenReturn(null);
        final RollingUpdateOp op = opFactory.start(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP, client);
        // Three ZK operations should return:
        // * create tasks node
        // * set the task index to 0
        // * set the status to ROLLING_OUT
        Assert.assertEquals(ImmutableSet.of(new CreateEmpty("/status/deployment-group-tasks/my_group"), new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-group-tasks/my_group", DeploymentGroupTasks.newBuilder().setRolloutTasks(rolloutTasks).setTaskIndex(0).setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build().toJsonBytes()), new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-groups/my_group", DeploymentGroupStatus.newBuilder().setState(ROLLING_OUT).build().toJsonBytes())), ImmutableSet.copyOf(op.operations()));
        // Two events should return: rollingUpdateStarted and rollingUpdateDone
        Assert.assertEquals(1, op.events().size());
        Mockito.verify(eventFactory).rollingUpdateStarted(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP);
    }

    @Test
    public void testStartHostsChanged() throws Exception {
        // Create a DeploymentGroupTasks object with some rolloutTasks.
        final ArrayList<RolloutTask> rolloutTasks = Lists.newArrayList(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"));
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setTaskIndex(0).setRolloutTasks(rolloutTasks).setDeploymentGroup(RollingUpdateOpFactoryTest.HOSTS_CHANGED_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final ZooKeeperClient client = Mockito.mock(ZooKeeperClient.class);
        Mockito.when(client.exists(ArgumentMatchers.anyString())).thenReturn(null);
        final RollingUpdateOp op = opFactory.start(RollingUpdateOpFactoryTest.HOSTS_CHANGED_DEPLOYMENT_GROUP, client);
        // Three ZK operations should return:
        // * create tasks node
        // * set the task index to 0
        // * another to set the status to ROLLING_OUT
        Assert.assertEquals(ImmutableSet.of(new CreateEmpty("/status/deployment-group-tasks/my_group"), new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-group-tasks/my_group", DeploymentGroupTasks.newBuilder().setRolloutTasks(rolloutTasks).setTaskIndex(0).setDeploymentGroup(RollingUpdateOpFactoryTest.HOSTS_CHANGED_DEPLOYMENT_GROUP).build().toJsonBytes()), new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-groups/my_group", DeploymentGroupStatus.newBuilder().setState(ROLLING_OUT).build().toJsonBytes())), ImmutableSet.copyOf(op.operations()));
        // Two events should return: rollingUpdateStarted and rollingUpdateDone
        Assert.assertEquals(1, op.events().size());
        Mockito.verify(eventFactory).rollingUpdateStarted(RollingUpdateOpFactoryTest.HOSTS_CHANGED_DEPLOYMENT_GROUP);
    }

    @Test
    public void testNextTaskNoOps() {
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setTaskIndex(0).setRolloutTasks(Lists.newArrayList(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"))).setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final RollingUpdateOp op = opFactory.nextTask();
        // A nextTask op with no ZK operations should result advancing the task index
        Assert.assertEquals(1, op.operations().size());
        Assert.assertEquals(new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-group-tasks/my_group", deploymentGroupTasks.toBuilder().setTaskIndex(1).build().toJsonBytes()), op.operations().get(0));
        // No events should be generated
        Assert.assertEquals(0, op.events().size());
    }

    @Test
    public void testNextTaskWithOps() {
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setTaskIndex(0).setRolloutTasks(Lists.newArrayList(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"))).setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final ZooKeeperOperation mockOp = Mockito.mock(ZooKeeperOperation.class);
        final RollingUpdateOp op = opFactory.nextTask(Lists.newArrayList(mockOp));
        // A nexTask op with ZK operations should result in advancing the task index
        // and also contain the specified ZK operations
        Assert.assertEquals(ImmutableSet.of(mockOp, new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-group-tasks/my_group", deploymentGroupTasks.toBuilder().setTaskIndex(1).build().toJsonBytes())), ImmutableSet.copyOf(op.operations()));
        // This is not a no-op -> an event should be emitted
        Assert.assertEquals(1, op.events().size());
        Mockito.verify(eventFactory).rollingUpdateTaskSucceeded(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP, deploymentGroupTasks.getRolloutTasks().get(deploymentGroupTasks.getTaskIndex()));
    }

    @Test
    public void testTransitionToDone() {
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setTaskIndex(2).setRolloutTasks(Lists.newArrayList(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"))).setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final RollingUpdateOp op = opFactory.nextTask();
        // When state -> DONE we expected
        // * deployment group tasks are deleted
        // * deployment group status is updated (to DONE)
        Assert.assertEquals(ImmutableSet.of(new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-groups/my_group", DeploymentGroupStatus.newBuilder().setState(DONE).setError(null).build().toJsonBytes()), new Delete("/status/deployment-group-tasks/my_group")), ImmutableSet.copyOf(op.operations()));
        // ...and that an event is emitted
        Assert.assertEquals(1, op.events().size());
        Mockito.verify(eventFactory).rollingUpdateDone(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP);
    }

    @Test
    public void testTransitionToFailed() {
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setTaskIndex(0).setRolloutTasks(Lists.newArrayList(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"))).setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final RollingUpdateOp op = opFactory.error("foo", "host1", HOST_NOT_FOUND);
        final Map<String, Object> failEvent = Maps.newHashMap();
        Mockito.when(eventFactory.rollingUpdateTaskFailed(ArgumentMatchers.any(DeploymentGroup.class), ArgumentMatchers.any(RolloutTask.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(RollingUpdateError.class))).thenReturn(failEvent);
        // When state -> FAILED we expected
        // * deployment group tasks are deleted
        // * deployment group status is updated (to FAILED)
        Assert.assertEquals(ImmutableSet.of(new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-groups/my_group", DeploymentGroupStatus.newBuilder().setState(FAILED).setError("host1: foo").build().toJsonBytes()), new Delete("/status/deployment-group-tasks/my_group")), ImmutableSet.copyOf(op.operations()));
        // ...and that a failed-task event and a rolling-update failed event are emitted
        Assert.assertEquals(2, op.events().size());
        Mockito.verify(eventFactory).rollingUpdateTaskFailed(ArgumentMatchers.eq(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP), ArgumentMatchers.eq(deploymentGroupTasks.getRolloutTasks().get(deploymentGroupTasks.getTaskIndex())), ArgumentMatchers.anyString(), ArgumentMatchers.eq(HOST_NOT_FOUND), ArgumentMatchers.eq(Collections.<String, Object>emptyMap()));
        Mockito.verify(eventFactory).rollingUpdateFailed(ArgumentMatchers.eq(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP), ArgumentMatchers.eq(failEvent));
    }

    @Test
    public void testYield() {
        final DeploymentGroupTasks deploymentGroupTasks = DeploymentGroupTasks.newBuilder().setTaskIndex(0).setRolloutTasks(Lists.newArrayList(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"))).setDeploymentGroup(RollingUpdateOpFactoryTest.MANUAL_DEPLOYMENT_GROUP).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(deploymentGroupTasks, eventFactory);
        final RollingUpdateOp op = opFactory.yield();
        Assert.assertEquals(0, op.operations().size());
        Assert.assertEquals(0, op.events().size());
    }

    @Test
    public void testErrorWhenIgnoreFailuresIsTrue() {
        final DeploymentGroup deploymentGroup = DeploymentGroup.newBuilder().setName("ignore_failure_group").setRolloutOptions(RolloutOptions.newBuilder().setIgnoreFailures(true).build()).setRollingUpdateReason(MANUAL).build();
        // the current task is the AWAIT_RUNNING one
        final DeploymentGroupTasks tasks = DeploymentGroupTasks.newBuilder().setTaskIndex(2).setRolloutTasks(ImmutableList.of(RolloutTask.of(UNDEPLOY_OLD_JOBS, "host1"), RolloutTask.of(DEPLOY_NEW_JOB, "host1"), RolloutTask.of(AWAIT_RUNNING, "host1"))).setDeploymentGroup(deploymentGroup).build();
        final RollingUpdateOpFactory opFactory = new RollingUpdateOpFactory(tasks, eventFactory);
        final RollingUpdateOp nextOp = opFactory.error("something went wrong", "host1", TIMED_OUT_WAITING_FOR_JOB_TO_REACH_RUNNING);
        Assert.assertThat(nextOp.operations(), Matchers.containsInAnyOrder(new com.spotify.helios.servicescommon.coordination.SetData("/status/deployment-groups/ignore_failure_group", DeploymentGroupStatus.newBuilder().setState(DONE).setError(null).build().toJsonBytes()), new Delete("/status/deployment-group-tasks/ignore_failure_group")));
    }
}

