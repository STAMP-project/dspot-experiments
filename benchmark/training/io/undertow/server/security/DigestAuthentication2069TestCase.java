/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.security;


import AuthenticationInfoToken.NEXT_NONCE;
import DigestAlgorithm.MD5;
import DigestAuthorizationToken.DIGEST_URI;
import DigestAuthorizationToken.RESPONSE;
import DigestAuthorizationToken.USERNAME;
import DigestWWWAuthenticateToken.ALGORITHM;
import DigestWWWAuthenticateToken.MESSAGE_QOP;
import DigestWWWAuthenticateToken.NONCE;
import DigestWWWAuthenticateToken.REALM;
import DigestWWWAuthenticateToken.STALE;
import EventType.AUTHENTICATED;
import EventType.FAILED_AUTHENTICATION;
import StatusCodes.OK;
import StatusCodes.UNAUTHORIZED;
import io.undertow.security.impl.AuthenticationInfoToken;
import io.undertow.security.impl.DigestWWWAuthenticateToken;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.Headers;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * For Digest authentication we support RFC2617, however this includes a requirement to allow a fall back to RFC2069, this test
 * case is to test the RFC2069 form of Digest authentication.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
// Test choosing different algorithm.
// Different URI - Test not matching the request as well.
// Different Method
@RunWith(DefaultServer.class)
public class DigestAuthentication2069TestCase extends AuthenticationTestBase {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static final String REALM_NAME = "Digest_Realm";

    /**
     * Test for a successful authentication.
     */
    @Test
    public void testDigestSuccess() throws Exception {
        TestHttpClient client = new TestHttpClient();
        HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
        HttpResponse result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        String value = values[0].getValue();
        Assert.assertTrue(value.startsWith(Headers.DIGEST.toString()));
        Map<DigestWWWAuthenticateToken, String> parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        Assert.assertEquals(DigestAuthentication2069TestCase.REALM_NAME, parsedHeader.get(REALM));
        Assert.assertEquals(MD5.getToken(), parsedHeader.get(ALGORITHM));
        Assert.assertFalse(parsedHeader.containsKey(MESSAGE_QOP));
        String nonce = parsedHeader.get(NONCE);
        String response = createResponse("userOne", DigestAuthentication2069TestCase.REALM_NAME, "passwordOne", "GET", "/", nonce);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        StringBuilder sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"userOne\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        values = result.getHeaders("Authentication-Info");
        Assert.assertEquals(1, values.length);
        Map<AuthenticationInfoToken, String> parsedAuthInfo = AuthenticationInfoToken.parseHeader(values[0].getValue());
        nonce = parsedAuthInfo.get(NEXT_NONCE);
        response = createResponse("userOne", DigestAuthentication2069TestCase.REALM_NAME, "passwordOne", "GET", "/", nonce);
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"userOne\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
    }

    /**
     * Test that a request is correctly rejected with a bad user name.
     *
     * In this case both the supplied username is wrong and also the generated response can not be valid as there is no
     * corresponding user.
     */
    @Test
    public void testBadUserName() throws Exception {
        TestHttpClient client = new TestHttpClient();
        HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
        HttpResponse result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        String value = values[0].getValue();
        Assert.assertTrue(value.startsWith(Headers.DIGEST.toString()));
        Map<DigestWWWAuthenticateToken, String> parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        Assert.assertEquals(DigestAuthentication2069TestCase.REALM_NAME, parsedHeader.get(REALM));
        Assert.assertEquals(MD5.getToken(), parsedHeader.get(ALGORITHM));
        String nonce = parsedHeader.get(NONCE);
        String response = createResponse("badUser", DigestAuthentication2069TestCase.REALM_NAME, "passwordOne", "GET", "/", nonce);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        StringBuilder sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"badUser\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        AuthenticationTestBase.assertSingleNotificationType(FAILED_AUTHENTICATION);
    }

    /**
     * Test that a request is correctly rejected if a bad password is used to generate the response value.
     */
    @Test
    public void testBadPassword() throws Exception {
        TestHttpClient client = new TestHttpClient();
        HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
        HttpResponse result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        String value = values[0].getValue();
        Assert.assertTrue(value.startsWith(Headers.DIGEST.toString()));
        Map<DigestWWWAuthenticateToken, String> parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        Assert.assertEquals(DigestAuthentication2069TestCase.REALM_NAME, parsedHeader.get(REALM));
        Assert.assertEquals(MD5.getToken(), parsedHeader.get(ALGORITHM));
        String nonce = parsedHeader.get(NONCE);
        String response = createResponse("userOne", DigestAuthentication2069TestCase.REALM_NAME, "badPassword", "GET", "/", nonce);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        StringBuilder sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"userOne\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        AuthenticationTestBase.assertSingleNotificationType(FAILED_AUTHENTICATION);
    }

    /**
     * Test that for a valid username and password if an invalid nonce is used the request should be rejected with the nonce
     * marked as stale, using the replacement nonce should then work.
     */
    @Test
    public void testDifferentNonce() throws Exception {
        TestHttpClient client = new TestHttpClient();
        HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
        HttpResponse result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        String value = values[0].getValue();
        Assert.assertTrue(value.startsWith(Headers.DIGEST.toString()));
        Map<DigestWWWAuthenticateToken, String> parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        Assert.assertEquals(DigestAuthentication2069TestCase.REALM_NAME, parsedHeader.get(REALM));
        Assert.assertEquals(MD5.getToken(), parsedHeader.get(ALGORITHM));
        String nonce = "AU1aCIiy48ENMTM1MTE3OTUxMDU2OLrHnBlV2GBzzguCWOPET+0=";
        String response = createResponse("userOne", DigestAuthentication2069TestCase.REALM_NAME, "passwordOne", "GET", "/", nonce);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        StringBuilder sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"userOne\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        value = values[0].getValue();
        Assert.assertTrue(value.startsWith(Headers.DIGEST.toString()));
        parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        Assert.assertEquals(DigestAuthentication2069TestCase.REALM_NAME, parsedHeader.get(REALM));
        Assert.assertEquals(MD5.getToken(), parsedHeader.get(ALGORITHM));
        Assert.assertEquals("true", parsedHeader.get(STALE));
        nonce = parsedHeader.get(NONCE);
        response = createResponse("userOne", DigestAuthentication2069TestCase.REALM_NAME, "passwordOne", "GET", "/", nonce);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"userOne\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        // The additional round trip for the bad nonce should not trigger a security notification.
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
    }

    /**
     * Test that in RFC2069 mode nonce re-use is rejected.
     */
    @Test
    public void testNonceReUse() throws Exception {
        TestHttpClient client = new TestHttpClient();
        HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
        HttpResponse result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        String value = values[0].getValue();
        Assert.assertTrue(value.startsWith(Headers.DIGEST.toString()));
        Map<DigestWWWAuthenticateToken, String> parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        Assert.assertEquals(DigestAuthentication2069TestCase.REALM_NAME, parsedHeader.get(REALM));
        Assert.assertEquals(MD5.getToken(), parsedHeader.get(ALGORITHM));
        String nonce = parsedHeader.get(NONCE);
        String response = createResponse("userOne", DigestAuthentication2069TestCase.REALM_NAME, "passwordOne", "GET", "/", nonce);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        StringBuilder sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"userOne\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        Assert.assertEquals(1, values.length);
        value = values[0].getValue();
        Assert.assertTrue(value.startsWith(Headers.DIGEST.toString()));
        parsedHeader = DigestWWWAuthenticateToken.parseHeader(value.substring(7));
        Assert.assertEquals(DigestAuthentication2069TestCase.REALM_NAME, parsedHeader.get(REALM));
        Assert.assertEquals(MD5.getToken(), parsedHeader.get(ALGORITHM));
        Assert.assertEquals("true", parsedHeader.get(STALE));
        nonce = parsedHeader.get(NONCE);
        response = createResponse("userOne", DigestAuthentication2069TestCase.REALM_NAME, "passwordOne", "GET", "/", nonce);
        client = new TestHttpClient();
        get = new HttpGet(DefaultServer.getDefaultServerURL());
        sb = new StringBuilder(Headers.DIGEST.toString());
        sb.append(" ");
        sb.append(USERNAME.getName()).append("=").append("\"userOne\"").append(",");
        sb.append(DigestAuthorizationToken.REALM.getName()).append("=\"").append(DigestAuthentication2069TestCase.REALM_NAME).append("\",");
        sb.append(DigestAuthorizationToken.NONCE.getName()).append("=\"").append(nonce).append("\",");
        sb.append(DIGEST_URI.getName()).append("=\"/\",");
        sb.append(RESPONSE.getName()).append("=\"").append(response).append("\"");
        get.addHeader(Headers.AUTHORIZATION.toString(), sb.toString());
        result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        values = result.getHeaders("ProcessedBy");
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        // The additional round trip for the bad nonce should not trigger a security notification.
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
    }
}

