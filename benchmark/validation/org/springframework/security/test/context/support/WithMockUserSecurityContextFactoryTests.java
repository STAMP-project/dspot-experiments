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
package org.springframework.security.test.context.support;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WithMockUserSecurityContextFactoryTests {
    @Mock
    private WithMockUser withUser;

    private WithMockUserSecurityContextFactory factory;

    @Test(expected = IllegalArgumentException.class)
    public void usernameNull() {
        factory.createSecurityContext(withUser);
    }

    @Test
    public void valueDefaultsUsername() {
        Mockito.when(withUser.value()).thenReturn("valueUser");
        Mockito.when(withUser.password()).thenReturn("password");
        Mockito.when(withUser.roles()).thenReturn(new String[]{ "USER" });
        Mockito.when(withUser.authorities()).thenReturn(new String[]{  });
        assertThat(factory.createSecurityContext(withUser).getAuthentication().getName()).isEqualTo(withUser.value());
    }

    @Test
    public void usernamePrioritizedOverValue() {
        Mockito.when(withUser.username()).thenReturn("customUser");
        Mockito.when(withUser.password()).thenReturn("password");
        Mockito.when(withUser.roles()).thenReturn(new String[]{ "USER" });
        Mockito.when(withUser.authorities()).thenReturn(new String[]{  });
        assertThat(factory.createSecurityContext(withUser).getAuthentication().getName()).isEqualTo(withUser.username());
    }

    @Test
    public void rolesWorks() {
        Mockito.when(withUser.value()).thenReturn("valueUser");
        Mockito.when(withUser.password()).thenReturn("password");
        Mockito.when(withUser.roles()).thenReturn(new String[]{ "USER", "CUSTOM" });
        Mockito.when(withUser.authorities()).thenReturn(new String[]{  });
        assertThat(factory.createSecurityContext(withUser).getAuthentication().getAuthorities()).extracting("authority").containsOnly("ROLE_USER", "ROLE_CUSTOM");
    }

    @Test
    public void authoritiesWorks() {
        Mockito.when(withUser.value()).thenReturn("valueUser");
        Mockito.when(withUser.password()).thenReturn("password");
        Mockito.when(withUser.roles()).thenReturn(new String[]{ "USER" });
        Mockito.when(withUser.authorities()).thenReturn(new String[]{ "USER", "CUSTOM" });
        assertThat(factory.createSecurityContext(withUser).getAuthentication().getAuthorities()).extracting("authority").containsOnly("USER", "CUSTOM");
    }

    @Test(expected = IllegalStateException.class)
    public void authoritiesAndRolesInvalid() {
        Mockito.when(withUser.value()).thenReturn("valueUser");
        Mockito.when(withUser.roles()).thenReturn(new String[]{ "CUSTOM" });
        Mockito.when(withUser.authorities()).thenReturn(new String[]{ "USER", "CUSTOM" });
        factory.createSecurityContext(withUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rolesWithRolePrefixFails() {
        Mockito.when(withUser.value()).thenReturn("valueUser");
        Mockito.when(withUser.roles()).thenReturn(new String[]{ "ROLE_FAIL" });
        Mockito.when(withUser.authorities()).thenReturn(new String[]{  });
        factory.createSecurityContext(withUser);
    }
}

