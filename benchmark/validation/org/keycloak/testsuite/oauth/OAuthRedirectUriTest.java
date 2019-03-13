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
package org.keycloak.testsuite.oauth;


import CookieSpecs.BEST_MATCH;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import OAuthClient.AuthorizationEndpointResponse;
import SimpleHttp.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.ClientManager;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:vrockai@redhat.com">Viliam Rockai</a>
 */
public class OAuthRedirectUriTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected ErrorPage errorPage;

    @Page
    protected LoginPage loginPage;

    private HttpServer server;

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "Hello";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    @Test
    public void testNoParam() throws IOException {
        oauth.redirectUri(null);
        oauth.openLoginForm();
        Assert.assertTrue(errorPage.isCurrent());
        Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
    }

    @Test
    public void testRelativeUri() throws IOException {
        oauth.redirectUri("/foo/../bar");
        oauth.openLoginForm();
        Assert.assertTrue(errorPage.isCurrent());
        Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
    }

    @Test
    public void testFileUri() throws IOException {
        oauth.redirectUri("file://test");
        oauth.openLoginForm();
        Assert.assertTrue(errorPage.isCurrent());
        Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
    }

    @Test
    public void testNoParamMultipleValidUris() throws IOException {
        ClientManager.realm(adminClient.realm("test")).clientId("test-app").addRedirectUris("http://localhost:8180/app2");
        try {
            oauth.redirectUri(null);
            oauth.openLoginForm();
            Assert.assertTrue(errorPage.isCurrent());
            Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
        } finally {
            ClientManager.realm(adminClient.realm("test")).clientId("test-app").removeRedirectUris("http://localhost:8180/app2");
        }
    }

    @Test
    public void testNoParamNoValidUris() throws IOException {
        ClientManager.realm(adminClient.realm("test")).clientId("test-app").removeRedirectUris("http://localhost:8180/auth/realms/master/app/auth/*");
        try {
            oauth.redirectUri(null);
            oauth.openLoginForm();
            Assert.assertTrue(errorPage.isCurrent());
            Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
        } finally {
            ClientManager.realm(adminClient.realm("test")).clientId("test-app").addRedirectUris("http://localhost:8180/auth/realms/master/app/auth/*");
        }
    }

    @Test
    public void testNoValidUris() throws IOException {
        ClientManager.realm(adminClient.realm("test")).clientId("test-app").removeRedirectUris("http://localhost:8180/auth/realms/master/app/auth/*");
        try {
            oauth.redirectUri(null);
            oauth.openLoginForm();
            Assert.assertTrue(errorPage.isCurrent());
            Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
        } finally {
            ClientManager.realm(adminClient.realm("test")).clientId("test-app").addRedirectUris("http://localhost:8180/auth/realms/master/app/auth/*");
        }
    }

    @Test
    public void testValid() throws IOException {
        oauth.redirectUri(((OAuthClient.APP_ROOT) + "/auth"));
        OAuthClient.AuthorizationEndpointResponse response = oauth.doLogin("test-user@localhost", "password");
        Assert.assertNotNull(response.getCode());
        URL url = new URL(driver.getCurrentUrl());
        Assert.assertTrue(url.toString().startsWith(OAuthClient.APP_ROOT));
        Assert.assertTrue(url.getQuery().contains("code="));
        Assert.assertTrue(url.getQuery().contains("state="));
    }

    @Test
    public void testInvalid() throws IOException {
        oauth.redirectUri("http://localhost:8180/app2");
        oauth.openLoginForm();
        Assert.assertTrue(errorPage.isCurrent());
        Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
    }

    @Test
    public void testWithParams() throws IOException {
        oauth.redirectUri(((OAuthClient.APP_ROOT) + "/auth?key=value"));
        OAuthClient.AuthorizationEndpointResponse response = oauth.doLogin("test-user@localhost", "password");
        Assert.assertNotNull(response.getCode());
        URL url = new URL(driver.getCurrentUrl());
        Assert.assertTrue(url.toString().startsWith(OAuthClient.APP_ROOT));
        Assert.assertTrue(url.getQuery().contains("key=value"));
        Assert.assertTrue(url.getQuery().contains("state="));
        Assert.assertTrue(url.getQuery().contains("code="));
    }

    @Test
    public void testWithFragment() throws IOException {
        oauth.clientId("test-fragment");
        oauth.responseMode("fragment");
        oauth.redirectUri(((OAuthClient.APP_ROOT) + "/auth#key=value"));
        OAuthClient.AuthorizationEndpointResponse response = oauth.doLogin("test-user@localhost", "password");
        Assert.assertNotNull(response.getCode());
        URL url = new URL(driver.getCurrentUrl());
        Assert.assertTrue(url.toString().startsWith(OAuthClient.APP_ROOT));
        Assert.assertTrue(url.toString().contains("key=value"));
    }

    @Test
    public void testWithCustomScheme() throws IOException {
        oauth.clientId("custom-scheme");
        oauth.redirectUri("android-app://org.keycloak.examples.cordova/https/keycloak-cordova-example.github.io/login");
        oauth.openLoginForm();
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(BEST_MATCH).build();
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        String loginUrl = driver.getCurrentUrl();
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();
        try {
            String loginPage = SimpleHttp.doGet(loginUrl, client).asString();
            String formAction = loginPage.split("action=\"")[1].split("\"")[0].replaceAll("&amp;", "&");
            SimpleHttp.Response response = SimpleHttp.doPost(formAction, client).param("username", "test-user@localhost").param("password", "password").asResponse();
            response.getStatus();
            Assert.assertThat(response.getFirstHeader("Location"), Matchers.startsWith("android-app://org.keycloak.examples.cordova/https/keycloak-cordova-example.github.io/login"));
        } finally {
            client.close();
        }
    }

    @Test
    public void testQueryComponents() throws IOException {
        // KEYCLOAK-3420
        oauth.clientId("test-query-component");
        checkRedirectUri("http://localhost?foo=bar", true);
        checkRedirectUri("http://localhost?foo=bara", false);
        checkRedirectUri("http://localhost?foo=bar/", false);
        checkRedirectUri("http://localhost?foo2=bar2&foo=bar", false);
        checkRedirectUri("http://localhost?foo=b", false);
        checkRedirectUri("http://localhost?foo", false);
        checkRedirectUri("http://localhost?foo=bar&bar=foo", false);
        checkRedirectUri("http://localhost?foo&bar=foo", false);
        checkRedirectUri("http://localhost?foo&bar", false);
        checkRedirectUri("http://localhost", false);
        // KEYCLOAK-3418
        oauth.clientId("test-installed");
        checkRedirectUri("http://localhost?foo=bar", false);
    }

    @Test
    public void testWildcard() throws IOException {
        oauth.clientId("test-wildcard");
        checkRedirectUri("http://example.com", false);
        checkRedirectUri("http://localhost:8080", false, true);
        checkRedirectUri("http://example.com/foo", true);
        checkRedirectUri("http://example.com/foo/bar", true);
        checkRedirectUri("http://localhost:8280/foo", true, true);
        checkRedirectUri("http://localhost:8280/foo/bar", true, true);
        checkRedirectUri("http://example.com/foobar", false);
        checkRedirectUri("http://localhost:8280/foobar", false, true);
    }

    @Test
    public void testDash() throws IOException {
        oauth.clientId("test-dash");
        checkRedirectUri("http://with-dash.example.local/foo", true);
    }

    @Test
    public void testDifferentCaseInHostname() throws IOException {
        oauth.clientId("test-dash");
        checkRedirectUri("http://with-dash.example.local", true);
        checkRedirectUri("http://wiTh-dAsh.example.local", true);
        checkRedirectUri("http://with-dash.example.local/foo", true);
        checkRedirectUri("http://wiTh-dAsh.example.local/foo", true);
        checkRedirectUri("http://with-dash.example.local/foo", true);
        checkRedirectUri("http://wiTh-dAsh.example.local/foo", true);
        checkRedirectUri("http://wiTh-dAsh.example.local/Foo", false);
        checkRedirectUri("http://wiTh-dAsh.example.local/foO", false);
    }

    @Test
    public void testDifferentCaseInScheme() throws IOException {
        oauth.clientId("test-dash");
        checkRedirectUri("HTTP://with-dash.example.local", true);
        checkRedirectUri("Http://wiTh-dAsh.example.local", true);
    }

    @Test
    public void testRelativeWithRoot() throws IOException {
        oauth.clientId("test-root-url");
        checkRedirectUri("http://with-dash.example.local/foo", true);
        checkRedirectUri("http://localhost:8180/foo", false);
    }

    @Test
    public void testRelative() throws IOException {
        oauth.clientId("test-relative-url");
        checkRedirectUri("http://with-dash.example.local/foo", false);
        checkRedirectUri("http://localhost:8180/auth", true);
    }

    @Test
    public void testLocalhost() throws IOException {
        oauth.clientId("test-installed");
        checkRedirectUri("urn:ietf:wg:oauth:2.0:oob", true, true);
        checkRedirectUri("http://localhost", true);
        checkRedirectUri("http://localhost:8280", true, true);
        checkRedirectUri("http://localhosts", false);
        checkRedirectUri("http://localhost/myapp", false);
        checkRedirectUri("http://localhost:8180/myapp", false, true);
        oauth.clientId("test-installed2");
        checkRedirectUri("http://localhost/myapp", true);
        checkRedirectUri("http://localhost:8280/myapp", true, true);
        checkRedirectUri("http://localhosts/myapp", false);
        checkRedirectUri("http://localhost", false);
        checkRedirectUri("http://localhost/myapp2", false);
    }

    @Test
    public void okThenNull() throws IOException {
        oauth.clientId("test-wildcard");
        oauth.redirectUri("http://localhost:8280/foo");
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        Assert.assertNotNull(code);
        oauth.redirectUri(null);
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals("Expected 400, but got something else", 400, tokenResponse.getStatusCode());
    }
}

