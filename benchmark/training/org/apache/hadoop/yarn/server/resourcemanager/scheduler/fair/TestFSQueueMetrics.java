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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import com.google.common.collect.ImmutableMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.yarn.api.records.Resource;
import org.junit.Assert;
import org.junit.Test;


/**
 * The test class for {@link FSQueueMetrics}.
 */
public class TestFSQueueMetrics {
    private static final Configuration CONF = new Configuration();

    private MetricsSystem ms;

    private static final String RESOURCE_NAME = "test1";

    private static final String QUEUE_NAME = "single";

    /**
     * Test if the metric scheduling policy is set correctly.
     */
    @Test
    public void testSchedulingPolicy() {
        String queueName = "single";
        FSQueueMetrics metrics = FSQueueMetrics.forQueue(ms, queueName, null, false, TestFSQueueMetrics.CONF);
        metrics.setSchedulingPolicy("drf");
        checkSchedulingPolicy(queueName, "drf");
        // test resetting the scheduling policy
        metrics.setSchedulingPolicy("fair");
        checkSchedulingPolicy(queueName, "fair");
    }

    @Test
    public void testSetFairShare() {
        FSQueueMetrics metrics = setupMetrics(TestFSQueueMetrics.RESOURCE_NAME);
        Resource res = Resource.newInstance(2048L, 4, ImmutableMap.of(TestFSQueueMetrics.RESOURCE_NAME, 20L));
        metrics.setFairShare(res);
        Assert.assertEquals(getErrorMessage("fairShareMB"), 2048L, metrics.getFairShareMB());
        Assert.assertEquals(getErrorMessage("fairShareVcores"), 4L, metrics.getFairShareVirtualCores());
        Assert.assertEquals(getErrorMessage("fairShareMB"), 2048L, metrics.getFairShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("fairShareVcores"), 4L, metrics.getFairShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("fairShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 20L, metrics.getFairShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
        res = Resource.newInstance(2049L, 5);
        metrics.setFairShare(res);
        Assert.assertEquals(getErrorMessage("fairShareMB"), 2049L, metrics.getFairShareMB());
        Assert.assertEquals(getErrorMessage("fairShareVcores"), 5L, metrics.getFairShareVirtualCores());
        Assert.assertEquals(getErrorMessage("fairShareMB"), 2049L, metrics.getFairShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("fairShareVcores"), 5L, metrics.getFairShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("fairShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 0, metrics.getFairShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
    }

    @Test
    public void testSetSteadyFairShare() {
        FSQueueMetrics metrics = setupMetrics(TestFSQueueMetrics.RESOURCE_NAME);
        Resource res = Resource.newInstance(2048L, 4, ImmutableMap.of(TestFSQueueMetrics.RESOURCE_NAME, 20L));
        metrics.setSteadyFairShare(res);
        Assert.assertEquals(getErrorMessage("steadyFairShareMB"), 2048L, metrics.getSteadyFairShareMB());
        Assert.assertEquals(getErrorMessage("steadyFairShareVcores"), 4L, metrics.getSteadyFairShareVCores());
        Resource steadyFairShare = metrics.getSteadyFairShare();
        Assert.assertEquals(getErrorMessage("steadyFairShareMB"), 2048L, steadyFairShare.getMemorySize());
        Assert.assertEquals(getErrorMessage("steadyFairShareVcores"), 4L, steadyFairShare.getVirtualCores());
        Assert.assertEquals(getErrorMessage(("steadyFairShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 20L, steadyFairShare.getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
        res = Resource.newInstance(2049L, 5);
        metrics.setSteadyFairShare(res);
        Assert.assertEquals(getErrorMessage("steadyFairShareMB"), 2049L, metrics.getSteadyFairShareMB());
        Assert.assertEquals(getErrorMessage("steadyFairShareVcores"), 5L, metrics.getSteadyFairShareVCores());
        steadyFairShare = metrics.getSteadyFairShare();
        Assert.assertEquals(getErrorMessage("steadyFairShareMB"), 2049L, steadyFairShare.getMemorySize());
        Assert.assertEquals(getErrorMessage("steadyFairShareVcores"), 5L, steadyFairShare.getVirtualCores());
        Assert.assertEquals(getErrorMessage(("steadyFairShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 0, steadyFairShare.getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
    }

    @Test
    public void testSetMinShare() {
        FSQueueMetrics metrics = setupMetrics(TestFSQueueMetrics.RESOURCE_NAME);
        Resource res = Resource.newInstance(2048L, 4, ImmutableMap.of(TestFSQueueMetrics.RESOURCE_NAME, 20L));
        metrics.setMinShare(res);
        Assert.assertEquals(getErrorMessage("minShareMB"), 2048L, metrics.getMinShareMB());
        Assert.assertEquals(getErrorMessage("minShareVcores"), 4L, metrics.getMinShareVirtualCores());
        Assert.assertEquals(getErrorMessage("minShareMB"), 2048L, metrics.getMinShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("minShareVcores"), 4L, metrics.getMinShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("minShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 20L, metrics.getMinShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
        res = Resource.newInstance(2049L, 5);
        metrics.setMinShare(res);
        Assert.assertEquals(getErrorMessage("minShareMB"), 2049L, metrics.getMinShareMB());
        Assert.assertEquals(getErrorMessage("minShareVcores"), 5L, metrics.getMinShareVirtualCores());
        Assert.assertEquals(getErrorMessage("minShareMB"), 2049L, metrics.getMinShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("minShareVcores"), 5L, metrics.getMinShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("minShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 0, metrics.getMinShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
    }

    @Test
    public void testSetMaxShare() {
        FSQueueMetrics metrics = setupMetrics(TestFSQueueMetrics.RESOURCE_NAME);
        Resource res = Resource.newInstance(2048L, 4, ImmutableMap.of(TestFSQueueMetrics.RESOURCE_NAME, 20L));
        metrics.setMaxShare(res);
        Assert.assertEquals(getErrorMessage("maxShareMB"), 2048L, metrics.getMaxShareMB());
        Assert.assertEquals(getErrorMessage("maxShareVcores"), 4L, metrics.getMaxShareVirtualCores());
        Assert.assertEquals(getErrorMessage("maxShareMB"), 2048L, metrics.getMaxShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("maxShareVcores"), 4L, metrics.getMaxShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("maxShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 20L, metrics.getMaxShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
        res = Resource.newInstance(2049L, 5);
        metrics.setMaxShare(res);
        Assert.assertEquals(getErrorMessage("maxShareMB"), 2049L, metrics.getMaxShareMB());
        Assert.assertEquals(getErrorMessage("maxShareVcores"), 5L, metrics.getMaxShareVirtualCores());
        Assert.assertEquals(getErrorMessage("maxShareMB"), 2049L, metrics.getMaxShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("maxShareVcores"), 5L, metrics.getMaxShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("maxShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 0, metrics.getMaxShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
    }

    @Test
    public void testSetMaxAMShare() {
        FSQueueMetrics metrics = setupMetrics(TestFSQueueMetrics.RESOURCE_NAME);
        Resource res = Resource.newInstance(2048L, 4, ImmutableMap.of(TestFSQueueMetrics.RESOURCE_NAME, 20L));
        metrics.setMaxAMShare(res);
        Assert.assertEquals(getErrorMessage("maxAMShareMB"), 2048L, metrics.getMaxAMShareMB());
        Assert.assertEquals(getErrorMessage("maxAMShareVcores"), 4L, metrics.getMaxAMShareVCores());
        Assert.assertEquals(getErrorMessage("maxAMShareMB"), 2048L, metrics.getMaxAMShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("maxAMShareVcores"), 4L, metrics.getMaxAMShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("maxAMShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 20L, metrics.getMaxAMShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
        res = Resource.newInstance(2049L, 5);
        metrics.setMaxAMShare(res);
        Assert.assertEquals(getErrorMessage("maxAMShareMB"), 2049L, metrics.getMaxAMShareMB());
        Assert.assertEquals(getErrorMessage("maxAMShareVcores"), 5L, metrics.getMaxAMShareVCores());
        Assert.assertEquals(getErrorMessage("maxAMShareMB"), 2049L, metrics.getMaxAMShare().getMemorySize());
        Assert.assertEquals(getErrorMessage("maxAMShareVcores"), 5L, metrics.getMaxAMShare().getVirtualCores());
        Assert.assertEquals(getErrorMessage(("maxAMShare for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 0, metrics.getMaxAMShare().getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
    }

    @Test
    public void testSetAMResourceUsage() {
        FSQueueMetrics metrics = setupMetrics(TestFSQueueMetrics.RESOURCE_NAME);
        Resource res = Resource.newInstance(2048L, 4, ImmutableMap.of(TestFSQueueMetrics.RESOURCE_NAME, 20L));
        metrics.setAMResourceUsage(res);
        Assert.assertEquals(getErrorMessage("AMResourceUsageMB"), 2048L, metrics.getAMResourceUsageMB());
        Assert.assertEquals(getErrorMessage("AMResourceUsageVcores"), 4L, metrics.getAMResourceUsageVCores());
        Resource amResourceUsage = metrics.getAMResourceUsage();
        Assert.assertEquals(getErrorMessage("AMResourceUsageMB"), 2048L, amResourceUsage.getMemorySize());
        Assert.assertEquals(getErrorMessage("AMResourceUsageVcores"), 4L, amResourceUsage.getVirtualCores());
        Assert.assertEquals(getErrorMessage(("AMResourceUsage for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 20L, amResourceUsage.getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
        res = Resource.newInstance(2049L, 5);
        metrics.setAMResourceUsage(res);
        Assert.assertEquals(getErrorMessage("AMResourceUsageMB"), 2049L, metrics.getAMResourceUsageMB());
        Assert.assertEquals(getErrorMessage("AMResourceUsageVcores"), 5L, metrics.getAMResourceUsageVCores());
        amResourceUsage = metrics.getAMResourceUsage();
        Assert.assertEquals(getErrorMessage("AMResourceUsageMB"), 2049L, amResourceUsage.getMemorySize());
        Assert.assertEquals(getErrorMessage("AMResourceUsageVcores"), 5L, amResourceUsage.getVirtualCores());
        Assert.assertEquals(getErrorMessage(("AMResourceUsage for resource: " + (TestFSQueueMetrics.RESOURCE_NAME))), 0, amResourceUsage.getResourceValue(TestFSQueueMetrics.RESOURCE_NAME));
    }

    @Test
    public void testSetMaxApps() {
        FSQueueMetrics metrics = setupMetrics(TestFSQueueMetrics.RESOURCE_NAME);
        metrics.setMaxApps(25);
        Assert.assertEquals(getErrorMessage("maxApps"), 25L, metrics.getMaxApps());
    }
}

