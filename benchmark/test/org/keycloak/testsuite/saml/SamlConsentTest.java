package org.keycloak.testsuite.saml;


import Binding.POST;
import SamlConfigAttributes.SAML_SERVER_SIGNATURE;
import SamlProtocol.SAML_ASSERTION_CONSUMER_URL_POST_ATTRIBUTE;
import SamlProtocol.SAML_IDP_INITIATED_SSO_URL_NAME;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.SamlClientBuilder;
import org.keycloak.testsuite.utils.io.IOUtil;


/**
 *
 *
 * @author mhajas
 */
public class SamlConsentTest extends AbstractSamlTest {
    @Test
    public void rejectedConsentResponseTest() throws ConfigurationException, ParsingException, ProcessingException {
        ClientRepresentation client = adminClient.realm(AbstractSamlTest.REALM_NAME).clients().findByClientId(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).get(0);
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(client.getId()).update(ClientBuilder.edit(client).consentRequired(true).attribute(SAML_IDP_INITIATED_SSO_URL_NAME, "sales-post").attribute(SAML_ASSERTION_CONSUMER_URL_POST_ATTRIBUTE, ((AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST) + "saml")).attribute(SAML_SERVER_SIGNATURE, "true").build());
        log.debug("Log in using idp initiated login");
        SAMLDocumentHolder documentHolder = new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, POST).build().login().user(bburkeUser).build().consentRequired().approveConsent(false).build().getSamlResponse(POST);
        final String samlDocumentString = IOUtil.documentToString(documentHolder.getSamlDocument());
        Assert.assertThat(samlDocumentString, CoreMatchers.containsString("<dsig:Signature"));// KEYCLOAK-4262

        Assert.assertThat(samlDocumentString, CoreMatchers.not(CoreMatchers.containsString("<samlp:LogoutResponse")));// KEYCLOAK-4261

        Assert.assertThat(samlDocumentString, CoreMatchers.containsString("<samlp:Response"));// KEYCLOAK-4261

        Assert.assertThat(samlDocumentString, CoreMatchers.containsString("<samlp:Status"));// KEYCLOAK-4181

        Assert.assertThat(samlDocumentString, CoreMatchers.containsString("<samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:RequestDenied\""));// KEYCLOAK-4181

    }
}

