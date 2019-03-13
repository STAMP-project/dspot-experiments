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
package org.apache.ambari.server.api.services;


import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.persistence.PersistenceManagerImpl;
import org.apache.ambari.server.controller.spi.ClusterController;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * PersistenceManagerImpl unit tests.
 */
public class PersistenceManagerImplTest {
    @Test
    public void testPersistenceManagerImplAsClusterAdministrator() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("ClusterAdmin", 2L));
        testCreate();
        testCreate___NoBodyProps();
        testCreate__MultipleResources();
        testUpdate();
        testDelete();
    }

    @Test
    public void testPersistenceManagerImplAsServiceAdministrator() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createServiceAdministrator("ServiceAdmin", 2L));
        testCreate();
        testCreate___NoBodyProps();
        testCreate__MultipleResources();
        testUpdate();
        testDelete();
    }

    @Test
    public void testPersistenceManagerImplAsServiceOperator() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createServiceOperator("ServiceOperator", 2L));
        testCreate();
        testCreate___NoBodyProps();
        testCreate__MultipleResources();
        testUpdate();
        testDelete();
    }

    @Test
    public void testPersistenceManagerImplAsClusterUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterUser("ClusterUser", 2L));
        testCreate();
        testCreate___NoBodyProps();
        testCreate__MultipleResources();
        testUpdate();
        testDelete();
    }

    @Test(expected = AuthorizationException.class)
    public void testPersistenceManagerImplAsViewUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createViewUser("ViewUser", 2L));
        testCreate();
        testCreate___NoBodyProps();
        testCreate__MultipleResources();
        testUpdate();
        testDelete();
    }

    private class TestPersistenceManager extends PersistenceManagerImpl {
        private Request m_request;

        private Set<Map<String, Object>> m_setProperties;

        private TestPersistenceManager(ClusterController controller, Set<Map<String, Object>> setProperties, Request controllerRequest) {
            super(controller);
            m_setProperties = setProperties;
            m_request = controllerRequest;
        }

        @Override
        protected Request createControllerRequest(RequestBody body) {
            Assert.assertEquals(m_setProperties, body.getPropertySets());
            // todo: assert request props
            return m_request;
        }
    }
}

