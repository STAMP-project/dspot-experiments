/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler;


import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.function.BiFunction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.resourcetypes.ResourceTypesTestHelper;
import org.junit.Test;

import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.AGGREGATE_CONTAINERS_ALLOCATED;
import static org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceMetricsChecker.ResourceMetricsKey.AGGREGATE_CONTAINERS_RELEASED;
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


public class TestQueueMetricsForCustomResources {
    public enum MetricsForCustomResource {

        ALLOCATED,
        AVAILABLE,
        PENDING,
        RESERVED,
        AGGREGATE_PREEMPTED_SECONDS;}

    public static final long GB = 1024;// MB


    private static final Configuration CONF = new Configuration();

    private static final String CUSTOM_RES_1 = "custom_res_1";

    private static final String CUSTOM_RES_2 = "custom_res_2";

    public static final String USER = "alice";

    private Resource defaultResource;

    private MetricsSystem ms;

    @Test
    public void testSetAvailableResourcesToQueue1() {
        String queueName = "single";
        QueueMetrics metrics = QueueMetrics.forQueue(ms, queueName, null, false, TestQueueMetricsForCustomResources.CONF);
        MetricsSource queueSource = TestQueueMetrics.queueSource(ms, queueName);
        metrics.setAvailableResourcesToQueue(newResource(TestQueueMetricsForCustomResources.GB, 4, ImmutableMap.<String, String>builder().put(TestQueueMetricsForCustomResources.CUSTOM_RES_1, String.valueOf((5 * (TestQueueMetricsForCustomResources.GB)))).put(TestQueueMetricsForCustomResources.CUSTOM_RES_2, String.valueOf((6 * (TestQueueMetricsForCustomResources.GB)))).build()));
        ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, TestQueueMetricsForCustomResources.GB).gaugeInt(AVAILABLE_V_CORES, 4).checkAgainst(queueSource);
        TestQueueMetricsForCustomResources.assertCustomResourceValue(metrics, TestQueueMetricsForCustomResources.MetricsForCustomResource.AVAILABLE, QueueMetrics::getAvailableResources, TestQueueMetricsForCustomResources.CUSTOM_RES_1, (5 * (TestQueueMetricsForCustomResources.GB)));
        TestQueueMetricsForCustomResources.assertCustomResourceValue(metrics, TestQueueMetricsForCustomResources.MetricsForCustomResource.AVAILABLE, QueueMetrics::getAvailableResources, TestQueueMetricsForCustomResources.CUSTOM_RES_2, (6 * (TestQueueMetricsForCustomResources.GB)));
    }

    @Test
    public void testSetAvailableResourcesToQueue2() {
        String queueName = "single";
        QueueMetrics metrics = QueueMetrics.forQueue(ms, queueName, null, false, TestQueueMetricsForCustomResources.CONF);
        MetricsSource queueSource = TestQueueMetrics.queueSource(ms, queueName);
        metrics.setAvailableResourcesToQueue(null, newResource(TestQueueMetricsForCustomResources.GB, 4, ImmutableMap.<String, String>builder().put(TestQueueMetricsForCustomResources.CUSTOM_RES_1, String.valueOf((15 * (TestQueueMetricsForCustomResources.GB)))).put(TestQueueMetricsForCustomResources.CUSTOM_RES_2, String.valueOf((20 * (TestQueueMetricsForCustomResources.GB)))).build()));
        ResourceMetricsChecker.create().gaugeLong(AVAILABLE_MB, TestQueueMetricsForCustomResources.GB).gaugeInt(AVAILABLE_V_CORES, 4).checkAgainst(queueSource);
        TestQueueMetricsForCustomResources.assertCustomResourceValue(metrics, TestQueueMetricsForCustomResources.MetricsForCustomResource.AVAILABLE, QueueMetrics::getAvailableResources, TestQueueMetricsForCustomResources.CUSTOM_RES_1, (15 * (TestQueueMetricsForCustomResources.GB)));
        TestQueueMetricsForCustomResources.assertCustomResourceValue(metrics, TestQueueMetricsForCustomResources.MetricsForCustomResource.AVAILABLE, QueueMetrics::getAvailableResources, TestQueueMetricsForCustomResources.CUSTOM_RES_2, (20 * (TestQueueMetricsForCustomResources.GB)));
    }

    @Test
    public void testIncreasePendingResources() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createBasicQueueHierarchy()).withResourceToDecrease(newResource(TestQueueMetricsForCustomResources.GB, 2, TestQueueMetricsForCustomResources.getCustomResourcesWithValue((2 * (TestQueueMetricsForCustomResources.GB)))), 2).withResources(defaultResource).build();
        testIncreasePendingResources(testData);
    }

    @Test
    public void testDecreasePendingResources() {
        Resource resourceToDecrease = newResource(TestQueueMetricsForCustomResources.GB, 2, TestQueueMetricsForCustomResources.getCustomResourcesWithValue((2 * (TestQueueMetricsForCustomResources.GB))));
        int containersToDecrease = 2;
        int containers = 5;
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(containers).withLeafQueue(createBasicQueueHierarchy()).withResourceToDecrease(resourceToDecrease, containers).withResources(defaultResource).build();
        // compute expected values
        final int vCoresToDecrease = resourceToDecrease.getVirtualCores();
        final long memoryMBToDecrease = resourceToDecrease.getMemorySize();
        final int containersAfterDecrease = containers - containersToDecrease;
        final int vcoresAfterDecrease = ((defaultResource.getVirtualCores()) * containers) - (vCoresToDecrease * containersToDecrease);
        final long memoryAfterDecrease = ((defaultResource.getMemorySize()) * containers) - (memoryMBToDecrease * containersToDecrease);
        // first, increase resources to be able to decrease some
        testIncreasePendingResources(testData);
        // decrease resources
        testData.leafQueue.queueMetrics.decrPendingResources(testData.partition, testData.user, containersToDecrease, ResourceTypesTestHelper.newResource(memoryMBToDecrease, vCoresToDecrease, extractCustomResourcesAsStrings(resourceToDecrease)));
        // check
        ResourceMetricsChecker checker = ResourceMetricsChecker.create().gaugeInt(PENDING_CONTAINERS, containersAfterDecrease).gaugeLong(PENDING_MB, memoryAfterDecrease).gaugeInt(PENDING_V_CORES, vcoresAfterDecrease).checkAgainst(testData.leafQueue.queueSource);
        assertAllMetrics(testData.leafQueue, checker, QueueMetrics::getPendingResources, TestQueueMetricsForCustomResources.MetricsForCustomResource.PENDING, computeExpectedCustomResourceValues(testData.customResourceValues, ( k, v) -> (v * containers) - ((resourceToDecrease.getResourceValue(k)) * containersToDecrease)));
    }

    @Test
    public void testAllocateResourcesWithoutDecreasePending() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createBasicQueueHierarchy()).withResources(defaultResource).build();
        testAllocateResources(false, testData);
    }

    @Test
    public void testAllocateResourcesWithDecreasePending() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createBasicQueueHierarchy()).withResourceToDecrease(newResource(TestQueueMetricsForCustomResources.GB, 2, TestQueueMetricsForCustomResources.getCustomResourcesWithValue((2 * (TestQueueMetricsForCustomResources.GB)))), 2).withResources(defaultResource).build();
        // first, increase pending resources to be able to decrease some
        testIncreasePendingResources(testData);
        // then allocate with decrease pending resources
        testAllocateResources(true, testData);
    }

    @Test
    public void testAllocateResourcesWithoutContainer() {
        QueueMetricsTestData testData = createDefaultQueueMetricsTestData().withLeafQueue(createBasicQueueHierarchy()).withResources(defaultResource).build();
        // first, increase pending resources
        testIncreasePendingResourcesWithoutContainer(testData);
        Resource resource = testData.resource;
        testData.leafQueue.queueMetrics.allocateResources(testData.partition, testData.user, resource);
        ResourceMetricsChecker checker = ResourceMetricsChecker.create().gaugeLong(ALLOCATED_MB, resource.getMemorySize()).gaugeInt(ALLOCATED_V_CORES, resource.getVirtualCores()).gaugeInt(PENDING_CONTAINERS, 1).gaugeLong(PENDING_MB, 0).gaugeInt(PENDING_V_CORES, 0);
        checker.checkAgainst(testData.leafQueue.queueSource);
        checker.checkAgainst(testData.leafQueue.getRoot().queueSource);
        assertAllMetrics(testData.leafQueue, checker, QueueMetrics::getPendingResources, TestQueueMetricsForCustomResources.MetricsForCustomResource.PENDING, computeExpectedCustomResourceValues(testData.customResourceValues, ( k, v) -> 0L));
        assertAllMetrics(testData.leafQueue, checker, QueueMetrics::getAllocatedResources, TestQueueMetricsForCustomResources.MetricsForCustomResource.ALLOCATED, computeExpectedCustomResourceValues(testData.customResourceValues, ( k, v) -> v));
    }

    @Test
    public void testReleaseResources() {
        int containers = 5;
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(containers).withLeafQueue(createBasicQueueHierarchy()).withResourceToDecrease(defaultResource, containers).withResources(defaultResource).build();
        // first, allocate some resources so that we can release some
        testAllocateResources(false, testData);
        testData.leafQueue.queueMetrics.releaseResources(testData.partition, testData.user, containers, defaultResource);
        ResourceMetricsChecker checker = ResourceMetricsChecker.create().counter(AGGREGATE_CONTAINERS_ALLOCATED, containers).counter(AGGREGATE_CONTAINERS_RELEASED, containers).checkAgainst(testData.leafQueue.queueSource);
        assertAllMetrics(testData.leafQueue, checker, QueueMetrics::getAllocatedResources, TestQueueMetricsForCustomResources.MetricsForCustomResource.ALLOCATED, computeExpectedCustomResourceValues(testData.customResourceValues, ( k, v) -> 0L));
    }

    @Test
    public void testUpdatePreemptedSecondsForCustomResources() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createFourLevelQueueHierarchy()).withResources(defaultResource).build();
        final int seconds = 1;
        testUpdatePreemptedSeconds(testData, seconds);
    }

    @Test
    public void testUpdatePreemptedSecondsForCustomResourcesMoreSeconds() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createFourLevelQueueHierarchy()).withResources(defaultResource).build();
        final int seconds = 15;
        testUpdatePreemptedSeconds(testData, seconds);
    }

    @Test
    public void testReserveResources() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createBasicQueueHierarchy()).withResources(defaultResource).build();
        testReserveResources(testData);
    }

    @Test
    public void testUnreserveResources() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createBasicQueueHierarchy()).withResources(defaultResource).build();
        testReserveResources(testData);
        testData.leafQueue.queueMetrics.unreserveResource(testData.partition, testData.user, defaultResource);
        ResourceMetricsChecker checker = ResourceMetricsChecker.create().gaugeInt(RESERVED_CONTAINERS, 0).gaugeLong(RESERVED_MB, 0).gaugeInt(RESERVED_V_CORES, 0).checkAgainst(testData.leafQueue.queueSource);
        assertAllMetrics(testData.leafQueue, checker, QueueMetrics::getReservedResources, TestQueueMetricsForCustomResources.MetricsForCustomResource.RESERVED, computeExpectedCustomResourceValues(testData.customResourceValues, ( k, v) -> 0L));
    }

    @Test
    public void testGetAllocatedResourcesWithCustomResources() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withLeafQueue(createBasicQueueHierarchy()).withResources(defaultResource).build();
        testGetAllocatedResources(testData);
    }

    @Test
    public void testGetAllocatedResourcesWithoutCustomResources() {
        QueueMetricsTestData testData = createQueueMetricsTestDataWithContainers(5).withResources(newResource((4 * (TestQueueMetricsForCustomResources.GB)), 4, Collections.emptyMap())).withLeafQueue(createBasicQueueHierarchy()).build();
        testGetAllocatedResources(testData);
    }
}

