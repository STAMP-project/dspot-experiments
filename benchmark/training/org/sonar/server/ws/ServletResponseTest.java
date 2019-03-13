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
package org.sonar.server.ws;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ServletResponseTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ServletOutputStream output = Mockito.mock(ServletOutputStream.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private ServletResponse underTest = new ServletResponse(response);

    @Test
    public void test_default_header() {
        Mockito.verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    }

    @Test
    public void set_header() {
        underTest.setHeader("header", "value");
        Mockito.verify(response).setHeader("header", "value");
    }

    @Test
    public void get_header() {
        underTest.getHeader("header");
        Mockito.verify(response).getHeader("header");
    }

    @Test
    public void get_header_names() {
        underTest.getHeaderNames();
        Mockito.verify(response).getHeaderNames();
    }

    @Test
    public void test_default_status() {
        Mockito.verify(response).setStatus(200);
    }

    @Test
    public void set_status() {
        underTest.stream().setStatus(404);
        Mockito.verify(response).setStatus(404);
    }

    @Test
    public void test_output() {
        assertThat(underTest.stream().output()).isEqualTo(output);
    }

    @Test
    public void test_reset() {
        underTest.stream().reset();
        Mockito.verify(response).reset();
    }

    @Test
    public void test_newJsonWriter() throws Exception {
        underTest.newJsonWriter();
        Mockito.verify(response).setContentType(JSON);
        Mockito.verify(response).getOutputStream();
    }

    @Test
    public void test_newXmlWriter() throws Exception {
        underTest.newXmlWriter();
        Mockito.verify(response).setContentType(XML);
        Mockito.verify(response).getOutputStream();
    }

    @Test
    public void test_noContent() throws Exception {
        underTest.noContent();
        Mockito.verify(response).setStatus(204);
        Mockito.verify(response, Mockito.never()).getOutputStream();
    }
}

