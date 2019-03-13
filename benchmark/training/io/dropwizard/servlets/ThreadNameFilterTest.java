package io.dropwizard.servlets;


import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ThreadNameFilterTest {
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private FilterConfig filterConfig = Mockito.mock(FilterConfig.class);

    private ThreadNameFilter threadNameFilter = new ThreadNameFilter();

    @Test
    public void setsThreadNameInChain() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getRequestURI()).thenReturn("/some/path");
        Mockito.doAnswer(( invocationOnMock) -> {
            assertThat(Thread.currentThread().getName()).isEqualTo("test-thread - GET /some/path");
            return null;
        }).when(chain).doFilter(request, response);
        Thread.currentThread().setName("test-thread");
        threadNameFilter.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);
        assertThat(Thread.currentThread().getName()).isEqualTo("test-thread");
    }
}

