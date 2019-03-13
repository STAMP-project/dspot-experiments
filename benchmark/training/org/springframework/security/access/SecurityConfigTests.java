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
package org.springframework.security.access;


import org.junit.Test;


/**
 * Tests {@link SecurityConfig}.
 *
 * @author Ben Alex
 */
public class SecurityConfigTests {
    // ~ Methods
    // ========================================================================================================
    @Test
    public void testHashCode() {
        SecurityConfig config = new SecurityConfig("TEST");
        assertThat(config.hashCode()).isEqualTo("TEST".hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotConstructWithNullAttribute() {
        new SecurityConfig(null);// SEC-727

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotConstructWithEmptyAttribute() {
        new SecurityConfig("");// SEC-727

    }

    @Test(expected = NoSuchMethodException.class)
    public void testNoArgConstructorDoesntExist() throws Exception {
        SecurityConfig.class.getDeclaredConstructor(((Class[]) (null)));
    }

    @Test
    public void testObjectEquals() throws Exception {
        SecurityConfig security1 = new SecurityConfig("TEST");
        SecurityConfig security2 = new SecurityConfig("TEST");
        assertThat(security2).isEqualTo(security1);
        // SEC-311: Must observe symmetry requirement of Object.equals(Object) contract
        String securityString1 = "TEST";
        assertThat(securityString1).isNotSameAs(security1);
        String securityString2 = "NOT_EQUAL";
        assertThat((!(security1.equals(securityString2)))).isTrue();
        SecurityConfig security3 = new SecurityConfig("NOT_EQUAL");
        assertThat((!(security1.equals(security3)))).isTrue();
        SecurityConfigTests.MockConfigAttribute mock1 = new SecurityConfigTests.MockConfigAttribute("TEST");
        assertThat(security1).isEqualTo(mock1);
        SecurityConfigTests.MockConfigAttribute mock2 = new SecurityConfigTests.MockConfigAttribute("NOT_EQUAL");
        assertThat(security1).isNotEqualTo(mock2);
        Integer int1 = Integer.valueOf(987);
        assertThat(security1).isNotEqualTo(int1);
    }

    @Test
    public void testToString() {
        SecurityConfig config = new SecurityConfig("TEST");
        assertThat(config.toString()).isEqualTo("TEST");
    }

    // ~ Inner Classes
    // ==================================================================================================
    private class MockConfigAttribute implements ConfigAttribute {
        private String attribute;

        public MockConfigAttribute(String configuration) {
            this.attribute = configuration;
        }

        public String getAttribute() {
            return this.attribute;
        }
    }
}

