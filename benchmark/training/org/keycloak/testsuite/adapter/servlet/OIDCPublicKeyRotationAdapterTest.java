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
package org.keycloak.testsuite.adapter.servlet;


import OAuth2Constants.REDIRECT_URI;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.common.util.Time;
import org.keycloak.constants.AdapterConstants;
import org.keycloak.protocol.oidc.OIDCAdvancedConfigWrapper;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.representations.adapters.action.GlobalRequestResult;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.adapter.page.CustomerDb;
import org.keycloak.testsuite.adapter.page.SecurePortal;
import org.keycloak.testsuite.adapter.page.TokenMinTTLPage;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.util.URLAssert;


/**
 * Tests related to public key rotation for OIDC adapter
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class OIDCPublicKeyRotationAdapterTest extends AbstractServletsAdapterTest {
    @Page
    private SecurePortal securePortal;

    @Page
    private TokenMinTTLPage tokenMinTTLPage;

    @Page
    private CustomerDb customerDb;

    @Test
    public void testRealmKeyRotationWithNewKeyDownload() throws Exception {
        // Login success first
        loginToTokenMinTtlApp();
        // Logout
        String logoutUri = OIDCLoginProtocolService.logoutUrl(authServerPage.createUriBuilder()).queryParam(REDIRECT_URI, tokenMinTTLPage.toString()).build("demo").toString();
        driver.navigate().to(logoutUri);
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        // Generate new realm key
        generateNewRealmKey();
        // Try to login again. It should fail now because not yet allowed to download new keys
        tokenMinTTLPage.navigateTo();
        Assert.assertTrue(testRealmLoginPage.form().isUsernamePresent());
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlStartsWith(tokenMinTTLPage.getInjectedUrl().toString());
        Assert.assertNull(tokenMinTTLPage.getAccessToken());
        driver.navigate().to(logoutUri);
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        setAdapterAndServerTimeOffset(300, ((tokenMinTTLPage.toString()) + "/unsecured/foo"));
        // Try to login. Should work now due to realm key change
        loginToTokenMinTtlApp();
        driver.navigate().to(logoutUri);
        // Revert public keys change
        resetKeycloakDeploymentForAdapter(((tokenMinTTLPage.toString()) + "/unsecured/foo"));
    }

    @Test
    public void testClientWithJwksUri() throws Exception {
        // Set client to bad JWKS URI
        ClientResource clientResource = ApiUtil.findClientResourceByClientId(testRealmResource(), "secure-portal");
        ClientRepresentation client = clientResource.toRepresentation();
        OIDCAdvancedConfigWrapper wrapper = OIDCAdvancedConfigWrapper.fromClientRepresentation(client);
        wrapper.setUseJwksUrl(true);
        wrapper.setJwksUrl(((securePortal) + "/bad-jwks-url"));
        clientResource.update(client);
        // Login should fail at the code-to-token
        securePortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        String pageSource = driver.getPageSource();
        URLAssert.assertCurrentUrlStartsWith(securePortal);
        Assert.assertFalse(((pageSource.contains("Bill Burke")) && (pageSource.contains("Stian Thorgersen"))));
        // Set client to correct JWKS URI
        client = clientResource.toRepresentation();
        wrapper = OIDCAdvancedConfigWrapper.fromClientRepresentation(client);
        wrapper.setUseJwksUrl(true);
        wrapper.setJwksUrl((((securePortal) + "/") + (AdapterConstants.K_JWKS)));
        clientResource.update(client);
        // Login to secure-portal should be fine now. Client keys downloaded from JWKS URI
        securePortal.navigateTo();
        URLAssert.assertCurrentUrlEquals(securePortal);
        pageSource = driver.getPageSource();
        Assert.assertTrue(((pageSource.contains("Bill Burke")) && (pageSource.contains("Stian Thorgersen"))));
        // Logout
        String logoutUri = OIDCLoginProtocolService.logoutUrl(authServerPage.createUriBuilder()).queryParam(REDIRECT_URI, securePortal.toString()).build("demo").toString();
        driver.navigate().to(logoutUri);
    }

    // KEYCLOAK-3824: Test for public-key-cache-ttl
    @Test
    public void testPublicKeyCacheTtl() {
        // increase accessTokenLifespan to 1200
        RealmRepresentation demoRealm = adminClient.realm(DEMO).toRepresentation();
        demoRealm.setAccessTokenLifespan(1200);
        adminClient.realm(DEMO).update(demoRealm);
        // authenticate in tokenMinTTL app
        loginToTokenMinTtlApp();
        String accessTokenString = tokenMinTTLPage.getAccessTokenString();
        // Send REST request to customer-db app. I should be successfully authenticated
        int status = invokeRESTEndpoint(accessTokenString);
        Assert.assertEquals(200, status);
        // Re-generate realm public key and remove the old key
        String oldActiveKeyProviderId = getActiveKeyProvider();
        generateNewRealmKey();
        adminClient.realm(DEMO).components().component(oldActiveKeyProviderId).remove();
        // Send REST request to the customer-db app. Should be still succcessfully authenticated as the JWKPublicKeyLocator cache is still valid
        status = invokeRESTEndpoint(accessTokenString);
        Assert.assertEquals(200, status);
        // TimeOffset to 900 on the REST app side. Token is still valid (1200) but JWKPublicKeyLocator should try to download new key (public-key-cache-ttl=600)
        setAdapterAndServerTimeOffset(900, ((customerDb.toString()) + "/unsecured/foo"));
        // Send REST request. New request to the publicKey cache should be sent, and key is no longer returned as token contains the old kid
        status = invokeRESTEndpoint(accessTokenString);
        Assert.assertEquals(401, status);
        // Revert public keys change and time offset
        resetKeycloakDeploymentForAdapter(((customerDb.toString()) + "/unsecured/foo"));
        resetKeycloakDeploymentForAdapter(((tokenMinTTLPage.toString()) + "/unsecured/foo"));
    }

    // KEYCLOAK-3823: Test that sending notBefore policy invalidates JWKPublicKeyLocator cache
    @Test
    public void testPublicKeyCacheInvalidatedWhenPushedNotBefore() {
        driver.manage().timeouts().pageLoadTimeout(1000, TimeUnit.SECONDS);
        String customerDBUnsecuredUrl = customerDb.getUriBuilder().clone().path("unsecured").path("foo").build().toASCIIString();
        String customerDBUrlNoTrailSlash = customerDb.getUriBuilder().build().toASCIIString();
        customerDBUrlNoTrailSlash = customerDBUrlNoTrailSlash.substring(0, ((customerDBUrlNoTrailSlash.length()) - 1));
        String tokenMinTTLUnsecuredUrl = tokenMinTTLPage.getUriBuilder().clone().path("unsecured").path("foo").build().toASCIIString();
        // increase accessTokenLifespan to 1200
        RealmRepresentation demoRealm = adminClient.realm(DEMO).toRepresentation();
        demoRealm.setAccessTokenLifespan(1200);
        adminClient.realm(DEMO).update(demoRealm);
        // authenticate in tokenMinTTL app
        loginToTokenMinTtlApp();
        String accessTokenString = tokenMinTTLPage.getAccessTokenString();
        // Generate new realm public key
        String oldActiveKeyProviderId = getActiveKeyProvider();
        generateNewRealmKey();
        // Send REST request to customer-db app. It should be successfully authenticated even that token is signed by the old key
        int status = invokeRESTEndpoint(accessTokenString);
        Assert.assertEquals(200, status);
        // Remove the old realm key now
        adminClient.realm(DEMO).components().component(oldActiveKeyProviderId).remove();
        // Set some offset to ensure pushing notBefore will pass
        setAdapterAndServerTimeOffset(130, customerDBUnsecuredUrl, tokenMinTTLUnsecuredUrl);
        // Send notBefore policy from the realm
        demoRealm.setNotBefore(((Time.currentTime()) - 1));
        adminClient.realm(DEMO).update(demoRealm);
        GlobalRequestResult result = adminClient.realm(DEMO).pushRevocation();
        Assert.assertTrue(result.getSuccessRequests().contains(customerDBUrlNoTrailSlash));
        // Send REST request. New request to the publicKey cache should be sent, and key is no longer returned as token contains the old kid
        status = invokeRESTEndpoint(accessTokenString);
        Assert.assertEquals(401, status);
        // Revert public keys change and time offset
        resetKeycloakDeploymentForAdapter(customerDBUnsecuredUrl);
        resetKeycloakDeploymentForAdapter(tokenMinTTLUnsecuredUrl);
    }
}

