/**
 * Copyright 2008 Web Cohesion
 *
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
 * limitations under the License.
 */
package org.springframework.security.oauth.consumer.client;


import HMAC_SHA1SignatureMethod.SIGNATURE_NAME;
import OAuthConsumerParameter.oauth_consumer_key;
import OAuthConsumerParameter.oauth_nonce;
import OAuthConsumerParameter.oauth_signature;
import OAuthConsumerParameter.oauth_signature_method;
import OAuthConsumerParameter.oauth_timestamp;
import OAuthConsumerParameter.oauth_token;
import OAuthConsumerParameter.oauth_version;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth.common.signature.OAuthSignatureMethod;
import org.springframework.security.oauth.common.signature.OAuthSignatureMethodFactory;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.InvalidOAuthRealmException;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.net.DefaultOAuthURLStreamHandlerFactory;
import sun.net.www.protocol.http.Handler;


/**
 *
 *
 * @author Ryan Heaton
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class CoreOAuthConsumerSupportTests {
    @Mock
    private ProtectedResourceDetails details;

    /**
     * afterPropertiesSet
     */
    @Test
    public void testAfterPropertiesSet() throws Exception {
        try {
            new CoreOAuthConsumerSupport().afterPropertiesSet();
            Assert.fail("should required a protected resource details service.");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * readResouce
     */
    @Test
    public void testReadResouce() throws Exception {
        OAuthConsumerToken token = new OAuthConsumerToken();
        URL url = new URL("http://myhost.com/resource?with=some&query=params&too");
        final CoreOAuthConsumerSupportTests.ConnectionProps connectionProps = new CoreOAuthConsumerSupportTests.ConnectionProps();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        final CoreOAuthConsumerSupportTests.HttpURLConnectionForTestingPurposes connectionMock = new CoreOAuthConsumerSupportTests.HttpURLConnectionForTestingPurposes(url) {
            @Override
            public void setRequestMethod(String method) throws ProtocolException {
                connectionProps.method = method;
            }

            @Override
            public void setDoOutput(boolean dooutput) {
                connectionProps.doOutput = dooutput;
            }

            @Override
            public void connect() throws IOException {
                connectionProps.connected = true;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                connectionProps.outputStream = out;
                return out;
            }

            @Override
            public int getResponseCode() throws IOException {
                return connectionProps.responseCode;
            }

            @Override
            public String getResponseMessage() throws IOException {
                return connectionProps.responseMessage;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return inputStream;
            }

            @Override
            public String getHeaderField(String name) {
                return connectionProps.headerFields.get(name);
            }
        };
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport() {
            @Override
            public URL configureURLForProtectedAccess(URL url, OAuthConsumerToken accessToken, ProtectedResourceDetails details, String httpMethod, Map<String, String> additionalParameters) throws OAuthRequestFailedException {
                try {
                    return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile(), new CoreOAuthConsumerSupportTests.StreamHandlerForTestingPurposes(connectionMock));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String getOAuthQueryString(ProtectedResourceDetails details, OAuthConsumerToken accessToken, URL url, String httpMethod, Map<String, String> additionalParameters) {
                return "POSTBODY";
            }
        };
        support.setStreamHandlerFactory(new DefaultOAuthURLStreamHandlerFactory());
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn("realm1");
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAdditionalRequestHeaders()).thenReturn(null);
        try {
            support.readResource(details, url, "POST", token, null, null);
            Assert.fail("shouldn't have been a valid response code.");
        } catch (OAuthRequestFailedException e) {
            // fall through...
        }
        Assert.assertFalse(connectionProps.doOutput);
        Assert.assertEquals("POST", connectionProps.method);
        Assert.assertTrue(connectionProps.connected);
        connectionProps.reset();
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn(null);
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAdditionalRequestHeaders()).thenReturn(null);
        connectionProps.responseCode = 400;
        connectionProps.responseMessage = "Nasty";
        try {
            support.readResource(details, url, "POST", token, null, null);
            Assert.fail("shouldn't have been a valid response code.");
        } catch (OAuthRequestFailedException e) {
            // fall through...
        }
        Assert.assertFalse(connectionProps.doOutput);
        Assert.assertEquals("POST", connectionProps.method);
        Assert.assertTrue(connectionProps.connected);
        connectionProps.reset();
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn(null);
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAdditionalRequestHeaders()).thenReturn(null);
        connectionProps.responseCode = 401;
        connectionProps.responseMessage = "Bad Realm";
        connectionProps.headerFields.put("WWW-Authenticate", "realm=\"goodrealm\"");
        try {
            support.readResource(details, url, "POST", token, null, null);
            Assert.fail("shouldn't have been a valid response code.");
        } catch (InvalidOAuthRealmException e) {
            // fall through...
        }
        Assert.assertFalse(connectionProps.doOutput);
        Assert.assertEquals("POST", connectionProps.method);
        Assert.assertTrue(connectionProps.connected);
        connectionProps.reset();
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn(null);
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAdditionalRequestHeaders()).thenReturn(null);
        connectionProps.responseCode = 200;
        connectionProps.responseMessage = "Congrats";
        Assert.assertSame(inputStream, support.readResource(details, url, "GET", token, null, null));
        Assert.assertFalse(connectionProps.doOutput);
        Assert.assertEquals("GET", connectionProps.method);
        Assert.assertTrue(connectionProps.connected);
        connectionProps.reset();
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn(null);
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(false);
        Mockito.when(details.getAdditionalRequestHeaders()).thenReturn(null);
        connectionProps.responseCode = 200;
        connectionProps.responseMessage = "Congrats";
        Assert.assertSame(inputStream, support.readResource(details, url, "POST", token, null, null));
        Assert.assertEquals("POSTBODY", new String(((ByteArrayOutputStream) (connectionProps.outputStream)).toByteArray()));
        Assert.assertTrue(connectionProps.doOutput);
        Assert.assertEquals("POST", connectionProps.method);
        Assert.assertTrue(connectionProps.connected);
        connectionProps.reset();
    }

    /**
     * configureURLForProtectedAccess
     */
    @Test
    public void testConfigureURLForProtectedAccess() throws Exception {
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport() {
            // Inherited.
            @Override
            public String getOAuthQueryString(ProtectedResourceDetails details, OAuthConsumerToken accessToken, URL url, String httpMethod, Map<String, String> additionalParameters) {
                return "myquerystring";
            }
        };
        support.setStreamHandlerFactory(new DefaultOAuthURLStreamHandlerFactory());
        OAuthConsumerToken token = new OAuthConsumerToken();
        URL url = new URL("https://myhost.com/somepath?with=some&query=params&too");
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Assert.assertEquals("https://myhost.com/somepath?with=some&query=params&too", support.configureURLForProtectedAccess(url, token, details, "GET", null).toString());
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(false);
        Assert.assertEquals("https://myhost.com/somepath?myquerystring", support.configureURLForProtectedAccess(url, token, details, "GET", null).toString());
        Assert.assertEquals("https://myhost.com/somepath?with=some&query=params&too", support.configureURLForProtectedAccess(url, token, details, "POST", null).toString());
        Assert.assertEquals("https://myhost.com/somepath?with=some&query=params&too", support.configureURLForProtectedAccess(url, token, details, "PUT", null).toString());
    }

    /**
     * test getAuthorizationHeader
     */
    @Test
    public void testGetAuthorizationHeader() throws Exception {
        final TreeMap<String, Set<CharSequence>> params = new TreeMap<String, Set<CharSequence>>();
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport() {
            @Override
            protected Map<String, Set<CharSequence>> loadOAuthParameters(ProtectedResourceDetails details, URL requestURL, OAuthConsumerToken requestToken, String httpMethod, Map<String, String> additionalParameters) {
                return params;
            }
        };
        URL url = new URL("https://myhost.com/somepath?with=some&query=params&too");
        OAuthConsumerToken token = new OAuthConsumerToken();
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(false);
        Assert.assertNull(support.getAuthorizationHeader(details, token, url, "POST", null));
        params.put("with", Collections.singleton(((CharSequence) ("some"))));
        params.put("query", Collections.singleton(((CharSequence) ("params"))));
        params.put("too", null);
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn("myrealm");
        Assert.assertEquals("OAuth realm=\"myrealm\", query=\"params\", with=\"some\"", support.getAuthorizationHeader(details, token, url, "POST", null));
        params.put(oauth_consumer_key.toString(), Collections.singleton(((CharSequence) ("mykey"))));
        params.put(oauth_nonce.toString(), Collections.singleton(((CharSequence) ("mynonce"))));
        params.put(oauth_timestamp.toString(), Collections.singleton(((CharSequence) ("myts"))));
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn("myrealm");
        Assert.assertEquals("OAuth realm=\"myrealm\", oauth_consumer_key=\"mykey\", oauth_nonce=\"mynonce\", oauth_timestamp=\"myts\", query=\"params\", with=\"some\"", support.getAuthorizationHeader(details, token, url, "POST", null));
    }

    /**
     * getOAuthQueryString
     */
    @Test
    public void testGetOAuthQueryString() throws Exception {
        final TreeMap<String, Set<CharSequence>> params = new TreeMap<String, Set<CharSequence>>();
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport() {
            @Override
            protected Map<String, Set<CharSequence>> loadOAuthParameters(ProtectedResourceDetails details, URL requestURL, OAuthConsumerToken requestToken, String httpMethod, Map<String, String> additionalParameters) {
                return params;
            }
        };
        URL url = new URL("https://myhost.com/somepath?with=some&query=params&too");
        OAuthConsumerToken token = new OAuthConsumerToken();
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        params.put("with", Collections.singleton(((CharSequence) ("some"))));
        params.put("query", Collections.singleton(((CharSequence) ("params"))));
        params.put("too", null);
        params.put(oauth_consumer_key.toString(), Collections.singleton(((CharSequence) ("mykey"))));
        params.put(oauth_nonce.toString(), Collections.singleton(((CharSequence) ("mynonce"))));
        params.put(oauth_timestamp.toString(), Collections.singleton(((CharSequence) ("myts"))));
        Assert.assertEquals("query=params&too&with=some", support.getOAuthQueryString(details, token, url, "POST", null));
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(false);
        params.put("with", Collections.singleton(((CharSequence) ("some"))));
        params.put("query", Collections.singleton(((CharSequence) ("params"))));
        params.put("too", null);
        params.put(oauth_consumer_key.toString(), Collections.singleton(((CharSequence) ("mykey"))));
        params.put(oauth_nonce.toString(), Collections.singleton(((CharSequence) ("mynonce"))));
        params.put(oauth_timestamp.toString(), Collections.singleton(((CharSequence) ("myts"))));
        Assert.assertEquals("oauth_consumer_key=mykey&oauth_nonce=mynonce&oauth_timestamp=myts&query=params&too&with=some", support.getOAuthQueryString(details, token, url, "POST", null));
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(false);
        params.put("with", Collections.singleton(((CharSequence) ("some"))));
        String encoded_space = URLEncoder.encode(" ", "utf-8");
        params.put("query", Collections.singleton(((CharSequence) ("params spaced"))));
        params.put("too", null);
        params.put(oauth_consumer_key.toString(), Collections.singleton(((CharSequence) ("mykey"))));
        params.put(oauth_nonce.toString(), Collections.singleton(((CharSequence) ("mynonce"))));
        params.put(oauth_timestamp.toString(), Collections.singleton(((CharSequence) ("myts"))));
        Assert.assertEquals((("oauth_consumer_key=mykey&oauth_nonce=mynonce&oauth_timestamp=myts&query=params" + encoded_space) + "spaced&too&with=some"), support.getOAuthQueryString(details, token, url, "POST", null));
    }

    /**
     * getTokenFromProvider
     */
    @Test
    public void testGetTokenFromProvider() throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream("oauth_token=mytoken&oauth_token_secret=mytokensecret".getBytes("UTF-8"));
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport() {
            @Override
            protected InputStream readResource(ProtectedResourceDetails details, URL url, String httpMethod, OAuthConsumerToken token, Map<String, String> additionalParameters, Map<String, String> additionalRequestHeaders) {
                return in;
            }
        };
        URL url = new URL("https://myhost.com/somepath?with=some&query=params&too");
        Mockito.when(details.getId()).thenReturn("resourceId");
        OAuthConsumerToken token = support.getTokenFromProvider(details, url, "POST", null, null);
        Assert.assertFalse(token.isAccessToken());
        Assert.assertEquals("mytoken", token.getValue());
        Assert.assertEquals("mytokensecret", token.getSecret());
        Assert.assertEquals("resourceId", token.getResourceId());
    }

    /**
     * loadOAuthParameters
     */
    @Test
    public void testLoadOAuthParameters() throws Exception {
        URL url = new URL("https://myhost.com/somepath?with=some&query=params&too");
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport() {
            @Override
            protected String getSignatureBaseString(Map<String, Set<CharSequence>> oauthParams, URL requestURL, String httpMethod) {
                return "MYSIGBASESTRING";
            }
        };
        OAuthSignatureMethodFactory sigFactory = Mockito.mock(OAuthSignatureMethodFactory.class);
        support.setSignatureFactory(sigFactory);
        OAuthConsumerToken token = new OAuthConsumerToken();
        OAuthSignatureMethod sigMethod = Mockito.mock(OAuthSignatureMethod.class);
        Mockito.when(details.getConsumerKey()).thenReturn("my-consumer-key");
        Mockito.when(details.getSignatureMethod()).thenReturn(SIGNATURE_NAME);
        Mockito.when(details.getSignatureMethod()).thenReturn(SIGNATURE_NAME);
        SharedConsumerSecret secret = new SharedConsumerSecretImpl("shh!!!");
        Mockito.when(details.getSharedSecret()).thenReturn(secret);
        Mockito.when(sigFactory.getSignatureMethod(SIGNATURE_NAME, secret, null)).thenReturn(sigMethod);
        Mockito.when(sigMethod.sign("MYSIGBASESTRING")).thenReturn("MYSIGNATURE");
        Map<String, Set<CharSequence>> params = support.loadOAuthParameters(details, url, token, "POST", null);
        Assert.assertEquals("some", params.remove("with").iterator().next().toString());
        Assert.assertEquals("params", params.remove("query").iterator().next().toString());
        Assert.assertTrue(params.containsKey("too"));
        Assert.assertTrue(params.remove("too").isEmpty());
        Assert.assertNull(params.remove(oauth_token.toString()));
        Assert.assertNotNull(params.remove(oauth_nonce.toString()).iterator().next());
        Assert.assertEquals("my-consumer-key", params.remove(oauth_consumer_key.toString()).iterator().next());
        Assert.assertEquals("MYSIGNATURE", params.remove(oauth_signature.toString()).iterator().next());
        Assert.assertEquals("1.0", params.remove(oauth_version.toString()).iterator().next());
        Assert.assertEquals(SIGNATURE_NAME, params.remove(oauth_signature_method.toString()).iterator().next());
        Assert.assertTrue(((Long.parseLong(params.remove(oauth_timestamp.toString()).iterator().next().toString())) <= ((System.currentTimeMillis()) / 1000)));
        Assert.assertTrue(params.isEmpty());
    }

    /**
     * tests getting the signature base string.
     */
    @Test
    public void testGetSignatureBaseString() throws Exception {
        Map<String, Set<CharSequence>> oauthParams = new HashMap<String, Set<CharSequence>>();
        oauthParams.put("oauth_consumer_key", Collections.singleton(((CharSequence) ("dpf43f3p2l4k3l03"))));
        oauthParams.put("oauth_token", Collections.singleton(((CharSequence) ("nnch734d00sl2jdk"))));
        oauthParams.put("oauth_signature_method", Collections.singleton(((CharSequence) ("HMAC-SHA1"))));
        oauthParams.put("oauth_timestamp", Collections.singleton(((CharSequence) ("1191242096"))));
        oauthParams.put("oauth_nonce", Collections.singleton(((CharSequence) ("kllo9940pd9333jh"))));
        oauthParams.put("oauth_version", Collections.singleton(((CharSequence) ("1.0"))));
        oauthParams.put("file", Collections.singleton(((CharSequence) ("vacation.jpg"))));
        oauthParams.put("size", Collections.singleton(((CharSequence) ("original"))));
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport();
        String baseString = support.getSignatureBaseString(oauthParams, new URL("http://photos.example.net/photos"), "geT");
        Assert.assertEquals("GET&http%3A%2F%2Fphotos.example.net%2Fphotos&file%3Dvacation.jpg%26oauth_consumer_key%3Ddpf43f3p2l4k3l03%26oauth_nonce%3Dkllo9940pd9333jh%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1191242096%26oauth_token%3Dnnch734d00sl2jdk%26oauth_version%3D1.0%26size%3Doriginal", baseString);
    }

    @Test
    public void testGetSignatureBaseStringSimple() throws Exception {
        Map<String, Set<CharSequence>> oauthParams = new HashMap<String, Set<CharSequence>>();
        oauthParams.put("foo", Collections.singleton(((CharSequence) ("bar"))));
        oauthParams.put("bar", new LinkedHashSet<CharSequence>(Arrays.<CharSequence>asList("120", "24")));
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport();
        String baseString = support.getSignatureBaseString(oauthParams, new URL("http://photos.example.net/photos"), "get");
        Assert.assertEquals("GET&http%3A%2F%2Fphotos.example.net%2Fphotos&bar%3D120%26bar%3D24%26foo%3Dbar", baseString);
    }

    // See SECOAUTH-383
    @Test
    public void testGetSignatureBaseStringMultivaluedLast() throws Exception {
        Map<String, Set<CharSequence>> oauthParams = new HashMap<String, Set<CharSequence>>();
        oauthParams.put("foo", Collections.singleton(((CharSequence) ("bar"))));
        oauthParams.put("pin", new LinkedHashSet<CharSequence>(Arrays.<CharSequence>asList("2", "1")));
        CoreOAuthConsumerSupport support = new CoreOAuthConsumerSupport();
        String baseString = support.getSignatureBaseString(oauthParams, new URL("http://photos.example.net/photos"), "get");
        Assert.assertEquals("GET&http%3A%2F%2Fphotos.example.net%2Fphotos&foo%3Dbar%26pin%3D1%26pin%3D2", baseString);
    }

    static class StreamHandlerForTestingPurposes extends Handler {
        private final CoreOAuthConsumerSupportTests.HttpURLConnectionForTestingPurposes connection;

        public StreamHandlerForTestingPurposes(CoreOAuthConsumerSupportTests.HttpURLConnectionForTestingPurposes connection) {
            this.connection = connection;
        }

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return connection;
        }

        @Override
        protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
            return connection;
        }
    }

    static class HttpURLConnectionForTestingPurposes extends HttpURLConnection {
        /**
         * Constructor for the HttpURLConnection.
         *
         * @param u
         * 		the URL
         */
        public HttpURLConnectionForTestingPurposes(URL u) {
            super(u);
        }

        public void disconnect() {
        }

        public boolean usingProxy() {
            return false;
        }

        public void connect() throws IOException {
        }
    }

    static class ConnectionProps {
        public int responseCode;

        public String responseMessage;

        public String method;

        public Boolean doOutput;

        public Boolean connected;

        public OutputStream outputStream;

        public final Map<String, String> headerFields = new TreeMap<String, String>();

        public void reset() {
            this.responseCode = 0;
            this.responseMessage = null;
            this.method = null;
            this.doOutput = null;
            this.connected = null;
            this.outputStream = null;
            this.headerFields.clear();
        }
    }
}

