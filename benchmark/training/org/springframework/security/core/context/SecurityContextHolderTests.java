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
package org.springframework.security.core.context;


import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


/**
 * Tests {@link SecurityContextHolder}.
 *
 * @author Ben Alex
 */
public class SecurityContextHolderTests {
    @Test
    public void testContextHolderGetterSetterClearer() {
        SecurityContext sc = new SecurityContextImpl();
        sc.setAuthentication(new UsernamePasswordAuthenticationToken("Foobar", "pass"));
        SecurityContextHolder.setContext(sc);
        assertThat(SecurityContextHolder.getContext()).isEqualTo(sc);
        SecurityContextHolder.clearContext();
        assertThat(SecurityContextHolder.getContext()).isNotSameAs(sc);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testNeverReturnsNull() {
        assertThat(SecurityContextHolder.getContext()).isNotNull();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testRejectsNulls() {
        try {
            SecurityContextHolder.setContext(null);
            fail("Should have rejected null");
        } catch (IllegalArgumentException expected) {
        }
    }
}

