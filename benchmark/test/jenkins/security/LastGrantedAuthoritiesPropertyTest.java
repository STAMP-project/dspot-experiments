package jenkins.security;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.security.AbstractPasswordBasedSecurityRealm;
import hudson.security.GroupDetails;
import hudson.security.UserMayOrMayNotExistException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.hudson.model.User;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.springframework.dao.DataAccessException;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class LastGrantedAuthoritiesPropertyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void basicFlow() throws Exception {
        j.jenkins.setSecurityRealm(new LastGrantedAuthoritiesPropertyTest.TestSecurityRealm());
        // login, and make sure it leaves the LastGrantedAuthoritiesProperty object
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice:development:us");
        hudson.model.User u = hudson.model.User.get("alice");
        LastGrantedAuthoritiesProperty p = u.getProperty(LastGrantedAuthoritiesProperty.class);
        assertAuthorities(p, "authenticated:alice:development:us");
        assertAuthorities(u.impersonate(), "authenticated:alice:development:us");
        // visiting the configuration page shouldn't change authorities
        HtmlPage pg = wc.goTo("user/alice/configure");
        j.submit(pg.getFormByName("config"));
        p = u.getProperty(LastGrantedAuthoritiesProperty.class);
        assertAuthorities(p, "authenticated:alice:development:us");
        assertAuthorities(u.impersonate(), "authenticated:alice:development:us");
        // change should be reflected right away
        wc.login("alice", "alice:development:uk");
        p = u.getProperty(LastGrantedAuthoritiesProperty.class);
        assertAuthorities(p, "authenticated:alice:development:uk");
        assertAuthorities(u.impersonate(), "authenticated:alice:development:uk");
        // if already receiving the authenticated group, we should avoid duplicate
        wc.login("alice", "alice:authenticated:development:uk");
        p = u.getProperty(LastGrantedAuthoritiesProperty.class);
        assertAuthorities(p, "authenticated:alice:development:uk");
        assertAuthorities(u.impersonate(), "authenticated:alice:development:uk");
    }

    /**
     * SecurityRealm that cannot load information about other users, such Active Directory.
     */
    private static class TestSecurityRealm extends AbstractPasswordBasedSecurityRealm {
        @Override
        protected UserDetails authenticate(String username, String password) throws AuthenticationException {
            if (password.equals("error"))
                throw new BadCredentialsException(username);

            String[] desiredAuthorities = password.split(":");
            List<GrantedAuthority> authorities = Arrays.stream(desiredAuthorities).map(GrantedAuthorityImpl::new).collect(Collectors.toList());
            return new User(username, "", true, authorities.toArray(new GrantedAuthority[0]));
        }

        @Override
        public GroupDetails loadGroupByGroupname(String groupname) throws UsernameNotFoundException, DataAccessException {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
            throw new UserMayOrMayNotExistException("fallback");
        }
    }
}

