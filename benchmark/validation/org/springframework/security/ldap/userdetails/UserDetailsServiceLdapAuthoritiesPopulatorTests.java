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


import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.UserDetailsServiceLdapAuthoritiesPopulator;


/**
 *
 *
 * @author Luke Taylor
 */
public class UserDetailsServiceLdapAuthoritiesPopulatorTests {
    @Test
    public void delegationToUserDetailsServiceReturnsCorrectRoles() throws Exception {
        UserDetailsService uds = Mockito.mock(UserDetailsService.class);
        UserDetails user = Mockito.mock(UserDetails.class);
        Mockito.when(uds.loadUserByUsername("joe")).thenReturn(user);
        List authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        Mockito.when(user.getAuthorities()).thenReturn(authorities);
        UserDetailsServiceLdapAuthoritiesPopulator populator = new UserDetailsServiceLdapAuthoritiesPopulator(uds);
        Collection<? extends GrantedAuthority> auths = populator.getGrantedAuthorities(new DirContextAdapter(), "joe");
        assertThat(auths).hasSize(1);
        assertThat(AuthorityUtils.authorityListToSet(auths).contains("ROLE_USER")).isTrue();
    }
}

