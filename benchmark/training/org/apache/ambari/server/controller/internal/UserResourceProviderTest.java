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


import UserResourceProvider.USER_ACTIVE_PROPERTY_ID;
import UserResourceProvider.USER_ADMIN_PROPERTY_ID;
import UserResourceProvider.USER_DISPLAY_NAME_PROPERTY_ID;
import UserResourceProvider.USER_LOCAL_USERNAME_PROPERTY_ID;
import UserResourceProvider.USER_PASSWORD_PROPERTY_ID;
import UserResourceProvider.USER_USERNAME_PROPERTY_ID;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.easymock.EasyMockSupport;
import org.junit.Test;


/**
 * UserResourceProvider tests.
 */
public class UserResourceProviderTest extends EasyMockSupport {
    private static final long CREATE_TIME = Calendar.getInstance().getTime().getTime();

    @Test
    public void testCreateResources_Administrator() throws Exception {
        Map<String, Object> resource = new HashMap<>();
        resource.put(USER_USERNAME_PROPERTY_ID, "User100");
        resource.put(USER_LOCAL_USERNAME_PROPERTY_ID, "user100");
        resource.put(USER_DISPLAY_NAME_PROPERTY_ID, "User 100");
        createResourcesTest(TestAuthenticationFactory.createAdministrator(), Collections.singleton(resource));
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResources_NonAdministrator() throws Exception {
        Map<String, Object> resource = new HashMap<>();
        resource.put(USER_USERNAME_PROPERTY_ID, "User100");
        resource.put(USER_LOCAL_USERNAME_PROPERTY_ID, "user100");
        resource.put(USER_DISPLAY_NAME_PROPERTY_ID, "User 100");
        createResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), Collections.singleton(resource));
    }

    @Test
    public void testCreateResources_Multiple() throws Exception {
        Map<String, Object> resource1 = new HashMap<>();
        resource1.put(USER_USERNAME_PROPERTY_ID, "User100");
        Map<String, Object> resource2 = new HashMap<>();
        resource2.put(USER_USERNAME_PROPERTY_ID, "User200");
        HashSet<Map<String, Object>> resourceProperties = new HashSet<>();
        resourceProperties.add(resource1);
        resourceProperties.add(resource2);
        createResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), resourceProperties);
    }

    /**
     * Test setting a user's local password when creating the account. This is for backward compatibility
     * to maintain the REST API V1 contract.
     */
    @Test
    public void testCreateResources_SetPassword() throws Exception {
        Map<String, Object> resource = new HashMap<>();
        resource.put(USER_USERNAME_PROPERTY_ID, "User100");
        resource.put(USER_PASSWORD_PROPERTY_ID, "password100");
        createResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), Collections.singleton(resource));
    }

    /**
     * Test give a user Ambari administrative rights by assigning the user to the AMBARI.ADMINISTRATOR role
     * when creating the account. This is for backward compatibility to maintain the REST API V1 contract.
     */
    @Test
    public void testCreateResources_SetAdmin() throws Exception {
        Map<String, Object> resource = new HashMap<>();
        resource.put(USER_USERNAME_PROPERTY_ID, "User100");
        resource.put(USER_ADMIN_PROPERTY_ID, true);
        createResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), Collections.singleton(resource));
    }

    @Test
    public void testCreateResources_SetInactive() throws Exception {
        Map<String, Object> resource = new HashMap<>();
        resource.put(USER_USERNAME_PROPERTY_ID, "User100");
        resource.put(USER_ACTIVE_PROPERTY_ID, false);
        createResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), Collections.singleton(resource));
    }

    @Test
    public void testGetResources_Administrator() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), null);
    }

    @Test
    public void testGetResources_NonAdministrator() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), null);
    }

    @Test
    public void testGetResource_Administrator_Self() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testGetResource_Administrator_Other() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "User1");
    }

    @Test
    public void testGetResource_NonAdministrator_Self() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResource_NonAdministrator_Other() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }

    @Test
    public void testUpdateResources_UpdateAdmin_Administrator_Self() throws Exception {
        testUpdateResources_UpdateAdmin(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testUpdateResources_UpdateAdmin_Administrator_Other() throws Exception {
        testUpdateResources_UpdateAdmin(TestAuthenticationFactory.createAdministrator("admin"), "User100");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdateAdmin_NonAdministrator_Self() throws Exception {
        testUpdateResources_UpdateAdmin(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdateAdmin_NonAdministrator_Other() throws Exception {
        testUpdateResources_UpdateAdmin(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }

    @Test
    public void testUpdateResources_UpdateActive_Administrator_Self() throws Exception {
        testUpdateResources_UpdateActive(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testUpdateResources_UpdateActive_Administrator_Other() throws Exception {
        testUpdateResources_UpdateActive(TestAuthenticationFactory.createAdministrator("admin"), "User100");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdateActive_NonAdministrator_Self() throws Exception {
        testUpdateResources_UpdateActive(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdateActive_NonAdministrator_Other() throws Exception {
        testUpdateResources_UpdateActive(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }

    @Test
    public void testUpdateResources_UpdateDisplayName_Administrator_Self() throws Exception {
        testUpdateResources_UpdateDisplayName(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testUpdateResources_UpdateDisplayName_Administrator_Other() throws Exception {
        testUpdateResources_UpdateDisplayName(TestAuthenticationFactory.createAdministrator("admin"), "User100");
    }

    @Test
    public void testUpdateResources_UpdateDisplayName_NonAdministrator_Self() throws Exception {
        testUpdateResources_UpdateDisplayName(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdateDisplayName_NonAdministrator_Other() throws Exception {
        testUpdateResources_UpdateDisplayName(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }

    @Test
    public void testUpdateResources_UpdateLocalUserName_Administrator_Self() throws Exception {
        testUpdateResources_UpdateLocalUserName(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testUpdateResources_UpdateLocalUserName_Administrator_Other() throws Exception {
        testUpdateResources_UpdateLocalUserName(TestAuthenticationFactory.createAdministrator("admin"), "User100");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdateLocalUserName_NonAdministrator_Self() throws Exception {
        testUpdateResources_UpdateLocalUserName(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdateLocalUserName_NonAdministrator_Other() throws Exception {
        testUpdateResources_UpdateLocalUserName(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }

    @Test
    public void testUpdateResources_UpdatePassword_Administrator_Self() throws Exception {
        testUpdateResources_UpdatePassword(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testUpdateResources_UpdatePassword_Administrator_Other() throws Exception {
        testUpdateResources_UpdatePassword(TestAuthenticationFactory.createAdministrator("admin"), "User100");
    }

    @Test
    public void testUpdateResources_UpdatePassword_NonAdministrator_Self() throws Exception {
        testUpdateResources_UpdatePassword(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_UpdatePassword_NonAdministrator_Other() throws Exception {
        testUpdateResources_UpdatePassword(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }

    @Test
    public void testUpdateResources_CreatePassword_Administrator_Self() throws Exception {
        testUpdateResources_CreatePassword(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testUpdateResources_CreatePassword_Administrator_Other() throws Exception {
        testUpdateResources_CreatePassword(TestAuthenticationFactory.createAdministrator("admin"), "User100");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_CreatePassword_NonAdministrator_Self() throws Exception {
        testUpdateResources_CreatePassword(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_CreatePassword_NonAdministrator_Other() throws Exception {
        testUpdateResources_CreatePassword(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }

    @Test
    public void testDeleteResource_Administrator_Self() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testDeleteResource_Administrator_Other() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "User100");
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResource_NonAdministrator_Self() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResource_NonAdministrator_Other() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User100");
    }
}

