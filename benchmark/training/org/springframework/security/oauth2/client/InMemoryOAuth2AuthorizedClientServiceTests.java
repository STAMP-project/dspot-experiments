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
package org.springframework.security.oauth2.client;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.core.OAuth2AccessToken;


/**
 * Tests for {@link InMemoryOAuth2AuthorizedClientService}.
 *
 * @author Joe Grandja
 */
public class InMemoryOAuth2AuthorizedClientServiceTests {
    private String principalName1 = "principal-1";

    private String principalName2 = "principal-2";

    private ClientRegistration registration1 = TestClientRegistrations.clientRegistration().build();

    private ClientRegistration registration2 = TestClientRegistrations.clientRegistration2().build();

    private ClientRegistration registration3 = TestClientRegistrations.clientRegistration().clientId("client-3").registrationId("registration-3").build();

    private ClientRegistrationRepository clientRegistrationRepository = new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(this.registration1, this.registration2, this.registration3);

    private InMemoryOAuth2AuthorizedClientService authorizedClientService = new InMemoryOAuth2AuthorizedClientService(this.clientRegistrationRepository);

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenClientRegistrationRepositoryIsNullThenThrowIllegalArgumentException() {
        new InMemoryOAuth2AuthorizedClientService(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadAuthorizedClientWhenClientRegistrationIdIsNullThenThrowIllegalArgumentException() {
        this.authorizedClientService.loadAuthorizedClient(null, this.principalName1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadAuthorizedClientWhenPrincipalNameIsNullThenThrowIllegalArgumentException() {
        this.authorizedClientService.loadAuthorizedClient(this.registration1.getRegistrationId(), null);
    }

    @Test
    public void loadAuthorizedClientWhenClientRegistrationNotFoundThenReturnNull() {
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient("registration-not-found", this.principalName1);
        assertThat(authorizedClient).isNull();
    }

    @Test
    public void loadAuthorizedClientWhenClientRegistrationFoundButNotAssociatedToPrincipalThenReturnNull() {
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient(this.registration1.getRegistrationId(), "principal-not-found");
        assertThat(authorizedClient).isNull();
    }

    @Test
    public void loadAuthorizedClientWhenClientRegistrationFoundAndAssociatedToPrincipalThenReturnAuthorizedClient() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(this.principalName1);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration1, this.principalName1, Mockito.mock(OAuth2AccessToken.class));
        this.authorizedClientService.saveAuthorizedClient(authorizedClient, authentication);
        OAuth2AuthorizedClient loadedAuthorizedClient = this.authorizedClientService.loadAuthorizedClient(this.registration1.getRegistrationId(), this.principalName1);
        assertThat(loadedAuthorizedClient).isEqualTo(authorizedClient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveAuthorizedClientWhenAuthorizedClientIsNullThenThrowIllegalArgumentException() {
        this.authorizedClientService.saveAuthorizedClient(null, Mockito.mock(Authentication.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveAuthorizedClientWhenPrincipalIsNullThenThrowIllegalArgumentException() {
        this.authorizedClientService.saveAuthorizedClient(Mockito.mock(OAuth2AuthorizedClient.class), null);
    }

    @Test
    public void saveAuthorizedClientWhenSavedThenCanLoad() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(this.principalName2);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration3, this.principalName2, Mockito.mock(OAuth2AccessToken.class));
        this.authorizedClientService.saveAuthorizedClient(authorizedClient, authentication);
        OAuth2AuthorizedClient loadedAuthorizedClient = this.authorizedClientService.loadAuthorizedClient(this.registration3.getRegistrationId(), this.principalName2);
        assertThat(loadedAuthorizedClient).isEqualTo(authorizedClient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeAuthorizedClientWhenClientRegistrationIdIsNullThenThrowIllegalArgumentException() {
        this.authorizedClientService.removeAuthorizedClient(null, this.principalName2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeAuthorizedClientWhenPrincipalNameIsNullThenThrowIllegalArgumentException() {
        this.authorizedClientService.removeAuthorizedClient(this.registration3.getRegistrationId(), null);
    }

    @Test
    public void removeAuthorizedClientWhenSavedThenRemoved() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(this.principalName2);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration2, this.principalName2, Mockito.mock(OAuth2AccessToken.class));
        this.authorizedClientService.saveAuthorizedClient(authorizedClient, authentication);
        OAuth2AuthorizedClient loadedAuthorizedClient = this.authorizedClientService.loadAuthorizedClient(this.registration2.getRegistrationId(), this.principalName2);
        assertThat(loadedAuthorizedClient).isNotNull();
        this.authorizedClientService.removeAuthorizedClient(this.registration2.getRegistrationId(), this.principalName2);
        loadedAuthorizedClient = this.authorizedClientService.loadAuthorizedClient(this.registration2.getRegistrationId(), this.principalName2);
        assertThat(loadedAuthorizedClient).isNull();
    }
}

