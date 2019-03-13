/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.client;


import ClientScopesClientRegistrationPolicyFactory.ALLOWED_CLIENT_SCOPES;
import MaxClientsClientRegistrationPolicyFactory.MAX_CLIENTS;
import OAuth2Constants.SCOPE_PROFILE;
import OIDCLoginProtocol.LOGIN_PROTOCOL;
import ProtocolMappersClientRegistrationPolicyFactory.ALLOWED_PROTOCOL_MAPPER_TYPES;
import RegistrationAuth.ANONYMOUS;
import TrustedHostClientRegistrationPolicyFactory.HOST_SENDING_REGISTRATION_REQUEST_MUST_MATCH;
import TrustedHostClientRegistrationPolicyFactory.PROVIDER_ID;
import TrustedHostClientRegistrationPolicyFactory.TRUSTED_HOSTS;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.client.registration.Auth;
import org.keycloak.representations.idm.ClientInitialAccessCreatePresentation;
import org.keycloak.representations.idm.ClientInitialAccessPresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.ComponentTypeRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.oidc.OIDCClientRepresentation;
import org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ClientRegistrationPoliciesTest extends AbstractClientRegistrationTest {
    private static final String PRIVATE_KEY = "MIICXAIBAAKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQABAoGAfmO8gVhyBxdqlxmIuglbz8bcjQbhXJLR2EoS8ngTXmN1bo2L90M0mUKSdc7qF10LgETBzqL8jYlQIbt+e6TH8fcEpKCjUlyq0Mf/vVbfZSNaVycY13nTzo27iPyWQHK5NLuJzn1xvxxrUeXI6A2WFpGEBLbHjwpx5WQG9A+2scECQQDvdn9NE75HPTVPxBqsEd2z10TKkl9CZxu10Qby3iQQmWLEJ9LNmy3acvKrE3gMiYNWb6xHPKiIqOR1as7L24aTAkEAtyvQOlCvr5kAjVqrEKXalj0Tzewjweuxc0pskvArTI2Oo070h65GpoIKLc9jf+UA69cRtquwP93aZKtW06U8dQJAF2Y44ks/mK5+eyDqik3koCI08qaC8HYq2wVl7G2QkJ6sbAaILtcvD92ToOvyGyeE0flvmDZxMYlvaZnaQ0lcSQJBAKZU6umJi3/xeEbkJqMfeLclD27XGEFoPeNrmdx0q10Azp4NfJAY+Z8KRyQCR2BEG+oNitBOZ+YXF9KCpH3cdmECQHEigJhYg+ykOvr1aiZUMFT72HU0jnmQe2FVekuG+LJUt2Tm7GtMjTFoGpf0JwrVuZN39fOYAlo+nTixgeW7X8Y=";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQAB";

    @Test
    public void testAnonCreateWithTrustedHost() throws Exception {
        // Failed to create client (untrusted host)
        OIDCClientRepresentation client = createRepOidc("http://root", "http://redirect");
        assertOidcFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, client, 403, "Host not trusted");
        // Should still fail (bad redirect_uri)
        setTrustedHost("localhost");
        assertOidcFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, client, 403, "URL doesn't match");
        // Should still fail (bad base_uri)
        client.setRedirectUris(Collections.singletonList("http://localhost:8080/foo"));
        assertOidcFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, client, 403, "URL doesn't match");
        // Success create client
        client.setClientUri("http://localhost:8080/foo");
        OIDCClientRepresentation oidcClientRep = reg.oidc().create(client);
        // Test registration access token
        assertRegAccessToken(oidcClientRep.getRegistrationAccessToken(), ANONYMOUS);
    }

    @Test
    public void testAnonUpdateWithTrustedHost() throws Exception {
        setTrustedHost("localhost");
        OIDCClientRepresentation client = create();
        // Fail update client
        client.setRedirectUris(Collections.singletonList("http://bad:8080/foo"));
        assertOidcFail(ClientRegistrationPoliciesTest.ClientRegOp.UPDATE, client, 403, "URL doesn't match");
        // Should be fine now
        client.setRedirectUris(Collections.singletonList("http://localhost:8080/foo"));
        reg.oidc().update(client);
    }

    @Test
    public void testRedirectUriWithDomain() throws Exception {
        // Change the policy to avoid checking hosts
        ComponentRepresentation trustedHostPolicyRep = findPolicyByProviderAndAuth(PROVIDER_ID, getPolicyAnon());
        trustedHostPolicyRep.getConfig().putSingle(HOST_SENDING_REGISTRATION_REQUEST_MUST_MATCH, "false");
        // Configure some trusted host and domain
        trustedHostPolicyRep.getConfig().put(TRUSTED_HOSTS, Arrays.asList("www.host.com", "*.example.com"));
        realmResource().components().component(trustedHostPolicyRep.getId()).update(trustedHostPolicyRep);
        // Verify client can be created with the redirectUri from trusted host and domain
        OIDCClientRepresentation oidcClientRep = createRepOidc("http://www.host.com", "http://www.example.com");
        reg.oidc().create(oidcClientRep);
        // Remove domain from the config
        trustedHostPolicyRep.getConfig().put(TRUSTED_HOSTS, Arrays.asList("www.host.com", "www1.example.com"));
        realmResource().components().component(trustedHostPolicyRep.getId()).update(trustedHostPolicyRep);
        // Check new client can't be created anymore
        oidcClientRep = createRepOidc("http://www.host.com", "http://www.example.com");
        assertOidcFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, oidcClientRep, 403, "URL doesn't match");
    }

    @Test
    public void testAnonConsentRequired() throws Exception {
        setTrustedHost("localhost");
        OIDCClientRepresentation client = create();
        // Assert new client has consent required
        String clientId = client.getClientId();
        ClientRepresentation clientRep = ApiUtil.findClientByClientId(realmResource(), clientId).toRepresentation();
        org.keycloak.testsuite.Assert.assertTrue(clientRep.isConsentRequired());
        // Try update with disabled consent required. Should fail
        clientRep.setConsentRequired(false);
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.UPDATE, clientRep, 403, "Not permitted to update consentRequired to false");
        // Try update with enabled consent required. Should pass
        clientRep.setConsentRequired(true);
        reg.update(clientRep);
    }

    @Test
    public void testAnonFullScopeAllowed() throws Exception {
        setTrustedHost("localhost");
        OIDCClientRepresentation client = create();
        // Assert new client has fullScopeAllowed disabled
        String clientId = client.getClientId();
        ClientRepresentation clientRep = ApiUtil.findClientByClientId(realmResource(), clientId).toRepresentation();
        org.keycloak.testsuite.Assert.assertFalse(clientRep.isFullScopeAllowed());
        // Try update with disabled consent required. Should fail
        clientRep.setFullScopeAllowed(true);
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.UPDATE, clientRep, 403, "Not permitted to enable fullScopeAllowed");
        // Try update with enabled consent required. Should pass
        clientRep.setFullScopeAllowed(false);
        reg.update(clientRep);
    }

    @Test
    public void testClientDisabledPolicy() throws Exception {
        setTrustedHost("localhost");
        // Assert new client is enabled
        OIDCClientRepresentation client = create();
        String clientId = client.getClientId();
        ClientRepresentation clientRep = ApiUtil.findClientByClientId(realmResource(), clientId).toRepresentation();
        org.keycloak.testsuite.Assert.assertTrue(clientRep.isEnabled());
        // Add client-disabled policy
        ComponentRepresentation rep = new ComponentRepresentation();
        rep.setName("Clients disabled");
        rep.setParentId(AbstractClientRegistrationTest.REALM_NAME);
        rep.setProviderId(ClientDisabledClientRegistrationPolicyFactory.PROVIDER_ID);
        rep.setProviderType(ClientRegistrationPolicy.class.getName());
        rep.setSubType(getPolicyAnon());
        Response response = realmResource().components().add(rep);
        String policyId = ApiUtil.getCreatedId(response);
        response.close();
        // Assert new client is disabled
        client = create();
        clientId = client.getClientId();
        clientRep = ApiUtil.findClientByClientId(realmResource(), clientId).toRepresentation();
        org.keycloak.testsuite.Assert.assertFalse(clientRep.isEnabled());
        // Try enable client. Should fail
        clientRep.setEnabled(true);
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.UPDATE, clientRep, 403, "Not permitted to enable client");
        // Try update disabled client. Should pass
        clientRep.setEnabled(false);
        reg.update(clientRep);
        // Revert
        realmResource().components().component(policyId).remove();
    }

    @Test
    public void testMaxClientsPolicy() throws Exception {
        setTrustedHost("localhost");
        int clientsCount = realmResource().clients().findAll().size();
        int newClientsLimit = clientsCount + 1;
        // Allow to create one more client to current limit
        ComponentRepresentation maxClientsPolicyRep = findPolicyByProviderAndAuth(MaxClientsClientRegistrationPolicyFactory.PROVIDER_ID, getPolicyAnon());
        maxClientsPolicyRep.getConfig().putSingle(MAX_CLIENTS, String.valueOf(newClientsLimit));
        realmResource().components().component(maxClientsPolicyRep.getId()).update(maxClientsPolicyRep);
        // I can register one new client
        OIDCClientRepresentation client = create();
        // I can't register more clients
        assertOidcFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, createRepOidc(), 403, (("It's allowed to have max " + newClientsLimit) + " clients per realm"));
        // Revert
        maxClientsPolicyRep.getConfig().putSingle(MAX_CLIENTS, String.valueOf(10000));
        realmResource().components().component(maxClientsPolicyRep.getId()).update(maxClientsPolicyRep);
    }

    @Test
    public void testProviders() throws Exception {
        List<ComponentTypeRepresentation> reps = realmResource().clientRegistrationPolicy().getProviders();
        Map<String, ComponentTypeRepresentation> providersMap = reps.stream().collect(Collectors.toMap((ComponentTypeRepresentation rep) -> {
            return rep.getId();
        }, (ComponentTypeRepresentation rep) -> {
            return rep;
        }));
        // test that ProtocolMappersClientRegistrationPolicy provider contains available protocol mappers
        ComponentTypeRepresentation protMappersRep = providersMap.get(ProtocolMappersClientRegistrationPolicyFactory.PROVIDER_ID);
        List<String> availableMappers = getProviderConfigProperty(protMappersRep, ALLOWED_PROTOCOL_MAPPER_TYPES);
        List<String> someExpectedMappers = Arrays.asList(UserAttributeStatementMapper.PROVIDER_ID, UserAttributeMapper.PROVIDER_ID, UserPropertyAttributeStatementMapper.PROVIDER_ID, UserPropertyMapper.PROVIDER_ID, HardcodedRole.PROVIDER_ID);
        availableMappers.containsAll(someExpectedMappers);
        // test that clientScope provider contains just the default client scopes
        ComponentTypeRepresentation clientScopeRep = providersMap.get(ClientScopesClientRegistrationPolicyFactory.PROVIDER_ID);
        List<String> clientScopes = getProviderConfigProperty(clientScopeRep, ALLOWED_CLIENT_SCOPES);
        org.keycloak.testsuite.Assert.assertFalse(clientScopes.isEmpty());
        org.keycloak.testsuite.Assert.assertTrue(clientScopes.contains(SCOPE_PROFILE));
        org.keycloak.testsuite.Assert.assertFalse(clientScopes.contains("foo"));
        org.keycloak.testsuite.Assert.assertFalse(clientScopes.contains("bar"));
        // Add some clientScopes
        ClientScopeRepresentation clientScope = new ClientScopeRepresentation();
        clientScope.setName("foo");
        Response response = realmResource().clientScopes().create(clientScope);
        String fooScopeId = ApiUtil.getCreatedId(response);
        response.close();
        clientScope = new ClientScopeRepresentation();
        clientScope.setName("bar");
        response = realmResource().clientScopes().create(clientScope);
        String barScopeId = ApiUtil.getCreatedId(response);
        response.close();
        // send request again and test that clientScope provider contains added client scopes
        reps = realmResource().clientRegistrationPolicy().getProviders();
        clientScopeRep = reps.stream().filter((ComponentTypeRepresentation rep1) -> {
            return rep1.getId().equals(ClientScopesClientRegistrationPolicyFactory.PROVIDER_ID);
        }).findFirst().get();
        clientScopes = getProviderConfigProperty(clientScopeRep, ALLOWED_CLIENT_SCOPES);
        org.keycloak.testsuite.Assert.assertTrue(clientScopes.contains("foo"));
        org.keycloak.testsuite.Assert.assertTrue(clientScopes.contains("bar"));
        // Revert client scopes
        realmResource().clientScopes().get(fooScopeId).remove();
        realmResource().clientScopes().get(barScopeId).remove();
    }

    @Test
    public void testClientScopesPolicy() throws Exception {
        setTrustedHost("localhost");
        // Add some clientScope through Admin REST
        ClientScopeRepresentation clientScope = new ClientScopeRepresentation();
        clientScope.setName("foo");
        Response response = realmResource().clientScopes().create(clientScope);
        String clientScopeId = ApiUtil.getCreatedId(response);
        response.close();
        // I can't register new client with this scope
        ClientRepresentation clientRep = createRep("test-app");
        clientRep.setDefaultClientScopes(Collections.singletonList("foo"));
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, clientRep, 403, "Not permitted to use specified clientScope");
        // Register client without scope - should success
        clientRep.setDefaultClientScopes(null);
        ClientRepresentation registeredClient = reg.create(clientRep);
        reg.auth(Auth.token(registeredClient));
        // Try to update client with scope - should fail
        registeredClient.setDefaultClientScopes(Collections.singletonList("foo"));
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.UPDATE, registeredClient, 403, "Not permitted to use specified clientScope");
        // Update client with the clientScope via Admin REST
        ClientResource client = ApiUtil.findClientByClientId(realmResource(), "test-app");
        client.addDefaultClientScope(clientScopeId);
        // Now the update via clientRegistration is permitted too as scope was already set
        reg.update(registeredClient);
        // Revert client scope
        realmResource().clients().get(client.toRepresentation().getId()).remove();
        realmResource().clientScopes().get(clientScopeId).remove();
    }

    @Test
    public void testClientScopesPolicyWithPermittedScope() throws Exception {
        setTrustedHost("localhost");
        // Add some clientScope through Admin REST
        ClientScopeRepresentation clientScope = new ClientScopeRepresentation();
        clientScope.setName("foo");
        Response response = realmResource().clientScopes().create(clientScope);
        String clientScopeId = ApiUtil.getCreatedId(response);
        response.close();
        // I can't register new client with this scope
        ClientRepresentation clientRep = createRep("test-app");
        clientRep.setDefaultClientScopes(Collections.singletonList("foo"));
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, clientRep, 403, "Not permitted to use specified clientScope");
        // Update the policy to allow the "foo" scope
        ComponentRepresentation clientScopesPolicyRep = findPolicyByProviderAndAuth(ClientScopesClientRegistrationPolicyFactory.PROVIDER_ID, getPolicyAnon());
        clientScopesPolicyRep.getConfig().putSingle(ALLOWED_CLIENT_SCOPES, "foo");
        realmResource().components().component(clientScopesPolicyRep.getId()).update(clientScopesPolicyRep);
        // Check that I can register client now
        ClientRepresentation registeredClient = reg.create(clientRep);
        org.keycloak.testsuite.Assert.assertNotNull(registeredClient.getRegistrationAccessToken());
        // Revert client scope
        ApiUtil.findClientResourceByClientId(realmResource(), "test-app").remove();
        realmResource().clientScopes().get(clientScopeId).remove();
    }

    // PROTOCOL MAPPERS
    @Test
    public void testProtocolMappersCreate() throws Exception {
        setTrustedHost("localhost");
        // Try to add client with some "hardcoded role" mapper. Should fail
        ClientRepresentation clientRep = createRep("test-app");
        clientRep.setProtocolMappers(Collections.singletonList(createHardcodedMapperRep()));
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, clientRep, 403, "ProtocolMapper type not allowed");
        // Try the same authenticated. Should still fail.
        ClientInitialAccessPresentation token = adminClient.realm(AbstractClientRegistrationTest.REALM_NAME).clientInitialAccess().create(new ClientInitialAccessCreatePresentation(0, 10));
        reg.auth(Auth.token(token));
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, clientRep, 403, "ProtocolMapper type not allowed");
        // Update the "authenticated" policy and allow hardcoded role mapper
        ComponentRepresentation protocolMapperPolicyRep = findPolicyByProviderAndAuth(ProtocolMappersClientRegistrationPolicyFactory.PROVIDER_ID, getPolicyAuth());
        protocolMapperPolicyRep.getConfig().add(ALLOWED_PROTOCOL_MAPPER_TYPES, HardcodedRole.PROVIDER_ID);
        realmResource().components().component(protocolMapperPolicyRep.getId()).update(protocolMapperPolicyRep);
        // Check authenticated registration is permitted
        ClientRepresentation registeredClient = reg.create(clientRep);
        org.keycloak.testsuite.Assert.assertNotNull(registeredClient.getRegistrationAccessToken());
        // Check "anonymous" registration still fails
        clientRep = createRep("test-app-2");
        clientRep.setProtocolMappers(Collections.singletonList(createHardcodedMapperRep()));
        reg.auth(null);
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.CREATE, clientRep, 403, "ProtocolMapper type not allowed");
        // Revert policy change
        ApiUtil.findClientResourceByClientId(realmResource(), "test-app").remove();
        protocolMapperPolicyRep.getConfig().remove(ALLOWED_PROTOCOL_MAPPER_TYPES, HardcodedRole.PROVIDER_ID);
        realmResource().components().component(protocolMapperPolicyRep.getId()).update(protocolMapperPolicyRep);
    }

    @Test
    public void testProtocolMappersUpdate() throws Exception {
        setTrustedHost("localhost");
        // Check I can add client with allowed protocolMappers
        ProtocolMapperRepresentation protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setName("Full name");
        protocolMapper.setProtocolMapper(FullNameMapper.PROVIDER_ID);
        protocolMapper.setProtocol(LOGIN_PROTOCOL);
        ClientRepresentation clientRep = createRep("test-app");
        clientRep.setProtocolMappers(Collections.singletonList(protocolMapper));
        ClientRepresentation registeredClient = reg.create(clientRep);
        reg.auth(Auth.token(registeredClient));
        // Add some disallowed protocolMapper
        registeredClient.getProtocolMappers().add(createHardcodedMapperRep());
        // Check I can't update client because of protocolMapper
        assertFail(ClientRegistrationPoliciesTest.ClientRegOp.UPDATE, registeredClient, 403, "ProtocolMapper type not allowed");
        // Remove "bad" protocolMapper
        registeredClient.getProtocolMappers().removeIf((ProtocolMapperRepresentation mapper) -> {
            return mapper.getProtocolMapper().equals(HardcodedRole.PROVIDER_ID);
        });
        // Check I can update client now
        reg.update(registeredClient);
        // Revert client
        ApiUtil.findClientResourceByClientId(realmResource(), "test-app").remove();
    }

    @Test
    public void testProtocolMappersConsentRequired() throws Exception {
        setTrustedHost("localhost");
        // Register client and assert it doesn't have builtin protocol mappers
        ClientRepresentation clientRep = createRep("test-app");
        ClientRepresentation registeredClient = reg.create(clientRep);
        org.keycloak.testsuite.Assert.assertNull(registeredClient.getProtocolMappers());
        // Revert
        ApiUtil.findClientResourceByClientId(realmResource(), "test-app").remove();
    }

    @Test
    public void testProtocolMappersRemoveBuiltins() throws Exception {
        setTrustedHost("localhost");
        // Change policy to allow hardcoded mapper
        ComponentRepresentation protocolMapperPolicyRep = findPolicyByProviderAndAuth(ProtocolMappersClientRegistrationPolicyFactory.PROVIDER_ID, getPolicyAnon());
        protocolMapperPolicyRep.getConfig().add(ALLOWED_PROTOCOL_MAPPER_TYPES, HardcodedRole.PROVIDER_ID);
        realmResource().components().component(protocolMapperPolicyRep.getId()).update(protocolMapperPolicyRep);
        // Create client with hardcoded mapper
        ClientRepresentation clientRep = createRep("test-app");
        clientRep.setProtocolMappers(Collections.singletonList(createHardcodedMapperRep()));
        ClientRepresentation registeredClient = reg.create(clientRep);
        org.keycloak.testsuite.Assert.assertEquals(1, registeredClient.getProtocolMappers().size());
        ProtocolMapperRepresentation hardcodedMapper = registeredClient.getProtocolMappers().get(0);
        // Revert
        ApiUtil.findClientResourceByClientId(realmResource(), "test-app").remove();
        protocolMapperPolicyRep.getConfig().remove(ALLOWED_PROTOCOL_MAPPER_TYPES, HardcodedRole.PROVIDER_ID);
        realmResource().components().component(protocolMapperPolicyRep.getId()).update(protocolMapperPolicyRep);
    }

    private enum ClientRegOp {

        CREATE,
        READ,
        UPDATE,
        DELETE;}
}

