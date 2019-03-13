/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.oauth2.client.web.server;


import ClientAuthenticationMethod.NONE;
import HttpStatus.BAD_REQUEST;
import PkceParameterNames.CODE_VERIFIER;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultServerOAuth2AuthorizationRequestResolverTests {
    @Mock
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    private DefaultServerOAuth2AuthorizationRequestResolver resolver;

    private ClientRegistration registration = TestClientRegistrations.clientRegistration().build();

    @Test
    public void resolveWhenNotMatchThenNull() {
        assertThat(resolve("/")).isNull();
    }

    @Test
    public void resolveWhenClientRegistrationNotFoundMatchThenBadRequest() {
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.empty());
        ResponseStatusException expected = catchThrowableOfType(() -> resolve("/oauth2/authorization/not-found-id"), ResponseStatusException.class);
        assertThat(expected.getStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void resolveWhenClientRegistrationFoundThenWorks() {
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(this.registration));
        OAuth2AuthorizationRequest request = resolve("/oauth2/authorization/not-found-id");
        assertThat(request.getAuthorizationRequestUri()).matches(("https://example.com/login/oauth/authorize\\?" + (("response_type=code&client_id=client-id&" + "scope=read:user&state=.*?&") + "redirect_uri=/login/oauth2/code/registration-id")));
    }

    @Test
    public void resolveWhenAuthorizationRequestWithValidPkceClientThenResolves() {
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(TestClientRegistrations.clientRegistration().clientAuthenticationMethod(NONE).clientSecret(null).build()));
        OAuth2AuthorizationRequest request = resolve("/oauth2/authorization/registration-id");
        assertThat(((String) (request.getAttribute(CODE_VERIFIER)))).matches("^([a-zA-Z0-9\\-\\.\\_\\~]){128}$");
        assertThat(request.getAuthorizationRequestUri()).matches(("https://example.com/login/oauth/authorize\\?" + (((("response_type=code&client_id=client-id&" + "scope=read:user&state=.*?&") + "redirect_uri=/login/oauth2/code/registration-id&") + "code_challenge_method=S256&") + "code_challenge=([a-zA-Z0-9\\-\\.\\_\\~]){43}")));
    }
}

