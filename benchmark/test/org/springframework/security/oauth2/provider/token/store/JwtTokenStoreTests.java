package org.springframework.security.oauth2.provider.token.store;


import java.util.Collections;
import java.util.Date;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.RequestTokenFactory;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;


/**
 *
 *
 * @author Dave Syer
 */
public class JwtTokenStoreTests {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private JwtAccessTokenConverter enhancer = new JwtAccessTokenConverter();

    private JwtTokenStore tokenStore = new JwtTokenStore(enhancer);

    private InMemoryApprovalStore approvalStore = new InMemoryApprovalStore();

    private OAuth2Request storedOAuth2Request = RequestTokenFactory.createOAuth2Request(Collections.singletonMap("grant_type", "password"), "id", null, true, Collections.singleton("read"), null, null, null, null);

    private OAuth2Authentication expectedAuthentication = new OAuth2Authentication(storedOAuth2Request, new JwtTokenStoreTests.TestAuthentication("test", true));

    private OAuth2AccessToken expectedOAuth2AccessToken;

    private OAuth2AccessToken expectedOAuth2RefreshToken;

    @Test
    public void testAccessTokenCannotBeExtractedFromAuthentication() throws Exception {
        OAuth2AccessToken accessToken = tokenStore.getAccessToken(expectedAuthentication);
        Assert.assertEquals(null, accessToken);
    }

    @Test
    public void testReadAccessToken() throws Exception {
        Assert.assertEquals(expectedOAuth2AccessToken, tokenStore.readAccessToken(expectedOAuth2AccessToken.getValue()));
    }

    @Test(expected = InvalidTokenException.class)
    public void testNonAccessTokenNotReadable() throws Exception {
        Assert.assertNull(tokenStore.readAccessToken("FOO"));
    }

    @Test(expected = InvalidTokenException.class)
    public void testNonRefreshTokenNotReadable() throws Exception {
        Assert.assertNull(tokenStore.readRefreshToken("FOO"));
    }

    @Test
    public void testAccessTokenIsNotARefreshToken() throws Exception {
        DefaultOAuth2AccessToken original = new DefaultOAuth2AccessToken("FOO");
        original.setExpiration(new Date());
        DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) (enhancer.enhance(original, expectedAuthentication)));
        expected.expect(InvalidTokenException.class);
        Assert.assertNull(tokenStore.readRefreshToken(token.getValue()));
    }

    @Test
    public void testRefreshTokenIsNotAnAccessToken() throws Exception {
        expected.expect(InvalidTokenException.class);
        Assert.assertNull(tokenStore.readAccessToken(expectedOAuth2RefreshToken.getValue()));
    }

    @Test
    public void testReadAccessTokenWithLongExpiration() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(expectedOAuth2AccessToken);
        token.setExpiration(new Date(((Long.MAX_VALUE) - 1)));
        expectedOAuth2AccessToken = enhancer.enhance(token, expectedAuthentication);
        Assert.assertEquals(expectedOAuth2AccessToken, tokenStore.readAccessToken(expectedOAuth2AccessToken.getValue()));
    }

    @Test
    public void testReadRefreshToken() throws Exception {
        Assert.assertEquals(expectedOAuth2RefreshToken, tokenStore.readRefreshToken(expectedOAuth2RefreshToken.getValue()));
    }

    @Test
    public void testReadNonExpiringRefreshToken() throws Exception {
        Assert.assertFalse(((tokenStore.readRefreshToken(expectedOAuth2RefreshToken.getValue())) instanceof DefaultExpiringOAuth2RefreshToken));
    }

    @Test
    public void testReadExpiringRefreshToken() throws Exception {
        DefaultOAuth2AccessToken original = new DefaultOAuth2AccessToken("FOO");
        original.setExpiration(new Date());
        convertToRefreshToken(original);
        DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) (enhancer.enhance(original, expectedAuthentication)));
        Assert.assertTrue(((tokenStore.readRefreshToken(token.getValue())) instanceof DefaultExpiringOAuth2RefreshToken));
    }

    @Test
    public void testReadAuthentication() throws Exception {
        checkAuthentications(expectedAuthentication, tokenStore.readAuthentication(expectedOAuth2AccessToken));
    }

    @Test
    public void testReadAuthenticationFromString() throws Exception {
        checkAuthentications(expectedAuthentication, tokenStore.readAuthentication(expectedOAuth2AccessToken.getValue()));
    }

    @Test
    public void testAuthenticationPreservesGrantType() throws Exception {
        DefaultAccessTokenConverter delegate = new DefaultAccessTokenConverter();
        delegate.setIncludeGrantType(true);
        enhancer.setAccessTokenConverter(delegate);
        expectedOAuth2AccessToken = enhancer.enhance(new DefaultOAuth2AccessToken("FOO"), expectedAuthentication);
        OAuth2Authentication authentication = tokenStore.readAuthentication(expectedOAuth2AccessToken.getValue());
        Assert.assertEquals("password", authentication.getOAuth2Request().getGrantType());
    }

    @Test
    public void testReadAuthenticationForRefreshToken() throws Exception {
        checkAuthentications(expectedAuthentication, tokenStore.readAuthenticationForRefreshToken(new org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken(expectedOAuth2AccessToken.getValue())));
    }

    @Test
    public void removeAccessToken() throws Exception {
        tokenStore.setApprovalStore(approvalStore);
        approvalStore.addApprovals(Collections.singleton(new org.springframework.security.oauth2.provider.approval.Approval("test", "id", "read", new Date(), ApprovalStatus.APPROVED)));
        Assert.assertEquals(1, approvalStore.getApprovals("test", "id").size());
        tokenStore.removeAccessToken(expectedOAuth2AccessToken);
        Assert.assertEquals(1, approvalStore.getApprovals("test", "id").size());
    }

    @Test
    public void removeRefreshToken() throws Exception {
        tokenStore.setApprovalStore(approvalStore);
        approvalStore.addApprovals(Collections.singleton(new org.springframework.security.oauth2.provider.approval.Approval("test", "id", "read", new Date(), ApprovalStatus.APPROVED)));
        Assert.assertEquals(1, approvalStore.getApprovals("test", "id").size());
        tokenStore.removeRefreshToken(new org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken(expectedOAuth2AccessToken.getValue()));
        Assert.assertEquals(0, approvalStore.getApprovals("test", "id").size());
    }

    @Test
    public void removeAccessTokenFromRefreshToken() throws Exception {
        tokenStore.setApprovalStore(approvalStore);
        approvalStore.addApprovals(Collections.singleton(new org.springframework.security.oauth2.provider.approval.Approval("test", "id", "read", new Date(), ApprovalStatus.APPROVED)));
        Assert.assertEquals(1, approvalStore.getApprovals("test", "id").size());
        tokenStore.removeAccessTokenUsingRefreshToken(new org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken(expectedOAuth2AccessToken.getValue()));
        Assert.assertEquals(1, approvalStore.getApprovals("test", "id").size());
    }

    @Test
    public void testReadRefreshTokenForUnapprovedScope() throws Exception {
        tokenStore.setApprovalStore(approvalStore);
        approvalStore.addApprovals(Collections.singleton(new org.springframework.security.oauth2.provider.approval.Approval("test", "id", "write", new Date(), ApprovalStatus.APPROVED)));
        Assert.assertEquals(1, approvalStore.getApprovals("test", "id").size());
        Assert.assertEquals(null, tokenStore.readRefreshToken(expectedOAuth2RefreshToken.getValue()));
    }

    protected static class TestAuthentication extends AbstractAuthenticationToken {
        private static final long serialVersionUID = 1L;

        private String principal;

        public TestAuthentication(String name, boolean authenticated) {
            super(null);
            setAuthenticated(authenticated);
            this.principal = name;
        }

        public Object getCredentials() {
            return "N/A";
        }

        public Object getPrincipal() {
            return this.principal;
        }
    }
}

