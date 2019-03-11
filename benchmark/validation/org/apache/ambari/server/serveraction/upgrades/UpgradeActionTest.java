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
import RepositoryType.PATCH;
import RepositoryVersionState.CURRENT;
import RepositoryVersionState.INSTALLED;
import RepositoryVersionState.NOT_REQUIRED;
import RepositoryVersionState.OUT_OF_SYNC;
import ServerAction.ACTION_USER_NAME;
import UpgradeState.NONE;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleCommandFactory;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.ServiceConfigVersionResponse;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.HostComponentStateDAO;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.dao.HostVersionDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.RequestDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.HostComponentStateEntity;
import org.apache.ambari.server.orm.entities.HostVersionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.stack.upgrade.UpgradePack;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.RepositoryVersionState;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.StackId;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests upgrade-related server side actions
 */
public class UpgradeActionTest {
    private static final String clusterName = "c1";

    private static final String HDP_2_1_1_0 = "2.1.1.0-1";

    private static final String HDP_2_1_1_1 = "2.1.1.1-2";

    private static final String HDP_2_2_0_1 = "2.2.0.1-3";

    private static final StackId HDP_21_STACK = new StackId("HDP-2.1.1");

    private static final StackId HDP_22_STACK = new StackId("HDP-2.2.0");

    private RepositoryVersionEntity sourceRepositoryVersion;

    private Injector m_injector;

    private AmbariManagementController amc;

    @Inject
    private OrmTestHelper m_helper;

    @Inject
    private RepositoryVersionDAO repoVersionDAO;

    @Inject
    private Clusters clusters;

    @Inject
    private HostVersionDAO hostVersionDAO;

    @Inject
    private HostDAO hostDAO;

    @Inject
    private HostRoleCommandFactory hostRoleCommandFactory;

    @Inject
    private ServiceFactory serviceFactory;

    @Inject
    private ServiceComponentFactory serviceComponentFactory;

    @Inject
    private ServiceComponentHostFactory serviceComponentHostFactory;

    @Inject
    private RequestDAO requestDAO;

    @Inject
    private UpgradeDAO upgradeDAO;

    @Inject
    private StackDAO stackDAO;

    @Inject
    private AmbariMetaInfo ambariMetaInfo;

    @Inject
    private FinalizeUpgradeAction finalizeUpgradeAction;

    @Inject
    private ConfigFactory configFactory;

    @Inject
    private RepositoryVersionDAO repositoryVersionDAO;

    @Inject
    private HostComponentStateDAO hostComponentStateDAO;

    private RepositoryVersionEntity repositoryVersion2110;

    private RepositoryVersionEntity repositoryVersion2111;

    private RepositoryVersionEntity repositoryVersion2201;

    /**
     * *
     * During an Express Upgrade that crosses a stack version, Ambari calls UpdateDesiredRepositoryAction
     * in order to change the stack and apply configs.
     * The configs that are applied must be saved with the username that is passed in the role params.
     */
    @Test
    public void testExpressUpgradeUpdateDesiredRepositoryAction() throws Exception {
        StackId sourceStack = UpgradeActionTest.HDP_21_STACK;
        StackId targetStack = UpgradeActionTest.HDP_22_STACK;
        String sourceRepo = UpgradeActionTest.HDP_2_1_1_0;
        String hostName = "h1";
        // Must be a NON_ROLLING upgrade that jumps stacks in order for it to apply config changes.
        // That upgrade pack has changes for ZK and NameNode.
        String upgradePackName = "upgrade_nonrolling_new_stack";
        Map<String, UpgradePack> packs = ambariMetaInfo.getUpgradePacks(sourceStack.getStackName(), sourceStack.getStackVersion());
        Assert.assertTrue(packs.containsKey(upgradePackName));
        makeCrossStackUpgradeClusterAndSourceRepo(sourceStack, sourceRepo, hostName);
        Cluster cluster = clusters.getCluster(UpgradeActionTest.clusterName);
        // Install ZK and HDFS with some components
        Service zk = installService(cluster, "ZOOKEEPER", repositoryVersion2110);
        addServiceComponent(cluster, zk, "ZOOKEEPER_SERVER");
        addServiceComponent(cluster, zk, "ZOOKEEPER_CLIENT");
        createNewServiceComponentHost(cluster, "ZOOKEEPER", "ZOOKEEPER_SERVER", "h1");
        createNewServiceComponentHost(cluster, "ZOOKEEPER", "ZOOKEEPER_CLIENT", "h1");
        Service hdfs = installService(cluster, "HDFS", repositoryVersion2110);
        addServiceComponent(cluster, hdfs, "NAMENODE");
        addServiceComponent(cluster, hdfs, "DATANODE");
        createNewServiceComponentHost(cluster, "HDFS", "NAMENODE", "h1");
        createNewServiceComponentHost(cluster, "HDFS", "DATANODE", "h1");
        makeCrossStackUpgradeTargetRepo(targetStack, repositoryVersion2201.getVersion(), hostName);
        createUpgrade(cluster, repositoryVersion2201);
        Assert.assertNotNull(repositoryVersion2201);
        // Create some configs
        createConfigs(cluster);
        Collection<Config> configs = cluster.getAllConfigs();
        Assert.assertFalse(configs.isEmpty());
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        Map<String, String> roleParams = new HashMap<>();
        // User that is performing the config changes
        String userName = "admin";
        roleParams.put(ACTION_USER_NAME, userName);
        executionCommand.setRoleParams(roleParams);
        executionCommand.setClusterName(UpgradeActionTest.clusterName);
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        // Call the action to change the desired stack and apply the configs from the Config Pack called by the Upgrade Pack.
        UpdateDesiredRepositoryAction action = m_injector.getInstance(UpdateDesiredRepositoryAction.class);
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hostRoleCommand);
        List<ServiceConfigVersionResponse> configVersionsBefore = cluster.getServiceConfigVersions();
        CommandReport report = action.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        List<ServiceConfigVersionResponse> configVersionsAfter = cluster.getServiceConfigVersions();
        Assert.assertFalse(configVersionsAfter.isEmpty());
        Assert.assertTrue((((configVersionsAfter.size()) - (configVersionsBefore.size())) >= 1));
    }

    @Test
    public void testFinalizeDowngrade() throws Exception {
        makeDowngradeCluster(repositoryVersion2110, repositoryVersion2111);
        Cluster cluster = clusters.getCluster(UpgradeActionTest.clusterName);
        createUpgrade(cluster, repositoryVersion2111);
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName(UpgradeActionTest.clusterName);
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        finalizeUpgradeAction.setExecutionCommand(executionCommand);
        finalizeUpgradeAction.setHostRoleCommand(hostRoleCommand);
        CommandReport report = finalizeUpgradeAction.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        for (HostVersionEntity entity : hostVersionDAO.findByClusterAndHost(UpgradeActionTest.clusterName, "h1")) {
            if (StringUtils.equals(entity.getRepositoryVersion().getVersion(), repositoryVersion2110.getVersion())) {
                Assert.assertEquals(CURRENT, entity.getState());
            } else {
                Assert.assertEquals(INSTALLED, entity.getState());
            }
        }
    }

    @Test
    public void testFinalizeUpgrade() throws Exception {
        String hostName = "h1";
        createUpgradeCluster(repositoryVersion2110, hostName);
        createHostVersions(repositoryVersion2111, hostName);
        Cluster cluster = clusters.getCluster(UpgradeActionTest.clusterName);
        createUpgrade(cluster, repositoryVersion2111);
        // Finalize the upgrade
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName(UpgradeActionTest.clusterName);
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        finalizeUpgradeAction.setExecutionCommand(executionCommand);
        finalizeUpgradeAction.setHostRoleCommand(hostRoleCommand);
        // this should fail since the host versions have not moved to current
        CommandReport report = finalizeUpgradeAction.execute(null);
        Assert.assertEquals(FAILED.name(), report.getStatus());
        List<HostVersionEntity> hostVersions = hostVersionDAO.findHostVersionByClusterAndRepository(cluster.getClusterId(), repositoryVersion2111);
        for (HostVersionEntity hostVersion : hostVersions) {
            hostVersion.setState(CURRENT);
        }
        report = finalizeUpgradeAction.execute(null);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        hostVersions = hostVersionDAO.findHostVersionByClusterAndRepository(cluster.getClusterId(), repositoryVersion2111);
        for (HostVersionEntity hostVersion : hostVersions) {
            Collection<HostComponentStateEntity> hostComponentStates = hostComponentStateDAO.findByHost(hostVersion.getHostName());
            for (HostComponentStateEntity hostComponentStateEntity : hostComponentStates) {
                Assert.assertEquals(NONE, hostComponentStateEntity.getUpgradeState());
            }
        }
    }

    /**
     * Tests that finalize still works when there are hosts which are already
     * {@link RepositoryVersionState#CURRENT}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFinalizeWithHostsAlreadyCurrent() throws Exception {
        String hostName = "h1";
        createUpgradeCluster(repositoryVersion2110, hostName);
        createHostVersions(repositoryVersion2111, hostName);
        // move the old version from CURRENT to INSTALLED and the new version from
        // UPGRADED to CURRENT - this will simulate what happens when a host is
        // removed before finalization and all hosts transition to CURRENT
        List<HostVersionEntity> hostVersions = hostVersionDAO.findAll();
        for (HostVersionEntity hostVersion : hostVersions) {
            if ((hostVersion.getState()) == (RepositoryVersionState.CURRENT)) {
                hostVersion.setState(INSTALLED);
            } else {
                hostVersion.setState(CURRENT);
            }
            hostVersionDAO.merge(hostVersion);
        }
        // Verify the repo before calling Finalize
        Cluster cluster = clusters.getCluster(UpgradeActionTest.clusterName);
        createUpgrade(cluster, repositoryVersion2111);
        // Finalize the upgrade
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName(UpgradeActionTest.clusterName);
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        finalizeUpgradeAction.setExecutionCommand(executionCommand);
        finalizeUpgradeAction.setHostRoleCommand(hostRoleCommand);
        CommandReport report = finalizeUpgradeAction.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
    }

    /**
     * Tests that all host versions are correct after upgrade. This test will
     * ensure that the prior CURRENT versions are moved to INSTALLED while not
     * touching any others.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHostVersionsAfterUpgrade() throws Exception {
        String hostName = "h1";
        Cluster cluster = createUpgradeCluster(repositoryVersion2110, hostName);
        createHostVersions(repositoryVersion2111, hostName);
        createHostVersions(repositoryVersion2201, hostName);
        // Install ZK with some components
        Service zk = installService(cluster, "ZOOKEEPER", repositoryVersion2110);
        addServiceComponent(cluster, zk, "ZOOKEEPER_SERVER");
        addServiceComponent(cluster, zk, "ZOOKEEPER_CLIENT");
        createNewServiceComponentHost(cluster, "ZOOKEEPER", "ZOOKEEPER_SERVER", hostName);
        createNewServiceComponentHost(cluster, "ZOOKEEPER", "ZOOKEEPER_CLIENT", hostName);
        List<HostVersionEntity> hostVersions = hostVersionDAO.findAll();
        Assert.assertEquals(3, hostVersions.size());
        // repo 2110 - CURRENT (upgrading from)
        // repo 2111 - CURRENT (all hosts reported in during upgrade)
        // repo 2201 - NOT_REQUIRED (different stack)
        for (HostVersionEntity hostVersion : hostVersions) {
            RepositoryVersionEntity hostRepoVersion = hostVersion.getRepositoryVersion();
            if (repositoryVersion2110.equals(hostRepoVersion)) {
                hostVersion.setState(CURRENT);
            } else
                if (repositoryVersion2111.equals(hostRepoVersion)) {
                    hostVersion.setState(CURRENT);
                } else {
                    hostVersion.setState(NOT_REQUIRED);
                }

            hostVersionDAO.merge(hostVersion);
        }
        // upgrade to 2111
        createUpgrade(cluster, repositoryVersion2111);
        // push all services to the correct repo version for finalize
        Map<String, Service> services = cluster.getServices();
        Assert.assertTrue(((services.size()) > 0));
        for (Service service : services.values()) {
            service.setDesiredRepositoryVersion(repositoryVersion2111);
        }
        // push all components to the correct version
        List<HostComponentStateEntity> hostComponentStates = hostComponentStateDAO.findByHost(hostName);
        for (HostComponentStateEntity hostComponentState : hostComponentStates) {
            hostComponentState.setVersion(repositoryVersion2111.getVersion());
            hostComponentStateDAO.merge(hostComponentState);
        }
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName(UpgradeActionTest.clusterName);
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        finalizeUpgradeAction.setExecutionCommand(executionCommand);
        finalizeUpgradeAction.setHostRoleCommand(hostRoleCommand);
        // finalize
        CommandReport report = finalizeUpgradeAction.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        for (HostVersionEntity hostVersion : hostVersions) {
            RepositoryVersionEntity hostRepoVersion = hostVersion.getRepositoryVersion();
            if (repositoryVersion2110.equals(hostRepoVersion)) {
                Assert.assertEquals(INSTALLED, hostVersion.getState());
            } else
                if (repositoryVersion2111.equals(hostRepoVersion)) {
                    Assert.assertEquals(CURRENT, hostVersion.getState());
                } else {
                    Assert.assertEquals(NOT_REQUIRED, hostVersion.getState());
                }

        }
    }

    /**
     * Tests the case where a revert happens on a patch upgrade and a new service
     * has been added which causes the repository to go OUT_OF_SYNC.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHostVersionsOutOfSyncAfterRevert() throws Exception {
        String hostName = "h1";
        Cluster cluster = createUpgradeCluster(repositoryVersion2110, hostName);
        createHostVersions(repositoryVersion2111, hostName);
        // Install ZK with some components (HBase is installed later to test the
        // logic of revert)
        Service zk = installService(cluster, "ZOOKEEPER", repositoryVersion2110);
        addServiceComponent(cluster, zk, "ZOOKEEPER_SERVER");
        addServiceComponent(cluster, zk, "ZOOKEEPER_CLIENT");
        createNewServiceComponentHost(cluster, "ZOOKEEPER", "ZOOKEEPER_SERVER", hostName);
        createNewServiceComponentHost(cluster, "ZOOKEEPER", "ZOOKEEPER_CLIENT", hostName);
        List<HostVersionEntity> hostVersions = hostVersionDAO.findAll();
        Assert.assertEquals(2, hostVersions.size());
        // repo 2110 - CURRENT
        // repo 2111 - CURRENT (PATCH)
        for (HostVersionEntity hostVersion : hostVersions) {
            hostVersion.setState(CURRENT);
            hostVersionDAO.merge(hostVersion);
        }
        // convert the repository into a PATCH repo (which should have ZK and HBase
        // as available services)
        repositoryVersion2111.setParent(repositoryVersion2110);
        repositoryVersion2111.setType(PATCH);
        repositoryVersion2111.setVersionXml(hostName);
        repositoryVersion2111.setVersionXsd("version_definition.xsd");
        File patchVdfFile = new File("src/test/resources/hbase_version_test.xml");
        repositoryVersion2111.setVersionXml(IOUtils.toString(new FileInputStream(patchVdfFile), Charset.defaultCharset()));
        repositoryVersion2111 = repositoryVersionDAO.merge(repositoryVersion2111);
        // pretend like we patched
        UpgradeEntity upgrade = createUpgrade(cluster, repositoryVersion2111);
        upgrade.setOrchestration(PATCH);
        upgrade.setRevertAllowed(true);
        upgrade = upgradeDAO.merge(upgrade);
        // add a service on the parent repo to cause the OUT_OF_SYNC on revert
        Service hbase = installService(cluster, "HBASE", repositoryVersion2110);
        addServiceComponent(cluster, hbase, "HBASE_MASTER");
        createNewServiceComponentHost(cluster, "HBASE", "HBASE_MASTER", hostName);
        // revert the patch
        UpgradeEntity revert = createRevert(cluster, upgrade);
        Assert.assertEquals(PATCH, revert.getOrchestration());
        // push all services to the revert repo version for finalize
        Map<String, Service> services = cluster.getServices();
        Assert.assertTrue(((services.size()) > 0));
        for (Service service : services.values()) {
            service.setDesiredRepositoryVersion(repositoryVersion2110);
        }
        // push all components to the revert version
        List<HostComponentStateEntity> hostComponentStates = hostComponentStateDAO.findByHost(hostName);
        for (HostComponentStateEntity hostComponentState : hostComponentStates) {
            hostComponentState.setVersion(repositoryVersion2110.getVersion());
            hostComponentStateDAO.merge(hostComponentState);
        }
        Map<String, String> commandParams = new HashMap<>();
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName(UpgradeActionTest.clusterName);
        HostRoleCommand hostRoleCommand = hostRoleCommandFactory.create(null, null, null, null);
        hostRoleCommand.setExecutionCommandWrapper(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand));
        finalizeUpgradeAction.setExecutionCommand(executionCommand);
        finalizeUpgradeAction.setHostRoleCommand(hostRoleCommand);
        // finalize
        CommandReport report = finalizeUpgradeAction.execute(null);
        Assert.assertNotNull(report);
        Assert.assertEquals(COMPLETED.name(), report.getStatus());
        for (HostVersionEntity hostVersion : hostVersions) {
            RepositoryVersionEntity hostRepoVersion = hostVersion.getRepositoryVersion();
            if (repositoryVersion2110.equals(hostRepoVersion)) {
                Assert.assertEquals(CURRENT, hostVersion.getState());
            } else {
                Assert.assertEquals(OUT_OF_SYNC, hostVersion.getState());
            }
        }
    }
}

