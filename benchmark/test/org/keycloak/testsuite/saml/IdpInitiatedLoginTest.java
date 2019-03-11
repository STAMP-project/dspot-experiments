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


import Binding.POST;
import Binding.REDIRECT;
import OIDCLoginProtocol.LOGIN_PROTOCOL;
import SamlProtocol.SAML_ASSERTION_CONSUMER_URL_POST_ATTRIBUTE;
import SamlProtocol.SAML_ASSERTION_CONSUMER_URL_REDIRECT_ATTRIBUTE;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.dom.saml.v2.protocol.ResponseType;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.updaters.ClientAttributeUpdater;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.Matchers;
import org.keycloak.testsuite.util.SamlClientBuilder;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;


/**
 *
 *
 * @author hmlnarik
 */
public class IdpInitiatedLoginTest extends AbstractSamlTest {
    @Test
    public void testIdpInitiatedLoginPost() {
        new SamlClientBuilder().idpInitiatedLogin(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), "sales-post").build().login().user(bburkeUser).build().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST));
            return null;
        }).build().execute();
    }

    @Test
    public void testIdpInitiatedLoginPostAdminUrl() throws IOException {
        String url = adminClient.realm(AbstractSamlTest.REALM_NAME).clients().findByClientId(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).get(0).getAttributes().get(SAML_ASSERTION_CONSUMER_URL_POST_ATTRIBUTE);
        try (Closeable c = ClientAttributeUpdater.forClient(adminClient, AbstractSamlTest.REALM_NAME, AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).setAdminUrl(url).setAttribute(SAML_ASSERTION_CONSUMER_URL_POST_ATTRIBUTE, null).setAttribute(SAML_ASSERTION_CONSUMER_URL_REDIRECT_ATTRIBUTE, null).update()) {
            new SamlClientBuilder().idpInitiatedLogin(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), "sales-post").build().login().user(bburkeUser).build().processSamlResponse(POST).transformObject(( ob) -> {
                assertThat(ob, Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
                ResponseType resp = ((ResponseType) (ob));
                assertThat(resp.getDestination(), is(AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST));
                return null;
            }).build().execute();
        }
    }

    @Test
    public void testIdpInitiatedLoginRedirect() throws IOException {
        String url = adminClient.realm(AbstractSamlTest.REALM_NAME).clients().findByClientId(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).get(0).getAttributes().get(SAML_ASSERTION_CONSUMER_URL_POST_ATTRIBUTE);
        try (Closeable c = ClientAttributeUpdater.forClient(adminClient, AbstractSamlTest.REALM_NAME, AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).setAttribute(SAML_ASSERTION_CONSUMER_URL_POST_ATTRIBUTE, null).setAttribute(SAML_ASSERTION_CONSUMER_URL_REDIRECT_ATTRIBUTE, url).update()) {
            new SamlClientBuilder().idpInitiatedLogin(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), "sales-post").build().login().user(bburkeUser).build().processSamlResponse(REDIRECT).transformObject(( ob) -> {
                assertThat(ob, Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
                ResponseType resp = ((ResponseType) (ob));
                assertThat(resp.getDestination(), is(AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST));
                return null;
            }).build().execute();
        }
    }

    @Test
    public void testTwoConsequentIdpInitiatedLogins() {
        new SamlClientBuilder().idpInitiatedLogin(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), "sales-post").build().login().user(bburkeUser).build().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST));
            return null;
        }).build().idpInitiatedLogin(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), "sales-post2").build().login().sso(true).build().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST2));
            return null;
        }).build().execute();
        final UsersResource users = adminClient.realm(AbstractSamlTest.REALM_NAME).users();
        final ClientsResource clients = adminClient.realm(AbstractSamlTest.REALM_NAME).clients();
        UserRepresentation bburkeUserRepresentation = users.search(bburkeUser.getUsername()).stream().findFirst().get();
        List<UserSessionRepresentation> userSessions = users.get(bburkeUserRepresentation.getId()).getUserSessions();
        Assert.assertThat(userSessions, hasSize(1));
        Map<String, String> clientSessions = userSessions.get(0).getClients();
        Set<String> clientIds = clientSessions.values().stream().flatMap(( c) -> clients.findByClientId(c).stream()).map(ClientRepresentation::getClientId).collect(Collectors.toSet());
        Assert.assertThat(clientIds, containsInAnyOrder(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_CLIENT_ID_SALES_POST2));
    }

    @Test
    public void testIdpInitiatedLoginWithOIDCClient() {
        ClientRepresentation clientRep = adminClient.realm(AbstractSamlTest.REALM_NAME).clients().findByClientId(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST).get(0);
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(clientRep.getId()).update(ClientBuilder.edit(clientRep).protocol(LOGIN_PROTOCOL).build());
        new SamlClientBuilder().idpInitiatedLogin(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), "sales-post").build().execute(( r) -> {
            Assert.assertThat(r, statusCodeIsHC(Response.Status.BAD_REQUEST));
            Assert.assertThat(r, bodyHC(containsString("Wrong client protocol.")));
        });
        adminClient.realm(AbstractSamlTest.REALM_NAME).clients().get(clientRep.getId()).update(ClientBuilder.edit(clientRep).protocol(SamlProtocol.LOGIN_PROTOCOL).build());
    }
}

