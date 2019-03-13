/**
 * Copyright 20013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.provider.token;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.RequestTokenFactory;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;


/**
 *
 *
 * @author Ismael Gomes
 */
public class DefaultTokenServicesAuthoritiesChangeTests {
    private DefaultTokenServices services;

    private InMemoryTokenStore tokenStore = new InMemoryTokenStore();

    @Rule
    public ExpectedException expected = ExpectedException.none();

    // This test will fail
    @Test
    public void testChangeAuthoritiesAuthenticationTokenFail() throws Exception {
        DefaultTokenServicesAuthoritiesChangeTests.TestChangeAuthentication testAuthentication = new DefaultTokenServicesAuthoritiesChangeTests.TestChangeAuthentication("test2", false, new SimpleGrantedAuthority("USER"));
        OAuth2Authentication oauth2Authentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false, Collections.singleton("read")), testAuthentication);
        OAuth2AccessToken createAccessToken = getTokenServices().createAccessToken(oauth2Authentication);
        // First time. The Authentication has 2 roles;
        Assert.assertEquals(getAuthorities(), getAuthorities());
        // Now I change the authorities from testAuthentication
        testAuthentication = new DefaultTokenServicesAuthoritiesChangeTests.TestChangeAuthentication("test2", false, new SimpleGrantedAuthority("NONE"));
        // I recreate the request
        oauth2Authentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false, Collections.singleton("read")), testAuthentication);
        // I create the authentication again
        createAccessToken = getTokenServices().createAccessToken(oauth2Authentication);
        Assert.assertEquals(getAuthorities(), getAuthorities());
    }

    protected static class TestChangeAuthentication extends AbstractAuthenticationToken {
        private static final long serialVersionUID = 1L;

        private String principal;

        public TestChangeAuthentication(String name, boolean authenticated, GrantedAuthority... authorities) {
            super(Arrays.asList(authorities));
            setAuthenticated(authenticated);
            this.principal = name;
        }

        public Object getCredentials() {
            return null;
        }

        public Object getPrincipal() {
            return this.principal;
        }
    }
}

