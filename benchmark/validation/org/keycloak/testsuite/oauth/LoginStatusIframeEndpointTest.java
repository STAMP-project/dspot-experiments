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


import Constants.ADMIN_CONSOLE_CLIENT_ID;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.models.Constants;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.ActionURIUtils;
import org.keycloak.testsuite.runonserver.ServerVersion;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class LoginStatusIframeEndpointTest extends AbstractKeycloakTest {
    @Test
    public void checkIframe() throws IOException {
        CookieStore cookieStore = new BasicCookieStore();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {
            String redirectUri = URLEncoder.encode(((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/admin/master/console"), "UTF-8");
            HttpGet get = new HttpGet((((((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/auth?response_type=code&client_id=") + (Constants.ADMIN_CONSOLE_CLIENT_ID)) + "&redirect_uri=") + redirectUri));
            CloseableHttpResponse response = client.execute(get);
            String s = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            response.close();
            String action = ActionURIUtils.getActionURIFromPageSource(s);
            HttpPost post = new HttpPost(action);
            List<NameValuePair> params = new LinkedList<>();
            params.add(new BasicNameValuePair("username", "admin"));
            params.add(new BasicNameValuePair("password", "admin"));
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new UrlEncodedFormEntity(params));
            response = client.execute(post);
            Assert.assertEquals("CP=\"This is not a P3P policy!\"", response.getFirstHeader("P3P").getValue());
            Header setIdentityCookieHeader = null;
            Header setSessionCookieHeader = null;
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals("Set-Cookie")) {
                    if (h.getValue().contains("KEYCLOAK_SESSION")) {
                        setSessionCookieHeader = h;
                    } else
                        if (h.getValue().contains("KEYCLOAK_IDENTITY")) {
                            setIdentityCookieHeader = h;
                        }

                }
            }
            Assert.assertNotNull(setIdentityCookieHeader);
            Assert.assertTrue(setIdentityCookieHeader.getValue().contains("HttpOnly"));
            Assert.assertNotNull(setSessionCookieHeader);
            Assert.assertFalse(setSessionCookieHeader.getValue().contains("HttpOnly"));
            response.close();
            Cookie sessionCookie = null;
            for (Cookie cookie : cookieStore.getCookies()) {
                if (cookie.getName().equals("KEYCLOAK_SESSION")) {
                    sessionCookie = cookie;
                    break;
                }
            }
            Assert.assertNotNull(sessionCookie);
            get = new HttpGet(((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html"));
            response = client.execute(get);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            s = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            Assert.assertTrue(s.contains("function getCookie()"));
            Assert.assertEquals("CP=\"This is not a P3P policy!\"", response.getFirstHeader("P3P").getValue());
            response.close();
            get = new HttpGet(((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html/init"));
            response = client.execute(get);
            Assert.assertEquals(403, response.getStatusLine().getStatusCode());
            response.close();
            get = new HttpGet((((((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html/init?") + "client_id=invalid") + "&origin=") + (suiteContext.getAuthServerInfo().getContextRoot())));
            response = client.execute(get);
            Assert.assertEquals(403, response.getStatusLine().getStatusCode());
            response.close();
            get = new HttpGet((((((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html/init?") + "client_id=") + (Constants.ADMIN_CONSOLE_CLIENT_ID)) + "&origin=http://invalid"));
            response = client.execute(get);
            Assert.assertEquals(403, response.getStatusLine().getStatusCode());
            response.close();
            get = new HttpGet(((((((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html/init?") + "client_id=") + (Constants.ADMIN_CONSOLE_CLIENT_ID)) + "&origin=") + (suiteContext.getAuthServerInfo().getContextRoot())));
            response = client.execute(get);
            Assert.assertEquals(204, response.getStatusLine().getStatusCode());
            response.close();
        }
    }

    @Test
    public void checkIframeWildcardOrigin() throws IOException {
        String id = adminClient.realm("master").clients().findByClientId(ADMIN_CONSOLE_CLIENT_ID).get(0).getId();
        ClientResource master = adminClient.realm("master").clients().get(id);
        ClientRepresentation rep = master.toRepresentation();
        List<String> org = rep.getWebOrigins();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            rep.setWebOrigins(Collections.singletonList("*"));
            master.update(rep);
            HttpGet get = new HttpGet(((((((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html/init?") + "client_id=") + (Constants.ADMIN_CONSOLE_CLIENT_ID)) + "&origin=") + "http://anything"));
            try (CloseableHttpResponse response = client.execute(get)) {
                Assert.assertEquals(204, response.getStatusLine().getStatusCode());
            }
        } finally {
            rep.setWebOrigins(org);
            master.update(rep);
        }
    }

    @Test
    public void checkIframeCache() throws IOException {
        String version = testingClient.server().fetch(new ServerVersion());
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html"));
            CloseableHttpResponse response = client.execute(get);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("no-cache, must-revalidate, no-transform, no-store", response.getHeaders("Cache-Control")[0].getValue());
            get = new HttpGet((((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth/realms/master/protocol/openid-connect/login-status-iframe.html?version=") + version));
            response = client.execute(get);
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertTrue(response.getHeaders("Cache-Control")[0].getValue().contains("max-age"));
        }
    }
}

