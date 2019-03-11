/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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


import JBossSAMLURIConstants.STATUS_INVALID_NAMEIDPOLICY;
import JBossSAMLURIConstants.STATUS_RESPONDER;
import Requirement.DISABLED;
import Requirement.REQUIRED;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.NameIDPolicyType;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.saml.processing.core.saml.v2.util.XMLTimeUtil;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.updaters.IdentityProviderCreator;
import org.keycloak.testsuite.util.Matchers;


/**
 *
 *
 * @author hmlnarik
 */
public class BrokerTest extends AbstractSamlTest {
    @Test
    public void testLogoutPropagatesToSamlIdentityProvider() throws IOException {
        final RealmResource realm = adminClient.realm(AbstractSamlTest.REALM_NAME);
        final ClientsResource clients = realm.clients();
        AuthenticationExecutionInfoRepresentation reviewProfileAuthenticator = null;
        String firstBrokerLoginFlowAlias = null;
        try (IdentityProviderCreator idp = new IdentityProviderCreator(realm, addIdentityProvider("http://saml.idp/saml"))) {
            IdentityProviderRepresentation idpRepresentation = idp.identityProvider().toRepresentation();
            firstBrokerLoginFlowAlias = idpRepresentation.getFirstBrokerLoginFlowAlias();
            List<AuthenticationExecutionInfoRepresentation> executions = realm.flows().getExecutions(firstBrokerLoginFlowAlias);
            reviewProfileAuthenticator = executions.stream().filter(( ex) -> Objects.equals(ex.getProviderId(), IdpReviewProfileAuthenticatorFactory.PROVIDER_ID)).findFirst().orElseGet(() -> {
                Assert.fail("Could not find update profile in first broker login flow");
                return null;
            });
            reviewProfileAuthenticator.setRequirement(DISABLED.name());
            realm.flows().updateExecutions(firstBrokerLoginFlowAlias, reviewProfileAuthenticator);
            SAMLDocumentHolder samlResponse = // after-first-broker-login
            // first-broker-login
            // Virtually perform login at IdP (return artificial SAML response)
            new org.keycloak.testsuite.util.SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, POST).transformObject(( ar) -> {
                NameIDPolicyType nameIDPolicy = new NameIDPolicyType();
                nameIDPolicy.setAllowCreate(Boolean.TRUE);
                nameIDPolicy.setFormat(JBossSAMLURIConstants.NAMEID_FORMAT_EMAIL.getUri());
                ar.setNameIDPolicy(nameIDPolicy);
                return ar;
            }).build().login().idp(AbstractSamlTest.SAML_BROKER_ALIAS).build().processSamlResponse(REDIRECT).transformObject(this::createAuthnResponse).targetAttributeSamlResponse().targetUri(getSamlBrokerUrl(AbstractSamlTest.REALM_NAME)).build().followOneRedirect().followOneRedirect().getSamlResponse(POST);
            Assert.assertThat(samlResponse.getSamlObject(), Matchers.isSamlStatusResponse(STATUS_RESPONDER, STATUS_INVALID_NAMEIDPOLICY));
        } finally {
            reviewProfileAuthenticator.setRequirement(REQUIRED.name());
            realm.flows().updateExecutions(firstBrokerLoginFlowAlias, reviewProfileAuthenticator);
        }
    }

    @Test
    public void testRedirectQueryParametersPreserved() throws IOException {
        final RealmResource realm = adminClient.realm(AbstractSamlTest.REALM_NAME);
        try (IdentityProviderCreator idp = new IdentityProviderCreator(realm, addIdentityProvider("http://saml.idp/?service=name&serviceType=prod"))) {
            SAMLDocumentHolder samlResponse = // Virtually perform login at IdP (return artificial SAML response)
            new org.keycloak.testsuite.util.SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, POST).build().login().idp(AbstractSamlTest.SAML_BROKER_ALIAS).build().getSamlResponse(REDIRECT);
            Assert.assertThat(samlResponse.getSamlObject(), org.hamcrest.Matchers.instanceOf(AuthnRequestType.class));
            AuthnRequestType ar = ((AuthnRequestType) (samlResponse.getSamlObject()));
            Assert.assertThat(ar.getDestination(), org.hamcrest.Matchers.equalTo(URI.create("http://saml.idp/?service=name&serviceType=prod")));
            Header[] headers = new org.keycloak.testsuite.util.SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, POST).build().login().idp(AbstractSamlTest.SAML_BROKER_ALIAS).build().doNotFollowRedirects().executeAndTransform(( resp) -> resp.getHeaders(HttpHeaders.LOCATION));
            Assert.assertThat(headers.length, org.hamcrest.Matchers.is(1));
            Assert.assertThat(headers[0].getValue(), org.hamcrest.Matchers.containsString("http://saml.idp/?service=name&serviceType=prod"));
            Assert.assertThat(headers[0].getValue(), org.hamcrest.Matchers.containsString("SAMLRequest"));
        }
    }

    @Test
    public void testExpiredAssertion() throws Exception {
        XMLGregorianCalendar now = XMLTimeUtil.getIssueInstant();
        XMLGregorianCalendar notBeforeInPast = XMLTimeUtil.subtract(now, ((60 * 60) * 1000));
        XMLGregorianCalendar notOnOrAfterInPast = XMLTimeUtil.subtract(now, ((59 * 60) * 1000));
        XMLGregorianCalendar notBeforeInFuture = XMLTimeUtil.add(now, ((59 * 60) * 1000));
        XMLGregorianCalendar notOnOrAfterInFuture = XMLTimeUtil.add(now, ((60 * 60) * 1000));
        // Should not pass:
        assertExpired(notBeforeInPast, notOnOrAfterInPast, false);
        assertExpired(notBeforeInFuture, notOnOrAfterInPast, false);
        assertExpired(null, notOnOrAfterInPast, false);
        assertExpired(notBeforeInFuture, notOnOrAfterInFuture, false);
        assertExpired(notBeforeInFuture, null, false);
        // Should pass:
        assertExpired(notBeforeInPast, notOnOrAfterInFuture, true);
        assertExpired(notBeforeInPast, null, true);
        assertExpired(null, notOnOrAfterInFuture, true);
        assertExpired(null, null, true);
    }

    @Test(expected = AssertionError.class)
    public void testNonexpiredAssertionShouldFail() throws Exception {
        assertExpired(null, null, false);// Expected result (false) is it should fail but it should pass and throw

    }

    @Test(expected = AssertionError.class)
    public void testExpiredAssertionShouldFail() throws Exception {
        XMLGregorianCalendar now = XMLTimeUtil.getIssueInstant();
        XMLGregorianCalendar notBeforeInPast = XMLTimeUtil.subtract(now, ((60 * 60) * 1000));
        XMLGregorianCalendar notOnOrAfterInPast = XMLTimeUtil.subtract(now, ((59 * 60) * 1000));
        assertExpired(notBeforeInPast, notOnOrAfterInPast, true);// Expected result (true) is it should succeed but it should pass and throw

    }
}

