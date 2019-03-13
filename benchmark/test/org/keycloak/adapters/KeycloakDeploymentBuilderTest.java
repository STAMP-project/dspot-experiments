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
package org.keycloak.adapters;


import ClientIdAndSecretCredentialsProvider.PROVIDER_ID;
import RelativeUrlsUsed.NEVER;
import SslRequired.EXTERNAL;
import TokenStore.COOKIE;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.adapters.rotation.HardcodedPublicKeyLocator;
import org.keycloak.adapters.rotation.JWKPublicKeyLocator;
import org.keycloak.common.util.PemUtils;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author <a href="mailto:brad.culley@spartasystems.com">Brad Culley</a>
 * @author <a href="mailto:john.ament@spartasystems.com">John D. Ament</a>
 */
public class KeycloakDeploymentBuilderTest {
    @Test
    public void load() throws Exception {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getClass().getResourceAsStream("/keycloak.json"));
        Assert.assertEquals("demo", deployment.getRealm());
        Assert.assertEquals("customer-portal", deployment.getResourceName());
        Assert.assertTrue(((deployment.getPublicKeyLocator()) instanceof HardcodedPublicKeyLocator));
        Assert.assertEquals(PemUtils.decodePublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQAB"), deployment.getPublicKeyLocator().getPublicKey(null, deployment));
        Assert.assertEquals("https://localhost:8443/auth/realms/demo/protocol/openid-connect/auth", deployment.getAuthUrl().build().toString());
        Assert.assertEquals(EXTERNAL, deployment.getSslRequired());
        Assert.assertTrue(deployment.isUseResourceRoleMappings());
        Assert.assertTrue(deployment.isCors());
        Assert.assertEquals(1000, deployment.getCorsMaxAge());
        Assert.assertEquals("POST, PUT, DELETE, GET", deployment.getCorsAllowedMethods());
        Assert.assertEquals("X-Custom, X-Custom2", deployment.getCorsAllowedHeaders());
        Assert.assertEquals("X-Custom3, X-Custom4", deployment.getCorsExposedHeaders());
        Assert.assertTrue(deployment.isBearerOnly());
        Assert.assertTrue(deployment.isPublicClient());
        Assert.assertTrue(deployment.isEnableBasicAuth());
        Assert.assertTrue(deployment.isExposeToken());
        Assert.assertFalse(deployment.isOAuthQueryParameterEnabled());
        Assert.assertEquals("234234-234234-234234", deployment.getResourceCredentials().get("secret"));
        Assert.assertEquals(PROVIDER_ID, deployment.getClientAuthenticator().getId());
        Assert.assertEquals(20, ((ThreadSafeClientConnManager) (deployment.getClient().getConnectionManager())).getMaxTotal());
        Assert.assertEquals("https://localhost:8443/auth/realms/demo/protocol/openid-connect/token", deployment.getTokenUrl());
        Assert.assertEquals(NEVER, deployment.getRelativeUrls());
        Assert.assertTrue(deployment.isAlwaysRefreshToken());
        Assert.assertTrue(deployment.isRegisterNodeAtStartup());
        Assert.assertEquals(1000, deployment.getRegisterNodePeriod());
        Assert.assertEquals(COOKIE, deployment.getTokenStore());
        Assert.assertEquals("email", deployment.getPrincipalAttribute());
        Assert.assertEquals(10, deployment.getTokenMinimumTimeToLive());
        Assert.assertEquals(20, deployment.getMinTimeBetweenJwksRequests());
        Assert.assertEquals(120, deployment.getPublicKeyCacheTtl());
        Assert.assertEquals("/api/$1", deployment.getRedirectRewriteRules().get("^/wsmaster/api/(.*)$"));
        Assert.assertTrue(deployment.isVerifyTokenAudience());
    }

    @Test
    public void loadNoClientCredentials() throws Exception {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getClass().getResourceAsStream("/keycloak-no-credentials.json"));
        Assert.assertEquals(PROVIDER_ID, deployment.getClientAuthenticator().getId());
        Assert.assertTrue(((deployment.getPublicKeyLocator()) instanceof JWKPublicKeyLocator));
        Assert.assertEquals(10, deployment.getMinTimeBetweenJwksRequests());
        Assert.assertEquals(86400, deployment.getPublicKeyCacheTtl());
    }

    @Test
    public void loadJwtCredentials() throws Exception {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getClass().getResourceAsStream("/keycloak-jwt.json"));
        Assert.assertEquals(JWTClientCredentialsProvider.PROVIDER_ID, deployment.getClientAuthenticator().getId());
    }

    @Test
    public void loadSecretJwtCredentials() throws Exception {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getClass().getResourceAsStream("/keycloak-secret-jwt.json"));
        Assert.assertEquals(JWTClientSecretCredentialsProvider.PROVIDER_ID, deployment.getClientAuthenticator().getId());
    }
}

