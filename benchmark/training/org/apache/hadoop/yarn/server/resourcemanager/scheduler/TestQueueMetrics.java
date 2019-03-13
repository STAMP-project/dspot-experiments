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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler;


import NodeType.NODE_LOCAL;
import NodeType.OFF_SWITCH;
import NodeType.RACK_LOCAL;
import RMAppState.FAILED;
import RMAppState.FINISHED;
import RMNodeLabelsManager.NO_LABEL;
import YarnConfiguration.RM_SCHEDULER;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.impl.MetricsSystemImpl;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.AppMetricsChecker.AppMetricsKey.APPS_COMPLETED;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.AppMetricsChecker.AppMetricsKey.APPS_FAILED;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.AppMetricsChecker.AppMetricsKey.APPS_PENDING;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.AppMetricsChecker.AppMetricsKey.APPS_RUNNING;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.AppMetricsChecker.AppMetricsKey.APPS_SUBMITTED;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.AGGREGATE_CONTAINERS_ALLOCATED;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.AGGREGATE_CONTAINERS_RELEASED;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.ALLOCATED_CONTAINERS;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.ALLOCATED_MB;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.ALLOCATED_V_CORES;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.AVAILABLE_MB;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.AVAILABLE_V_CORES;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.PENDING_CONTAINERS;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.PENDING_MB;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.PENDING_V_CORES;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.RESERVED_CONTAINERS;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.RESERVED_MB;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.RESERVED_V_CORES;


public class TestQueueMetrics {
    private static final int GB = 1024;// MB


    private static final String USER = "alice";

    private static final String USER_2 = "dodo";

    private static final Configuration conf = new Configuration();

    private MetricsSystem ms;

    @Test
    public void testDefaultSingleQueueMetrics() {
        String queueName = "single";
        QueueMetrics metrics = QueueMetrics.forQueue(ms, queueName, null, false, TestQueueMetrics.conf);
        MetricsSource queueSource = TestQueueMetrics.queueSource(ms, queueName);
        AppSchedulingInfo app = TestQueueMetrics.mockApp(TestQueueMetrics.USER);
        metrics.submitApp(TestQueueMetrics.USER);
        MetricsSource userSource = TestQueueMetrics.userSource(ms, queueName, TestQueueMetrics.USER);
        AppMetricsChecker appMetricsChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(queueSource, true);
        metrics.submitAppAttempt(TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(queueSource, true);
        metrics.setAvailableResourcesToQueue(NO_LABEL, Resources.createResource((100 * (TestQueueMetrics.GB)), 100));
        metrics.incrPendingResources(NO_LABEL, TestQueueMetrics.USER, 5, Resources.createResource((3 * (TestQueueMetrics.GB)), 3));
        // Available resources is set externally, as it depends on dynamic
        // configurable cluster/queue resources
        ResourceMetricsChecker rmChecker = ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, (100 * (TestQueueMetrics.GB))).gaugeInt(AVAILABLE_V_CORES, 100).gaugeLong(PENDING_MB, (15 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 15).gaugeInt(PENDING_CONTAINERS, 5).checkAgainst(queueSource);
        metrics.runAppAttempt(app.getApplicationId(), TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(queueSource, true);
        metrics.allocateResources(NO_LABEL, TestQueueMetrics.USER, 3, Resources.createResource((2 * (TestQueueMetrics.GB)), 2), true);
        rmChecker = ResourceMetricsChecker.createFromChecker(rmChecker).gaugeLong(ALLOCATED_MB, (6 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 6).gaugeInt(ALLOCATED_CONTAINERS, 3).counter(AGGREGATE_CONTAINERS_ALLOCATED, 3).gaugeLong(PENDING_MB, (9 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 9).gaugeInt(PENDING_CONTAINERS, 2).checkAgainst(queueSource);
        metrics.releaseResources(NO_LABEL, TestQueueMetrics.USER, 1, Resources.createResource((2 * (TestQueueMetrics.GB)), 2));
        rmChecker = ResourceMetricsChecker.createFromChecker(rmChecker).gaugeLong(ALLOCATED_MB, (4 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 4).gaugeInt(ALLOCATED_CONTAINERS, 2).counter(AGGREGATE_CONTAINERS_RELEASED, 1).checkAgainst(queueSource);
        metrics.incrPendingResources(NO_LABEL, TestQueueMetrics.USER, 0, Resources.createResource((2 * (TestQueueMetrics.GB)), 2));
        // nothing should change in values
        rmChecker = ResourceMetricsChecker.createFromChecker(rmChecker).checkAgainst(queueSource);
        metrics.decrPendingResources(NO_LABEL, TestQueueMetrics.USER, 0, Resources.createResource((2 * (TestQueueMetrics.GB)), 2));
        // nothing should change in values
        ResourceMetricsChecker.createFromChecker(rmChecker).checkAgainst(queueSource);
        metrics.finishAppAttempt(app.getApplicationId(), app.isPending(), app.getUser());
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).counter(APPS_SUBMITTED, 1).gaugeInt(APPS_RUNNING, 0).checkAgainst(queueSource, true);
        metrics.finishApp(TestQueueMetrics.USER, FINISHED);
        AppMetricsChecker.createFromChecker(appMetricsChecker).counter(APPS_COMPLETED, 1).checkAgainst(queueSource, true);
        Assert.assertNull(userSource);
    }

    @Test
    public void testQueueAppMetricsForMultipleFailures() {
        String queueName = "single";
        QueueMetrics metrics = QueueMetrics.forQueue(ms, queueName, null, false, new Configuration());
        MetricsSource queueSource = TestQueueMetrics.queueSource(ms, queueName);
        AppSchedulingInfo app = TestQueueMetrics.mockApp(TestQueueMetrics.USER);
        metrics.submitApp(TestQueueMetrics.USER);
        MetricsSource userSource = TestQueueMetrics.userSource(ms, queueName, TestQueueMetrics.USER);
        AppMetricsChecker appMetricsChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(queueSource, true);
        metrics.submitAppAttempt(TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(queueSource, true);
        metrics.runAppAttempt(app.getApplicationId(), TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(queueSource, true);
        metrics.finishAppAttempt(app.getApplicationId(), app.isPending(), app.getUser());
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_RUNNING, 0).checkAgainst(queueSource, true);
        // As the application has failed, framework retries the same application
        // based on configuration
        metrics.submitAppAttempt(TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(queueSource, true);
        metrics.runAppAttempt(app.getApplicationId(), TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(queueSource, true);
        // Suppose say application has failed this time as well.
        metrics.finishAppAttempt(app.getApplicationId(), app.isPending(), app.getUser());
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_RUNNING, 0).checkAgainst(queueSource, true);
        // As the application has failed, framework retries the same application
        // based on configuration
        metrics.submitAppAttempt(TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(queueSource, true);
        metrics.runAppAttempt(app.getApplicationId(), TestQueueMetrics.USER);
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(queueSource, true);
        // Suppose say application has failed, and there's no more retries.
        metrics.finishAppAttempt(app.getApplicationId(), app.isPending(), app.getUser());
        appMetricsChecker = AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_RUNNING, 0).checkAgainst(queueSource, true);
        metrics.finishApp(TestQueueMetrics.USER, FAILED);
        AppMetricsChecker.createFromChecker(appMetricsChecker).gaugeInt(APPS_RUNNING, 0).counter(APPS_FAILED, 1).checkAgainst(queueSource, true);
        Assert.assertNull(userSource);
    }

    @Test
    public void testSingleQueueWithUserMetrics() {
        String queueName = "single2";
        QueueMetrics metrics = QueueMetrics.forQueue(ms, queueName, null, true, TestQueueMetrics.conf);
        MetricsSource queueSource = TestQueueMetrics.queueSource(ms, queueName);
        AppSchedulingInfo app = TestQueueMetrics.mockApp(TestQueueMetrics.USER_2);
        metrics.submitApp(TestQueueMetrics.USER_2);
        MetricsSource userSource = TestQueueMetrics.userSource(ms, queueName, TestQueueMetrics.USER_2);
        AppMetricsChecker appMetricsQueueSourceChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(queueSource, true);
        AppMetricsChecker appMetricsUserSourceChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(userSource, true);
        metrics.submitAppAttempt(TestQueueMetrics.USER_2);
        appMetricsQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(queueSource, true);
        appMetricsUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(userSource, true);
        metrics.setAvailableResourcesToQueue(NO_LABEL, Resources.createResource((100 * (TestQueueMetrics.GB)), 100));
        metrics.setAvailableResourcesToUser(NO_LABEL, TestQueueMetrics.USER_2, Resources.createResource((10 * (TestQueueMetrics.GB)), 10));
        metrics.incrPendingResources(NO_LABEL, TestQueueMetrics.USER_2, 5, Resources.createResource((3 * (TestQueueMetrics.GB)), 3));
        // Available resources is set externally, as it depends on dynamic
        // configurable cluster/queue resources
        ResourceMetricsChecker resMetricsQueueSourceChecker = ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, (100 * (TestQueueMetrics.GB))).gaugeInt(AVAILABLE_V_CORES, 100).gaugeLong(PENDING_MB, (15 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 15).gaugeInt(PENDING_CONTAINERS, 5).checkAgainst(queueSource);
        ResourceMetricsChecker resMetricsUserSourceChecker = ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, (10 * (TestQueueMetrics.GB))).gaugeInt(AVAILABLE_V_CORES, 10).gaugeLong(PENDING_MB, (15 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 15).gaugeInt(PENDING_CONTAINERS, 5).checkAgainst(userSource);
        metrics.runAppAttempt(app.getApplicationId(), TestQueueMetrics.USER_2);
        appMetricsQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(queueSource, true);
        appMetricsUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(userSource, true);
        metrics.allocateResources(NO_LABEL, TestQueueMetrics.USER_2, 3, Resources.createResource((2 * (TestQueueMetrics.GB)), 2), true);
        resMetricsQueueSourceChecker = ResourceMetricsChecker.createFromChecker(resMetricsQueueSourceChecker).gaugeLong(ALLOCATED_MB, (6 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 6).gaugeInt(ALLOCATED_CONTAINERS, 3).counter(AGGREGATE_CONTAINERS_ALLOCATED, 3).gaugeLong(PENDING_MB, (9 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 9).gaugeInt(PENDING_CONTAINERS, 2).checkAgainst(queueSource);
        resMetricsUserSourceChecker = ResourceMetricsChecker.createFromChecker(resMetricsUserSourceChecker).gaugeLong(ALLOCATED_MB, (6 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 6).gaugeInt(ALLOCATED_CONTAINERS, 3).counter(AGGREGATE_CONTAINERS_ALLOCATED, 3).gaugeLong(PENDING_MB, (9 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 9).gaugeInt(PENDING_CONTAINERS, 2).checkAgainst(userSource);
        metrics.releaseResources(NO_LABEL, TestQueueMetrics.USER_2, 1, Resources.createResource((2 * (TestQueueMetrics.GB)), 2));
        ResourceMetricsChecker.createFromChecker(resMetricsQueueSourceChecker).gaugeLong(ALLOCATED_MB, (4 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 4).gaugeInt(ALLOCATED_CONTAINERS, 2).counter(AGGREGATE_CONTAINERS_RELEASED, 1).checkAgainst(queueSource);
        ResourceMetricsChecker.createFromChecker(resMetricsUserSourceChecker).gaugeLong(ALLOCATED_MB, (4 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 4).gaugeInt(ALLOCATED_CONTAINERS, 2).counter(AGGREGATE_CONTAINERS_RELEASED, 1).checkAgainst(userSource);
        metrics.finishAppAttempt(app.getApplicationId(), app.isPending(), app.getUser());
        appMetricsQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).gaugeInt(APPS_RUNNING, 0).checkAgainst(queueSource, true);
        appMetricsUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).gaugeInt(APPS_RUNNING, 0).checkAgainst(userSource, true);
        metrics.finishApp(TestQueueMetrics.USER_2, FINISHED);
        AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).counter(APPS_COMPLETED, 1).checkAgainst(queueSource, true);
        AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).counter(APPS_COMPLETED, 1).checkAgainst(userSource, true);
    }

    @Test
    public void testNodeTypeMetrics() {
        String parentQueueName = "root";
        String leafQueueName = "root.leaf";
        QueueMetrics parentMetrics = QueueMetrics.forQueue(ms, parentQueueName, null, true, TestQueueMetrics.conf);
        Queue parentQueue = Mockito.mock(Queue.class);
        Mockito.when(parentQueue.getMetrics()).thenReturn(parentMetrics);
        QueueMetrics metrics = QueueMetrics.forQueue(ms, leafQueueName, parentQueue, true, TestQueueMetrics.conf);
        MetricsSource parentQueueSource = TestQueueMetrics.queueSource(ms, parentQueueName);
        MetricsSource queueSource = TestQueueMetrics.queueSource(ms, leafQueueName);
        // AppSchedulingInfo app = mockApp(user);
        metrics.submitApp(TestQueueMetrics.USER);
        MetricsSource userSource = TestQueueMetrics.userSource(ms, leafQueueName, TestQueueMetrics.USER);
        MetricsSource parentUserSource = TestQueueMetrics.userSource(ms, parentQueueName, TestQueueMetrics.USER);
        metrics.incrNodeTypeAggregations(TestQueueMetrics.USER, NODE_LOCAL);
        TestQueueMetrics.checkAggregatedNodeTypes(queueSource, 1L, 0L, 0L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentQueueSource, 1L, 0L, 0L);
        TestQueueMetrics.checkAggregatedNodeTypes(userSource, 1L, 0L, 0L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentUserSource, 1L, 0L, 0L);
        metrics.incrNodeTypeAggregations(TestQueueMetrics.USER, RACK_LOCAL);
        TestQueueMetrics.checkAggregatedNodeTypes(queueSource, 1L, 1L, 0L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentQueueSource, 1L, 1L, 0L);
        TestQueueMetrics.checkAggregatedNodeTypes(userSource, 1L, 1L, 0L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentUserSource, 1L, 1L, 0L);
        metrics.incrNodeTypeAggregations(TestQueueMetrics.USER, OFF_SWITCH);
        TestQueueMetrics.checkAggregatedNodeTypes(queueSource, 1L, 1L, 1L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentQueueSource, 1L, 1L, 1L);
        TestQueueMetrics.checkAggregatedNodeTypes(userSource, 1L, 1L, 1L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentUserSource, 1L, 1L, 1L);
        metrics.incrNodeTypeAggregations(TestQueueMetrics.USER, OFF_SWITCH);
        TestQueueMetrics.checkAggregatedNodeTypes(queueSource, 1L, 1L, 2L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentQueueSource, 1L, 1L, 2L);
        TestQueueMetrics.checkAggregatedNodeTypes(userSource, 1L, 1L, 2L);
        TestQueueMetrics.checkAggregatedNodeTypes(parentUserSource, 1L, 1L, 2L);
    }

    @Test
    public void testTwoLevelWithUserMetrics() {
        AppSchedulingInfo app = TestQueueMetrics.mockApp(TestQueueMetrics.USER);
        QueueInfo root = new QueueInfo(null, "root", ms, TestQueueMetrics.conf, TestQueueMetrics.USER);
        QueueInfo leaf = new QueueInfo(root, "root.leaf", ms, TestQueueMetrics.conf, TestQueueMetrics.USER);
        leaf.queueMetrics.submitApp(TestQueueMetrics.USER);
        AppMetricsChecker appMetricsQueueSourceChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(leaf.queueSource, true);
        AppMetricsChecker appMetricsParentQueueSourceChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(root.queueSource, true);
        AppMetricsChecker appMetricsUserSourceChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(leaf.userSource, true);
        AppMetricsChecker appMetricsParentUserSourceChecker = AppMetricsChecker.create().counter(APPS_SUBMITTED, 1).checkAgainst(root.userSource, true);
        leaf.queueMetrics.submitAppAttempt(TestQueueMetrics.USER);
        appMetricsQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(leaf.queueSource, true);
        appMetricsParentQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsParentQueueSourceChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(root.queueSource, true);
        appMetricsUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(leaf.userSource, true);
        appMetricsParentUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsParentUserSourceChecker).gaugeInt(APPS_PENDING, 1).checkAgainst(root.userSource, true);
        root.queueMetrics.setAvailableResourcesToQueue(NO_LABEL, Resources.createResource((100 * (TestQueueMetrics.GB)), 100));
        leaf.queueMetrics.setAvailableResourcesToQueue(NO_LABEL, Resources.createResource((100 * (TestQueueMetrics.GB)), 100));
        root.queueMetrics.setAvailableResourcesToUser(NO_LABEL, TestQueueMetrics.USER, Resources.createResource((10 * (TestQueueMetrics.GB)), 10));
        leaf.queueMetrics.setAvailableResourcesToUser(NO_LABEL, TestQueueMetrics.USER, Resources.createResource((10 * (TestQueueMetrics.GB)), 10));
        leaf.queueMetrics.incrPendingResources(NO_LABEL, TestQueueMetrics.USER, 5, Resources.createResource((3 * (TestQueueMetrics.GB)), 3));
        ResourceMetricsChecker resMetricsQueueSourceChecker = ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, (100 * (TestQueueMetrics.GB))).gaugeInt(AVAILABLE_V_CORES, 100).gaugeLong(PENDING_MB, (15 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 15).gaugeInt(PENDING_CONTAINERS, 5).checkAgainst(leaf.queueSource);
        ResourceMetricsChecker resMetricsParentQueueSourceChecker = ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, (100 * (TestQueueMetrics.GB))).gaugeInt(AVAILABLE_V_CORES, 100).gaugeLong(PENDING_MB, (15 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 15).gaugeInt(PENDING_CONTAINERS, 5).checkAgainst(root.queueSource);
        ResourceMetricsChecker resMetricsUserSourceChecker = ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, (10 * (TestQueueMetrics.GB))).gaugeInt(AVAILABLE_V_CORES, 10).gaugeLong(PENDING_MB, (15 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 15).gaugeInt(PENDING_CONTAINERS, 5).checkAgainst(leaf.userSource);
        ResourceMetricsChecker resMetricsParentUserSourceChecker = ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, (10 * (TestQueueMetrics.GB))).gaugeInt(AVAILABLE_V_CORES, 10).gaugeLong(PENDING_MB, (15 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 15).gaugeInt(PENDING_CONTAINERS, 5).checkAgainst(root.userSource);
        leaf.queueMetrics.runAppAttempt(app.getApplicationId(), TestQueueMetrics.USER);
        appMetricsQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(leaf.queueSource, true);
        appMetricsUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 1).checkAgainst(leaf.userSource, true);
        leaf.queueMetrics.allocateResources(NO_LABEL, TestQueueMetrics.USER, 3, Resources.createResource((2 * (TestQueueMetrics.GB)), 2), true);
        leaf.queueMetrics.reserveResource(NO_LABEL, TestQueueMetrics.USER, Resources.createResource((3 * (TestQueueMetrics.GB)), 3));
        // Available resources is set externally, as it depends on dynamic
        // configurable cluster/queue resources
        resMetricsQueueSourceChecker = ResourceMetricsChecker.createFromChecker(resMetricsQueueSourceChecker).gaugeLong(ALLOCATED_MB, (6 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 6).gaugeInt(ALLOCATED_CONTAINERS, 3).counter(AGGREGATE_CONTAINERS_ALLOCATED, 3).gaugeLong(PENDING_MB, (9 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 9).gaugeInt(PENDING_CONTAINERS, 2).gaugeLong(RESERVED_MB, (3 * (TestQueueMetrics.GB))).gaugeInt(RESERVED_V_CORES, 3).gaugeInt(RESERVED_CONTAINERS, 1).checkAgainst(leaf.queueSource);
        resMetricsParentQueueSourceChecker = ResourceMetricsChecker.createFromChecker(resMetricsParentQueueSourceChecker).gaugeLong(ALLOCATED_MB, (6 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 6).gaugeInt(ALLOCATED_CONTAINERS, 3).counter(AGGREGATE_CONTAINERS_ALLOCATED, 3).gaugeLong(PENDING_MB, (9 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 9).gaugeInt(PENDING_CONTAINERS, 2).gaugeLong(RESERVED_MB, (3 * (TestQueueMetrics.GB))).gaugeInt(RESERVED_V_CORES, 3).gaugeInt(RESERVED_CONTAINERS, 1).checkAgainst(root.queueSource);
        resMetricsUserSourceChecker = ResourceMetricsChecker.createFromChecker(resMetricsUserSourceChecker).gaugeLong(ALLOCATED_MB, (6 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 6).gaugeInt(ALLOCATED_CONTAINERS, 3).counter(AGGREGATE_CONTAINERS_ALLOCATED, 3).gaugeLong(PENDING_MB, (9 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 9).gaugeInt(PENDING_CONTAINERS, 2).gaugeLong(RESERVED_MB, (3 * (TestQueueMetrics.GB))).gaugeInt(RESERVED_V_CORES, 3).gaugeInt(RESERVED_CONTAINERS, 1).checkAgainst(leaf.userSource);
        resMetricsParentUserSourceChecker = ResourceMetricsChecker.createFromChecker(resMetricsParentUserSourceChecker).gaugeLong(ALLOCATED_MB, (6 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 6).gaugeInt(ALLOCATED_CONTAINERS, 3).counter(AGGREGATE_CONTAINERS_ALLOCATED, 3).gaugeLong(PENDING_MB, (9 * (TestQueueMetrics.GB))).gaugeInt(PENDING_V_CORES, 9).gaugeInt(PENDING_CONTAINERS, 2).gaugeLong(RESERVED_MB, (3 * (TestQueueMetrics.GB))).gaugeInt(RESERVED_V_CORES, 3).gaugeInt(RESERVED_CONTAINERS, 1).checkAgainst(root.userSource);
        leaf.queueMetrics.releaseResources(NO_LABEL, TestQueueMetrics.USER, 1, Resources.createResource((2 * (TestQueueMetrics.GB)), 2));
        leaf.queueMetrics.unreserveResource(NO_LABEL, TestQueueMetrics.USER, Resources.createResource((3 * (TestQueueMetrics.GB)), 3));
        ResourceMetricsChecker.createFromChecker(resMetricsQueueSourceChecker).gaugeLong(ALLOCATED_MB, (4 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 4).gaugeInt(ALLOCATED_CONTAINERS, 2).counter(AGGREGATE_CONTAINERS_RELEASED, 1).gaugeLong(RESERVED_MB, 0).gaugeInt(RESERVED_V_CORES, 0).gaugeInt(RESERVED_CONTAINERS, 0).checkAgainst(leaf.queueSource);
        ResourceMetricsChecker.createFromChecker(resMetricsParentQueueSourceChecker).gaugeLong(ALLOCATED_MB, (4 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 4).gaugeInt(ALLOCATED_CONTAINERS, 2).counter(AGGREGATE_CONTAINERS_RELEASED, 1).gaugeLong(RESERVED_MB, 0).gaugeInt(RESERVED_V_CORES, 0).gaugeInt(RESERVED_CONTAINERS, 0).checkAgainst(root.queueSource);
        ResourceMetricsChecker.createFromChecker(resMetricsUserSourceChecker).gaugeLong(ALLOCATED_MB, (4 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 4).gaugeInt(ALLOCATED_CONTAINERS, 2).counter(AGGREGATE_CONTAINERS_RELEASED, 1).gaugeLong(RESERVED_MB, 0).gaugeInt(RESERVED_V_CORES, 0).gaugeInt(RESERVED_CONTAINERS, 0).checkAgainst(leaf.userSource);
        ResourceMetricsChecker.createFromChecker(resMetricsParentUserSourceChecker).gaugeLong(ALLOCATED_MB, (4 * (TestQueueMetrics.GB))).gaugeInt(ALLOCATED_V_CORES, 4).gaugeInt(ALLOCATED_CONTAINERS, 2).counter(AGGREGATE_CONTAINERS_RELEASED, 1).gaugeLong(RESERVED_MB, 0).gaugeInt(RESERVED_V_CORES, 0).gaugeInt(RESERVED_CONTAINERS, 0).checkAgainst(root.userSource);
        leaf.queueMetrics.finishAppAttempt(app.getApplicationId(), app.isPending(), app.getUser());
        appMetricsQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).counter(APPS_SUBMITTED, 1).gaugeInt(APPS_RUNNING, 0).checkAgainst(leaf.queueSource, true);
        appMetricsParentQueueSourceChecker = AppMetricsChecker.createFromChecker(appMetricsParentQueueSourceChecker).counter(APPS_SUBMITTED, 1).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 0).checkAgainst(root.queueSource, true);
        appMetricsUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).counter(APPS_SUBMITTED, 1).gaugeInt(APPS_RUNNING, 0).checkAgainst(leaf.userSource, true);
        appMetricsParentUserSourceChecker = AppMetricsChecker.createFromChecker(appMetricsParentUserSourceChecker).counter(APPS_SUBMITTED, 1).gaugeInt(APPS_PENDING, 0).gaugeInt(APPS_RUNNING, 0).checkAgainst(root.userSource, true);
        leaf.queueMetrics.finishApp(TestQueueMetrics.USER, FINISHED);
        AppMetricsChecker.createFromChecker(appMetricsQueueSourceChecker).counter(APPS_COMPLETED, 1).checkAgainst(leaf.queueSource, true);
        AppMetricsChecker.createFromChecker(appMetricsParentQueueSourceChecker).counter(APPS_COMPLETED, 1).checkAgainst(root.queueSource, true);
        AppMetricsChecker.createFromChecker(appMetricsUserSourceChecker).counter(APPS_COMPLETED, 1).checkAgainst(leaf.userSource, true);
        AppMetricsChecker.createFromChecker(appMetricsParentUserSourceChecker).counter(APPS_COMPLETED, 1).checkAgainst(root.userSource, true);
    }

    @Test
    public void testMetricsCache() {
        MetricsSystem ms = new MetricsSystemImpl("cache");
        ms.start();
        try {
            String p1 = "root1";
            String leafQueueName = "root1.leaf";
            QueueMetrics p1Metrics = QueueMetrics.forQueue(ms, p1, null, true, TestQueueMetrics.conf);
            Queue parentQueue1 = Mockito.mock(Queue.class);
            Mockito.when(parentQueue1.getMetrics()).thenReturn(p1Metrics);
            QueueMetrics metrics = QueueMetrics.forQueue(ms, leafQueueName, parentQueue1, true, TestQueueMetrics.conf);
            Assert.assertNotNull("QueueMetrics for A shoudn't be null", metrics);
            // Re-register to check for cache hit, shouldn't blow up metrics-system...
            // also, verify parent-metrics
            QueueMetrics alterMetrics = QueueMetrics.forQueue(ms, leafQueueName, parentQueue1, true, TestQueueMetrics.conf);
            Assert.assertNotNull("QueueMetrics for alterMetrics shoudn't be null", alterMetrics);
        } finally {
            ms.shutdown();
        }
    }

    @Test
    public void testMetricsInitializedOnRMInit() {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setClass(RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        AppMetricsChecker.create().checkAgainst(metrics, true);
        MetricsAsserts.assertGauge(RESERVED_CONTAINERS.getValue(), 0, metrics);
    }

    // This is to test all metrics can consistently show up if specified true to
    // collect all metrics, even though they are not modified from last time they
    // are collected. If not collecting all metrics, only modified metrics will show up.
    @Test
    public void testCollectAllMetrics() {
        String queueName = "single";
        QueueMetrics.forQueue(ms, queueName, null, false, TestQueueMetrics.conf);
        MetricsSource queueSource = TestQueueMetrics.queueSource(ms, queueName);
        AppMetricsChecker.create().checkAgainst(queueSource, true);
        try {
            // do not collect all metrics
            AppMetricsChecker.create().checkAgainst(queueSource, false);
            Assert.fail();
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains("Expected exactly one metric for name "));
        }
        // collect all metrics
        AppMetricsChecker.create().checkAgainst(queueSource, true);
    }
}

