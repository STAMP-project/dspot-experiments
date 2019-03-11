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


import InfinispanConnectionProvider.KEYS_CACHE_NAME;
import OAuth2Constants.CLIENT_CREDENTIALS;
import OIDCLoginProtocol.PRIVATE_KEY_JWT;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.client.registration.Auth;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.jose.jwk.JWKBuilder;
import org.keycloak.keys.PublicKeyStorageUtils;
import org.keycloak.representations.oidc.OIDCClientRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.client.resources.TestApplicationResourceUrls;
import org.keycloak.testsuite.client.resources.TestOIDCEndpointsApplicationResource;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCJwksClientRegistrationTest extends AbstractClientRegistrationTest {
    private static final String PRIVATE_KEY = "MIICXAIBAAKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQABAoGAfmO8gVhyBxdqlxmIuglbz8bcjQbhXJLR2EoS8ngTXmN1bo2L90M0mUKSdc7qF10LgETBzqL8jYlQIbt+e6TH8fcEpKCjUlyq0Mf/vVbfZSNaVycY13nTzo27iPyWQHK5NLuJzn1xvxxrUeXI6A2WFpGEBLbHjwpx5WQG9A+2scECQQDvdn9NE75HPTVPxBqsEd2z10TKkl9CZxu10Qby3iQQmWLEJ9LNmy3acvKrE3gMiYNWb6xHPKiIqOR1as7L24aTAkEAtyvQOlCvr5kAjVqrEKXalj0Tzewjweuxc0pskvArTI2Oo070h65GpoIKLc9jf+UA69cRtquwP93aZKtW06U8dQJAF2Y44ks/mK5+eyDqik3koCI08qaC8HYq2wVl7G2QkJ6sbAaILtcvD92ToOvyGyeE0flvmDZxMYlvaZnaQ0lcSQJBAKZU6umJi3/xeEbkJqMfeLclD27XGEFoPeNrmdx0q10Azp4NfJAY+Z8KRyQCR2BEG+oNitBOZ+YXF9KCpH3cdmECQHEigJhYg+ykOvr1aiZUMFT72HU0jnmQe2FVekuG+LJUt2Tm7GtMjTFoGpf0JwrVuZN39fOYAlo+nTixgeW7X8Y=";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQAB";

    @Test
    public void createClientWithJWKS_generatedKid() throws Exception {
        OIDCClientRepresentation clientRep = createRep();
        clientRep.setGrantTypes(Collections.singletonList(CLIENT_CREDENTIALS));
        clientRep.setTokenEndpointAuthMethod(PRIVATE_KEY_JWT);
        // Generate keys for client
        TestOIDCEndpointsApplicationResource oidcClientEndpointsResource = testingClient.testApp().oidcClientEndpoints();
        Map<String, String> generatedKeys = oidcClientEndpointsResource.generateKeys("RS256");
        JSONWebKeySet keySet = oidcClientEndpointsResource.getJwks();
        clientRep.setJwks(keySet);
        OIDCClientRepresentation response = reg.oidc().create(clientRep);
        org.keycloak.testsuite.Assert.assertEquals(PRIVATE_KEY_JWT, response.getTokenEndpointAuthMethod());
        org.keycloak.testsuite.Assert.assertNull(response.getClientSecret());
        org.keycloak.testsuite.Assert.assertNull(response.getClientSecretExpiresAt());
        // Tries to authenticate client with privateKey JWT
        assertAuthenticateClientSuccess(generatedKeys, response, OIDCJwksClientRegistrationTest.KEEP_GENERATED_KID);
    }

    // The "kid" is null in the signed JWT. This is backwards compatibility test as in versions prior to 2.3.0, the "kid" wasn't set by JWTClientCredentialsProvider
    @Test
    public void createClientWithJWKS_nullKid() throws Exception {
        OIDCClientRepresentation clientRep = createRep();
        clientRep.setGrantTypes(Collections.singletonList(CLIENT_CREDENTIALS));
        clientRep.setTokenEndpointAuthMethod(PRIVATE_KEY_JWT);
        // Generate keys for client
        TestOIDCEndpointsApplicationResource oidcClientEndpointsResource = testingClient.testApp().oidcClientEndpoints();
        Map<String, String> generatedKeys = oidcClientEndpointsResource.generateKeys("RS256");
        JSONWebKeySet keySet = oidcClientEndpointsResource.getJwks();
        clientRep.setJwks(keySet);
        OIDCClientRepresentation response = reg.oidc().create(clientRep);
        // Tries to authenticate client with privateKey JWT
        assertAuthenticateClientSuccess(generatedKeys, response, null);
    }

    // The "kid" is set manually to some custom value
    @Test
    public void createClientWithJWKS_customKid() throws Exception {
        OIDCClientRepresentation response = createClientWithManuallySetKid("a1");
        Map<String, String> generatedKeys = testingClient.testApp().oidcClientEndpoints().getKeysAsPem();
        // Tries to authenticate client with privateKey JWT
        assertAuthenticateClientSuccess(generatedKeys, response, "a1");
    }

    @Test
    public void testTwoClientsWithSameKid() throws Exception {
        // Create client with manually set "kid"
        OIDCClientRepresentation response = createClientWithManuallySetKid("a1");
        // Create client2
        OIDCClientRepresentation clientRep2 = createRep();
        clientRep2.setGrantTypes(Collections.singletonList(CLIENT_CREDENTIALS));
        clientRep2.setTokenEndpointAuthMethod(PRIVATE_KEY_JWT);
        // Generate some random keys for client2
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        PublicKey client2PublicKey = generator.generateKeyPair().getPublic();
        // Set client2 with manually set "kid" to be same like kid of client1 (but keys for both clients are different)
        JSONWebKeySet keySet = new JSONWebKeySet();
        keySet.setKeys(new JWK[]{ JWKBuilder.create().kid("a1").rs256(client2PublicKey) });
        clientRep2.setJwks(keySet);
        clientRep2 = reg.oidc().create(clientRep2);
        // Authenticate client1
        Map<String, String> generatedKeys = testingClient.testApp().oidcClientEndpoints().getKeysAsPem();
        assertAuthenticateClientSuccess(generatedKeys, response, "a1");
        // Assert item in publicKey cache for client1
        String expectedCacheKey = PublicKeyStorageUtils.getClientModelCacheKey(AbstractClientRegistrationTest.REALM_NAME, response.getClientId());
        org.keycloak.testsuite.Assert.assertTrue(testingClient.testing().cache(KEYS_CACHE_NAME).contains(expectedCacheKey));
        // Assert it's not possible to authenticate as client2 with the same "kid" like client1
        assertAuthenticateClientError(generatedKeys, clientRep2, "a1");
    }

    @Test
    public void testPublicKeyCacheInvalidatedWhenUpdatingClient() throws Exception {
        OIDCClientRepresentation response = createClientWithManuallySetKid("a1");
        Map<String, String> generatedKeys = testingClient.testApp().oidcClientEndpoints().getKeysAsPem();
        // Tries to authenticate client with privateKey JWT
        assertAuthenticateClientSuccess(generatedKeys, response, "a1");
        // Assert item in publicKey cache for client1
        String expectedCacheKey = PublicKeyStorageUtils.getClientModelCacheKey(AbstractClientRegistrationTest.REALM_NAME, response.getClientId());
        org.keycloak.testsuite.Assert.assertTrue(testingClient.testing().cache(KEYS_CACHE_NAME).contains(expectedCacheKey));
        // Update client with some bad JWKS_URI
        response.setJwksUri("http://localhost:4321/non-existent");
        reg.auth(Auth.token(response.getRegistrationAccessToken())).oidc().update(response);
        // Assert item not any longer for client1
        org.keycloak.testsuite.Assert.assertFalse(testingClient.testing().cache(KEYS_CACHE_NAME).contains(expectedCacheKey));
        // Assert it's not possible to authenticate as client1
        assertAuthenticateClientError(generatedKeys, response, "a1");
    }

    @Test
    public void createClientWithJWKSURI() throws Exception {
        OIDCClientRepresentation clientRep = createRep();
        clientRep.setGrantTypes(Collections.singletonList(CLIENT_CREDENTIALS));
        clientRep.setTokenEndpointAuthMethod(PRIVATE_KEY_JWT);
        // Generate keys for client
        TestOIDCEndpointsApplicationResource oidcClientEndpointsResource = testingClient.testApp().oidcClientEndpoints();
        Map<String, String> generatedKeys = oidcClientEndpointsResource.generateKeys("RS256");
        clientRep.setJwksUri(TestApplicationResourceUrls.clientJwksUri());
        OIDCClientRepresentation response = reg.oidc().create(clientRep);
        org.keycloak.testsuite.Assert.assertEquals(PRIVATE_KEY_JWT, response.getTokenEndpointAuthMethod());
        org.keycloak.testsuite.Assert.assertNull(response.getClientSecret());
        org.keycloak.testsuite.Assert.assertNull(response.getClientSecretExpiresAt());
        org.keycloak.testsuite.Assert.assertEquals(response.getJwksUri(), TestApplicationResourceUrls.clientJwksUri());
        // Tries to authenticate client with privateKey JWT
        assertAuthenticateClientSuccess(generatedKeys, response, OIDCJwksClientRegistrationTest.KEEP_GENERATED_KID);
    }

    @Test
    public void createClientWithJWKSURI_rotateClientKeys() throws Exception {
        OIDCClientRepresentation clientRep = createRep();
        clientRep.setGrantTypes(Collections.singletonList(CLIENT_CREDENTIALS));
        clientRep.setTokenEndpointAuthMethod(PRIVATE_KEY_JWT);
        // Generate keys for client
        TestOIDCEndpointsApplicationResource oidcClientEndpointsResource = testingClient.testApp().oidcClientEndpoints();
        Map<String, String> generatedKeys = oidcClientEndpointsResource.generateKeys("RS256");
        clientRep.setJwksUri(TestApplicationResourceUrls.clientJwksUri());
        OIDCClientRepresentation response = reg.oidc().create(clientRep);
        org.keycloak.testsuite.Assert.assertEquals(PRIVATE_KEY_JWT, response.getTokenEndpointAuthMethod());
        org.keycloak.testsuite.Assert.assertNull(response.getClientSecret());
        org.keycloak.testsuite.Assert.assertNull(response.getClientSecretExpiresAt());
        org.keycloak.testsuite.Assert.assertEquals(response.getJwksUri(), TestApplicationResourceUrls.clientJwksUri());
        // Tries to authenticate client with privateKey JWT
        assertAuthenticateClientSuccess(generatedKeys, response, OIDCJwksClientRegistrationTest.KEEP_GENERATED_KID);
        // Add new key to the jwks
        Map<String, String> generatedKeys2 = oidcClientEndpointsResource.generateKeys("RS256");
        // Error should happen. KeyStorageProvider won't yet download new keys because of timeout
        assertAuthenticateClientError(generatedKeys2, response, OIDCJwksClientRegistrationTest.KEEP_GENERATED_KID);
        setTimeOffset(20);
        // Now new keys should be successfully downloaded
        assertAuthenticateClientSuccess(generatedKeys2, response, OIDCJwksClientRegistrationTest.KEEP_GENERATED_KID);
    }

    private static final String KEEP_GENERATED_KID = "KEEP_GENERATED_KID";
}

