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
package org.apache.ambari.server.stageplanner;


import CommandExecutionType.DEPENDENCY_ORDERED;
import Role.DATANODE;
import Role.GANGLIA_MONITOR;
import Role.GANGLIA_SERVER;
import Role.HBASE_MASTER;
import Role.HBASE_REGIONSERVER;
import Role.HIVE_METASTORE;
import Role.JOBTRACKER;
import Role.MYSQL_SERVER;
import Role.OOZIE_SERVER;
import Role.RESOURCEMANAGER;
import Role.SECONDARY_NAMENODE;
import Role.TASKTRACKER;
import Role.WEBHCAT_SERVER;
import Role.ZOOKEEPER_SERVER;
import RoleCommand.CUSTOM_COMMAND;
import RoleCommand.START;
import RoleCommand.STOP;
import RoleCommand.UPGRADE;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.List;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.actionmanager.StageFactory;
import org.apache.ambari.server.metadata.RoleCommandOrder;
import org.apache.ambari.server.metadata.RoleCommandOrderProvider;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.cluster.ClusterImpl;
import org.apache.ambari.server.state.svccomphost.ServiceComponentHostServerActionEvent;
import org.apache.ambari.server.state.svccomphost.ServiceComponentHostStartEvent;
import org.apache.ambari.server.utils.StageUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class TestStagePlanner {
    private static final Logger log = LoggerFactory.getLogger(TestStagePlanner.class);

    private Injector injector;

    @Inject
    private StageFactory stageFactory;

    @Inject
    private RoleCommandOrderProvider roleCommandOrderProvider;

    @Inject
    private StageUtils stageUtils;

    @Inject
    private RoleGraphFactory roleGraphFactory;

    @Test
    public void testSingleStagePlan() throws AmbariException {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6"));
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        String hostname = "dummy";
        Stage stage = StageUtils.getATestStage(1, 1, hostname, "", "");
        rg.build(stage);
        List<Stage> outStages = rg.getStages();
        for (Stage s : outStages) {
            TestStagePlanner.log.info(s.toString());
        }
        Assert.assertEquals(1, outStages.size());
        Assert.assertEquals(stage.getExecutionCommands(hostname), outStages.get(0).getExecutionCommands(hostname));
    }

    @Test
    public void testSCCInGraphDetectedShort() {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6.1"));
        Service hbaseService = Mockito.mock(Service.class);
        Mockito.when(hbaseService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6.1"));
        Service zkService = Mockito.mock(Service.class);
        Mockito.when(zkService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6.1"));
        Mockito.when(cluster.getServices()).thenReturn(ImmutableMap.<String, Service>builder().put("HBASE", hbaseService).put("ZOOKEEPER", zkService).build());
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        long now = System.currentTimeMillis();
        Stage stage = StageUtils.getATestStage(1, 1, "host1", "", "");
        stage.addHostRoleExecutionCommand("host2", HBASE_MASTER, START, new ServiceComponentHostStartEvent("HBASE_MASTER", "host2", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host3", ZOOKEEPER_SERVER, START, new ServiceComponentHostStartEvent("ZOOKEEPER_SERVER", "host3", now), "cluster1", "ZOOKEEPER", false, false);
        TestStagePlanner.log.info("Build and ready to detect circular dependencies - short chain");
        rg.build(stage);
        boolean exceptionThrown = false;
        try {
            List<Stage> outStages = rg.getStages();
        } catch (AmbariException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testSCCInGraphDetectedLong() {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6.1"));
        Service hbaseService = Mockito.mock(Service.class);
        Mockito.when(hbaseService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6.1"));
        Service zkService = Mockito.mock(Service.class);
        Mockito.when(zkService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6.1"));
        Service yarnService = Mockito.mock(Service.class);
        Mockito.when(yarnService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6.1"));
        Mockito.when(cluster.getServices()).thenReturn(ImmutableMap.<String, Service>builder().put("HBASE", hbaseService).put("ZOOKEEPER", zkService).put("YARN", yarnService).build());
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        long now = System.currentTimeMillis();
        Stage stage = StageUtils.getATestStage(1, 1, "host1", "", "");
        stage.addHostRoleExecutionCommand("host2", HBASE_MASTER, STOP, new ServiceComponentHostStartEvent("HBASE_MASTER", "host2", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host3", ZOOKEEPER_SERVER, STOP, new ServiceComponentHostStartEvent("ZOOKEEPER_SERVER", "host3", now), "cluster1", "ZOOKEEPER", false, false);
        stage.addHostRoleExecutionCommand("host2", RESOURCEMANAGER, STOP, new ServiceComponentHostStartEvent("RESOURCEMANAGER", "host4", now), "cluster1", "YARN", false, false);
        TestStagePlanner.log.info("Build and ready to detect circular dependencies - long chain");
        rg.build(stage);
        boolean exceptionThrown = false;
        try {
            List<Stage> outStages = rg.getStages();
        } catch (AmbariException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testSCCInGraphDetectedLongTwo() {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6.1"));
        Service hbaseService = Mockito.mock(Service.class);
        Mockito.when(hbaseService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6.1"));
        Service zkService = Mockito.mock(Service.class);
        Mockito.when(zkService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6.1"));
        Mockito.when(cluster.getServices()).thenReturn(ImmutableMap.<String, Service>builder().put("HBASE", hbaseService).put("ZOOKEEPER", zkService).build());
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        long now = System.currentTimeMillis();
        Stage stage = StageUtils.getATestStage(1, 1, "host1", "", "");
        stage.addHostRoleExecutionCommand("host2", HBASE_MASTER, UPGRADE, new ServiceComponentHostStartEvent("HBASE_MASTER", "host2", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host3", ZOOKEEPER_SERVER, UPGRADE, new ServiceComponentHostStartEvent("ZOOKEEPER_SERVER", "host3", now), "cluster1", "ZOOKEEPER", false, false);
        stage.addHostRoleExecutionCommand("host2", HBASE_REGIONSERVER, UPGRADE, new ServiceComponentHostStartEvent("HBASE_REGIONSERVER", "host4", now), "cluster1", "HBASE", false, false);
        TestStagePlanner.log.info("Build and ready to detect circular dependencies - long chain");
        rg.build(stage);
        boolean exceptionThrown = false;
        try {
            List<Stage> outStages = rg.getStages();
        } catch (AmbariException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testNoSCCInGraphDetected() {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6"));
        Service hbaseService = Mockito.mock(Service.class);
        Mockito.when(hbaseService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service zkService = Mockito.mock(Service.class);
        Mockito.when(zkService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Mockito.when(cluster.getServices()).thenReturn(ImmutableMap.<String, Service>builder().put("HBASE", hbaseService).put("ZOOKEEPER", zkService).build());
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        long now = System.currentTimeMillis();
        Stage stage = StageUtils.getATestStage(1, 1, "host1", "", "");
        stage.addHostRoleExecutionCommand("host2", HBASE_MASTER, STOP, new ServiceComponentHostStartEvent("HBASE_MASTER", "host2", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host3", ZOOKEEPER_SERVER, STOP, new ServiceComponentHostStartEvent("ZOOKEEPER_SERVER", "host3", now), "cluster1", "ZOOKEEPER", false, false);
        stage.addHostRoleExecutionCommand("host2", HBASE_REGIONSERVER, STOP, new ServiceComponentHostStartEvent("HBASE_REGIONSERVER", "host4", now), "cluster1", "HBASE", false, false);
        TestStagePlanner.log.info("Build and ready to detect circular dependencies");
        rg.build(stage);
        boolean exceptionThrown = false;
        try {
            List<Stage> outStages = rg.getStages();
        } catch (AmbariException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testMultiStagePlan() throws Throwable {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6"));
        Service hbaseService = Mockito.mock(Service.class);
        Mockito.when(hbaseService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service zkService = Mockito.mock(Service.class);
        Mockito.when(zkService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Mockito.when(cluster.getServices()).thenReturn(ImmutableMap.<String, Service>builder().put("HBASE", hbaseService).put("ZOOKEEPER", zkService).build());
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        long now = System.currentTimeMillis();
        Stage stage = StageUtils.getATestStage(1, 1, "host1", "", "");
        stage.addHostRoleExecutionCommand("host2", HBASE_MASTER, START, new ServiceComponentHostStartEvent("HBASE_MASTER", "host2", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host3", ZOOKEEPER_SERVER, START, new ServiceComponentHostStartEvent("ZOOKEEPER_SERVER", "host3", now), "cluster1", "ZOOKEEPER", false, false);
        TestStagePlanner.log.info(stage.toString());
        rg.build(stage);
        TestStagePlanner.log.info(rg.stringifyGraph());
        List<Stage> outStages = rg.getStages();
        for (Stage s : outStages) {
            TestStagePlanner.log.info(s.toString());
        }
        Assert.assertEquals(3, outStages.size());
    }

    @Test
    public void testRestartStagePlan() throws Throwable {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6"));
        Service hiveService = Mockito.mock(Service.class);
        Mockito.when(hiveService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Mockito.when(cluster.getServices()).thenReturn(ImmutableMap.<String, Service>builder().put("HIVE", hiveService).build());
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        Stage stage = stageFactory.createNew(1, "/tmp", "cluster1", 1L, "execution command wrapper test", "commandParamsStage", "hostParamsStage");
        stage.setStageId(1);
        stage.addServerActionCommand("RESTART", null, HIVE_METASTORE, CUSTOM_COMMAND, "cluster1", new ServiceComponentHostServerActionEvent("host2", System.currentTimeMillis()), null, "command detail", null, null, false, false);
        stage.addServerActionCommand("RESTART", null, MYSQL_SERVER, CUSTOM_COMMAND, "cluster1", new ServiceComponentHostServerActionEvent("host2", System.currentTimeMillis()), null, "command detail", null, null, false, false);
        TestStagePlanner.log.info(stage.toString());
        rg.build(stage);
        TestStagePlanner.log.info(rg.stringifyGraph());
        List<Stage> outStages = rg.getStages();
        for (Stage s : outStages) {
            TestStagePlanner.log.info(s.toString());
        }
        Assert.assertEquals(2, outStages.size());
    }

    @Test
    public void testManyStages() throws Throwable {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6"));
        Service hdfsService = Mockito.mock(Service.class);
        Mockito.when(hdfsService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service hbaseService = Mockito.mock(Service.class);
        Mockito.when(hbaseService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service zkService = Mockito.mock(Service.class);
        Mockito.when(zkService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service mrService = Mockito.mock(Service.class);
        Mockito.when(mrService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service oozieService = Mockito.mock(Service.class);
        Mockito.when(oozieService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service webhcatService = Mockito.mock(Service.class);
        Mockito.when(webhcatService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Service gangliaService = Mockito.mock(Service.class);
        Mockito.when(gangliaService.getDesiredStackId()).thenReturn(new StackId("HDP-2.0.6"));
        Mockito.when(cluster.getServices()).thenReturn(ImmutableMap.<String, Service>builder().put("HDFS", hdfsService).put("HBASE", hbaseService).put("ZOOKEEPER", zkService).put("MAPREDUCE", mrService).put("OOZIE", oozieService).put("WEBHCAT", webhcatService).put("GANGLIA", gangliaService).build());
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        long now = System.currentTimeMillis();
        Stage stage = StageUtils.getATestStage(1, 1, "host1", "", "");
        stage.addHostRoleExecutionCommand("host11", SECONDARY_NAMENODE, START, new ServiceComponentHostStartEvent("SECONDARY_NAMENODE", "host11", now), "cluster1", "HDFS", false, false);
        stage.addHostRoleExecutionCommand("host2", HBASE_MASTER, START, new ServiceComponentHostStartEvent("HBASE_MASTER", "host2", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host3", ZOOKEEPER_SERVER, START, new ServiceComponentHostStartEvent("ZOOKEEPER_SERVER", "host3", now), "cluster1", "ZOOKEEPER", false, false);
        stage.addHostRoleExecutionCommand("host4", DATANODE, START, new ServiceComponentHostStartEvent("DATANODE", "host4", now), "cluster1", "HDFS", false, false);
        stage.addHostRoleExecutionCommand("host4", HBASE_REGIONSERVER, START, new ServiceComponentHostStartEvent("HBASE_REGIONSERVER", "host4", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host4", TASKTRACKER, START, new ServiceComponentHostStartEvent("TASKTRACKER", "host4", now), "cluster1", "MAPREDUCE", false, false);
        stage.addHostRoleExecutionCommand("host5", JOBTRACKER, START, new ServiceComponentHostStartEvent("JOBTRACKER", "host5", now), "cluster1", "MAPREDUCE", false, false);
        stage.addHostRoleExecutionCommand("host6", OOZIE_SERVER, START, new ServiceComponentHostStartEvent("OOZIE_SERVER", "host6", now), "cluster1", "OOZIE", false, false);
        stage.addHostRoleExecutionCommand("host7", WEBHCAT_SERVER, START, new ServiceComponentHostStartEvent("WEBHCAT_SERVER", "host7", now), "cluster1", "WEBHCAT", false, false);
        stage.addHostRoleExecutionCommand("host4", GANGLIA_MONITOR, START, new ServiceComponentHostStartEvent("GANGLIA_MONITOR", "host4", now), "cluster1", "GANGLIA", false, false);
        stage.addHostRoleExecutionCommand("host9", GANGLIA_SERVER, START, new ServiceComponentHostStartEvent("GANGLIA_SERVER", "host9", now), "cluster1", "GANGLIA", false, false);
        TestStagePlanner.log.info(stage.toString());
        rg.build(stage);
        TestStagePlanner.log.info(rg.stringifyGraph());
        List<Stage> outStages = rg.getStages();
        for (Stage s : outStages) {
            TestStagePlanner.log.info(s.toString());
        }
        Assert.assertEquals(4, outStages.size());
    }

    @Test
    public void testDependencyOrderedStageCreate() throws Throwable {
        ClusterImpl cluster = Mockito.mock(ClusterImpl.class);
        Mockito.when(cluster.getCurrentStackVersion()).thenReturn(new StackId("HDP-2.0.6"));
        RoleCommandOrder rco = roleCommandOrderProvider.getRoleCommandOrder(cluster);
        RoleGraph rg = roleGraphFactory.createNew(rco);
        rg.setCommandExecutionType(DEPENDENCY_ORDERED);
        long now = System.currentTimeMillis();
        Stage stage = StageUtils.getATestStage(1, 1, "host1", "", "");
        stage.addHostRoleExecutionCommand("host11", SECONDARY_NAMENODE, START, new ServiceComponentHostStartEvent("SECONDARY_NAMENODE", "host11", now), "cluster1", "HDFS", false, false);
        stage.addHostRoleExecutionCommand("host2", HBASE_MASTER, START, new ServiceComponentHostStartEvent("HBASE_MASTER", "host2", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host3", ZOOKEEPER_SERVER, START, new ServiceComponentHostStartEvent("ZOOKEEPER_SERVER", "host3", now), "cluster1", "ZOOKEEPER", false, false);
        stage.addHostRoleExecutionCommand("host4", DATANODE, START, new ServiceComponentHostStartEvent("DATANODE", "host4", now), "cluster1", "HDFS", false, false);
        stage.addHostRoleExecutionCommand("host4", HBASE_REGIONSERVER, START, new ServiceComponentHostStartEvent("HBASE_REGIONSERVER", "host4", now), "cluster1", "HBASE", false, false);
        stage.addHostRoleExecutionCommand("host4", TASKTRACKER, START, new ServiceComponentHostStartEvent("TASKTRACKER", "host4", now), "cluster1", "MAPREDUCE", false, false);
        stage.addHostRoleExecutionCommand("host5", JOBTRACKER, START, new ServiceComponentHostStartEvent("JOBTRACKER", "host5", now), "cluster1", "MAPREDUCE", false, false);
        stage.addHostRoleExecutionCommand("host6", OOZIE_SERVER, START, new ServiceComponentHostStartEvent("OOZIE_SERVER", "host6", now), "cluster1", "OOZIE", false, false);
        stage.addHostRoleExecutionCommand("host7", WEBHCAT_SERVER, START, new ServiceComponentHostStartEvent("WEBHCAT_SERVER", "host7", now), "cluster1", "WEBHCAT", false, false);
        stage.addHostRoleExecutionCommand("host4", GANGLIA_MONITOR, START, new ServiceComponentHostStartEvent("GANGLIA_MONITOR", "host4", now), "cluster1", "GANGLIA", false, false);
        stage.addHostRoleExecutionCommand("host9", GANGLIA_SERVER, START, new ServiceComponentHostStartEvent("GANGLIA_SERVER", "host9", now), "cluster1", "GANGLIA", false, false);
        TestStagePlanner.log.info(stage.toString());
        rg.build(stage);
        TestStagePlanner.log.info(rg.stringifyGraph());
        List<Stage> outStages = rg.getStages();
        for (Stage s : outStages) {
            TestStagePlanner.log.info(s.toString());
        }
        Assert.assertEquals(1, outStages.size());
    }
}

