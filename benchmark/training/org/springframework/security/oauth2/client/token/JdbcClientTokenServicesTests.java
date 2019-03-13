package org.springframework.security.oauth2.client.token;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;


/**
 *
 *
 * @author Dave Syer
 */
public class JdbcClientTokenServicesTests {
    private JdbcClientTokenServices tokenStore;

    private EmbeddedDatabase db;

    @Test
    public void testSaveAndRetrieveToken() throws Exception {
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("FOO");
        Authentication authentication = new UsernamePasswordAuthenticationToken("marissa", "koala");
        AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
        resource.setClientId("client");
        resource.setScope(Arrays.asList("foo", "bar"));
        tokenStore.saveAccessToken(resource, authentication, accessToken);
        OAuth2AccessToken result = tokenStore.getAccessToken(resource, authentication);
        Assert.assertEquals(accessToken, result);
    }

    @Test
    public void testSaveAndRetrieveTokenForClientCredentials() throws Exception {
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("FOO");
        AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
        resource.setClientId("client");
        resource.setScope(Arrays.asList("foo", "bar"));
        tokenStore.saveAccessToken(resource, null, accessToken);
        OAuth2AccessToken result = tokenStore.getAccessToken(resource, null);
        Assert.assertEquals(accessToken, result);
    }

    @Test
    public void testSaveAndRemoveToken() throws Exception {
        OAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("FOO");
        Authentication authentication = new UsernamePasswordAuthenticationToken("marissa", "koala");
        AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
        resource.setClientId("client");
        resource.setScope(Arrays.asList("foo", "bar"));
        tokenStore.saveAccessToken(resource, authentication, accessToken);
        tokenStore.removeAccessToken(resource, authentication);
        // System.err.println(new JdbcTemplate(db).queryForList("select * from oauth_client_token"));
        OAuth2AccessToken result = tokenStore.getAccessToken(resource, authentication);
        Assert.assertNull(result);
    }
}

