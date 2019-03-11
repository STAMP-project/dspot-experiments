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
package org.apache.ambari.server.serveraction.upgrades;


import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import RepositoryVersionState.INSTALLED;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleCommandFactory;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.HostVersionDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.HostVersionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.orm.entities.UpgradeHistoryEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests upgrade-related server side actions
 */
public class ComponentVersionCheckActionTest {
    private static final String HDP_2_1_1_0 = "2.1.1.0-1";

    private static final String HDP_2_1_1_1 = "2.1.1.1-2";

    private static final String HDP_2_2_1_0 = "2.2.1.0-1";

    private static final StackId HDP_21_STACK = new StackId("HDP-2.1.1");

    private static final StackId HDP_22_STACK = new StackId("HDP-2.2.0");

    private static final String HDP_211_CENTOS6_REPO_URL = "http://public-repo-1.hortonworks.com/HDP/centos6/2.x/updates/2.0.6.0";

    private Injector m_injector;

    @Inject
    private OrmTestHelper m_helper;

    @Inject
    private RepositoryVersionDAO repoVersionDAO;

    @Inject
    private HostVersionDAO hostVersionDAO;

    @Inject
    private HostRoleCommandFactory hostRoleCommandFactory;

    @Inject
    private ServiceFactory serviceFactory;

    @Inject
    private ServiceComponentFactory serviceComponentFactory;

    @Inject
    private ServiceComponentHostFactory serviceComponentHostFactory;

    @Inject
    private ConfigFactory configFactory;

    @Test
    public void testMatchingVersions() throws Exception {
        StackId sourceStack = ComponentVersionCheckActionTest.HDP_21_STACK;
        StackId targetStack = ComponentVersionCheckActionTest.HDP_21_STACK;
        String sourceRepo = ComponentVersionCheckActionTest.HDP_2_1_1_0;
        String targetRepo = ComponentVersionCheckActionTest.HDP_2_1_1_1;
        makeUpgradeCluster(sourceStack, sourceRepo, targetStack, targetRepo);
        // Finalize the upgrade
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName("c1");
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        ComponentVersionCheckAction action = m_injector.getInstance(ComponentVersionCheckAction.class);
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hostRoleCommand);
        CommandReport report = action.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        Assert.assertEquals(0, report.getExitCode());
    }

    @Test
    public void testMixedComponentVersions() throws Exception {
        StackId sourceStack = ComponentVersionCheckActionTest.HDP_21_STACK;
        StackId targetStack = ComponentVersionCheckActionTest.HDP_22_STACK;
        String sourceVersion = ComponentVersionCheckActionTest.HDP_2_1_1_0;
        String targetVersion = ComponentVersionCheckActionTest.HDP_2_2_1_0;
        String clusterName = "c1";
        String hostName = "h1";
        makeCrossStackUpgradeCluster(sourceStack, sourceVersion, targetStack, targetVersion, clusterName, hostName);
        Clusters clusters = m_injector.getInstance(Clusters.class);
        Cluster cluster = clusters.getCluster("c1");
        RepositoryVersionEntity sourceRepoVersion = m_helper.getOrCreateRepositoryVersion(ComponentVersionCheckActionTest.HDP_21_STACK, ComponentVersionCheckActionTest.HDP_2_1_1_0);
        RepositoryVersionEntity targetRepoVersion = m_helper.getOrCreateRepositoryVersion(ComponentVersionCheckActionTest.HDP_22_STACK, ComponentVersionCheckActionTest.HDP_2_2_1_0);
        Service service = installService(cluster, "HDFS", sourceRepoVersion);
        addServiceComponent(cluster, service, "NAMENODE");
        addServiceComponent(cluster, service, "DATANODE");
        createNewServiceComponentHost(cluster, "HDFS", "NAMENODE", hostName);
        createNewServiceComponentHost(cluster, "HDFS", "DATANODE", hostName);
        // create some configs
        createConfigs(cluster);
        // install the target repo
        installRepositoryOnHost(hostName, targetRepoVersion);
        // setup the cluster for the upgrade across stacks
        cluster.setCurrentStackVersion(sourceStack);
        cluster.setDesiredStackVersion(targetStack);
        // tell the upgrade that HDFS is upgrading - without this, no services will
        // be participating in the upgrade
        UpgradeEntity upgrade = cluster.getUpgradeInProgress();
        UpgradeHistoryEntity history = new UpgradeHistoryEntity();
        history.setUpgrade(upgrade);
        history.setServiceName("HDFS");
        history.setComponentName("NAMENODE");
        history.setFromRepositoryVersion(sourceRepoVersion);
        history.setTargetRepositoryVersion(targetRepoVersion);
        upgrade.addHistory(history);
        history = new UpgradeHistoryEntity();
        history.setUpgrade(upgrade);
        history.setServiceName("HDFS");
        history.setComponentName("DATANODE");
        history.setFromRepositoryVersion(sourceRepoVersion);
        history.setTargetRepositoryVersion(targetRepoVersion);
        upgrade.addHistory(history);
        UpgradeDAO upgradeDAO = m_injector.getInstance(UpgradeDAO.class);
        upgrade = upgradeDAO.merge(upgrade);
        // set the SCH versions to the new stack so that the finalize action is
        // happy - don't update DATANODE - we want to make the action complain
        cluster.getServiceComponentHosts("HDFS", "NAMENODE").get(0).setVersion(targetVersion);
        // verify the conditions for the test are met properly
        List<HostVersionEntity> hostVersions = hostVersionDAO.findByClusterStackAndVersion("c1", ComponentVersionCheckActionTest.HDP_22_STACK, targetVersion);
        Assert.assertTrue(((hostVersions.size()) > 0));
        for (HostVersionEntity hostVersion : hostVersions) {
            Assert.assertEquals(INSTALLED, hostVersion.getState());
        }
        // now finalize and ensure we can transition from UPGRADING to UPGRADED
        // automatically before CURRENT
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName("c1");
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        ComponentVersionCheckAction action = m_injector.getInstance(ComponentVersionCheckAction.class);
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hostRoleCommand);
        CommandReport report = action.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(FAILED.name(), report.getStatus());
        Assert.assertEquals((-1), report.getExitCode());
        // OK, now set the datanode so it completes
        cluster.getServiceComponentHosts("HDFS", "DATANODE").get(0).setVersion(targetVersion);
        report = action.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        Assert.assertEquals(0, report.getExitCode());
    }

    @Test
    public void testMatchingPartialVersions() throws Exception {
        StackId sourceStack = ComponentVersionCheckActionTest.HDP_21_STACK;
        StackId targetStack = ComponentVersionCheckActionTest.HDP_21_STACK;
        String sourceRepo = ComponentVersionCheckActionTest.HDP_2_1_1_0;
        String targetRepo = ComponentVersionCheckActionTest.HDP_2_1_1_1;
        makeUpgradeCluster(sourceStack, sourceRepo, targetStack, targetRepo);
        Clusters clusters = m_injector.getInstance(Clusters.class);
        Host host = clusters.getHost("h1");
        Assert.assertNotNull(host);
        host.setOsInfo("redhat6");
        Cluster cluster = clusters.getCluster("c1");
        clusters.mapHostToCluster("h1", "c1");
        RepositoryVersionEntity repositoryVersion2110 = m_helper.getOrCreateRepositoryVersion(ComponentVersionCheckActionTest.HDP_21_STACK, ComponentVersionCheckActionTest.HDP_2_1_1_0);
        RepositoryVersionEntity repositoryVersion2111 = m_helper.getOrCreateRepositoryVersion(ComponentVersionCheckActionTest.HDP_21_STACK, ComponentVersionCheckActionTest.HDP_2_1_1_1);
        Service service = installService(cluster, "HDFS", repositoryVersion2110);
        addServiceComponent(cluster, service, "NAMENODE");
        addServiceComponent(cluster, service, "DATANODE");
        ServiceComponentHost sch = createNewServiceComponentHost(cluster, "HDFS", "NAMENODE", "h1");
        sch.setVersion(ComponentVersionCheckActionTest.HDP_2_1_1_0);
        sch = createNewServiceComponentHost(cluster, "HDFS", "DATANODE", "h1");
        sch.setVersion(ComponentVersionCheckActionTest.HDP_2_1_1_0);
        service = installService(cluster, "ZOOKEEPER", repositoryVersion2111);
        addServiceComponent(cluster, service, "ZOOKEEPER_SERVER");
        sch = createNewServiceComponentHost(cluster, "ZOOKEEPER", "ZOOKEEPER_SERVER", "h1");
        sch.setVersion(ComponentVersionCheckActionTest.HDP_2_1_1_1);
        // Finalize the upgrade
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName("c1");
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        ComponentVersionCheckAction action = m_injector.getInstance(ComponentVersionCheckAction.class);
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hostRoleCommand);
        CommandReport report = action.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        Assert.assertEquals(0, report.getExitCode());
    }
}

