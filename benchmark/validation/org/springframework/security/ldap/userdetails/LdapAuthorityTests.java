/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.ldap.userdetails;


import SpringSecurityLdapTemplate.DN_KEY;
import org.junit.Test;


/**
 *
 *
 * @author Filip Hanik
 */
public class LdapAuthorityTests {
    public static final String DN = "cn=filip,ou=Users,dc=test,dc=com";

    LdapAuthority authority;

    @Test
    public void testGetDn() throws Exception {
        assertThat(authority.getDn()).isEqualTo(LdapAuthorityTests.DN);
        assertThat(authority.getAttributeValues(DN_KEY)).isNotNull();
        assertThat(authority.getAttributeValues(DN_KEY)).hasSize(1);
        assertThat(authority.getFirstAttributeValue(DN_KEY)).isEqualTo(LdapAuthorityTests.DN);
    }

    @Test
    public void testGetAttributes() throws Exception {
        assertThat(authority.getAttributes()).isNotNull();
        assertThat(authority.getAttributeValues("mail")).isNotNull();
        assertThat(authority.getAttributeValues("mail")).hasSize(2);
        assertThat(authority.getFirstAttributeValue("mail")).isEqualTo("filip@ldap.test.org");
        assertThat(authority.getAttributeValues("mail").get(0)).isEqualTo("filip@ldap.test.org");
        assertThat(authority.getAttributeValues("mail").get(1)).isEqualTo("filip@ldap.test2.org");
    }

    @Test
    public void testGetAuthority() throws Exception {
        assertThat(authority.getAuthority()).isNotNull();
        assertThat(authority.getAuthority()).isEqualTo("testRole");
    }
}

