package org.apereo.cas.logging.web;


import HttpStatus.OK;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link ThreadContextMDCServletFilterTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class ThreadContextMDCServletFilterTests {
    @Mock
    private CookieRetrievingCookieGenerator cookieRetrievingCookieGenerator;

    @Mock
    private TicketRegistrySupport ticketSupport;

    @InjectMocks
    private ThreadContextMDCServletFilter filter;

    @Test
    public void verifyFilter() {
        val request = new MockHttpServletRequest();
        request.setRequestURI("/cas/login");
        request.setRemoteAddr("1.2.3.4");
        request.setRemoteUser("casuser");
        request.setServerName("serverName");
        request.setServerPort(1000);
        request.setContextPath("ctxpath");
        request.setContentType("contenttype");
        request.setRemotePort(2000);
        request.setQueryString("queryString");
        request.setMethod("method");
        request.setParameter("p1", "v1");
        request.setAttribute("a1", "v1");
        request.addHeader("h1", "v1");
        val response = new MockHttpServletResponse();
        val filterChain = new MockFilterChain();
        lenient().when(cookieRetrievingCookieGenerator.retrieveCookieValue(request)).thenReturn("TICKET");
        lenient().when(ticketSupport.getAuthenticatedPrincipalFrom(ArgumentMatchers.anyString())).thenReturn(CoreAuthenticationTestUtils.getPrincipal());
        try {
            filter.doFilter(request, response, filterChain);
            Assertions.assertEquals(OK.value(), response.getStatus());
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

