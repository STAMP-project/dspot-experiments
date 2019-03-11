package org.keycloak.testsuite.broker;


import Algorithm.RS256;
import Binding.POST;
import SAMLIdentityProviderConfig.SIGNING_CERTIFICATE_KEY;
import SAMLIdentityProviderConfig.VALIDATE_SIGNATURE;
import SAMLIdentityProviderConfig.WANT_ASSERTIONS_ENCRYPTED;
import SAMLIdentityProviderConfig.WANT_ASSERTIONS_SIGNED;
import SAMLIdentityProviderConfig.WANT_AUTHN_REQUESTS_SIGNED;
import SamlConfigAttributes.SAML_ASSERTION_SIGNATURE;
import SamlConfigAttributes.SAML_CLIENT_SIGNATURE_ATTRIBUTE;
import SamlConfigAttributes.SAML_ENCRYPT;
import SamlConfigAttributes.SAML_SERVER_SIGNATURE;
import SamlConfigAttributes.SAML_SIGNATURE_ALGORITHM;
import SamlConfigAttributes.SAML_SIGNING_CERTIFICATE_ATTRIBUTE;
import Status.BAD_REQUEST;
import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.org.keycloak.testsuite.util.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.saml.processing.api.saml.v2.request.SAML2Request;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.arquillian.SuiteContext;
import org.keycloak.testsuite.saml.AbstractSamlTest;
import org.keycloak.testsuite.updaters.ClientAttributeUpdater;
import org.keycloak.testsuite.util.KeyUtils;
import org.keycloak.testsuite.util.SamlClient;
import org.keycloak.testsuite.util.SamlClientBuilder;
import org.w3c.dom.Document;

import static org.keycloak.testsuite.util.Matchers.isSamlResponse;


public class KcSamlSignedBrokerTest extends KcSamlBrokerTest {
    public class KcSamlSignedBrokerConfiguration extends KcSamlBrokerConfiguration {
        @Override
        public RealmRepresentation createProviderRealm() {
            RealmRepresentation realm = super.createProviderRealm();
            realm.setPublicKey(REALM_PUBLIC_KEY);
            realm.setPrivateKey(REALM_PRIVATE_KEY);
            return realm;
        }

        @Override
        public RealmRepresentation createConsumerRealm() {
            RealmRepresentation realm = super.createConsumerRealm();
            realm.setPublicKey(REALM_PUBLIC_KEY);
            realm.setPrivateKey(REALM_PRIVATE_KEY);
            return realm;
        }

        @Override
        public List<ClientRepresentation> createProviderClients(SuiteContext suiteContext) {
            List<ClientRepresentation> clientRepresentationList = super.createProviderClients(suiteContext);
            String consumerCert = KeyUtils.getActiveKey(adminClient.realm(consumerRealmName()).keys().getKeyMetadata(), RS256).getCertificate();
            Assert.assertThat(consumerCert, Matchers.notNullValue());
            for (ClientRepresentation client : clientRepresentationList) {
                client.setClientAuthenticatorType("client-secret");
                client.setSurrogateAuthRequired(false);
                Map<String, String> attributes = client.getAttributes();
                if (attributes == null) {
                    attributes = new HashMap<>();
                    client.setAttributes(attributes);
                }
                attributes.put(SAML_ASSERTION_SIGNATURE, "true");
                attributes.put(SAML_SERVER_SIGNATURE, "true");
                attributes.put(SAML_CLIENT_SIGNATURE_ATTRIBUTE, "true");
                attributes.put(SAML_SIGNATURE_ALGORITHM, "RSA_SHA256");
                attributes.put(SAML_SIGNING_CERTIFICATE_ATTRIBUTE, consumerCert);
            }
            return clientRepresentationList;
        }

        @Override
        public IdentityProviderRepresentation setUpIdentityProvider(SuiteContext suiteContext) {
            IdentityProviderRepresentation result = super.setUpIdentityProvider(suiteContext);
            String providerCert = KeyUtils.getActiveKey(adminClient.realm(providerRealmName()).keys().getKeyMetadata(), RS256).getCertificate();
            Assert.assertThat(providerCert, Matchers.notNullValue());
            Map<String, String> config = result.getConfig();
            config.put(VALIDATE_SIGNATURE, "true");
            config.put(WANT_ASSERTIONS_SIGNED, "true");
            config.put(WANT_AUTHN_REQUESTS_SIGNED, "true");
            config.put(SIGNING_CERTIFICATE_KEY, providerCert);
            return result;
        }
    }

    @Test
    public void testSignedEncryptedAssertions() throws Exception {
        withSignedEncryptedAssertions(this::testAssertionSignatureRespected, true, true);
    }

    @Test
    public void testSignedAssertion() throws Exception {
        withSignedEncryptedAssertions(this::testAssertionSignatureRespected, true, false);
    }

    // KEYCLOAK-5581
    @Test
    public void loginUserAllNamespacesInTopElement() {
        AuthnRequestType loginRep = SamlClient.createLoginRequestDocument(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, ((((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://localhost:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)) + "/sales-post/saml"), null);
        Document doc;
        try {
            doc = extractNamespacesToTopLevelElement(SAML2Request.convert(loginRep));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        SAMLDocumentHolder samlResponse = // first-broker flow
        // Response from producer IdP
        // AuthnRequest to producer IdP
        // Request to consumer IdP
        new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(bc.consumerRealmName()), doc, POST).build().login().idp(bc.getIDPAlias()).build().processSamlResponse(POST).targetAttributeSamlRequest().transformDocument(this::extractNamespacesToTopLevelElement).build().login().user(bc.getUserLogin(), bc.getUserPassword()).build().processSamlResponse(POST).transformDocument(this::extractNamespacesToTopLevelElement).build().updateProfile().firstName("a").lastName("b").email(bc.getUserEmail()).username(bc.getUserLogin()).build().followOneRedirect().getSamlResponse(POST);// Response from consumer IdP

        Assert.assertThat(samlResponse, Matchers.notNullValue());
        Assert.assertThat(samlResponse.getSamlObject(), isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
    }

    @Test
    public void loginUserAllNamespacesInTopElementSignedEncryptedAssertion() throws Exception {
        withSignedEncryptedAssertions(this::loginUserAllNamespacesInTopElement, true, true);
    }

    @Test
    public void loginUserAllNamespacesInTopElementSignedAssertion() throws Exception {
        withSignedEncryptedAssertions(this::loginUserAllNamespacesInTopElement, true, false);
    }

    @Test
    public void loginUserAllNamespacesInTopElementEncryptedAssertion() throws Exception {
        withSignedEncryptedAssertions(this::loginUserAllNamespacesInTopElement, false, true);
    }

    @Test
    public void testWithExpiredBrokerCertificate() throws Exception {
        try (Closeable idpUpdater = new org.keycloak.testsuite.updaters.IdentityProviderAttributeUpdater(identityProviderResource).setAttribute(VALIDATE_SIGNATURE, Boolean.toString(true)).setAttribute(WANT_ASSERTIONS_SIGNED, Boolean.toString(true)).setAttribute(WANT_ASSERTIONS_ENCRYPTED, Boolean.toString(false)).setAttribute(WANT_AUTHN_REQUESTS_SIGNED, "true").setAttribute(SIGNING_CERTIFICATE_KEY, AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_EXPIRED_CERTIFICATE).update();Closeable clientUpdater = ClientAttributeUpdater.forClient(adminClient, bc.providerRealmName(), bc.getIDPClientIdInProviderRealm(suiteContext)).setAttribute(SAML_ENCRYPT, Boolean.toString(false)).setAttribute(SAML_SERVER_SIGNATURE, "true").setAttribute(SAML_ASSERTION_SIGNATURE, Boolean.toString(true)).setAttribute(SAML_CLIENT_SIGNATURE_ATTRIBUTE, "false").update();Closeable realmUpdater = setPublicKey(AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_EXPIRED_PUBLIC_KEY).setPrivateKey(AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_EXPIRED_PRIVATE_KEY).update()) {
            AuthnRequestType loginRep = SamlClient.createLoginRequestDocument(((AbstractSamlTest.SAML_CLIENT_ID_SALES_POST) + ".dot/ted"), AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, null);
            Document doc = SAML2Request.convert(loginRep);
            // Request to consumer IdP
            new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(bc.consumerRealmName()), doc, POST).build().login().idp(bc.getIDPAlias()).build().assertResponse(org.keycloak.testsuite.util.Matchers.statusCodeIsHC(BAD_REQUEST));
        }
    }
}

