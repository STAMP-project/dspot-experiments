package org.springframework.security.oauth2.provider;


import HttpStatus.BAD_REQUEST;
import HttpStatus.NOT_ACCEPTABLE;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import OAuth2AccessToken.BEARER_TYPE;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.web.client.HttpClientErrorException;


/**
 *
 *
 * @author Ryan Heaton
 * @author Dave Syer
 */
public class ResourceOwnerPasswordProviderTests {
    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    @Rule
    public OAuth2ContextSetup context = OAuth2ContextSetup.standard(serverRunning);

    private ClientHttpResponse tokenEndpointResponse;

    @Test
    public void testUnauthenticated() throws Exception {
        // first make sure the resource is actually protected.
        Assert.assertEquals(UNAUTHORIZED, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
    }

    @Test
    public void testUnauthenticatedErrorMessage() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Void> response = serverRunning.getForResponse("/sparklr2/photos?format=json", headers);
        Assert.assertEquals(UNAUTHORIZED, response.getStatusCode());
        String authenticate = response.getHeaders().getFirst("WWW-Authenticate");
        Assert.assertTrue(("Wrong header: " + authenticate), authenticate.contains("error=\"unauthorized\""));
    }

    @Test
    public void testInvalidTokenErrorMessage() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer FOO");
        ResponseEntity<Void> response = serverRunning.getForResponse("/sparklr2/photos?format=json", headers);
        Assert.assertEquals(UNAUTHORIZED, response.getStatusCode());
        String authenticate = response.getHeaders().getFirst("WWW-Authenticate");
        Assert.assertTrue(("Wrong header: " + authenticate), authenticate.contains("error=\"invalid_token\""));
    }

    @Test
    @OAuth2ContextConfiguration(ResourceOwnerPasswordProviderTests.ResourceOwner.class)
    public void testTokenObtainedWithHeaderAuthentication() throws Exception {
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
        int expiry = context.getAccessToken().getExpiresIn();
        Assert.assertTrue(("Expiry not overridden in config: " + expiry), (expiry < 1000));
        Assert.assertEquals(new MediaType("application", "json", Charset.forName("UTF-8")), tokenEndpointResponse.getHeaders().getContentType());
    }

    @Test
    @OAuth2ContextConfiguration(ResourceOwnerPasswordProviderTests.ResourceOwnerQuery.class)
    public void testTokenObtainedWithQueryAuthentication() throws Exception {
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
    }

    @Test
    @OAuth2ContextConfiguration(resource = ResourceOwnerPasswordProviderTests.ResourceOwnerNoSecretProvided.class, initialize = false)
    public void testTokenNotGrantedIfSecretNotProvided() throws Exception {
        try {
            context.getAccessToken();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(UNAUTHORIZED, e.getStatusCode());
            List<String> values = tokenEndpointResponse.getHeaders().get("WWW-Authenticate");
            Assert.assertEquals(1, values.size());
            String header = values.get(0);
            Assert.assertTrue(("Wrong header " + header), header.contains("Basic realm=\"sparklr2/client\""));
        }
    }

    @Test
    @OAuth2ContextConfiguration(ResourceOwnerPasswordProviderTests.ResourceOwnerSecretProvidedInForm.class)
    public void testSecretProvidedInForm() throws Exception {
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
    }

    @Test
    @OAuth2ContextConfiguration(ResourceOwnerPasswordProviderTests.ResourceOwnerSecretProvided.class)
    public void testSecretProvidedInHeader() throws Exception {
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
    }

    @Test
    @OAuth2ContextConfiguration(resource = ResourceOwnerPasswordProviderTests.InvalidGrantType.class, initialize = false)
    public void testInvalidGrantType() throws Exception {
        // The error comes back as additional information because OAuth2AccessToken is so extensible!
        try {
            context.getAccessToken();
        } catch (Exception e) {
            // assertEquals("invalid_client", e.getOAuth2ErrorCode());
        }
        Assert.assertEquals(UNAUTHORIZED, tokenEndpointResponse.getStatusCode());
        List<String> newCookies = tokenEndpointResponse.getHeaders().get("Set-Cookie");
        if ((newCookies != null) && (!(newCookies.isEmpty()))) {
            Assert.fail((("No cookies should be set. Found: " + (newCookies.get(0))) + "."));
        }
    }

    @Test
    public void testUserMessageIsProtectedResource() throws Exception {
        Assert.assertEquals(UNAUTHORIZED, serverRunning.getStatusCode("/sparklr2/photos/user/message"));
    }

    @Test
    @OAuth2ContextConfiguration(ResourceOwnerPasswordProviderTests.ResourceOwnerWithTrustedClient.class)
    public void testClientRoleBasedSecurity() throws Exception {
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos/user/message"));
    }

    /**
     * tests a happy-day flow of the native application provider.
     */
    @Test
    @OAuth2ContextConfiguration(ResourceOwnerPasswordProviderTests.ResourceOwnerWithTrustedClient.class)
    public void testUnsupportedMediaType() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(APPLICATION_XML));
        // Oddly enough this passes - the payload is a String so the message converter thinks it can handle it
        // the caller will get a surprise when he finds that the response is not actually XML, but that's a different
        // story.
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos/user/message", headers));
    }

    /**
     * tests that we get the correct error response if the media type is unacceptable.
     */
    @Test
    public void testUnsupportedMediaTypeWithInvalidToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("%s %s", BEARER_TYPE, "FOO"));
        headers.setAccept(Arrays.asList(MediaType.valueOf("text/foo")));
        Assert.assertEquals(NOT_ACCEPTABLE, serverRunning.getStatusCode("/sparklr2/photos/user/message", headers));
    }

    /**
     * tests that we get the correct error response if the media type is unacceptable.
     */
    @Test
    public void testMissingGrantType() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Basic %s", new String(Base64.encode("my-trusted-client:".getBytes()))));
        headers.setAccept(Arrays.asList(APPLICATION_JSON));
        ResponseEntity<String> response = serverRunning.postForString("/sparklr2/oauth/token", headers, new org.springframework.util.LinkedMultiValueMap<String, String>());
        Assert.assertEquals(BAD_REQUEST, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("invalid_request"));
    }

    static class ResourceOwner extends ResourceOwnerPasswordResourceDetails {
        public ResourceOwner(Object target) {
            setClientId("my-trusted-client");
            setScope(Arrays.asList("read"));
            setId(getClientId());
            setUsername("marissa");
            setPassword("koala");
            ResourceOwnerPasswordProviderTests test = ((ResourceOwnerPasswordProviderTests) (target));
            setAccessTokenUri(test.serverRunning.getUrl("/sparklr2/oauth/token"));
        }
    }

    static class ResourceOwnerQuery extends ResourceOwnerPasswordProviderTests.ResourceOwner {
        public ResourceOwnerQuery(Object target) {
            super(target);
            setAuthenticationScheme(AuthenticationScheme.query);
        }
    }

    static class ResourceOwnerNoSecretProvided extends ResourceOwnerPasswordProviderTests.ResourceOwner {
        public ResourceOwnerNoSecretProvided(Object target) {
            super(target);
            setClientId("my-trusted-client-with-secret");
        }
    }

    static class ResourceOwnerSecretProvided extends ResourceOwnerPasswordProviderTests.ResourceOwner {
        public ResourceOwnerSecretProvided(Object target) {
            super(target);
            setClientId("my-trusted-client-with-secret");
            setClientSecret("somesecret");
        }
    }

    static class ResourceOwnerSecretProvidedInForm extends ResourceOwnerPasswordProviderTests.ResourceOwnerSecretProvided {
        public ResourceOwnerSecretProvidedInForm(Object target) {
            super(target);
            setAuthenticationScheme(AuthenticationScheme.form);
        }
    }

    static class InvalidGrantType extends ResourceOwnerPasswordProviderTests.ResourceOwner {
        public InvalidGrantType(Object target) {
            super(target);
            setClientId("my-untrusted-client-with-registered-redirect");
        }
    }

    static class ResourceOwnerWithTrustedClient extends ResourceOwnerPasswordProviderTests.ResourceOwner {
        public ResourceOwnerWithTrustedClient(Object target) {
            super(target);
            setClientId("my-trusted-client");
            setScope(Arrays.asList("trust"));
        }
    }
}

