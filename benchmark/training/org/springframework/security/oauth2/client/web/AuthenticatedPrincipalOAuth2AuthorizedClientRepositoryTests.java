/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.oauth2.client.web;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;


/**
 * Tests for {@link AuthenticatedPrincipalOAuth2AuthorizedClientRepository}.
 *
 * @author Joe Grandja
 */
public class AuthenticatedPrincipalOAuth2AuthorizedClientRepositoryTests {
    private String registrationId = "registrationId";

    private String principalName = "principalName";

    private OAuth2AuthorizedClientService authorizedClientService;

    private OAuth2AuthorizedClientRepository anonymousAuthorizedClientRepository;

    private AuthenticatedPrincipalOAuth2AuthorizedClientRepository authorizedClientRepository;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void constructorWhenAuthorizedClientServiceIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setAuthorizedClientRepositoryWhenAuthorizedClientRepositoryIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.authorizedClientRepository.setAnonymousAuthorizedClientRepository(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void loadAuthorizedClientWhenAuthenticatedPrincipalThenLoadFromService() {
        Authentication authentication = this.createAuthenticatedPrincipal();
        this.authorizedClientRepository.loadAuthorizedClient(this.registrationId, authentication, this.request);
        Mockito.verify(this.authorizedClientService).loadAuthorizedClient(this.registrationId, this.principalName);
    }

    @Test
    public void loadAuthorizedClientWhenAnonymousPrincipalThenLoadFromAnonymousRepository() {
        Authentication authentication = this.createAnonymousPrincipal();
        this.authorizedClientRepository.loadAuthorizedClient(this.registrationId, authentication, this.request);
        Mockito.verify(this.anonymousAuthorizedClientRepository).loadAuthorizedClient(this.registrationId, authentication, this.request);
    }

    @Test
    public void saveAuthorizedClientWhenAuthenticatedPrincipalThenSaveToService() {
        Authentication authentication = this.createAuthenticatedPrincipal();
        OAuth2AuthorizedClient authorizedClient = Mockito.mock(OAuth2AuthorizedClient.class);
        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, authentication, this.request, this.response);
        Mockito.verify(this.authorizedClientService).saveAuthorizedClient(authorizedClient, authentication);
    }

    @Test
    public void saveAuthorizedClientWhenAnonymousPrincipalThenSaveToAnonymousRepository() {
        Authentication authentication = this.createAnonymousPrincipal();
        OAuth2AuthorizedClient authorizedClient = Mockito.mock(OAuth2AuthorizedClient.class);
        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, authentication, this.request, this.response);
        Mockito.verify(this.anonymousAuthorizedClientRepository).saveAuthorizedClient(authorizedClient, authentication, this.request, this.response);
    }

    @Test
    public void removeAuthorizedClientWhenAuthenticatedPrincipalThenRemoveFromService() {
        Authentication authentication = this.createAuthenticatedPrincipal();
        this.authorizedClientRepository.removeAuthorizedClient(this.registrationId, authentication, this.request, this.response);
        Mockito.verify(this.authorizedClientService).removeAuthorizedClient(this.registrationId, this.principalName);
    }

    @Test
    public void removeAuthorizedClientWhenAnonymousPrincipalThenRemoveFromAnonymousRepository() {
        Authentication authentication = this.createAnonymousPrincipal();
        this.authorizedClientRepository.removeAuthorizedClient(this.registrationId, authentication, this.request, this.response);
        Mockito.verify(this.anonymousAuthorizedClientRepository).removeAuthorizedClient(this.registrationId, authentication, this.request, this.response);
    }
}

