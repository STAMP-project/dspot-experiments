package org.springframework.security.oauth2.provider.token.store.redis;


import java.util.Date;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.RequestTokenFactory;
import org.springframework.security.oauth2.provider.token.store.TokenStoreBaseTests;


/**
 *
 *
 * @author efenderbosch
 */
public class RedisTokenStorePrefixTests extends TokenStoreBaseTests {
    private RedisTokenStore tokenStore;

    @Test
    public void testExpiringRefreshToken() throws InterruptedException {
        String refreshToken = UUID.randomUUID().toString();
        DefaultOAuth2RefreshToken expectedExpiringRefreshToken = new DefaultExpiringOAuth2RefreshToken(refreshToken, new Date(((System.currentTimeMillis()) + 1500)));
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TokenStoreBaseTests.TestAuthentication("test2", false));
        getTokenStore().storeRefreshToken(expectedExpiringRefreshToken, expectedAuthentication);
        OAuth2RefreshToken actualExpiringRefreshToken = getTokenStore().readRefreshToken(refreshToken);
        Assert.assertEquals(expectedExpiringRefreshToken, actualExpiringRefreshToken);
        Assert.assertEquals(expectedAuthentication, getTokenStore().readAuthenticationForRefreshToken(expectedExpiringRefreshToken));
        // let the token expire
        Thread.sleep(1500);
        // now it should be gone
        Assert.assertNull(getTokenStore().readRefreshToken(refreshToken));
        Assert.assertNull(getTokenStore().readAuthenticationForRefreshToken(expectedExpiringRefreshToken));
    }

    @Test
    public void testExpiringAccessToken() throws InterruptedException {
        String accessToken = UUID.randomUUID().toString();
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TokenStoreBaseTests.TestAuthentication("test2", false));
        DefaultOAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        expectedOAuth2AccessToken.setExpiration(new Date(((System.currentTimeMillis()) + 1500)));
        getTokenStore().storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication);
        OAuth2AccessToken actualOAuth2AccessToken = getTokenStore().readAccessToken(accessToken);
        Assert.assertEquals(expectedOAuth2AccessToken, actualOAuth2AccessToken);
        Assert.assertEquals(expectedAuthentication, getTokenStore().readAuthentication(expectedOAuth2AccessToken));
        // let the token expire
        Thread.sleep(1500);
        // now it should be gone
        Assert.assertNull(getTokenStore().readAccessToken(accessToken));
        Assert.assertNull(getTokenStore().readAuthentication(expectedOAuth2AccessToken));
    }
}

