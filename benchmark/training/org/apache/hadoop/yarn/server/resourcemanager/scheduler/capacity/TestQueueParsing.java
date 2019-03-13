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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import CapacitySchedulerConfiguration.ROOT;
import RMNodeLabelsManager.ANY;
import YarnConfiguration.RM_SCHEDULER;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.service.ServiceOperations;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContextImpl;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.NullRMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.policy.PriorityUtilizationQueueOrderingPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CapacitySchedulerConfiguration.ROOT;


public class TestQueueParsing {
    private static final Logger LOG = LoggerFactory.getLogger(TestQueueParsing.class);

    private static final double DELTA = 1.0E-6;

    private RMNodeLabelsManager nodeLabelManager;

    @Test
    public void testQueueParsing() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(csConf);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        capacityScheduler.setConf(conf);
        capacityScheduler.setRMContext(TestUtils.getMockRMContext());
        capacityScheduler.init(conf);
        capacityScheduler.start();
        capacityScheduler.reinitialize(conf, TestUtils.getMockRMContext());
        CSQueue a = capacityScheduler.getQueue("a");
        Assert.assertEquals(0.1, a.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.15, a.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue b1 = capacityScheduler.getQueue("b1");
        Assert.assertEquals((0.2 * 0.5), b1.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals("Parent B has no MAX_CAP", 0.85, b1.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue c12 = capacityScheduler.getQueue("c12");
        Assert.assertEquals(((0.7 * 0.5) * 0.45), c12.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(((0.7 * 0.55) * 0.7), c12.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRootQueueParsing() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        // non-100 percent value will throw IllegalArgumentException
        conf.setCapacity(ROOT, 90);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        capacityScheduler.setConf(new YarnConfiguration());
        capacityScheduler.init(conf);
        capacityScheduler.start();
        capacityScheduler.reinitialize(conf, null);
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    @Test
    public void testQueueParsingReinitializeWithLabels() throws IOException {
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("red", "blue"));
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithoutLabels(csConf);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(conf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(conf);
        capacityScheduler.start();
        csConf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithLabels(csConf);
        conf = new YarnConfiguration(csConf);
        capacityScheduler.reinitialize(conf, rmContext);
        checkQueueLabels(capacityScheduler);
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    @Test
    public void testQueueParsingWithLabels() throws IOException {
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("red", "blue"));
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabels(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        checkQueueLabels(capacityScheduler);
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    @Test
    public void testQueueParsingWithLeafQueueDisableElasticity() throws IOException {
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("red", "blue"));
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabelsAndReleaseCheck(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        checkQueueLabelsWithLeafQueueDisableElasticity(capacityScheduler);
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    @Test
    public void testQueueParsingWithLabelsInherit() throws IOException {
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("red", "blue"));
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabelsInherit(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        checkQueueLabelsInheritConfig(capacityScheduler);
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    @Test
    public void testQueueParsingWhenLabelsNotExistedInNodeLabelManager() throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabels(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        RMNodeLabelsManager nodeLabelsManager = new NullRMNodeLabelsManager();
        nodeLabelsManager.init(conf);
        nodeLabelsManager.start();
        rmContext.setNodeLabelManager(nodeLabelsManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        ServiceOperations.stopQuietly(capacityScheduler);
        ServiceOperations.stopQuietly(nodeLabelsManager);
    }

    @Test
    public void testQueueParsingWhenLabelsInheritedNotExistedInNodeLabelManager() throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabelsInherit(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        RMNodeLabelsManager nodeLabelsManager = new NullRMNodeLabelsManager();
        nodeLabelsManager.init(conf);
        nodeLabelsManager.start();
        rmContext.setNodeLabelManager(nodeLabelsManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        ServiceOperations.stopQuietly(capacityScheduler);
        ServiceOperations.stopQuietly(nodeLabelsManager);
    }

    @Test
    public void testSingleLevelQueueParsingWhenLabelsNotExistedInNodeLabelManager() throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithSingleLevel(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        RMNodeLabelsManager nodeLabelsManager = new NullRMNodeLabelsManager();
        nodeLabelsManager.init(conf);
        nodeLabelsManager.start();
        rmContext.setNodeLabelManager(nodeLabelsManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        ServiceOperations.stopQuietly(capacityScheduler);
        ServiceOperations.stopQuietly(nodeLabelsManager);
    }

    @Test
    public void testQueueParsingWhenLabelsNotExist() throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabels(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        RMNodeLabelsManager nodeLabelsManager = new NullRMNodeLabelsManager();
        nodeLabelsManager.init(conf);
        nodeLabelsManager.start();
        rmContext.setNodeLabelManager(nodeLabelsManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        ServiceOperations.stopQuietly(capacityScheduler);
        ServiceOperations.stopQuietly(nodeLabelsManager);
    }

    @Test
    public void testQueueParsingWithUnusedLabels() throws IOException {
        final ImmutableSet<String> labels = ImmutableSet.of("red", "blue");
        // Initialize a cluster with labels, but doesn't use them, reinitialize
        // shouldn't fail
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(labels);
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(csConf);
        csConf.setAccessibleNodeLabels(ROOT, labels);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        capacityScheduler.setConf(conf);
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(conf);
        capacityScheduler.start();
        capacityScheduler.reinitialize(conf, rmContext);
        // check root queue's capacity by label -- they should be all zero
        CSQueue root = capacityScheduler.getQueue(ROOT);
        Assert.assertEquals(0, root.getQueueCapacities().getCapacity("red"), TestQueueParsing.DELTA);
        Assert.assertEquals(0, root.getQueueCapacities().getCapacity("blue"), TestQueueParsing.DELTA);
        CSQueue a = capacityScheduler.getQueue("a");
        Assert.assertEquals(0.1, a.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.15, a.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue b1 = capacityScheduler.getQueue("b1");
        Assert.assertEquals((0.2 * 0.5), b1.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals("Parent B has no MAX_CAP", 0.85, b1.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue c12 = capacityScheduler.getQueue("c12");
        Assert.assertEquals(((0.7 * 0.5) * 0.45), c12.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(((0.7 * 0.55) * 0.7), c12.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        capacityScheduler.stop();
    }

    @Test
    public void testQueueParsingShouldTrimSpaces() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithSpacesShouldBeTrimmed(csConf);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        capacityScheduler.setConf(conf);
        capacityScheduler.setRMContext(TestUtils.getMockRMContext());
        capacityScheduler.init(conf);
        capacityScheduler.start();
        capacityScheduler.reinitialize(conf, TestUtils.getMockRMContext());
        CSQueue a = capacityScheduler.getQueue("a");
        Assert.assertNotNull(a);
        Assert.assertEquals(0.1, a.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.15, a.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue c = capacityScheduler.getQueue("c");
        Assert.assertNotNull(c);
        Assert.assertEquals(0.7, c.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.7, c.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
    }

    @Test
    public void testNestedQueueParsingShouldTrimSpaces() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupNestedQueueConfigurationWithSpacesShouldBeTrimmed(csConf);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        capacityScheduler.setConf(conf);
        capacityScheduler.setRMContext(TestUtils.getMockRMContext());
        capacityScheduler.init(conf);
        capacityScheduler.start();
        capacityScheduler.reinitialize(conf, TestUtils.getMockRMContext());
        CSQueue a = capacityScheduler.getQueue("a");
        Assert.assertNotNull(a);
        Assert.assertEquals(0.1, a.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.15, a.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue c = capacityScheduler.getQueue("c");
        Assert.assertNotNull(c);
        Assert.assertEquals(0.7, c.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.7, c.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue a1 = capacityScheduler.getQueue("a1");
        Assert.assertNotNull(a1);
        Assert.assertEquals((0.1 * 0.6), a1.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.15, a1.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
        CSQueue a2 = capacityScheduler.getQueue("a2");
        Assert.assertNotNull(a2);
        Assert.assertEquals((0.1 * 0.4), a2.getAbsoluteCapacity(), TestQueueParsing.DELTA);
        Assert.assertEquals(0.15, a2.getAbsoluteMaximumCapacity(), TestQueueParsing.DELTA);
    }

    /**
     * Test init a queue configuration, children's capacity for a given label
     * doesn't equals to 100%. This expect IllegalArgumentException thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testQueueParsingFailWhenSumOfChildrenNonLabeledCapacityNot100Percent() throws IOException {
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("red", "blue"));
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfiguration(csConf);
        csConf.setCapacity(((ROOT) + ".c.c2"), 5);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    /**
     * Test init a queue configuration, children's capacity for a given label
     * doesn't equals to 100%. This expect IllegalArgumentException thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testQueueParsingFailWhenSumOfChildrenLabeledCapacityNot100Percent() throws IOException {
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("red", "blue"));
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabels(csConf);
        csConf.setCapacityByLabel(((ROOT) + ".b.b3"), "red", 24);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    /**
     * Test init a queue configuration, children's capacity for a given label
     * doesn't equals to 100%. This expect IllegalArgumentException thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testQueueParsingWithSumOfChildLabelCapacityNot100PercentWithWildCard() throws IOException {
        nodeLabelManager.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("red", "blue"));
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationWithLabels(csConf);
        csConf.setCapacityByLabel(((ROOT) + ".b.b3"), "red", 24);
        csConf.setAccessibleNodeLabels(ROOT, ImmutableSet.of(ANY));
        csConf.setAccessibleNodeLabels(((ROOT) + ".b"), ImmutableSet.of(ANY));
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        ServiceOperations.stopQuietly(capacityScheduler);
    }

    @Test(expected = IOException.class)
    public void testQueueParsingWithMoveQueue() throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        csConf.setQueues("root", new String[]{ "a" });
        csConf.setQueues("root.a", new String[]{ "x", "y" });
        csConf.setCapacity("root.a", 100);
        csConf.setCapacity("root.a.x", 50);
        csConf.setCapacity("root.a.y", 50);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(csConf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(csConf);
        capacityScheduler.start();
        csConf.setQueues("root", new String[]{ "a", "x" });
        csConf.setQueues("root.a", new String[]{ "y" });
        csConf.setCapacity("root.x", 50);
        csConf.setCapacity("root.a", 50);
        csConf.setCapacity("root.a.y", 100);
        capacityScheduler.reinitialize(csConf, rmContext);
    }

    @Test(timeout = 60000, expected = IllegalArgumentException.class)
    public void testRMStartWrongNodeCapacity() throws Exception {
        YarnConfiguration config = new YarnConfiguration();
        nodeLabelManager = new NullRMNodeLabelsManager();
        nodeLabelManager.init(config);
        config.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration(config);
        // Define top-level queues
        conf.setQueues(ROOT, new String[]{ "a" });
        conf.setCapacityByLabel(ROOT, "x", 100);
        conf.setCapacityByLabel(ROOT, "y", 100);
        conf.setCapacityByLabel(ROOT, "z", 100);
        final String A = (ROOT) + ".a";
        conf.setCapacity(A, 100);
        conf.setAccessibleNodeLabels(A, ImmutableSet.of("x", "y", "z"));
        conf.setCapacityByLabel(A, "x", 100);
        conf.setCapacityByLabel(A, "y", 100);
        conf.setCapacityByLabel(A, "z", 70);
        MockRM rm = null;
        try {
            rm = new MockRM(conf) {
                @Override
                public RMNodeLabelsManager createNodeLabelManager() {
                    return nodeLabelManager;
                }
            };
        } finally {
            IOUtils.closeStream(rm);
        }
    }

    @Test
    public void testQueueOrderingPolicyUpdatedAfterReinitialize() throws IOException {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithoutLabels(csConf);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        CapacityScheduler capacityScheduler = new CapacityScheduler();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null);
        rmContext.setNodeLabelManager(nodeLabelManager);
        capacityScheduler.setConf(conf);
        capacityScheduler.setRMContext(rmContext);
        capacityScheduler.init(conf);
        capacityScheduler.start();
        // Add a new b4 queue
        csConf.setQueues(((ROOT) + ".b"), new String[]{ "b1", "b2", "b3", "b4" });
        csConf.setCapacity(((ROOT) + ".b.b4"), 0.0F);
        ParentQueue bQ = ((ParentQueue) (capacityScheduler.getQueue("b")));
        checkEqualsToQueueSet(bQ.getChildQueues(), new String[]{ "b1", "b2", "b3" });
        capacityScheduler.reinitialize(new YarnConfiguration(csConf), rmContext);
        // Check child queue of b
        checkEqualsToQueueSet(bQ.getChildQueues(), new String[]{ "b1", "b2", "b3", "b4" });
        PriorityUtilizationQueueOrderingPolicy queueOrderingPolicy = ((PriorityUtilizationQueueOrderingPolicy) (bQ.getQueueOrderingPolicy()));
        checkEqualsToQueueSet(queueOrderingPolicy.getQueues(), new String[]{ "b1", "b2", "b3", "b4" });
        ServiceOperations.stopQuietly(capacityScheduler);
    }
}

