package com.ctrip.framework.apollo.openapi.filter;


import HttpServletResponse.SC_UNAUTHORIZED;
import com.ctrip.framework.apollo.openapi.util.ConsumerAuditUtil;
import com.ctrip.framework.apollo.openapi.util.ConsumerAuthUtil;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsumerAuthenticationFilterTest {
    private ConsumerAuthenticationFilter authenticationFilter;

    @Mock
    private ConsumerAuthUtil consumerAuthUtil;

    @Mock
    private ConsumerAuditUtil consumerAuditUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    public void testAuthSuccessfully() throws Exception {
        String someToken = "someToken";
        Long someConsumerId = 1L;
        Mockito.when(request.getHeader("Authorization")).thenReturn(someToken);
        Mockito.when(consumerAuthUtil.getConsumerId(someToken)).thenReturn(someConsumerId);
        authenticationFilter.doFilter(request, response, filterChain);
        Mockito.verify(consumerAuthUtil, Mockito.times(1)).storeConsumerId(request, someConsumerId);
        Mockito.verify(consumerAuditUtil, Mockito.times(1)).audit(request, someConsumerId);
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);
    }

    @Test
    public void testAuthFailed() throws Exception {
        String someInvalidToken = "someInvalidToken";
        Mockito.when(request.getHeader("Authorization")).thenReturn(someInvalidToken);
        Mockito.when(consumerAuthUtil.getConsumerId(someInvalidToken)).thenReturn(null);
        authenticationFilter.doFilter(request, response, filterChain);
        Mockito.verify(response, Mockito.times(1)).sendError(ArgumentMatchers.eq(SC_UNAUTHORIZED), ArgumentMatchers.anyString());
        Mockito.verify(consumerAuthUtil, Mockito.never()).storeConsumerId(ArgumentMatchers.eq(request), ArgumentMatchers.anyLong());
        Mockito.verify(consumerAuditUtil, Mockito.never()).audit(ArgumentMatchers.eq(request), ArgumentMatchers.anyLong());
        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }
}

