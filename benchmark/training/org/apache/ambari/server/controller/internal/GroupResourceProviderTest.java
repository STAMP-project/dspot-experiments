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


import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.junit.Test;


/**
 * GroupResourceProvider tests.
 */
public class GroupResourceProviderTest {
    @Test
    public void testCreateResources_Administrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResources_ClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator("User1", 2L));
    }

    @Test
    public void testGetResources_Administrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_ClusterAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator("User1", 2L));
    }

    @Test
    public void testUpdateResources_Adminstrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_ClusterAdminstrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator("User1", 2L));
    }

    @Test
    public void testDeleteResources_Administrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResources_ClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator("User1", 2L));
    }
}

