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
package org.springframework.security.access.expression;


import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;


/**
 *
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class SecurityExpressionRootTests {
    static final Authentication JOE = new TestingAuthenticationToken("joe", "pass", "ROLE_A", "ROLE_B");

    SecurityExpressionRoot root;

    @Test
    public void denyAllIsFalsePermitAllTrue() throws Exception {
        assertThat(root.denyAll()).isFalse();
        assertThat(root.denyAll).isFalse();
        assertThat(root.permitAll()).isTrue();
        assertThat(root.permitAll).isTrue();
    }

    @Test
    public void rememberMeIsCorrectlyDetected() throws Exception {
        AuthenticationTrustResolver atr = Mockito.mock(AuthenticationTrustResolver.class);
        root.setTrustResolver(atr);
        Mockito.when(atr.isRememberMe(SecurityExpressionRootTests.JOE)).thenReturn(true);
        assertThat(root.isRememberMe()).isTrue();
        assertThat(root.isFullyAuthenticated()).isFalse();
    }

    @Test
    public void roleHierarchySupportIsCorrectlyUsedInEvaluatingRoles() throws Exception {
        root.setRoleHierarchy(new RoleHierarchy() {
            public Collection<GrantedAuthority> getReachableGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
                return AuthorityUtils.createAuthorityList("ROLE_C");
            }
        });
        assertThat(root.hasRole("C")).isTrue();
        assertThat(root.hasAuthority("ROLE_C")).isTrue();
        assertThat(root.hasRole("A")).isFalse();
        assertThat(root.hasRole("B")).isFalse();
        assertThat(root.hasAnyRole("C", "A", "B")).isTrue();
        assertThat(root.hasAnyAuthority("ROLE_C", "ROLE_A", "ROLE_B")).isTrue();
        assertThat(root.hasAnyRole("A", "B")).isFalse();
    }

    @Test
    public void hasRoleAddsDefaultPrefix() throws Exception {
        assertThat(root.hasRole("A")).isTrue();
        assertThat(root.hasRole("NO")).isFalse();
    }

    @Test
    public void hasRoleEmptyPrefixDoesNotAddsDefaultPrefix() throws Exception {
        root.setDefaultRolePrefix("");
        assertThat(root.hasRole("A")).isFalse();
        assertThat(root.hasRole("ROLE_A")).isTrue();
    }

    @Test
    public void hasRoleNullPrefixDoesNotAddsDefaultPrefix() throws Exception {
        root.setDefaultRolePrefix(null);
        assertThat(root.hasRole("A")).isFalse();
        assertThat(root.hasRole("ROLE_A")).isTrue();
    }

    @Test
    public void hasRoleDoesNotAddDefaultPrefixForAlreadyPrefixedRoles() throws Exception {
        SecurityExpressionRoot root = new SecurityExpressionRoot(SecurityExpressionRootTests.JOE) {};
        assertThat(root.hasRole("ROLE_A")).isTrue();
        assertThat(root.hasRole("ROLE_NO")).isFalse();
    }

    @Test
    public void hasAnyRoleAddsDefaultPrefix() throws Exception {
        assertThat(root.hasAnyRole("NO", "A")).isTrue();
        assertThat(root.hasAnyRole("NO", "NOT")).isFalse();
    }

    @Test
    public void hasAnyRoleDoesNotAddDefaultPrefixForAlreadyPrefixedRoles() throws Exception {
        assertThat(root.hasAnyRole("ROLE_NO", "ROLE_A")).isTrue();
        assertThat(root.hasAnyRole("ROLE_NO", "ROLE_NOT")).isFalse();
    }

    @Test
    public void hasAnyRoleEmptyPrefixDoesNotAddsDefaultPrefix() throws Exception {
        root.setDefaultRolePrefix("");
        assertThat(root.hasRole("A")).isFalse();
        assertThat(root.hasRole("ROLE_A")).isTrue();
    }

    @Test
    public void hasAnyRoleNullPrefixDoesNotAddsDefaultPrefix() throws Exception {
        root.setDefaultRolePrefix(null);
        assertThat(root.hasAnyRole("A")).isFalse();
        assertThat(root.hasAnyRole("ROLE_A")).isTrue();
    }

    @Test
    public void hasAuthorityDoesNotAddDefaultPrefix() throws Exception {
        assertThat(root.hasAuthority("A")).isFalse();
        assertThat(root.hasAnyAuthority("NO", "A")).isFalse();
        assertThat(root.hasAnyAuthority("ROLE_A", "NOT")).isTrue();
    }
}

