package io.hawt.web.auth;


import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class LoginRedirectFilterTest {
    private LoginRedirectFilter loginRedirectFilter;

    private FilterConfig filterConfig;

    private ServletContext servletContext;

    @Test
    public void shouldTestSecuredPaths() throws Exception {
        loginRedirectFilter = new LoginRedirectFilter();
        Mockito.when(filterConfig.getServletContext()).thenReturn(servletContext);
        loginRedirectFilter.init(filterConfig);
        Assert.assertTrue(loginRedirectFilter.isSecuredPath("/d"));
        Assert.assertTrue(loginRedirectFilter.isSecuredPath("/e/f"));
        Assert.assertFalse(loginRedirectFilter.isSecuredPath("/auth/login"));
        Assert.assertFalse(loginRedirectFilter.isSecuredPath("/auth/logout"));
    }

    @Test
    public void customizedUnsecuredPaths() throws Exception {
        String[] unsecuredPaths = new String[]{ "/hawtio/auth", "/hawtio/secret/content" };
        loginRedirectFilter = new LoginRedirectFilter(unsecuredPaths);
        Mockito.when(filterConfig.getServletContext()).thenReturn(servletContext);
        loginRedirectFilter.init(filterConfig);
        Assert.assertTrue(loginRedirectFilter.isSecuredPath("/d"));
        Assert.assertTrue(loginRedirectFilter.isSecuredPath("/e/f"));
        Assert.assertFalse(loginRedirectFilter.isSecuredPath("/hawtio/auth/login"));
        Assert.assertFalse(loginRedirectFilter.isSecuredPath("/hawtio/secret/content/secure"));
    }
}

