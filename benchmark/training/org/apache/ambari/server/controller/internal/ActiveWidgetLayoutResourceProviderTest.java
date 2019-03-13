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


import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.easymock.EasyMockSupport;
import org.junit.Ignore;
import org.junit.Test;


/**
 * ActiveWidgetLayout tests
 */
// need to fix StackOverflowError
@Ignore
public class ActiveWidgetLayoutResourceProviderTest extends EasyMockSupport {
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
    public void testCreateResources_Administrator() throws Exception {
        createResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "User1");
    }

    @Test(expected = SystemException.class)
    public void testCreateResources_NonAdministrator_Self() throws Exception {
        createResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = SystemException.class)
    public void testCreateResources_NonAdministrator_Other() throws Exception {
        createResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User10");
    }

    @Test
    public void testUpdateResources_Administrator() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "User1");
    }

    @Test
    public void testUpdateResources_NonAdministrator_Self() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test
    public void testUpdateResources_NoUserName_Self() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1", false);
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_NonAdministrator_Other() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User10");
    }

    @Test(expected = SystemException.class)
    public void testDeleteResources_Administrator() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "User1");
    }

    @Test(expected = SystemException.class)
    public void testDeleteResources_NonAdministrator_Self() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = SystemException.class)
    public void testDeleteResources_NonAdministrator_Other() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User10");
    }
}

