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
package org.apache.ambari.server.controller.internal;


import ConfigureTask.PARAMETER_ASSOCIATED_SERVICE;
import Direction.DOWNGRADE;
import Direction.UPGRADE;
import HostRoleStatus.ABORTED;
import HostRoleStatus.COMPLETED;
import HostRoleStatus.IN_PROGRESS;
import KerberosHelper.ALLOW_RETRY;
import KerberosHelper.DIRECTIVE_CONFIG_UPDATE_POLICY;
import KerberosHelperImpl.SupportedCustomOperation.REGENERATE_KEYTABS;
import KeyNames.COMMAND_TIMEOUT;
import RepositoryType.PATCH;
import RepositoryType.STANDARD;
import Role.AMBARI_SERVER_ACTION;
import Role.ZOOKEEPER_SERVER;
import RoleCommand.CUSTOM_COMMAND;
import SecurityType.KERBEROS;
import UpdateConfigurationPolicy.NEW_AND_IDENTITIES;
import UpgradeItemResourceProvider.UPGRADE_GROUP_ID;
import UpgradeResourceProvider.EXECUTE_TASK_ROLE;
import UpgradeResourceProvider.REQUEST_PROGRESS_PERCENT_ID;
import UpgradeResourceProvider.UPGRADE_CLUSTER_NAME;
import UpgradeResourceProvider.UPGRADE_DIRECTION;
import UpgradeResourceProvider.UPGRADE_HOST_ORDERED_HOSTS;
import UpgradeResourceProvider.UPGRADE_ID;
import UpgradeResourceProvider.UPGRADE_PACK;
import UpgradeResourceProvider.UPGRADE_REPO_VERSION_ID;
import UpgradeResourceProvider.UPGRADE_REQUEST_ID;
import UpgradeResourceProvider.UPGRADE_REQUEST_STATUS;
import UpgradeResourceProvider.UPGRADE_REVERT_UPGRADE_ID;
import UpgradeResourceProvider.UPGRADE_SKIP_FAILURES;
import UpgradeResourceProvider.UPGRADE_SKIP_MANUAL_VERIFICATION;
import UpgradeResourceProvider.UPGRADE_SKIP_PREREQUISITE_CHECKS;
import UpgradeResourceProvider.UPGRADE_SKIP_SC_FAILURES;
import UpgradeResourceProvider.UPGRADE_SUSPENDED;
import UpgradeResourceProvider.UPGRADE_TYPE;
import UpgradeState.NONE;
import UpgradeType.HOST_ORDERED;
import UpgradeType.ROLLING;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.RoleCommand;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapper;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapperFactory;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.agent.stomp.AgentConfigsHolder;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.ExecutionCommandDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.RequestDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.dao.StageDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.ExecutionCommandEntity;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.RepoDefinitionEntity;
import org.apache.ambari.server.orm.entities.RepoOsEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.RequestEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.orm.entities.UpgradeGroupEntity;
import org.apache.ambari.server.orm.entities.UpgradeHistoryEntity;
import org.apache.ambari.server.orm.entities.UpgradeItemEntity;
import org.apache.ambari.server.serveraction.upgrades.AutoSkipFailedSummaryAction;
import org.apache.ambari.server.serveraction.upgrades.ConfigureAction;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.topology.TopologyManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;

import static UpgradeResourceProvider.EXECUTE_TASK_ROLE;
import static UpgradeResourceProvider.s_upgradeDAO;


/**
 * UpgradeResourceDefinition tests.
 */
public class UpgradeResourceProviderTest extends EasyMockSupport {
    private UpgradeDAO upgradeDao = null;

    private RequestDAO requestDao = null;

    private RepositoryVersionDAO repoVersionDao = null;

    private Injector injector;

    private Clusters clusters;

    private Cluster cluster;

    private AmbariManagementController amc;

    private ConfigHelper configHelper;

    private AgentConfigsHolder agentConfigsHolder = createNiceMock(AgentConfigsHolder.class);

    private StackDAO stackDAO;

    private TopologyManager topologyManager;

    private ConfigFactory configFactory;

    private HostRoleCommandDAO hrcDAO;

    private KerberosHelper kerberosHelperMock = createNiceMock(KerberosHelper.class);

    RepositoryVersionEntity repoVersionEntity2110;

    RepositoryVersionEntity repoVersionEntity2111;

    RepositoryVersionEntity repoVersionEntity2112;

    RepositoryVersionEntity repoVersionEntity2200;

    @Test
    public void testCreateResourcesWithAutoSkipFailures() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_TYPE, ROLLING.toString());
        requestProps.put(UPGRADE_SKIP_FAILURES, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_SC_FAILURES, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_MANUAL_VERIFICATION, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity entity = upgrades.get(0);
        Assert.assertEquals(cluster.getClusterId(), entity.getClusterId().longValue());
        List<UpgradeGroupEntity> upgradeGroups = entity.getUpgradeGroups();
        Assert.assertEquals(3, upgradeGroups.size());
        UpgradeGroupEntity preClusterGroup = upgradeGroups.get(0);
        Assert.assertEquals("PRE_CLUSTER", preClusterGroup.getName());
        List<UpgradeItemEntity> preClusterUpgradeItems = preClusterGroup.getItems();
        Assert.assertEquals(2, preClusterUpgradeItems.size());
        Assert.assertEquals("Foo", parseSingleMessage(preClusterUpgradeItems.get(0).getText()));
        Assert.assertEquals("Foo", parseSingleMessage(preClusterUpgradeItems.get(1).getText()));
        UpgradeGroupEntity zookeeperGroup = upgradeGroups.get(1);
        Assert.assertEquals("ZOOKEEPER", zookeeperGroup.getName());
        List<UpgradeItemEntity> zookeeperUpgradeItems = zookeeperGroup.getItems();
        Assert.assertEquals(5, zookeeperUpgradeItems.size());
        Assert.assertEquals("This is a manual task with a placeholder of placeholder-rendered-properly", parseSingleMessage(zookeeperUpgradeItems.get(0).getText()));
        Assert.assertEquals("Restarting ZooKeeper Server on h1", zookeeperUpgradeItems.get(1).getText());
        Assert.assertEquals("Updating configuration zookeeper-newconfig", zookeeperUpgradeItems.get(2).getText());
        Assert.assertEquals("Service Check ZooKeeper", zookeeperUpgradeItems.get(3).getText());
        Assert.assertTrue(zookeeperUpgradeItems.get(4).getText().contains("There are failures that were automatically skipped"));
        // the last upgrade item is the skipped failure check
        UpgradeItemEntity skippedFailureCheck = zookeeperUpgradeItems.get(((zookeeperUpgradeItems.size()) - 1));
        skippedFailureCheck.getTasks().contains(AutoSkipFailedSummaryAction.class.getName());
        UpgradeGroupEntity postClusterGroup = upgradeGroups.get(2);
        Assert.assertEquals("POST_CLUSTER", postClusterGroup.getName());
        List<UpgradeItemEntity> postClusterUpgradeItems = postClusterGroup.getItems();
        Assert.assertEquals(2, postClusterUpgradeItems.size());
        Assert.assertEquals("Please confirm you are ready to finalize", parseSingleMessage(postClusterUpgradeItems.get(0).getText()));
        Assert.assertEquals("Save Cluster State", postClusterUpgradeItems.get(1).getText());
    }

    @Test
    public void testCreateResourcesWithAutoSkipManualVerification() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_TYPE, ROLLING.toString());
        requestProps.put(UPGRADE_SKIP_MANUAL_VERIFICATION, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity entity = upgrades.get(0);
        Assert.assertEquals(cluster.getClusterId(), entity.getClusterId().longValue());
        List<UpgradeGroupEntity> upgradeGroups = entity.getUpgradeGroups();
        Assert.assertEquals(2, upgradeGroups.size());
        UpgradeGroupEntity zookeeperGroup = upgradeGroups.get(0);
        Assert.assertEquals("ZOOKEEPER", zookeeperGroup.getName());
        List<UpgradeItemEntity> zookeeperUpgradeItems = zookeeperGroup.getItems();
        Assert.assertEquals(3, zookeeperUpgradeItems.size());
        Assert.assertEquals("Restarting ZooKeeper Server on h1", zookeeperUpgradeItems.get(0).getText());
        Assert.assertEquals("Updating configuration zookeeper-newconfig", zookeeperUpgradeItems.get(1).getText());
        Assert.assertEquals("Service Check ZooKeeper", zookeeperUpgradeItems.get(2).getText());
        UpgradeGroupEntity postClusterGroup = upgradeGroups.get(1);
        Assert.assertEquals("POST_CLUSTER", postClusterGroup.getName());
        List<UpgradeItemEntity> postClusterUpgradeItems = postClusterGroup.getItems();
        Assert.assertEquals(1, postClusterUpgradeItems.size());
        Assert.assertEquals("Save Cluster State", postClusterUpgradeItems.get(0).getText());
    }

    @Test
    public void testCreateResourcesWithAutoSkipAll() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_TYPE, ROLLING.toString());
        requestProps.put(UPGRADE_SKIP_FAILURES, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_SC_FAILURES, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_MANUAL_VERIFICATION, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity entity = upgrades.get(0);
        Assert.assertEquals(cluster.getClusterId(), entity.getClusterId().longValue());
        List<UpgradeGroupEntity> upgradeGroups = entity.getUpgradeGroups();
        Assert.assertEquals(2, upgradeGroups.size());
        UpgradeGroupEntity zookeeperGroup = upgradeGroups.get(0);
        Assert.assertEquals("ZOOKEEPER", zookeeperGroup.getName());
        List<UpgradeItemEntity> zookeeperUpgradeItems = zookeeperGroup.getItems();
        Assert.assertEquals(4, zookeeperUpgradeItems.size());
        Assert.assertEquals("Restarting ZooKeeper Server on h1", zookeeperUpgradeItems.get(0).getText());
        Assert.assertEquals("Updating configuration zookeeper-newconfig", zookeeperUpgradeItems.get(1).getText());
        Assert.assertEquals("Service Check ZooKeeper", zookeeperUpgradeItems.get(2).getText());
        Assert.assertTrue(zookeeperUpgradeItems.get(3).getText().contains("There are failures that were automatically skipped"));
        // the last upgrade item is the skipped failure check
        UpgradeItemEntity skippedFailureCheck = zookeeperUpgradeItems.get(((zookeeperUpgradeItems.size()) - 1));
        skippedFailureCheck.getTasks().contains(AutoSkipFailedSummaryAction.class.getName());
        UpgradeGroupEntity postClusterGroup = upgradeGroups.get(1);
        Assert.assertEquals("POST_CLUSTER", postClusterGroup.getName());
        List<UpgradeItemEntity> postClusterUpgradeItems = postClusterGroup.getItems();
        Assert.assertEquals(1, postClusterUpgradeItems.size());
        Assert.assertEquals("Save Cluster State", postClusterUpgradeItems.get(0).getText());
    }

    @Test
    public void testGetResources() throws Exception {
        RequestStatus status = testCreateResources();
        Set<Resource> createdResources = status.getAssociatedResources();
        Assert.assertEquals(1, createdResources.size());
        Resource res = createdResources.iterator().next();
        Long id = ((Long) (res.getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(id);
        Assert.assertEquals(Long.valueOf(1), id);
        // upgrade
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add("Upgrade");
        Predicate predicate = new PredicateBuilder().property(UPGRADE_REQUEST_ID).equals("1").and().property(UPGRADE_CLUSTER_NAME).equals("c1").toPredicate();
        Request request = PropertyHelper.getReadRequest(propertyIds);
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Set<Resource> resources = upgradeResourceProvider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        res = resources.iterator().next();
        Assert.assertNotNull(res.getPropertyValue("Upgrade/progress_percent"));
        Assert.assertNotNull(res.getPropertyValue(UPGRADE_DIRECTION));
        Assert.assertEquals(UPGRADE, res.getPropertyValue(UPGRADE_DIRECTION));
        Assert.assertEquals(false, res.getPropertyValue(UPGRADE_SKIP_FAILURES));
        Assert.assertEquals(false, res.getPropertyValue(UPGRADE_SKIP_SC_FAILURES));
        Assert.assertEquals(ROLLING, res.getPropertyValue(UPGRADE_TYPE));
        // upgrade groups
        propertyIds.clear();
        propertyIds.add("UpgradeGroup");
        predicate = new PredicateBuilder().property(UpgradeGroupResourceProvider.UPGRADE_REQUEST_ID).equals("1").and().property(UpgradeGroupResourceProvider.UPGRADE_CLUSTER_NAME).equals("c1").toPredicate();
        request = PropertyHelper.getReadRequest(propertyIds);
        ResourceProvider upgradeGroupResourceProvider = new UpgradeGroupResourceProvider(amc);
        resources = upgradeGroupResourceProvider.getResources(request, predicate);
        Assert.assertEquals(3, resources.size());
        res = resources.iterator().next();
        Assert.assertNotNull(res.getPropertyValue("UpgradeGroup/status"));
        Assert.assertNotNull(res.getPropertyValue("UpgradeGroup/group_id"));
        Assert.assertNotNull(res.getPropertyValue("UpgradeGroup/total_task_count"));
        Assert.assertNotNull(res.getPropertyValue("UpgradeGroup/in_progress_task_count"));
        Assert.assertNotNull(res.getPropertyValue("UpgradeGroup/completed_task_count"));
        // upgrade items
        propertyIds.clear();
        propertyIds.add("UpgradeItem");
        predicate = new PredicateBuilder().property(UPGRADE_GROUP_ID).equals("1").and().property(UpgradeItemResourceProvider.UPGRADE_REQUEST_ID).equals("1").and().property(UpgradeItemResourceProvider.UPGRADE_CLUSTER_NAME).equals("c1").toPredicate();
        request = PropertyHelper.getReadRequest(propertyIds);
        ResourceProvider upgradeItemResourceProvider = new UpgradeItemResourceProvider(amc);
        resources = upgradeItemResourceProvider.getResources(request, predicate);
        Assert.assertEquals(2, resources.size());
        res = resources.iterator().next();
        Assert.assertNotNull(res.getPropertyValue("UpgradeItem/status"));
        // !!! check for manual stage vs item text
        propertyIds.clear();
        propertyIds.add("UpgradeItem");
        predicate = new PredicateBuilder().property(UPGRADE_GROUP_ID).equals("3").and().property(UpgradeItemResourceProvider.UPGRADE_REQUEST_ID).equals("1").and().property(UpgradeItemResourceProvider.UPGRADE_CLUSTER_NAME).equals("c1").toPredicate();
        request = PropertyHelper.getReadRequest(propertyIds);
        upgradeItemResourceProvider = new UpgradeItemResourceProvider(amc);
        resources = upgradeItemResourceProvider.getResources(request, predicate);
        Assert.assertEquals(2, resources.size());
        res = resources.iterator().next();
        Assert.assertEquals("Confirm Finalize", res.getPropertyValue("UpgradeItem/context"));
        String msgStr = res.getPropertyValue("UpgradeItem/text").toString();
        JsonParser parser = new JsonParser();
        JsonArray msgArray = ((JsonArray) (parser.parse(msgStr)));
        JsonObject msg = ((JsonObject) (msgArray.get(0)));
        Assert.assertTrue(msg.get("message").getAsString().startsWith("Please confirm"));
    }

    /**
     * Tests that retrieving an upgrade correctly populates less common upgrade
     * options correctly.
     */
    @Test
    public void testGetResourcesWithSpecialOptions() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(0, upgrades.size());
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2111.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        // tests skipping SC failure options
        requestProps.put(UPGRADE_SKIP_FAILURES, "true");
        requestProps.put(UPGRADE_SKIP_SC_FAILURES, "true");
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        Assert.assertNotNull(status);
        // upgrade
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add("Upgrade");
        Predicate predicate = new PredicateBuilder().property(UPGRADE_REQUEST_ID).equals("1").and().property(UPGRADE_CLUSTER_NAME).equals("c1").toPredicate();
        request = PropertyHelper.getReadRequest(propertyIds);
        Set<Resource> resources = upgradeResourceProvider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        Assert.assertEquals(true, resource.getPropertyValue(UPGRADE_SKIP_FAILURES));
        Assert.assertEquals(true, resource.getPropertyValue(UPGRADE_SKIP_SC_FAILURES));
    }

    @Test
    public void testCreatePartialDowngrade() throws Exception {
        clusters.addHost("h2");
        Host host = clusters.getHost("h2");
        Map<String, String> hostAttributes = new HashMap<>();
        hostAttributes.put("os_family", "redhat");
        hostAttributes.put("os_release_version", "6.3");
        host.setHostAttributes(hostAttributes);
        clusters.mapHostToCluster("h2", "c1");
        Cluster cluster = clusters.getCluster("c1");
        Service service = cluster.getService("ZOOKEEPER");
        // start out with 0 (sanity check)
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(0, upgrades.size());
        // a downgrade MUST have an upgrade to come from, so populate an upgrade in
        // the DB
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestId(2L);
        requestEntity.setClusterId(cluster.getClusterId());
        requestEntity.setStages(new ArrayList());
        requestDao.create(requestEntity);
        UpgradeEntity upgradeEntity = new UpgradeEntity();
        upgradeEntity.setClusterId(cluster.getClusterId());
        upgradeEntity.setDirection(UPGRADE);
        upgradeEntity.setRepositoryVersion(repoVersionEntity2200);
        upgradeEntity.setUpgradePackage("upgrade_test");
        upgradeEntity.setUpgradePackStackId(repoVersionEntity2200.getStackId());
        upgradeEntity.setUpgradeType(ROLLING);
        upgradeEntity.setRequestEntity(requestEntity);
        UpgradeHistoryEntity history = new UpgradeHistoryEntity();
        history.setUpgrade(upgradeEntity);
        history.setFromRepositoryVersion(service.getDesiredRepositoryVersion());
        history.setTargetRepositoryVersion(repoVersionEntity2200);
        history.setServiceName(service.getName());
        history.setComponentName("ZOKKEEPER_SERVER");
        upgradeEntity.addHistory(history);
        history = new UpgradeHistoryEntity();
        history.setUpgrade(upgradeEntity);
        history.setFromRepositoryVersion(service.getDesiredRepositoryVersion());
        history.setTargetRepositoryVersion(repoVersionEntity2200);
        history.setServiceName(service.getName());
        history.setComponentName("ZOKKEEPER_CLIENT");
        upgradeEntity.addHistory(history);
        upgradeDao.create(upgradeEntity);
        upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        // push a ZK server foward to the new repo version, leaving the old one on
        // the old version
        ServiceComponent component = service.getServiceComponent("ZOOKEEPER_SERVER");
        ServiceComponentHost sch = component.addServiceComponentHost("h2");
        sch.setVersion(repoVersionEntity2200.getVersion());
        UpgradeEntity lastUpgrade = upgradeDao.findLastUpgradeForCluster(cluster.getClusterId(), UPGRADE);
        Assert.assertNotNull(lastUpgrade);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, DOWNGRADE.name());
        Map<String, String> requestInfoProperties = new HashMap<>();
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), requestInfoProperties);
        upgradeResourceProvider.createResources(request);
        upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(2, upgrades.size());
        UpgradeEntity downgrade = upgrades.get(1);
        Assert.assertEquals(cluster.getClusterId(), downgrade.getClusterId().longValue());
        List<UpgradeGroupEntity> upgradeGroups = downgrade.getUpgradeGroups();
        Assert.assertEquals(3, upgradeGroups.size());
        // the ZK restart group should only have 3 entries since the ZK server on h1
        // didn't get upgraded
        UpgradeGroupEntity group = upgradeGroups.get(1);
        Assert.assertEquals("ZOOKEEPER", group.getName());
        Assert.assertEquals(3, group.getItems().size());
    }

    @Test
    public void testDowngradeToBase() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Assert.assertEquals(cluster.getDesiredStackVersion().getStackId(), "HDP-2.1.1");
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity upgrade = upgrades.get(0);
        Assert.assertEquals("HDP-2.2.0", cluster.getDesiredStackVersion().getStackId());
        // now abort the upgrade so another can be created
        abortUpgrade(upgrade.getRequestId());
        // create another upgrade which should fail
        requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, "9999");
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        try {
            upgradeResourceProvider.createResources(request);
            Assert.fail("Expected an exception going downgrade with no upgrade pack");
        } catch (Exception e) {
            // !!! expected
        }
        // fix the properties and try again
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, DOWNGRADE.name());
        Map<String, String> requestInfoProperties = new HashMap<>();
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), requestInfoProperties);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        Assert.assertEquals(1, status.getAssociatedResources().size());
        Resource r = status.getAssociatedResources().iterator().next();
        String id = r.getPropertyValue("Upgrade/request_id").toString();
        Assert.assertEquals("HDP-2.1.1", cluster.getDesiredStackVersion().getStackId());
        UpgradeEntity entity = upgradeDao.findUpgrade(Long.parseLong(id));
        Assert.assertNotNull(entity);
        Assert.assertEquals(DOWNGRADE, entity.getDirection());
        // associated version is the FROM on DOWNGRADE
        Assert.assertEquals(repoVersionEntity2200.getVersion(), entity.getRepositoryVersion().getVersion());
        // target is by service
        Assert.assertEquals(repoVersionEntity2110.getVersion(), entity.getHistory().iterator().next().getTargetVersion());
        StageDAO dao = injector.getInstance(StageDAO.class);
        List<StageEntity> stages = dao.findByRequestId(entity.getRequestId());
        Assert.assertEquals("HDP-2.1.1", cluster.getDesiredStackVersion().getStackId());
        Gson gson = new Gson();
        for (StageEntity se : stages) {
            Map<String, String> map = gson.<Map<String, String>>fromJson(se.getCommandParamsStage(), Map.class);
            Assert.assertTrue(map.containsKey("upgrade_direction"));
            Assert.assertEquals("downgrade", map.get("upgrade_direction"));
        }
    }

    /**
     * Test Downgrade from the partially completed upgrade
     */
    @Test
    public void testNotFullDowngrade() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        // add additional service for the test
        Service service = cluster.addService("HIVE", repoVersionEntity2110);
        ServiceComponent component = service.addServiceComponent("HIVE_SERVER");
        ServiceComponentHost sch = component.addServiceComponentHost("h1");
        sch.setVersion("2.1.1.0");
        // create upgrade request
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_nonrolling_new_stack");
        requestProps.put(UPGRADE_TYPE, "NON_ROLLING");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        // check that upgrade was created and groups for the tested services are on place
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity upgrade = upgrades.get(0);
        List<UpgradeGroupEntity> groups = upgrade.getUpgradeGroups();
        boolean isHiveGroupFound = false;
        boolean isZKGroupFound = false;
        // look only for testing groups
        for (UpgradeGroupEntity group : groups) {
            if (group.getName().equalsIgnoreCase("hive")) {
                isHiveGroupFound = true;
            } else
                if (group.getName().equalsIgnoreCase("zookeeper")) {
                    isZKGroupFound = true;
                }

        }
        Assert.assertTrue(isHiveGroupFound);
        Assert.assertTrue(isZKGroupFound);
        isHiveGroupFound = false;
        isZKGroupFound = false;
        sch.setVersion("2.2.0.0");
        // now abort the upgrade so another can be created
        abortUpgrade(upgrade.getRequestId());
        // create downgrade with one upgraded service
        service.setDesiredRepositoryVersion(repoVersionEntity2200);
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_PACK, "upgrade_nonrolling_new_stack");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, DOWNGRADE.name());
        Map<String, String> requestInfoProperties = new HashMap<>();
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), requestInfoProperties);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        UpgradeEntity upgradeEntity = upgradeDao.findUpgradeByRequestId(getRequestId(status));
        for (UpgradeGroupEntity group : upgradeEntity.getUpgradeGroups()) {
            if (group.getName().equalsIgnoreCase("hive")) {
                isHiveGroupFound = true;
            } else
                if (group.getName().equalsIgnoreCase("zookeeper")) {
                    isZKGroupFound = true;
                }

        }
        // as services not updated, nothing to downgrade
        Assert.assertTrue(isHiveGroupFound);
        Assert.assertFalse(isZKGroupFound);
    }

    @Test
    public void testAbortUpgrade() throws Exception {
        RequestStatus status = testCreateResources();
        Set<Resource> createdResources = status.getAssociatedResources();
        Assert.assertEquals(1, createdResources.size());
        Resource res = createdResources.iterator().next();
        Long id = ((Long) (res.getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(id);
        Assert.assertEquals(Long.valueOf(1), id);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_REQUEST_ID, id.toString());
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REQUEST_STATUS, "ABORTED");
        requestProps.put(UPGRADE_SUSPENDED, "true");
        UpgradeResourceProvider urp = createProvider(amc);
        Request req = PropertyHelper.getUpdateRequest(requestProps, null);
        urp.updateResources(req, null);
        List<HostRoleCommandEntity> commands = hrcDAO.findByRequest(id);
        int i = 0;
        for (HostRoleCommandEntity command : commands) {
            if (i < 3) {
                command.setStatus(COMPLETED);
            } else {
                command.setStatus(ABORTED);
            }
            hrcDAO.merge(command);
            i++;
        }
        req = PropertyHelper.getReadRequest(UPGRADE_CLUSTER_NAME, UPGRADE_ID, REQUEST_PROGRESS_PERCENT_ID);
        Predicate pred = new PredicateBuilder().property(UPGRADE_REQUEST_ID).equals(id.toString()).and().property(UPGRADE_CLUSTER_NAME).equals("c1").toPredicate();
        Set<Resource> resources = urp.getResources(req, pred);
        Assert.assertEquals(1, resources.size());
        res = resources.iterator().next();
        Double value = ((Double) (res.getPropertyValue(REQUEST_PROGRESS_PERCENT_ID)));
        Assert.assertEquals(37.5, value, 0.1);
    }

    @Test
    public void testResumeUpgrade() throws Exception {
        RequestStatus status = testCreateResources();
        Set<Resource> createdResources = status.getAssociatedResources();
        Assert.assertEquals(1, createdResources.size());
        Resource res = createdResources.iterator().next();
        Long id = ((Long) (res.getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(id);
        Assert.assertEquals(Long.valueOf(1), id);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_REQUEST_ID, id.toString());
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REQUEST_STATUS, "ABORTED");
        requestProps.put(UPGRADE_SUSPENDED, "true");
        UpgradeResourceProvider urp = createProvider(amc);
        // !!! make sure we can.  actual abort is tested elsewhere
        Request req = PropertyHelper.getUpdateRequest(requestProps, null);
        urp.updateResources(req, null);
        ActionManager am = injector.getInstance(ActionManager.class);
        List<HostRoleCommand> commands = am.getRequestTasks(id);
        boolean foundOne = false;
        for (HostRoleCommand hrc : commands) {
            if (hrc.getRole().equals(AMBARI_SERVER_ACTION)) {
                Assert.assertEquals((-1L), hrc.getHostId());
                Assert.assertNull(hrc.getHostName());
                foundOne = true;
            }
        }
        Assert.assertTrue("Expected at least one server-side action", foundOne);
        // make sure we have enough commands to properly test this
        Assert.assertTrue(((commands.size()) > 5));
        // now, set some status of the commands to reflect an actual aborted upgrade
        // (some completed, some faiures, some timeouts)
        HostRoleCommandDAO dao = injector.getInstance(HostRoleCommandDAO.class);
        for (int i = 0; i < (commands.size()); i++) {
            HostRoleCommand command = commands.get(i);
            HostRoleCommandEntity entity = dao.findByPK(command.getTaskId());
            // make sure to interweave failures/timeouts with completed
            final HostRoleStatus newStatus;
            switch (i) {
                case 0 :
                case 1 :
                    newStatus = HostRoleStatus.COMPLETED;
                    break;
                case 2 :
                    newStatus = HostRoleStatus.TIMEDOUT;
                    break;
                case 3 :
                    newStatus = HostRoleStatus.COMPLETED;
                    break;
                case 4 :
                    newStatus = HostRoleStatus.FAILED;
                    break;
                default :
                    newStatus = HostRoleStatus.ABORTED;
                    break;
            }
            entity.setStatus(newStatus);
            dao.merge(entity);
        }
        // resume the upgrade
        requestProps = new HashMap<>();
        requestProps.put(UPGRADE_REQUEST_ID, id.toString());
        requestProps.put(UPGRADE_REQUEST_STATUS, "PENDING");
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_SUSPENDED, "false");
        // !!! make sure we can.  actual reset is tested elsewhere
        req = PropertyHelper.getUpdateRequest(requestProps, null);
        urp.updateResources(req, null);
        // test that prior completions (both timedout and failure) did not go back
        // to PENDING
        commands = am.getRequestTasks(id);
        for (int i = 0; i < (commands.size()); i++) {
            HostRoleCommand command = commands.get(i);
            if (i < 5) {
                Assert.assertTrue(((command.getStatus()) != (HostRoleStatus.PENDING)));
            } else {
                Assert.assertTrue(((command.getStatus()) == (HostRoleStatus.PENDING)));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbortWithoutSuspendFlag() throws Exception {
        RequestStatus status = testCreateResources();
        Set<Resource> createdResources = status.getAssociatedResources();
        Assert.assertEquals(1, createdResources.size());
        Resource res = createdResources.iterator().next();
        Long id = ((Long) (res.getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(id);
        Assert.assertEquals(Long.valueOf(1), id);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_REQUEST_ID, id.toString());
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REQUEST_STATUS, "ABORTED");
        UpgradeResourceProvider urp = createProvider(amc);
        Request req = PropertyHelper.getUpdateRequest(requestProps, null);
        urp.updateResources(req, null);
    }

    @Test
    public void testDirectionUpgrade() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        StackEntity stackEntity = stackDAO.find("HDP", "2.1.1");
        RepositoryVersionEntity repoVersionEntity = new RepositoryVersionEntity();
        repoVersionEntity.setDisplayName("My New Version 3");
        repoVersionEntity.addRepoOsEntities(createTestOperatingSystems());
        repoVersionEntity.setStack(stackEntity);
        repoVersionEntity.setVersion("2.2.2.3");
        repoVersionDao.create(repoVersionEntity);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_direction");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity upgrade = upgrades.get(0);
        Long id = upgrade.getRequestId();
        Assert.assertEquals(3, upgrade.getUpgradeGroups().size());
        // Ensure that there are no items related to downgrade in the upgrade direction
        UpgradeGroupEntity group = upgrade.getUpgradeGroups().get(2);
        Assert.assertEquals("POST_CLUSTER", group.getName());
        Assert.assertTrue((!(group.getItems().isEmpty())));
        for (UpgradeItemEntity item : group.getItems()) {
            Assert.assertFalse(item.getText().toLowerCase().contains("downgrade"));
        }
        // now abort the upgrade so another can be created
        abortUpgrade(upgrade.getRequestId());
        requestProps.clear();
        // Now perform a downgrade
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_PACK, "upgrade_direction");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, DOWNGRADE.name());
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(2, upgrades.size());
        upgrade = null;
        for (UpgradeEntity u : upgrades) {
            if (!(u.getRequestId().equals(id))) {
                upgrade = u;
            }
        }
        Assert.assertNotNull(upgrade);
        List<UpgradeGroupEntity> groups = upgrade.getUpgradeGroups();
        Assert.assertEquals("Downgrade groups reduced from 3 to 2", 1, groups.size());
        group = upgrade.getUpgradeGroups().get(0);
        Assert.assertEquals("Execution items increased from 1 to 2", 2, group.getItems().size());
    }

    @Test
    public void testPercents() throws Exception {
        RequestStatus status = testCreateResources();
        Set<Resource> createdResources = status.getAssociatedResources();
        Assert.assertEquals(1, createdResources.size());
        Resource res = createdResources.iterator().next();
        Long id = ((Long) (res.getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(id);
        Assert.assertEquals(Long.valueOf(1), id);
        StageDAO stageDao = injector.getInstance(StageDAO.class);
        HostRoleCommandDAO hrcDao = injector.getInstance(HostRoleCommandDAO.class);
        List<StageEntity> stages = stageDao.findByRequestId(id);
        List<HostRoleCommandEntity> tasks = hrcDao.findByRequest(id);
        Set<Long> stageIds = new HashSet<>();
        for (StageEntity se : stages) {
            stageIds.add(se.getStageId());
        }
        CalculatedStatus calc = null;
        int i = 0;
        for (HostRoleCommandEntity hrce : tasks) {
            hrce.setStatus(IN_PROGRESS);
            hrcDao.merge(hrce);
            calc = CalculatedStatus.statusFromStageSummary(hrcDao.findAggregateCounts(id), stageIds);
            Assert.assertEquals((((i++) + 1) * 4.375), calc.getPercent(), 0.01);
            Assert.assertEquals(IN_PROGRESS, calc.getStatus());
        }
        i = 0;
        for (HostRoleCommandEntity hrce : tasks) {
            hrce.setStatus(COMPLETED);
            hrcDao.merge(hrce);
            calc = CalculatedStatus.statusFromStageSummary(hrcDao.findAggregateCounts(id), stageIds);
            Assert.assertEquals((35 + (((i++) + 1) * 8.125)), calc.getPercent(), 0.01);
            if (i < 8) {
                Assert.assertEquals(IN_PROGRESS, calc.getStatus());
            }
        }
        calc = CalculatedStatus.statusFromStageSummary(hrcDao.findAggregateCounts(id), stageIds);
        Assert.assertEquals(COMPLETED, calc.getStatus());
        Assert.assertEquals(100.0, calc.getPercent(), 0.01);
    }

    @Test
    public void testCreateCrossStackUpgrade() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        StackId oldStack = repoVersionEntity2110.getStackId();
        Assert.assertEquals(cluster.getDesiredStackVersion(), oldStack);
        for (Service s : cluster.getServices().values()) {
            Assert.assertEquals(oldStack, s.getDesiredStackId());
            for (ServiceComponent sc : s.getServiceComponents().values()) {
                Assert.assertEquals(oldStack, sc.getDesiredStackId());
                for (ServiceComponentHost sch : sc.getServiceComponentHosts().values()) {
                    Assert.assertEquals(repoVersionEntity2110.getVersion(), sch.getVersion());
                }
            }
        }
        Config config = configFactory.createNew(cluster, "zoo.cfg", "abcdefg", Collections.singletonMap("a", "b"), null);
        cluster.addDesiredConfig("admin", Collections.singleton(config));
        // create the upgrade across major versions
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity upgrade = upgrades.get(0);
        Assert.assertEquals(3, upgrade.getUpgradeGroups().size());
        UpgradeGroupEntity group = upgrade.getUpgradeGroups().get(2);
        Assert.assertEquals(2, group.getItems().size());
        group = upgrade.getUpgradeGroups().get(0);
        Assert.assertEquals(2, group.getItems().size());
        Assert.assertTrue(cluster.getDesiredConfigs().containsKey("zoo.cfg"));
        Assert.assertTrue(cluster.getDesiredStackVersion().getStackId().equals("HDP-2.2.0"));
        for (Service s : cluster.getServices().values()) {
            Assert.assertEquals(repoVersionEntity2200, s.getDesiredRepositoryVersion());
            for (ServiceComponent sc : s.getServiceComponents().values()) {
                Assert.assertEquals(repoVersionEntity2200, sc.getDesiredRepositoryVersion());
            }
        }
    }

    @Test
    public void testUpdateSkipSCFailures() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_TYPE, ROLLING.toString());
        requestProps.put(UPGRADE_SKIP_FAILURES, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_SKIP_SC_FAILURES, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_MANUAL_VERIFICATION, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(1);
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity entity = upgrades.get(0);
        HostRoleCommandDAO dao = injector.getInstance(HostRoleCommandDAO.class);
        List<HostRoleCommandEntity> tasks = dao.findByRequest(entity.getRequestId());
        for (HostRoleCommandEntity task : tasks) {
            if ((task.getRoleCommand()) == (RoleCommand.SERVICE_CHECK)) {
                StageEntity stage = task.getStage();
                if ((stage.isSkippable()) && (stage.isAutoSkipOnFailureSupported())) {
                    Assert.assertTrue(task.isFailureAutoSkipped());
                } else {
                    Assert.assertFalse(task.isFailureAutoSkipped());
                }
            } else {
                Assert.assertFalse(task.isFailureAutoSkipped());
            }
        }
    }

    @Test
    public void testUpdateSkipFailures() throws Exception {
        testCreateResourcesWithAutoSkipFailures();
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(1);
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity entity = upgrades.get(0);
        HostRoleCommandDAO dao = injector.getInstance(HostRoleCommandDAO.class);
        List<HostRoleCommandEntity> tasks = dao.findByRequest(entity.getRequestId());
        for (HostRoleCommandEntity task : tasks) {
            StageEntity stage = task.getStage();
            if ((stage.isSkippable()) && (stage.isAutoSkipOnFailureSupported())) {
                Assert.assertTrue(task.isFailureAutoSkipped());
            } else {
                Assert.assertFalse(task.isFailureAutoSkipped());
            }
        }
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_SKIP_FAILURES, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_SC_FAILURES, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_REQUEST_ID, ("" + (entity.getRequestId())));
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getUpdateRequest(requestProps, null);
        upgradeResourceProvider.updateResources(request, null);
        tasks = dao.findByRequest(entity.getRequestId());
        for (HostRoleCommandEntity task : tasks) {
            if ((task.getRoleCommand()) == (RoleCommand.SERVICE_CHECK)) {
                Assert.assertFalse(task.isFailureAutoSkipped());
            } else {
                StageEntity stage = task.getStage();
                if ((stage.isSkippable()) && (stage.isAutoSkipOnFailureSupported())) {
                    Assert.assertTrue(task.isFailureAutoSkipped());
                } else {
                    Assert.assertFalse(task.isFailureAutoSkipped());
                }
            }
        }
        requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_SKIP_FAILURES, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_SKIP_SC_FAILURES, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_REQUEST_ID, ("" + (entity.getRequestId())));
        request = PropertyHelper.getUpdateRequest(requestProps, null);
        upgradeResourceProvider.updateResources(request, null);
        tasks = dao.findByRequest(entity.getRequestId());
        for (HostRoleCommandEntity task : tasks) {
            if ((task.getRoleCommand()) == (RoleCommand.SERVICE_CHECK)) {
                StageEntity stage = task.getStage();
                if ((stage.isSkippable()) && (stage.isAutoSkipOnFailureSupported())) {
                    Assert.assertTrue(task.isFailureAutoSkipped());
                } else {
                    Assert.assertFalse(task.isFailureAutoSkipped());
                }
            } else {
                Assert.assertFalse(task.isFailureAutoSkipped());
            }
        }
        requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_SKIP_FAILURES, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_SKIP_SC_FAILURES, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_REQUEST_ID, ("" + (entity.getRequestId())));
        request = PropertyHelper.getUpdateRequest(requestProps, null);
        upgradeResourceProvider.updateResources(request, null);
        tasks = dao.findByRequest(entity.getRequestId());
        for (HostRoleCommandEntity task : tasks) {
            Assert.assertFalse(task.isFailureAutoSkipped());
        }
    }

    /**
     * Tests that an error while commiting the data cleanly rolls back the transaction so that
     * no request/stage/tasks are created.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRollback() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_TYPE, ROLLING.toString());
        requestProps.put(UPGRADE_SKIP_MANUAL_VERIFICATION, Boolean.FALSE.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        // this will cause a NPE when creating the upgrade, allowing us to test
        // rollback
        UpgradeResourceProvider upgradeResourceProvider = createProvider(amc);
        s_upgradeDAO = null;
        try {
            Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
            upgradeResourceProvider.createResources(request);
            Assert.fail("Expected a NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(0, upgrades.size());
        List<Long> requestIds = requestDao.findAllRequestIds(1, true, cluster.getClusterId());
        Assert.assertEquals(0, requestIds.size());
    }

    /**
     * Tests that a {@link UpgradeType#HOST_ORDERED} upgrade throws an exception
     * on missing hosts.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateHostOrderedUpgradeThrowsExceptions() throws Exception {
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test_host_ordered");
        requestProps.put(UPGRADE_TYPE, HOST_ORDERED.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        try {
            upgradeResourceProvider.createResources(request);
            Assert.fail("The request should have failed due to the missing Upgrade/host_order property");
        } catch (SystemException systemException) {
            // expected
        }
        // stick a bad host_ordered_hosts in there which has the wrong hosts
        Set<Map<String, List<String>>> hostsOrder = new LinkedHashSet<>();
        Map<String, List<String>> hostGrouping = new HashMap<>();
        hostGrouping.put("hosts", Lists.newArrayList("invalid-host"));
        hostsOrder.add(hostGrouping);
        requestProps.put(UPGRADE_HOST_ORDERED_HOSTS, hostsOrder);
        try {
            upgradeResourceProvider.createResources(request);
            Assert.fail("The request should have failed due to invalid hosts");
        } catch (SystemException systemException) {
            // expected
        }
        // use correct hosts now
        hostsOrder = new LinkedHashSet<>();
        hostGrouping = new HashMap<>();
        hostGrouping.put("hosts", Lists.newArrayList("h1"));
        hostsOrder.add(hostGrouping);
        requestProps.put(UPGRADE_HOST_ORDERED_HOSTS, hostsOrder);
        upgradeResourceProvider.createResources(request);
        // make sure that the desired versions are updated
        Cluster cluster = clusters.getCluster("c1");
        Assert.assertNotNull(cluster);
        Service service = cluster.getService("ZOOKEEPER");
        Assert.assertEquals(repoVersionEntity2200, service.getDesiredRepositoryVersion());
    }

    /**
     * Exercises that a component that goes from upgrade->downgrade that switches
     * {@code versionAdvertised} between will go to UKNOWN. This exercises
     * {@link UpgradeHelper#updateDesiredRepositoriesAndConfigs(UpgradeContext)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateUpgradeDowngradeCycleAdvertisingVersion() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Service service = cluster.addService("STORM", repoVersionEntity2110);
        ServiceComponent component = service.addServiceComponent("DRPC_SERVER");
        ServiceComponentHost sch = component.addServiceComponentHost("h1");
        sch.setVersion("2.1.1.0");
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        Map<String, String> requestInfoProperties = new HashMap<>();
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), requestInfoProperties);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        Assert.assertEquals(1, status.getAssociatedResources().size());
        Resource r = status.getAssociatedResources().iterator().next();
        String id = r.getPropertyValue("Upgrade/request_id").toString();
        component = service.getServiceComponent("DRPC_SERVER");
        Assert.assertNotNull(component);
        Assert.assertEquals("2.2.0.0", component.getDesiredVersion());
        ServiceComponentHost hostComponent = component.getServiceComponentHost("h1");
        Assert.assertEquals(UpgradeState.IN_PROGRESS, hostComponent.getUpgradeState());
        // !!! can't start a downgrade until cancelling the previous upgrade
        abortUpgrade(Long.parseLong(id));
        requestProps.clear();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, DOWNGRADE.name());
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), requestInfoProperties);
        status = upgradeResourceProvider.createResources(request);
        component = service.getServiceComponent("DRPC_SERVER");
        Assert.assertNotNull(component);
        Assert.assertEquals(repoVersionEntity2110, component.getDesiredRepositoryVersion());
        hostComponent = component.getServiceComponentHost("h1");
        Assert.assertEquals(NONE, hostComponent.getUpgradeState());
        Assert.assertEquals("UNKNOWN", hostComponent.getVersion());
    }

    /**
     * Ensures that stages created with an HOU are sequential and do not skip any
     * IDs. When there are stages with IDs like (1,2,3,5,6,7,10), the request will
     * get stuck in a PENDING state. This affects HOU specifically since they can
     * potentially try to create empty stages which won't get persisted (such as a
     * STOP on client-only hosts).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEmptyGroupingsDoNotSkipStageIds() throws Exception {
        StageDAO stageDao = injector.getInstance(StageDAO.class);
        Assert.assertEquals(0, stageDao.findAll().size());
        // strip out all non-client components - clients don't have STOP commands
        Cluster cluster = clusters.getCluster("c1");
        List<ServiceComponentHost> schs = cluster.getServiceComponentHosts("h1");
        for (ServiceComponentHost sch : schs) {
            if (sch.isClientComponent()) {
                continue;
            }
            cluster.removeServiceComponentHost(sch);
        }
        // define host order
        Set<Map<String, List<String>>> hostsOrder = new LinkedHashSet<>();
        Map<String, List<String>> hostGrouping = new HashMap<>();
        hostGrouping = new HashMap<>();
        hostGrouping.put("hosts", Lists.newArrayList("h1"));
        hostsOrder.add(hostGrouping);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test_host_ordered");
        requestProps.put(UPGRADE_TYPE, HOST_ORDERED.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        requestProps.put(UPGRADE_HOST_ORDERED_HOSTS, hostsOrder);
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<StageEntity> stages = stageDao.findByRequestId(cluster.getUpgradeInProgress().getRequestId());
        Assert.assertEquals(3, stages.size());
        long expectedStageId = 1L;
        for (StageEntity stage : stages) {
            Assert.assertEquals((expectedStageId++), stage.getStageId().longValue());
        }
    }

    /**
     * Tests that from/to repository version history is created correctly on the
     * upgrade.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUpgradeHistory() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_TYPE, ROLLING.toString());
        requestProps.put(UPGRADE_SKIP_MANUAL_VERIFICATION, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity upgrade = cluster.getUpgradeInProgress();
        List<UpgradeHistoryEntity> histories = upgrade.getHistory();
        Assert.assertEquals(2, histories.size());
        for (UpgradeHistoryEntity history : histories) {
            Assert.assertEquals("ZOOKEEPER", history.getServiceName());
            Assert.assertEquals(repoVersionEntity2110, history.getFromReposistoryVersion());
            Assert.assertEquals(repoVersionEntity2200, history.getTargetRepositoryVersion());
        }
        // abort the upgrade and create the downgrade
        abortUpgrade(upgrade.getRequestId());
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_PACK, "upgrade_nonrolling_new_stack");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, DOWNGRADE.name());
        Map<String, String> requestInfoProperties = new HashMap<>();
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), requestInfoProperties);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        UpgradeEntity downgrade = upgradeDao.findUpgradeByRequestId(getRequestId(status));
        Assert.assertEquals(DOWNGRADE, downgrade.getDirection());
        // check from/to history
        histories = downgrade.getHistory();
        Assert.assertEquals(2, histories.size());
        for (UpgradeHistoryEntity history : histories) {
            Assert.assertEquals("ZOOKEEPER", history.getServiceName());
            Assert.assertEquals(repoVersionEntity2200, history.getFromReposistoryVersion());
            Assert.assertEquals(repoVersionEntity2110, history.getTargetRepositoryVersion());
        }
    }

    /**
     * Tests that from/to repository version history is created correctly on the
     * upgrade.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreatePatchRevertUpgrade() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        // add a single ZK server and client on 2.1.1.0
        Service service = cluster.addService("HBASE", repoVersionEntity2110);
        ServiceComponent component = service.addServiceComponent("HBASE_MASTER");
        ServiceComponentHost sch = component.addServiceComponentHost("h1");
        sch.setVersion("2.1.1.0");
        File f = new File("src/test/resources/hbase_version_test.xml");
        repoVersionEntity2112.setType(PATCH);
        repoVersionEntity2112.setVersionXml(IOUtils.toString(new FileInputStream(f)));
        repoVersionEntity2112.setVersionXsd("version_definition.xsd");
        repoVersionDao.merge(repoVersionEntity2112);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(0, upgrades.size());
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2112.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity upgradeEntity = upgrades.get(0);
        Assert.assertEquals(PATCH, upgradeEntity.getOrchestration());
        // should be false since only finalization actually sets this bit
        Assert.assertEquals(false, upgradeEntity.isRevertAllowed());
        // fake it now so the rest of the test passes
        upgradeEntity.setRevertAllowed(true);
        upgradeEntity = upgradeDao.merge(upgradeEntity);
        // !!! make it look like the cluster is done
        cluster.setUpgradeEntity(null);
        requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REVERT_UPGRADE_ID, upgradeEntity.getId());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(2, upgrades.size());
        boolean found = false;
        Function<UpgradeHistoryEntity, String> function = new Function<UpgradeHistoryEntity, String>() {
            @Override
            public String apply(UpgradeHistoryEntity input) {
                return ((input.getServiceName()) + "/") + (input.getComponentName());
            }
        };
        for (UpgradeEntity upgrade : upgrades) {
            if ((upgrade.getId()) != (upgradeEntity.getId())) {
                found = true;
                Assert.assertEquals(upgradeEntity.getOrchestration(), upgrade.getOrchestration());
                Collection<String> upgradeEntityStrings = Collections2.transform(upgradeEntity.getHistory(), function);
                Collection<String> upgradeStrings = Collections2.transform(upgrade.getHistory(), function);
                Collection<?> diff = CollectionUtils.disjunction(upgradeEntityStrings, upgradeStrings);
                Assert.assertEquals("Verify the same set of components was orchestrated", 0, diff.size());
            }
        }
        Assert.assertTrue(found);
    }

    /**
     * Tests that when there is no revertable upgrade, a reversion of a specific
     * ugprade ID is not allowed.
     */
    @Test(expected = SystemException.class)
    public void testRevertFailsWhenNoRevertableUpgradeIsFound() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        // add a single ZK server and client on 2.1.1.0
        Service service = cluster.addService("HBASE", repoVersionEntity2110);
        ServiceComponent component = service.addServiceComponent("HBASE_MASTER");
        ServiceComponentHost sch = component.addServiceComponentHost("h1");
        sch.setVersion("2.1.1.0");
        File f = new File("src/test/resources/hbase_version_test.xml");
        repoVersionEntity2112.setType(PATCH);
        repoVersionEntity2112.setVersionXml(IOUtils.toString(new FileInputStream(f)));
        repoVersionEntity2112.setVersionXsd("version_definition.xsd");
        repoVersionDao.merge(repoVersionEntity2112);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(0, upgrades.size());
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2112.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
        upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(1, upgrades.size());
        UpgradeEntity upgradeEntity = upgrades.get(0);
        Assert.assertEquals(PATCH, upgradeEntity.getOrchestration());
        // !!! make it look like the cluster is done
        cluster.setUpgradeEntity(null);
        requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REVERT_UPGRADE_ID, upgradeEntity.getId());
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, Boolean.TRUE.toString());
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        upgradeResourceProvider.createResources(request);
    }

    @Test
    public void testCreatePatchWithConfigChanges() throws Exception {
        Cluster cluster = clusters.getCluster("c1");
        File f = new File("src/test/resources/version_definition_test_patch_config.xml");
        repoVersionEntity2112.setType(PATCH);
        repoVersionEntity2112.setVersionXml(IOUtils.toString(new FileInputStream(f)));
        repoVersionEntity2112.setVersionXsd("version_definition.xsd");
        repoVersionDao.merge(repoVersionEntity2112);
        List<UpgradeEntity> upgrades = upgradeDao.findUpgrades(cluster.getClusterId());
        Assert.assertEquals(0, upgrades.size());
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2112.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        // !!! test that a PATCH upgrade skips config changes
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        Set<Resource> resources = status.getAssociatedResources();
        Assert.assertEquals(1, resources.size());
        Long requestId = ((Long) (resources.iterator().next().getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(requestId);
        UpgradeEntity upgradeEntity = upgradeDao.findUpgradeByRequestId(requestId);
        Assert.assertEquals(PATCH, upgradeEntity.getOrchestration());
        HostRoleCommandDAO hrcDAO = injector.getInstance(HostRoleCommandDAO.class);
        List<HostRoleCommandEntity> commands = hrcDAO.findByRequest(upgradeEntity.getRequestId());
        boolean foundConfigTask = false;
        for (HostRoleCommandEntity command : commands) {
            if ((StringUtils.isNotBlank(command.getCustomCommandName())) && (command.getCustomCommandName().equals(ConfigureAction.class.getName()))) {
                foundConfigTask = true;
                break;
            }
        }
        Assert.assertFalse(foundConfigTask);
        // !!! test that a patch with a supported patch change gets picked up
        cluster.setUpgradeEntity(null);
        requestProps.put(UPGRADE_PACK, "upgrade_test_force_config_change");
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        status = upgradeResourceProvider.createResources(request);
        resources = status.getAssociatedResources();
        Assert.assertEquals(1, resources.size());
        requestId = ((Long) (resources.iterator().next().getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(requestId);
        upgradeEntity = upgradeDao.findUpgradeByRequestId(requestId);
        Assert.assertEquals(PATCH, upgradeEntity.getOrchestration());
        commands = hrcDAO.findByRequest(upgradeEntity.getRequestId());
        foundConfigTask = false;
        for (HostRoleCommandEntity command : commands) {
            if ((StringUtils.isNotBlank(command.getCustomCommandName())) && (command.getCustomCommandName().equals(ConfigureAction.class.getName()))) {
                foundConfigTask = true;
                ExecutionCommandDAO dao = injector.getInstance(ExecutionCommandDAO.class);
                ExecutionCommandEntity entity = dao.findByPK(command.getTaskId());
                ExecutionCommandWrapperFactory factory = injector.getInstance(ExecutionCommandWrapperFactory.class);
                ExecutionCommandWrapper wrapper = factory.createFromJson(new String(entity.getCommand()));
                Map<String, String> params = wrapper.getExecutionCommand().getCommandParams();
                Assert.assertTrue(params.containsKey(PARAMETER_ASSOCIATED_SERVICE));
                Assert.assertEquals("ZOOKEEPER", params.get(PARAMETER_ASSOCIATED_SERVICE));
                break;
            }
        }
        Assert.assertTrue(foundConfigTask);
        // !!! test that a regular upgrade will pick up the config change
        cluster.setUpgradeEntity(null);
        repoVersionEntity2112.setType(STANDARD);
        repoVersionDao.merge(repoVersionEntity2112);
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        status = upgradeResourceProvider.createResources(request);
        resources = status.getAssociatedResources();
        Assert.assertEquals(1, resources.size());
        requestId = ((Long) (resources.iterator().next().getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(requestId);
        upgradeEntity = upgradeDao.findUpgradeByRequestId(requestId);
        Assert.assertEquals(STANDARD, upgradeEntity.getOrchestration());
        commands = hrcDAO.findByRequest(upgradeEntity.getRequestId());
        foundConfigTask = false;
        for (HostRoleCommandEntity command : commands) {
            if ((StringUtils.isNotBlank(command.getCustomCommandName())) && (command.getCustomCommandName().equals(ConfigureAction.class.getName()))) {
                foundConfigTask = true;
                break;
            }
        }
        Assert.assertTrue(foundConfigTask);
    }

    @Test
    public void testTimeouts() throws Exception {
        StackEntity stackEntity = stackDAO.find("HDP", "2.1.1");
        RepositoryVersionEntity repoVersionEntity = new RepositoryVersionEntity();
        repoVersionEntity.setDisplayName("My New Version 3");
        List<RepoOsEntity> operatingSystems = new ArrayList<>();
        RepoDefinitionEntity repoDefinitionEntity1 = new RepoDefinitionEntity();
        repoDefinitionEntity1.setRepoID("HDP-UTILS");
        repoDefinitionEntity1.setBaseUrl("");
        repoDefinitionEntity1.setRepoName("HDP-UTILS");
        RepoDefinitionEntity repoDefinitionEntity2 = new RepoDefinitionEntity();
        repoDefinitionEntity2.setRepoID("HDP");
        repoDefinitionEntity2.setBaseUrl("");
        repoDefinitionEntity2.setRepoName("HDP");
        RepoOsEntity repoOsEntity = new RepoOsEntity();
        repoOsEntity.setFamily("redhat6");
        repoOsEntity.setAmbariManaged(true);
        repoOsEntity.addRepoDefinition(repoDefinitionEntity1);
        repoOsEntity.addRepoDefinition(repoDefinitionEntity2);
        operatingSystems.add(repoOsEntity);
        repoVersionEntity.addRepoOsEntities(operatingSystems);
        repoVersionEntity.setStack(stackEntity);
        repoVersionEntity.setVersion("2.2.2.3");
        repoVersionDao.create(repoVersionEntity);
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        Set<Resource> createdResources = status.getAssociatedResources();
        Assert.assertEquals(1, createdResources.size());
        Resource res = createdResources.iterator().next();
        Long id = ((Long) (res.getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(id);
        Assert.assertEquals(Long.valueOf(1), id);
        ActionManager am = injector.getInstance(ActionManager.class);
        List<HostRoleCommand> commands = am.getRequestTasks(id);
        boolean found = false;
        for (HostRoleCommand command : commands) {
            ExecutionCommandWrapper wrapper = command.getExecutionCommandWrapper();
            if ((command.getRole().equals(ZOOKEEPER_SERVER)) && (command.getRoleCommand().equals(CUSTOM_COMMAND))) {
                Map<String, String> commandParams = wrapper.getExecutionCommand().getCommandParams();
                Assert.assertTrue(commandParams.containsKey(COMMAND_TIMEOUT));
                Assert.assertEquals("824", commandParams.get(COMMAND_TIMEOUT));
                found = true;
            }
        }
        Assert.assertTrue("ZooKeeper timeout override was found", found);
    }

    /**
     * Tests that commands created for {@link org.apache.ambari.server.stack.upgrade.orchestrate.StageWrapper.Type#UPGRADE_TASKS} set the
     * service and component on the {@link ExecutionCommand}.
     * <p/>
     * Without this, commands of this type would not be able to determine which
     * service/component repository they should use when the command is scheduled
     * to run.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExecutionCommandServiceAndComponent() throws Exception {
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_execute_task_test");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        RequestStatus status = upgradeResourceProvider.createResources(request);
        Set<Resource> createdResources = status.getAssociatedResources();
        Assert.assertEquals(1, createdResources.size());
        Resource res = createdResources.iterator().next();
        Long id = ((Long) (res.getPropertyValue("Upgrade/request_id")));
        Assert.assertNotNull(id);
        Assert.assertEquals(Long.valueOf(1), id);
        ActionManager am = injector.getInstance(ActionManager.class);
        List<HostRoleCommand> commands = am.getRequestTasks(id);
        boolean foundActionExecuteCommand = false;
        for (HostRoleCommand command : commands) {
            ExecutionCommand executionCommand = command.getExecutionCommandWrapper().getExecutionCommand();
            if (StringUtils.equals(EXECUTE_TASK_ROLE, executionCommand.getRole())) {
                foundActionExecuteCommand = true;
                Assert.assertNotNull(executionCommand.getServiceName());
                Assert.assertNotNull(executionCommand.getComponentName());
            }
        }
        Assert.assertTrue(("There was no task found with the role of " + (EXECUTE_TASK_ROLE)), foundActionExecuteCommand);
    }

    /**
     * Tests that a {@link RegenerateKeytabsTask} causes the upgrade to inject the
     * correct stages.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateRegenerateKeytabStages() throws Exception {
        Capture<Map<String, String>> requestPropertyMapCapture = EasyMock.newCapture();
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(UPGRADE_CLUSTER_NAME, "c1");
        requestProps.put(UPGRADE_REPO_VERSION_ID, String.valueOf(repoVersionEntity2200.getId()));
        requestProps.put(UPGRADE_PACK, "upgrade_test_regenerate_keytabs");
        requestProps.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        requestProps.put(UPGRADE_DIRECTION, UPGRADE.name());
        cluster.setSecurityType(KERBEROS);
        RequestStageContainer requestStageContainer = createNiceMock(RequestStageContainer.class);
        expect(requestStageContainer.getStages()).andReturn(Lists.newArrayList()).once();
        expect(kerberosHelperMock.executeCustomOperations(eq(cluster), EasyMock.capture(requestPropertyMapCapture), EasyMock.anyObject(RequestStageContainer.class), eq(null))).andReturn(requestStageContainer).once();
        replayAll();
        ResourceProvider upgradeResourceProvider = createProvider(amc);
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        try {
            upgradeResourceProvider.createResources(request);
            Assert.fail("The mock request stage container should have caused a problem in JPA");
        } catch (IllegalArgumentException illegalArgumentException) {
            // ignore
        }
        verifyAll();
        Map<String, String> requestPropertyMap = requestPropertyMapCapture.getValue();
        Assert.assertEquals("true", requestPropertyMap.get(ALLOW_RETRY));
        Assert.assertEquals("missing", requestPropertyMap.get(REGENERATE_KEYTABS.name().toLowerCase()));
        Assert.assertEquals(NEW_AND_IDENTITIES.name(), requestPropertyMap.get(DIRECTIVE_CONFIG_UPDATE_POLICY.toLowerCase()));
    }

    /**
     *
     */
    private class MockModule implements Module {
        /**
         *
         */
        @Override
        public void configure(Binder binder) {
            binder.bind(ConfigHelper.class).toInstance(configHelper);
            binder.bind(AgentConfigsHolder.class).toInstance(agentConfigsHolder);
            binder.bind(KerberosHelper.class).toInstance(kerberosHelperMock);
        }
    }
}

