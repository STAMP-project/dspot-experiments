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


import Direction.UPGRADE;
import HostRoleStatus.FAILED;
import HostRoleStatus.PENDING;
import Resource.Type;
import Role.ZOOKEEPER_SERVER;
import RoleCommand.RESTART;
import UpgradeSummaryResourceProvider.UPGRADE_SUMMARY_CLUSTER_NAME;
import UpgradeSummaryResourceProvider.UPGRADE_SUMMARY_FAIL_REASON;
import UpgradeSummaryResourceProvider.UPGRADE_SUMMARY_REQUEST_ID;
import UpgradeType.ROLLING;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.RequestDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.dao.StageDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.RequestEntity;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeHelper;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNull;


/**
 * UpgradeSummaryResourceProvider tests.
 */
public class UpgradeSummaryResourceProviderTest {
    private HostDAO hostDAO;

    private StackDAO stackDAO;

    private RepositoryVersionDAO repoVersionDAO;

    private UpgradeDAO upgradeDAO;

    private RequestDAO requestDAO;

    private StageDAO stageDAO;

    private HostRoleCommandDAO hrcDAO;

    @Inject
    private UpgradeHelper m_upgradeHelper;

    private Injector injector;

    private Clusters clusters;

    private OrmTestHelper helper;

    private AmbariManagementController amc;

    private String clusterName = "c1";

    /**
     * Test UpgradeSummaryResourceProvider on several cases.
     * 1. Incorrect cluster name throws exception
     * 2. Upgrade with no tasks.
     * 3. Construct Upgrade with a single COMPLETED task. Resource should not have a failed reason.
     * 4. Append a failed task to the Upgrade. Resource should have a failed reason.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetUpgradeSummary() throws Exception {
        createCluster();
        Cluster cluster = clusters.getCluster(clusterName);
        ResourceProvider upgradeSummaryResourceProvider = createProvider(amc);
        // Case 1: Incorrect cluster name throws exception
        Request requestResource = PropertyHelper.getReadRequest();
        Predicate pBogus = new PredicateBuilder().property(UPGRADE_SUMMARY_CLUSTER_NAME).equals("bogus name").toPredicate();
        try {
            Set<Resource> resources = upgradeSummaryResourceProvider.getResources(requestResource, pBogus);
            Assert.assertTrue("Expected exception to be thrown", false);
        } catch (Exception e) {
        }
        // Case 2: Upgrade with no tasks.
        Long upgradeRequestId = 1L;
        Predicate p1 = new PredicateBuilder().property(UPGRADE_SUMMARY_CLUSTER_NAME).equals(clusterName).toPredicate();
        Predicate p2 = new PredicateBuilder().property(UPGRADE_SUMMARY_REQUEST_ID).equals(upgradeRequestId.toString()).toPredicate();
        Predicate p1And2 = new org.apache.ambari.server.controller.predicate.AndPredicate(p1, p2);
        Set<Resource> resources = upgradeSummaryResourceProvider.getResources(requestResource, p1And2);
        Assert.assertEquals(0, resources.size());
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestId(1L);
        requestEntity.setClusterId(cluster.getClusterId());
        requestEntity.setStatus(PENDING);
        requestEntity.setStages(new ArrayList());
        requestDAO.create(requestEntity);
        UpgradeEntity upgrade = new UpgradeEntity();
        upgrade.setRequestEntity(requestEntity);
        upgrade.setClusterId(cluster.getClusterId());
        upgrade.setId(1L);
        upgrade.setUpgradePackage("some-name");
        upgrade.setUpgradePackStackId(new StackId(((String) (null))));
        upgrade.setUpgradeType(ROLLING);
        upgrade.setDirection(UPGRADE);
        RepositoryVersionEntity repositoryVersion2201 = injector.getInstance(RepositoryVersionDAO.class).findByStackNameAndVersion("HDP", "2.2.0.1-1234");
        upgrade.setRepositoryVersion(repositoryVersion2201);
        upgradeDAO.create(upgrade);
        // Resource used to make assertions.
        Resource r;
        resources = upgradeSummaryResourceProvider.getResources(requestResource, p1And2);
        Assert.assertEquals(1, resources.size());
        r = resources.iterator().next();
        assertNull(r.getPropertyValue(UPGRADE_SUMMARY_FAIL_REASON));
        // Case 3: Construct Upgrade with a single COMPLETED task. Resource should not have a failed reason.
        Long currentStageId = 1L;
        createCommands(cluster, upgradeRequestId, currentStageId);
        resources = upgradeSummaryResourceProvider.getResources(requestResource, p1And2);
        Assert.assertEquals(1, resources.size());
        r = resources.iterator().next();
        assertNull(r.getPropertyValue(UPGRADE_SUMMARY_FAIL_REASON));
        // Case 4: Append a failed task to the Upgrade. Resource should have a failed reason.
        requestEntity = requestDAO.findByPK(upgradeRequestId);
        HostEntity h1 = hostDAO.findByName("h1");
        StageEntity nextStage = new StageEntity();
        nextStage.setRequest(requestEntity);
        nextStage.setClusterId(cluster.getClusterId());
        nextStage.setRequestId(upgradeRequestId);
        nextStage.setStageId((++currentStageId));
        requestEntity.getStages().add(nextStage);
        stageDAO.create(nextStage);
        requestDAO.merge(requestEntity);
        // Create the task and add it to the stage
        HostRoleCommandEntity hrc2 = new HostRoleCommandEntity();
        hrc2.setStage(nextStage);
        // Important that it's on its own stage with a FAILED status.
        hrc2.setStatus(FAILED);
        hrc2.setRole(ZOOKEEPER_SERVER);
        hrc2.setRoleCommand(RESTART);
        hrc2.setCommandDetail("Restart ZOOKEEPER_SERVER");
        hrc2.setHostEntity(h1);
        nextStage.setHostRoleCommands(new ArrayList());
        nextStage.getHostRoleCommands().add(hrc2);
        h1.getHostRoleCommandEntities().add(hrc2);
        hrcDAO.create(hrc2);
        hostDAO.merge(h1);
        hrc2.setRequestId(upgradeRequestId);
        hrc2.setStageId(nextStage.getStageId());
        hrcDAO.merge(hrc2);
        Resource failedTask = new ResourceImpl(Type.Task);
        expect(m_upgradeHelper.getTaskResource(anyString(), anyLong(), anyLong(), anyLong())).andReturn(failedTask).anyTimes();
        replay(m_upgradeHelper);
        resources = upgradeSummaryResourceProvider.getResources(requestResource, p1And2);
        Assert.assertEquals(1, resources.size());
        r = resources.iterator().next();
        Assert.assertEquals("Failed calling Restart ZOOKEEPER_SERVER on host h1", r.getPropertyValue(UPGRADE_SUMMARY_FAIL_REASON));
    }

    /**
     * Mock module that will bind UpgradeHelper to a mock instance.
     */
    private class MockModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(UpgradeHelper.class).toInstance(m_upgradeHelper);
        }
    }
}

