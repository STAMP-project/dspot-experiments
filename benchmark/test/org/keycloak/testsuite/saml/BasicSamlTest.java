package org.keycloak.testsuite.saml;


import Binding.POST;
import Binding.REDIRECT;
import Response.Status.OK;
import Status.BAD_REQUEST;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.processing.api.saml.v2.request.SAML2Request;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.SamlClient;
import org.keycloak.testsuite.util.SamlClient.RedirectStrategyWithSwitchableFollowRedirect;
import org.keycloak.testsuite.util.SamlClientBuilder;
import org.w3c.dom.Document;

import static org.keycloak.testsuite.util.Matchers.statusCodeIsHC;


/**
 *
 *
 * @author mhajas
 */
public class BasicSamlTest extends AbstractSamlTest {
    // KEYCLOAK-4160
    @Test
    public void testPropertyValueInAssertion() throws ConfigurationException, ParsingException, ProcessingException {
        SAMLDocumentHolder document = new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, POST).transformDocument(( doc) -> {
            setDocElementAttributeValue(doc, "samlp:AuthnRequest", "ID", "${java.version}");
            return doc;
        }).build().login().user(bburkeUser).build().getSamlResponse(POST);
        Assert.assertThat(documentToString(document.getSamlDocument()), CoreMatchers.not(Matchers.containsString((("InResponseTo=\"" + (System.getProperty("java.version"))) + "\""))));
    }

    @Test
    public void testRedirectUrlSigned() throws Exception {
        testSpecialCharsInRelayState(null);
    }

    @Test
    public void testRedirectUrlUnencodedSpecialChars() throws Exception {
        testSpecialCharsInRelayState("New%20Document%20(1).doc");
    }

    @Test
    public void testRedirectUrlEncodedSpecialChars() throws Exception {
        testSpecialCharsInRelayState("New%20Document%20%281%29.doc");
    }

    @Test
    public void testNoDestinationPost() throws Exception {
        AuthnRequestType loginRep = SamlClient.createLoginRequestDocument(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, null);
        Document doc = SAML2Request.convert(loginRep);
        HttpUriRequest post = POST.createSamlUnsignedRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), null, doc);
        try (CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new RedirectStrategyWithSwitchableFollowRedirect()).build();CloseableHttpResponse response = client.execute(post)) {
            Assert.assertThat(response, statusCodeIsHC(OK));
            Assert.assertThat(EntityUtils.toString(response.getEntity(), "UTF-8"), Matchers.containsString("login"));
        }
    }

    @Test
    public void testNoDestinationRedirect() throws Exception {
        AuthnRequestType loginRep = SamlClient.createLoginRequestDocument(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, null);
        Document doc = SAML2Request.convert(loginRep);
        HttpUriRequest post = REDIRECT.createSamlUnsignedRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), null, doc);
        try (CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new RedirectStrategyWithSwitchableFollowRedirect()).build();CloseableHttpResponse response = client.execute(post)) {
            Assert.assertThat(response, statusCodeIsHC(OK));
            Assert.assertThat(EntityUtils.toString(response.getEntity(), "UTF-8"), Matchers.containsString("login"));
        }
    }

    @Test
    public void testNoDestinationSignedPost() throws Exception {
        AuthnRequestType loginRep = SamlClient.createLoginRequestDocument(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST_SIG, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST_SIG, null);
        Document doc = SAML2Request.convert(loginRep);
        HttpUriRequest post = POST.createSamlSignedRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), null, doc, AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_PRIVATE_KEY, AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_PUBLIC_KEY);
        try (CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new RedirectStrategyWithSwitchableFollowRedirect()).build();CloseableHttpResponse response = client.execute(post)) {
            Assert.assertThat(response, statusCodeIsHC(BAD_REQUEST));
        }
    }

    @Test
    public void testNoPortInDestination() throws Exception {
        // note that this test relies on settings of the login-protocol.saml.knownProtocols configuration option
        testWithOverriddenPort((-1), OK, Matchers.containsString("login"));
    }

    @Test
    public void testExplicitPortInDestination() throws Exception {
        testWithOverriddenPort(Integer.valueOf(AbstractKeycloakTest.AUTH_SERVER_PORT), OK, Matchers.containsString("login"));
    }

    @Test
    public void testWrongPortInDestination() throws Exception {
        testWithOverriddenPort(123, BAD_REQUEST, Matchers.containsString("Invalid Request"));
    }
}

