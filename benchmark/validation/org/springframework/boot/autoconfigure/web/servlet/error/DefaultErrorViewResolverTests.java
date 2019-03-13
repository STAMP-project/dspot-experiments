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
package org.springframework.boot.autoconfigure.web.servlet.error;


import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.SERVICE_UNAVAILABLE;
import MediaType.TEXT_HTML_VALUE;
import Ordered.LOWEST_PRECEDENCE;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;


/**
 * Tests for {@link DefaultErrorViewResolver}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class DefaultErrorViewResolverTests {
    private DefaultErrorViewResolver resolver;

    @Mock
    private TemplateAvailabilityProvider templateAvailabilityProvider;

    private ResourceProperties resourceProperties;

    private Map<String, Object> model = new HashMap<>();

    private HttpServletRequest request = new MockHttpServletRequest();

    @Test
    public void createWhenApplicationContextIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new DefaultErrorViewResolver(null, new ResourceProperties())).withMessageContaining("ApplicationContext must not be null");
    }

    @Test
    public void createWhenResourcePropertiesIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new DefaultErrorViewResolver(mock(.class), null)).withMessageContaining("ResourceProperties must not be null");
    }

    @Test
    public void resolveWhenNoMatchShouldReturnNull() {
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, NOT_FOUND, this.model);
        assertThat(resolved).isNull();
    }

    @Test
    public void resolveWhenExactTemplateMatchShouldReturnTemplate() {
        BDDMockito.given(this.templateAvailabilityProvider.isTemplateAvailable(ArgumentMatchers.eq("error/404"), ArgumentMatchers.any(Environment.class), ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.any(ResourceLoader.class))).willReturn(true);
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, NOT_FOUND, this.model);
        assertThat(resolved).isNotNull();
        assertThat(resolved.getViewName()).isEqualTo("error/404");
        Mockito.verify(this.templateAvailabilityProvider).isTemplateAvailable(ArgumentMatchers.eq("error/404"), ArgumentMatchers.any(Environment.class), ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.any(ResourceLoader.class));
        Mockito.verifyNoMoreInteractions(this.templateAvailabilityProvider);
    }

    @Test
    public void resolveWhenSeries5xxTemplateMatchShouldReturnTemplate() {
        BDDMockito.given(this.templateAvailabilityProvider.isTemplateAvailable(ArgumentMatchers.eq("error/5xx"), ArgumentMatchers.any(Environment.class), ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.any(ResourceLoader.class))).willReturn(true);
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, SERVICE_UNAVAILABLE, this.model);
        assertThat(resolved.getViewName()).isEqualTo("error/5xx");
    }

    @Test
    public void resolveWhenSeries4xxTemplateMatchShouldReturnTemplate() {
        BDDMockito.given(this.templateAvailabilityProvider.isTemplateAvailable(ArgumentMatchers.eq("error/4xx"), ArgumentMatchers.any(Environment.class), ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.any(ResourceLoader.class))).willReturn(true);
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, NOT_FOUND, this.model);
        assertThat(resolved.getViewName()).isEqualTo("error/4xx");
    }

    @Test
    public void resolveWhenExactResourceMatchShouldReturnResource() throws Exception {
        setResourceLocation("/exact");
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, NOT_FOUND, this.model);
        MockHttpServletResponse response = render(resolved);
        assertThat(response.getContentAsString().trim()).isEqualTo("exact/404");
        assertThat(response.getContentType()).isEqualTo(TEXT_HTML_VALUE);
    }

    @Test
    public void resolveWhenSeries4xxResourceMatchShouldReturnResource() throws Exception {
        setResourceLocation("/4xx");
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, NOT_FOUND, this.model);
        MockHttpServletResponse response = render(resolved);
        assertThat(response.getContentAsString().trim()).isEqualTo("4xx/4xx");
        assertThat(response.getContentType()).isEqualTo(TEXT_HTML_VALUE);
    }

    @Test
    public void resolveWhenSeries5xxResourceMatchShouldReturnResource() throws Exception {
        setResourceLocation("/5xx");
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, INTERNAL_SERVER_ERROR, this.model);
        MockHttpServletResponse response = render(resolved);
        assertThat(response.getContentAsString().trim()).isEqualTo("5xx/5xx");
        assertThat(response.getContentType()).isEqualTo(TEXT_HTML_VALUE);
    }

    @Test
    public void resolveWhenTemplateAndResourceMatchShouldFavorTemplate() {
        setResourceLocation("/exact");
        BDDMockito.given(this.templateAvailabilityProvider.isTemplateAvailable(ArgumentMatchers.eq("error/404"), ArgumentMatchers.any(Environment.class), ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.any(ResourceLoader.class))).willReturn(true);
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, NOT_FOUND, this.model);
        assertThat(resolved.getViewName()).isEqualTo("error/404");
    }

    @Test
    public void resolveWhenExactResourceMatchAndSeriesTemplateMatchShouldFavorResource() throws Exception {
        setResourceLocation("/exact");
        BDDMockito.given(this.templateAvailabilityProvider.isTemplateAvailable(ArgumentMatchers.eq("error/4xx"), ArgumentMatchers.any(Environment.class), ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.any(ResourceLoader.class))).willReturn(true);
        ModelAndView resolved = this.resolver.resolveErrorView(this.request, NOT_FOUND, this.model);
        MockHttpServletResponse response = render(resolved);
        assertThat(response.getContentAsString().trim()).isEqualTo("exact/404");
        assertThat(response.getContentType()).isEqualTo(TEXT_HTML_VALUE);
    }

    @Test
    public void orderShouldBeLowest() {
        assertThat(this.resolver.getOrder()).isEqualTo(LOWEST_PRECEDENCE);
    }

    @Test
    public void setOrderShouldChangeOrder() {
        this.resolver.setOrder(123);
        assertThat(this.resolver.getOrder()).isEqualTo(123);
    }

    private static class TestTemplateAvailabilityProviders extends TemplateAvailabilityProviders {
        TestTemplateAvailabilityProviders(TemplateAvailabilityProvider provider) {
            super(Collections.singletonList(provider));
        }
    }
}

