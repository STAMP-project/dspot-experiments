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
package org.springframework.security.oauth2.client.web.reactive.result.method.annotation;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2AuthorizedClientArgumentResolverTests {
    @Mock
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    private OAuth2AuthorizedClientArgumentResolver argumentResolver;

    private OAuth2AuthorizedClient authorizedClient;

    private Authentication authentication = new TestingAuthenticationToken("test", "this");

    @Test
    public void constructorWhenOAuth2AuthorizedClientServiceIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OAuth2AuthorizedClientArgumentResolver(this.clientRegistrationRepository, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void supportsParameterWhenParameterTypeOAuth2AuthorizedClientThenTrue() {
        MethodParameter methodParameter = this.getMethodParameter("paramTypeAuthorizedClient", OAuth2AuthorizedClient.class);
        assertThat(this.argumentResolver.supportsParameter(methodParameter)).isTrue();
    }

    @Test
    public void supportsParameterWhenParameterTypeOAuth2AuthorizedClientWithoutAnnotationThenFalse() {
        MethodParameter methodParameter = this.getMethodParameter("paramTypeAuthorizedClientWithoutAnnotation", OAuth2AuthorizedClient.class);
        assertThat(this.argumentResolver.supportsParameter(methodParameter)).isFalse();
    }

    @Test
    public void supportsParameterWhenParameterTypeUnsupportedWithoutAnnotationThenFalse() {
        MethodParameter methodParameter = this.getMethodParameter("paramTypeUnsupportedWithoutAnnotation", String.class);
        assertThat(this.argumentResolver.supportsParameter(methodParameter)).isFalse();
    }

    @Test
    public void resolveArgumentWhenRegistrationIdEmptyAndNotOAuth2AuthenticationThenThrowIllegalArgumentException() {
        MethodParameter methodParameter = this.getMethodParameter("registrationIdEmpty", OAuth2AuthorizedClient.class);
        assertThatThrownBy(() -> resolveArgument(methodParameter)).isInstanceOf(IllegalArgumentException.class).hasMessage("The clientRegistrationId could not be resolved. Please provide one");
    }

    @Test
    public void resolveArgumentWhenRegistrationIdEmptyAndOAuth2AuthenticationThenResolves() {
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(TestClientRegistrations.clientRegistration().build()));
        this.authentication = Mockito.mock(OAuth2AuthenticationToken.class);
        Mockito.when(getAuthorizedClientRegistrationId()).thenReturn("client1");
        MethodParameter methodParameter = this.getMethodParameter("registrationIdEmpty", OAuth2AuthorizedClient.class);
        resolveArgument(methodParameter);
    }

    @Test
    public void resolveArgumentWhenParameterTypeOAuth2AuthorizedClientAndCurrentAuthenticationNullThenResolves() {
        this.authentication = null;
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(TestClientRegistrations.clientRegistration().build()));
        MethodParameter methodParameter = this.getMethodParameter("paramTypeAuthorizedClient", OAuth2AuthorizedClient.class);
        assertThat(resolveArgument(methodParameter)).isSameAs(this.authorizedClient);
    }

    @Test
    public void resolveArgumentWhenOAuth2AuthorizedClientFoundThenResolves() {
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(TestClientRegistrations.clientRegistration().build()));
        MethodParameter methodParameter = this.getMethodParameter("paramTypeAuthorizedClient", OAuth2AuthorizedClient.class);
        assertThat(resolveArgument(methodParameter)).isSameAs(this.authorizedClient);
    }

    @Test
    public void resolveArgumentWhenOAuth2AuthorizedClientNotFoundThenThrowClientAuthorizationRequiredException() {
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(TestClientRegistrations.clientRegistration().build()));
        Mockito.when(this.authorizedClientRepository.loadAuthorizedClient(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        MethodParameter methodParameter = this.getMethodParameter("paramTypeAuthorizedClient", OAuth2AuthorizedClient.class);
        assertThatThrownBy(() -> resolveArgument(methodParameter)).isInstanceOf(ClientAuthorizationRequiredException.class);
    }

    static class TestController {
        void paramTypeAuthorizedClient(@RegisteredOAuth2AuthorizedClient("client1")
        OAuth2AuthorizedClient authorizedClient) {
        }

        void paramTypeAuthorizedClientWithoutAnnotation(OAuth2AuthorizedClient authorizedClient) {
        }

        void paramTypeUnsupported(@RegisteredOAuth2AuthorizedClient("client1")
        String param) {
        }

        void paramTypeUnsupportedWithoutAnnotation(String param) {
        }

        void registrationIdEmpty(@RegisteredOAuth2AuthorizedClient
        OAuth2AuthorizedClient authorizedClient) {
        }
    }
}

