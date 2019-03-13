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


import LoggerLevel.INFO;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.web.ServletFilter;
import org.sonar.api.web.ServletFilter.UrlPattern;


public class MasterServletFilterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void should_init_and_destroy_filters() throws Exception {
        ServletFilter filter = MasterServletFilterTest.createMockFilter();
        FilterConfig config = Mockito.mock(FilterConfig.class);
        MasterServletFilter master = new MasterServletFilter();
        master.init(config, Collections.singletonList(filter));
        assertThat(master.getFilters()).containsOnly(filter);
        Mockito.verify(filter).init(config);
        master.destroy();
        Mockito.verify(filter).destroy();
    }

    @Test
    public void servlet_container_should_instantiate_only_a_single_master_instance() {
        new MasterServletFilter();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Servlet filter org.sonar.server.platform.web.MasterServletFilter is already instantiated");
        new MasterServletFilter();
    }

    @Test
    public void should_propagate_initialization_failure() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("foo");
        ServletFilter filter = MasterServletFilterTest.createMockFilter();
        Mockito.doThrow(new IllegalStateException("foo")).when(filter).init(ArgumentMatchers.any(FilterConfig.class));
        FilterConfig config = Mockito.mock(FilterConfig.class);
        MasterServletFilter filters = new MasterServletFilter();
        filters.init(config, Collections.singletonList(filter));
    }

    @Test
    public void filters_should_be_optional() throws Exception {
        FilterConfig config = Mockito.mock(FilterConfig.class);
        MasterServletFilter filters = new MasterServletFilter();
        filters.init(config, Collections.emptyList());
        ServletRequest request = Mockito.mock(HttpServletRequest.class);
        ServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);
        filters.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);
    }

    @Test
    public void should_keep_filter_ordering() throws Exception {
        MasterServletFilterTest.TrueFilter filter1 = new MasterServletFilterTest.TrueFilter();
        MasterServletFilterTest.TrueFilter filter2 = new MasterServletFilterTest.TrueFilter();
        MasterServletFilter filters = new MasterServletFilter();
        filters.init(Mockito.mock(FilterConfig.class), Arrays.asList(filter1, filter2));
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/foo/bar");
        Mockito.when(request.getContextPath()).thenReturn("");
        ServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);
        filters.doFilter(request, response, chain);
        assertThat(filter1.count).isEqualTo(1);
        assertThat(filter2.count).isEqualTo(2);
    }

    @Test
    public void display_servlet_filter_patterns_in_INFO_log() throws Exception {
        ServletFilter filter = new MasterServletFilterTest.PatternFilter(UrlPattern.builder().includes("/api/issues").excludes("/batch/projects").build());
        FilterConfig config = Mockito.mock(FilterConfig.class);
        MasterServletFilter master = new MasterServletFilter();
        master.init(config, Collections.singletonList(filter));
        assertThat(logTester.logs(INFO)).containsOnly("Initializing servlet filter PatternFilter [pattern=UrlPattern{inclusions=[/api/issues], exclusions=[/batch/projects]}]");
    }

    private static final class TrueFilter extends ServletFilter {
        private static int globalCount = 0;

        private int count = 0;

        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            (MasterServletFilterTest.TrueFilter.globalCount)++;
            count = MasterServletFilterTest.TrueFilter.globalCount;
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {
        }
    }

    private static class PatternFilter extends ServletFilter {
        private final UrlPattern urlPattern;

        PatternFilter(UrlPattern urlPattern) {
            this.urlPattern = urlPattern;
        }

        @Override
        public UrlPattern doGetPattern() {
            return urlPattern;
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
            // Nothing to do
        }

        @Override
        public void init(FilterConfig filterConfig) {
            // Nothing to do
        }

        @Override
        public void destroy() {
            // Nothing to do
        }

        @Override
        public String toString() {
            return "PatternFilter";
        }
    }
}

