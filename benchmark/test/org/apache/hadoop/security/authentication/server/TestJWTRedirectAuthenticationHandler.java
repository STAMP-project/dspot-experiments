/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */
package org.apache.hadoop.security.authentication.server;


import JWTRedirectAuthenticationHandler.AUTHENTICATION_PROVIDER_URL;
import JWTRedirectAuthenticationHandler.EXPECTED_JWT_AUDIENCES;
import JWTRedirectAuthenticationHandler.JWT_COOKIE_NAME;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.minikdc.KerberosSecurityTestcase;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestJWTRedirectAuthenticationHandler extends KerberosSecurityTestcase {
    private static final String SERVICE_URL = "https://localhost:8888/resource";

    private static final String REDIRECT_LOCATION = "https://localhost:8443/authserver?originalUrl=" + (TestJWTRedirectAuthenticationHandler.SERVICE_URL);

    RSAPublicKey publicKey = null;

    RSAPrivateKey privateKey = null;

    JWTRedirectAuthenticationHandler handler = null;

    @Test
    public void testNoPublicKeyJWT() throws Exception {
        try {
            Properties props = getProperties();
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Assert.fail("alternateAuthentication should have thrown a ServletException");
        } catch (ServletException se) {
            Assert.assertTrue(se.getMessage().contains("Public key for signature validation must be provisioned"));
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testCustomCookieNameJWT() throws Exception {
        try {
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            props.put(JWT_COOKIE_NAME, "jowt");
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("jowt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Assert.assertEquals("bob", token.getUserName());
        } catch (ServletException se) {
            Assert.fail(("alternateAuthentication should NOT have thrown a ServletException: " + (se.getMessage())));
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testNoProviderURLJWT() throws Exception {
        try {
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            props.remove(AUTHENTICATION_PROVIDER_URL);
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Assert.fail("alternateAuthentication should have thrown an AuthenticationException");
        } catch (ServletException se) {
            Assert.assertTrue(se.getMessage().contains("Authentication provider URL must not be null"));
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testUnableToParseJWT() throws Exception {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            RSAPublicKey publicKey = ((RSAPublicKey) (kp.getPublic()));
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", ("ljm" + (jwt.serialize())));
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Mockito.verify(response).sendRedirect(TestJWTRedirectAuthenticationHandler.REDIRECT_LOCATION);
        } catch (ServletException se) {
            Assert.fail("alternateAuthentication should NOT have thrown a ServletException");
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testFailedSignatureValidationJWT() throws Exception {
        try {
            // Create a public key that doesn't match the one needed to
            // verify the signature - in order to make it fail verification...
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            RSAPublicKey publicKey = ((RSAPublicKey) (kp.getPublic()));
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Mockito.verify(response).sendRedirect(TestJWTRedirectAuthenticationHandler.REDIRECT_LOCATION);
        } catch (ServletException se) {
            Assert.fail("alternateAuthentication should NOT have thrown a ServletException");
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testExpiredJWT() throws Exception {
        try {
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) - 1000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Mockito.verify(response).sendRedirect(TestJWTRedirectAuthenticationHandler.REDIRECT_LOCATION);
        } catch (ServletException se) {
            Assert.fail("alternateAuthentication should NOT have thrown a ServletException");
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testNoExpirationJWT() throws Exception {
        try {
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            handler.init(props);
            SignedJWT jwt = getJWT("bob", null, privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Assert.assertNotNull("Token should not be null.", token);
            Assert.assertEquals("bob", token.getUserName());
        } catch (ServletException se) {
            Assert.fail("alternateAuthentication should NOT have thrown a ServletException");
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testInvalidAudienceJWT() throws Exception {
        try {
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            props.put(EXPECTED_JWT_AUDIENCES, "foo");
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Mockito.verify(response).sendRedirect(TestJWTRedirectAuthenticationHandler.REDIRECT_LOCATION);
        } catch (ServletException se) {
            Assert.fail("alternateAuthentication should NOT have thrown a ServletException");
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown a AuthenticationException");
        }
    }

    @Test
    public void testValidAudienceJWT() throws Exception {
        try {
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            props.put(EXPECTED_JWT_AUDIENCES, "bar");
            handler.init(props);
            SignedJWT jwt = getJWT("bob", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Assert.assertEquals("bob", token.getUserName());
        } catch (ServletException se) {
            Assert.fail("alternateAuthentication should NOT have thrown a ServletException");
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown an AuthenticationException");
        }
    }

    @Test
    public void testValidJWT() throws Exception {
        try {
            handler.setPublicKey(publicKey);
            Properties props = getProperties();
            handler.init(props);
            SignedJWT jwt = getJWT("alice", new Date(((new Date().getTime()) + 5000)), privateKey);
            Cookie cookie = new Cookie("hadoop-jwt", jwt.serialize());
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ cookie });
            Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(response.encodeRedirectURL(TestJWTRedirectAuthenticationHandler.SERVICE_URL)).thenReturn(TestJWTRedirectAuthenticationHandler.SERVICE_URL);
            AuthenticationToken token = handler.alternateAuthenticate(request, response);
            Assert.assertNotNull("Token should not be null.", token);
            Assert.assertEquals("alice", token.getUserName());
        } catch (ServletException se) {
            Assert.fail("alternateAuthentication should NOT have thrown a ServletException.");
        } catch (AuthenticationException ae) {
            Assert.fail("alternateAuthentication should NOT have thrown an AuthenticationException");
        }
    }

    @Test
    public void testOrigURLWithQueryString() throws Exception {
        handler.setPublicKey(publicKey);
        Properties props = getProperties();
        handler.init(props);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
        Mockito.when(request.getQueryString()).thenReturn("name=value");
        String loginURL = handler.constructLoginURL(request);
        Assert.assertNotNull("loginURL should not be null.", loginURL);
        Assert.assertEquals((("https://localhost:8443/authserver?originalUrl=" + (TestJWTRedirectAuthenticationHandler.SERVICE_URL)) + "?name=value"), loginURL);
    }

    @Test
    public void testOrigURLNoQueryString() throws Exception {
        handler.setPublicKey(publicKey);
        Properties props = getProperties();
        handler.init(props);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer(TestJWTRedirectAuthenticationHandler.SERVICE_URL));
        Mockito.when(request.getQueryString()).thenReturn(null);
        String loginURL = handler.constructLoginURL(request);
        Assert.assertNotNull("LoginURL should not be null.", loginURL);
        Assert.assertEquals(("https://localhost:8443/authserver?originalUrl=" + (TestJWTRedirectAuthenticationHandler.SERVICE_URL)), loginURL);
    }
}

