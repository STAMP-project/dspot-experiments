/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.security.authorization;


import com.google.inject.Injector;
import javax.naming.ldap.LdapName;
import org.apache.ambari.server.ldap.domain.AmbariLdapConfiguration;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;


public class AmbariLdapBindAuthenticatorTest extends EasyMockSupport {
    private Injector injector;

    private AmbariLdapConfiguration ldapConfiguration;

    @Test
    public void testAuthenticateWithoutLogin() throws Exception {
        testAuthenticate("username", "username", false);
    }

    @Test
    public void testAuthenticateWithNullLDAPUsername() throws Exception {
        testAuthenticate("username", null, false);
    }

    @Test
    public void testAuthenticateWithLoginAliasDefault() throws Exception {
        testAuthenticate("username", "ldapUsername", false);
    }

    @Test
    public void testAuthenticateWithLoginAliasForceToLower() throws Exception {
        testAuthenticate("username", "ldapUsername", true);
    }

    @Test
    public void testAuthenticateBadPassword() throws Exception {
        String basePathString = "dc=apache,dc=org";
        String ldapUserRelativeDNString = String.format("uid=%s,ou=people,ou=dev", "ldapUsername");
        LdapName ldapUserRelativeDN = new LdapName(ldapUserRelativeDNString);
        String ldapUserDNString = String.format("%s,%s", ldapUserRelativeDNString, basePathString);
        LdapName basePath = LdapUtils.newLdapName(basePathString);
        LdapContextSource ldapCtxSource = createMock(LdapContextSource.class);
        expect(ldapCtxSource.getBaseLdapName()).andReturn(basePath).atLeastOnce();
        expect(ldapCtxSource.getContext(ldapUserDNString, "password")).andThrow(new AuthenticationException(null)).once();
        DirContextOperations searchedUserContext = createMock(DirContextOperations.class);
        expect(searchedUserContext.getDn()).andReturn(ldapUserRelativeDN).atLeastOnce();
        FilterBasedLdapUserSearch userSearch = createMock(FilterBasedLdapUserSearch.class);
        expect(userSearch.searchForUser(anyString())).andReturn(searchedUserContext).once();
        setupDatabaseConfigurationExpectations(false, false);
        replayAll();
        AmbariLdapBindAuthenticator bindAuthenticator = new AmbariLdapBindAuthenticator(ldapCtxSource, ldapConfiguration);
        bindAuthenticator.setUserSearch(userSearch);
        try {
            bindAuthenticator.authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
            Assert.fail("Expected thrown exception: org.springframework.security.authentication.BadCredentialsException");
        } catch (org.springframework e) {
            // expected
        } catch (Throwable t) {
            Assert.fail(("Expected thrown exception: org.springframework.security.authentication.BadCredentialsException\nEncountered thrown exception " + (t.getClass().getName())));
        }
        verifyAll();
    }
}

