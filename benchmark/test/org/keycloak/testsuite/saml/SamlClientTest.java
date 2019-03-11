package org.keycloak.testsuite.saml;


import OIDCLoginProtocol.LOGIN_PROTOCOL;
import SamlClient.Binding.POST;
import SamlClient.RedirectStrategyWithSwitchableFollowRedirect;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.processing.api.saml.v2.request.SAML2Request;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.SamlClient;
import org.w3c.dom.Document;


/**
 *
 *
 * @author mkanis
 */
public class SamlClientTest extends AbstractSamlTest {
    @Test
    public void testLoginWithOIDCClient() throws IOException, ConfigurationException, ParsingException, ProcessingException {
        ClientRepresentation salesRep = adminClient.realm(AbstractSamlTest.REALM_NAME).clients().findByClientId(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).get(0);
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(salesRep.getId()).update(ClientBuilder.edit(salesRep).protocol(LOGIN_PROTOCOL).build());
        AuthnRequestType loginRep = createLoginRequestDocument(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, AbstractSamlTest.REALM_NAME);
        Document samlRequest = SAML2Request.convert(loginRep);
        SamlClient.RedirectStrategyWithSwitchableFollowRedirect strategy = new SamlClient.RedirectStrategyWithSwitchableFollowRedirect();
        URI samlEndpoint = getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME);
        try (CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(strategy).build()) {
            HttpUriRequest post = POST.createSamlUnsignedRequest(samlEndpoint, null, samlRequest);
            CloseableHttpResponse response = sendPost(post, client);
            Assert.assertEquals(response.getStatusLine().getStatusCode(), 400);
            String s = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            Assert.assertThat(s, Matchers.containsString("Wrong client protocol."));
            response.close();
        }
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(salesRep.getId()).update(ClientBuilder.edit(salesRep).protocol(SamlProtocol.LOGIN_PROTOCOL).build());
    }
}

