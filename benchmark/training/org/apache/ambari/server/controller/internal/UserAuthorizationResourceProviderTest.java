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


import com.google.inject.Injector;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * UserAuthorizationResourceProvider tests.
 */
public class UserAuthorizationResourceProviderTest extends EasyMockSupport {
    @Test
    public void testGetResources_Administrator() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "User1");
    }

    @Test
    public void testGetResources_NonAdministrator_Self() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_NonAdministrator_Other() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User10");
    }

    @Test(expected = SystemException.class)
    public void testCreateResources() throws Exception {
        Injector injector = createInjector();
        replayAll();
        // Set the authenticated user to a non-administrator
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("user1", 2L));
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        UserAuthorizationResourceProvider provider = new UserAuthorizationResourceProvider(managementController);
        provider.createResources(createNiceMock(Request.class));
        verifyAll();
    }

    @Test(expected = SystemException.class)
    public void testUpdateResources() throws Exception {
        Injector injector = createInjector();
        replayAll();
        // Set the authenticated user to a non-administrator
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("user1", 2L));
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        UserAuthorizationResourceProvider provider = new UserAuthorizationResourceProvider(managementController);
        provider.updateResources(createNiceMock(Request.class), null);
        verifyAll();
    }

    @Test(expected = SystemException.class)
    public void testDeleteResources() throws Exception {
        Injector injector = createInjector();
        replayAll();
        // Set the authenticated user to a non-administrator
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("user1", 2L));
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        UserAuthorizationResourceProvider provider = new UserAuthorizationResourceProvider(managementController);
        provider.deleteResources(createNiceMock(Request.class), null);
        verifyAll();
    }
}

