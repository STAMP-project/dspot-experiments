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
package org.springframework.security.oauth2.client.registration;


import AuthenticationMethod.FORM;
import AuthenticationMethod.HEADER;
import AuthorizationGrantType.AUTHORIZATION_CODE;
import AuthorizationGrantType.CLIENT_CREDENTIALS;
import AuthorizationGrantType.IMPLICIT;
import ClientAuthenticationMethod.BASIC;
import ClientAuthenticationMethod.NONE;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;


/**
 * Tests for {@link ClientRegistration}.
 *
 * @author Joe Grandja
 */
public class ClientRegistrationTests {
    private static final String REGISTRATION_ID = "registration-1";

    private static final String CLIENT_ID = "client-1";

    private static final String CLIENT_SECRET = "secret";

    private static final String REDIRECT_URI = "https://example.com";

    private static final Set<String> SCOPES = Collections.unmodifiableSet(Stream.of("openid", "profile", "email").collect(Collectors.toSet()));

    private static final String AUTHORIZATION_URI = "https://provider.com/oauth2/authorization";

    private static final String TOKEN_URI = "https://provider.com/oauth2/token";

    private static final String JWK_SET_URI = "https://provider.com/oauth2/keys";

    private static final String CLIENT_NAME = "Client 1";

    private static final Map<String, Object> PROVIDER_CONFIGURATION_METADATA = Collections.unmodifiableMap(ClientRegistrationTests.createProviderConfigurationMetadata());

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenAuthorizationGrantTypeIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(null).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test
    public void buildWhenAuthorizationCodeGrantAllAttributesProvidedThenAllAttributesAreSet() {
        ClientRegistration registration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).providerConfigurationMetadata(ClientRegistrationTests.PROVIDER_CONFIGURATION_METADATA).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(registration.getRegistrationId()).isEqualTo(ClientRegistrationTests.REGISTRATION_ID);
        assertThat(registration.getClientId()).isEqualTo(ClientRegistrationTests.CLIENT_ID);
        assertThat(registration.getClientSecret()).isEqualTo(ClientRegistrationTests.CLIENT_SECRET);
        assertThat(registration.getClientAuthenticationMethod()).isEqualTo(BASIC);
        assertThat(registration.getAuthorizationGrantType()).isEqualTo(AUTHORIZATION_CODE);
        assertThat(registration.getRedirectUriTemplate()).isEqualTo(ClientRegistrationTests.REDIRECT_URI);
        assertThat(registration.getScopes()).isEqualTo(ClientRegistrationTests.SCOPES);
        assertThat(registration.getProviderDetails().getAuthorizationUri()).isEqualTo(ClientRegistrationTests.AUTHORIZATION_URI);
        assertThat(registration.getProviderDetails().getTokenUri()).isEqualTo(ClientRegistrationTests.TOKEN_URI);
        assertThat(registration.getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod()).isEqualTo(FORM);
        assertThat(registration.getProviderDetails().getJwkSetUri()).isEqualTo(ClientRegistrationTests.JWK_SET_URI);
        assertThat(registration.getProviderDetails().getConfigurationMetadata()).isEqualTo(ClientRegistrationTests.PROVIDER_CONFIGURATION_METADATA);
        assertThat(registration.getClientName()).isEqualTo(ClientRegistrationTests.CLIENT_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenAuthorizationCodeGrantRegistrationIdIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(null).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenAuthorizationCodeGrantClientIdIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(null).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test
    public void buildWhenAuthorizationCodeGrantClientSecretIsNullThenDefaultToEmpty() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(null).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(clientRegistration.getClientSecret()).isEqualTo("");
    }

    @Test
    public void buildWhenAuthorizationCodeGrantClientAuthenticationMethodNotProvidedThenDefaultToBasic() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(clientRegistration.getClientAuthenticationMethod()).isEqualTo(BASIC);
    }

    @Test
    public void buildWhenAuthorizationCodeGrantClientAuthenticationMethodNotProvidedAndClientSecretNullThenDefaultToNone() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(null).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(clientRegistration.getClientAuthenticationMethod()).isEqualTo(NONE);
    }

    @Test
    public void buildWhenAuthorizationCodeGrantClientAuthenticationMethodNotProvidedAndClientSecretBlankThenDefaultToNone() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(" ").authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(clientRegistration.getClientAuthenticationMethod()).isEqualTo(NONE);
        assertThat(clientRegistration.getClientSecret()).isEqualTo("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenAuthorizationCodeGrantRedirectUriIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(null).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    // gh-5494
    @Test
    public void buildWhenAuthorizationCodeGrantScopeIsNullThenScopeNotRequired() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(((String[]) (null))).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenAuthorizationCodeGrantAuthorizationUriIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(null).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenAuthorizationCodeGrantTokenUriIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(null).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test
    public void buildWhenAuthorizationCodeGrantClientNameNotProvidedThenDefaultToRegistrationId() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(FORM).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).build();
        assertThat(clientRegistration.getClientName()).isEqualTo(clientRegistration.getRegistrationId());
    }

    @Test
    public void buildWhenAuthorizationCodeGrantScopeDoesNotContainOpenidThenJwkSetUriNotRequired() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope("scope1").authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).userInfoAuthenticationMethod(FORM).tokenUri(ClientRegistrationTests.TOKEN_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    // gh-5494
    @Test
    public void buildWhenAuthorizationCodeGrantScopeIsNullThenJwkSetUriNotRequired() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test
    public void buildWhenAuthorizationCodeGrantProviderConfigurationMetadataIsNullThenDefaultToEmpty() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(HEADER).providerConfigurationMetadata(null).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(clientRegistration.getProviderDetails().getConfigurationMetadata()).isNotNull();
        assertThat(clientRegistration.getProviderDetails().getConfigurationMetadata()).isEmpty();
    }

    @Test
    public void buildWhenAuthorizationCodeGrantProviderConfigurationMetadataEmptyThenIsEmpty() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).userInfoAuthenticationMethod(HEADER).providerConfigurationMetadata(Collections.emptyMap()).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(clientRegistration.getProviderDetails().getConfigurationMetadata()).isNotNull();
        assertThat(clientRegistration.getProviderDetails().getConfigurationMetadata()).isEmpty();
    }

    @Test
    public void buildWhenImplicitGrantAllAttributesProvidedThenAllAttributesAreSet() {
        ClientRegistration registration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).authorizationGrantType(IMPLICIT).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).userInfoAuthenticationMethod(FORM).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(registration.getRegistrationId()).isEqualTo(ClientRegistrationTests.REGISTRATION_ID);
        assertThat(registration.getClientId()).isEqualTo(ClientRegistrationTests.CLIENT_ID);
        assertThat(registration.getAuthorizationGrantType()).isEqualTo(IMPLICIT);
        assertThat(registration.getRedirectUriTemplate()).isEqualTo(ClientRegistrationTests.REDIRECT_URI);
        assertThat(registration.getScopes()).isEqualTo(ClientRegistrationTests.SCOPES);
        assertThat(registration.getProviderDetails().getAuthorizationUri()).isEqualTo(ClientRegistrationTests.AUTHORIZATION_URI);
        assertThat(registration.getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod()).isEqualTo(FORM);
        assertThat(registration.getClientName()).isEqualTo(ClientRegistrationTests.CLIENT_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenImplicitGrantRegistrationIdIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(null).clientId(ClientRegistrationTests.CLIENT_ID).authorizationGrantType(IMPLICIT).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).userInfoAuthenticationMethod(FORM).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenImplicitGrantClientIdIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(null).authorizationGrantType(IMPLICIT).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).userInfoAuthenticationMethod(FORM).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenImplicitGrantRedirectUriIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).authorizationGrantType(IMPLICIT).redirectUriTemplate(null).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).userInfoAuthenticationMethod(FORM).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    // gh-5494
    @Test
    public void buildWhenImplicitGrantScopeIsNullThenScopeNotRequired() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).authorizationGrantType(IMPLICIT).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(((String[]) (null))).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).userInfoAuthenticationMethod(FORM).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenImplicitGrantAuthorizationUriIsNullThenThrowIllegalArgumentException() {
        ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).authorizationGrantType(IMPLICIT).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(null).userInfoAuthenticationMethod(FORM).clientName(ClientRegistrationTests.CLIENT_NAME).build();
    }

    @Test
    public void buildWhenImplicitGrantClientNameNotProvidedThenDefaultToRegistrationId() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).authorizationGrantType(IMPLICIT).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).userInfoAuthenticationMethod(FORM).build();
        assertThat(clientRegistration.getClientName()).isEqualTo(clientRegistration.getRegistrationId());
    }

    @Test
    public void buildWhenOverrideRegistrationIdThenOverridden() {
        String overriddenId = "override";
        ClientRegistration registration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).registrationId(overriddenId).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(AUTHORIZATION_CODE).redirectUriTemplate(ClientRegistrationTests.REDIRECT_URI).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).authorizationUri(ClientRegistrationTests.AUTHORIZATION_URI).tokenUri(ClientRegistrationTests.TOKEN_URI).jwkSetUri(ClientRegistrationTests.JWK_SET_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(registration.getRegistrationId()).isEqualTo(overriddenId);
    }

    @Test
    public void buildWhenClientCredentialsGrantAllAttributesProvidedThenAllAttributesAreSet() {
        ClientRegistration registration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).clientAuthenticationMethod(BASIC).authorizationGrantType(CLIENT_CREDENTIALS).scope(ClientRegistrationTests.SCOPES.toArray(new String[0])).tokenUri(ClientRegistrationTests.TOKEN_URI).clientName(ClientRegistrationTests.CLIENT_NAME).build();
        assertThat(registration.getRegistrationId()).isEqualTo(ClientRegistrationTests.REGISTRATION_ID);
        assertThat(registration.getClientId()).isEqualTo(ClientRegistrationTests.CLIENT_ID);
        assertThat(registration.getClientSecret()).isEqualTo(ClientRegistrationTests.CLIENT_SECRET);
        assertThat(registration.getClientAuthenticationMethod()).isEqualTo(BASIC);
        assertThat(registration.getAuthorizationGrantType()).isEqualTo(CLIENT_CREDENTIALS);
        assertThat(registration.getScopes()).isEqualTo(ClientRegistrationTests.SCOPES);
        assertThat(registration.getProviderDetails().getTokenUri()).isEqualTo(ClientRegistrationTests.TOKEN_URI);
        assertThat(registration.getClientName()).isEqualTo(ClientRegistrationTests.CLIENT_NAME);
    }

    @Test
    public void buildWhenClientCredentialsGrantRegistrationIdIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> ClientRegistration.withRegistrationId(null).clientId(CLIENT_ID).clientSecret(CLIENT_SECRET).clientAuthenticationMethod(ClientAuthenticationMethod.BASIC).authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).tokenUri(TOKEN_URI).build()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void buildWhenClientCredentialsGrantClientIdIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> ClientRegistration.withRegistrationId(REGISTRATION_ID).clientId(null).clientSecret(CLIENT_SECRET).clientAuthenticationMethod(ClientAuthenticationMethod.BASIC).authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).tokenUri(TOKEN_URI).build()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void buildWhenClientCredentialsGrantClientSecretIsNullThenDefaultToEmpty() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(null).clientAuthenticationMethod(BASIC).authorizationGrantType(CLIENT_CREDENTIALS).tokenUri(ClientRegistrationTests.TOKEN_URI).build();
        assertThat(clientRegistration.getClientSecret()).isEqualTo("");
    }

    @Test
    public void buildWhenClientCredentialsGrantClientAuthenticationMethodNotProvidedThenDefaultToBasic() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(ClientRegistrationTests.REGISTRATION_ID).clientId(ClientRegistrationTests.CLIENT_ID).clientSecret(ClientRegistrationTests.CLIENT_SECRET).authorizationGrantType(CLIENT_CREDENTIALS).tokenUri(ClientRegistrationTests.TOKEN_URI).build();
        assertThat(clientRegistration.getClientAuthenticationMethod()).isEqualTo(BASIC);
    }

    @Test
    public void buildWhenClientCredentialsGrantTokenUriIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> ClientRegistration.withRegistrationId(REGISTRATION_ID).clientId(CLIENT_ID).clientSecret(CLIENT_SECRET).clientAuthenticationMethod(ClientAuthenticationMethod.BASIC).authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).tokenUri(null).build()).isInstanceOf(IllegalArgumentException.class);
    }

    // gh-6256
    @Test
    public void buildWhenScopesContainASpaceThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> TestClientRegistrations.clientCredentials().scope("openid profile email").build()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void buildWhenScopesContainAnInvalidCharacterThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> TestClientRegistrations.clientCredentials().scope("an\"invalid\"scope").build()).isInstanceOf(IllegalArgumentException.class);
    }
}

