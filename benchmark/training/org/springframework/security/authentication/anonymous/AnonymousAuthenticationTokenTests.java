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
package org.springframework.security.authentication.anonymous;


import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;


/**
 * Tests {@link AnonymousAuthenticationToken}.
 *
 * @author Ben Alex
 */
public class AnonymousAuthenticationTokenTests {
    private static final List<GrantedAuthority> ROLES_12 = AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO");

    // ~ Methods
    // ========================================================================================================
    @Test
    public void testConstructorRejectsNulls() {
        try {
            new AnonymousAuthenticationToken(null, "Test", AnonymousAuthenticationTokenTests.ROLES_12);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            new AnonymousAuthenticationToken("key", null, AnonymousAuthenticationTokenTests.ROLES_12);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            new AnonymousAuthenticationToken("key", "Test", ((List<GrantedAuthority>) (null)));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            new AnonymousAuthenticationToken("key", "Test", AuthorityUtils.NO_AUTHORITIES);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testEqualsWhenEqual() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        AnonymousAuthenticationToken token2 = new AnonymousAuthenticationToken("key", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        assertThat(token2).isEqualTo(token1);
    }

    @Test
    public void testGetters() {
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("key", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        assertThat(token.getKeyHash()).isEqualTo("key".hashCode());
        assertThat(token.getPrincipal()).isEqualTo("Test");
        assertThat(token.getCredentials()).isEqualTo("");
        assertThat(AuthorityUtils.authorityListToSet(token.getAuthorities())).contains("ROLE_ONE", "ROLE_TWO");
        assertThat(token.isAuthenticated()).isTrue();
    }

    @Test
    public void testNoArgConstructorDoesntExist() {
        Class<?> clazz = AnonymousAuthenticationToken.class;
        try {
            clazz.getDeclaredConstructor(((Class[]) (null)));
            fail("Should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
        }
    }

    @Test
    public void testNotEqualsDueToAbstractParentEqualsCheck() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        AnonymousAuthenticationToken token2 = new AnonymousAuthenticationToken("key", "DIFFERENT_PRINCIPAL", AnonymousAuthenticationTokenTests.ROLES_12);
        assertThat(token1.equals(token2)).isFalse();
    }

    @Test
    public void testNotEqualsDueToDifferentAuthenticationClass() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        UsernamePasswordAuthenticationToken token2 = new UsernamePasswordAuthenticationToken("Test", "Password", AnonymousAuthenticationTokenTests.ROLES_12);
        assertThat(token1.equals(token2)).isFalse();
    }

    @Test
    public void testNotEqualsDueToKey() {
        AnonymousAuthenticationToken token1 = new AnonymousAuthenticationToken("key", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        AnonymousAuthenticationToken token2 = new AnonymousAuthenticationToken("DIFFERENT_KEY", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        assertThat(token1.equals(token2)).isFalse();
    }

    @Test
    public void testSetAuthenticatedIgnored() {
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("key", "Test", AnonymousAuthenticationTokenTests.ROLES_12);
        assertThat(token.isAuthenticated()).isTrue();
        token.setAuthenticated(false);
        assertThat((!(token.isAuthenticated()))).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenNullAuthoritiesThenThrowIllegalArgumentException() throws Exception {
        new AnonymousAuthenticationToken("key", "principal", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenEmptyAuthoritiesThenThrowIllegalArgumentException() throws Exception {
        new AnonymousAuthenticationToken("key", "principal", Collections.<GrantedAuthority>emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenPrincipalIsEmptyStringThenThrowIllegalArgumentException() throws Exception {
        new AnonymousAuthenticationToken("key", "", AnonymousAuthenticationTokenTests.ROLES_12);
    }
}

