package io.dropwizard.servlets;


import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class CacheBustingFilterTest {
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private final FilterChain chain = Mockito.mock(FilterChain.class);

    private final CacheBustingFilter filter = new CacheBustingFilter();

    @Test
    public void passesThroughNonHttpRequests() throws Exception {
        final ServletRequest req = Mockito.mock(ServletRequest.class);
        final ServletResponse res = Mockito.mock(ServletResponse.class);
        filter.doFilter(req, res, chain);
        Mockito.verify(chain).doFilter(req, res);
        Mockito.verifyZeroInteractions(res);
    }

    @Test
    public void setsACacheHeaderOnTheResponse() throws Exception {
        filter.doFilter(request, response, chain);
        final InOrder inOrder = Mockito.inOrder(response, chain);
        inOrder.verify(response).setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        inOrder.verify(chain).doFilter(request, response);
    }
}

