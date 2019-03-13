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


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;


public class CacheControlFilterTest {
    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private CacheControlFilter underTest = new CacheControlFilter();

    @Test
    public void max_age_is_set_to_one_year_on_js() throws Exception {
        HttpServletRequest request = CacheControlFilterTest.newRequest("/js/sonar.js");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).addHeader("Cache-Control", String.format("max-age=%s", 31536000));
    }

    @Test
    public void max_age_is_set_to_one_year_on_css() throws Exception {
        HttpServletRequest request = CacheControlFilterTest.newRequest("/css/sonar.css");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).addHeader("Cache-Control", String.format("max-age=%s", 31536000));
    }

    @Test
    public void max_age_is_set_to_five_minutes_on_images() throws Exception {
        HttpServletRequest request = CacheControlFilterTest.newRequest("/images/logo.png");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).addHeader("Cache-Control", String.format("max-age=%s", 300));
    }

    @Test
    public void max_age_is_set_to_five_minutes_on_static() throws Exception {
        HttpServletRequest request = CacheControlFilterTest.newRequest("/static/something");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).addHeader("Cache-Control", String.format("max-age=%s", 300));
    }

    @Test
    public void max_age_is_set_to_five_minutes_on_css_of_static() throws Exception {
        HttpServletRequest request = CacheControlFilterTest.newRequest("/static/css/custom.css");
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).addHeader("Cache-Control", String.format("max-age=%s", 300));
    }

    @Test
    public void does_nothing_on_home() throws Exception {
        HttpServletRequest request = CacheControlFilterTest.newRequest("/");
        underTest.doFilter(request, response, chain);
        Mockito.verifyZeroInteractions(response);
    }

    @Test
    public void does_nothing_on_web_service() throws Exception {
        HttpServletRequest request = CacheControlFilterTest.newRequest("/api/ping");
        underTest.doFilter(request, response, chain);
        Mockito.verifyZeroInteractions(response);
    }
}

