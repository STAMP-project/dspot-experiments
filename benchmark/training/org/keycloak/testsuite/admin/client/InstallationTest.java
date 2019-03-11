/**
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.keycloak.testsuite.admin.client;


import OperationType.DELETE;
import ResourceType.CLIENT_SCOPE;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.AdminEventPaths;


/**
 * Test getting the installation/configuration files for OIDC and SAML.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class InstallationTest extends AbstractClientTest {
    private static final String OIDC_NAME = "oidcInstallationClient";

    private static final String OIDC_NAME_BEARER_ONLY_NAME = "oidcInstallationClientBearerOnly";

    private static final String OIDC_NAME_BEARER_ONLY_WITH_AUTHZ_NAME = "oidcInstallationClientBearerOnlyWithAuthz";

    private static final String SAML_NAME = "samlInstallationClient";

    private ClientResource oidcClient;

    private String oidcClientId;

    private ClientResource oidcBearerOnlyClient;

    private String oidcBearerOnlyClientId;

    private ClientResource oidcBearerOnlyClientWithAuthz;

    private String oidcBearerOnlyClientWithAuthzId;

    private ClientResource samlClient;

    private String samlClientId;

    @Test
    public void testOidcJBossXml() {
        String xml = oidcClient.getInstallationProvider("keycloak-oidc-jboss-subsystem");
        assertOidcInstallationConfig(xml);
        Assert.assertThat(xml, containsString("<secure-deployment"));
    }

    @Test
    public void testOidcJson() {
        String json = oidcClient.getInstallationProvider("keycloak-oidc-keycloak-json");
        assertOidcInstallationConfig(json);
    }

    @Test
    public void testOidcBearerOnlyJson() {
        String json = oidcBearerOnlyClient.getInstallationProvider("keycloak-oidc-keycloak-json");
        assertOidcInstallationConfig(json);
        Assert.assertThat(json, containsString("bearer-only"));
        Assert.assertThat(json, not(containsString("public-client")));
        Assert.assertThat(json, not(containsString("credentials")));
        Assert.assertThat(json, not(containsString("verify-token-audience")));
    }

    @Test
    public void testOidcBearerOnlyJsonWithAudienceClientScope() {
        // Generate audience client scope
        String clientScopeId = testingClient.testing().generateAudienceClientScope("test", InstallationTest.OIDC_NAME_BEARER_ONLY_NAME);
        String json = oidcBearerOnlyClient.getInstallationProvider("keycloak-oidc-keycloak-json");
        assertOidcInstallationConfig(json);
        Assert.assertThat(json, containsString("bearer-only"));
        Assert.assertThat(json, not(containsString("public-client")));
        Assert.assertThat(json, not(containsString("credentials")));
        Assert.assertThat(json, containsString("verify-token-audience"));
        // Remove clientScope
        testRealmResource().clientScopes().get(clientScopeId).remove();
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.clientScopeResourcePath(clientScopeId), null, CLIENT_SCOPE);
    }

    @Test
    public void testOidcBearerOnlyWithAuthzJson() {
        oidcBearerOnlyClientWithAuthzId = createOidcBearerOnlyClientWithAuthz(InstallationTest.OIDC_NAME_BEARER_ONLY_WITH_AUTHZ_NAME);
        oidcBearerOnlyClientWithAuthz = findClientResource(InstallationTest.OIDC_NAME_BEARER_ONLY_WITH_AUTHZ_NAME);
        String json = oidcBearerOnlyClientWithAuthz.getInstallationProvider("keycloak-oidc-keycloak-json");
        assertOidcInstallationConfig(json);
        Assert.assertThat(json, containsString("bearer-only"));
        Assert.assertThat(json, not(containsString("public-client")));
        Assert.assertThat(json, containsString("credentials"));
        Assert.assertThat(json, containsString("secret"));
        removeClient(oidcBearerOnlyClientWithAuthzId);
    }

    @Test
    public void testSamlMetadataIdpDescriptor() {
        String xml = samlClient.getInstallationProvider("saml-idp-descriptor");
        Assert.assertThat(xml, containsString("<EntityDescriptor"));
        Assert.assertThat(xml, containsString("<IDPSSODescriptor"));
        Assert.assertThat(xml, containsString(ApiUtil.findActiveKey(testRealmResource()).getCertificate()));
        Assert.assertThat(xml, containsString(samlUrl()));
    }

    @Test
    public void testSamlAdapterXml() {
        String xml = samlClient.getInstallationProvider("keycloak-saml");
        Assert.assertThat(xml, containsString("<keycloak-saml-adapter>"));
        Assert.assertThat(xml, containsString(InstallationTest.SAML_NAME));
        Assert.assertThat(xml, not(containsString(ApiUtil.findActiveKey(testRealmResource()).getCertificate())));
        Assert.assertThat(xml, containsString(samlUrl()));
    }

    @Test
    public void testSamlMetadataSpDescriptor() {
        String xml = samlClient.getInstallationProvider("saml-sp-descriptor");
        Assert.assertThat(xml, containsString("<EntityDescriptor"));
        Assert.assertThat(xml, containsString("<SPSSODescriptor"));
        Assert.assertThat(xml, containsString(InstallationTest.SAML_NAME));
    }

    @Test
    public void testSamlJBossXml() {
        String xml = samlClient.getInstallationProvider("keycloak-saml-subsystem");
        Assert.assertThat(xml, containsString("<secure-deployment"));
        Assert.assertThat(xml, containsString(InstallationTest.SAML_NAME));
        Assert.assertThat(xml, not(containsString(ApiUtil.findActiveKey(testRealmResource()).getCertificate())));
        Assert.assertThat(xml, containsString(samlUrl()));
    }
}

