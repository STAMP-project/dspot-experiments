package io.dropwizard.jersey.filter;


import Response.Status.METHOD_NOT_ALLOWED;
import Response.Status.OK;
import io.dropwizard.jersey.AbstractJerseyTest;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AllowedMethodsFilterTest extends AbstractJerseyTest {
    private static final int DISALLOWED_STATUS_CODE = METHOD_NOT_ALLOWED.getStatusCode();

    private static final int OK_STATUS_CODE = OK.getStatusCode();

    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private final FilterChain chain = Mockito.mock(FilterChain.class);

    private final FilterConfig config = Mockito.mock(FilterConfig.class);

    private final AllowedMethodsFilter filter = new AllowedMethodsFilter();

    @Test
    public void testGetRequestAllowed() {
        Assertions.assertEquals(AllowedMethodsFilterTest.OK_STATUS_CODE, getResponseStatusForRequestMethod("GET", false));
    }

    @Test
    public void testPostRequestAllowed() {
        Assertions.assertEquals(AllowedMethodsFilterTest.OK_STATUS_CODE, getResponseStatusForRequestMethod("POST", true));
    }

    @Test
    public void testPutRequestBlocked() {
        Assertions.assertEquals(AllowedMethodsFilterTest.DISALLOWED_STATUS_CODE, getResponseStatusForRequestMethod("PUT", true));
    }

    @Test
    public void testDeleteRequestBlocked() {
        Assertions.assertEquals(AllowedMethodsFilterTest.DISALLOWED_STATUS_CODE, getResponseStatusForRequestMethod("DELETE", false));
    }

    @Test
    public void testTraceRequestBlocked() {
        Assertions.assertEquals(AllowedMethodsFilterTest.DISALLOWED_STATUS_CODE, getResponseStatusForRequestMethod("TRACE", false));
    }

    @Test
    public void allowsAllowedMethod() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("GET");
        filter.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);
    }

    @Test
    public void blocksDisallowedMethod() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("TRACE");
        filter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
    }

    @Test
    public void disallowedMethodCausesMethodNotAllowedResponse() throws IOException, ServletException {
        Mockito.when(request.getMethod()).thenReturn("TRACE");
        filter.doFilter(request, response, chain);
        Mockito.verify(response).sendError(ArgumentMatchers.eq(AllowedMethodsFilterTest.DISALLOWED_STATUS_CODE));
    }
}

