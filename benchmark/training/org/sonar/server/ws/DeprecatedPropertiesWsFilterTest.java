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


import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class DeprecatedPropertiesWsFilterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WebServiceEngine webServiceEngine = Mockito.mock(WebServiceEngine.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private ArgumentCaptor<ServletRequest> servletRequestCaptor = ArgumentCaptor.forClass(ServletRequest.class);

    private DeprecatedPropertiesWsFilter underTest = new DeprecatedPropertiesWsFilter(webServiceEngine);

    @Test
    public void do_get_pattern() {
        assertThat(underTest.doGetPattern().matches("/api/properties")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/properties/")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/properties/my.property")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/properties/my_property")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/issues/search")).isFalse();
        assertThat(underTest.doGetPattern().matches("/batch/index")).isFalse();
        assertThat(underTest.doGetPattern().matches("/foo")).isFalse();
    }

    @Test
    public void redirect_api_properties_to_api_properties_index() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties");
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/properties/index", "GET");
        assertNoParam("key", "component", "value", "values");
    }

    @Test
    public void redirect_api_properties_to_api_properties_index_when_no_property() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/");
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/properties/index", "GET");
        assertNoParam("key", "component", "value", "values");
    }

    @Test
    public void redirect_api_properties_with_property_key() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/properties/index", "GET");
        assertParam("id", "my.property");
        assertNoParam("component", "value", "values");
    }

    @Test
    public void redirect_api_properties_with_property_id() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties");
        Mockito.when(request.getParameter("id")).thenReturn("my.property");
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/properties/index", "GET");
        assertParam("id", "my.property");
        assertNoParam("component", "value", "values");
    }

    @Test
    public void redirect_api_properties_when_url_ands_with_slash() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/");
        Mockito.when(request.getParameter("id")).thenReturn("my.property");
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/properties/index", "GET");
        assertParam("id", "my.property");
        assertNoParam("component", "value", "values");
    }

    @Test
    public void redirect_api_properties_when_resource() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("resource")).thenReturn("my_project");
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/properties/index", "GET");
        assertParam("id", "my.property");
        assertParam("resource", "my_project");
        assertNoParam("component", "value", "values");
    }

    @Test
    public void redirect_api_properties_when_format() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("format")).thenReturn("json");
        Mockito.when(request.getMethod()).thenReturn("GET");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/properties/index", "GET");
        assertParam("id", "my.property");
        assertParam("format", "json");
        assertNoParam("component", "value", "values");
    }

    @Test
    public void redirect_put_api_properties_to_api_settings_set() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("value")).thenReturn("my_value");
        Mockito.when(request.getParameter("resource")).thenReturn("my_project");
        Mockito.when(request.getMethod()).thenReturn("PUT");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/settings/set", "POST");
        assertParam("key", "my.property");
        assertParam("value", "my_value");
        assertParam("component", "my_project");
        assertNoParam("values");
    }

    @Test
    public void redirect_post_api_properties_to_api_settings_set() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("value")).thenReturn("my_value");
        Mockito.when(request.getParameter("resource")).thenReturn("my_project");
        Mockito.when(request.getMethod()).thenReturn("POST");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/settings/set", "POST");
        assertParam("key", "my.property");
        assertParam("value", "my_value");
        assertParam("component", "my_project");
        assertNoParam("values");
    }

    @Test
    public void redirect_post_api_properties_to_api_settings_set_when_value_is_in_body() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getInputStream()).thenReturn(new DeprecatedPropertiesWsFilterTest.TestInputStream("my_value"));
        underTest.doFilter(request, response, chain);
        assertRedirection("api/settings/set", "POST");
        assertParam("key", "my.property");
        assertParam("value", "my_value");
        assertNoParam("values", "component");
    }

    @Test
    public void redirect_post_api_properties_to_api_settings_set_when_multi_values() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("value")).thenReturn("value1,value2,value3");
        Mockito.when(request.getMethod()).thenReturn("POST");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/settings/set", "POST");
        assertParam("key", "my.property");
        assertNoParam("value");
        assertThat(servletRequestCaptor.getValue().hasParam("values")).as("Parameter '%s' hasn't been found", "values").isTrue();
        assertThat(servletRequestCaptor.getValue().readMultiParam("values")).containsOnly("value1", "value2", "value3");
    }

    @Test
    public void redirect_post_api_properties_to_api_settings_reset_when_no_value() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("resource")).thenReturn("my_project");
        Mockito.when(request.getMethod()).thenReturn("POST");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/settings/reset", "POST");
        assertParam("keys", "my.property");
        assertParam("component", "my_project");
        assertNoParam("value", "values");
    }

    @Test
    public void redirect_post_api_properties_to_api_settings_reset_when_empty_value() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("value")).thenReturn("");
        Mockito.when(request.getParameter("resource")).thenReturn("my_project");
        Mockito.when(request.getMethod()).thenReturn("POST");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/settings/reset", "POST");
        assertParam("keys", "my.property");
        assertParam("component", "my_project");
        assertNoParam("value", "values");
    }

    @Test
    public void redirect_delete_api_properties_to_api_settings_reset() throws Exception {
        Mockito.when(request.getRequestURI()).thenReturn("/api/properties/my.property");
        Mockito.when(request.getParameter("resource")).thenReturn("my_project");
        Mockito.when(request.getMethod()).thenReturn("DELETE");
        underTest.doFilter(request, response, chain);
        assertRedirection("api/settings/reset", "POST");
        assertParam("keys", "my.property");
        assertParam("component", "my_project");
        assertNoParam("value", "values");
    }

    private static class TestInputStream extends ServletInputStream {
        private final ByteArrayInputStream byteArrayInputStream;

        TestInputStream(String value) {
            this.byteArrayInputStream = new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("Not available");
        }

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }
    }
}

