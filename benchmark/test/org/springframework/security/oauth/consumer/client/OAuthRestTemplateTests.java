package org.springframework.security.oauth.consumer.client;


import java.util.Collections;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.test.web.client.MockRestServiceServer;


@RunWith(MockitoJUnitRunner.class)
public class OAuthRestTemplateTests {
    @Mock
    private ProtectedResourceDetails details;

    @Test
    public void testOAuthRestTemplateNoAdditionalParameters() {
        String url = "http://myhost.com/resource?with=some&query=params&too";
        Mockito.when(details.getSignatureMethod()).thenReturn("HMAC-SHA1");
        Mockito.when(details.getConsumerKey()).thenReturn("consumerKey");
        Mockito.when(details.getSharedSecret()).thenReturn(new SharedConsumerSecretImpl("consumerSecret"));
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn("realm");
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAdditionalRequestHeaders()).thenReturn(null);
        Mockito.when(details.getAdditionalParameters()).thenReturn(null);
        OAuthRestTemplate restTemplate = new OAuthRestTemplate(details);
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(requestTo(url)).andExpect(method(POST)).andExpect(headerContains("Authorization", "OAuth realm=\"realm\"")).andExpect(headerContains("Authorization", "oauth_consumer_key=\"consumerKey\"")).andExpect(headerDoesNotContain("Authorization", "oauth_token")).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        MatcherAssert.assertThat(restTemplate.getRequestFactory(), CoreMatchers.is(CoreMatchers.instanceOf(OAuthClientHttpRequestFactory.class)));
        Assert.assertTrue(getAdditionalOAuthParameters().isEmpty());
        MatcherAssert.assertThat(restTemplate.postForObject(url, "foo", String.class), CoreMatchers.is(CoreMatchers.equalTo("{}")));
    }

    @Test
    public void testOAuthRestTemplateWithAdditionalParameters() {
        String url = "http://myhost.com/resource?with=some&query=params&too";
        Mockito.when(details.getSignatureMethod()).thenReturn("HMAC-SHA1");
        Mockito.when(details.getConsumerKey()).thenReturn("consumerKey");
        Mockito.when(details.getSharedSecret()).thenReturn(new SharedConsumerSecretImpl("consumerSecret"));
        Mockito.when(details.getAuthorizationHeaderRealm()).thenReturn("realm");
        Mockito.when(details.isAcceptsAuthorizationHeader()).thenReturn(true);
        Mockito.when(details.getAdditionalRequestHeaders()).thenReturn(null);
        Mockito.when(details.getAdditionalParameters()).thenReturn(Collections.singletonMap("oauth_token", ""));
        OAuthRestTemplate restTemplate = new OAuthRestTemplate(details);
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(requestTo(url)).andExpect(method(POST)).andExpect(headerContains("Authorization", "OAuth realm=\"realm\"")).andExpect(headerContains("Authorization", "oauth_consumer_key=\"consumerKey\"")).andExpect(headerContains("Authorization", "oauth_token=\"\"")).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        MatcherAssert.assertThat(restTemplate.getRequestFactory(), CoreMatchers.is(CoreMatchers.instanceOf(OAuthClientHttpRequestFactory.class)));
        Map<String, String> additionalOAuthParameters = ((OAuthClientHttpRequestFactory) (restTemplate.getRequestFactory())).getAdditionalOAuthParameters();
        Assert.assertTrue(additionalOAuthParameters.containsKey("oauth_token"));
        Assert.assertTrue(additionalOAuthParameters.get("oauth_token").isEmpty());
        MatcherAssert.assertThat(restTemplate.postForObject(url, "foo", String.class), CoreMatchers.is(CoreMatchers.equalTo("{}")));
    }
}

