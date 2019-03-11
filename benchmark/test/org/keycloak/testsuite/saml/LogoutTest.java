/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.saml;


import SamlProtocol.SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE;
import SamlProtocol.SAML_SINGLE_LOGOUT_SERVICE_URL_REDIRECT_ATTRIBUTE;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.transform.dom.DOMSource;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.dom.saml.v2.SAML2Object;
import org.keycloak.dom.saml.v2.assertion.NameIDType;
import org.keycloak.dom.saml.v2.protocol.LogoutRequestType;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.saml.SAML2LogoutResponseBuilder;
import org.keycloak.saml.processing.core.parsers.saml.SAMLParser;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.updaters.ClientAttributeUpdater;
import org.keycloak.testsuite.util.ClientBuilder;


/**
 *
 *
 * @author hmlnarik
 */
public class LogoutTest extends AbstractSamlTest {
    private ClientRepresentation salesRep;

    private ClientRepresentation sales2Rep;

    private final AtomicReference<NameIDType> nameIdRef = new AtomicReference<>();

    private final AtomicReference<String> sessionIndexRef = new AtomicReference<>();

    @Test
    public void testLogoutDifferentBrowser() {
        // This is in fact the same as admin logging out a session from admin console.
        // This always succeeds as it is essentially the same as backend logout which
        // does not report errors to client but only to the server log
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(sales2Rep.getId()).update(ClientBuilder.edit(sales2Rep).frontchannelLogout(false).attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE, "").removeAttribute(SAML_SINGLE_LOGOUT_SERVICE_URL_REDIRECT_ATTRIBUTE).build());
        SAMLDocumentHolder samlResponse = prepareLogIntoTwoApps().clearCookies().logoutRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, POST).nameId(nameIdRef::get).sessionIndex(sessionIndexRef::get).build().getSamlResponse(POST);
        Assert.assertThat(samlResponse.getSamlObject(), isSamlStatusResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
    }

    @Test
    public void testFrontchannelLogoutInSameBrowser() {
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(sales2Rep.getId()).update(ClientBuilder.edit(sales2Rep).frontchannelLogout(true).attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE, "").build());
        SAMLDocumentHolder samlResponse = prepareLogIntoTwoApps().logoutRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, POST).nameId(nameIdRef::get).sessionIndex(sessionIndexRef::get).build().getSamlResponse(POST);
        Assert.assertThat(samlResponse.getSamlObject(), isSamlStatusResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
        assertLogoutEvent(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST);
    }

    @Test
    public void testFrontchannelLogoutNoLogoutServiceUrlSetInSameBrowser() {
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(sales2Rep.getId()).update(ClientBuilder.edit(sales2Rep).frontchannelLogout(true).attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE, "").attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_REDIRECT_ATTRIBUTE, "").build());
        SAMLDocumentHolder samlResponse = prepareLogIntoTwoApps().logoutRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, POST).nameId(nameIdRef::get).sessionIndex(sessionIndexRef::get).build().getSamlResponse(POST);
        Assert.assertThat(samlResponse.getSamlObject(), isSamlStatusResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
    }

    @Test
    public void testFrontchannelLogoutDifferentBrowser() {
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(sales2Rep.getId()).update(ClientBuilder.edit(sales2Rep).frontchannelLogout(true).attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE, "").build());
        SAMLDocumentHolder samlResponse = prepareLogIntoTwoApps().clearCookies().logoutRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, POST).nameId(nameIdRef::get).sessionIndex(sessionIndexRef::get).build().getSamlResponse(POST);
        Assert.assertThat(samlResponse.getSamlObject(), isSamlStatusResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
    }

    @Test
    public void testFrontchannelLogoutWithRedirectUrlDifferentBrowser() {
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(salesRep.getId()).update(ClientBuilder.edit(salesRep).frontchannelLogout(true).attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE, "").attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_REDIRECT_ATTRIBUTE, "http://url").build());
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(sales2Rep.getId()).update(ClientBuilder.edit(sales2Rep).frontchannelLogout(true).attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE, "").attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_REDIRECT_ATTRIBUTE, "").build());
        SAMLDocumentHolder samlResponse = prepareLogIntoTwoApps().clearCookies().logoutRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, REDIRECT).nameId(nameIdRef::get).sessionIndex(sessionIndexRef::get).build().getSamlResponse(REDIRECT);
        Assert.assertThat(samlResponse.getSamlObject(), isSamlStatusResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
    }

    @Test
    public void testLogoutWithPostBindingUnsetRedirectBindingSet() {
        // https://issues.jboss.org/browse/KEYCLOAK-4779
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(sales2Rep.getId()).update(ClientBuilder.edit(sales2Rep).frontchannelLogout(true).attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE, "").attribute(SAML_SINGLE_LOGOUT_SERVICE_URL_REDIRECT_ATTRIBUTE, "http://url-to-sales-2").build());
        SAMLDocumentHolder samlResponse = prepareLogIntoTwoApps().logoutRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, POST).nameId(nameIdRef::get).sessionIndex(sessionIndexRef::get).build().processSamlResponse(REDIRECT).transformDocument(( doc) -> {
            // Expect logout request for sales-post2
            SAML2Object so = ((SAML2Object) (SAMLParser.getInstance().parse(new DOMSource(doc))));
            assertThat(so, isSamlLogoutRequest("http://url-to-sales-2"));
            // Emulate successful logout response from sales-post2 logout
            return new SAML2LogoutResponseBuilder().destination(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME).toString()).issuer(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST2).logoutRequestID(((LogoutRequestType) (so)).getID()).buildDocument();
        }).targetAttributeSamlResponse().targetUri(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME)).build().getSamlResponse(POST);
        // Expect final successful logout response from auth server signalling final successful logout
        Assert.assertThat(samlResponse.getSamlObject(), isSamlStatusResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
        Assert.assertThat(getDestination(), Matchers.is("http://url"));
        assertLogoutEvent(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST2);
    }

    @Test
    public void testLogoutPropagatesToSamlIdentityProvider() throws IOException {
        final RealmResource realm = adminClient.realm(AbstractSamlTest.REALM_NAME);
        try (Closeable sales = ClientAttributeUpdater.forClient(adminClient, AbstractSamlTest.REALM_NAME, AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).setFrontchannelLogout(true).removeAttribute(SAML_SINGLE_LOGOUT_SERVICE_URL_POST_ATTRIBUTE).setAttribute(SAML_SINGLE_LOGOUT_SERVICE_URL_REDIRECT_ATTRIBUTE, "http://url").update();Closeable idp = new org.keycloak.testsuite.updaters.IdentityProviderCreator(realm, addIdentityProvider())) {
            SAMLDocumentHolder samlResponse = // Should redirect now to logout from IdP
            // ----- Logout phase ------
            // Logout initiated from the app
            // Now returning back to the app
            // Virtually perform login at IdP (return artificial SAML response)
            new org.keycloak.testsuite.util.SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, POST).build().login().idp(AbstractSamlTest.SAML_BROKER_ALIAS).build().processSamlResponse(REDIRECT).transformObject(this::createAuthnResponse).targetAttributeSamlResponse().targetUri(getSamlBrokerUrl(AbstractSamlTest.REALM_NAME)).build().updateProfile().username("a").email("a@b.c").firstName("A").lastName("B").build().followOneRedirect().processSamlResponse(POST).transformObject(this::extractNameIdAndSessionIndexAndTerminate).build().logoutRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, REDIRECT).nameId(nameIdRef::get).sessionIndex(sessionIndexRef::get).build().processSamlResponse(REDIRECT).transformObject(this::createIdPLogoutResponse).targetAttributeSamlResponse().targetUri(getSamlBrokerUrl(AbstractSamlTest.REALM_NAME)).build().getSamlResponse(REDIRECT);
            Assert.assertThat(samlResponse.getSamlObject(), isSamlStatusResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
        }
    }
}

