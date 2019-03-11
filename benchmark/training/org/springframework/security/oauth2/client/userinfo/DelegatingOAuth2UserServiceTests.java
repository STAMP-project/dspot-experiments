/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.oauth2.client.userinfo;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.user.OAuth2User;


/**
 * Tests for {@link DelegatingOAuth2UserService}.
 *
 * @author Joe Grandja
 */
public class DelegatingOAuth2UserServiceTests {
    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenUserServicesIsNullThenThrowIllegalArgumentException() {
        new DelegatingOAuth2UserService(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenUserServicesIsEmptyThenThrowIllegalArgumentException() {
        new DelegatingOAuth2UserService(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void loadUserWhenUserRequestIsNullThenThrowIllegalArgumentException() {
        DelegatingOAuth2UserService<OAuth2UserRequest, OAuth2User> delegatingUserService = new DelegatingOAuth2UserService(Arrays.asList(Mockito.mock(OAuth2UserService.class), Mockito.mock(OAuth2UserService.class)));
        delegatingUserService.loadUser(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void loadUserWhenUserServiceCanLoadThenReturnUser() {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> userService1 = Mockito.mock(OAuth2UserService.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> userService2 = Mockito.mock(OAuth2UserService.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> userService3 = Mockito.mock(OAuth2UserService.class);
        OAuth2User mockUser = Mockito.mock(OAuth2User.class);
        Mockito.when(userService3.loadUser(ArgumentMatchers.any(OAuth2UserRequest.class))).thenReturn(mockUser);
        DelegatingOAuth2UserService<OAuth2UserRequest, OAuth2User> delegatingUserService = new DelegatingOAuth2UserService(Arrays.asList(userService1, userService2, userService3));
        OAuth2User loadedUser = delegatingUserService.loadUser(Mockito.mock(OAuth2UserRequest.class));
        assertThat(loadedUser).isEqualTo(mockUser);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void loadUserWhenUserServiceCannotLoadThenReturnNull() {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> userService1 = Mockito.mock(OAuth2UserService.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> userService2 = Mockito.mock(OAuth2UserService.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> userService3 = Mockito.mock(OAuth2UserService.class);
        DelegatingOAuth2UserService<OAuth2UserRequest, OAuth2User> delegatingUserService = new DelegatingOAuth2UserService(Arrays.asList(userService1, userService2, userService3));
        OAuth2User loadedUser = delegatingUserService.loadUser(Mockito.mock(OAuth2UserRequest.class));
        assertThat(loadedUser).isNull();
    }
}

