/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.web;


import LoggerLevel.DEBUG;
import RootFilter.ServletRequestWrapper;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;


public class RootFilterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private RootFilter underTest;

    @Test
    public void throwable_in_doFilter_is_caught_and_500_error_returned_if_response_is_not_committed() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        HttpServletResponse response = RootFilterTest.mockHttpResponse(false);
        underTest.doFilter(request("POST", "/context/service/call", "param=value"), response, chain);
        Mockito.verify(response).sendError(500);
    }

    @Test
    public void throwable_in_doFilter_is_caught_but_no_500_response_is_sent_if_response_already_committed() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        HttpServletResponse response = RootFilterTest.mockHttpResponse(true);
        underTest.doFilter(request("POST", "/context/service/call", "param=value"), response, chain);
        Mockito.verify(response, Mockito.never()).sendError(500);
    }

    @Test
    public void throwable_in_doFilter_is_logged_in_debug_if_response_is_already_committed() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(chain).doFilter(ArgumentMatchers.any(ServletRequest.class), ArgumentMatchers.any(ServletResponse.class));
        HttpServletResponse response = RootFilterTest.mockHttpResponse(true);
        underTest.doFilter(request("POST", "/context/service/call", "param=value"), response, chain);
        List<String> debugLogs = logTester.logs(DEBUG);
        assertThat(debugLogs.size()).isEqualTo(1);
        assertThat(debugLogs.get(0)).contains("Processing of request", "failed");
    }

    @Test
    public void request_used_in_chain_do_filter_is_a_servlet_wrapper_when_static_resource() throws Exception {
        underTest.doFilter(request("GET", "/context/static/image.png", null), Mockito.mock(HttpServletResponse.class), chain);
        ArgumentCaptor<ServletRequest> requestArgumentCaptor = ArgumentCaptor.forClass(ServletRequest.class);
        Mockito.verify(chain).doFilter(requestArgumentCaptor.capture(), ArgumentMatchers.any(HttpServletResponse.class));
        assertThat(requestArgumentCaptor.getValue()).isInstanceOf(ServletRequestWrapper.class);
    }

    @Test
    public void request_used_in_chain_do_filter_is_a_servlet_wrapper_when_service_call() throws Exception {
        underTest.doFilter(request("POST", "/context/service/call", "param=value"), Mockito.mock(HttpServletResponse.class), chain);
        ArgumentCaptor<ServletRequest> requestArgumentCaptor = ArgumentCaptor.forClass(ServletRequest.class);
        Mockito.verify(chain).doFilter(requestArgumentCaptor.capture(), ArgumentMatchers.any(HttpServletResponse.class));
        assertThat(requestArgumentCaptor.getValue()).isInstanceOf(ServletRequestWrapper.class);
    }

    @Test
    public void fail_to_get_session_from_request() throws Exception {
        underTest.doFilter(request("GET", "/context/static/image.png", null), Mockito.mock(HttpServletResponse.class), chain);
        ArgumentCaptor<ServletRequest> requestArgumentCaptor = ArgumentCaptor.forClass(ServletRequest.class);
        Mockito.verify(chain).doFilter(requestArgumentCaptor.capture(), ArgumentMatchers.any(ServletResponse.class));
        expectedException.expect(UnsupportedOperationException.class);
        ((javax.servlet.http.HttpServletRequest) (requestArgumentCaptor.getValue())).getSession();
    }

    @Test
    public void fail_to_get_session_with_create_from_request() throws Exception {
        underTest.doFilter(request("GET", "/context/static/image.png", null), Mockito.mock(HttpServletResponse.class), chain);
        ArgumentCaptor<ServletRequest> requestArgumentCaptor = ArgumentCaptor.forClass(ServletRequest.class);
        Mockito.verify(chain).doFilter(requestArgumentCaptor.capture(), ArgumentMatchers.any(ServletResponse.class));
        expectedException.expect(UnsupportedOperationException.class);
        getSession(true);
    }
}

