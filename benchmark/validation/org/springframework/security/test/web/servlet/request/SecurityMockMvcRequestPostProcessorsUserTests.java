/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.security.test.web.servlet.request;


import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.web.support.WebTestUtils;
import org.springframework.security.web.context.SecurityContextRepository;


@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(WebTestUtils.class)
public class SecurityMockMvcRequestPostProcessorsUserTests {
    @Captor
    private ArgumentCaptor<SecurityContext> contextCaptor;

    @Mock
    private SecurityContextRepository repository;

    private MockHttpServletRequest request;

    @Mock
    private GrantedAuthority authority1;

    @Mock
    private GrantedAuthority authority2;

    @Test
    public void userWithDefaults() {
        String username = "userabc";
        SecurityMockMvcRequestPostProcessors.user(username).postProcessRequest(request);
        Mockito.verify(repository).saveContext(contextCaptor.capture(), ArgumentMatchers.eq(request), ArgumentMatchers.any(HttpServletResponse.class));
        SecurityContext context = contextCaptor.getValue();
        assertThat(context.getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(context.getAuthentication().getName()).isEqualTo(username);
        assertThat(context.getAuthentication().getCredentials()).isEqualTo("password");
        assertThat(context.getAuthentication().getAuthorities()).extracting("authority").containsOnly("ROLE_USER");
    }

    @Test
    public void userWithCustom() {
        String username = "customuser";
        SecurityMockMvcRequestPostProcessors.user(username).roles("CUSTOM", "ADMIN").password("newpass").postProcessRequest(request);
        Mockito.verify(repository).saveContext(contextCaptor.capture(), ArgumentMatchers.eq(request), ArgumentMatchers.any(HttpServletResponse.class));
        SecurityContext context = contextCaptor.getValue();
        assertThat(context.getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(context.getAuthentication().getName()).isEqualTo(username);
        assertThat(context.getAuthentication().getCredentials()).isEqualTo("newpass");
        assertThat(context.getAuthentication().getAuthorities()).extracting("authority").containsOnly("ROLE_CUSTOM", "ROLE_ADMIN");
    }

    @Test
    public void userCustomAuthoritiesVarargs() {
        String username = "customuser";
        SecurityMockMvcRequestPostProcessors.user(username).authorities(authority1, authority2).postProcessRequest(request);
        Mockito.verify(repository).saveContext(contextCaptor.capture(), ArgumentMatchers.eq(request), ArgumentMatchers.any(HttpServletResponse.class));
        SecurityContext context = contextCaptor.getValue();
        assertThat(((java.util.List<GrantedAuthority>) (context.getAuthentication().getAuthorities()))).containsOnly(authority1, authority2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void userRolesWithRolePrefixErrors() {
        SecurityMockMvcRequestPostProcessors.user("user").roles("ROLE_INVALID").postProcessRequest(request);
    }

    @Test
    public void userCustomAuthoritiesList() {
        String username = "customuser";
        SecurityMockMvcRequestPostProcessors.user(username).authorities(Arrays.asList(authority1, authority2)).postProcessRequest(request);
        Mockito.verify(repository).saveContext(contextCaptor.capture(), ArgumentMatchers.eq(request), ArgumentMatchers.any(HttpServletResponse.class));
        SecurityContext context = contextCaptor.getValue();
        assertThat(((java.util.List<GrantedAuthority>) (context.getAuthentication().getAuthorities()))).containsOnly(authority1, authority2);
    }
}

