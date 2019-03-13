package org.springframework.security.oauth2.provider;


import java.util.Arrays;
import java.util.Collections;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.SerializationUtils;


public class OAuth2AuthenticationTests {
    private OAuth2Request request = RequestTokenFactory.createOAuth2Request(null, "id", null, false, Collections.singleton("read"), null, null, null, null);

    private UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken("foo", "bar", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

    @Test
    @Rollback
    public void testIsAuthenticated() {
        request = RequestTokenFactory.createOAuth2Request("id", true, Collections.singleton("read"));
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        Assert.assertTrue(authentication.isAuthenticated());
    }

    @Test
    public void testGetCredentials() {
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        Assert.assertEquals("", authentication.getCredentials());
    }

    @Test
    public void testGetPrincipal() {
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        Assert.assertEquals(userAuthentication.getPrincipal(), authentication.getPrincipal());
    }

    @Test
    public void testIsClientOnly() {
        OAuth2Authentication authentication = new OAuth2Authentication(request, null);
        Assert.assertTrue(authentication.isClientOnly());
    }

    @Test
    public void testJsonSerialization() throws Exception {
        System.err.println(new ObjectMapper().writeValueAsString(new OAuth2Authentication(request, userAuthentication)));
    }

    @Test
    public void testSerialization() {
        OAuth2Authentication holder = new OAuth2Authentication(new AuthorizationRequest("client", Arrays.asList("read")).createOAuth2Request(), new UsernamePasswordAuthenticationToken("user", "pwd"));
        OAuth2Authentication other = ((OAuth2Authentication) (SerializationUtils.deserialize(SerializationUtils.serialize(holder))));
        Assert.assertEquals(holder, other);
    }

    @Test
    public void testSerializationWithDetails() {
        OAuth2Authentication holder = new OAuth2Authentication(new AuthorizationRequest("client", Arrays.asList("read")).createOAuth2Request(), new UsernamePasswordAuthenticationToken("user", "pwd"));
        holder.setDetails(new org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails(new MockHttpServletRequest()));
        OAuth2Authentication other = ((OAuth2Authentication) (SerializationUtils.deserialize(SerializationUtils.serialize(holder))));
        Assert.assertEquals(holder, other);
    }

    // gh-573
    @Test
    public void testEraseCredentialsUserAuthentication() {
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        authentication.eraseCredentials();
        Assert.assertNull(authentication.getUserAuthentication().getCredentials());
    }
}

