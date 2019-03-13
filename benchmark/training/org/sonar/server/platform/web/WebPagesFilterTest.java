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
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class WebPagesFilterTest {
    private static final String TEST_CONTEXT = "/sonarqube";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ServletContext servletContext = Mockito.mock(ServletContext.class, Mockito.RETURNS_MOCKS);

    private WebPagesCache webPagesCache = Mockito.mock(WebPagesCache.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private WebPagesFilter underTest = new WebPagesFilter(webPagesCache);

    @Test
    public void return_web_page_content() throws Exception {
        String path = "/index.html";
        Mockito.when(webPagesCache.getContent(path)).thenReturn("test");
        Mockito.when(request.getRequestURI()).thenReturn(path);
        Mockito.when(request.getContextPath()).thenReturn(WebPagesFilterTest.TEST_CONTEXT);
        WebPagesFilterTest.StringOutputStream outputStream = new WebPagesFilterTest.StringOutputStream();
        Mockito.when(response.getOutputStream()).thenReturn(outputStream);
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setContentType("text/html");
        Mockito.verify(response).setCharacterEncoding("utf-8");
        Mockito.verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        assertThat(outputStream.toString()).isEqualTo("test");
    }

    @Test
    public void does_nothing_when_static_resource() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/static");
        Mockito.when(request.getContextPath()).thenReturn(WebPagesFilterTest.TEST_CONTEXT);
        underTest.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);
        Mockito.verifyZeroInteractions(webPagesCache);
    }

    class StringOutputStream extends ServletOutputStream {
        private final StringBuilder buf = new StringBuilder();

        StringOutputStream() {
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
        }

        public void write(byte[] b) {
            this.buf.append(new String(b));
        }

        public void write(byte[] b, int off, int len) {
            this.buf.append(new String(b, off, len));
        }

        public void write(int b) {
            byte[] bytes = new byte[]{ ((byte) (b)) };
            this.buf.append(new String(bytes));
        }

        public String toString() {
            return this.buf.toString();
        }
    }
}

