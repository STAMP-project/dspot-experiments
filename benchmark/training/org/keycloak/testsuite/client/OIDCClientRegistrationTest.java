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


import Algorithm.ES256;
import Algorithm.RS256;
import Errors.INVALID_CLIENT;
import OAuth2Constants.AUTHORIZATION_CODE;
import OAuth2Constants.CODE;
import OAuth2Constants.IMPLICIT;
import OAuth2Constants.PASSWORD;
import OAuth2Constants.REFRESH_TOKEN;
import OIDCLoginProtocol.CLIENT_SECRET_BASIC;
import OIDCResponseType.ID_TOKEN;
import OIDCResponseType.NONE;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.protocol.oidc.OIDCAdvancedConfigWrapper;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.oidc.OIDCClientRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class OIDCClientRegistrationTest extends AbstractClientRegistrationTest {
    private static final String PRIVATE_KEY = "MIICXAIBAAKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQABAoGAfmO8gVhyBxdqlxmIuglbz8bcjQbhXJLR2EoS8ngTXmN1bo2L90M0mUKSdc7qF10LgETBzqL8jYlQIbt+e6TH8fcEpKCjUlyq0Mf/vVbfZSNaVycY13nTzo27iPyWQHK5NLuJzn1xvxxrUeXI6A2WFpGEBLbHjwpx5WQG9A+2scECQQDvdn9NE75HPTVPxBqsEd2z10TKkl9CZxu10Qby3iQQmWLEJ9LNmy3acvKrE3gMiYNWb6xHPKiIqOR1as7L24aTAkEAtyvQOlCvr5kAjVqrEKXalj0Tzewjweuxc0pskvArTI2Oo070h65GpoIKLc9jf+UA69cRtquwP93aZKtW06U8dQJAF2Y44ks/mK5+eyDqik3koCI08qaC8HYq2wVl7G2QkJ6sbAaILtcvD92ToOvyGyeE0flvmDZxMYlvaZnaQ0lcSQJBAKZU6umJi3/xeEbkJqMfeLclD27XGEFoPeNrmdx0q10Azp4NfJAY+Z8KRyQCR2BEG+oNitBOZ+YXF9KCpH3cdmECQHEigJhYg+ykOvr1aiZUMFT72HU0jnmQe2FVekuG+LJUt2Tm7GtMjTFoGpf0JwrVuZN39fOYAlo+nTixgeW7X8Y=";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQAB";

    // KEYCLOAK-3421
    @Test
    public void createClientWithUriFragment() {
        OIDCClientRepresentation client = createRep();
        client.setRedirectUris(Arrays.asList("http://localhost/auth", "http://localhost/auth#fragment", "http://localhost/auth*"));
        assertCreateFail(client, 400, "URI fragment");
    }

    @Test
    public void createClient() throws ClientRegistrationException {
        OIDCClientRepresentation response = create();
        Assert.assertNotNull(response.getRegistrationAccessToken());
        Assert.assertNotNull(response.getClientIdIssuedAt());
        Assert.assertNotNull(response.getClientId());
        Assert.assertNotNull(response.getClientSecret());
        Assert.assertEquals(0, response.getClientSecretExpiresAt().intValue());
        Assert.assertNotNull(response.getRegistrationClientUri());
        Assert.assertEquals("RegistrationAccessTokenTest", response.getClientName());
        Assert.assertEquals("http://root", response.getClientUri());
        Assert.assertEquals(1, response.getRedirectUris().size());
        Assert.assertEquals("http://redirect", response.getRedirectUris().get(0));
        Assert.assertEquals(Arrays.asList("code", "none"), response.getResponseTypes());
        Assert.assertEquals(Arrays.asList(AUTHORIZATION_CODE, REFRESH_TOKEN), response.getGrantTypes());
        Assert.assertEquals(CLIENT_SECRET_BASIC, response.getTokenEndpointAuthMethod());
        org.keycloak.testsuite.Assert.assertNull(response.getUserinfoSignedResponseAlg());
    }

    @Test
    public void getClient() throws ClientRegistrationException {
        OIDCClientRepresentation response = create();
        reg.auth(Auth.token(response));
        OIDCClientRepresentation rep = reg.oidc().get(response.getClientId());
        Assert.assertNotNull(rep);
        Assert.assertEquals(response.getRegistrationAccessToken(), rep.getRegistrationAccessToken());
        Assert.assertTrue(CollectionUtil.collectionEquals(Arrays.asList("code", "none"), response.getResponseTypes()));
        Assert.assertTrue(CollectionUtil.collectionEquals(Arrays.asList(AUTHORIZATION_CODE, REFRESH_TOKEN), response.getGrantTypes()));
        Assert.assertNotNull(response.getClientSecret());
        Assert.assertEquals(0, response.getClientSecretExpiresAt().intValue());
        Assert.assertEquals(CLIENT_SECRET_BASIC, response.getTokenEndpointAuthMethod());
    }

    @Test
    public void updateClient() throws ClientRegistrationException {
        OIDCClientRepresentation response = create();
        reg.auth(Auth.token(response));
        response.setRedirectUris(Collections.singletonList("http://newredirect"));
        response.setResponseTypes(Arrays.asList("code", "id_token token", "code id_token token"));
        response.setGrantTypes(Arrays.asList(AUTHORIZATION_CODE, REFRESH_TOKEN, PASSWORD));
        OIDCClientRepresentation updated = reg.oidc().update(response);
        Assert.assertTrue(CollectionUtil.collectionEquals(Collections.singletonList("http://newredirect"), updated.getRedirectUris()));
        Assert.assertTrue(CollectionUtil.collectionEquals(Arrays.asList(AUTHORIZATION_CODE, IMPLICIT, REFRESH_TOKEN, PASSWORD), updated.getGrantTypes()));
        Assert.assertTrue(CollectionUtil.collectionEquals(Arrays.asList(CODE, NONE, ID_TOKEN, "id_token token", "code id_token", "code token", "code id_token token"), updated.getResponseTypes()));
    }

    @Test
    public void updateClientError() throws ClientRegistrationException {
        try {
            OIDCClientRepresentation response = create();
            reg.auth(Auth.token(response));
            response.setResponseTypes(Arrays.asList("code", "tokenn"));
            reg.oidc().update(response);
            Assert.fail("Not expected to end with success");
        } catch (ClientRegistrationException cre) {
        }
    }

    @Test
    public void deleteClient() throws ClientRegistrationException {
        OIDCClientRepresentation response = create();
        reg.auth(Auth.token(response));
        reg.oidc().delete(response);
    }

    @Test
    public void testSignaturesRequired() throws Exception {
        OIDCClientRepresentation clientRep = createRep();
        clientRep.setUserinfoSignedResponseAlg(ES256.toString());
        clientRep.setRequestObjectSigningAlg(ES256.toString());
        OIDCClientRepresentation response = reg.oidc().create(clientRep);
        org.keycloak.testsuite.Assert.assertEquals(ES256.toString(), response.getUserinfoSignedResponseAlg());
        org.keycloak.testsuite.Assert.assertEquals(ES256.toString(), response.getRequestObjectSigningAlg());
        org.keycloak.testsuite.Assert.assertNotNull(response.getClientSecret());
        // Test Keycloak representation
        ClientRepresentation kcClient = getClient(response.getClientId());
        OIDCAdvancedConfigWrapper config = OIDCAdvancedConfigWrapper.fromClientRepresentation(kcClient);
        org.keycloak.testsuite.Assert.assertEquals(config.getUserInfoSignedResponseAlg(), ES256);
        org.keycloak.testsuite.Assert.assertEquals(config.getRequestObjectSignatureAlg(), ES256);
        // update (ES256 to RS256)
        clientRep.setUserinfoSignedResponseAlg(RS256.toString());
        clientRep.setRequestObjectSigningAlg(RS256.toString());
        response = reg.oidc().create(clientRep);
        org.keycloak.testsuite.Assert.assertEquals(RS256.toString(), response.getUserinfoSignedResponseAlg());
        org.keycloak.testsuite.Assert.assertEquals(RS256.toString(), response.getRequestObjectSigningAlg());
        // keycloak representation
        kcClient = getClient(response.getClientId());
        config = OIDCAdvancedConfigWrapper.fromClientRepresentation(kcClient);
        org.keycloak.testsuite.Assert.assertEquals(config.getUserInfoSignedResponseAlg(), RS256);
        org.keycloak.testsuite.Assert.assertEquals(config.getRequestObjectSignatureAlg(), RS256);
    }

    @Test
    public void createClientImplicitFlow() throws ClientRegistrationException {
        OIDCClientRepresentation clientRep = createRep();
        // create implicitFlow client and assert it's public client
        clientRep.setResponseTypes(Arrays.asList("id_token token"));
        OIDCClientRepresentation response = reg.oidc().create(clientRep);
        String clientId = response.getClientId();
        ClientRepresentation kcClientRep = getKeycloakClient(clientId);
        org.keycloak.testsuite.Assert.assertTrue(kcClientRep.isPublicClient());
        // Update client to hybrid and check it's not public client anymore
        reg.auth(Auth.token(response));
        response.setResponseTypes(Arrays.asList("id_token token", "code id_token", "code"));
        reg.oidc().update(response);
        kcClientRep = getKeycloakClient(clientId);
        org.keycloak.testsuite.Assert.assertFalse(kcClientRep.isPublicClient());
    }

    // KEYCLOAK-6771 Certificate Bound Token
    // https://tools.ietf.org/html/draft-ietf-oauth-mtls-08#section-6.5
    @Test
    public void testMtlsHoKTokenEnabled() throws Exception {
        // create (no specification)
        OIDCClientRepresentation clientRep = createRep();
        OIDCClientRepresentation response = reg.oidc().create(clientRep);
        org.keycloak.testsuite.Assert.assertEquals(Boolean.FALSE, response.getTlsClientCertificateBoundAccessTokens());
        org.keycloak.testsuite.Assert.assertNotNull(response.getClientSecret());
        // Test Keycloak representation
        ClientRepresentation kcClient = getClient(response.getClientId());
        OIDCAdvancedConfigWrapper config = OIDCAdvancedConfigWrapper.fromClientRepresentation(kcClient);
        Assert.assertTrue((!(config.isUseMtlsHokToken())));
        // update (true)
        reg.auth(Auth.token(response));
        response.setTlsClientCertificateBoundAccessTokens(Boolean.TRUE);
        OIDCClientRepresentation updated = reg.oidc().update(response);
        Assert.assertTrue(updated.getTlsClientCertificateBoundAccessTokens().booleanValue());
        // Test Keycloak representation
        kcClient = getClient(updated.getClientId());
        config = OIDCAdvancedConfigWrapper.fromClientRepresentation(kcClient);
        Assert.assertTrue(config.isUseMtlsHokToken());
        // update (false)
        reg.auth(Auth.token(updated));
        updated.setTlsClientCertificateBoundAccessTokens(Boolean.FALSE);
        OIDCClientRepresentation reUpdated = reg.oidc().update(updated);
        Assert.assertTrue((!(reUpdated.getTlsClientCertificateBoundAccessTokens().booleanValue())));
        // Test Keycloak representation
        kcClient = getClient(reUpdated.getClientId());
        config = OIDCAdvancedConfigWrapper.fromClientRepresentation(kcClient);
        Assert.assertTrue((!(config.isUseMtlsHokToken())));
    }

    @Test
    public void testOIDCEndpointCreateWithSamlClient() throws Exception {
        ClientsResource clientsResource = adminClient.realm(TEST).clients();
        ClientRepresentation samlClient = clientsResource.findByClientId("saml-client").get(0);
        String samlClientServiceId = clientsResource.get(samlClient.getId()).getServiceAccountUser().getId();
        String realmManagementId = clientsResource.findByClientId("realm-management").get(0).getId();
        RoleRepresentation role = clientsResource.get(realmManagementId).roles().get("create-client").toRepresentation();
        adminClient.realm(TEST).users().get(samlClientServiceId).roles().clientLevel(realmManagementId).add(Arrays.asList(role));
        String accessToken = oauth.clientId("saml-client").doClientCredentialsGrantAccessTokenRequest("secret").getAccessToken();
        reg.auth(Auth.token(accessToken));
        // change client to saml
        samlClient.setProtocol("saml");
        clientsResource.get(samlClient.getId()).update(samlClient);
        OIDCClientRepresentation client = createRep();
        assertCreateFail(client, 400, INVALID_CLIENT);
        // revert client
        samlClient.setProtocol("openid-connect");
        clientsResource.get(samlClient.getId()).update(samlClient);
    }

    @Test
    public void testOIDCEndpointGetWithSamlClient() {
        ClientsResource clientsResource = adminClient.realm(TEST).clients();
        ClientRepresentation samlClient = clientsResource.findByClientId("saml-client").get(0);
        reg.auth(Auth.client("saml-client", "secret"));
        // change client to saml
        samlClient.setProtocol("saml");
        clientsResource.get(samlClient.getId()).update(samlClient);
        assertGetFail(samlClient.getClientId(), 400, INVALID_CLIENT);
        // revert client
        samlClient.setProtocol("openid-connect");
        clientsResource.get(samlClient.getId()).update(samlClient);
    }
}

