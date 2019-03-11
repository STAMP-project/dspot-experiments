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
package org.apache.ambari.server.state.stack;


import Direction.DOWNGRADE;
import Direction.UPGRADE;
import ParallelScheduler.DEFAULT_MAX_DEGREE_OF_PARALLELISM;
import Task.Type.RESTART;
import UpgradePack.OrderService;
import UpgradeType.HOST_ORDERED;
import category.StackUpgradeTest;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.stack.upgrade.ClusterGrouping;
import org.apache.ambari.server.stack.upgrade.ConfigureTask;
import org.apache.ambari.server.stack.upgrade.ExecuteStage;
import org.apache.ambari.server.stack.upgrade.Grouping;
import org.apache.ambari.server.stack.upgrade.HostOrderGrouping;
import org.apache.ambari.server.stack.upgrade.RestartGrouping;
import org.apache.ambari.server.stack.upgrade.RestartTask;
import org.apache.ambari.server.stack.upgrade.ServiceCheckGrouping;
import org.apache.ambari.server.stack.upgrade.StopGrouping;
import org.apache.ambari.server.stack.upgrade.Task;
import org.apache.ambari.server.stack.upgrade.UpdateStackGrouping;
import org.apache.ambari.server.stack.upgrade.UpgradePack;
import org.apache.ambari.server.stack.upgrade.UpgradePack.PrerequisiteCheckConfig;
import org.apache.ambari.server.stack.upgrade.UpgradePack.ProcessingComponent;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for the upgrade pack
 */
@Category({ StackUpgradeTest.class })
public class UpgradePackTest {
    private Injector injector;

    private AmbariMetaInfo ambariMetaInfo;

    private static final Logger LOG = LoggerFactory.getLogger(UpgradePackTest.class);

    /**
     * Tests that boolean values are property serialized in the upgrade pack.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testIsDowngradeAllowed() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.2.0");
        Assert.assertTrue(((upgrades.size()) > 0));
        String upgradePackWithoutDowngrade = "upgrade_test_no_downgrade";
        boolean foundAtLeastOnePackWithoutDowngrade = false;
        for (String key : upgrades.keySet()) {
            UpgradePack upgradePack = upgrades.get(key);
            if (upgradePack.getName().equals(upgradePackWithoutDowngrade)) {
                foundAtLeastOnePackWithoutDowngrade = true;
                Assert.assertFalse(upgradePack.isDowngradeAllowed());
                continue;
            }
            Assert.assertTrue(upgradePack.isDowngradeAllowed());
        }
        Assert.assertTrue(foundAtLeastOnePackWithoutDowngrade);
    }

    @Test
    public void testExistence() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("foo", "bar");
        Assert.assertTrue(upgrades.isEmpty());
        upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        Assert.assertTrue(((upgrades.size()) > 0));
        Assert.assertTrue(upgrades.containsKey("upgrade_test"));
    }

    @Test
    public void testUpgradeParsing() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        Assert.assertTrue(((upgrades.size()) > 0));
        Assert.assertTrue(upgrades.containsKey("upgrade_test"));
        UpgradePack upgrade = upgrades.get("upgrade_test");
        Assert.assertEquals("2.2.*.*", upgrade.getTarget());
        Map<String, List<String>> expectedStages = new LinkedHashMap<String, List<String>>() {
            {
                put("ZOOKEEPER", Arrays.asList("ZOOKEEPER_SERVER"));
                put("HDFS", Arrays.asList("NAMENODE", "DATANODE"));
            }
        };
        // !!! test the tasks
        int i = 0;
        for (Map.Entry<String, List<String>> entry : expectedStages.entrySet()) {
            Assert.assertTrue(upgrade.getTasks().containsKey(entry.getKey()));
            Assert.assertEquals((i++), indexOf(upgrade.getTasks(), entry.getKey()));
            // check that the number of components matches
            Assert.assertEquals(entry.getValue().size(), upgrade.getTasks().get(entry.getKey()).size());
            // check component ordering
            int j = 0;
            for (String comp : entry.getValue()) {
                Assert.assertEquals((j++), indexOf(upgrade.getTasks().get(entry.getKey()), comp));
            }
        }
        // !!! test specific tasks
        Assert.assertTrue(upgrade.getTasks().containsKey("HDFS"));
        Assert.assertTrue(upgrade.getTasks().get("HDFS").containsKey("NAMENODE"));
        ProcessingComponent pc = upgrade.getTasks().get("HDFS").get("NAMENODE");
        Assert.assertNotNull(pc.preTasks);
        Assert.assertNotNull(pc.postTasks);
        Assert.assertNotNull(pc.tasks);
        Assert.assertNotNull(pc.preDowngradeTasks);
        Assert.assertNotNull(pc.postDowngradeTasks);
        Assert.assertEquals(1, pc.tasks.size());
        Assert.assertEquals(3, pc.preDowngradeTasks.size());
        Assert.assertEquals(1, pc.postDowngradeTasks.size());
        Assert.assertEquals(RESTART, pc.tasks.get(0).getType());
        Assert.assertEquals(RestartTask.class, pc.tasks.get(0).getClass());
        Assert.assertTrue(upgrade.getTasks().containsKey("ZOOKEEPER"));
        Assert.assertTrue(upgrade.getTasks().get("ZOOKEEPER").containsKey("ZOOKEEPER_SERVER"));
        pc = upgrade.getTasks().get("HDFS").get("DATANODE");
        Assert.assertNotNull(pc.preDowngradeTasks);
        Assert.assertEquals(0, pc.preDowngradeTasks.size());
        Assert.assertNotNull(pc.postDowngradeTasks);
        Assert.assertEquals(1, pc.postDowngradeTasks.size());
        pc = upgrade.getTasks().get("ZOOKEEPER").get("ZOOKEEPER_SERVER");
        Assert.assertNotNull(pc.preTasks);
        Assert.assertEquals(1, pc.preTasks.size());
        Assert.assertNotNull(pc.postTasks);
        Assert.assertEquals(1, pc.postTasks.size());
        Assert.assertNotNull(pc.tasks);
        Assert.assertEquals(1, pc.tasks.size());
        pc = upgrade.getTasks().get("YARN").get("NODEMANAGER");
        Assert.assertNotNull(pc.preTasks);
        Assert.assertEquals(2, pc.preTasks.size());
        Task t = pc.preTasks.get(1);
        Assert.assertEquals(ConfigureTask.class, t.getClass());
        ConfigureTask ct = ((ConfigureTask) (t));
        // check that the Configure task successfully parsed id
        Assert.assertEquals("hdp_2_1_1_nm_pre_upgrade", ct.getId());
        Assert.assertFalse(ct.supportsPatch);
    }

    @Test
    public void testGroupOrdersForRolling() {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        Assert.assertTrue(((upgrades.size()) > 0));
        Assert.assertTrue(upgrades.containsKey("upgrade_test_checks"));
        UpgradePack upgrade = upgrades.get("upgrade_test_checks");
        PrerequisiteCheckConfig prerequisiteCheckConfig = upgrade.getPrerequisiteCheckConfig();
        Assert.assertNotNull(prerequisiteCheckConfig);
        Assert.assertNotNull(prerequisiteCheckConfig.globalProperties);
        Assert.assertTrue(prerequisiteCheckConfig.getGlobalProperties().containsKey("global-property-1"));
        Assert.assertEquals("global-value-1", prerequisiteCheckConfig.getGlobalProperties().get("global-property-1"));
        Assert.assertNotNull(prerequisiteCheckConfig.prerequisiteCheckProperties);
        Assert.assertEquals(2, prerequisiteCheckConfig.prerequisiteCheckProperties.size());
        Assert.assertNotNull(prerequisiteCheckConfig.getCheckProperties("org.apache.ambari.server.checks.ServicesMapReduceDistributedCacheCheck"));
        Assert.assertTrue(prerequisiteCheckConfig.getCheckProperties("org.apache.ambari.server.checks.ServicesMapReduceDistributedCacheCheck").containsKey("dfs-protocols-regex"));
        Assert.assertEquals("^([^:]*dfs|wasb|ecs):.*", prerequisiteCheckConfig.getCheckProperties("org.apache.ambari.server.checks.ServicesMapReduceDistributedCacheCheck").get("dfs-protocols-regex"));
        Assert.assertNotNull(prerequisiteCheckConfig.getCheckProperties("org.apache.ambari.server.checks.ServicesTezDistributedCacheCheck"));
        Assert.assertTrue(prerequisiteCheckConfig.getCheckProperties("org.apache.ambari.server.checks.ServicesTezDistributedCacheCheck").containsKey("dfs-protocols-regex"));
        Assert.assertEquals("^([^:]*dfs|wasb|ecs):.*", prerequisiteCheckConfig.getCheckProperties("org.apache.ambari.server.checks.ServicesTezDistributedCacheCheck").get("dfs-protocols-regex"));
        List<String> expected_up = Arrays.asList("PRE_CLUSTER", "ZOOKEEPER", "CORE_MASTER", "SERVICE_CHECK_1", "CORE_SLAVES", "SERVICE_CHECK_2", "OOZIE", "POST_CLUSTER");
        List<String> expected_down = Arrays.asList("PRE_CLUSTER", "OOZIE", "CORE_SLAVES", "SERVICE_CHECK_2", "CORE_MASTER", "SERVICE_CHECK_1", "ZOOKEEPER", "POST_CLUSTER");
        Grouping serviceCheckGroup = null;
        int i = 0;
        List<Grouping> groups = upgrade.getGroups(UPGRADE);
        for (Grouping g : groups) {
            Assert.assertEquals(expected_up.get(i), g.name);
            i++;
            if (g.name.equals("SERVICE_CHECK_1")) {
                serviceCheckGroup = g;
            }
        }
        List<String> expected_priority = Arrays.asList("HDFS", "HBASE", "YARN");
        Assert.assertNotNull(serviceCheckGroup);
        Assert.assertEquals(ServiceCheckGrouping.class, serviceCheckGroup.getClass());
        ServiceCheckGrouping scg = ((ServiceCheckGrouping) (serviceCheckGroup));
        Set<String> priorities = scg.getPriorities();
        Assert.assertEquals(3, priorities.size());
        i = 0;
        for (String s : priorities) {
            Assert.assertEquals(expected_priority.get((i++)), s);
        }
        i = 0;
        groups = upgrade.getGroups(DOWNGRADE);
        for (Grouping g : groups) {
            Assert.assertEquals(expected_down.get(i), g.name);
            i++;
        }
    }

    // TODO AMBARI-12698, add the Downgrade case
    @Test
    public void testGroupOrdersForNonRolling() {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        Assert.assertTrue(((upgrades.size()) > 0));
        Assert.assertTrue(upgrades.containsKey("upgrade_test_nonrolling"));
        UpgradePack upgrade = upgrades.get("upgrade_test_nonrolling");
        List<String> expected_up = Arrays.asList("PRE_CLUSTER", "Stop High-Level Daemons", "Backups", "Stop Low-Level Daemons", "UPDATE_DESIRED_REPOSITORY_ID", "ALL_HOST_OPS", "ZOOKEEPER", "HDFS", "MR and YARN", "POST_CLUSTER");
        List<String> expected_down = Arrays.asList("Restore Backups", "UPDATE_DESIRED_REPOSITORY_ID", "ALL_HOST_OPS", "ZOOKEEPER", "HDFS", "MR and YARN", "POST_CLUSTER");
        Iterator<String> itr_up = expected_up.iterator();
        List<Grouping> upgrade_groups = upgrade.getGroups(UPGRADE);
        for (Grouping g : upgrade_groups) {
            Assert.assertEquals(true, itr_up.hasNext());
            Assert.assertEquals(itr_up.next(), g.name);
        }
        Iterator<String> itr_down = expected_down.iterator();
        List<Grouping> downgrade_groups = upgrade.getGroups(DOWNGRADE);
        for (Grouping g : downgrade_groups) {
            Assert.assertEquals(true, itr_down.hasNext());
            Assert.assertEquals(itr_down.next(), g.name);
        }
    }

    @Test
    public void testDirectionForRolling() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        Assert.assertTrue(((upgrades.size()) > 0));
        Assert.assertTrue(upgrades.containsKey("upgrade_direction"));
        UpgradePack upgrade = upgrades.get("upgrade_direction");
        Assert.assertTrue(((upgrade.getType()) == (UpgradeType.ROLLING)));
        List<Grouping> groups = upgrade.getGroups(UPGRADE);
        Assert.assertEquals(4, groups.size());
        Grouping group = groups.get(2);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        ClusterGrouping cluster_group = ((ClusterGrouping) (group));
        Assert.assertEquals("Run on All", group.title);
        cluster_group = ((ClusterGrouping) (groups.get(3)));
        List<ExecuteStage> stages = cluster_group.executionStages;
        Assert.assertEquals(3, stages.size());
        Assert.assertNotNull(stages.get(0).intendedDirection);
        Assert.assertEquals(DOWNGRADE, stages.get(0).intendedDirection);
        groups = upgrade.getGroups(DOWNGRADE);
        Assert.assertEquals(3, groups.size());
        // there are two clustergroupings at the end
        group = groups.get(1);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        Assert.assertEquals("Run on All", group.title);
        group = groups.get(2);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        Assert.assertEquals("Finalize Upgrade", group.title);
    }

    @Test
    public void testSkippableFailures() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        Set<String> keys = upgrades.keySet();
        for (String key : keys) {
            Assert.assertFalse(upgrades.get(key).isComponentFailureAutoSkipped());
            Assert.assertFalse(upgrades.get(key).isServiceCheckFailureAutoSkipped());
        }
        upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.2.0");
        UpgradePack upgradePack = upgrades.get("upgrade_test_skip_failures");
        Assert.assertTrue(upgradePack.isComponentFailureAutoSkipped());
        Assert.assertTrue(upgradePack.isServiceCheckFailureAutoSkipped());
    }

    /**
     * Tests that the XML for not auto skipping skippable failures works.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoAutoSkipFailure() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.2.0");
        UpgradePack upgradePack = upgrades.get("upgrade_test_skip_failures");
        List<Grouping> groups = upgradePack.getGroups(UPGRADE);
        for (Grouping grouping : groups) {
            if (grouping.name.equals("SKIPPABLE_BUT_NOT_AUTO_SKIPPABLE")) {
                Assert.assertFalse(grouping.supportsAutoSkipOnFailure);
            } else {
                Assert.assertTrue(grouping.supportsAutoSkipOnFailure);
            }
        }
    }

    @Test
    public void testDirectionForNonRolling() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        Assert.assertTrue(((upgrades.size()) > 0));
        Assert.assertTrue(upgrades.containsKey("upgrade_test_nonrolling"));
        UpgradePack upgrade = upgrades.get("upgrade_test_nonrolling");
        Assert.assertTrue(((upgrade.getType()) == (UpgradeType.NON_ROLLING)));
        List<Grouping> groups = upgrade.getGroups(UPGRADE);
        Assert.assertEquals(10, groups.size());
        Grouping group = null;
        ClusterGrouping clusterGroup = null;
        UpdateStackGrouping updateStackGroup = null;
        StopGrouping stopGroup = null;
        RestartGrouping restartGroup = null;
        group = groups.get(0);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        clusterGroup = ((ClusterGrouping) (group));
        Assert.assertEquals("Prepare Upgrade", clusterGroup.title);
        Assert.assertNull(clusterGroup.parallelScheduler);
        group = groups.get(1);
        Assert.assertEquals(StopGrouping.class, group.getClass());
        stopGroup = ((StopGrouping) (group));
        Assert.assertEquals("Stop Daemons for High-Level Services", stopGroup.title);
        Assert.assertNotNull(stopGroup.parallelScheduler);
        Assert.assertEquals(DEFAULT_MAX_DEGREE_OF_PARALLELISM, stopGroup.parallelScheduler.maxDegreeOfParallelism);
        group = groups.get(2);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        clusterGroup = ((ClusterGrouping) (group));
        Assert.assertEquals("Take Backups", clusterGroup.title);
        Assert.assertNull(clusterGroup.parallelScheduler);
        group = groups.get(3);
        Assert.assertEquals(StopGrouping.class, group.getClass());
        stopGroup = ((StopGrouping) (group));
        Assert.assertEquals("Stop Daemons for Low-Level Services", stopGroup.title);
        Assert.assertNotNull(stopGroup.parallelScheduler);
        Assert.assertEquals(DEFAULT_MAX_DEGREE_OF_PARALLELISM, stopGroup.parallelScheduler.maxDegreeOfParallelism);
        group = groups.get(4);
        Assert.assertEquals(UpdateStackGrouping.class, group.getClass());
        updateStackGroup = ((UpdateStackGrouping) (group));
        Assert.assertEquals("Update Desired Stack Id", updateStackGroup.title);
        Assert.assertNull(updateStackGroup.parallelScheduler);
        group = groups.get(5);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        clusterGroup = ((ClusterGrouping) (group));
        Assert.assertEquals("Set Version On All Hosts", clusterGroup.title);
        Assert.assertNull(clusterGroup.parallelScheduler);
        group = groups.get(6);
        Assert.assertEquals(RestartGrouping.class, group.getClass());
        restartGroup = ((RestartGrouping) (group));
        Assert.assertEquals("Zookeeper", restartGroup.title);
        Assert.assertNull(restartGroup.parallelScheduler);
        group = groups.get(7);
        Assert.assertEquals(RestartGrouping.class, group.getClass());
        restartGroup = ((RestartGrouping) (group));
        Assert.assertEquals("HDFS", restartGroup.title);
        Assert.assertNotNull(restartGroup.parallelScheduler);
        Assert.assertEquals(2, restartGroup.parallelScheduler.maxDegreeOfParallelism);
        group = groups.get(8);
        Assert.assertEquals(RestartGrouping.class, group.getClass());
        restartGroup = ((RestartGrouping) (group));
        Assert.assertEquals("MR and YARN", restartGroup.title);
        Assert.assertNotNull(restartGroup.parallelScheduler);
        Assert.assertEquals(DEFAULT_MAX_DEGREE_OF_PARALLELISM, restartGroup.parallelScheduler.maxDegreeOfParallelism);
        group = groups.get(9);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        clusterGroup = ((ClusterGrouping) (group));
        Assert.assertEquals("Finalize {{direction.text.proper}}", clusterGroup.title);
        Assert.assertNull(clusterGroup.parallelScheduler);
    }

    /**
     * Tests that the service level XML merges correctly for 2.0.5/HDFS/HDP/2.2.0.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testServiceLevelUpgradePackMerge() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.2.0");
        Assert.assertTrue(upgrades.containsKey("upgrade_test_15388"));
        UpgradePack upgradePack = upgrades.get("upgrade_test_15388");
        List<String> checks = upgradePack.getPrerequisiteChecks();
        Assert.assertEquals(11, checks.size());
        Assert.assertTrue(checks.contains("org.apache.ambari.server.checks.FooCheck"));
        List<Grouping> groups = upgradePack.getGroups(UPGRADE);
        Assert.assertEquals(8, groups.size());
        Grouping group = groups.get(0);
        Assert.assertEquals(ClusterGrouping.class, group.getClass());
        ClusterGrouping cluster_group = ((ClusterGrouping) (group));
        Assert.assertEquals("Pre {{direction.text.proper}}", cluster_group.title);
        List<ExecuteStage> stages = cluster_group.executionStages;
        Assert.assertEquals(5, stages.size());
        ExecuteStage stage = stages.get(3);
        Assert.assertEquals("Backup FOO", stage.title);
        group = groups.get(2);
        Assert.assertEquals("Core Masters", group.title);
        List<UpgradePack.OrderService> services = group.services;
        Assert.assertEquals(3, services.size());
        UpgradePack.OrderService service = services.get(2);
        Assert.assertEquals("HBASE", service.serviceName);
        group = groups.get(3);
        Assert.assertEquals("Core Slaves", group.title);
        services = group.services;
        Assert.assertEquals(3, services.size());
        service = services.get(1);
        Assert.assertEquals("HBASE", service.serviceName);
        group = groups.get(4);
        Assert.assertEquals(ServiceCheckGrouping.class, group.getClass());
        ServiceCheckGrouping scGroup = ((ServiceCheckGrouping) (group));
        Set<String> priorityServices = scGroup.getPriorities();
        Assert.assertEquals(4, priorityServices.size());
        Iterator<String> serviceIterator = priorityServices.iterator();
        Assert.assertEquals("ZOOKEEPER", serviceIterator.next());
        Assert.assertEquals("HBASE", serviceIterator.next());
        group = groups.get(5);
        Assert.assertEquals("Hive", group.title);
        group = groups.get(6);
        Assert.assertEquals("Foo", group.title);
        services = group.services;
        Assert.assertEquals(2, services.size());
        service = services.get(1);
        Assert.assertEquals("FOO2", service.serviceName);
        Map<String, Map<String, ProcessingComponent>> tasks = upgradePack.getTasks();
        Assert.assertTrue(tasks.containsKey("HBASE"));
        // !!! generalized upgrade pack shouldn't be in this
        boolean found = false;
        for (Grouping grouping : upgradePack.getAllGroups()) {
            if (grouping.name.equals("GANGLIA_UPGRADE")) {
                found = true;
                break;
            }
        }
        Assert.assertFalse(found);
        // !!! test merge of a generalized upgrade pack
        upgradePack = upgrades.get("upgrade_test_conditions");
        Assert.assertNotNull(upgradePack);
        for (Grouping grouping : upgradePack.getAllGroups()) {
            if (grouping.name.equals("GANGLIA_UPGRADE")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testPackWithHostGroup() {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.2.0");
        UpgradePack upgradePack = upgrades.get("upgrade_test_host_ordered");
        Assert.assertNotNull(upgradePack);
        Assert.assertEquals(upgradePack.getType(), HOST_ORDERED);
        Assert.assertEquals(3, upgradePack.getAllGroups().size());
        Assert.assertEquals(HostOrderGrouping.class, upgradePack.getAllGroups().get(0).getClass());
        Assert.assertEquals(Grouping.class, upgradePack.getAllGroups().get(1).getClass());
    }

    @Test
    public void testDowngradeComponentTasks() throws Exception {
        Map<String, UpgradePack> upgrades = ambariMetaInfo.getUpgradePacks("HDP", "2.1.1");
        UpgradePack upgradePack = upgrades.get("upgrade_component_tasks_test");
        Assert.assertNotNull(upgradePack);
        Map<String, Map<String, ProcessingComponent>> components = upgradePack.getTasks();
        Assert.assertTrue(components.containsKey("ZOOKEEPER"));
        Assert.assertTrue(components.containsKey("HDFS"));
        Map<String, ProcessingComponent> zkMap = components.get("ZOOKEEPER");
        Map<String, ProcessingComponent> hdfsMap = components.get("HDFS");
        Assert.assertTrue(zkMap.containsKey("ZOOKEEPER_SERVER"));
        Assert.assertTrue(zkMap.containsKey("ZOOKEEPER_CLIENT"));
        Assert.assertTrue(hdfsMap.containsKey("NAMENODE"));
        Assert.assertTrue(hdfsMap.containsKey("DATANODE"));
        Assert.assertTrue(hdfsMap.containsKey("HDFS_CLIENT"));
        Assert.assertTrue(hdfsMap.containsKey("JOURNALNODE"));
        ProcessingComponent zkServer = zkMap.get("ZOOKEEPER_SERVER");
        ProcessingComponent zkClient = zkMap.get("ZOOKEEPER_CLIENT");
        ProcessingComponent hdfsNN = hdfsMap.get("NAMENODE");
        ProcessingComponent hdfsDN = hdfsMap.get("DATANODE");
        ProcessingComponent hdfsClient = hdfsMap.get("HDFS_CLIENT");
        ProcessingComponent hdfsJN = hdfsMap.get("JOURNALNODE");
        // ZK server has only pretasks defined, with pre-downgrade being a copy of pre-upgrade
        Assert.assertNotNull(zkServer.preTasks);
        Assert.assertNotNull(zkServer.preDowngradeTasks);
        Assert.assertNull(zkServer.postTasks);
        Assert.assertNull(zkServer.postDowngradeTasks);
        Assert.assertEquals(1, zkServer.preTasks.size());
        Assert.assertEquals(1, zkServer.preDowngradeTasks.size());
        // ZK client has only post-tasks defined, with post-downgrade being a copy of pre-upgrade
        Assert.assertNull(zkClient.preTasks);
        Assert.assertNull(zkClient.preDowngradeTasks);
        Assert.assertNotNull(zkClient.postTasks);
        Assert.assertNotNull(zkClient.postDowngradeTasks);
        Assert.assertEquals(1, zkClient.postTasks.size());
        Assert.assertEquals(1, zkClient.postDowngradeTasks.size());
        // NN has only pre-tasks defined, with an empty pre-downgrade
        Assert.assertNotNull(hdfsNN.preTasks);
        Assert.assertNotNull(hdfsNN.preDowngradeTasks);
        Assert.assertNull(hdfsNN.postTasks);
        Assert.assertNull(hdfsNN.postDowngradeTasks);
        Assert.assertEquals(1, hdfsNN.preTasks.size());
        Assert.assertEquals(0, hdfsNN.preDowngradeTasks.size());
        // DN has only post-tasks defined, with post-downgrade being empty
        Assert.assertNull(hdfsDN.preTasks);
        Assert.assertNull(hdfsDN.preDowngradeTasks);
        Assert.assertNotNull(hdfsDN.postTasks);
        Assert.assertNotNull(hdfsDN.postDowngradeTasks);
        Assert.assertEquals(1, hdfsDN.postTasks.size());
        Assert.assertEquals(0, hdfsDN.postDowngradeTasks.size());
        // HDFS client has only post and post-downgrade tasks
        Assert.assertNull(hdfsClient.preTasks);
        Assert.assertNotNull(hdfsClient.preDowngradeTasks);
        Assert.assertNull(hdfsClient.postTasks);
        Assert.assertNotNull(hdfsClient.postDowngradeTasks);
        Assert.assertEquals(1, hdfsClient.preDowngradeTasks.size());
        Assert.assertEquals(1, hdfsClient.postDowngradeTasks.size());
        // JN has differing tasks for pre and post downgrade
        Assert.assertNotNull(hdfsJN.preTasks);
        Assert.assertNotNull(hdfsJN.preDowngradeTasks);
        Assert.assertNotNull(hdfsJN.postTasks);
        Assert.assertNotNull(hdfsJN.postDowngradeTasks);
        Assert.assertEquals(1, hdfsJN.preTasks.size());
        Assert.assertEquals(2, hdfsJN.preDowngradeTasks.size());
        Assert.assertEquals(1, hdfsJN.postTasks.size());
        Assert.assertEquals(2, hdfsJN.postDowngradeTasks.size());
        // make sure all ids are accounted for
        Set<String> allIds = Sets.newHashSet("some_id", "some_id1", "some_id2", "some_id3", "some_id4", "some_id5");
        @SuppressWarnings("unchecked")
        Set<List<Task>> allTasks = Sets.newHashSet(hdfsJN.preTasks, hdfsJN.preDowngradeTasks, hdfsJN.postTasks, hdfsJN.postDowngradeTasks);
        for (List<Task> list : allTasks) {
            for (Task t : list) {
                Assert.assertEquals(ConfigureTask.class, t.getClass());
                ConfigureTask ct = ((ConfigureTask) (t));
                Assert.assertTrue(allIds.contains(ct.id));
                allIds.remove(ct.id);
            }
        }
        Assert.assertTrue(allIds.isEmpty());
    }
}

