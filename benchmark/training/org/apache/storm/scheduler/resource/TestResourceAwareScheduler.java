/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.scheduler.resource;


import Config.NIMBUS_SCHEDULER_STRATEGY_CLASS_WHITELIST;
import Config.TOPOLOGY_COMPONENT_RESOURCES_ONHEAP_MEMORY_MB;
import Config.TOPOLOGY_SCHEDULER_STRATEGY;
import Config.TOPOLOGY_SUBMITTER_USER;
import Config.TOPOLOGY_WORKER_MAX_HEAP_SIZE_MB;
import DaemonConfig.SCHEDULING_TIMEOUT_SECONDS_PER_TOPOLOGY;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.generated.WorkerResources;
import org.apache.storm.metric.StormMetricsRegistry;
import org.apache.storm.scheduler.Cluster;
import org.apache.storm.scheduler.ExecutorDetails;
import org.apache.storm.scheduler.INimbus;
import org.apache.storm.scheduler.IScheduler;
import org.apache.storm.scheduler.SchedulerAssignment;
import org.apache.storm.scheduler.SchedulerAssignmentImpl;
import org.apache.storm.scheduler.SupervisorDetails;
import org.apache.storm.scheduler.Topologies;
import org.apache.storm.scheduler.TopologyDetails;
import org.apache.storm.scheduler.WorkerSlot;
import org.apache.storm.scheduler.resource.strategies.scheduling.BaseResourceAwareStrategy;
import org.apache.storm.scheduler.resource.strategies.scheduling.DefaultResourceAwareStrategy;
import org.apache.storm.scheduler.resource.strategies.scheduling.GenericResourceAwareStrategy;
import org.apache.storm.scheduler.resource.strategies.scheduling.IStrategy;
import org.apache.storm.testing.PerformanceTest;
import org.apache.storm.testing.TestWordCounter;
import org.apache.storm.testing.TestWordSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.ConfigUtils;
import org.apache.storm.utils.DisallowedStrategyException;
import org.apache.storm.utils.ReflectionUtils;
import org.apache.storm.utils.Utils;
import org.apache.storm.validation.ConfigValidation;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestResourceAwareScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(TestResourceAwareScheduler.class);

    private static final Config defaultTopologyConf = TestUtilsForResourceAwareScheduler.createClusterConfig(10, 128, 0, null);

    private static int currentTime = 1450418597;

    private static IScheduler scheduler = null;

    @Test
    public void testRASNodeSlotAssign() {
        Config config = new Config();
        config.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(5, 4, 400, 2000);
        TopologyDetails topology1 = TestUtilsForResourceAwareScheduler.genTopology("topology1", config, 1, 0, 2, 0, 0, 0, "user");
        TopologyDetails topology2 = TestUtilsForResourceAwareScheduler.genTopology("topology2", config, 1, 0, 2, 0, 0, 0, "user");
        Topologies topologies = new Topologies(topology1, topology2);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        Map<String, RAS_Node> nodes = RAS_Nodes.getAllNodesFrom(cluster);
        Assert.assertEquals(5, nodes.size());
        RAS_Node node = nodes.get("r000s000");
        Assert.assertEquals("r000s000", node.getId());
        Assert.assertTrue(node.isAlive());
        Assert.assertEquals(0, node.getRunningTopologies().size());
        Assert.assertTrue(node.isTotallyFree());
        Assert.assertEquals(4, node.totalSlotsFree());
        Assert.assertEquals(0, node.totalSlotsUsed());
        Assert.assertEquals(4, node.totalSlots());
        List<ExecutorDetails> executors11 = new ArrayList<>();
        executors11.add(new ExecutorDetails(1, 1));
        node.assign(node.getFreeSlots().iterator().next(), topology1, executors11);
        Assert.assertEquals(1, node.getRunningTopologies().size());
        Assert.assertFalse(node.isTotallyFree());
        Assert.assertEquals(3, node.totalSlotsFree());
        Assert.assertEquals(1, node.totalSlotsUsed());
        Assert.assertEquals(4, node.totalSlots());
        List<ExecutorDetails> executors12 = new ArrayList<>();
        executors12.add(new ExecutorDetails(2, 2));
        node.assign(node.getFreeSlots().iterator().next(), topology1, executors12);
        Assert.assertEquals(1, node.getRunningTopologies().size());
        Assert.assertFalse(node.isTotallyFree());
        Assert.assertEquals(2, node.totalSlotsFree());
        Assert.assertEquals(2, node.totalSlotsUsed());
        Assert.assertEquals(4, node.totalSlots());
        List<ExecutorDetails> executors21 = new ArrayList<>();
        executors21.add(new ExecutorDetails(1, 1));
        node.assign(node.getFreeSlots().iterator().next(), topology2, executors21);
        Assert.assertEquals(2, node.getRunningTopologies().size());
        Assert.assertFalse(node.isTotallyFree());
        Assert.assertEquals(1, node.totalSlotsFree());
        Assert.assertEquals(3, node.totalSlotsUsed());
        Assert.assertEquals(4, node.totalSlots());
        List<ExecutorDetails> executors22 = new ArrayList<>();
        executors22.add(new ExecutorDetails(2, 2));
        node.assign(node.getFreeSlots().iterator().next(), topology2, executors22);
        Assert.assertEquals(2, node.getRunningTopologies().size());
        Assert.assertFalse(node.isTotallyFree());
        Assert.assertEquals(0, node.totalSlotsFree());
        Assert.assertEquals(4, node.totalSlotsUsed());
        Assert.assertEquals(4, node.totalSlots());
        node.freeAllSlots();
        Assert.assertEquals(0, node.getRunningTopologies().size());
        Assert.assertTrue(node.isTotallyFree());
        Assert.assertEquals(4, node.totalSlotsFree());
        Assert.assertEquals(0, node.totalSlotsUsed());
        Assert.assertEquals(4, node.totalSlots());
    }

    @Test
    public void sanityTestOfScheduling() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(1, 2, 400, 2000);
        Config config = new Config();
        config.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TopologyDetails topology1 = TestUtilsForResourceAwareScheduler.genTopology("topology1", config, 1, 1, 1, 1, 0, 0, "user");
        Topologies topologies = new Topologies(topology1);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        SchedulerAssignment assignment = cluster.getAssignmentById(topology1.getId());
        Set<WorkerSlot> assignedSlots = assignment.getSlots();
        Set<String> nodesIDs = new HashSet<>();
        for (WorkerSlot slot : assignedSlots) {
            nodesIDs.add(slot.getNodeId());
        }
        Collection<ExecutorDetails> executors = assignment.getExecutors();
        Assert.assertEquals(1, assignedSlots.size());
        Assert.assertEquals(1, nodesIDs.size());
        Assert.assertEquals(2, executors.size());
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster.getStatusMap().get(topology1.getId()));
    }

    @Test
    public void testTopologyWithMultipleSpouts() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(2, 4, 400, 2000);
        TopologyBuilder builder1 = new TopologyBuilder();// a topology with multiple spouts

        builder1.setSpout("wordSpout1", new TestWordSpout(), 1);
        builder1.setSpout("wordSpout2", new TestWordSpout(), 1);
        builder1.setBolt("wordCountBolt1", new TestWordCounter(), 1).shuffleGrouping("wordSpout1").shuffleGrouping("wordSpout2");
        builder1.setBolt("wordCountBolt2", new TestWordCounter(), 1).shuffleGrouping("wordCountBolt1");
        builder1.setBolt("wordCountBolt3", new TestWordCounter(), 1).shuffleGrouping("wordCountBolt1");
        builder1.setBolt("wordCountBolt4", new TestWordCounter(), 1).shuffleGrouping("wordCountBolt2");
        builder1.setBolt("wordCountBolt5", new TestWordCounter(), 1).shuffleGrouping("wordSpout2");
        StormTopology stormTopology1 = builder1.createTopology();
        Config config = new Config();
        config.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        Map<ExecutorDetails, String> executorMap1 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology1);
        TopologyDetails topology1 = new TopologyDetails("topology1", config, stormTopology1, 0, executorMap1, 0, "user");
        TopologyBuilder builder2 = new TopologyBuilder();// a topology with two unconnected partitions

        builder2.setSpout("wordSpoutX", new TestWordSpout(), 1);
        builder2.setSpout("wordSpoutY", new TestWordSpout(), 1);
        StormTopology stormTopology2 = builder2.createTopology();
        Map<ExecutorDetails, String> executorMap2 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology2);
        TopologyDetails topology2 = new TopologyDetails("topology2", config, stormTopology2, 0, executorMap2, 0, "user");
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        Topologies topologies = new Topologies(topology1, topology2);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        SchedulerAssignment assignment1 = cluster.getAssignmentById(topology1.getId());
        Set<WorkerSlot> assignedSlots1 = assignment1.getSlots();
        Set<String> nodesIDs1 = new HashSet<>();
        for (WorkerSlot slot : assignedSlots1) {
            nodesIDs1.add(slot.getNodeId());
        }
        Collection<ExecutorDetails> executors1 = assignment1.getExecutors();
        Assert.assertEquals(1, assignedSlots1.size());
        Assert.assertEquals(1, nodesIDs1.size());
        Assert.assertEquals(7, executors1.size());
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster.getStatusMap().get(topology1.getId()));
        SchedulerAssignment assignment2 = cluster.getAssignmentById(topology2.getId());
        Set<WorkerSlot> assignedSlots2 = assignment2.getSlots();
        Set<String> nodesIDs2 = new HashSet<>();
        for (WorkerSlot slot : assignedSlots2) {
            nodesIDs2.add(slot.getNodeId());
        }
        Collection<ExecutorDetails> executors2 = assignment2.getExecutors();
        Assert.assertEquals(1, assignedSlots2.size());
        Assert.assertEquals(1, nodesIDs2.size());
        Assert.assertEquals(2, executors2.size());
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster.getStatusMap().get(topology2.getId()));
    }

    @Test
    public void testTopologySetCpuAndMemLoad() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(2, 2, 400, 2000);
        TopologyBuilder builder1 = new TopologyBuilder();// a topology with multiple spouts

        builder1.setSpout("wordSpout", new TestWordSpout(), 1).setCPULoad(20.0).setMemoryLoad(200.0);
        builder1.setBolt("wordCountBolt", new TestWordCounter(), 1).shuffleGrouping("wordSpout").setCPULoad(20.0).setMemoryLoad(200.0);
        StormTopology stormTopology1 = builder1.createTopology();
        Config config = new Config();
        config.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        Map<ExecutorDetails, String> executorMap1 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology1);
        TopologyDetails topology1 = new TopologyDetails("topology1", config, stormTopology1, 0, executorMap1, 0, "user");
        ResourceAwareScheduler rs = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler = rs;
        Topologies topologies = new Topologies(topology1);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        rs.prepare(config);
        rs.schedule(topologies, cluster);
        SchedulerAssignment assignment1 = cluster.getAssignmentById(topology1.getId());
        Map<WorkerSlot, WorkerResources> assignedSlots1 = assignment1.getScheduledResources();
        double assignedMemory = 0.0;
        double assignedCpu = 0.0;
        Set<String> nodesIDs1 = new HashSet<>();
        for (Map.Entry<WorkerSlot, WorkerResources> entry : assignedSlots1.entrySet()) {
            WorkerResources wr = entry.getValue();
            nodesIDs1.add(entry.getKey().getNodeId());
            assignedMemory += (wr.get_mem_on_heap()) + (wr.get_mem_off_heap());
            assignedCpu += wr.get_cpu();
        }
        Collection<ExecutorDetails> executors1 = assignment1.getExecutors();
        Assert.assertEquals(1, assignedSlots1.size());
        Assert.assertEquals(1, nodesIDs1.size());
        Assert.assertEquals(2, executors1.size());
        Assert.assertEquals(400.0, assignedMemory, 0.001);
        Assert.assertEquals(40.0, assignedCpu, 0.001);
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster.getStatusMap().get(topology1.getId()));
    }

    @Test
    public void testResourceLimitation() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(2, 2, 400, 2000);
        TopologyBuilder builder1 = new TopologyBuilder();// a topology with multiple spouts

        builder1.setSpout("wordSpout", new TestWordSpout(), 2).setCPULoad(250.0).setMemoryLoad(1000.0, 200.0);
        builder1.setBolt("wordCountBolt", new TestWordCounter(), 1).shuffleGrouping("wordSpout").setCPULoad(100.0).setMemoryLoad(500.0, 100.0);
        StormTopology stormTopology1 = builder1.createTopology();
        Config config = new Config();
        config.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        Map<ExecutorDetails, String> executorMap1 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology1);
        TopologyDetails topology1 = new TopologyDetails("topology1", config, stormTopology1, 2, executorMap1, 0, "user");
        ResourceAwareScheduler rs = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler = rs;
        Topologies topologies = new Topologies(topology1);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        rs.prepare(config);
        rs.schedule(topologies, cluster);
        SchedulerAssignment assignment1 = cluster.getAssignmentById(topology1.getId());
        Set<WorkerSlot> assignedSlots1 = assignment1.getSlots();
        Set<String> nodesIDs1 = new HashSet<>();
        for (WorkerSlot slot : assignedSlots1) {
            nodesIDs1.add(slot.getNodeId());
        }
        Collection<ExecutorDetails> executors1 = assignment1.getExecutors();
        List<Double> assignedExecutorMemory = new ArrayList<>();
        List<Double> assignedExecutorCpu = new ArrayList<>();
        for (ExecutorDetails executor : executors1) {
            assignedExecutorMemory.add(topology1.getTotalMemReqTask(executor));
            assignedExecutorCpu.add(topology1.getTotalCpuReqTask(executor));
        }
        Collections.sort(assignedExecutorCpu);
        Collections.sort(assignedExecutorMemory);
        Map<ExecutorDetails, SupervisorDetails> executorToSupervisor = new HashMap<>();
        Map<SupervisorDetails, List<ExecutorDetails>> supervisorToExecutors = new HashMap<>();
        Map<Double, Double> cpuAvailableToUsed = new HashMap<>();
        Map<Double, Double> memoryAvailableToUsed = new HashMap<>();
        for (Map.Entry<ExecutorDetails, WorkerSlot> entry : assignment1.getExecutorToSlot().entrySet()) {
            executorToSupervisor.put(entry.getKey(), cluster.getSupervisorById(entry.getValue().getNodeId()));
        }
        for (Map.Entry<ExecutorDetails, SupervisorDetails> entry : executorToSupervisor.entrySet()) {
            List<ExecutorDetails> executorsOnSupervisor = supervisorToExecutors.get(entry.getValue());
            if (executorsOnSupervisor == null) {
                executorsOnSupervisor = new ArrayList();
                supervisorToExecutors.put(entry.getValue(), executorsOnSupervisor);
            }
            executorsOnSupervisor.add(entry.getKey());
        }
        for (Map.Entry<SupervisorDetails, List<ExecutorDetails>> entry : supervisorToExecutors.entrySet()) {
            Double supervisorTotalCpu = entry.getKey().getTotalCpu();
            Double supervisorTotalMemory = entry.getKey().getTotalMemory();
            Double supervisorUsedCpu = 0.0;
            Double supervisorUsedMemory = 0.0;
            for (ExecutorDetails executor : entry.getValue()) {
                supervisorUsedMemory += topology1.getTotalCpuReqTask(executor);
                supervisorTotalCpu += topology1.getTotalMemReqTask(executor);
            }
            cpuAvailableToUsed.put(supervisorTotalCpu, supervisorUsedCpu);
            memoryAvailableToUsed.put(supervisorTotalMemory, supervisorUsedMemory);
        }
        // executor0 resides one one worker (on one), executor1 and executor2 on another worker (on the other node)
        Assert.assertEquals(2, assignedSlots1.size());
        Assert.assertEquals(2, nodesIDs1.size());
        Assert.assertEquals(3, executors1.size());
        Assert.assertEquals(100.0, assignedExecutorCpu.get(0), 0.001);
        Assert.assertEquals(250.0, assignedExecutorCpu.get(1), 0.001);
        Assert.assertEquals(250.0, assignedExecutorCpu.get(2), 0.001);
        Assert.assertEquals(600.0, assignedExecutorMemory.get(0), 0.001);
        Assert.assertEquals(1200.0, assignedExecutorMemory.get(1), 0.001);
        Assert.assertEquals(1200.0, assignedExecutorMemory.get(2), 0.001);
        for (Map.Entry<Double, Double> entry : memoryAvailableToUsed.entrySet()) {
            Assert.assertTrue((((entry.getKey()) - (entry.getValue())) >= 0));
        }
        for (Map.Entry<Double, Double> entry : cpuAvailableToUsed.entrySet()) {
            Assert.assertTrue((((entry.getKey()) - (entry.getValue())) >= 0));
        }
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster.getStatusMap().get(topology1.getId()));
    }

    @Test
    public void testScheduleResilience() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(2, 2, 400, 2000);
        TopologyBuilder builder1 = new TopologyBuilder();
        builder1.setSpout("wordSpout1", new TestWordSpout(), 3);
        StormTopology stormTopology1 = builder1.createTopology();
        Config config1 = new Config();
        config1.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        Map<ExecutorDetails, String> executorMap1 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology1);
        TopologyDetails topology1 = new TopologyDetails("topology1", config1, stormTopology1, 3, executorMap1, 0, "user");
        TopologyBuilder builder2 = new TopologyBuilder();
        builder2.setSpout("wordSpout2", new TestWordSpout(), 2);
        StormTopology stormTopology2 = builder2.createTopology();
        Config config2 = new Config();
        config2.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        // memory requirement is large enough so that two executors can not be fully assigned to one node
        config2.put(TOPOLOGY_COMPONENT_RESOURCES_ONHEAP_MEMORY_MB, 1280.0);
        Map<ExecutorDetails, String> executorMap2 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology2);
        TopologyDetails topology2 = new TopologyDetails("topology2", config2, stormTopology2, 2, executorMap2, 0, "user");
        // Test1: When a worker fails, RAS does not alter existing assignments on healthy workers
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        Topologies topologies = new Topologies(topology2);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config1);
        TestResourceAwareScheduler.scheduler.prepare(config1);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        SchedulerAssignment assignment = cluster.getAssignmentById(topology2.getId());
        // pick a worker to mock as failed
        WorkerSlot failedWorker = new ArrayList(assignment.getSlots()).get(0);
        Map<ExecutorDetails, WorkerSlot> executorToSlot = assignment.getExecutorToSlot();
        List<ExecutorDetails> failedExecutors = new ArrayList<>();
        for (Map.Entry<ExecutorDetails, WorkerSlot> entry : executorToSlot.entrySet()) {
            if (entry.getValue().equals(failedWorker)) {
                failedExecutors.add(entry.getKey());
            }
        }
        for (ExecutorDetails executor : failedExecutors) {
            executorToSlot.remove(executor);// remove executor details assigned to the failed worker

        }
        Map<ExecutorDetails, WorkerSlot> copyOfOldMapping = new HashMap(executorToSlot);
        Set<ExecutorDetails> healthyExecutors = copyOfOldMapping.keySet();
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        SchedulerAssignment newAssignment = cluster.getAssignmentById(topology2.getId());
        Map<ExecutorDetails, WorkerSlot> newExecutorToSlot = newAssignment.getExecutorToSlot();
        for (ExecutorDetails executor : healthyExecutors) {
            Assert.assertEquals(copyOfOldMapping.get(executor), newExecutorToSlot.get(executor));
        }
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster.getStatusMap().get(topology2.getId()));
        // end of Test1
        // Test2: When a supervisor fails, RAS does not alter existing assignments
        executorToSlot = new HashMap();
        executorToSlot.put(new ExecutorDetails(0, 0), new WorkerSlot("r000s000", 0));
        executorToSlot.put(new ExecutorDetails(1, 1), new WorkerSlot("r000s000", 1));
        executorToSlot.put(new ExecutorDetails(2, 2), new WorkerSlot("r000s001", 1));
        Map<String, SchedulerAssignment> existingAssignments = new HashMap<>();
        assignment = new SchedulerAssignmentImpl(topology1.getId(), executorToSlot, null, null);
        existingAssignments.put(topology1.getId(), assignment);
        copyOfOldMapping = new HashMap(executorToSlot);
        Set<ExecutorDetails> existingExecutors = copyOfOldMapping.keySet();
        Map<String, SupervisorDetails> supMap1 = new HashMap(supMap);
        supMap1.remove("r000s000");// mock the supervisor r000s000 as a failed supervisor

        topologies = new Topologies(topology1);
        Cluster cluster1 = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap1, existingAssignments, topologies, config1);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster1);
        newAssignment = cluster1.getAssignmentById(topology1.getId());
        newExecutorToSlot = newAssignment.getExecutorToSlot();
        for (ExecutorDetails executor : existingExecutors) {
            Assert.assertEquals(copyOfOldMapping.get(executor), newExecutorToSlot.get(executor));
        }
        Assert.assertEquals("Fully Scheduled", cluster1.getStatusMap().get(topology1.getId()));
        // end of Test2
        // Test3: When a supervisor and a worker on it fails, RAS does not alter existing assignments
        executorToSlot = new HashMap();
        executorToSlot.put(new ExecutorDetails(0, 0), new WorkerSlot("r000s000", 1));// the worker to orphan

        executorToSlot.put(new ExecutorDetails(1, 1), new WorkerSlot("r000s000", 2));// the worker that fails

        executorToSlot.put(new ExecutorDetails(2, 2), new WorkerSlot("r000s001", 1));// the healthy worker

        existingAssignments = new HashMap();
        assignment = new SchedulerAssignmentImpl(topology1.getId(), executorToSlot, null, null);
        existingAssignments.put(topology1.getId(), assignment);
        // delete one worker of r000s000 (failed) from topo1 assignment to enable actual schedule for testing
        executorToSlot.remove(new ExecutorDetails(1, 1));
        copyOfOldMapping = new HashMap(executorToSlot);
        existingExecutors = copyOfOldMapping.keySet();// namely the two eds on the orphaned worker and the healthy worker

        supMap1 = new HashMap(supMap);
        supMap1.remove("r000s000");// mock the supervisor r000s000 as a failed supervisor

        topologies = new Topologies(topology1);
        cluster1 = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap1, existingAssignments, topologies, config1);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster1);
        newAssignment = cluster1.getAssignmentById(topology1.getId());
        newExecutorToSlot = newAssignment.getExecutorToSlot();
        for (ExecutorDetails executor : existingExecutors) {
            Assert.assertEquals(copyOfOldMapping.get(executor), newExecutorToSlot.get(executor));
        }
        Assert.assertEquals("Fully Scheduled", cluster1.getStatusMap().get(topology1.getId()));
        // end of Test3
        // Test4: Scheduling a new topology does not disturb other assignments unnecessarily
        topologies = new Topologies(topology1);
        cluster1 = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config1);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster1);
        assignment = cluster1.getAssignmentById(topology1.getId());
        executorToSlot = assignment.getExecutorToSlot();
        copyOfOldMapping = new HashMap(executorToSlot);
        topologies = TestUtilsForResourceAwareScheduler.addTopologies(topologies, topology2);
        cluster1 = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config1);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster1);
        newAssignment = cluster1.getAssignmentById(topology1.getId());
        newExecutorToSlot = newAssignment.getExecutorToSlot();
        for (ExecutorDetails executor : copyOfOldMapping.keySet()) {
            Assert.assertEquals(copyOfOldMapping.get(executor), newExecutorToSlot.get(executor));
        }
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster1.getStatusMap().get(topology1.getId()));
        Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster1.getStatusMap().get(topology2.getId()));
    }

    @Test
    public void testHeterogeneousClusterwithDefaultRas() {
        testHeterogeneousCluster(TestResourceAwareScheduler.defaultTopologyConf, DefaultResourceAwareStrategy.class.getSimpleName());
    }

    @Test
    public void testHeterogeneousClusterwithGras() {
        Config grasClusterConfig = ((Config) (TestResourceAwareScheduler.defaultTopologyConf.clone()));
        grasClusterConfig.put(TOPOLOGY_SCHEDULER_STRATEGY, GenericResourceAwareStrategy.class.getName());
        testHeterogeneousCluster(grasClusterConfig, GenericResourceAwareStrategy.class.getSimpleName());
    }

    @Test
    public void testTopologyWorkerMaxHeapSize() {
        // Test1: If RAS spreads executors across multiple workers based on the set limit for a worker used by the topology
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(2, 2, 400, 2000);
        TopologyBuilder builder1 = new TopologyBuilder();
        builder1.setSpout("wordSpout1", new TestWordSpout(), 4);
        StormTopology stormTopology1 = builder1.createTopology();
        Config config1 = new Config();
        config1.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        config1.put(TOPOLOGY_WORKER_MAX_HEAP_SIZE_MB, 128.0);
        Map<ExecutorDetails, String> executorMap1 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology1);
        TopologyDetails topology1 = new TopologyDetails("topology1", config1, stormTopology1, 1, executorMap1, 0, "user");
        ResourceAwareScheduler rs = new ResourceAwareScheduler();
        Topologies topologies = new Topologies(topology1);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config1);
        rs.prepare(config1);
        try {
            rs.schedule(topologies, cluster);
            Assert.assertEquals("Running - Fully Scheduled by DefaultResourceAwareStrategy", cluster.getStatusMap().get(topology1.getId()));
            Assert.assertEquals(4, cluster.getAssignedNumWorkers(topology1));
        } finally {
            rs.cleanup();
        }
        // Test2: test when no more workers are available due to topology worker max heap size limit but there is memory is still available
        // wordSpout2 is going to contain 5 executors that needs scheduling. Each of those executors has a memory requirement of 128.0 MB
        // The cluster contains 4 free WorkerSlots. For this topolology each worker is limited to a max heap size of 128.0
        // Thus, one executor not going to be able to get scheduled thus failing the scheduling of this topology and no executors of this
        // topology will be scheduleded
        TopologyBuilder builder2 = new TopologyBuilder();
        builder2.setSpout("wordSpout2", new TestWordSpout(), 5);
        StormTopology stormTopology2 = builder2.createTopology();
        Config config2 = new Config();
        config2.putAll(TestResourceAwareScheduler.defaultTopologyConf);
        config2.put(TOPOLOGY_WORKER_MAX_HEAP_SIZE_MB, 128.0);
        Map<ExecutorDetails, String> executorMap2 = TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology2);
        TopologyDetails topology2 = new TopologyDetails("topology2", config2, stormTopology2, 1, executorMap2, 0, "user");
        topologies = new Topologies(topology2);
        cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config2);
        rs.prepare(config2);
        try {
            rs.schedule(topologies, cluster);
            String status = cluster.getStatusMap().get(topology2.getId());
            assert status.startsWith("Not enough resources to schedule") : status;
            assert status.endsWith("5 executors not scheduled") : status;
            Assert.assertEquals(5, cluster.getUnassignedExecutors(topology2).size());
        } finally {
            rs.cleanup();
        }
    }

    @Test
    public void testReadInResourceAwareSchedulerUserPools() {
        Map<String, Object> fromFile = Utils.findAndReadConfigFile("user-resource-pools.yaml", false);
        TestResourceAwareScheduler.LOG.info("fromFile: {}", fromFile);
        ConfigValidation.validateFields(fromFile);
    }

    @Test
    public void testSubmitUsersWithNoGuarantees() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(4, 4, 100, 1000);
        Map<String, Map<String, Number>> resourceUserPool = TestUtilsForResourceAwareScheduler.userResourcePool(TestUtilsForResourceAwareScheduler.userRes("jerry", 200, 2000));
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(100, 500, 500, resourceUserPool);
        Topologies topologies = new Topologies(TestUtilsForResourceAwareScheduler.genTopology("topo-1", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-2", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-3", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-4", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-5", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 20, "bobby"));
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        TestUtilsForResourceAwareScheduler.assertTopologiesFullyScheduled(cluster, "topo-1", "topo-2", "topo-3", "topo-4");
        TestUtilsForResourceAwareScheduler.assertTopologiesNotScheduled(cluster, "topo-5");
    }

    @Test
    public void testMultipleUsers() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(20, 4, 1000, (1024 * 10));
        Map<String, Map<String, Number>> resourceUserPool = TestUtilsForResourceAwareScheduler.userResourcePool(TestUtilsForResourceAwareScheduler.userRes("jerry", 1000, 8192), TestUtilsForResourceAwareScheduler.userRes("bobby", 10000, 32768), TestUtilsForResourceAwareScheduler.userRes("derek", 5000, 16384));
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(10, 128, 0, resourceUserPool);
        Topologies topologies = new Topologies(TestUtilsForResourceAwareScheduler.genTopology("topo-1", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-2", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 8), 29, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-3", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 16), 29, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-4", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 16), 20, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-5", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 24), 29, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-6", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 2), 20, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-7", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 8), 29, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-8", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 16), 29, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-9", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 16), 20, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-10", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 24), 29, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-11", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 2), 20, "derek"), TestUtilsForResourceAwareScheduler.genTopology("topo-12", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 8), 29, "derek"), TestUtilsForResourceAwareScheduler.genTopology("topo-13", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 16), 29, "derek"), TestUtilsForResourceAwareScheduler.genTopology("topo-14", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 16), 20, "derek"), TestUtilsForResourceAwareScheduler.genTopology("topo-15", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 24), 29, "derek"));
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        for (TopologyDetails td : topologies) {
            TestUtilsForResourceAwareScheduler.assertTopologiesFullyScheduled(cluster, td.getName());
        }
    }

    @Test
    public void testHandlingClusterSubscription() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(1, 4, 200, (1024 * 10));
        Map<String, Map<String, Number>> resourceUserPool = TestUtilsForResourceAwareScheduler.userResourcePool(TestUtilsForResourceAwareScheduler.userRes("jerry", 1000, 8192), TestUtilsForResourceAwareScheduler.userRes("bobby", 10000, 32768), TestUtilsForResourceAwareScheduler.userRes("derek", 5000, 16384));
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(10, 128, 0, resourceUserPool);
        Topologies topologies = new Topologies(TestUtilsForResourceAwareScheduler.genTopology("topo-1", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-2", config, 5, 15, 1, 1, ((TestResourceAwareScheduler.currentTime) - 8), 29, "jerry"));
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        TestUtilsForResourceAwareScheduler.assertTopologiesFullyScheduled(cluster, "topo-1");
        TestUtilsForResourceAwareScheduler.assertTopologiesNotScheduled(cluster, "topo-2");
    }

    /**
     * Test correct behavior when a supervisor dies.  Check if the scheduler handles it correctly and evicts the correct
     * topology when rescheduling the executors from the died supervisor
     */
    @Test
    public void testFaultTolerance() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(6, 4, 100, 1000);
        Map<String, Map<String, Number>> resourceUserPool = TestUtilsForResourceAwareScheduler.userResourcePool(TestUtilsForResourceAwareScheduler.userRes("jerry", 50, 500), TestUtilsForResourceAwareScheduler.userRes("bobby", 200, 2000), TestUtilsForResourceAwareScheduler.userRes("derek", 100, 1000));
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(100, 500, 500, resourceUserPool);
        Topologies topologies = new Topologies(TestUtilsForResourceAwareScheduler.genTopology("topo-1", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 21, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-2", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry"), TestUtilsForResourceAwareScheduler.genTopology("topo-3", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-4", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "bobby"), TestUtilsForResourceAwareScheduler.genTopology("topo-5", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 29, "derek"), TestUtilsForResourceAwareScheduler.genTopology("topo-6", config, 1, 0, 1, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "derek"));
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        TestUtilsForResourceAwareScheduler.assertTopologiesFullyScheduled(cluster, "topo-1", "topo-2", "topo-3", "topo-4", "topo-5", "topo-6");
        // fail supervisor
        SupervisorDetails supFailed = cluster.getSupervisors().values().iterator().next();
        TestResourceAwareScheduler.LOG.info("/***** failing supervisor: {} ****/", supFailed.getHost());
        supMap.remove(supFailed.getId());
        Map<String, SchedulerAssignmentImpl> newAssignments = new HashMap<>();
        for (Map.Entry<String, SchedulerAssignment> topoToAssignment : cluster.getAssignments().entrySet()) {
            String topoId = topoToAssignment.getKey();
            SchedulerAssignment assignment = topoToAssignment.getValue();
            Map<ExecutorDetails, WorkerSlot> executorToSlots = new HashMap<ExecutorDetails, WorkerSlot>();
            for (Map.Entry<ExecutorDetails, WorkerSlot> execToWorker : assignment.getExecutorToSlot().entrySet()) {
                ExecutorDetails exec = execToWorker.getKey();
                WorkerSlot ws = execToWorker.getValue();
                if (!(ws.getNodeId().equals(supFailed.getId()))) {
                    executorToSlots.put(exec, ws);
                }
            }
            newAssignments.put(topoId, new SchedulerAssignmentImpl(topoId, executorToSlots, null, null));
        }
        Map<String, String> statusMap = cluster.getStatusMap();
        TestResourceAwareScheduler.LOG.warn("Rescheduling with removed Supervisor....");
        cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, newAssignments, topologies, config);
        cluster.setStatusMap(statusMap);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        TestUtilsForResourceAwareScheduler.assertTopologiesFullyScheduled(cluster, "topo-2", "topo-3", "topo-4", "topo-5", "topo-6");
        TestUtilsForResourceAwareScheduler.assertTopologiesNotScheduled(cluster, "topo-1");
    }

    /**
     * test if free slots on nodes work correctly
     */
    @Test
    public void testNodeFreeSlot() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(4, 4, 100, 1000);
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(100, 500, 500, null);
        Topologies topologies = new Topologies(TestUtilsForResourceAwareScheduler.genTopology("topo-1", config, 1, 0, 2, 0, ((TestResourceAwareScheduler.currentTime) - 2), 29, "user"), TestUtilsForResourceAwareScheduler.genTopology("topo-2", config, 1, 0, 2, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "user"));
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        Map<String, RAS_Node> nodes = RAS_Nodes.getAllNodesFrom(cluster);
        for (SchedulerAssignment assignment : cluster.getAssignments().values()) {
            for (Map.Entry<WorkerSlot, WorkerResources> entry : new HashMap(assignment.getScheduledResources()).entrySet()) {
                WorkerSlot ws = entry.getKey();
                WorkerResources wr = entry.getValue();
                double memoryBefore = nodes.get(ws.getNodeId()).getAvailableMemoryResources();
                double cpuBefore = nodes.get(ws.getNodeId()).getAvailableCpuResources();
                double memoryUsedByWorker = (wr.get_mem_on_heap()) + (wr.get_mem_off_heap());
                Assert.assertEquals("Check if memory used by worker is calculated correctly", 1000.0, memoryUsedByWorker, 0.001);
                double cpuUsedByWorker = wr.get_cpu();
                Assert.assertEquals("Check if CPU used by worker is calculated correctly", 100.0, cpuUsedByWorker, 0.001);
                nodes.get(ws.getNodeId()).free(ws);
                double memoryAfter = nodes.get(ws.getNodeId()).getAvailableMemoryResources();
                double cpuAfter = nodes.get(ws.getNodeId()).getAvailableCpuResources();
                Assert.assertEquals("Check if free correctly frees amount of memory", (memoryBefore + memoryUsedByWorker), memoryAfter, 0.001);
                Assert.assertEquals("Check if free correctly frees amount of memory", (cpuBefore + cpuUsedByWorker), cpuAfter, 0.001);
                Assert.assertFalse("Check if worker was removed from assignments", assignment.getSlotToExecutors().containsKey(ws));
            }
        }
    }

    /**
     * When the first topology failed to be scheduled make sure subsequent schedulings can still succeed
     */
    @Test
    public void testSchedulingAfterFailedScheduling() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(8, 4, 100, 1000);
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(100, 500, 500, null);
        TopologyDetails topo1 = TestUtilsForResourceAwareScheduler.genTopology("topo-1", config, 8, 0, 2, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "jerry");
        TopologyDetails topo2 = TestUtilsForResourceAwareScheduler.genTopology("topo-2", config, 2, 0, 2, 0, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry");
        TopologyDetails topo3 = TestUtilsForResourceAwareScheduler.genTopology("topo-3", config, 1, 2, 1, 1, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry");
        Topologies topologies = new Topologies(topo1, topo2, topo3);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap<String, SchedulerAssignmentImpl>(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        Assert.assertTrue("Topo-2 scheduled?", ((cluster.getAssignmentById(topo2.getId())) != null));
        Assert.assertEquals("Topo-2 all executors scheduled?", 4, cluster.getAssignmentById(topo2.getId()).getExecutorToSlot().size());
        Assert.assertTrue("Topo-3 scheduled?", ((cluster.getAssignmentById(topo3.getId())) != null));
        Assert.assertEquals("Topo-3 all executors scheduled?", 3, cluster.getAssignmentById(topo3.getId()).getExecutorToSlot().size());
    }

    /**
     * Test multiple spouts and cyclic topologies
     */
    @Test
    public void testMultipleSpoutsAndCyclicTopologies() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout-1", new TestUtilsForResourceAwareScheduler.TestSpout(), 5);
        builder.setSpout("spout-2", new TestUtilsForResourceAwareScheduler.TestSpout(), 5);
        builder.setBolt("bolt-1", new TestUtilsForResourceAwareScheduler.TestBolt(), 5).shuffleGrouping("spout-1").shuffleGrouping("bolt-3");
        builder.setBolt("bolt-2", new TestUtilsForResourceAwareScheduler.TestBolt(), 5).shuffleGrouping("bolt-1");
        builder.setBolt("bolt-3", new TestUtilsForResourceAwareScheduler.TestBolt(), 5).shuffleGrouping("bolt-2").shuffleGrouping("spout-2");
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(25, 1, 100, 1000);
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(100, 500, 500, null);
        StormTopology stormTopology = builder.createTopology();
        config.put(TOPOLOGY_SUBMITTER_USER, "jerry");
        TopologyDetails topo = new TopologyDetails("topo-1", config, stormTopology, 0, TestUtilsForResourceAwareScheduler.genExecsAndComps(stormTopology), 0, "jerry");
        Topologies topologies = new Topologies(topo);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap<String, SchedulerAssignmentImpl>(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        Assert.assertTrue("Topo scheduled?", ((cluster.getAssignmentById(topo.getId())) != null));
        Assert.assertEquals("Topo all executors scheduled?", 25, cluster.getAssignmentById(topo.getId()).getExecutorToSlot().size());
    }

    @Test
    public void testSchedulerStrategyWhitelist() {
        Map<String, Object> config = ConfigUtils.readStormConfig();
        String allowed = DefaultResourceAwareStrategy.class.getName();
        config.put(NIMBUS_SCHEDULER_STRATEGY_CLASS_WHITELIST, Arrays.asList(allowed));
        Object sched = ReflectionUtils.newSchedulerStrategyInstance(allowed, config);
        Assert.assertEquals(sched.getClass().getName(), allowed);
    }

    @Test
    public void testSchedulerStrategyWhitelistException() {
        Map<String, Object> config = ConfigUtils.readStormConfig();
        String allowed = "org.apache.storm.scheduler.resource.strategies.scheduling.SomeNonExistantStrategy";
        String notAllowed = DefaultResourceAwareStrategy.class.getName();
        config.put(NIMBUS_SCHEDULER_STRATEGY_CLASS_WHITELIST, Arrays.asList(allowed));
        Assertions.assertThrows(DisallowedStrategyException.class, () -> ReflectionUtils.newSchedulerStrategyInstance(notAllowed, config));
    }

    @Test
    public void testSchedulerStrategyEmptyWhitelist() {
        Map<String, Object> config = ConfigUtils.readStormConfig();
        String allowed = DefaultResourceAwareStrategy.class.getName();
        Object sched = ReflectionUtils.newSchedulerStrategyInstance(allowed, config);
        Assert.assertEquals(sched.getClass().getName(), allowed);
    }

    @PerformanceTest
    @Test
    public void testLargeTopologiesOnLargeClusters() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(30), () -> testLargeTopologiesCommon(DefaultResourceAwareStrategy.class.getName(), false, 1));
    }

    @PerformanceTest
    @Test
    public void testLargeTopologiesOnLargeClustersGras() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(75), () -> testLargeTopologiesCommon(GenericResourceAwareStrategy.class.getName(), true, 1));
    }

    public static class NeverEndingSchedulingStrategy extends BaseResourceAwareStrategy implements IStrategy {
        @Override
        public void prepare(Map<String, Object> config) {
        }

        @Override
        protected TreeSet<ObjectResources> sortObjectResources(BaseResourceAwareStrategy.AllResources allResources, ExecutorDetails exec, TopologyDetails topologyDetails, ExistingScheduleFunc existingScheduleFunc) {
            // NO-OP
            return null;
        }

        @Override
        public SchedulingResult schedule(Cluster schedulingState, TopologyDetails td) {
            this.cluster = schedulingState;
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    TestResourceAwareScheduler.LOG.info("scheduling interrupted");
                    return null;
                }
            } 
        }
    }

    @Test
    public void testStrategyTakingTooLong() {
        INimbus iNimbus = new TestUtilsForResourceAwareScheduler.INimbusTest();
        Map<String, SupervisorDetails> supMap = TestUtilsForResourceAwareScheduler.genSupervisors(8, 4, 100, 1000);
        Config config = TestUtilsForResourceAwareScheduler.createClusterConfig(100, 500, 500, null);
        List<String> allowedSchedulerStrategies = new ArrayList();
        allowedSchedulerStrategies.add(DefaultResourceAwareStrategy.class.getName());
        allowedSchedulerStrategies.add(TestResourceAwareScheduler.NeverEndingSchedulingStrategy.class.getName());
        config.put(NIMBUS_SCHEDULER_STRATEGY_CLASS_WHITELIST, allowedSchedulerStrategies);
        config.put(SCHEDULING_TIMEOUT_SECONDS_PER_TOPOLOGY, 30);
        TopologyDetails topo1 = TestUtilsForResourceAwareScheduler.genTopology("topo-1", config, 1, 0, 2, 0, ((TestResourceAwareScheduler.currentTime) - 2), 10, "jerry");
        TopologyDetails topo3 = TestUtilsForResourceAwareScheduler.genTopology("topo-3", config, 1, 2, 1, 1, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry");
        config.put(TOPOLOGY_SCHEDULER_STRATEGY, TestResourceAwareScheduler.NeverEndingSchedulingStrategy.class.getName());
        TopologyDetails topo2 = TestUtilsForResourceAwareScheduler.genTopology("topo-2", config, 2, 0, 2, 0, ((TestResourceAwareScheduler.currentTime) - 2), 20, "jerry");
        Topologies topologies = new Topologies(topo1, topo2, topo3);
        Cluster cluster = new Cluster(iNimbus, new org.apache.storm.scheduler.resource.normalization.ResourceMetrics(new StormMetricsRegistry()), supMap, new HashMap(), topologies, config);
        TestResourceAwareScheduler.scheduler = new ResourceAwareScheduler();
        TestResourceAwareScheduler.scheduler.prepare(config);
        TestResourceAwareScheduler.scheduler.schedule(topologies, cluster);
        Assert.assertTrue("Topo-1 scheduled?", ((cluster.getAssignmentById(topo1.getId())) != null));
        Assert.assertEquals("Topo-1 all executors scheduled?", 2, cluster.getAssignmentById(topo1.getId()).getExecutorToSlot().size());
        Assert.assertTrue("Topo-2 not scheduled", ((cluster.getAssignmentById(topo2.getId())) == null));
        Assert.assertEquals((((("Scheduling took too long for " + (topo2.getId())) + " using strategy ") + (TestResourceAwareScheduler.NeverEndingSchedulingStrategy.class.getName())) + " timeout after 30 seconds using config scheduling.timeout.seconds.per.topology."), cluster.getStatusMap().get(topo2.getId()));
        Assert.assertTrue("Topo-3 scheduled?", ((cluster.getAssignmentById(topo3.getId())) != null));
        Assert.assertEquals("Topo-3 all executors scheduled?", 3, cluster.getAssignmentById(topo3.getId()).getExecutorToSlot().size());
    }
}

