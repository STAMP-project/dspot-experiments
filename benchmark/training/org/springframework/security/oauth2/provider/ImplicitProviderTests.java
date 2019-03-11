package org.springframework.security.oauth2.provider;


import OAuth2Utils.USER_OAUTH_APPROVAL;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;


/**
 *
 *
 * @author Ryan Heaton
 * @author Dave Syer
 */
public class ImplicitProviderTests {
    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    @Rule
    public OAuth2ContextSetup context = OAuth2ContextSetup.standard(serverRunning);

    private HttpHeaders latestHeaders = null;

    @Test(expected = UserRedirectRequiredException.class)
    @OAuth2ContextConfiguration(resource = ImplicitProviderTests.AutoApproveImplicit.class, initialize = false)
    public void testRedirectRequiredForAuthentication() throws Exception {
        context.getAccessToken();
    }

    @Test
    @OAuth2ContextConfiguration(resource = ImplicitProviderTests.AutoApproveImplicit.class, initialize = false)
    public void testPostForAutomaticApprovalToken() throws Exception {
        final ImplicitAccessTokenProvider implicitProvider = new ImplicitAccessTokenProvider();
        implicitProvider.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(new ClientHttpRequestInterceptor() {
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                ClientHttpResponse result = execution.execute(request, body);
                latestHeaders = result.getHeaders();
                return result;
            }
        }));
        context.setAccessTokenProvider(implicitProvider);
        context.getAccessTokenRequest().setCookie(this.loginAndExtractCookie());
        Assert.assertNotNull(context.getAccessToken());
        Assert.assertTrue(("Wrong location header: " + (latestHeaders.getLocation().getFragment())), latestHeaders.getLocation().getFragment().contains("scope=read write trust"));
    }

    @Test
    @OAuth2ContextConfiguration(resource = ImplicitProviderTests.NonAutoApproveImplicit.class, initialize = false)
    public void testPostForNonAutomaticApprovalToken() throws Exception {
        context.getAccessTokenRequest().setCookie(this.loginAndExtractCookie());
        try {
            Assert.assertNotNull(context.getAccessToken());
            Assert.fail("Expected UserRedirectRequiredException");
        } catch (UserRedirectRequiredException e) {
            // ignore
        }
        // add user approval parameter for the second request
        context.getAccessTokenRequest().add(USER_OAUTH_APPROVAL, "true");
        context.getAccessTokenRequest().add("scope.read", "true");
        Assert.assertNotNull(context.getAccessToken());
    }

    static class AutoApproveImplicit extends ImplicitResourceDetails {
        public AutoApproveImplicit(Object target) {
            super();
            setClientId("my-less-trusted-autoapprove-client");
            setId(getClientId());
            setPreEstablishedRedirectUri("http://anywhere");
            ImplicitProviderTests test = ((ImplicitProviderTests) (target));
            setAccessTokenUri(test.serverRunning.getUrl("/sparklr2/oauth/authorize"));
            setUserAuthorizationUri(test.serverRunning.getUrl("/sparklr2/oauth/authorize"));
        }
    }

    static class NonAutoApproveImplicit extends ImplicitProviderTests.AutoApproveImplicit {
        public NonAutoApproveImplicit(Object target) {
            super(target);
            setClientId("my-less-trusted-client");
        }
    }
}

