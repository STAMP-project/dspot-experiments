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


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SecurityServletFilterTest {
    private SecurityServletFilter underTest = new SecurityServletFilter();

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    @Test
    public void allow_GET_method() throws IOException, ServletException {
        assertThatMethodIsAllowed("GET");
    }

    @Test
    public void allow_HEAD_method() throws IOException, ServletException {
        assertThatMethodIsAllowed("HEAD");
    }

    @Test
    public void allow_PUT_method() throws IOException, ServletException {
        assertThatMethodIsAllowed("PUT");
    }

    @Test
    public void allow_POST_method() throws IOException, ServletException {
        assertThatMethodIsAllowed("POST");
    }

    @Test
    public void deny_OPTIONS_method() throws IOException, ServletException {
        assertThatMethodIsDenied("OPTIONS");
    }

    @Test
    public void deny_TRACE_method() throws IOException, ServletException {
        assertThatMethodIsDenied("TRACE");
    }

    @Test
    public void set_security_headers() throws Exception {
        HttpServletRequest request = SecurityServletFilterTest.newRequest("GET", "/");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).addHeader("X-Frame-Options", "SAMEORIGIN");
        Mockito.verify(response).addHeader("X-XSS-Protection", "1; mode=block");
        Mockito.verify(response).addHeader("X-Content-Type-Options", "nosniff");
    }

    @Test
    public void do_not_set_frame_protection_on_integration_resources() throws Exception {
        HttpServletRequest request = SecurityServletFilterTest.newRequest("GET", "/integration/vsts/index.html");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response, Mockito.never()).addHeader(ArgumentMatchers.eq("X-Frame-Options"), ArgumentMatchers.anyString());
        Mockito.verify(response).addHeader("X-XSS-Protection", "1; mode=block");
        Mockito.verify(response).addHeader("X-Content-Type-Options", "nosniff");
    }

    @Test
    public void do_not_set_frame_protection_on_integration_resources_with_context() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getRequestURI()).thenReturn("/sonarqube/integration/vsts/index.html");
        Mockito.when(request.getContextPath()).thenReturn("/sonarqube");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response, Mockito.never()).addHeader(ArgumentMatchers.eq("X-Frame-Options"), ArgumentMatchers.anyString());
        Mockito.verify(response).addHeader("X-XSS-Protection", "1; mode=block");
        Mockito.verify(response).addHeader("X-Content-Type-Options", "nosniff");
    }
}

