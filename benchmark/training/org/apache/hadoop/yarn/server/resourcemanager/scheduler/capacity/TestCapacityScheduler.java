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


import AllocationState.APP_SKIPPED;
import AllocationState.LOCALITY_SKIPPED;
import AllocationState.PRIORITY_SKIPPED;
import AllocationState.QUEUE_SKIPPED;
import ApplicationAccessType.VIEW_APP;
import CapacitySchedulerConfiguration.ASSIGN_MULTIPLE_ENABLED;
import CapacitySchedulerConfiguration.DOT;
import CapacitySchedulerConfiguration.ENABLE_USER_METRICS;
import CapacitySchedulerConfiguration.MAXIMUM_CAPACITY_VALUE;
import CapacitySchedulerConfiguration.MAX_ASSIGN_PER_HEARTBEAT;
import CapacitySchedulerConfiguration.PREFIX;
import CapacitySchedulerConfiguration.QUEUE_MAPPING;
import CapacitySchedulerConfiguration.RESOURCE_CALCULATOR_CLASS;
import CapacitySchedulerConfiguration.ROOT;
import CapacitySchedulerConfiguration.SCHEDULE_ASYNCHRONOUSLY_ENABLE;
import CapacitySchedulerConfiguration.STATE;
import CommonConfigurationKeys.HADOOP_SECURITY_GROUP_MAPPING;
import CommonConfigurationKeys.HADOOP_USER_GROUP_STATIC_OVERRIDES;
import ContainerState.COMPLETE;
import ContainerUpdateType.INCREASE_RESOURCE;
import ExecutionType.GUARANTEED;
import NetworkTopology.DEFAULT_RACK;
import NodeState.DECOMMISSIONING;
import QueueState.DRAINING;
import QueueState.STOPPED;
import RMAppAttemptState.LAUNCHED;
import RMAppState.KILLED;
import RMContainerState.ACQUIRED;
import RMContainerState.ALLOCATED;
import RMContainerState.RUNNING;
import RMNodeLabelsManager.NO_LABEL;
import ResourceRequest.ANY;
import TestGroupsCaching.FakeunPrivilegedGroupMapping;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB;
import YarnConfiguration.NODE_LABELS_ENABLED;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.RM_SCHEDULER_ENABLE_MONITORS;
import YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_MB;
import YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES;
import YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB;
import YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.ShellBasedUnixGroupsMapping;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.LocalConfigurationProvider;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.UpdateContainerRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;
import org.apache.hadoop.yarn.server.resourcemanager.AdminService;
import org.apache.hadoop.yarn.server.resourcemanager.Application;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNodes;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.NodeManager;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.RMContextImpl;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.Task;
import org.apache.hadoop.yarn.server.resourcemanager.TestAMAuthorization;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.NullRMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeResourceUpdateEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerUpdates;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueResourceQuotas;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplication;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNodeReport;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.TestSchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.ResourceCommitRequest;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy.FairOrderingPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerLeafQueueInfo;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.DominantResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CapacitySchedulerConfiguration.MAXIMUM_ALLOCATION_MB;
import static CapacitySchedulerConfiguration.MAXIMUM_ALLOCATION_VCORES;
import static CapacitySchedulerConfiguration.ROOT;
import static org.apache.hadoop.yarn.server.resourcemanager.TestAMAuthorization.MockRMWithAMS.setupAndReturnAMRMToken;


public class TestCapacityScheduler extends CapacitySchedulerTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestCapacityScheduler.class);

    private static final ContainerUpdates NULL_UPDATE_REQUESTS = new ContainerUpdates();

    private ResourceManager resourceManager = null;

    private RMContext mockContext;

    @Test(timeout = 30000)
    public void testConfValidation() throws Exception {
        CapacityScheduler scheduler = new CapacityScheduler();
        scheduler.setRMContext(resourceManager.getRMContext());
        Configuration conf = new YarnConfiguration();
        conf.setInt(RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 2048);
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, 1024);
        try {
            scheduler.init(conf);
            Assert.fail(("Exception is expected because the min memory allocation is" + " larger than the max memory allocation."));
        } catch (YarnRuntimeException e) {
            // Exception is expected.
            Assert.assertTrue("The thrown exception is not the expected one.", e.getMessage().startsWith("Invalid resource scheduler memory"));
        }
        conf = new YarnConfiguration();
        conf.setInt(RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES, 2);
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, 1);
        try {
            scheduler.reinitialize(conf, mockContext);
            Assert.fail(("Exception is expected because the min vcores allocation is" + " larger than the max vcores allocation."));
        } catch (YarnRuntimeException e) {
            // Exception is expected.
            Assert.assertTrue("The thrown exception is not the expected one.", e.getMessage().startsWith("Invalid resource scheduler vcores"));
        }
    }

    @Test
    public void testCapacityScheduler() throws Exception {
        TestCapacityScheduler.LOG.info("--- START: testCapacityScheduler ---");
        // Register node1
        String host_0 = "host_0";
        NodeManager nm_0 = registerNode(host_0, 1234, 2345, DEFAULT_RACK, Resources.createResource((4 * (GB)), 1));
        // Register node2
        String host_1 = "host_1";
        NodeManager nm_1 = registerNode(host_1, 1234, 2345, DEFAULT_RACK, Resources.createResource((2 * (GB)), 1));
        // ResourceRequest priorities
        Priority priority_0 = Priority.newInstance(0);
        Priority priority_1 = Priority.newInstance(1);
        // Submit an application
        Application application_0 = new Application("user_0", "a1", resourceManager);
        application_0.submit();
        application_0.addNodeManager(host_0, 1234, nm_0);
        application_0.addNodeManager(host_1, 1234, nm_1);
        Resource capability_0_0 = Resources.createResource((1 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_1, capability_0_0);
        Resource capability_0_1 = Resources.createResource((2 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_0, capability_0_1);
        Task task_0_0 = new Task(application_0, priority_1, new String[]{ host_0, host_1 });
        application_0.addTask(task_0_0);
        // Submit another application
        Application application_1 = new Application("user_1", "b2", resourceManager);
        application_1.submit();
        application_1.addNodeManager(host_0, 1234, nm_0);
        application_1.addNodeManager(host_1, 1234, nm_1);
        Resource capability_1_0 = Resources.createResource((3 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_1, capability_1_0);
        Resource capability_1_1 = Resources.createResource((2 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_0, capability_1_1);
        Task task_1_0 = new Task(application_1, priority_1, new String[]{ host_0, host_1 });
        application_1.addTask(task_1_0);
        // Send resource requests to the scheduler
        application_0.schedule();
        application_1.schedule();
        // Send a heartbeat to kick the tires on the Scheduler
        TestCapacityScheduler.LOG.info("Kick!");
        // task_0_0 and task_1_0 allocated, used=4G
        nodeUpdate(nm_0);
        // nothing allocated
        nodeUpdate(nm_1);
        // Get allocations from the scheduler
        application_0.schedule();// task_0_0

        checkApplicationResourceUsage((1 * (GB)), application_0);
        application_1.schedule();// task_1_0

        checkApplicationResourceUsage((3 * (GB)), application_1);
        checkNodeResourceUsage((4 * (GB)), nm_0);// task_0_0 (1G) and task_1_0 (3G)

        checkNodeResourceUsage((0 * (GB)), nm_1);// no tasks, 2G available

        TestCapacityScheduler.LOG.info("Adding new tasks...");
        Task task_1_1 = new Task(application_1, priority_0, new String[]{ ResourceRequest.ANY });
        application_1.addTask(task_1_1);
        application_1.schedule();
        Task task_0_1 = new Task(application_0, priority_0, new String[]{ host_0, host_1 });
        application_0.addTask(task_0_1);
        application_0.schedule();
        // Send a heartbeat to kick the tires on the Scheduler
        TestCapacityScheduler.LOG.info(("Sending hb from " + (nm_0.getHostName())));
        // nothing new, used=4G
        nodeUpdate(nm_0);
        TestCapacityScheduler.LOG.info(("Sending hb from " + (nm_1.getHostName())));
        // task_0_1 is prefer as locality, used=2G
        nodeUpdate(nm_1);
        // Get allocations from the scheduler
        TestCapacityScheduler.LOG.info("Trying to allocate...");
        application_0.schedule();
        checkApplicationResourceUsage((1 * (GB)), application_0);
        application_1.schedule();
        checkApplicationResourceUsage((5 * (GB)), application_1);
        nodeUpdate(nm_0);
        nodeUpdate(nm_1);
        checkNodeResourceUsage((4 * (GB)), nm_0);
        checkNodeResourceUsage((2 * (GB)), nm_1);
        TestCapacityScheduler.LOG.info("--- END: testCapacityScheduler ---");
    }

    @Test
    public void testNotAssignMultiple() throws Exception {
        TestCapacityScheduler.LOG.info("--- START: testNotAssignMultiple ---");
        ResourceManager rm = new ResourceManager() {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                RMNodeLabelsManager mgr = new NullRMNodeLabelsManager();
                mgr.init(getConfig());
                return mgr;
            }
        };
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setBoolean(ASSIGN_MULTIPLE_ENABLED, false);
        setupQueueConfiguration(csConf);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        rm.init(conf);
        rm.getRMContext().getContainerTokenSecretManager().rollMasterKey();
        rm.getRMContext().getNMTokenSecretManager().rollMasterKey();
        start();
        RMContext mC = Mockito.mock(RMContext.class);
        Mockito.when(mC.getConfigurationProvider()).thenReturn(new LocalConfigurationProvider());
        // Register node1
        String host0 = "host_0";
        NodeManager nm0 = registerNode(rm, host0, 1234, 2345, DEFAULT_RACK, Resources.createResource((10 * (GB)), 10));
        // ResourceRequest priorities
        Priority priority0 = Priority.newInstance(0);
        Priority priority1 = Priority.newInstance(1);
        // Submit an application
        Application application0 = new Application("user_0", "a1", rm);
        application0.submit();
        application0.addNodeManager(host0, 1234, nm0);
        Resource capability00 = Resources.createResource((1 * (GB)), 1);
        application0.addResourceRequestSpec(priority0, capability00);
        Resource capability01 = Resources.createResource((2 * (GB)), 1);
        application0.addResourceRequestSpec(priority1, capability01);
        Task task00 = new Task(application0, priority0, new String[]{ host0 });
        Task task01 = new Task(application0, priority1, new String[]{ host0 });
        application0.addTask(task00);
        application0.addTask(task01);
        // Submit another application
        Application application1 = new Application("user_1", "b2", rm);
        application1.submit();
        application1.addNodeManager(host0, 1234, nm0);
        Resource capability10 = Resources.createResource((3 * (GB)), 1);
        application1.addResourceRequestSpec(priority0, capability10);
        Resource capability11 = Resources.createResource((4 * (GB)), 1);
        application1.addResourceRequestSpec(priority1, capability11);
        Task task10 = new Task(application1, priority0, new String[]{ host0 });
        Task task11 = new Task(application1, priority1, new String[]{ host0 });
        application1.addTask(task10);
        application1.addTask(task11);
        // Send resource requests to the scheduler
        application0.schedule();
        application1.schedule();
        // Send a heartbeat to kick the tires on the Scheduler
        TestCapacityScheduler.LOG.info("Kick!");
        // task00, used=1G
        nodeUpdate(rm, nm0);
        // Get allocations from the scheduler
        application0.schedule();
        application1.schedule();
        // 1 Task per heart beat should be scheduled
        checkNodeResourceUsage((3 * (GB)), nm0);// task00 (1G)

        checkApplicationResourceUsage((0 * (GB)), application0);
        checkApplicationResourceUsage((3 * (GB)), application1);
        // Another heartbeat
        nodeUpdate(rm, nm0);
        application0.schedule();
        checkApplicationResourceUsage((1 * (GB)), application0);
        application1.schedule();
        checkApplicationResourceUsage((3 * (GB)), application1);
        checkNodeResourceUsage((4 * (GB)), nm0);
        TestCapacityScheduler.LOG.info("--- END: testNotAssignMultiple ---");
    }

    @Test
    public void testAssignMultiple() throws Exception {
        TestCapacityScheduler.LOG.info("--- START: testAssignMultiple ---");
        ResourceManager rm = new ResourceManager() {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                RMNodeLabelsManager mgr = new NullRMNodeLabelsManager();
                mgr.init(getConfig());
                return mgr;
            }
        };
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setBoolean(ASSIGN_MULTIPLE_ENABLED, true);
        // Each heartbeat will assign 2 containers at most
        csConf.setInt(MAX_ASSIGN_PER_HEARTBEAT, 2);
        setupQueueConfiguration(csConf);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        rm.init(conf);
        rm.getRMContext().getContainerTokenSecretManager().rollMasterKey();
        rm.getRMContext().getNMTokenSecretManager().rollMasterKey();
        start();
        RMContext mC = Mockito.mock(RMContext.class);
        Mockito.when(mC.getConfigurationProvider()).thenReturn(new LocalConfigurationProvider());
        // Register node1
        String host0 = "host_0";
        NodeManager nm0 = registerNode(rm, host0, 1234, 2345, DEFAULT_RACK, Resources.createResource((10 * (GB)), 10));
        // ResourceRequest priorities
        Priority priority0 = Priority.newInstance(0);
        Priority priority1 = Priority.newInstance(1);
        // Submit an application
        Application application0 = new Application("user_0", "a1", rm);
        application0.submit();
        application0.addNodeManager(host0, 1234, nm0);
        Resource capability00 = Resources.createResource((1 * (GB)), 1);
        application0.addResourceRequestSpec(priority0, capability00);
        Resource capability01 = Resources.createResource((2 * (GB)), 1);
        application0.addResourceRequestSpec(priority1, capability01);
        Task task00 = new Task(application0, priority0, new String[]{ host0 });
        Task task01 = new Task(application0, priority1, new String[]{ host0 });
        application0.addTask(task00);
        application0.addTask(task01);
        // Submit another application
        Application application1 = new Application("user_1", "b2", rm);
        application1.submit();
        application1.addNodeManager(host0, 1234, nm0);
        Resource capability10 = Resources.createResource((3 * (GB)), 1);
        application1.addResourceRequestSpec(priority0, capability10);
        Resource capability11 = Resources.createResource((4 * (GB)), 1);
        application1.addResourceRequestSpec(priority1, capability11);
        Task task10 = new Task(application1, priority0, new String[]{ host0 });
        Task task11 = new Task(application1, priority1, new String[]{ host0 });
        application1.addTask(task10);
        application1.addTask(task11);
        // Send resource requests to the scheduler
        application0.schedule();
        application1.schedule();
        // Send a heartbeat to kick the tires on the Scheduler
        TestCapacityScheduler.LOG.info("Kick!");
        // task_0_0, used=1G
        nodeUpdate(rm, nm0);
        // Get allocations from the scheduler
        application0.schedule();
        application1.schedule();
        // 1 Task per heart beat should be scheduled
        checkNodeResourceUsage((4 * (GB)), nm0);// task00 (1G)

        checkApplicationResourceUsage((1 * (GB)), application0);
        checkApplicationResourceUsage((3 * (GB)), application1);
        // Another heartbeat
        nodeUpdate(rm, nm0);
        application0.schedule();
        checkApplicationResourceUsage((3 * (GB)), application0);
        application1.schedule();
        checkApplicationResourceUsage((7 * (GB)), application1);
        checkNodeResourceUsage((10 * (GB)), nm0);
        TestCapacityScheduler.LOG.info("--- END: testAssignMultiple ---");
    }

    @Test
    public void testMaximumCapacitySetup() {
        float delta = 1.0E-7F;
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        Assert.assertEquals(MAXIMUM_CAPACITY_VALUE, conf.getNonLabeledQueueMaximumCapacity(CapacitySchedulerTestBase.A), delta);
        conf.setMaximumCapacity(CapacitySchedulerTestBase.A, 50.0F);
        Assert.assertEquals(50.0F, conf.getNonLabeledQueueMaximumCapacity(CapacitySchedulerTestBase.A), delta);
        conf.setMaximumCapacity(CapacitySchedulerTestBase.A, (-1));
        Assert.assertEquals(MAXIMUM_CAPACITY_VALUE, conf.getNonLabeledQueueMaximumCapacity(CapacitySchedulerTestBase.A), delta);
    }

    @Test
    public void testQueueMaximumAllocations() {
        CapacityScheduler scheduler = new CapacityScheduler();
        scheduler.setConf(new YarnConfiguration());
        scheduler.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        conf.set(((CapacitySchedulerConfiguration.getQueuePrefix(CapacitySchedulerTestBase.A1)) + (MAXIMUM_ALLOCATION_MB)), "1024");
        conf.set(((CapacitySchedulerConfiguration.getQueuePrefix(CapacitySchedulerTestBase.A1)) + (MAXIMUM_ALLOCATION_VCORES)), "1");
        scheduler.init(conf);
        scheduler.start();
        Resource maxAllocationForQueue = scheduler.getMaximumResourceCapability("a1");
        Resource maxAllocation1 = scheduler.getMaximumResourceCapability("");
        Resource maxAllocation2 = scheduler.getMaximumResourceCapability(null);
        Resource maxAllocation3 = scheduler.getMaximumResourceCapability();
        Assert.assertEquals(maxAllocation1, maxAllocation2);
        Assert.assertEquals(maxAllocation1, maxAllocation3);
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, maxAllocation1.getMemorySize());
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, maxAllocation1.getVirtualCores());
        Assert.assertEquals(1024, maxAllocationForQueue.getMemorySize());
        Assert.assertEquals(1, maxAllocationForQueue.getVirtualCores());
    }

    @Test
    public void testRefreshQueues() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null);
        setupQueueConfiguration(conf);
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, rmContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        conf.setCapacity(CapacitySchedulerTestBase.A, 80.0F);
        conf.setCapacity(CapacitySchedulerTestBase.B, 20.0F);
        cs.reinitialize(conf, mockContext);
        checkQueueCapacities(cs, 80.0F, 20.0F);
        cs.stop();
    }

    /**
     * Test that parseQueue throws an exception when two leaf queues have the
     *  same name
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testParseQueue() throws IOException {
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        cs.init(conf);
        cs.start();
        conf.setQueues(((ROOT) + ".a.a1"), new String[]{ "b1" });
        conf.setCapacity(((ROOT) + ".a.a1.b1"), 100.0F);
        conf.setUserLimitFactor(((ROOT) + ".a.a1.b1"), 100.0F);
        cs.reinitialize(conf, new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null));
    }

    @Test
    public void testParseQueueWithAbsoluteResource() {
        String childQueue = "testQueue";
        String labelName = "testLabel";
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        conf.setQueues("root", new String[]{ childQueue });
        conf.setCapacity(("root." + childQueue), "[memory=20480,vcores=200]");
        conf.setAccessibleNodeLabels(("root." + childQueue), Sets.newHashSet(labelName));
        conf.setCapacityByLabel("root", labelName, "[memory=10240,vcores=100]");
        conf.setCapacityByLabel(("root." + childQueue), labelName, "[memory=4096,vcores=10]");
        cs.init(conf);
        cs.start();
        Resource rootQueueLableCapacity = cs.getQueue("root").getQueueResourceQuotas().getConfiguredMinResource(labelName);
        Assert.assertEquals(10240, rootQueueLableCapacity.getMemorySize());
        Assert.assertEquals(100, rootQueueLableCapacity.getVirtualCores());
        QueueResourceQuotas childQueueQuotas = cs.getQueue(childQueue).getQueueResourceQuotas();
        Resource childQueueCapacity = childQueueQuotas.getConfiguredMinResource();
        Assert.assertEquals(20480, childQueueCapacity.getMemorySize());
        Assert.assertEquals(200, childQueueCapacity.getVirtualCores());
        Resource childQueueLabelCapacity = childQueueQuotas.getConfiguredMinResource(labelName);
        Assert.assertEquals(4096, childQueueLabelCapacity.getMemorySize());
        Assert.assertEquals(10, childQueueLabelCapacity.getVirtualCores());
    }

    @Test
    public void testReconnectedNode() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(csConf);
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(csConf);
        cs.start();
        cs.reinitialize(csConf, new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(csConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(csConf), new ClientToAMTokenSecretManagerInRM(), null));
        RMNode n1 = MockNodes.newNodeInfo(0, MockNodes.newResource((4 * (GB))), 1);
        RMNode n2 = MockNodes.newNodeInfo(0, MockNodes.newResource((2 * (GB))), 2);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n1));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n2));
        Assert.assertEquals((6 * (GB)), cs.getClusterResource().getMemorySize());
        // reconnect n1 with downgraded memory
        n1 = MockNodes.newNodeInfo(0, MockNodes.newResource((2 * (GB))), 1);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(n1));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n1));
        Assert.assertEquals((4 * (GB)), cs.getClusterResource().getMemorySize());
        cs.stop();
    }

    @Test
    public void testRefreshQueuesWithNewQueue() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null));
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        // Add a new queue b4
        String B4 = (CapacitySchedulerTestBase.B) + ".b4";
        float B4_CAPACITY = 10;
        CapacitySchedulerTestBase.B3_CAPACITY -= B4_CAPACITY;
        try {
            conf.setCapacity(CapacitySchedulerTestBase.A, 80.0F);
            conf.setCapacity(CapacitySchedulerTestBase.B, 20.0F);
            conf.setQueues(CapacitySchedulerTestBase.B, new String[]{ "b1", "b2", "b3", "b4" });
            conf.setCapacity(CapacitySchedulerTestBase.B1, CapacitySchedulerTestBase.B1_CAPACITY);
            conf.setCapacity(CapacitySchedulerTestBase.B2, CapacitySchedulerTestBase.B2_CAPACITY);
            conf.setCapacity(CapacitySchedulerTestBase.B3, CapacitySchedulerTestBase.B3_CAPACITY);
            conf.setCapacity(B4, B4_CAPACITY);
            cs.reinitialize(conf, mockContext);
            checkQueueCapacities(cs, 80.0F, 20.0F);
            // Verify parent for B4
            CSQueue rootQueue = cs.getRootQueue();
            CSQueue queueB = findQueue(rootQueue, CapacitySchedulerTestBase.B);
            CSQueue queueB4 = findQueue(queueB, B4);
            Assert.assertEquals(queueB, queueB4.getParent());
        } finally {
            CapacitySchedulerTestBase.B3_CAPACITY += B4_CAPACITY;
            cs.stop();
        }
    }

    @Test
    public void testCapacitySchedulerInfo() throws Exception {
        QueueInfo queueInfo = resourceManager.getResourceScheduler().getQueueInfo("a", true, true);
        Assert.assertEquals("Queue Name should be a", "a", queueInfo.getQueueName());
        Assert.assertEquals("Child Queues size should be 2", 2, queueInfo.getChildQueues().size());
        List<QueueUserACLInfo> userACLInfo = resourceManager.getResourceScheduler().getQueueUserAclInfo();
        Assert.assertNotNull(userACLInfo);
        for (QueueUserACLInfo queueUserACLInfo : userACLInfo) {
            Assert.assertEquals(1, getQueueCount(userACLInfo, queueUserACLInfo.getQueueName()));
        }
    }

    @Test
    public void testBlackListNodes() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        String host = "127.0.0.1";
        RMNode node = MockNodes.newNodeInfo(0, MockNodes.newResource((4 * (GB))), 1, host);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
        ApplicationId appId = BuilderUtils.newApplicationId(100, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        RMAppAttemptMetrics attemptMetric = new RMAppAttemptMetrics(appAttemptId, getRMContext());
        RMAppImpl app = Mockito.mock(RMAppImpl.class);
        Mockito.when(app.getApplicationId()).thenReturn(appId);
        RMAppAttemptImpl attempt = Mockito.mock(RMAppAttemptImpl.class);
        Container container = Mockito.mock(Container.class);
        Mockito.when(attempt.getMasterContainer()).thenReturn(container);
        ApplicationSubmissionContext submissionContext = Mockito.mock(ApplicationSubmissionContext.class);
        Mockito.when(attempt.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt.getAppAttemptId()).thenReturn(appAttemptId);
        Mockito.when(attempt.getRMAppAttemptMetrics()).thenReturn(attemptMetric);
        Mockito.when(app.getCurrentAppAttempt()).thenReturn(attempt);
        getRMContext().getRMApps().put(appId, app);
        SchedulerEvent addAppEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId, "default", "user");
        cs.handle(addAppEvent);
        SchedulerEvent addAttemptEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId, false);
        cs.handle(addAttemptEvent);
        // Verify the blacklist can be updated independent of requesting containers
        cs.allocate(appAttemptId, Collections.<ResourceRequest>emptyList(), null, Collections.<ContainerId>emptyList(), Collections.singletonList(host), null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        Assert.assertTrue(cs.getApplicationAttempt(appAttemptId).isPlaceBlacklisted(host));
        cs.allocate(appAttemptId, Collections.<ResourceRequest>emptyList(), null, Collections.<ContainerId>emptyList(), null, Collections.singletonList(host), TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        Assert.assertFalse(cs.getApplicationAttempt(appAttemptId).isPlaceBlacklisted(host));
        stop();
    }

    @Test
    public void testAllocateReorder() throws Exception {
        // Confirm that allocation (resource request) alone will trigger a change in
        // application ordering where appropriate
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        LeafQueue q = ((LeafQueue) (cs.getQueue("default")));
        Assert.assertNotNull(q);
        FairOrderingPolicy fop = new FairOrderingPolicy();
        fop.setSizeBasedWeight(true);
        q.setOrderingPolicy(fop);
        String host = "127.0.0.1";
        RMNode node = MockNodes.newNodeInfo(0, MockNodes.newResource((4 * (GB))), 1, host);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
        // add app begin
        ApplicationId appId1 = BuilderUtils.newApplicationId(100, 1);
        ApplicationAttemptId appAttemptId1 = BuilderUtils.newApplicationAttemptId(appId1, 1);
        RMAppAttemptMetrics attemptMetric1 = new RMAppAttemptMetrics(appAttemptId1, getRMContext());
        RMAppImpl app1 = Mockito.mock(RMAppImpl.class);
        Mockito.when(app1.getApplicationId()).thenReturn(appId1);
        RMAppAttemptImpl attempt1 = Mockito.mock(RMAppAttemptImpl.class);
        Container container = Mockito.mock(Container.class);
        Mockito.when(attempt1.getMasterContainer()).thenReturn(container);
        ApplicationSubmissionContext submissionContext = Mockito.mock(ApplicationSubmissionContext.class);
        Mockito.when(attempt1.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt1.getAppAttemptId()).thenReturn(appAttemptId1);
        Mockito.when(attempt1.getRMAppAttemptMetrics()).thenReturn(attemptMetric1);
        Mockito.when(app1.getCurrentAppAttempt()).thenReturn(attempt1);
        getRMContext().getRMApps().put(appId1, app1);
        SchedulerEvent addAppEvent1 = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId1, "default", "user");
        cs.handle(addAppEvent1);
        SchedulerEvent addAttemptEvent1 = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId1, false);
        cs.handle(addAttemptEvent1);
        // add app end
        // add app begin
        ApplicationId appId2 = BuilderUtils.newApplicationId(100, 2);
        ApplicationAttemptId appAttemptId2 = BuilderUtils.newApplicationAttemptId(appId2, 1);
        RMAppAttemptMetrics attemptMetric2 = new RMAppAttemptMetrics(appAttemptId2, getRMContext());
        RMAppImpl app2 = Mockito.mock(RMAppImpl.class);
        Mockito.when(app2.getApplicationId()).thenReturn(appId2);
        RMAppAttemptImpl attempt2 = Mockito.mock(RMAppAttemptImpl.class);
        Mockito.when(attempt2.getMasterContainer()).thenReturn(container);
        Mockito.when(attempt2.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt2.getAppAttemptId()).thenReturn(appAttemptId2);
        Mockito.when(attempt2.getRMAppAttemptMetrics()).thenReturn(attemptMetric2);
        Mockito.when(app2.getCurrentAppAttempt()).thenReturn(attempt2);
        getRMContext().getRMApps().put(appId2, app2);
        SchedulerEvent addAppEvent2 = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId2, "default", "user");
        cs.handle(addAppEvent2);
        SchedulerEvent addAttemptEvent2 = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId2, false);
        cs.handle(addAttemptEvent2);
        // add app end
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        Priority priority = TestUtils.createMockPriority(1);
        ResourceRequest r1 = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
        // This will allocate for app1
        cs.allocate(appAttemptId1, Collections.<ResourceRequest>singletonList(r1), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        // And this will result in container assignment for app1
        CapacityScheduler.schedule(cs);
        // Verify that app1 is still first in assignment order
        // This happens because app2 has no demand/a magnitude of NaN, which
        // results in app1 and app2 being equal in the fairness comparison and
        // failling back to fifo (start) ordering
        Assert.assertEquals(q.getOrderingPolicy().getAssignmentIterator().next().getId(), appId1.toString());
        // Now, allocate for app2 (this would be the first/AM allocation)
        ResourceRequest r2 = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
        cs.allocate(appAttemptId2, Collections.<ResourceRequest>singletonList(r2), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        // In this case we do not perform container assignment because we want to
        // verify re-ordering based on the allocation alone
        // Now, the first app for assignment is app2
        Assert.assertEquals(q.getOrderingPolicy().getAssignmentIterator().next().getId(), appId2.toString());
        stop();
    }

    @Test
    public void testResourceOverCommit() throws Exception {
        int waitCount;
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("127.0.0.1:1234", (4 * (GB)));
        RMApp app1 = rm.submitApp(2048);
        // kick the scheduling, 2 GB given to AM1, remaining 2GB on nm1
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
        MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
        am1.registerAppAttempt();
        SchedulerNodeReport report_nm1 = getResourceScheduler().getNodeReport(nm1.getNodeId());
        // check node report, 2 GB used and 2 GB available
        Assert.assertEquals((2 * (GB)), report_nm1.getUsedResource().getMemorySize());
        Assert.assertEquals((2 * (GB)), report_nm1.getAvailableResource().getMemorySize());
        // add request for containers
        am1.addRequests(new String[]{ "127.0.0.1", "127.0.0.2" }, (2 * (GB)), 1, 1);
        AllocateResponse alloc1Response = am1.schedule();// send the request

        // kick the scheduler, 2 GB given to AM1, resource remaining 0
        nm1.nodeHeartbeat(true);
        while ((alloc1Response.getAllocatedContainers().size()) < 1) {
            TestCapacityScheduler.LOG.info("Waiting for containers to be created for app 1...");
            Thread.sleep(100);
            alloc1Response = am1.schedule();
        } 
        List<Container> allocated1 = alloc1Response.getAllocatedContainers();
        Assert.assertEquals(1, allocated1.size());
        Assert.assertEquals((2 * (GB)), allocated1.get(0).getResource().getMemorySize());
        Assert.assertEquals(nm1.getNodeId(), allocated1.get(0).getNodeId());
        report_nm1 = getResourceScheduler().getNodeReport(nm1.getNodeId());
        // check node report, 4 GB used and 0 GB available
        Assert.assertEquals(0, report_nm1.getAvailableResource().getMemorySize());
        Assert.assertEquals((4 * (GB)), report_nm1.getUsedResource().getMemorySize());
        // check container is assigned with 2 GB.
        Container c1 = allocated1.get(0);
        Assert.assertEquals((2 * (GB)), c1.getResource().getMemorySize());
        // update node resource to 2 GB, so resource is over-consumed.
        Map<NodeId, ResourceOption> nodeResourceMap = new HashMap<NodeId, ResourceOption>();
        nodeResourceMap.put(nm1.getNodeId(), ResourceOption.newInstance(Resource.newInstance((2 * (GB)), 1), (-1)));
        UpdateNodeResourceRequest request = UpdateNodeResourceRequest.newInstance(nodeResourceMap);
        AdminService as = ((MockRM) (rm)).getAdminService();
        as.updateNodeResource(request);
        waitCount = 0;
        while ((waitCount++) != 20) {
            report_nm1 = getResourceScheduler().getNodeReport(nm1.getNodeId());
            if ((report_nm1.getAvailableResource().getMemorySize()) != 0) {
                break;
            }
            TestCapacityScheduler.LOG.info((("Waiting for RMNodeResourceUpdateEvent to be handled... Tried " + waitCount) + " times already.."));
            Thread.sleep(1000);
        } 
        // Now, the used resource is still 4 GB, and available resource is minus value.
        report_nm1 = getResourceScheduler().getNodeReport(nm1.getNodeId());
        Assert.assertEquals((4 * (GB)), report_nm1.getUsedResource().getMemorySize());
        Assert.assertEquals(((-2) * (GB)), report_nm1.getAvailableResource().getMemorySize());
        // Check container can complete successfully in case of resource over-commitment.
        ContainerStatus containerStatus = BuilderUtils.newContainerStatus(c1.getId(), COMPLETE, "", 0, c1.getResource());
        nm1.containerStatus(containerStatus);
        waitCount = 0;
        while (((attempt1.getJustFinishedContainers().size()) < 1) && ((waitCount++) != 20)) {
            TestCapacityScheduler.LOG.info((("Waiting for containers to be finished for app 1... Tried " + waitCount) + " times already.."));
            Thread.sleep(100);
        } 
        Assert.assertEquals(1, attempt1.getJustFinishedContainers().size());
        Assert.assertEquals(1, am1.schedule().getCompletedContainersStatuses().size());
        report_nm1 = getResourceScheduler().getNodeReport(nm1.getNodeId());
        Assert.assertEquals((2 * (GB)), report_nm1.getUsedResource().getMemorySize());
        // As container return 2 GB back, the available resource becomes 0 again.
        Assert.assertEquals((0 * (GB)), report_nm1.getAvailableResource().getMemorySize());
        // Verify no NPE is trigger in schedule after resource is updated.
        am1.addRequests(new String[]{ "127.0.0.1", "127.0.0.2" }, (3 * (GB)), 1, 1);
        alloc1Response = am1.schedule();
        Assert.assertEquals("Shouldn't have enough resource to allocate containers", 0, alloc1Response.getAllocatedContainers().size());
        int times = 0;
        // try 10 times as scheduling is async process.
        while (((alloc1Response.getAllocatedContainers().size()) < 1) && ((times++) < 10)) {
            TestCapacityScheduler.LOG.info((("Waiting for containers to be allocated for app 1... Tried " + times) + " times already.."));
            Thread.sleep(100);
        } 
        Assert.assertEquals("Shouldn't have enough resource to allocate containers", 0, alloc1Response.getAllocatedContainers().size());
        stop();
    }

    @Test
    public void testGetAppsInQueue() throws Exception {
        Application application_0 = new Application("user_0", "a1", resourceManager);
        application_0.submit();
        Application application_1 = new Application("user_0", "a2", resourceManager);
        application_1.submit();
        Application application_2 = new Application("user_0", "b2", resourceManager);
        application_2.submit();
        ResourceScheduler scheduler = resourceManager.getResourceScheduler();
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(application_0.getApplicationAttemptId()));
        Assert.assertTrue(appsInA.contains(application_1.getApplicationAttemptId()));
        Assert.assertEquals(2, appsInA.size());
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(application_0.getApplicationAttemptId()));
        Assert.assertTrue(appsInRoot.contains(application_1.getApplicationAttemptId()));
        Assert.assertTrue(appsInRoot.contains(application_2.getApplicationAttemptId()));
        Assert.assertEquals(3, appsInRoot.size());
        Assert.assertNull(scheduler.getAppsInQueue("nonexistentqueue"));
    }

    @Test
    public void testAddAndRemoveAppFromCapacityScheduler() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        @SuppressWarnings("unchecked")
        AbstractYarnScheduler<SchedulerApplicationAttempt, SchedulerNode> cs = ((AbstractYarnScheduler<SchedulerApplicationAttempt, SchedulerNode>) (getResourceScheduler()));
        SchedulerApplication<SchedulerApplicationAttempt> app = TestSchedulerUtils.verifyAppAddedAndRemovedFromScheduler(cs.getSchedulerApplications(), cs, "a1");
        Assert.assertEquals("a1", app.getQueue().getQueueName());
    }

    @Test
    public void testAsyncScheduling() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        final int NODES = 100;
        // Register nodes
        for (int i = 0; i < NODES; ++i) {
            String host = "192.168.1." + i;
            RMNode node = MockNodes.newNodeInfo(0, MockNodes.newResource((4 * (GB))), 1, host);
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
        }
        // Now directly exercise the scheduling loop
        for (int i = 0; i < NODES; ++i) {
            CapacityScheduler.schedule(cs);
        }
    }

    @Test(timeout = 30000)
    public void testAllocateDoesNotBlockOnSchedulerLock() throws Exception {
        final YarnConfiguration conf = new YarnConfiguration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        TestAMAuthorization.MyContainerManager containerManager = new TestAMAuthorization.MyContainerManager();
        final TestAMAuthorization.MockRMWithAMS rm = new TestAMAuthorization.MockRMWithAMS(conf, containerManager);
        start();
        MockNM nm1 = rm.registerNode("localhost:1234", 5120);
        Map<ApplicationAccessType, String> acls = new HashMap<ApplicationAccessType, String>(2);
        acls.put(VIEW_APP, "*");
        RMApp app = rm.submitApp(1024, "appname", "appuser", acls);
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt = app.getCurrentAppAttempt();
        ApplicationAttemptId applicationAttemptId = attempt.getAppAttemptId();
        int msecToWait = 10000;
        int msecToSleep = 100;
        while (((attempt.getAppAttemptState()) != (RMAppAttemptState.LAUNCHED)) && (msecToWait > 0)) {
            TestCapacityScheduler.LOG.info((("Waiting for AppAttempt to reach LAUNCHED state. " + "Current state is ") + (attempt.getAppAttemptState())));
            Thread.sleep(msecToSleep);
            msecToWait -= msecToSleep;
        } 
        Assert.assertEquals(attempt.getAppAttemptState(), LAUNCHED);
        // Create a client to the RM.
        final YarnRPC rpc = YarnRPC.create(conf);
        UserGroupInformation currentUser = UserGroupInformation.createRemoteUser(applicationAttemptId.toString());
        Credentials credentials = containerManager.getContainerCredentials();
        final InetSocketAddress rmBindAddress = getApplicationMasterService().getBindAddress();
        Token<? extends TokenIdentifier> amRMToken = setupAndReturnAMRMToken(rmBindAddress, credentials.getAllTokens());
        currentUser.addToken(amRMToken);
        ApplicationMasterProtocol client = currentUser.doAs(new PrivilegedAction<ApplicationMasterProtocol>() {
            @Override
            public ApplicationMasterProtocol run() {
                return ((ApplicationMasterProtocol) (rpc.getProxy(ApplicationMasterProtocol.class, rmBindAddress, conf)));
            }
        });
        RegisterApplicationMasterRequest request = RegisterApplicationMasterRequest.newInstance("localhost", 12345, "");
        client.registerApplicationMaster(request);
        // Allocate a container
        List<ResourceRequest> asks = Collections.singletonList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((2 * (GB))), 1));
        AllocateRequest allocateRequest = AllocateRequest.newInstance(0, 0.0F, asks, null, null);
        client.allocate(allocateRequest);
        // Make sure the container is allocated in RM
        nm1.nodeHeartbeat(true);
        ContainerId containerId2 = ContainerId.newContainerId(applicationAttemptId, 2);
        Assert.assertTrue(rm.waitForState(nm1, containerId2, ALLOCATED));
        // Acquire the container
        allocateRequest = AllocateRequest.newInstance(1, 0.0F, null, null, null);
        client.allocate(allocateRequest);
        // Launch the container
        final CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        RMContainer rmContainer = cs.getRMContainer(containerId2);
        rmContainer.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEvent(containerId2, RMContainerEventType.LAUNCHED));
        // grab the scheduler lock from another thread
        // and verify an allocate call in this thread doesn't block on it
        final CyclicBarrier barrier = new CyclicBarrier(2);
        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(cs) {
                    try {
                        barrier.await();
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        otherThread.start();
        barrier.await();
        List<ContainerId> release = Collections.singletonList(containerId2);
        allocateRequest = AllocateRequest.newInstance(2, 0.0F, null, release, null);
        client.allocate(allocateRequest);
        barrier.await();
        otherThread.join();
        stop();
    }

    @Test
    public void testNumClusterNodes() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(conf);
        RMContext rmContext = TestUtils.getMockRMContext();
        cs.setRMContext(rmContext);
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(csConf);
        cs.init(csConf);
        cs.start();
        Assert.assertEquals(0, cs.getNumClusterNodes());
        RMNode n1 = MockNodes.newNodeInfo(0, MockNodes.newResource((4 * (GB))), 1);
        RMNode n2 = MockNodes.newNodeInfo(0, MockNodes.newResource((2 * (GB))), 2);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n1));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n2));
        Assert.assertEquals(2, cs.getNumClusterNodes());
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(n1));
        Assert.assertEquals(1, cs.getNumClusterNodes());
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n1));
        Assert.assertEquals(2, cs.getNumClusterNodes());
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(n2));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(n1));
        Assert.assertEquals(0, cs.getNumClusterNodes());
        cs.stop();
    }

    @Test(timeout = 120000)
    public void testPreemptionInfo() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(RM_AM_MAX_ATTEMPTS, 3);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        int CONTAINER_MEMORY = 1024;// start RM

        MockRM rm1 = new MockRM(conf);
        start();
        // get scheduler
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        // start NM
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the AM
        RMApp app0 = rm1.submitApp(CONTAINER_MEMORY);
        MockAM am0 = MockRM.launchAM(app0, rm1, nm1);
        am0.registerAppAttempt();
        // get scheduler app
        FiCaSchedulerApp schedulerAppAttempt = cs.getSchedulerApplications().get(app0.getApplicationId()).getCurrentAppAttempt();
        // allocate some containers and launch them
        List<Container> allocatedContainers = am0.allocateAndWaitForContainers(3, CONTAINER_MEMORY, nm1);
        // kill the 3 containers
        for (Container c : allocatedContainers) {
            cs.markContainerForKillable(schedulerAppAttempt.getRMContainer(c.getId()));
        }
        // check values
        waitForAppPreemptionInfo(app0, Resource.newInstance((CONTAINER_MEMORY * 3), 3), 0, 3, Resource.newInstance((CONTAINER_MEMORY * 3), 3), false, 3);
        // kill app0-attempt0 AM container
        cs.markContainerForKillable(schedulerAppAttempt.getRMContainer(app0.getCurrentAppAttempt().getMasterContainer().getId()));
        // wait for app0 failed
        waitForNewAttemptCreated(app0, am0.getApplicationAttemptId());
        // check values
        waitForAppPreemptionInfo(app0, Resource.newInstance((CONTAINER_MEMORY * 4), 4), 1, 3, Resource.newInstance(0, 0), false, 0);
        // launch app0-attempt1
        MockAM am1 = MockRM.launchAM(app0, rm1, nm1);
        am1.registerAppAttempt();
        schedulerAppAttempt = cs.getSchedulerApplications().get(app0.getApplicationId()).getCurrentAppAttempt();
        // allocate some containers and launch them
        allocatedContainers = am1.allocateAndWaitForContainers(3, CONTAINER_MEMORY, nm1);
        for (Container c : allocatedContainers) {
            cs.markContainerForKillable(schedulerAppAttempt.getRMContainer(c.getId()));
        }
        // check values
        waitForAppPreemptionInfo(app0, Resource.newInstance((CONTAINER_MEMORY * 7), 7), 1, 6, Resource.newInstance((CONTAINER_MEMORY * 3), 3), false, 3);
        stop();
    }

    @Test(timeout = 300000)
    public void testRecoverRequestAfterPreemption() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm1 = new MockRM(conf);
        start();
        MockNM nm1 = rm1.registerNode("127.0.0.1:1234", 8000);
        RMApp app1 = rm1.submitApp(1024);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        // request a container.
        am1.allocate("127.0.0.1", 1024, 1, new ArrayList<ContainerId>());
        ContainerId containerId1 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        rm1.waitForState(nm1, containerId1, ALLOCATED);
        RMContainer rmContainer = cs.getRMContainer(containerId1);
        List<ResourceRequest> requests = rmContainer.getContainerRequest().getResourceRequests();
        FiCaSchedulerApp app = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        FiCaSchedulerNode node = cs.getNode(rmContainer.getAllocatedNode());
        for (ResourceRequest request : requests) {
            // Skip the OffRack and RackLocal resource requests.
            if ((request.getResourceName().equals(node.getRackName())) || (request.getResourceName().equals(ANY))) {
                continue;
            }
            // Already the node local resource request is cleared from RM after
            // allocation.
            Assert.assertEquals(0, app.getOutstandingAsksCount(SchedulerRequestKey.create(request), request.getResourceName()));
        }
        // Call killContainer to preempt the container
        cs.markContainerForKillable(rmContainer);
        Assert.assertEquals(3, requests.size());
        for (ResourceRequest request : requests) {
            // Resource request must have added back in RM after preempt event
            // handling.
            Assert.assertEquals(1, app.getOutstandingAsksCount(SchedulerRequestKey.create(request), request.getResourceName()));
        }
        // New container will be allocated and will move to ALLOCATED state
        ContainerId containerId2 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 3);
        rm1.waitForState(nm1, containerId2, ALLOCATED);
        // allocate container
        List<Container> containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        // Now with updated ResourceRequest, a container is allocated for AM.
        Assert.assertTrue(((containers.size()) == 1));
    }

    @Test
    public void testMoveAppBasic() throws Exception {
        MockRM rm = setUpMove();
        AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        String queue = scheduler.getApplicationAttempt(appsInA1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("a1", queue);
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        List<ApplicationAttemptId> appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        List<ApplicationAttemptId> appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        // now move the app
        scheduler.moveApplication(app.getApplicationId(), "b1");
        // check postconditions
        appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertEquals(1, appsInB1.size());
        queue = scheduler.getApplicationAttempt(appsInB1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("b1", queue);
        appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.contains(appAttemptId));
        Assert.assertEquals(1, appsInB.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertTrue(appsInA1.isEmpty());
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.isEmpty());
        stop();
    }

    @Test
    public void testMoveAppSameParent() throws Exception {
        MockRM rm = setUpMove();
        AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        String queue = scheduler.getApplicationAttempt(appsInA1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("a1", queue);
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        List<ApplicationAttemptId> appsInA2 = scheduler.getAppsInQueue("a2");
        Assert.assertTrue(appsInA2.isEmpty());
        // now move the app
        scheduler.moveApplication(app.getApplicationId(), "a2");
        // check postconditions
        appsInA2 = scheduler.getAppsInQueue("a2");
        Assert.assertEquals(1, appsInA2.size());
        queue = scheduler.getApplicationAttempt(appsInA2.get(0)).getQueue().getQueueName();
        Assert.assertEquals("a2", queue);
        appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertTrue(appsInA1.isEmpty());
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        stop();
    }

    @Test
    public void testMoveAppForMoveToQueueWithFreeCap() throws Exception {
        ResourceScheduler scheduler = resourceManager.getResourceScheduler();
        // Register node1
        String host_0 = "host_0";
        NodeManager nm_0 = registerNode(host_0, 1234, 2345, DEFAULT_RACK, Resources.createResource((4 * (GB)), 1));
        // Register node2
        String host_1 = "host_1";
        NodeManager nm_1 = registerNode(host_1, 1234, 2345, DEFAULT_RACK, Resources.createResource((2 * (GB)), 1));
        // ResourceRequest priorities
        Priority priority_0 = Priority.newInstance(0);
        Priority priority_1 = Priority.newInstance(1);
        // Submit application_0
        Application application_0 = new Application("user_0", "a1", resourceManager);
        application_0.submit();// app + app attempt event sent to scheduler

        application_0.addNodeManager(host_0, 1234, nm_0);
        application_0.addNodeManager(host_1, 1234, nm_1);
        Resource capability_0_0 = Resources.createResource((1 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_1, capability_0_0);
        Resource capability_0_1 = Resources.createResource((2 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_0, capability_0_1);
        Task task_0_0 = new Task(application_0, priority_1, new String[]{ host_0, host_1 });
        application_0.addTask(task_0_0);
        // Submit application_1
        Application application_1 = new Application("user_1", "b2", resourceManager);
        application_1.submit();// app + app attempt event sent to scheduler

        application_1.addNodeManager(host_0, 1234, nm_0);
        application_1.addNodeManager(host_1, 1234, nm_1);
        Resource capability_1_0 = Resources.createResource((1 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_1, capability_1_0);
        Resource capability_1_1 = Resources.createResource((2 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_0, capability_1_1);
        Task task_1_0 = new Task(application_1, priority_1, new String[]{ host_0, host_1 });
        application_1.addTask(task_1_0);
        // Send resource requests to the scheduler
        application_0.schedule();// allocate

        application_1.schedule();// allocate

        // task_0_0 task_1_0 allocated, used=2G
        nodeUpdate(nm_0);
        // nothing allocated
        nodeUpdate(nm_1);
        // Get allocations from the scheduler
        application_0.schedule();// task_0_0

        checkApplicationResourceUsage((1 * (GB)), application_0);
        application_1.schedule();// task_1_0

        checkApplicationResourceUsage((1 * (GB)), application_1);
        checkNodeResourceUsage((2 * (GB)), nm_0);// task_0_0 (1G) and task_1_0 (1G) 2G

        // available
        checkNodeResourceUsage((0 * (GB)), nm_1);// no tasks, 2G available

        // move app from a1(30% cap of total 10.5% cap) to b1(79,2% cap of 89,5%
        // total cap)
        scheduler.moveApplication(application_0.getApplicationId(), "b1");
        // 2GB 1C
        Task task_1_1 = new Task(application_1, priority_0, new String[]{ ResourceRequest.ANY });
        application_1.addTask(task_1_1);
        application_1.schedule();
        // 2GB 1C
        Task task_0_1 = new Task(application_0, priority_0, new String[]{ host_0, host_1 });
        application_0.addTask(task_0_1);
        application_0.schedule();
        // prev 2G used free 2G
        nodeUpdate(nm_0);
        // prev 0G used free 2G
        nodeUpdate(nm_1);
        // Get allocations from the scheduler
        application_1.schedule();
        checkApplicationResourceUsage((3 * (GB)), application_1);
        // Get allocations from the scheduler
        application_0.schedule();
        checkApplicationResourceUsage((3 * (GB)), application_0);
        checkNodeResourceUsage((4 * (GB)), nm_0);
        checkNodeResourceUsage((2 * (GB)), nm_1);
    }

    @Test
    public void testMoveAppSuccess() throws Exception {
        ResourceScheduler scheduler = resourceManager.getResourceScheduler();
        // Register node1
        String host_0 = "host_0";
        NodeManager nm_0 = registerNode(host_0, 1234, 2345, DEFAULT_RACK, Resources.createResource((5 * (GB)), 1));
        // Register node2
        String host_1 = "host_1";
        NodeManager nm_1 = registerNode(host_1, 1234, 2345, DEFAULT_RACK, Resources.createResource((5 * (GB)), 1));
        // ResourceRequest priorities
        Priority priority_0 = Priority.newInstance(0);
        Priority priority_1 = Priority.newInstance(1);
        // Submit application_0
        Application application_0 = new Application("user_0", "a1", resourceManager);
        application_0.submit();// app + app attempt event sent to scheduler

        application_0.addNodeManager(host_0, 1234, nm_0);
        application_0.addNodeManager(host_1, 1234, nm_1);
        Resource capability_0_0 = Resources.createResource((3 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_1, capability_0_0);
        Resource capability_0_1 = Resources.createResource((2 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_0, capability_0_1);
        Task task_0_0 = new Task(application_0, priority_1, new String[]{ host_0, host_1 });
        application_0.addTask(task_0_0);
        // Submit application_1
        Application application_1 = new Application("user_1", "b2", resourceManager);
        application_1.submit();// app + app attempt event sent to scheduler

        application_1.addNodeManager(host_0, 1234, nm_0);
        application_1.addNodeManager(host_1, 1234, nm_1);
        Resource capability_1_0 = Resources.createResource((1 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_1, capability_1_0);
        Resource capability_1_1 = Resources.createResource((2 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_0, capability_1_1);
        Task task_1_0 = new Task(application_1, priority_1, new String[]{ host_0, host_1 });
        application_1.addTask(task_1_0);
        // Send resource requests to the scheduler
        application_0.schedule();// allocate

        application_1.schedule();// allocate

        // b2 can only run 1 app at a time
        scheduler.moveApplication(application_0.getApplicationId(), "b2");
        nodeUpdate(nm_0);
        nodeUpdate(nm_1);
        // Get allocations from the scheduler
        application_0.schedule();// task_0_0

        checkApplicationResourceUsage((0 * (GB)), application_0);
        application_1.schedule();// task_1_0

        checkApplicationResourceUsage((1 * (GB)), application_1);
        // task_1_0 (1G) application_0 moved to b2 with max running app 1 so it is
        // not scheduled
        checkNodeResourceUsage((1 * (GB)), nm_0);
        checkNodeResourceUsage((0 * (GB)), nm_1);
        // lets move application_0 to a queue where it can run
        scheduler.moveApplication(application_0.getApplicationId(), "a2");
        application_0.schedule();
        nodeUpdate(nm_1);
        // Get allocations from the scheduler
        application_0.schedule();// task_0_0

        checkApplicationResourceUsage((3 * (GB)), application_0);
        checkNodeResourceUsage((1 * (GB)), nm_0);
        checkNodeResourceUsage((3 * (GB)), nm_1);
    }

    @Test(expected = YarnException.class)
    public void testMoveAppViolateQueueState() throws Exception {
        resourceManager = new ResourceManager() {
            @Override
            protected RMNodeLabelsManager createNodeLabelManager() {
                RMNodeLabelsManager mgr = new NullRMNodeLabelsManager();
                mgr.init(getConfig());
                return mgr;
            }
        };
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(csConf);
        StringBuilder qState = new StringBuilder();
        qState.append(PREFIX).append(CapacitySchedulerTestBase.B).append(DOT).append(STATE);
        csConf.set(qState.toString(), STOPPED.name());
        YarnConfiguration conf = new YarnConfiguration(csConf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        resourceManager.init(conf);
        resourceManager.getRMContext().getContainerTokenSecretManager().rollMasterKey();
        resourceManager.getRMContext().getNMTokenSecretManager().rollMasterKey();
        start();
        mockContext = Mockito.mock(RMContext.class);
        Mockito.when(mockContext.getConfigurationProvider()).thenReturn(new LocalConfigurationProvider());
        ResourceScheduler scheduler = resourceManager.getResourceScheduler();
        // Register node1
        String host_0 = "host_0";
        NodeManager nm_0 = registerNode(host_0, 1234, 2345, DEFAULT_RACK, Resources.createResource((6 * (GB)), 1));
        // ResourceRequest priorities
        Priority priority_0 = Priority.newInstance(0);
        Priority priority_1 = Priority.newInstance(1);
        // Submit application_0
        Application application_0 = new Application("user_0", "a1", resourceManager);
        application_0.submit();// app + app attempt event sent to scheduler

        application_0.addNodeManager(host_0, 1234, nm_0);
        Resource capability_0_0 = Resources.createResource((3 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_1, capability_0_0);
        Resource capability_0_1 = Resources.createResource((2 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_0, capability_0_1);
        Task task_0_0 = new Task(application_0, priority_1, new String[]{ host_0 });
        application_0.addTask(task_0_0);
        // Send resource requests to the scheduler
        application_0.schedule();// allocate

        // task_0_0 allocated
        nodeUpdate(nm_0);
        // Get allocations from the scheduler
        application_0.schedule();// task_0_0

        checkApplicationResourceUsage((3 * (GB)), application_0);
        checkNodeResourceUsage((3 * (GB)), nm_0);
        // b2 queue contains 3GB consumption app,
        // add another 3GB will hit max capacity limit on queue b
        scheduler.moveApplication(application_0.getApplicationId(), "b1");
    }

    @Test
    public void testMoveAppQueueMetricsCheck() throws Exception {
        ResourceScheduler scheduler = resourceManager.getResourceScheduler();
        // Register node1
        String host_0 = "host_0";
        NodeManager nm_0 = registerNode(host_0, 1234, 2345, DEFAULT_RACK, Resources.createResource((5 * (GB)), 1));
        // Register node2
        String host_1 = "host_1";
        NodeManager nm_1 = registerNode(host_1, 1234, 2345, DEFAULT_RACK, Resources.createResource((5 * (GB)), 1));
        // ResourceRequest priorities
        Priority priority_0 = Priority.newInstance(0);
        Priority priority_1 = Priority.newInstance(1);
        // Submit application_0
        Application application_0 = new Application("user_0", "a1", resourceManager);
        application_0.submit();// app + app attempt event sent to scheduler

        application_0.addNodeManager(host_0, 1234, nm_0);
        application_0.addNodeManager(host_1, 1234, nm_1);
        Resource capability_0_0 = Resources.createResource((3 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_1, capability_0_0);
        Resource capability_0_1 = Resources.createResource((2 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_0, capability_0_1);
        Task task_0_0 = new Task(application_0, priority_1, new String[]{ host_0, host_1 });
        application_0.addTask(task_0_0);
        // Submit application_1
        Application application_1 = new Application("user_1", "b2", resourceManager);
        application_1.submit();// app + app attempt event sent to scheduler

        application_1.addNodeManager(host_0, 1234, nm_0);
        application_1.addNodeManager(host_1, 1234, nm_1);
        Resource capability_1_0 = Resources.createResource((1 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_1, capability_1_0);
        Resource capability_1_1 = Resources.createResource((2 * (GB)), 1);
        application_1.addResourceRequestSpec(priority_0, capability_1_1);
        Task task_1_0 = new Task(application_1, priority_1, new String[]{ host_0, host_1 });
        application_1.addTask(task_1_0);
        // Send resource requests to the scheduler
        application_0.schedule();// allocate

        application_1.schedule();// allocate

        nodeUpdate(nm_0);
        nodeUpdate(nm_1);
        CapacityScheduler cs = ((CapacityScheduler) (resourceManager.getResourceScheduler()));
        CSQueue origRootQ = cs.getRootQueue();
        CapacitySchedulerInfo oldInfo = new CapacitySchedulerInfo(origRootQ, cs);
        int origNumAppsA = getNumAppsInQueue("a", origRootQ.getChildQueues());
        int origNumAppsRoot = origRootQ.getNumApplications();
        scheduler.moveApplication(application_0.getApplicationId(), "a2");
        CSQueue newRootQ = cs.getRootQueue();
        int newNumAppsA = getNumAppsInQueue("a", newRootQ.getChildQueues());
        int newNumAppsRoot = newRootQ.getNumApplications();
        CapacitySchedulerInfo newInfo = new CapacitySchedulerInfo(newRootQ, cs);
        CapacitySchedulerLeafQueueInfo origOldA1 = ((CapacitySchedulerLeafQueueInfo) (getQueueInfo("a1", oldInfo.getQueues())));
        CapacitySchedulerLeafQueueInfo origNewA1 = ((CapacitySchedulerLeafQueueInfo) (getQueueInfo("a1", newInfo.getQueues())));
        CapacitySchedulerLeafQueueInfo targetOldA2 = ((CapacitySchedulerLeafQueueInfo) (getQueueInfo("a2", oldInfo.getQueues())));
        CapacitySchedulerLeafQueueInfo targetNewA2 = ((CapacitySchedulerLeafQueueInfo) (getQueueInfo("a2", newInfo.getQueues())));
        // originally submitted here
        Assert.assertEquals(1, origOldA1.getNumApplications());
        Assert.assertEquals(1, origNumAppsA);
        Assert.assertEquals(2, origNumAppsRoot);
        // after the move
        Assert.assertEquals(0, origNewA1.getNumApplications());
        Assert.assertEquals(1, newNumAppsA);
        Assert.assertEquals(2, newNumAppsRoot);
        // original consumption on a1
        Assert.assertEquals((3 * (GB)), origOldA1.getResourcesUsed().getMemorySize());
        Assert.assertEquals(1, origOldA1.getResourcesUsed().getvCores());
        Assert.assertEquals(0, origNewA1.getResourcesUsed().getMemorySize());// after the move

        Assert.assertEquals(0, origNewA1.getResourcesUsed().getvCores());// after the move

        // app moved here with live containers
        Assert.assertEquals((3 * (GB)), targetNewA2.getResourcesUsed().getMemorySize());
        Assert.assertEquals(1, targetNewA2.getResourcesUsed().getvCores());
        // it was empty before the move
        Assert.assertEquals(0, targetOldA2.getNumApplications());
        Assert.assertEquals(0, targetOldA2.getResourcesUsed().getMemorySize());
        Assert.assertEquals(0, targetOldA2.getResourcesUsed().getvCores());
        // after the app moved here
        Assert.assertEquals(1, targetNewA2.getNumApplications());
        // 1 container on original queue before move
        Assert.assertEquals(1, origOldA1.getNumContainers());
        // after the move the resource released
        Assert.assertEquals(0, origNewA1.getNumContainers());
        // and moved to the new queue
        Assert.assertEquals(1, targetNewA2.getNumContainers());
        // which originally didn't have any
        Assert.assertEquals(0, targetOldA2.getNumContainers());
        // 1 user with 3GB
        Assert.assertEquals((3 * (GB)), origOldA1.getUsers().getUsersList().get(0).getResourcesUsed().getMemorySize());
        // 1 user with 1 core
        Assert.assertEquals(1, origOldA1.getUsers().getUsersList().get(0).getResourcesUsed().getvCores());
        // user ha no more running app in the orig queue
        Assert.assertEquals(0, origNewA1.getUsers().getUsersList().size());
        // 1 user with 3GB
        Assert.assertEquals((3 * (GB)), targetNewA2.getUsers().getUsersList().get(0).getResourcesUsed().getMemorySize());
        // 1 user with 1 core
        Assert.assertEquals(1, targetNewA2.getUsers().getUsersList().get(0).getResourcesUsed().getvCores());
        // Get allocations from the scheduler
        application_0.schedule();// task_0_0

        checkApplicationResourceUsage((3 * (GB)), application_0);
        application_1.schedule();// task_1_0

        checkApplicationResourceUsage((1 * (GB)), application_1);
        // task_1_0 (1G) application_0 moved to b2 with max running app 1 so it is
        // not scheduled
        checkNodeResourceUsage((4 * (GB)), nm_0);
        checkNodeResourceUsage((0 * (GB)), nm_1);
    }

    @Test
    public void testMoveAllApps() throws Exception {
        MockRM rm = setUpMove();
        AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        String queue = scheduler.getApplicationAttempt(appsInA1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("a1", queue);
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        List<ApplicationAttemptId> appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        List<ApplicationAttemptId> appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        // now move the app
        scheduler.moveAllApps("a1", "b1");
        // check postconditions
        Thread.sleep(1000);
        appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertEquals(1, appsInB1.size());
        queue = scheduler.getApplicationAttempt(appsInB1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("b1", queue);
        appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.contains(appAttemptId));
        Assert.assertEquals(1, appsInB.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertTrue(appsInA1.isEmpty());
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.isEmpty());
        stop();
    }

    @Test
    public void testMoveAllAppsInvalidDestination() throws Exception {
        MockRM rm = setUpMove();
        YarnScheduler scheduler = rm.getResourceScheduler();
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        List<ApplicationAttemptId> appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        List<ApplicationAttemptId> appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        // now move the app
        try {
            scheduler.moveAllApps("a1", "DOES_NOT_EXIST");
            Assert.fail();
        } catch (YarnException e) {
            // expected
        }
        // check postconditions, app should still be in a1
        appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        stop();
    }

    @Test
    public void testMoveAllAppsInvalidSource() throws Exception {
        MockRM rm = setUpMove();
        YarnScheduler scheduler = rm.getResourceScheduler();
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        List<ApplicationAttemptId> appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        List<ApplicationAttemptId> appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        // now move the app
        try {
            scheduler.moveAllApps("DOES_NOT_EXIST", "b1");
            Assert.fail();
        } catch (YarnException e) {
            // expected
        }
        // check postconditions, app should still be in a1
        appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        stop();
    }

    @Test(timeout = 60000)
    public void testMoveAttemptNotAdded() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(getCapacityConfiguration(conf));
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        ApplicationId appId = BuilderUtils.newApplicationId(100, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        RMAppAttemptMetrics attemptMetric = new RMAppAttemptMetrics(appAttemptId, getRMContext());
        RMAppImpl app = Mockito.mock(RMAppImpl.class);
        Mockito.when(app.getApplicationId()).thenReturn(appId);
        RMAppAttemptImpl attempt = Mockito.mock(RMAppAttemptImpl.class);
        Container container = Mockito.mock(Container.class);
        Mockito.when(attempt.getMasterContainer()).thenReturn(container);
        ApplicationSubmissionContext submissionContext = Mockito.mock(ApplicationSubmissionContext.class);
        Mockito.when(attempt.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt.getAppAttemptId()).thenReturn(appAttemptId);
        Mockito.when(attempt.getRMAppAttemptMetrics()).thenReturn(attemptMetric);
        Mockito.when(app.getCurrentAppAttempt()).thenReturn(attempt);
        getRMContext().getRMApps().put(appId, app);
        SchedulerEvent addAppEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId, "a1", "user");
        try {
            cs.moveApplication(appId, "b1");
            Assert.fail("Move should throw exception app not available");
        } catch (YarnException e) {
            Assert.assertEquals("App to be moved application_100_0001 not found.", e.getMessage());
        }
        cs.handle(addAppEvent);
        cs.moveApplication(appId, "b1");
        SchedulerEvent addAttemptEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId, false);
        cs.handle(addAttemptEvent);
        CSQueue rootQ = cs.getRootQueue();
        CSQueue queueB = cs.getQueue("b");
        CSQueue queueA = cs.getQueue("a");
        CSQueue queueA1 = cs.getQueue("a1");
        CSQueue queueB1 = cs.getQueue("b1");
        Assert.assertEquals(1, rootQ.getNumApplications());
        Assert.assertEquals(0, queueA.getNumApplications());
        Assert.assertEquals(1, queueB.getNumApplications());
        Assert.assertEquals(0, queueA1.getNumApplications());
        Assert.assertEquals(1, queueB1.getNumApplications());
        close();
    }

    @Test
    public void testRemoveAttemptMoveAdded() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, CapacityScheduler.class);
        conf.setInt(RM_AM_MAX_ATTEMPTS, 2);
        // Create Mock RM
        MockRM rm = new MockRM(getCapacityConfiguration(conf));
        CapacityScheduler sch = ((CapacityScheduler) (getResourceScheduler()));
        // add node
        Resource newResource = Resource.newInstance((4 * (GB)), 1);
        RMNode node = MockNodes.newNodeInfo(0, newResource, 1, "127.0.0.1");
        SchedulerEvent addNode = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node);
        sch.handle(addNode);
        // create appid
        ApplicationId appId = BuilderUtils.newApplicationId(100, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        RMAppAttemptMetrics attemptMetric = new RMAppAttemptMetrics(appAttemptId, getRMContext());
        RMAppImpl app = Mockito.mock(RMAppImpl.class);
        Mockito.when(app.getApplicationId()).thenReturn(appId);
        RMAppAttemptImpl attempt = Mockito.mock(RMAppAttemptImpl.class);
        Container container = Mockito.mock(Container.class);
        Mockito.when(attempt.getMasterContainer()).thenReturn(container);
        ApplicationSubmissionContext submissionContext = Mockito.mock(ApplicationSubmissionContext.class);
        Mockito.when(attempt.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt.getAppAttemptId()).thenReturn(appAttemptId);
        Mockito.when(attempt.getRMAppAttemptMetrics()).thenReturn(attemptMetric);
        Mockito.when(app.getCurrentAppAttempt()).thenReturn(attempt);
        getRMContext().getRMApps().put(appId, app);
        // Add application
        SchedulerEvent addAppEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId, "a1", "user");
        sch.handle(addAppEvent);
        // Add application attempt
        SchedulerEvent addAttemptEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId, false);
        sch.handle(addAttemptEvent);
        // get Queues
        CSQueue queueA1 = sch.getQueue("a1");
        CSQueue queueB = sch.getQueue("b");
        CSQueue queueB1 = sch.getQueue("b1");
        // add Running rm container and simulate live containers to a1
        ContainerId newContainerId = ContainerId.newContainerId(appAttemptId, 2);
        RMContainerImpl rmContainer = Mockito.mock(RMContainerImpl.class);
        Mockito.when(rmContainer.getState()).thenReturn(RUNNING);
        Container container2 = Mockito.mock(Container.class);
        Mockito.when(rmContainer.getContainer()).thenReturn(container2);
        Resource resource = Resource.newInstance(1024, 1);
        Mockito.when(container2.getResource()).thenReturn(resource);
        Mockito.when(rmContainer.getExecutionType()).thenReturn(GUARANTEED);
        Mockito.when(container2.getNodeId()).thenReturn(node.getNodeID());
        Mockito.when(container2.getId()).thenReturn(newContainerId);
        Mockito.when(rmContainer.getNodeLabelExpression()).thenReturn(NO_LABEL);
        Mockito.when(rmContainer.getContainerId()).thenReturn(newContainerId);
        sch.getApplicationAttempt(appAttemptId).getLiveContainersMap().put(newContainerId, rmContainer);
        QueueMetrics queueA1M = queueA1.getMetrics();
        queueA1M.incrPendingResources(rmContainer.getNodeLabelExpression(), "user1", 1, resource);
        queueA1M.allocateResources(rmContainer.getNodeLabelExpression(), "user1", resource);
        // remove attempt
        sch.handle(new AppAttemptRemovedSchedulerEvent(appAttemptId, RMAppAttemptState.KILLED, true));
        // Move application to queue b1
        sch.moveApplication(appId, "b1");
        // Check queue metrics after move
        Assert.assertEquals(0, queueA1.getNumApplications());
        Assert.assertEquals(1, queueB.getNumApplications());
        Assert.assertEquals(0, queueB1.getNumApplications());
        // Release attempt add event
        ApplicationAttemptId appAttemptId2 = BuilderUtils.newApplicationAttemptId(appId, 2);
        SchedulerEvent addAttemptEvent2 = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId2, true);
        sch.handle(addAttemptEvent2);
        // Check metrics after attempt added
        Assert.assertEquals(0, queueA1.getNumApplications());
        Assert.assertEquals(1, queueB.getNumApplications());
        Assert.assertEquals(1, queueB1.getNumApplications());
        QueueMetrics queueB1M = queueB1.getMetrics();
        QueueMetrics queueBM = queueB.getMetrics();
        // Verify allocation MB of current state
        Assert.assertEquals(0, queueA1M.getAllocatedMB());
        Assert.assertEquals(0, queueA1M.getAllocatedVirtualCores());
        Assert.assertEquals(1024, queueB1M.getAllocatedMB());
        Assert.assertEquals(1, queueB1M.getAllocatedVirtualCores());
        // remove attempt
        sch.handle(new AppAttemptRemovedSchedulerEvent(appAttemptId2, RMAppAttemptState.FINISHED, false));
        Assert.assertEquals(0, queueA1M.getAllocatedMB());
        Assert.assertEquals(0, queueA1M.getAllocatedVirtualCores());
        Assert.assertEquals(0, queueB1M.getAllocatedMB());
        Assert.assertEquals(0, queueB1M.getAllocatedVirtualCores());
        verifyQueueMetrics(queueB1M);
        verifyQueueMetrics(queueBM);
        // Verify queue A1 metrics
        verifyQueueMetrics(queueA1M);
        close();
    }

    @Test
    public void testKillAllAppsInQueue() throws Exception {
        MockRM rm = setUpMove();
        AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        String queue = scheduler.getApplicationAttempt(appsInA1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("a1", queue);
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        // now kill the app
        scheduler.killAllAppsInQueue("a1");
        // check postconditions
        rm.waitForState(app.getApplicationId(), KILLED);
        rm.waitForAppRemovedFromScheduler(app.getApplicationId());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.isEmpty());
        appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertTrue(appsInA1.isEmpty());
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.isEmpty());
        stop();
    }

    @Test
    public void testKillAllAppsInvalidSource() throws Exception {
        MockRM rm = setUpMove();
        YarnScheduler scheduler = rm.getResourceScheduler();
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        // now kill the app
        try {
            scheduler.killAllAppsInQueue("DOES_NOT_EXIST");
            Assert.fail();
        } catch (YarnException e) {
            // expected
        }
        // check postconditions, app should still be in a1
        appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        stop();
    }

    // Test to ensure that we don't carry out reservation on nodes
    // that have no CPU available when using the DominantResourceCalculator
    @Test(timeout = 30000)
    public void testAppReservationWithDominantResourceCalculator() throws Exception {
        CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration();
        csconf.setResourceComparator(DominantResourceCalculator.class);
        YarnConfiguration conf = new YarnConfiguration(csconf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("127.0.0.1:1234", (10 * (GB)), 1);
        // register extra nodes to bump up cluster resource
        MockNM nm2 = rm.registerNode("127.0.0.1:1235", (10 * (GB)), 4);
        rm.registerNode("127.0.0.1:1236", (10 * (GB)), 4);
        RMApp app1 = rm.submitApp(1024);
        // kick the scheduling
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
        MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
        am1.registerAppAttempt();
        SchedulerNodeReport report_nm1 = getResourceScheduler().getNodeReport(nm1.getNodeId());
        // check node report
        Assert.assertEquals((1 * (GB)), report_nm1.getUsedResource().getMemorySize());
        Assert.assertEquals((9 * (GB)), report_nm1.getAvailableResource().getMemorySize());
        // add request for containers
        am1.addRequests(new String[]{ "127.0.0.1", "127.0.0.2" }, (1 * (GB)), 1, 1);
        am1.schedule();// send the request

        // kick the scheduler, container reservation should not happen
        nm1.nodeHeartbeat(true);
        Thread.sleep(1000);
        AllocateResponse allocResponse = am1.schedule();
        ApplicationResourceUsageReport report = getResourceScheduler().getAppResourceUsageReport(attempt1.getAppAttemptId());
        Assert.assertEquals(0, allocResponse.getAllocatedContainers().size());
        Assert.assertEquals(0, report.getNumReservedContainers());
        // container should get allocated on this node
        nm2.nodeHeartbeat(true);
        while ((allocResponse.getAllocatedContainers().size()) == 0) {
            Thread.sleep(100);
            allocResponse = am1.schedule();
        } 
        report = getResourceScheduler().getAppResourceUsageReport(attempt1.getAppAttemptId());
        Assert.assertEquals(1, allocResponse.getAllocatedContainers().size());
        Assert.assertEquals(0, report.getNumReservedContainers());
        stop();
    }

    @Test
    public void testPreemptionDisabled() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, true);
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null);
        setupQueueConfiguration(conf);
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, rmContext);
        CSQueue rootQueue = cs.getRootQueue();
        CSQueue queueB = findQueue(rootQueue, CapacitySchedulerTestBase.B);
        CSQueue queueB2 = findQueue(queueB, CapacitySchedulerTestBase.B2);
        // When preemption turned on for the whole system
        // (yarn.resourcemanager.scheduler.monitor.enable=true), and with no other
        // preemption properties set, queue root.b.b2 should be preemptable.
        Assert.assertFalse((("queue " + (CapacitySchedulerTestBase.B2)) + " should default to preemptable"), queueB2.getPreemptionDisabled());
        // Disable preemption at the root queue level.
        // The preemption property should be inherited from root all the
        // way down so that root.b.b2 should NOT be preemptable.
        conf.setPreemptionDisabled(rootQueue.getQueuePath(), true);
        cs.reinitialize(conf, rmContext);
        Assert.assertTrue((("queue " + (CapacitySchedulerTestBase.B2)) + " should have inherited non-preemptability from root"), queueB2.getPreemptionDisabled());
        // Enable preemption for root (grandparent) but disable for root.b (parent).
        // root.b.b2 should inherit property from parent and NOT be preemptable
        conf.setPreemptionDisabled(rootQueue.getQueuePath(), false);
        conf.setPreemptionDisabled(queueB.getQueuePath(), true);
        cs.reinitialize(conf, rmContext);
        Assert.assertTrue((("queue " + (CapacitySchedulerTestBase.B2)) + " should have inherited non-preemptability from parent"), queueB2.getPreemptionDisabled());
        // When preemption is turned on for root.b.b2, it should be preemptable
        // even though preemption is disabled on root.b (parent).
        conf.setPreemptionDisabled(queueB2.getQueuePath(), false);
        cs.reinitialize(conf, rmContext);
        Assert.assertFalse((("queue " + (CapacitySchedulerTestBase.B2)) + " should have been preemptable"), queueB2.getPreemptionDisabled());
    }

    @Test
    public void testRefreshQueuesMaxAllocationRefresh() throws Exception {
        // queue refresh should not allow changing the maximum allocation setting
        // per queue to be smaller than previous setting
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, mockContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        Assert.assertEquals("max allocation in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max allocation for A1", Resources.none(), conf.getQueueMaximumAllocation(CapacitySchedulerTestBase.A1));
        Assert.assertEquals("max allocation", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, ResourceUtils.fetchMaximumAllocationFromConfig(conf).getMemorySize());
        CSQueue rootQueue = cs.getRootQueue();
        CSQueue queueA = findQueue(rootQueue, CapacitySchedulerTestBase.A);
        CSQueue queueA1 = findQueue(queueA, CapacitySchedulerTestBase.A1);
        Assert.assertEquals("queue max allocation", getMaximumAllocation().getMemorySize(), 8192);
        setMaxAllocMb(conf, CapacitySchedulerTestBase.A1, 4096);
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("should have thrown exception");
        } catch (IOException e) {
            Assert.assertTrue("max allocation exception", e.getCause().toString().contains("not be decreased"));
        }
        setMaxAllocMb(conf, CapacitySchedulerTestBase.A1, 8192);
        cs.reinitialize(conf, mockContext);
        setMaxAllocVcores(conf, CapacitySchedulerTestBase.A1, ((YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES) - 1));
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("should have thrown exception");
        } catch (IOException e) {
            Assert.assertTrue("max allocation exception", e.getCause().toString().contains("not be decreased"));
        }
    }

    @Test
    public void testRefreshQueuesMaxAllocationPerQueueLarge() throws Exception {
        // verify we can't set the allocation per queue larger then cluster setting
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        cs.init(conf);
        cs.start();
        // change max allocation for B3 queue to be larger then cluster max
        setMaxAllocMb(conf, CapacitySchedulerTestBase.B3, ((YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB) + 2048));
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("should have thrown exception");
        } catch (IOException e) {
            Assert.assertTrue("maximum allocation exception", e.getCause().getMessage().contains("maximum allocation"));
        }
        setMaxAllocMb(conf, CapacitySchedulerTestBase.B3, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB);
        cs.reinitialize(conf, mockContext);
        setMaxAllocVcores(conf, CapacitySchedulerTestBase.B3, ((YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES) + 1));
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("should have thrown exception");
        } catch (IOException e) {
            Assert.assertTrue("maximum allocation exception", e.getCause().getMessage().contains("maximum allocation"));
        }
    }

    @Test
    public void testRefreshQueuesMaxAllocationRefreshLarger() throws Exception {
        // queue refresh should allow max allocation per queue to go larger
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        setMaxAllocMb(conf, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB);
        setMaxAllocVcores(conf, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
        setMaxAllocMb(conf, CapacitySchedulerTestBase.A1, 4096);
        setMaxAllocVcores(conf, CapacitySchedulerTestBase.A1, 2);
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, mockContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        CSQueue rootQueue = cs.getRootQueue();
        CSQueue queueA = findQueue(rootQueue, CapacitySchedulerTestBase.A);
        CSQueue queueA1 = findQueue(queueA, CapacitySchedulerTestBase.A1);
        Assert.assertEquals("max capability MB in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max capability vcores in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getVirtualCores());
        Assert.assertEquals("max allocation MB A1", 4096, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores A1", 2, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("cluster max allocation MB", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, ResourceUtils.fetchMaximumAllocationFromConfig(conf).getMemorySize());
        Assert.assertEquals("cluster max allocation vcores", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, ResourceUtils.fetchMaximumAllocationFromConfig(conf).getVirtualCores());
        Assert.assertEquals("queue max allocation", 4096, queueA1.getMaximumAllocation().getMemorySize());
        setMaxAllocMb(conf, CapacitySchedulerTestBase.A1, 6144);
        setMaxAllocVcores(conf, CapacitySchedulerTestBase.A1, 3);
        cs.reinitialize(conf, null);
        // conf will have changed but we shouldn't be able to change max allocation
        // for the actual queue
        Assert.assertEquals("max allocation MB A1", 6144, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores A1", 3, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("max allocation MB cluster", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, ResourceUtils.fetchMaximumAllocationFromConfig(conf).getMemorySize());
        Assert.assertEquals("max allocation vcores cluster", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, ResourceUtils.fetchMaximumAllocationFromConfig(conf).getVirtualCores());
        Assert.assertEquals("queue max allocation MB", 6144, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("queue max allocation vcores", 3, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("max capability MB cluster", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("cluster max capability vcores", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getVirtualCores());
    }

    @Test
    public void testRefreshQueuesMaxAllocationCSError() throws Exception {
        // Try to refresh the cluster level max allocation size to be smaller
        // and it should error out
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        setMaxAllocMb(conf, 10240);
        setMaxAllocVcores(conf, 10);
        setMaxAllocMb(conf, CapacitySchedulerTestBase.A1, 4096);
        setMaxAllocVcores(conf, CapacitySchedulerTestBase.A1, 4);
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, mockContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        Assert.assertEquals("max allocation MB in CS", 10240, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max allocation vcores in CS", 10, cs.getMaximumResourceCapability().getVirtualCores());
        setMaxAllocMb(conf, 6144);
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("should have thrown exception");
        } catch (IOException e) {
            Assert.assertTrue("max allocation exception", e.getCause().toString().contains("not be decreased"));
        }
        setMaxAllocMb(conf, 10240);
        cs.reinitialize(conf, mockContext);
        setMaxAllocVcores(conf, 8);
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("should have thrown exception");
        } catch (IOException e) {
            Assert.assertTrue("max allocation exception", e.getCause().toString().contains("not be decreased"));
        }
    }

    @Test
    public void testRefreshQueuesMaxAllocationCSLarger() throws Exception {
        // Try to refresh the cluster level max allocation size to be larger
        // and verify that if there is no setting per queue it uses the
        // cluster level setting.
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        setMaxAllocMb(conf, 10240);
        setMaxAllocVcores(conf, 10);
        setMaxAllocMb(conf, CapacitySchedulerTestBase.A1, 4096);
        setMaxAllocVcores(conf, CapacitySchedulerTestBase.A1, 4);
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, mockContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        Assert.assertEquals("max allocation MB in CS", 10240, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max allocation vcores in CS", 10, cs.getMaximumResourceCapability().getVirtualCores());
        CSQueue rootQueue = cs.getRootQueue();
        CSQueue queueA = findQueue(rootQueue, CapacitySchedulerTestBase.A);
        CSQueue queueB = findQueue(rootQueue, CapacitySchedulerTestBase.B);
        CSQueue queueA1 = findQueue(queueA, CapacitySchedulerTestBase.A1);
        CSQueue queueA2 = findQueue(queueA, CapacitySchedulerTestBase.A2);
        CSQueue queueB2 = findQueue(queueB, CapacitySchedulerTestBase.B2);
        Assert.assertEquals("queue A1 max allocation MB", 4096, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("queue A1 max allocation vcores", 4, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("queue A2 max allocation MB", 10240, queueA2.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("queue A2 max allocation vcores", 10, queueA2.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("queue B2 max allocation MB", 10240, queueB2.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("queue B2 max allocation vcores", 10, queueB2.getMaximumAllocation().getVirtualCores());
        setMaxAllocMb(conf, 12288);
        setMaxAllocVcores(conf, 12);
        cs.reinitialize(conf, null);
        // cluster level setting should change and any queues without
        // per queue setting
        Assert.assertEquals("max allocation MB in CS", 12288, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max allocation vcores in CS", 12, cs.getMaximumResourceCapability().getVirtualCores());
        Assert.assertEquals("queue A1 max MB allocation", 4096, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("queue A1 max vcores allocation", 4, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("queue A2 max MB allocation", 12288, queueA2.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("queue A2 max vcores allocation", 12, queueA2.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("queue B2 max MB allocation", 12288, queueB2.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("queue B2 max vcores allocation", 12, queueB2.getMaximumAllocation().getVirtualCores());
    }

    @Test
    public void testQueuesMaxAllocationInheritance() throws Exception {
        // queue level max allocation is set by the queue configuration explicitly
        // or inherits from the parent.
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        setMaxAllocMb(conf, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB);
        setMaxAllocVcores(conf, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
        // Test the child queue overrides
        setMaxAllocation(conf, ROOT, "memory-mb=4096,vcores=2");
        setMaxAllocation(conf, CapacitySchedulerTestBase.A1, "memory-mb=6144,vcores=2");
        setMaxAllocation(conf, CapacitySchedulerTestBase.B, "memory-mb=5120, vcores=2");
        setMaxAllocation(conf, CapacitySchedulerTestBase.B2, "memory-mb=1024, vcores=2");
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, mockContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        CSQueue rootQueue = cs.getRootQueue();
        CSQueue queueA = findQueue(rootQueue, CapacitySchedulerTestBase.A);
        CSQueue queueB = findQueue(rootQueue, CapacitySchedulerTestBase.B);
        CSQueue queueA1 = findQueue(queueA, CapacitySchedulerTestBase.A1);
        CSQueue queueA2 = findQueue(queueA, CapacitySchedulerTestBase.A2);
        CSQueue queueB1 = findQueue(queueB, CapacitySchedulerTestBase.B1);
        CSQueue queueB2 = findQueue(queueB, CapacitySchedulerTestBase.B2);
        Assert.assertEquals("max capability MB in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max capability vcores in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getVirtualCores());
        Assert.assertEquals("max allocation MB A1", 6144, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores A1", 2, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("max allocation MB A2", 4096, queueA2.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores A2", 2, queueA2.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("max allocation MB B", 5120, queueB.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation MB B1", 5120, queueB1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation MB B2", 1024, queueB2.getMaximumAllocation().getMemorySize());
        // Test get the max-allocation from different parent
        unsetMaxAllocation(conf, CapacitySchedulerTestBase.A1);
        unsetMaxAllocation(conf, CapacitySchedulerTestBase.B);
        unsetMaxAllocation(conf, CapacitySchedulerTestBase.B1);
        setMaxAllocation(conf, ROOT, "memory-mb=6144,vcores=2");
        setMaxAllocation(conf, CapacitySchedulerTestBase.A, "memory-mb=8192,vcores=2");
        cs.reinitialize(conf, mockContext);
        Assert.assertEquals("max capability MB in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max capability vcores in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getVirtualCores());
        Assert.assertEquals("max allocation MB A1", 8192, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores A1", 2, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("max allocation MB B1", 6144, queueB1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores B1", 2, queueB1.getMaximumAllocation().getVirtualCores());
        // Test the default
        unsetMaxAllocation(conf, ROOT);
        unsetMaxAllocation(conf, CapacitySchedulerTestBase.A);
        unsetMaxAllocation(conf, CapacitySchedulerTestBase.A1);
        cs.reinitialize(conf, mockContext);
        Assert.assertEquals("max capability MB in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getMemorySize());
        Assert.assertEquals("max capability vcores in CS", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getVirtualCores());
        Assert.assertEquals("max allocation MB A1", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, queueA1.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores A1", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, queueA1.getMaximumAllocation().getVirtualCores());
        Assert.assertEquals("max allocation MB A2", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, queueA2.getMaximumAllocation().getMemorySize());
        Assert.assertEquals("max allocation vcores A2", DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, queueA2.getMaximumAllocation().getVirtualCores());
    }

    @Test
    public void testVerifyQueuesMaxAllocationConf() throws Exception {
        // queue level max allocation can't exceed the cluster setting
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        setMaxAllocMb(conf, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB);
        setMaxAllocVcores(conf, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
        long largerMem = (YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB) + 1024;
        long largerVcores = (YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES) + 10;
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, mockContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        setMaxAllocation(conf, ROOT, (("memory-mb=" + largerMem) + ",vcores=2"));
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("Queue Root maximum allocation can't exceed the cluster setting");
        } catch (Exception e) {
            Assert.assertTrue("maximum allocation exception", e.getCause().getMessage().contains("maximum allocation"));
        }
        setMaxAllocation(conf, ROOT, "memory-mb=4096,vcores=2");
        setMaxAllocation(conf, CapacitySchedulerTestBase.A, "memory-mb=6144,vcores=2");
        setMaxAllocation(conf, CapacitySchedulerTestBase.A1, (("memory-mb=" + largerMem) + ",vcores=2"));
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("Queue A1 maximum allocation can't exceed the cluster setting");
        } catch (Exception e) {
            Assert.assertTrue("maximum allocation exception", e.getCause().getMessage().contains("maximum allocation"));
        }
        setMaxAllocation(conf, CapacitySchedulerTestBase.A1, (("memory-mb=8192" + ",vcores=") + largerVcores));
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail("Queue A1 maximum allocation can't exceed the cluster setting");
        } catch (Exception e) {
            Assert.assertTrue("maximum allocation exception", e.getCause().getMessage().contains("maximum allocation"));
        }
    }

    @Test
    public void testSchedulerKeyGarbageCollection() throws Exception {
        YarnConfiguration conf = new YarnConfiguration(new CapacitySchedulerConfiguration());
        conf.setBoolean(ENABLE_USER_METRICS, true);
        MockRM rm = new MockRM(conf);
        start();
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h3:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h4:1234", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        RMApp app1 = rm.submitApp((1 * (GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId = app1.getCurrentAppAttempt().getAppAttemptId();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2);
        ResourceScheduler scheduler = rm.getResourceScheduler();
        // All nodes 1 - 4 will be applicable for scheduling.
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        nm3.nodeHeartbeat(true);
        nm4.nodeHeartbeat(true);
        Thread.sleep(1000);
        AllocateResponse allocateResponse = am1.allocate(Arrays.asList(TestCapacityScheduler.newResourceRequest(1, 1, ANY, Resources.createResource((3 * (GB))), 1, true, GUARANTEED), TestCapacityScheduler.newResourceRequest(2, 2, ANY, Resources.createResource((3 * (GB))), 1, true, GUARANTEED), TestCapacityScheduler.newResourceRequest(3, 3, ANY, Resources.createResource((3 * (GB))), 1, true, GUARANTEED), TestCapacityScheduler.newResourceRequest(4, 4, ANY, Resources.createResource((3 * (GB))), 1, true, GUARANTEED)), null);
        List<Container> allocatedContainers = allocateResponse.getAllocatedContainers();
        Assert.assertEquals(0, allocatedContainers.size());
        Collection<SchedulerRequestKey> schedulerKeys = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getAppSchedulingInfo().getSchedulerKeys();
        Assert.assertEquals(4, schedulerKeys.size());
        // Get a Node to HB... at which point 1 container should be
        // allocated
        nm1.nodeHeartbeat(true);
        Thread.sleep(200);
        allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        allocatedContainers = allocateResponse.getAllocatedContainers();
        Assert.assertEquals(1, allocatedContainers.size());
        // Verify 1 outstanding schedulerKey is removed
        Assert.assertEquals(3, schedulerKeys.size());
        List<ResourceRequest> resReqs = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getAppSchedulingInfo().getAllResourceRequests();
        // Verify 1 outstanding schedulerKey is removed from the
        // rrMap as well
        Assert.assertEquals(3, resReqs.size());
        // Verify One more container Allocation on node nm2
        // And ensure the outstanding schedulerKeys go down..
        nm2.nodeHeartbeat(true);
        Thread.sleep(200);
        // Update the allocateReq to send 0 numContainer req.
        // For the satisfied container...
        allocateResponse = am1.allocate(Arrays.asList(TestCapacityScheduler.newResourceRequest(1, allocatedContainers.get(0).getAllocationRequestId(), ANY, Resources.createResource((3 * (GB))), 0, true, GUARANTEED)), new ArrayList());
        allocatedContainers = allocateResponse.getAllocatedContainers();
        Assert.assertEquals(1, allocatedContainers.size());
        // Verify 1 outstanding schedulerKey is removed
        Assert.assertEquals(2, schedulerKeys.size());
        resReqs = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getAppSchedulingInfo().getAllResourceRequests();
        // Verify the map size is not increased due to 0 req
        Assert.assertEquals(2, resReqs.size());
        // Now Verify that the AM can cancel 1 Ask:
        SchedulerRequestKey sk = schedulerKeys.iterator().next();
        am1.allocate(Arrays.asList(TestCapacityScheduler.newResourceRequest(sk.getPriority().getPriority(), sk.getAllocationRequestId(), ANY, Resources.createResource((3 * (GB))), 0, true, GUARANTEED)), null);
        schedulerKeys = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getAppSchedulingInfo().getSchedulerKeys();
        Thread.sleep(200);
        // Verify 1 outstanding schedulerKey is removed because of the
        // cancel ask
        Assert.assertEquals(1, schedulerKeys.size());
        // Now verify that after the next node heartbeat, we allocate
        // the last schedulerKey
        nm3.nodeHeartbeat(true);
        Thread.sleep(200);
        allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        allocatedContainers = allocateResponse.getAllocatedContainers();
        Assert.assertEquals(1, allocatedContainers.size());
        // Verify no more outstanding schedulerKeys..
        Assert.assertEquals(0, schedulerKeys.size());
        resReqs = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getAppSchedulingInfo().getAllResourceRequests();
        Assert.assertEquals(0, resReqs.size());
    }

    @Test
    public void testHierarchyQueuesCurrentLimits() throws Exception {
        /* Queue tree:
                 Root
               /     \
              A       B
             / \    / | \
            A1 A2  B1 B2 B3
         */
        YarnConfiguration conf = new YarnConfiguration(setupQueueConfiguration(new CapacitySchedulerConfiguration()));
        conf.setBoolean(ENABLE_USER_METRICS, true);
        MockRM rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", (100 * (GB)), getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp((1 * (GB)), "app", "user", null, "b1");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        waitContainerAllocated(am1, (1 * (GB)), 1, 2, rm1, nm1);
        // Maximum resoure of b1 is 100 * 0.895 * 0.792 = 71 GB
        // 2 GBs used by am, so it's 71 - 2 = 69G.
        Assert.assertEquals((69 * (GB)), am1.doHeartbeat().getAvailableResources().getMemorySize());
        RMApp app2 = rm1.submitApp((1 * (GB)), "app", "user", null, "b2");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm1);
        // Allocate 5 containers, each one is 8 GB in am2 (40 GB in total)
        waitContainerAllocated(am2, (8 * (GB)), 5, 2, rm1, nm1);
        // Allocated one more container with 1 GB resource in b1
        waitContainerAllocated(am1, (1 * (GB)), 1, 3, rm1, nm1);
        // Total is 100 GB,
        // B2 uses 41 GB (5 * 8GB containers and 1 AM container)
        // B1 uses 3 GB (2 * 1GB containers and 1 AM container)
        // Available is 100 - 41 - 3 = 56 GB
        Assert.assertEquals((56 * (GB)), am1.doHeartbeat().getAvailableResources().getMemorySize());
        // Now we submit app3 to a1 (in higher level hierarchy), to see if headroom
        // of app1 (in queue b1) updated correctly
        RMApp app3 = rm1.submitApp((1 * (GB)), "app", "user", null, "a1");
        MockAM am3 = MockRM.launchAndRegisterAM(app3, rm1, nm1);
        // Allocate 3 containers, each one is 8 GB in am3 (24 GB in total)
        waitContainerAllocated(am3, (8 * (GB)), 3, 2, rm1, nm1);
        // Allocated one more container with 4 GB resource in b1
        waitContainerAllocated(am1, (1 * (GB)), 1, 4, rm1, nm1);
        // Total is 100 GB,
        // B2 uses 41 GB (5 * 8GB containers and 1 AM container)
        // B1 uses 4 GB (3 * 1GB containers and 1 AM container)
        // A1 uses 25 GB (3 * 8GB containers and 1 AM container)
        // Available is 100 - 41 - 4 - 25 = 30 GB
        Assert.assertEquals((30 * (GB)), am1.doHeartbeat().getAvailableResources().getMemorySize());
    }

    @Test
    public void testParentQueueMaxCapsAreRespected() throws Exception {
        /* Queue tree:
                 Root
               /     \
              A       B
             / \
            A1 A2
         */
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setQueues(ROOT, new String[]{ "a", "b" });
        csConf.setCapacity(CapacitySchedulerTestBase.A, 50);
        csConf.setMaximumCapacity(CapacitySchedulerTestBase.A, 50);
        csConf.setCapacity(CapacitySchedulerTestBase.B, 50);
        // Define 2nd-level queues
        csConf.setQueues(CapacitySchedulerTestBase.A, new String[]{ "a1", "a2" });
        csConf.setCapacity(CapacitySchedulerTestBase.A1, 50);
        csConf.setUserLimitFactor(CapacitySchedulerTestBase.A1, 100.0F);
        csConf.setCapacity(CapacitySchedulerTestBase.A2, 50);
        csConf.setUserLimitFactor(CapacitySchedulerTestBase.A2, 100.0F);
        csConf.setCapacity(CapacitySchedulerTestBase.B1, CapacitySchedulerTestBase.B1_CAPACITY);
        csConf.setUserLimitFactor(CapacitySchedulerTestBase.B1, 100.0F);
        YarnConfiguration conf = new YarnConfiguration(csConf);
        conf.setBoolean(ENABLE_USER_METRICS, true);
        MockRM rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", (24 * (GB)), getResourceTrackerService());
        nm1.registerNode();
        // Launch app1 in a1, resource usage is 1GB (am) + 4GB * 2 = 9GB
        RMApp app1 = rm1.submitApp((1 * (GB)), "app", "user", null, "a1");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        waitContainerAllocated(am1, (4 * (GB)), 2, 2, rm1, nm1);
        // Try to launch app2 in a2, asked 2GB, should success
        RMApp app2 = rm1.submitApp((2 * (GB)), "app", "user", null, "a2");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm1);
        try {
            // Try to allocate a container, a's usage=11G/max=12
            // a1's usage=9G/max=12
            // a2's usage=2G/max=12
            // In this case, if a2 asked 2G, should fail.
            waitContainerAllocated(am2, (2 * (GB)), 1, 2, rm1, nm1);
        } catch (AssertionError failure) {
            // Expected, return;
            return;
        }
        Assert.fail(("Shouldn't successfully allocate containers for am2, " + "queue-a's max capacity will be violated if container allocated"));
    }

    @Test
    public void testQueueHierarchyPendingResourceUpdate() throws Exception {
        Configuration conf = TestUtils.getConfigurationWithQueueLabels(new Configuration(false));
        conf.setBoolean(NODE_LABELS_ENABLED, true);
        final RMNodeLabelsManager mgr = new NullRMNodeLabelsManager();
        mgr.init(conf);
        mgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        mgr.addLabelsToNode(ImmutableMap.of(NodeId.newInstance("h1", 0), toSet("x")));
        MockRM rm = new MockRM(conf) {
            protected RMNodeLabelsManager createNodeLabelManager() {
                return mgr;
            }
        };
        start();
        // label = x
        MockNM nm1 = new MockNM("h1:1234", (200 * (GB)), getResourceTrackerService());
        nm1.registerNode();
        // label = ""
        MockNM nm2 = new MockNM("h2:1234", (200 * (GB)), getResourceTrackerService());
        nm2.registerNode();
        // Launch app1 in queue=a1
        RMApp app1 = rm.submitApp((1 * (GB)), "app", "user", null, "a1");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2);
        // Launch app2 in queue=b1
        RMApp app2 = rm.submitApp((8 * (GB)), "app", "user", null, "b1");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm, nm2);
        // am1 asks for 8 * 1GB container for no label
        am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (GB))), 8)), null);
        checkPendingResource(rm, "a1", (8 * (GB)), null);
        checkPendingResource(rm, "a", (8 * (GB)), null);
        checkPendingResource(rm, "root", (8 * (GB)), null);
        // am2 asks for 8 * 1GB container for no label
        am2.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (GB))), 8)), null);
        checkPendingResource(rm, "a1", (8 * (GB)), null);
        checkPendingResource(rm, "a", (8 * (GB)), null);
        checkPendingResource(rm, "b1", (8 * (GB)), null);
        checkPendingResource(rm, "b", (8 * (GB)), null);
        // root = a + b
        checkPendingResource(rm, "root", (16 * (GB)), null);
        // am2 asks for 8 * 1GB container in another priority for no label
        am2.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(2), "*", Resources.createResource((1 * (GB))), 8)), null);
        checkPendingResource(rm, "a1", (8 * (GB)), null);
        checkPendingResource(rm, "a", (8 * (GB)), null);
        checkPendingResource(rm, "b1", (16 * (GB)), null);
        checkPendingResource(rm, "b", (16 * (GB)), null);
        // root = a + b
        checkPendingResource(rm, "root", (24 * (GB)), null);
        // am1 asks 4 GB resource instead of 8 * GB for priority=1
        am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((4 * (GB))), 1)), null);
        checkPendingResource(rm, "a1", (4 * (GB)), null);
        checkPendingResource(rm, "a", (4 * (GB)), null);
        checkPendingResource(rm, "b1", (16 * (GB)), null);
        checkPendingResource(rm, "b", (16 * (GB)), null);
        // root = a + b
        checkPendingResource(rm, "root", (20 * (GB)), null);
        // am1 asks 8 * GB resource which label=x
        am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(2), "*", Resources.createResource((8 * (GB))), 1, true, "x")), null);
        checkPendingResource(rm, "a1", (4 * (GB)), null);
        checkPendingResource(rm, "a", (4 * (GB)), null);
        checkPendingResource(rm, "a1", (8 * (GB)), "x");
        checkPendingResource(rm, "a", (8 * (GB)), "x");
        checkPendingResource(rm, "b1", (16 * (GB)), null);
        checkPendingResource(rm, "b", (16 * (GB)), null);
        // root = a + b
        checkPendingResource(rm, "root", (20 * (GB)), null);
        checkPendingResource(rm, "root", (8 * (GB)), "x");
        // some containers allocated for am1, pending resource should decrease
        ContainerId containerId = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        Assert.assertTrue(rm.waitForState(nm1, containerId, ALLOCATED));
        containerId = ContainerId.newContainerId(am1.getApplicationAttemptId(), 3);
        Assert.assertTrue(rm.waitForState(nm2, containerId, ALLOCATED));
        checkPendingResource(rm, "a1", (0 * (GB)), null);
        checkPendingResource(rm, "a", (0 * (GB)), null);
        checkPendingResource(rm, "a1", (0 * (GB)), "x");
        checkPendingResource(rm, "a", (0 * (GB)), "x");
        // some containers could be allocated for am2 when we allocating containers
        // for am1, just check if pending resource of b1/b/root > 0
        checkPendingResourceGreaterThanZero(rm, "b1", null);
        checkPendingResourceGreaterThanZero(rm, "b", null);
        // root = a + b
        checkPendingResourceGreaterThanZero(rm, "root", null);
        checkPendingResource(rm, "root", (0 * (GB)), "x");
        // complete am2, pending resource should be 0 now
        AppAttemptRemovedSchedulerEvent appRemovedEvent = new AppAttemptRemovedSchedulerEvent(am2.getApplicationAttemptId(), RMAppAttemptState.FINISHED, false);
        getResourceScheduler().handle(appRemovedEvent);
        checkPendingResource(rm, "a1", (0 * (GB)), null);
        checkPendingResource(rm, "a", (0 * (GB)), null);
        checkPendingResource(rm, "a1", (0 * (GB)), "x");
        checkPendingResource(rm, "a", (0 * (GB)), "x");
        checkPendingResource(rm, "b1", (0 * (GB)), null);
        checkPendingResource(rm, "b", (0 * (GB)), null);
        checkPendingResource(rm, "root", (0 * (GB)), null);
        checkPendingResource(rm, "root", (0 * (GB)), "x");
    }

    // Test verifies AM Used resource for LeafQueue when AM ResourceRequest is
    // lesser than minimumAllocation
    @Test(timeout = 30000)
    public void testAMUsedResource() throws Exception {
        MockRM rm = setUpMove();
        rm.registerNode("127.0.0.1:1234", (4 * (GB)));
        Configuration conf = rm.getConfig();
        int minAllocMb = conf.getInt(RM_SCHEDULER_MINIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB);
        int amMemory = 50;
        Assert.assertTrue("AM memory is greater than or equal to minAllocation", (amMemory < minAllocMb));
        Resource minAllocResource = Resource.newInstance(minAllocMb, 1);
        String queueName = "a1";
        RMApp rmApp = rm.submitApp(amMemory, "app-1", "user_0", null, queueName);
        Assert.assertEquals("RMApp does not containes minimum allocation", minAllocResource, rmApp.getAMResourceRequests().get(0).getCapability());
        ResourceScheduler scheduler = getRMContext().getScheduler();
        LeafQueue queueA = ((LeafQueue) (getQueue(queueName)));
        Assert.assertEquals("Minimum Resource for AM is incorrect", minAllocResource, queueA.getUser("user_0").getResourceUsage().getAMUsed());
        stop();
    }

    // Verifies headroom passed to ApplicationMaster has been updated in
    // RMAppAttemptMetrics
    @Test
    public void testApplicationHeadRoom() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        ApplicationId appId = BuilderUtils.newApplicationId(100, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        RMAppAttemptMetrics attemptMetric = new RMAppAttemptMetrics(appAttemptId, getRMContext());
        RMAppImpl app = Mockito.mock(RMAppImpl.class);
        Mockito.when(app.getApplicationId()).thenReturn(appId);
        RMAppAttemptImpl attempt = Mockito.mock(RMAppAttemptImpl.class);
        Container container = Mockito.mock(Container.class);
        Mockito.when(attempt.getMasterContainer()).thenReturn(container);
        ApplicationSubmissionContext submissionContext = Mockito.mock(ApplicationSubmissionContext.class);
        Mockito.when(attempt.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt.getAppAttemptId()).thenReturn(appAttemptId);
        Mockito.when(attempt.getRMAppAttemptMetrics()).thenReturn(attemptMetric);
        Mockito.when(app.getCurrentAppAttempt()).thenReturn(attempt);
        getRMContext().getRMApps().put(appId, app);
        SchedulerEvent addAppEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId, "default", "user");
        cs.handle(addAppEvent);
        SchedulerEvent addAttemptEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId, false);
        cs.handle(addAttemptEvent);
        Allocation allocate = cs.allocate(appAttemptId, Collections.<ResourceRequest>emptyList(), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        Assert.assertNotNull(attempt);
        Assert.assertEquals(Resource.newInstance(0, 0), allocate.getResourceLimit());
        Assert.assertEquals(Resource.newInstance(0, 0), attemptMetric.getApplicationAttemptHeadroom());
        // Add a node to cluster
        Resource newResource = Resource.newInstance((4 * (GB)), 1);
        RMNode node = MockNodes.newNodeInfo(0, newResource, 1, "127.0.0.1");
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
        allocate = cs.allocate(appAttemptId, Collections.<ResourceRequest>emptyList(), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        // All resources should be sent as headroom
        Assert.assertEquals(newResource, allocate.getResourceLimit());
        Assert.assertEquals(newResource, attemptMetric.getApplicationAttemptHeadroom());
        stop();
    }

    @Test
    public void testHeadRoomCalculationWithDRC() throws Exception {
        // test with total cluster resource of 20GB memory and 20 vcores.
        // the queue where two apps running has user limit 0.8
        // allocate 10GB memory and 1 vcore to app 1.
        // app 1 should have headroom
        // 20GB*0.8 - 10GB = 6GB memory available and 15 vcores.
        // allocate 1GB memory and 1 vcore to app2.
        // app 2 should have headroom 20GB - 10 - 1 = 1GB memory,
        // and 20*0.8 - 1 = 15 vcores.
        CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration();
        csconf.setResourceComparator(DominantResourceCalculator.class);
        YarnConfiguration conf = new YarnConfiguration(csconf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        LeafQueue qb = ((LeafQueue) (cs.getQueue("default")));
        qb.setUserLimitFactor(((float) (0.8)));
        // add app 1
        ApplicationId appId = BuilderUtils.newApplicationId(100, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        RMAppAttemptMetrics attemptMetric = new RMAppAttemptMetrics(appAttemptId, getRMContext());
        RMAppImpl app = Mockito.mock(RMAppImpl.class);
        Mockito.when(app.getApplicationId()).thenReturn(appId);
        RMAppAttemptImpl attempt = Mockito.mock(RMAppAttemptImpl.class);
        Container container = Mockito.mock(Container.class);
        Mockito.when(attempt.getMasterContainer()).thenReturn(container);
        ApplicationSubmissionContext submissionContext = Mockito.mock(ApplicationSubmissionContext.class);
        Mockito.when(attempt.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt.getAppAttemptId()).thenReturn(appAttemptId);
        Mockito.when(attempt.getRMAppAttemptMetrics()).thenReturn(attemptMetric);
        Mockito.when(app.getCurrentAppAttempt()).thenReturn(attempt);
        getRMContext().getRMApps().put(appId, app);
        SchedulerEvent addAppEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId, "default", "user1");
        cs.handle(addAppEvent);
        SchedulerEvent addAttemptEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId, false);
        cs.handle(addAttemptEvent);
        // add app 2
        ApplicationId appId2 = BuilderUtils.newApplicationId(100, 2);
        ApplicationAttemptId appAttemptId2 = BuilderUtils.newApplicationAttemptId(appId2, 1);
        RMAppAttemptMetrics attemptMetric2 = new RMAppAttemptMetrics(appAttemptId2, getRMContext());
        RMAppImpl app2 = Mockito.mock(RMAppImpl.class);
        Mockito.when(app2.getApplicationId()).thenReturn(appId2);
        RMAppAttemptImpl attempt2 = Mockito.mock(RMAppAttemptImpl.class);
        Mockito.when(attempt2.getMasterContainer()).thenReturn(container);
        Mockito.when(attempt2.getSubmissionContext()).thenReturn(submissionContext);
        Mockito.when(attempt2.getAppAttemptId()).thenReturn(appAttemptId2);
        Mockito.when(attempt2.getRMAppAttemptMetrics()).thenReturn(attemptMetric2);
        Mockito.when(app2.getCurrentAppAttempt()).thenReturn(attempt2);
        getRMContext().getRMApps().put(appId2, app2);
        addAppEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId2, "default", "user2");
        cs.handle(addAppEvent);
        addAttemptEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent(appAttemptId2, false);
        cs.handle(addAttemptEvent);
        // add nodes  to cluster, so cluster have 20GB and 20 vcores
        Resource newResource = Resource.newInstance((10 * (GB)), 10);
        RMNode node = MockNodes.newNodeInfo(0, newResource, 1, "127.0.0.1");
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
        Resource newResource2 = Resource.newInstance((10 * (GB)), 10);
        RMNode node2 = MockNodes.newNodeInfo(0, newResource2, 1, "127.0.0.2");
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node2));
        FiCaSchedulerApp fiCaApp1 = cs.getSchedulerApplications().get(app.getApplicationId()).getCurrentAppAttempt();
        FiCaSchedulerApp fiCaApp2 = cs.getSchedulerApplications().get(app2.getApplicationId()).getCurrentAppAttempt();
        Priority u0Priority = TestUtils.createMockPriority(1);
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        // allocate container for app1 with 10GB memory and 1 vcore
        fiCaApp1.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (10 * (GB)), 1, true, u0Priority, recordFactory)));
        cs.handle(new NodeUpdateSchedulerEvent(node));
        cs.handle(new NodeUpdateSchedulerEvent(node2));
        Assert.assertEquals((6 * (GB)), fiCaApp1.getHeadroom().getMemorySize());
        Assert.assertEquals(15, fiCaApp1.getHeadroom().getVirtualCores());
        // allocate container for app2 with 1GB memory and 1 vcore
        fiCaApp2.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, u0Priority, recordFactory)));
        cs.handle(new NodeUpdateSchedulerEvent(node));
        cs.handle(new NodeUpdateSchedulerEvent(node2));
        Assert.assertEquals((9 * (GB)), fiCaApp2.getHeadroom().getMemorySize());
        Assert.assertEquals(15, fiCaApp2.getHeadroom().getVirtualCores());
    }

    @Test
    public void testDefaultNodeLabelExpressionQueueConfig() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        conf.setDefaultNodeLabelExpression("root.a", " x");
        conf.setDefaultNodeLabelExpression("root.b", " y ");
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        QueueInfo queueInfoA = cs.getQueueInfo("a", true, false);
        Assert.assertEquals("Queue Name should be a", "a", queueInfoA.getQueueName());
        Assert.assertEquals("Default Node Label Expression should be x", "x", queueInfoA.getDefaultNodeLabelExpression());
        QueueInfo queueInfoB = cs.getQueueInfo("b", true, false);
        Assert.assertEquals("Queue Name should be b", "b", queueInfoB.getQueueName());
        Assert.assertEquals("Default Node Label Expression should be y", "y", queueInfoB.getDefaultNodeLabelExpression());
    }

    @Test(timeout = 60000)
    public void testAMLimitUsage() throws Exception {
        CapacitySchedulerConfiguration config = new CapacitySchedulerConfiguration();
        config.set(RESOURCE_CALCULATOR_CLASS, DefaultResourceCalculator.class.getName());
        verifyAMLimitForLeafQueue(config);
        config.set(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class.getName());
        verifyAMLimitForLeafQueue(config);
    }

    @Test
    public void testPendingResourceUpdatedAccordingToIncreaseRequestChanges() throws Exception {
        Configuration conf = TestUtils.getConfigurationWithQueueLabels(new Configuration(false));
        conf.setBoolean(NODE_LABELS_ENABLED, true);
        final RMNodeLabelsManager mgr = new NullRMNodeLabelsManager();
        mgr.init(conf);
        MockRM rm = new MockRM(conf) {
            protected RMNodeLabelsManager createNodeLabelManager() {
                return mgr;
            }
        };
        start();
        // label = ""
        MockNM nm1 = new MockNM("h1:1234", (200 * (GB)), getResourceTrackerService());
        nm1.registerNode();
        // Launch app1 in queue=a1
        RMApp app1 = rm.submitApp((1 * (GB)), "app", "user", null, "a1");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm1);
        // Allocate two more containers
        am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((2 * (GB))), 2)), null);
        ContainerId containerId1 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 1);
        ContainerId containerId2 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        ContainerId containerId3 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 3);
        Assert.assertTrue(rm.waitForState(nm1, containerId3, ALLOCATED));
        // Acquire them
        am1.allocate(null, null);
        sentRMContainerLaunched(rm, ContainerId.newContainerId(am1.getApplicationAttemptId(), 1L));
        sentRMContainerLaunched(rm, ContainerId.newContainerId(am1.getApplicationAttemptId(), 2L));
        sentRMContainerLaunched(rm, ContainerId.newContainerId(am1.getApplicationAttemptId(), 3L));
        // am1 asks to change its AM container from 1GB to 3GB
        am1.sendContainerResizingRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, containerId1, INCREASE_RESOURCE, Resources.createResource((3 * (GB))), null)));
        FiCaSchedulerApp app = getFiCaSchedulerApp(rm, app1.getApplicationId());
        Assert.assertEquals((2 * (GB)), app.getAppAttemptResourceUsage().getPending().getMemorySize());
        checkPendingResource(rm, "a1", (2 * (GB)), null);
        checkPendingResource(rm, "a", (2 * (GB)), null);
        checkPendingResource(rm, "root", (2 * (GB)), null);
        // am1 asks to change containerId2 (2G -> 3G) and containerId3 (2G -> 5G)
        am1.sendContainerResizingRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, containerId2, INCREASE_RESOURCE, Resources.createResource((3 * (GB))), null), UpdateContainerRequest.newInstance(0, containerId3, INCREASE_RESOURCE, Resources.createResource((5 * (GB))), null)));
        Assert.assertEquals((6 * (GB)), app.getAppAttemptResourceUsage().getPending().getMemorySize());
        checkPendingResource(rm, "a1", (6 * (GB)), null);
        checkPendingResource(rm, "a", (6 * (GB)), null);
        checkPendingResource(rm, "root", (6 * (GB)), null);
        // am1 asks to change containerId1 (1G->3G), containerId2 (2G -> 4G) and
        // containerId3 (2G -> 2G)
        am1.sendContainerResizingRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, containerId1, INCREASE_RESOURCE, Resources.createResource((3 * (GB))), null), UpdateContainerRequest.newInstance(0, containerId2, INCREASE_RESOURCE, Resources.createResource((4 * (GB))), null), UpdateContainerRequest.newInstance(0, containerId3, INCREASE_RESOURCE, Resources.createResource((2 * (GB))), null)));
        Assert.assertEquals((4 * (GB)), app.getAppAttemptResourceUsage().getPending().getMemorySize());
        checkPendingResource(rm, "a1", (4 * (GB)), null);
        checkPendingResource(rm, "a", (4 * (GB)), null);
        checkPendingResource(rm, "root", (4 * (GB)), null);
    }

    @Test
    public void testRemovedNodeDecomissioningNode() throws Exception {
        // Register nodemanager
        NodeManager nm = registerNode("host_decom", 1234, 2345, DEFAULT_RACK, Resources.createResource((8 * (GB)), 4));
        RMNode node = resourceManager.getRMContext().getRMNodes().get(nm.getNodeId());
        // Send a heartbeat to kick the tires on the Scheduler
        NodeUpdateSchedulerEvent nodeUpdate = new NodeUpdateSchedulerEvent(node);
        resourceManager.getResourceScheduler().handle(nodeUpdate);
        // force remove the node to simulate race condition
        getNodeTracker().removeNode(nm.getNodeId());
        // Kick off another heartbeat with the node state mocked to decommissioning
        RMNode spyNode = Mockito.spy(resourceManager.getRMContext().getRMNodes().get(nm.getNodeId()));
        Mockito.when(spyNode.getState()).thenReturn(DECOMMISSIONING);
        resourceManager.getResourceScheduler().handle(new NodeUpdateSchedulerEvent(spyNode));
    }

    @Test
    public void testResourceUpdateDecommissioningNode() throws Exception {
        // Mock the RMNodeResourceUpdate event handler to update SchedulerNode
        // to have 0 available resource
        RMContext spyContext = Mockito.spy(resourceManager.getRMContext());
        Dispatcher mockDispatcher = Mockito.mock(AsyncDispatcher.class);
        Mockito.when(mockDispatcher.getEventHandler()).thenReturn(new org.apache.hadoop.yarn.event.EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (event instanceof RMNodeResourceUpdateEvent) {
                    RMNodeResourceUpdateEvent resourceEvent = ((RMNodeResourceUpdateEvent) (event));
                    resourceManager.getResourceScheduler().getSchedulerNode(resourceEvent.getNodeId()).updateTotalResource(resourceEvent.getResourceOption().getResource());
                }
            }
        });
        Mockito.doReturn(mockDispatcher).when(spyContext).getDispatcher();
        ((CapacityScheduler) (resourceManager.getResourceScheduler())).setRMContext(spyContext);
        start();
        // Register node
        String host_0 = "host_0";
        NodeManager nm_0 = registerNode(host_0, 1234, 2345, DEFAULT_RACK, Resources.createResource((8 * (GB)), 4));
        // ResourceRequest priorities
        Priority priority_0 = Priority.newInstance(0);
        // Submit an application
        Application application_0 = new Application("user_0", "a1", resourceManager);
        application_0.submit();
        application_0.addNodeManager(host_0, 1234, nm_0);
        Resource capability_0_0 = Resources.createResource((1 * (GB)), 1);
        application_0.addResourceRequestSpec(priority_0, capability_0_0);
        Task task_0_0 = new Task(application_0, priority_0, new String[]{ host_0 });
        application_0.addTask(task_0_0);
        // Send resource requests to the scheduler
        application_0.schedule();
        nodeUpdate(nm_0);
        // Kick off another heartbeat with the node state mocked to decommissioning
        // This should update the schedulernodes to have 0 available resource
        RMNode spyNode = Mockito.spy(resourceManager.getRMContext().getRMNodes().get(nm_0.getNodeId()));
        Mockito.when(spyNode.getState()).thenReturn(DECOMMISSIONING);
        resourceManager.getResourceScheduler().handle(new NodeUpdateSchedulerEvent(spyNode));
        // Get allocations from the scheduler
        application_0.schedule();
        // Check the used resource is 1 GB 1 core
        Assert.assertEquals((1 * (GB)), nm_0.getUsed().getMemorySize());
        Resource usedResource = resourceManager.getResourceScheduler().getSchedulerNode(nm_0.getNodeId()).getAllocatedResource();
        Assert.assertEquals("Used Resource Memory Size should be 1GB", (1 * (GB)), usedResource.getMemorySize());
        Assert.assertEquals("Used Resource Virtual Cores should be 1", 1, usedResource.getVirtualCores());
        // Check total resource of scheduler node is also changed to 1 GB 1 core
        Resource totalResource = resourceManager.getResourceScheduler().getSchedulerNode(nm_0.getNodeId()).getTotalResource();
        Assert.assertEquals("Total Resource Memory Size should be 1GB", (1 * (GB)), totalResource.getMemorySize());
        Assert.assertEquals("Total Resource Virtual Cores should be 1", 1, totalResource.getVirtualCores());
        // Check the available resource is 0/0
        Resource availableResource = resourceManager.getResourceScheduler().getSchedulerNode(nm_0.getNodeId()).getUnallocatedResource();
        Assert.assertEquals("Available Resource Memory Size should be 0", 0, availableResource.getMemorySize());
        Assert.assertEquals("Available Resource Memory Size should be 0", 0, availableResource.getVirtualCores());
    }

    @Test
    public void testSchedulingOnRemovedNode() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.setBoolean(SCHEDULE_ASYNCHRONOUSLY_ENABLE, false);
        MockRM rm = new MockRM(conf);
        start();
        RMApp app = rm.submitApp(100);
        rm.drainEvents();
        MockNM nm1 = rm.registerNode("127.0.0.1:1234", 10240, 10);
        MockAM am = MockRM.launchAndRegisterAM(app, rm, nm1);
        // remove nm2 to keep am alive
        MockNM nm2 = rm.registerNode("127.0.0.1:1235", 10240, 10);
        am.allocate(ANY, 2048, 1, null);
        CapacityScheduler scheduler = ((CapacityScheduler) (getRMContext().getScheduler()));
        FiCaSchedulerNode node = ((FiCaSchedulerNode) (scheduler.getNodeTracker().getNode(nm2.getNodeId())));
        scheduler.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(getRMContext().getRMNodes().get(nm2.getNodeId())));
        // schedulerNode is removed, try allocate a container
        scheduler.allocateContainersToNode(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.placement.SimpleCandidateNodeSet(node), true);
        AppAttemptRemovedSchedulerEvent appRemovedEvent1 = new AppAttemptRemovedSchedulerEvent(am.getApplicationAttemptId(), RMAppAttemptState.FINISHED, false);
        scheduler.handle(appRemovedEvent1);
        stop();
    }

    @Test
    public void testCSReservationWithRootUnblocked() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        conf.setResourceComparator(DominantResourceCalculator.class);
        setupOtherBlockedQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        ParentQueue q = ((ParentQueue) (cs.getQueue("p1")));
        Assert.assertNotNull(q);
        String host = "127.0.0.1";
        String host1 = "test";
        RMNode node = MockNodes.newNodeInfo(0, Resource.newInstance((8 * (GB)), 8), 1, host);
        RMNode node1 = MockNodes.newNodeInfo(0, Resource.newInstance((8 * (GB)), 8), 2, host1);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node1));
        ApplicationAttemptId appAttemptId1 = appHelper(rm, cs, 100, 1, "x1", "userX1");
        ApplicationAttemptId appAttemptId2 = appHelper(rm, cs, 100, 2, "x2", "userX2");
        ApplicationAttemptId appAttemptId3 = appHelper(rm, cs, 100, 3, "y1", "userY1");
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        Priority priority = TestUtils.createMockPriority(1);
        ResourceRequest y1Req = null;
        ResourceRequest x1Req = null;
        ResourceRequest x2Req = null;
        for (int i = 0; i < 4; i++) {
            y1Req = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
            cs.allocate(appAttemptId3, Collections.<ResourceRequest>singletonList(y1Req), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
            CapacityScheduler.schedule(cs);
        }
        Assert.assertEquals("Y1 Used Resource should be 4 GB", (4 * (GB)), cs.getQueue("y1").getUsedResources().getMemorySize());
        Assert.assertEquals("P2 Used Resource should be 4 GB", (4 * (GB)), cs.getQueue("p2").getUsedResources().getMemorySize());
        for (int i = 0; i < 7; i++) {
            x1Req = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
            cs.allocate(appAttemptId1, Collections.<ResourceRequest>singletonList(x1Req), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
            CapacityScheduler.schedule(cs);
        }
        Assert.assertEquals("X1 Used Resource should be 7 GB", (7 * (GB)), cs.getQueue("x1").getUsedResources().getMemorySize());
        Assert.assertEquals("P1 Used Resource should be 7 GB", (7 * (GB)), cs.getQueue("p1").getUsedResources().getMemorySize());
        x2Req = TestUtils.createResourceRequest(ANY, (2 * (GB)), 1, true, priority, recordFactory);
        cs.allocate(appAttemptId2, Collections.<ResourceRequest>singletonList(x2Req), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        CapacityScheduler.schedule(cs);
        Assert.assertEquals("X2 Used Resource should be 0", 0, cs.getQueue("x2").getUsedResources().getMemorySize());
        Assert.assertEquals("P1 Used Resource should be 7 GB", (7 * (GB)), cs.getQueue("p1").getUsedResources().getMemorySize());
        // this assign should fail
        x1Req = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
        cs.allocate(appAttemptId1, Collections.<ResourceRequest>singletonList(x1Req), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        CapacityScheduler.schedule(cs);
        Assert.assertEquals("X1 Used Resource should be 7 GB", (7 * (GB)), cs.getQueue("x1").getUsedResources().getMemorySize());
        Assert.assertEquals("P1 Used Resource should be 7 GB", (7 * (GB)), cs.getQueue("p1").getUsedResources().getMemorySize());
        // this should get thru
        for (int i = 0; i < 4; i++) {
            y1Req = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
            cs.allocate(appAttemptId3, Collections.<ResourceRequest>singletonList(y1Req), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
            CapacityScheduler.schedule(cs);
        }
        Assert.assertEquals("P2 Used Resource should be 8 GB", (8 * (GB)), cs.getQueue("p2").getUsedResources().getMemorySize());
        // Free a container from X1
        ContainerId containerId = ContainerId.newContainerId(appAttemptId1, 2);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerExpiredSchedulerEvent(containerId));
        // Schedule pending request
        CapacityScheduler.schedule(cs);
        Assert.assertEquals("X2 Used Resource should be 2 GB", (2 * (GB)), cs.getQueue("x2").getUsedResources().getMemorySize());
        Assert.assertEquals("P1 Used Resource should be 8 GB", (8 * (GB)), cs.getQueue("p1").getUsedResources().getMemorySize());
        Assert.assertEquals("P2 Used Resource should be 8 GB", (8 * (GB)), cs.getQueue("p2").getUsedResources().getMemorySize());
        Assert.assertEquals("Root Used Resource should be 16 GB", (16 * (GB)), cs.getRootQueue().getUsedResources().getMemorySize());
        stop();
    }

    @Test
    public void testCSQueueBlocked() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupBlockedQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        LeafQueue q = ((LeafQueue) (cs.getQueue("a")));
        Assert.assertNotNull(q);
        String host = "127.0.0.1";
        String host1 = "test";
        RMNode node = MockNodes.newNodeInfo(0, Resource.newInstance((8 * (GB)), 8), 1, host);
        RMNode node1 = MockNodes.newNodeInfo(0, Resource.newInstance((8 * (GB)), 8), 2, host1);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node1));
        // add app begin
        ApplicationAttemptId appAttemptId1 = appHelper(rm, cs, 100, 1, "a", "user1");
        ApplicationAttemptId appAttemptId2 = appHelper(rm, cs, 100, 2, "b", "user2");
        // add app end
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        Priority priority = TestUtils.createMockPriority(1);
        ResourceRequest r1 = TestUtils.createResourceRequest(ANY, (2 * (GB)), 1, true, priority, recordFactory);
        // This will allocate for app1
        cs.allocate(appAttemptId1, Collections.<ResourceRequest>singletonList(r1), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS).getContainers().size();
        CapacityScheduler.schedule(cs);
        ResourceRequest r2 = null;
        for (int i = 0; i < 13; i++) {
            r2 = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
            cs.allocate(appAttemptId2, Collections.<ResourceRequest>singletonList(r2), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
            CapacityScheduler.schedule(cs);
        }
        Assert.assertEquals("A Used Resource should be 2 GB", (2 * (GB)), cs.getQueue("a").getUsedResources().getMemorySize());
        Assert.assertEquals("B Used Resource should be 13 GB", (13 * (GB)), cs.getQueue("b").getUsedResources().getMemorySize());
        r1 = TestUtils.createResourceRequest(ANY, (2 * (GB)), 1, true, priority, recordFactory);
        r2 = TestUtils.createResourceRequest(ANY, (1 * (GB)), 1, true, priority, recordFactory);
        cs.allocate(appAttemptId1, Collections.<ResourceRequest>singletonList(r1), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS).getContainers().size();
        CapacityScheduler.schedule(cs);
        cs.allocate(appAttemptId2, Collections.<ResourceRequest>singletonList(r2), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        CapacityScheduler.schedule(cs);
        // Check blocked Resource
        Assert.assertEquals("A Used Resource should be 2 GB", (2 * (GB)), cs.getQueue("a").getUsedResources().getMemorySize());
        Assert.assertEquals("B Used Resource should be 13 GB", (13 * (GB)), cs.getQueue("b").getUsedResources().getMemorySize());
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId2, 10);
        ContainerId containerId2 = ContainerId.newContainerId(appAttemptId2, 11);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerExpiredSchedulerEvent(containerId1));
        rm.drainEvents();
        CapacityScheduler.schedule(cs);
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerExpiredSchedulerEvent(containerId2));
        CapacityScheduler.schedule(cs);
        rm.drainEvents();
        Assert.assertEquals("A Used Resource should be 4 GB", (4 * (GB)), cs.getQueue("a").getUsedResources().getMemorySize());
        Assert.assertEquals("B Used Resource should be 12 GB", (12 * (GB)), cs.getQueue("b").getUsedResources().getMemorySize());
        Assert.assertEquals("Used Resource on Root should be 16 GB", (16 * (GB)), cs.getRootQueue().getUsedResources().getMemorySize());
        stop();
    }

    @Test
    public void testAppAttemptLocalityStatistics() throws Exception {
        Configuration conf = TestUtils.getConfigurationWithMultipleQueues(new Configuration(false));
        conf.setBoolean(NODE_LABELS_ENABLED, true);
        final RMNodeLabelsManager mgr = new NullRMNodeLabelsManager();
        mgr.init(conf);
        MockRM rm = new MockRM(conf) {
            protected RMNodeLabelsManager createNodeLabelManager() {
                return mgr;
            }
        };
        start();
        MockNM nm1 = new MockNM("h1:1234", (200 * (GB)), getResourceTrackerService());
        nm1.registerNode();
        // Launch app1 in queue=a1
        RMApp app1 = rm.submitApp((1 * (GB)), "app", "user", null, "a");
        // Got one offswitch request and offswitch allocation
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm1);
        // am1 asks for 1 GB resource on h1/default-rack/offswitch
        am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (GB))), 2), ResourceRequest.newInstance(Priority.newInstance(1), "/default-rack", Resources.createResource((1 * (GB))), 2), ResourceRequest.newInstance(Priority.newInstance(1), "h1", Resources.createResource((1 * (GB))), 1)), null);
        CapacityScheduler cs = ((CapacityScheduler) (getRMContext().getScheduler()));
        // Got one nodelocal request and nodelocal allocation
        cs.nodeUpdate(getRMContext().getRMNodes().get(nm1.getNodeId()));
        // Got one nodelocal request and racklocal allocation
        cs.nodeUpdate(getRMContext().getRMNodes().get(nm1.getNodeId()));
        RMAppAttemptMetrics attemptMetrics = getRMContext().getRMApps().get(app1.getApplicationId()).getCurrentAppAttempt().getRMAppAttemptMetrics();
        // We should get one node-local allocation, one rack-local allocation
        // And one off-switch allocation
        Assert.assertArrayEquals(new int[][]{ new int[]{ 1, 0, 0 }, new int[]{ 0, 1, 0 }, new int[]{ 0, 0, 1 } }, attemptMetrics.getLocalityStatistics());
    }

    /**
     * Test for queue deletion.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRefreshQueuesWithQueueDelete() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null);
        setupQueueConfiguration(conf);
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, rmContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        // test delete leaf queue when there is application running.
        Map<String, CSQueue> queues = cs.getCapacitySchedulerQueueManager().getQueues();
        String b1QTobeDeleted = "b1";
        LeafQueue csB1Queue = Mockito.spy(((LeafQueue) (queues.get(b1QTobeDeleted))));
        Mockito.when(csB1Queue.getState()).thenReturn(DRAINING).thenReturn(STOPPED);
        queues.put(b1QTobeDeleted, csB1Queue);
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithOutB1(conf);
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail(("Expected to throw exception when refresh queue tries to delete a" + " queue with running apps"));
        } catch (IOException e) {
            // ignore
        }
        // test delete leaf queue(root.b.b1) when there is no application running.
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithOutB1(conf);
        try {
            cs.reinitialize(conf, mockContext);
        } catch (IOException e) {
            TestCapacityScheduler.LOG.error(("Expected to NOT throw exception when refresh queue tries to delete" + " a queue WITHOUT running apps"), e);
            Assert.fail(("Expected to NOT throw exception when refresh queue tries to delete" + " a queue WITHOUT running apps"));
        }
        CSQueue rootQueue = cs.getRootQueue();
        CSQueue queueB = findQueue(rootQueue, CapacitySchedulerTestBase.B);
        CSQueue queueB3 = findQueue(queueB, CapacitySchedulerTestBase.B1);
        Assert.assertNull("Refresh needs to support delete of leaf queue ", queueB3);
        // reset back to default configuration for testing parent queue delete
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        cs.reinitialize(conf, rmContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        // set the configurations such that it fails once but should be successfull
        // next time
        queues = cs.getCapacitySchedulerQueueManager().getQueues();
        CSQueue bQueue = Mockito.spy(((ParentQueue) (queues.get("b"))));
        Mockito.when(bQueue.getState()).thenReturn(DRAINING).thenReturn(STOPPED);
        queues.put("b", bQueue);
        bQueue = Mockito.spy(((LeafQueue) (queues.get("b1"))));
        Mockito.when(bQueue.getState()).thenReturn(STOPPED);
        queues.put("b1", bQueue);
        bQueue = Mockito.spy(((LeafQueue) (queues.get("b2"))));
        Mockito.when(bQueue.getState()).thenReturn(STOPPED);
        queues.put("b2", bQueue);
        bQueue = Mockito.spy(((LeafQueue) (queues.get("b3"))));
        Mockito.when(bQueue.getState()).thenReturn(STOPPED);
        queues.put("b3", bQueue);
        // test delete Parent queue when there is application running.
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithOutB(conf);
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail(("Expected to throw exception when refresh queue tries to delete a" + " parent queue with running apps in children queue"));
        } catch (IOException e) {
            // ignore
        }
        // test delete Parent queue when there is no application running.
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithOutB(conf);
        try {
            cs.reinitialize(conf, mockContext);
        } catch (IOException e) {
            Assert.fail(("Expected to not throw exception when refresh queue tries to delete" + " a queue without running apps"));
        }
        rootQueue = cs.getRootQueue();
        queueB = findQueue(rootQueue, CapacitySchedulerTestBase.B);
        String message = "Refresh needs to support delete of Parent queue and its children.";
        Assert.assertNull(message, queueB);
        Assert.assertNull(message, cs.getCapacitySchedulerQueueManager().getQueues().get("b"));
        Assert.assertNull(message, cs.getCapacitySchedulerQueueManager().getQueues().get("b1"));
        Assert.assertNull(message, cs.getCapacitySchedulerQueueManager().getQueues().get("b2"));
        cs.stop();
    }

    /**
     * Test for all child queue deletion and thus making parent queue a child.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRefreshQueuesWithAllChildQueuesDeleted() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null);
        setupQueueConfiguration(conf);
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, rmContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        // test delete all leaf queues when there is no application running.
        Map<String, CSQueue> queues = cs.getCapacitySchedulerQueueManager().getQueues();
        CSQueue bQueue = Mockito.spy(((LeafQueue) (queues.get("b1"))));
        Mockito.when(bQueue.getState()).thenReturn(QueueState.RUNNING).thenReturn(STOPPED);
        queues.put("b1", bQueue);
        bQueue = Mockito.spy(((LeafQueue) (queues.get("b2"))));
        Mockito.when(bQueue.getState()).thenReturn(STOPPED);
        queues.put("b2", bQueue);
        bQueue = Mockito.spy(((LeafQueue) (queues.get("b3"))));
        Mockito.when(bQueue.getState()).thenReturn(STOPPED);
        queues.put("b3", bQueue);
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfWithOutChildrenOfB(conf);
        // test convert parent queue to leaf queue(root.b) when there is no
        // application running.
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail(("Expected to throw exception when refresh queue tries to make parent" + " queue a child queue when one of its children is still running."));
        } catch (IOException e) {
            // do not do anything, expected exception
        }
        // test delete leaf queues(root.b.b1,b2,b3) when there is no application
        // running.
        try {
            cs.reinitialize(conf, mockContext);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(("Expected to NOT throw exception when refresh queue tries to delete" + " all children of a parent queue(without running apps)."));
        }
        CSQueue rootQueue = cs.getRootQueue();
        CSQueue queueB = findQueue(rootQueue, CapacitySchedulerTestBase.B);
        Assert.assertNotNull("Parent Queue B should not be deleted", queueB);
        Assert.assertTrue("As Queue'B children are not deleted", (queueB instanceof LeafQueue));
        String message = "Refresh needs to support delete of all children of Parent queue.";
        Assert.assertNull(message, cs.getCapacitySchedulerQueueManager().getQueues().get("b3"));
        Assert.assertNull(message, cs.getCapacitySchedulerQueueManager().getQueues().get("b1"));
        Assert.assertNull(message, cs.getCapacitySchedulerQueueManager().getQueues().get("b2"));
        cs.stop();
    }

    /**
     * Test if we can convert a leaf queue to a parent queue
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testConvertLeafQueueToParentQueue() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        RMContextImpl rmContext = new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null);
        setupQueueConfiguration(conf);
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        cs.init(conf);
        cs.start();
        cs.reinitialize(conf, rmContext);
        checkQueueCapacities(cs, CapacitySchedulerTestBase.A_CAPACITY, CapacitySchedulerTestBase.B_CAPACITY);
        String targetQueue = "b1";
        CSQueue b1 = cs.getQueue(targetQueue);
        Assert.assertEquals(QueueState.RUNNING, b1.getState());
        // test if we can convert a leaf queue which is in RUNNING state
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithB1AsParentQueue(conf);
        try {
            cs.reinitialize(conf, mockContext);
            Assert.fail(("Expected to throw exception when refresh queue tries to convert" + " a child queue to a parent queue."));
        } catch (IOException e) {
            // ignore
        }
        // now set queue state for b1 to STOPPED
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        conf.set("yarn.scheduler.capacity.root.b.b1.state", "STOPPED");
        cs.reinitialize(conf, mockContext);
        Assert.assertEquals(STOPPED, b1.getState());
        // test if we can convert a leaf queue which is in STOPPED state
        conf = new CapacitySchedulerConfiguration();
        setupQueueConfigurationWithB1AsParentQueue(conf);
        try {
            cs.reinitialize(conf, mockContext);
        } catch (IOException e) {
            Assert.fail(("Expected to NOT throw exception when refresh queue tries" + " to convert a leaf queue WITHOUT running apps"));
        }
        b1 = cs.getQueue(targetQueue);
        Assert.assertTrue((b1 instanceof ParentQueue));
        Assert.assertEquals(QueueState.RUNNING, b1.getState());
        Assert.assertTrue((!(b1.getChildQueues().isEmpty())));
    }

    @Test(timeout = 30000)
    public void testAMLimitDouble() throws Exception {
        CapacitySchedulerConfiguration config = new CapacitySchedulerConfiguration();
        config.set(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class.getName());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration(config);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.setInt("yarn.scheduler.minimum-allocation-mb", 512);
        conf.setInt("yarn.scheduler.minimum-allocation-vcores", 1);
        MockRM rm = new MockRM(conf);
        start();
        rm.registerNode("127.0.0.1:1234", (10 * (GB)));
        rm.registerNode("127.0.0.1:1235", (10 * (GB)));
        rm.registerNode("127.0.0.1:1236", (10 * (GB)));
        rm.registerNode("127.0.0.1:1237", (10 * (GB)));
        ResourceScheduler scheduler = getRMContext().getScheduler();
        waitforNMRegistered(scheduler, 4, 5);
        LeafQueue queueA = ((LeafQueue) (getQueue("default")));
        Resource amResourceLimit = queueA.getAMResourceLimit();
        Assert.assertEquals(4096, amResourceLimit.getMemorySize());
        Assert.assertEquals(4, amResourceLimit.getVirtualCores());
        stop();
    }

    @Test
    public void testQueueMappingWithCurrentUserQueueMappingForaGroup() throws Exception {
        CapacitySchedulerConfiguration config = new CapacitySchedulerConfiguration();
        config.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        setupQueueConfiguration(config);
        config.setClass(HADOOP_SECURITY_GROUP_MAPPING, FakeunPrivilegedGroupMapping.class, ShellBasedUnixGroupsMapping.class);
        config.set(HADOOP_USER_GROUP_STATIC_OVERRIDES, ("a1" + (("=" + "agroup") + "")));
        config.set(QUEUE_MAPPING, "g:agroup:%user");
        MockRM rm = new MockRM(config);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        cs.start();
        RMApp app = rm.submitApp(GB, "appname", "a1", null, "default");
        List<ApplicationAttemptId> appsInA1 = cs.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
    }

    @Test(timeout = 30000)
    public void testcheckAndGetApplicationLifetime() throws Exception {
        long maxLifetime = 10;
        long defaultLifetime = 5;
        // positive integer value
        CapacityScheduler cs = setUpCSQueue(maxLifetime, defaultLifetime);
        Assert.assertEquals(maxLifetime, cs.checkAndGetApplicationLifetime("default", 100));
        Assert.assertEquals(9, cs.checkAndGetApplicationLifetime("default", 9));
        Assert.assertEquals(defaultLifetime, cs.checkAndGetApplicationLifetime("default", (-1)));
        Assert.assertEquals(defaultLifetime, cs.checkAndGetApplicationLifetime("default", 0));
        Assert.assertEquals(maxLifetime, cs.getMaximumApplicationLifetime("default"));
        maxLifetime = -1;
        defaultLifetime = -1;
        // test for default values
        cs = setUpCSQueue(maxLifetime, defaultLifetime);
        Assert.assertEquals(100, cs.checkAndGetApplicationLifetime("default", 100));
        Assert.assertEquals(defaultLifetime, cs.checkAndGetApplicationLifetime("default", (-1)));
        Assert.assertEquals(0, cs.checkAndGetApplicationLifetime("default", 0));
        Assert.assertEquals(maxLifetime, cs.getMaximumApplicationLifetime("default"));
        maxLifetime = 10;
        defaultLifetime = 10;
        cs = setUpCSQueue(maxLifetime, defaultLifetime);
        Assert.assertEquals(maxLifetime, cs.checkAndGetApplicationLifetime("default", 100));
        Assert.assertEquals(defaultLifetime, cs.checkAndGetApplicationLifetime("default", (-1)));
        Assert.assertEquals(defaultLifetime, cs.checkAndGetApplicationLifetime("default", 0));
        Assert.assertEquals(maxLifetime, cs.getMaximumApplicationLifetime("default"));
        maxLifetime = 0;
        defaultLifetime = 0;
        cs = setUpCSQueue(maxLifetime, defaultLifetime);
        Assert.assertEquals(100, cs.checkAndGetApplicationLifetime("default", 100));
        Assert.assertEquals((-1), cs.checkAndGetApplicationLifetime("default", (-1)));
        Assert.assertEquals(0, cs.checkAndGetApplicationLifetime("default", 0));
        maxLifetime = 10;
        defaultLifetime = -1;
        cs = setUpCSQueue(maxLifetime, defaultLifetime);
        Assert.assertEquals(maxLifetime, cs.checkAndGetApplicationLifetime("default", 100));
        Assert.assertEquals(maxLifetime, cs.checkAndGetApplicationLifetime("default", (-1)));
        Assert.assertEquals(maxLifetime, cs.checkAndGetApplicationLifetime("default", 0));
        maxLifetime = 5;
        defaultLifetime = 10;
        try {
            setUpCSQueue(maxLifetime, defaultLifetime);
            Assert.fail("Expected to fails since maxLifetime < defaultLifetime.");
        } catch (YarnRuntimeException ye) {
            Assert.assertTrue(ye.getMessage().contains("can't exceed maximum lifetime"));
        }
    }

    @Test(timeout = 60000)
    public void testClearRequestsBeforeApplyTheProposal() throws Exception {
        // init RM & NMs & Nodes
        final MockRM rm = new MockRM(new CapacitySchedulerConfiguration());
        start();
        final MockNM nm = rm.registerNode("h1:1234", (200 * (GB)));
        // submit app
        final RMApp app = rm.submitApp(200, "app", "user");
        MockRM.launchAndRegisterAM(app, rm, nm);
        // spy capacity scheduler to handle CapacityScheduler#apply
        final Priority priority = Priority.newInstance(1);
        final CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        final CapacityScheduler spyCs = Mockito.spy(cs);
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Exception {
                // clear resource request before applying the proposal for container_2
                spyCs.allocate(app.getCurrentAppAttempt().getAppAttemptId(), Arrays.asList(ResourceRequest.newInstance(priority, "*", Resources.createResource((1 * (GB))), 0)), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
                // trigger real apply which can raise NPE before YARN-6629
                try {
                    FiCaSchedulerApp schedulerApp = cs.getApplicationAttempt(app.getCurrentAppAttempt().getAppAttemptId());
                    schedulerApp.apply(((Resource) (invocation.getArguments()[0])), ((ResourceCommitRequest) (invocation.getArguments()[1])), ((Boolean) (invocation.getArguments()[2])));
                    // the proposal of removed request should be rejected
                    Assert.assertEquals(1, schedulerApp.getLiveContainers().size());
                } catch (Throwable e) {
                    Assert.fail();
                }
                return null;
            }
        }).when(spyCs).tryCommit(Mockito.any(Resource.class), Mockito.any(ResourceCommitRequest.class), Mockito.anyBoolean());
        // rm allocates container_2 to reproduce the process that can raise NPE
        spyCs.allocate(app.getCurrentAppAttempt().getAppAttemptId(), Arrays.asList(ResourceRequest.newInstance(priority, "*", Resources.createResource((1 * (GB))), 1)), null, Collections.<ContainerId>emptyList(), null, null, TestCapacityScheduler.NULL_UPDATE_REQUESTS);
        spyCs.handle(new NodeUpdateSchedulerEvent(spyCs.getNode(nm.getNodeId()).getRMNode()));
    }

    // Testcase for YARN-8528
    // This is to test whether ContainerAllocation constants are holding correct
    // values during scheduling.
    @Test
    public void testContainerAllocationLocalitySkipped() throws Exception {
        Assert.assertEquals(APP_SKIPPED, ContainerAllocation.APP_SKIPPED.getAllocationState());
        Assert.assertEquals(LOCALITY_SKIPPED, ContainerAllocation.LOCALITY_SKIPPED.getAllocationState());
        Assert.assertEquals(PRIORITY_SKIPPED, ContainerAllocation.PRIORITY_SKIPPED.getAllocationState());
        Assert.assertEquals(QUEUE_SKIPPED, ContainerAllocation.QUEUE_SKIPPED.getAllocationState());
        // init RM & NMs & Nodes
        final MockRM rm = new MockRM(new CapacitySchedulerConfiguration());
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        start();
        final MockNM nm1 = rm.registerNode("h1:1234", (4 * (GB)));
        final MockNM nm2 = rm.registerNode("h2:1234", (6 * (GB)));// maximum-allocation-mb = 6GB

        // submit app and request resource
        // container2 is larger than nm1 total resource, will trigger locality skip
        final RMApp app = rm.submitApp((1 * (GB)), "app", "user");
        final MockAM am = MockRM.launchAndRegisterAM(app, rm, nm1);
        am.addRequests(new String[]{ "*" }, (5 * (GB)), 1, 1, 2);
        am.schedule();
        // container1 (am) should be acquired, container2 should not
        RMNode node1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        cs.handle(new NodeUpdateSchedulerEvent(node1));
        ContainerId cid = ContainerId.newContainerId(am.getApplicationAttemptId(), 1L);
        Assert.assertEquals(cs.getRMContainer(cid).getState(), ACQUIRED);
        cid = ContainerId.newContainerId(am.getApplicationAttemptId(), 2L);
        Assert.assertNull(cs.getRMContainer(cid));
        Assert.assertEquals(APP_SKIPPED, ContainerAllocation.APP_SKIPPED.getAllocationState());
        Assert.assertEquals(LOCALITY_SKIPPED, ContainerAllocation.LOCALITY_SKIPPED.getAllocationState());
        Assert.assertEquals(PRIORITY_SKIPPED, ContainerAllocation.PRIORITY_SKIPPED.getAllocationState());
        Assert.assertEquals(QUEUE_SKIPPED, ContainerAllocation.QUEUE_SKIPPED.getAllocationState());
    }

    @Test
    public void testMoveAppWithActiveUsersWithOnlyPendingApps() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        CapacitySchedulerConfiguration newConf = new CapacitySchedulerConfiguration(conf);
        // Define top-level queues
        newConf.setQueues(ROOT, new String[]{ "a", "b" });
        newConf.setCapacity(CapacitySchedulerTestBase.A, 50);
        newConf.setCapacity(CapacitySchedulerTestBase.B, 50);
        // Define 2nd-level queues
        newConf.setQueues(CapacitySchedulerTestBase.A, new String[]{ "a1" });
        newConf.setCapacity(CapacitySchedulerTestBase.A1, 100);
        newConf.setUserLimitFactor(CapacitySchedulerTestBase.A1, 2.0F);
        newConf.setMaximumAMResourcePercentPerPartition(CapacitySchedulerTestBase.A1, "", 0.1F);
        newConf.setQueues(CapacitySchedulerTestBase.B, new String[]{ "b1" });
        newConf.setCapacity(CapacitySchedulerTestBase.B1, 100);
        newConf.setUserLimitFactor(CapacitySchedulerTestBase.B1, 2.0F);
        TestCapacityScheduler.LOG.info("Setup top-level queues a and b");
        MockRM rm = new MockRM(newConf);
        start();
        CapacityScheduler scheduler = ((CapacityScheduler) (getResourceScheduler()));
        MockNM nm1 = rm.registerNode("h1:1234", (16 * (GB)));
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "u1", null, "a1");
        MockAM am1 = MockRM.launchAndRegisterAM(app, rm, nm1);
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        RMApp app2 = rm.submitApp((1 * (GB)), "app", "u2", null, "a1");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm, nm1);
        RMApp app3 = rm.submitApp((1 * (GB)), "app", "u3", null, "a1");
        RMApp app4 = rm.submitApp((1 * (GB)), "app", "u4", null, "a1");
        // Each application asks 50 * 1GB containers
        am1.allocate("*", (1 * (GB)), 50, null);
        am2.allocate("*", (1 * (GB)), 50, null);
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(4, appsInA1.size());
        String queue = scheduler.getApplicationAttempt(appsInA1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("a1", queue);
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(4, appsInA.size());
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(4, appsInRoot.size());
        List<ApplicationAttemptId> appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        List<ApplicationAttemptId> appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        UsersManager um = ((UsersManager) (scheduler.getQueue("a1").getAbstractUsersManager()));
        Assert.assertEquals(4, um.getNumActiveUsers());
        Assert.assertEquals(2, um.getNumActiveUsersWithOnlyPendingApps());
        // now move the app
        scheduler.moveAllApps("a1", "b1");
        // Triggering this event so that user limit computation can
        // happen again
        for (int i = 0; i < 10; i++) {
            cs.handle(new NodeUpdateSchedulerEvent(rmNode1));
            Thread.sleep(500);
        }
        // check postconditions
        appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertEquals(4, appsInB1.size());
        queue = scheduler.getApplicationAttempt(appsInB1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("b1", queue);
        appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.contains(appAttemptId));
        Assert.assertEquals(4, appsInB.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(4, appsInRoot.size());
        List<ApplicationAttemptId> oldAppsInA1 = scheduler.getAppsInQueue("a1");
        Assert.assertEquals(0, oldAppsInA1.size());
        UsersManager um_b1 = ((UsersManager) (scheduler.getQueue("b1").getAbstractUsersManager()));
        Assert.assertEquals(2, um_b1.getNumActiveUsers());
        Assert.assertEquals(2, um_b1.getNumActiveUsersWithOnlyPendingApps());
        appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertEquals(4, appsInB1.size());
        close();
    }

    @Test
    public void testCSQueueMetrics() throws Exception {
        CapacityScheduler cs = new CapacityScheduler();
        cs.setConf(new YarnConfiguration());
        cs.setRMContext(resourceManager.getRMContext());
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(conf);
        cs.init(conf);
        cs.start();
        RMNode n1 = MockNodes.newNodeInfo(0, MockNodes.newResource((50 * (GB))), 1, "n1");
        RMNode n2 = MockNodes.newNodeInfo(0, MockNodes.newResource((50 * (GB))), 2, "n2");
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n1));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(n2));
        Assert.assertEquals(10240, getGuaranteedMB());
        Assert.assertEquals(71680, getGuaranteedMB());
        Assert.assertEquals(102400, getMaxCapacityMB());
        Assert.assertEquals(102400, getMaxCapacityMB());
        // Remove a node, metrics should be updated
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(n2));
        Assert.assertEquals(5120, getGuaranteedMB());
        Assert.assertEquals(35840, getGuaranteedMB());
        Assert.assertEquals(51200, getMaxCapacityMB());
        Assert.assertEquals(51200, getMaxCapacityMB());
        // Add child queue to a, and reinitialize. Metrics should be updated
        conf.setQueues(((ROOT) + ".a"), new String[]{ "a1", "a2", "a3" });
        conf.setCapacity(((ROOT) + ".a.a2"), 30.0F);
        conf.setCapacity(((ROOT) + ".a.a3"), 40.0F);
        conf.setMaximumCapacity(((ROOT) + ".a.a3"), 50.0F);
        cs.reinitialize(conf, new RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(conf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(conf), new ClientToAMTokenSecretManagerInRM(), null));
        Assert.assertEquals(1024, getGuaranteedMB());
        Assert.assertEquals(2048, getGuaranteedMB());
        Assert.assertEquals(51200, getMaxCapacityMB());
        Assert.assertEquals(25600, getMaxCapacityMB());
    }
}

