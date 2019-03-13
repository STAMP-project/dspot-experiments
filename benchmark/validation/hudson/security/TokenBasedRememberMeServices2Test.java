package hudson.security;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.springframework.dao.DataAccessException;


public class TokenBasedRememberMeServices2Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logging = new LoggerRule();

    private static boolean failureInduced;

    @Test
    public void rememberMeAutoLoginFailure() throws Exception {
        j.jenkins.setSecurityRealm(new TokenBasedRememberMeServices2Test.InvalidUserWhenLoggingBackInRealm());
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice", true);
        // we should see a remember me cookie
        Cookie c = getRememberMeCookie(wc);
        Assert.assertNotNull(c);
        // start a new session and attempt to access Jenkins,
        // which should cause autoLogin failures
        wc = j.createWebClient();
        wc.getCookieManager().addCookie(c);
        // even if SecurityRealm chokes, it shouldn't kill the page
        logging.capture(1000).record(TokenBasedRememberMeServices.class, Level.FINEST);
        wc.goTo("");
        // make sure that the server recorded this failure
        Assert.assertTrue(TokenBasedRememberMeServices2Test.failureInduced);
        Assert.assertTrue(logging.getMessages().stream().anyMatch(( m) -> m.contains("contained username 'alice' but was not found")));
        // and the problematic cookie should have been removed
        Assert.assertNull(getRememberMeCookie(wc));
    }

    private static class InvalidUserWhenLoggingBackInRealm extends AbstractPasswordBasedSecurityRealm {
        @Override
        protected UserDetails authenticate(String username, String password) throws AuthenticationException {
            if (username.equals(password)) {
                return new org.acegisecurity.userdetails.User(username, password, true, new GrantedAuthority[]{ new GrantedAuthorityImpl("myteam") });
            }
            throw new BadCredentialsException(username);
        }

        @Override
        public GroupDetails loadGroupByGroupname(String groupname) throws UsernameNotFoundException, DataAccessException {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
            TokenBasedRememberMeServices2Test.failureInduced = true;
            throw new UsernameNotFoundException("intentionally not working");
        }
    }

    @Test
    public void basicFlow() throws Exception {
        j.jenkins.setSecurityRealm(new TokenBasedRememberMeServices2Test.StupidRealm());
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("bob", "bob", true);
        // we should see a remember me cookie
        Cookie c = getRememberMeCookie(wc);
        Assert.assertNotNull(c);
        // start a new session and attempt to access Jenkins,
        wc = j.createWebClient();
        wc.getCookieManager().addCookie(c);
        // this will trigger remember me
        wc.goTo("");
        // make sure that our security realm failed to report the info correctly
        Assert.assertTrue(TokenBasedRememberMeServices2Test.failureInduced);
        // but we should have logged in
        wc.executeOnServer(() -> {
            Authentication a = Jenkins.getAuthentication();
            assertEquals("bob", a.getName());
            assertEquals(ImmutableList.of("authenticated", "myteam"), Arrays.asList(a.getAuthorities()).stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            return null;
        });
    }

    private static class StupidRealm extends TokenBasedRememberMeServices2Test.InvalidUserWhenLoggingBackInRealm {
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
            TokenBasedRememberMeServices2Test.failureInduced = true;
            throw new UserMayOrMayNotExistException("I cannot tell");
        }
    }
}

