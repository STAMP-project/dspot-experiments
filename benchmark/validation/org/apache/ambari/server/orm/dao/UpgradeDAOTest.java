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
package org.apache.ambari.server.orm.dao;


import Direction.DOWNGRADE;
import Direction.UPGRADE;
import HostRoleStatus.PENDING;
import RepositoryType.MAINT;
import RepositoryType.PATCH;
import UpgradeType.ROLLING;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.RequestEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.orm.entities.UpgradeGroupEntity;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link UpgradeDAO} for interacting with {@link UpgradeEntity}.
 */
public class UpgradeDAOTest {
    private Injector injector;

    private Long clusterId;

    private UpgradeDAO dao;

    private RequestDAO requestDAO;

    private OrmTestHelper helper;

    RepositoryVersionEntity repositoryVersion2200;

    RepositoryVersionEntity repositoryVersion2500;

    RepositoryVersionEntity repositoryVersion2511;

    @Test
    public void testFindForCluster() throws Exception {
        List<UpgradeEntity> items = dao.findUpgrades(clusterId.longValue());
        Assert.assertEquals(1, items.size());
    }

    @Test
    public void testFindUpgrade() throws Exception {
        List<UpgradeEntity> items = dao.findUpgrades(clusterId.longValue());
        Assert.assertTrue(((items.size()) > 0));
        UpgradeEntity entity = dao.findUpgrade(items.get(0).getId().longValue());
        Assert.assertNotNull(entity);
        Assert.assertEquals(1, entity.getUpgradeGroups().size());
        UpgradeGroupEntity group = dao.findUpgradeGroup(entity.getUpgradeGroups().get(0).getId().longValue());
        Assert.assertNotNull(group);
        Assert.assertNotSame(entity.getUpgradeGroups().get(0), group);
        Assert.assertEquals("group_name", group.getName());
        Assert.assertEquals("group title", group.getTitle());
    }

    /**
     * Create upgrades and downgrades and verify only latest upgrade is given
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFindLastUpgradeForCluster() throws Exception {
        // create upgrade entities
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestId(1L);
        requestEntity.setClusterId(clusterId.longValue());
        requestEntity.setStatus(PENDING);
        requestEntity.setStages(new ArrayList());
        requestDAO.create(requestEntity);
        UpgradeEntity entity1 = new UpgradeEntity();
        entity1.setId(11L);
        entity1.setClusterId(clusterId.longValue());
        entity1.setDirection(UPGRADE);
        entity1.setRequestEntity(requestEntity);
        entity1.setRepositoryVersion(repositoryVersion2500);
        entity1.setUpgradeType(ROLLING);
        entity1.setUpgradePackage("test-upgrade");
        entity1.setUpgradePackStackId(new StackId(((String) (null))));
        entity1.setDowngradeAllowed(true);
        dao.create(entity1);
        UpgradeEntity entity2 = new UpgradeEntity();
        entity2.setId(22L);
        entity2.setClusterId(clusterId.longValue());
        entity2.setDirection(DOWNGRADE);
        entity2.setRequestEntity(requestEntity);
        entity2.setRepositoryVersion(repositoryVersion2200);
        entity2.setUpgradeType(ROLLING);
        entity2.setUpgradePackage("test-upgrade");
        entity2.setUpgradePackStackId(new StackId(((String) (null))));
        entity2.setDowngradeAllowed(true);
        dao.create(entity2);
        UpgradeEntity entity3 = new UpgradeEntity();
        entity3.setId(33L);
        entity3.setClusterId(clusterId.longValue());
        entity3.setDirection(UPGRADE);
        entity3.setRequestEntity(requestEntity);
        entity3.setRepositoryVersion(repositoryVersion2511);
        entity3.setUpgradeType(ROLLING);
        entity3.setUpgradePackage("test-upgrade");
        entity3.setUpgradePackStackId(new StackId(((String) (null))));
        entity3.setDowngradeAllowed(true);
        dao.create(entity3);
        UpgradeEntity lastUpgradeForCluster = dao.findLastUpgradeForCluster(clusterId.longValue(), UPGRADE);
        Assert.assertNotNull(lastUpgradeForCluster);
        Assert.assertEquals(33L, ((long) (lastUpgradeForCluster.getId())));
    }

    /**
     * Tests that certain columns in an {@link UpgradeEntity} are updatable.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUpdatableColumns() throws Exception {
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestId(1L);
        requestEntity.setClusterId(clusterId.longValue());
        requestEntity.setStatus(PENDING);
        requestEntity.setStages(new ArrayList());
        requestDAO.create(requestEntity);
        UpgradeEntity upgradeEntity = new UpgradeEntity();
        upgradeEntity.setId(11L);
        upgradeEntity.setClusterId(clusterId.longValue());
        upgradeEntity.setDirection(UPGRADE);
        upgradeEntity.setRequestEntity(requestEntity);
        upgradeEntity.setRepositoryVersion(repositoryVersion2500);
        upgradeEntity.setUpgradeType(ROLLING);
        upgradeEntity.setUpgradePackage("test-upgrade");
        upgradeEntity.setUpgradePackStackId(new StackId(((String) (null))));
        dao.create(upgradeEntity);
        UpgradeEntity lastUpgradeForCluster = dao.findLastUpgradeForCluster(1, UPGRADE);
        Assert.assertFalse(lastUpgradeForCluster.isComponentFailureAutoSkipped());
        Assert.assertFalse(lastUpgradeForCluster.isServiceCheckFailureAutoSkipped());
        lastUpgradeForCluster.setAutoSkipComponentFailures(true);
        lastUpgradeForCluster.setAutoSkipServiceCheckFailures(true);
        dao.merge(lastUpgradeForCluster);
        lastUpgradeForCluster = dao.findLastUpgradeForCluster(1, UPGRADE);
        Assert.assertTrue(lastUpgradeForCluster.isComponentFailureAutoSkipped());
        Assert.assertTrue(lastUpgradeForCluster.isServiceCheckFailureAutoSkipped());
    }

    /**
     * Tests the logic that finds the one-and-only revertable upgrade.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFindRevertableUpgrade() throws Exception {
        // create upgrade entities
        UpgradeEntity revertable = dao.findRevertable(1L);
        UpgradeEntity revertableViaJPQL = dao.findRevertableUsingJPQL(1L);
        Assert.assertEquals(null, revertable);
        Assert.assertEquals(null, revertableViaJPQL);
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestId(1L);
        requestEntity.setClusterId(clusterId.longValue());
        requestEntity.setStatus(PENDING);
        requestEntity.setStages(new ArrayList<org.apache.ambari.server.orm.entities.StageEntity>());
        requestDAO.create(requestEntity);
        UpgradeEntity entity1 = new UpgradeEntity();
        entity1.setId(11L);
        entity1.setClusterId(clusterId.longValue());
        entity1.setDirection(UPGRADE);
        entity1.setRequestEntity(requestEntity);
        entity1.setRepositoryVersion(repositoryVersion2500);
        entity1.setUpgradeType(ROLLING);
        entity1.setUpgradePackage("test-upgrade");
        entity1.setUpgradePackStackId(new StackId(((String) (null))));
        entity1.setDowngradeAllowed(true);
        entity1.setOrchestration(PATCH);
        entity1.setRevertAllowed(true);
        dao.create(entity1);
        revertable = dao.findRevertable(1L);
        revertableViaJPQL = dao.findRevertableUsingJPQL(1L);
        Assert.assertEquals(revertable.getId(), entity1.getId());
        Assert.assertEquals(revertableViaJPQL.getId(), entity1.getId());
        UpgradeEntity entity2 = new UpgradeEntity();
        entity2.setId(22L);
        entity2.setClusterId(clusterId.longValue());
        entity2.setDirection(UPGRADE);
        entity2.setRequestEntity(requestEntity);
        entity2.setRepositoryVersion(repositoryVersion2511);
        entity2.setUpgradeType(ROLLING);
        entity2.setUpgradePackage("test-upgrade");
        entity2.setUpgradePackStackId(new StackId(((String) (null))));
        entity2.setDowngradeAllowed(true);
        entity2.setOrchestration(MAINT);
        entity2.setRevertAllowed(true);
        dao.create(entity2);
        revertable = dao.findRevertable(1L);
        revertableViaJPQL = dao.findRevertableUsingJPQL(1L);
        Assert.assertEquals(revertable.getId(), entity2.getId());
        Assert.assertEquals(revertableViaJPQL.getId(), entity2.getId());
        // now make it look like upgrade ID 22 was reverted
        entity2.setRevertAllowed(false);
        entity2 = dao.merge(entity2);
        // create a downgrade for ID 22
        UpgradeEntity entity3 = new UpgradeEntity();
        entity3.setId(33L);
        entity3.setClusterId(clusterId.longValue());
        entity3.setDirection(DOWNGRADE);
        entity3.setRequestEntity(requestEntity);
        entity3.setRepositoryVersion(repositoryVersion2511);
        entity3.setUpgradeType(ROLLING);
        entity3.setUpgradePackage("test-upgrade");
        entity3.setUpgradePackStackId(new StackId(((String) (null))));
        entity3.setOrchestration(MAINT);
        entity3.setDowngradeAllowed(false);
        dao.create(entity3);
        revertable = dao.findRevertable(1L);
        revertableViaJPQL = dao.findRevertableUsingJPQL(1L);
        Assert.assertEquals(revertable.getId(), entity1.getId());
        Assert.assertEquals(revertableViaJPQL.getId(), entity1.getId());
        // now make it look like upgrade ID 11 was reverted
        entity1.setRevertAllowed(false);
        entity1 = dao.merge(entity1);
        // create a downgrade for ID 11
        UpgradeEntity entity4 = new UpgradeEntity();
        entity4.setId(44L);
        entity4.setClusterId(clusterId.longValue());
        entity4.setDirection(DOWNGRADE);
        entity4.setRequestEntity(requestEntity);
        entity4.setRepositoryVersion(repositoryVersion2500);
        entity4.setUpgradeType(ROLLING);
        entity4.setUpgradePackage("test-upgrade");
        entity4.setUpgradePackStackId(new StackId(((String) (null))));
        entity4.setOrchestration(MAINT);
        entity4.setDowngradeAllowed(false);
        dao.create(entity4);
        revertable = dao.findRevertable(1L);
        revertableViaJPQL = dao.findRevertableUsingJPQL(1L);
        Assert.assertEquals(null, revertable);
        Assert.assertEquals(null, revertableViaJPQL);
    }
}

