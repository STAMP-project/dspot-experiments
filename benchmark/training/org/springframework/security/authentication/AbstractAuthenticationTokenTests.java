/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.authentication;


import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


/**
 * Tests {@link AbstractAuthenticationToken}.
 *
 * @author Ben Alex
 */
public class AbstractAuthenticationTokenTests {
    // ~ Instance fields
    // ================================================================================================
    private List<GrantedAuthority> authorities = null;

    @Test(expected = UnsupportedOperationException.class)
    public void testAuthoritiesAreImmutable() {
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        List<GrantedAuthority> gotAuthorities = ((List<GrantedAuthority>) (getAuthorities()));
        assertThat(gotAuthorities).isNotSameAs(authorities);
        gotAuthorities.set(0, new SimpleGrantedAuthority("ROLE_SUPER_USER"));
    }

    @Test
    public void testGetters() throws Exception {
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        assertThat(token.getPrincipal()).isEqualTo("Test");
        assertThat(token.getCredentials()).isEqualTo("Password");
        assertThat(getName()).isEqualTo("Test");
    }

    @Test
    public void testHashCode() throws Exception {
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token1 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token2 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token3 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl(null, null, AuthorityUtils.NO_AUTHORITIES);
        assertThat(token2.hashCode()).isEqualTo(token1.hashCode());
        assertThat(((token1.hashCode()) != (token3.hashCode()))).isTrue();
        setAuthenticated(true);
        assertThat(((token1.hashCode()) != (token2.hashCode()))).isTrue();
    }

    @Test
    public void testObjectsEquals() throws Exception {
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token1 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token2 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        assertThat(token2).isEqualTo(token1);
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token3 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password_Changed", authorities);
        assertThat((!(token1.equals(token3)))).isTrue();
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token4 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test_Changed", "Password", authorities);
        assertThat((!(token1.equals(token4)))).isTrue();
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token5 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO_CHANGED"));
        assertThat((!(token1.equals(token5)))).isTrue();
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token6 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", AuthorityUtils.createAuthorityList("ROLE_ONE"));
        assertThat((!(token1.equals(token6)))).isTrue();
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token7 = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", null);
        assertThat((!(token1.equals(token7)))).isTrue();
        assertThat((!(token7.equals(token1)))).isTrue();
        assertThat((!(token1.equals(Integer.valueOf(100))))).isTrue();
    }

    @Test
    public void testSetAuthenticated() throws Exception {
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        assertThat((!(isAuthenticated()))).isTrue();
        setAuthenticated(true);
        assertThat(isAuthenticated()).isTrue();
    }

    @Test
    public void testToStringWithAuthorities() {
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", authorities);
        assertThat(((token.toString().lastIndexOf("ROLE_TWO")) != (-1))).isTrue();
    }

    @Test
    public void testToStringWithNullAuthorities() {
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token = new AbstractAuthenticationTokenTests.MockAuthenticationImpl("Test", "Password", null);
        assertThat(((token.toString().lastIndexOf("Not granted any authorities")) != (-1))).isTrue();
    }

    @Test
    public void testGetNameWhenPrincipalIsAuthenticatedPrincipal() {
        String principalName = "test";
        AuthenticatedPrincipal principal = Mockito.mock(AuthenticatedPrincipal.class);
        Mockito.when(principal.getName()).thenReturn(principalName);
        AbstractAuthenticationTokenTests.MockAuthenticationImpl token = new AbstractAuthenticationTokenTests.MockAuthenticationImpl(principal, "Password", authorities);
        assertThat(getName()).isEqualTo(principalName);
        getName();
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockAuthenticationImpl extends AbstractAuthenticationToken {
        private Object credentials;

        private Object principal;

        public MockAuthenticationImpl(Object principal, Object credentials, List<GrantedAuthority> authorities) {
            super(authorities);
            this.principal = principal;
            this.credentials = credentials;
        }

        public Object getCredentials() {
            return this.credentials;
        }

        public Object getPrincipal() {
            return this.principal;
        }
    }
}

