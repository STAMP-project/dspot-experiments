package io.dropwizard.servlets;


import io.dropwizard.util.Duration;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;


public class SlowRequestFilterTest {
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private FilterConfig filterConfig = Mockito.mock(FilterConfig.class);

    private Logger logger = Mockito.mock(Logger.class);

    private SlowRequestFilter slowRequestFilter = new SlowRequestFilter(Duration.milliseconds(500));

    @Test
    public void logsSlowRequests() throws Exception {
        Mockito.doAnswer(( invocationOnMock) -> {
            slowRequestFilter.setCurrentTimeProvider(() -> 1510330745000000L);
            return null;
        }).when(chain).doFilter(request, response);
        slowRequestFilter.doFilter(request, response, chain);
        Mockito.verify(logger).warn("Slow request: {} {} ({}ms)", "GET", "/some/path", 501L);
    }

    @Test
    public void doesNotLogFastRequests() throws Exception {
        Mockito.doAnswer(( invocationOnMock) -> {
            slowRequestFilter.setCurrentTimeProvider(() -> 1510330743000000L);
            return null;
        }).when(chain).doFilter(request, response);
        slowRequestFilter.doFilter(request, response, chain);
        Mockito.verify(logger, Mockito.never()).warn("Slow request: {} {} ({}ms)", "GET", "/some/path", 499L);
    }
}

