package org.springframework.security.oauth2.provider.token.store;


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.RequestTokenFactory;


/**
 *
 *
 * @author Dave Syer
 */
public class JdbcTokenStoreTests extends TokenStoreBaseTests {
    private JdbcTokenStore tokenStore;

    private EmbeddedDatabase db;

    @Test
    public void testFindAccessTokensByUserName() {
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TokenStoreBaseTests.TestAuthentication("test2", false));
        OAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken");
        getTokenStore().storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication);
        Collection<OAuth2AccessToken> actualOAuth2AccessTokens = getTokenStore().findTokensByUserName("test2");
        Assert.assertEquals(1, actualOAuth2AccessTokens.size());
    }
}

