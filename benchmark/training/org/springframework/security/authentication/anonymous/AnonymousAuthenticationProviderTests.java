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


import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;


/**
 * Tests {@link AnonymousAuthenticationProvider}.
 *
 * @author Ben Alex
 */
public class AnonymousAuthenticationProviderTests {
    // ~ Methods
    // ========================================================================================================
    @Test
    public void testDetectsAnInvalidKey() throws Exception {
        AnonymousAuthenticationProvider aap = new AnonymousAuthenticationProvider("qwerty");
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("WRONG_KEY", "Test", AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO"));
        try {
            aap.authenticate(token);
            fail("Should have thrown BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
    }

    @Test
    public void testDetectsMissingKey() throws Exception {
        try {
            new AnonymousAuthenticationProvider(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGettersSetters() throws Exception {
        AnonymousAuthenticationProvider aap = new AnonymousAuthenticationProvider("qwerty");
        assertThat(aap.getKey()).isEqualTo("qwerty");
    }

    @Test
    public void testIgnoresClassesItDoesNotSupport() throws Exception {
        AnonymousAuthenticationProvider aap = new AnonymousAuthenticationProvider("qwerty");
        TestingAuthenticationToken token = new TestingAuthenticationToken("user", "password", "ROLE_A");
        assertThat(aap.supports(TestingAuthenticationToken.class)).isFalse();
        // Try it anyway
        assertThat(aap.authenticate(token)).isNull();
    }

    @Test
    public void testNormalOperation() throws Exception {
        AnonymousAuthenticationProvider aap = new AnonymousAuthenticationProvider("qwerty");
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("qwerty", "Test", AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO"));
        Authentication result = aap.authenticate(token);
        assertThat(token).isEqualTo(result);
    }

    @Test
    public void testSupports() {
        AnonymousAuthenticationProvider aap = new AnonymousAuthenticationProvider("qwerty");
        assertThat(aap.supports(AnonymousAuthenticationToken.class)).isTrue();
        assertThat(aap.supports(TestingAuthenticationToken.class)).isFalse();
    }
}

