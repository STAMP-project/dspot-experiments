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
package org.apache.ambari.server.security.authorization;


import Warning.NONFINAL_FIELDS;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.apache.ambari.server.audit.AuditLoggerModule;
import org.apache.ambari.server.ldap.LdapModule;
import org.junit.Assert;
import org.junit.Test;


public class LdapServerPropertiesTest {
    private final Injector injector;

    private static final String INCORRECT_URL_LIST = "Incorrect LDAP URL list created";

    private static final String INCORRECT_USER_SEARCH_FILTER = "Incorrect search filter";

    protected LdapServerProperties ldapServerProperties;

    public LdapServerPropertiesTest() {
        injector = Guice.createInjector(new AuditLoggerModule(), new AuthorizationTestModule(), new LdapModule());
        injector.injectMembers(this);
    }

    @Test
    public void testGetLdapUrls() throws Exception {
        List<String> urls = ldapServerProperties.getLdapUrls();
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_URL_LIST, 1, urls.size());
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_URL_LIST, "ldap://1.2.3.4:389", urls.get(0));
        ldapServerProperties.setSecondaryUrl("4.3.2.1:1234");
        urls = ldapServerProperties.getLdapUrls();
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_URL_LIST, 2, urls.size());
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_URL_LIST, "ldap://4.3.2.1:1234", urls.get(1));
        ldapServerProperties.setUseSsl(true);
        urls = ldapServerProperties.getLdapUrls();
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_URL_LIST, "ldaps://1.2.3.4:389", urls.get(0));
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_URL_LIST, "ldaps://4.3.2.1:1234", urls.get(1));
    }

    @Test
    public void testGetUserSearchFilter() throws Exception {
        ldapServerProperties.setUserSearchFilter("(&({usernameAttribute}={0})(objectClass={userObjectClass}))");
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_USER_SEARCH_FILTER, "(&(uid={0})(objectClass=dummyObjectClass))", ldapServerProperties.getUserSearchFilter(false));
        ldapServerProperties.setUsernameAttribute("anotherName");
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_USER_SEARCH_FILTER, "(&(anotherName={0})(objectClass=dummyObjectClass))", ldapServerProperties.getUserSearchFilter(false));
    }

    @Test
    public void testGetAlternatUserSearchFilterForUserPrincipalName() throws Exception {
        ldapServerProperties.setAlternateUserSearchFilter("(&({usernameAttribute}={0})(objectClass={userObjectClass}))");
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_USER_SEARCH_FILTER, "(&(uid={0})(objectClass=dummyObjectClass))", ldapServerProperties.getUserSearchFilter(true));
        ldapServerProperties.setUsernameAttribute("anotherName");
        Assert.assertEquals(LdapServerPropertiesTest.INCORRECT_USER_SEARCH_FILTER, "(&(anotherName={0})(objectClass=dummyObjectClass))", ldapServerProperties.getUserSearchFilter(true));
    }

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier<LdapServerProperties> verifier = EqualsVerifier.forClass(LdapServerProperties.class);
        verifier.suppress(NONFINAL_FIELDS);
        verifier.verify();
    }

    @Test
    public void testResolveUserSearchFilterPlaceHolders() throws Exception {
        String ldapUserSearchFilter = "{usernameAttribute}={0}  {userObjectClass}={1}";
        String filter = ldapServerProperties.resolveUserSearchFilterPlaceHolders(ldapUserSearchFilter);
        Assert.assertEquals("uid={0}  dummyObjectClass={1}", filter);
    }
}

