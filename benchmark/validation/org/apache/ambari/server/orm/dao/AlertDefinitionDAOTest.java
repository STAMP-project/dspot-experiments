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


import AlertState.OK;
import MaintenanceState.OFF;
import NotificationState.PENDING;
import Scope.SERVICE;
import SourceType.SCRIPT;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.AlertCurrentEntity;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.orm.entities.AlertGroupEntity;
import org.apache.ambari.server.orm.entities.AlertHistoryEntity;
import org.apache.ambari.server.orm.entities.AlertNoticeEntity;
import org.apache.ambari.server.orm.entities.ClusterEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * Tests {@link AlertDefinitionDAO} for interacting with
 * {@link AlertDefinitionEntity}.
 */
public class AlertDefinitionDAOTest {
    static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    Injector injector;

    Long clusterId;

    AlertDefinitionDAO dao;

    AlertsDAO alertsDao;

    AlertDispatchDAO dispatchDao;

    OrmTestHelper helper;

    /**
     *
     */
    @Test
    public void testFindByName() {
        List<AlertDefinitionEntity> definitions = dao.findAll();
        Assert.assertNotNull(definitions);
        AlertDefinitionEntity definition = definitions.get(2);
        AlertDefinitionEntity retrieved = dao.findByName(definition.getClusterId(), definition.getDefinitionName());
        Assert.assertEquals(definition, retrieved);
    }

    /**
     *
     */
    @Test
    public void testFindAll() {
        List<AlertDefinitionEntity> definitions = dao.findAll();
        Assert.assertNotNull(definitions);
        Assert.assertEquals(15, definitions.size());
    }

    /**
     *
     */
    @Test
    public void testFindAllEnabled() {
        List<AlertDefinitionEntity> definitions = dao.findAll();
        Assert.assertNotNull(definitions);
        Assert.assertEquals(15, definitions.size());
        List<AlertDefinitionEntity> enabledDefinitions = dao.findAllEnabled(clusterId);
        Assert.assertNotNull(enabledDefinitions);
        Assert.assertEquals(definitions.size(), enabledDefinitions.size());
        enabledDefinitions.get(0).setEnabled(false);
        dao.merge(enabledDefinitions.get(0));
        enabledDefinitions = dao.findAllEnabled(clusterId);
        Assert.assertNotNull(enabledDefinitions);
        Assert.assertEquals(((definitions.size()) - 1), enabledDefinitions.size());
    }

    /**
     *
     */
    @Test
    public void testFindById() {
        List<AlertDefinitionEntity> definitions = dao.findAll();
        Assert.assertNotNull(definitions);
        AlertDefinitionEntity definition = definitions.get(2);
        AlertDefinitionEntity retrieved = dao.findById(definition.getDefinitionId());
        Assert.assertEquals(definition, retrieved);
    }

    /**
     *
     */
    @Test
    public void testFindByIds() {
        List<AlertDefinitionEntity> definitions = dao.findAll();
        List<Long> ids = new ArrayList<>();
        ids.add(definitions.get(0).getDefinitionId());
        ids.add(definitions.get(1).getDefinitionId());
        ids.add(99999L);
        definitions = dao.findByIds(ids);
        Assert.assertEquals(2, definitions.size());
    }

    /**
     *
     */
    @Test
    public void testFindByService() {
        List<AlertDefinitionEntity> definitions = dao.findByService(clusterId, "HDFS");
        Assert.assertNotNull(definitions);
        Assert.assertEquals(10, definitions.size());
        definitions = dao.findByService(clusterId, "YARN");
        Assert.assertNotNull(definitions);
        Assert.assertEquals(0, definitions.size());
    }

    /**
     *
     */
    @Test
    public void testFindByServiceComponent() {
        List<AlertDefinitionEntity> definitions = dao.findByServiceComponent(clusterId, "OOZIE", "OOZIE_SERVER");
        Assert.assertNotNull(definitions);
        Assert.assertEquals(2, definitions.size());
    }

    /**
     *
     */
    @Test
    public void testFindAgentScoped() {
        List<AlertDefinitionEntity> definitions = dao.findAgentScoped(clusterId);
        Assert.assertNotNull(definitions);
        Assert.assertEquals(3, definitions.size());
    }

    @Test
    public void testRemove() throws Exception {
        AlertDefinitionEntity definition = helper.createAlertDefinition(clusterId);
        definition = dao.findById(definition.getDefinitionId());
        Assert.assertNotNull(definition);
        dao.remove(definition);
        definition = dao.findById(definition.getDefinitionId());
        Assert.assertNull(definition);
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCascadeDelete() throws Exception {
        AlertDefinitionEntity definition = helper.createAlertDefinition(clusterId);
        AlertGroupEntity group = helper.createAlertGroup(clusterId, null);
        group.addAlertDefinition(definition);
        dispatchDao.merge(group);
        AlertHistoryEntity history = new AlertHistoryEntity();
        history.setServiceName(definition.getServiceName());
        history.setClusterId(clusterId);
        history.setAlertDefinition(definition);
        history.setAlertLabel("Label");
        history.setAlertState(OK);
        history.setAlertText("Alert Text");
        history.setAlertTimestamp(AlertDefinitionDAOTest.calendar.getTimeInMillis());
        alertsDao.create(history);
        AlertCurrentEntity current = new AlertCurrentEntity();
        current.setAlertHistory(history);
        current.setLatestTimestamp(new Date().getTime());
        current.setOriginalTimestamp(((new Date().getTime()) - 10800000));
        current.setMaintenanceState(OFF);
        alertsDao.create(current);
        AlertNoticeEntity notice = new AlertNoticeEntity();
        notice.setAlertHistory(history);
        notice.setAlertTarget(helper.createAlertTarget());
        notice.setNotifyState(PENDING);
        notice.setUuid(UUID.randomUUID().toString());
        dispatchDao.create(notice);
        group = dispatchDao.findGroupById(group.getGroupId());
        Assert.assertNotNull(group);
        Assert.assertNotNull(group.getAlertDefinitions());
        Assert.assertEquals(1, group.getAlertDefinitions().size());
        history = alertsDao.findById(history.getAlertId());
        Assert.assertNotNull(history);
        current = alertsDao.findCurrentById(current.getAlertId());
        Assert.assertNotNull(current);
        Assert.assertNotNull(current.getAlertHistory());
        notice = dispatchDao.findNoticeById(notice.getNotificationId());
        Assert.assertNotNull(notice);
        Assert.assertNotNull(notice.getAlertHistory());
        Assert.assertNotNull(notice.getAlertTarget());
        // delete the definition
        definition = dao.findById(definition.getDefinitionId());
        dao.refresh(definition);
        dao.remove(definition);
        notice = dispatchDao.findNoticeById(notice.getNotificationId());
        Assert.assertNull(notice);
        current = alertsDao.findCurrentById(current.getAlertId());
        Assert.assertNull(current);
        history = alertsDao.findById(history.getAlertId());
        Assert.assertNull(history);
        group = dispatchDao.findGroupById(group.getGroupId());
        Assert.assertNotNull(group);
        Assert.assertNotNull(group.getAlertDefinitions());
        Assert.assertEquals(0, group.getAlertDefinitions().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCascadeDeleteForCluster() throws Exception {
        AlertDefinitionEntity definition = helper.createAlertDefinition(clusterId);
        definition = dao.findById(definition.getDefinitionId());
        dao.refresh(definition);
        ClusterDAO clusterDAO = injector.getInstance(ClusterDAO.class);
        ClusterEntity clusterEntity = clusterDAO.findById(clusterId);
        clusterDAO.refresh(clusterEntity);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = clusters.getClusterById(clusterId);
        cluster.delete();
        Assert.assertNull(clusterDAO.findById(clusterId));
        Assert.assertNull(dao.findById(definition.getDefinitionId()));
        Assert.assertEquals(0, dispatchDao.findAllGroups(clusterId).size());
    }

    @Test
    public void testNestedClusterEntity() throws Exception {
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setDefinitionName("nested-cluster-entity-test");
        definition.setServiceName("HDFS");
        definition.setComponentName(null);
        definition.setClusterId(clusterId);
        definition.setHash(UUID.randomUUID().toString());
        definition.setScheduleInterval(60);
        definition.setScope(SERVICE);
        definition.setSource("{\"type\" : \"SCRIPT\"}");
        definition.setSourceType(SCRIPT);
        dao.create(definition);
        definition = dao.findById(definition.getDefinitionId());
        Assert.assertNotNull(definition.getCluster());
        Assert.assertEquals(clusterId, definition.getCluster().getClusterId());
    }

    @Test
    public void testBatchDeleteOfNoticeEntities() throws Exception {
        AlertDefinitionEntity definition = helper.createAlertDefinition(clusterId);
        AlertGroupEntity group = helper.createAlertGroup(clusterId, null);
        group.addAlertDefinition(definition);
        dispatchDao.merge(group);
        // Add 1000+ notice entities
        for (int i = 0; i < 1500; i++) {
            AlertHistoryEntity history = new AlertHistoryEntity();
            history.setServiceName(definition.getServiceName());
            history.setClusterId(clusterId);
            history.setAlertDefinition(definition);
            history.setAlertLabel("Label");
            history.setAlertState(OK);
            history.setAlertText("Alert Text");
            history.setAlertTimestamp(AlertDefinitionDAOTest.calendar.getTimeInMillis());
            alertsDao.create(history);
            AlertCurrentEntity current = new AlertCurrentEntity();
            current.setAlertHistory(history);
            current.setLatestTimestamp(new Date().getTime());
            current.setOriginalTimestamp(((new Date().getTime()) - 10800000));
            current.setMaintenanceState(OFF);
            alertsDao.create(current);
            AlertNoticeEntity notice = new AlertNoticeEntity();
            notice.setAlertHistory(history);
            notice.setAlertTarget(helper.createAlertTarget());
            notice.setNotifyState(PENDING);
            notice.setUuid(UUID.randomUUID().toString());
            dispatchDao.create(notice);
        }
        group = dispatchDao.findGroupById(group.getGroupId());
        Assert.assertNotNull(group);
        Assert.assertNotNull(group.getAlertDefinitions());
        Assert.assertEquals(1, group.getAlertDefinitions().size());
        List<AlertHistoryEntity> historyEntities = alertsDao.findAll();
        Assert.assertEquals(1500, historyEntities.size());
        List<AlertCurrentEntity> currentEntities = alertsDao.findCurrentByDefinitionId(definition.getDefinitionId());
        Assert.assertNotNull(currentEntities);
        Assert.assertEquals(1500, currentEntities.size());
        List<AlertNoticeEntity> noticeEntities = dispatchDao.findAllNotices();
        assertEquals(1500, noticeEntities.size());
        // delete the definition
        definition = dao.findById(definition.getDefinitionId());
        dao.refresh(definition);
        dao.remove(definition);
        List<AlertNoticeEntity> notices = dispatchDao.findAllNotices();
        Assert.assertTrue(notices.isEmpty());
        currentEntities = alertsDao.findCurrentByDefinitionId(definition.getDefinitionId());
        Assert.assertTrue(((currentEntities == null) || (currentEntities.isEmpty())));
        historyEntities = alertsDao.findAll();
        Assert.assertTrue(((historyEntities == null) || (historyEntities.isEmpty())));
        group = dispatchDao.findGroupById(group.getGroupId());
        Assert.assertNotNull(group);
        Assert.assertNotNull(group.getAlertDefinitions());
        Assert.assertEquals(0, group.getAlertDefinitions().size());
    }
}

