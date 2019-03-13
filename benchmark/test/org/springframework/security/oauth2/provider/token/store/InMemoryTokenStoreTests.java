package org.springframework.security.oauth2.provider.token.store;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.RequestTokenFactory;


/**
 *
 *
 * @author Dave Syer
 */
public class InMemoryTokenStoreTests extends TokenStoreBaseTests {
    private InMemoryTokenStore tokenStore;

    @Test
    public void testTokenCountConsistency() throws Exception {
        for (int i = 0; i <= 10; i++) {
            OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request(("id" + i), false), new TokenStoreBaseTests.TestAuthentication("test", false));
            DefaultOAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken(("testToken" + i));
            expectedOAuth2AccessToken.setExpiration(new Date(((System.currentTimeMillis()) - 1000)));
            if (i > 1) {
                Assert.assertEquals(i, getTokenStore().getAccessTokenCount());
            }
            getTokenStore().storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication);
        }
    }

    @Test
    public void testTokenCountConsistentWithExpiryQueue() throws Exception {
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TokenStoreBaseTests.TestAuthentication("test", false));
        DefaultOAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken");
        expectedOAuth2AccessToken.setExpiration(new Date(((System.currentTimeMillis()) + 10000)));
        for (int i = 0; i <= 10; i++) {
            getTokenStore().storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication);
            Assert.assertEquals(getTokenStore().getAccessTokenCount(), getTokenStore().getExpiryTokenCount());
        }
    }

    @Test
    public void testAutoFlush() throws Exception {
        getTokenStore().setFlushInterval(3);
        for (int i = 0; i <= 10; i++) {
            OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request(("id" + i), false), new TokenStoreBaseTests.TestAuthentication("test", false));
            DefaultOAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken(("testToken" + i));
            expectedOAuth2AccessToken.setExpiration(new Date(((System.currentTimeMillis()) - 1000)));
            if (i > 2) {
                Assert.assertEquals(((i % 3) + 1), getTokenStore().getAccessTokenCount());
            }
            getTokenStore().storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication);
        }
    }
}

