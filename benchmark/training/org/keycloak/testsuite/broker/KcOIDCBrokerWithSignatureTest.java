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
package org.keycloak.testsuite.broker;


import KeysMetadataRepresentation.KeyMetadataRepresentation;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.keys.PublicKeyStorageUtils;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.KeysMetadataRepresentation;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.client.resources.TestingCacheResource;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class KcOIDCBrokerWithSignatureTest extends AbstractBaseBrokerTest {
    @Test
    public void testSignatureVerificationJwksUrl() throws Exception {
        // Configure OIDC identity provider with JWKS URL
        updateIdentityProviderWithJwksUrl();
        // Check that user is able to login
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        // Rotate public keys on the parent broker
        rotateKeys();
        // User not able to login now as new keys can't be yet downloaded (10s timeout)
        logInAsUserInIDP();
        assertErrorPage("Unexpected error when authenticating with identity provider");
        logoutFromRealm(bc.consumerRealmName());
        // Set time offset. New keys can be downloaded. Check that user is able to login.
        setTimeOffset(20);
        logInAsUserInIDP();
        assertLoggedInAccountManagement();
    }

    @Test
    public void testSignatureVerificationHardcodedPublicKey() throws Exception {
        // Configure OIDC identity provider with JWKS URL
        IdentityProviderRepresentation idpRep = getIdentityProvider();
        OIDCIdentityProviderConfigRep cfg = new OIDCIdentityProviderConfigRep(idpRep);
        setValidateSignature(true);
        setUseJwksUrl(false);
        KeysMetadataRepresentation.KeyMetadataRepresentation key = ApiUtil.findActiveKey(providerRealm());
        cfg.setPublicKeySignatureVerifier(key.getPublicKey());
        updateIdentityProvider(idpRep);
        // Check that user is able to login
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        // Rotate public keys on the parent broker
        rotateKeys();
        // User not able to login now as new keys can't be yet downloaded (10s timeout)
        logInAsUserInIDP();
        assertErrorPage("Unexpected error when authenticating with identity provider");
        logoutFromRealm(bc.consumerRealmName());
        // Even after time offset is user not able to login, because it uses old key hardcoded in identityProvider config
        setTimeOffset(20);
        logInAsUserInIDP();
        assertErrorPage("Unexpected error when authenticating with identity provider");
    }

    @Test
    public void testSignatureVerificationHardcodedPublicKeyWithKeyIdSetExplicitly() throws Exception {
        // Configure OIDC identity provider with JWKS URL
        IdentityProviderRepresentation idpRep = getIdentityProvider();
        OIDCIdentityProviderConfigRep cfg = new OIDCIdentityProviderConfigRep(idpRep);
        setValidateSignature(true);
        setUseJwksUrl(false);
        KeysMetadataRepresentation.KeyMetadataRepresentation key = ApiUtil.findActiveKey(providerRealm());
        String pemData = key.getPublicKey();
        setPublicKeySignatureVerifier(pemData);
        String expectedKeyId = KeyUtils.createKeyId(PemUtils.decodePublicKey(pemData));
        updateIdentityProvider(idpRep);
        // Check that user is able to login
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        // Set key id to an invalid one
        cfg.setPublicKeySignatureVerifierKeyId("invalid-key-id");
        updateIdentityProvider(idpRep);
        logInAsUserInIDP();
        assertErrorPage("Unexpected error when authenticating with identity provider");
        // Set key id to a valid one
        cfg.setPublicKeySignatureVerifierKeyId(expectedKeyId);
        updateIdentityProvider(idpRep);
        logInAsUserInIDP();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        // Set key id to empty
        cfg.setPublicKeySignatureVerifierKeyId("");
        updateIdentityProvider(idpRep);
        logInAsUserInIDP();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        // Unset key id
        setPublicKeySignatureVerifierKeyId(null);
        updateIdentityProvider(idpRep);
        logInAsUserInIDP();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
    }

    @Test
    public void testClearKeysCache() throws Exception {
        // Configure OIDC identity provider with JWKS URL
        updateIdentityProviderWithJwksUrl();
        // Check that user is able to login
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        // Check that key is cached
        IdentityProviderRepresentation idpRep = getIdentityProvider();
        String expectedCacheKey = PublicKeyStorageUtils.getIdpModelCacheKey(consumerRealm().toRepresentation().getId(), idpRep.getInternalId());
        TestingCacheResource cache = testingClient.testing(bc.consumerRealmName()).cache(InfinispanConnectionProvider.KEYS_CACHE_NAME);
        org.keycloak.testsuite.Assert.assertTrue(cache.contains(expectedCacheKey));
        // Clear cache and check nothing cached
        consumerRealm().clearKeysCache();
        org.keycloak.testsuite.Assert.assertFalse(cache.contains(expectedCacheKey));
        org.keycloak.testsuite.Assert.assertEquals(cache.size(), 0);
    }

    // Test that when I update identityProvier, then the record in publicKey cache is cleared and it's not possible to authenticate with it anymore
    @Test
    public void testPublicKeyCacheInvalidatedWhenProviderUpdated() throws Exception {
        // Configure OIDC identity provider with JWKS URL
        updateIdentityProviderWithJwksUrl();
        // Check that user is able to login
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        // Check that key is cached
        IdentityProviderRepresentation idpRep = getIdentityProvider();
        String expectedCacheKey = PublicKeyStorageUtils.getIdpModelCacheKey(consumerRealm().toRepresentation().getId(), idpRep.getInternalId());
        TestingCacheResource cache = testingClient.testing(bc.consumerRealmName()).cache(InfinispanConnectionProvider.KEYS_CACHE_NAME);
        org.keycloak.testsuite.Assert.assertTrue(cache.contains(expectedCacheKey));
        // Update identityProvider to some bad JWKS_URL
        OIDCIdentityProviderConfigRep cfg = new OIDCIdentityProviderConfigRep(idpRep);
        setJwksUrl("http://localhost:43214/non-existent");
        updateIdentityProvider(idpRep);
        // Check that key is not cached anymore
        org.keycloak.testsuite.Assert.assertFalse(cache.contains(expectedCacheKey));
        // Check that user is not able to login with IDP
        setTimeOffset(20);
        logInAsUserInIDP();
        assertErrorPage("Unexpected error when authenticating with identity provider");
    }
}

