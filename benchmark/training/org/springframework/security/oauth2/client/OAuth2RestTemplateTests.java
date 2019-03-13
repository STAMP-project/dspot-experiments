package org.springframework.security.oauth2.client;


import HttpMethod.GET;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.util.UriTemplate;


/**
 *
 *
 * @author Ryan Heaton
 * @author Dave Syer
 */
public class OAuth2RestTemplateTests {
    private BaseOAuth2ProtectedResourceDetails resource;

    private OAuth2RestTemplate restTemplate;

    private AccessTokenProvider accessTokenProvider = Mockito.mock(AccessTokenProvider.class);

    private ClientHttpRequest request;

    private HttpHeaders headers;

    @Test
    public void testNonBearerToken() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        token.setTokenType("MINE");
        restTemplate.getOAuth2ClientContext().setAccessToken(token);
        ClientHttpRequest http = restTemplate.createRequest(URI.create("https://nowhere.com/api/crap"), GET);
        String auth = http.getHeaders().getFirst("Authorization");
        Assert.assertTrue(auth.startsWith("MINE "));
    }

    @Test
    public void testCustomAuthenticator() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        token.setTokenType("MINE");
        restTemplate.setAuthenticator(new OAuth2RequestAuthenticator() {
            @Override
            public void authenticate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext clientContext, ClientHttpRequest req) {
                req.getHeaders().set("X-Authorization", (((clientContext.getAccessToken().getTokenType()) + " ") + "Nah-nah-na-nah-nah"));
            }
        });
        restTemplate.getOAuth2ClientContext().setAccessToken(token);
        ClientHttpRequest http = restTemplate.createRequest(URI.create("https://nowhere.com/api/crap"), GET);
        String auth = http.getHeaders().getFirst("X-Authorization");
        Assert.assertEquals("MINE Nah-nah-na-nah-nah", auth);
    }

    /**
     * tests appendQueryParameter
     */
    @Test
    public void testAppendQueryParameter() throws Exception {
        OAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        URI appended = restTemplate.appendQueryParameter(URI.create("https://graph.facebook.com/search?type=checkin"), token);
        Assert.assertEquals("https://graph.facebook.com/search?type=checkin&bearer_token=12345", appended.toString());
    }

    /**
     * tests appendQueryParameter
     */
    @Test
    public void testAppendQueryParameterWithNoExistingParameters() throws Exception {
        OAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        URI appended = restTemplate.appendQueryParameter(URI.create("https://graph.facebook.com/search"), token);
        Assert.assertEquals("https://graph.facebook.com/search?bearer_token=12345", appended.toString());
    }

    /**
     * tests encoding of access token value
     */
    @Test
    public void testDoubleEncodingOfParameterValue() throws Exception {
        OAuth2AccessToken token = new DefaultOAuth2AccessToken("1/qIxxx");
        URI appended = restTemplate.appendQueryParameter(URI.create("https://graph.facebook.com/search"), token);
        Assert.assertEquals("https://graph.facebook.com/search?bearer_token=1%2FqIxxx", appended.toString());
    }

    /**
     * tests no double encoding of existing query parameter
     */
    @Test
    public void testNonEncodingOfUriTemplate() throws Exception {
        OAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        UriTemplate uriTemplate = new UriTemplate("https://graph.facebook.com/fql?q={q}");
        URI expanded = uriTemplate.expand("[q: fql]");
        URI appended = restTemplate.appendQueryParameter(expanded, token);
        Assert.assertEquals("https://graph.facebook.com/fql?q=%5Bq:%20fql%5D&bearer_token=12345", appended.toString());
    }

    /**
     * tests URI with fragment value
     */
    @Test
    public void testFragmentUri() throws Exception {
        OAuth2AccessToken token = new DefaultOAuth2AccessToken("1234");
        URI appended = restTemplate.appendQueryParameter(URI.create("https://graph.facebook.com/search#foo"), token);
        Assert.assertEquals("https://graph.facebook.com/search?bearer_token=1234#foo", appended.toString());
    }

    /**
     * tests encoding of access token value passed in protected requests ref: SECOAUTH-90
     */
    @Test
    public void testDoubleEncodingOfAccessTokenValue() throws Exception {
        // try with fictitious token value with many characters to encode
        OAuth2AccessToken token = new DefaultOAuth2AccessToken("1 qI+x:y=z");
        // System.err.println(UriUtils.encodeQueryParam(token.getValue(), "UTF-8"));
        URI appended = restTemplate.appendQueryParameter(URI.create("https://graph.facebook.com/search"), token);
        Assert.assertEquals("https://graph.facebook.com/search?bearer_token=1+qI%2Bx%3Ay%3Dz", appended.toString());
    }

    @Test(expected = AccessTokenRequiredException.class)
    public void testNoRetryAccessDeniedExceptionForNoExistingToken() throws Exception {
        restTemplate.setAccessTokenProvider(new OAuth2RestTemplateTests.StubAccessTokenProvider());
        restTemplate.setRequestFactory(new ClientHttpRequestFactory() {
            public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
                throw new AccessTokenRequiredException(resource);
            }
        });
        restTemplate.doExecute(new URI("http://foo"), GET, new OAuth2RestTemplateTests.NullRequestCallback(), new OAuth2RestTemplateTests.SimpleResponseExtractor());
    }

    @Test
    public void testRetryAccessDeniedException() throws Exception {
        final AtomicBoolean failed = new AtomicBoolean(false);
        restTemplate.getOAuth2ClientContext().setAccessToken(new DefaultOAuth2AccessToken("TEST"));
        restTemplate.setAccessTokenProvider(new OAuth2RestTemplateTests.StubAccessTokenProvider());
        restTemplate.setRequestFactory(new ClientHttpRequestFactory() {
            public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
                if (!(failed.get())) {
                    failed.set(true);
                    throw new AccessTokenRequiredException(resource);
                }
                return request;
            }
        });
        Boolean result = restTemplate.doExecute(new URI("http://foo"), GET, new OAuth2RestTemplateTests.NullRequestCallback(), new OAuth2RestTemplateTests.SimpleResponseExtractor());
        Assert.assertTrue(result);
    }

    @Test
    public void testNewTokenAcquiredIfExpired() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("TEST");
        token.setExpiration(new Date(((System.currentTimeMillis()) - 1000)));
        restTemplate.getOAuth2ClientContext().setAccessToken(token);
        restTemplate.setAccessTokenProvider(new OAuth2RestTemplateTests.StubAccessTokenProvider());
        OAuth2AccessToken newToken = restTemplate.getAccessToken();
        Assert.assertNotNull(newToken);
        Assert.assertTrue((!(token.equals(newToken))));
    }

    @Test
    public void testTokenIsResetIfInvalid() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("TEST");
        token.setExpiration(new Date(((System.currentTimeMillis()) - 1000)));
        restTemplate.getOAuth2ClientContext().setAccessToken(token);
        restTemplate.setAccessTokenProvider(new OAuth2RestTemplateTests.StubAccessTokenProvider() {
            @Override
            public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest parameters) throws AccessDeniedException, UserRedirectRequiredException {
                throw new UserRedirectRequiredException("http://foo.com", Collections.<String, String>emptyMap());
            }
        });
        try {
            OAuth2AccessToken newToken = restTemplate.getAccessToken();
            Assert.assertNotNull(newToken);
            Assert.fail("Expected UserRedirectRequiredException");
        } catch (UserRedirectRequiredException e) {
            // planned
        }
        // context token should be reset as it clearly is invalid at this point
        Assert.assertNull(restTemplate.getOAuth2ClientContext().getAccessToken());
    }

    private final class SimpleResponseExtractor implements ResponseExtractor<Boolean> {
        public Boolean extractData(ClientHttpResponse response) throws IOException {
            return true;
        }
    }

    private static class NullRequestCallback implements RequestCallback {
        public void doWithRequest(ClientHttpRequest request) throws IOException {
        }
    }

    private static class StubAccessTokenProvider implements AccessTokenProvider {
        public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest parameters) throws AccessDeniedException, UserRedirectRequiredException {
            return new DefaultOAuth2AccessToken("FOO");
        }

        public boolean supportsRefresh(OAuth2ProtectedResourceDetails resource) {
            return false;
        }

        public OAuth2AccessToken refreshAccessToken(OAuth2ProtectedResourceDetails resource, OAuth2RefreshToken refreshToken, AccessTokenRequest request) throws UserRedirectRequiredException {
            return null;
        }

        public boolean supportsResource(OAuth2ProtectedResourceDetails resource) {
            return true;
        }
    }
}

