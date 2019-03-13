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


import javax.servlet.Filter;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.servlet.mock.MockFilter;


/**
 * Tests for {@link FilterRegistrationBean}.
 *
 * @author Phillip Webb
 */
public class FilterRegistrationBeanTests extends AbstractFilterRegistrationBeanTests {
    private final MockFilter filter = new MockFilter();

    @Test
    public void setFilter() throws Exception {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean();
        bean.setFilter(this.filter);
        bean.onStartup(this.servletContext);
        Mockito.verify(this.servletContext).addFilter("mockFilter", this.filter);
    }

    @Test
    public void setFilterMustNotBeNull() throws Exception {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean();
        assertThatIllegalArgumentException().isThrownBy(() -> bean.onStartup(this.servletContext)).withMessageContaining("Filter must not be null");
    }

    @Test
    public void constructFilterMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new FilterRegistrationBean<>(null)).withMessageContaining("Filter must not be null");
    }

    @Test
    public void createServletRegistrationBeanMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new FilterRegistrationBean<>(this.filter, ((ServletRegistrationBean[]) (null)))).withMessageContaining("ServletRegistrationBeans must not be null");
    }
}

