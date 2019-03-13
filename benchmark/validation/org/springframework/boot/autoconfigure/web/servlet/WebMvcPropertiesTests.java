/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.web.servlet;


import org.assertj.core.util.Throwables;
import org.junit.Test;
import org.springframework.boot.context.properties.bind.BindException;


/**
 * Tests for {@link WebMvcProperties}.
 *
 * @author Stephane Nicoll
 */
public class WebMvcPropertiesTests {
    private final WebMvcProperties properties = new WebMvcProperties();

    @Test
    public void servletPathWhenEndsWithSlashHasValidMappingAndPrefix() {
        bind("spring.mvc.servlet.path", "/foo/");
        assertThat(this.properties.getServlet().getServletMapping()).isEqualTo("/foo/*");
        assertThat(this.properties.getServlet().getServletPrefix()).isEqualTo("/foo");
    }

    @Test
    public void servletPathWhenDoesNotEndWithSlashHasValidMappingAndPrefix() {
        bind("spring.mvc.servlet.path", "/foo");
        assertThat(this.properties.getServlet().getServletMapping()).isEqualTo("/foo/*");
        assertThat(this.properties.getServlet().getServletPrefix()).isEqualTo("/foo");
    }

    @Test
    public void servletPathWhenHasWildcardThrowsException() {
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> bind("spring.mvc.servlet.path", "/*")).withRootCauseInstanceOf(IllegalArgumentException.class).satisfies(( ex) -> assertThat(Throwables.getRootCause(ex)).hasMessage("Path must not contain wildcards"));
    }
}

