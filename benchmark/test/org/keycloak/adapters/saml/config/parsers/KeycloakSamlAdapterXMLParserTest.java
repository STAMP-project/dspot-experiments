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
package org.keycloak.adapters.saml.config.parsers;


import Key.KeyStoreConfig;
import java.io.InputStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.keycloak.adapters.saml.config.IDP;
import org.keycloak.adapters.saml.config.Key;
import org.keycloak.adapters.saml.config.KeycloakSamlAdapter;
import org.keycloak.adapters.saml.config.SP;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.keycloak.saml.common.util.StaxParserUtil;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class KeycloakSamlAdapterXMLParserTest {
    private static final String CURRENT_XSD_LOCATION = "/schema/keycloak_saml_adapter_1_9.xsd";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testValidationSimpleFile() throws Exception {
        testValidationValid("keycloak-saml.xml");
    }

    @Test
    public void testValidationMultipleKeys() throws Exception {
        testValidationValid("keycloak-saml-multiple-signing-keys.xml");
    }

    @Test
    public void testValidationWithHttpClient() throws Exception {
        testValidationValid("keycloak-saml-wth-http-client-settings.xml");
    }

    @Test
    public void testValidationKeyInvalid() throws Exception {
        InputStream schemaIs = KeycloakSamlAdapterV1Parser.class.getResourceAsStream(KeycloakSamlAdapterXMLParserTest.CURRENT_XSD_LOCATION);
        InputStream is = getClass().getResourceAsStream("keycloak-saml-invalid.xml");
        Assert.assertNotNull(is);
        Assert.assertNotNull(schemaIs);
        expectedException.expect(ParsingException.class);
        StaxParserUtil.validate(is, schemaIs);
    }

    @Test
    public void testParseSimpleFileNoNamespace() throws Exception {
        KeycloakSamlAdapter config = parseKeycloakSamlAdapterConfig("keycloak-saml-no-namespace.xml", KeycloakSamlAdapter.class);
    }

    @Test
    public void testXmlParserBaseFile() throws Exception {
        KeycloakSamlAdapter config = parseKeycloakSamlAdapterConfig("keycloak-saml.xml", KeycloakSamlAdapter.class);
        Assert.assertNotNull(config);
        Assert.assertEquals(1, config.getSps().size());
        SP sp = config.getSps().get(0);
        Assert.assertEquals("sp", sp.getEntityID());
        Assert.assertEquals("EXTERNAL", sp.getSslPolicy());
        Assert.assertEquals("format", sp.getNameIDPolicyFormat());
        Assert.assertTrue(sp.isForceAuthentication());
        Assert.assertTrue(sp.isIsPassive());
        Assert.assertFalse(sp.isAutodetectBearerOnly());
        Assert.assertEquals(2, sp.getKeys().size());
        Key signing = sp.getKeys().get(0);
        Assert.assertTrue(signing.isSigning());
        Key.KeyStoreConfig keystore = signing.getKeystore();
        Assert.assertNotNull(keystore);
        Assert.assertEquals("file", keystore.getFile());
        Assert.assertEquals("cp", keystore.getResource());
        Assert.assertEquals("pw", keystore.getPassword());
        Assert.assertEquals("private alias", keystore.getPrivateKeyAlias());
        Assert.assertEquals("private pw", keystore.getPrivateKeyPassword());
        Assert.assertEquals("cert alias", keystore.getCertificateAlias());
        Key encryption = sp.getKeys().get(1);
        Assert.assertTrue(encryption.isEncryption());
        Assert.assertEquals("private pem", encryption.getPrivateKeyPem());
        Assert.assertEquals("public pem", encryption.getPublicKeyPem());
        Assert.assertEquals("FROM_ATTRIBUTE", sp.getPrincipalNameMapping().getPolicy());
        Assert.assertEquals("attribute", sp.getPrincipalNameMapping().getAttributeName());
        Assert.assertTrue(((sp.getRoleAttributes().size()) == 1));
        Assert.assertTrue(sp.getRoleAttributes().contains("member"));
        IDP idp = sp.getIdp();
        Assert.assertEquals("idp", idp.getEntityID());
        Assert.assertEquals("RSA_SHA256", idp.getSignatureAlgorithm());
        Assert.assertEquals("canon", idp.getSignatureCanonicalizationMethod());
        Assert.assertTrue(idp.getSingleSignOnService().isSignRequest());
        Assert.assertTrue(idp.getSingleSignOnService().isValidateResponseSignature());
        Assert.assertEquals("POST", idp.getSingleSignOnService().getRequestBinding());
        Assert.assertEquals("url", idp.getSingleSignOnService().getBindingUrl());
        Assert.assertFalse(idp.getSingleLogoutService().isSignRequest());
        Assert.assertTrue(idp.getSingleLogoutService().isSignResponse());
        Assert.assertTrue(idp.getSingleLogoutService().isValidateRequestSignature());
        Assert.assertTrue(idp.getSingleLogoutService().isValidateResponseSignature());
        Assert.assertEquals("REDIRECT", idp.getSingleLogoutService().getRequestBinding());
        Assert.assertEquals("POST", idp.getSingleLogoutService().getResponseBinding());
        Assert.assertEquals("posturl", idp.getSingleLogoutService().getPostBindingUrl());
        Assert.assertEquals("redirecturl", idp.getSingleLogoutService().getRedirectBindingUrl());
        Assert.assertTrue(((idp.getKeys().size()) == 1));
        Assert.assertTrue(idp.getKeys().get(0).isSigning());
        Assert.assertEquals("cert pem", idp.getKeys().get(0).getCertificatePem());
    }

    @Test
    public void testXmlParserMultipleSigningKeys() throws Exception {
        KeycloakSamlAdapter config = parseKeycloakSamlAdapterConfig("keycloak-saml-multiple-signing-keys.xml", KeycloakSamlAdapter.class);
        Assert.assertNotNull(config);
        Assert.assertEquals(1, config.getSps().size());
        SP sp = config.getSps().get(0);
        IDP idp = sp.getIdp();
        Assert.assertTrue(((idp.getKeys().size()) == 4));
        for (int i = 0; i < 4; i++) {
            Key key = idp.getKeys().get(i);
            Assert.assertTrue(key.isSigning());
            Assert.assertEquals(("cert pem " + i), idp.getKeys().get(i).getCertificatePem());
        }
    }

    @Test
    public void testXmlParserHttpClientSettings() throws Exception {
        KeycloakSamlAdapter config = parseKeycloakSamlAdapterConfig("keycloak-saml-wth-http-client-settings.xml", KeycloakSamlAdapter.class);
        Assert.assertNotNull(config);
        Assert.assertEquals(1, config.getSps().size());
        SP sp = config.getSps().get(0);
        IDP idp = sp.getIdp();
        Assert.assertThat(idp.getHttpClientConfig(), CoreMatchers.notNullValue());
        Assert.assertThat(idp.getHttpClientConfig().getClientKeystore(), CoreMatchers.is("ks"));
        Assert.assertThat(idp.getHttpClientConfig().getClientKeystorePassword(), CoreMatchers.is("ks-pwd"));
        Assert.assertThat(idp.getHttpClientConfig().getProxyUrl(), CoreMatchers.is("pu"));
        Assert.assertThat(idp.getHttpClientConfig().getTruststore(), CoreMatchers.is("ts"));
        Assert.assertThat(idp.getHttpClientConfig().getTruststorePassword(), CoreMatchers.is("tsp"));
        Assert.assertThat(idp.getHttpClientConfig().getConnectionPoolSize(), CoreMatchers.is(42));
        Assert.assertThat(idp.getHttpClientConfig().isAllowAnyHostname(), CoreMatchers.is(true));
        Assert.assertThat(idp.getHttpClientConfig().isDisableTrustManager(), CoreMatchers.is(true));
    }

    @Test
    public void testXmlParserSystemPropertiesNoPropertiesSet() throws Exception {
        KeycloakSamlAdapter config = parseKeycloakSamlAdapterConfig("keycloak-saml-properties.xml", KeycloakSamlAdapter.class);
        Assert.assertNotNull(config);
        Assert.assertThat(config.getSps(), Matchers.contains(CoreMatchers.instanceOf(SP.class)));
        SP sp = config.getSps().get(0);
        IDP idp = sp.getIdp();
        Assert.assertThat(sp.getEntityID(), CoreMatchers.is("sp"));
        Assert.assertThat(sp.getSslPolicy(), CoreMatchers.is("${keycloak-saml-properties.sslPolicy}"));
        Assert.assertThat(idp.isSignaturesRequired(), CoreMatchers.is(false));
        Assert.assertThat(idp.getSingleLogoutService().isSignRequest(), CoreMatchers.is(true));
        Assert.assertThat(idp.getSingleLogoutService().isSignResponse(), CoreMatchers.is(false));
        Assert.assertThat(idp.getSingleSignOnService().isSignRequest(), CoreMatchers.is(true));
        Assert.assertThat(idp.getSingleSignOnService().isValidateResponseSignature(), CoreMatchers.is(true));
        // These should take default from IDP.signaturesRequired
        Assert.assertThat(idp.getSingleLogoutService().isValidateRequestSignature(), CoreMatchers.is(false));
        Assert.assertThat(idp.getSingleLogoutService().isValidateResponseSignature(), CoreMatchers.is(false));
        Assert.assertThat(idp.getSingleSignOnService().isValidateAssertionSignature(), CoreMatchers.is(false));
    }

    @Test
    public void testXmlParserSystemPropertiesWithPropertiesSet() throws Exception {
        try {
            System.setProperty("keycloak-saml-properties.entityID", "meid");
            System.setProperty("keycloak-saml-properties.sslPolicy", "INTERNAL");
            System.setProperty("keycloak-saml-properties.signaturesRequired", "true");
            KeycloakSamlAdapter config = parseKeycloakSamlAdapterConfig("keycloak-saml-properties.xml", KeycloakSamlAdapter.class);
            Assert.assertNotNull(config);
            Assert.assertThat(config.getSps(), Matchers.contains(CoreMatchers.instanceOf(SP.class)));
            SP sp = config.getSps().get(0);
            IDP idp = sp.getIdp();
            Assert.assertThat(sp.getEntityID(), CoreMatchers.is("meid"));
            Assert.assertThat(sp.getSslPolicy(), CoreMatchers.is("INTERNAL"));
            Assert.assertThat(idp.isSignaturesRequired(), CoreMatchers.is(true));
            Assert.assertThat(idp.getSingleLogoutService().isSignRequest(), CoreMatchers.is(true));
            Assert.assertThat(idp.getSingleLogoutService().isSignResponse(), CoreMatchers.is(false));
            Assert.assertThat(idp.getSingleSignOnService().isSignRequest(), CoreMatchers.is(true));
            Assert.assertThat(idp.getSingleSignOnService().isValidateResponseSignature(), CoreMatchers.is(true));
            // These should take default from IDP.signaturesRequired
            Assert.assertThat(idp.getSingleLogoutService().isValidateRequestSignature(), CoreMatchers.is(true));
            Assert.assertThat(idp.getSingleLogoutService().isValidateResponseSignature(), CoreMatchers.is(true));
            // This is false by default
            Assert.assertThat(idp.getSingleSignOnService().isValidateAssertionSignature(), CoreMatchers.is(false));
        } finally {
            System.clearProperty("keycloak-saml-properties.entityID");
            System.clearProperty("keycloak-saml-properties.sslPolicy");
            System.clearProperty("keycloak-saml-properties.signaturesRequired");
        }
    }
}

