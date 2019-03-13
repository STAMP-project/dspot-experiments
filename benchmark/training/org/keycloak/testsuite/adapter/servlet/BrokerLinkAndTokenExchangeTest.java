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
package org.keycloak.testsuite.adapter.servlet;


import ExportImportConfig.ACTION_EXPORT;
import ExportImportConfig.ACTION_IMPORT;
import HttpHeaders.AUTHORIZATION;
import OAuth2Constants.ACCESS_TOKEN_TYPE;
import OAuth2Constants.GRANT_TYPE;
import OAuth2Constants.JWT_TOKEN_TYPE;
import OAuth2Constants.REQUESTED_ISSUER;
import OAuth2Constants.REQUESTED_SUBJECT;
import OAuth2Constants.SUBJECT_ISSUER;
import OAuth2Constants.SUBJECT_TOKEN;
import OAuth2Constants.SUBJECT_TOKEN_TYPE;
import OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE;
import OAuthClient.AUTH_SERVER_ROOT;
import OIDCIdentityProviderConfig.JWKS_URL;
import OIDCIdentityProviderConfig.USE_JWKS_URL;
import OIDCIdentityProviderConfig.VALIDATE_SIGNATURE;
import SingleFileExportProviderFactory.PROVIDER_ID;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.page.AbstractPageWithInjectedUrl;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginUpdateProfilePage;
import org.keycloak.util.BasicAuthHelper;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class BrokerLinkAndTokenExchangeTest extends AbstractServletsAdapterTest {
    public static final String CHILD_IDP = "child";

    public static final String PARENT_IDP = "parent-idp";

    public static final String PARENT_USERNAME = "parent";

    public static final String PARENT2_USERNAME = "parent2";

    public static final String UNAUTHORIZED_CHILD_CLIENT = "unauthorized-child-client";

    public static final String PARENT_CLIENT = "parent-client";

    @Page
    protected LoginUpdateProfilePage loginUpdateProfilePage;

    @Page
    protected AccountUpdateProfilePage profilePage;

    @Page
    private LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    public static class ClientApp extends AbstractPageWithInjectedUrl {
        public static final String DEPLOYMENT_NAME = "exchange-linking";

        @ArquillianResource
        @OperateOnDeployment(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME)
        private URL url;

        @Override
        public URL getInjectedUrl() {
            return url;
        }
    }

    @Page
    private BrokerLinkAndTokenExchangeTest.ClientApp appPage;

    private String childUserId = null;

    @Test
    public void testAccountLink() throws Exception {
        testingClient.server().run(BrokerLinkAndTokenExchangeTest::turnOnTokenStore);
        RealmResource realm = adminClient.realms().realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        String servletUri = appPage.getInjectedUrl().toString();
        UriBuilder linkBuilder = UriBuilder.fromUri(servletUri).path("link");
        String linkUrl = linkBuilder.clone().queryParam("realm", BrokerLinkAndTokenExchangeTest.CHILD_IDP).queryParam("provider", BrokerLinkAndTokenExchangeTest.PARENT_IDP).build().toString();
        System.out.println(("linkUrl: " + linkUrl));
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(BrokerLinkAndTokenExchangeTest.CHILD_IDP));
        Assert.assertTrue(driver.getPageSource().contains(BrokerLinkAndTokenExchangeTest.PARENT_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(loginPage.isCurrent(BrokerLinkAndTokenExchangeTest.PARENT_IDP));
        loginPage.login(BrokerLinkAndTokenExchangeTest.PARENT_USERNAME, "password");
        System.out.println(("After linking: " + (driver.getCurrentUrl())));
        System.out.println(driver.getPageSource());
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account Linked"));
        Assert.assertTrue(driver.getPageSource().contains("Exchange token received"));
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        // do exchange
        String accessToken = oauth.doGrantAccessTokenRequest(BrokerLinkAndTokenExchangeTest.CHILD_IDP, "child", "password", null, BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password").getAccessToken();
        Client httpClient = ClientBuilder.newClient();
        WebTarget exchangeUrl = childTokenExchangeWebTarget(httpClient);
        System.out.println(("Exchange url: " + (exchangeUrl.getUri().toString())));
        Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, ACCESS_TOKEN_TYPE).param(REQUESTED_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
        Assert.assertEquals(200, response.getStatus());
        AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
        response.close();
        String externalToken = tokenResponse.getToken();
        Assert.assertNotNull(externalToken);
        Assert.assertTrue(((tokenResponse.getExpiresIn()) > 0));
        setTimeOffset((((int) (tokenResponse.getExpiresIn())) + 1));
        // test that token refresh happens
        // get access token again because we may have timed out
        accessToken = oauth.doGrantAccessTokenRequest(BrokerLinkAndTokenExchangeTest.CHILD_IDP, "child", "password", null, BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password").getAccessToken();
        response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, ACCESS_TOKEN_TYPE).param(REQUESTED_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
        Assert.assertEquals(200, response.getStatus());
        tokenResponse = response.readEntity(AccessTokenResponse.class);
        response.close();
        Assert.assertNotEquals(externalToken, tokenResponse.getToken());
        // test direct exchange
        response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("direct-exchanger", "secret")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(REQUESTED_SUBJECT, "child").param(REQUESTED_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
        Assert.assertEquals(200, response.getStatus());
        tokenResponse = response.readEntity(AccessTokenResponse.class);
        response.close();
        Assert.assertNotEquals(externalToken, tokenResponse.getToken());
        logoutAll();
        realm.users().get(childUserId).removeFederatedIdentity(BrokerLinkAndTokenExchangeTest.PARENT_IDP);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
    }

    @Test
    public void testAccountLinkNoTokenStore() throws Exception {
        testingClient.server().run(BrokerLinkAndTokenExchangeTest::turnOffTokenStore);
        RealmResource realm = adminClient.realms().realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        UriBuilder linkBuilder = UriBuilder.fromUri(appPage.getInjectedUrl().toString()).path("link");
        String linkUrl = linkBuilder.clone().queryParam("realm", BrokerLinkAndTokenExchangeTest.CHILD_IDP).queryParam("provider", BrokerLinkAndTokenExchangeTest.PARENT_IDP).build().toString();
        System.out.println(("linkUrl: " + linkUrl));
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(BrokerLinkAndTokenExchangeTest.CHILD_IDP));
        Assert.assertTrue(driver.getPageSource().contains(BrokerLinkAndTokenExchangeTest.PARENT_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(loginPage.isCurrent(BrokerLinkAndTokenExchangeTest.PARENT_IDP));
        loginPage.login(BrokerLinkAndTokenExchangeTest.PARENT_USERNAME, "password");
        System.out.println(("After linking: " + (driver.getCurrentUrl())));
        System.out.println(driver.getPageSource());
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account Linked"));
        Assert.assertTrue(driver.getPageSource().contains("Exchange token received"));
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        logoutAll();
        realm.users().get(childUserId).removeFederatedIdentity(BrokerLinkAndTokenExchangeTest.PARENT_IDP);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
    }

    /**
     * KEYCLOAK-6026
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExportImport() throws Exception {
        testExternalExchange();
        testingClient.testing().exportImport().setProvider(PROVIDER_ID);
        String targetFilePath = ((testingClient.testing().exportImport().getExportImportTestDirectory()) + (File.separator)) + "singleFile-full.json";
        testingClient.testing().exportImport().setFile(targetFilePath);
        testingClient.testing().exportImport().setAction(ACTION_EXPORT);
        testingClient.testing().exportImport().setRealmName(BrokerLinkAndTokenExchangeTest.CHILD_IDP);
        testingClient.testing().exportImport().runExport();
        adminClient.realms().realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).remove();
        testingClient.testing().exportImport().setAction(ACTION_IMPORT);
        testingClient.testing().exportImport().runImport();
        testingClient.testing().exportImport().clear();
        testExternalExchange();
    }

    @Test
    public void testExternalExchange() throws Exception {
        RealmResource childRealm = adminClient.realms().realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP);
        String accessToken = oauth.doGrantAccessTokenRequest(BrokerLinkAndTokenExchangeTest.PARENT_IDP, BrokerLinkAndTokenExchangeTest.PARENT2_USERNAME, "password", null, BrokerLinkAndTokenExchangeTest.PARENT_CLIENT, "password").getAccessToken();
        Assert.assertEquals(0, adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).getClientSessionStats().size());
        Client httpClient = ClientBuilder.newClient();
        WebTarget exchangeUrl = childTokenExchangeWebTarget(httpClient);
        System.out.println(("Exchange url: " + (exchangeUrl.getUri().toString())));
        {
            IdentityProviderRepresentation rep = adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).identityProviders().get(BrokerLinkAndTokenExchangeTest.PARENT_IDP).toRepresentation();
            rep.getConfig().put(VALIDATE_SIGNATURE, String.valueOf(false));
            adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).identityProviders().get(BrokerLinkAndTokenExchangeTest.PARENT_IDP).update(rep);
            // test user info validation.
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, JWT_TOKEN_TYPE).param(SUBJECT_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            String exchangedAccessToken = tokenResponse.getToken();
            Assert.assertNotNull(exchangedAccessToken);
            response.close();
            Assert.assertEquals(1, adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).getClientSessionStats().size());
            // test logout
            response = childLogoutWebTarget(httpClient).queryParam("id_token_hint", exchangedAccessToken).request().get();
            response.close();
            Assert.assertEquals(0, adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).getClientSessionStats().size());
        }
        IdentityProviderRepresentation rep = adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).identityProviders().get(BrokerLinkAndTokenExchangeTest.PARENT_IDP).toRepresentation();
        rep.getConfig().put(VALIDATE_SIGNATURE, String.valueOf(true));
        rep.getConfig().put(USE_JWKS_URL, String.valueOf(true));
        rep.getConfig().put(JWKS_URL, parentJwksUrl());
        String parentIssuer = UriBuilder.fromUri(AUTH_SERVER_ROOT).path("/realms").path(BrokerLinkAndTokenExchangeTest.PARENT_IDP).build().toString();
        rep.getConfig().put("issuer", parentIssuer);
        adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).identityProviders().get(BrokerLinkAndTokenExchangeTest.PARENT_IDP).update(rep);
        String exchangedUserId = null;
        String exchangedUsername = null;
        {
            // test signature validation
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, JWT_TOKEN_TYPE).param(SUBJECT_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            String exchangedAccessToken = tokenResponse.getToken();
            JWSInput jws = new JWSInput(tokenResponse.getToken());
            AccessToken token = jws.readJsonContent(AccessToken.class);
            response.close();
            exchangedUserId = token.getSubject();
            exchangedUsername = token.getPreferredUsername();
            System.out.println(("exchangedUserId: " + exchangedUserId));
            System.out.println(("exchangedUsername: " + exchangedUsername));
            // test that we can exchange back to external token
            response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, tokenResponse.getToken()).param(SUBJECT_TOKEN_TYPE, ACCESS_TOKEN_TYPE).param(REQUESTED_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
            Assert.assertEquals(200, response.getStatus());
            tokenResponse = response.readEntity(AccessTokenResponse.class);
            Assert.assertEquals(accessToken, tokenResponse.getToken());
            response.close();
            Assert.assertEquals(1, adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).getClientSessionStats().size());
            // test logout
            response = childLogoutWebTarget(httpClient).queryParam("id_token_hint", exchangedAccessToken).request().get();
            response.close();
            Assert.assertEquals(0, adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).getClientSessionStats().size());
            List<FederatedIdentityRepresentation> links = childRealm.users().get(exchangedUserId).getFederatedIdentity();
            Assert.assertEquals(1, links.size());
        }
        {
            // check that we can request an exchange again and that the previously linked user is obtained
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, JWT_TOKEN_TYPE).param(SUBJECT_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            String exchangedAccessToken = tokenResponse.getToken();
            JWSInput jws = new JWSInput(tokenResponse.getToken());
            AccessToken token = jws.readJsonContent(AccessToken.class);
            response.close();
            String exchanged2UserId = token.getSubject();
            String exchanged2Username = token.getPreferredUsername();
            // assert that we get the same linked account as was previously imported
            Assert.assertEquals(exchangedUserId, exchanged2UserId);
            Assert.assertEquals(exchangedUsername, exchanged2Username);
            // test logout
            response = childLogoutWebTarget(httpClient).queryParam("id_token_hint", exchangedAccessToken).request().get();
            response.close();
            Assert.assertEquals(0, adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).getClientSessionStats().size());
            List<FederatedIdentityRepresentation> links = childRealm.users().get(exchangedUserId).getFederatedIdentity();
            Assert.assertEquals(1, links.size());
        }
        {
            // check that we can exchange without specifying an SUBJECT_ISSUER
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.ClientApp.DEPLOYMENT_NAME, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, JWT_TOKEN_TYPE)));
            Assert.assertEquals(200, response.getStatus());
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            String exchangedAccessToken = tokenResponse.getToken();
            JWSInput jws = new JWSInput(tokenResponse.getToken());
            AccessToken token = jws.readJsonContent(AccessToken.class);
            response.close();
            String exchanged2UserId = token.getSubject();
            String exchanged2Username = token.getPreferredUsername();
            // assert that we get the same linked account as was previously imported
            Assert.assertEquals(exchangedUserId, exchanged2UserId);
            Assert.assertEquals(exchangedUsername, exchanged2Username);
            // test logout
            response = childLogoutWebTarget(httpClient).queryParam("id_token_hint", exchangedAccessToken).request().get();
            response.close();
            Assert.assertEquals(0, adminClient.realm(BrokerLinkAndTokenExchangeTest.CHILD_IDP).getClientSessionStats().size());
            List<FederatedIdentityRepresentation> links = childRealm.users().get(exchangedUserId).getFederatedIdentity();
            Assert.assertEquals(1, links.size());
        }
        // cleanup  remove the user
        childRealm.users().get(exchangedUserId).remove();
        {
            // test unauthorized client gets 403
            Response response = exchangeUrl.request().header(AUTHORIZATION, BasicAuthHelper.createHeader(BrokerLinkAndTokenExchangeTest.UNAUTHORIZED_CHILD_CLIENT, "password")).post(Entity.form(new Form().param(GRANT_TYPE, TOKEN_EXCHANGE_GRANT_TYPE).param(SUBJECT_TOKEN, accessToken).param(SUBJECT_TOKEN_TYPE, JWT_TOKEN_TYPE).param(SUBJECT_ISSUER, BrokerLinkAndTokenExchangeTest.PARENT_IDP)));
            Assert.assertEquals(403, response.getStatus());
        }
    }
}

