package org.jivesoftware.admin;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hamcrest.CoreMatchers;
import org.jivesoftware.openfire.admin.AdminManager;
import org.jivesoftware.openfire.auth.AuthToken;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AuthCheckFilterTest {
    private static final String adminUser = "test-admin-user";

    private static final String normalUser = "test-normal-user";

    private static final String remoteAddr = "a.b.c.d";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession httpSession;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AdminManager adminManager;

    @Mock
    private LoginLimitManager loginLimitManager;

    // login.jsp,index.jsp?logout=true,setup/index.jsp,setup/setup-,.gif,.png,error-serverdown.jsp
    @Test
    public void testExcludeRules() {
        Assert.assertFalse(AuthCheckFilter.testURLPassesExclude("blahblah/login.jsp", "login.jsp"));
        Assert.assertTrue(AuthCheckFilter.testURLPassesExclude("login.jsp", "login.jsp"));
        Assert.assertTrue(AuthCheckFilter.testURLPassesExclude("login.jsp?yousuck&blah", "login.jsp"));
        Assert.assertTrue(AuthCheckFilter.testURLPassesExclude("login.jsp?another=true&login.jsp?true", "login.jsp"));
        Assert.assertFalse(AuthCheckFilter.testURLPassesExclude("blahblah/login.jsp", "login.jsp?logout=false"));
        Assert.assertTrue(AuthCheckFilter.testURLPassesExclude("login.jsp?logout=false", "login.jsp?logout=false"));
        Assert.assertFalse(AuthCheckFilter.testURLPassesExclude("login.jsp?logout=false&another=true", "login.jsp?logout=false"));
        Assert.assertFalse(AuthCheckFilter.testURLPassesExclude("login.jsp?logout=false&another=true", "login.jsp?logout=false"));
        Assert.assertFalse(AuthCheckFilter.testURLPassesExclude("setup/setup-/../../log.jsp?log=info&mode=asc&lines=All", "setup/setup-*"));
        Assert.assertFalse(AuthCheckFilter.testURLPassesExclude("setup/setup-/%2E/%2E/log.jsp?log=info&mode=asc&lines=All", "setup/setup-*"));
        Assert.assertTrue(AuthCheckFilter.testURLPassesExclude("setup/setup-new.jsp", "setup/setup-*"));
        Assert.assertFalse(AuthCheckFilter.testURLPassesExclude("another.jsp?login.jsp", "login.jsp"));
    }

    @Test
    public void willNotRedirectARequestFromAnAdminUser() throws Exception {
        final AuthCheckFilter filter = new AuthCheckFilter(adminManager, loginLimitManager, AuthCheckFilterTest.AdminUserServletAuthenticatorClass.class.getName());
        filter.doFilter(request, response, filterChain);
        Mockito.verify(response, Mockito.never()).sendRedirect(ArgumentMatchers.anyString());
        Mockito.verify(loginLimitManager).recordSuccessfulAttempt(AuthCheckFilterTest.adminUser, AuthCheckFilterTest.remoteAddr);
        final ArgumentCaptor<AuthToken> argumentCaptor = ArgumentCaptor.forClass(AuthToken.class);
        Mockito.verify(httpSession).setAttribute(ArgumentMatchers.eq("jive.admin.authToken"), argumentCaptor.capture());
        final AuthToken authToken = argumentCaptor.getValue();
        Assert.assertThat(authToken.getUsername(), CoreMatchers.is(AuthCheckFilterTest.adminUser));
    }

    @Test
    public void willRedirectARequestWithoutAServletRequestAuthenticator() throws Exception {
        final AuthCheckFilter filter = new AuthCheckFilter(adminManager, loginLimitManager, "");
        filter.doFilter(request, response, filterChain);
        Mockito.verify(response).sendRedirect(ArgumentMatchers.anyString());
    }

    @Test
    public void willRedirectARequestWithABrokenServletRequestAuthenticator() throws Exception {
        final AuthCheckFilter filter = new AuthCheckFilter(adminManager, loginLimitManager, "this-is-not-a-class-name");
        filter.doFilter(request, response, filterChain);
        Mockito.verify(response).sendRedirect(ArgumentMatchers.anyString());
    }

    @Test
    public void willRedirectARequestIfTheServletRequestAuthenticatorReturnsNoUser() throws Exception {
        final AuthCheckFilter filter = new AuthCheckFilter(adminManager, loginLimitManager, AuthCheckFilterTest.NoUserServletAuthenticatorClass.class.getName());
        filter.doFilter(request, response, filterChain);
        Mockito.verify(response).sendRedirect(ArgumentMatchers.anyString());
    }

    @Test
    public void willRedirectARequestIfTheServletRequestAuthenticatorReturnsAnUnauthorisedUser() throws Exception {
        final AuthCheckFilter filter = new AuthCheckFilter(adminManager, loginLimitManager, AuthCheckFilterTest.NormalUserServletAuthenticatorClass.class.getName());
        filter.doFilter(request, response, filterChain);
        Mockito.verify(response).sendRedirect(ArgumentMatchers.anyString());
    }

    @Test
    public void willReturnTrueIfTheCorrectServletRequestAuthenticatorIsConfigured() throws Exception {
        new AuthCheckFilter(adminManager, loginLimitManager, AuthCheckFilterTest.NormalUserServletAuthenticatorClass.class.getName());
        Assert.assertThat(AuthCheckFilter.isServletRequestAuthenticatorInstanceOf(AuthCheckFilterTest.NormalUserServletAuthenticatorClass.class), CoreMatchers.is(true));
    }

    @Test
    public void willReturnFalseIfTheWrongServletRequestAuthenticatorIsConfigured() throws Exception {
        new AuthCheckFilter(adminManager, loginLimitManager, AuthCheckFilterTest.NormalUserServletAuthenticatorClass.class.getName());
        Assert.assertThat(AuthCheckFilter.isServletRequestAuthenticatorInstanceOf(AuthCheckFilterTest.AdminUserServletAuthenticatorClass.class), CoreMatchers.is(false));
    }

    @Test
    public void willReturnFalseIfNoServletRequestAuthenticatorIsConfigured() throws Exception {
        new AuthCheckFilter(adminManager, loginLimitManager, "");
        Assert.assertThat(AuthCheckFilter.isServletRequestAuthenticatorInstanceOf(AuthCheckFilterTest.AdminUserServletAuthenticatorClass.class), CoreMatchers.is(false));
    }

    public static class AdminUserServletAuthenticatorClass implements ServletRequestAuthenticator {
        @Override
        public String authenticateRequest(final HttpServletRequest request) {
            return AuthCheckFilterTest.adminUser;
        }
    }

    public static class NormalUserServletAuthenticatorClass implements ServletRequestAuthenticator {
        @Override
        public String authenticateRequest(final HttpServletRequest request) {
            return AuthCheckFilterTest.normalUser;
        }
    }

    public static class NoUserServletAuthenticatorClass implements ServletRequestAuthenticator {
        @Override
        public String authenticateRequest(final HttpServletRequest request) {
            return null;
        }
    }
}

