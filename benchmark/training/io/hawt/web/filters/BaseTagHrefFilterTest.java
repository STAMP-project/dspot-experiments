package io.hawt.web.filters;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;


public class BaseTagHrefFilterTest {
    private BaseTagHrefFilter filter;

    private FilterConfig filterConfig;

    private ServletContext servletContext;

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private ServletOutputStream outputStream;

    private FilterChain filterChain;

    @Test
    public void filterWithDefaultContextPath() throws Exception {
        assertFilteredContentContainsBaseHref("/hawtio", "/hawtio/");
    }

    @Test
    public void filterWithNullContextPath() throws Exception {
        assertFilteredContentContainsBaseHref(null, "/");
    }

    @Test
    public void filterWithEmptyContextPath() throws Exception {
        assertFilteredContentContainsBaseHref("", "/");
    }

    @Test
    public void filterWithCustomContextPaths() throws Exception {
        assertFilteredContentContainsBaseHref("/foo", "/foo/");
        assertFilteredContentContainsBaseHref("/foo/bar", "/foo/bar/");
        assertFilteredContentContainsBaseHref("/foo/bar/cheese", "/foo/bar/cheese/");
    }

    @Test
    public void filterWithSpringBootManagementContextPath() throws Exception {
        assertFilteredContentContainsBaseHref("/management", "text/html; charset=utf-8", "/hawtio", "/hawtio/management/");
    }

    @Test
    public void filterWithCustomContextPathAndContentTypeApplicationJson() throws Exception {
        assertFilteredContentContainsBaseHref(null, "application/json", "/foo", "/hawtio/");
    }

    @Test
    public void filterWithManagementContextPathAndNullContextPath() throws Exception {
        assertFilteredContentContainsBaseHref("/management", "text/html; charset=utf-8", null, "/management/");
    }

    private class MockFilterChain implements FilterChain {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            servletResponse.getOutputStream().write(readHtml().getBytes());
        }
    }
}

