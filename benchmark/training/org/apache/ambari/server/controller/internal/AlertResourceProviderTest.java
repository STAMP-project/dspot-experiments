/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.internal;


import AlertResourceProvider.ALERT_CLUSTER_NAME;
import AlertResourceProvider.ALERT_DEFINITION_NAME;
import AlertResourceProvider.ALERT_ID;
import Configuration.ALERTS_EXECUTION_SCHEDULER_THREADS_CORE_SIZE;
import Configuration.ALERTS_EXECUTION_SCHEDULER_THREADS_MAX_SIZE;
import Configuration.ALERTS_EXECUTION_SCHEDULER_WORKER_QUEUE_SIZE;
import Configuration.DEFAULT_DERBY_SCHEMA;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.controller.AlertCurrentRequest;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.PageRequest;
import org.apache.ambari.server.controller.spi.PageRequest.StartingPoint;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.QueryResponse;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.DBAccessor;
import org.apache.ambari.server.orm.DBAccessorImpl;
import org.apache.ambari.server.orm.dao.AlertsDAO;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Clusters;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Test the AlertResourceProvider class
 */
public class AlertResourceProviderTest {
    private static final Long ALERT_VALUE_ID = 1000L;

    private static final String ALERT_VALUE_LABEL = "My Label";

    private static final Long ALERT_VALUE_TIMESTAMP = 1L;

    private static final String ALERT_VALUE_TEXT = "My Text";

    private static final String ALERT_VALUE_COMPONENT = "component";

    private static final String ALERT_VALUE_HOSTNAME = "host";

    private static final String ALERT_VALUE_SERVICE = "service";

    private AlertsDAO m_dao;

    private Injector m_injector;

    private AmbariManagementController m_amc;

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetClusterAsAdministrator() throws Exception {
        testGetCluster(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetClusterAsClusterAdministrator() throws Exception {
        testGetCluster(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetClusterAsClusterUser() throws Exception {
        testGetCluster(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetClusterAsViewOnlyUser() throws Exception {
        testGetCluster(TestAuthenticationFactory.createViewUser(99L));
    }

    /**
     * Test for service
     */
    @Test
    public void testGetServiceAsAdministrator() throws Exception {
        testGetService(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetServiceAsClusterAdministrator() throws Exception {
        testGetService(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetServiceAsClusterUser() throws Exception {
        testGetService(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetServiceAsViewOnlyUser() throws Exception {
        testGetService(TestAuthenticationFactory.createViewUser(99L));
    }

    /**
     * Test for service
     */
    @Test
    public void testGetHostAsAdministrator() throws Exception {
        testGetHost(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetHostAsClusterAdministrator() throws Exception {
        testGetHost(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetHostAsClusterUser() throws Exception {
        testGetHost(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetHostAsViewOnlyUser() throws Exception {
        testGetHost(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testGetClusterSummaryAsAdministrator() throws Exception {
        testGetClusterSummary(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetClusterSummaryAsClusterAdministrator() throws Exception {
        testGetClusterSummary(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetClusterSummaryAsClusterUser() throws Exception {
        testGetClusterSummary(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetClusterSummaryAsViewOnlyUser() throws Exception {
        testGetClusterSummary(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testGetClusterGroupedSummaryAsAdministrator() throws Exception {
        testGetClusterGroupedSummary(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetClusterGroupedSummaryAsClusterAdministrator() throws Exception {
        testGetClusterGroupedSummary(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetClusterGroupedSummaryAsClusterUser() throws Exception {
        testGetClusterGroupedSummary(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetClusterGroupedSummaryAsViewOnlyUser() throws Exception {
        testGetClusterGroupedSummary(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testGetClusterGroupedSummaryMaintenanceCountsAsAdministrator() throws Exception {
        testGetClusterGroupedSummaryMaintenanceCounts(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetClusterGroupedSummaryMaintenanceCountsAsClusterAdministrator() throws Exception {
        testGetClusterGroupedSummaryMaintenanceCounts(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetClusterGroupedSummaryMaintenanceCountsAsClusterUser() throws Exception {
        testGetClusterGroupedSummaryMaintenanceCounts(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetClusterGroupedSummaryMaintenanceCountsAsViewOnlyUser() throws Exception {
        testGetClusterGroupedSummaryMaintenanceCounts(TestAuthenticationFactory.createViewUser(99L));
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testResponseIsPaginated() throws Exception {
        expect(m_dao.findAll(EasyMock.anyObject(AlertCurrentRequest.class))).andReturn(getClusterMockEntities()).atLeastOnce();
        expect(m_dao.getCount(EasyMock.anyObject(Predicate.class))).andReturn(0).atLeastOnce();
        replay(m_dao);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        Set<String> requestProperties = new HashSet<>();
        requestProperties.add(ALERT_ID);
        requestProperties.add(ALERT_DEFINITION_NAME);
        Request request = PropertyHelper.getReadRequest(requestProperties);
        Predicate predicate = new PredicateBuilder().property(ALERT_CLUSTER_NAME).equals("c1").toPredicate();
        AlertResourceProvider provider = createProvider();
        QueryResponse response = provider.queryForResources(request, predicate);
        // since the request didn't have paging, then this should be false
        Assert.assertFalse(response.isPagedResponse());
        // add a paged request
        PageRequest pageRequest = new PageRequestImpl(StartingPoint.Beginning, 5, 10, predicate, null);
        request = PropertyHelper.getReadRequest(requestProperties, null, null, pageRequest, null);
        response = provider.queryForResources(request, predicate);
        // now the request has paging
        Assert.assertTrue(response.isPagedResponse());
        verify(m_dao);
    }

    /**
     *
     */
    private class MockModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(EntityManager.class).toInstance(EasyMock.createMock(EntityManager.class));
            binder.bind(AlertsDAO.class).toInstance(m_dao);
            binder.bind(AmbariManagementController.class).toInstance(createMock(AmbariManagementController.class));
            binder.bind(DBAccessor.class).to(DBAccessorImpl.class);
            Clusters clusters = EasyMock.createNiceMock(Clusters.class);
            Configuration configuration = EasyMock.createNiceMock(Configuration.class);
            binder.bind(Clusters.class).toInstance(clusters);
            binder.bind(Configuration.class).toInstance(configuration);
            expect(configuration.getDatabaseUrl()).andReturn(Configuration.JDBC_IN_MEMORY_URL).anyTimes();
            expect(configuration.getDatabaseDriver()).andReturn(Configuration.JDBC_IN_MEMORY_DRIVER).anyTimes();
            expect(configuration.getDatabaseUser()).andReturn("sa").anyTimes();
            expect(configuration.getDatabasePassword()).andReturn("").anyTimes();
            expect(configuration.getAlertEventPublisherCorePoolSize()).andReturn(Integer.valueOf(ALERTS_EXECUTION_SCHEDULER_THREADS_CORE_SIZE.getDefaultValue())).anyTimes();
            expect(configuration.getAlertEventPublisherMaxPoolSize()).andReturn(Integer.valueOf(ALERTS_EXECUTION_SCHEDULER_THREADS_MAX_SIZE.getDefaultValue())).anyTimes();
            expect(configuration.getAlertEventPublisherWorkerQueueSize()).andReturn(Integer.valueOf(ALERTS_EXECUTION_SCHEDULER_WORKER_QUEUE_SIZE.getDefaultValue())).anyTimes();
            expect(configuration.getMasterKeyLocation()).andReturn(new File("/test")).anyTimes();
            expect(configuration.getTemporaryKeyStoreRetentionMinutes()).andReturn(2L).anyTimes();
            expect(configuration.isActivelyPurgeTemporaryKeyStore()).andReturn(true).anyTimes();
            expect(configuration.getDatabaseSchema()).andReturn(DEFAULT_DERBY_SCHEMA).anyTimes();
            replay(configuration);
        }
    }
}

