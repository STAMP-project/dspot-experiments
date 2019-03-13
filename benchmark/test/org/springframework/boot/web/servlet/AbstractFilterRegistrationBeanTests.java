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
package org.springframework.boot.web.servlet;


import DispatcherType.FORWARD;
import DispatcherType.INCLUDE;
import DispatcherType.REQUEST;
import FilterRegistration.Dynamic;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Abstract base for {@link AbstractFilterRegistrationBean} tests.
 *
 * @author Phillip Webb
 */
public abstract class AbstractFilterRegistrationBeanTests {
    @Mock
    ServletContext servletContext;

    @Mock
    Dynamic registration;

    @Test
    public void startupWithDefaults() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addFilter(ArgumentMatchers.eq("mockFilter"), getExpectedFilter());
        Mockito.verify(this.registration).setAsyncSupported(true);
        Mockito.verify(this.registration).addMappingForUrlPatterns(EnumSet.of(REQUEST), false, "/*");
    }

    @Test
    public void startupWithSpecifiedValues() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        bean.setName("test");
        bean.setAsyncSupported(false);
        bean.setInitParameters(Collections.singletonMap("a", "b"));
        bean.addInitParameter("c", "d");
        bean.setUrlPatterns(new LinkedHashSet(Arrays.asList("/a", "/b")));
        bean.addUrlPatterns("/c");
        bean.setServletNames(new LinkedHashSet(Arrays.asList("s1", "s2")));
        bean.addServletNames("s3");
        bean.setServletRegistrationBeans(Collections.singleton(mockServletRegistration("s4")));
        bean.addServletRegistrationBeans(mockServletRegistration("s5"));
        bean.setMatchAfter(true);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addFilter(ArgumentMatchers.eq("test"), getExpectedFilter());
        Mockito.verify(this.registration).setAsyncSupported(false);
        Map<String, String> expectedInitParameters = new HashMap<>();
        expectedInitParameters.put("a", "b");
        expectedInitParameters.put("c", "d");
        Mockito.verify(this.registration).setInitParameters(expectedInitParameters);
        Mockito.verify(this.registration).addMappingForUrlPatterns(EnumSet.of(REQUEST), true, "/a", "/b", "/c");
        Mockito.verify(this.registration).addMappingForServletNames(EnumSet.of(REQUEST), true, "s4", "s5", "s1", "s2", "s3");
    }

    @Test
    public void specificName() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        bean.setName("specificName");
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addFilter(ArgumentMatchers.eq("specificName"), getExpectedFilter());
    }

    @Test
    public void deducedName() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addFilter(ArgumentMatchers.eq("mockFilter"), getExpectedFilter());
    }

    @Test
    public void disable() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        bean.setEnabled(false);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext, Mockito.never()).addFilter(ArgumentMatchers.eq("mockFilter"), getExpectedFilter());
    }

    @Test
    public void setServletRegistrationBeanMustNotBeNull() {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.setServletRegistrationBeans(null)).withMessageContaining("ServletRegistrationBeans must not be null");
    }

    @Test
    public void addServletRegistrationBeanMustNotBeNull() {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.addServletRegistrationBeans(((ServletRegistrationBean[]) (null)))).withMessageContaining("ServletRegistrationBeans must not be null");
    }

    @Test
    public void setServletRegistrationBeanReplacesValue() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean(mockServletRegistration("a"));
        bean.setServletRegistrationBeans(new LinkedHashSet<ServletRegistrationBean<?>>(Collections.singletonList(mockServletRegistration("b"))));
        bean.onStartup(this.servletContext);
        Mockito.verify(this.registration).addMappingForServletNames(EnumSet.of(REQUEST), false, "b");
    }

    @Test
    public void modifyInitParameters() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        bean.addInitParameter("a", "b");
        bean.getInitParameters().put("a", "c");
        bean.onStartup(this.servletContext);
        Mockito.verify(this.registration).setInitParameters(Collections.singletonMap("a", "c"));
    }

    @Test
    public void setUrlPatternMustNotBeNull() {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.setUrlPatterns(null)).withMessageContaining("UrlPatterns must not be null");
    }

    @Test
    public void addUrlPatternMustNotBeNull() {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.addUrlPatterns(((String[]) (null)))).withMessageContaining("UrlPatterns must not be null");
    }

    @Test
    public void setServletNameMustNotBeNull() {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.setServletNames(null)).withMessageContaining("ServletNames must not be null");
    }

    @Test
    public void addServletNameMustNotBeNull() {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.addServletNames(((String[]) (null)))).withMessageContaining("ServletNames must not be null");
    }

    @Test
    public void withSpecificDispatcherTypes() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        bean.setDispatcherTypes(INCLUDE, FORWARD);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.registration).addMappingForUrlPatterns(EnumSet.of(INCLUDE, FORWARD), false, "/*");
    }

    @Test
    public void withSpecificDispatcherTypesEnumSet() throws Exception {
        AbstractFilterRegistrationBean<?> bean = createFilterRegistrationBean();
        EnumSet<DispatcherType> types = EnumSet.of(INCLUDE, FORWARD);
        bean.setDispatcherTypes(types);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.registration).addMappingForUrlPatterns(types, false, "/*");
    }
}

