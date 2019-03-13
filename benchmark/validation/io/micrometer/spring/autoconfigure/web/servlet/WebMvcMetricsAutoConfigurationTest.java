/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring.autoconfigure.web.servlet;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.spring.autoconfigure.MetricsAutoConfiguration;
import io.micrometer.spring.autoconfigure.web.TestController;
import io.micrometer.spring.web.servlet.DefaultWebMvcTagsProvider;
import io.micrometer.spring.web.servlet.WebMvcMetricsFilter;
import io.micrometer.spring.web.servlet.WebMvcTagsProvider;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Tests for {@link WebMvcMetricsAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Johnny Lim
 * @author Dmytro Nosan
 */
class WebMvcMetricsAutoConfigurationTest {
    private AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

    @Test
    public void definesTagsProviderAndFilterWhenMeterRegistryIsPresent() {
        registerAndRefresh(WebMvcMetricsAutoConfigurationTest.MeterRegistryConfiguration.class, WebMvcMetricsAutoConfiguration.class);
        assertThat(this.context.getBean(DefaultWebMvcTagsProvider.class)).isNotNull();
        assertThat(this.context.getBean(WebMvcMetricsFilter.class)).isNotNull();
    }

    @Test
    public void tagsProviderBacksOff() {
        registerAndRefresh(WebMvcMetricsAutoConfigurationTest.MeterRegistryConfiguration.class, WebMvcMetricsAutoConfigurationTest.TagsProviderConfiguration.class, WebMvcMetricsAutoConfiguration.class);
        assertThatThrownBy(() -> this.context.getBean(.class)).isInstanceOf(NoSuchBeanDefinitionException.class);
        assertThat(this.context.getBean(WebMvcMetricsAutoConfigurationTest.TestWebMvcTagsProvider.class)).isNotNull();
    }

    @Test
    public void afterMaxUrisReachedFurtherUrisAreDenied() throws Exception {
        this.context.setServletContext(new MockServletContext());
        EnvironmentTestUtils.addEnvironment(this.context, "management.metrics.web.server.max-uri-tags=2");
        registerAndRefresh(WebMvcMetricsAutoConfigurationTest.MeterRegistryConfiguration.class, TestController.class, HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class, MetricsAutoConfiguration.class, WebMvcMetricsAutoConfiguration.class);
        WebMvcMetricsFilter filter = this.context.getBean(WebMvcMetricsFilter.class);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filter).build();
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(("/test" + i))).andExpect(status().isOk());
        }
        MeterRegistry registry = this.context.getBean(MeterRegistry.class);
        assertThat(registry.get("http.server.requests").meters()).hasSize(2);
    }

    @Test
    public void shouldNotDenyIfMaxUrisIsNotReached() throws Exception {
        this.context.setServletContext(new MockServletContext());
        EnvironmentTestUtils.addEnvironment(this.context, "management.metrics.web.server.max-uri-tags=5");
        registerAndRefresh(WebMvcMetricsAutoConfigurationTest.MeterRegistryConfiguration.class, TestController.class, HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class, MetricsAutoConfiguration.class, WebMvcMetricsAutoConfiguration.class);
        WebMvcMetricsFilter filter = this.context.getBean(WebMvcMetricsFilter.class);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filter).build();
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get(("/test" + i))).andExpect(status().isOk());
        }
        MeterRegistry registry = this.context.getBean(MeterRegistry.class);
        assertThat(registry.get("http.server.requests").meters()).hasSize(3);
    }

    @Configuration
    static class MeterRegistryConfiguration {
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Configuration
    static class TagsProviderConfiguration {
        @Bean
        public WebMvcMetricsAutoConfigurationTest.TestWebMvcTagsProvider tagsProvider() {
            return new WebMvcMetricsAutoConfigurationTest.TestWebMvcTagsProvider();
        }
    }

    private static final class TestWebMvcTagsProvider implements WebMvcTagsProvider {
        @Override
        public Iterable<Tag> httpLongRequestTags(HttpServletRequest request, Object handler) {
            return Collections.emptyList();
        }

        @Override
        public Iterable<Tag> httpRequestTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable ex) {
            return Collections.emptyList();
        }
    }
}

