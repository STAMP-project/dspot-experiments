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


import TargetType.EMAIL;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.UUID;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.dao.AlertDispatchDAO;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.junit.Test;


/**
 * {@link AlertGroupResourceProvider} tests.
 */
public class AlertGroupResourceProviderTest {
    private static final Long ALERT_GROUP_ID = Long.valueOf(28);

    private static final String ALERT_GROUP_NAME = "Important Alerts";

    private static final long ALERT_GROUP_CLUSTER_ID = 1L;

    private static final String ALERT_GROUP_CLUSTER_NAME = "c1";

    private static final Long ALERT_TARGET_ID = Long.valueOf(28);

    private static final String ALERT_TARGET_NAME = "The Administrators";

    private static final String ALERT_TARGET_DESC = "Admins and Others";

    private static final String ALERT_TARGET_TYPE = EMAIL.name();

    private static final Long ALERT_DEF_ID = 10L;

    private static final String ALERT_DEF_NAME = "Mock Definition";

    private static final String ALERT_DEF_LABEL = "Mock Label";

    private static final String ALERT_DEF_DESCRIPTION = "Mock Description";

    private static String DEFINITION_UUID = UUID.randomUUID().toString();

    private AlertDispatchDAO m_dao;

    private AlertDefinitionDAO m_definitionDao;

    private Injector m_injector;

    private AmbariManagementController m_amc;

    private Clusters m_clusters;

    private Cluster m_cluster;

    @Test
    public void testGetResourcesNoPredicateAsAdministrator() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesNoPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesNoPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesNoPredicateAsClusterUser() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetResourcesNoPredicateAsViewUser() throws Exception {
        testGetResourcesNoPredicate(TestAuthenticationFactory.createViewUser(99L));
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
    public void testGetResourcesAllPropertiesAsAdministrator() throws Exception {
        testGetResourcesAllProperties(TestAuthenticationFactory.createAdministrator(), true);
    }

    @Test
    public void testGetResourcesAllPropertiesAsClusterAdministrator() throws Exception {
        testGetResourcesAllProperties(TestAuthenticationFactory.createClusterAdministrator(), true);
    }

    @Test
    public void testGetResourcesAllPropertiesAsServiceAdministrator() throws Exception {
        testGetResourcesAllProperties(TestAuthenticationFactory.createServiceAdministrator(), true);
    }

    @Test
    public void testGetResourcesAllPropertiesAsClusterUser() throws Exception {
        testGetResourcesAllProperties(TestAuthenticationFactory.createClusterUser(), true);
    }

    @Test
    public void testGetResourcesAllPropertiesAsViewUser() throws Exception {
        testGetResourcesAllProperties(TestAuthenticationFactory.createViewUser(99L), false);
    }

    @Test
    public void testGetSingleResourceAsAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createAdministrator(), true);
    }

    @Test
    public void testGetSingleResourceAsClusterAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createClusterAdministrator(), true);
    }

    @Test
    public void testGetSingleResourceAsServiceAdministrator() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createServiceAdministrator(), true);
    }

    @Test
    public void testGetSingleResourceAsClusterUser() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createClusterUser(), true);
    }

    @Test(expected = AuthorizationException.class)
    public void testGetSingleResourceAsViewUser() throws Exception {
        testGetSingleResource(TestAuthenticationFactory.createViewUser(99L), false);
    }

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
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

    @Test
    public void testUpdateDefaultGroupAsAdministrator() throws Exception {
        testUpdateDefaultGroup(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateDefaultGroupAsClusterAdministrator() throws Exception {
        testUpdateDefaultGroup(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateDefaultGroupAsServiceAdministrator() throws Exception {
        testUpdateDefaultGroup(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateDefaultGroupAsClusterUser() throws Exception {
        testUpdateDefaultGroup(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateDefaultGroupAsViewUser() throws Exception {
        testUpdateDefaultGroup(TestAuthenticationFactory.createViewUser(99L));
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
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

    @Test
    public void testDeleteDefaultGroupAsAdministrator() throws Exception {
        testDeleteDefaultGroup(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteDefaultGroupAsClusterAdministrator() throws Exception {
        testDeleteDefaultGroup(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteDefaultGroupAsServiceAdministrator() throws Exception {
        testDeleteDefaultGroup(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteDefaultGroupAsClusterUser() throws Exception {
        testDeleteDefaultGroup(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteDefaultGroupAsViewUser() throws Exception {
        testDeleteDefaultGroup(TestAuthenticationFactory.createViewUser(99L));
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
            binder.bind(AlertDispatchDAO.class).toInstance(m_dao);
            binder.bind(AlertDefinitionDAO.class).toInstance(m_definitionDao);
            binder.bind(Clusters.class).toInstance(m_clusters);
            binder.bind(Cluster.class).toInstance(m_cluster);
            binder.bind(ActionMetadata.class);
        }
    }
}

