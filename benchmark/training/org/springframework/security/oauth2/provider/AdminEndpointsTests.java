package org.springframework.security.oauth2.provider;


import HttpMethod.DELETE;
import HttpStatus.FORBIDDEN;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import MediaType.APPLICATION_JSON;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;


/**
 *
 *
 * @author Dave Syer
 */
public class AdminEndpointsTests {
    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    @Rule
    public OAuth2ContextSetup context = OAuth2ContextSetup.standard(serverRunning);

    @Test
    @OAuth2ContextConfiguration(AdminEndpointsTests.ResourceOwnerReadOnly.class)
    public void testListTokensByUser() throws Exception {
        ResponseEntity<String> result = serverRunning.getForString("/sparklr2/oauth/clients/my-trusted-client/users/marissa/tokens");
        Assert.assertEquals(OK, result.getStatusCode());
        // System.err.println(result.getBody());
        Assert.assertTrue(result.getBody().contains(context.getAccessToken().getValue()));
    }

    @Test
    @OAuth2ContextConfiguration(AdminEndpointsTests.ResourceOwnerWriteOnly.class)
    public void testRevokeTokenByUser() throws Exception {
        OAuth2AccessToken token = context.getAccessToken();
        String tokenValueBeforeDeletion = token.getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(APPLICATION_JSON));
        HttpEntity<?> request = new HttpEntity<Void>(headers);
        Assert.assertEquals(NO_CONTENT, serverRunning.getRestTemplate().exchange(serverRunning.getUrl("/sparklr2/oauth/users/{user}/tokens/{token}"), DELETE, request, Void.class, "marissa", token.getValue()).getStatusCode());
        try {
            // The request above will delete the oauth token so that the next request will initially fail. However,
            // the failure will be detected and a new access token will be obtained.  The new access token
            // only has "write" scope and the requested resource needs "read" scope.  So, an insufficient_scope
            // exception should be thrown.
            ResponseEntity<String> result = serverRunning.getForString("/sparklr2/oauth/clients/my-client-with-registered-redirect/users/marissa/tokens", headers);
            Assert.fail("Should have thrown an exception");
            Assert.assertNotNull(result);
        } catch (InsufficientScopeException ex) {
            Assert.assertEquals(FORBIDDEN.value(), ex.getHttpErrorCode());
            Assert.assertEquals("insufficient_scope", ex.getOAuth2ErrorCode());
            String secondTokenWithWriteOnlyScope = context.getOAuth2ClientContext().getAccessToken().getValue();
            Assert.assertNotNull(secondTokenWithWriteOnlyScope);
            Assert.assertFalse(secondTokenWithWriteOnlyScope.equals(tokenValueBeforeDeletion));
        }
    }

    @Test
    @OAuth2ContextConfiguration(AdminEndpointsTests.ClientCredentialsReadOnly.class)
    public void testClientListsTokensOfUser() throws Exception {
        ResponseEntity<String> result = serverRunning.getForString("/sparklr2/oauth/clients/my-client-with-registered-redirect/users/marissa/tokens");
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertTrue(result.getBody().startsWith("["));
        Assert.assertTrue(result.getBody().endsWith("]"));
        Assert.assertTrue(((result.getBody().length()) > 0));
    }

    @Test
    @OAuth2ContextConfiguration(AdminEndpointsTests.ResourceOwnerReadOnly.class)
    public void testCannotListTokensOfAnotherUser() throws Exception {
        try {
            serverRunning.getStatusCode("/sparklr2/oauth/clients/my-client-with-registered-redirect/users/foo/tokens");
            Assert.fail("Should have thrown an exception");
        } catch (UserDeniedAuthorizationException ex) {
            // assertEquals(HttpStatus.FORBIDDEN.value(), ex.getHttpErrorCode());
            Assert.assertEquals("access_denied", ex.getOAuth2ErrorCode());
        }
    }

    @Test
    @OAuth2ContextConfiguration(AdminEndpointsTests.ClientCredentialsReadOnly.class)
    public void testListTokensByClient() throws Exception {
        ResponseEntity<String> result = serverRunning.getForString("/sparklr2/oauth/clients/my-client-with-registered-redirect/tokens");
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertTrue(result.getBody().contains(context.getAccessToken().getValue()));
    }

    @Test
    @OAuth2ContextConfiguration(AdminEndpointsTests.ResourceOwnerReadOnly.class)
    public void testUserCannotListTokensOfClient() throws Exception {
        try {
            serverRunning.getStatusCode("/sparklr2/oauth/clients/my-client-with-registered-redirect/tokens");
            Assert.fail("Should have thrown an exception");
        } catch (UserDeniedAuthorizationException ex) {
            // assertEquals(HttpStatus.FORBIDDEN.value(), ex.getHttpErrorCode());
            Assert.assertEquals("access_denied", ex.getOAuth2ErrorCode());
        }
    }

    static class ResourceOwnerReadOnly extends ResourceOwnerPasswordResourceDetails {
        public ResourceOwnerReadOnly(Object target) {
            setClientId("my-trusted-client");
            setId(getClientId());
            setScope(Arrays.asList("read"));
            setUsername("marissa");
            setPassword("koala");
            AdminEndpointsTests test = ((AdminEndpointsTests) (target));
            setAccessTokenUri(test.serverRunning.getUrl("/sparklr2/oauth/token"));
        }
    }

    static class ClientCredentialsReadOnly extends ClientCredentialsResourceDetails {
        public ClientCredentialsReadOnly(Object target) {
            setClientId("my-client-with-registered-redirect");
            setId(getClientId());
            setScope(Arrays.asList("read"));
            AdminEndpointsTests test = ((AdminEndpointsTests) (target));
            setAccessTokenUri(test.serverRunning.getUrl("/sparklr2/oauth/token"));
        }
    }

    static class ResourceOwnerWriteOnly extends AdminEndpointsTests.ResourceOwnerReadOnly {
        public ResourceOwnerWriteOnly(Object target) {
            super(target);
            setScope(Arrays.asList("write"));
        }
    }
}

