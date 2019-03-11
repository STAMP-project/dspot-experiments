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


import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.AlertDispatchDAO;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Clusters;
import org.junit.Test;


/**
 * {@link AlertNoticeResourceProvider} tests.
 */
public class AlertNoticeResourceProviderTest {
    private AlertDispatchDAO m_dao = null;

    private Injector m_injector;

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
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesClusterPredicateAsClusterAdministrator() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesClusterPredicateAsServiceAdministrator() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesClusterPredicateAsClusterUser() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResourcesClusterPredicateAsViewUser() throws Exception {
        testGetResourcesClusterPredicate(TestAuthenticationFactory.createViewUser(99L));
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

    /**
     *
     */
    private class MockModule implements Module {
        /**
         *
         */
        @Override
        public void configure(Binder binder) {
            Clusters clusters = createMock(Clusters.class);
            AmbariManagementController amc = createMock(AmbariManagementController.class);
            expect(amc.getClusters()).andReturn(clusters).anyTimes();
            binder.bind(AlertDispatchDAO.class).toInstance(m_dao);
            binder.bind(Clusters.class).toInstance(clusters);
            binder.bind(AmbariManagementController.class).toInstance(amc);
            binder.bind(ActionMetadata.class);
        }
    }
}

