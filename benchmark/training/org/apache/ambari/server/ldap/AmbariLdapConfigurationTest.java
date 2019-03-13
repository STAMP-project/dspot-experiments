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
package org.apache.ambari.server.ldap;


import AmbariServerConfigurationKey.ALTERNATE_USER_SEARCH_ENABLED;
import AmbariServerConfigurationKey.ALTERNATE_USER_SEARCH_FILTER;
import AmbariServerConfigurationKey.ANONYMOUS_BIND;
import AmbariServerConfigurationKey.BIND_DN;
import AmbariServerConfigurationKey.BIND_PASSWORD;
import AmbariServerConfigurationKey.GROUP_BASE;
import AmbariServerConfigurationKey.GROUP_MAPPING_RULES;
import AmbariServerConfigurationKey.GROUP_MEMBER_ATTRIBUTE;
import AmbariServerConfigurationKey.GROUP_NAME_ATTRIBUTE;
import AmbariServerConfigurationKey.GROUP_OBJECT_CLASS;
import AmbariServerConfigurationKey.GROUP_SEARCH_FILTER;
import AmbariServerConfigurationKey.SECONDARY_SERVER_HOST;
import AmbariServerConfigurationKey.SECONDARY_SERVER_PORT;
import AmbariServerConfigurationKey.SERVER_HOST;
import AmbariServerConfigurationKey.SERVER_PORT;
import AmbariServerConfigurationKey.USER_BASE;
import AmbariServerConfigurationKey.USER_NAME_ATTRIBUTE;
import AmbariServerConfigurationKey.USER_OBJECT_CLASS;
import AmbariServerConfigurationKey.USER_SEARCH_BASE;
import AmbariServerConfigurationKey.USER_SEARCH_FILTER;
import AmbariServerConfigurationKey.USE_SSL;
import org.apache.ambari.server.ldap.domain.AmbariLdapConfiguration;
import org.apache.ambari.server.security.authorization.LdapServerProperties;
import org.junit.Assert;
import org.junit.Test;


public class AmbariLdapConfigurationTest {
    private AmbariLdapConfiguration configuration;

    @Test
    public void testLdapUserSearchFilterDefault() throws Exception {
        Assert.assertEquals("(&(uid={0})(objectClass=person))", configuration.getLdapServerProperties().getUserSearchFilter(false));
    }

    @Test
    public void testLdapUserSearchFilter() throws Exception {
        configuration.setValueFor(USER_NAME_ATTRIBUTE, "test_uid");
        configuration.setValueFor(USER_SEARCH_FILTER, "{usernameAttribute}={0}");
        Assert.assertEquals("test_uid={0}", configuration.getLdapServerProperties().getUserSearchFilter(false));
    }

    @Test
    public void testAlternateLdapUserSearchFilterDefault() throws Exception {
        Assert.assertEquals("(&(userPrincipalName={0})(objectClass=person))", configuration.getLdapServerProperties().getUserSearchFilter(true));
    }

    @Test
    public void testAlternatLdapUserSearchFilter() throws Exception {
        configuration.setValueFor(USER_NAME_ATTRIBUTE, "test_uid");
        configuration.setValueFor(ALTERNATE_USER_SEARCH_FILTER, "{usernameAttribute}={5}");
        Assert.assertEquals("test_uid={5}", configuration.getLdapServerProperties().getUserSearchFilter(true));
    }

    @Test
    public void testAlternateUserSearchEnabledIsSetToFalseByDefault() throws Exception {
        Assert.assertFalse(configuration.isLdapAlternateUserSearchEnabled());
    }

    @Test
    public void testAlternateUserSearchEnabledTrue() throws Exception {
        configuration.setValueFor(ALTERNATE_USER_SEARCH_ENABLED, "true");
        Assert.assertTrue(configuration.isLdapAlternateUserSearchEnabled());
    }

    @Test
    public void testAlternateUserSearchEnabledFalse() throws Exception {
        configuration.setValueFor(ALTERNATE_USER_SEARCH_ENABLED, "false");
        Assert.assertFalse(configuration.isLdapAlternateUserSearchEnabled());
    }

    @Test
    public void testGetLdapServerProperties() throws Exception {
        final String managerPw = "ambariTest";
        configuration.setValueFor(SERVER_HOST, "host");
        configuration.setValueFor(SERVER_PORT, "1");
        configuration.setValueFor(SECONDARY_SERVER_HOST, "secHost");
        configuration.setValueFor(SECONDARY_SERVER_PORT, "2");
        configuration.setValueFor(USE_SSL, "true");
        configuration.setValueFor(ANONYMOUS_BIND, "true");
        configuration.setValueFor(BIND_DN, "5");
        configuration.setValueFor(BIND_PASSWORD, managerPw);
        configuration.setValueFor(USER_SEARCH_BASE, "7");
        configuration.setValueFor(USER_NAME_ATTRIBUTE, "8");
        configuration.setValueFor(USER_BASE, "9");
        configuration.setValueFor(USER_OBJECT_CLASS, "10");
        configuration.setValueFor(GROUP_BASE, "11");
        configuration.setValueFor(GROUP_OBJECT_CLASS, "12");
        configuration.setValueFor(GROUP_MEMBER_ATTRIBUTE, "13");
        configuration.setValueFor(GROUP_NAME_ATTRIBUTE, "14");
        configuration.setValueFor(GROUP_MAPPING_RULES, "15");
        configuration.setValueFor(GROUP_SEARCH_FILTER, "16");
        final LdapServerProperties ldapProperties = configuration.getLdapServerProperties();
        Assert.assertEquals("host:1", ldapProperties.getPrimaryUrl());
        Assert.assertEquals("secHost:2", ldapProperties.getSecondaryUrl());
        Assert.assertTrue(ldapProperties.isUseSsl());
        Assert.assertTrue(ldapProperties.isAnonymousBind());
        Assert.assertEquals("5", ldapProperties.getManagerDn());
        Assert.assertEquals(managerPw, ldapProperties.getManagerPassword());
        Assert.assertEquals("7", ldapProperties.getBaseDN());
        Assert.assertEquals("8", ldapProperties.getUsernameAttribute());
        Assert.assertEquals("9", ldapProperties.getUserBase());
        Assert.assertEquals("10", ldapProperties.getUserObjectClass());
        Assert.assertEquals("11", ldapProperties.getGroupBase());
        Assert.assertEquals("12", ldapProperties.getGroupObjectClass());
        Assert.assertEquals("13", ldapProperties.getGroupMembershipAttr());
        Assert.assertEquals("14", ldapProperties.getGroupNamingAttr());
        Assert.assertEquals("15", ldapProperties.getAdminGroupMappingRules());
        Assert.assertEquals("16", ldapProperties.getGroupSearchFilter());
    }
}

