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


import Configuration.ALERTS_EXECUTION_SCHEDULER_THREADS_CORE_SIZE;
import Configuration.ALERTS_EXECUTION_SCHEDULER_THREADS_MAX_SIZE;
import Configuration.ALERTS_EXECUTION_SCHEDULER_WORKER_QUEUE_SIZE;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.orm.entities.AlertCurrentEntity;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.orm.entities.AlertHistoryEntity;
import org.apache.ambari.server.testutils.PartialNiceMockBinder;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cached alerts are merged correct with the set from JPA.
 */
public class AlertsDAOCachedTest {
    private static final String HOST = "c6401.ambari.apache.org";

    private Injector m_injector;

    private enum CachedAlertTestArea {

        FIND_ALL() {
            @Override
            List<AlertCurrentEntity> execute(AlertsDAO alertsDAO) throws Exception {
                return alertsDAO.findCurrent();
            }

            @Override
            String getNamedQuery() {
                return "AlertCurrentEntity.findAll";
            }
        },
        FIND_BY_DEFINITION_ID() {
            @Override
            List<AlertCurrentEntity> execute(AlertsDAO alertsDAO) throws Exception {
                return alertsDAO.findCurrentByDefinitionId(0L);
            }

            @Override
            String getNamedQuery() {
                return "AlertCurrentEntity.findByDefinitionId";
            }
        },
        FIND_BY_CLUSTER_ID() {
            @Override
            List<AlertCurrentEntity> execute(AlertsDAO alertsDAO) throws Exception {
                return alertsDAO.findCurrentByCluster(0L);
            }

            @Override
            String getNamedQuery() {
                return "AlertCurrentEntity.findByCluster";
            }
        },
        FIND_BY_SERVICE() {
            @Override
            List<AlertCurrentEntity> execute(AlertsDAO alertsDAO) throws Exception {
                return alertsDAO.findCurrentByService(0L, AlertsDAOCachedTest.HOST);
            }

            @Override
            String getNamedQuery() {
                return "AlertCurrentEntity.findByService";
            }
        };
        abstract List<AlertCurrentEntity> execute(AlertsDAO alertsDAO) throws Exception;

        abstract String getNamedQuery();
    }

    /**
     * Tests that finding all alerts supplements with the cache.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFindAll() throws Exception {
        testFindUsesCache(AlertsDAOCachedTest.CachedAlertTestArea.FIND_ALL);
    }

    /**
     * Tests that finding alerts by cluster ID supplements with the cache.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFindByClusterId() throws Exception {
        testFindUsesCache(AlertsDAOCachedTest.CachedAlertTestArea.FIND_BY_CLUSTER_ID);
    }

    /**
     * Tests that finding alerts by definition ID supplements with the cache.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFindByDefinitionId() throws Exception {
        testFindUsesCache(AlertsDAOCachedTest.CachedAlertTestArea.FIND_BY_DEFINITION_ID);
    }

    /**
     * Tests that finding alerts by service supplements with the cache.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFindByService() throws Exception {
        testFindUsesCache(AlertsDAOCachedTest.CachedAlertTestArea.FIND_BY_SERVICE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMergeIntoCacheOnly() throws Exception {
        EntityManager entityManager = m_injector.getInstance(EntityManager.class);
        DaoUtils daoUtils = m_injector.getInstance(DaoUtils.class);
        AlertHistoryEntity history = EasyMock.createNiceMock(AlertHistoryEntity.class);
        AlertDefinitionEntity definition = EasyMock.createNiceMock(AlertDefinitionEntity.class);
        mock(definition, history);
        AlertCurrentEntity jpaCurrent = new AlertCurrentEntity();
        jpaCurrent.setAlertHistory(history);
        jpaCurrent.setOriginalTimestamp(1L);
        jpaCurrent.setLatestTimestamp(2L);
        AlertCurrentEntity memoryCurrent = new AlertCurrentEntity();
        memoryCurrent.setAlertHistory(history);
        memoryCurrent.setOriginalTimestamp(1L);
        memoryCurrent.setLatestTimestamp(3L);
        // mock the EM; notice we do not mock merge since this should not call merge
        TypedQuery<AlertCurrentEntity> typedQuery = EasyMock.createNiceMock(TypedQuery.class);
        EasyMock.expect(entityManager.createNamedQuery(AlertsDAOCachedTest.CachedAlertTestArea.FIND_ALL.getNamedQuery(), AlertCurrentEntity.class)).andReturn(typedQuery).atLeastOnce();
        // create the data to return from mocked JPA
        List<AlertCurrentEntity> jpaCurrentAlerts = Lists.newArrayList(jpaCurrent);
        EasyMock.expect(daoUtils.selectList(typedQuery)).andReturn(jpaCurrentAlerts).atLeastOnce();
        EasyMock.replay(entityManager, daoUtils, typedQuery);
        // invoke merge on our in-memory current entity
        AlertsDAO alertsDAO = m_injector.getInstance(AlertsDAO.class);
        alertsDAO.merge(memoryCurrent, true);
        List<AlertCurrentEntity> testCurrentAlerts = alertsDAO.findCurrent();
        // verify that the stale JPA data is augemented with the cached data
        Assert.assertEquals(1, testCurrentAlerts.size());
        Assert.assertEquals(Long.valueOf(3), testCurrentAlerts.get(0).getLatestTimestamp());
        EasyMock.verify(definition, history, entityManager, daoUtils);
    }

    /**
     *
     */
    private class MockModule implements Module {
        /**
         * {@inheritDoc }
         */
        @Override
        public void configure(Binder binder) {
            // required for since the configuration is being mocked
            Configuration configuration = EasyMock.createNiceMock(Configuration.class);
            EasyMock.expect(configuration.getAlertEventPublisherCorePoolSize()).andReturn(Integer.valueOf(ALERTS_EXECUTION_SCHEDULER_THREADS_CORE_SIZE.getDefaultValue())).anyTimes();
            EasyMock.expect(configuration.getAlertEventPublisherMaxPoolSize()).andReturn(Integer.valueOf(ALERTS_EXECUTION_SCHEDULER_THREADS_MAX_SIZE.getDefaultValue())).anyTimes();
            EasyMock.expect(configuration.getAlertEventPublisherWorkerQueueSize()).andReturn(Integer.valueOf(ALERTS_EXECUTION_SCHEDULER_WORKER_QUEUE_SIZE.getDefaultValue())).anyTimes();
            EasyMock.expect(configuration.isAlertCacheEnabled()).andReturn(Boolean.TRUE).anyTimes();
            EasyMock.expect(configuration.getAlertCacheSize()).andReturn(100).anyTimes();
            EasyMock.replay(configuration);
            binder.bind(Configuration.class).toInstance(configuration);
            PartialNiceMockBinder.newBuilder().addConfigsBindings().addAlertDefinitionBinding().addLdapBindings().build().configure(binder);
        }
    }
}

