/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.security.oauth2.client;


import IdTokenClaimNames.SUB;
import java.util.Map;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Provider;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.core.AuthenticationMethod.FORM;
import org.springframework.security.oauth2.core.AuthenticationMethod.HEADER;
import org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod.BASIC;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod.POST;


/**
 * Tests for {@link OAuth2ClientPropertiesRegistrationAdapter}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 * @author Thiago Hirata
 */
public class OAuth2ClientPropertiesRegistrationAdapterTests {
    private MockWebServer server;

    @Test
    public void getClientRegistrationsWhenUsingDefinedProviderShouldAdapt() {
        OAuth2ClientProperties properties = new OAuth2ClientProperties();
        Provider provider = createProvider();
        provider.setUserInfoAuthenticationMethod("form");
        OAuth2ClientProperties.Registration registration = createRegistration("provider");
        registration.setClientName("clientName");
        properties.getRegistration().put("registration", registration);
        properties.getProvider().put("provider", provider);
        Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties);
        ClientRegistration adapted = registrations.get("registration");
        ProviderDetails adaptedProvider = adapted.getProviderDetails();
        assertThat(adaptedProvider.getAuthorizationUri()).isEqualTo("http://example.com/auth");
        assertThat(adaptedProvider.getTokenUri()).isEqualTo("http://example.com/token");
        UserInfoEndpoint userInfoEndpoint = adaptedProvider.getUserInfoEndpoint();
        assertThat(userInfoEndpoint.getUri()).isEqualTo("http://example.com/info");
        assertThat(userInfoEndpoint.getAuthenticationMethod()).isEqualTo(FORM);
        assertThat(userInfoEndpoint.getUserNameAttributeName()).isEqualTo("sub");
        assertThat(adaptedProvider.getJwkSetUri()).isEqualTo("http://example.com/jwk");
        assertThat(adapted.getRegistrationId()).isEqualTo("registration");
        assertThat(adapted.getClientId()).isEqualTo("clientId");
        assertThat(adapted.getClientSecret()).isEqualTo("clientSecret");
        assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(POST);
        assertThat(adapted.getAuthorizationGrantType()).isEqualTo(AUTHORIZATION_CODE);
        assertThat(adapted.getRedirectUriTemplate()).isEqualTo("http://example.com/redirect");
        assertThat(adapted.getScopes()).containsExactly("user");
        assertThat(adapted.getClientName()).isEqualTo("clientName");
    }

    @Test
    public void getClientRegistrationsWhenUsingCommonProviderShouldAdapt() {
        OAuth2ClientProperties properties = new OAuth2ClientProperties();
        OAuth2ClientProperties.Registration registration = new OAuth2ClientProperties.Registration();
        registration.setProvider("google");
        registration.setClientId("clientId");
        registration.setClientSecret("clientSecret");
        properties.getRegistration().put("registration", registration);
        Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties);
        ClientRegistration adapted = registrations.get("registration");
        ProviderDetails adaptedProvider = adapted.getProviderDetails();
        assertThat(adaptedProvider.getAuthorizationUri()).isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
        assertThat(adaptedProvider.getTokenUri()).isEqualTo("https://www.googleapis.com/oauth2/v4/token");
        UserInfoEndpoint userInfoEndpoint = adaptedProvider.getUserInfoEndpoint();
        assertThat(userInfoEndpoint.getUri()).isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
        assertThat(userInfoEndpoint.getUserNameAttributeName()).isEqualTo(SUB);
        assertThat(adaptedProvider.getJwkSetUri()).isEqualTo("https://www.googleapis.com/oauth2/v3/certs");
        assertThat(adapted.getRegistrationId()).isEqualTo("registration");
        assertThat(adapted.getClientId()).isEqualTo("clientId");
        assertThat(adapted.getClientSecret()).isEqualTo("clientSecret");
        assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(BASIC);
        assertThat(adapted.getAuthorizationGrantType()).isEqualTo(AUTHORIZATION_CODE);
        assertThat(adapted.getRedirectUriTemplate()).isEqualTo("{baseUrl}/{action}/oauth2/code/{registrationId}");
        assertThat(adapted.getScopes()).containsExactly("openid", "profile", "email");
        assertThat(adapted.getClientName()).isEqualTo("Google");
    }

    @Test
    public void getClientRegistrationsWhenUsingCommonProviderWithOverrideShouldAdapt() {
        OAuth2ClientProperties properties = new OAuth2ClientProperties();
        OAuth2ClientProperties.Registration registration = createRegistration("google");
        registration.setClientName("clientName");
        properties.getRegistration().put("registration", registration);
        Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties);
        ClientRegistration adapted = registrations.get("registration");
        ProviderDetails adaptedProvider = adapted.getProviderDetails();
        assertThat(adaptedProvider.getAuthorizationUri()).isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
        assertThat(adaptedProvider.getTokenUri()).isEqualTo("https://www.googleapis.com/oauth2/v4/token");
        UserInfoEndpoint userInfoEndpoint = adaptedProvider.getUserInfoEndpoint();
        assertThat(userInfoEndpoint.getUri()).isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
        assertThat(userInfoEndpoint.getUserNameAttributeName()).isEqualTo(SUB);
        assertThat(userInfoEndpoint.getAuthenticationMethod()).isEqualTo(HEADER);
        assertThat(adaptedProvider.getJwkSetUri()).isEqualTo("https://www.googleapis.com/oauth2/v3/certs");
        assertThat(adapted.getRegistrationId()).isEqualTo("registration");
        assertThat(adapted.getClientId()).isEqualTo("clientId");
        assertThat(adapted.getClientSecret()).isEqualTo("clientSecret");
        assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(POST);
        assertThat(adapted.getAuthorizationGrantType()).isEqualTo(AUTHORIZATION_CODE);
        assertThat(adapted.getRedirectUriTemplate()).isEqualTo("http://example.com/redirect");
        assertThat(adapted.getScopes()).containsExactly("user");
        assertThat(adapted.getClientName()).isEqualTo("clientName");
    }

    @Test
    public void getClientRegistrationsWhenUnknownProviderShouldThrowException() {
        OAuth2ClientProperties properties = new OAuth2ClientProperties();
        OAuth2ClientProperties.Registration registration = new OAuth2ClientProperties.Registration();
        registration.setProvider("missing");
        properties.getRegistration().put("registration", registration);
        assertThatIllegalStateException().isThrownBy(() -> OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties)).withMessageContaining("Unknown provider ID 'missing'");
    }

    @Test
    public void getClientRegistrationsWhenProviderNotSpecifiedShouldUseRegistrationId() {
        OAuth2ClientProperties properties = new OAuth2ClientProperties();
        OAuth2ClientProperties.Registration registration = new OAuth2ClientProperties.Registration();
        registration.setClientId("clientId");
        registration.setClientSecret("clientSecret");
        properties.getRegistration().put("google", registration);
        Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties);
        ClientRegistration adapted = registrations.get("google");
        ProviderDetails adaptedProvider = adapted.getProviderDetails();
        assertThat(adaptedProvider.getAuthorizationUri()).isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
        assertThat(adaptedProvider.getTokenUri()).isEqualTo("https://www.googleapis.com/oauth2/v4/token");
        UserInfoEndpoint userInfoEndpoint = adaptedProvider.getUserInfoEndpoint();
        assertThat(userInfoEndpoint.getUri()).isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
        assertThat(userInfoEndpoint.getAuthenticationMethod()).isEqualTo(HEADER);
        assertThat(adaptedProvider.getJwkSetUri()).isEqualTo("https://www.googleapis.com/oauth2/v3/certs");
        assertThat(adapted.getRegistrationId()).isEqualTo("google");
        assertThat(adapted.getClientId()).isEqualTo("clientId");
        assertThat(adapted.getClientSecret()).isEqualTo("clientSecret");
        assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(BASIC);
        assertThat(adapted.getAuthorizationGrantType()).isEqualTo(AUTHORIZATION_CODE);
        assertThat(adapted.getRedirectUriTemplate()).isEqualTo("{baseUrl}/{action}/oauth2/code/{registrationId}");
        assertThat(adapted.getScopes()).containsExactly("openid", "profile", "email");
        assertThat(adapted.getClientName()).isEqualTo("Google");
    }

    @Test
    public void getClientRegistrationsWhenProviderNotSpecifiedAndUnknownProviderShouldThrowException() {
        OAuth2ClientProperties properties = new OAuth2ClientProperties();
        OAuth2ClientProperties.Registration registration = new OAuth2ClientProperties.Registration();
        properties.getRegistration().put("missing", registration);
        assertThatIllegalStateException().isThrownBy(() -> OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties)).withMessageContaining("Provider ID must be specified for client registration 'missing'");
    }

    @Test
    public void oidcProviderConfigurationWhenProviderNotSpecifiedOnRegistration() throws Exception {
        Registration login = new OAuth2ClientProperties.Registration();
        login.setClientId("clientId");
        login.setClientSecret("clientSecret");
        testOidcConfiguration(login, "okta");
    }

    @Test
    public void oidcProviderConfigurationWhenProviderSpecifiedOnRegistration() throws Exception {
        OAuth2ClientProperties.Registration login = new Registration();
        login.setProvider("okta-oidc");
        login.setClientId("clientId");
        login.setClientSecret("clientSecret");
        testOidcConfiguration(login, "okta-oidc");
    }

    @Test
    public void oidcProviderConfigurationWithCustomConfigurationOverridesProviderDefaults() throws Exception {
        this.server = new MockWebServer();
        this.server.start();
        String issuer = this.server.url("").toString();
        setupMockResponse(issuer);
        OAuth2ClientProperties.Registration registration = createRegistration("okta-oidc");
        Provider provider = createProvider();
        provider.setIssuerUri(issuer);
        OAuth2ClientProperties properties = new OAuth2ClientProperties();
        properties.getProvider().put("okta-oidc", provider);
        properties.getRegistration().put("okta", registration);
        Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties);
        ClientRegistration adapted = registrations.get("okta");
        ProviderDetails providerDetails = adapted.getProviderDetails();
        assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(ClientAuthenticationMethod.POST);
        assertThat(adapted.getAuthorizationGrantType()).isEqualTo(AuthorizationGrantType.AUTHORIZATION_CODE);
        assertThat(adapted.getRegistrationId()).isEqualTo("okta");
        assertThat(adapted.getClientName()).isEqualTo(issuer);
        assertThat(adapted.getScopes()).containsOnly("user");
        assertThat(adapted.getRedirectUriTemplate()).isEqualTo("http://example.com/redirect");
        assertThat(providerDetails.getAuthorizationUri()).isEqualTo("http://example.com/auth");
        assertThat(providerDetails.getTokenUri()).isEqualTo("http://example.com/token");
        assertThat(providerDetails.getJwkSetUri()).isEqualTo("http://example.com/jwk");
        UserInfoEndpoint userInfoEndpoint = providerDetails.getUserInfoEndpoint();
        assertThat(userInfoEndpoint.getUri()).isEqualTo("http://example.com/info");
        assertThat(userInfoEndpoint.getUserNameAttributeName()).isEqualTo("sub");
    }
}

