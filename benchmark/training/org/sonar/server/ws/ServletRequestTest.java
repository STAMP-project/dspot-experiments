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


import MediaTypes.DEFAULT;
import MediaTypes.JSON;
import MediaTypes.PROTOBUF;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ServletRequestTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HttpServletRequest source = Mockito.mock(HttpServletRequest.class);

    private ServletRequest underTest = new ServletRequest(source);

    @Test
    public void call_method() {
        underTest.method();
        Mockito.verify(source).getMethod();
    }

    @Test
    public void getMediaType() {
        Mockito.when(source.getHeader(HttpHeaders.ACCEPT)).thenReturn(JSON);
        Mockito.when(source.getRequestURI()).thenReturn("/path/to/resource/search");
        assertThat(underTest.getMediaType()).isEqualTo(JSON);
    }

    @Test
    public void default_media_type_is_octet_stream() {
        Mockito.when(source.getRequestURI()).thenReturn("/path/to/resource/search");
        assertThat(underTest.getMediaType()).isEqualTo(DEFAULT);
    }

    @Test
    public void media_type_taken_in_url_first() {
        Mockito.when(source.getHeader(HttpHeaders.ACCEPT)).thenReturn(JSON);
        Mockito.when(source.getRequestURI()).thenReturn("/path/to/resource/search.protobuf");
        assertThat(underTest.getMediaType()).isEqualTo(PROTOBUF);
    }

    @Test
    public void has_param_from_source() {
        Mockito.when(source.getParameterMap()).thenReturn(ImmutableMap.of("param", new String[]{ "value" }));
        ServletRequest request = new ServletRequest(source);
        assertThat(request.hasParam("param")).isTrue();
    }

    @Test
    public void read_param_from_source() {
        Mockito.when(source.getParameter("param")).thenReturn("value");
        assertThat(underTest.readParam("param")).isEqualTo("value");
    }

    @Test
    public void read_multi_param_from_source_with_values() {
        Mockito.when(source.getParameterValues("param")).thenReturn(new String[]{ "firstValue", "secondValue", "thirdValue" });
        List<String> result = underTest.readMultiParam("param");
        assertThat(result).containsExactly("firstValue", "secondValue", "thirdValue");
    }

    @Test
    public void read_multi_param_from_source_with_one_value() {
        Mockito.when(source.getParameterValues("param")).thenReturn(new String[]{ "firstValue" });
        List<String> result = underTest.readMultiParam("param");
        assertThat(result).containsExactly("firstValue");
    }

    @Test
    public void read_multi_param_from_source_without_value() {
        Mockito.when(source.getParameterValues("param")).thenReturn(null);
        List<String> result = underTest.readMultiParam("param");
        assertThat(result).isEmpty();
    }

    @Test
    public void read_input_stream() throws Exception {
        Mockito.when(source.getContentType()).thenReturn("multipart/form-data");
        InputStream file = Mockito.mock(InputStream.class);
        Part part = Mockito.mock(Part.class);
        Mockito.when(part.getInputStream()).thenReturn(file);
        Mockito.when(part.getSize()).thenReturn(10L);
        Mockito.when(source.getPart("param1")).thenReturn(part);
        assertThat(underTest.readInputStreamParam("param1")).isEqualTo(file);
        assertThat(underTest.readInputStreamParam("param2")).isNull();
    }

    @Test
    public void read_no_input_stream_when_part_size_is_zero() throws Exception {
        Mockito.when(source.getContentType()).thenReturn("multipart/form-data");
        InputStream file = Mockito.mock(InputStream.class);
        Part part = Mockito.mock(Part.class);
        Mockito.when(part.getInputStream()).thenReturn(file);
        Mockito.when(part.getSize()).thenReturn(0L);
        Mockito.when(source.getPart("param1")).thenReturn(part);
        assertThat(underTest.readInputStreamParam("param1")).isNull();
    }

    @Test
    public void return_no_input_stream_when_content_type_is_not_multipart() {
        Mockito.when(source.getContentType()).thenReturn("multipart/form-data");
        assertThat(underTest.readInputStreamParam("param1")).isNull();
    }

    @Test
    public void return_no_input_stream_when_content_type_is_null() {
        Mockito.when(source.getContentType()).thenReturn(null);
        assertThat(underTest.readInputStreamParam("param1")).isNull();
    }

    @Test
    public void returns_null_when_invalid_part() throws Exception {
        Mockito.when(source.getContentType()).thenReturn("multipart/form-data");
        InputStream file = Mockito.mock(InputStream.class);
        Part part = Mockito.mock(Part.class);
        Mockito.when(part.getSize()).thenReturn(0L);
        Mockito.when(part.getInputStream()).thenReturn(file);
        Mockito.doThrow(IllegalArgumentException.class).when(source).getPart("param1");
        assertThat(underTest.readInputStreamParam("param1")).isNull();
    }

    @Test
    public void getPath() {
        Mockito.when(source.getRequestURI()).thenReturn("/sonar/path/to/resource/search");
        Mockito.when(source.getContextPath()).thenReturn("/sonar");
        assertThat(underTest.getPath()).isEqualTo("/path/to/resource/search");
    }

    @Test
    public void to_string() {
        Mockito.when(source.getRequestURL()).thenReturn(new StringBuffer("http:localhost:9000/api/issues"));
        assertThat(underTest.toString()).isEqualTo("http:localhost:9000/api/issues");
        Mockito.when(source.getQueryString()).thenReturn("components=sonar");
        assertThat(underTest.toString()).isEqualTo("http:localhost:9000/api/issues?components=sonar");
    }

    @Test
    public void header_returns_the_value_of_http_header() {
        Mockito.when(source.getHeader("Accept")).thenReturn("text/plain");
        assertThat(underTest.header("Accept")).hasValue("text/plain");
    }

    @Test
    public void header_is_empty_if_absent_from_request() {
        Mockito.when(source.getHeader("Accept")).thenReturn(null);
        assertThat(underTest.header("Accept")).isEmpty();
    }

    @Test
    public void header_has_empty_value_if_present_in_request_without_value() {
        Mockito.when(source.getHeader("Accept")).thenReturn("");
        assertThat(underTest.header("Accept")).hasValue("");
    }

    @Test
    public void getReader() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("foo"));
        Mockito.when(source.getReader()).thenReturn(reader);
        assertThat(underTest.getReader()).isEqualTo(reader);
    }
}

