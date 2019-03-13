package sagan;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import sagan.SecurityConfig.SecurityContextAuthenticationFilter;


public class SecurityContextAuthenticationFilterTests {
    private SecurityContextAuthenticationFilter filter = new SecurityContextAuthenticationFilter("/foo");

    @Test
    public void testSuccessfulAuthentication() throws Exception {
        List<GrantedAuthority> roleUser = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(123L, "githubusername", roleUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assert.assertEquals(authentication, filter.attemptAuthentication(null, null));
    }

    @Test
    public void testUnsuccessfulAuthentication() throws Exception {
        Assert.assertEquals(null, filter.attemptAuthentication(null, null));
    }
}

