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


import YarnConfiguration.RM_SCHEDULER;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.junit.Assert;
import org.junit.Test;

import static CapacitySchedulerConfiguration.ROOT;


public class TestAbsoluteResourceConfiguration {
    private static final int GB = 1024;

    private static final String QUEUEA = "queueA";

    private static final String QUEUEB = "queueB";

    private static final String QUEUEC = "queueC";

    private static final String QUEUEA1 = "queueA1";

    private static final String QUEUEA2 = "queueA2";

    private static final String QUEUEB1 = "queueB1";

    private static final String QUEUEA_FULL = ((ROOT) + ".") + (TestAbsoluteResourceConfiguration.QUEUEA);

    private static final String QUEUEB_FULL = ((ROOT) + ".") + (TestAbsoluteResourceConfiguration.QUEUEB);

    private static final String QUEUEC_FULL = ((ROOT) + ".") + (TestAbsoluteResourceConfiguration.QUEUEC);

    private static final String QUEUEA1_FULL = ((TestAbsoluteResourceConfiguration.QUEUEA_FULL) + ".") + (TestAbsoluteResourceConfiguration.QUEUEA1);

    private static final String QUEUEA2_FULL = ((TestAbsoluteResourceConfiguration.QUEUEA_FULL) + ".") + (TestAbsoluteResourceConfiguration.QUEUEA2);

    private static final String QUEUEB1_FULL = ((TestAbsoluteResourceConfiguration.QUEUEB_FULL) + ".") + (TestAbsoluteResourceConfiguration.QUEUEB1);

    private static final Resource QUEUE_A_MINRES = Resource.newInstance((100 * (TestAbsoluteResourceConfiguration.GB)), 10);

    private static final Resource QUEUE_A_MAXRES = Resource.newInstance((200 * (TestAbsoluteResourceConfiguration.GB)), 30);

    private static final Resource QUEUE_A1_MINRES = Resource.newInstance((50 * (TestAbsoluteResourceConfiguration.GB)), 5);

    private static final Resource QUEUE_A2_MINRES = Resource.newInstance((50 * (TestAbsoluteResourceConfiguration.GB)), 5);

    private static final Resource QUEUE_B_MINRES = Resource.newInstance((50 * (TestAbsoluteResourceConfiguration.GB)), 10);

    private static final Resource QUEUE_B1_MINRES = Resource.newInstance((40 * (TestAbsoluteResourceConfiguration.GB)), 10);

    private static final Resource QUEUE_B_MAXRES = Resource.newInstance((150 * (TestAbsoluteResourceConfiguration.GB)), 30);

    private static final Resource QUEUE_C_MINRES = Resource.newInstance((50 * (TestAbsoluteResourceConfiguration.GB)), 10);

    private static final Resource QUEUE_C_MAXRES = Resource.newInstance((150 * (TestAbsoluteResourceConfiguration.GB)), 20);

    private static final Resource QUEUEA_REDUCED = Resource.newInstance(64000, 6);

    private static final Resource QUEUEB_REDUCED = Resource.newInstance(32000, 6);

    private static final Resource QUEUEC_REDUCED = Resource.newInstance(32000, 6);

    private static final Resource QUEUEMAX_REDUCED = Resource.newInstance(128000, 20);

    private static Set<String> resourceTypes = new HashSet<>(Arrays.asList("memory", "vcores"));

    @Test
    public void testSimpleMinMaxResourceConfigurartionPerQueue() {
        CapacitySchedulerConfiguration csConf = setupSimpleQueueConfiguration(true);
        setupMinMaxResourceConfiguration(csConf);
        Assert.assertEquals("Min resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, csConf.getMinimumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEA_FULL, TestAbsoluteResourceConfiguration.resourceTypes));
        Assert.assertEquals("Max resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, csConf.getMaximumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEA_FULL, TestAbsoluteResourceConfiguration.resourceTypes));
        Assert.assertEquals("Min resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, csConf.getMinimumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEB_FULL, TestAbsoluteResourceConfiguration.resourceTypes));
        Assert.assertEquals("Max resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, csConf.getMaximumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEB_FULL, TestAbsoluteResourceConfiguration.resourceTypes));
        Assert.assertEquals("Min resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, csConf.getMinimumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEC_FULL, TestAbsoluteResourceConfiguration.resourceTypes));
        Assert.assertEquals("Max resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, csConf.getMaximumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEC_FULL, TestAbsoluteResourceConfiguration.resourceTypes));
    }

    @Test
    public void testEffectiveMinMaxResourceConfigurartionPerQueue() throws Exception {
        // create conf with basic queue configuration.
        CapacitySchedulerConfiguration csConf = setupSimpleQueueConfiguration(false);
        setupMinMaxResourceConfiguration(csConf);
        csConf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        @SuppressWarnings("resource")
        MockRM rm = new MockRM(csConf);
        start();
        // Add few nodes
        rm.registerNode("127.0.0.1:1234", (250 * (TestAbsoluteResourceConfiguration.GB)), 40);
        // Get queue object to verify min/max resource configuration.
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        LeafQueue qA = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEA)));
        Assert.assertNotNull(qA);
        Assert.assertEquals("Min resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, qA.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, qA.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qB = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEB)));
        Assert.assertNotNull(qB);
        Assert.assertEquals("Min resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, qB.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, qB.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qC = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEC)));
        Assert.assertNotNull(qC);
        Assert.assertEquals("Min resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getEffectiveMaxResource());
        stop();
    }

    @Test
    public void testSimpleValidateAbsoluteResourceConfig() throws Exception {
        /**
         * Queue structure is as follows.
         *    root
         *   / | \
         *   a b c
         *   / \ |
         *  a1 a2 b1
         *
         * Test below cases 1) Configure percentage based capacity and absolute
         * resource together. 2) As per above tree structure, ensure all values
         * could be retrieved. 3) Validate whether min resource cannot be more than
         * max resources. 4) Validate whether max resource of queue cannot be more
         * than its parent max resource.
         */
        // create conf with basic queue configuration.
        CapacitySchedulerConfiguration csConf = setupSimpleQueueConfiguration(false);
        setupMinMaxResourceConfiguration(csConf);
        csConf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        @SuppressWarnings("resource")
        MockRM rm = new MockRM(csConf);
        start();
        // Add few nodes
        rm.registerNode("127.0.0.1:1234", (250 * (TestAbsoluteResourceConfiguration.GB)), 40);
        // Get queue object to verify min/max resource configuration.
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        // 1. Create a new config with min/max.
        CapacitySchedulerConfiguration csConf1 = setupSimpleQueueConfiguration(true);
        setupMinMaxResourceConfiguration(csConf1);
        try {
            cs.reinitialize(csConf1, getRMContext());
        } catch (IOException e) {
            Assert.fail();
        }
        stop();
        // 2. Create a new config with min/max alone with a complex queue config.
        // Check all values could be fetched correctly.
        CapacitySchedulerConfiguration csConf2 = setupComplexQueueConfiguration(false);
        setupComplexMinMaxResourceConfig(csConf2);
        rm = new MockRM(csConf2);
        start();
        rm.registerNode("127.0.0.1:1234", (250 * (TestAbsoluteResourceConfiguration.GB)), 40);
        cs = ((CapacityScheduler) (getResourceScheduler()));
        LeafQueue qA1 = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEA1)));
        Assert.assertEquals("Effective Min resource for QUEUEA1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A1_MINRES, qA1.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA1.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qA2 = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEA2)));
        Assert.assertEquals("Effective Min resource for QUEUEA2 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A2_MINRES, qA2.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA2 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA2.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qB1 = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEB1)));
        Assert.assertNotNull(qB1);
        Assert.assertEquals("Min resource configured for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B1_MINRES, qB1.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB1.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B1_MINRES, qB1.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB1.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qC = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEC)));
        Assert.assertNotNull(qC);
        Assert.assertEquals("Min resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getEffectiveMaxResource());
        // 3. Create a new config and make sure one queue's min resource is more
        // than its max resource configured.
        CapacitySchedulerConfiguration csConf3 = setupComplexQueueConfiguration(false);
        setupComplexMinMaxResourceConfig(csConf3);
        csConf3.setMinimumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEB1_FULL, TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES);
        csConf3.setMaximumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEB1_FULL, TestAbsoluteResourceConfiguration.QUEUE_B1_MINRES);
        try {
            cs.reinitialize(csConf3, getRMContext());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue((e instanceof IOException));
            Assert.assertEquals(("Failed to re-init queues : Min resource configuration " + ("<memory:153600, vCores:30> is greater than its " + "max value:<memory:40960, vCores:10> in queue:queueB1")), e.getMessage());
        }
        // 4. Create a new config and make sure one queue's max resource is more
        // than its preant's max resource configured.
        CapacitySchedulerConfiguration csConf4 = setupComplexQueueConfiguration(false);
        setupComplexMinMaxResourceConfig(csConf4);
        csConf4.setMaximumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEB1_FULL, TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES);
        try {
            cs.reinitialize(csConf4, getRMContext());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue((e instanceof IOException));
            Assert.assertEquals(("Failed to re-init queues : Max resource configuration " + ("<memory:204800, vCores:30> is greater than parents max value:" + "<memory:153600, vCores:30> in queue:queueB1")), e.getMessage());
        }
        stop();
    }

    @Test
    public void testComplexValidateAbsoluteResourceConfig() throws Exception {
        /**
         * Queue structure is as follows.
         *   root
         *  / | \
         *  a b c
         * / \ |
         * a1 a2 b1
         *
         * Test below cases: 1) Parent and its child queues must use either
         * percentage based or absolute resource configuration. 2) Parent's min
         * resource must be more than sum of child's min resource.
         */
        // create conf with basic queue configuration.
        CapacitySchedulerConfiguration csConf = setupComplexQueueConfiguration(false);
        setupComplexMinMaxResourceConfig(csConf);
        csConf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        @SuppressWarnings("resource")
        MockRM rm = new MockRM(csConf);
        start();
        // Add few nodes
        rm.registerNode("127.0.0.1:1234", (250 * (TestAbsoluteResourceConfiguration.GB)), 40);
        // 1. Explicitly set percentage based config for parent queues. This will
        // make Queue A,B and C with percentage based and A1,A2 or B1 with absolute
        // resource.
        csConf.setCapacity(TestAbsoluteResourceConfiguration.QUEUEA_FULL, 50.0F);
        csConf.setCapacity(TestAbsoluteResourceConfiguration.QUEUEB_FULL, 25.0F);
        csConf.setCapacity(TestAbsoluteResourceConfiguration.QUEUEC_FULL, 25.0F);
        // Get queue object to verify min/max resource configuration.
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        try {
            cs.reinitialize(csConf, getRMContext());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue((e instanceof IOException));
            Assert.assertEquals(("Failed to re-init queues : Parent queue 'queueA' " + ("and child queue 'queueA1' should use either percentage based" + " capacity configuration or absolute resource together.")), e.getMessage());
        }
        // 2. Create a new config and make sure one queue's min resource is more
        // than its max resource configured.
        CapacitySchedulerConfiguration csConf1 = setupComplexQueueConfiguration(false);
        setupComplexMinMaxResourceConfig(csConf1);
        // Configure QueueA with lesser resource than its children.
        csConf1.setMinimumResourceRequirement("", TestAbsoluteResourceConfiguration.QUEUEA_FULL, TestAbsoluteResourceConfiguration.QUEUE_A1_MINRES);
        try {
            cs.reinitialize(csConf1, getRMContext());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue((e instanceof IOException));
            Assert.assertEquals(("Failed to re-init queues : Parent Queues capacity: " + ("<memory:51200, vCores:5> is less than to its children:" + "<memory:102400, vCores:10> for queue:queueA")), e.getMessage());
        }
    }

    @Test
    public void testEffectiveResourceAfterReducingClusterResource() throws Exception {
        // create conf with basic queue configuration.
        CapacitySchedulerConfiguration csConf = setupSimpleQueueConfiguration(false);
        setupMinMaxResourceConfiguration(csConf);
        csConf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        @SuppressWarnings("resource")
        MockRM rm = new MockRM(csConf);
        start();
        // Add few nodes
        MockNM nm1 = rm.registerNode("127.0.0.1:1234", (125 * (TestAbsoluteResourceConfiguration.GB)), 20);
        rm.registerNode("127.0.0.2:1234", (125 * (TestAbsoluteResourceConfiguration.GB)), 20);
        // Get queue object to verify min/max resource configuration.
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        LeafQueue qA = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEA)));
        Assert.assertNotNull(qA);
        Assert.assertEquals("Min resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, qA.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, qA.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qB = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEB)));
        Assert.assertNotNull(qB);
        Assert.assertEquals("Min resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, qB.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, qB.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qC = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEC)));
        Assert.assertNotNull(qC);
        Assert.assertEquals("Min resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getEffectiveMaxResource());
        // unregister one NM.
        rm.unRegisterNode(nm1);
        // After loosing one NM, effective min res of queueA will become just
        // above half. Hence A's min will be 60Gi and 6 cores and max will be
        // 128GB and 20 cores.
        Assert.assertEquals("Effective Min resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUEA_REDUCED, qA.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUEMAX_REDUCED, qA.queueResourceQuotas.getEffectiveMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUEB_REDUCED, qB.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUEMAX_REDUCED, qB.queueResourceQuotas.getEffectiveMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUEC_REDUCED, qC.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUEMAX_REDUCED, qC.queueResourceQuotas.getEffectiveMaxResource());
        stop();
    }

    @Test
    public void testEffectiveResourceAfterIncreasingClusterResource() throws Exception {
        // create conf with basic queue configuration.
        CapacitySchedulerConfiguration csConf = setupComplexQueueConfiguration(false);
        setupComplexMinMaxResourceConfig(csConf);
        csConf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        @SuppressWarnings("resource")
        MockRM rm = new MockRM(csConf);
        start();
        // Add few nodes
        rm.registerNode("127.0.0.1:1234", (125 * (TestAbsoluteResourceConfiguration.GB)), 20);
        rm.registerNode("127.0.0.2:1234", (125 * (TestAbsoluteResourceConfiguration.GB)), 20);
        // Get queue object to verify min/max resource configuration.
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        ParentQueue qA = ((ParentQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEA)));
        Assert.assertNotNull(qA);
        Assert.assertEquals("Min resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, qA.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, qA.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA.queueResourceQuotas.getEffectiveMaxResource());
        ParentQueue qB = ((ParentQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEB)));
        Assert.assertNotNull(qB);
        Assert.assertEquals("Min resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, qB.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, qB.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qC = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEC)));
        Assert.assertNotNull(qC);
        Assert.assertEquals("Min resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qA1 = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEA1)));
        Assert.assertEquals("Effective Min resource for QUEUEA1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A1_MINRES, qA1.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA1.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qA2 = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEA2)));
        Assert.assertEquals("Effective Min resource for QUEUEA2 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A2_MINRES, qA2.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA2 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA2.queueResourceQuotas.getEffectiveMaxResource());
        LeafQueue qB1 = ((LeafQueue) (cs.getQueue(TestAbsoluteResourceConfiguration.QUEUEB1)));
        Assert.assertEquals("Min resource configured for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B1_MINRES, qB1.queueResourceQuotas.getConfiguredMinResource());
        Assert.assertEquals("Max resource configured for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB1.queueResourceQuotas.getConfiguredMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B1_MINRES, qB1.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB1.queueResourceQuotas.getEffectiveMaxResource());
        // add new NM.
        rm.registerNode("127.0.0.3:1234", (125 * (TestAbsoluteResourceConfiguration.GB)), 20);
        // There will be no change in effective resource when nodes are added.
        // Since configured capacity was based on initial node capacity, a
        // re configurations is needed to use this added capacity.
        Assert.assertEquals("Effective Min resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MINRES, qA.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA.queueResourceQuotas.getEffectiveMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MINRES, qB.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB.queueResourceQuotas.getEffectiveMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MINRES, qC.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEC is not correct", TestAbsoluteResourceConfiguration.QUEUE_C_MAXRES, qC.queueResourceQuotas.getEffectiveMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B1_MINRES, qB1.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEB1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_B_MAXRES, qB1.queueResourceQuotas.getEffectiveMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEA1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A1_MINRES, qA1.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA1 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA1.queueResourceQuotas.getEffectiveMaxResource());
        Assert.assertEquals("Effective Min resource for QUEUEA2 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A2_MINRES, qA2.queueResourceQuotas.getEffectiveMinResource());
        Assert.assertEquals("Effective Max resource for QUEUEA2 is not correct", TestAbsoluteResourceConfiguration.QUEUE_A_MAXRES, qA2.queueResourceQuotas.getEffectiveMaxResource());
        stop();
    }
}

