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
package org.apache.ambari.server.api.services.stackadvisor;


import StackAdvisorRequestType.CONFIGURATIONS;
import StackAdvisorRequestType.HOST_GROUPS;
import StackAdvisorRequestType.KERBEROS_CONFIGURATIONS;
import StackAdvisorRequestType.LDAP_CONFIGURATIONS;
import StackAdvisorRequestType.SSO_CONFIGURATIONS;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorRequest.StackAdvisorRequestType;
import org.junit.Assert;
import org.junit.Test;


/**
 * StackAdvisorRequestTypeTest unit tests.
 */
public class StackAdvisorRequestTypeTest {
    @Test
    public void testFromString_returnsHostGroupType() throws StackAdvisorException {
        testFromString("host_groups", HOST_GROUPS);
    }

    @Test
    public void testFromString_returnsConfigurationsType() throws StackAdvisorException {
        testFromString("configurations", CONFIGURATIONS);
    }

    @Test
    public void testFromString_returnsSingleSignOnConfigurationsType() throws StackAdvisorException {
        testFromString("sso-configurations", SSO_CONFIGURATIONS);
    }

    @Test
    public void testFromString_returnsLDAPConfigurationsType() throws StackAdvisorException {
        testFromString("ldap-configurations", LDAP_CONFIGURATIONS);
    }

    @Test
    public void testFromString_returnsKerberosConfigurationsType() throws StackAdvisorException {
        testFromString("kerberos-configurations", KERBEROS_CONFIGURATIONS);
    }

    @Test(expected = StackAdvisorException.class)
    public void testFromString_throwsException() throws StackAdvisorException {
        String text = "unknown_type";
        StackAdvisorRequestType.fromString(text);
        Assert.fail("Expected StackAdvisorException");
    }
}

