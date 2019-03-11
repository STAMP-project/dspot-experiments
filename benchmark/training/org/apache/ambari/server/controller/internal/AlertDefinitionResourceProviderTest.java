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


import AlertDefinitionResourceProvider.ALERT_DEF_CLUSTER_NAME;
import AlertDefinitionResourceProvider.ALERT_DEF_INTERVAL;
import AlertDefinitionResourceProvider.ALERT_DEF_NAME;
import AlertDefinitionResourceProvider.ALERT_DEF_SERVICE_NAME;
import AlertDefinitionResourceProvider.ALERT_DEF_SOURCE_TYPE;
import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.alert.AlertDefinitionFactory;
import org.apache.ambari.server.state.alert.AlertDefinitionHash;
import org.apache.ambari.server.state.alert.MetricSource;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * AlertDefinition tests
 */
public class AlertDefinitionResourceProviderTest {
    private AlertDefinitionDAO dao = null;

    private AlertDefinitionHash definitionHash = null;

    private AlertDefinitionFactory m_factory = new AlertDefinitionFactory();

    private Injector m_injector;

    private static String DEFINITION_UUID = UUID.randomUUID().toString();

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetResourcesNoPredicate() throws Exception {
        AlertDefinitionResourceProvider provider = createProvider(null);
        Request request = PropertyHelper.getReadRequest("AlertDefinition/cluster_name", "AlertDefinition/id");
        Set<Resource> results = provider.getResources(request, null);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testGetResourcesClusterPredicateAsAdministrator() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createAdministrator(), true);
    }

    @Test
    public void testGetResourcesClusterPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createClusterAdministrator(), true);
    }

    @Test
    public void testGetResourcesClusterPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createServiceAdministrator(), true);
    }

    @Test
    public void testGetResourcesClusterPredicateAsClusterUser() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createClusterUser(), true);
    }

    @Test
    public void testGetResourcesClusterPredicateAsViewUser() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createViewUser(99L), false);
    }

    @Test
    public void testGetSingleResourceAsAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetSingleResourceAsClusterAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetSingleResourceAsServiceAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetSingleResourceAsClusterUser() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetSingleResourceAsViewUser() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testGetResourcesAssertSourceTypeAsAdministrator() throws Exception {
        testGetResourcesAssertSourceType(TestAuthenticationFactory.createAdministrator(), true);
    }

    @Test
    public void testGetResourcesAssertSourceTypeAsClusterAdministrator() throws Exception {
        testGetResourcesAssertSourceType(TestAuthenticationFactory.createClusterAdministrator(), true);
    }

    @Test
    public void testGetResourcesAssertSourceTypeAsServiceAdministrator() throws Exception {
        testGetResourcesAssertSourceType(TestAuthenticationFactory.createServiceAdministrator(), true);
    }

    @Test
    public void testGetResourcesAssertSourceTypeAsClusterUser() throws Exception {
        testGetResourcesAssertSourceType(TestAuthenticationFactory.createClusterUser(), true);
    }

    @Test
    public void testGetResourcesAssertSourceTypeAsViewUser() throws Exception {
        testGetResourcesAssertSourceType(TestAuthenticationFactory.createViewUser(99L), false);
    }

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsServiceAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsClusterUser() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsViewUser() throws Exception {
        testCreateResources(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testUpdateResourcesAsAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsClusterUser() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsViewUser() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createViewUser(99L));
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUpdateResourcesWithNumbersAsStrings() throws Exception {
        AmbariManagementController amc = createMock(AmbariManagementController.class);
        Clusters clusters = createMock(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        expect(amc.getClusters()).andReturn(clusters).atLeastOnce();
        expect(clusters.getCluster(((String) (anyObject())))).andReturn(cluster).atLeastOnce();
        expect(cluster.getClusterId()).andReturn(Long.valueOf(1)).atLeastOnce();
        Capture<AlertDefinitionEntity> entityCapture = EasyMock.newCapture();
        dao.create(capture(entityCapture));
        expectLastCall();
        // updateing a single definition should invalidate hosts of the definition
        expect(definitionHash.invalidateHosts(EasyMock.anyObject(AlertDefinitionEntity.class))).andReturn(new HashSet()).atLeastOnce();
        replay(amc, clusters, cluster, dao, definitionHash);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        MetricSource source = ((MetricSource) (getMockSource()));
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(ALERT_DEF_CLUSTER_NAME, "c1");
        requestProps.put(ALERT_DEF_INTERVAL, "1");
        requestProps.put(ALERT_DEF_NAME, "my_def");
        requestProps.put(ALERT_DEF_SERVICE_NAME, "HDFS");
        requestProps.put(ALERT_DEF_SOURCE_TYPE, "METRIC");
        // Ambari converts all request bodies into Map<String,String>, even they are
        // numbers - this will ensure that we can convert a string to a number
        // properly
        requestProps.put("AlertDefinition/source/reporting/critical/text", source.getReporting().getCritical().getText());
        requestProps.put("AlertDefinition/source/reporting/critical/value", "1234.5");
        Request request = PropertyHelper.getCreateRequest(Collections.singleton(requestProps), null);
        AlertDefinitionResourceProvider provider = createProvider(amc);
        provider.createResources(request);
        Assert.assertTrue(entityCapture.hasCaptured());
        AlertDefinitionEntity entity = entityCapture.getValue();
        Assert.assertNotNull(entity);
        String sourceJson = entity.getSource();
        Gson gson = new Gson();
        source = gson.fromJson(sourceJson, MetricSource.class);
        // ensure it's actually a double and NOT a string
        Assert.assertEquals(new Double(1234.5), source.getReporting().getCritical().getValue());
        verify(amc, clusters, cluster, dao);
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsServiceAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsClusterUser() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsViewUser() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createViewUser(99L));
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
            binder.bind(AlertDefinitionDAO.class).toInstance(dao);
            binder.bind(AlertDefinitionHash.class).toInstance(definitionHash);
            binder.bind(Clusters.class).toInstance(EasyMock.createNiceMock(Clusters.class));
            binder.bind(Cluster.class).toInstance(EasyMock.createNiceMock(Cluster.class));
            binder.bind(ActionMetadata.class);
        }
    }
}

