package org.springframework.security.oauth2.provider.vote;


import AccessDecisionVoter.ACCESS_DENIED;
import AccessDecisionVoter.ACCESS_GRANTED;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;


public class ClientScopeVoterTests {
    private ClientScopeVoter voter = new ClientScopeVoter();

    private Authentication userAuthentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", "password", AuthorityUtils.commaSeparatedStringToAuthorityList("read,write"));

    private OAuth2Authentication authentication;

    private BaseClientDetails client;

    @Test
    public void testAccessGranted() {
        Assert.assertEquals(ACCESS_GRANTED, voter.vote(authentication, null, Arrays.<ConfigAttribute>asList(new SecurityConfig("CLIENT_HAS_SCOPE"))));
    }

    @Test(expected = AccessDeniedException.class)
    public void testAccessDenied() {
        client.setScope(Arrays.asList("none"));
        Assert.assertEquals(ACCESS_DENIED, voter.vote(authentication, null, Arrays.<ConfigAttribute>asList(new SecurityConfig("CLIENT_HAS_SCOPE"))));
    }

    @Test
    public void testAccessDeniedNoException() {
        voter.setThrowException(false);
        client.setScope(Arrays.asList("none"));
        Assert.assertEquals(ACCESS_DENIED, voter.vote(authentication, null, Arrays.<ConfigAttribute>asList(new SecurityConfig("CLIENT_HAS_SCOPE"))));
    }
}

