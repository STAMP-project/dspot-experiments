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
package org.apache.ambari.server.controller.logging;


import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.junit.Test;


/**
 * This test case verifies the basic behavior of the
 * LoggingSearchPropertyProvider.
 *
 * Specifically, it verifies that this PropertyProvider
 * implementation uses the output from LogSearch queries
 * to attach the correct logging-related output to the
 * HostComponent resources in Ambari.
 */
public class LoggingSearchPropertyProviderTest {
    @Test
    public void testBasicCallAsAdministrator() throws Exception {
        testBasicCall(TestAuthenticationFactory.createAdministrator(), true);
    }

    @Test
    public void testBasicCallAsClusterAdministrator() throws Exception {
        testBasicCall(TestAuthenticationFactory.createClusterAdministrator(), true);
    }

    @Test
    public void testBasicCallAsClusterOperator() throws Exception {
        testBasicCall(TestAuthenticationFactory.createClusterOperator(), true);
    }

    @Test
    public void testBasicCallAsServiceAdministrator() throws Exception {
        testBasicCall(TestAuthenticationFactory.createServiceAdministrator(), true);
    }

    @Test
    public void testBasicCallAsServiceOperator() throws Exception {
        testBasicCall(TestAuthenticationFactory.createServiceOperator(), false);
    }

    @Test
    public void testBasicCallAsClusterUser() throws Exception {
        testBasicCall(TestAuthenticationFactory.createClusterUser(), false);
    }

    @Test
    public void testBasicCallWithNullTailLogURIReturnedAsAdministrator() throws Exception {
        testBasicCallWithNullTailLogURIReturned(TestAuthenticationFactory.createAdministrator(), true);
    }

    @Test
    public void testBasicCallWithNullTailLogURIReturnedAsClusterAdministrator() throws Exception {
        testBasicCallWithNullTailLogURIReturned(TestAuthenticationFactory.createClusterAdministrator(), true);
    }

    @Test
    public void testBasicCallWithNullTailLogURIReturnedAsClusterOperator() throws Exception {
        testBasicCallWithNullTailLogURIReturned(TestAuthenticationFactory.createClusterOperator(), true);
    }

    @Test
    public void testBasicCallWithNullTailLogURIReturnedAsServiceAdministrator() throws Exception {
        testBasicCallWithNullTailLogURIReturned(TestAuthenticationFactory.createServiceAdministrator(), true);
    }

    @Test
    public void testBasicCallWithNullTailLogURIReturnedAsServiceOperator() throws Exception {
        testBasicCallWithNullTailLogURIReturned(TestAuthenticationFactory.createServiceOperator(), false);
    }

    @Test
    public void testBasicCallWithNullTailLogURIReturnedAsClusterUser() throws Exception {
        testBasicCallWithNullTailLogURIReturned(TestAuthenticationFactory.createClusterUser(), false);
    }

    @Test
    public void testCheckWhenLogSearchNotAvailableAsAdministrator() throws Exception {
        testCheckWhenLogSearchNotAvailable(TestAuthenticationFactory.createAdministrator(), true);
    }

    @Test
    public void testCheckWhenLogSearchNotAvailableAsClusterAdministrator() throws Exception {
        testCheckWhenLogSearchNotAvailable(TestAuthenticationFactory.createClusterAdministrator(), true);
    }

    @Test
    public void testCheckWhenLogSearchNotAvailableAsClusterOperator() throws Exception {
        testCheckWhenLogSearchNotAvailable(TestAuthenticationFactory.createClusterOperator(), true);
    }

    @Test
    public void testCheckWhenLogSearchNotAvailableAsServiceAdministrator() throws Exception {
        testCheckWhenLogSearchNotAvailable(TestAuthenticationFactory.createServiceAdministrator(), true);
    }

    @Test
    public void testCheckWhenLogSearchNotAvailableAsServiceOperator() throws Exception {
        testCheckWhenLogSearchNotAvailable(TestAuthenticationFactory.createServiceOperator(), false);
    }

    @Test
    public void testCheckWhenLogSearchNotAvailableAsClusterUser() throws Exception {
        testCheckWhenLogSearchNotAvailable(TestAuthenticationFactory.createClusterUser(), false);
    }
}

