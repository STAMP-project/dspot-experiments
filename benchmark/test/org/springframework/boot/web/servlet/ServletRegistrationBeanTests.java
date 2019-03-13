/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.web.servlet;


import ServletRegistration.Dynamic;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.web.servlet.mock.MockServlet;


/**
 * Tests for {@link ServletRegistrationBean}.
 *
 * @author Phillip Webb
 */
public class ServletRegistrationBeanTests {
    private final MockServlet servlet = new MockServlet();

    @Mock
    private ServletContext servletContext;

    @Mock
    private Dynamic registration;

    @Mock
    private FilterRegistration.Dynamic filterRegistration;

    @Test
    public void startupWithDefaults() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean(this.servlet);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addServlet("mockServlet", this.servlet);
        Mockito.verify(this.registration).setAsyncSupported(true);
        Mockito.verify(this.registration).addMapping("/*");
    }

    @Test
    public void startupWithDoubleRegistration() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean(this.servlet);
        BDDMockito.given(this.servletContext.addServlet(ArgumentMatchers.anyString(), ArgumentMatchers.any(Servlet.class))).willReturn(null);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addServlet("mockServlet", this.servlet);
        Mockito.verify(this.registration, Mockito.never()).setAsyncSupported(true);
    }

    @Test
    public void startupWithSpecifiedValues() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean();
        bean.setName("test");
        bean.setServlet(this.servlet);
        bean.setAsyncSupported(false);
        bean.setInitParameters(Collections.singletonMap("a", "b"));
        bean.addInitParameter("c", "d");
        bean.setUrlMappings(new LinkedHashSet(Arrays.asList("/a", "/b")));
        bean.addUrlMappings("/c");
        bean.setLoadOnStartup(10);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addServlet("test", this.servlet);
        Mockito.verify(this.registration).setAsyncSupported(false);
        Map<String, String> expectedInitParameters = new HashMap<>();
        expectedInitParameters.put("a", "b");
        expectedInitParameters.put("c", "d");
        Mockito.verify(this.registration).setInitParameters(expectedInitParameters);
        Mockito.verify(this.registration).addMapping("/a", "/b", "/c");
        Mockito.verify(this.registration).setLoadOnStartup(10);
    }

    @Test
    public void specificName() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean();
        bean.setName("specificName");
        bean.setServlet(this.servlet);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addServlet("specificName", this.servlet);
    }

    @Test
    public void deducedName() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean();
        bean.setServlet(this.servlet);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addServlet("mockServlet", this.servlet);
    }

    @Test
    public void disable() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean();
        bean.setServlet(this.servlet);
        bean.setEnabled(false);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext, Mockito.never()).addServlet("mockServlet", this.servlet);
    }

    @Test
    public void setServletMustNotBeNull() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.onStartup(this.servletContext)).withMessageContaining("Servlet must not be null");
    }

    @Test
    public void createServletMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ServletRegistrationBean<MockServlet>(null)).withMessageContaining("Servlet must not be null");
    }

    @Test
    public void setMappingMustNotBeNull() {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean(this.servlet);
        assertThatIllegalArgumentException().isThrownBy(() -> bean.setUrlMappings(null)).withMessageContaining("UrlMappings must not be null");
    }

    @Test
    public void createMappingMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ServletRegistrationBean<>(this.servlet, ((String[]) (null)))).withMessageContaining("UrlMappings must not be null");
    }

    @Test
    public void addMappingMustNotBeNull() {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean(this.servlet);
        assertThatIllegalArgumentException().isThrownBy(() -> bean.addUrlMappings(((String[]) (null)))).withMessageContaining("UrlMappings must not be null");
    }

    @Test
    public void setMappingReplacesValue() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean(this.servlet, "/a", "/b");
        bean.setUrlMappings(new LinkedHashSet(Arrays.asList("/c", "/d")));
        bean.onStartup(this.servletContext);
        Mockito.verify(this.registration).addMapping("/c", "/d");
    }

    @Test
    public void modifyInitParameters() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean(this.servlet, "/a", "/b");
        bean.addInitParameter("a", "b");
        bean.getInitParameters().put("a", "c");
        bean.onStartup(this.servletContext);
        Mockito.verify(this.registration).setInitParameters(Collections.singletonMap("a", "c"));
    }

    @Test
    public void withoutDefaultMappings() throws Exception {
        ServletRegistrationBean<MockServlet> bean = new ServletRegistrationBean(this.servlet, false);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.registration, Mockito.never()).addMapping(ArgumentMatchers.any(String[].class));
    }
}

