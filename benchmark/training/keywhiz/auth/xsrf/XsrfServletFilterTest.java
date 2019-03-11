/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package keywhiz.auth.xsrf;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class XsrfServletFilterTest {
    private static final String COOKIE_NAME = "session";

    private static final String HEADER_NAME = "X-XSRF-TOKEN";

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    HttpServletRequest mockRequest;

    @Mock
    HttpServletResponse mockResponse;

    @Mock
    FilterChain mockChain;

    XsrfServletFilter filter;

    XsrfProtection xsrfProtection;

    @Test
    public void rejectsMissingHeader() throws Exception {
        Mockito.when(mockRequest.getCookies()).thenReturn(new Cookie[]{ new Cookie(XsrfServletFilterTest.COOKIE_NAME, "content") });
        filter.doFilter(mockRequest, mockResponse, mockChain);
        Mockito.verify(mockResponse).sendError(401);
        Mockito.verify(mockChain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
    }

    @Test
    public void rejectsMissingCookie() throws Exception {
        Mockito.when(mockRequest.getCookies()).thenReturn(new Cookie[]{  });
        Mockito.when(mockRequest.getHeader(XsrfServletFilterTest.HEADER_NAME)).thenReturn("content");
        filter.doFilter(mockRequest, mockResponse, mockChain);
        Mockito.verify(mockResponse).sendError(401);
        Mockito.verify(mockChain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
    }

    @Test
    public void rejectsNullCookies() throws Exception {
        Mockito.when(mockRequest.getCookies()).thenReturn(null);
        Mockito.when(mockRequest.getHeader(XsrfServletFilterTest.HEADER_NAME)).thenReturn("content");
        filter.doFilter(mockRequest, mockResponse, mockChain);
        Mockito.verify(mockResponse).sendError(401);
        Mockito.verify(mockChain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
    }

    @Test
    public void rejectsInvalidHeader() throws Exception {
        Mockito.when(mockRequest.getCookies()).thenReturn(new Cookie[]{ new Cookie(XsrfServletFilterTest.COOKIE_NAME, "content") });
        Mockito.when(mockRequest.getHeader(XsrfServletFilterTest.HEADER_NAME)).thenReturn("content");
        filter.doFilter(mockRequest, mockResponse, mockChain);
        Mockito.verify(mockResponse).sendError(401);
        Mockito.verify(mockChain, Mockito.never()).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
    }

    @Test
    public void continuesValidHeader() throws Exception {
        String sessionCookie = xsrfProtection.generate("some session").toString();
        Matcher matcher = Pattern.compile(((XsrfServletFilterTest.COOKIE_NAME) + "=([^;]+);.*")).matcher(sessionCookie);
        matcher.matches();
        Mockito.when(mockRequest.getCookies()).thenReturn(new Cookie[]{ new Cookie(XsrfServletFilterTest.COOKIE_NAME, "some session") });
        Mockito.when(mockRequest.getHeader(XsrfServletFilterTest.HEADER_NAME)).thenReturn(matcher.group(1));
        filter.doFilter(mockRequest, mockResponse, mockChain);
        Mockito.verify(mockChain).doFilter(mockRequest, mockResponse);
    }
}

